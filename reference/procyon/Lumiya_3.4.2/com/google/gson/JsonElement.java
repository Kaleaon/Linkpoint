// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson;

import java.io.IOException;
import com.google.gson.internal.Streams;
import java.io.Writer;
import com.google.gson.stream.JsonWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.math.BigDecimal;

public abstract class JsonElement
{
    abstract JsonElement deepCopy();
    
    public BigDecimal getAsBigDecimal() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }
    
    public BigInteger getAsBigInteger() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }
    
    public boolean getAsBoolean() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }
    
    Boolean getAsBooleanWrapper() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }
    
    public byte getAsByte() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }
    
    public char getAsCharacter() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }
    
    public double getAsDouble() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }
    
    public float getAsFloat() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }
    
    public int getAsInt() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }
    
    public JsonArray getAsJsonArray() {
        if (!this.isJsonArray()) {
            throw new IllegalStateException("Not a JSON Array: " + this);
        }
        return (JsonArray)this;
    }
    
    public JsonNull getAsJsonNull() {
        if (!this.isJsonNull()) {
            throw new IllegalStateException("Not a JSON Null: " + this);
        }
        return (JsonNull)this;
    }
    
    public JsonObject getAsJsonObject() {
        if (!this.isJsonObject()) {
            throw new IllegalStateException("Not a JSON Object: " + this);
        }
        return (JsonObject)this;
    }
    
    public JsonPrimitive getAsJsonPrimitive() {
        if (!this.isJsonPrimitive()) {
            throw new IllegalStateException("Not a JSON Primitive: " + this);
        }
        return (JsonPrimitive)this;
    }
    
    public long getAsLong() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }
    
    public Number getAsNumber() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }
    
    public short getAsShort() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }
    
    public String getAsString() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }
    
    public boolean isJsonArray() {
        return this instanceof JsonArray;
    }
    
    public boolean isJsonNull() {
        return this instanceof JsonNull;
    }
    
    public boolean isJsonObject() {
        return this instanceof JsonObject;
    }
    
    public boolean isJsonPrimitive() {
        return this instanceof JsonPrimitive;
    }
    
    @Override
    public String toString() {
        try {
            final StringWriter stringWriter = new StringWriter();
            final JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.setLenient(true);
            Streams.write(this, jsonWriter);
            return stringWriter.toString();
        }
        catch (final IOException detailMessage) {
            throw new AssertionError((Object)detailMessage);
        }
    }
}
