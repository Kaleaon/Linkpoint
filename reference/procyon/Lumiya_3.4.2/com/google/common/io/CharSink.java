// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import java.util.Iterator;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.Writer;

public abstract class CharSink
{
    protected CharSink() {
    }
    
    public Writer openBufferedStream() throws IOException {
        final Writer openStream = this.openStream();
        BufferedWriter bufferedWriter;
        if (!(openStream instanceof BufferedWriter)) {
            bufferedWriter = new BufferedWriter(openStream);
        }
        else {
            bufferedWriter = (BufferedWriter)openStream;
        }
        return bufferedWriter;
    }
    
    public abstract Writer openStream() throws IOException;
    
    public void write(final CharSequence csq) throws IOException {
        Preconditions.checkNotNull(csq);
        final Closer create = Closer.create();
        try {
            final Writer writer = create.register(this.openStream());
            writer.append(csq);
            writer.flush();
        }
        catch (final Throwable t) {
            throw create.rethrow(t);
        }
        finally {
            create.close();
        }
    }
    
    public long writeFrom(final Readable readable) throws IOException {
        Preconditions.checkNotNull(readable);
        final Closer create = Closer.create();
        try {
            final Writer writer = create.register(this.openStream());
            final long copy = CharStreams.copy(readable, writer);
            writer.flush();
            return copy;
        }
        catch (final Throwable t) {
            throw create.rethrow(t);
        }
        finally {
            create.close();
        }
    }
    
    public void writeLines(final Iterable<? extends CharSequence> iterable) throws IOException {
        this.writeLines(iterable, System.getProperty("line.separator"));
    }
    
    public void writeLines(final Iterable<? extends CharSequence> iterable, final String csq) throws IOException {
        Preconditions.checkNotNull(iterable);
        Preconditions.checkNotNull(csq);
        final Closer create = Closer.create();
        try {
            final Writer writer = create.register(this.openBufferedStream());
            final Iterator<? extends CharSequence> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                writer.append((CharSequence)iterator.next()).append((CharSequence)csq);
            }
            writer.flush();
        }
        catch (final Throwable t) {
            throw create.rethrow(t);
        }
        finally {
            create.close();
        }
    }
}
