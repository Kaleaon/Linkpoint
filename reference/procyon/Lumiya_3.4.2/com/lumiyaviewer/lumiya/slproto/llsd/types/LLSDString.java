// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import org.xmlpull.v1.XmlSerializer;
import java.io.IOException;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.io.DataOutputStream;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;

public class LLSDString extends LLSDNode
{
    private String value;
    
    public LLSDString(final String value) {
        this.value = value;
    }
    
    @Override
    public boolean asBoolean() {
        return "true".equalsIgnoreCase(this.value);
    }
    
    @Override
    public String asString() {
        return this.value;
    }
    
    @Override
    public UUID asUUID() {
        return UUID.fromString(this.value);
    }
    
    @Override
    public void toBinary(final DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(115);
        if (this.value.isEmpty()) {
            dataOutputStream.writeInt(0);
        }
        else {
            final byte[] stringToVariableUTF = SLMessage.stringToVariableUTF(this.value);
            dataOutputStream.writeInt(stringToVariableUTF.length);
            dataOutputStream.write(stringToVariableUTF);
        }
    }
    
    @Override
    public void toXML(final XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "string");
        xmlSerializer.text(this.value);
        xmlSerializer.endTag("", "string");
    }
}
