package com.google.vr.ndk.base;

import android.util.Log;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Version {
    public static final Version CURRENT = parse("1.10.0");
    public static final String TAG = BuildConstants.class.getSimpleName();
    public final int majorVersion;
    public final int minorVersion;
    public final int patchVersion;

    public Version(int i, int i2, int i3) {
        this.majorVersion = i;
        this.minorVersion = i2;
        this.patchVersion = i3;
    }

    public static Version parse(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        Matcher matcher = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)").matcher(str);
        if (matcher.matches()) {
            return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
        }
        String str2 = TAG;
        String valueOf = String.valueOf(str);
        Log.w(str2, valueOf.length() == 0 ? new String("Failed to parse version from: ") : "Failed to parse version from: ".concat(valueOf));
        return null;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof Version)) {
            return false;
        }
        Version version = (Version) obj;
        return this.majorVersion == version.majorVersion && this.minorVersion == version.minorVersion && this.patchVersion == version.patchVersion;
    }

    public final int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(this.majorVersion), Integer.valueOf(this.minorVersion), Integer.valueOf(this.patchVersion)});
    }

    public final boolean isAtLeast(Version version) {
        if (this.majorVersion > version.majorVersion) {
            return true;
        }
        if (this.majorVersion < version.majorVersion) {
            return false;
        }
        if (this.minorVersion > version.minorVersion) {
            return true;
        }
        if (this.minorVersion >= version.minorVersion) {
            return this.patchVersion > version.patchVersion || this.patchVersion >= version.patchVersion;
        }
        return false;
    }

    public final String toString() {
        return String.format("%d.%d.%d", new Object[]{Integer.valueOf(this.majorVersion), Integer.valueOf(this.minorVersion), Integer.valueOf(this.patchVersion)});
    }
}
