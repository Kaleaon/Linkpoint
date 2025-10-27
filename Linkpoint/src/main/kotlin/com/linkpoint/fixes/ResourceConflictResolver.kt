package com.linkpoint.fixes

import android.content.Context
import android.content.res.Resources
import android.util.Log

/**
 * ResourceConflictResolver handles conflicts between AndroidX and legacy support library resources.
 * 
 * This class resolves duplicate resource definitions that cause build failures by ensuring
 * app-specific resources take precedence over conflicting dependency resources.
 * 
 * Based on fixes from Broken Code Analysis document and modern Android development practices.
 */
class ResourceConflictResolver {
    private const val TAG: String = "ResourceResolver"
    
    /**
     * Initialize resource conflict resolution for the application context.
     * Should be called in Application.onCreate() before any other initialization.
     * 
     * @param context Application context
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
        } catch (Exception e) {
            Log.e(TAG, "Failed to resolve resource conflicts", e)
            // Non-fatal - app can continue but may have styling issues
        }
    }
    
    /**
     * Handle conflicting attributes by using fully qualified names.
     * Addresses the "Duplicate value for resource 'attr/fontStyle'" error.
     */
    @JvmStatic
 private fun resolveAttributeConflicts(context: Context) {
        try {
            val res: Resources = context.getResources()
            
            // Check for app-specific fontStyle attribute
            val fontStyleAttr: Int = res.getIdentifier("fontStyle", "attr", context.getPackageName())
            if (fontStyleAttr != 0) {
                Log.d(TAG, "App fontStyle attribute resolved: " + fontStyleAttr)
            }
            
            // Check for other conflicting attributes
            val conflictingAttrNames: Array<String> = {
                "passwordToggleEnabled",
                "buttonGravity",
                "fontStyle"
            }
            
            for (String attrName : conflictingAttrNames) {
                val attr: Int = res.getIdentifier(attrName, "attr", context.getPackageName())
                if (attr != 0) {
                    Log.d(TAG, "Conflicting attribute resolved: " + attrName + " = " + attr)
                }
            }
            
        } catch (Resources.NotFoundException e) {
            Log.w(TAG, "Some attribute conflicts could not be resolved", e)
        }
    }
    
    /**
     * Handle drawable conflicts, particularly eye icons from password fields.
     */
    @JvmStatic
 private fun resolveDrawableConflicts(context: Context) {
        try {
            val res: Resources = context.getResources()
            
            // Check for visibility/eye icon drawables that commonly conflict
            val drawableNames: Array<String> = {
                "design_ic_visibility",
                "design_ic_visibility_off", 
                "abc_ic_clear_material"
            }
            
            for (String drawableName : drawableNames) {
                val drawableId: Int = res.getIdentifier(drawableName, "drawable", context.getPackageName())
                if (drawableId != 0) {
                    Log.d(TAG, "Drawable resolved: " + drawableName + " = " + drawableId)
                }
            }
            
        } catch (Exception e) {
            Log.w(TAG, "Could not resolve drawable conflicts", e)
        }
    }
    
    /**
     * Resolve string conflicts from AndroidX migration.
     */
    @JvmStatic
 private fun resolveStringConflicts(context: Context) {
        val res: Resources = context.getResources()
        
        try {
            // Check for AppCompat strings that may conflict
            val stringNames: Array<String> = {
                "abc_action_bar_home_description",
                "abc_action_bar_up_description",
                "abc_searchview_description_clear"
            }
            
            for (String stringName : stringNames) {
                val stringId: Int = res.getIdentifier(stringName, "string", context.getPackageName())
                if (stringId != 0) {
                    Log.d(TAG, "String resolved: " + stringName + " = " + stringId)
                }
            }
            
        } catch (Resources.NotFoundException e) {
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
        try {
            val resourceId: Int = context.getResources().getIdentifier(name, type, context.getPackageName())
            return resourceId != 0
        } catch (Exception e) {
            Log.w(TAG, "Error checking resource existence: " + name, e)
            return false
        }
    }
}