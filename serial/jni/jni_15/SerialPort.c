/*
 * Copyright 2009-2011 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <jni.h>
#include "com_landleaf_serial_jni_SerialPortJNI.h"
#include "android/log.h"

#define FALSE	-1
#define TRUE	0
#define PULL_UP_GPIO_PE2 1
#define PULL_DOWN_GPIO_PE2 0
#define CFG_FLAG 1
#define DEVICE_NAME "/dev/ioctrl"
#define EN_485 0xFF07
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

jobject createFileDescriptor(JNIEnv *env, int fd);

jint getFileDescriptorID(JNIEnv *env, jobject thiz, jobject jfd);

jint speed_arr[] = { B115200, B57600, B38400, B19200, B9600, B4800, B2400,
		B1200, B300, B38400, B19200, B9600, B4800, B2400, B1200, B300, };

jint name_arr[] = { 115200, 57600, 38400, 19200, 9600, 4800, 2400, 1200, 300,
		38400, 19200, 9600, 4800, 2400, 1200, 300, };

jint Fd485;

static const char *TAG = "JNI C file SerialPort";
/*
 * Class:     com_landleaf_serial_jni_SerialPortJNI
 * Method:    setSpeed
 * Signature: (Ljava/io/FileDescriptor;I)I
 */
JNIEXPORT jint JNICALL Java_com_landleaf_serial_jni_SerialPortJNI_setSpeed(JNIEnv *env, jobject thiz, jobject mfd, jint speed) {
	jint i;
	jint status;
	jint fd;
	struct termios cfg;

	fd = getFileDescriptorID(env, thiz, mfd);
	LOGD("setSpeed==>get ID: %d", fd);

	LOGD("setSpeed==>get Serial cfg");
	if (0 != tcgetattr(fd, &cfg)) {
		LOGE("setSpeed==>get Serial cfg failed");
		LOGD("setSpeed==>serialPort close");
		close(fd);
		LOGD("setSpeed==>serialPort closed");
		return (FALSE);
	}
	LOGD("setSpeed==>get Serial cfg succeed");

	for (i = 0; i < sizeof(speed_arr) / sizeof(int); i++) {
		if (speed == name_arr[i]) {
			tcflush(fd, TCIOFLUSH);
			cfsetispeed(&cfg, speed_arr[i]);
			cfsetospeed(&cfg, speed_arr[i]);
			LOGD("setSpeed==>set Serial cfg");
			if (0 != tcsetattr(fd, TCSANOW, &cfg)) {
				LOGE("setSpeed==>set tcsetattr speed failed");
				LOGD("setSpeed==>serialPort close");
				close(fd);
				LOGD("setSpeed==>serialPort closed");
				return FALSE;
			}
			LOGD("setSpeed==>set Serial cfg succeed");
			tcflush(fd, TCIOFLUSH);
		}
	}
	return TRUE;
}

/*
 * Class:     com_landleaf_serial_jni_SerialPortJNI
 * Method:    setParity
 * Signature: (Ljava/io/FileDescriptor;III)I
 */
JNIEXPORT jint JNICALL Java_com_landleaf_serial_jni_SerialPortJNI_setParity(
		JNIEnv *env, jobject thiz, jobject mfd, jint databits, jint stopbits,
		jint parity) {

	jint fd;
	struct termios cfg;

	fd = getFileDescriptorID(env, thiz, mfd);
	LOGD("setParity==>get ID: %d", fd);

	LOGD("setParity==>get Serial cfg");
	if (0 != tcgetattr(fd, &cfg)) {
		LOGE("setParity==>get Serial cfg failed");
		LOGD("setParity==>serialPort close");
		close(fd);
		LOGD("setParity==>serialPort closed");
		return (FALSE);
	}
	LOGD("setParity==>get Serial cfg succeed");

	cfg.c_cflag &= ~CSIZE;
	LOGD("databits:%d, stopbits:%d, parity:%c\n", databits, stopbits, parity);
	switch (databits) {
	case 5:
		cfg.c_cflag |= CS5;
		break;
	case 6:
		cfg.c_cflag |= CS6;
		break;
	case 7:
		cfg.c_cflag |= CS7;
		break;
	case 8:
		cfg.c_cflag |= CS8;
		break;
	default:
		fprintf(stderr, "Unsupported data size\n");
		return (FALSE);
	}
	switch (parity) {
	case 'n':
	case 'N':

		LOGD("parity:None\n");
		cfg.c_cflag &= ~PARENB; /* Clear parity enable */
		cfg.c_iflag &= ~INPCK; /* Disnable parity checking */
		break;
	case 'o':
	case 'O':
		LOGD("parity:Odd\n");
		cfg.c_cflag |= PARENB; /* Enable parity */
		cfg.c_cflag |= PARODD;
		cfg.c_iflag |= PARMRK;
		cfg.c_cflag &= ~CMSPAR;
		break;
	case 'e':
	case 'E':
		LOGD("parity:Even\n");
		cfg.c_cflag |= PARENB; /* Enable parity */
		cfg.c_cflag &= ~PARODD;
		cfg.c_iflag |= PARMRK;
		cfg.c_cflag &= ~CMSPAR;
		break;
	case 'S':
	case 's': /*parity bit to 0*/
		LOGD("parity:Space\n");
		cfg.c_cflag |= PARENB;
		cfg.c_cflag |= CMSPAR;
		cfg.c_cflag &= ~PARODD;/* Set parity bit to 0*/
		//options.c_cflag &= ~PARENB;
		//options.c_cflag &= ~CSTOPB;
		break;
	case 'M':
	case 'm': /*parity bit to 1*/
		LOGD("parity:Mark\n");
		cfg.c_cflag |= PARENB;
		cfg.c_cflag |= CMSPAR;
		cfg.c_cflag |= PARODD;/* Set parity bit to 1*/
		break;

	default:
		LOGE("Unsupported parity\n");
		return (FALSE);
	}

	switch (stopbits) {
	case 1:
		cfg.c_cflag &= ~CSTOPB;
		break;
	case 2:
		cfg.c_cflag |= CSTOPB;
		break;
	default:
		fprintf(stderr, "Unsupported stop bits\n");
		return (FALSE);
	}

	/* Set input parity option */
	if ((parity != 'n') && (parity != 'N'))
		cfg.c_iflag |= INPCK;

	cfg.c_cc[VTIME] = 0;
	cfg.c_cc[VMIN] = 0;
	LOGD("cfg.c_cc[VTIME] %d", cfg.c_cc[VTIME]);
	LOGD("cfg.c_cc[VTIME] %d", cfg.c_cc[VTIME]);

#if CFG_FLAG
	cfg.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG); /*Input*/
	cfg.c_oflag &= ~OPOST; /*Output*/
#else
	cfg.c_cflag |= (CLOCAL | CREAD);
	cfg.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);
	cfg.c_oflag &= ~OPOST;
	cfg.c_oflag &= ~(ONLCR | OCRNL);
	cfg.c_iflag &= ~(ICRNL | INLCR | IGNCR);
	cfg.c_iflag &= ~(IXON | IXOFF | IXANY);
	cfg.c_cflag &= ~CSIZE;
#endif

	LOGD("setParity==>set Serial cfg");
	if (0 != tcsetattr(fd, TCSANOW, &cfg)) {
		LOGE("setParity==>set tcsetattr parity failed");
		LOGD("setParity==>serialPort close");
		close(fd);
		LOGD("setParity==>serialPort closed");
		return FALSE;
	} /* Update the options and do it NOW */
	LOGD("setParity==>set Serial cfg succeed");
	return (TRUE);
}

/*
 * Class:     com_landleaf_serial_jni_SerialPortJNI
 * Method:    openDev
 * Signature: (Ljava/lang/String;)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_com_landleaf_serial_jni_SerialPortJNI_openDev(
		JNIEnv *env, jobject thiz, jstring devnum) {

	LOGD("openDev==>fun in openDEV %s",DEVICE_NAME);
	jobject mFileDescriptor;
	struct termios cfg;
	bzero(&cfg, sizeof(cfg));
	char dev[20];
	sprintf(dev, "/dev/tty%s", (*env)->GetStringUTFChars(env, devnum, 0));
	LOGD("openDev==>success");
	LOGD("openDev==>open %s", dev);

	Fd485 = open(DEVICE_NAME, O_RDONLY);
	if(Fd485){
	LOGD("opendev success");
	}
	
	if (Fd485 == -1)
	{
		perror("IOCTRL");
		LOGD(">>>init_ioctrl_callback: open /dev/ioctrl error.\n");
		return NULL;
	}
	jint fd = open(dev, O_RDWR | O_NOCTTY | O_NDELAY); //O_NOCTTY | O_NDELAY

	LOGD("openDev==>get Serial cfg");
	if (0 != tcgetattr(fd, &cfg)) {
		LOGE("openDev==>get Serial cfg failed");
		LOGD("openDev==>serialPort close");
		close(fd);
		LOGD("openDev==>serialPort closed");
		fd = -1;
	}
	LOGD("openDev==>get Serial cfg endClient");

	cfg.c_iflag &= ~(IGNBRK | BRKINT | PARMRK | ISTRIP | INLCR | IGNCR | ICRNL
			| IXON);
	cfg.c_oflag &= ~OPOST;
	cfg.c_lflag &= ~(ECHO | ECHONL | ICANON | ISIG | IEXTEN);
	cfg.c_cflag &= ~(CSIZE | PARENB);
	cfg.c_cflag |= CS8;

	LOGD("openDev==>cfg.c_cflag %x", cfg.c_cflag);
	LOGD("openDev==>cfg.c_iflag %x", cfg.c_iflag);
	LOGD("openDev==>cfg.c_oflag %x", cfg.c_oflag);
	LOGD("openDev==>cfg.c_lflag %x", cfg.c_lflag);

	tcflush(fd, TCIOFLUSH);
	LOGD("setParity==>set Serial cfg");
	if (0 != tcsetattr(fd, TCSANOW, &cfg)) {
		LOGE("setParity==>set tcsetattr parity failed");
		LOGD("setParity==>serialPort close");
		close(fd);
		LOGD("setParity==>serialPort closed");
		fd = -1;
	}
	LOGD("setParity==>set Serial cfg endClient");

	if (-1 == fd) {
		LOGE("Open Serial Port Failed");
		return NULL;
	} else
		LOGD("Open Serial Port Succeed");

	LOGD("openDev==>open dev %d", fd);

	LOGD("openDev==>fun endClient");
	mFileDescriptor = createFileDescriptor(env, fd);
	return mFileDescriptor;
}

/*
 * Class:     com_landleaf_serial_jni_SerialPortJNI
 * Method:    closeDev
 * Signature: (Ljava/io/FileDescriptor;)I
 */
JNIEXPORT jint JNICALL Java_com_landleaf_serial_jni_SerialPortJNI_closeDev(
		JNIEnv *env, jobject thiz, jobject mfd) {

	jint fd;
	//char dev[20];
	//sprintf(dev, "/dev/ttyS%d", fd);
	LOGD("closeDev==>fun in01");
	LOGD("closeDev==>fun in");
	fd = getFileDescriptorID(env, thiz, mfd);
	LOGD("closeDev==>get ID: %d", fd);
	LOGD("closeDev==>close dev %d", fd);
	LOGD("closeDev==>fun endClient");
	close(Fd485);
	return close(fd);
}

/*
 * Class:     com_landleaf_serial_jni_SerialPortJNI
 * Method:    readBytes
 * Signature: (Ljava/io/FileDescriptor;[BI)I
 */
JNIEXPORT jint JNICALL Java_com_landleaf_serial_jni_SerialPortJNI_readBytes(
		JNIEnv *env, jobject thiz, jobject mfd, jbyteArray readbuf,
		jint byteCount, jint time) {
    LOGD("readBytes==> byteCount %d",byteCount);
	int fd;
	fd = getFileDescriptorID(env, thiz, mfd);
	int byteRemains = byteCount;
	int byteGetCount = 0;
	fd_set read_fd_set;
	jbyte* jBuffer = (*env)->GetByteArrayElements(env, readbuf, JNI_FALSE);

	while (byteRemains > 0) {
		FD_ZERO(&read_fd_set);

		FD_SET(fd, &read_fd_set);

		//select(fd + 1, &read_fd_set, NULL, NULL, NULL);
		int result = read(fd, jBuffer + (byteCount - byteRemains), byteRemains);

		if (result > 0) {
			byteRemains -= result;
			byteGetCount += result;
		} else {
			FD_CLR(fd, &read_fd_set);
			(*env)->ReleaseByteArrayElements(env, readbuf, (jbyte *) jBuffer,
					0);
			return byteGetCount;
		}
	}
	FD_CLR(fd, &read_fd_set);
	(*env)->ReleaseByteArrayElements(env, readbuf, (jbyte *) jBuffer, 0);

	//ioctl(Fd485, EN_485, 0);
	return (jint) byteGetCount;
}

/*
 * Class:     com_landleaf_serial_jni_SerialPortJNI
 * Method:    writeBytes
 * Signature: (Ljava/io/FileDescriptor;[BI)Z
 */
JNIEXPORT jboolean JNICALL Java_com_landleaf_serial_jni_SerialPortJNI_writeBytes(
		JNIEnv *env, jobject thiz, jobject mfd, jbyteArray writebuf,
		jint length) {
	int fd;
	fd = getFileDescriptorID(env, thiz, mfd);
	jbyte* jBuffer = (*env)->GetByteArrayElements(env, writebuf, JNI_FALSE);
	#if 0
	LOGD("writeBytes==>if 0");
	jint result = write(fd, jBuffer, (size_t) length);
	(*env)->ReleaseByteArrayElements(env, writebuf, jBuffer, 0);

	//延迟挂起进程,单位1/1000ms
	if(result == length)
		usleep((1+result)*1000);
		
	//ioctl(Fd485, EN_485, 1);

	return result == length ? JNI_TRUE : JNI_FALSE;
	#else
	LOGD("writeBytes==>else sleep 300us");
	jint result = write(fd, jBuffer, (size_t) length);
    if(result == length){
        usleep(300+(result)*1000);
        ioctl(Fd485, EN_485, 0);
    }
    (*env)->ReleaseByteArrayElements(env, writebuf, jBuffer, 0);
    return result == length ? JNI_TRUE : JNI_FALSE;
    #endif
}

/* Create a corresponding file descriptor */
jobject createFileDescriptor(JNIEnv *env, int fd) {

	LOGD("createFileDescriptor==>fun in");
	jobject mFileDescriptor;

	jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
	jmethodID iFileDescriptor = (*env)->GetMethodID(env, cFileDescriptor,
			"<init>", "()V");
	jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor,
			"descriptor", "I");
	mFileDescriptor = (*env)->NewObject(env, cFileDescriptor, iFileDescriptor);
	(*env)->SetIntField(env, mFileDescriptor, descriptorID, (jint) fd);

	LOGD("createFileDescriptor==>fun endClient");
	return mFileDescriptor;

}

jint getFileDescriptorID(JNIEnv *env, jobject thiz, jobject jfd) {
	LOGD("getFileDescriptorID==>fun in");
	jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
	jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd","Ljava/io/FileDescriptor;");
	jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
	jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
	jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor,
			"descriptor", "I");
	jint descriptor = (*env)->GetIntField(env, jfd, descriptorID);
	LOGD("getFileDescriptorID==>descriptor ID: %d", descriptor);

	LOGD("getFileDescriptorID==>fun endClient");
	return descriptor;
}

/*
 * Class:     com_dwin_navy_serialportapi_SerialPortJNI
 * Method:    open485Dev
 * Signature: (I)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_com_landleaf_serial_jni_SerialPortJNI_open485Dev(
		JNIEnv *env, jobject thiz, jstring devnum) {
	return Java_com_landleaf_serial_jni_SerialPortJNI_openDev(env, thiz,
			devnum);
}

/*
 * Class:     com_landleaf_serial_jni_SerialPortJNI
 * Method:    open485Dev
 * Signature: (Ljava/lang/String;)Ljava/io/FileDescriptor;
 */
JNIEXPORT jint JNICALL Java_com_landleaf_serial_jni_SerialPortJNI_close485Dev(
		JNIEnv *env, jobject thiz, jobject mfd) {
	return Java_com_landleaf_serial_jni_SerialPortJNI_closeDev(env, thiz,
			mfd);
}


/*
 * Class:     com_landleaf_serial_jni_SerialPortJNI
 * Method:    set485mod
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_landleaf_serial_jni_SerialPortJNI_set485mod(
		JNIEnv *env, jobject thiz, jint jmode) {
	int mode = (int) jmode;
	int mod485fd;
	jfieldID jmod485fd;
	int ret;
	#if 0
	Fd = open(DEVICE_NAME, O_RDONLY);
	if (Fd == -1)
	{
		LOGD(">>>init_ioctrl_callback: open /dev/ioctrl error!.\n");
		return NULL;
	}
	#endif
	LOGD("mode = %d",mode);

	LOGD("init_ioctrl_callback");
	
	ret = ioctl(Fd485, EN_485, mode);

	return (jint) ret;
}