// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

import java.util.Objects;
import java.util.regex.Matcher;
import android.util.Log;
import java.util.regex.Pattern;

public final class Version
{
    public static final Version CURRENT;
    public static final String TAG;
    public final int majorVersion;
    public final int minorVersion;
    public final int patchVersion;
    
    static {
        TAG = BuildConstants.class.getSimpleName();
        CURRENT = parse("1.10.0");
    }
    
    public Version(final int majorVersion, final int minorVersion, final int patchVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchVersion = patchVersion;
    }
    
    public static Version parse(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        final Matcher matcher = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)").matcher(str);
        if (matcher.matches()) {
            return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
        }
        final String tag = Version.TAG;
        str = String.valueOf(str);
        if (str.length() == 0) {
            str = new String("Failed to parse version from: ");
        }
        else {
            str = "Failed to parse version from: ".concat(str);
        }
        Log.w(tag, str);
        return null;
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (o instanceof Version) {
            final Version version = (Version)o;
            return this.majorVersion == version.majorVersion && this.minorVersion == version.minorVersion && this.patchVersion == version.patchVersion;
        }
        return false;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(this.majorVersion, this.minorVersion, this.patchVersion);
    }
    
    public final boolean isAtLeast(final Version version) {
        return this.majorVersion > version.majorVersion || (this.majorVersion >= version.majorVersion && (this.minorVersion > version.minorVersion || (this.minorVersion >= version.minorVersion && (this.patchVersion > version.patchVersion || this.patchVersion >= version.patchVersion))));
    }
    
    @Override
    public final String toString() {
        return String.format("%d.%d.%d", this.majorVersion, this.minorVersion, this.patchVersion);
    }
}
