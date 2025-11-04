package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: classes.dex */
public class LLSDInt extends LLSDNode {
    private int value;

    public LLSDInt(int i) {
        this.value = i;
    }

    public LLSDInt(String str) {
        try {
            this.value = Integer.parseInt(str);
        } catch (Exception e) {
            this.value = 0;
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public boolean asBoolean() {
        return this.value != 0;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public int asInt() {
        return this.value;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public void toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(105);
        dataOutputStream.writeInt(this.value);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public void toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "integer");
        xmlSerializer.text(Integer.toString(this.value));
        xmlSerializer.endTag("", "integer");
    }
}
