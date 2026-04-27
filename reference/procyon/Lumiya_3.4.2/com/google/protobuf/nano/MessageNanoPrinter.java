// 
// Decompiled by Procyon v0.6.0
// 

package com.google.protobuf.nano;

import java.util.Iterator;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Map;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

public final class MessageNanoPrinter
{
    private static final String INDENT = "  ";
    private static final int MAX_STRING_LEN = 200;
    
    private MessageNanoPrinter() {
    }
    
    private static void appendQuotedBytes(final byte[] array, final StringBuffer sb) {
        if (array != null) {
            sb.append('\"');
            for (int i = 0; i < array.length; ++i) {
                final int j = array[i] & 0xFF;
                if (j != 92 && j != 34) {
                    if (j >= 32 && j < 127) {
                        sb.append((char)j);
                    }
                    else {
                        sb.append(String.format("\\%03o", j));
                    }
                }
                else {
                    sb.append('\\').append((char)j);
                }
            }
            sb.append('\"');
            return;
        }
        sb.append("\"\"");
    }
    
    private static String deCamelCaseify(final String s) {
        int i = 0;
        final StringBuffer sb = new StringBuffer();
        while (i < s.length()) {
            final char char1 = s.charAt(i);
            if (i != 0) {
                if (!Character.isUpperCase(char1)) {
                    sb.append(char1);
                }
                else {
                    sb.append('_').append(Character.toLowerCase(char1));
                }
            }
            else {
                sb.append(Character.toLowerCase(char1));
            }
            ++i;
        }
        return sb.toString();
    }
    
    private static String escapeString(final String s) {
        final int length = s.length();
        final StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 >= ' ' && char1 <= '~' && char1 != '\"' && char1 != '\'') {
                sb.append(char1);
            }
            else {
                sb.append(String.format("\\u%04x", (int)char1));
            }
        }
        return sb.toString();
    }
    
    public static <T extends MessageNano> String print(final T t) {
        if (t == null) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        try {
            print(null, t, new StringBuffer(), sb);
            return sb.toString();
        }
        catch (final IllegalAccessException ex) {
            return "Error printing proto: " + ex.getMessage();
        }
        catch (final InvocationTargetException ex2) {
            return "Error printing proto: " + ex2.getMessage();
        }
    }
    
    private static void print(String str, Object o, final StringBuffer sb, final StringBuffer sb2) throws IllegalAccessException, InvocationTargetException {
        if (o != null) {
        Label_0379_Outer:
            while (true) {
            Label_0527_Outer:
                while (true) {
                    Object o2 = null;
                    String name2 = null;
                Label_0385:
                    while (true) {
                        if (o instanceof MessageNano) {
                            final int length = sb.length();
                            if (str != null) {
                                sb2.append(sb).append(deCamelCaseify(str)).append(" <\n");
                                sb.append("  ");
                            }
                            o2 = o.getClass();
                            for (final Field field : ((Class)o2).getFields()) {
                                final int modifiers = field.getModifiers();
                                final String name = field.getName();
                                if (!"cachedSize".equals(name) && (modifiers & 0x1) == 0x1 && (modifiers & 0x8) != 0x8 && !name.startsWith("_") && !name.endsWith("_")) {
                                    final Class<?> type = field.getType();
                                    final Object value = field.get(o);
                                    if (!type.isArray()) {
                                        print(name, value, sb, sb2);
                                    }
                                    else if (type.getComponentType() != Byte.TYPE) {
                                        int length3;
                                        if (value != null) {
                                            length3 = Array.getLength(value);
                                        }
                                        else {
                                            length3 = 0;
                                        }
                                        for (int j = 0; j < length3; ++j) {
                                            print(name, Array.get(value, j), sb, sb2);
                                        }
                                    }
                                    else {
                                        print(name, value, sb, sb2);
                                    }
                                }
                            }
                            final Method[] methods = ((Class)o2).getMethods();
                            final int length4 = methods.length;
                            final int length5 = 0;
                            break Label_0119;
                        }
                        if (o instanceof Map) {
                            break Label_0379_Outer;
                        }
                        str = deCamelCaseify(str);
                        sb2.append(sb).append(str).append(": ");
                        if (o instanceof String) {
                            break Label_0379_Outer;
                        }
                        if (o instanceof byte[]) {
                            break Label_0379_Outer;
                        }
                        sb2.append(o);
                        sb2.append("\n");
                        return;
                        int length4 = 0;
                        int length5 = 0;
                        if (length5 >= length4) {
                            if (str != null) {
                                final int length;
                                sb.setLength(length);
                                sb2.append(sb).append(">\n");
                            }
                            return;
                        }
                        else {
                            final Method[] methods;
                            name2 = methods[length5].getName();
                            if (name2.startsWith("set")) {
                                break Label_0385;
                            }
                        }
                        ++length5;
                        continue Label_0379_Outer;
                    }
                    final String substring = name2.substring(3);
                    try {
                        if (!(boolean)((Class)o2).getMethod("has" + substring, (Class[])new Class[0]).invoke(o, new Object[0])) {
                            continue;
                        }
                        try {
                            print(substring, ((Class)o2).getMethod("get" + substring, (Class[])new Class[0]).invoke(o, new Object[0]), sb, sb2);
                            continue Label_0527_Outer;
                            Block_21: {
                                while (true) {
                                    iftrue(Label_0065:)(!((Iterator)o2).hasNext());
                                    break Block_21;
                                    o = o;
                                    str = deCamelCaseify(str);
                                    o2 = ((Map<Object, Object>)o).entrySet().iterator();
                                    continue;
                                }
                                str = sanitizeString((String)o);
                                sb2.append("\"").append(str).append("\"");
                                continue Label_0379_Outer;
                                appendQuotedBytes((byte[])o, sb2);
                                continue Label_0379_Outer;
                            }
                            o = ((Iterator)o2).next();
                            sb2.append(sb).append(str).append(" <\n");
                            final int length5 = sb.length();
                            sb.append("  ");
                            print("key", ((Map.Entry<Object, V>)o).getKey(), sb, sb2);
                            print("value", ((Map.Entry<K, Object>)o).getValue(), sb, sb2);
                            sb.setLength(length5);
                            sb2.append(sb).append(">\n");
                        }
                        catch (final NoSuchMethodException ex) {}
                    }
                    catch (final NoSuchMethodException ex2) {
                        continue;
                    }
                    break;
                }
                break;
            }
        }
        Label_0065:;
    }
    
    private static String sanitizeString(final String s) {
        String string;
        if (s.startsWith("http")) {
            string = s;
        }
        else {
            string = s;
            if (s.length() > 200) {
                string = s.substring(0, 200) + "[...]";
            }
        }
        return escapeString(string);
    }
}
