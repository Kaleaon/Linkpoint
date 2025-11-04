package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: classes.dex */
public class LLSDURI extends LLSDNode {
    private URI value;

    public LLSDURI(String str) {
        this.value = URI.create("");
    }

    public LLSDURI(URI uri) {
        this.value = uri;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public URI asURI() {
        return this.value;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public void toBinary(DataOutputStream dataOutputStream) throws IOException {
        String uri = this.value.toString();
        dataOutputStream.writeByte(108);
        if (uri.isEmpty()) {
            dataOutputStream.writeInt(0);
            return;
        }
        byte[] stringToVariableUTF = SLMessage.stringToVariableUTF(uri);
        dataOutputStream.writeInt(stringToVariableUTF.length);
        dataOutputStream.write(stringToVariableUTF);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public void toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "uri");
        xmlSerializer.text(this.value.toString());
        xmlSerializer.endTag("", "uri");
    }
}
