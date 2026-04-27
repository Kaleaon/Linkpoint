package okhttp3.internal.ws;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import java.io.EOFException;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.concurrent.TimeUnit;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;

final class WebSocketReader {
    boolean closed;
    long frameBytesRead;
    final FrameCallback frameCallback;
    long frameLength;
    final boolean isClient;
    boolean isControlFrame;
    boolean isFinalFrame;
    boolean isMasked;
    final byte[] maskBuffer = new byte[8192];
    final byte[] maskKey = new byte[4];
    int opcode;
    final BufferedSource source;

    public interface FrameCallback {
        void onReadClose(int i, String str);

        void onReadMessage(String str) throws IOException;

        void onReadMessage(ByteString byteString) throws IOException;

        void onReadPing(ByteString byteString);

        void onReadPong(ByteString byteString);
    }

    WebSocketReader(boolean z, BufferedSource bufferedSource, FrameCallback frameCallback2) {
        if (bufferedSource == null) {
            throw new NullPointerException("source == null");
        } else if (frameCallback2 != null) {
            this.isClient = z;
            this.source = bufferedSource;
            this.frameCallback = frameCallback2;
        } else {
            throw new NullPointerException("frameCallback == null");
        }
    }

    private void readControlFrame() throws IOException {
        Buffer buffer = new Buffer();
        if (!(this.frameBytesRead >= this.frameLength)) {
            if (!this.isClient) {
                while (true) {
                    if (this.frameBytesRead >= this.frameLength) {
                        break;
                    }
                    int read = this.source.read(this.maskBuffer, 0, (int) Math.min(this.frameLength - this.frameBytesRead, (long) this.maskBuffer.length));
                    if (read != -1) {
                        WebSocketProtocol.toggleMask(this.maskBuffer, (long) read, this.maskKey, this.frameBytesRead);
                        buffer.write(this.maskBuffer, 0, read);
                        this.frameBytesRead += (long) read;
                    } else {
                        throw new EOFException();
                    }
                }
            } else {
                this.source.readFully(buffer, this.frameLength);
            }
        }
        switch (this.opcode) {
            case 8:
                short s = 1005;
                String str = "";
                long size = buffer.size();
                if (size == 1) {
                    throw new ProtocolException("Malformed close payload length of 1.");
                }
                if (size != 0) {
                    s = buffer.readShort();
                    str = buffer.readUtf8();
                    String closeCodeExceptionMessage = WebSocketProtocol.closeCodeExceptionMessage(s);
                    if (closeCodeExceptionMessage != null) {
                        throw new ProtocolException(closeCodeExceptionMessage);
                    }
                }
                this.frameCallback.onReadClose(s, str);
                this.closed = true;
                return;
            case 9:
                this.frameCallback.onReadPing(buffer.readByteString());
                return;
            case 10:
                this.frameCallback.onReadPong(buffer.readByteString());
                return;
            default:
                throw new ProtocolException("Unknown control opcode: " + Integer.toHexString(this.opcode));
        }
    }

    /* JADX INFO: finally extract failed */
    private void readHeader() throws IOException {
        boolean z = true;
        if (!this.closed) {
            long timeoutNanos = this.source.timeout().timeoutNanos();
            this.source.timeout().clearTimeout();
            try {
                byte readByte = this.source.readByte() & UnsignedBytes.MAX_VALUE;
                this.source.timeout().timeout(timeoutNanos, TimeUnit.NANOSECONDS);
                this.opcode = readByte & 15;
                this.isFinalFrame = (readByte & 128) != 0;
                this.isControlFrame = (readByte & 8) != 0;
                if (this.isControlFrame && !this.isFinalFrame) {
                    throw new ProtocolException("Control frames must be final.");
                }
                boolean z2 = (readByte & 64) != 0;
                boolean z3 = (readByte & 32) != 0;
                boolean z4 = (readByte & 16) != 0;
                if (!z2 && !z3 && !z4) {
                    byte readByte2 = this.source.readByte() & UnsignedBytes.MAX_VALUE;
                    this.isMasked = (readByte2 & 128) != 0;
                    if (this.isMasked != this.isClient) {
                        this.frameLength = (long) (readByte2 & Ascii.DEL);
                        if (this.frameLength == 126) {
                            this.frameLength = ((long) this.source.readShort()) & 65535;
                        } else if (this.frameLength == 127) {
                            this.frameLength = this.source.readLong();
                            if (!(this.frameLength >= 0)) {
                                throw new ProtocolException("Frame length 0x" + Long.toHexString(this.frameLength) + " > 0x7FFFFFFFFFFFFFFF");
                            }
                        }
                        this.frameBytesRead = 0;
                        if (this.isControlFrame) {
                            if (this.frameLength > 125) {
                                z = false;
                            }
                            if (!z) {
                                throw new ProtocolException("Control frame must be less than 125B.");
                            }
                        }
                        if (this.isMasked) {
                            this.source.readFully(this.maskKey);
                            return;
                        }
                        return;
                    }
                    throw new ProtocolException(!this.isClient ? "Client-sent frames must be masked." : "Server-sent frames must not be masked.");
                }
                throw new ProtocolException("Reserved flags are unsupported.");
            } catch (Throwable th) {
                this.source.timeout().timeout(timeoutNanos, TimeUnit.NANOSECONDS);
                throw th;
            }
        } else {
            throw new IOException("closed");
        }
    }

    private void readMessage(Buffer buffer) throws IOException {
        long j;
        while (!this.closed) {
            if (this.frameBytesRead == this.frameLength) {
                if (!this.isFinalFrame) {
                    readUntilNonControlFrame();
                    if (this.opcode != 0) {
                        throw new ProtocolException("Expected continuation opcode. Got: " + Integer.toHexString(this.opcode));
                    } else if (this.isFinalFrame && this.frameLength == 0) {
                        return;
                    }
                } else {
                    return;
                }
            }
            long j2 = this.frameLength - this.frameBytesRead;
            if (!this.isMasked) {
                j = this.source.read(buffer, j2);
                if (j == -1) {
                    throw new EOFException();
                }
            } else {
                j = (long) this.source.read(this.maskBuffer, 0, (int) Math.min(j2, (long) this.maskBuffer.length));
                if (j == -1) {
                    throw new EOFException();
                }
                WebSocketProtocol.toggleMask(this.maskBuffer, j, this.maskKey, this.frameBytesRead);
                buffer.write(this.maskBuffer, 0, (int) j);
            }
            this.frameBytesRead += j;
        }
        throw new IOException("closed");
    }

    private void readMessageFrame() throws IOException {
        int i = this.opcode;
        if (i == 1 || i == 2) {
            Buffer buffer = new Buffer();
            readMessage(buffer);
            if (i != 1) {
                this.frameCallback.onReadMessage(buffer.readByteString());
            } else {
                this.frameCallback.onReadMessage(buffer.readUtf8());
            }
        } else {
            throw new ProtocolException("Unknown opcode: " + Integer.toHexString(i));
        }
    }

    /* access modifiers changed from: package-private */
    public void processNextFrame() throws IOException {
        readHeader();
        if (!this.isControlFrame) {
            readMessageFrame();
        } else {
            readControlFrame();
        }
    }

    /* access modifiers changed from: package-private */
    public void readUntilNonControlFrame() throws IOException {
        while (!this.closed) {
            readHeader();
            if (this.isControlFrame) {
                readControlFrame();
            } else {
                return;
            }
        }
    }
}
