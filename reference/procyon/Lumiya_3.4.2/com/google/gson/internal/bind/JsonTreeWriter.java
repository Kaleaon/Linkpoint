// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal.bind;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;
import com.google.gson.JsonElement;
import java.io.Writer;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

public final class JsonTreeWriter extends JsonWriter
{
    private static final JsonPrimitive SENTINEL_CLOSED;
    private static final Writer UNWRITABLE_WRITER;
    private String pendingName;
    private JsonElement product;
    private final List<JsonElement> stack;
    
    static {
        UNWRITABLE_WRITER = new Writer() {
            @Override
            public void close() throws IOException {
                throw new AssertionError();
            }
            
            @Override
            public void flush() throws IOException {
                throw new AssertionError();
            }
            
            @Override
            public void write(final char[] array, final int n, final int n2) {
                throw new AssertionError();
            }
        };
        SENTINEL_CLOSED = new JsonPrimitive("closed");
    }
    
    public JsonTreeWriter() {
        super(JsonTreeWriter.UNWRITABLE_WRITER);
        this.stack = new ArrayList<JsonElement>();
        this.product = JsonNull.INSTANCE;
    }
    
    private JsonElement peek() {
        return this.stack.get(this.stack.size() - 1);
    }
    
    private void put(final JsonElement product) {
        if (this.pendingName == null) {
            if (!this.stack.isEmpty()) {
                final JsonElement peek = this.peek();
                if (!(peek instanceof JsonArray)) {
                    throw new IllegalStateException();
                }
                ((JsonArray)peek).add(product);
            }
            else {
                this.product = product;
            }
        }
        else {
            if (!product.isJsonNull() || this.getSerializeNulls()) {
                ((JsonObject)this.peek()).add(this.pendingName, product);
            }
            this.pendingName = null;
        }
    }
    
    @Override
    public JsonWriter beginArray() throws IOException {
        final JsonArray jsonArray = new JsonArray();
        this.put(jsonArray);
        this.stack.add(jsonArray);
        return this;
    }
    
    @Override
    public JsonWriter beginObject() throws IOException {
        final JsonObject jsonObject = new JsonObject();
        this.put(jsonObject);
        this.stack.add(jsonObject);
        return this;
    }
    
    @Override
    public void close() throws IOException {
        if (this.stack.isEmpty()) {
            this.stack.add(JsonTreeWriter.SENTINEL_CLOSED);
            return;
        }
        throw new IOException("Incomplete document");
    }
    
    @Override
    public JsonWriter endArray() throws IOException {
        if (this.stack.isEmpty() || this.pendingName != null) {
            throw new IllegalStateException();
        }
        if (!(this.peek() instanceof JsonArray)) {
            throw new IllegalStateException();
        }
        this.stack.remove(this.stack.size() - 1);
        return this;
    }
    
    @Override
    public JsonWriter endObject() throws IOException {
        if (this.stack.isEmpty() || this.pendingName != null) {
            throw new IllegalStateException();
        }
        if (!(this.peek() instanceof JsonObject)) {
            throw new IllegalStateException();
        }
        this.stack.remove(this.stack.size() - 1);
        return this;
    }
    
    @Override
    public void flush() throws IOException {
    }
    
    public JsonElement get() {
        if (this.stack.isEmpty()) {
            return this.product;
        }
        throw new IllegalStateException("Expected one JSON element but was " + this.stack);
    }
    
    @Override
    public JsonWriter name(final String pendingName) throws IOException {
        if (this.stack.isEmpty() || this.pendingName != null) {
            throw new IllegalStateException();
        }
        if (!(this.peek() instanceof JsonObject)) {
            throw new IllegalStateException();
        }
        this.pendingName = pendingName;
        return this;
    }
    
    @Override
    public JsonWriter nullValue() throws IOException {
        this.put(JsonNull.INSTANCE);
        return this;
    }
    
    @Override
    public JsonWriter value(final double n) throws IOException {
        if (!this.isLenient() && (Double.isNaN(n) || Double.isInfinite(n))) {
            throw new IllegalArgumentException("JSON forbids NaN and infinities: " + n);
        }
        this.put(new JsonPrimitive(n));
        return this;
    }
    
    @Override
    public JsonWriter value(final long l) throws IOException {
        this.put(new JsonPrimitive(l));
        return this;
    }
    
    @Override
    public JsonWriter value(final Boolean b) throws IOException {
        if (b != null) {
            this.put(new JsonPrimitive(b));
            return this;
        }
        return this.nullValue();
    }
    
    @Override
    public JsonWriter value(final Number obj) throws IOException {
        if (obj != null) {
            if (!this.isLenient()) {
                final double doubleValue = obj.doubleValue();
                if (Double.isNaN(doubleValue) || Double.isInfinite(doubleValue)) {
                    throw new IllegalArgumentException("JSON forbids NaN and infinities: " + obj);
                }
            }
            this.put(new JsonPrimitive(obj));
            return this;
        }
        return this.nullValue();
    }
    
    @Override
    public JsonWriter value(final String s) throws IOException {
        if (s != null) {
            this.put(new JsonPrimitive(s));
            return this;
        }
        return this.nullValue();
    }
    
    @Override
    public JsonWriter value(final boolean b) throws IOException {
        this.put(new JsonPrimitive(b));
        return this;
    }
}
