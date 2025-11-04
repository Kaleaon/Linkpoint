package com.lumiyaviewer.lumiya.slproto.https;

/* loaded from: classes.dex */
public class LLSDContentTypeDetector {

    /* loaded from: classes.dex */
    public enum LLSDContentType {
        llsdXML,
        llsdBinary;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static LLSDContentType[] valuesCustom() {
            return values();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x0058  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x005f  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0081  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00b9  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00b7  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x00b3  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x00af  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector.LLSDContentType DetectContentType(java.io.BufferedInputStream r11, java.lang.String r12) throws java.io.IOException {
        /*
            r10 = 3
            r3 = 1
            r1 = 0
            r0 = 64
            r11.mark(r0)
            byte[] r2 = new byte[r10]
            r2 = {x00ca: FILL_ARRAY_DATA , data: [-17, -69, -65} // fill-array
            r0 = 32
            byte[] r4 = new byte[r0]
            int r0 = r4.length
            int r5 = r11.read(r4, r1, r0)
            int r0 = r2.length
            if (r5 < r0) goto Lc7
            r0 = r1
        L1a:
            if (r0 >= r5) goto L8c
            int r6 = r2.length
            if (r0 >= r6) goto L8c
            r6 = r4[r0]
            r7 = r2[r0]
            if (r6 == r7) goto L8e
            r0 = r1
        L26:
            if (r0 == 0) goto Lc7
            int r0 = r2.length
        L29:
            java.lang.String r6 = new java.lang.String
            int r2 = r5 - r0
            java.lang.String r5 = "UTF-8"
            r6.<init>(r4, r0, r2, r5)
            r11.reset()
            long r4 = (long) r0
            r11.skip(r4)
            java.lang.String r2 = "<llsd>"
            boolean r2 = r6.startsWith(r2)
            if (r2 != 0) goto L4c
            java.lang.String r2 = "<?xml"
            boolean r2 = r6.startsWith(r2)
            if (r2 == 0) goto L91
        L4c:
            r2 = r3
            r4 = r1
        L4e:
            java.lang.String r7 = "LLSD: contentType '%s', detected binary %s, xml %s, skipBytes %d, firstString '%s'"
            r5 = 5
            java.lang.Object[] r8 = new java.lang.Object[r5]
            r8[r1] = r12
            if (r4 == 0) goto Laf
            java.lang.String r5 = "true"
        L5b:
            r8[r3] = r5
            if (r2 == 0) goto Lb3
            java.lang.String r5 = "true"
        L62:
            r9 = 2
            r8[r9] = r5
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r8[r10] = r0
            r0 = 4
            r8[r0] = r6
            com.lumiyaviewer.lumiya.Debug.Printf(r7, r8)
            if (r4 != 0) goto Lb7
        L73:
            if (r2 != 0) goto L7f
            java.lang.String r0 = "application/llsd+binary"
            boolean r0 = r12.equalsIgnoreCase(r0)
            if (r0 == 0) goto L7f
            r4 = r3
        L7f:
            if (r4 == 0) goto Lb9
            java.lang.String r0 = "LLSD: using binary parser"
            java.lang.Object[] r1 = new java.lang.Object[r1]
            com.lumiyaviewer.lumiya.Debug.Printf(r0, r1)
            com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector$LLSDContentType r0 = com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector.LLSDContentType.llsdBinary
            return r0
        L8c:
            r0 = r3
            goto L26
        L8e:
            int r0 = r0 + 1
            goto L1a
        L91:
            java.lang.String r2 = "<? LLSD/Binary ?>"
            boolean r2 = r6.startsWith(r2)
            if (r2 != 0) goto Lac
            java.lang.String r2 = "{"
            boolean r2 = r6.startsWith(r2)
            if (r2 != 0) goto Lac
            java.lang.String r2 = "<?llsd/binary"
            boolean r2 = r6.startsWith(r2)
            if (r2 == 0) goto Lc4
        Lac:
            r2 = r1
            r4 = r3
            goto L4e
        Laf:
            java.lang.String r5 = "false"
            goto L5b
        Lb3:
            java.lang.String r5 = "false"
            goto L62
        Lb7:
            r2 = r3
            goto L73
        Lb9:
            java.lang.String r0 = "LLSD: using XML parser"
            java.lang.Object[] r1 = new java.lang.Object[r1]
            com.lumiyaviewer.lumiya.Debug.Printf(r0, r1)
            com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector$LLSDContentType r0 = com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector.LLSDContentType.llsdXML
            return r0
        Lc4:
            r2 = r1
            r4 = r1
            goto L4e
        Lc7:
            r0 = r1
            goto L29
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector.DetectContentType(java.io.BufferedInputStream, java.lang.String):com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector$LLSDContentType");
    }
}
