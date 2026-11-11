package com.lumiyaviewer.lumiya.slproto.auth;

import com.lumiyaviewer.lumiya.utils.HashUtils;
import java.io.IOException;
/* loaded from: classes.dex */
public class SLAuth {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class LoginRequestField {
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

    /* JADX WARN: Removed duplicated region for block: B:10:0x004b  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x007a  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0091  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x00d2  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00ea A[LOOP:1: B:28:0x00e7->B:30:0x00ea, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0101  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x019c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply SendLoginRequest(com.lumiyaviewer.lumiya.slproto.auth.SLAuthParams r12) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 889
            To view this dump add '--comments-level debug' option
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
