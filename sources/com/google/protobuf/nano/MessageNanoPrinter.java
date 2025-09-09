package com.google.protobuf.nano;

import com.google.common.primitives.UnsignedBytes;
import com.google.vr.cardboard.VrSettingsProviderContract;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public final class MessageNanoPrinter {
    private static final String INDENT = "  ";
    private static final int MAX_STRING_LEN = 200;

    private MessageNanoPrinter() {
    }

    private static void appendQuotedBytes(byte[] bArr, StringBuffer stringBuffer) {
        if (bArr != null) {
            stringBuffer.append('\"');
            for (byte b : bArr) {
                byte b2 = b & UnsignedBytes.MAX_VALUE;
                if (b2 == 92 || b2 == 34) {
                    stringBuffer.append('\\').append((char) b2);
                } else if (b2 >= 32 && b2 < Byte.MAX_VALUE) {
                    stringBuffer.append((char) b2);
                } else {
                    stringBuffer.append(String.format("\\%03o", new Object[]{Integer.valueOf(b2)}));
                }
            }
            stringBuffer.append('\"');
            return;
        }
        stringBuffer.append("\"\"");
    }

    private static String deCamelCaseify(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (i == 0) {
                stringBuffer.append(Character.toLowerCase(charAt));
            } else if (!Character.isUpperCase(charAt)) {
                stringBuffer.append(charAt);
            } else {
                stringBuffer.append('_').append(Character.toLowerCase(charAt));
            }
        }
        return stringBuffer.toString();
    }

    private static String escapeString(String str) {
        int length = str.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (charAt >= ' ' && charAt <= '~' && charAt != '\"' && charAt != '\'') {
                sb.append(charAt);
            } else {
                sb.append(String.format("\\u%04x", new Object[]{Integer.valueOf(charAt)}));
            }
        }
        return sb.toString();
    }

    public static <T extends MessageNano> String print(T t) {
        if (t == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        try {
            print((String) null, t, new StringBuffer(), stringBuffer);
            return stringBuffer.toString();
        } catch (IllegalAccessException e) {
            return "Error printing proto: " + e.getMessage();
        } catch (InvocationTargetException e2) {
            return "Error printing proto: " + e2.getMessage();
        }
    }

    private static void print(String str, Object obj, StringBuffer stringBuffer, StringBuffer stringBuffer2) throws IllegalAccessException, InvocationTargetException {
        if (obj == null) {
            return;
        }
        if (obj instanceof MessageNano) {
            int length = stringBuffer.length();
            if (str != null) {
                stringBuffer2.append(stringBuffer).append(deCamelCaseify(str)).append(" <\n");
                stringBuffer.append(INDENT);
            }
            Class<?> cls = obj.getClass();
            for (Field field : cls.getFields()) {
                int modifiers = field.getModifiers();
                String name = field.getName();
                if (!"cachedSize".equals(name) && (modifiers & 1) == 1 && (modifiers & 8) != 8 && !name.startsWith("_") && !name.endsWith("_")) {
                    Class<?> type = field.getType();
                    Object obj2 = field.get(obj);
                    if (!type.isArray()) {
                        print(name, obj2, stringBuffer, stringBuffer2);
                    } else if (type.getComponentType() != Byte.TYPE) {
                        int length2 = obj2 != null ? Array.getLength(obj2) : 0;
                        for (int i = 0; i < length2; i++) {
                            print(name, Array.get(obj2, i), stringBuffer, stringBuffer2);
                        }
                    } else {
                        print(name, obj2, stringBuffer, stringBuffer2);
                    }
                }
            }
            for (Method name2 : cls.getMethods()) {
                String name3 = name2.getName();
                if (name3.startsWith("set")) {
                    String substring = name3.substring(3);
                    try {
                        if (((Boolean) cls.getMethod("has" + substring, new Class[0]).invoke(obj, new Object[0])).booleanValue()) {
                            try {
                                print(substring, cls.getMethod("get" + substring, new Class[0]).invoke(obj, new Object[0]), stringBuffer, stringBuffer2);
                            } catch (NoSuchMethodException e) {
                            }
                        }
                    } catch (NoSuchMethodException e2) {
                    }
                }
            }
            if (str != null) {
                stringBuffer.setLength(length);
                stringBuffer2.append(stringBuffer).append(">\n");
            }
        } else if (!(obj instanceof Map)) {
            stringBuffer2.append(stringBuffer).append(deCamelCaseify(str)).append(": ");
            if (obj instanceof String) {
                stringBuffer2.append("\"").append(sanitizeString((String) obj)).append("\"");
            } else if (!(obj instanceof byte[])) {
                stringBuffer2.append(obj);
            } else {
                appendQuotedBytes((byte[]) obj, stringBuffer2);
            }
            stringBuffer2.append("\n");
        } else {
            String deCamelCaseify = deCamelCaseify(str);
            for (Map.Entry entry : ((Map) obj).entrySet()) {
                stringBuffer2.append(stringBuffer).append(deCamelCaseify).append(" <\n");
                int length3 = stringBuffer.length();
                stringBuffer.append(INDENT);
                print("key", entry.getKey(), stringBuffer, stringBuffer2);
                print(VrSettingsProviderContract.SETTING_VALUE_KEY, entry.getValue(), stringBuffer, stringBuffer2);
                stringBuffer.setLength(length3);
                stringBuffer2.append(stringBuffer).append(">\n");
            }
        }
    }

    private static String sanitizeString(String str) {
        if (!str.startsWith("http") && str.length() > 200) {
            str = str.substring(0, 200) + "[...]";
        }
        return escapeString(str);
    }
}
