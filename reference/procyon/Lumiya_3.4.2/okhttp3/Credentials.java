// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import java.io.UnsupportedEncodingException;
import okio.ByteString;

public final class Credentials
{
    private Credentials() {
    }
    
    public static String basic(String string, String base64) {
        try {
            base64 = ByteString.of((string + ":" + base64).getBytes("ISO-8859-1")).base64();
            string = "Basic " + base64;
            return string;
        }
        catch (final UnsupportedEncodingException ex) {
            throw new AssertionError();
        }
    }
}
