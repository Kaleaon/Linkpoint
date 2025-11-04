// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import org.xmlpull.v1.XmlSerializer;
import java.io.IOException;
import java.io.DataOutputStream;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;

public class LLSDUUID extends LLSDNode
{
    private UUID value;
    
    public LLSDUUID() {
        this.value = null;
    }
    
    public LLSDUUID(final String s) {
        final int length = s.length();
        long n = 0L;
        int n2 = 0;
        int n3 = 0;
        long leastSigBits = 0L;
        long mostSigBits = 0L;
        int i = 0;
    Label_0130_Outer:
        while (i < length) {
            final char char1 = s.charAt(i);
            long n4 = n;
            int n5 = n2;
            while (true) {
                Label_0238: {
                    if (char1 == '-') {
                        break Label_0238;
                    }
                    int n6;
                    if (char1 >= '0' && char1 <= '9') {
                        n6 = char1 - '0';
                    }
                    else if (char1 >= 'a' && char1 <= 'f') {
                        n6 = char1 - 'a' + 10;
                    }
                    else if (char1 >= 'A' && char1 <= 'F') {
                        n6 = char1 - 'A' + 10;
                    }
                    else {
                        n6 = 0;
                    }
                    n = (n << 4 | (long)n6);
                    ++n2;
                    n4 = n;
                    n5 = n2;
                    if (n2 < 16) {
                        break Label_0238;
                    }
                    if (n3 == 0) {
                        mostSigBits = n;
                    }
                    else {
                        leastSigBits = n;
                    }
                    final long n7 = mostSigBits;
                    final long n8 = leastSigBits;
                    final int n9 = n3 + 1;
                    final int n10 = n2;
                    final long n11 = n7;
                    ++i;
                    final long n12 = n8;
                    mostSigBits = n11;
                    n2 = n10;
                    leastSigBits = n12;
                    n3 = n9;
                    continue Label_0130_Outer;
                }
                final long n13 = mostSigBits;
                final int n14 = n5;
                final int n9 = n3;
                final int n10 = n14;
                n = n4;
                final long n8 = leastSigBits;
                final long n11 = n13;
                continue;
            }
        }
        this.value = new UUID(mostSigBits, leastSigBits);
    }
    
    public LLSDUUID(final UUID value) {
        this.value = value;
    }
    
    @Override
    public String asString() {
        return this.value.toString();
    }
    
    @Override
    public UUID asUUID() {
        return this.value;
    }
    
    @Override
    public void toBinary(final DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(117);
        dataOutputStream.writeLong(this.value.getMostSignificantBits());
        dataOutputStream.writeLong(this.value.getLeastSignificantBits());
    }
    
    @Override
    public void toXML(final XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "uuid");
        if (this.value != null) {
            xmlSerializer.text(this.value.toString());
        }
        xmlSerializer.endTag("", "uuid");
    }
}
