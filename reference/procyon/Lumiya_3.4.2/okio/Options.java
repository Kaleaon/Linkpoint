// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.util.RandomAccess;
import java.util.AbstractList;

public final class Options extends AbstractList<ByteString> implements RandomAccess
{
    final ByteString[] byteStrings;
    
    private Options(final ByteString[] byteStrings) {
        this.byteStrings = byteStrings;
    }
    
    public static Options of(final ByteString... array) {
        return new Options(array.clone());
    }
    
    @Override
    public ByteString get(final int n) {
        return this.byteStrings[n];
    }
    
    @Override
    public int size() {
        return this.byteStrings.length;
    }
}
