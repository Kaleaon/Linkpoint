// 
// Decompiled by Procyon v0.6.0
// 

package com.google.protobuf.nano;

import java.io.IOException;

public abstract class ExtendableMessageNano<M extends ExtendableMessageNano<M>> extends MessageNano
{
    protected FieldArray unknownFieldData;
    
    @Override
    public M clone() throws CloneNotSupportedException {
        final ExtendableMessageNano extendableMessageNano = (ExtendableMessageNano)super.clone();
        InternalNano.cloneUnknownFieldData(this, extendableMessageNano);
        return (M)extendableMessageNano;
    }
    
    @Override
    protected int computeSerializedSize() {
        int i = 0;
        int n = 0;
        if (this.unknownFieldData != null) {
            n = 0;
            while (i < this.unknownFieldData.size()) {
                n += this.unknownFieldData.dataAt(i).computeSerializedSize();
                ++i;
            }
        }
        return n;
    }
    
    public final <T> T getExtension(final Extension<M, T> extension) {
        T value = null;
        if (this.unknownFieldData != null) {
            final FieldData value2 = this.unknownFieldData.get(WireFormatNano.getTagFieldNumber(extension.tag));
            if (value2 != null) {
                value = value2.getValue(extension);
            }
            return value;
        }
        return null;
    }
    
    public final boolean hasExtension(final Extension<M, ?> extension) {
        boolean b = false;
        if (this.unknownFieldData != null) {
            if (this.unknownFieldData.get(WireFormatNano.getTagFieldNumber(extension.tag)) != null) {
                b = true;
            }
            return b;
        }
        return false;
    }
    
    public final <T> M setExtension(final Extension<M, T> extension, final T t) {
        FieldData value = null;
        final int tagFieldNumber = WireFormatNano.getTagFieldNumber(extension.tag);
        if (t != null) {
            if (this.unknownFieldData != null) {
                value = this.unknownFieldData.get(tagFieldNumber);
            }
            else {
                this.unknownFieldData = new FieldArray();
            }
            if (value != null) {
                value.setValue(extension, t);
            }
            else {
                this.unknownFieldData.put(tagFieldNumber, new FieldData((Extension<?, T>)extension, (T)t));
            }
        }
        else if (this.unknownFieldData != null) {
            this.unknownFieldData.remove(tagFieldNumber);
            if (this.unknownFieldData.isEmpty()) {
                this.unknownFieldData = null;
            }
        }
        return (M)this;
    }
    
    protected final boolean storeUnknownField(final CodedInputByteBufferNano codedInputByteBufferNano, final int n) throws IOException {
        final FieldData fieldData = null;
        final int position = codedInputByteBufferNano.getPosition();
        if (codedInputByteBufferNano.skipField(n)) {
            final int tagFieldNumber = WireFormatNano.getTagFieldNumber(n);
            final UnknownFieldData unknownFieldData = new UnknownFieldData(n, codedInputByteBufferNano.getData(position, codedInputByteBufferNano.getPosition() - position));
            FieldData value;
            if (this.unknownFieldData != null) {
                value = this.unknownFieldData.get(tagFieldNumber);
            }
            else {
                this.unknownFieldData = new FieldArray();
                value = fieldData;
            }
            if (value == null) {
                value = new FieldData();
                this.unknownFieldData.put(tagFieldNumber, value);
            }
            value.addUnknownField(unknownFieldData);
            return true;
        }
        return false;
    }
    
    @Override
    public void writeTo(final CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (this.unknownFieldData != null) {
            for (int i = 0; i < this.unknownFieldData.size(); ++i) {
                this.unknownFieldData.dataAt(i).writeTo(codedOutputByteBufferNano);
            }
        }
    }
}
