package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.base64.Base64;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: classes.dex */
public class LLSDBinary extends LLSDNode {
    private byte[] value;

    public LLSDBinary(String str) {
        this.value = Base64.decode(str);
    }

    public LLSDBinary(byte[] bArr) {
        this.value = bArr;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public byte[] asBinary() {
        return this.value;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public int asInt() {
        int i = 0;
        for (int i2 = 0; i2 < 4 && i2 < this.value.length; i2++) {
            i = (i << 8) | (this.value[i2] & UnsignedBytes.MAX_VALUE);
        }
        return i;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public long asLong() {
        long j = 0;
        for (int i = 0; i < 8 && i < this.value.length; i++) {
            j = (j << 8) | (this.value[i] & UnsignedBytes.MAX_VALUE);
        }
        return j;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public void toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(98);
        dataOutputStream.writeInt(this.value.length);
        dataOutputStream.write(this.value);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public void toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "binary");
        xmlSerializer.text(Base64.encodeToString(this.value, false));
        xmlSerializer.endTag("", "binary");
    }
}
