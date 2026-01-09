package com.linkpoint.ui.render

import android.view.View

interface OnHoverListenerCompat {
    fun onHoverEnter(view: View): Boolean

    fun onHoverExit(view: View): Boolean
}