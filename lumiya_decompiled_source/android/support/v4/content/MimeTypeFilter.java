package android.support.v4.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
/* loaded from: classes.dex */
public final class MimeTypeFilter {
    private MimeTypeFilter() {
    }

    public static String matches(@Nullable String str, @NonNull String[] strArr) {
        if (str != null) {
            String[] split = str.split("/");
            for (String str2 : strArr) {
                if (mimeTypeAgainstFilter(split, str2.split("/"))) {
                    return str2;
                }
            }
            return null;
        }
        return null;
    }

    public static String matches(@Nullable String[] strArr, @NonNull String str) {
        if (strArr != null) {
            String[] split = str.split("/");
            for (String str2 : strArr) {
                if (mimeTypeAgainstFilter(str2.split("/"), split)) {
                    return str2;
                }
            }
            return null;
        }
        return null;
    }

    public static boolean matches(@Nullable String str, @NonNull String str2) {
        if (str != null) {
            return mimeTypeAgainstFilter(str.split("/"), str2.split("/"));
        }
        return false;
    }

    public static String[] matchesMany(@Nullable String[] strArr, @NonNull String str) {
        if (strArr != null) {
            ArrayList arrayList = new ArrayList();
            String[] split = str.split("/");
            for (String str2 : strArr) {
                if (mimeTypeAgainstFilter(str2.split("/"), split)) {
                    arrayList.add(str2);
                }
            }
            return (String[]) arrayList.toArray(new String[arrayList.size()]);
        }
        return new String[0];
    }

    private static boolean mimeTypeAgainstFilter(@NonNull String[] strArr, @NonNull String[] strArr2) {
        if (strArr2.length == 2) {
            if (strArr2[0].isEmpty() || strArr2[1].isEmpty()) {
                throw new IllegalArgumentException("Ill-formatted MIME type filter. Type or subtype empty.");
            }
            if (strArr.length == 2) {
                if ("*".equals(strArr2[0]) || strArr2[0].equals(strArr[0])) {
                    return "*".equals(strArr2[1]) || strArr2[1].equals(strArr[1]);
                }
                return false;
            }
            return false;
        }
        throw new IllegalArgumentException("Ill-formatted MIME type filter. Must be type/subtype.");
    }
}
