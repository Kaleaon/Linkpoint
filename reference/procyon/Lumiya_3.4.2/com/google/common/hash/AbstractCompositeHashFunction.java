// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.hash;

import java.nio.charset.Charset;
import com.google.common.base.Preconditions;

abstract class AbstractCompositeHashFunction extends AbstractStreamingHashFunction
{
    private static final long serialVersionUID = 0L;
    final HashFunction[] functions;
    
    AbstractCompositeHashFunction(final HashFunction... functions) {
        for (int length = functions.length, i = 0; i < length; ++i) {
            Preconditions.checkNotNull(functions[i]);
        }
        this.functions = functions;
    }
    
    abstract HashCode makeHash(final Hasher[] p0);
    
    @Override
    public Hasher newHasher() {
        final Hasher[] array = new Hasher[this.functions.length];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.functions[i].newHasher();
        }
        return new Hasher() {
            @Override
            public HashCode hash() {
                return AbstractCompositeHashFunction.this.makeHash(array);
            }
            
            @Override
            public Hasher putBoolean(final boolean b) {
                final Hasher[] val$hashers = array;
                for (int length = val$hashers.length, i = 0; i < length; ++i) {
                    val$hashers[i].putBoolean(b);
                }
                return this;
            }
            
            @Override
            public Hasher putByte(final byte b) {
                final Hasher[] val$hashers = array;
                for (int length = val$hashers.length, i = 0; i < length; ++i) {
                    val$hashers[i].putByte(b);
                }
                return this;
            }
            
            @Override
            public Hasher putBytes(final byte[] array) {
                final Hasher[] val$hashers = array;
                for (int length = val$hashers.length, i = 0; i < length; ++i) {
                    val$hashers[i].putBytes(array);
                }
                return this;
            }
            
            @Override
            public Hasher putBytes(final byte[] array, final int n, final int n2) {
                final Hasher[] val$hashers = array;
                for (int length = val$hashers.length, i = 0; i < length; ++i) {
                    val$hashers[i].putBytes(array, n, n2);
                }
                return this;
            }
            
            @Override
            public Hasher putChar(final char c) {
                final Hasher[] val$hashers = array;
                for (int length = val$hashers.length, i = 0; i < length; ++i) {
                    val$hashers[i].putChar(c);
                }
                return this;
            }
            
            @Override
            public Hasher putDouble(final double n) {
                final Hasher[] val$hashers = array;
                for (int length = val$hashers.length, i = 0; i < length; ++i) {
                    val$hashers[i].putDouble(n);
                }
                return this;
            }
            
            @Override
            public Hasher putFloat(final float n) {
                final Hasher[] val$hashers = array;
                for (int length = val$hashers.length, i = 0; i < length; ++i) {
                    val$hashers[i].putFloat(n);
                }
                return this;
            }
            
            @Override
            public Hasher putInt(final int n) {
                final Hasher[] val$hashers = array;
                for (int length = val$hashers.length, i = 0; i < length; ++i) {
                    val$hashers[i].putInt(n);
                }
                return this;
            }
            
            @Override
            public Hasher putLong(final long n) {
                final Hasher[] val$hashers = array;
                for (int length = val$hashers.length, i = 0; i < length; ++i) {
                    val$hashers[i].putLong(n);
                }
                return this;
            }
            
            @Override
            public <T> Hasher putObject(final T t, final Funnel<? super T> funnel) {
                final Hasher[] val$hashers = array;
                for (int length = val$hashers.length, i = 0; i < length; ++i) {
                    val$hashers[i].putObject(t, funnel);
                }
                return this;
            }
            
            @Override
            public Hasher putShort(final short n) {
                final Hasher[] val$hashers = array;
                for (int length = val$hashers.length, i = 0; i < length; ++i) {
                    val$hashers[i].putShort(n);
                }
                return this;
            }
            
            @Override
            public Hasher putString(final CharSequence charSequence, final Charset charset) {
                final Hasher[] val$hashers = array;
                for (int length = val$hashers.length, i = 0; i < length; ++i) {
                    val$hashers[i].putString(charSequence, charset);
                }
                return this;
            }
            
            @Override
            public Hasher putUnencodedChars(final CharSequence charSequence) {
                final Hasher[] val$hashers = array;
                for (int length = val$hashers.length, i = 0; i < length; ++i) {
                    val$hashers[i].putUnencodedChars(charSequence);
                }
                return this;
            }
        };
    }
}
