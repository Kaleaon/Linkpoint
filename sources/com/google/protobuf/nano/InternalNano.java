package com.google.protobuf.nano;

import com.google.protobuf.nano.MapFactories;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public final class InternalNano {
    protected static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    public static final Object LAZY_INIT_LOCK = new Object();
    public static final int TYPE_BOOL = 8;
    public static final int TYPE_BYTES = 12;
    public static final int TYPE_DOUBLE = 1;
    public static final int TYPE_ENUM = 14;
    public static final int TYPE_FIXED32 = 7;
    public static final int TYPE_FIXED64 = 6;
    public static final int TYPE_FLOAT = 2;
    public static final int TYPE_GROUP = 10;
    public static final int TYPE_INT32 = 5;
    public static final int TYPE_INT64 = 3;
    public static final int TYPE_MESSAGE = 11;
    public static final int TYPE_SFIXED32 = 15;
    public static final int TYPE_SFIXED64 = 16;
    public static final int TYPE_SINT32 = 17;
    public static final int TYPE_SINT64 = 18;
    public static final int TYPE_STRING = 9;
    public static final int TYPE_UINT32 = 13;
    public static final int TYPE_UINT64 = 4;
    protected static final Charset UTF_8 = Charset.forName("UTF-8");

    private InternalNano() {
    }

    public static byte[] bytesDefaultValue(String str) {
        return str.getBytes(ISO_8859_1);
    }

    public static void cloneUnknownFieldData(ExtendableMessageNano extendableMessageNano, ExtendableMessageNano extendableMessageNano2) {
        if (extendableMessageNano.unknownFieldData != null) {
            extendableMessageNano2.unknownFieldData = extendableMessageNano.unknownFieldData.clone();
        }
    }

    public static <K, V> int computeMapFieldSize(Map<K, V> map, int i, int i2, int i3) {
        int i4 = 0;
        int computeTagSize = CodedOutputByteBufferNano.computeTagSize(i);
        Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        while (true) {
            int i5 = i4;
            if (!it.hasNext()) {
                return i5;
            }
            Map.Entry next = it.next();
            Object key = next.getKey();
            Object value = next.getValue();
            if (!(key == null || value == null)) {
                int computeFieldSize = CodedOutputByteBufferNano.computeFieldSize(2, i3, value) + CodedOutputByteBufferNano.computeFieldSize(1, i2, key);
                i4 = CodedOutputByteBufferNano.computeRawVarint32Size(computeFieldSize) + computeTagSize + computeFieldSize + i5;
            }
        }
        throw new IllegalStateException("keys and values in maps cannot be null");
    }

    public static byte[] copyFromUtf8(String str) {
        return str.getBytes(UTF_8);
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0036  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static <K, V> boolean equals(java.util.Map<K, V> r5, java.util.Map<K, V> r6) {
        /*
            r2 = 1
            r1 = 0
            if (r5 == r6) goto L_0x0021
            if (r5 == 0) goto L_0x0022
            if (r6 == 0) goto L_0x002c
            int r0 = r5.size()
            int r3 = r6.size()
            if (r0 != r3) goto L_0x0035
            java.util.Set r0 = r5.entrySet()
            java.util.Iterator r3 = r0.iterator()
        L_0x001a:
            boolean r0 = r3.hasNext()
            if (r0 != 0) goto L_0x0036
            return r2
        L_0x0021:
            return r2
        L_0x0022:
            int r0 = r6.size()
            if (r0 == 0) goto L_0x002a
            r0 = r1
        L_0x0029:
            return r0
        L_0x002a:
            r0 = r2
            goto L_0x0029
        L_0x002c:
            int r0 = r5.size()
            if (r0 == 0) goto L_0x0033
        L_0x0032:
            return r1
        L_0x0033:
            r1 = r2
            goto L_0x0032
        L_0x0035:
            return r1
        L_0x0036:
            java.lang.Object r0 = r3.next()
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0
            java.lang.Object r4 = r0.getKey()
            boolean r4 = r6.containsKey(r4)
            if (r4 == 0) goto L_0x0059
            java.lang.Object r4 = r0.getValue()
            java.lang.Object r0 = r0.getKey()
            java.lang.Object r0 = r6.get(r0)
            boolean r0 = equalsMapValue(r4, r0)
            if (r0 != 0) goto L_0x001a
            return r1
        L_0x0059:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.nano.InternalNano.equals(java.util.Map, java.util.Map):boolean");
    }

    public static boolean equals(double[] dArr, double[] dArr2) {
        return (dArr == null || dArr.length == 0) ? dArr2 == null || dArr2.length == 0 : Arrays.equals(dArr, dArr2);
    }

    public static boolean equals(float[] fArr, float[] fArr2) {
        return (fArr == null || fArr.length == 0) ? fArr2 == null || fArr2.length == 0 : Arrays.equals(fArr, fArr2);
    }

    public static boolean equals(int[] iArr, int[] iArr2) {
        return (iArr == null || iArr.length == 0) ? iArr2 == null || iArr2.length == 0 : Arrays.equals(iArr, iArr2);
    }

    public static boolean equals(long[] jArr, long[] jArr2) {
        return (jArr == null || jArr.length == 0) ? jArr2 == null || jArr2.length == 0 : Arrays.equals(jArr, jArr2);
    }

    public static boolean equals(Object[] objArr, Object[] objArr2) {
        int length = objArr != null ? objArr.length : 0;
        int length2 = objArr2 != null ? objArr2.length : 0;
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i2 < length && objArr[i2] == null) {
                i2++;
            } else {
                int i3 = i;
                while (i3 < length2 && objArr2[i3] == null) {
                    i3++;
                }
                boolean z = i2 >= length;
                boolean z2 = i3 >= length2;
                if (z && z2) {
                    return true;
                }
                if (z != z2 || !objArr[i2].equals(objArr2[i3])) {
                    return false;
                }
                i = i3 + 1;
                i2++;
            }
        }
    }

    public static boolean equals(boolean[] zArr, boolean[] zArr2) {
        return (zArr == null || zArr.length == 0) ? zArr2 == null || zArr2.length == 0 : Arrays.equals(zArr, zArr2);
    }

    public static boolean equals(byte[][] bArr, byte[][] bArr2) {
        int length = bArr != null ? bArr.length : 0;
        int length2 = bArr2 != null ? bArr2.length : 0;
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i2 < length && bArr[i2] == null) {
                i2++;
            } else {
                int i3 = i;
                while (i3 < length2 && bArr2[i3] == null) {
                    i3++;
                }
                boolean z = i2 >= length;
                boolean z2 = i3 >= length2;
                if (z && z2) {
                    return true;
                }
                if (z != z2 || !Arrays.equals(bArr[i2], bArr2[i3])) {
                    return false;
                }
                i = i3 + 1;
                i2++;
            }
        }
    }

    private static boolean equalsMapValue(Object obj, Object obj2) {
        if (obj != null && obj2 != null) {
            return ((obj instanceof byte[]) && (obj2 instanceof byte[])) ? Arrays.equals((byte[]) obj, (byte[]) obj2) : obj.equals(obj2);
        }
        throw new IllegalStateException("keys and values in maps cannot be null");
    }

    public static <K, V> int hashCode(Map<K, V> map) {
        int i = 0;
        if (map == null) {
            return 0;
        }
        Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            Map.Entry next = it.next();
            i = (hashCodeForMap(next.getValue()) ^ hashCodeForMap(next.getKey())) + i2;
        }
    }

    public static int hashCode(double[] dArr) {
        if (dArr == null || dArr.length == 0) {
            return 0;
        }
        return Arrays.hashCode(dArr);
    }

    public static int hashCode(float[] fArr) {
        if (fArr == null || fArr.length == 0) {
            return 0;
        }
        return Arrays.hashCode(fArr);
    }

    public static int hashCode(int[] iArr) {
        if (iArr == null || iArr.length == 0) {
            return 0;
        }
        return Arrays.hashCode(iArr);
    }

    public static int hashCode(long[] jArr) {
        if (jArr == null || jArr.length == 0) {
            return 0;
        }
        return Arrays.hashCode(jArr);
    }

    public static int hashCode(Object[] objArr) {
        int i = 0;
        int length = objArr != null ? objArr.length : 0;
        for (int i2 = 0; i2 < length; i2++) {
            Object obj = objArr[i2];
            if (obj != null) {
                i = (i * 31) + obj.hashCode();
            }
        }
        return i;
    }

    public static int hashCode(boolean[] zArr) {
        if (zArr == null || zArr.length == 0) {
            return 0;
        }
        return Arrays.hashCode(zArr);
    }

    public static int hashCode(byte[][] bArr) {
        int i = 0;
        int length = bArr != null ? bArr.length : 0;
        for (int i2 = 0; i2 < length; i2++) {
            byte[] bArr2 = bArr[i2];
            if (bArr2 != null) {
                i = (i * 31) + Arrays.hashCode(bArr2);
            }
        }
        return i;
    }

    private static int hashCodeForMap(Object obj) {
        return !(obj instanceof byte[]) ? obj.hashCode() : Arrays.hashCode((byte[]) obj);
    }

    public static final <K, V> Map<K, V> mergeMapEntry(CodedInputByteBufferNano codedInputByteBufferNano, Map<K, V> map, MapFactories.MapFactory mapFactory, int i, int i2, V v, int i3, int i4) throws IOException {
        Map<K, V> forMap = mapFactory.forMap(map);
        int pushLimit = codedInputByteBufferNano.pushLimit(codedInputByteBufferNano.readRawVarint32());
        Object obj = null;
        V v2 = v;
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                break;
            }
            if (readTag == i3) {
                obj = codedInputByteBufferNano.readPrimitiveField(i);
            } else if (readTag != i4) {
                if (!codedInputByteBufferNano.skipField(readTag)) {
                    break;
                }
            } else if (i2 != 11) {
                v2 = codedInputByteBufferNano.readPrimitiveField(i2);
            } else {
                codedInputByteBufferNano.readMessage((MessageNano) v2);
            }
            obj = obj;
        }
        codedInputByteBufferNano.checkLastTagWas(0);
        codedInputByteBufferNano.popLimit(pushLimit);
        if (obj == null) {
            obj = primitiveDefaultValue(i);
        }
        if (v2 == null) {
            v2 = primitiveDefaultValue(i2);
        }
        forMap.put(obj, v2);
        return forMap;
    }

    private static Object primitiveDefaultValue(int i) {
        switch (i) {
            case 1:
                return Double.valueOf(0.0d);
            case 2:
                return Float.valueOf(0.0f);
            case 3:
            case 4:
            case 6:
            case 16:
            case 18:
                return 0L;
            case 5:
            case 7:
            case 13:
            case 14:
            case 15:
            case 17:
                return 0;
            case 8:
                return Boolean.FALSE;
            case 9:
                return "";
            case 12:
                return WireFormatNano.EMPTY_BYTES;
            default:
                throw new IllegalArgumentException("Type: " + i + " is not a primitive type.");
        }
    }

    public static <K, V> void serializeMapField(CodedOutputByteBufferNano codedOutputByteBufferNano, Map<K, V> map, int i, int i2, int i3) throws IOException {
        for (Map.Entry next : map.entrySet()) {
            Object key = next.getKey();
            Object value = next.getValue();
            if (key == null || value == null) {
                throw new IllegalStateException("keys and values in maps cannot be null");
            }
            codedOutputByteBufferNano.writeTag(i, 2);
            codedOutputByteBufferNano.writeRawVarint32(CodedOutputByteBufferNano.computeFieldSize(1, i2, key) + CodedOutputByteBufferNano.computeFieldSize(2, i3, value));
            codedOutputByteBufferNano.writeField(1, i2, key);
            codedOutputByteBufferNano.writeField(2, i3, value);
        }
    }

    public static String stringDefaultValue(String str) {
        return new String(str.getBytes(ISO_8859_1), UTF_8);
    }
}
