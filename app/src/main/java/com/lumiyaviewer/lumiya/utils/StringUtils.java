package com.lumiyaviewer.lumiya.utils;

/**
 * String utility functions
 */
public class StringUtils {
    
    /**
     * Count occurrences of a character in a string
     */
    public static int countOccurrences(String str, char ch) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Check if string is null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Safe string comparison
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        return str1.equals(str2);
    }
    
    /**
     * Get string or default value if null/empty
     */
    public static String getOrDefault(String str, String defaultValue) {
        return isNullOrEmpty(str) ? defaultValue : str;
    }
}