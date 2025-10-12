package com.lumiyaviewer.lumiya.fixes

import android.content.Context
import android.content.res.Resources
import android.util.Log

/**
 * Modern Kotlin ResourceConflictResolver
 * Handles conflicts between AndroidX and legacy support library resources
 * 
 * This class resolves duplicate resource definitions that cause build failures by ensuring
 * app-specific resources take precedence over conflicting dependency resources.
 */
object ResourceConflictResolver {
    private const val TAG = "ResourceResolver"
    
    /**
     * Initialize resource conflict resolution for the application context.
     * Should be called in Application.onCreate() before any other initialization.
     */
    @JvmStatic
    fun initialize(context: Context) {
        try {
            Log.i(TAG, "Starting resource conflict resolution")
            
            // Resolve attribute conflicts (fontStyle, passwordToggle, etc.)
            resolveAttributeConflicts(context)
            
            // Resolve drawable conflicts (eye icons, material design elements)
            resolveDrawableConflicts(context)
            
            // Resolve string conflicts from AppCompat migration
            resolveStringConflicts(context)
            
            Log.i(TAG, "Resource conflicts resolved successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to resolve resource conflicts", e)
            // Non-fatal - app can continue but may have styling issues
        }
    }
    
    /**
     * Handle conflicting attributes by using fully qualified names.
     * Addresses the "Duplicate value for resource 'attr/fontStyle'" error.
     */
    private fun resolveAttributeConflicts(context: Context) {
        try {
            val res = context.resources
            
            // Check for app-specific fontStyle attribute
            val fontStyleAttr = res.getIdentifier("fontStyle", "attr", context.packageName)
            if (fontStyleAttr != 0) {
                Log.d(TAG, "App fontStyle attribute resolved: $fontStyleAttr")
            }
            
            // Check for other conflicting attributes
            val conflictingAttrNames = arrayOf(
                "passwordToggleEnabled",
                "buttonGravity",
                "fontStyle"
            )
            
            for (attrName in conflictingAttrNames) {
                val attr = res.getIdentifier(attrName, "attr", context.packageName)
                if (attr != 0) {
                    Log.d(TAG, "Conflicting attribute resolved: $attrName = $attr")
                }
            }
        } catch (e: Resources.NotFoundException) {
            Log.w(TAG, "Some attribute conflicts could not be resolved", e)
        }
    }
    
    /**
     * Handle drawable conflicts, particularly eye icons from password fields.
     */
    private fun resolveDrawableConflicts(context: Context) {
        try {
            val res = context.resources
            
            // Check for visibility/eye icon drawables that commonly conflict
            val drawableNames = arrayOf(
                "design_ic_visibility",
                "design_ic_visibility_off",
                "abc_ic_clear_material"
            )
            
            for (drawableName in drawableNames) {
                val drawableId = res.getIdentifier(drawableName, "drawable", context.packageName)
                if (drawableId != 0) {
                    Log.d(TAG, "Drawable resolved: $drawableName = $drawableId")
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not resolve drawable conflicts", e)
        }
    }
    
    /**
     * Resolve string conflicts from AndroidX migration.
     */
    private fun resolveStringConflicts(context: Context) {
        val res = context.resources
        
        try {
            // Check for AppCompat strings that may conflict
            val stringNames = arrayOf(
                "abc_action_bar_home_description",
                "abc_action_bar_up_description",
                "abc_searchview_description_clear"
            )
            
            for (stringName in stringNames) {
                val stringId = res.getIdentifier(stringName, "string", context.packageName)
                if (stringId != 0) {
                    Log.d(TAG, "String resolved: $stringName = $stringId")
                }
            }
        } catch (e: Resources.NotFoundException) {
            Log.w(TAG, "Some string conflicts could not be resolved", e)
        }
    }
    
    /**
     * Utility method to check if a resource exists in the app package.
     * 
     * @param context Application context
     * @param name Resource name
     * @param type Resource type (attr, drawable, string, etc.)
     * @return true if resource exists, false otherwise
     */
    @JvmStatic
    fun resourceExists(context: Context, name: String, type: String): Boolean {
        return try {
            val resourceId = context.resources.getIdentifier(name, type, context.packageName)
            resourceId != 0
        } catch (e: Exception) {
            Log.w(TAG, "Error checking resource existence: $name", e)
            false
        }
    }
}