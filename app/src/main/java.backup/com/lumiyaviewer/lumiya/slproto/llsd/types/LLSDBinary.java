package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.base64.Base64;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

public class LLSDBinary extends LLSDNode {
    private byte[] value;

    public LLSDBinary(String str) {
        this.value = Base64.decode(str);
    }

    public LLSDBinary(byte[] bArr) {
        this.value = bArr;
    }

    public byte[] asBinary() {
        return this.value;
    }

    public int asInt() {
        int i = 0;
        byte b = 0;
        while (i < 4 && i < this.value.length) {
            b = (b << 8) | (this.value[i] & UnsignedBytes.MAX_VALUE);
            i++;
        }
        return b;
    }

    public long asLong() {
        long j = 0;
        int i = 0;
        while (i < 8 && i < this.value.length) {
            j = (j << 8) | ((long) (this.value[i] & UnsignedBytes.MAX_VALUE));
            i++;
        }
        return j;
    }

    public void toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(98);
        dataOutputStream.writeInt(this.value.length);
        dataOutputStream.write(this.value);
    }

    public void toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "binary");
        xmlSerializer.text(Base64.encodeToString(this.value, false));
        xmlSerializer.endTag("", "binary");
    }
}
