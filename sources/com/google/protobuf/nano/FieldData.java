package com.google.protobuf.nano;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class FieldData implements Cloneable {
    private Extension<?, ?> cachedExtension;
    private List<UnknownFieldData> unknownFieldData;
    private Object value;

    FieldData() {
        this.unknownFieldData = new ArrayList();
    }

    <T> FieldData(Extension<?, T> extension, T t) {
        this.cachedExtension = extension;
        this.value = t;
    }

    private byte[] toByteArray() throws IOException {
        byte[] bArr = new byte[computeSerializedSize()];
        writeTo(CodedOutputByteBufferNano.newInstance(bArr));
        return bArr;
    }

    /* access modifiers changed from: package-private */
    public void addUnknownField(UnknownFieldData unknownFieldData2) {
        this.unknownFieldData.add(unknownFieldData2);
    }

    public final FieldData clone() {
        FieldData fieldData = new FieldData();
        try {
            fieldData.cachedExtension = this.cachedExtension;
            if (this.unknownFieldData != null) {
                fieldData.unknownFieldData.addAll(this.unknownFieldData);
            } else {
                fieldData.unknownFieldData = null;
            }
            if (this.value != null) {
                if (this.value instanceof MessageNano) {
                    fieldData.value = ((MessageNano) this.value).clone();
                } else if (this.value instanceof byte[]) {
                    fieldData.value = ((byte[]) this.value).clone();
                } else if (this.value instanceof byte[][]) {
                    byte[][] bArr = (byte[][]) this.value;
                    byte[][] bArr2 = new byte[bArr.length][];
                    fieldData.value = bArr2;
                    for (int i = 0; i < bArr.length; i++) {
                        bArr2[i] = (byte[]) bArr[i].clone();
                    }
                } else if (this.value instanceof boolean[]) {
                    fieldData.value = ((boolean[]) this.value).clone();
                } else if (this.value instanceof int[]) {
                    fieldData.value = ((int[]) this.value).clone();
                } else if (this.value instanceof long[]) {
                    fieldData.value = ((long[]) this.value).clone();
                } else if (this.value instanceof float[]) {
                    fieldData.value = ((float[]) this.value).clone();
                } else if (this.value instanceof double[]) {
                    fieldData.value = ((double[]) this.value).clone();
                } else if (this.value instanceof MessageNano[]) {
                    MessageNano[] messageNanoArr = (MessageNano[]) this.value;
                    MessageNano[] messageNanoArr2 = new MessageNano[messageNanoArr.length];
                    fieldData.value = messageNanoArr2;
                    for (int i2 = 0; i2 < messageNanoArr.length; i2++) {
                        messageNanoArr2[i2] = messageNanoArr[i2].clone();
                    }
                }
            }
            return fieldData;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    /* access modifiers changed from: package-private */
    public int computeSerializedSize() {
        int i = 0;
        if (this.value != null) {
            return this.cachedExtension.computeSerializedSize(this.value);
        }
        Iterator<UnknownFieldData> it = this.unknownFieldData.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = it.next().computeSerializedSize() + i2;
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FieldData)) {
            return false;
        }
        FieldData fieldData = (FieldData) obj;
        if (this.value == null || fieldData.value == null) {
            if (this.unknownFieldData != null && fieldData.unknownFieldData != null) {
                return this.unknownFieldData.equals(fieldData.unknownFieldData);
            }
            try {
                return Arrays.equals(toByteArray(), fieldData.toByteArray());
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else if (this.cachedExtension == fieldData.cachedExtension) {
            return this.cachedExtension.clazz.isArray() ? !(this.value instanceof byte[]) ? !(this.value instanceof int[]) ? !(this.value instanceof long[]) ? !(this.value instanceof float[]) ? !(this.value instanceof double[]) ? !(this.value instanceof boolean[]) ? Arrays.deepEquals((Object[]) this.value, (Object[]) fieldData.value) : Arrays.equals((boolean[]) this.value, (boolean[]) fieldData.value) : Arrays.equals((double[]) this.value, (double[]) fieldData.value) : Arrays.equals((float[]) this.value, (float[]) fieldData.value) : Arrays.equals((long[]) this.value, (long[]) fieldData.value) : Arrays.equals((int[]) this.value, (int[]) fieldData.value) : Arrays.equals((byte[]) this.value, (byte[]) fieldData.value) : this.value.equals(fieldData.value);
        } else {
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public UnknownFieldData getUnknownField(int i) {
        if (this.unknownFieldData == null || i >= this.unknownFieldData.size()) {
            return null;
        }
        return this.unknownFieldData.get(i);
    }

    /* access modifiers changed from: package-private */
    public int getUnknownFieldSize() {
        if (this.unknownFieldData != null) {
            return this.unknownFieldData.size();
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public <T> T getValue(Extension<?, T> extension) {
        if (this.value == null) {
            this.cachedExtension = extension;
            this.value = extension.getValueFrom(this.unknownFieldData);
            this.unknownFieldData = null;
        } else if (this.cachedExtension != extension) {
            throw new IllegalStateException("Tried to getExtension with a differernt Extension.");
        }
        return this.value;
    }

    public int hashCode() {
        try {
            return Arrays.hashCode(toByteArray()) + 527;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /* access modifiers changed from: package-private */
    public <T> void setValue(Extension<?, T> extension, T t) {
        this.cachedExtension = extension;
        this.value = t;
        this.unknownFieldData = null;
    }

    /* access modifiers changed from: package-private */
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (this.value == null) {
            for (UnknownFieldData writeTo : this.unknownFieldData) {
                writeTo.writeTo(codedOutputByteBufferNano);
            }
            return;
        }
        this.cachedExtension.writeTo(this.value, codedOutputByteBufferNano);
    }
}
