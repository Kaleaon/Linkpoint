package com.linkpoint.slproto.https

class LLSDContentTypeDetector {

    enum LLSDContentType {
        llsdXML,
        llsdBinary
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0058  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x005f  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0081  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0091  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00af  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00b3  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00b7  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00b9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    com.linkpoint.slproto.https.LLSDContentTypeDetector.LLSDContentType DetectContentType(java.io.BufferedInputStream r11, java.lang.String r12) throws java.io.IOException {
        /*
            r10 = 3
            r3 = 1
            r1 = 0
            r0 = 64
            r11.mark(r0)
            ByteArray r2 = byte[r10]
            r2 = {-17, -69, -65} // fill-array
            r0 = 32
            ByteArray r4 = byte[r0]
            var r0: Int = r4.size
            var r5: Int = r11.read(r4, r1, r0)
            var r0: Int = r2.size
            if (r5 < r0) goto L_0x00c7
            r0 = r1
        L_0x001a:
            if (r0 >= r5) goto L_0x008c
            var r6: Int = r2.size
            if (r0 >= r6) goto L_0x008c
            byte r6 = r4[r0]
            byte r7 = r2[r0]
            if (r6 == r7) goto L_0x008e
            r0 = r1
        L_0x0026:
            if (r0 == 0) goto L_0x00c7
            var r0: Int = r2.size
        L_0x0029:
            java.lang.String r6 = java.lang.String
            var r2: Int = r5 - r0
            java.lang.String r5 = "UTF-8"
            r6.<init>(r4, r0, r2, r5)
            r11.reset()
            var r4: Long = (Long) r0
            r11.skip(r4)
            java.lang.String r2 = "<llsd>"
            var r2: Boolean = r6.startsWith(r2)
            if (r2 != 0) goto L_0x004c
            java.lang.String r2 = "<?xml"
            var r2: Boolean = r6.startsWith(r2)
            if (r2 == 0) goto L_0x0091
        L_0x004c:
            r2 = r3
            r4 = r1
        L_0x004e:
            java.lang.String r7 = "LLSD: contentType '%s', detected binary %s, xml %s, skipBytes %d, firstString '%s'"
            r5 = 5
            java.lang.Array<Any> r8 = java.lang.Object[r5]
            r8[r1] = r12
            if (r4 == 0) goto L_0x00af
            java.lang.String r5 = "true"
        L_0x005b:
            r8[r3] = r5
            if (r2 == 0) goto L_0x00b3
            java.lang.String r5 = "true"
        L_0x0062:
            r9 = 2
            r8[r9] = r5
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r8[r10] = r0
            r0 = 4
            r8[r0] = r6
            com.linkpoint.Debug.Printf(r7, r8)
            if (r4 != 0) goto L_0x00b7
        L_0x0073:
            if (r2 != 0) goto L_0x007f
            java.lang.String r0 = "application/llsd+binary"
            var r0: Boolean = r12.equalsIgnoreCase(r0)
            if (r0 == 0) goto L_0x007f
            r4 = r3
        L_0x007f:
            if (r4 == 0) goto L_0x00b9
            java.lang.String r0 = "LLSD: using binary parser"
            java.lang.Array<Any> r1 = java.lang.Object[r1]
            com.linkpoint.Debug.Printf(r0, r1)
            com.linkpoint.slproto.https.LLSDContentTypeDetector$LLSDContentType r0 = com.linkpoint.slproto.https.LLSDContentTypeDetector.LLSDContentType.llsdBinary
            return r0
        L_0x008c:
            r0 = r3
            goto L_0x0026
        L_0x008e:
            var r0: Int = r0 + 1
            goto L_0x001a
        L_0x0091:
            java.lang.String r2 = "<? LLSD/Binary ?>"
            var r2: Boolean = r6.startsWith(r2)
            if (r2 != 0) goto L_0x00ac
            java.lang.String r2 = "{"
            var r2: Boolean = r6.startsWith(r2)
            if (r2 != 0) goto L_0x00ac
            java.lang.String r2 = "<?llsd/binary"
            var r2: Boolean = r6.startsWith(r2)
            if (r2 == 0) goto L_0x00c4
        L_0x00ac:
            r2 = r1
            r4 = r3
            goto L_0x004e
        L_0x00af:
            java.lang.String r5 = "false"
            goto L_0x005b
        L_0x00b3:
            java.lang.String r5 = "false"
            goto L_0x0062
        L_0x00b7:
            r2 = r3
            goto L_0x0073
        L_0x00b9:
            java.lang.String r0 = "LLSD: using XML parser"
            java.lang.Array<Any> r1 = java.lang.Object[r1]
            com.linkpoint.Debug.Printf(r0, r1)
            com.linkpoint.slproto.https.LLSDContentTypeDetector$LLSDContentType r0 = com.linkpoint.slproto.https.LLSDContentTypeDetector.LLSDContentType.llsdXML
            return r0
        L_0x00c4:
            r2 = r1
            r4 = r1
            goto L_0x004e
        L_0x00c7:
            r0 = r1
            goto L_0x0029
        */
        throw UnsupportedOperationException("Method not decompiled: com.linkpoint.slproto.https.LLSDContentTypeDetector.DetectContentType(java.io.BufferedInputStream, java.lang.String):com.linkpoint.slproto.https.LLSDContentTypeDetector$LLSDContentType")
    }
}
