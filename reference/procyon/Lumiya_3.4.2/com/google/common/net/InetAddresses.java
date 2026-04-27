// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.net;

import com.google.common.base.MoreObjects;
import javax.annotation.Nullable;
import com.google.common.hash.Hashing;
import java.nio.ByteBuffer;
import java.net.Inet6Address;
import com.google.common.primitives.Ints;
import java.util.Locale;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import com.google.common.io.ByteStreams;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.Inet4Address;
import com.google.common.annotations.Beta;

@Beta
public final class InetAddresses
{
    private static final Inet4Address ANY4;
    private static final int IPV4_PART_COUNT = 4;
    private static final int IPV6_PART_COUNT = 8;
    private static final Inet4Address LOOPBACK4;
    
    static {
        LOOPBACK4 = (Inet4Address)forString("127.0.0.1");
        ANY4 = (Inet4Address)forString("0.0.0.0");
    }
    
    private InetAddresses() {
    }
    
    private static InetAddress bytesToInetAddress(final byte[] addr) {
        try {
            return InetAddress.getByAddress(addr);
        }
        catch (final UnknownHostException detailMessage) {
            throw new AssertionError((Object)detailMessage);
        }
    }
    
    public static int coerceToInteger(final InetAddress inetAddress) {
        return ByteStreams.newDataInput(getCoercedIPv4Address(inetAddress).getAddress()).readInt();
    }
    
    private static void compressLongestRunOfZeroes(final int[] a) {
        int i = 0;
        int n = -1;
        int n2 = -1;
        int fromIndex = -1;
        while (i < a.length + 1) {
            int n3;
            int n4;
            int n5;
            if (i < a.length && a[i] == 0) {
                n3 = n;
                n4 = n2;
                n5 = fromIndex;
                if (n < 0) {
                    n3 = i;
                    n4 = n2;
                    n5 = fromIndex;
                }
            }
            else if (n < 0) {
                n5 = fromIndex;
                n4 = n2;
                n3 = n;
            }
            else {
                final int n6 = i - n;
                if (n6 > n2) {
                    n2 = n6;
                    fromIndex = n;
                }
                n3 = -1;
                n4 = n2;
                n5 = fromIndex;
            }
            ++i;
            n = n3;
            n2 = n4;
            fromIndex = n5;
        }
        if (n2 >= 2) {
            Arrays.fill(a, fromIndex, fromIndex + n2, -1);
        }
    }
    
    private static String convertDottedQuadToHex(String hexString) {
        final int lastIndex = hexString.lastIndexOf(58);
        final String substring = hexString.substring(0, lastIndex + 1);
        final byte[] textToNumericFormatV4 = textToNumericFormatV4(hexString.substring(lastIndex + 1));
        if (textToNumericFormatV4 != null) {
            hexString = Integer.toHexString((textToNumericFormatV4[0] & 0xFF) << 8 | (textToNumericFormatV4[1] & 0xFF));
            return substring + hexString + ":" + Integer.toHexString((textToNumericFormatV4[3] & 0xFF) | (textToNumericFormatV4[2] & 0xFF) << 8);
        }
        return null;
    }
    
    public static InetAddress decrement(final InetAddress inetAddress) {
        byte[] address;
        int n;
        for (address = inetAddress.getAddress(), n = address.length - 1; n >= 0 && address[n] == 0; --n) {
            address[n] = -1;
        }
        Preconditions.checkArgument(n >= 0, "Decrementing %s would wrap.", inetAddress);
        --address[n];
        return bytesToInetAddress(address);
    }
    
    public static InetAddress forString(final String s) {
        final byte[] ipStringToBytes = ipStringToBytes(s);
        if (ipStringToBytes != null) {
            return bytesToInetAddress(ipStringToBytes);
        }
        throw formatIllegalArgumentException("'%s' is not an IP string literal.", s);
    }
    
    public static InetAddress forUriString(final String s) {
        Preconditions.checkNotNull(s);
        String substring;
        int n;
        if (s.startsWith("[") && s.endsWith("]")) {
            substring = s.substring(1, s.length() - 1);
            n = 16;
        }
        else {
            n = 4;
            substring = s;
        }
        final byte[] ipStringToBytes = ipStringToBytes(substring);
        if (ipStringToBytes != null && ipStringToBytes.length == n) {
            return bytesToInetAddress(ipStringToBytes);
        }
        throw formatIllegalArgumentException("Not a valid URI IP literal: '%s'", s);
    }
    
    private static IllegalArgumentException formatIllegalArgumentException(final String format, final Object... args) {
        return new IllegalArgumentException(String.format(Locale.ROOT, format, args));
    }
    
    public static Inet4Address fromInteger(final int n) {
        return getInet4Address(Ints.toByteArray(n));
    }
    
    public static InetAddress fromLittleEndianByteArray(final byte[] array) throws UnknownHostException {
        final byte[] addr = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            addr[i] = array[array.length - i - 1];
        }
        return InetAddress.getByAddress(addr);
    }
    
    public static Inet4Address get6to4IPv4Address(final Inet6Address inet6Address) {
        Preconditions.checkArgument(is6to4Address(inet6Address), "Address '%s' is not a 6to4 address.", toAddrString(inet6Address));
        return getInet4Address(Arrays.copyOfRange(inet6Address.getAddress(), 2, 6));
    }
    
    public static Inet4Address getCoercedIPv4Address(final InetAddress inetAddress) {
        if (!(inetAddress instanceof Inet4Address)) {
            final byte[] address = inetAddress.getAddress();
            int i = 0;
            while (true) {
                while (i < 15) {
                    if (address[i] == 0) {
                        ++i;
                    }
                    else {
                        final int n = 0;
                        if (n != 0 && address[15] == 1) {
                            return InetAddresses.LOOPBACK4;
                        }
                        if (n != 0 && address[15] == 0) {
                            return InetAddresses.ANY4;
                        }
                        final Inet6Address inet6Address = (Inet6Address)inetAddress;
                        long long1;
                        if (!hasEmbeddedIPv4ClientAddress(inet6Address)) {
                            long1 = ByteBuffer.wrap(inet6Address.getAddress(), 0, 8).getLong();
                        }
                        else {
                            long1 = getEmbeddedIPv4ClientAddress(inet6Address).hashCode();
                        }
                        int n2 = Hashing.murmur3_32().hashLong(long1).asInt() | 0xE0000000;
                        if (n2 == -1) {
                            n2 = -2;
                        }
                        return getInet4Address(Ints.toByteArray(n2));
                    }
                }
                final int n = 1;
                continue;
            }
        }
        return (Inet4Address)inetAddress;
    }
    
    public static Inet4Address getCompatIPv4Address(final Inet6Address inet6Address) {
        Preconditions.checkArgument(isCompatIPv4Address(inet6Address), "Address '%s' is not IPv4-compatible.", toAddrString(inet6Address));
        return getInet4Address(Arrays.copyOfRange(inet6Address.getAddress(), 12, 16));
    }
    
    public static Inet4Address getEmbeddedIPv4ClientAddress(final Inet6Address inet6Address) {
        if (isCompatIPv4Address(inet6Address)) {
            return getCompatIPv4Address(inet6Address);
        }
        if (is6to4Address(inet6Address)) {
            return get6to4IPv4Address(inet6Address);
        }
        if (!isTeredoAddress(inet6Address)) {
            throw formatIllegalArgumentException("'%s' has no embedded IPv4 address.", toAddrString(inet6Address));
        }
        return getTeredoInfo(inet6Address).getClient();
    }
    
    private static Inet4Address getInet4Address(final byte[] array) {
        Preconditions.checkArgument(array.length == 4, "Byte array has invalid length for an IPv4 address: %s != 4.", array.length);
        return (Inet4Address)bytesToInetAddress(array);
    }
    
    public static Inet4Address getIsatapIPv4Address(final Inet6Address inet6Address) {
        Preconditions.checkArgument(isIsatapAddress(inet6Address), "Address '%s' is not an ISATAP address.", toAddrString(inet6Address));
        return getInet4Address(Arrays.copyOfRange(inet6Address.getAddress(), 12, 16));
    }
    
    public static TeredoInfo getTeredoInfo(final Inet6Address inet6Address) {
        int i = 0;
        Preconditions.checkArgument(isTeredoAddress(inet6Address), "Address '%s' is not a Teredo address.", toAddrString(inet6Address));
        final byte[] address = inet6Address.getAddress();
        final Inet4Address inet4Address = getInet4Address(Arrays.copyOfRange(address, 4, 8));
        final short short1 = ByteStreams.newDataInput(address, 8).readShort();
        final short short2 = ByteStreams.newDataInput(address, 10).readShort();
        byte[] copyOfRange;
        for (copyOfRange = Arrays.copyOfRange(address, 12, 16); i < copyOfRange.length; ++i) {
            copyOfRange[i] ^= -1;
        }
        return new TeredoInfo(inet4Address, getInet4Address(copyOfRange), ~short2 & 0xFFFF, short1 & 0xFFFF);
    }
    
    public static boolean hasEmbeddedIPv4ClientAddress(final Inet6Address inet6Address) {
        boolean b = false;
        if (isCompatIPv4Address(inet6Address) || is6to4Address(inet6Address) || isTeredoAddress(inet6Address)) {
            b = true;
        }
        return b;
    }
    
    private static String hextetsToIPv6String(final int[] array) {
        final StringBuilder sb = new StringBuilder(39);
        int i = 0;
        int n = 0;
        while (i < array.length) {
            boolean b;
            if (array[i] < 0) {
                b = false;
            }
            else {
                b = true;
            }
            if (!b) {
                if (i == 0 || n != 0) {
                    sb.append("::");
                }
            }
            else {
                if (n != 0) {
                    sb.append(':');
                }
                sb.append(Integer.toHexString(array[i]));
            }
            ++i;
            n = (b ? 1 : 0);
        }
        return sb.toString();
    }
    
    public static InetAddress increment(final InetAddress inetAddress) {
        byte[] address;
        int n;
        for (address = inetAddress.getAddress(), n = address.length - 1; n >= 0 && address[n] == -1; --n) {
            address[n] = 0;
        }
        Preconditions.checkArgument(n >= 0, "Incrementing %s would wrap.", inetAddress);
        ++address[n];
        return bytesToInetAddress(address);
    }
    
    private static byte[] ipStringToBytes(String convertDottedQuadToHex) {
        int i = 0;
        int n = 0;
        int n2 = 0;
        while (i < convertDottedQuadToHex.length()) {
            final char char1 = convertDottedQuadToHex.charAt(i);
            if (char1 != '.') {
                if (char1 != ':') {
                    if (Character.digit(char1, 16) == -1) {
                        return null;
                    }
                }
                else {
                    if (n != 0) {
                        return null;
                    }
                    n2 = 1;
                }
            }
            else {
                n = 1;
            }
            ++i;
        }
        if (n2 == 0) {
            if (n == 0) {
                return null;
            }
            return textToNumericFormatV4(convertDottedQuadToHex);
        }
        else {
            if (n != 0 && (convertDottedQuadToHex = convertDottedQuadToHex(convertDottedQuadToHex)) == null) {
                return null;
            }
            return textToNumericFormatV6(convertDottedQuadToHex);
        }
    }
    
    public static boolean is6to4Address(final Inet6Address inet6Address) {
        boolean b = true;
        final byte[] address = inet6Address.getAddress();
        if (address[0] != 32 || address[1] != 2) {
            b = false;
        }
        return b;
    }
    
    public static boolean isCompatIPv4Address(final Inet6Address inet6Address) {
        if (inet6Address.isIPv4CompatibleAddress()) {
            final byte[] address = inet6Address.getAddress();
            return address[12] != 0 || address[13] != 0 || address[14] != 0 || (address[15] != 0 && address[15] != 1);
        }
        return false;
    }
    
    public static boolean isInetAddress(final String s) {
        return ipStringToBytes(s) != null;
    }
    
    public static boolean isIsatapAddress(final Inet6Address inet6Address) {
        final boolean b = false;
        if (isTeredoAddress(inet6Address)) {
            return false;
        }
        final byte[] address = inet6Address.getAddress();
        if ((address[8] | 0x3) == 0x3) {
            boolean b2;
            if (address[9] != 0) {
                b2 = b;
            }
            else {
                b2 = b;
                if (address[10] == 94) {
                    b2 = b;
                    if (address[11] == -2) {
                        b2 = true;
                    }
                }
            }
            return b2;
        }
        return false;
    }
    
    public static boolean isMappedIPv4Address(final String s) {
        final int n = 10;
        final byte[] ipStringToBytes = ipStringToBytes(s);
        if (ipStringToBytes != null && ipStringToBytes.length == 16) {
            for (int i = 0; i < 10; ++i) {
                if (ipStringToBytes[i] != 0) {
                    return false;
                }
            }
            for (int j = n; j < 12; ++j) {
                if (ipStringToBytes[j] != -1) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public static boolean isMaximum(final InetAddress inetAddress) {
        final byte[] address = inetAddress.getAddress();
        for (int i = 0; i < address.length; ++i) {
            if (address[i] != -1) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isTeredoAddress(final Inet6Address inet6Address) {
        boolean b = true;
        final byte[] address = inet6Address.getAddress();
        if (address[0] != 32 || address[1] != 1 || address[2] != 0 || address[3] != 0) {
            b = false;
        }
        return b;
    }
    
    public static boolean isUriInetAddress(final String s) {
        try {
            forUriString(s);
            return true;
        }
        catch (final IllegalArgumentException ex) {
            return false;
        }
    }
    
    private static short parseHextet(final String s) {
        final int int1 = Integer.parseInt(s, 16);
        if (int1 <= 65535) {
            return (short)int1;
        }
        throw new NumberFormatException();
    }
    
    private static byte parseOctet(final String s) {
        final int int1 = Integer.parseInt(s);
        if (int1 <= 255 && (!s.startsWith("0") || s.length() <= 1)) {
            return (byte)int1;
        }
        throw new NumberFormatException();
    }
    
    private static byte[] textToNumericFormatV4(final String s) {
        final String[] split = s.split("\\.", 5);
        Label_0029: {
            if (split.length != 4) {
                break Label_0029;
            }
            final byte[] array = new byte[4];
            int i = 0;
            try {
                while (i < array.length) {
                    array[i] = parseOctet(split[i]);
                    ++i;
                }
                return array;
                return null;
            }
            catch (final NumberFormatException ex) {
                return null;
            }
        }
    }
    
    private static byte[] textToNumericFormatV6(final String s) {
        final int n = 0;
        final String[] split = s.split(":", 10);
        if (split.length >= 3 && split.length <= 9) {
            int n2 = -1;
            for (int i = 1; i < split.length - 1; ++i) {
                if (split[i].length() == 0) {
                    if (n2 >= 0) {
                        return null;
                    }
                    n2 = i;
                }
            }
            int length;
            int n3;
            if (n2 < 0) {
                length = split.length;
                n3 = 0;
            }
            else {
                int n4 = split.length - n2 - 1;
                int n5;
                if (split[0].length() != 0) {
                    n5 = n2;
                }
                else if ((n5 = n2 - 1) != 0) {
                    return null;
                }
                if (split[split.length - 1].length() == 0 && --n4 != 0) {
                    return null;
                }
                final int n6 = n5;
                n3 = n4;
                length = n6;
            }
            final int n7 = 8 - (length + n3);
            if (n2 < 0) {
                if (n7 != 0) {
                    return null;
                }
            }
            else if (n7 < 1) {
                return null;
            }
            final ByteBuffer allocate = ByteBuffer.allocate(16);
            int n8 = 0;
        Label_0083_Outer:
            while (true) {
                Label_0209: {
                    if (n8 < length) {
                        break Label_0209;
                    }
                    int n9 = n;
                Label_0089_Outer:
                    while (true) {
                        Label_0227: {
                            if (n9 < n7) {
                                break Label_0227;
                            }
                            while (true) {
                                if (n3 <= 0) {
                                    break;
                                }
                                Label_0240: {
                                    break Label_0240;
                                    try {
                                        allocate.putShort(parseHextet(split[n8]));
                                        ++n8;
                                        continue Label_0083_Outer;
                                        allocate.putShort(parseHextet(split[split.length - n3]));
                                        --n3;
                                        continue;
                                        allocate.putShort((short)0);
                                        ++n9;
                                        continue Label_0089_Outer;
                                    }
                                    catch (final NumberFormatException ex) {
                                        return null;
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            return allocate.array();
        }
        return null;
    }
    
    public static String toAddrString(final InetAddress inetAddress) {
        Preconditions.checkNotNull(inetAddress);
        if (!(inetAddress instanceof Inet4Address)) {
            Preconditions.checkArgument(inetAddress instanceof Inet6Address);
            final byte[] address = inetAddress.getAddress();
            final int[] array = new int[8];
            for (int i = 0; i < array.length; ++i) {
                array[i] = Ints.fromBytes((byte)0, (byte)0, address[i * 2], address[i * 2 + 1]);
            }
            compressLongestRunOfZeroes(array);
            return hextetsToIPv6String(array);
        }
        return inetAddress.getHostAddress();
    }
    
    public static String toUriString(final InetAddress inetAddress) {
        if (!(inetAddress instanceof Inet6Address)) {
            return toAddrString(inetAddress);
        }
        return "[" + toAddrString(inetAddress) + "]";
    }
    
    @Beta
    public static final class TeredoInfo
    {
        private final Inet4Address client;
        private final int flags;
        private final int port;
        private final Inet4Address server;
        
        public TeredoInfo(@Nullable final Inet4Address inet4Address, @Nullable final Inet4Address inet4Address2, final int n, final int n2) {
            Preconditions.checkArgument(n >= 0 && n <= 65535, "port '%s' is out of range (0 <= port <= 0xffff)", n);
            Preconditions.checkArgument(n2 >= 0 && n2 <= 65535, "flags '%s' is out of range (0 <= flags <= 0xffff)", n2);
            this.server = MoreObjects.firstNonNull(inet4Address, InetAddresses.ANY4);
            this.client = MoreObjects.firstNonNull(inet4Address2, InetAddresses.ANY4);
            this.port = n;
            this.flags = n2;
        }
        
        public Inet4Address getClient() {
            return this.client;
        }
        
        public int getFlags() {
            return this.flags;
        }
        
        public int getPort() {
            return this.port;
        }
        
        public Inet4Address getServer() {
            return this.server;
        }
    }
}
