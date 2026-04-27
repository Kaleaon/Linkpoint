// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.http2;

import java.io.IOException;
import okhttp3.internal.Util;
import okio.ByteString;

public final class Http2
{
    static final String[] BINARY;
    static final ByteString CONNECTION_PREFACE;
    static final String[] FLAGS;
    static final byte FLAG_ACK = 1;
    static final byte FLAG_COMPRESSED = 32;
    static final byte FLAG_END_HEADERS = 4;
    static final byte FLAG_END_PUSH_PROMISE = 4;
    static final byte FLAG_END_STREAM = 1;
    static final byte FLAG_NONE = 0;
    static final byte FLAG_PADDED = 8;
    static final byte FLAG_PRIORITY = 32;
    private static final String[] FRAME_NAMES;
    static final int INITIAL_MAX_FRAME_SIZE = 16384;
    static final byte TYPE_CONTINUATION = 9;
    static final byte TYPE_DATA = 0;
    static final byte TYPE_GOAWAY = 7;
    static final byte TYPE_HEADERS = 1;
    static final byte TYPE_PING = 6;
    static final byte TYPE_PRIORITY = 2;
    static final byte TYPE_PUSH_PROMISE = 5;
    static final byte TYPE_RST_STREAM = 3;
    static final byte TYPE_SETTINGS = 4;
    static final byte TYPE_WINDOW_UPDATE = 8;
    
    static {
        final int n = 0;
        CONNECTION_PREFACE = ByteString.encodeUtf8("PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n");
        FRAME_NAMES = new String[] { "DATA", "HEADERS", "PRIORITY", "RST_STREAM", "SETTINGS", "PUSH_PROMISE", "PING", "GOAWAY", "WINDOW_UPDATE", "CONTINUATION" };
        FLAGS = new String[64];
        BINARY = new String[256];
        for (int i = 0; i < Http2.BINARY.length; ++i) {
            Http2.BINARY[i] = Util.format("%8s", Integer.toBinaryString(i)).replace(' ', '0');
        }
        Http2.FLAGS[0] = "";
        Http2.FLAGS[1] = "END_STREAM";
        final int[] array = { 1 };
        Http2.FLAGS[8] = "PADDED";
        for (final int n2 : array) {
            Http2.FLAGS[n2 | 0x8] = Http2.FLAGS[n2] + "|PADDED";
        }
        Http2.FLAGS[4] = "END_HEADERS";
        Http2.FLAGS[32] = "PRIORITY";
        Http2.FLAGS[36] = "END_HEADERS|PRIORITY";
        for (final int n3 : new int[] { 4, 32, 36 }) {
            for (final int n4 : array) {
                Http2.FLAGS[n4 | n3] = Http2.FLAGS[n4] + '|' + Http2.FLAGS[n3];
                Http2.FLAGS[n4 | n3 | 0x8] = Http2.FLAGS[n4] + '|' + Http2.FLAGS[n3] + "|PADDED";
            }
        }
        for (int n5 = n; n5 < Http2.FLAGS.length; ++n5) {
            if (Http2.FLAGS[n5] == null) {
                Http2.FLAGS[n5] = Http2.BINARY[n5];
            }
        }
    }
    
    private Http2() {
    }
    
    static String formatFlags(final byte b, final byte b2) {
        if (b2 == 0) {
            return "";
        }
        switch (b) {
            default: {
                String s;
                if (b2 >= Http2.FLAGS.length) {
                    s = Http2.BINARY[b2];
                }
                else {
                    s = Http2.FLAGS[b2];
                }
                if (b == 5 && (b2 & 0x4) != 0x0) {
                    return s.replace("HEADERS", "PUSH_PROMISE");
                }
                if (b == 0 && (b2 & 0x20) != 0x0) {
                    return s.replace("PRIORITY", "COMPRESSED");
                }
                return s;
            }
            case 4:
            case 6: {
                String s2;
                if (b2 != 1) {
                    s2 = Http2.BINARY[b2];
                }
                else {
                    s2 = "ACK";
                }
                return s2;
            }
            case 2:
            case 3:
            case 7:
            case 8: {
                return Http2.BINARY[b2];
            }
        }
    }
    
    static String frameLog(final boolean b, final int i, final int j, final byte b2, final byte b3) {
        String format;
        if (b2 >= Http2.FRAME_NAMES.length) {
            format = Util.format("0x%02x", b2);
        }
        else {
            format = Http2.FRAME_NAMES[b2];
        }
        final String formatFlags = formatFlags(b2, b3);
        String s;
        if (!b) {
            s = ">>";
        }
        else {
            s = "<<";
        }
        return Util.format("%s 0x%08x %5d %-13s %s", s, i, j, format, formatFlags);
    }
    
    static IllegalArgumentException illegalArgument(final String s, final Object... array) {
        throw new IllegalArgumentException(Util.format(s, array));
    }
    
    static IOException ioException(final String s, final Object... array) throws IOException {
        throw new IOException(Util.format(s, array));
    }
}
