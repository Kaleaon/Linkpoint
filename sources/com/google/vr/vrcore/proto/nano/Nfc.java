package com.google.vr.vrcore.proto.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.ExtendableMessageNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import java.io.IOException;

public interface Nfc {

    public static final class NfcParams extends ExtendableMessageNano<NfcParams> implements Cloneable {
        private static volatile NfcParams[] _emptyArray;
        private int bitField0_;
        private int viewerId_;

        public NfcParams() {
            clear();
        }

        public static NfcParams[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new NfcParams[0];
                    }
                }
            }
            return _emptyArray;
        }

        public static NfcParams parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new NfcParams().mergeFrom(codedInputByteBufferNano);
        }

        public static NfcParams parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (NfcParams) MessageNano.mergeFrom(new NfcParams(), bArr);
        }

        public final NfcParams clear() {
            this.bitField0_ = 0;
            this.viewerId_ = 0;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public final NfcParams clearViewerId() {
            this.viewerId_ = 0;
            this.bitField0_ &= -2;
            return this;
        }

        public final NfcParams clone() {
            try {
                return (NfcParams) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        /* access modifiers changed from: protected */
        public final int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            return (this.bitField0_ & 1) == 0 ? computeSerializedSize : computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(1, this.viewerId_);
        }

        public final int getViewerId() {
            return this.viewerId_;
        }

        public final boolean hasViewerId() {
            return (this.bitField0_ & 1) != 0;
        }

        public final NfcParams mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                switch (readTag) {
                    case 0:
                        return this;
                    case 8:
                        this.viewerId_ = codedInputByteBufferNano.readInt32();
                        this.bitField0_ |= 1;
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

        public final NfcParams setViewerId(int i) {
            this.viewerId_ = i;
            this.bitField0_ |= 1;
            return this;
        }

        public final void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                codedOutputByteBufferNano.writeInt32(1, this.viewerId_);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
}
