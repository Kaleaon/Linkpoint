package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDBoolean : LLSDNode {
    private Boolean value

    LLSDBoolean(String str) {
        Boolean z = true
        if (str.equalsIgnoreCase("true")) {
            this.value = true
        } else if (str.equalsIgnoreCase("false")) {
            this.value = false
        } else {
            this.value = Integer.parseInt(str) == 0 ? false : z
        }
    }

    LLSDBoolean(Boolean z) {
        this.value = z
    }

    Boolean asBoolean() {
        return this.value
    }

    Unit toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(this.value ? 49 : 48)
    }

    Unit toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "Boolean")
        xmlSerializer.text(this.value ? "1" : "0")
        xmlSerializer.endTag("", "Boolean")
    }
}
