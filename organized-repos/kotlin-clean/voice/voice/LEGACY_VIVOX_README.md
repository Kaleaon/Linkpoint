# Legacy Vivox Voice System - DEPRECATED

## ⚠️ DEPRECATION NOTICE

The files in this directory related to Vivox (`VivoxController.kt`, `VivoxMessageController.kt`, `VivoxMessageQueue.kt`, `VoiceService.kt`, and related voice connector files) are **LEGACY** code from a decompiled APK.

These files have been **REPLACED** by modern WebRTC implementations:

## Modern Replacements (Use These Instead)

### Primary WebRTC Implementation:
- **`WebRTCVoiceManager.kt`** - Core WebRTC voice manager with modern Kotlin coroutines
- **`WebRTCVoiceAdapter.kt`** - Adapter providing Vivox-compatible API using WebRTC
- **`SecondLifeWebRTCBridge.kt`** - Bridge for Second Life voice server integration
- **`LinkpointVoiceManager.kt`** - High-level Linkpoint voice manager

## Why These Files Are Deprecated

1. **Proprietary SDK**: Depend on closed-source Vivox SDK (`com.vivox.service.*`)
2. **Decompiled Code**: Heavily decompiled Java with poor Kotlin translation
3. **Java-Style Syntax**: Use Java patterns instead of modern Kotlin idioms
4. **No Maintenance**: Cannot be properly maintained without Vivox SDK
5. **Better Alternative**: Open-source WebRTC provides better features and performance

## Migration Path

If you're using the legacy Vivox system:

1. Replace `VivoxController` with `WebRTCVoiceAdapter`
2. Use `WebRTCVoiceManager` for direct WebRTC access
3. Use `SecondLifeWebRTCBridge` for SL voice server integration
4. Update voice callbacks to use modern coroutines

### Example Migration:

**Old (Legacy Vivox):**
```kotlin
val vivoxController = VivoxController.getInstance(context, handler)
vivoxController.ConnectChannel(channelInfo, credentials, messenger)
```

**New (Modern WebRTC):**
```kotlin
val voiceAdapter = WebRTCVoiceAdapter.getInstance(context)
viewModelScope.launch {
    voiceAdapter.initialize()
    voiceAdapter.sessionConnect(channelUri, authToken)
}
```

## Files Marked as Legacy

- `VivoxController.kt` - Main Vivox controller
- `VivoxMessageController.kt` - Message handling
- `VivoxMessageQueue.kt` - Message queue
- `VoiceService.kt` - Voice service
- `voicecon/VoiceConnector.kt` - Voice connector
- `voicecon/VoiceAccountConnection.kt` - Account management  
- `voicecon/VoiceSession.kt` - Session management

## Do NOT Use These Files

These files are kept for reference only and should not be used in new code. They will be removed in a future release once the transition to WebRTC is complete.

---

**Last Updated:** 2025-10-19
**Status:** DEPRECATED - Use WebRTC implementations
