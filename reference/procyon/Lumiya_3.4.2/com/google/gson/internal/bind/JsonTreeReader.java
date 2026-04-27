// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal.bind;

import com.google.gson.JsonNull;
import java.util.Map;
import com.google.gson.JsonPrimitive;
import java.util.Iterator;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.stream.JsonToken;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.io.Reader;
import com.google.gson.stream.JsonReader;

public final class JsonTreeReader extends JsonReader
{
    private static final Object SENTINEL_CLOSED;
    private static final Reader UNREADABLE_READER;
    private int[] pathIndices;
    private String[] pathNames;
    private Object[] stack;
    private int stackSize;
    
    static {
        UNREADABLE_READER = new Reader() {
            @Override
            public void close() throws IOException {
                throw new AssertionError();
            }
            
            @Override
            public int read(final char[] array, final int n, final int n2) throws IOException {
                throw new AssertionError();
            }
        };
        SENTINEL_CLOSED = new Object();
    }
    
    public JsonTreeReader(final JsonElement jsonElement) {
        super(JsonTreeReader.UNREADABLE_READER);
        this.stack = new Object[32];
        this.stackSize = 0;
        this.pathNames = new String[32];
        this.pathIndices = new int[32];
        this.push(jsonElement);
    }
    
    private void expect(final JsonToken obj) throws IOException {
        if (this.peek() == obj) {
            return;
        }
        throw new IllegalStateException("Expected " + obj + " but was " + this.peek() + this.locationString());
    }
    
    private String locationString() {
        return " at path " + this.getPath();
    }
    
    private Object peekStack() {
        return this.stack[this.stackSize - 1];
    }
    
    private Object popStack() {
        final Object[] stack = this.stack;
        final int stackSize = this.stackSize - 1;
        this.stackSize = stackSize;
        final Object o = stack[stackSize];
        this.stack[this.stackSize] = null;
        return o;
    }
    
    private void push(final Object o) {
        if (this.stackSize == this.stack.length) {
            final Object[] stack = new Object[this.stackSize * 2];
            final int[] pathIndices = new int[this.stackSize * 2];
            final String[] pathNames = new String[this.stackSize * 2];
            System.arraycopy(this.stack, 0, stack, 0, this.stackSize);
            System.arraycopy(this.pathIndices, 0, pathIndices, 0, this.stackSize);
            System.arraycopy(this.pathNames, 0, pathNames, 0, this.stackSize);
            this.stack = stack;
            this.pathIndices = pathIndices;
            this.pathNames = pathNames;
        }
        this.stack[this.stackSize++] = o;
    }
    
    @Override
    public void beginArray() throws IOException {
        this.expect(JsonToken.BEGIN_ARRAY);
        this.push(((JsonArray)this.peekStack()).iterator());
        this.pathIndices[this.stackSize - 1] = 0;
    }
    
    @Override
    public void beginObject() throws IOException {
        this.expect(JsonToken.BEGIN_OBJECT);
        this.push(((JsonObject)this.peekStack()).entrySet().iterator());
    }
    
    @Override
    public void close() throws IOException {
        this.stack = new Object[] { JsonTreeReader.SENTINEL_CLOSED };
        this.stackSize = 1;
    }
    
    @Override
    public void endArray() throws IOException {
        this.expect(JsonToken.END_ARRAY);
        this.popStack();
        this.popStack();
        if (this.stackSize > 0) {
            final int[] pathIndices = this.pathIndices;
            final int n = this.stackSize - 1;
            ++pathIndices[n];
        }
    }
    
    @Override
    public void endObject() throws IOException {
        this.expect(JsonToken.END_OBJECT);
        this.popStack();
        this.popStack();
        if (this.stackSize > 0) {
            final int[] pathIndices = this.pathIndices;
            final int n = this.stackSize - 1;
            ++pathIndices[n];
        }
    }
    
    @Override
    public String getPath() {
        int i = 0;
        final StringBuilder append = new StringBuilder().append('$');
        while (i < this.stackSize) {
            if (!(this.stack[i] instanceof JsonArray)) {
                if (this.stack[i] instanceof JsonObject) {
                    final Object[] stack = this.stack;
                    final int n = ++i;
                    if (stack[n] instanceof Iterator) {
                        append.append('.');
                        i = n;
                        if (this.pathNames[n] != null) {
                            append.append(this.pathNames[n]);
                            i = n;
                        }
                    }
                }
            }
            else {
                final Object[] stack2 = this.stack;
                final int n2 = ++i;
                if (stack2[n2] instanceof Iterator) {
                    append.append('[').append(this.pathIndices[n2]).append(']');
                    i = n2;
                }
            }
            ++i;
        }
        return append.toString();
    }
    
    @Override
    public boolean hasNext() throws IOException {
        final JsonToken peek = this.peek();
        return peek != JsonToken.END_OBJECT && peek != JsonToken.END_ARRAY;
    }
    
    @Override
    public boolean nextBoolean() throws IOException {
        this.expect(JsonToken.BOOLEAN);
        final boolean asBoolean = ((JsonPrimitive)this.popStack()).getAsBoolean();
        if (this.stackSize > 0) {
            final int[] pathIndices = this.pathIndices;
            final int n = this.stackSize - 1;
            ++pathIndices[n];
        }
        return asBoolean;
    }
    
    @Override
    public double nextDouble() throws IOException {
        final JsonToken peek = this.peek();
        if (peek != JsonToken.NUMBER && peek != JsonToken.STRING) {
            throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + peek + this.locationString());
        }
        final double asDouble = ((JsonPrimitive)this.peekStack()).getAsDouble();
        if (!this.isLenient() && (Double.isNaN(asDouble) || Double.isInfinite(asDouble))) {
            throw new NumberFormatException("JSON forbids NaN and infinities: " + asDouble);
        }
        this.popStack();
        if (this.stackSize > 0) {
            final int[] pathIndices = this.pathIndices;
            final int n = this.stackSize - 1;
            ++pathIndices[n];
        }
        return asDouble;
    }
    
    @Override
    public int nextInt() throws IOException {
        final JsonToken peek = this.peek();
        if (peek != JsonToken.NUMBER && peek != JsonToken.STRING) {
            throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + peek + this.locationString());
        }
        final int asInt = ((JsonPrimitive)this.peekStack()).getAsInt();
        this.popStack();
        if (this.stackSize > 0) {
            final int[] pathIndices = this.pathIndices;
            final int n = this.stackSize - 1;
            ++pathIndices[n];
        }
        return asInt;
    }
    
    @Override
    public long nextLong() throws IOException {
        final JsonToken peek = this.peek();
        if (peek != JsonToken.NUMBER && peek != JsonToken.STRING) {
            throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + peek + this.locationString());
        }
        final long asLong = ((JsonPrimitive)this.peekStack()).getAsLong();
        this.popStack();
        if (this.stackSize > 0) {
            final int[] pathIndices = this.pathIndices;
            final int n = this.stackSize - 1;
            ++pathIndices[n];
        }
        return asLong;
    }
    
    @Override
    public String nextName() throws IOException {
        this.expect(JsonToken.NAME);
        final Map.Entry<String, V> entry = ((Iterator)this.peekStack()).next();
        final String s = entry.getKey();
        this.pathNames[this.stackSize - 1] = s;
        this.push(entry.getValue());
        return s;
    }
    
    @Override
    public void nextNull() throws IOException {
        this.expect(JsonToken.NULL);
        this.popStack();
        if (this.stackSize > 0) {
            final int[] pathIndices = this.pathIndices;
            final int n = this.stackSize - 1;
            ++pathIndices[n];
        }
    }
    
    @Override
    public String nextString() throws IOException {
        final JsonToken peek = this.peek();
        if (peek != JsonToken.STRING && peek != JsonToken.NUMBER) {
            throw new IllegalStateException("Expected " + JsonToken.STRING + " but was " + peek + this.locationString());
        }
        final String asString = ((JsonPrimitive)this.popStack()).getAsString();
        if (this.stackSize > 0) {
            final int[] pathIndices = this.pathIndices;
            final int n = this.stackSize - 1;
            ++pathIndices[n];
        }
        return asString;
    }
    
    @Override
    public JsonToken peek() throws IOException {
        if (this.stackSize == 0) {
            return JsonToken.END_DOCUMENT;
        }
        final Object peekStack = this.peekStack();
        if (!(peekStack instanceof Iterator)) {
            if (peekStack instanceof JsonObject) {
                return JsonToken.BEGIN_OBJECT;
            }
            if (peekStack instanceof JsonArray) {
                return JsonToken.BEGIN_ARRAY;
            }
            if (!(peekStack instanceof JsonPrimitive)) {
                if (peekStack instanceof JsonNull) {
                    return JsonToken.NULL;
                }
                if (peekStack != JsonTreeReader.SENTINEL_CLOSED) {
                    throw new AssertionError();
                }
                throw new IllegalStateException("JsonReader is closed");
            }
            else {
                final JsonPrimitive jsonPrimitive = (JsonPrimitive)peekStack;
                if (jsonPrimitive.isString()) {
                    return JsonToken.STRING;
                }
                if (jsonPrimitive.isBoolean()) {
                    return JsonToken.BOOLEAN;
                }
                if (!jsonPrimitive.isNumber()) {
                    throw new AssertionError();
                }
                return JsonToken.NUMBER;
            }
        }
        else {
            final boolean b = this.stack[this.stackSize - 2] instanceof JsonObject;
            final Iterator iterator = (Iterator)peekStack;
            if (!iterator.hasNext()) {
                JsonToken jsonToken;
                if (!b) {
                    jsonToken = JsonToken.END_ARRAY;
                }
                else {
                    jsonToken = JsonToken.END_OBJECT;
                }
                return jsonToken;
            }
            if (!b) {
                this.push(iterator.next());
                return this.peek();
            }
            return JsonToken.NAME;
        }
    }
    
    public void promoteNameToValue() throws IOException {
        this.expect(JsonToken.NAME);
        final Map.Entry<K, Object> entry = ((Iterator)this.peekStack()).next();
        this.push(entry.getValue());
        this.push(new JsonPrimitive((String)entry.getKey()));
    }
    
    @Override
    public void skipValue() throws IOException {
        if (this.peek() != JsonToken.NAME) {
            this.popStack();
            if (this.stackSize > 0) {
                this.pathNames[this.stackSize - 1] = "null";
            }
        }
        else {
            this.nextName();
            this.pathNames[this.stackSize - 2] = "null";
        }
        if (this.stackSize > 0) {
            final int[] pathIndices = this.pathIndices;
            final int n = this.stackSize - 1;
            ++pathIndices[n];
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
