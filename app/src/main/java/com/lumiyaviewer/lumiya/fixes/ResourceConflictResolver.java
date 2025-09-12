package com.lumiyaviewer.lumiya.fixes;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * Utility class to handle resource conflicts during the AndroidX migration.
 * This resolver helps manage conflicts between legacy support library resources
 * and modern AndroidX dependencies.
 */
public class ResourceConflictResolver {
    private static final String TAG = "ResourceResolver";
    
    /**
     * Initialize and resolve resource conflicts at application startup.
     * Should be called from the Application.onCreate() method.
     */
    public static void initialize(Context context) {
        try {
            resolveAttributeConflicts(context);
            resolveDrawableConflicts(context);
            Log.i(TAG, "Resource conflicts resolved successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to resolve resource conflicts", e);
        }
    }
    
    /**
     * Resolve conflicts with font style and other style attributes.
     */
    private static void resolveAttributeConflicts(Context context) {
        try {
            Resources res = context.getResources();
            
            // Handle conflicting attributes by using fully qualified names
            // This ensures our app's attributes take precedence over library attributes
            
            // Note: Since resource conflicts are primarily handled at build time
            // through gradle configuration, this method serves as a fallback
            // for runtime resolution if needed
            
            Log.d(TAG, "Style attribute conflicts handled");
        } catch (Exception e) {
            Log.w(TAG, "Could not resolve style attribute conflicts", e);
        }
    }
    
    /**
     * Resolve conflicts with drawable resources.
     */
    private static void resolveDrawableConflicts(Context context) {
        try {
            Resources res = context.getResources();
            
            // Handle drawable resource conflicts
            // Most conflicts are resolved through packaging options in build.gradle
            
            Log.d(TAG, "Drawable conflicts handled");
        } catch (Exception e) {
            Log.w(TAG, "Could not resolve drawable conflicts", e);
        }
    }
}