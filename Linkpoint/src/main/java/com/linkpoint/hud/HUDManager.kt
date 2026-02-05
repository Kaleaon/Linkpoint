package com.linkpoint.hud

import android.content.Context
import android.content.res.Configuration
import android.os.Parcelable
import android.util.Log
import com.linkpoint.objects.ObjectManager
import com.linkpoint.objects.SceneObject
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * HUDManager - Manages Heads-Up Display (HUD) attachments in Second Life.
 * 
 * HUDs are special objects attached to the avatar that are:
 * - Rendered in screen space (not world space)
 * - Interactive (can be clicked/touched)
 * - Only visible to the owner
 * 
 * HUD attachment points:
 * - HUD Center 1 (35)
 * - HUD Center 2 (31)
 * - HUD Top Right (32)
 * - HUD Top Center (33)
 * - HUD Top Left (34)
 * - HUD Bottom Left (36)
 * - HUD Bottom (37)
 * - HUD Bottom Right (38)
 * 
 * Based on Lumiya's HUD implementation.
 */
class HUDManager(
    private val context: Context,
    private val objectManager: ObjectManager,
    private val udpConnection: UDPConnectionFixed,
    private val agentId: UUID
) {
    companion object {
        private const val TAG = "HUDManager"
        private const val PREFS_NAME = "linkpoint_hud_layout"
        private const val KEY_LAYOUT_CONFIG = "hud_layout_config"
        private const val TABLET_SMALLEST_WIDTH_DP = 600
        
        // HUD attachment points
        const val ATTACH_HUD_CENTER_2 = 31
        const val ATTACH_HUD_TOP_RIGHT = 32
        const val ATTACH_HUD_TOP_CENTER = 33
        const val ATTACH_HUD_TOP_LEFT = 34
        const val ATTACH_HUD_CENTER_1 = 35
        const val ATTACH_HUD_BOTTOM_LEFT = 36
        const val ATTACH_HUD_BOTTOM = 37
        const val ATTACH_HUD_BOTTOM_RIGHT = 38
        
        // All HUD attachment points
        val HUD_ATTACHMENT_POINTS = setOf(
            ATTACH_HUD_CENTER_2,
            ATTACH_HUD_TOP_RIGHT,
            ATTACH_HUD_TOP_CENTER,
            ATTACH_HUD_TOP_LEFT,
            ATTACH_HUD_CENTER_1,
            ATTACH_HUD_BOTTOM_LEFT,
            ATTACH_HUD_BOTTOM,
            ATTACH_HUD_BOTTOM_RIGHT
        )
        
        // Screen layout positions for HUD points
        // Values are in normalized screen coordinates (0-1)
        val HUD_SCREEN_POSITIONS = mapOf(
            ATTACH_HUD_TOP_LEFT to HUDPosition(0.05f, 0.05f, HUDAlignment.TOP_LEFT),
            ATTACH_HUD_TOP_CENTER to HUDPosition(0.5f, 0.05f, HUDAlignment.TOP_CENTER),
            ATTACH_HUD_TOP_RIGHT to HUDPosition(0.95f, 0.05f, HUDAlignment.TOP_RIGHT),
            ATTACH_HUD_CENTER_1 to HUDPosition(0.5f, 0.5f, HUDAlignment.CENTER),
            ATTACH_HUD_CENTER_2 to HUDPosition(0.5f, 0.6f, HUDAlignment.CENTER),
            ATTACH_HUD_BOTTOM_LEFT to HUDPosition(0.05f, 0.95f, HUDAlignment.BOTTOM_LEFT),
            ATTACH_HUD_BOTTOM to HUDPosition(0.5f, 0.95f, HUDAlignment.BOTTOM_CENTER),
            ATTACH_HUD_BOTTOM_RIGHT to HUDPosition(0.95f, 0.95f, HUDAlignment.BOTTOM_RIGHT)
        )
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var layoutConfig = HudLayoutConfig()
    
    // HUD objects by attachment point
    private val hudsByPoint = ConcurrentHashMap<Int, MutableList<HUDObject>>()
    
    // All HUD objects
    private val allHuds = ConcurrentHashMap<Int, HUDObject>() // By local ID
    
    // HUD visibility settings
    private val _hudsVisible = MutableStateFlow(true)
    val hudsVisible: StateFlow<Boolean> = _hudsVisible
    
    // Currently focused HUD (for input)
    private val _focusedHud = MutableStateFlow<HUDObject?>(null)
    val focusedHud: StateFlow<HUDObject?> = _focusedHud

    init {
        loadLayoutConfig()
    }
    
    /**
     * Check if an attachment point is a HUD point.
     */
    fun isHUDAttachmentPoint(attachmentPoint: Int): Boolean {
        return attachmentPoint in HUD_ATTACHMENT_POINTS
    }
    
    /**
     * Get the name of an attachment point.
     */
    fun getAttachmentPointName(attachmentPoint: Int): String {
        return when (attachmentPoint) {
            ATTACH_HUD_CENTER_1 -> "HUD Center 1"
            ATTACH_HUD_CENTER_2 -> "HUD Center 2"
            ATTACH_HUD_TOP_RIGHT -> "HUD Top Right"
            ATTACH_HUD_TOP_CENTER -> "HUD Top Center"
            ATTACH_HUD_TOP_LEFT -> "HUD Top Left"
            ATTACH_HUD_BOTTOM_LEFT -> "HUD Bottom Left"
            ATTACH_HUD_BOTTOM -> "HUD Bottom"
            ATTACH_HUD_BOTTOM_RIGHT -> "HUD Bottom Right"
            else -> "Attachment Point $attachmentPoint"
        }
    }
    
    /**
     * Register an object as a HUD.
     * Called when an object update indicates an object is attached to a HUD point.
     */
    fun registerHUD(sceneObject: SceneObject, attachmentPoint: Int) {
        if (!isHUDAttachmentPoint(attachmentPoint)) {
            Log.w(TAG, "Not a HUD attachment point: $attachmentPoint")
            return
        }
        
        val hudObject = HUDObject(
            localId = sceneObject.localId,
            fullId = sceneObject.fullId,
            attachmentPoint = attachmentPoint,
            position = sceneObject.position,
            scale = sceneObject.scale,
            name = sceneObject.name.ifEmpty { "HUD" }
        )
        
        // Add to tracking maps
        allHuds[sceneObject.localId] = hudObject
        hudsByPoint.getOrPut(attachmentPoint) { mutableListOf() }.add(hudObject)
        
        Log.d(TAG, "Registered HUD: ${hudObject.name} at ${getAttachmentPointName(attachmentPoint)}")
    }
    
    /**
     * Unregister a HUD object.
     */
    fun unregisterHUD(localId: Int) {
        val hud = allHuds.remove(localId) ?: return
        hudsByPoint[hud.attachmentPoint]?.remove(hud)
        
        if (_focusedHud.value?.localId == localId) {
            _focusedHud.value = null
        }
        
        Log.d(TAG, "Unregistered HUD: ${hud.name}")
    }
    
    /**
     * Get all HUDs at a specific attachment point.
     */
    fun getHUDsAtPoint(attachmentPoint: Int): List<HUDObject> {
        return hudsByPoint[attachmentPoint]?.toList() ?: emptyList()
    }
    
    /**
     * Get all HUDs.
     */
    fun getAllHUDs(): List<HUDObject> {
        return allHuds.values.toList()
    }
    
    /**
     * Get a specific HUD by local ID.
     */
    fun getHUD(localId: Int): HUDObject? = allHuds[localId]
    
    /**
     * Show or hide all HUDs.
     */
    fun setHUDsVisible(visible: Boolean) {
        _hudsVisible.value = visible
        Log.d(TAG, "HUDs visibility: $visible")
    }
    
    /**
     * Toggle HUD visibility.
     */
    fun toggleHUDsVisibility() {
        _hudsVisible.value = !_hudsVisible.value
    }
    
    /**
     * Touch/click a HUD element.
     * This triggers the HUD's scripts just like clicking in the 3D viewer.
     */
    fun touchHUD(localId: Int, touchPosition: LLVector3) {
        val hud = allHuds[localId] ?: return
        
        // Use ObjectManager to send the touch
        objectManager.touchObject(
            localId = localId,
            position = touchPosition,
            normal = LLVector3(0f, 0f, 1f),
            binormal = LLVector3(0f, 1f, 0f)
        )
        
        Log.d(TAG, "Touched HUD: ${hud.name} at $touchPosition")
    }
    
    /**
     * Set focus to a HUD for text input.
     */
    fun setFocusedHUD(localId: Int?) {
        _focusedHud.value = if (localId != null) allHuds[localId] else null
    }
    
    /**
     * Get the screen position for a HUD attachment point.
     */
    fun getScreenPositionForPoint(attachmentPoint: Int): HUDPosition? {
        return HUD_SCREEN_POSITIONS[attachmentPoint]
    }

    /**
     * Get saved layout entry for the current device class and orientation.
     */
    fun getLayoutEntry(attachmentPoint: Int): HudLayoutEntry? {
        val deviceClass = getCurrentDeviceClass()
        val orientation = getCurrentOrientation()
        return layoutConfig.layouts[deviceClass]
            ?.get(orientation)
            ?.get(attachmentPoint)
    }

    /**
     * Update layout entry for the current device class and orientation.
     */
    fun setLayoutEntry(attachmentPoint: Int, entry: HudLayoutEntry) {
        val deviceClass = getCurrentDeviceClass()
        val orientation = getCurrentOrientation()
        val deviceLayouts = layoutConfig.layouts.getOrPut(deviceClass) { mutableMapOf() }
        val orientationLayouts = deviceLayouts.getOrPut(orientation) { mutableMapOf() }
        orientationLayouts[attachmentPoint] = entry
    }

    /**
     * Persist layout config to preferences.
     */
    fun persistLayoutConfig() {
        saveLayoutConfig()
    }
    
    /**
     * Get ordered HUDs for rendering.
     * Returns HUDs grouped by their screen position (top-left, top-center, etc.)
     */
    fun getHUDsForRendering(): Map<HUDAlignment, List<HUDObject>> {
        if (!_hudsVisible.value) return emptyMap()
        
        return allHuds.values
            .groupBy { hud ->
                HUD_SCREEN_POSITIONS[hud.attachmentPoint]?.alignment ?: HUDAlignment.CENTER
            }
    }
    
    /**
     * Hit test to find which HUD is at screen coordinates.
     * @param screenX Normalized X (0-1)
     * @param screenY Normalized Y (0-1)
     * @return The HUD at that position, or null
     */
    fun hitTest(screenX: Float, screenY: Float): HUDObject? {
        if (!_hudsVisible.value) return null
        
        // Check each HUD's bounding box
        for (hud in allHuds.values) {
            val layoutEntry = getLayoutEntry(hud.attachmentPoint)
            val (left, right, top, bottom) = if (layoutEntry != null && layoutEntry.width > 0f && layoutEntry.height > 0f) {
                val rightBound = layoutEntry.x + layoutEntry.width
                val bottomBound = layoutEntry.y + layoutEntry.height
                Quad(layoutEntry.x, rightBound, layoutEntry.y, bottomBound)
            } else {
                val screenPos = HUD_SCREEN_POSITIONS[hud.attachmentPoint] ?: continue
                val halfWidth = hud.scale.x * 0.1f  // Approximate screen size
                val halfHeight = hud.scale.y * 0.1f
                Quad(
                    screenPos.x - halfWidth,
                    screenPos.x + halfWidth,
                    screenPos.y - halfHeight,
                    screenPos.y + halfHeight
                )
            }

            if (screenX in left..right && screenY in top..bottom) {
                return hud
            }
        }
        
        return null
    }
    
    /**
     * Update HUD position/scale from object update.
     */
    fun updateHUD(localId: Int, position: LLVector3?, scale: LLVector3?) {
        val hud = allHuds[localId] ?: return
        
        if (position != null) {
            hud.position = position
        }
        if (scale != null) {
            hud.scale = scale
        }
    }
    
    /**
     * Get diagnostic information about HUDs.
     */
    fun getDiagnostics(): HUDDiagnostics {
        return HUDDiagnostics(
            totalHUDs = allHuds.size,
            hudsVisible = _hudsVisible.value,
            hudsByPoint = hudsByPoint.mapValues { it.value.size },
            focusedHUD = _focusedHud.value?.name
        )
    }
    
    fun shutdown() {
        scope.cancel()
        allHuds.clear()
        hudsByPoint.clear()
    }

    private fun getCurrentDeviceClass(): HudDeviceClass {
        val smallestWidth = context.resources.configuration.smallestScreenWidthDp
        return if (smallestWidth >= TABLET_SMALLEST_WIDTH_DP) {
            HudDeviceClass.TABLET
        } else {
            HudDeviceClass.PHONE
        }
    }

    private fun getCurrentOrientation(): HudOrientation {
        val orientation = context.resources.configuration.orientation
        return if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            HudOrientation.LANDSCAPE
        } else {
            HudOrientation.PORTRAIT
        }
    }

    private fun loadLayoutConfig() {
        val rawJson = prefs.getString(KEY_LAYOUT_CONFIG, null) ?: return
        try {
            val root = JSONObject(rawJson)
            val version = root.optInt("version", 1)
            val layoutsObj = root.optJSONObject("layouts") ?: return
            val loadedLayouts = mutableMapOf<HudDeviceClass, MutableMap<HudOrientation, MutableMap<Int, HudLayoutEntry>>>()
            val deviceKeys = layoutsObj.keys()
            while (deviceKeys.hasNext()) {
                val deviceKey = deviceKeys.next()
                val deviceClass = runCatching { HudDeviceClass.valueOf(deviceKey) }.getOrNull() ?: continue
                val deviceObj = layoutsObj.optJSONObject(deviceKey) ?: continue
                val orientationKeys = deviceObj.keys()
                val orientationMap = mutableMapOf<HudOrientation, MutableMap<Int, HudLayoutEntry>>()
                while (orientationKeys.hasNext()) {
                    val orientationKey = orientationKeys.next()
                    val orientation = runCatching { HudOrientation.valueOf(orientationKey) }.getOrNull() ?: continue
                    val attachmentObj = deviceObj.optJSONObject(orientationKey) ?: continue
                    val attachmentKeys = attachmentObj.keys()
                    val entryMap = mutableMapOf<Int, HudLayoutEntry>()
                    while (attachmentKeys.hasNext()) {
                        val attachmentKey = attachmentKeys.next()
                        val attachmentPoint = attachmentKey.toIntOrNull() ?: continue
                        val entryObj = attachmentObj.optJSONObject(attachmentKey) ?: continue
                        val entry = HudLayoutEntry(
                            x = entryObj.optDouble("x", 0.0).toFloat(),
                            y = entryObj.optDouble("y", 0.0).toFloat(),
                            width = entryObj.optDouble("width", 0.0).toFloat(),
                            height = entryObj.optDouble("height", 0.0).toFloat()
                        )
                        entryMap[attachmentPoint] = entry
                    }
                    orientationMap[orientation] = entryMap
                }
                loadedLayouts[deviceClass] = orientationMap
            }
            layoutConfig = HudLayoutConfig(version = version, layouts = loadedLayouts)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse HUD layout config", e)
        }
    }

    private fun saveLayoutConfig() {
        try {
            val root = JSONObject()
            root.put("version", layoutConfig.version)
            val layoutsObj = JSONObject()
            layoutConfig.layouts.forEach { (deviceClass, orientationMap) ->
                val deviceObj = JSONObject()
                orientationMap.forEach { (orientation, attachmentMap) ->
                    val attachmentObj = JSONObject()
                    attachmentMap.forEach { (attachmentPoint, entry) ->
                        val entryObj = JSONObject()
                        entryObj.put("x", entry.x)
                        entryObj.put("y", entry.y)
                        entryObj.put("width", entry.width)
                        entryObj.put("height", entry.height)
                        attachmentObj.put(attachmentPoint.toString(), entryObj)
                    }
                    deviceObj.put(orientation.name, attachmentObj)
                }
                layoutsObj.put(deviceClass.name, deviceObj)
            }
            root.put("layouts", layoutsObj)
            prefs.edit().putString(KEY_LAYOUT_CONFIG, root.toString()).apply()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to save HUD layout config", e)
        }
    }
}

/**
 * Represents a HUD object.
 */
@Parcelize
data class HUDObject(
    val localId: Int,
    val fullId: UUID,
    val attachmentPoint: Int,
    var position: LLVector3,
    var scale: LLVector3,
    var name: String = "HUD"
) : Parcelable

/**
 * Screen position for HUD placement.
 */
data class HUDPosition(
    val x: Float,
    val y: Float,
    val alignment: HUDAlignment
)

/**
 * HUD layout config data.
 */
data class HudLayoutConfig(
    val version: Int = 1,
    val layouts: MutableMap<HudDeviceClass, MutableMap<HudOrientation, MutableMap<Int, HudLayoutEntry>>> = mutableMapOf()
)

/**
 * Normalized layout entry for a HUD attachment point.
 */
data class HudLayoutEntry(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)

enum class HudOrientation {
    PORTRAIT,
    LANDSCAPE
}

enum class HudDeviceClass {
    PHONE,
    TABLET
}

/**
 * HUD alignment options.
 */
enum class HUDAlignment {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    CENTER,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT
}

/**
 * Diagnostic data for HUD manager.
 */
data class HUDDiagnostics(
    val totalHUDs: Int,
    val hudsVisible: Boolean,
    val hudsByPoint: Map<Int, Int>,
    val focusedHUD: String?
)

private data class Quad(
    val left: Float,
    val right: Float,
    val top: Float,
    val bottom: Float
)
