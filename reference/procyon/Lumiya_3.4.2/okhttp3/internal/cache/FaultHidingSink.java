// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.cache;

import okio.Buffer;
import java.io.IOException;
import okio.Sink;
import okio.ForwardingSink;

class FaultHidingSink extends ForwardingSink
{
    private boolean hasErrors;
    
    public FaultHidingSink(final Sink sink) {
        super(sink);
    }
    
    @Override
    public void close() throws IOException {
        if (this.hasErrors) {
            return;
        }
        try {
            super.close();
        }
        catch (final IOException ex) {
            this.hasErrors = true;
            this.onException(ex);
        }
    }
    
    @Override
    public void flush() throws IOException {
        if (this.hasErrors) {
            return;
        }
        try {
            super.flush();
        }
        catch (final IOException ex) {
            this.hasErrors = true;
            this.onException(ex);
        }
    }
    
    protected void onException(final IOException ex) {
    }
    
    @Override
    public void write(final Buffer buffer, final long n) throws IOException {
        Label_0014: {
            if (this.hasErrors) {
                break Label_0014;
            }
            try {
                super.write(buffer, n);
                return;
                buffer.skip(n);
            }
            catch (final IOException ex) {
                this.hasErrors = true;
                this.onException(ex);
            }
        }
    }
}
