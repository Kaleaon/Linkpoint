// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import org.xmlpull.v1.XmlSerializer;
import java.io.IOException;
import java.io.DataOutputStream;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;

public class LLSDDouble extends LLSDNode
{
    private double value;
    
    public LLSDDouble(final double value) {
        this.value = value;
    }
    
    public LLSDDouble(final String s) {
        this.value = Double.parseDouble(s);
    }
    
    @Override
    public double asDouble() {
        return this.value;
    }
    
    @Override
    public void toBinary(final DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(114);
        dataOutputStream.writeDouble(this.value);
    }
    
    @Override
    public void toXML(final XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "real");
        xmlSerializer.text(Double.toString(this.value));
        xmlSerializer.endTag("", "real");
    }
}
