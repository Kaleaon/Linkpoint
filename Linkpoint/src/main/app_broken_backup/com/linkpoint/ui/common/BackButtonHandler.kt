package com.linkpoint.ui.common

/**
 * Modern Kotlin BackButtonHandler interface
 * Interface for handling back button presses
 */
interface BackButtonHandler {
    /**
     * Called when back button is pressed
     * @return true if the event was handled, false otherwise
     */
    fun onBackButtonPressed(): Boolean
}
