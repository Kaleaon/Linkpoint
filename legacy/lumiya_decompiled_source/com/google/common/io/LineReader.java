package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.LinkedList;
import java.util.Queue;
@Beta
/* loaded from: classes.dex */
public final class LineReader {
    private final Readable readable;
    private final Reader reader;
    private final char[] buf = new char[4096];
    private final CharBuffer cbuf = CharBuffer.wrap(this.buf);
    private final Queue<String> lines = new LinkedList();
    private final LineBuffer lineBuf = new LineBuffer() { // from class: com.google.common.io.LineReader.1
        @Override // com.google.common.io.LineBuffer
        protected void handleLine(String str, String str2) {
            LineReader.this.lines.add(str);
        }
    };

    public LineReader(Readable readable) {
        this.readable = (Readable) Preconditions.checkNotNull(readable);
        this.reader = !(readable instanceof Reader) ? null : (Reader) readable;
    }

    public String readLine() throws IOException {
        while (true) {
            if (this.lines.peek() == null) {
                this.cbuf.clear();
                int read = this.reader == null ? this.readable.read(this.cbuf) : this.reader.read(this.buf, 0, this.buf.length);
                if (read == -1) {
                    this.lineBuf.finish();
                    break;
                }
                this.lineBuf.add(this.buf, 0, read);
            } else {
                break;
            }
        }
        return this.lines.poll();
    }
}
