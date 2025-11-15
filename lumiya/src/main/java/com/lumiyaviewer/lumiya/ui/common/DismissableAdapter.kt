package com.lumiyaviewer.lumiya.ui.common

interface DismissableAdapter {
    fun canDismiss(position: Int): Boolean

    fun onDismiss(position: Int)
}
