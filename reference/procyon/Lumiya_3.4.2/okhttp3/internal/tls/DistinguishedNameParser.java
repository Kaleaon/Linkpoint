// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.tls;

import javax.security.auth.x500.X500Principal;

final class DistinguishedNameParser
{
    private int beg;
    private char[] chars;
    private int cur;
    private final String dn;
    private int end;
    private final int length;
    private int pos;
    
    public DistinguishedNameParser(final X500Principal x500Principal) {
        this.dn = x500Principal.getName("RFC2253");
        this.length = this.dn.length();
    }
    
    private String escapedAV() {
        this.beg = this.pos;
        this.end = this.pos;
        while (this.pos < this.length) {
            switch (this.chars[this.pos]) {
                default: {
                    this.chars[this.end++] = this.chars[this.pos];
                    ++this.pos;
                    continue;
                }
                case '+':
                case ',':
                case ';': {
                    return new String(this.chars, this.beg, this.end - this.beg);
                }
                case '\\': {
                    this.chars[this.end++] = this.getEscaped();
                    ++this.pos;
                    continue;
                }
                case ' ': {
                    this.cur = this.end;
                    ++this.pos;
                    this.chars[this.end++] = 32;
                    while (this.pos < this.length && this.chars[this.pos] == ' ') {
                        this.chars[this.end++] = 32;
                        ++this.pos;
                    }
                    if (this.pos != this.length && this.chars[this.pos] != ',' && this.chars[this.pos] != '+' && this.chars[this.pos] != ';') {
                        continue;
                    }
                    return new String(this.chars, this.beg, this.cur - this.beg);
                }
            }
        }
        return new String(this.chars, this.beg, this.end - this.beg);
    }
    
    private int getByte(int n) {
        if (n + 1 < this.length) {
            int n2 = this.chars[n];
            if (n2 >= 48 && n2 <= 57) {
                n2 -= 48;
            }
            else if (n2 >= 97 && n2 <= 102) {
                n2 -= 87;
            }
            else {
                if (n2 < 65 || n2 > 70) {
                    throw new IllegalStateException("Malformed DN: " + this.dn);
                }
                n2 -= 55;
            }
            n = this.chars[n + 1];
            if (n >= 48 && n <= 57) {
                n -= 48;
            }
            else if (n >= 97 && n <= 102) {
                n -= 87;
            }
            else {
                if (n < 65 || n > 70) {
                    throw new IllegalStateException("Malformed DN: " + this.dn);
                }
                n -= 55;
            }
            return (n2 << 4) + n;
        }
        throw new IllegalStateException("Malformed DN: " + this.dn);
    }
    
    private char getEscaped() {
        ++this.pos;
        if (this.pos == this.length) {
            throw new IllegalStateException("Unexpected end of DN: " + this.dn);
        }
        switch (this.chars[this.pos]) {
            default: {
                return this.getUTF8();
            }
            case ' ':
            case '\"':
            case '#':
            case '%':
            case '*':
            case '+':
            case ',':
            case ';':
            case '<':
            case '=':
            case '>':
            case '\\':
            case '_': {
                return this.chars[this.pos];
            }
        }
    }
    
    private char getUTF8() {
        final int byte1 = this.getByte(this.pos);
        ++this.pos;
        if (byte1 < 128) {
            return (char)byte1;
        }
        if (byte1 >= 192 && byte1 <= 247) {
            int n;
            int n2;
            if (byte1 > 223) {
                if (byte1 > 239) {
                    n = 3;
                    n2 = (byte1 & 0x7);
                }
                else {
                    n = 2;
                    n2 = (byte1 & 0xF);
                }
            }
            else {
                n = 1;
                n2 = (byte1 & 0x1F);
            }
            for (int i = 0; i < n; ++i) {
                ++this.pos;
                if (this.pos == this.length || this.chars[this.pos] != '\\') {
                    return '?';
                }
                ++this.pos;
                final int byte2 = this.getByte(this.pos);
                ++this.pos;
                if ((byte2 & 0xC0) != 0x80) {
                    return '?';
                }
                n2 = (n2 << 6) + (byte2 & 0x3F);
            }
            return (char)n2;
        }
        return '?';
    }
    
    private String hexAV() {
        int i = 0;
        if (this.pos + 4 < this.length) {
            this.beg = this.pos;
            ++this.pos;
            while (true) {
                while (this.pos != this.length && this.chars[this.pos] != '+' && this.chars[this.pos] != ',' && this.chars[this.pos] != ';') {
                    if (this.chars[this.pos] != ' ') {
                        if (this.chars[this.pos] >= 'A' && this.chars[this.pos] <= 'F') {
                            final char[] chars = this.chars;
                            final int pos = this.pos;
                            chars[pos] += ' ';
                        }
                        ++this.pos;
                    }
                    else {
                        this.end = this.pos;
                        ++this.pos;
                        while (this.pos < this.length && this.chars[this.pos] == ' ') {
                            ++this.pos;
                        }
                        final int count = this.end - this.beg;
                        if (count >= 5 && (count & 0x1) != 0x0) {
                            final byte[] array = new byte[count / 2];
                            int n = this.beg + 1;
                            while (i < array.length) {
                                array[i] = (byte)this.getByte(n);
                                n += 2;
                                ++i;
                            }
                            return new String(this.chars, this.beg, count);
                        }
                        throw new IllegalStateException("Unexpected end of DN: " + this.dn);
                    }
                }
                this.end = this.pos;
                continue;
            }
        }
        throw new IllegalStateException("Unexpected end of DN: " + this.dn);
    }
    
    private String nextAT() {
        while (this.pos < this.length && this.chars[this.pos] == ' ') {
            ++this.pos;
        }
        if (this.pos == this.length) {
            return null;
        }
        this.beg = this.pos;
        ++this.pos;
        while (this.pos < this.length && this.chars[this.pos] != '=' && this.chars[this.pos] != ' ') {
            ++this.pos;
        }
        if (this.pos < this.length) {
            this.end = this.pos;
            if (this.chars[this.pos] == ' ') {
                while (this.pos < this.length && this.chars[this.pos] != '=' && this.chars[this.pos] == ' ') {
                    ++this.pos;
                }
                if (this.chars[this.pos] != '=' || this.pos == this.length) {
                    throw new IllegalStateException("Unexpected end of DN: " + this.dn);
                }
            }
            ++this.pos;
            while (this.pos < this.length && this.chars[this.pos] == ' ') {
                ++this.pos;
            }
            if (this.end - this.beg > 4 && this.chars[this.beg + 3] == '.') {
                if (this.chars[this.beg] == 'O' || this.chars[this.beg] == 'o') {
                    if (this.chars[this.beg + 1] == 'I' || this.chars[this.beg + 1] == 'i') {
                        if (this.chars[this.beg + 2] == 'D' || this.chars[this.beg + 2] == 'd') {
                            this.beg += 4;
                        }
                    }
                }
            }
            return new String(this.chars, this.beg, this.end - this.beg);
        }
        throw new IllegalStateException("Unexpected end of DN: " + this.dn);
    }
    
    private String quotedAV() {
        ++this.pos;
        this.beg = this.pos;
        this.end = this.beg;
        while (this.pos != this.length) {
            if (this.chars[this.pos] == '\"') {
                ++this.pos;
                while (this.pos < this.length && this.chars[this.pos] == ' ') {
                    ++this.pos;
                }
                return new String(this.chars, this.beg, this.end - this.beg);
            }
            if (this.chars[this.pos] != '\\') {
                this.chars[this.end] = this.chars[this.pos];
            }
            else {
                this.chars[this.end] = this.getEscaped();
            }
            ++this.pos;
            ++this.end;
        }
        throw new IllegalStateException("Unexpected end of DN: " + this.dn);
    }
    
    public String findMostSpecific(final String s) {
        this.pos = 0;
        this.beg = 0;
        this.end = 0;
        this.cur = 0;
        this.chars = this.dn.toCharArray();
        String anotherString = this.nextAT();
        if (anotherString == null) {
            return null;
        }
        while (true) {
            String s2 = "";
            if (this.pos == this.length) {
                return null;
            }
            Label_0117: {
                switch (this.chars[this.pos]) {
                    default: {
                        s2 = this.escapedAV();
                        break Label_0117;
                    }
                    case '#': {
                        s2 = this.hexAV();
                        break Label_0117;
                    }
                    case '\"': {
                        s2 = this.quotedAV();
                    }
                    case '+':
                    case ',':
                    case ';': {
                        if (s.equalsIgnoreCase(anotherString)) {
                            return s2;
                        }
                        if (this.pos >= this.length) {
                            return null;
                        }
                        if (this.chars[this.pos] != ',' && this.chars[this.pos] != ';' && this.chars[this.pos] != '+') {
                            throw new IllegalStateException("Malformed DN: " + this.dn);
                        }
                        ++this.pos;
                        if ((anotherString = this.nextAT()) == null) {
                            throw new IllegalStateException("Malformed DN: " + this.dn);
                        }
                        continue;
                    }
                }
            }
        }
    }
}
