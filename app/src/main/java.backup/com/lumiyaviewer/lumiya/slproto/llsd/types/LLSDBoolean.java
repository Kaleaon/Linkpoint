package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

public class LLSDBoolean extends LLSDNode {
    private boolean value;

    public LLSDBoolean(String str) {
        boolean z = true;
        if (str.equalsIgnoreCase("true")) {
            this.value = true;
        } else if (str.equalsIgnoreCase("false")) {
            this.value = false;
        } else {
            this.value = Integer.parseInt(str) == 0 ? false : z;
        }
    }

    public LLSDBoolean(boolean z) {
        this.value = z;
    }

    public boolean asBoolean() {
        return this.value;
    }

    public void toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(this.value ? 49 : 48);
    }

    public void toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "boolean");
        xmlSerializer.text(this.value ? "1" : "0");
        xmlSerializer.endTag("", "boolean");
    }
}
