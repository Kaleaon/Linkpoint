package android.support.v4.util;

import android.support.annotation.RestrictTo;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class DebugUtils {
    public static void buildShortClassTag(Object obj, StringBuilder sb) {
        if (obj == null) {
            sb.append("null");
            return;
        }
        
        String name = obj.getClass().getSimpleName();
        if (name == null || name.length() <= 0) {
            name = obj.getClass().getName();
            int lastIndexOf = name.lastIndexOf('.');
            if (lastIndexOf > 0) {
                name = name.substring(lastIndexOf + 1);
            }
        }
        
        sb.append(name);
        sb.append('{');
        sb.append(Integer.toHexString(System.identityHashCode(obj)));
        sb.append('}');
    }
}
