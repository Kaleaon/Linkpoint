package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: classes.dex */
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

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public Date asDate() {
        return this.value;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public void toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(100);
        dataOutputStream.writeDouble(this.value.getTime() / 1000);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public void toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "date");
        xmlSerializer.text(this.value.toGMTString());
        xmlSerializer.endTag("", "date");
    }
}
