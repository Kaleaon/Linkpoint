// 
// Decompiled by Procyon v0.6.0
// 

package com.google.protobuf.nano;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.nio.charset.Charset;

public final class InternalNano
{
    protected static final Charset ISO_8859_1;
    public static final Object LAZY_INIT_LOCK;
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
    protected static final Charset UTF_8;
    
    static {
        UTF_8 = Charset.forName("UTF-8");
        ISO_8859_1 = Charset.forName("ISO-8859-1");
        LAZY_INIT_LOCK = new Object();
    }
    
    private InternalNano() {
    }
    
    public static byte[] bytesDefaultValue(final String s) {
        return s.getBytes(InternalNano.ISO_8859_1);
    }
    
    public static void cloneUnknownFieldData(final ExtendableMessageNano extendableMessageNano, final ExtendableMessageNano extendableMessageNano2) {
        if (extendableMessageNano.unknownFieldData != null) {
            extendableMessageNano2.unknownFieldData = extendableMessageNano.unknownFieldData.clone();
        }
    }
    
    public static <K, V> int computeMapFieldSize(final Map<K, V> map, int n, final int n2, final int n3) {
        final int computeTagSize = CodedOutputByteBufferNano.computeTagSize(n);
        final Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        n = 0;
        while (iterator.hasNext()) {
            final Map.Entry<Object, V> entry = (Map.Entry<Object, V>)iterator.next();
            final Object key = entry.getKey();
            final V value = entry.getValue();
            if (key == null || value == null) {
                throw new IllegalStateException("keys and values in maps cannot be null");
            }
            final int n4 = CodedOutputByteBufferNano.computeFieldSize(2, n3, value) + CodedOutputByteBufferNano.computeFieldSize(1, n2, key);
            n += CodedOutputByteBufferNano.computeRawVarint32Size(n4) + (computeTagSize + n4);
        }
        return n;
    }
    
    public static byte[] copyFromUtf8(final String s) {
        return s.getBytes(InternalNano.UTF_8);
    }
    
    public static <K, V> boolean equals(final Map<K, V> map, final Map<K, V> map2) {
        boolean b = false;
        if (map == map2) {
            return true;
        }
        if (map == null) {
            return map2.size() == 0;
        }
        if (map2 == null) {
            if (map.size() == 0) {
                b = true;
            }
            return b;
        }
        if (map.size() == map2.size()) {
            for (final Map.Entry<Object, V> entry : map.entrySet()) {
                if (!map2.containsKey(entry.getKey())) {
                    return false;
                }
                if (!equalsMapValue(entry.getValue(), map2.get(entry.getKey()))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public static boolean equals(final double[] a, final double[] a2) {
        boolean b = false;
        if (a != null && a.length != 0) {
            return Arrays.equals(a, a2);
        }
        if (a2 == null || a2.length == 0) {
            b = true;
        }
        return b;
    }
    
    public static boolean equals(final float[] a, final float[] a2) {
        boolean b = false;
        if (a != null && a.length != 0) {
            return Arrays.equals(a, a2);
        }
        if (a2 == null || a2.length == 0) {
            b = true;
        }
        return b;
    }
    
    public static boolean equals(final int[] a, final int[] a2) {
        boolean b = false;
        if (a != null && a.length != 0) {
            return Arrays.equals(a, a2);
        }
        if (a2 == null || a2.length == 0) {
            b = true;
        }
        return b;
    }
    
    public static boolean equals(final long[] a, final long[] a2) {
        boolean b = false;
        if (a != null && a.length != 0) {
            return Arrays.equals(a, a2);
        }
        if (a2 == null || a2.length == 0) {
            b = true;
        }
        return b;
    }
    
    public static boolean equals(final Object[] array, final Object[] array2) {
        int length;
        if (array != null) {
            length = array.length;
        }
        else {
            length = 0;
        }
        int length2;
        if (array2 != null) {
            length2 = array2.length;
        }
        else {
            length2 = 0;
        }
        int n = 0;
        int n2 = 0;
        while (true) {
            if (n2 < length && array[n2] == null) {
                ++n2;
            }
            else {
                while (n < length2 && array2[n] == null) {
                    ++n;
                }
                boolean b;
                if (n2 < length) {
                    b = false;
                }
                else {
                    b = true;
                }
                boolean b2;
                if (n < length2) {
                    b2 = false;
                }
                else {
                    b2 = true;
                }
                if (b && b2) {
                    return true;
                }
                if (b != b2) {
                    return false;
                }
                if (!array[n2].equals(array2[n])) {
                    return false;
                }
                ++n;
                ++n2;
            }
        }
    }
    
    public static boolean equals(final boolean[] a, final boolean[] a2) {
        boolean b = false;
        if (a != null && a.length != 0) {
            return Arrays.equals(a, a2);
        }
        if (a2 == null || a2.length == 0) {
            b = true;
        }
        return b;
    }
    
    public static boolean equals(final byte[][] array, final byte[][] array2) {
        int length;
        if (array != null) {
            length = array.length;
        }
        else {
            length = 0;
        }
        int length2;
        if (array2 != null) {
            length2 = array2.length;
        }
        else {
            length2 = 0;
        }
        int n = 0;
        int n2 = 0;
        while (true) {
            if (n2 < length && array[n2] == null) {
                ++n2;
            }
            else {
                while (n < length2 && array2[n] == null) {
                    ++n;
                }
                boolean b;
                if (n2 < length) {
                    b = false;
                }
                else {
                    b = true;
                }
                boolean b2;
                if (n < length2) {
                    b2 = false;
                }
                else {
                    b2 = true;
                }
                if (b && b2) {
                    return true;
                }
                if (b != b2) {
                    return false;
                }
                if (!Arrays.equals(array[n2], array2[n])) {
                    return false;
                }
                ++n;
                ++n2;
            }
        }
    }
    
    private static boolean equalsMapValue(final Object o, final Object obj) {
        if (o == null || obj == null) {
            throw new IllegalStateException("keys and values in maps cannot be null");
        }
        if (o instanceof byte[] && obj instanceof byte[]) {
            return Arrays.equals((byte[])o, (byte[])obj);
        }
        return o.equals(obj);
    }
    
    public static <K, V> int hashCode(final Map<K, V> map) {
        if (map != null) {
            final Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
            int n = 0;
            while (iterator.hasNext()) {
                final Map.Entry entry = iterator.next();
                n += (hashCodeForMap(entry.getValue()) ^ hashCodeForMap(entry.getKey()));
            }
            return n;
        }
        return 0;
    }
    
    public static int hashCode(final double[] a) {
        int hashCode = 0;
        if (a != null && a.length != 0) {
            hashCode = Arrays.hashCode(a);
        }
        return hashCode;
    }
    
    public static int hashCode(final float[] a) {
        int hashCode = 0;
        if (a != null && a.length != 0) {
            hashCode = Arrays.hashCode(a);
        }
        return hashCode;
    }
    
    public static int hashCode(final int[] a) {
        int hashCode = 0;
        if (a != null && a.length != 0) {
            hashCode = Arrays.hashCode(a);
        }
        return hashCode;
    }
    
    public static int hashCode(final long[] a) {
        int hashCode = 0;
        if (a != null && a.length != 0) {
            hashCode = Arrays.hashCode(a);
        }
        return hashCode;
    }
    
    public static int hashCode(final Object[] array) {
        int n = 0;
        if (array != null) {
            final int length = array.length;
        }
        else {
            final int length = 0;
        }
        for (final Object o : array) {
            if (o != null) {
                n = n * 31 + o.hashCode();
            }
        }
        return n;
    }
    
    public static int hashCode(final boolean[] a) {
        int hashCode = 0;
        if (a != null && a.length != 0) {
            hashCode = Arrays.hashCode(a);
        }
        return hashCode;
    }
    
    public static int hashCode(final byte[][] array) {
        int n = 0;
        if (array != null) {
            final int length = array.length;
        }
        else {
            final int length = 0;
        }
        for (final byte[] a : array) {
            if (a != null) {
                n = n * 31 + Arrays.hashCode(a);
            }
        }
        return n;
    }
    
    private static int hashCodeForMap(final Object o) {
        if (!(o instanceof byte[])) {
            return o.hashCode();
        }
        return Arrays.hashCode((byte[])o);
    }
    
    public static final <K, V> Map<K, V> mergeMapEntry(final CodedInputByteBufferNano codedInputByteBufferNano, final Map<K, V> map, final MapFactories.MapFactory mapFactory, final int n, final int n2, final V v, final int n3, final int n4) throws IOException {
        final Map<K, V> forMap = mapFactory.forMap(map);
        final int pushLimit = codedInputByteBufferNano.pushLimit(codedInputByteBufferNano.readRawVarint32());
        Object o = null;
        Object o2 = v;
        while (true) {
            final int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                break;
            }
            if (tag != n3) {
                if (tag != n4) {
                    if (codedInputByteBufferNano.skipField(tag)) {
                        continue;
                    }
                    break;
                }
                else if (n2 != 11) {
                    o2 = codedInputByteBufferNano.readPrimitiveField(n2);
                }
                else {
                    codedInputByteBufferNano.readMessage((MessageNano)o2);
                }
            }
            else {
                o = codedInputByteBufferNano.readPrimitiveField(n);
            }
        }
        codedInputByteBufferNano.checkLastTagWas(0);
        codedInputByteBufferNano.popLimit(pushLimit);
        if (o == null) {
            o = primitiveDefaultValue(n);
        }
        if (o2 == null) {
            o2 = primitiveDefaultValue(n2);
        }
        forMap.put((K)o, (V)o2);
        return forMap;
    }
    
    private static Object primitiveDefaultValue(final int i) {
        switch (i) {
            default: {
                throw new IllegalArgumentException("Type: " + i + " is not a primitive type.");
            }
            case 8: {
                return Boolean.FALSE;
            }
            case 12: {
                return WireFormatNano.EMPTY_BYTES;
            }
            case 9: {
                return "";
            }
            case 2: {
                return 0.0f;
            }
            case 1: {
                return 0.0;
            }
            case 5:
            case 7:
            case 13:
            case 14:
            case 15:
            case 17: {
                return 0;
            }
            case 3:
            case 4:
            case 6:
            case 16:
            case 18: {
                return 0L;
            }
        }
    }
    
    public static <K, V> void serializeMapField(final CodedOutputByteBufferNano codedOutputByteBufferNano, final Map<K, V> map, final int n, final int n2, final int n3) throws IOException {
        for (final Map.Entry<Object, V> entry : map.entrySet()) {
            final Object key = entry.getKey();
            final V value = entry.getValue();
            if (key == null || value == null) {
                throw new IllegalStateException("keys and values in maps cannot be null");
            }
            final int computeFieldSize = CodedOutputByteBufferNano.computeFieldSize(1, n2, key);
            final int computeFieldSize2 = CodedOutputByteBufferNano.computeFieldSize(2, n3, value);
            codedOutputByteBufferNano.writeTag(n, 2);
            codedOutputByteBufferNano.writeRawVarint32(computeFieldSize + computeFieldSize2);
            codedOutputByteBufferNano.writeField(1, n2, key);
            codedOutputByteBufferNano.writeField(2, n3, value);
        }
    }
    
    public static String stringDefaultValue(final String s) {
        return new String(s.getBytes(InternalNano.ISO_8859_1), InternalNano.UTF_8);
    }
}
