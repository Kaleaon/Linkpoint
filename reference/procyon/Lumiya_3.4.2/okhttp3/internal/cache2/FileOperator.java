// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.cache2;

import java.io.IOException;
import java.io.EOFException;
import okio.Buffer;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;

final class FileOperator
{
    private static final int BUFFER_SIZE = 8192;
    private final byte[] byteArray;
    private final ByteBuffer byteBuffer;
    private final FileChannel fileChannel;
    
    public FileOperator(final FileChannel fileChannel) {
        this.byteArray = new byte[8192];
        this.byteBuffer = ByteBuffer.wrap(this.byteArray);
        this.fileChannel = fileChannel;
    }
    
    public void read(long n, final Buffer buffer, long b) throws IOException {
        int position;
        if (b >= 0L) {
            position = 1;
        }
        else {
            position = 0;
        }
        if (position == 0) {
            throw new IndexOutOfBoundsException();
        }
    Label_0039_Outer:
        while (true) {
            Label_0123: {
                if (b > 0L) {
                    break Label_0123;
                }
                position = 1;
                while (true) {
                    if (position != 0) {
                        break;
                    }
                    try {
                        this.byteBuffer.limit();
                        if (this.fileChannel.read(this.byteBuffer, n) != -1) {
                            position = this.byteBuffer.position();
                            buffer.write(this.byteArray, 0, position);
                            n += position;
                            b -= position;
                            continue Label_0039_Outer;
                        }
                        throw new EOFException();
                        position = 0;
                        continue;
                    }
                    finally {
                        this.byteBuffer.clear();
                    }
                    break;
                }
            }
            break;
        }
    }
    
    public void write(long n, final Buffer buffer, long b) throws IOException {
        int n2;
        if (b < 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            int n3;
            if (b <= buffer.size()) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            if (n3 != 0) {
            Label_0063_Outer:
                while (true) {
                    Label_0158: {
                        if (b > 0L) {
                            break Label_0158;
                        }
                        n3 = 1;
                        while (true) {
                            if (n3 != 0) {
                                break;
                            }
                            try {
                                n3 = (int)Math.min(8192L, b);
                                buffer.read(this.byteArray, 0, n3);
                                this.byteBuffer.limit();
                                long n4 = n;
                                boolean hasRemaining;
                                do {
                                    n = n4 + this.fileChannel.write(this.byteBuffer, n4);
                                    hasRemaining = this.byteBuffer.hasRemaining();
                                    n4 = n;
                                } while (hasRemaining);
                                b -= n3;
                                continue Label_0063_Outer;
                                n3 = 0;
                                continue;
                            }
                            finally {
                                this.byteBuffer.clear();
                            }
                            break;
                        }
                    }
                    break;
                }
                return;
            }
        }
        throw new IndexOutOfBoundsException();
    }
}
