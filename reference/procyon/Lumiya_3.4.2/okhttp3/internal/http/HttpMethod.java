// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.http;

public final class HttpMethod
{
    private HttpMethod() {
    }
    
    public static boolean invalidatesCache(final String s) {
        boolean b = false;
        if (s.equals("POST") || s.equals("PATCH") || s.equals("PUT") || s.equals("DELETE") || s.equals("MOVE")) {
            b = true;
        }
        return b;
    }
    
    public static boolean permitsRequestBody(final String s) {
        boolean b = false;
        if (requiresRequestBody(s) || s.equals("OPTIONS") || s.equals("DELETE") || s.equals("PROPFIND") || s.equals("MKCOL") || s.equals("LOCK")) {
            b = true;
        }
        return b;
    }
    
    public static boolean redirectsToGet(final String s) {
        boolean b = false;
        if (!s.equals("PROPFIND")) {
            b = true;
        }
        return b;
    }
    
    public static boolean redirectsWithBody(final String s) {
        return s.equals("PROPFIND");
    }
    
    public static boolean requiresRequestBody(final String s) {
        boolean b = false;
        if (s.equals("POST") || s.equals("PUT") || s.equals("PATCH") || s.equals("PROPPATCH") || s.equals("REPORT")) {
            b = true;
        }
        return b;
    }
}
