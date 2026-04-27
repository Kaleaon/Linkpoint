package com.linkpoint.ui.world

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.linkpoint.hud.HUDOverlayView

/**
 * Single interop wrapper for world viewport rendering surfaces.
 * All backend surface views are attached through this host.
 */
class WorldViewportHost @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val viewportContainer = FrameLayout(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    val hudOverlay: HUDOverlayView = HUDOverlayView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    init {
        addView(viewportContainer)
        addView(hudOverlay)
    }

    fun attachViewport(view: View) {
        if (view.parent === viewportContainer && viewportContainer.childCount == 1) return
        viewportContainer.removeAllViews()
        viewportContainer.addView(view)
    }

    fun clearViewport() {
        viewportContainer.removeAllViews()
    }

    fun setHudAttachmentsVisible(visible: Boolean) {
        hudOverlay.visibility = if (visible) View.VISIBLE else View.GONE
    }
}
