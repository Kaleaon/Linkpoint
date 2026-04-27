// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.hash;

import com.google.common.primitives.UnsignedBytes;
import com.google.common.primitives.SignedBytes;
import java.io.DataOutputStream;
import java.io.OutputStream;
import com.google.common.base.Objects;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import com.google.common.annotations.VisibleForTesting;
import javax.annotation.CheckReturnValue;
import com.google.common.base.Preconditions;
import com.google.common.annotations.Beta;
import java.io.Serializable;
import com.google.common.base.Predicate;

@Beta
public final class BloomFilter<T> implements Predicate<T>, Serializable
{
    private final BloomFilterStrategies.BitArray bits;
    private final Funnel<? super T> funnel;
    private final int numHashFunctions;
    private final Strategy strategy;
    
    private BloomFilter(final BloomFilterStrategies.BitArray bitArray, final int numHashFunctions, final Funnel<? super T> funnel, final Strategy strategy) {
        Preconditions.checkArgument(numHashFunctions > 0, "numHashFunctions (%s) must be > 0", numHashFunctions);
        Preconditions.checkArgument(numHashFunctions <= 255, "numHashFunctions (%s) must be <= 255", numHashFunctions);
        this.bits = Preconditions.checkNotNull(bitArray);
        this.numHashFunctions = numHashFunctions;
        this.funnel = Preconditions.checkNotNull(funnel);
        this.strategy = Preconditions.checkNotNull(strategy);
    }
    
    @CheckReturnValue
    public static <T> BloomFilter<T> create(final Funnel<? super T> funnel, final int n) {
        return create(funnel, (long)n);
    }
    
    @CheckReturnValue
    public static <T> BloomFilter<T> create(final Funnel<? super T> funnel, final int n, final double n2) {
        return create(funnel, (long)n, n2);
    }
    
    @CheckReturnValue
    public static <T> BloomFilter<T> create(final Funnel<? super T> funnel, final long n) {
        return create(funnel, n, 0.03);
    }
    
    @CheckReturnValue
    public static <T> BloomFilter<T> create(final Funnel<? super T> funnel, final long n, final double n2) {
        return create(funnel, n, n2, (Strategy)BloomFilterStrategies.MURMUR128_MITZ_64);
    }
    
    @VisibleForTesting
    static <T> BloomFilter<T> create(final Funnel<? super T> funnel, long optimalNumOfBits, final double n, final Strategy strategy) {
        Preconditions.checkNotNull(funnel);
        Label_0155: {
            if (optimalNumOfBits >= 0L) {
                break Label_0155;
            }
            int n2 = 1;
        Label_0022_Outer:
            while (true) {
                Label_0161: {
                    if (n2 != 0) {
                        break Label_0161;
                    }
                    boolean b = true;
                Label_0049_Outer:
                    while (true) {
                        Preconditions.checkArgument(b, "Expected insertions (%s) must be >= 0", optimalNumOfBits);
                        Label_0167: {
                            if (n <= 0.0) {
                                break Label_0167;
                            }
                            boolean b2 = true;
                        Label_0076_Outer:
                            while (true) {
                                Preconditions.checkArgument(b2, "False positive probability (%s) must be > 0.0", n);
                                Label_0173: {
                                    if (n >= 1.0) {
                                        break Label_0173;
                                    }
                                    boolean b3 = true;
                                    while (true) {
                                        Preconditions.checkArgument(b3, "False positive probability (%s) must be < 1.0", n);
                                        Preconditions.checkNotNull(strategy);
                                        long n3 = optimalNumOfBits;
                                        if (optimalNumOfBits == 0L) {
                                            n3 = 1L;
                                        }
                                        optimalNumOfBits = optimalNumOfBits(n3, n);
                                        final int optimalNumOfHashFunctions = optimalNumOfHashFunctions(n3, optimalNumOfBits);
                                        try {
                                            return new BloomFilter<T>(new BloomFilterStrategies.BitArray(optimalNumOfBits), optimalNumOfHashFunctions, (Funnel<? super Object>)funnel, strategy);
                                            n2 = 0;
                                            continue Label_0022_Outer;
                                            b2 = false;
                                            continue Label_0076_Outer;
                                            b = false;
                                            continue Label_0049_Outer;
                                            b3 = false;
                                            continue;
                                        }
                                        catch (final IllegalArgumentException cause) {
                                            throw new IllegalArgumentException("Could not create BloomFilter of " + optimalNumOfBits + " bits", cause);
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
    }
    
    @VisibleForTesting
    static long optimalNumOfBits(final long n, final double n2) {
        double a = n2;
        if (n2 == 0.0) {
            a = Double.MIN_VALUE;
        }
        return (long)(-n * Math.log(a) / (Math.log(2.0) * Math.log(2.0)));
    }
    
    @VisibleForTesting
    static int optimalNumOfHashFunctions(final long n, final long n2) {
        return Math.max(1, (int)Math.round(n2 / (double)n * Math.log(2.0)));
    }
    
    @CheckReturnValue
    public static <T> BloomFilter<T> readFrom(final InputStream p0, final Funnel<T> p1) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore_2       
        //     2: aload_0        
        //     3: ldc             "InputStream"
        //     5: invokestatic    com/google/common/base/Preconditions.checkNotNull:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //     8: pop            
        //     9: aload_1        
        //    10: ldc             "Funnel"
        //    12: invokestatic    com/google/common/base/Preconditions.checkNotNull:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //    15: pop            
        //    16: new             Ljava/io/DataInputStream;
        //    19: astore_3       
        //    20: aload_3        
        //    21: aload_0        
        //    22: invokespecial   java/io/DataInputStream.<init>:(Ljava/io/InputStream;)V
        //    25: aload_3        
        //    26: invokevirtual   java/io/DataInputStream.readByte:()B
        //    29: istore          4
        //    31: aload_3        
        //    32: invokevirtual   java/io/DataInputStream.readByte:()B
        //    35: invokestatic    com/google/common/primitives/UnsignedBytes.toInt:(B)I
        //    38: istore          5
        //    40: iload_2        
        //    41: istore          6
        //    43: aload_3        
        //    44: invokevirtual   java/io/DataInputStream.readInt:()I
        //    47: istore          7
        //    49: iload           7
        //    51: istore          6
        //    53: invokestatic    com/google/common/hash/BloomFilterStrategies.values:()[Lcom/google/common/hash/BloomFilterStrategies;
        //    56: iload           4
        //    58: aaload         
        //    59: astore_0       
        //    60: iload           7
        //    62: istore          6
        //    64: iload           7
        //    66: newarray        J
        //    68: astore          8
        //    70: iconst_0       
        //    71: istore_2       
        //    72: iload           7
        //    74: istore          6
        //    76: iload_2        
        //    77: aload           8
        //    79: arraylength    
        //    80: if_icmplt       118
        //    83: iload           7
        //    85: istore          6
        //    87: new             Lcom/google/common/hash/BloomFilterStrategies$BitArray;
        //    90: astore_3       
        //    91: iload           7
        //    93: istore          6
        //    95: aload_3        
        //    96: aload           8
        //    98: invokespecial   com/google/common/hash/BloomFilterStrategies$BitArray.<init>:([J)V
        //   101: iload           7
        //   103: istore          6
        //   105: new             Lcom/google/common/hash/BloomFilter;
        //   108: dup            
        //   109: aload_3        
        //   110: iload           5
        //   112: aload_1        
        //   113: aload_0        
        //   114: invokespecial   com/google/common/hash/BloomFilter.<init>:(Lcom/google/common/hash/BloomFilterStrategies$BitArray;ILcom/google/common/hash/Funnel;Lcom/google/common/hash/BloomFilter$Strategy;)V
        //   117: areturn        
        //   118: iload           7
        //   120: istore          6
        //   122: aload           8
        //   124: iload_2        
        //   125: aload_3        
        //   126: invokevirtual   java/io/DataInputStream.readLong:()J
        //   129: lastore        
        //   130: iinc            2, 1
        //   133: goto            72
        //   136: astore_0       
        //   137: iconst_m1      
        //   138: istore          6
        //   140: iconst_m1      
        //   141: istore          4
        //   143: new             Ljava/io/IOException;
        //   146: dup            
        //   147: new             Ljava/lang/StringBuilder;
        //   150: dup            
        //   151: invokespecial   java/lang/StringBuilder.<init>:()V
        //   154: ldc             "Unable to deserialize BloomFilter from InputStream. strategyOrdinal: "
        //   156: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   159: iload           4
        //   161: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //   164: ldc             " numHashFunctions: "
        //   166: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   169: iload           6
        //   171: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //   174: ldc             " dataLength: "
        //   176: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   179: iload_2        
        //   180: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //   183: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   186: invokespecial   java/io/IOException.<init>:(Ljava/lang/String;)V
        //   189: astore_1       
        //   190: aload_1        
        //   191: aload_0        
        //   192: invokevirtual   java/io/IOException.initCause:(Ljava/lang/Throwable;)Ljava/lang/Throwable;
        //   195: pop            
        //   196: aload_1        
        //   197: athrow         
        //   198: astore_0       
        //   199: iconst_m1      
        //   200: istore          6
        //   202: goto            143
        //   205: astore_0       
        //   206: iload           6
        //   208: istore_2       
        //   209: iload           5
        //   211: istore          6
        //   213: goto            143
        //    Exceptions:
        //  throws java.io.IOException
        //    Signature:
        //  <T:Ljava/lang/Object;>(Ljava/io/InputStream;Lcom/google/common/hash/Funnel<TT;>;)Lcom/google/common/hash/BloomFilter<TT;>;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                        
        //  -----  -----  -----  -----  ----------------------------
        //  16     31     136    143    Ljava/lang/RuntimeException;
        //  31     40     198    205    Ljava/lang/RuntimeException;
        //  43     49     205    216    Ljava/lang/RuntimeException;
        //  53     60     205    216    Ljava/lang/RuntimeException;
        //  64     70     205    216    Ljava/lang/RuntimeException;
        //  76     83     205    216    Ljava/lang/RuntimeException;
        //  87     91     205    216    Ljava/lang/RuntimeException;
        //  95     101    205    216    Ljava/lang/RuntimeException;
        //  105    118    205    216    Ljava/lang/RuntimeException;
        //  122    130    205    216    Ljava/lang/RuntimeException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 115 out of bounds for length 115
        //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
        //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
        //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
        //     at java.base/java.util.Objects.checkIndex(Objects.java:385)
        //     at java.base/java.util.ArrayList.get(ArrayList.java:427)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3362)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:112)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private Object writeReplace() {
        return new SerialForm((BloomFilter<Object>)this);
    }
    
    @Deprecated
    @CheckReturnValue
    @Override
    public boolean apply(final T t) {
        return this.mightContain(t);
    }
    
    @VisibleForTesting
    long bitSize() {
        return this.bits.bitSize();
    }
    
    @CheckReturnValue
    public BloomFilter<T> copy() {
        return new BloomFilter<T>(this.bits.copy(), this.numHashFunctions, this.funnel, this.strategy);
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        boolean b = true;
        if (o == this) {
            return true;
        }
        if (!(o instanceof BloomFilter)) {
            return false;
        }
        final BloomFilter bloomFilter = (BloomFilter)o;
        if (this.numHashFunctions != bloomFilter.numHashFunctions || !this.funnel.equals(bloomFilter.funnel) || !this.bits.equals(bloomFilter.bits) || !this.strategy.equals(bloomFilter.strategy)) {
            b = false;
        }
        return b;
    }
    
    @CheckReturnValue
    public double expectedFpp() {
        return Math.pow(this.bits.bitCount() / (double)this.bitSize(), this.numHashFunctions);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.numHashFunctions, this.funnel, this.strategy, this.bits);
    }
    
    @CheckReturnValue
    public boolean isCompatible(final BloomFilter<T> bloomFilter) {
        final boolean b = false;
        Preconditions.checkNotNull(bloomFilter);
        boolean b2;
        if (this == bloomFilter) {
            b2 = b;
        }
        else {
            b2 = b;
            if (this.numHashFunctions == bloomFilter.numHashFunctions) {
                b2 = b;
                if (this.bitSize() == bloomFilter.bitSize()) {
                    b2 = b;
                    if (this.strategy.equals(bloomFilter.strategy)) {
                        b2 = b;
                        if (this.funnel.equals(bloomFilter.funnel)) {
                            b2 = true;
                        }
                    }
                }
            }
        }
        return b2;
    }
    
    @CheckReturnValue
    public boolean mightContain(final T t) {
        return this.strategy.mightContain(t, this.funnel, this.numHashFunctions, this.bits);
    }
    
    public boolean put(final T t) {
        return this.strategy.put(t, this.funnel, this.numHashFunctions, this.bits);
    }
    
    public void putAll(final BloomFilter<T> bloomFilter) {
        Preconditions.checkNotNull(bloomFilter);
        Preconditions.checkArgument(this != bloomFilter, (Object)"Cannot combine a BloomFilter with itself.");
        Preconditions.checkArgument(this.numHashFunctions == bloomFilter.numHashFunctions, "BloomFilters must have the same number of hash functions (%s != %s)", this.numHashFunctions, bloomFilter.numHashFunctions);
        Preconditions.checkArgument(this.bitSize() == bloomFilter.bitSize(), "BloomFilters must have the same size underlying bit arrays (%s != %s)", this.bitSize(), bloomFilter.bitSize());
        Preconditions.checkArgument(this.strategy.equals(bloomFilter.strategy), "BloomFilters must have equal strategies (%s != %s)", this.strategy, bloomFilter.strategy);
        Preconditions.checkArgument(this.funnel.equals(bloomFilter.funnel), "BloomFilters must have equal funnels (%s != %s)", this.funnel, bloomFilter.funnel);
        this.bits.putAll(bloomFilter.bits);
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        final DataOutputStream dataOutputStream = new DataOutputStream(out);
        dataOutputStream.writeByte(SignedBytes.checkedCast(this.strategy.ordinal()));
        dataOutputStream.writeByte(UnsignedBytes.checkedCast(this.numHashFunctions));
        dataOutputStream.writeInt(this.bits.data.length);
        final long[] data = this.bits.data;
        for (int length = data.length, i = 0; i < length; ++i) {
            dataOutputStream.writeLong(data[i]);
        }
    }
    
    private static class SerialForm<T> implements Serializable
    {
        private static final long serialVersionUID = 1L;
        final long[] data;
        final Funnel<? super T> funnel;
        final int numHashFunctions;
        final Strategy strategy;
        
        SerialForm(final BloomFilter<T> bloomFilter) {
            this.data = ((BloomFilter<Object>)bloomFilter).bits.data;
            this.numHashFunctions = ((BloomFilter<Object>)bloomFilter).numHashFunctions;
            this.funnel = ((BloomFilter<Object>)bloomFilter).funnel;
            this.strategy = ((BloomFilter<Object>)bloomFilter).strategy;
        }
        
        Object readResolve() {
            return new BloomFilter(new BloomFilterStrategies.BitArray(this.data), this.numHashFunctions, this.funnel, this.strategy, null);
        }
    }
    
    interface Strategy extends Serializable
    {
         <T> boolean mightContain(final T p0, final Funnel<? super T> p1, final int p2, final BloomFilterStrategies.BitArray p3);
        
        int ordinal();
        
         <T> boolean put(final T p0, final Funnel<? super T> p1, final int p2, final BloomFilterStrategies.BitArray p3);
    }
}
