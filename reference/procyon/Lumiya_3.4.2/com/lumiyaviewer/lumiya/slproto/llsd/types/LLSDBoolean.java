// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import org.xmlpull.v1.XmlSerializer;
import java.io.IOException;
import java.io.DataOutputStream;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;

public class LLSDBoolean extends LLSDNode
{
    private boolean value;
    
    public LLSDBoolean(final String s) {
        boolean value = true;
        if (s.equalsIgnoreCase("true")) {
            this.value = true;
        }
        else if (s.equalsIgnoreCase("false")) {
            this.value = false;
        }
        else {
            if (Integer.parseInt(s) == 0) {
                value = false;
            }
            this.value = value;
        }
    }
    
    public LLSDBoolean(final boolean value) {
        this.value = value;
    }
    
    @Override
    public boolean asBoolean() {
        return this.value;
    }
    
    @Override
    public void toBinary(final DataOutputStream dataOutputStream) throws IOException {
        int v;
        if (this.value) {
            v = 49;
        }
        else {
            v = 48;
        }
        dataOutputStream.writeByte(v);
    }
    
    @Override
    public void toXML(final XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "boolean");
        String s;
        if (this.value) {
            s = "1";
        }
        else {
            s = "0";
        }
        xmlSerializer.text(s);
        xmlSerializer.endTag("", "boolean");
    }
}
