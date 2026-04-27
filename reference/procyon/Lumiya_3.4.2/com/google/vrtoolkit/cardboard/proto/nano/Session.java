// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vrtoolkit.cardboard.proto.nano;

import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.WireFormatNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import java.io.IOException;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.ExtendableMessageNano;

public interface Session
{
    public static final class TrackerState extends ExtendableMessageNano<TrackerState> implements Cloneable
    {
        private static volatile TrackerState[] _emptyArray;
        private int bitField0_;
        public double[] gyroscopeBias;
        public double[] lastGyroscopeSample;
        private double lastGyroscopeTimestamp_;
        public float[] lensOffset;
        public double[] q;
        private long timeSinceEpochSeconds_;
        
        public TrackerState() {
            this.clear();
        }
        
        public static TrackerState[] emptyArray() {
            if (TrackerState._emptyArray == null) {
                while (true) {
                    while (true) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (TrackerState._emptyArray != null) {
                                break;
                            }
                        }
                        TrackerState._emptyArray = new TrackerState[0];
                        continue;
                    }
                }
            }
            return TrackerState._emptyArray;
        }
        
        public static TrackerState parseFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new TrackerState().mergeFrom(codedInputByteBufferNano);
        }
        
        public static TrackerState parseFrom(final byte[] array) throws InvalidProtocolBufferNanoException {
            return MessageNano.mergeFrom(new TrackerState(), array);
        }
        
        public final TrackerState clear() {
            this.bitField0_ = 0;
            this.q = WireFormatNano.EMPTY_DOUBLE_ARRAY;
            this.timeSinceEpochSeconds_ = 0L;
            this.gyroscopeBias = WireFormatNano.EMPTY_DOUBLE_ARRAY;
            this.lensOffset = WireFormatNano.EMPTY_FLOAT_ARRAY;
            this.lastGyroscopeSample = WireFormatNano.EMPTY_DOUBLE_ARRAY;
            this.lastGyroscopeTimestamp_ = 0.0;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }
        
        public final TrackerState clearLastGyroscopeTimestamp() {
            this.lastGyroscopeTimestamp_ = 0.0;
            this.bitField0_ &= 0xFFFFFFFD;
            return this;
        }
        
        public final TrackerState clearTimeSinceEpochSeconds() {
            this.timeSinceEpochSeconds_ = 0L;
            this.bitField0_ &= 0xFFFFFFFE;
            return this;
        }
        
        @Override
        public final TrackerState clone() {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: invokespecial   com/google/protobuf/nano/ExtendableMessageNano.clone:()Lcom/google/protobuf/nano/ExtendableMessageNano;
            //     4: checkcast       Lcom/google/vrtoolkit/cardboard/proto/nano/Session$TrackerState;
            //     7: astore_1       
            //     8: aload_0        
            //     9: getfield        com/google/vrtoolkit/cardboard/proto/nano/Session$TrackerState.q:[D
            //    12: ifnonnull       48
            //    15: aload_0        
            //    16: getfield        com/google/vrtoolkit/cardboard/proto/nano/Session$TrackerState.gyroscopeBias:[D
            //    19: ifnonnull       73
            //    22: aload_0        
            //    23: getfield        com/google/vrtoolkit/cardboard/proto/nano/Session$TrackerState.lensOffset:[F
            //    26: ifnonnull       98
            //    29: aload_0        
            //    30: getfield        com/google/vrtoolkit/cardboard/proto/nano/Session$TrackerState.lastGyroscopeSample:[D
            //    33: ifnonnull       123
            //    36: aload_1        
            //    37: areturn        
            //    38: astore_1       
            //    39: new             Ljava/lang/AssertionError;
            //    42: dup            
            //    43: aload_1        
            //    44: invokespecial   java/lang/AssertionError.<init>:(Ljava/lang/Object;)V
            //    47: athrow         
            //    48: aload_0        
            //    49: getfield        com/google/vrtoolkit/cardboard/proto/nano/Session$TrackerState.q:[D
            //    52: arraylength    
            //    53: ifle            15
            //    56: aload_1        
            //    57: aload_0        
            //    58: getfield        com/google/vrtoolkit/cardboard/proto/nano/Session$TrackerState.q:[D
            //    61: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
            //    64: checkcast       [D
            //    67: putfield        com/google/vrtoolkit/cardboard/proto/nano/Session$TrackerState.q:[D
            //    70: goto            15
            //    73: aload_0        
            //    74: getfield        com/google/vrtoolkit/cardboard/proto/nano/Session$TrackerState.gyroscopeBias:[D
            //    77: arraylength    
            //    78: ifle            22
            //    81: aload_1        
            //    82: aload_0        
            //    83: getfield        com/google/vrtoolkit/cardboard/proto/nano/Session$TrackerState.gyroscopeBias:[D
            //    86: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
            //    89: checkcast       [D
            //    92: putfield        com/google/vrtoolkit/cardboard/proto/nano/Session$TrackerState.gyroscopeBias:[D
            //    95: goto            22
            //    98: aload_0        
            //    99: getfield        com/google/vrtoolkit/cardboard/proto/nano/Session$TrackerState.lensOffset:[F
            //   102: arraylength    
            //   103: ifle            29
            //   106: aload_1        
            //   107: aload_0        
            //   108: getfield        com/google/vrtoolkit/cardboard/proto/nano/Session$TrackerState.lensOffset:[F
            //   111: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
            //   114: checkcast       [F
            //   117: putfield        com/google/vrtoolkit/cardboard/proto/nano/Session$TrackerState.lensOffset:[F
            //   120: goto            29
            //   123: aload_0        
            //   124: getfield        com/google/vrtoolkit/cardboard/proto/nano/Session$TrackerState.lastGyroscopeSample:[D
            //   127: arraylength    
            //   128: ifle            36
            //   131: aload_1        
            //   132: aload_0        
            //   133: getfield        com/google/vrtoolkit/cardboard/proto/nano/Session$TrackerState.lastGyroscopeSample:[D
            //   136: invokevirtual   java/lang/Object.clone:()Ljava/lang/Object;
            //   139: checkcast       [D
            //   142: putfield        com/google/vrtoolkit/cardboard/proto/nano/Session$TrackerState.lastGyroscopeSample:[D
            //   145: goto            36
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                                  
            //  -----  -----  -----  -----  --------------------------------------
            //  0      8      38     48     Ljava/lang/CloneNotSupportedException;
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: cmpgt:boolean(arraylength:int(getfield:double[](TrackerState::lastGyroscopeSample, this:TrackerState)), ldc:int(0))
            //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
            //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
            //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
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
        
        @Override
        protected final int computeSerializedSize() {
            final int computeSerializedSize = super.computeSerializedSize();
            int n;
            if (this.q == null) {
                n = computeSerializedSize;
            }
            else {
                n = computeSerializedSize;
                if (this.q.length > 0) {
                    final int n2 = this.q.length * 8;
                    n = computeSerializedSize + n2 + 1 + CodedOutputByteBufferNano.computeRawVarint32Size(n2);
                }
            }
            if ((this.bitField0_ & 0x1) != 0x0) {
                n += CodedOutputByteBufferNano.computeInt64Size(2, this.timeSinceEpochSeconds_);
            }
            int n3;
            if (this.gyroscopeBias == null) {
                n3 = n;
            }
            else {
                n3 = n;
                if (this.gyroscopeBias.length > 0) {
                    final int n4 = this.gyroscopeBias.length * 8;
                    n3 = n + n4 + 1 + CodedOutputByteBufferNano.computeRawVarint32Size(n4);
                }
            }
            int n5;
            if (this.lensOffset == null) {
                n5 = n3;
            }
            else {
                n5 = n3;
                if (this.lensOffset.length > 0) {
                    final int n6 = this.lensOffset.length * 4;
                    n5 = n3 + n6 + 1 + CodedOutputByteBufferNano.computeRawVarint32Size(n6);
                }
            }
            int n7;
            if (this.lastGyroscopeSample == null) {
                n7 = n5;
            }
            else {
                n7 = n5;
                if (this.lastGyroscopeSample.length > 0) {
                    final int n8 = this.lastGyroscopeSample.length * 8;
                    n7 = n5 + n8 + 1 + CodedOutputByteBufferNano.computeRawVarint32Size(n8);
                }
            }
            if ((this.bitField0_ & 0x2) != 0x0) {
                n7 += CodedOutputByteBufferNano.computeDoubleSize(6, this.lastGyroscopeTimestamp_);
            }
            return n7;
        }
        
        public final double getLastGyroscopeTimestamp() {
            return this.lastGyroscopeTimestamp_;
        }
        
        public final long getTimeSinceEpochSeconds() {
            return this.timeSinceEpochSeconds_;
        }
        
        public final boolean hasLastGyroscopeTimestamp() {
            return (this.bitField0_ & 0x2) != 0x0;
        }
        
        public final boolean hasTimeSinceEpochSeconds() {
            return (this.bitField0_ & 0x1) != 0x0;
        }
        
        @Override
        public final TrackerState mergeFrom(final CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                final int tag = codedInputByteBufferNano.readTag();
                switch (tag) {
                    default: {
                        if (!super.storeUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                        continue;
                    }
                    case 0: {
                        return this;
                    }
                    case 9: {
                        final int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 9);
                        int i;
                        if (this.q != null) {
                            i = this.q.length;
                        }
                        else {
                            i = 0;
                        }
                        final double[] q = new double[repeatedFieldArrayLength + i];
                        if (i != 0) {
                            System.arraycopy(this.q, 0, q, 0, i);
                        }
                        while (i < q.length - 1) {
                            q[i] = codedInputByteBufferNano.readDouble();
                            codedInputByteBufferNano.readTag();
                            ++i;
                        }
                        q[i] = codedInputByteBufferNano.readDouble();
                        this.q = q;
                        continue;
                    }
                    case 10: {
                        final int rawVarint32 = codedInputByteBufferNano.readRawVarint32();
                        final int pushLimit = codedInputByteBufferNano.pushLimit(rawVarint32);
                        final int n = rawVarint32 / 8;
                        int j;
                        if (this.q != null) {
                            j = this.q.length;
                        }
                        else {
                            j = 0;
                        }
                        final double[] q2 = new double[n + j];
                        if (j != 0) {
                            System.arraycopy(this.q, 0, q2, 0, j);
                        }
                        while (j < q2.length) {
                            q2[j] = codedInputByteBufferNano.readDouble();
                            ++j;
                        }
                        this.q = q2;
                        codedInputByteBufferNano.popLimit(pushLimit);
                        continue;
                    }
                    case 16: {
                        this.timeSinceEpochSeconds_ = codedInputByteBufferNano.readInt64();
                        this.bitField0_ |= 0x1;
                        continue;
                    }
                    case 25: {
                        final int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 25);
                        int k;
                        if (this.gyroscopeBias != null) {
                            k = this.gyroscopeBias.length;
                        }
                        else {
                            k = 0;
                        }
                        final double[] gyroscopeBias = new double[repeatedFieldArrayLength2 + k];
                        if (k != 0) {
                            System.arraycopy(this.gyroscopeBias, 0, gyroscopeBias, 0, k);
                        }
                        while (k < gyroscopeBias.length - 1) {
                            gyroscopeBias[k] = codedInputByteBufferNano.readDouble();
                            codedInputByteBufferNano.readTag();
                            ++k;
                        }
                        gyroscopeBias[k] = codedInputByteBufferNano.readDouble();
                        this.gyroscopeBias = gyroscopeBias;
                        continue;
                    }
                    case 26: {
                        final int rawVarint33 = codedInputByteBufferNano.readRawVarint32();
                        final int pushLimit2 = codedInputByteBufferNano.pushLimit(rawVarint33);
                        final int n2 = rawVarint33 / 8;
                        int l;
                        if (this.gyroscopeBias != null) {
                            l = this.gyroscopeBias.length;
                        }
                        else {
                            l = 0;
                        }
                        final double[] gyroscopeBias2 = new double[n2 + l];
                        if (l != 0) {
                            System.arraycopy(this.gyroscopeBias, 0, gyroscopeBias2, 0, l);
                        }
                        while (l < gyroscopeBias2.length) {
                            gyroscopeBias2[l] = codedInputByteBufferNano.readDouble();
                            ++l;
                        }
                        this.gyroscopeBias = gyroscopeBias2;
                        codedInputByteBufferNano.popLimit(pushLimit2);
                        continue;
                    }
                    case 37: {
                        final int repeatedFieldArrayLength3 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 37);
                        int length;
                        if (this.lensOffset != null) {
                            length = this.lensOffset.length;
                        }
                        else {
                            length = 0;
                        }
                        final float[] lensOffset = new float[repeatedFieldArrayLength3 + length];
                        if (length != 0) {
                            System.arraycopy(this.lensOffset, 0, lensOffset, 0, length);
                        }
                        while (length < lensOffset.length - 1) {
                            lensOffset[length] = codedInputByteBufferNano.readFloat();
                            codedInputByteBufferNano.readTag();
                            ++length;
                        }
                        lensOffset[length] = codedInputByteBufferNano.readFloat();
                        this.lensOffset = lensOffset;
                        continue;
                    }
                    case 34: {
                        final int rawVarint34 = codedInputByteBufferNano.readRawVarint32();
                        final int pushLimit3 = codedInputByteBufferNano.pushLimit(rawVarint34);
                        final int n3 = rawVarint34 / 4;
                        int length2;
                        if (this.lensOffset != null) {
                            length2 = this.lensOffset.length;
                        }
                        else {
                            length2 = 0;
                        }
                        final float[] lensOffset2 = new float[n3 + length2];
                        if (length2 != 0) {
                            System.arraycopy(this.lensOffset, 0, lensOffset2, 0, length2);
                        }
                        while (length2 < lensOffset2.length) {
                            lensOffset2[length2] = codedInputByteBufferNano.readFloat();
                            ++length2;
                        }
                        this.lensOffset = lensOffset2;
                        codedInputByteBufferNano.popLimit(pushLimit3);
                        continue;
                    }
                    case 41: {
                        final int repeatedFieldArrayLength4 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 41);
                        int length3;
                        if (this.lastGyroscopeSample != null) {
                            length3 = this.lastGyroscopeSample.length;
                        }
                        else {
                            length3 = 0;
                        }
                        final double[] lastGyroscopeSample = new double[repeatedFieldArrayLength4 + length3];
                        if (length3 != 0) {
                            System.arraycopy(this.lastGyroscopeSample, 0, lastGyroscopeSample, 0, length3);
                        }
                        while (length3 < lastGyroscopeSample.length - 1) {
                            lastGyroscopeSample[length3] = codedInputByteBufferNano.readDouble();
                            codedInputByteBufferNano.readTag();
                            ++length3;
                        }
                        lastGyroscopeSample[length3] = codedInputByteBufferNano.readDouble();
                        this.lastGyroscopeSample = lastGyroscopeSample;
                        continue;
                    }
                    case 42: {
                        final int rawVarint35 = codedInputByteBufferNano.readRawVarint32();
                        final int pushLimit4 = codedInputByteBufferNano.pushLimit(rawVarint35);
                        final int n4 = rawVarint35 / 8;
                        int length4;
                        if (this.lastGyroscopeSample != null) {
                            length4 = this.lastGyroscopeSample.length;
                        }
                        else {
                            length4 = 0;
                        }
                        final double[] lastGyroscopeSample2 = new double[n4 + length4];
                        if (length4 != 0) {
                            System.arraycopy(this.lastGyroscopeSample, 0, lastGyroscopeSample2, 0, length4);
                        }
                        while (length4 < lastGyroscopeSample2.length) {
                            lastGyroscopeSample2[length4] = codedInputByteBufferNano.readDouble();
                            ++length4;
                        }
                        this.lastGyroscopeSample = lastGyroscopeSample2;
                        codedInputByteBufferNano.popLimit(pushLimit4);
                        continue;
                    }
                    case 49: {
                        this.lastGyroscopeTimestamp_ = codedInputByteBufferNano.readDouble();
                        this.bitField0_ |= 0x2;
                        continue;
                    }
                }
            }
        }
        
        public final TrackerState setLastGyroscopeTimestamp(final double lastGyroscopeTimestamp_) {
            this.lastGyroscopeTimestamp_ = lastGyroscopeTimestamp_;
            this.bitField0_ |= 0x2;
            return this;
        }
        
        public final TrackerState setTimeSinceEpochSeconds(final long timeSinceEpochSeconds_) {
            this.timeSinceEpochSeconds_ = timeSinceEpochSeconds_;
            this.bitField0_ |= 0x1;
            return this;
        }
        
        @Override
        public final void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            final int n = 0;
            if (this.q != null && this.q.length > 0) {
                final int length = this.q.length;
                codedOutputByteBufferNano.writeRawVarint32(10);
                codedOutputByteBufferNano.writeRawVarint32(length * 8);
                for (int i = 0; i < this.q.length; ++i) {
                    codedOutputByteBufferNano.writeDoubleNoTag(this.q[i]);
                }
            }
            if ((this.bitField0_ & 0x1) != 0x0) {
                codedOutputByteBufferNano.writeInt64(2, this.timeSinceEpochSeconds_);
            }
            if (this.gyroscopeBias != null && this.gyroscopeBias.length > 0) {
                final int length2 = this.gyroscopeBias.length;
                codedOutputByteBufferNano.writeRawVarint32(26);
                codedOutputByteBufferNano.writeRawVarint32(length2 * 8);
                for (int j = 0; j < this.gyroscopeBias.length; ++j) {
                    codedOutputByteBufferNano.writeDoubleNoTag(this.gyroscopeBias[j]);
                }
            }
            if (this.lensOffset != null && this.lensOffset.length > 0) {
                final int length3 = this.lensOffset.length;
                codedOutputByteBufferNano.writeRawVarint32(34);
                codedOutputByteBufferNano.writeRawVarint32(length3 * 4);
                for (int k = 0; k < this.lensOffset.length; ++k) {
                    codedOutputByteBufferNano.writeFloatNoTag(this.lensOffset[k]);
                }
            }
            if (this.lastGyroscopeSample != null && this.lastGyroscopeSample.length > 0) {
                final int length4 = this.lastGyroscopeSample.length;
                codedOutputByteBufferNano.writeRawVarint32(42);
                codedOutputByteBufferNano.writeRawVarint32(length4 * 8);
                for (int l = n; l < this.lastGyroscopeSample.length; ++l) {
                    codedOutputByteBufferNano.writeDoubleNoTag(this.lastGyroscopeSample[l]);
                }
            }
            if ((this.bitField0_ & 0x2) != 0x0) {
                codedOutputByteBufferNano.writeDouble(6, this.lastGyroscopeTimestamp_);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
}
