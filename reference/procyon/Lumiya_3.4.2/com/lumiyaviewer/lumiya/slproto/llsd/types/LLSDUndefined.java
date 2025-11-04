// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import org.xmlpull.v1.XmlSerializer;
import java.io.IOException;
import java.io.DataOutputStream;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;

public class LLSDUndefined extends LLSDNode
{
    @Override
    public void toBinary(final DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(33);
    }
    
    @Override
    public void toXML(final XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "undef");
        xmlSerializer.endTag("", "undef");
    }
}
