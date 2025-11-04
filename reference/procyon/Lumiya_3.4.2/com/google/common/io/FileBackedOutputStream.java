// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import java.io.ByteArrayOutputStream;
import com.google.common.annotations.VisibleForTesting;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import com.google.common.annotations.Beta;
import java.io.OutputStream;

@Beta
public final class FileBackedOutputStream extends OutputStream
{
    private File file;
    private final int fileThreshold;
    private MemoryOutput memory;
    private OutputStream out;
    private final boolean resetOnFinalize;
    private final ByteSource source;
    
    public FileBackedOutputStream(final int n) {
        this(n, false);
    }
    
    public FileBackedOutputStream(final int fileThreshold, final boolean resetOnFinalize) {
        this.fileThreshold = fileThreshold;
        this.resetOnFinalize = resetOnFinalize;
        this.memory = new MemoryOutput();
        this.out = this.memory;
        if (!resetOnFinalize) {
            this.source = new ByteSource() {
                @Override
                public InputStream openStream() throws IOException {
                    return FileBackedOutputStream.this.openInputStream();
                }
            };
        }
        else {
            this.source = new ByteSource() {
                @Override
                protected void finalize() {
                    try {
                        FileBackedOutputStream.this.reset();
                    }
                    catch (final Throwable t) {
                        t.printStackTrace(System.err);
                    }
                }
                
                @Override
                public InputStream openStream() throws IOException {
                    return FileBackedOutputStream.this.openInputStream();
                }
            };
        }
    }
    
    private InputStream openInputStream() throws IOException {
        synchronized (this) {
            if (this.file == null) {
                return new ByteArrayInputStream(this.memory.getBuffer(), 0, this.memory.getCount());
            }
            return new FileInputStream(this.file);
        }
    }
    
    private void update(final int n) throws IOException {
        if (this.file == null && this.memory.getCount() + n > this.fileThreshold) {
            final File tempFile = File.createTempFile("FileBackedOutputStream", null);
            if (this.resetOnFinalize) {
                tempFile.deleteOnExit();
            }
            final FileOutputStream out = new FileOutputStream(tempFile);
            out.write(this.memory.getBuffer(), 0, this.memory.getCount());
            out.flush();
            this.out = out;
            this.file = tempFile;
            this.memory = null;
        }
    }
    
    public ByteSource asByteSource() {
        return this.source;
    }
    
    @Override
    public void close() throws IOException {
        synchronized (this) {
            this.out.close();
        }
    }
    
    @Override
    public void flush() throws IOException {
        synchronized (this) {
            this.out.flush();
        }
    }
    
    @VisibleForTesting
    File getFile() {
        synchronized (this) {
            return this.file;
        }
    }
    
    public void reset() throws IOException {
        monitorenter(this);
        try {
            File file;
            while (true) {
                this.close();
                try {
                    if (this.memory != null) {
                        this.memory.reset();
                    }
                    else {
                        this.memory = new MemoryOutput();
                    }
                    this.out = this.memory;
                    if (this.file == null) {
                        return;
                    }
                }
                finally {
                    monitorexit(this);
                }
                file = this.file;
                this.file = null;
                if (!file.delete()) {
                    break;
                }
                return;
            }
            throw new IOException("Could not delete: " + file);
        }
        finally {
            if (this.memory != null) {
                this.memory.reset();
            }
            else {
                this.memory = new MemoryOutput();
            }
            this.out = this.memory;
            if (this.file != null) {
                final File file2 = this.file;
                this.file = null;
                if (!file2.delete()) {
                    final IOException ex = new IOException("Could not delete: " + file2);
                }
            }
        }
    }
    
    @Override
    public void write(final int n) throws IOException {
        synchronized (this) {
            this.update(1);
            this.out.write(n);
        }
    }
    
    @Override
    public void write(final byte[] array) throws IOException {
        synchronized (this) {
            this.write(array, 0, array.length);
        }
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        synchronized (this) {
            this.update(len);
            this.out.write(b, off, len);
        }
    }
    
    private static class MemoryOutput extends ByteArrayOutputStream
    {
        byte[] getBuffer() {
            return this.buf;
        }
        
        int getCount() {
            return this.count;
        }
    }
}
