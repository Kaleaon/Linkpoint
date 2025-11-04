// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import org.xmlpull.v1.XmlSerializer;
import java.io.IOException;
import java.io.DataOutputStream;
import java.util.Date;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;

public class LLSDDate extends LLSDNode
{
    private Date value;
    
    public LLSDDate(final String s) {
        try {
            this.value = new Date(s);
        }
        catch (final Exception ex) {
            this.value = new Date();
        }
    }
    
    public LLSDDate(final Date value) {
        this.value = value;
    }
    
    @Override
    public Date asDate() {
        return this.value;
    }
    
    @Override
    public void toBinary(final DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(100);
        dataOutputStream.writeDouble((double)(this.value.getTime() / 1000L));
    }
    
    @Override
    public void toXML(final XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "date");
        xmlSerializer.text(this.value.toGMTString());
        xmlSerializer.endTag("", "date");
    }
}
