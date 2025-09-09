package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

public class LLSDUndefined extends LLSDNode {
    public void toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(33);
    }

    public void toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "undef");
        xmlSerializer.endTag("", "undef");
    }
}
