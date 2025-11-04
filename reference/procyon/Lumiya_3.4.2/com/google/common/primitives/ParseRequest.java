// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@GwtCompatible
final class ParseRequest
{
    final int radix;
    final String rawValue;
    
    private ParseRequest(final String rawValue, final int radix) {
        this.rawValue = rawValue;
        this.radix = radix;
    }
    
    static ParseRequest fromString(String s) {
        int n = 16;
        if (s.length() != 0) {
            final char char1 = s.charAt(0);
            if (!s.startsWith("0x") && !s.startsWith("0X")) {
                if (char1 != '#') {
                    if (char1 == '0' && s.length() > 1) {
                        s = s.substring(1);
                        n = 8;
                    }
                    else {
                        n = 10;
                    }
                }
                else {
                    s = s.substring(1);
                }
            }
            else {
                s = s.substring(2);
            }
            return new ParseRequest(s, n);
        }
        throw new NumberFormatException("empty string");
    }
}
