package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: classes.dex */
public class LLSDDouble extends LLSDNode {
    private double value;

    public LLSDDouble(double d) {
        this.value = d;
    }

    public LLSDDouble(String str) {
        this.value = Double.parseDouble(str);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public double asDouble() {
        return this.value;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public void toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(114);
        dataOutputStream.writeDouble(this.value);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public void toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "real");
        xmlSerializer.text(Double.toString(this.value));
        xmlSerializer.endTag("", "real");
    }
}
