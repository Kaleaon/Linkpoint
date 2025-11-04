// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson;

import java.util.Locale;
import java.lang.reflect.Field;

public enum FieldNamingPolicy implements FieldNamingStrategy
{
    IDENTITY {
        @Override
        public String translateName(final Field field) {
            return field.getName();
        }
    }, 
    LOWER_CASE_WITH_DASHES {
        @Override
        public String translateName(final Field field) {
            return FieldNamingPolicy.separateCamelCase(field.getName(), "-").toLowerCase(Locale.ENGLISH);
        }
    }, 
    LOWER_CASE_WITH_UNDERSCORES {
        @Override
        public String translateName(final Field field) {
            return FieldNamingPolicy.separateCamelCase(field.getName(), "_").toLowerCase(Locale.ENGLISH);
        }
    }, 
    UPPER_CAMEL_CASE {
        @Override
        public String translateName(final Field field) {
            return FieldNamingPolicy.upperCaseFirstLetter(field.getName());
        }
    }, 
    UPPER_CAMEL_CASE_WITH_SPACES {
        @Override
        public String translateName(final Field field) {
            return FieldNamingPolicy.upperCaseFirstLetter(FieldNamingPolicy.separateCamelCase(field.getName(), " "));
        }
    };
    
    private static String modifyString(final char c, String s, final int beginIndex) {
        if (beginIndex >= s.length()) {
            s = String.valueOf(c);
        }
        else {
            s = c + s.substring(beginIndex);
        }
        return s;
    }
    
    static String separateCamelCase(final String s, final String str) {
        int i = 0;
        final StringBuilder sb = new StringBuilder();
        while (i < s.length()) {
            final char char1 = s.charAt(i);
            if (Character.isUpperCase(char1) && sb.length() != 0) {
                sb.append(str);
            }
            sb.append(char1);
            ++i;
        }
        return sb.toString();
    }
    
    static String upperCaseFirstLetter(final String s) {
        int index = 0;
        final StringBuilder sb = new StringBuilder();
        final char char1 = s.charAt(0);
        int length;
        char char2;
        for (length = s.length(), char2 = char1; index < length - 1 && !Character.isLetter(char2); ++index, char2 = s.charAt(index)) {
            sb.append(char2);
        }
        if (Character.isUpperCase(char2)) {
            return s;
        }
        return sb.append(modifyString(Character.toUpperCase(char2), s, index + 1)).toString();
    }
}
