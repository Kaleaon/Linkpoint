// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.http;

import java.io.IOException;
import java.net.ProtocolException;
import okhttp3.Response;
import okhttp3.Protocol;

public final class StatusLine
{
    public static final int HTTP_CONTINUE = 100;
    public static final int HTTP_PERM_REDIRECT = 308;
    public static final int HTTP_TEMP_REDIRECT = 307;
    public final int code;
    public final String message;
    public final Protocol protocol;
    
    public StatusLine(final Protocol protocol, final int code, final String message) {
        this.protocol = protocol;
        this.code = code;
        this.message = message;
    }
    
    public static StatusLine get(final Response response) {
        return new StatusLine(response.protocol(), response.code(), response.message());
    }
    
    public static StatusLine parse(String substring) throws IOException {
        int beginIndex = 9;
        while (true) {
            while (true) {
                Label_0145: {
                    Label_0191: {
                        Protocol protocol;
                        if (!substring.startsWith("HTTP/1.")) {
                            if (!substring.startsWith("ICY ")) {
                                throw new ProtocolException("Unexpected status line: " + substring);
                            }
                            break Label_0191;
                        }
                        else {
                            if (substring.length() < 9 || substring.charAt(8) != ' ') {
                                throw new ProtocolException("Unexpected status line: " + substring);
                            }
                            final int n = substring.charAt(7) - '0';
                            if (n == 0) {
                                protocol = Protocol.HTTP_1_0;
                                break Label_0145;
                            }
                            if (n != 1) {
                                throw new ProtocolException("Unexpected status line: " + substring);
                            }
                            break Label_0191;
                        }
                        while (true) {
                            while (true) {
                                Label_0262: {
                                    try {
                                        final int int1 = Integer.parseInt(substring.substring(beginIndex, beginIndex + 3));
                                        if (substring.length() <= beginIndex + 3) {
                                            substring = "";
                                            return new StatusLine(protocol, int1, substring);
                                        }
                                        break Label_0262;
                                        throw new ProtocolException("Unexpected status line: " + substring);
                                        protocol = Protocol.HTTP_1_1;
                                        break Label_0145;
                                        protocol = Protocol.HTTP_1_0;
                                        beginIndex = 4;
                                        break Label_0145;
                                    }
                                    catch (final NumberFormatException ex) {
                                        throw new ProtocolException("Unexpected status line: " + substring);
                                    }
                                }
                                if (substring.charAt(beginIndex + 3) == ' ') {
                                    substring = substring.substring(beginIndex + 4);
                                    continue;
                                }
                                break;
                            }
                            break;
                        }
                    }
                    throw new ProtocolException("Unexpected status line: " + substring);
                }
                if (substring.length() >= beginIndex + 3) {
                    continue;
                }
                break;
            }
            continue;
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        String str;
        if (this.protocol != Protocol.HTTP_1_0) {
            str = "HTTP/1.1";
        }
        else {
            str = "HTTP/1.0";
        }
        sb.append(str);
        sb.append(' ').append(this.code);
        if (this.message != null) {
            sb.append(' ').append(this.message);
        }
        return sb.toString();
    }
}
