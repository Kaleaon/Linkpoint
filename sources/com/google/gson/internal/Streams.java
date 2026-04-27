package com.google.gson.internal;

import com.google.gson.JsonElement;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Writer;

public final class Streams {

    private static final class AppendableWriter extends Writer {
        private final Appendable appendable;
        private final CurrentWrite currentWrite = new CurrentWrite();

        static class CurrentWrite implements CharSequence {
            char[] chars;

            CurrentWrite() {
            }

            public char charAt(int i) {
                return this.chars[i];
            }

            public int length() {
                return this.chars.length;
            }

            public CharSequence subSequence(int i, int i2) {
                return new String(this.chars, i, i2 - i);
            }
        }

        AppendableWriter(Appendable appendable2) {
            this.appendable = appendable2;
        }

        public void close() {
        }

        public void flush() {
        }

        public void write(int i) throws IOException {
            this.appendable.append((char) i);
        }

        public void write(char[] cArr, int i, int i2) throws IOException {
            this.currentWrite.chars = cArr;
            this.appendable.append(this.currentWrite, i, i + i2);
        }
    }

    private Streams() {
        throw new UnsupportedOperationException();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0017, code lost:
        throw new com.google.gson.JsonSyntaxException((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001a, code lost:
        return com.google.gson.JsonNull.INSTANCE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0021, code lost:
        throw new com.google.gson.JsonSyntaxException((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0022, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0028, code lost:
        throw new com.google.gson.JsonIOException((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0029, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x002f, code lost:
        throw new com.google.gson.JsonSyntaxException((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0030, code lost:
        r0 = e;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0018  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x001b A[ExcHandler: MalformedJsonException (r0v2 'e' com.google.gson.stream.MalformedJsonException A[CUSTOM_DECLARE]), Splitter:B:1:0x0002] */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0022 A[ExcHandler: IOException (r0v1 'e' java.io.IOException A[CUSTOM_DECLARE]), Splitter:B:1:0x0002] */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0029 A[ExcHandler: NumberFormatException (r0v0 'e' java.lang.NumberFormatException A[CUSTOM_DECLARE]), Splitter:B:1:0x0002] */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0012  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.google.gson.JsonElement parse(com.google.gson.stream.JsonReader r3) throws com.google.gson.JsonParseException {
        /*
            r1 = 0
            r2 = 1
            r3.peek()     // Catch:{ EOFException -> 0x000e, MalformedJsonException -> 0x001b, IOException -> 0x0022, NumberFormatException -> 0x0029 }
            com.google.gson.TypeAdapter<com.google.gson.JsonElement> r0 = com.google.gson.internal.bind.TypeAdapters.JSON_ELEMENT     // Catch:{ EOFException -> 0x0030, MalformedJsonException -> 0x001b, IOException -> 0x0022, NumberFormatException -> 0x0029 }
            java.lang.Object r0 = r0.read(r3)     // Catch:{ EOFException -> 0x0030, MalformedJsonException -> 0x001b, IOException -> 0x0022, NumberFormatException -> 0x0029 }
            com.google.gson.JsonElement r0 = (com.google.gson.JsonElement) r0     // Catch:{ EOFException -> 0x0030, MalformedJsonException -> 0x001b, IOException -> 0x0022, NumberFormatException -> 0x0029 }
            return r0
        L_0x000e:
            r0 = move-exception
            r1 = r2
        L_0x0010:
            if (r1 != 0) goto L_0x0018
            com.google.gson.JsonSyntaxException r1 = new com.google.gson.JsonSyntaxException
            r1.<init>((java.lang.Throwable) r0)
            throw r1
        L_0x0018:
            com.google.gson.JsonNull r0 = com.google.gson.JsonNull.INSTANCE
            return r0
        L_0x001b:
            r0 = move-exception
            com.google.gson.JsonSyntaxException r1 = new com.google.gson.JsonSyntaxException
            r1.<init>((java.lang.Throwable) r0)
            throw r1
        L_0x0022:
            r0 = move-exception
            com.google.gson.JsonIOException r1 = new com.google.gson.JsonIOException
            r1.<init>((java.lang.Throwable) r0)
            throw r1
        L_0x0029:
            r0 = move-exception
            com.google.gson.JsonSyntaxException r1 = new com.google.gson.JsonSyntaxException
            r1.<init>((java.lang.Throwable) r0)
            throw r1
        L_0x0030:
            r0 = move-exception
            goto L_0x0010
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.internal.Streams.parse(com.google.gson.stream.JsonReader):com.google.gson.JsonElement");
    }

    public static void write(JsonElement jsonElement, JsonWriter jsonWriter) throws IOException {
        TypeAdapters.JSON_ELEMENT.write(jsonWriter, jsonElement);
    }

    public static Writer writerForAppendable(Appendable appendable) {
        return !(appendable instanceof Writer) ? new AppendableWriter(appendable) : (Writer) appendable;
    }
}
