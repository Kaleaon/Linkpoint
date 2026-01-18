/**
 * Lumiya Translation Layer Package
 * 
 * This package provides compatibility between Linkpoint's modern Kotlin implementation
 * and the communication patterns used by Lumiya's original Java implementation.
 * 
 * ## Background
 * 
 * When connecting to Second Life servers, the protocol has specific quirks that
 * Lumiya's original implementation handled correctly. "Straight Kotlin" implementations
 * may fail due to:
 * 
 * 1. **Capability URL issues**: The official SL grid (Agni) sometimes returns incomplete
 *    hostnames in capability URLs that need to be repaired.
 *    
 * 2. **Message ID encoding**: Lumiya uses signed byte/short values for message IDs,
 *    which affects how high/medium/low frequency messages are decoded.
 *    
 * 3. **LLSD formatting**: The seed capability expects an LLSD array of strings,
 *    not a map or other format.
 *    
 * 4. **Asset transfer URLs**: GetTexture, GetMesh, and other asset URLs need the
 *    same hostname repair as capability URLs.
 * 
 * ## Components
 * 
 * - [LumiyaTranslationLayer]: Core translation utilities for URL repair, grid detection,
 *   and asset transfer handling (textures, meshes, sounds, animations)
 * - [MessageTranslation]: Message ID encoding/decoding compatible with Lumiya
 * - [LumiyaProtocolBridge]: High-level bridge coordinating all translation, including
 *   asset fetching with proper URL repair
 * - [ProtocolDiagnostics]: Comprehensive debugging and statistics collection
 * 
 * ## Usage
 * 
 * ### Capability Initialization
 * 
 * The translation layer is automatically used when initializing capabilities:
 * 
 * ```kotlin
 * // Old way (may fail on some grids):
 * capabilityManager.initialize(seedCapUrl)
 * 
 * // New way with Lumiya translation:
 * capabilityManager.initialize(seedCapUrl, loginUrl)
 * ```
 * 
 * ### Asset Fetching
 * 
 * For textures, meshes, and other assets:
 * 
 * ```kotlin
 * val bridge = LumiyaProtocolBridge(loginUrl)
 * 
 * // Fetch texture with URL repair
 * val textureData = bridge.fetchTexture(textureUuid)
 * 
 * // Fetch mesh with URL repair
 * val meshData = bridge.fetchMesh(meshUuid)
 * 
 * // Get a repaired asset URL for external use
 * val textureUrl = bridge.prepareAssetUrl(
 *     "GetTexture", textureUuid, AssetTransferType.TEXTURE
 * )
 * ```
 * 
 * ### Configuration
 * 
 * You can also configure the behavior:
 * 
 * ```kotlin
 * LumiyaTranslationLayer.configure(
 *     LumiyaTranslationLayer.CompatibilityConfig(
 *         repairCapabilityUrls = true,
 *         useLumiyaCapabilityList = true,
 *         verboseLogging = true
 *     )
 * )
 * ```
 * 
 * ## Diagnostics
 * 
 * For debugging protocol issues:
 * 
 * ```kotlin
 * // Start a diagnostic session
 * ProtocolDiagnostics.startSession()
 * 
 * // Record packets (called automatically by UDPConnection)
 * ProtocolDiagnostics.recordPacketSent(messageId, size, sequenceNumber)
 * ProtocolDiagnostics.recordPacketReceived(messageId, size, sequenceNumber)
 * 
 * // Generate a report
 * val report = ProtocolDiagnostics.generateReport()
 * ProtocolDiagnostics.logReport()
 * ```
 * 
 * ## Reference
 * 
 * Based on analysis of Lumiya's decompiled source code:
 * - `com.lumiyaviewer.lumiya.slproto.caps.SLCaps` - Capability URL repair
 * - `com.lumiyaviewer.lumiya.slproto.SLAgentCircuit` - Message handling patterns
 * - `com.lumiyaviewer.lumiya.slproto.SLMessage` - Message ID encoding
 * 
 * @see com.linkpoint.protocol.capabilities.CapabilityManager
 * @see com.linkpoint.protocol.messages.UDPConnection
 * @see com.linkpoint.assets.TextureManager
 * @see com.linkpoint.assets.MeshManager
 */
package com.linkpoint.protocol.translation
