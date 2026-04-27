// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.cache;

import java.util.concurrent.atomic.AtomicLong;
import com.google.common.base.Supplier;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
final class LongAddables
{
    private static final Supplier<LongAddable> SUPPLIER;
    
    static {
        while (true) {
            try {
                new LongAdder();
                final Supplier<LongAddable> supplier = new Supplier<LongAddable>() {
                    @Override
                    public LongAddable get() {
                        return new LongAdder();
                    }
                };
                SUPPLIER = supplier;
            }
            catch (final Throwable t) {
                final Supplier<LongAddable> supplier = new Supplier<LongAddable>() {
                    @Override
                    public LongAddable get() {
                        return new PureJavaLongAddable();
                    }
                };
                continue;
            }
            break;
        }
    }
    
    public static LongAddable create() {
        return LongAddables.SUPPLIER.get();
    }
    
    private static final class PureJavaLongAddable extends AtomicLong implements LongAddable
    {
        @Override
        public void add(final long delta) {
            this.getAndAdd(delta);
        }
        
        @Override
        public void increment() {
            this.getAndIncrement();
        }
        
        @Override
        public long sum() {
            return this.get();
        }
    }
}
