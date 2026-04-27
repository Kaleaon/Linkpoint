// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.auth;

import org.xmlpull.v1.XmlPullParser;
import okhttp3.Response;
import java.util.Iterator;
import java.io.Serializable;
import org.xmlpull.v1.XmlPullParserException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Request;
import com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection;
import com.lumiyaviewer.lumiya.utils.HashUtils;
import android.os.Build$VERSION;
import com.lumiyaviewer.lumiya.utils.StringUtils;
import com.lumiyaviewer.lumiya.LumiyaApp;
import java.util.LinkedList;
import com.lumiyaviewer.lumiya.Debug;

public class SLAuth
{
    private SLAuthReply SendLoginRequest(final SLAuthParams slAuthParams) throws IOException {
        final int n = 0;
        final String passwordHash = slAuthParams.passwordHash;
        Debug.Log("md5 hash: " + passwordHash);
        while (true) {
            Label_1252: {
                if (slAuthParams.startLocation == null) {
                    break Label_1252;
                }
                Serializable startLocation;
                if (slAuthParams.startLocation.equals("first")) {
                    startLocation = "home";
                }
                else {
                    if (!slAuthParams.startLocation.startsWith("uri:")) {
                        break Label_1252;
                    }
                    startLocation = slAuthParams.startLocation;
                }
                final LinkedList list = new LinkedList();
                final String trim = slAuthParams.loginName.trim();
                int length = trim.length();
                int n2;
                for (int i = 0; i < " ._".length(); ++i, length = n2) {
                    final int index = trim.indexOf(" ._".substring(i, i + 1));
                    n2 = length;
                    if (index != -1 && index < (n2 = length)) {
                        n2 = index;
                    }
                }
                final String substring = trim.substring(0, length);
                String substring2 = "";
                if (length < trim.length()) {
                    substring2 = trim.substring(length + 1);
                }
                final String trim2 = substring.trim();
                String trim3;
                if ((trim3 = substring2.trim()).equalsIgnoreCase("")) {
                    trim3 = "Resident";
                }
                list.add(new LoginRequestField("first", trim2, null));
                list.add(new LoginRequestField("last", trim3, null));
                list.add(new LoginRequestField("passwd", passwordHash, null));
                list.add(new LoginRequestField("start", (String)startLocation, null));
                final StringBuilder append = new StringBuilder().append("Lumiya ");
                String str;
                if (Debug.isDebugBuild()) {
                    str = "Test";
                }
                else {
                    str = "Release";
                }
                final String string = append.append(str).toString();
                String str2 = LumiyaApp.getAppVersion();
                for (int j = StringUtils.countOccurrences(str2, '.'); j < 3; ++j) {
                    str2 += ".0";
                }
                Debug.Printf("Auth: viewer channel '%s', version '%s'", string, str2);
                list.add(new LoginRequestField("channel", string, null));
                list.add(new LoginRequestField("version", str2, null));
                list.add(new LoginRequestField("platform", "Android", null));
                list.add(new LoginRequestField("platform_version", Build$VERSION.RELEASE, null));
                list.add(new LoginRequestField("mac", HashUtils.MD5_Hash("android_id"), null));
                list.add(new LoginRequestField("user-agent", "Lumiya", null));
                list.add(new LoginRequestField("id0", slAuthParams.clientID.toString(), null));
                list.add(new LoginRequestField("agree_to_tos", "true", null));
                list.add(new LoginRequestField("viewer_digest", "f50cfcc3-d6ce-4f16-a822-b91271de4c48", null));
                String s = slAuthParams.loginURL;
                String nextMethod = "login_to_simulator";
                SLAuthReply slAuthReply = null;
                int k = n;
                while (k < 5) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("<?xml version=\"1.0\"?>\n");
                    sb.append("<methodCall>");
                    sb.append("<methodName>").append(nextMethod).append("</methodName>");
                    sb.append("<params>");
                    sb.append("<param>");
                    sb.append("<value>");
                    sb.append("<struct>");
                    for (final LoginRequestField loginRequestField : list) {
                        sb.append("<member>");
                        sb.append("<name>");
                        sb.append(loginRequestField.name);
                        sb.append("</name>");
                        sb.append("<value>");
                        sb.append("<string>");
                        sb.append(loginRequestField.value);
                        sb.append("</string>");
                        sb.append("</value>");
                        sb.append("</member>");
                    }
                    sb.append("<member>");
                    sb.append("<name>options</name>");
                    sb.append("<value>");
                    sb.append("<array><data>");
                    sb.append("<value><string>buddy-list</string></value>");
                    sb.append("<value><string>display_names</string></value>");
                    sb.append("<value><string>inventory-root</string></value>");
                    sb.append("<value><string>inventory-lib-root</string></value>");
                    sb.append("<value><string>max-agent-groups</string></value>");
                    sb.append("</data></array>");
                    sb.append("</value>");
                    sb.append("</member>");
                    sb.append("</struct>");
                    sb.append("</value>");
                    sb.append("</param>");
                    sb.append("</params>");
                    sb.append("</methodCall>");
                    final String string2 = sb.toString();
                    Debug.Log("Start location: " + (String)startLocation);
                    Debug.Log("Auth request: " + string2);
                    final Response execute = SLHTTPSConnection.getOkHttpClient().newCall(new Request.Builder().url(s).header("Connection", "close").post(RequestBody.create(MediaType.parse("text/xml"), string2)).header("Content-Type", "text/xml").build()).execute();
                    if (execute == null) {
                        throw new IOException("Null response");
                    }
                    try {
                        if (!execute.isSuccessful()) {
                            startLocation = new StringBuilder();
                            throw new IOException(((StringBuilder)startLocation).append("Login error code ").append(execute.code()).toString());
                        }
                    }
                    finally {
                        execute.close();
                    }
                    try {
                        final XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
                        pullParser.setInput((InputStream)new BufferedInputStream(execute.body().byteStream(), 65536), (String)null);
                        final SLAuthParams slAuthParams2;
                        slAuthReply = new SLAuthReply(slAuthParams2.gridName, slAuthParams2.loginURL, pullParser);
                        if (slAuthReply.isIndeterminate && slAuthReply.nextMethod != null && slAuthReply.nextURL != null) {
                            nextMethod = slAuthReply.nextMethod;
                            s = slAuthReply.nextURL;
                            execute.close();
                            ++k;
                            continue;
                        }
                        execute.close();
                        return slAuthReply;
                    }
                    catch (final XmlPullParserException cause) {
                        Debug.Warning((Throwable)cause);
                        throw new IOException("Login reply parse error", (Throwable)cause);
                    }
                    break;
                }
                return slAuthReply;
            }
            Serializable startLocation = "last";
            continue;
        }
    }
    
    public static String getPasswordHash(String s) {
        final String s2 = s = s.trim();
        if (s2.length() > 16) {
            s = s2.substring(0, 16);
        }
        return "$1$" + HashUtils.MD5_Hash(s);
    }
    
    public SLAuthReply Login(final SLAuthParams slAuthParams) throws IOException {
        try {
            return this.SendLoginRequest(slAuthParams);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new IOException("Failed to login to simulator");
        }
    }
    
    private static class LoginRequestField
    {
        public final String name;
        public final String value;
        
        private LoginRequestField(final String name, final String value) {
            this.name = name;
            this.value = value;
        }
    }
}
