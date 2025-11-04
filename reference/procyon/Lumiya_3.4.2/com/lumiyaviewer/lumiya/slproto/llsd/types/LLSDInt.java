// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import org.xmlpull.v1.XmlSerializer;
import java.io.IOException;
import java.io.DataOutputStream;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;

public class LLSDInt extends LLSDNode
{
    private int value;
    
    public LLSDInt(final int value) {
        this.value = value;
    }
    
    public LLSDInt(final String s) {
        try {
            this.value = Integer.parseInt(s);
        }
        catch (final Exception ex) {
            this.value = 0;
        }
    }
    
    @Override
    public boolean asBoolean() {
        boolean b = false;
        if (this.value != 0) {
            b = true;
        }
        return b;
    }
    
    @Override
    public int asInt() {
        return this.value;
    }
    
    @Override
    public void toBinary(final DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(105);
        dataOutputStream.writeInt(this.value);
    }
    
    @Override
    public void toXML(final XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "integer");
        xmlSerializer.text(Integer.toString(this.value));
        xmlSerializer.endTag("", "integer");
    }
}
