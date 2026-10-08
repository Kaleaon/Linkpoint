#pragma once

#include <string>
#include <unordered_map>
#include <vector>
#include <mutex>
#include <functional>
#include <chrono>

struct GestureShortcut {
    int slot{-1};
    std::string gestureId;
    std::string name;
    std::string triggerCommand;
    bool active{false};
};

class GestureTriggerEngine {
public:
    GestureTriggerEngine();
    ~GestureTriggerEngine();

    // Direct shortcut registration API
    bool registerShortcut(int slot, const std::string& gestureId, const std::string& name = "");
    bool registerShortcutWithCommand(int slot, const std::string& gestureId, const std::string& name, const std::string& triggerCommand);
    bool unregisterShortcut(int slot);
    void clearShortcuts();

    // Query methods
    bool hasShortcut(int slot) const;
    GestureShortcut getShortcut(int slot) const;
    std::vector<GestureShortcut> getRegisteredShortcuts() const;
    int getShortcutCount() const;

    // Execution methods (guaranteed < 1s latency)
    bool triggerShortcut(int slot, long long* executionTimeMs = nullptr);
    bool triggerByGestureId(const std::string& gestureId, long long* executionTimeMs = nullptr);
    bool processSlashCommand(const std::string& command, std::string* triggeredGestureId = nullptr);

    // Callbacks
    using GestureTriggerCallback = std::function<void(int slot, const std::string& gestureId)>;
    void setTriggerCallback(GestureTriggerCallback callback);

private:
    mutable std::mutex engineMutex_;
    std::unordered_map<int, GestureShortcut> shortcutMap_;
    std::unordered_map<std::string, std::string> slashCommandMap_; // command -> gestureId
    GestureTriggerCallback callback_;
};
