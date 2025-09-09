package okhttp3.internal.cache2;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import okio.Buffer;

final class FileOperator {
    private static final int BUFFER_SIZE = 8192;
    private final byte[] byteArray = new byte[8192];
    private final ByteBuffer byteBuffer = ByteBuffer.wrap(this.byteArray);
    private final FileChannel fileChannel;

    public FileOperator(FileChannel fileChannel2) {
        this.fileChannel = fileChannel2;
    }

    public void read(long j, Buffer buffer, long j2) throws IOException {
        if (!(j2 >= 0)) {
            throw new IndexOutOfBoundsException();
        }
        while (true) {
            if (!(j2 <= 0)) {
                try {
                    this.byteBuffer.limit((int) Math.min(8192, j2));
                    if (this.fileChannel.read(this.byteBuffer, j) != -1) {
                        int position = this.byteBuffer.position();
                        buffer.write(this.byteArray, 0, position);
                        j += (long) position;
                        j2 -= (long) position;
                    } else {
                        throw new EOFException();
                    }
                } finally {
                    this.byteBuffer.clear();
                }
            } else {
                return;
            }
        }
    }

    public void write(long j, Buffer buffer, long j2) throws IOException {
        if (!(j2 < 0)) {
            if (j2 <= buffer.size()) {
                long j3 = j;
                while (true) {
                    if (!(j2 <= 0)) {
                        try {
                            int min = (int) Math.min(8192, j2);
                            buffer.read(this.byteArray, 0, min);
                            this.byteBuffer.limit(min);
                            do {
                                j3 += (long) this.fileChannel.write(this.byteBuffer, j3);
                            } while (this.byteBuffer.hasRemaining());
                            j2 -= (long) min;
                        } finally {
                            this.byteBuffer.clear();
                        }
                    } else {
                        return;
                    }
                }
            }
        }
        throw new IndexOutOfBoundsException();
    }
}
