package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.nio.charset.Charset;

abstract class AbstractCompositeHashFunction extends AbstractStreamingHashFunction {
    private static final long serialVersionUID = 0;
    final HashFunction[] functions;

    AbstractCompositeHashFunction(HashFunction... hashFunctionArr) {
        for (HashFunction checkNotNull : hashFunctionArr) {
            Preconditions.checkNotNull(checkNotNull);
        }
        this.functions = hashFunctionArr;
    }

    /* access modifiers changed from: package-private */
    public abstract HashCode makeHash(Hasher[] hasherArr);

    public Hasher newHasher() {
        final Hasher[] hasherArr = new Hasher[this.functions.length];
        for (int i = 0; i < hasherArr.length; i++) {
            hasherArr[i] = this.functions[i].newHasher();
        }
        return new Hasher() {
            public HashCode hash() {
                return AbstractCompositeHashFunction.this.makeHash(hasherArr);
            }

            public Hasher putBoolean(boolean z) {
                for (Hasher putBoolean : hasherArr) {
                    putBoolean.putBoolean(z);
                }
                return this;
            }

            public Hasher putByte(byte b) {
                for (Hasher putByte : hasherArr) {
                    putByte.putByte(b);
                }
                return this;
            }

            public Hasher putBytes(byte[] bArr) {
                for (Hasher putBytes : hasherArr) {
                    putBytes.putBytes(bArr);
                }
                return this;
            }

            public Hasher putBytes(byte[] bArr, int i, int i2) {
                for (Hasher putBytes : hasherArr) {
                    putBytes.putBytes(bArr, i, i2);
                }
                return this;
            }

            public Hasher putChar(char c) {
                for (Hasher putChar : hasherArr) {
                    putChar.putChar(c);
                }
                return this;
            }

            public Hasher putDouble(double d) {
                for (Hasher putDouble : hasherArr) {
                    putDouble.putDouble(d);
                }
                return this;
            }

            public Hasher putFloat(float f) {
                for (Hasher putFloat : hasherArr) {
                    putFloat.putFloat(f);
                }
                return this;
            }

            public Hasher putInt(int i) {
                for (Hasher putInt : hasherArr) {
                    putInt.putInt(i);
                }
                return this;
            }

            public Hasher putLong(long j) {
                for (Hasher putLong : hasherArr) {
                    putLong.putLong(j);
                }
                return this;
            }

            public <T> Hasher putObject(T t, Funnel<? super T> funnel) {
                for (Hasher putObject : hasherArr) {
                    putObject.putObject(t, funnel);
                }
                return this;
            }

            public Hasher putShort(short s) {
                for (Hasher putShort : hasherArr) {
                    putShort.putShort(s);
                }
                return this;
            }

            public Hasher putString(CharSequence charSequence, Charset charset) {
                for (Hasher putString : hasherArr) {
                    putString.putString(charSequence, charset);
                }
                return this;
            }

            public Hasher putUnencodedChars(CharSequence charSequence) {
                for (Hasher putUnencodedChars : hasherArr) {
                    putUnencodedChars.putUnencodedChars(charSequence);
                }
                return this;
            }
        };
    }
}
