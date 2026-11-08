#include "GestureTriggerEngine.h"
#include <algorithm>
#include <chrono>
#include <jni.h>
#include <android/log.h>

#define LOG_TAG "GestureTriggerEngine"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

GestureTriggerEngine::GestureTriggerEngine() = default;
GestureTriggerEngine::~GestureTriggerEngine() = default;

bool GestureTriggerEngine::registerShortcut(int slot, const std::string& gestureId, const std::string& name) {
    return registerShortcutWithCommand(slot, gestureId, name, "");
}

bool GestureTriggerEngine::registerShortcutWithCommand(int slot, const std::string& gestureId, const std::string& name, const std::string& triggerCommand) {
    if (slot < 0 || gestureId.empty()) {
        return false;
    }

    std::lock_guard<std::mutex> lock(engineMutex_);
    GestureShortcut shortcut;
    shortcut.slot = slot;
    shortcut.gestureId = gestureId;
    shortcut.name = name.empty() ? ("Gesture " + std::to_string(slot)) : name;
    shortcut.triggerCommand = triggerCommand;
    shortcut.active = true;

    shortcutMap_[slot] = shortcut;

    if (!triggerCommand.empty()) {
        std::string cmd = triggerCommand;
        std::transform(cmd.begin(), cmd.end(), cmd.begin(), ::tolower);
        if (cmd.front() != '/') {
            cmd = "/" + cmd;
        }
        slashCommandMap_[cmd] = gestureId;
    }

    LOGI("Registered gesture shortcut slot %d -> gesture %s", slot, gestureId.c_str());
    return true;
}

bool GestureTriggerEngine::unregisterShortcut(int slot) {
    std::lock_guard<std::mutex> lock(engineMutex_);
    auto it = shortcutMap_.find(slot);
    if (it == shortcutMap_.end()) {
        return false;
    }

    if (!it->second.triggerCommand.empty()) {
        std::string cmd = it->second.triggerCommand;
        std::transform(cmd.begin(), cmd.end(), cmd.begin(), ::tolower);
        if (cmd.front() != '/') {
            cmd = "/" + cmd;
        }
        slashCommandMap_.erase(cmd);
    }

    shortcutMap_.erase(it);
    LOGI("Unregistered gesture shortcut slot %d", slot);
    return true;
}

void GestureTriggerEngine::clearShortcuts() {
    std::lock_guard<std::mutex> lock(engineMutex_);
    shortcutMap_.clear();
    slashCommandMap_.clear();
    LOGI("Cleared all gesture shortcuts");
}

bool GestureTriggerEngine::hasShortcut(int slot) const {
    std::lock_guard<std::mutex> lock(engineMutex_);
    return shortcutMap_.find(slot) != shortcutMap_.end();
}

GestureShortcut GestureTriggerEngine::getShortcut(int slot) const {
    std::lock_guard<std::mutex> lock(engineMutex_);
    auto it = shortcutMap_.find(slot);
    if (it != shortcutMap_.end()) {
        return it->second;
    }
    return GestureShortcut();
}

std::vector<GestureShortcut> GestureTriggerEngine::getRegisteredShortcuts() const {
    std::lock_guard<std::mutex> lock(engineMutex_);
    std::vector<GestureShortcut> list;
    list.reserve(shortcutMap_.size());
    for (const auto& pair : shortcutMap_) {
        list.push_back(pair.second);
    }
    std::sort(list.begin(), list.end(), [](const GestureShortcut& a, const GestureShortcut& b) {
        return a.slot < b.slot;
    });
    return list;
}

int GestureTriggerEngine::getShortcutCount() const {
    std::lock_guard<std::mutex> lock(engineMutex_);
    return static_cast<int>(shortcutMap_.size());
}

bool GestureTriggerEngine::triggerShortcut(int slot, long long* executionTimeMs) {
    auto startTime = std::chrono::high_resolution_clock::now();

    GestureShortcut shortcut;
    GestureTriggerCallback cb;
    {
        std::lock_guard<std::mutex> lock(engineMutex_);
        auto it = shortcutMap_.find(slot);
        if (it == shortcutMap_.end() || !it->second.active) {
            return false;
        }
        shortcut = it->second;
        cb = callback_;
    }

    if (cb) {
        cb(shortcut.slot, shortcut.gestureId);
    }

    auto endTime = std::chrono::high_resolution_clock::now();
    long long durationMs = std::chrono::duration_cast<std::chrono::milliseconds>(endTime - startTime).count();
    if (executionTimeMs) {
        *executionTimeMs = durationMs;
    }

    LOGI("Triggered shortcut slot %d (gesture %s) in %lld ms", slot, shortcut.gestureId.c_str(), durationMs);
    return true;
}

bool GestureTriggerEngine::triggerByGestureId(const std::string& gestureId, long long* executionTimeMs) {
    auto startTime = std::chrono::high_resolution_clock::now();

    if (gestureId.empty()) {
        return false;
    }

    int foundSlot = -1;
    GestureTriggerCallback cb;
    {
        std::lock_guard<std::mutex> lock(engineMutex_);
        for (const auto& pair : shortcutMap_) {
            if (pair.second.gestureId == gestureId && pair.second.active) {
                foundSlot = pair.first;
                break;
            }
        }
        cb = callback_;
    }

    if (cb) {
        cb(foundSlot, gestureId);
    }

    auto endTime = std::chrono::high_resolution_clock::now();
    long long durationMs = std::chrono::duration_cast<std::chrono::milliseconds>(endTime - startTime).count();
    if (executionTimeMs) {
        *executionTimeMs = durationMs;
    }

    return true;
}

bool GestureTriggerEngine::processSlashCommand(const std::string& command, std::string* triggeredGestureId) {
    if (command.empty()) {
        return false;
    }

    std::string cmd = command;
    std::transform(cmd.begin(), cmd.end(), cmd.begin(), ::tolower);
    if (cmd.front() != '/') {
        cmd = "/" + cmd;
    }

    std::string gestureId;
    {
        std::lock_guard<std::mutex> lock(engineMutex_);
        auto it = slashCommandMap_.find(cmd);
        if (it == slashCommandMap_.end()) {
            return false;
        }
        gestureId = it->second;
    }

    if (triggeredGestureId) {
        *triggeredGestureId = gestureId;
    }

    return triggerByGestureId(gestureId, nullptr);
}

void GestureTriggerEngine::setTriggerCallback(GestureTriggerCallback callback) {
    std::lock_guard<std::mutex> lock(engineMutex_);
    callback_ = callback;
}

// Global JNI Instance management
static GestureTriggerEngine gEngine;

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_linkpoint_inventory_GestureTriggerEngine_nativeRegisterShortcut(
    JNIEnv* env, jobject thiz, jint slot, jstring jGestureId, jstring jName) {
    if (!jGestureId) return JNI_FALSE;
    const char* gestureId = env->GetStringUTFChars(jGestureId, nullptr);
    const char* name = jName ? env->GetStringUTFChars(jName, nullptr) : "";

    bool result = gEngine.registerShortcut(slot, gestureId, name);

    env->ReleaseStringUTFChars(jGestureId, gestureId);
    if (jName) env->ReleaseStringUTFChars(jName, name);
    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_linkpoint_inventory_GestureTriggerEngine_nativeUnregisterShortcut(
    JNIEnv* env, jobject thiz, jint slot) {
    return gEngine.unregisterShortcut(slot) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_linkpoint_inventory_GestureTriggerEngine_nativeClearShortcuts(
    JNIEnv* env, jobject thiz) {
    gEngine.clearShortcuts();
}

JNIEXPORT jboolean JNICALL
Java_com_linkpoint_inventory_GestureTriggerEngine_nativeTriggerShortcut(
    JNIEnv* env, jobject thiz, jint slot) {
    long long durationMs = 0;
    return gEngine.triggerShortcut(slot, &durationMs) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_linkpoint_inventory_GestureTriggerEngine_nativeProcessSlashCommand(
    JNIEnv* env, jobject thiz, jstring jCommand) {
    if (!jCommand) return JNI_FALSE;
    const char* command = env->GetStringUTFChars(jCommand, nullptr);
    bool result = gEngine.processSlashCommand(command, nullptr);
    env->ReleaseStringUTFChars(jCommand, command);
    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jint JNICALL
Java_com_linkpoint_inventory_GestureTriggerEngine_nativeGetShortcutCount(
    JNIEnv* env, jobject thiz) {
    return gEngine.getShortcutCount();
}

} // extern "C"
