// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import java.io.IOException;

abstract class LineBuffer
{
    private StringBuilder line;
    private boolean sawReturn;
    
    LineBuffer() {
        this.line = new StringBuilder();
    }
    
    private boolean finishLine(final boolean b) throws IOException {
        final String string = this.line.toString();
        String s;
        if (!this.sawReturn) {
            if (!b) {
                s = "";
            }
            else {
                s = "\n";
            }
        }
        else if (!b) {
            s = "\r";
        }
        else {
            s = "\r\n";
        }
        this.handleLine(string, s);
        this.line = new StringBuilder();
        this.sawReturn = false;
        return b;
    }
    
    protected void add(final char[] str, final int n, final int n2) throws IOException {
        int n3;
        if (this.sawReturn && n2 > 0) {
            if (!this.finishLine(str[n] == '\n')) {
                n3 = n;
            }
            else {
                n3 = n + 1;
            }
        }
        else {
            n3 = n;
        }
        final int n4 = n + n2;
        int i = n3;
        int offset = n3;
        while (i < n4) {
            int n5 = offset;
            int n6 = i;
            Label_0144: {
                switch (str[i]) {
                    default: {
                        n6 = i;
                        n5 = offset;
                        break Label_0144;
                    }
                    case '\n': {
                        this.line.append(str, offset, i - offset);
                        this.finishLine(true);
                        n5 = i + 1;
                        n6 = i;
                        break Label_0144;
                    }
                    case '\r': {
                        this.line.append(str, offset, i - offset);
                        this.sawReturn = true;
                        if (i + 1 < n4) {
                            if (this.finishLine(str[i + 1] == '\n')) {
                                ++i;
                            }
                        }
                        n6 = i;
                        n5 = i + 1;
                    }
                    case '\u000b':
                    case '\f': {
                        i = n6 + 1;
                        offset = n5;
                        continue;
                    }
                }
            }
        }
        this.line.append(str, offset, n + n2 - offset);
    }
    
    protected void finish() throws IOException {
        if (this.sawReturn || this.line.length() > 0) {
            this.finishLine(false);
        }
    }
    
    protected abstract void handleLine(final String p0, final String p1) throws IOException;
}
