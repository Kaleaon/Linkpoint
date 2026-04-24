# Code Repair Issue List (Java/Kotlin + Smali)

Total issues: **6**
- High: **3**
- Medium: **3**
- Low: **0**

| Severity | Category | File | Symbol | Details |
|---|---|---|---|---|
| high | java_smali_reconstruction | `file_bundle/ActiveChattersManager.java` | `com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager.AnonymousClass2.processRequest` | Unresolved decompilation stub; rebuild from smali. params=(com.lumiyaviewer.lumiya.slproto.users.ChatterID) return=com.lumiyaviewer.lumiya.slproto.users.manager.UnreadMessageInfo |
| high | java_smali_reconstruction | `file_bundle/ActiveChattersManager.java` | `com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager.m273lambda$com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_13379` | Unresolved decompilation stub; rebuild from smali. params=(com.lumiyaviewer.lumiya.slproto.users.ChatterID, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent, boolean) return=void |
| medium | kotlin_logic_placeholder | `src/main/java/com/linkpoint/protocol/messages/RobustUDPConnection.kt` | `ByteArray(0)` | Potential placeholder packet/body content; verify against smali/original protocol behavior. |
| medium | kotlin_backpressure_risk | `src/main/java/com/linkpoint/protocol/messages/RobustUDPConnection.kt` | `Channel.UNLIMITED` | Unbounded channel can cause memory growth; verify expected throughput and add backpressure strategy. |
| medium | kotlin_backpressure_risk | `src/main/java/com/linkpoint/utils/SessionLogRecorder.kt` | `Channel.UNLIMITED` | Unbounded channel can cause memory growth; verify expected throughput and add backpressure strategy. |
| high | smali_artifact_availability | `docs/apk_analysis/java_smali_gap_report.json` | `smali_class_count` | Smali class inventory missing/incomplete for current pass; rerun extraction with local APK to unblock stub reconstruction. |
