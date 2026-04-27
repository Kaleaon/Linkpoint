package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: classes.dex */
public class LLSDString extends LLSDNode {
    private String value;

    public LLSDString(String str) {
        this.value = str;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public boolean asBoolean() {
        return "true".equalsIgnoreCase(this.value);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public String asString() {
        return this.value;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public UUID asUUID() {
        return UUID.fromString(this.value);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public void toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(115);
        if (this.value.isEmpty()) {
            dataOutputStream.writeInt(0);
            return;
        }
        byte[] stringToVariableUTF = SLMessage.stringToVariableUTF(this.value);
        dataOutputStream.writeInt(stringToVariableUTF.length);
        dataOutputStream.write(stringToVariableUTF);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public void toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "string");
        xmlSerializer.text(this.value);
        xmlSerializer.endTag("", "string");
    }
}
