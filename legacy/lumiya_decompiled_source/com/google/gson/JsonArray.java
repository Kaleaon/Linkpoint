package com.google.gson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public final class JsonArray extends JsonElement implements Iterable<JsonElement> {
    private final List<JsonElement> elements;

    public JsonArray() {
        this.elements = new ArrayList();
    }

    public JsonArray(int i) {
        this.elements = new ArrayList(i);
    }

    public void add(JsonElement jsonElement) {
        if (jsonElement == null) {
            jsonElement = JsonNull.INSTANCE;
        }
        this.elements.add(jsonElement);
    }

    public void add(Boolean bool) {
        this.elements.add(bool != null ? new JsonPrimitive(bool) : JsonNull.INSTANCE);
    }

    public void add(Character ch) {
        this.elements.add(ch != null ? new JsonPrimitive(ch) : JsonNull.INSTANCE);
    }

    public void add(Number number) {
        this.elements.add(number != null ? new JsonPrimitive(number) : JsonNull.INSTANCE);
    }

    public void add(String str) {
        this.elements.add(str != null ? new JsonPrimitive(str) : JsonNull.INSTANCE);
    }

    public void addAll(JsonArray jsonArray) {
        this.elements.addAll(jsonArray.elements);
    }

    public boolean contains(JsonElement jsonElement) {
        return this.elements.contains(jsonElement);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.gson.JsonElement
    public JsonArray deepCopy() {
        if (this.elements.isEmpty()) {
            return new JsonArray();
        }
        JsonArray jsonArray = new JsonArray(this.elements.size());
        for (JsonElement jsonElement : this.elements) {
            jsonArray.add(jsonElement.deepCopy());
        }
        return jsonArray;
    }

    public boolean equals(Object obj) {
        return obj == this || ((obj instanceof JsonArray) && ((JsonArray) obj).elements.equals(this.elements));
    }

    public JsonElement get(int i) {
        return this.elements.get(i);
    }

    @Override // com.google.gson.JsonElement
    public BigDecimal getAsBigDecimal() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsBigDecimal();
    }

    @Override // com.google.gson.JsonElement
    public BigInteger getAsBigInteger() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsBigInteger();
    }

    @Override // com.google.gson.JsonElement
    public boolean getAsBoolean() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsBoolean();
    }

    @Override // com.google.gson.JsonElement
    public byte getAsByte() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsByte();
    }

    @Override // com.google.gson.JsonElement
    public char getAsCharacter() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsCharacter();
    }

    @Override // com.google.gson.JsonElement
    public double getAsDouble() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsDouble();
    }

    @Override // com.google.gson.JsonElement
    public float getAsFloat() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsFloat();
    }

    @Override // com.google.gson.JsonElement
    public int getAsInt() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsInt();
    }

    @Override // com.google.gson.JsonElement
    public long getAsLong() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsLong();
    }

    @Override // com.google.gson.JsonElement
    public Number getAsNumber() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsNumber();
    }

    @Override // com.google.gson.JsonElement
    public short getAsShort() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsShort();
    }

    @Override // com.google.gson.JsonElement
    public String getAsString() {
        if (this.elements.size() != 1) {
            throw new IllegalStateException();
        }
        return this.elements.get(0).getAsString();
    }

    public int hashCode() {
        return this.elements.hashCode();
    }

    @Override // java.lang.Iterable
    public Iterator<JsonElement> iterator() {
        return this.elements.iterator();
    }

    public JsonElement remove(int i) {
        return this.elements.remove(i);
    }

    public boolean remove(JsonElement jsonElement) {
        return this.elements.remove(jsonElement);
    }

    public JsonElement set(int i, JsonElement jsonElement) {
        return this.elements.set(i, jsonElement);
    }

    public int size() {
        return this.elements.size();
    }
}
