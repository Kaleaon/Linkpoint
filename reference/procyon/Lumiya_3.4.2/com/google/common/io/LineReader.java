// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import java.io.IOException;
import com.google.common.base.Preconditions;
import java.util.LinkedList;
import java.io.Reader;
import java.util.Queue;
import java.nio.CharBuffer;
import com.google.common.annotations.Beta;

@Beta
public final class LineReader
{
    private final char[] buf;
    private final CharBuffer cbuf;
    private final LineBuffer lineBuf;
    private final Queue<String> lines;
    private final Readable readable;
    private final Reader reader;
    
    public LineReader(final Readable readable) {
        this.buf = new char[4096];
        this.cbuf = CharBuffer.wrap(this.buf);
        this.lines = new LinkedList<String>();
        this.lineBuf = new LineBuffer() {
            @Override
            protected void handleLine(final String s, final String s2) {
                LineReader.this.lines.add(s);
            }
        };
        this.readable = Preconditions.checkNotNull(readable);
        Reader reader;
        if (!(readable instanceof Reader)) {
            reader = null;
        }
        else {
            reader = (Reader)readable;
        }
        this.reader = reader;
    }
    
    public String readLine() throws IOException {
        while (this.lines.peek() == null) {
            this.cbuf.clear();
            int n;
            if (this.reader == null) {
                n = this.readable.read(this.cbuf);
            }
            else {
                n = this.reader.read(this.buf, 0, this.buf.length);
            }
            if (n == -1) {
                this.lineBuf.finish();
                break;
            }
            this.lineBuf.add(this.buf, 0, n);
        }
        return this.lines.poll();
    }
}
