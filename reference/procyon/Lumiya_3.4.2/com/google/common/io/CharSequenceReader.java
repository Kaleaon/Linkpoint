// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import java.nio.CharBuffer;
import java.io.IOException;
import com.google.common.base.Preconditions;
import java.io.Reader;

final class CharSequenceReader extends Reader
{
    private int mark;
    private int pos;
    private CharSequence seq;
    
    public CharSequenceReader(final CharSequence charSequence) {
        this.seq = Preconditions.checkNotNull(charSequence);
    }
    
    private void checkOpen() throws IOException {
        if (this.seq != null) {
            return;
        }
        throw new IOException("reader closed");
    }
    
    private boolean hasRemaining() {
        boolean b = false;
        if (this.remaining() > 0) {
            b = true;
        }
        return b;
    }
    
    private int remaining() {
        return this.seq.length() - this.pos;
    }
    
    @Override
    public void close() throws IOException {
        synchronized (this) {
            this.seq = null;
        }
    }
    
    @Override
    public void mark(final int i) throws IOException {
        boolean b = false;
        monitorenter(this);
        Label_0040: {
            if (i >= 0) {
                break Label_0040;
            }
            try {
                while (true) {
                    Preconditions.checkArgument(b, "readAheadLimit (%s) may not be negative", i);
                    this.checkOpen();
                    this.mark = this.pos;
                    return;
                    b = true;
                    continue;
                }
            }
            finally {
                monitorexit(this);
            }
        }
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public int read() throws IOException {
        synchronized (this) {
            this.checkOpen();
            int char1;
            if (!this.hasRemaining()) {
                char1 = -1;
            }
            else {
                char1 = this.seq.charAt(this.pos++);
            }
            return char1;
        }
    }
    
    @Override
    public int read(final CharBuffer charBuffer) throws IOException {
        int i = 0;
        synchronized (this) {
            Preconditions.checkNotNull(charBuffer);
            this.checkOpen();
            if (this.hasRemaining()) {
                int min;
                for (min = Math.min(charBuffer.remaining(), this.remaining()); i < min; ++i) {
                    charBuffer.put(this.seq.charAt(this.pos++));
                }
                return min;
            }
            return -1;
        }
    }
    
    @Override
    public int read(final char[] array, final int n, int i) throws IOException {
        final int n2 = 0;
        synchronized (this) {
            Preconditions.checkPositionIndexes(n, n + i, array.length);
            this.checkOpen();
            if (this.hasRemaining()) {
                int min;
                for (min = Math.min(i, this.remaining()), i = n2; i < min; ++i) {
                    array[n + i] = this.seq.charAt(this.pos++);
                }
                return min;
            }
            return -1;
        }
    }
    
    @Override
    public boolean ready() throws IOException {
        synchronized (this) {
            this.checkOpen();
            return true;
        }
    }
    
    @Override
    public void reset() throws IOException {
        synchronized (this) {
            this.checkOpen();
            this.pos = this.mark;
        }
    }
    
    @Override
    public long skip(long n) throws IOException {
        boolean b = true;
        monitorenter(this);
        Label_0070: {
            if (n >= 0L) {
                break Label_0070;
            }
            int n2 = 1;
        Label_0018_Outer:
            while (true) {
                Label_0076: {
                    if (n2 != 0) {
                        break Label_0076;
                    }
                    try {
                        while (true) {
                            Preconditions.checkArgument(b, "n (%s) may not be negative", n);
                            this.checkOpen();
                            n2 = (int)Math.min(this.remaining(), n);
                            this.pos += n2;
                            n = n2;
                            return n;
                            b = false;
                            continue;
                        }
                        n2 = 0;
                        continue Label_0018_Outer;
                    }
                    finally {
                        monitorexit(this);
                    }
                }
                break;
            }
        }
    }
}
