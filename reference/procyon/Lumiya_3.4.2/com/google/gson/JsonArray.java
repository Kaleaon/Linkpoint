// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public final class JsonArray extends JsonElement implements Iterable<JsonElement>
{
    private final List<JsonElement> elements;
    
    public JsonArray() {
        this.elements = new ArrayList<JsonElement>();
    }
    
    public JsonArray(final int initialCapacity) {
        this.elements = new ArrayList<JsonElement>(initialCapacity);
    }
    
    public void add(JsonElement instance) {
        if (instance == null) {
            instance = JsonNull.INSTANCE;
        }
        this.elements.add(instance);
    }
    
    public void add(final Boolean b) {
        final List<JsonElement> elements = this.elements;
        JsonElement instance;
        if (b != null) {
            instance = new JsonPrimitive(b);
        }
        else {
            instance = JsonNull.INSTANCE;
        }
        elements.add(instance);
    }
    
    public void add(final Character c) {
        final List<JsonElement> elements = this.elements;
        JsonElement instance;
        if (c != null) {
            instance = new JsonPrimitive(c);
        }
        else {
            instance = JsonNull.INSTANCE;
        }
        elements.add(instance);
    }
    
    public void add(final Number n) {
        final List<JsonElement> elements = this.elements;
        JsonElement instance;
        if (n != null) {
            instance = new JsonPrimitive(n);
        }
        else {
            instance = JsonNull.INSTANCE;
        }
        elements.add(instance);
    }
    
    public void add(final String s) {
        final List<JsonElement> elements = this.elements;
        JsonElement instance;
        if (s != null) {
            instance = new JsonPrimitive(s);
        }
        else {
            instance = JsonNull.INSTANCE;
        }
        elements.add(instance);
    }
    
    public void addAll(final JsonArray jsonArray) {
        this.elements.addAll(jsonArray.elements);
    }
    
    public boolean contains(final JsonElement jsonElement) {
        return this.elements.contains(jsonElement);
    }
    
    @Override
    JsonArray deepCopy() {
        if (this.elements.isEmpty()) {
            return new JsonArray();
        }
        final JsonArray jsonArray = new JsonArray(this.elements.size());
        final Iterator<JsonElement> iterator = this.elements.iterator();
        while (iterator.hasNext()) {
            jsonArray.add(iterator.next().deepCopy());
        }
        return jsonArray;
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = false;
        if (o != this) {
            boolean b2 = b;
            if (!(o instanceof JsonArray)) {
                return b2;
            }
            if (!((JsonArray)o).elements.equals(this.elements)) {
                b2 = b;
                return b2;
            }
        }
        return true;
    }
    
    public JsonElement get(final int n) {
        return this.elements.get(n);
    }
    
    @Override
    public BigDecimal getAsBigDecimal() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsBigDecimal();
    }
    
    @Override
    public BigInteger getAsBigInteger() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsBigInteger();
    }
    
    @Override
    public boolean getAsBoolean() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsBoolean();
    }
    
    @Override
    public byte getAsByte() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsByte();
    }
    
    @Override
    public char getAsCharacter() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsCharacter();
    }
    
    @Override
    public double getAsDouble() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsDouble();
    }
    
    @Override
    public float getAsFloat() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsFloat();
    }
    
    @Override
    public int getAsInt() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsInt();
    }
    
    @Override
    public long getAsLong() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsLong();
    }
    
    @Override
    public Number getAsNumber() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsNumber();
    }
    
    @Override
    public short getAsShort() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsShort();
    }
    
    @Override
    public String getAsString() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsString();
    }
    
    @Override
    public int hashCode() {
        return this.elements.hashCode();
    }
    
    @Override
    public Iterator<JsonElement> iterator() {
        return this.elements.iterator();
    }
    
    public JsonElement remove(final int n) {
        return this.elements.remove(n);
    }
    
    public boolean remove(final JsonElement jsonElement) {
        return this.elements.remove(jsonElement);
    }
    
    public JsonElement set(final int n, final JsonElement jsonElement) {
        return this.elements.set(n, jsonElement);
    }
    
    public int size() {
        return this.elements.size();
    }
}
