package android.support.v4.util;

import android.support.annotation.RestrictTo;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class DebugUtils {
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x000c, code lost:
        r0 = r2.getClass().getName();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void buildShortClassTag(java.lang.Object r2, java.lang.StringBuilder r3) {
        /*
            if (r2 == 0) goto L_0x0030
            java.lang.Class r0 = r2.getClass()
            java.lang.String r0 = r0.getSimpleName()
            if (r0 != 0) goto L_0x0037
        L_0x000c:
            java.lang.Class r0 = r2.getClass()
            java.lang.String r0 = r0.getName()
            r1 = 46
            int r1 = r0.lastIndexOf(r1)
            if (r1 > 0) goto L_0x003e
        L_0x001c:
            r3.append(r0)
            r0 = 123(0x7b, float:1.72E-43)
            r3.append(r0)
            int r0 = java.lang.System.identityHashCode(r2)
            java.lang.String r0 = java.lang.Integer.toHexString(r0)
            r3.append(r0)
        L_0x002f:
            return
        L_0x0030:
            java.lang.String r0 = "null"
            r3.append(r0)
            goto L_0x002f
        L_0x0037:
            int r1 = r0.length()
            if (r1 <= 0) goto L_0x000c
            goto L_0x001c
        L_0x003e:
            int r1 = r1 + 1
            java.lang.String r0 = r0.substring(r1)
            goto L_0x001c
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.util.DebugUtils.buildShortClassTag(java.lang.Object, java.lang.StringBuilder):void");
    }
}
