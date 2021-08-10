LOCAL_PATH:= $(call my-dir)

ccc = $(shell cd $(LOCAL_PATH) && git rev-list --count HEAD)
always = $(shell cd $(LOCAL_PATH) && git describe  --always --abbrev=6)
aa=V1
bb=0
version_name=$(aa).$(bb).$(ccc).$(always)

include $(CLEAR_VARS)
LOCAL_PACKAGE_NAME := GwmMediaPlay
LOCAL_MODULE_TAGS := optional
LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true
LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_USE_AAPT2 := true
LOCAL_SRC_FILES := $(call all-java-files-under, app/src/main/java)
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/app/src/main/res
LOCAL_MANIFEST_FILE := app/src/main/AndroidManifest.xml

#LOCAL_STATIC_JAVA_LIBRARIES := \
#     $(LOCAL_PATH)/app/libs/glide-3.6.0.jar\
#   # vendor.gwm.hardware.information-V1.0-java \
#    vendor.gwm.hardware.health-V1.0-java \
#    vendor.gwm.hardware.fota-V1.0-java \

LOCAL_STATIC_ANDROID_LIBRARIES := \
     androidx.preference_preference \
     androidx.annotation_annotation \
     androidx.appcompat_appcompat \
     androidx-constraintlayout_constraintlayout \
     androidx.legacy_legacy-support-v4


LOCAL_AAPT_FLAGS := \
    --auto-add-overlay
LOCAL_AAPT_FLAGS += --version-name $(version_name)

LOCAL_PROGUARD_ENABLED := disabled
LOCAL_DEX_PREOPT := false
include $(BUILD_PACKAGE)

