// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import java.io.FilterInputStream;
import java.io.DataOutputStream;
import java.io.DataOutput;
import java.io.DataInputStream;
import java.io.DataInput;
import java.util.Arrays;
import java.io.EOFException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.io.IOException;
import java.io.InputStream;
import com.google.common.base.Preconditions;
import java.io.OutputStream;
import com.google.common.annotations.Beta;

@Beta
public final class ByteStreams
{
    static final int BUF_SIZE = 8192;
    private static final OutputStream NULL_OUTPUT_STREAM;
    private static final int ZERO_COPY_CHUNK_SIZE = 524288;
    static final byte[] skipBuffer;
    
    static {
        skipBuffer = new byte[8192];
        NULL_OUTPUT_STREAM = new OutputStream() {
            @Override
            public String toString() {
                return "ByteStreams.nullOutputStream()";
            }
            
            @Override
            public void write(final int n) {
            }
            
            @Override
            public void write(final byte[] array) {
                Preconditions.checkNotNull(array);
            }
            
            @Override
            public void write(final byte[] array, final int n, final int n2) {
                Preconditions.checkNotNull(array);
            }
        };
    }
    
    private ByteStreams() {
    }
    
    public static long copy(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        Preconditions.checkNotNull(inputStream);
        Preconditions.checkNotNull(outputStream);
        final byte[] array = new byte[8192];
        long n = 0L;
        while (true) {
            final int read = inputStream.read(array);
            if (read == -1) {
                break;
            }
            outputStream.write(array, 0, read);
            n += read;
        }
        return n;
    }
    
    public static long copy(final ReadableByteChannel readableByteChannel, final WritableByteChannel writableByteChannel) throws IOException {
        Preconditions.checkNotNull(readableByteChannel);
        Preconditions.checkNotNull(writableByteChannel);
        if (!(readableByteChannel instanceof FileChannel)) {
            final ByteBuffer allocate = ByteBuffer.allocate(8192);
            long n = 0L;
            while (readableByteChannel.read(allocate) != -1) {
                allocate.flip();
                while (allocate.hasRemaining()) {
                    n += writableByteChannel.write(allocate);
                }
                allocate.clear();
            }
            return n;
        }
        final FileChannel fileChannel = (FileChannel)readableByteChannel;
        long position;
        final long n2 = position = fileChannel.position();
        long n3;
        while (true) {
            final long transferTo = fileChannel.transferTo(position, 524288L, writableByteChannel);
            n3 = position + transferTo;
            fileChannel.position(n3);
            int n4;
            if (transferTo > 0L) {
                n4 = 1;
            }
            else {
                n4 = 0;
            }
            position = n3;
            if (n4 == 0) {
                int n5;
                if (n3 < fileChannel.size()) {
                    n5 = 1;
                }
                else {
                    n5 = 0;
                }
                position = n3;
                if (n5 == 0) {
                    break;
                }
                continue;
            }
        }
        return n3 - n2;
    }
    
    public static InputStream limit(final InputStream inputStream, final long n) {
        return new LimitedInputStream(inputStream, n);
    }
    
    public static ByteArrayDataInput newDataInput(final ByteArrayInputStream byteArrayInputStream) {
        return new ByteArrayDataInputStream(Preconditions.checkNotNull(byteArrayInputStream));
    }
    
    public static ByteArrayDataInput newDataInput(final byte[] buf) {
        return newDataInput(new ByteArrayInputStream(buf));
    }
    
    public static ByteArrayDataInput newDataInput(final byte[] buf, final int offset) {
        Preconditions.checkPositionIndex(offset, buf.length);
        return newDataInput(new ByteArrayInputStream(buf, offset, buf.length - offset));
    }
    
    public static ByteArrayDataOutput newDataOutput() {
        return newDataOutput(new ByteArrayOutputStream());
    }
    
    public static ByteArrayDataOutput newDataOutput(final int n) {
        if (n >= 0) {
            return newDataOutput(new ByteArrayOutputStream(n));
        }
        throw new IllegalArgumentException(String.format("Invalid size: %s", n));
    }
    
    public static ByteArrayDataOutput newDataOutput(final ByteArrayOutputStream byteArrayOutputStream) {
        return new ByteArrayDataOutputStream(Preconditions.checkNotNull(byteArrayOutputStream));
    }
    
    public static OutputStream nullOutputStream() {
        return ByteStreams.NULL_OUTPUT_STREAM;
    }
    
    public static int read(final InputStream inputStream, final byte[] b, final int n, final int n2) throws IOException {
        int i = 0;
        Preconditions.checkNotNull(inputStream);
        Preconditions.checkNotNull(b);
        if (n2 >= 0) {
            while (i < n2) {
                final int read = inputStream.read(b, n + i, n2 - i);
                if (read == -1) {
                    break;
                }
                i += read;
            }
            return i;
        }
        throw new IndexOutOfBoundsException("len is negative");
    }
    
    public static <T> T readBytes(final InputStream inputStream, final ByteProcessor<T> byteProcessor) throws IOException {
        Preconditions.checkNotNull(inputStream);
        Preconditions.checkNotNull(byteProcessor);
        final byte[] b = new byte[8192];
        int read;
        do {
            read = inputStream.read(b);
        } while (read != -1 && byteProcessor.processBytes(b, 0, read));
        return (T)byteProcessor.getResult();
    }
    
    public static void readFully(final InputStream inputStream, final byte[] array) throws IOException {
        readFully(inputStream, array, 0, array.length);
    }
    
    public static void readFully(final InputStream inputStream, final byte[] array, int read, final int i) throws IOException {
        read = read(inputStream, array, read, i);
        if (read == i) {
            return;
        }
        throw new EOFException("reached end of stream after reading " + read + " bytes; " + i + " bytes expected");
    }
    
    public static void skipFully(final InputStream inputStream, final long lng) throws IOException {
        final long skipUpTo = skipUpTo(inputStream, lng);
        int n;
        if (skipUpTo >= lng) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n == 0) {
            throw new EOFException("reached end of stream after skipping " + skipUpTo + " bytes; " + lng + " bytes expected");
        }
    }
    
    private static long skipSafely(final InputStream inputStream, long skip) throws IOException {
        final int available = inputStream.available();
        if (available != 0) {
            skip = inputStream.skip(Math.min(available, skip));
        }
        else {
            skip = 0L;
        }
        return skip;
    }
    
    static long skipUpTo(final InputStream inputStream, final long n) throws IOException {
        long n2 = 0L;
        while (true) {
            int n3;
            if (n2 >= n) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            if (n3 != 0) {
                break;
            }
            final long a = n - n2;
            long skipSafely;
            if ((skipSafely = skipSafely(inputStream, a)) == 0L && (skipSafely = inputStream.read(ByteStreams.skipBuffer, 0, (int)Math.min(a, ByteStreams.skipBuffer.length))) == -1L) {
                break;
            }
            n2 += skipSafely;
        }
        return n2;
    }
    
    public static byte[] toByteArray(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        copy(inputStream, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    static byte[] toByteArray(final InputStream inputStream, int read) throws IOException {
        final byte[] array = new byte[read];
        int read2;
        for (int i = read; i > 0; i -= read2) {
            final int n = read - i;
            read2 = inputStream.read(array, n, i);
            if (read2 == -1) {
                return Arrays.copyOf(array, n);
            }
        }
        read = inputStream.read();
        if (read != -1) {
            final FastByteArrayOutputStream fastByteArrayOutputStream = new FastByteArrayOutputStream();
            fastByteArrayOutputStream.write(read);
            copy(inputStream, fastByteArrayOutputStream);
            final byte[] array2 = new byte[array.length + fastByteArrayOutputStream.size()];
            System.arraycopy(array, 0, array2, 0, array.length);
            fastByteArrayOutputStream.writeTo(array2, array.length);
            return array2;
        }
        return array;
    }
    
    private static class ByteArrayDataInputStream implements ByteArrayDataInput
    {
        final DataInput input;
        
        ByteArrayDataInputStream(final ByteArrayInputStream in) {
            this.input = new DataInputStream(in);
        }
        
        @Override
        public boolean readBoolean() {
            try {
                return this.input.readBoolean();
            }
            catch (final IOException cause) {
                throw new IllegalStateException(cause);
            }
        }
        
        @Override
        public byte readByte() {
            try {
                return this.input.readByte();
            }
            catch (final EOFException cause) {
                throw new IllegalStateException(cause);
            }
            catch (final IOException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        @Override
        public char readChar() {
            try {
                return this.input.readChar();
            }
            catch (final IOException cause) {
                throw new IllegalStateException(cause);
            }
        }
        
        @Override
        public double readDouble() {
            try {
                return this.input.readDouble();
            }
            catch (final IOException cause) {
                throw new IllegalStateException(cause);
            }
        }
        
        @Override
        public float readFloat() {
            try {
                return this.input.readFloat();
            }
            catch (final IOException cause) {
                throw new IllegalStateException(cause);
            }
        }
        
        @Override
        public void readFully(final byte[] array) {
            try {
                this.input.readFully(array);
            }
            catch (final IOException cause) {
                throw new IllegalStateException(cause);
            }
        }
        
        @Override
        public void readFully(final byte[] array, final int n, final int n2) {
            try {
                this.input.readFully(array, n, n2);
            }
            catch (final IOException cause) {
                throw new IllegalStateException(cause);
            }
        }
        
        @Override
        public int readInt() {
            try {
                return this.input.readInt();
            }
            catch (final IOException cause) {
                throw new IllegalStateException(cause);
            }
        }
        
        @Override
        public String readLine() {
            try {
                return this.input.readLine();
            }
            catch (final IOException cause) {
                throw new IllegalStateException(cause);
            }
        }
        
        @Override
        public long readLong() {
            try {
                return this.input.readLong();
            }
            catch (final IOException cause) {
                throw new IllegalStateException(cause);
            }
        }
        
        @Override
        public short readShort() {
            try {
                return this.input.readShort();
            }
            catch (final IOException cause) {
                throw new IllegalStateException(cause);
            }
        }
        
        @Override
        public String readUTF() {
            try {
                return this.input.readUTF();
            }
            catch (final IOException cause) {
                throw new IllegalStateException(cause);
            }
        }
        
        @Override
        public int readUnsignedByte() {
            try {
                return this.input.readUnsignedByte();
            }
            catch (final IOException cause) {
                throw new IllegalStateException(cause);
            }
        }
        
        @Override
        public int readUnsignedShort() {
            try {
                return this.input.readUnsignedShort();
            }
            catch (final IOException cause) {
                throw new IllegalStateException(cause);
            }
        }
        
        @Override
        public int skipBytes(int skipBytes) {
            try {
                skipBytes = this.input.skipBytes(skipBytes);
                return skipBytes;
            }
            catch (final IOException cause) {
                throw new IllegalStateException(cause);
            }
        }
    }
    
    private static class ByteArrayDataOutputStream implements ByteArrayDataOutput
    {
        final ByteArrayOutputStream byteArrayOutputSteam;
        final DataOutput output;
        
        ByteArrayDataOutputStream(final ByteArrayOutputStream byteArrayOutputStream) {
            this.byteArrayOutputSteam = byteArrayOutputStream;
            this.output = new DataOutputStream(byteArrayOutputStream);
        }
        
        @Override
        public byte[] toByteArray() {
            return this.byteArrayOutputSteam.toByteArray();
        }
        
        @Override
        public void write(final int n) {
            try {
                this.output.write(n);
            }
            catch (final IOException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        @Override
        public void write(final byte[] array) {
            try {
                this.output.write(array);
            }
            catch (final IOException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        @Override
        public void write(final byte[] array, final int n, final int n2) {
            try {
                this.output.write(array, n, n2);
            }
            catch (final IOException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        @Override
        public void writeBoolean(final boolean b) {
            try {
                this.output.writeBoolean(b);
            }
            catch (final IOException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        @Override
        public void writeByte(final int n) {
            try {
                this.output.writeByte(n);
            }
            catch (final IOException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        @Override
        public void writeBytes(final String s) {
            try {
                this.output.writeBytes(s);
            }
            catch (final IOException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        @Override
        public void writeChar(final int n) {
            try {
                this.output.writeChar(n);
            }
            catch (final IOException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        @Override
        public void writeChars(final String s) {
            try {
                this.output.writeChars(s);
            }
            catch (final IOException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        @Override
        public void writeDouble(final double n) {
            try {
                this.output.writeDouble(n);
            }
            catch (final IOException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        @Override
        public void writeFloat(final float n) {
            try {
                this.output.writeFloat(n);
            }
            catch (final IOException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        @Override
        public void writeInt(final int n) {
            try {
                this.output.writeInt(n);
            }
            catch (final IOException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        @Override
        public void writeLong(final long n) {
            try {
                this.output.writeLong(n);
            }
            catch (final IOException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        @Override
        public void writeShort(final int n) {
            try {
                this.output.writeShort(n);
            }
            catch (final IOException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        @Override
        public void writeUTF(final String s) {
            try {
                this.output.writeUTF(s);
            }
            catch (final IOException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
    }
    
    private static final class FastByteArrayOutputStream extends ByteArrayOutputStream
    {
        void writeTo(final byte[] array, final int n) {
            System.arraycopy(this.buf, 0, array, n, this.count);
        }
    }
    
    private static final class LimitedInputStream extends FilterInputStream
    {
        private long left;
        private long mark;
        
        LimitedInputStream(final InputStream in, final long left) {
            boolean b = true;
            super(in);
            this.mark = -1L;
            Preconditions.checkNotNull(in);
            int n;
            if (left < 0L) {
                n = 1;
            }
            else {
                n = 0;
            }
            if (n != 0) {
                b = false;
            }
            Preconditions.checkArgument(b, (Object)"limit must be non-negative");
            this.left = left;
        }
        
        @Override
        public int available() throws IOException {
            return (int)Math.min(this.in.available(), this.left);
        }
        
        @Override
        public void mark(final int readlimit) {
            synchronized (this) {
                this.in.mark(readlimit);
                this.mark = this.left;
            }
        }
        
        @Override
        public int read() throws IOException {
            if (this.left == 0L) {
                return -1;
            }
            final int read = this.in.read();
            if (read != -1) {
                --this.left;
            }
            return read;
        }
        
        @Override
        public int read(final byte[] b, int read, int len) throws IOException {
            if (this.left == 0L) {
                return -1;
            }
            len = (int)Math.min(len, this.left);
            read = this.in.read(b, read, len);
            if (read != -1) {
                this.left -= read;
            }
            return read;
        }
        
        @Override
        public void reset() throws IOException {
            Label_0052: {
                synchronized (this) {
                    if (this.in.markSupported()) {
                        if (this.mark == -1L) {
                            throw new IOException("Mark not set");
                        }
                        break Label_0052;
                    }
                }
                throw new IOException("Mark not supported");
            }
            this.in.reset();
            this.left = this.mark;
            monitorexit(this);
        }
        
        @Override
        public long skip(long n) throws IOException {
            n = Math.min(n, this.left);
            n = this.in.skip(n);
            this.left -= n;
            return n;
        }
    }
}
