// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import org.xmlpull.v1.XmlSerializer;
import java.io.IOException;
import java.io.DataOutputStream;
import com.lumiyaviewer.lumiya.base64.Base64;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;

public class LLSDBinary extends LLSDNode
{
    private byte[] value;
    
    public LLSDBinary(final String s) {
        this.value = Base64.decode(s);
    }
    
    public LLSDBinary(final byte[] value) {
        this.value = value;
    }
    
    @Override
    public byte[] asBinary() {
        return this.value;
    }
    
    @Override
    public int asInt() {
        int n = 0;
        int n2 = 0;
        while (n < 4 && n < this.value.length) {
            n2 = (n2 << 8 | (this.value[n] & 0xFF));
            ++n;
        }
        return n2;
    }
    
    @Override
    public long asLong() {
        long n = 0L;
        for (int n2 = 0; n2 < 8 && n2 < this.value.length; ++n2) {
            n = (n << 8 | (long)(this.value[n2] & 0xFF));
        }
        return n;
    }
    
    @Override
    public void toBinary(final DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(98);
        dataOutputStream.writeInt(this.value.length);
        dataOutputStream.write(this.value);
    }
    
    @Override
    public void toXML(final XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "binary");
        xmlSerializer.text(Base64.encodeToString(this.value, false));
        xmlSerializer.endTag("", "binary");
    }
}
