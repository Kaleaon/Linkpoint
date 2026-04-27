package com.lumiyaviewer.lumiya.slproto.auth;
import java.util.*;

import com.lumiyaviewer.lumiya.utils.HashUtils;
import java.io.IOException;

public class SLAuth {

    private static class LoginRequestField {
        public final String name;
        public final String value;

        private LoginRequestField(String str, String str2) {
            this.name = str;
            this.value = str2;
        }

        /* synthetic */ LoginRequestField(String str, String str2, LoginRequestField loginRequestField) {
            this(str, str2);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x007a  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0091  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00d2  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00ea A[LOOP:1: B:26:0x00e7->B:28:0x00ea, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x019c  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x004b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply SendLoginRequest(com.lumiyaviewer.lumiya.slproto.auth.SLAuthParams r12) throws java.io.IOException {
        /*
            r11 = this;
            r2 = 0
            r5 = 0
            java.lang.String r6 = r12.passwordHash
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "md5 hash: "
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.StringBuilder r0 = r0.append(r6)
            java.lang.String r0 = r0.toString()
            com.lumiyaviewer.lumiya.Debug.Log(r0)
            java.lang.String r0 = "last"
            java.lang.String r1 = r12.startLocation
            if (r1 == 0) goto L_0x0376
            java.lang.String r1 = r12.startLocation
            java.lang.String r3 = "first"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x005e
            java.lang.String r0 = "home"
            r1 = r0
        L_0x0031:
            java.util.LinkedList r7 = new java.util.LinkedList
            r7.<init>()
            java.lang.String r0 = r12.loginName
            java.lang.String r8 = r0.trim()
            int r0 = r8.length()
            java.lang.String r9 = " ._"
            r3 = r0
            r0 = r2
        L_0x0045:
            int r4 = r9.length()
            if (r0 >= r4) goto L_0x006d
            int r4 = r0 + 1
            java.lang.String r4 = r9.substring(r0, r4)
            int r4 = r8.indexOf(r4)
            r10 = -1
            if (r4 == r10) goto L_0x005b
            if (r4 >= r3) goto L_0x005b
            r3 = r4
        L_0x005b:
            int r0 = r0 + 1
            goto L_0x0045
        L_0x005e:
            java.lang.String r1 = r12.startLocation
            java.lang.String r3 = "uri:"
            boolean r1 = r1.startsWith(r3)
            if (r1 == 0) goto L_0x0376
            java.lang.String r0 = r12.startLocation
            r1 = r0
            goto L_0x0031
        L_0x006d:
            java.lang.String r4 = r8.substring(r2, r3)
            java.lang.String r0 = ""
            int r9 = r8.length()
            if (r3 >= r9) goto L_0x0080
            int r0 = r3 + 1
            java.lang.String r0 = r8.substring(r0)
        L_0x0080:
            java.lang.String r3 = r4.trim()
            java.lang.String r0 = r0.trim()
            java.lang.String r4 = ""
            boolean r4 = r0.equalsIgnoreCase(r4)
            if (r4 == 0) goto L_0x0094
            java.lang.String r0 = "Resident"
        L_0x0094:
            com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField r4 = new com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField
            java.lang.String r8 = "first"
            r4.<init>(r8, r3, r5)
            r7.add(r4)
            com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField r3 = new com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField
            java.lang.String r4 = "last"
            r3.<init>(r4, r0, r5)
            r7.add(r3)
            com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField r0 = new com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField
            java.lang.String r3 = "passwd"
            r0.<init>(r3, r6, r5)
            r7.add(r0)
            com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField r0 = new com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField
            java.lang.String r3 = "start"
            r0.<init>(r3, r1, r5)
            r7.add(r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "Lumiya "
            java.lang.StringBuilder r3 = r0.append(r3)
            boolean r0 = com.lumiyaviewer.lumiya.Debug.isDebugBuild()
            if (r0 == 0) goto L_0x0101
            java.lang.String r0 = "Test"
        L_0x00d5:
            java.lang.StringBuilder r0 = r3.append(r0)
            java.lang.String r4 = r0.toString()
            java.lang.String r3 = com.lumiyaviewer.lumiya.LumiyaApp.getAppVersion()
            r0 = 46
            int r0 = com.lumiyaviewer.lumiya.utils.StringUtils.countOccurrences(r3, r0)
        L_0x00e7:
            r6 = 3
            if (r0 >= r6) goto L_0x0105
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.StringBuilder r3 = r6.append(r3)
            java.lang.String r6 = ".0"
            java.lang.StringBuilder r3 = r3.append(r6)
            java.lang.String r3 = r3.toString()
            int r0 = r0 + 1
            goto L_0x00e7
        L_0x0101:
            java.lang.String r0 = "Release"
            goto L_0x00d5
        L_0x0105:
            java.lang.String r0 = "Auth: viewer channel '%s', version '%s'"
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r2] = r4
            r8 = 1
            r6[r8] = r3
            com.lumiyaviewer.lumiya.Debug.Printf(r0, r6)
            com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField r0 = new com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField
            java.lang.String r6 = "channel"
            r0.<init>(r6, r4, r5)
            r7.add(r0)
            com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField r0 = new com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField
            java.lang.String r4 = "version"
            r0.<init>(r4, r3, r5)
            r7.add(r0)
            com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField r0 = new com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField
            java.lang.String r3 = "platform"
            java.lang.String r4 = "Android"
            r0.<init>(r3, r4, r5)
            r7.add(r0)
            com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField r0 = new com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField
            java.lang.String r3 = "platform_version"
            java.lang.String r4 = android.os.Build.VERSION.RELEASE
            r0.<init>(r3, r4, r5)
            r7.add(r0)
            com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField r0 = new com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField
            java.lang.String r3 = "mac"
            java.lang.String r4 = "android_id"
            java.lang.String r4 = com.lumiyaviewer.lumiya.utils.HashUtils.MD5_Hash(r4)
            r0.<init>(r3, r4, r5)
            r7.add(r0)
            com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField r0 = new com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField
            java.lang.String r3 = "user-agent"
            java.lang.String r4 = "Lumiya"
            r0.<init>(r3, r4, r5)
            r7.add(r0)
            com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField r0 = new com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField
            java.lang.String r3 = "id0"
            java.util.UUID r4 = r12.clientID
            java.lang.String r4 = r4.toString()
            r0.<init>(r3, r4, r5)
            r7.add(r0)
            com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField r0 = new com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField
            java.lang.String r3 = "agree_to_tos"
            java.lang.String r4 = "true"
            r0.<init>(r3, r4, r5)
            r7.add(r0)
            com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField r0 = new com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField
            java.lang.String r3 = "viewer_digest"
            java.lang.String r4 = "f50cfcc3-d6ce-4f16-a822-b91271de4c48"
            r0.<init>(r3, r4, r5)
            r7.add(r0)
            java.lang.String r3 = r12.loginURL
            java.lang.String r0 = "login_to_simulator"
            r4 = r3
            r3 = r0
            r0 = r5
        L_0x0199:
            r5 = 5
            if (r2 >= r5) goto L_0x0375
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r0 = "<?xml version=\"1.0\"?>\n"
            r5.append(r0)
            java.lang.String r0 = "<methodCall>"
            r5.append(r0)
            java.lang.String r0 = "<methodName>"
            java.lang.StringBuilder r0 = r5.append(r0)
            java.lang.StringBuilder r0 = r0.append(r3)
            java.lang.String r3 = "</methodName>"
            r0.append(r3)
            java.lang.String r0 = "<params>"
            r5.append(r0)
            java.lang.String r0 = "<param>"
            r5.append(r0)
            java.lang.String r0 = "<value>"
            r5.append(r0)
            java.lang.String r0 = "<struct>"
            r5.append(r0)
            java.util.Iterator r3 = r7.iterator()
        L_0x01da:
            boolean r0 = r3.hasNext()
            if (r0 == 0) goto L_0x0221
            java.lang.Object r0 = r3.next()
            com.lumiyaviewer.lumiya.slproto.auth.SLAuth$LoginRequestField r0 = (com.lumiyaviewer.lumiya.slproto.auth.SLAuth.LoginRequestField) r0
            java.lang.String r6 = "<member>"
            r5.append(r6)
            java.lang.String r6 = "<name>"
            r5.append(r6)
            java.lang.String r6 = r0.name
            r5.append(r6)
            java.lang.String r6 = "</name>"
            r5.append(r6)
            java.lang.String r6 = "<value>"
            r5.append(r6)
            java.lang.String r6 = "<string>"
            r5.append(r6)
            java.lang.String r0 = r0.value
            r5.append(r0)
            java.lang.String r0 = "</string>"
            r5.append(r0)
            java.lang.String r0 = "</value>"
            r5.append(r0)
            java.lang.String r0 = "</member>"
            r5.append(r0)
            goto L_0x01da
        L_0x0221:
            java.lang.String r0 = "<member>"
            r5.append(r0)
            java.lang.String r0 = "<name>options</name>"
            r5.append(r0)
            java.lang.String r0 = "<value>"
            r5.append(r0)
            java.lang.String r0 = "<array><data>"
            r5.append(r0)
            java.lang.String r0 = "<value><string>buddy-list</string></value>"
            r5.append(r0)
            java.lang.String r0 = "<value><string>display_names</string></value>"
            r5.append(r0)
            java.lang.String r0 = "<value><string>inventory-root</string></value>"
            r5.append(r0)
            java.lang.String r0 = "<value><string>inventory-lib-root</string></value>"
            r5.append(r0)
            java.lang.String r0 = "<value><string>max-agent-groups</string></value>"
            r5.append(r0)
            java.lang.String r0 = "</data></array>"
            r5.append(r0)
            java.lang.String r0 = "</value>"
            r5.append(r0)
            java.lang.String r0 = "</member>"
            r5.append(r0)
            java.lang.String r0 = "</struct>"
            r5.append(r0)
            java.lang.String r0 = "</value>"
            r5.append(r0)
            java.lang.String r0 = "</param>"
            r5.append(r0)
            java.lang.String r0 = "</params>"
            r5.append(r0)
            java.lang.String r0 = "</methodCall>"
            r5.append(r0)
            java.lang.String r0 = r5.toString()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "Start location: "
            java.lang.StringBuilder r3 = r3.append(r5)
            java.lang.StringBuilder r3 = r3.append(r1)
            java.lang.String r3 = r3.toString()
            com.lumiyaviewer.lumiya.Debug.Log(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "Auth request: "
            java.lang.StringBuilder r3 = r3.append(r5)
            java.lang.StringBuilder r3 = r3.append(r0)
            java.lang.String r3 = r3.toString()
            com.lumiyaviewer.lumiya.Debug.Log(r3)
            okhttp3.Request$Builder r3 = new okhttp3.Request$Builder
            r3.<init>()
            okhttp3.Request$Builder r3 = r3.url((java.lang.String) r4)
            java.lang.String r4 = "Connection"
            java.lang.String r5 = "close"
            okhttp3.Request$Builder r3 = r3.header(r4, r5)
            java.lang.String r4 = "text/xml"
            okhttp3.MediaType r4 = okhttp3.MediaType.parse(r4)
            okhttp3.RequestBody r0 = okhttp3.RequestBody.create((okhttp3.MediaType) r4, (java.lang.String) r0)
            okhttp3.Request$Builder r0 = r3.post(r0)
            java.lang.String r3 = "Content-Type"
            java.lang.String r4 = "text/xml"
            okhttp3.Request$Builder r0 = r0.header(r3, r4)
            okhttp3.Request r0 = r0.build()
            okhttp3.OkHttpClient r3 = com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection.getOkHttpClient()
            okhttp3.Call r0 = r3.newCall(r0)
            okhttp3.Response r5 = r0.execute()
            if (r5 != 0) goto L_0x0300
            java.io.IOException r0 = new java.io.IOException
            java.lang.String r1 = "Null response"
            r0.<init>(r1)
            throw r0
        L_0x0300:
            boolean r0 = r5.isSuccessful()     // Catch:{ all -> 0x0324 }
            if (r0 != 0) goto L_0x0329
            java.io.IOException r0 = new java.io.IOException     // Catch:{ all -> 0x0324 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0324 }
            r1.<init>()     // Catch:{ all -> 0x0324 }
            java.lang.String r2 = "Login error code "
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x0324 }
            int r2 = r5.code()     // Catch:{ all -> 0x0324 }
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x0324 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0324 }
            r0.<init>(r1)     // Catch:{ all -> 0x0324 }
            throw r0     // Catch:{ all -> 0x0324 }
        L_0x0324:
            r0 = move-exception
            r5.close()
            throw r0
        L_0x0329:
            org.xmlpull.v1.XmlPullParserFactory r0 = org.xmlpull.v1.XmlPullParserFactory.newInstance()     // Catch:{ XmlPullParserException -> 0x0368 }
            org.xmlpull.v1.XmlPullParser r3 = r0.newPullParser()     // Catch:{ XmlPullParserException -> 0x0368 }
            java.io.BufferedInputStream r0 = new java.io.BufferedInputStream     // Catch:{ XmlPullParserException -> 0x0368 }
            okhttp3.ResponseBody r4 = r5.body()     // Catch:{ XmlPullParserException -> 0x0368 }
            java.io.InputStream r4 = r4.byteStream()     // Catch:{ XmlPullParserException -> 0x0368 }
            r6 = 65536(0x10000, float:9.18355E-41)
            r0.<init>(r4, r6)     // Catch:{ XmlPullParserException -> 0x0368 }
            r4 = 0
            r3.setInput(r0, r4)     // Catch:{ XmlPullParserException -> 0x0368 }
            com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply r0 = new com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply     // Catch:{ XmlPullParserException -> 0x0368 }
            java.lang.String r4 = r12.gridName     // Catch:{ XmlPullParserException -> 0x0368 }
            java.lang.String r6 = r12.loginURL     // Catch:{ XmlPullParserException -> 0x0368 }
            r0.<init>(r4, r6, r3)     // Catch:{ XmlPullParserException -> 0x0368 }
            boolean r3 = r0.isIndeterminate     // Catch:{ XmlPullParserException -> 0x0368 }
            if (r3 == 0) goto L_0x0364
            java.lang.String r3 = r0.nextMethod     // Catch:{ XmlPullParserException -> 0x0368 }
            if (r3 == 0) goto L_0x0364
            java.lang.String r3 = r0.nextURL     // Catch:{ XmlPullParserException -> 0x0368 }
            if (r3 == 0) goto L_0x0364
            java.lang.String r3 = r0.nextMethod     // Catch:{ XmlPullParserException -> 0x0368 }
            java.lang.String r4 = r0.nextURL     // Catch:{ XmlPullParserException -> 0x0368 }
            r5.close()
            int r2 = r2 + 1
            goto L_0x0199
        L_0x0364:
            r5.close()
            return r0
        L_0x0368:
            r0 = move-exception
            com.lumiyaviewer.lumiya.Debug.Warning(r0)     // Catch:{ all -> 0x0324 }
            java.io.IOException r1 = new java.io.IOException     // Catch:{ all -> 0x0324 }
            java.lang.String r2 = "Login reply parse error"
            r1.<init>(r2, r0)     // Catch:{ all -> 0x0324 }
            throw r1     // Catch:{ all -> 0x0324 }
        L_0x0375:
            return r0
        L_0x0376:
            r1 = r0
            goto L_0x0031
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.auth.SLAuth.SendLoginRequest(com.lumiyaviewer.lumiya.slproto.auth.SLAuthParams):com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply");
    }

    public static String getPasswordHash(String str) {
        String trim = str.trim();
        if (trim.length() > 16) {
            trim = trim.substring(0, 16);
        }
        return "$1$" + HashUtils.MD5_Hash(trim);
    }

    public SLAuthReply Login(SLAuthParams sLAuthParams) throws IOException {
        try {
            return SendLoginRequest(sLAuthParams);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to login to simulator");
        }
    }
}
