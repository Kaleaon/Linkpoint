// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.stream;

import java.io.EOFException;
import java.io.IOException;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.JsonReaderInternalAccess;
import java.io.Reader;
import java.io.Closeable;

public class JsonReader implements Closeable
{
    private static final long MIN_INCOMPLETE_INTEGER = -922337203685477580L;
    private static final char[] NON_EXECUTE_PREFIX;
    private static final int NUMBER_CHAR_DECIMAL = 3;
    private static final int NUMBER_CHAR_DIGIT = 2;
    private static final int NUMBER_CHAR_EXP_DIGIT = 7;
    private static final int NUMBER_CHAR_EXP_E = 5;
    private static final int NUMBER_CHAR_EXP_SIGN = 6;
    private static final int NUMBER_CHAR_FRACTION_DIGIT = 4;
    private static final int NUMBER_CHAR_NONE = 0;
    private static final int NUMBER_CHAR_SIGN = 1;
    private static final int PEEKED_BEGIN_ARRAY = 3;
    private static final int PEEKED_BEGIN_OBJECT = 1;
    private static final int PEEKED_BUFFERED = 11;
    private static final int PEEKED_DOUBLE_QUOTED = 9;
    private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
    private static final int PEEKED_END_ARRAY = 4;
    private static final int PEEKED_END_OBJECT = 2;
    private static final int PEEKED_EOF = 17;
    private static final int PEEKED_FALSE = 6;
    private static final int PEEKED_LONG = 15;
    private static final int PEEKED_NONE = 0;
    private static final int PEEKED_NULL = 7;
    private static final int PEEKED_NUMBER = 16;
    private static final int PEEKED_SINGLE_QUOTED = 8;
    private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
    private static final int PEEKED_TRUE = 5;
    private static final int PEEKED_UNQUOTED = 10;
    private static final int PEEKED_UNQUOTED_NAME = 14;
    private final char[] buffer;
    private final Reader in;
    private boolean lenient;
    private int limit;
    private int lineNumber;
    private int lineStart;
    private int[] pathIndices;
    private String[] pathNames;
    int peeked;
    private long peekedLong;
    private int peekedNumberLength;
    private String peekedString;
    private int pos;
    private int[] stack;
    private int stackSize;
    
    static {
        NON_EXECUTE_PREFIX = ")]}'\n".toCharArray();
        JsonReaderInternalAccess.INSTANCE = new JsonReaderInternalAccess() {
            @Override
            public void promoteNameToValue(final JsonReader jsonReader) throws IOException {
                if (!(jsonReader instanceof JsonTreeReader)) {
                    int n = jsonReader.peeked;
                    if (n == 0) {
                        n = jsonReader.doPeek();
                    }
                    if (n != 13) {
                        if (n != 12) {
                            if (n != 14) {
                                throw new IllegalStateException("Expected a name but was " + jsonReader.peek() + jsonReader.locationString());
                            }
                            jsonReader.peeked = 10;
                        }
                        else {
                            jsonReader.peeked = 8;
                        }
                    }
                    else {
                        jsonReader.peeked = 9;
                    }
                    return;
                }
                ((JsonTreeReader)jsonReader).promoteNameToValue();
            }
        };
    }
    
    public JsonReader(final Reader in) {
        this.lenient = false;
        this.buffer = new char[1024];
        this.pos = 0;
        this.limit = 0;
        this.lineNumber = 0;
        this.lineStart = 0;
        this.peeked = 0;
        this.stack = new int[32];
        this.stackSize = 0;
        this.stack[this.stackSize++] = 6;
        this.pathNames = new String[32];
        this.pathIndices = new int[32];
        if (in != null) {
            this.in = in;
            return;
        }
        throw new NullPointerException("in == null");
    }
    
    private void checkLenient() throws IOException {
        if (this.lenient) {
            return;
        }
        throw this.syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
    }
    
    private void consumeNonExecutePrefix() throws IOException {
        int i = 0;
        this.nextNonWhitespace(true);
        --this.pos;
        if (this.pos + JsonReader.NON_EXECUTE_PREFIX.length > this.limit && !this.fillBuffer(JsonReader.NON_EXECUTE_PREFIX.length)) {
            return;
        }
        while (i < JsonReader.NON_EXECUTE_PREFIX.length) {
            if (this.buffer[this.pos + i] != JsonReader.NON_EXECUTE_PREFIX[i]) {
                return;
            }
            ++i;
        }
        this.pos += JsonReader.NON_EXECUTE_PREFIX.length;
    }
    
    private boolean fillBuffer(int n) throws IOException {
        final char[] buffer = this.buffer;
        this.lineStart -= this.pos;
        if (this.limit == this.pos) {
            this.limit = 0;
        }
        else {
            this.limit -= this.pos;
            System.arraycopy(buffer, this.pos, buffer, 0, this.limit);
        }
        this.pos = 0;
        while (true) {
            final int read = this.in.read(buffer, this.limit, buffer.length - this.limit);
            if (read == -1) {
                return false;
            }
            this.limit += read;
            int n2;
            if (this.lineNumber != 0) {
                n2 = n;
            }
            else {
                n2 = n;
                if (this.lineStart == 0) {
                    n2 = n;
                    if (this.limit > 0) {
                        n2 = n;
                        if (buffer[0] == '\ufeff') {
                            ++this.pos;
                            ++this.lineStart;
                            n2 = n + 1;
                        }
                    }
                }
            }
            if (this.limit >= (n = n2)) {
                return true;
            }
        }
    }
    
    private boolean isLiteral(final char c) throws IOException {
        switch (c) {
            default: {
                return true;
            }
            case '#':
            case '/':
            case ';':
            case '=':
            case '\\': {
                this.checkLenient();
            }
            case '\t':
            case '\n':
            case '\f':
            case '\r':
            case ' ':
            case ',':
            case ':':
            case '[':
            case ']':
            case '{':
            case '}': {
                return false;
            }
        }
    }
    
    private int nextNonWhitespace(final boolean b) throws IOException {
        final char[] buffer = this.buffer;
        int lineStart = this.pos;
        int n = this.limit;
        while (true) {
            int pos;
            if (lineStart != n) {
                pos = lineStart;
            }
            else {
                this.pos = lineStart;
                if (this.fillBuffer(1)) {
                    pos = this.pos;
                    n = this.limit;
                }
                else {
                    if (!b) {
                        return -1;
                    }
                    throw new EOFException("End of input" + this.locationString());
                }
            }
            lineStart = pos + 1;
            final char c = buffer[pos];
            if (c != '\n') {
                if (c == ' ' || c == '\r') {
                    continue;
                }
                if (c == '\t') {
                    continue;
                }
                if (c != '/') {
                    if (c != '#') {
                        this.pos = lineStart;
                        return c;
                    }
                    this.pos = lineStart;
                    this.checkLenient();
                    this.skipToEndOfLine();
                    lineStart = this.pos;
                    n = this.limit;
                }
                else {
                    if ((this.pos = lineStart) == n) {
                        --this.pos;
                        final boolean fillBuffer = this.fillBuffer(2);
                        ++this.pos;
                        if (!fillBuffer) {
                            return c;
                        }
                    }
                    this.checkLenient();
                    switch (buffer[this.pos]) {
                        default: {
                            return c;
                        }
                        case '*': {
                            ++this.pos;
                            if (this.skipTo("*/")) {
                                lineStart = this.pos + 2;
                                n = this.limit;
                                continue;
                            }
                            throw this.syntaxError("Unterminated comment");
                        }
                        case '/': {
                            ++this.pos;
                            this.skipToEndOfLine();
                            lineStart = this.pos;
                            n = this.limit;
                            continue;
                        }
                    }
                }
            }
            else {
                ++this.lineNumber;
                this.lineStart = lineStart;
            }
        }
    }
    
    private String nextQuotedValue(final char c) throws IOException {
        StringBuilder sb = null;
        final char[] buffer = this.buffer;
        while (true) {
            int pos = this.pos;
            int n = this.limit;
            int i = pos;
            while (i < n) {
                int pos2 = i + 1;
                final char c2 = buffer[i];
                if (c2 != c) {
                    if (c2 != '\\') {
                        if (c2 == '\n') {
                            ++this.lineNumber;
                            this.lineStart = pos2;
                        }
                    }
                    else {
                        this.pos = pos2;
                        final int len = pos2 - pos - 1;
                        if (sb == null) {
                            sb = new StringBuilder(Math.max((len + 1) * 2, 16));
                        }
                        sb.append(buffer, pos, len);
                        sb.append(this.readEscapeCharacter());
                        pos2 = this.pos;
                        n = this.limit;
                        pos = pos2;
                    }
                    i = pos2;
                }
                else {
                    this.pos = pos2;
                    final int n2 = pos2 - pos - 1;
                    if (sb != null) {
                        sb.append(buffer, pos, n2);
                        return sb.toString();
                    }
                    return new String(buffer, pos, n2);
                }
            }
            if (sb == null) {
                sb = new StringBuilder(Math.max((i - pos) * 2, 16));
            }
            sb.append(buffer, pos, i - pos);
            this.pos = i;
            if (!this.fillBuffer(1)) {
                throw this.syntaxError("Unterminated string");
            }
        }
    }
    
    private String nextUnquotedValue() throws IOException {
        final int n = 0;
        StringBuilder sb = null;
        int n2 = 0;
        StringBuilder sb2 = null;
        Label_0237: {
        Label_0234:
            while (true) {
                if (this.pos + n2 >= this.limit) {
                    if (n2 >= this.buffer.length) {
                        if (sb == null) {
                            sb = new StringBuilder(Math.max(n2, 16));
                        }
                        sb.append(this.buffer, this.pos, n2);
                        this.pos += n2;
                        n2 = n;
                        sb2 = sb;
                        if (!this.fillBuffer(1)) {
                            break Label_0237;
                        }
                        n2 = 0;
                    }
                    else {
                        if (!this.fillBuffer(n2 + 1)) {
                            sb2 = sb;
                            break Label_0237;
                        }
                        continue;
                    }
                }
                else {
                    switch (this.buffer[this.pos + n2]) {
                        default: {
                            ++n2;
                            continue;
                        }
                        case '#':
                        case '/':
                        case ';':
                        case '=':
                        case '\\': {
                            this.checkLenient();
                        }
                        case '\t':
                        case '\n':
                        case '\f':
                        case '\r':
                        case ' ':
                        case ',':
                        case ':':
                        case '[':
                        case ']':
                        case '{':
                        case '}': {
                            break Label_0234;
                        }
                    }
                }
            }
            sb2 = sb;
        }
        String string;
        if (sb2 != null) {
            string = sb2.append(this.buffer, this.pos, n2).toString();
        }
        else {
            string = new String(this.buffer, this.pos, n2);
        }
        this.pos += n2;
        return string;
    }
    
    private int peekKeyword() throws IOException {
        final char c = this.buffer[this.pos];
        String s;
        String s2;
        int peeked;
        if (c != 't' && c != 'T') {
            if (c != 'f' && c != 'F') {
                if (c != 'n' && c != 'N') {
                    return 0;
                }
                s = "null";
                s2 = "NULL";
                peeked = 7;
            }
            else {
                s = "false";
                s2 = "FALSE";
                peeked = 6;
            }
        }
        else {
            s = "true";
            s2 = "TRUE";
            peeked = 5;
        }
        final int length = s.length();
        for (int i = 1; i < length; ++i) {
            if (this.pos + i >= this.limit && !this.fillBuffer(i + 1)) {
                return 0;
            }
            final char c2 = this.buffer[this.pos + i];
            if (c2 != s.charAt(i) && c2 != s2.charAt(i)) {
                return 0;
            }
        }
        if (this.pos + length < this.limit || this.fillBuffer(length + 1)) {
            if (this.isLiteral(this.buffer[this.pos + length])) {
                return 0;
            }
        }
        this.pos += length;
        return this.peeked = peeked;
    }
    
    private int peekNumber() throws IOException {
        final char[] buffer = this.buffer;
        int n = this.pos;
        int n2 = this.limit;
        long n3 = 0L;
        int n4 = 0;
        boolean b = true;
        int n5 = 0;
        int peekedNumberLength = 0;
    Label_0506:
        while (true) {
            if (n + peekedNumberLength == n2) {
                if (peekedNumberLength == buffer.length) {
                    return 0;
                }
                if (!this.fillBuffer(peekedNumberLength + 1)) {
                    break;
                }
                n = this.pos;
                n2 = this.limit;
            }
            final char c = buffer[n + peekedNumberLength];
            int n7 = 0;
            int n8 = 0;
            switch (c) {
                default: {
                    if (c >= '0' && c <= '9') {
                        if (n5 == 1 || n5 == 0) {
                            n3 = -(c - '0');
                            final int n6 = 2;
                            n7 = n4;
                            n8 = n6;
                            break;
                        }
                        if (n5 != 2) {
                            if (n5 == 3) {
                                final int n9 = 4;
                                n7 = n4;
                                n8 = n9;
                                break;
                            }
                            if (n5 != 5 && n5 != 6) {
                                final int n10 = n5;
                                n7 = n4;
                                n8 = n10;
                                break;
                            }
                            final int n11 = 7;
                            n7 = n4;
                            n8 = n11;
                            break;
                        }
                        else {
                            if (n3 == 0L) {
                                return 0;
                            }
                            final long n12 = 10L * n3 - (c - '0');
                            int n13;
                            if (n3 > -922337203685477580L) {
                                n13 = 1;
                            }
                            else {
                                n13 = 0;
                            }
                            boolean b2 = false;
                            Label_0426: {
                                Label_0423: {
                                    if (n13 == 0) {
                                        if (n3 == -922337203685477580L) {
                                            int n14;
                                            if (n12 >= n3) {
                                                n14 = 1;
                                            }
                                            else {
                                                n14 = 0;
                                            }
                                            if (n14 == 0) {
                                                break Label_0423;
                                            }
                                        }
                                        b2 = false;
                                        break Label_0426;
                                    }
                                }
                                b2 = true;
                            }
                            final int n15 = n4;
                            n3 = n12;
                            b &= b2;
                            n8 = n5;
                            n7 = n15;
                            break;
                        }
                    }
                    else {
                        if (this.isLiteral(c)) {
                            return 0;
                        }
                        break Label_0506;
                    }
                    break;
                }
                case 45: {
                    if (n5 == 0) {
                        n8 = 1;
                        n7 = 1;
                        break;
                    }
                    if (n5 != 5) {
                        return 0;
                    }
                    final int n16 = 6;
                    n7 = n4;
                    n8 = n16;
                    break;
                }
                case 43: {
                    if (n5 != 5) {
                        return 0;
                    }
                    final int n17 = 6;
                    n7 = n4;
                    n8 = n17;
                    break;
                }
                case 69:
                case 101: {
                    if (n5 != 2 && n5 != 4) {
                        return 0;
                    }
                    final int n18 = 5;
                    n7 = n4;
                    n8 = n18;
                    break;
                }
                case 46: {
                    if (n5 != 2) {
                        return 0;
                    }
                    final int n19 = 3;
                    n7 = n4;
                    n8 = n19;
                    break;
                }
            }
            final int n20 = peekedNumberLength + 1;
            final int n21 = n7;
            n5 = n8;
            n4 = n21;
            peekedNumberLength = n20;
        }
        if (n5 == 2 && b && (n3 != Long.MIN_VALUE || n4 != 0) && (n3 != 0L || n4 == 0)) {
            long peekedLong = n3;
            if (n4 == 0) {
                peekedLong = -n3;
            }
            this.peekedLong = peekedLong;
            this.pos += peekedNumberLength;
            return this.peeked = 15;
        }
        if (n5 != 2 && n5 != 4 && n5 != 7) {
            return 0;
        }
        this.peekedNumberLength = peekedNumberLength;
        return this.peeked = 16;
    }
    
    private void push(final int n) {
        if (this.stackSize == this.stack.length) {
            final int[] stack = new int[this.stackSize * 2];
            final int[] pathIndices = new int[this.stackSize * 2];
            final String[] pathNames = new String[this.stackSize * 2];
            System.arraycopy(this.stack, 0, stack, 0, this.stackSize);
            System.arraycopy(this.pathIndices, 0, pathIndices, 0, this.stackSize);
            System.arraycopy(this.pathNames, 0, pathNames, 0, this.stackSize);
            this.stack = stack;
            this.pathIndices = pathIndices;
            this.pathNames = pathNames;
        }
        this.stack[this.stackSize++] = n;
    }
    
    private char readEscapeCharacter() throws IOException {
        if (this.pos == this.limit && !this.fillBuffer(1)) {
            throw this.syntaxError("Unterminated escape sequence");
        }
        final char c = this.buffer[this.pos++];
        switch (c) {
            default: {
                throw this.syntaxError("Invalid escape sequence");
            }
            case 117: {
                if (this.pos + 4 > this.limit && !this.fillBuffer(4)) {
                    throw this.syntaxError("Unterminated escape sequence");
                }
                final int pos = this.pos;
                final char c2 = '\0';
                int i = pos;
                char c3 = c2;
                while (i < pos + 4) {
                    final char c4 = this.buffer[i];
                    final char c5 = (char)(c3 << 4);
                    char c6;
                    if (c4 >= '0' && c4 <= '9') {
                        c6 = (char)(c5 + (c4 - '0'));
                    }
                    else if (c4 >= 'a' && c4 <= 'f') {
                        c6 = (char)(c5 + (c4 - 'a' + 10));
                    }
                    else {
                        if (c4 < 'A' || c4 > 'F') {
                            throw new NumberFormatException("\\u" + new String(this.buffer, this.pos, 4));
                        }
                        c6 = (char)(c5 + (c4 - 'A' + 10));
                    }
                    ++i;
                    c3 = c6;
                }
                this.pos += 4;
                return c3;
            }
            case 116: {
                return '\t';
            }
            case 98: {
                return '\b';
            }
            case 110: {
                return '\n';
            }
            case 114: {
                return '\r';
            }
            case 102: {
                return '\f';
            }
            case 10: {
                ++this.lineNumber;
                this.lineStart = this.pos;
            }
            case 34:
            case 39:
            case 47:
            case 92: {
                return c;
            }
        }
    }
    
    private void skipQuotedValue(final char c) throws IOException {
        final char[] buffer = this.buffer;
        while (true) {
            int i = this.pos;
            int n = this.limit;
            while (i < n) {
                final int pos = i + 1;
                final char c2 = buffer[i];
                if (c2 == c) {
                    this.pos = pos;
                    return;
                }
                if (c2 != '\\') {
                    if (c2 != '\n') {
                        i = pos;
                    }
                    else {
                        ++this.lineNumber;
                        this.lineStart = pos;
                        i = pos;
                    }
                }
                else {
                    this.pos = pos;
                    this.readEscapeCharacter();
                    i = this.pos;
                    n = this.limit;
                }
            }
            this.pos = i;
            if (!this.fillBuffer(1)) {
                throw this.syntaxError("Unterminated string");
            }
        }
    }
    
    private boolean skipTo(final String s) throws IOException {
        final int length = s.length();
        while (this.pos + length <= this.limit || this.fillBuffer(length)) {
            Label_0071: {
                if (this.buffer[this.pos] != '\n') {
                    for (int i = 0; i < length; ++i) {
                        if (this.buffer[this.pos + i] != s.charAt(i)) {
                            break Label_0071;
                        }
                    }
                    return true;
                }
                ++this.lineNumber;
                this.lineStart = this.pos + 1;
            }
            ++this.pos;
        }
        return false;
    }
    
    private void skipToEndOfLine() throws IOException {
        while (this.pos < this.limit || this.fillBuffer(1)) {
            final char c = this.buffer[this.pos++];
            if (c != '\n') {
                if (c != '\r') {
                    continue;
                }
            }
            else {
                ++this.lineNumber;
                this.lineStart = this.pos;
            }
        }
    }
    
    private void skipUnquotedValue() throws IOException {
        int n = 0;
    Label_0194:
        while (true) {
            n = 0;
            while (this.pos + n < this.limit) {
                switch (this.buffer[this.pos + n]) {
                    default: {
                        ++n;
                        continue;
                    }
                    case '#':
                    case '/':
                    case ';':
                    case '=':
                    case '\\': {
                        this.checkLenient();
                    }
                    case '\t':
                    case '\n':
                    case '\f':
                    case '\r':
                    case ' ':
                    case ',':
                    case ':':
                    case '[':
                    case ']':
                    case '{':
                    case '}': {
                        break Label_0194;
                    }
                }
            }
            this.pos += n;
            if (!this.fillBuffer(1)) {
                return;
            }
        }
        this.pos += n;
    }
    
    private IOException syntaxError(final String str) throws IOException {
        throw new MalformedJsonException(str + this.locationString());
    }
    
    public void beginArray() throws IOException {
        int n = this.peeked;
        if (n == 0) {
            n = this.doPeek();
        }
        if (n != 3) {
            throw new IllegalStateException("Expected BEGIN_ARRAY but was " + this.peek() + this.locationString());
        }
        this.push(1);
        this.pathIndices[this.stackSize - 1] = 0;
        this.peeked = 0;
    }
    
    public void beginObject() throws IOException {
        int n = this.peeked;
        if (n == 0) {
            n = this.doPeek();
        }
        if (n != 1) {
            throw new IllegalStateException("Expected BEGIN_OBJECT but was " + this.peek() + this.locationString());
        }
        this.push(3);
        this.peeked = 0;
    }
    
    @Override
    public void close() throws IOException {
        this.peeked = 0;
        this.stack[0] = 8;
        this.stackSize = 1;
        this.in.close();
    }
    
    int doPeek() throws IOException {
        final int n = this.stack[this.stackSize - 1];
        if (n != 1) {
            if (n != 2) {
                if (n != 3 && n != 5) {
                    if (n != 4) {
                        if (n != 6) {
                            if (n != 7) {
                                if (n == 8) {
                                    throw new IllegalStateException("JsonReader is closed");
                                }
                            }
                            else {
                                if (this.nextNonWhitespace(false) == -1) {
                                    return this.peeked = 17;
                                }
                                this.checkLenient();
                                --this.pos;
                            }
                        }
                        else {
                            if (this.lenient) {
                                this.consumeNonExecutePrefix();
                            }
                            this.stack[this.stackSize - 1] = 7;
                        }
                    }
                    else {
                        this.stack[this.stackSize - 1] = 5;
                        switch (this.nextNonWhitespace(true)) {
                            case 58: {
                                break;
                            }
                            default: {
                                throw this.syntaxError("Expected ':'");
                            }
                            case 61: {
                                this.checkLenient();
                                if (this.pos >= this.limit && !this.fillBuffer(1)) {
                                    break;
                                }
                                if (this.buffer[this.pos] == '>') {
                                    ++this.pos;
                                    break;
                                }
                                break;
                            }
                        }
                    }
                }
                else {
                    this.stack[this.stackSize - 1] = 4;
                    if (n == 5) {
                        switch (this.nextNonWhitespace(true)) {
                            case 59: {
                                this.checkLenient();
                            }
                            case 44: {
                                break;
                            }
                            default: {
                                throw this.syntaxError("Unterminated object");
                            }
                            case 125: {
                                return this.peeked = 2;
                            }
                        }
                    }
                    final int nextNonWhitespace = this.nextNonWhitespace(true);
                    switch (nextNonWhitespace) {
                        default: {
                            this.checkLenient();
                            --this.pos;
                            if (!this.isLiteral((char)nextNonWhitespace)) {
                                throw this.syntaxError("Expected name");
                            }
                            return this.peeked = 14;
                        }
                        case 34: {
                            return this.peeked = 13;
                        }
                        case 39: {
                            this.checkLenient();
                            return this.peeked = 12;
                        }
                        case 125: {
                            if (n == 5) {
                                throw this.syntaxError("Expected name");
                            }
                            return this.peeked = 2;
                        }
                    }
                }
            }
            else {
                switch (this.nextNonWhitespace(true)) {
                    case 59: {
                        this.checkLenient();
                    }
                    case 44: {
                        break;
                    }
                    default: {
                        throw this.syntaxError("Unterminated array");
                    }
                    case 93: {
                        return this.peeked = 4;
                    }
                }
            }
        }
        else {
            this.stack[this.stackSize - 1] = 2;
        }
        switch (this.nextNonWhitespace(true)) {
            default: {
                --this.pos;
                final int peekKeyword = this.peekKeyword();
                if (peekKeyword != 0) {
                    return peekKeyword;
                }
                final int peekNumber = this.peekNumber();
                if (peekNumber != 0) {
                    return peekNumber;
                }
                if (this.isLiteral(this.buffer[this.pos])) {
                    this.checkLenient();
                    return this.peeked = 10;
                }
                throw this.syntaxError("Expected value");
            }
            case 93: {
                if (n != 1)
                return this.peeked = 4;
            }
            case 44:
            case 59: {
                if (n != 1 && n != 2) {
                    throw this.syntaxError("Unexpected value");
                }
                this.checkLenient();
                --this.pos;
                return this.peeked = 7;
            }
            case 39: {
                this.checkLenient();
                return this.peeked = 8;
            }
            case 34: {
                return this.peeked = 9;
            }
            case 91: {
                return this.peeked = 3;
            }
            case 123: {
                return this.peeked = 1;
            }
        }
    }
    
    public void endArray() throws IOException {
        int n = this.peeked;
        if (n == 0) {
            n = this.doPeek();
        }
        if (n != 4) {
            throw new IllegalStateException("Expected END_ARRAY but was " + this.peek() + this.locationString());
        }
        --this.stackSize;
        final int[] pathIndices = this.pathIndices;
        final int n2 = this.stackSize - 1;
        ++pathIndices[n2];
        this.peeked = 0;
    }
    
    public void endObject() throws IOException {
        int n = this.peeked;
        if (n == 0) {
            n = this.doPeek();
        }
        if (n != 2) {
            throw new IllegalStateException("Expected END_OBJECT but was " + this.peek() + this.locationString());
        }
        --this.stackSize;
        this.pathNames[this.stackSize] = null;
        final int[] pathIndices = this.pathIndices;
        final int n2 = this.stackSize - 1;
        ++pathIndices[n2];
        this.peeked = 0;
    }
    
    public String getPath() {
        final StringBuilder append = new StringBuilder().append('$');
        for (int i = 0; i < this.stackSize; ++i) {
            switch (this.stack[i]) {
                case 1:
                case 2: {
                    append.append('[').append(this.pathIndices[i]).append(']');
                    break;
                }
                case 3:
                case 4:
                case 5: {
                    append.append('.');
                    if (this.pathNames[i] != null) {
                        append.append(this.pathNames[i]);
                        break;
                    }
                    break;
                }
            }
        }
        return append.toString();
    }
    
    public boolean hasNext() throws IOException {
        boolean b = false;
        int n = this.peeked;
        if (n == 0) {
            n = this.doPeek();
        }
        if (n != 2 && n != 4) {
            b = true;
        }
        return b;
    }
    
    public final boolean isLenient() {
        return this.lenient;
    }
    
    String locationString() {
        return " at line " + (this.lineNumber + 1) + " column " + (this.pos - this.lineStart + 1) + " path " + this.getPath();
    }
    
    public boolean nextBoolean() throws IOException {
        int n = this.peeked;
        if (n == 0) {
            n = this.doPeek();
        }
        if (n == 5) {
            this.peeked = 0;
            final int[] pathIndices = this.pathIndices;
            final int n2 = this.stackSize - 1;
            ++pathIndices[n2];
            return true;
        }
        if (n != 6) {
            throw new IllegalStateException("Expected a boolean but was " + this.peek() + this.locationString());
        }
        this.peeked = 0;
        final int[] pathIndices2 = this.pathIndices;
        final int n3 = this.stackSize - 1;
        ++pathIndices2[n3];
        return false;
    }
    
    public double nextDouble() throws IOException {
        int n = this.peeked;
        if (n == 0) {
            n = this.doPeek();
        }
        if (n == 15) {
            this.peeked = 0;
            final int[] pathIndices = this.pathIndices;
            final int n2 = this.stackSize - 1;
            ++pathIndices[n2];
            return (double)this.peekedLong;
        }
        if (n != 16) {
            if (n != 8 && n != 9) {
                if (n != 10) {
                    if (n != 11) {
                        throw new IllegalStateException("Expected a double but was " + this.peek() + this.locationString());
                    }
                }
                else {
                    this.peekedString = this.nextUnquotedValue();
                }
            }
            else {
                char c;
                if (n != 8) {
                    c = '\"';
                }
                else {
                    c = '\'';
                }
                this.peekedString = this.nextQuotedValue(c);
            }
        }
        else {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        }
        this.peeked = 11;
        final double double1 = Double.parseDouble(this.peekedString);
        if (!this.lenient && (Double.isNaN(double1) || Double.isInfinite(double1))) {
            throw new MalformedJsonException("JSON forbids NaN and infinities: " + double1 + this.locationString());
        }
        this.peekedString = null;
        this.peeked = 0;
        final int[] pathIndices2 = this.pathIndices;
        final int n3 = this.stackSize - 1;
        ++pathIndices2[n3];
        return double1;
    }
    
    public int nextInt() throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        com/google/gson/stream/JsonReader.peeked:I
        //     4: istore_1       
        //     5: iload_1        
        //     6: ifeq            91
        //     9: iload_1        
        //    10: bipush          15
        //    12: if_icmpeq       99
        //    15: iload_1        
        //    16: bipush          16
        //    18: if_icmpeq       183
        //    21: iload_1        
        //    22: bipush          8
        //    24: if_icmpne       284
        //    27: iload_1        
        //    28: bipush          10
        //    30: if_icmpeq       334
        //    33: iload_1        
        //    34: bipush          8
        //    36: if_icmpeq       345
        //    39: bipush          34
        //    41: istore_1       
        //    42: iload_1        
        //    43: istore_2       
        //    44: aload_0        
        //    45: aload_0        
        //    46: iload_2        
        //    47: invokespecial   com/google/gson/stream/JsonReader.nextQuotedValue:(C)Ljava/lang/String;
        //    50: putfield        com/google/gson/stream/JsonReader.peekedString:Ljava/lang/String;
        //    53: aload_0        
        //    54: getfield        com/google/gson/stream/JsonReader.peekedString:Ljava/lang/String;
        //    57: invokestatic    java/lang/Integer.parseInt:(Ljava/lang/String;)I
        //    60: istore_1       
        //    61: aload_0        
        //    62: iconst_0       
        //    63: putfield        com/google/gson/stream/JsonReader.peeked:I
        //    66: aload_0        
        //    67: getfield        com/google/gson/stream/JsonReader.pathIndices:[I
        //    70: astore_3       
        //    71: aload_0        
        //    72: getfield        com/google/gson/stream/JsonReader.stackSize:I
        //    75: iconst_1       
        //    76: isub           
        //    77: istore          4
        //    79: aload_3        
        //    80: iload           4
        //    82: aload_3        
        //    83: iload           4
        //    85: iaload         
        //    86: iconst_1       
        //    87: iadd           
        //    88: iastore        
        //    89: iload_1        
        //    90: ireturn        
        //    91: aload_0        
        //    92: invokevirtual   com/google/gson/stream/JsonReader.doPeek:()I
        //    95: istore_1       
        //    96: goto            9
        //    99: aload_0        
        //   100: getfield        com/google/gson/stream/JsonReader.peekedLong:J
        //   103: l2i            
        //   104: istore          4
        //   106: aload_0        
        //   107: getfield        com/google/gson/stream/JsonReader.peekedLong:J
        //   110: iload           4
        //   112: i2l            
        //   113: lcmp           
        //   114: ifeq            155
        //   117: new             Ljava/lang/NumberFormatException;
        //   120: dup            
        //   121: new             Ljava/lang/StringBuilder;
        //   124: dup            
        //   125: invokespecial   java/lang/StringBuilder.<init>:()V
        //   128: ldc_w           "Expected an int but was "
        //   131: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   134: aload_0        
        //   135: getfield        com/google/gson/stream/JsonReader.peekedLong:J
        //   138: invokevirtual   java/lang/StringBuilder.append:(J)Ljava/lang/StringBuilder;
        //   141: aload_0        
        //   142: invokevirtual   com/google/gson/stream/JsonReader.locationString:()Ljava/lang/String;
        //   145: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   148: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   151: invokespecial   java/lang/NumberFormatException.<init>:(Ljava/lang/String;)V
        //   154: athrow         
        //   155: aload_0        
        //   156: iconst_0       
        //   157: putfield        com/google/gson/stream/JsonReader.peeked:I
        //   160: aload_0        
        //   161: getfield        com/google/gson/stream/JsonReader.pathIndices:[I
        //   164: astore_3       
        //   165: aload_0        
        //   166: getfield        com/google/gson/stream/JsonReader.stackSize:I
        //   169: iconst_1       
        //   170: isub           
        //   171: istore_1       
        //   172: aload_3        
        //   173: iload_1        
        //   174: aload_3        
        //   175: iload_1        
        //   176: iaload         
        //   177: iconst_1       
        //   178: iadd           
        //   179: iastore        
        //   180: iload           4
        //   182: ireturn        
        //   183: aload_0        
        //   184: new             Ljava/lang/String;
        //   187: dup            
        //   188: aload_0        
        //   189: getfield        com/google/gson/stream/JsonReader.buffer:[C
        //   192: aload_0        
        //   193: getfield        com/google/gson/stream/JsonReader.pos:I
        //   196: aload_0        
        //   197: getfield        com/google/gson/stream/JsonReader.peekedNumberLength:I
        //   200: invokespecial   java/lang/String.<init>:([CII)V
        //   203: putfield        com/google/gson/stream/JsonReader.peekedString:Ljava/lang/String;
        //   206: aload_0        
        //   207: aload_0        
        //   208: getfield        com/google/gson/stream/JsonReader.pos:I
        //   211: aload_0        
        //   212: getfield        com/google/gson/stream/JsonReader.peekedNumberLength:I
        //   215: iadd           
        //   216: putfield        com/google/gson/stream/JsonReader.pos:I
        //   219: aload_0        
        //   220: bipush          11
        //   222: putfield        com/google/gson/stream/JsonReader.peeked:I
        //   225: aload_0        
        //   226: getfield        com/google/gson/stream/JsonReader.peekedString:Ljava/lang/String;
        //   229: invokestatic    java/lang/Double.parseDouble:(Ljava/lang/String;)D
        //   232: dstore          5
        //   234: dload           5
        //   236: d2i            
        //   237: istore_1       
        //   238: iload_1        
        //   239: i2d            
        //   240: dload           5
        //   242: dcmpl          
        //   243: ifeq            353
        //   246: new             Ljava/lang/NumberFormatException;
        //   249: dup            
        //   250: new             Ljava/lang/StringBuilder;
        //   253: dup            
        //   254: invokespecial   java/lang/StringBuilder.<init>:()V
        //   257: ldc_w           "Expected an int but was "
        //   260: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   263: aload_0        
        //   264: getfield        com/google/gson/stream/JsonReader.peekedString:Ljava/lang/String;
        //   267: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   270: aload_0        
        //   271: invokevirtual   com/google/gson/stream/JsonReader.locationString:()Ljava/lang/String;
        //   274: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   277: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   280: invokespecial   java/lang/NumberFormatException.<init>:(Ljava/lang/String;)V
        //   283: athrow         
        //   284: iload_1        
        //   285: bipush          9
        //   287: if_icmpeq       27
        //   290: iload_1        
        //   291: bipush          10
        //   293: if_icmpeq       27
        //   296: new             Ljava/lang/IllegalStateException;
        //   299: dup            
        //   300: new             Ljava/lang/StringBuilder;
        //   303: dup            
        //   304: invokespecial   java/lang/StringBuilder.<init>:()V
        //   307: ldc_w           "Expected an int but was "
        //   310: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   313: aload_0        
        //   314: invokevirtual   com/google/gson/stream/JsonReader.peek:()Lcom/google/gson/stream/JsonToken;
        //   317: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   320: aload_0        
        //   321: invokevirtual   com/google/gson/stream/JsonReader.locationString:()Ljava/lang/String;
        //   324: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   327: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   330: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
        //   333: athrow         
        //   334: aload_0        
        //   335: aload_0        
        //   336: invokespecial   com/google/gson/stream/JsonReader.nextUnquotedValue:()Ljava/lang/String;
        //   339: putfield        com/google/gson/stream/JsonReader.peekedString:Ljava/lang/String;
        //   342: goto            53
        //   345: bipush          39
        //   347: istore_1       
        //   348: iload_1        
        //   349: istore_2       
        //   350: goto            44
        //   353: aload_0        
        //   354: aconst_null    
        //   355: putfield        com/google/gson/stream/JsonReader.peekedString:Ljava/lang/String;
        //   358: aload_0        
        //   359: iconst_0       
        //   360: putfield        com/google/gson/stream/JsonReader.peeked:I
        //   363: aload_0        
        //   364: getfield        com/google/gson/stream/JsonReader.pathIndices:[I
        //   367: astore_3       
        //   368: aload_0        
        //   369: getfield        com/google/gson/stream/JsonReader.stackSize:I
        //   372: iconst_1       
        //   373: isub           
        //   374: istore          4
        //   376: aload_3        
        //   377: iload           4
        //   379: aload_3        
        //   380: iload           4
        //   382: iaload         
        //   383: iconst_1       
        //   384: iadd           
        //   385: iastore        
        //   386: iload_1        
        //   387: ireturn        
        //   388: astore_3       
        //   389: goto            219
        //    Exceptions:
        //  throws java.io.IOException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                             
        //  -----  -----  -----  -----  ---------------------------------
        //  53     79     388    392    Ljava/lang/NumberFormatException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: cmpeq:boolean(var_1_04:int, ldc:int(9))
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public long nextLong() throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        com/google/gson/stream/JsonReader.peeked:I
        //     4: istore_1       
        //     5: iload_1        
        //     6: ifeq            91
        //     9: iload_1        
        //    10: bipush          15
        //    12: if_icmpeq       99
        //    15: iload_1        
        //    16: bipush          16
        //    18: if_icmpeq       132
        //    21: iload_1        
        //    22: bipush          8
        //    24: if_icmpne       233
        //    27: iload_1        
        //    28: bipush          10
        //    30: if_icmpeq       283
        //    33: iload_1        
        //    34: bipush          8
        //    36: if_icmpeq       294
        //    39: bipush          34
        //    41: istore_1       
        //    42: iload_1        
        //    43: istore_2       
        //    44: aload_0        
        //    45: aload_0        
        //    46: iload_2        
        //    47: invokespecial   com/google/gson/stream/JsonReader.nextQuotedValue:(C)Ljava/lang/String;
        //    50: putfield        com/google/gson/stream/JsonReader.peekedString:Ljava/lang/String;
        //    53: aload_0        
        //    54: getfield        com/google/gson/stream/JsonReader.peekedString:Ljava/lang/String;
        //    57: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //    60: lstore_3       
        //    61: aload_0        
        //    62: iconst_0       
        //    63: putfield        com/google/gson/stream/JsonReader.peeked:I
        //    66: aload_0        
        //    67: getfield        com/google/gson/stream/JsonReader.pathIndices:[I
        //    70: astore          5
        //    72: aload_0        
        //    73: getfield        com/google/gson/stream/JsonReader.stackSize:I
        //    76: iconst_1       
        //    77: isub           
        //    78: istore_1       
        //    79: aload           5
        //    81: iload_1        
        //    82: aload           5
        //    84: iload_1        
        //    85: iaload         
        //    86: iconst_1       
        //    87: iadd           
        //    88: iastore        
        //    89: lload_3        
        //    90: lreturn        
        //    91: aload_0        
        //    92: invokevirtual   com/google/gson/stream/JsonReader.doPeek:()I
        //    95: istore_1       
        //    96: goto            9
        //    99: aload_0        
        //   100: iconst_0       
        //   101: putfield        com/google/gson/stream/JsonReader.peeked:I
        //   104: aload_0        
        //   105: getfield        com/google/gson/stream/JsonReader.pathIndices:[I
        //   108: astore          5
        //   110: aload_0        
        //   111: getfield        com/google/gson/stream/JsonReader.stackSize:I
        //   114: iconst_1       
        //   115: isub           
        //   116: istore_1       
        //   117: aload           5
        //   119: iload_1        
        //   120: aload           5
        //   122: iload_1        
        //   123: iaload         
        //   124: iconst_1       
        //   125: iadd           
        //   126: iastore        
        //   127: aload_0        
        //   128: getfield        com/google/gson/stream/JsonReader.peekedLong:J
        //   131: lreturn        
        //   132: aload_0        
        //   133: new             Ljava/lang/String;
        //   136: dup            
        //   137: aload_0        
        //   138: getfield        com/google/gson/stream/JsonReader.buffer:[C
        //   141: aload_0        
        //   142: getfield        com/google/gson/stream/JsonReader.pos:I
        //   145: aload_0        
        //   146: getfield        com/google/gson/stream/JsonReader.peekedNumberLength:I
        //   149: invokespecial   java/lang/String.<init>:([CII)V
        //   152: putfield        com/google/gson/stream/JsonReader.peekedString:Ljava/lang/String;
        //   155: aload_0        
        //   156: aload_0        
        //   157: getfield        com/google/gson/stream/JsonReader.pos:I
        //   160: aload_0        
        //   161: getfield        com/google/gson/stream/JsonReader.peekedNumberLength:I
        //   164: iadd           
        //   165: putfield        com/google/gson/stream/JsonReader.pos:I
        //   168: aload_0        
        //   169: bipush          11
        //   171: putfield        com/google/gson/stream/JsonReader.peeked:I
        //   174: aload_0        
        //   175: getfield        com/google/gson/stream/JsonReader.peekedString:Ljava/lang/String;
        //   178: invokestatic    java/lang/Double.parseDouble:(Ljava/lang/String;)D
        //   181: dstore          6
        //   183: dload           6
        //   185: d2l            
        //   186: lstore_3       
        //   187: lload_3        
        //   188: l2d            
        //   189: dload           6
        //   191: dcmpl          
        //   192: ifeq            302
        //   195: new             Ljava/lang/NumberFormatException;
        //   198: dup            
        //   199: new             Ljava/lang/StringBuilder;
        //   202: dup            
        //   203: invokespecial   java/lang/StringBuilder.<init>:()V
        //   206: ldc_w           "Expected a long but was "
        //   209: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   212: aload_0        
        //   213: getfield        com/google/gson/stream/JsonReader.peekedString:Ljava/lang/String;
        //   216: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   219: aload_0        
        //   220: invokevirtual   com/google/gson/stream/JsonReader.locationString:()Ljava/lang/String;
        //   223: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   226: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   229: invokespecial   java/lang/NumberFormatException.<init>:(Ljava/lang/String;)V
        //   232: athrow         
        //   233: iload_1        
        //   234: bipush          9
        //   236: if_icmpeq       27
        //   239: iload_1        
        //   240: bipush          10
        //   242: if_icmpeq       27
        //   245: new             Ljava/lang/IllegalStateException;
        //   248: dup            
        //   249: new             Ljava/lang/StringBuilder;
        //   252: dup            
        //   253: invokespecial   java/lang/StringBuilder.<init>:()V
        //   256: ldc_w           "Expected a long but was "
        //   259: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   262: aload_0        
        //   263: invokevirtual   com/google/gson/stream/JsonReader.peek:()Lcom/google/gson/stream/JsonToken;
        //   266: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   269: aload_0        
        //   270: invokevirtual   com/google/gson/stream/JsonReader.locationString:()Ljava/lang/String;
        //   273: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   276: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   279: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
        //   282: athrow         
        //   283: aload_0        
        //   284: aload_0        
        //   285: invokespecial   com/google/gson/stream/JsonReader.nextUnquotedValue:()Ljava/lang/String;
        //   288: putfield        com/google/gson/stream/JsonReader.peekedString:Ljava/lang/String;
        //   291: goto            53
        //   294: bipush          39
        //   296: istore_1       
        //   297: iload_1        
        //   298: istore_2       
        //   299: goto            44
        //   302: aload_0        
        //   303: aconst_null    
        //   304: putfield        com/google/gson/stream/JsonReader.peekedString:Ljava/lang/String;
        //   307: aload_0        
        //   308: iconst_0       
        //   309: putfield        com/google/gson/stream/JsonReader.peeked:I
        //   312: aload_0        
        //   313: getfield        com/google/gson/stream/JsonReader.pathIndices:[I
        //   316: astore          5
        //   318: aload_0        
        //   319: getfield        com/google/gson/stream/JsonReader.stackSize:I
        //   322: iconst_1       
        //   323: isub           
        //   324: istore_1       
        //   325: aload           5
        //   327: iload_1        
        //   328: aload           5
        //   330: iload_1        
        //   331: iaload         
        //   332: iconst_1       
        //   333: iadd           
        //   334: iastore        
        //   335: lload_3        
        //   336: lreturn        
        //   337: astore          5
        //   339: goto            168
        //    Exceptions:
        //  throws java.io.IOException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                             
        //  -----  -----  -----  -----  ---------------------------------
        //  53     79     337    342    Ljava/lang/NumberFormatException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: cmpeq:boolean(var_1_04:int, ldc:int(9))
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public String nextName() throws IOException {
        int n = this.peeked;
        if (n == 0) {
            n = this.doPeek();
        }
        String s;
        if (n != 14) {
            if (n != 12) {
                if (n != 13) {
                    throw new IllegalStateException("Expected a name but was " + this.peek() + this.locationString());
                }
                s = this.nextQuotedValue('\"');
            }
            else {
                s = this.nextQuotedValue('\'');
            }
        }
        else {
            s = this.nextUnquotedValue();
        }
        this.peeked = 0;
        return this.pathNames[this.stackSize - 1] = s;
    }
    
    public void nextNull() throws IOException {
        int n = this.peeked;
        if (n == 0) {
            n = this.doPeek();
        }
        if (n != 7) {
            throw new IllegalStateException("Expected null but was " + this.peek() + this.locationString());
        }
        this.peeked = 0;
        final int[] pathIndices = this.pathIndices;
        final int n2 = this.stackSize - 1;
        ++pathIndices[n2];
    }
    
    public String nextString() throws IOException {
        int n = this.peeked;
        if (n == 0) {
            n = this.doPeek();
        }
        String s;
        if (n != 10) {
            if (n != 8) {
                if (n != 9) {
                    if (n != 11) {
                        if (n != 15) {
                            if (n != 16) {
                                throw new IllegalStateException("Expected a string but was " + this.peek() + this.locationString());
                            }
                            s = new String(this.buffer, this.pos, this.peekedNumberLength);
                            this.pos += this.peekedNumberLength;
                        }
                        else {
                            s = Long.toString(this.peekedLong);
                        }
                    }
                    else {
                        s = this.peekedString;
                        this.peekedString = null;
                    }
                }
                else {
                    s = this.nextQuotedValue('\"');
                }
            }
            else {
                s = this.nextQuotedValue('\'');
            }
        }
        else {
            s = this.nextUnquotedValue();
        }
        this.peeked = 0;
        final int[] pathIndices = this.pathIndices;
        final int n2 = this.stackSize - 1;
        ++pathIndices[n2];
        return s;
    }
    
    public JsonToken peek() throws IOException {
        int n = this.peeked;
        if (n == 0) {
            n = this.doPeek();
        }
        switch (n) {
            default: {
                throw new AssertionError();
            }
            case 1: {
                return JsonToken.BEGIN_OBJECT;
            }
            case 2: {
                return JsonToken.END_OBJECT;
            }
            case 3: {
                return JsonToken.BEGIN_ARRAY;
            }
            case 4: {
                return JsonToken.END_ARRAY;
            }
            case 12:
            case 13:
            case 14: {
                return JsonToken.NAME;
            }
            case 5:
            case 6: {
                return JsonToken.BOOLEAN;
            }
            case 7: {
                return JsonToken.NULL;
            }
            case 8:
            case 9:
            case 10:
            case 11: {
                return JsonToken.STRING;
            }
            case 15:
            case 16: {
                return JsonToken.NUMBER;
            }
            case 17: {
                return JsonToken.END_DOCUMENT;
            }
        }
    }
    
    public final void setLenient(final boolean lenient) {
        this.lenient = lenient;
    }
    
    public void skipValue() throws IOException {
        int n = 0;
        int n3;
        do {
            int n2 = this.peeked;
            if (n2 == 0) {
                n2 = this.doPeek();
            }
            if (n2 != 3) {
                if (n2 != 1) {
                    if (n2 != 4) {
                        if (n2 != 2) {
                            if (n2 != 14 && n2 != 10) {
                                if (n2 != 8 && n2 != 12) {
                                    if (n2 != 9 && n2 != 13) {
                                        n3 = n;
                                        if (n2 == 16) {
                                            this.pos += this.peekedNumberLength;
                                            n3 = n;
                                        }
                                    }
                                    else {
                                        this.skipQuotedValue('\"');
                                        n3 = n;
                                    }
                                }
                                else {
                                    this.skipQuotedValue('\'');
                                    n3 = n;
                                }
                            }
                            else {
                                this.skipUnquotedValue();
                                n3 = n;
                            }
                        }
                        else {
                            --this.stackSize;
                            n3 = n - 1;
                        }
                    }
                    else {
                        --this.stackSize;
                        n3 = n - 1;
                    }
                }
                else {
                    this.push(3);
                    n3 = n + 1;
                }
            }
            else {
                this.push(1);
                n3 = n + 1;
            }
            this.peeked = 0;
        } while ((n = n3) != 0);
        final int[] pathIndices = this.pathIndices;
        final int n4 = this.stackSize - 1;
        ++pathIndices[n4];
        this.pathNames[this.stackSize - 1] = "null";
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + this.locationString();
    }
}
