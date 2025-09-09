package com.google.vrtoolkit.cardboard.proto.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.ExtendableMessageNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public interface Session {

    public static final class TrackerState extends ExtendableMessageNano<TrackerState> implements Cloneable {
        private static volatile TrackerState[] _emptyArray;
        private int bitField0_;
        public double[] gyroscopeBias;
        public double[] lastGyroscopeSample;
        private double lastGyroscopeTimestamp_;
        public float[] lensOffset;
        public double[] q;
        private long timeSinceEpochSeconds_;

        public TrackerState() {
            clear();
        }

        public static TrackerState[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new TrackerState[0];
                    }
                }
            }
            return _emptyArray;
        }

        public static TrackerState parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new TrackerState().mergeFrom(codedInputByteBufferNano);
        }

        public static TrackerState parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (TrackerState) MessageNano.mergeFrom(new TrackerState(), bArr);
        }

        public final TrackerState clear() {
            this.bitField0_ = 0;
            this.q = WireFormatNano.EMPTY_DOUBLE_ARRAY;
            this.timeSinceEpochSeconds_ = 0;
            this.gyroscopeBias = WireFormatNano.EMPTY_DOUBLE_ARRAY;
            this.lensOffset = WireFormatNano.EMPTY_FLOAT_ARRAY;
            this.lastGyroscopeSample = WireFormatNano.EMPTY_DOUBLE_ARRAY;
            this.lastGyroscopeTimestamp_ = 0.0d;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public final TrackerState clearLastGyroscopeTimestamp() {
            this.lastGyroscopeTimestamp_ = 0.0d;
            this.bitField0_ &= -3;
            return this;
        }

        public final TrackerState clearTimeSinceEpochSeconds() {
            this.timeSinceEpochSeconds_ = 0;
            this.bitField0_ &= -2;
            return this;
        }

        public final TrackerState clone() {
            try {
                TrackerState trackerState = (TrackerState) super.clone();
                if (this.q != null && this.q.length > 0) {
                    trackerState.q = (double[]) this.q.clone();
                }
                if (this.gyroscopeBias != null && this.gyroscopeBias.length > 0) {
                    trackerState.gyroscopeBias = (double[]) this.gyroscopeBias.clone();
                }
                if (this.lensOffset != null && this.lensOffset.length > 0) {
                    trackerState.lensOffset = (float[]) this.lensOffset.clone();
                }
                if (this.lastGyroscopeSample != null && this.lastGyroscopeSample.length > 0) {
                    trackerState.lastGyroscopeSample = (double[]) this.lastGyroscopeSample.clone();
                }
                return trackerState;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        /* access modifiers changed from: protected */
        public final int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.q != null && this.q.length > 0) {
                int length = this.q.length * 8;
                computeSerializedSize = computeSerializedSize + length + 1 + CodedOutputByteBufferNano.computeRawVarint32Size(length);
            }
            if ((this.bitField0_ & 1) != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(2, this.timeSinceEpochSeconds_);
            }
            if (this.gyroscopeBias != null && this.gyroscopeBias.length > 0) {
                int length2 = this.gyroscopeBias.length * 8;
                computeSerializedSize = computeSerializedSize + length2 + 1 + CodedOutputByteBufferNano.computeRawVarint32Size(length2);
            }
            if (this.lensOffset != null && this.lensOffset.length > 0) {
                int length3 = this.lensOffset.length * 4;
                computeSerializedSize = computeSerializedSize + length3 + 1 + CodedOutputByteBufferNano.computeRawVarint32Size(length3);
            }
            if (this.lastGyroscopeSample != null && this.lastGyroscopeSample.length > 0) {
                int length4 = this.lastGyroscopeSample.length * 8;
                computeSerializedSize = computeSerializedSize + length4 + 1 + CodedOutputByteBufferNano.computeRawVarint32Size(length4);
            }
            return (this.bitField0_ & 2) == 0 ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeDoubleSize(6, this.lastGyroscopeTimestamp_);
        }

        public final double getLastGyroscopeTimestamp() {
            return this.lastGyroscopeTimestamp_;
        }

        public final long getTimeSinceEpochSeconds() {
            return this.timeSinceEpochSeconds_;
        }

        public final boolean hasLastGyroscopeTimestamp() {
            return (this.bitField0_ & 2) != 0;
        }

        public final boolean hasTimeSinceEpochSeconds() {
            return (this.bitField0_ & 1) != 0;
        }

        public final TrackerState mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                switch (readTag) {
                    case 0:
                        return this;
                    case 9:
                        int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 9);
                        int length = this.q != null ? this.q.length : 0;
                        double[] dArr = new double[(repeatedFieldArrayLength + length)];
                        if (length != 0) {
                            System.arraycopy(this.q, 0, dArr, 0, length);
                        }
                        while (length < dArr.length - 1) {
                            dArr[length] = codedInputByteBufferNano.readDouble();
                            codedInputByteBufferNano.readTag();
                            length++;
                        }
                        dArr[length] = codedInputByteBufferNano.readDouble();
                        this.q = dArr;
                        break;
                    case 10:
                        int readRawVarint32 = codedInputByteBufferNano.readRawVarint32();
                        int pushLimit = codedInputByteBufferNano.pushLimit(readRawVarint32);
                        int i = readRawVarint32 / 8;
                        int length2 = this.q != null ? this.q.length : 0;
                        double[] dArr2 = new double[(i + length2)];
                        if (length2 != 0) {
                            System.arraycopy(this.q, 0, dArr2, 0, length2);
                        }
                        while (length2 < dArr2.length) {
                            dArr2[length2] = codedInputByteBufferNano.readDouble();
                            length2++;
                        }
                        this.q = dArr2;
                        codedInputByteBufferNano.popLimit(pushLimit);
                        break;
                    case 16:
                        this.timeSinceEpochSeconds_ = codedInputByteBufferNano.readInt64();
                        this.bitField0_ |= 1;
                        break;
                    case 25:
                        int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 25);
                        int length3 = this.gyroscopeBias != null ? this.gyroscopeBias.length : 0;
                        double[] dArr3 = new double[(repeatedFieldArrayLength2 + length3)];
                        if (length3 != 0) {
                            System.arraycopy(this.gyroscopeBias, 0, dArr3, 0, length3);
                        }
                        while (length3 < dArr3.length - 1) {
                            dArr3[length3] = codedInputByteBufferNano.readDouble();
                            codedInputByteBufferNano.readTag();
                            length3++;
                        }
                        dArr3[length3] = codedInputByteBufferNano.readDouble();
                        this.gyroscopeBias = dArr3;
                        break;
                    case 26:
                        int readRawVarint322 = codedInputByteBufferNano.readRawVarint32();
                        int pushLimit2 = codedInputByteBufferNano.pushLimit(readRawVarint322);
                        int i2 = readRawVarint322 / 8;
                        int length4 = this.gyroscopeBias != null ? this.gyroscopeBias.length : 0;
                        double[] dArr4 = new double[(i2 + length4)];
                        if (length4 != 0) {
                            System.arraycopy(this.gyroscopeBias, 0, dArr4, 0, length4);
                        }
                        while (length4 < dArr4.length) {
                            dArr4[length4] = codedInputByteBufferNano.readDouble();
                            length4++;
                        }
                        this.gyroscopeBias = dArr4;
                        codedInputByteBufferNano.popLimit(pushLimit2);
                        break;
                    case 34:
                        int readRawVarint323 = codedInputByteBufferNano.readRawVarint32();
                        int pushLimit3 = codedInputByteBufferNano.pushLimit(readRawVarint323);
                        int i3 = readRawVarint323 / 4;
                        int length5 = this.lensOffset != null ? this.lensOffset.length : 0;
                        float[] fArr = new float[(i3 + length5)];
                        if (length5 != 0) {
                            System.arraycopy(this.lensOffset, 0, fArr, 0, length5);
                        }
                        while (length5 < fArr.length) {
                            fArr[length5] = codedInputByteBufferNano.readFloat();
                            length5++;
                        }
                        this.lensOffset = fArr;
                        codedInputByteBufferNano.popLimit(pushLimit3);
                        break;
                    case 37:
                        int repeatedFieldArrayLength3 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 37);
                        int length6 = this.lensOffset != null ? this.lensOffset.length : 0;
                        float[] fArr2 = new float[(repeatedFieldArrayLength3 + length6)];
                        if (length6 != 0) {
                            System.arraycopy(this.lensOffset, 0, fArr2, 0, length6);
                        }
                        while (length6 < fArr2.length - 1) {
                            fArr2[length6] = codedInputByteBufferNano.readFloat();
                            codedInputByteBufferNano.readTag();
                            length6++;
                        }
                        fArr2[length6] = codedInputByteBufferNano.readFloat();
                        this.lensOffset = fArr2;
                        break;
                    case 41:
                        int repeatedFieldArrayLength4 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 41);
                        int length7 = this.lastGyroscopeSample != null ? this.lastGyroscopeSample.length : 0;
                        double[] dArr5 = new double[(repeatedFieldArrayLength4 + length7)];
                        if (length7 != 0) {
                            System.arraycopy(this.lastGyroscopeSample, 0, dArr5, 0, length7);
                        }
                        while (length7 < dArr5.length - 1) {
                            dArr5[length7] = codedInputByteBufferNano.readDouble();
                            codedInputByteBufferNano.readTag();
                            length7++;
                        }
                        dArr5[length7] = codedInputByteBufferNano.readDouble();
                        this.lastGyroscopeSample = dArr5;
                        break;
                    case 42:
                        int readRawVarint324 = codedInputByteBufferNano.readRawVarint32();
                        int pushLimit4 = codedInputByteBufferNano.pushLimit(readRawVarint324);
                        int i4 = readRawVarint324 / 8;
                        int length8 = this.lastGyroscopeSample != null ? this.lastGyroscopeSample.length : 0;
                        double[] dArr6 = new double[(i4 + length8)];
                        if (length8 != 0) {
                            System.arraycopy(this.lastGyroscopeSample, 0, dArr6, 0, length8);
                        }
                        while (length8 < dArr6.length) {
                            dArr6[length8] = codedInputByteBufferNano.readDouble();
                            length8++;
                        }
                        this.lastGyroscopeSample = dArr6;
                        codedInputByteBufferNano.popLimit(pushLimit4);
                        break;
                    case 49:
                        this.lastGyroscopeTimestamp_ = codedInputByteBufferNano.readDouble();
                        this.bitField0_ |= 2;
                        break;
                    default:
                        if (super.storeUnknownField(codedInputByteBufferNano, readTag)) {
                            break;
                        } else {
                            return this;
                        }
                }
            }
        }

        public final TrackerState setLastGyroscopeTimestamp(double d) {
            this.lastGyroscopeTimestamp_ = d;
            this.bitField0_ |= 2;
            return this;
        }

        public final TrackerState setTimeSinceEpochSeconds(long j) {
            this.timeSinceEpochSeconds_ = j;
            this.bitField0_ |= 1;
            return this;
        }

        public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.q != null && this.q.length > 0) {
                codedOutputByteBufferNano.writeRawVarint32(10);
                codedOutputByteBufferNano.writeRawVarint32(this.q.length * 8);
                for (double writeDoubleNoTag : this.q) {
                    codedOutputByteBufferNano.writeDoubleNoTag(writeDoubleNoTag);
                }
            }
            if ((this.bitField0_ & 1) != 0) {
                codedOutputByteBufferNano.writeInt64(2, this.timeSinceEpochSeconds_);
            }
            if (this.gyroscopeBias != null && this.gyroscopeBias.length > 0) {
                codedOutputByteBufferNano.writeRawVarint32(26);
                codedOutputByteBufferNano.writeRawVarint32(this.gyroscopeBias.length * 8);
                for (double writeDoubleNoTag2 : this.gyroscopeBias) {
                    codedOutputByteBufferNano.writeDoubleNoTag(writeDoubleNoTag2);
                }
            }
            if (this.lensOffset != null && this.lensOffset.length > 0) {
                codedOutputByteBufferNano.writeRawVarint32(34);
                codedOutputByteBufferNano.writeRawVarint32(this.lensOffset.length * 4);
                for (float writeFloatNoTag : this.lensOffset) {
                    codedOutputByteBufferNano.writeFloatNoTag(writeFloatNoTag);
                }
            }
            if (this.lastGyroscopeSample != null && this.lastGyroscopeSample.length > 0) {
                codedOutputByteBufferNano.writeRawVarint32(42);
                codedOutputByteBufferNano.writeRawVarint32(this.lastGyroscopeSample.length * 8);
                for (double writeDoubleNoTag3 : this.lastGyroscopeSample) {
                    codedOutputByteBufferNano.writeDoubleNoTag(writeDoubleNoTag3);
                }
            }
            if ((this.bitField0_ & 2) != 0) {
                codedOutputByteBufferNano.writeDouble(6, this.lastGyroscopeTimestamp_);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
}
