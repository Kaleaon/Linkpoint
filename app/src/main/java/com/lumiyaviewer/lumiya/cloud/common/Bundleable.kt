package com.lumiyaviewer.lumiya.cloud.common

import android.os.Bundle

/**
 * Modern Kotlin Bundleable interface
 * Defines objects that can be converted to Android Bundles
 */
interface Bundleable {
    /**
     * Converts this object to an Android Bundle
     * @return Bundle representation of this object
     */
    fun toBundle(): Bundle
}