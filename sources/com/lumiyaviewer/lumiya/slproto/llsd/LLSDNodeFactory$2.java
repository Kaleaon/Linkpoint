// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.llsd;

import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.llsd:
//            LLSDNodeFactory, LLSDNode

static final class SDNodeConstructor
    implements SDNodeConstructor
{

    public LLSDNode createNodeFromXML(XmlPullParser xmlpullparser)
        throws XmlPullParserException, IOException
    {
        return new LLSDBoolean(xmlpullparser.nextText());
    }

    SDNodeConstructor()
    {
    }
}
