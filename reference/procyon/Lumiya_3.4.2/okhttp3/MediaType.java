// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.Locale;
import java.util.regex.Pattern;

public final class MediaType
{
    private static final Pattern PARAMETER;
    private static final String QUOTED = "\"([^\"]*)\"";
    private static final String TOKEN = "([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)";
    private static final Pattern TYPE_SUBTYPE;
    private final String charset;
    private final String mediaType;
    private final String subtype;
    private final String type;
    
    static {
        TYPE_SUBTYPE = Pattern.compile("([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)/([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)");
        PARAMETER = Pattern.compile(";\\s*(?:([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)=(?:([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)|\"([^\"]*)\"))?");
    }
    
    private MediaType(final String mediaType, final String type, final String subtype, final String charset) {
        this.mediaType = mediaType;
        this.type = type;
        this.subtype = subtype;
        this.charset = charset;
    }
    
    public static MediaType parse(final String str) {
        final Matcher matcher = MediaType.TYPE_SUBTYPE.matcher(str);
        if (matcher.lookingAt()) {
            final String lowerCase = matcher.group(1).toLowerCase(Locale.US);
            final String lowerCase2 = matcher.group(2).toLowerCase(Locale.US);
            final Matcher matcher2 = MediaType.PARAMETER.matcher(str);
            int i = matcher.end();
            String anotherString = null;
            while (i < str.length()) {
                matcher2.region(i, str.length());
                if (!matcher2.lookingAt()) {
                    return null;
                }
                final String group = matcher2.group(1);
                String s;
                if (group == null) {
                    s = anotherString;
                }
                else {
                    s = anotherString;
                    if (group.equalsIgnoreCase("charset")) {
                        final String group2 = matcher2.group(2);
                        if (group2 == null) {
                            s = matcher2.group(3);
                        }
                        else {
                            s = group2;
                            if (group2.startsWith("'")) {
                                s = group2;
                                if (group2.endsWith("'")) {
                                    s = group2;
                                    if (group2.length() > 2) {
                                        s = group2.substring(1, group2.length() - 1);
                                    }
                                }
                            }
                        }
                        if (anotherString != null && !s.equalsIgnoreCase(anotherString)) {
                            throw new IllegalArgumentException("Multiple different charsets: " + str);
                        }
                    }
                }
                i = matcher2.end();
                anotherString = s;
            }
            return new MediaType(str, lowerCase, lowerCase2, anotherString);
        }
        return null;
    }
    
    public Charset charset() {
        Charset forName = null;
        if (this.charset != null) {
            forName = Charset.forName(this.charset);
        }
        return forName;
    }
    
    public Charset charset(Charset forName) {
        if (this.charset != null) {
            forName = Charset.forName(this.charset);
        }
        return forName;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = false;
        if (o instanceof MediaType && ((MediaType)o).mediaType.equals(this.mediaType)) {
            b = true;
        }
        return b;
    }
    
    @Override
    public int hashCode() {
        return this.mediaType.hashCode();
    }
    
    public String subtype() {
        return this.subtype;
    }
    
    @Override
    public String toString() {
        return this.mediaType;
    }
    
    public String type() {
        return this.type;
    }
}
