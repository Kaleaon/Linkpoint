package com.google.gson.stream;

import com.google.common.logging.nano.Vr;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.bind.JsonTreeReader;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

public class JsonReader implements Closeable {
    private static final long MIN_INCOMPLETE_INTEGER = -922337203685477580L;
    private static final char[] NON_EXECUTE_PREFIX = ")]}'\n".toCharArray();
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
    private final char[] buffer = new char[1024];
    private final Reader in;
    private boolean lenient = false;
    private int limit = 0;
    private int lineNumber = 0;
    private int lineStart = 0;
    private int[] pathIndices;
    private String[] pathNames;
    int peeked = 0;
    private long peekedLong;
    private int peekedNumberLength;
    private String peekedString;
    private int pos = 0;
    private int[] stack = new int[32];
    private int stackSize = 0;

    static {
        JsonReaderInternalAccess.INSTANCE = new JsonReaderInternalAccess() {
            public void promoteNameToValue(JsonReader jsonReader) throws IOException {
                if (!(jsonReader instanceof JsonTreeReader)) {
                    int i = jsonReader.peeked;
                    if (i == 0) {
                        i = jsonReader.doPeek();
                    }
                    if (i == 13) {
                        jsonReader.peeked = 9;
                    } else if (i == 12) {
                        jsonReader.peeked = 8;
                    } else if (i != 14) {
                        throw new IllegalStateException("Expected a name but was " + jsonReader.peek() + jsonReader.locationString());
                    } else {
                        jsonReader.peeked = 10;
                    }
                } else {
                    ((JsonTreeReader) jsonReader).promoteNameToValue();
                }
            }
        };
    }

    public JsonReader(Reader reader) {
        int[] iArr = this.stack;
        int i = this.stackSize;
        this.stackSize = i + 1;
        iArr[i] = 6;
        this.pathNames = new String[32];
        this.pathIndices = new int[32];
        if (reader != null) {
            this.in = reader;
            return;
        }
        throw new NullPointerException("in == null");
    }

    private void checkLenient() throws IOException {
        if (!this.lenient) {
            throw syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
        }
    }

    private void consumeNonExecutePrefix() throws IOException {
        int i = 0;
        nextNonWhitespace(true);
        this.pos--;
        if (this.pos + NON_EXECUTE_PREFIX.length <= this.limit || fillBuffer(NON_EXECUTE_PREFIX.length)) {
            while (i < NON_EXECUTE_PREFIX.length) {
                if (this.buffer[this.pos + i] == NON_EXECUTE_PREFIX[i]) {
                    i++;
                } else {
                    return;
                }
            }
            this.pos += NON_EXECUTE_PREFIX.length;
        }
    }

    private boolean fillBuffer(int i) throws IOException {
        char[] cArr = this.buffer;
        this.lineStart -= this.pos;
        if (this.limit == this.pos) {
            this.limit = 0;
        } else {
            this.limit -= this.pos;
            System.arraycopy(cArr, this.pos, cArr, 0, this.limit);
        }
        this.pos = 0;
        do {
            int read = this.in.read(cArr, this.limit, cArr.length - this.limit);
            if (read == -1) {
                return false;
            }
            this.limit = read + this.limit;
            if (this.lineNumber == 0 && this.lineStart == 0 && this.limit > 0 && cArr[0] == 65279) {
                this.pos++;
                this.lineStart++;
                i++;
            }
        } while (this.limit < i);
        return true;
    }

    private boolean isLiteral(char c) throws IOException {
        switch (c) {
            case 9:
            case 10:
            case 12:
            case 13:
            case ' ':
            case ',':
            case ':':
            case '[':
            case ']':
            case Vr.VREvent.VrCore.ErrorCode.CONTROLLER_GATT_CHARACTERISTIC_NOT_FOUND:
            case Vr.VREvent.VrCore.ErrorCode.CONTROLLER_BATTERY_READ_FAILED:
                return false;
            case '#':
            case '/':
            case ';':
            case '=':
            case '\\':
                checkLenient();
                return false;
            default:
                return true;
        }
    }

    private int nextNonWhitespace(boolean z) throws IOException {
        char[] cArr = this.buffer;
        int i = this.pos;
        int i2 = this.limit;
        while (true) {
            if (i == i2) {
                this.pos = i;
                if (fillBuffer(1)) {
                    i = this.pos;
                    i2 = this.limit;
                } else if (!z) {
                    return -1;
                } else {
                    throw new EOFException("End of input" + locationString());
                }
            }
            int i3 = i + 1;
            char c = cArr[i];
            if (c == 10) {
                this.lineNumber++;
                this.lineStart = i3;
                i = i3;
            } else if (c == ' ' || c == 13) {
                i = i3;
            } else if (c == 9) {
                i = i3;
            } else if (c == '/') {
                this.pos = i3;
                if (i3 == i2) {
                    this.pos--;
                    boolean fillBuffer = fillBuffer(2);
                    this.pos++;
                    if (!fillBuffer) {
                        return c;
                    }
                }
                checkLenient();
                switch (cArr[this.pos]) {
                    case '*':
                        this.pos++;
                        if (skipTo("*/")) {
                            i = this.pos + 2;
                            i2 = this.limit;
                            break;
                        } else {
                            throw syntaxError("Unterminated comment");
                        }
                    case '/':
                        this.pos++;
                        skipToEndOfLine();
                        i = this.pos;
                        i2 = this.limit;
                        break;
                    default:
                        return c;
                }
            } else if (c != '#') {
                this.pos = i3;
                return c;
            } else {
                this.pos = i3;
                checkLenient();
                skipToEndOfLine();
                i = this.pos;
                i2 = this.limit;
            }
        }
    }

    private String nextQuotedValue(char c) throws IOException {
        StringBuilder sb;
        int i;
        int i2;
        int i3;
        StringBuilder sb2 = null;
        char[] cArr = this.buffer;
        do {
            int i4 = this.pos;
            int i5 = this.limit;
            int i6 = i4;
            while (i6 < i5) {
                int i7 = i6 + 1;
                char c2 = cArr[i6];
                if (c2 != c) {
                    if (c2 == '\\') {
                        this.pos = i7;
                        int i8 = (i7 - i4) - 1;
                        if (sb2 == null) {
                            sb2 = new StringBuilder(Math.max((i8 + 1) * 2, 16));
                        }
                        sb2.append(cArr, i4, i8);
                        sb2.append(readEscapeCharacter());
                        int i9 = this.pos;
                        sb = sb2;
                        i = i9;
                        int i10 = i9;
                        i2 = this.limit;
                        i3 = i10;
                    } else if (c2 != 10) {
                        int i11 = i4;
                        i2 = i5;
                        i3 = i7;
                        sb = sb2;
                        i = i11;
                    } else {
                        this.lineNumber++;
                        this.lineStart = i7;
                        int i12 = i4;
                        i2 = i5;
                        i3 = i7;
                        sb = sb2;
                        i = i12;
                    }
                    i6 = i3;
                    i5 = i2;
                    i4 = i;
                    sb2 = sb;
                } else {
                    this.pos = i7;
                    int i13 = (i7 - i4) - 1;
                    if (sb2 == null) {
                        return new String(cArr, i4, i13);
                    }
                    sb2.append(cArr, i4, i13);
                    return sb2.toString();
                }
            }
            if (sb2 == null) {
                sb2 = new StringBuilder(Math.max((i6 - i4) * 2, 16));
            }
            sb2.append(cArr, i4, i6 - i4);
            this.pos = i6;
        } while (fillBuffer(1));
        throw syntaxError("Unterminated string");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0037, code lost:
        r1 = r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String nextUnquotedValue() throws java.io.IOException {
        /*
            r5 = this;
            r0 = 0
            r1 = 0
            r2 = r0
            r0 = r1
        L_0x0004:
            int r3 = r5.pos
            int r3 = r3 + r0
            int r4 = r5.limit
            if (r3 < r4) goto L_0x0027
            char[] r3 = r5.buffer
            int r3 = r3.length
            if (r0 < r3) goto L_0x004c
            if (r2 == 0) goto L_0x0056
        L_0x0012:
            char[] r3 = r5.buffer
            int r4 = r5.pos
            r2.append(r3, r4, r0)
            int r3 = r5.pos
            int r0 = r0 + r3
            r5.pos = r0
            r0 = 1
            boolean r0 = r5.fillBuffer(r0)
            if (r0 == 0) goto L_0x0038
            r0 = r1
            goto L_0x0004
        L_0x0027:
            char[] r3 = r5.buffer
            int r4 = r5.pos
            int r4 = r4 + r0
            char r3 = r3[r4]
            switch(r3) {
                case 9: goto L_0x0037;
                case 10: goto L_0x0037;
                case 12: goto L_0x0037;
                case 13: goto L_0x0037;
                case 32: goto L_0x0037;
                case 35: goto L_0x0034;
                case 44: goto L_0x0037;
                case 47: goto L_0x0034;
                case 58: goto L_0x0037;
                case 59: goto L_0x0034;
                case 61: goto L_0x0034;
                case 91: goto L_0x0037;
                case 92: goto L_0x0034;
                case 93: goto L_0x0037;
                case 123: goto L_0x0037;
                case 125: goto L_0x0037;
                default: goto L_0x0031;
            }
        L_0x0031:
            int r0 = r0 + 1
            goto L_0x0004
        L_0x0034:
            r5.checkLenient()
        L_0x0037:
            r1 = r0
        L_0x0038:
            if (r2 == 0) goto L_0x0062
            char[] r0 = r5.buffer
            int r3 = r5.pos
            java.lang.StringBuilder r0 = r2.append(r0, r3, r1)
            java.lang.String r0 = r0.toString()
        L_0x0046:
            int r2 = r5.pos
            int r1 = r1 + r2
            r5.pos = r1
            return r0
        L_0x004c:
            int r3 = r0 + 1
            boolean r3 = r5.fillBuffer(r3)
            if (r3 != 0) goto L_0x0004
            r1 = r0
            goto L_0x0038
        L_0x0056:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r3 = 16
            int r3 = java.lang.Math.max(r0, r3)
            r2.<init>(r3)
            goto L_0x0012
        L_0x0062:
            java.lang.String r0 = new java.lang.String
            char[] r2 = r5.buffer
            int r3 = r5.pos
            r0.<init>(r2, r3, r1)
            goto L_0x0046
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.stream.JsonReader.nextUnquotedValue():java.lang.String");
    }

    private int peekKeyword() throws IOException {
        String str;
        String str2;
        int i;
        char c = this.buffer[this.pos];
        if (c == 't' || c == 'T') {
            str = "true";
            str2 = "TRUE";
            i = 5;
        } else if (c == 'f' || c == 'F') {
            str = "false";
            str2 = "FALSE";
            i = 6;
        } else if (c != 'n' && c != 'N') {
            return 0;
        } else {
            str = "null";
            str2 = "NULL";
            i = 7;
        }
        int length = str.length();
        for (int i2 = 1; i2 < length; i2++) {
            if (this.pos + i2 >= this.limit && !fillBuffer(i2 + 1)) {
                return 0;
            }
            char c2 = this.buffer[this.pos + i2];
            if (c2 != str.charAt(i2) && c2 != str2.charAt(i2)) {
                return 0;
            }
        }
        if ((this.pos + length < this.limit || fillBuffer(length + 1)) && isLiteral(this.buffer[this.pos + length])) {
            return 0;
        }
        this.pos += length;
        this.peeked = i;
        return i;
    }

    /* JADX WARNING: type inference failed for: r2v5 */
    /* JADX WARNING: type inference failed for: r2v8 */
    /* JADX WARNING: type inference failed for: r2v10 */
    /* JADX WARNING: type inference failed for: r2v11 */
    /* JADX WARNING: type inference failed for: r2v12 */
    /* JADX WARNING: type inference failed for: r2v14 */
    /* JADX WARNING: type inference failed for: r2v21 */
    /* JADX WARNING: type inference failed for: r2v31 */
    /* JADX WARNING: type inference failed for: r2v34 */
    /* JADX WARNING: type inference failed for: r2v35 */
    /* JADX WARNING: type inference failed for: r2v38 */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x00b4, code lost:
        if (r2 == false) goto L_0x00b6;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int peekNumber() throws java.io.IOException {
        /*
            r15 = this;
            char[] r11 = r15.buffer
            int r2 = r15.pos
            int r1 = r15.limit
            r6 = 0
            r5 = 0
            r4 = 1
            r3 = 0
            r0 = 0
            r10 = r0
            r0 = r1
            r1 = r2
        L_0x000f:
            int r2 = r1 + r10
            if (r2 == r0) goto L_0x0026
        L_0x0013:
            int r2 = r1 + r10
            char r2 = r11[r2]
            switch(r2) {
                case 43: goto L_0x004f;
                case 45: goto L_0x0038;
                case 46: goto L_0x0064;
                case 69: goto L_0x0058;
                case 101: goto L_0x0058;
                default: goto L_0x001a;
            }
        L_0x001a:
            r8 = 48
            if (r2 >= r8) goto L_0x006d
        L_0x001e:
            boolean r0 = r15.isLiteral(r2)
            if (r0 == 0) goto L_0x00d1
            r0 = 0
            return r0
        L_0x0026:
            int r0 = r11.length
            if (r10 == r0) goto L_0x0036
            int r0 = r10 + 1
            boolean r0 = r15.fillBuffer(r0)
            if (r0 == 0) goto L_0x00d1
            int r1 = r15.pos
            int r0 = r15.limit
            goto L_0x0013
        L_0x0036:
            r0 = 0
            return r0
        L_0x0038:
            if (r3 == 0) goto L_0x003f
            r2 = 5
            if (r3 == r2) goto L_0x004b
            r0 = 0
            return r0
        L_0x003f:
            r3 = 1
            r2 = 1
            r14 = r4
            r4 = r3
            r3 = r14
        L_0x0044:
            int r5 = r10 + 1
            r10 = r5
            r5 = r4
            r4 = r3
            r3 = r2
            goto L_0x000f
        L_0x004b:
            r2 = 6
            r3 = r4
            r4 = r5
            goto L_0x0044
        L_0x004f:
            r2 = 5
            if (r3 == r2) goto L_0x0054
            r0 = 0
            return r0
        L_0x0054:
            r2 = 6
            r3 = r4
            r4 = r5
            goto L_0x0044
        L_0x0058:
            r2 = 2
            if (r3 != r2) goto L_0x005f
        L_0x005b:
            r2 = 5
            r3 = r4
            r4 = r5
            goto L_0x0044
        L_0x005f:
            r2 = 4
            if (r3 == r2) goto L_0x005b
            r0 = 0
            return r0
        L_0x0064:
            r2 = 2
            if (r3 == r2) goto L_0x0069
            r0 = 0
            return r0
        L_0x0069:
            r2 = 3
            r3 = r4
            r4 = r5
            goto L_0x0044
        L_0x006d:
            r8 = 57
            if (r2 > r8) goto L_0x001e
            r8 = 1
            if (r3 != r8) goto L_0x007c
        L_0x0074:
            int r2 = r2 + -48
            int r2 = -r2
            long r6 = (long) r2
            r2 = 2
            r3 = r4
            r4 = r5
            goto L_0x0044
        L_0x007c:
            if (r3 == 0) goto L_0x0074
            r8 = 2
            if (r3 == r8) goto L_0x008b
            r2 = 3
            if (r3 == r2) goto L_0x00c4
            r2 = 5
            if (r3 != r2) goto L_0x00c9
        L_0x0087:
            r2 = 7
            r3 = r4
            r4 = r5
            goto L_0x0044
        L_0x008b:
            r8 = 0
            int r8 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r8 != 0) goto L_0x0093
            r0 = 0
            return r0
        L_0x0093:
            r8 = 10
            long r8 = r8 * r6
            int r2 = r2 + -48
            long r12 = (long) r2
            long r8 = r8 - r12
            r12 = -922337203685477580(0xf333333333333334, double:-8.390303882365713E246)
            int r2 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
            if (r2 <= 0) goto L_0x00be
            r2 = 1
        L_0x00a4:
            if (r2 != 0) goto L_0x00b6
            r12 = -922337203685477580(0xf333333333333334, double:-8.390303882365713E246)
            int r2 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
            if (r2 != 0) goto L_0x00c2
            int r2 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r2 < 0) goto L_0x00c0
            r2 = 1
        L_0x00b4:
            if (r2 != 0) goto L_0x00c2
        L_0x00b6:
            r2 = 1
        L_0x00b7:
            r2 = r2 & r4
            r4 = r5
            r6 = r8
            r14 = r3
            r3 = r2
            r2 = r14
            goto L_0x0044
        L_0x00be:
            r2 = 0
            goto L_0x00a4
        L_0x00c0:
            r2 = 0
            goto L_0x00b4
        L_0x00c2:
            r2 = 0
            goto L_0x00b7
        L_0x00c4:
            r2 = 4
            r3 = r4
            r4 = r5
            goto L_0x0044
        L_0x00c9:
            r2 = 6
            if (r3 == r2) goto L_0x0087
            r2 = r3
            r3 = r4
            r4 = r5
            goto L_0x0044
        L_0x00d1:
            r0 = 2
            if (r3 == r0) goto L_0x00e0
        L_0x00d4:
            r0 = 2
            if (r3 != r0) goto L_0x0103
        L_0x00d7:
            r15.peekedNumberLength = r10
            r0 = 16
            r15.peeked = r0
            r0 = 16
            return r0
        L_0x00e0:
            if (r4 == 0) goto L_0x00d4
            r0 = -9223372036854775808
            int r0 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r0 != 0) goto L_0x00ea
            if (r5 == 0) goto L_0x00d4
        L_0x00ea:
            r0 = 0
            int r0 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r0 != 0) goto L_0x00f2
            if (r5 != 0) goto L_0x00d4
        L_0x00f2:
            if (r5 != 0) goto L_0x00f5
            long r6 = -r6
        L_0x00f5:
            r15.peekedLong = r6
            int r0 = r15.pos
            int r0 = r0 + r10
            r15.pos = r0
            r0 = 15
            r15.peeked = r0
            r0 = 15
            return r0
        L_0x0103:
            r0 = 4
            if (r3 == r0) goto L_0x00d7
            r0 = 7
            if (r3 == r0) goto L_0x00d7
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.stream.JsonReader.peekNumber():int");
    }

    private void push(int i) {
        if (this.stackSize == this.stack.length) {
            int[] iArr = new int[(this.stackSize * 2)];
            int[] iArr2 = new int[(this.stackSize * 2)];
            String[] strArr = new String[(this.stackSize * 2)];
            System.arraycopy(this.stack, 0, iArr, 0, this.stackSize);
            System.arraycopy(this.pathIndices, 0, iArr2, 0, this.stackSize);
            System.arraycopy(this.pathNames, 0, strArr, 0, this.stackSize);
            this.stack = iArr;
            this.pathIndices = iArr2;
            this.pathNames = strArr;
        }
        int[] iArr3 = this.stack;
        int i2 = this.stackSize;
        this.stackSize = i2 + 1;
        iArr3[i2] = i;
    }

    private char readEscapeCharacter() throws IOException {
        int i;
        if (this.pos == this.limit && !fillBuffer(1)) {
            throw syntaxError("Unterminated escape sequence");
        }
        char[] cArr = this.buffer;
        int i2 = this.pos;
        this.pos = i2 + 1;
        char c = cArr[i2];
        switch (c) {
            case 10:
                this.lineNumber++;
                this.lineStart = this.pos;
                break;
            case '\"':
            case '\'':
            case '/':
            case '\\':
                break;
            case 'b':
                return 8;
            case 'f':
                return 12;
            case 'n':
                return 10;
            case 'r':
                return 13;
            case 't':
                return 9;
            case 'u':
                if (this.pos + 4 > this.limit && !fillBuffer(4)) {
                    throw syntaxError("Unterminated escape sequence");
                }
                int i3 = this.pos;
                int i4 = i3 + 4;
                int i5 = i3;
                char c2 = 0;
                for (int i6 = i5; i6 < i4; i6++) {
                    char c3 = this.buffer[i6];
                    char c4 = (char) (c2 << 4);
                    if (c3 >= '0' && c3 <= '9') {
                        i = c3 - '0';
                    } else if (c3 >= 'a' && c3 <= 'f') {
                        i = (c3 - 'a') + 10;
                    } else if (c3 >= 'A' && c3 <= 'F') {
                        i = (c3 - 'A') + 10;
                    } else {
                        throw new NumberFormatException("\\u" + new String(this.buffer, this.pos, 4));
                    }
                    c2 = (char) (c4 + i);
                }
                this.pos += 4;
                return c2;
            default:
                throw syntaxError("Invalid escape sequence");
        }
        return c;
    }

    private void skipQuotedValue(char c) throws IOException {
        char[] cArr = this.buffer;
        do {
            int i = this.pos;
            int i2 = this.limit;
            int i3 = i;
            while (i3 < i2) {
                int i4 = i3 + 1;
                char c2 = cArr[i3];
                if (c2 != c) {
                    if (c2 == '\\') {
                        this.pos = i4;
                        readEscapeCharacter();
                        i4 = this.pos;
                        i2 = this.limit;
                    } else if (c2 == 10) {
                        this.lineNumber++;
                        this.lineStart = i4;
                    }
                    i3 = i4;
                } else {
                    this.pos = i4;
                    return;
                }
            }
            this.pos = i3;
        } while (fillBuffer(1));
        throw syntaxError("Unterminated string");
    }

    private boolean skipTo(String str) throws IOException {
        int length = str.length();
        while (true) {
            if (this.pos + length > this.limit && !fillBuffer(length)) {
                return false;
            }
            if (this.buffer[this.pos] != 10) {
                int i = 0;
                while (i < length) {
                    if (this.buffer[this.pos + i] == str.charAt(i)) {
                        i++;
                    }
                }
                return true;
            }
            this.lineNumber++;
            this.lineStart = this.pos + 1;
            this.pos++;
        }
    }

    private void skipToEndOfLine() throws IOException {
        char c;
        do {
            if (this.pos < this.limit || fillBuffer(1)) {
                char[] cArr = this.buffer;
                int i = this.pos;
                this.pos = i + 1;
                c = cArr[i];
                if (c == 10) {
                    this.lineNumber++;
                    this.lineStart = this.pos;
                    return;
                }
            } else {
                return;
            }
        } while (c != 13);
    }

    private void skipUnquotedValue() throws IOException {
        do {
            int i = 0;
            while (this.pos + i < this.limit) {
                switch (this.buffer[this.pos + i]) {
                    case 9:
                    case 10:
                    case 12:
                    case 13:
                    case ' ':
                    case ',':
                    case ':':
                    case '[':
                    case ']':
                    case Vr.VREvent.VrCore.ErrorCode.CONTROLLER_GATT_CHARACTERISTIC_NOT_FOUND:
                    case Vr.VREvent.VrCore.ErrorCode.CONTROLLER_BATTERY_READ_FAILED:
                        break;
                    case '#':
                    case '/':
                    case ';':
                    case '=':
                    case '\\':
                        checkLenient();
                        break;
                    default:
                        i++;
                }
                this.pos = i + this.pos;
                return;
            }
            this.pos = i + this.pos;
        } while (fillBuffer(1));
    }

    private IOException syntaxError(String str) throws IOException {
        throw new MalformedJsonException(str + locationString());
    }

    public void beginArray() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i != 3) {
            throw new IllegalStateException("Expected BEGIN_ARRAY but was " + peek() + locationString());
        }
        push(1);
        this.pathIndices[this.stackSize - 1] = 0;
        this.peeked = 0;
    }

    public void beginObject() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i != 1) {
            throw new IllegalStateException("Expected BEGIN_OBJECT but was " + peek() + locationString());
        }
        push(3);
        this.peeked = 0;
    }

    public void close() throws IOException {
        this.peeked = 0;
        this.stack[0] = 8;
        this.stackSize = 1;
        this.in.close();
    }

    /* access modifiers changed from: package-private */
    public int doPeek() throws IOException {
        int i = this.stack[this.stackSize - 1];
        if (i == 1) {
            this.stack[this.stackSize - 1] = 2;
        } else if (i == 2) {
            switch (nextNonWhitespace(true)) {
                case 44:
                    break;
                case 59:
                    checkLenient();
                    break;
                case 93:
                    this.peeked = 4;
                    return 4;
                default:
                    throw syntaxError("Unterminated array");
            }
        } else if (i == 3 || i == 5) {
            this.stack[this.stackSize - 1] = 4;
            if (i == 5) {
                switch (nextNonWhitespace(true)) {
                    case 44:
                        break;
                    case 59:
                        checkLenient();
                        break;
                    case Vr.VREvent.VrCore.ErrorCode.CONTROLLER_BATTERY_READ_FAILED:
                        this.peeked = 2;
                        return 2;
                    default:
                        throw syntaxError("Unterminated object");
                }
            }
            int nextNonWhitespace = nextNonWhitespace(true);
            switch (nextNonWhitespace) {
                case 34:
                    this.peeked = 13;
                    return 13;
                case 39:
                    checkLenient();
                    this.peeked = 12;
                    return 12;
                case Vr.VREvent.VrCore.ErrorCode.CONTROLLER_BATTERY_READ_FAILED:
                    if (i == 5) {
                        throw syntaxError("Expected name");
                    }
                    this.peeked = 2;
                    return 2;
                default:
                    checkLenient();
                    this.pos--;
                    if (!isLiteral((char) nextNonWhitespace)) {
                        throw syntaxError("Expected name");
                    }
                    this.peeked = 14;
                    return 14;
            }
        } else if (i == 4) {
            this.stack[this.stackSize - 1] = 5;
            switch (nextNonWhitespace(true)) {
                case 58:
                    break;
                case 61:
                    checkLenient();
                    if ((this.pos < this.limit || fillBuffer(1)) && this.buffer[this.pos] == '>') {
                        this.pos++;
                        break;
                    }
                default:
                    throw syntaxError("Expected ':'");
            }
        } else if (i == 6) {
            if (this.lenient) {
                consumeNonExecutePrefix();
            }
            this.stack[this.stackSize - 1] = 7;
        } else if (i != 7) {
            if (i == 8) {
                throw new IllegalStateException("JsonReader is closed");
            }
        } else if (nextNonWhitespace(false) != -1) {
            checkLenient();
            this.pos--;
        } else {
            this.peeked = 17;
            return 17;
        }
        switch (nextNonWhitespace(true)) {
            case 34:
                this.peeked = 9;
                return 9;
            case 39:
                checkLenient();
                this.peeked = 8;
                return 8;
            case 44:
            case 59:
                break;
            case 91:
                this.peeked = 3;
                return 3;
            case 93:
                if (i == 1) {
                    this.peeked = 4;
                    return 4;
                }
                break;
            case Vr.VREvent.VrCore.ErrorCode.CONTROLLER_GATT_CHARACTERISTIC_NOT_FOUND:
                this.peeked = 1;
                return 1;
            default:
                this.pos--;
                int peekKeyword = peekKeyword();
                if (peekKeyword != 0) {
                    return peekKeyword;
                }
                int peekNumber = peekNumber();
                if (peekNumber != 0) {
                    return peekNumber;
                }
                if (isLiteral(this.buffer[this.pos])) {
                    checkLenient();
                    this.peeked = 10;
                    return 10;
                }
                throw syntaxError("Expected value");
        }
        if (i == 1 || i == 2) {
            checkLenient();
            this.pos--;
            this.peeked = 7;
            return 7;
        }
        throw syntaxError("Unexpected value");
    }

    public void endArray() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i != 4) {
            throw new IllegalStateException("Expected END_ARRAY but was " + peek() + locationString());
        }
        this.stackSize--;
        int[] iArr = this.pathIndices;
        int i2 = this.stackSize - 1;
        iArr[i2] = iArr[i2] + 1;
        this.peeked = 0;
    }

    public void endObject() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i != 2) {
            throw new IllegalStateException("Expected END_OBJECT but was " + peek() + locationString());
        }
        this.stackSize--;
        this.pathNames[this.stackSize] = null;
        int[] iArr = this.pathIndices;
        int i2 = this.stackSize - 1;
        iArr[i2] = iArr[i2] + 1;
        this.peeked = 0;
    }

    public String getPath() {
        StringBuilder append = new StringBuilder().append('$');
        int i = this.stackSize;
        for (int i2 = 0; i2 < i; i2++) {
            switch (this.stack[i2]) {
                case 1:
                case 2:
                    append.append('[').append(this.pathIndices[i2]).append(']');
                    break;
                case 3:
                case 4:
                case 5:
                    append.append('.');
                    if (this.pathNames[i2] == null) {
                        break;
                    } else {
                        append.append(this.pathNames[i2]);
                        break;
                    }
            }
        }
        return append.toString();
    }

    public boolean hasNext() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        return (i == 2 || i == 4) ? false : true;
    }

    public final boolean isLenient() {
        return this.lenient;
    }

    /* access modifiers changed from: package-private */
    public String locationString() {
        return " at line " + (this.lineNumber + 1) + " column " + ((this.pos - this.lineStart) + 1) + " path " + getPath();
    }

    public boolean nextBoolean() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 5) {
            this.peeked = 0;
            int[] iArr = this.pathIndices;
            int i2 = this.stackSize - 1;
            iArr[i2] = iArr[i2] + 1;
            return true;
        } else if (i != 6) {
            throw new IllegalStateException("Expected a boolean but was " + peek() + locationString());
        } else {
            this.peeked = 0;
            int[] iArr2 = this.pathIndices;
            int i3 = this.stackSize - 1;
            iArr2[i3] = iArr2[i3] + 1;
            return false;
        }
    }

    public double nextDouble() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i != 15) {
            if (i == 16) {
                this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
                this.pos += this.peekedNumberLength;
            } else if (i == 8 || i == 9) {
                this.peekedString = nextQuotedValue(i != 8 ? '\"' : '\'');
            } else if (i == 10) {
                this.peekedString = nextUnquotedValue();
            } else if (i != 11) {
                throw new IllegalStateException("Expected a double but was " + peek() + locationString());
            }
            this.peeked = 11;
            double parseDouble = Double.parseDouble(this.peekedString);
            if (!this.lenient && (Double.isNaN(parseDouble) || Double.isInfinite(parseDouble))) {
                throw new MalformedJsonException("JSON forbids NaN and infinities: " + parseDouble + locationString());
            }
            this.peekedString = null;
            this.peeked = 0;
            int[] iArr = this.pathIndices;
            int i2 = this.stackSize - 1;
            iArr[i2] = iArr[i2] + 1;
            return parseDouble;
        }
        this.peeked = 0;
        int[] iArr2 = this.pathIndices;
        int i3 = this.stackSize - 1;
        iArr2[i3] = iArr2[i3] + 1;
        return (double) this.peekedLong;
    }

    public int nextInt() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 15) {
            int i2 = (int) this.peekedLong;
            if (this.peekedLong != ((long) i2)) {
                throw new NumberFormatException("Expected an int but was " + this.peekedLong + locationString());
            }
            this.peeked = 0;
            int[] iArr = this.pathIndices;
            int i3 = this.stackSize - 1;
            iArr[i3] = iArr[i3] + 1;
            return i2;
        } else if (i == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
            this.peeked = 11;
            double parseDouble = Double.parseDouble(this.peekedString);
            int i4 = (int) parseDouble;
            if (((double) i4) != parseDouble) {
                throw new NumberFormatException("Expected an int but was " + this.peekedString + locationString());
            }
            this.peekedString = null;
            this.peeked = 0;
            int[] iArr2 = this.pathIndices;
            int i5 = this.stackSize - 1;
            iArr2[i5] = iArr2[i5] + 1;
            return i4;
        } else if (i == 8 || i == 9 || i == 10) {
            if (i != 10) {
                this.peekedString = nextQuotedValue(i != 8 ? '\"' : '\'');
            } else {
                this.peekedString = nextUnquotedValue();
            }
            try {
                int parseInt = Integer.parseInt(this.peekedString);
                this.peeked = 0;
                int[] iArr3 = this.pathIndices;
                int i6 = this.stackSize - 1;
                iArr3[i6] = iArr3[i6] + 1;
                return parseInt;
            } catch (NumberFormatException e) {
            }
        } else {
            throw new IllegalStateException("Expected an int but was " + peek() + locationString());
        }
    }

    public long nextLong() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 15) {
            this.peeked = 0;
            int[] iArr = this.pathIndices;
            int i2 = this.stackSize - 1;
            iArr[i2] = iArr[i2] + 1;
            return this.peekedLong;
        } else if (i == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
            this.peeked = 11;
            double parseDouble = Double.parseDouble(this.peekedString);
            long j = (long) parseDouble;
            if (((double) j) != parseDouble) {
                throw new NumberFormatException("Expected a long but was " + this.peekedString + locationString());
            }
            this.peekedString = null;
            this.peeked = 0;
            int[] iArr2 = this.pathIndices;
            int i3 = this.stackSize - 1;
            iArr2[i3] = iArr2[i3] + 1;
            return j;
        } else if (i == 8 || i == 9 || i == 10) {
            if (i != 10) {
                this.peekedString = nextQuotedValue(i != 8 ? '\"' : '\'');
            } else {
                this.peekedString = nextUnquotedValue();
            }
            try {
                long parseLong = Long.parseLong(this.peekedString);
                this.peeked = 0;
                int[] iArr3 = this.pathIndices;
                int i4 = this.stackSize - 1;
                iArr3[i4] = iArr3[i4] + 1;
                return parseLong;
            } catch (NumberFormatException e) {
            }
        } else {
            throw new IllegalStateException("Expected a long but was " + peek() + locationString());
        }
    }

    public String nextName() throws IOException {
        String str;
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 14) {
            str = nextUnquotedValue();
        } else if (i == 12) {
            str = nextQuotedValue('\'');
        } else if (i != 13) {
            throw new IllegalStateException("Expected a name but was " + peek() + locationString());
        } else {
            str = nextQuotedValue('\"');
        }
        this.peeked = 0;
        this.pathNames[this.stackSize - 1] = str;
        return str;
    }

    public void nextNull() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i != 7) {
            throw new IllegalStateException("Expected null but was " + peek() + locationString());
        }
        this.peeked = 0;
        int[] iArr = this.pathIndices;
        int i2 = this.stackSize - 1;
        iArr[i2] = iArr[i2] + 1;
    }

    public String nextString() throws IOException {
        String str;
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 10) {
            str = nextUnquotedValue();
        } else if (i == 8) {
            str = nextQuotedValue('\'');
        } else if (i == 9) {
            str = nextQuotedValue('\"');
        } else if (i == 11) {
            str = this.peekedString;
            this.peekedString = null;
        } else if (i == 15) {
            str = Long.toString(this.peekedLong);
        } else if (i != 16) {
            throw new IllegalStateException("Expected a string but was " + peek() + locationString());
        } else {
            str = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        }
        this.peeked = 0;
        int[] iArr = this.pathIndices;
        int i2 = this.stackSize - 1;
        iArr[i2] = iArr[i2] + 1;
        return str;
    }

    public JsonToken peek() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        switch (i) {
            case 1:
                return JsonToken.BEGIN_OBJECT;
            case 2:
                return JsonToken.END_OBJECT;
            case 3:
                return JsonToken.BEGIN_ARRAY;
            case 4:
                return JsonToken.END_ARRAY;
            case 5:
            case 6:
                return JsonToken.BOOLEAN;
            case 7:
                return JsonToken.NULL;
            case 8:
            case 9:
            case 10:
            case 11:
                return JsonToken.STRING;
            case 12:
            case 13:
            case 14:
                return JsonToken.NAME;
            case 15:
            case 16:
                return JsonToken.NUMBER;
            case 17:
                return JsonToken.END_DOCUMENT;
            default:
                throw new AssertionError();
        }
    }

    public final void setLenient(boolean z) {
        this.lenient = z;
    }

    public void skipValue() throws IOException {
        int i = 0;
        do {
            int i2 = this.peeked;
            if (i2 == 0) {
                i2 = doPeek();
            }
            if (i2 == 3) {
                push(1);
                i++;
            } else if (i2 == 1) {
                push(3);
                i++;
            } else if (i2 == 4) {
                this.stackSize--;
                i--;
            } else if (i2 == 2) {
                this.stackSize--;
                i--;
            } else if (i2 == 14 || i2 == 10) {
                skipUnquotedValue();
            } else if (i2 == 8 || i2 == 12) {
                skipQuotedValue('\'');
            } else if (i2 == 9 || i2 == 13) {
                skipQuotedValue('\"');
            } else if (i2 == 16) {
                this.pos += this.peekedNumberLength;
            }
            this.peeked = 0;
        } while (i != 0);
        int[] iArr = this.pathIndices;
        int i3 = this.stackSize - 1;
        iArr[i3] = iArr[i3] + 1;
        this.pathNames[this.stackSize - 1] = "null";
    }

    public String toString() {
        return getClass().getSimpleName() + locationString();
    }
}
