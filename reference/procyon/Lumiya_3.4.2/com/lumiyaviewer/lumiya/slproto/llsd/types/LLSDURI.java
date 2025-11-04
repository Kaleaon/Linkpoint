// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import org.xmlpull.v1.XmlSerializer;
import java.io.IOException;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.io.DataOutputStream;
import java.net.URI;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;

public class LLSDURI extends LLSDNode
{
    private URI value;
    
    public LLSDURI(final String s) {
        this.value = URI.create("");
    }
    
    public LLSDURI(final URI value) {
        this.value = value;
    }
    
    @Override
    public URI asURI() {
        return this.value;
    }
    
    @Override
    public void toBinary(final DataOutputStream dataOutputStream) throws IOException {
        final String string = this.value.toString();
        dataOutputStream.writeByte(108);
        if (string.isEmpty()) {
            dataOutputStream.writeInt(0);
        }
        else {
            final byte[] stringToVariableUTF = SLMessage.stringToVariableUTF(string);
            dataOutputStream.writeInt(stringToVariableUTF.length);
            dataOutputStream.write(stringToVariableUTF);
        }
    }
    
    @Override
    public void toXML(final XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "uri");
        xmlSerializer.text(this.value.toString());
        xmlSerializer.endTag("", "uri");
    }
}
