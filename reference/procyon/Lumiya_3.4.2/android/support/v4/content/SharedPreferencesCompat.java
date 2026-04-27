// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.content;

import android.support.annotation.NonNull;
import android.content.SharedPreferences$Editor;

public final class SharedPreferencesCompat
{
    private SharedPreferencesCompat() {
    }
    
    public static final class EditorCompat
    {
        private static EditorCompat sInstance;
        private final Helper mHelper;
        
        private EditorCompat() {
            this.mHelper = new Helper();
        }
        
        public static EditorCompat getInstance() {
            if (EditorCompat.sInstance == null) {
                EditorCompat.sInstance = new EditorCompat();
            }
            return EditorCompat.sInstance;
        }
        
        public void apply(@NonNull final SharedPreferences$Editor sharedPreferences$Editor) {
            this.mHelper.apply(sharedPreferences$Editor);
        }
        
        private static class Helper
        {
            Helper() {
            }
            
            public void apply(@NonNull final SharedPreferences$Editor sharedPreferences$Editor) {
                try {
                    sharedPreferences$Editor.apply();
                }
                catch (final AbstractMethodError abstractMethodError) {
                    sharedPreferences$Editor.commit();
                }
            }
        }
    }
}
