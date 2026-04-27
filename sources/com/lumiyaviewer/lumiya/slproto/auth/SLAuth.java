// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.auth;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection;
import com.lumiyaviewer.lumiya.utils.HashUtils;
import com.lumiyaviewer.lumiya.utils.StringUtils;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.auth:
//            SLAuthParams, SLAuthReply

public class SLAuth
{
    private static class LoginRequestField
    {

        public final String name;
        public final String value;

        private LoginRequestField(String s, String s1)
        {
            name = s;
            value = s1;
        }

        LoginRequestField(String s, String s1, LoginRequestField loginrequestfield)
        {
            this(s, s1);
        }
    }


    public SLAuth()
    {
    }

    private SLAuthReply SendLoginRequest(SLAuthParams slauthparams)
        throws IOException
    {
_L4:
        linkedlist = new LinkedList();
        String s1 = slauthparams.loginName.trim();
        int l = s1.length();
        for (int i = 0; i < " ._".length();)
        {
            int j1 = s1.indexOf(" ._".substring(i, i + 1));
            int i1 = l;
            if (j1 != -1)
            {
                i1 = l;
                if (j1 < l)
                {
                    i1 = j1;
                }
            }
            i++;
            l = i1;
        }

        obj1 = s1.substring(0, l);
        obj = "";
        if (l < s1.length())
        {
            obj = s1.substring(l + 1);
        }
        s1 = ((String) (obj1)).trim();
        obj1 = ((String) (obj)).trim();
        obj = obj1;
        if (((String) (obj1)).equalsIgnoreCase(""))
        {
            obj = "Resident";
        }
        linkedlist.add(new LoginRequestField("first", s1, null));
        linkedlist.add(new LoginRequestField("last", ((String) (obj)), null));
        linkedlist.add(new LoginRequestField("passwd", ((String) (obj2)), null));
        linkedlist.add(new LoginRequestField("start", s, null));
        obj1 = (new StringBuilder()).append("Lumiya ");
        if (Debug.isDebugBuild())
        {
            obj = "Test";
        } else
        {
            obj = "Release";
        }
        obj1 = ((StringBuilder) (obj1)).append(((String) (obj))).toString();
        obj = LumiyaApp.getAppVersion();
        for (int j = StringUtils.countOccurrences(((String) (obj)), '.'); j < 3; j++)
        {
            obj = (new StringBuilder()).append(((String) (obj))).append(".0").toString();
        }

        Debug.Printf("Auth: viewer channel '%s', version '%s'", new Object[] {
            obj1, obj
        });
        linkedlist.add(new LoginRequestField("channel", ((String) (obj1)), null));
        linkedlist.add(new LoginRequestField("version", ((String) (obj)), null));
        linkedlist.add(new LoginRequestField("platform", "Android", null));
        linkedlist.add(new LoginRequestField("platform_version", android.os.Build.VERSION.RELEASE, null));
        linkedlist.add(new LoginRequestField("mac", HashUtils.MD5_Hash("android_id"), null));
        linkedlist.add(new LoginRequestField("user-agent", "Lumiya", null));
        linkedlist.add(new LoginRequestField("id0", slauthparams.clientID.toString(), null));
        linkedlist.add(new LoginRequestField("agree_to_tos", "true", null));
        linkedlist.add(new LoginRequestField("viewer_digest", "f50cfcc3-d6ce-4f16-a822-b91271de4c48", null));
        obj = slauthparams.loginURL;
        obj1 = "login_to_simulator";
        obj2 = null;
        k = ((flag) ? 1 : 0);
_L2:
        if (k >= 5)
        {
            break MISSING_BLOCK_LABEL_1264;
        }
        obj2 = new StringBuilder();
        ((StringBuilder) (obj2)).append("<?xml version=\"1.0\"?>\n");
        ((StringBuilder) (obj2)).append("<methodCall>");
        ((StringBuilder) (obj2)).append("<methodName>").append(((String) (obj1))).append("</methodName>");
        ((StringBuilder) (obj2)).append("<params>");
        ((StringBuilder) (obj2)).append("<param>");
        ((StringBuilder) (obj2)).append("<value>");
        ((StringBuilder) (obj2)).append("<struct>");
        for (obj1 = linkedlist.iterator(); ((Iterator) (obj1)).hasNext(); ((StringBuilder) (obj2)).append("</member>"))
        {
            LoginRequestField loginrequestfield = (LoginRequestField)((Iterator) (obj1)).next();
            ((StringBuilder) (obj2)).append("<member>");
            ((StringBuilder) (obj2)).append("<name>");
            ((StringBuilder) (obj2)).append(loginrequestfield.name);
            ((StringBuilder) (obj2)).append("</name>");
            ((StringBuilder) (obj2)).append("<value>");
            ((StringBuilder) (obj2)).append("<string>");
            ((StringBuilder) (obj2)).append(loginrequestfield.value);
            ((StringBuilder) (obj2)).append("</string>");
            ((StringBuilder) (obj2)).append("</value>");
        }

        ((StringBuilder) (obj2)).append("<member>");
        ((StringBuilder) (obj2)).append("<name>options</name>");
        ((StringBuilder) (obj2)).append("<value>");
        ((StringBuilder) (obj2)).append("<array><data>");
        ((StringBuilder) (obj2)).append("<value><string>buddy-list</string></value>");
        ((StringBuilder) (obj2)).append("<value><string>display_names</string></value>");
        ((StringBuilder) (obj2)).append("<value><string>inventory-root</string></value>");
        ((StringBuilder) (obj2)).append("<value><string>inventory-lib-root</string></value>");
        ((StringBuilder) (obj2)).append("<value><string>max-agent-groups</string></value>");
        ((StringBuilder) (obj2)).append("</data></array>");
        ((StringBuilder) (obj2)).append("</value>");
        ((StringBuilder) (obj2)).append("</member>");
        ((StringBuilder) (obj2)).append("</struct>");
        ((StringBuilder) (obj2)).append("</value>");
        ((StringBuilder) (obj2)).append("</param>");
        ((StringBuilder) (obj2)).append("</params>");
        ((StringBuilder) (obj2)).append("</methodCall>");
        obj1 = ((StringBuilder) (obj2)).toString();
        Debug.Log((new StringBuilder()).append("Start location: ").append(s).toString());
        Debug.Log((new StringBuilder()).append("Auth request: ").append(((String) (obj1))).toString());
        obj = (new okhttp3.Request.Builder()).url(((String) (obj))).header("Connection", "close").post(RequestBody.create(MediaType.parse("text/xml"), ((String) (obj1)))).header("Content-Type", "text/xml").build();
        response = SLHTTPSConnection.getOkHttpClient().newCall(((okhttp3.Request) (obj))).execute();
        if (response == null)
        {
            throw new IOException("Null response");
        }
        if (!response.isSuccessful())
        {
            throw new IOException((new StringBuilder()).append("Login error code ").append(response.code()).toString());
        }
        break MISSING_BLOCK_LABEL_1138;
        slauthparams;
        response.close();
        throw slauthparams;
        obj = XmlPullParserFactory.newInstance().newPullParser();
        ((XmlPullParser) (obj)).setInput(new BufferedInputStream(response.body().byteStream(), 0x10000), null);
        obj2 = new SLAuthReply(slauthparams.gridName, slauthparams.loginURL, ((XmlPullParser) (obj)));
        if (!((SLAuthReply) (obj2)).isIndeterminate || ((SLAuthReply) (obj2)).nextMethod == null || ((SLAuthReply) (obj2)).nextURL == null)
        {
            break; /* Loop/switch isn't completed */
        }
        obj1 = ((SLAuthReply) (obj2)).nextMethod;
        obj = ((SLAuthReply) (obj2)).nextURL;
        response.close();
        k++;
        if (true) goto _L2; else goto _L1
_L1:
        response.close();
        return ((SLAuthReply) (obj2));
        slauthparams;
        Debug.Warning(slauthparams);
        throw new IOException("Login reply parse error", slauthparams);
        return ((SLAuthReply) (obj2));
        boolean flag = false;
        Object obj2 = slauthparams.passwordHash;
        Debug.Log((new StringBuilder()).append("md5 hash: ").append(((String) (obj2))).toString());
        String s;
        Object obj;
        Object obj1;
        LinkedList linkedlist;
        Response response;
        int k;
        if (slauthparams.startLocation != null)
        {
            if (slauthparams.startLocation.equals("first"))
            {
                s = "home";
                break MISSING_BLOCK_LABEL_54;
            }
            if (slauthparams.startLocation.startsWith("uri:"))
            {
                s = slauthparams.startLocation;
                continue; /* Loop/switch isn't completed */
            }
        }
        s = "last";
        if (true) goto _L4; else goto _L3
_L3:
    }

    public static String getPasswordHash(String s)
    {
        String s1 = s.trim();
        s = s1;
        if (s1.length() > 16)
        {
            s = s1.substring(0, 16);
        }
        return (new StringBuilder()).append("$1$").append(HashUtils.MD5_Hash(s)).toString();
    }

    public SLAuthReply Login(SLAuthParams slauthparams)
        throws IOException
    {
        try
        {
            slauthparams = SendLoginRequest(slauthparams);
        }
        // Misplaced declaration of an exception variable
        catch (SLAuthParams slauthparams)
        {
            slauthparams.printStackTrace();
            throw new IOException("Failed to login to simulator");
        }
        return slauthparams;
    }
}
