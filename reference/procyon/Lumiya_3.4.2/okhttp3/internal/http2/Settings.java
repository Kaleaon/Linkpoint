// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.http2;

import java.util.Arrays;

public final class Settings
{
    static final int COUNT = 10;
    static final int DEFAULT_INITIAL_WINDOW_SIZE = 65535;
    static final int ENABLE_PUSH = 2;
    static final int HEADER_TABLE_SIZE = 1;
    static final int INITIAL_WINDOW_SIZE = 7;
    static final int MAX_CONCURRENT_STREAMS = 4;
    static final int MAX_FRAME_SIZE = 5;
    static final int MAX_HEADER_LIST_SIZE = 6;
    private int set;
    private final int[] values;
    
    public Settings() {
        this.values = new int[10];
    }
    
    void clear() {
        this.set = 0;
        Arrays.fill(this.values, 0);
    }
    
    int get(final int n) {
        return this.values[n];
    }
    
    boolean getEnablePush(final boolean b) {
        final boolean b2 = false;
        int n;
        if ((this.set & 0x4) == 0x0) {
            if (!b) {
                n = 0;
            }
            else {
                n = 1;
            }
        }
        else {
            n = this.values[2];
        }
        return n == 1 || b2;
    }
    
    int getHeaderTableSize() {
        int n;
        if ((this.set & 0x2) == 0x0) {
            n = -1;
        }
        else {
            n = this.values[1];
        }
        return n;
    }
    
    int getInitialWindowSize() {
        int n;
        if ((this.set & 0x80) == 0x0) {
            n = 65535;
        }
        else {
            n = this.values[7];
        }
        return n;
    }
    
    int getMaxConcurrentStreams(int n) {
        if ((this.set & 0x10) != 0x0) {
            n = this.values[4];
        }
        return n;
    }
    
    int getMaxFrameSize(int n) {
        if ((this.set & 0x20) != 0x0) {
            n = this.values[5];
        }
        return n;
    }
    
    int getMaxHeaderListSize(int n) {
        if ((this.set & 0x40) != 0x0) {
            n = this.values[6];
        }
        return n;
    }
    
    boolean isSet(final int n) {
        boolean b = false;
        if ((1 << n & this.set) != 0x0) {
            b = true;
        }
        return b;
    }
    
    void merge(final Settings settings) {
        for (int i = 0; i < 10; ++i) {
            if (settings.isSet(i)) {
                this.set(i, settings.get(i));
            }
        }
    }
    
    Settings set(final int n, final int n2) {
        if (n < this.values.length) {
            this.set |= 1 << n;
            this.values[n] = n2;
            return this;
        }
        return this;
    }
    
    int size() {
        return Integer.bitCount(this.set);
    }
}
