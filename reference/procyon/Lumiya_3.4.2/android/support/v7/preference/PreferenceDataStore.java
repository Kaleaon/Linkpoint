// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import java.util.Set;
import android.support.annotation.Nullable;

public abstract class PreferenceDataStore
{
    public boolean getBoolean(final String s, final boolean b) {
        return b;
    }
    
    public float getFloat(final String s, final float n) {
        return n;
    }
    
    public int getInt(final String s, final int n) {
        return n;
    }
    
    public long getLong(final String s, final long n) {
        return n;
    }
    
    @Nullable
    public String getString(final String s, @Nullable final String s2) {
        return s2;
    }
    
    @Nullable
    public Set<String> getStringSet(final String s, @Nullable final Set<String> set) {
        return set;
    }
    
    public void putBoolean(final String s, final boolean b) {
        throw new UnsupportedOperationException("Not implemented on this data store");
    }
    
    public void putFloat(final String s, final float n) {
        throw new UnsupportedOperationException("Not implemented on this data store");
    }
    
    public void putInt(final String s, final int n) {
        throw new UnsupportedOperationException("Not implemented on this data store");
    }
    
    public void putLong(final String s, final long n) {
        throw new UnsupportedOperationException("Not implemented on this data store");
    }
    
    public void putString(final String s, @Nullable final String s2) {
        throw new UnsupportedOperationException("Not implemented on this data store");
    }
    
    public void putStringSet(final String s, @Nullable final Set<String> set) {
        throw new UnsupportedOperationException("Not implemented on this data store");
    }
}
