// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.xml;

import com.google.common.escape.Escapers;
import com.google.common.escape.Escaper;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public class XmlEscapers
{
    private static final char MAX_ASCII_CONTROL_CHAR = '\u001f';
    private static final char MIN_ASCII_CONTROL_CHAR = '\0';
    private static final Escaper XML_ATTRIBUTE_ESCAPER;
    private static final Escaper XML_CONTENT_ESCAPER;
    private static final Escaper XML_ESCAPER;
    
    static {
        final char c = '\0';
        final Escapers.Builder builder = Escapers.builder();
        builder.setSafeRange('\0', '\ufffd');
        builder.setUnsafeReplacement("\ufffd");
        for (char c2 = c; c2 <= '\u001f'; ++c2) {
            if (c2 != '\t' && c2 != '\n' && c2 != '\r') {
                builder.addEscape(c2, "\ufffd");
            }
        }
        builder.addEscape('&', "&amp;");
        builder.addEscape('<', "&lt;");
        builder.addEscape('>', "&gt;");
        XML_CONTENT_ESCAPER = builder.build();
        builder.addEscape('\'', "&apos;");
        builder.addEscape('\"', "&quot;");
        XML_ESCAPER = builder.build();
        builder.addEscape('\t', "&#x9;");
        builder.addEscape('\n', "&#xA;");
        builder.addEscape('\r', "&#xD;");
        XML_ATTRIBUTE_ESCAPER = builder.build();
    }
    
    private XmlEscapers() {
    }
    
    public static Escaper xmlAttributeEscaper() {
        return XmlEscapers.XML_ATTRIBUTE_ESCAPER;
    }
    
    public static Escaper xmlContentEscaper() {
        return XmlEscapers.XML_CONTENT_ESCAPER;
    }
}
