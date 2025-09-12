// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.llsd;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.llsd:
//            LLSDNodeFactory, LLSDXMLException, LLSDNode

private static interface 
{

    public abstract LLSDNode createNodeFromXML(XmlPullParser xmlpullparser)
        throws LLSDXMLException, XmlPullParserException, IOException;
}
