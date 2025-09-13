package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import org.xmlpull.v1.XmlSerializer;

public class LLSDDate extends LLSDNode {
    private Date value;

    public LLSDDate(String str) {
        try {
            this.value = new Date(str);
        } catch (Exception e) {
            this.value = new Date();
        }
    }

    public LLSDDate(Date date) {
        this.value = date;
    }

    public Date asDate() {
        return this.value;
    }

    public void toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(100);
        dataOutputStream.writeDouble((double) (this.value.getTime() / 1000));
    }

    public void toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "date");
        xmlSerializer.text(this.value.toGMTString());
        xmlSerializer.endTag("", "date");
    }
}
