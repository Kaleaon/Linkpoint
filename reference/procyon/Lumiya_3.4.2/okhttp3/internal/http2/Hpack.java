// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.http2;

import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Buffer;
import java.util.Collection;
import java.util.Arrays;
import okio.Okio;
import java.util.ArrayList;
import okio.Source;
import okio.BufferedSource;
import java.util.List;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.io.IOException;
import okio.ByteString;
import java.util.Map;

final class Hpack
{
    static final Map<ByteString, Integer> NAME_TO_FIRST_INDEX;
    private static final int PREFIX_4_BITS = 15;
    private static final int PREFIX_5_BITS = 31;
    private static final int PREFIX_6_BITS = 63;
    private static final int PREFIX_7_BITS = 127;
    static final Header[] STATIC_HEADER_TABLE;
    
    static {
        STATIC_HEADER_TABLE = new Header[] { new Header(Header.TARGET_AUTHORITY, ""), new Header(Header.TARGET_METHOD, "GET"), new Header(Header.TARGET_METHOD, "POST"), new Header(Header.TARGET_PATH, "/"), new Header(Header.TARGET_PATH, "/index.html"), new Header(Header.TARGET_SCHEME, "http"), new Header(Header.TARGET_SCHEME, "https"), new Header(Header.RESPONSE_STATUS, "200"), new Header(Header.RESPONSE_STATUS, "204"), new Header(Header.RESPONSE_STATUS, "206"), new Header(Header.RESPONSE_STATUS, "304"), new Header(Header.RESPONSE_STATUS, "400"), new Header(Header.RESPONSE_STATUS, "404"), new Header(Header.RESPONSE_STATUS, "500"), new Header("accept-charset", ""), new Header("accept-encoding", "gzip, deflate"), new Header("accept-language", ""), new Header("accept-ranges", ""), new Header("accept", ""), new Header("access-control-allow-origin", ""), new Header("age", ""), new Header("allow", ""), new Header("authorization", ""), new Header("cache-control", ""), new Header("content-disposition", ""), new Header("content-encoding", ""), new Header("content-language", ""), new Header("content-length", ""), new Header("content-location", ""), new Header("content-range", ""), new Header("content-type", ""), new Header("cookie", ""), new Header("date", ""), new Header("etag", ""), new Header("expect", ""), new Header("expires", ""), new Header("from", ""), new Header("host", ""), new Header("if-match", ""), new Header("if-modified-since", ""), new Header("if-none-match", ""), new Header("if-range", ""), new Header("if-unmodified-since", ""), new Header("last-modified", ""), new Header("link", ""), new Header("location", ""), new Header("max-forwards", ""), new Header("proxy-authenticate", ""), new Header("proxy-authorization", ""), new Header("range", ""), new Header("referer", ""), new Header("refresh", ""), new Header("retry-after", ""), new Header("server", ""), new Header("set-cookie", ""), new Header("strict-transport-security", ""), new Header("transfer-encoding", ""), new Header("user-agent", ""), new Header("vary", ""), new Header("via", ""), new Header("www-authenticate", "") };
        NAME_TO_FIRST_INDEX = nameToFirstIndex();
    }
    
    private Hpack() {
    }
    
    static ByteString checkLowercase(final ByteString byteString) throws IOException {
        for (int i = 0; i < byteString.size(); ++i) {
            final byte byte1 = byteString.getByte(i);
            if (byte1 >= 65 && byte1 <= 90) {
                throw new IOException("PROTOCOL_ERROR response malformed: mixed case name: " + byteString.utf8());
            }
        }
        return byteString;
    }
    
    private static Map<ByteString, Integer> nameToFirstIndex() {
        int i = 0;
        final LinkedHashMap m = new LinkedHashMap(Hpack.STATIC_HEADER_TABLE.length);
        while (i < Hpack.STATIC_HEADER_TABLE.length) {
            if (!m.containsKey(Hpack.STATIC_HEADER_TABLE[i].name)) {
                m.put(Hpack.STATIC_HEADER_TABLE[i].name, i);
            }
            ++i;
        }
        return (Map<ByteString, Integer>)Collections.unmodifiableMap((Map<?, ?>)m);
    }
    
    static final class Reader
    {
        Header[] dynamicTable;
        int dynamicTableByteCount;
        int headerCount;
        private final List<Header> headerList;
        private final int headerTableSizeSetting;
        private int maxDynamicTableByteCount;
        int nextHeaderIndex;
        private final BufferedSource source;
        
        Reader(final int headerTableSizeSetting, final int maxDynamicTableByteCount, final Source source) {
            this.headerList = new ArrayList<Header>();
            this.dynamicTable = new Header[8];
            this.nextHeaderIndex = this.dynamicTable.length - 1;
            this.headerCount = 0;
            this.dynamicTableByteCount = 0;
            this.headerTableSizeSetting = headerTableSizeSetting;
            this.maxDynamicTableByteCount = maxDynamicTableByteCount;
            this.source = Okio.buffer(source);
        }
        
        Reader(final int n, final Source source) {
            this(n, n, source);
        }
        
        private void adjustDynamicTableByteCount() {
            if (this.maxDynamicTableByteCount < this.dynamicTableByteCount) {
                if (this.maxDynamicTableByteCount != 0) {
                    this.evictToRecoverBytes(this.dynamicTableByteCount - this.maxDynamicTableByteCount);
                }
                else {
                    this.clearDynamicTable();
                }
            }
        }
        
        private void clearDynamicTable() {
            Arrays.fill(this.dynamicTable, null);
            this.nextHeaderIndex = this.dynamicTable.length - 1;
            this.headerCount = 0;
            this.dynamicTableByteCount = 0;
        }
        
        private int dynamicTableIndex(final int n) {
            return this.nextHeaderIndex + 1 + n;
        }
        
        private int evictToRecoverBytes(int n) {
            final int n2 = 0;
            final int n3 = 0;
            if (n <= 0) {
                n = n3;
            }
            else {
                int length;
                int n4;
                for (length = this.dynamicTable.length, n4 = n, n = n2; --length >= this.nextHeaderIndex && n4 > 0; n4 -= this.dynamicTable[length].hpackSize, this.dynamicTableByteCount -= this.dynamicTable[length].hpackSize, --this.headerCount, ++n) {}
                System.arraycopy(this.dynamicTable, this.nextHeaderIndex + 1, this.dynamicTable, this.nextHeaderIndex + 1 + n, this.headerCount);
                this.nextHeaderIndex += n;
            }
            return n;
        }
        
        private ByteString getName(final int n) {
            if (!this.isStaticHeader(n)) {
                return this.dynamicTable[this.dynamicTableIndex(n - Hpack.STATIC_HEADER_TABLE.length)].name;
            }
            return Hpack.STATIC_HEADER_TABLE[n].name;
        }
        
        private void insertIntoDynamicTable(int n, final Header header) {
            this.headerList.add(header);
            int hpackSize = header.hpackSize;
            if (n != -1) {
                hpackSize -= this.dynamicTable[this.dynamicTableIndex(n)].hpackSize;
            }
            if (hpackSize <= this.maxDynamicTableByteCount) {
                final int evictToRecoverBytes = this.evictToRecoverBytes(this.dynamicTableByteCount + hpackSize - this.maxDynamicTableByteCount);
                if (n != -1) {
                    this.dynamicTable[evictToRecoverBytes + this.dynamicTableIndex(n) + n] = header;
                }
                else {
                    if (this.headerCount + 1 > this.dynamicTable.length) {
                        final Header[] dynamicTable = new Header[this.dynamicTable.length * 2];
                        System.arraycopy(this.dynamicTable, 0, dynamicTable, this.dynamicTable.length, this.dynamicTable.length);
                        this.nextHeaderIndex = this.dynamicTable.length - 1;
                        this.dynamicTable = dynamicTable;
                    }
                    n = this.nextHeaderIndex--;
                    this.dynamicTable[n] = header;
                    ++this.headerCount;
                }
                this.dynamicTableByteCount += hpackSize;
                return;
            }
            this.clearDynamicTable();
        }
        
        private boolean isStaticHeader(final int n) {
            boolean b = false;
            if (n >= 0 && n <= Hpack.STATIC_HEADER_TABLE.length - 1) {
                b = true;
            }
            return b;
        }
        
        private int readByte() throws IOException {
            return this.source.readByte() & 0xFF;
        }
        
        private void readIndexedHeader(final int n) throws IOException {
            if (!this.isStaticHeader(n)) {
                final int dynamicTableIndex = this.dynamicTableIndex(n - Hpack.STATIC_HEADER_TABLE.length);
                if (dynamicTableIndex < 0 || dynamicTableIndex > this.dynamicTable.length - 1) {
                    throw new IOException("Header index too large " + (n + 1));
                }
                this.headerList.add(this.dynamicTable[dynamicTableIndex]);
            }
            else {
                this.headerList.add(Hpack.STATIC_HEADER_TABLE[n]);
            }
        }
        
        private void readLiteralHeaderWithIncrementalIndexingIndexedName(final int n) throws IOException {
            this.insertIntoDynamicTable(-1, new Header(this.getName(n), this.readByteString()));
        }
        
        private void readLiteralHeaderWithIncrementalIndexingNewName() throws IOException {
            this.insertIntoDynamicTable(-1, new Header(Hpack.checkLowercase(this.readByteString()), this.readByteString()));
        }
        
        private void readLiteralHeaderWithoutIndexingIndexedName(final int n) throws IOException {
            this.headerList.add(new Header(this.getName(n), this.readByteString()));
        }
        
        private void readLiteralHeaderWithoutIndexingNewName() throws IOException {
            this.headerList.add(new Header(Hpack.checkLowercase(this.readByteString()), this.readByteString()));
        }
        
        public List<Header> getAndResetHeaderList() {
            final ArrayList list = new ArrayList((Collection<? extends E>)this.headerList);
            this.headerList.clear();
            return list;
        }
        
        int maxDynamicTableByteCount() {
            return this.maxDynamicTableByteCount;
        }
        
        ByteString readByteString() throws IOException {
            int n = 0;
            final int byte1 = this.readByte();
            if ((byte1 & 0x80) == 0x80) {
                n = 1;
            }
            final int int1 = this.readInt(byte1, 127);
            if (n == 0) {
                return this.source.readByteString(int1);
            }
            return ByteString.of(Huffman.get().decode(this.source.readByteArray(int1)));
        }
        
        void readHeaders() throws IOException {
            while (!this.source.exhausted()) {
                final int n = this.source.readByte() & 0xFF;
                if (n == 128) {
                    throw new IOException("index == 0");
                }
                if ((n & 0x80) != 0x80) {
                    if (n != 64) {
                        if ((n & 0x40) != 0x40) {
                            if ((n & 0x20) != 0x20) {
                                if (n != 16 && n != 0) {
                                    this.readLiteralHeaderWithoutIndexingIndexedName(this.readInt(n, 15) - 1);
                                }
                                else {
                                    this.readLiteralHeaderWithoutIndexingNewName();
                                }
                            }
                            else {
                                this.maxDynamicTableByteCount = this.readInt(n, 31);
                                if (this.maxDynamicTableByteCount < 0 || this.maxDynamicTableByteCount > this.headerTableSizeSetting) {
                                    throw new IOException("Invalid dynamic table size update " + this.maxDynamicTableByteCount);
                                }
                                this.adjustDynamicTableByteCount();
                            }
                        }
                        else {
                            this.readLiteralHeaderWithIncrementalIndexingIndexedName(this.readInt(n, 63) - 1);
                        }
                    }
                    else {
                        this.readLiteralHeaderWithIncrementalIndexingNewName();
                    }
                }
                else {
                    this.readIndexedHeader(this.readInt(n, 127) - 1);
                }
            }
        }
        
        int readInt(int n, int n2) throws IOException {
            final int n3 = 0;
            n &= n2;
            if (n >= n2) {
                n = n3;
                int byte1;
                while (true) {
                    byte1 = this.readByte();
                    if ((byte1 & 0x80) == 0x0) {
                        break;
                    }
                    n2 += (byte1 & 0x7F) << n;
                    n += 7;
                }
                return (byte1 << n) + n2;
            }
            return n;
        }
    }
    
    static final class Writer
    {
        private static final int SETTINGS_HEADER_TABLE_SIZE = 4096;
        private static final int SETTINGS_HEADER_TABLE_SIZE_LIMIT = 16384;
        Header[] dynamicTable;
        int dynamicTableByteCount;
        private boolean emitDynamicTableSizeUpdate;
        int headerCount;
        int headerTableSizeSetting;
        int maxDynamicTableByteCount;
        int nextHeaderIndex;
        private final Buffer out;
        private int smallestHeaderTableSizeSetting;
        private final boolean useCompression;
        
        Writer(final int n, final boolean useCompression, final Buffer out) {
            this.smallestHeaderTableSizeSetting = Integer.MAX_VALUE;
            this.dynamicTable = new Header[8];
            this.nextHeaderIndex = this.dynamicTable.length - 1;
            this.headerCount = 0;
            this.dynamicTableByteCount = 0;
            this.headerTableSizeSetting = n;
            this.maxDynamicTableByteCount = n;
            this.useCompression = useCompression;
            this.out = out;
        }
        
        Writer(final Buffer buffer) {
            this(4096, true, buffer);
        }
        
        private void adjustDynamicTableByteCount() {
            if (this.maxDynamicTableByteCount < this.dynamicTableByteCount) {
                if (this.maxDynamicTableByteCount != 0) {
                    this.evictToRecoverBytes(this.dynamicTableByteCount - this.maxDynamicTableByteCount);
                }
                else {
                    this.clearDynamicTable();
                }
            }
        }
        
        private void clearDynamicTable() {
            Arrays.fill(this.dynamicTable, null);
            this.nextHeaderIndex = this.dynamicTable.length - 1;
            this.headerCount = 0;
            this.dynamicTableByteCount = 0;
        }
        
        private int evictToRecoverBytes(int n) {
            final int n2 = 0;
            final int n3 = 0;
            if (n <= 0) {
                n = n3;
            }
            else {
                int length;
                int n4;
                for (length = this.dynamicTable.length, n4 = n, n = n2; --length >= this.nextHeaderIndex && n4 > 0; n4 -= this.dynamicTable[length].hpackSize, this.dynamicTableByteCount -= this.dynamicTable[length].hpackSize, --this.headerCount, ++n) {}
                System.arraycopy(this.dynamicTable, this.nextHeaderIndex + 1, this.dynamicTable, this.nextHeaderIndex + 1 + n, this.headerCount);
                Arrays.fill(this.dynamicTable, this.nextHeaderIndex + 1, this.nextHeaderIndex + 1 + n, null);
                this.nextHeaderIndex += n;
            }
            return n;
        }
        
        private void insertIntoDynamicTable(final Header header) {
            final int hpackSize = header.hpackSize;
            if (hpackSize <= this.maxDynamicTableByteCount) {
                this.evictToRecoverBytes(this.dynamicTableByteCount + hpackSize - this.maxDynamicTableByteCount);
                if (this.headerCount + 1 > this.dynamicTable.length) {
                    final Header[] dynamicTable = new Header[this.dynamicTable.length * 2];
                    System.arraycopy(this.dynamicTable, 0, dynamicTable, this.dynamicTable.length, this.dynamicTable.length);
                    this.nextHeaderIndex = this.dynamicTable.length - 1;
                    this.dynamicTable = dynamicTable;
                }
                this.dynamicTable[this.nextHeaderIndex--] = header;
                ++this.headerCount;
                this.dynamicTableByteCount += hpackSize;
                return;
            }
            this.clearDynamicTable();
        }
        
        void setHeaderTableSizeSetting(int min) {
            this.headerTableSizeSetting = min;
            min = Math.min(min, 16384);
            if (this.maxDynamicTableByteCount != min) {
                if (min < this.maxDynamicTableByteCount) {
                    this.smallestHeaderTableSizeSetting = Math.min(this.smallestHeaderTableSizeSetting, min);
                }
                this.emitDynamicTableSizeUpdate = true;
                this.maxDynamicTableByteCount = min;
                this.adjustDynamicTableByteCount();
            }
        }
        
        void writeByteString(ByteString byteString) throws IOException {
            if (this.useCompression && Huffman.get().encodedLength(byteString) < byteString.size()) {
                final Buffer buffer = new Buffer();
                Huffman.get().encode(byteString, buffer);
                byteString = buffer.readByteString();
                this.writeInt(byteString.size(), 127, 128);
                this.out.write(byteString);
            }
            else {
                this.writeInt(byteString.size(), 127, 0);
                this.out.write(byteString);
            }
        }
        
        void writeHeaders(final List<Header> list) throws IOException {
            if (this.emitDynamicTableSizeUpdate) {
                if (this.smallestHeaderTableSizeSetting < this.maxDynamicTableByteCount) {
                    this.writeInt(this.smallestHeaderTableSizeSetting, 31, 32);
                }
                this.emitDynamicTableSizeUpdate = false;
                this.smallestHeaderTableSizeSetting = Integer.MAX_VALUE;
                this.writeInt(this.maxDynamicTableByteCount, 31, 32);
            }
            for (int size = list.size(), i = 0; i < size; ++i) {
                final Header header = list.get(i);
                final ByteString asciiLowercase = header.name.toAsciiLowercase();
                final ByteString value = header.value;
                final Integer n = Hpack.NAME_TO_FIRST_INDEX.get(asciiLowercase);
                int n2;
                int n3;
                if (n == null) {
                    n2 = -1;
                    n3 = -1;
                }
                else {
                    n2 = n + 1;
                    if (n2 > 1 && n2 < 8) {
                        if (!Util.equal(Hpack.STATIC_HEADER_TABLE[n2 - 1].value, value)) {
                            if (!Util.equal(Hpack.STATIC_HEADER_TABLE[n2].value, value)) {
                                n3 = -1;
                            }
                            else {
                                n3 = n2 + 1;
                            }
                        }
                        else {
                            n3 = n2;
                        }
                    }
                    else {
                        n3 = -1;
                    }
                }
                int n4 = 0;
                int n5 = 0;
                Label_0143: {
                    if (n3 != -1) {
                        n4 = n3;
                        n5 = n2;
                    }
                    else {
                        int n6 = this.nextHeaderIndex + 1;
                        final int length = this.dynamicTable.length;
                        while (true) {
                            n5 = n2;
                            n4 = n3;
                            if (n6 >= length) {
                                break Label_0143;
                            }
                            int n7;
                            if (!Util.equal(this.dynamicTable[n6].name, asciiLowercase)) {
                                n7 = n2;
                            }
                            else {
                                if (Util.equal(this.dynamicTable[n6].value, value)) {
                                    break;
                                }
                                if ((n7 = n2) == -1) {
                                    n7 = n6 - this.nextHeaderIndex + Hpack.STATIC_HEADER_TABLE.length;
                                }
                            }
                            ++n6;
                            n2 = n7;
                        }
                        n4 = n6 - this.nextHeaderIndex + Hpack.STATIC_HEADER_TABLE.length;
                        n5 = n2;
                    }
                }
                if (n4 == -1) {
                    if (n5 != -1) {
                        if (asciiLowercase.startsWith(Header.PSEUDO_PREFIX) && !Header.TARGET_AUTHORITY.equals(asciiLowercase)) {
                            this.writeInt(n5, 15, 0);
                            this.writeByteString(value);
                        }
                        else {
                            this.writeInt(n5, 63, 64);
                            this.writeByteString(value);
                            this.insertIntoDynamicTable(header);
                        }
                    }
                    else {
                        this.out.writeByte(64);
                        this.writeByteString(asciiLowercase);
                        this.writeByteString(value);
                        this.insertIntoDynamicTable(header);
                    }
                }
                else {
                    this.writeInt(n4, 127, 128);
                }
            }
        }
        
        void writeInt(int i, final int n, final int n2) {
            if (i >= n) {
                this.out.writeByte(n2 | n);
                for (i -= n; i >= 128; i >>>= 7) {
                    this.out.writeByte((i & 0x7F) | 0x80);
                }
                this.out.writeByte(i);
                return;
            }
            this.out.writeByte(n2 | i);
        }
    }
}
