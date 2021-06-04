##libserialport.so
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_LDLIBS := -lm -llog
LOCAL_MODULE := serialport
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := serialport.c

include $(BUILD_SHARED_LIBRARY)