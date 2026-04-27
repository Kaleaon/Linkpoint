// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.https;

import java.io.IOException;
import com.lumiyaviewer.lumiya.Debug;
import java.io.BufferedInputStream;

public class LLSDContentTypeDetector
{
    public static LLSDContentType DetectContentType(final BufferedInputStream bufferedInputStream, final String s) throws IOException {
        bufferedInputStream.mark(64);
        final byte[] array2;
        final byte[] array = array2 = new byte[3];
        array2[0] = -17;
        array2[1] = -69;
        array2[2] = -65;
        final byte[] array3 = new byte[32];
        final int read = bufferedInputStream.read(array3, 0, array3.length);
    Label_0089:
        while (true) {
            Label_0328: {
                if (read >= array.length) {
                    int n = 0;
                    while (true) {
                        while (n < read && n < array.length) {
                            if (array3[n] != array[n]) {
                                final int n2 = 0;
                                if (n2 != 0) {
                                    final int length = array.length;
                                    break Label_0089;
                                }
                                break Label_0328;
                            }
                            else {
                                ++n;
                            }
                        }
                        final int n2 = 1;
                        continue;
                    }
                }
                break Label_0328;
                int length = 0;
                final String s2 = new String(array3, length, read - length, "UTF-8");
                bufferedInputStream.reset();
                bufferedInputStream.skip(length);
                int n3;
                boolean b;
                if (s2.startsWith("<llsd>") || s2.startsWith("<?xml")) {
                    n3 = 1;
                    b = false;
                }
                else if (s2.startsWith("<? LLSD/Binary ?>") || s2.startsWith("{") || s2.startsWith("<?llsd/binary")) {
                    n3 = 0;
                    b = true;
                }
                else {
                    n3 = 0;
                    b = false;
                }
                String s3;
                if (b) {
                    s3 = "true";
                }
                else {
                    s3 = "false";
                }
                String s4;
                if (n3 != 0) {
                    s4 = "true";
                }
                else {
                    s4 = "false";
                }
                Debug.Printf("LLSD: contentType '%s', detected binary %s, xml %s, skipBytes %d, firstString '%s'", s, s3, s4, length, s2);
                if (b) {
                    n3 = 1;
                }
                int n4 = b ? 1 : 0;
                if (n3 == 0) {
                    n4 = (b ? 1 : 0);
                    if (s.equalsIgnoreCase("application/llsd+binary")) {
                        n4 = 1;
                    }
                }
                if (n4 != 0) {
                    Debug.Printf("LLSD: using binary parser", new Object[0]);
                    return LLSDContentType.llsdBinary;
                }
                Debug.Printf("LLSD: using XML parser", new Object[0]);
                return LLSDContentType.llsdXML;
            }
            final int length = 0;
            continue Label_0089;
        }
    }
    
    public enum LLSDContentType
    {
        llsdBinary("llsdBinary", 1), 
        llsdXML("llsdXML", 0);
        
        private LLSDContentType(final String name, final int ordinal) {
        }
    }
}
