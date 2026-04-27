package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

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

    public boolean asBoolean() {
        return this.value != 0;
    }

    public int asInt() {
        return this.value;
    }

    public void toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(105);
        dataOutputStream.writeInt(this.value);
    }

    public void toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "integer");
        xmlSerializer.text(Integer.toString(this.value));
        xmlSerializer.endTag("", "integer");
    }
}
