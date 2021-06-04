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
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <jni.h>

#include "com_landleaf_jni_SerialPortJNI.h"

#include "android/log.h"

static const char *TAG = "Serialport";

#define FALSE    -1
#define TRUE    0

#define PULL_UP_GPIO_PE2 1

#define PULL_DOWN_GPIO_PE2 0

#define CFG_FLAG 1

#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

jobject createFileDescriptor(JNIEnv *env, int fd);

jint getFileDescriptorID(JNIEnv *env, jobject thiz, jobject jfd);

speed_t speed_arr[] = {B115200, B57600, B38400, B19200, B9600, B4800, B2400,
                       B1200, B300, B38400, B19200, B9600, B4800, B2400, B1200, B300,};

jint name_arr[] = {115200, 57600, 38400, 19200, 9600, 4800, 2400, 1200, 300,
                   38400, 19200, 9600, 4800, 2400, 1200, 300,};

int mod485fd;

char dev[20];

JNIEXPORT jint JNICALL
Java_com_landleaf_serial_jni_SerialPortJNI_setSpeed(JNIEnv *env, jobject instance, jobject jfd,
                                             jint speed) {
    struct termios cfg;
    jint fd = getFileDescriptorID(env, instance, jfd);

    if (0 != tcgetattr(fd, &cfg)) {
        close(fd);
        return FALSE;
    }
    int i;
    for (i = 0; i < sizeof(speed_arr) / sizeof(int); i++) {
        if (speed == name_arr[i]) {
            tcflush(fd, TCIOFLUSH);
            cfsetispeed(&cfg, speed_arr[i]);
            cfsetospeed(&cfg, speed_arr[i]);
            if (0 != tcsetattr(fd, TCSANOW, &cfg)) {
                close(fd);
                return FALSE;
            }
            tcflush(fd, TCIOFLUSH);
        }
    }
    return TRUE;
}

JNIEXPORT jint JNICALL
Java_com_landleaf_serial_jni_SerialPortJNI_setParity(JNIEnv *env, jobject instance, jobject jfd,
                                              jint databits, jint stopbits, jint parity) {
    jint fd;
    struct termios cfg;

    fd = getFileDescriptorID(env, instance, jfd);

    if (0 != tcgetattr(fd, &cfg)) {

        close(fd);
        return FALSE;
    }

    cfg.c_cflag &= ~CSIZE;
    LOGI(" :%d, stopbits:%d, parity:%c\n", databits, stopbits, parity);
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
            return FALSE;
    }
    switch (parity) {
        case 'n':
        case 'N':

            LOGI("parity:None\n");
            cfg.c_cflag &= ~PARENB; /* Clear parity enable */
            cfg.c_iflag &= ~INPCK; /* Disnable parity checking */
            break;
        case 'o':
        case 'O':
            LOGI("parity:Odd\n");
            cfg.c_cflag |= PARENB; /* Enable parity */
            cfg.c_cflag |= PARODD;
            cfg.c_iflag |= PARMRK;
            cfg.c_cflag &= ~CMSPAR;
            break;
        case 'e':
        case 'E':
            LOGI("parity:Even\n");
            cfg.c_cflag |= PARENB; /* Enable parity */
            cfg.c_cflag &= ~PARODD;
            cfg.c_iflag |= PARMRK;
            cfg.c_cflag &= ~CMSPAR;
            break;
        case 'S':
        case 's': /*parity bit to 0*/
            LOGI("parity:Space\n");
            cfg.c_cflag |= PARENB;
            cfg.c_cflag |= CMSPAR;
            cfg.c_cflag &= ~PARODD;/* Set parity bit to 0*/
            //options.c_cflag &= ~PARENB;
            //options.c_cflag &= ~CSTOPB;
            break;
        case 'M':
        case 'm': /*parity bit to 1*/
            LOGI("parity:Mark\n");
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

    if (0 != tcsetattr(fd, TCSANOW, &cfg)) {
        close(fd);
        return FALSE;
    } /* Update the options and do it NOW */
    return (TRUE);
}

JNIEXPORT jobject JNICALL
Java_com_landleaf_serial_jni_SerialPortJNI_openDev(JNIEnv *env, jobject instance, jstring devNum) {
    LOGI("openDev==>fun in");
    jobject mFileDescriptor;
    struct termios cfg;
    bzero(&cfg, sizeof(cfg));
    char dev[20];
    sprintf(dev, "/dev/tty%s", (*env)->GetStringUTFChars(env, devNum, JNI_FALSE));

    //O_RDWR:读、写打开 O_NOCTTY: O_NDELAY:使I/O变成非搁置模式
    jint fd = open(dev, O_RDWR | O_NOCTTY | O_NONBLOCK); //O_NOCTTY | O_NDELAY

    mod485fd = fd;

    if (mod485fd) {
        LOGI("opendev success %s",dev);
    }

    if (mod485fd == -1) {
        perror("IOCTRL");
        LOGE(">>>init_ioctrl_callback: open /dev/ioctrl error.\n");
    }

    if (0 != tcgetattr(fd, &cfg)) {
        close(fd);
        fd = -1;
    }
    //***************************************
    //以下为cfmakeraw(&cfg);函数设置内容
    //***************************************
    cfg.c_iflag &= ~(IGNBRK | BRKINT | PARMRK | ISTRIP | INLCR | IGNCR | ICRNL
                     | IXON);
    cfg.c_oflag &= ~OPOST;
    cfg.c_lflag &= ~(ECHO | ECHONL | ICANON | ISIG | IEXTEN);
    cfg.c_cflag &= ~(CSIZE | PARENB);
    cfg.c_cflag |= CS8;

    //***************************************
    //以下为cfmakeraw(&cfg);函数设置内容
    //***************************************

    tcflush(fd, TCIOFLUSH);
    if (0 != tcsetattr(fd, TCSANOW, &cfg)) {
        close(fd);
        fd = -1;
    }

    if (-1 == fd) {
        LOGE("Open Serial Port Failed");
        return NULL;
    } else
        LOGD("Open Serial Port Succeed");

    mFileDescriptor = createFileDescriptor(env, fd);
    return mFileDescriptor;
}

JNIEXPORT jobject JNICALL
Java_com_landleaf_serial_jni_SerialPortJNI_open485Dev(JNIEnv *env, jobject instance, jstring devNum) {
    return Java_com_landleaf_serial_jni_SerialPortJNI_openDev(env, instance, devNum);
}

JNIEXPORT jint JNICALL
Java_com_landleaf_serial_jni_SerialPortJNI_closeDev(JNIEnv *env, jobject instance, jobject jfd) {
    LOGI("Close Serial Port Succeed");

    jint fd;

    fd = getFileDescriptorID(env, instance, jfd);

    return close(fd);
}

/* Create a corresponding file descriptor */
jobject createFileDescriptor(JNIEnv *env, int fd) {

    jobject mFileDescriptor;

    jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
    jmethodID iFileDescriptor = (*env)->GetMethodID(env, cFileDescriptor,
                                                    "<init>", "()V");
    jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor,
                                               "descriptor", "I");
    mFileDescriptor = (*env)->NewObject(env, cFileDescriptor, iFileDescriptor);
    (*env)->SetIntField(env, mFileDescriptor, descriptorID, (jint) fd);

    return mFileDescriptor;

}

jint getFileDescriptorID(JNIEnv *env, jobject thiz, jobject jfd) {

    //LOGD("getFileDescriptorID==>fun in");
    //jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
    //jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd","Ljava/io/FileDescriptor;");
    //jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
    jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
    jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor,
                                               "descriptor", "I");
    jint descriptor = (*env)->GetIntField(env, jfd, descriptorID);
    //LOGD("getFileDescriptorID==>descriptor ID: %d", descriptor);

    //LOGD("getFileDescriptorID==>fun end");
    return descriptor;
}

JNIEXPORT jint JNICALL
Java_com_landleaf_serial_jni_SerialPortJNI_close485Dev(JNIEnv *env, jobject instance, jobject fd) {
    return Java_com_landleaf_serial_jni_SerialPortJNI_closeDev(env, instance, fd);
}

JNIEXPORT jint JNICALL
Java_com_landleaf_serial_jni_SerialPortJNI_readBytes(JNIEnv *env, jobject instance, jobject jfd,
                                              jbyteArray buffer_, jint length, jint time) {

    //程序挂起指定时长(1/1000ms)
    usleep((unsigned long) (time * 1000));

    int fd;
    fd = getFileDescriptorID(env, instance, jfd);
    int byteRemains = length;
    int byteGetCount = 0;
    fd_set read_fd_set;
    jbyte *jBuffer = (*env)->GetByteArrayElements(env, buffer_, JNI_FALSE);
    while (byteRemains > 0) {
        FD_ZERO(&read_fd_set);

        FD_SET(fd, &read_fd_set);

        int result = read(fd, jBuffer + (length - byteRemains), (size_t) byteRemains);

        if (result > 0) {
            byteRemains -= result;
            byteGetCount += result;
        } else {
            FD_CLR(fd, &read_fd_set);
            (*env)->ReleaseByteArrayElements(env, buffer_, jBuffer, 0);
            return byteGetCount;
        }
    }
    FD_CLR(fd, &read_fd_set);
    (*env)->ReleaseByteArrayElements(env, buffer_, jBuffer, JNI_FALSE);

    LOGI("Serial port read count:%d", byteGetCount);

    return (jint) byteGetCount;
}

JNIEXPORT jboolean JNICALL
Java_com_landleaf_serial_jni_SerialPortJNI_writeBytes(JNIEnv *env, jobject instance, jobject jfd,
                                               jbyteArray buffer_, jint length) {
    jboolean true = JNI_TRUE;

    jboolean false = JNI_FALSE;

    int fd = getFileDescriptorID(env, instance, jfd);

    jbyte *jBuffer = (*env)->GetByteArrayElements(env, buffer_, JNI_FALSE);
    jint result = write(fd, jBuffer, (size_t) length);
    (*env)->ReleaseByteArrayElements(env, buffer_, jBuffer, JNI_FALSE);

    return (result == length ? true : false);
}

JNIEXPORT jint JNICALL
Java_com_landleaf_serial_jni_SerialPortJNI_set485mod(JNIEnv *env, jobject instance, jint jmode) {
    int mode = (int) jmode;
    int ret;
    if (mode == 1)
        ret = ioctl(mod485fd, PULL_UP_GPIO_PE2, jmode);
    else if (mode == 0)
        ret = ioctl(mod485fd, PULL_DOWN_GPIO_PE2, jmode);
    else
        ret = -1;
    return (jint) ret;
}