package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: classes.dex */
public class LLSDBoolean extends LLSDNode {
    private boolean value;

    public LLSDBoolean(String str) {
        if (str.equalsIgnoreCase("true")) {
            this.value = true;
        } else if (str.equalsIgnoreCase("false")) {
            this.value = false;
        } else {
            this.value = Integer.parseInt(str) != 0;
        }
    }

    public LLSDBoolean(boolean z) {
        this.value = z;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public boolean asBoolean() {
        return this.value;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public void toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(this.value ? 49 : 48);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public void toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "boolean");
        xmlSerializer.text(this.value ? "1" : "0");
        xmlSerializer.endTag("", "boolean");
    }
}
