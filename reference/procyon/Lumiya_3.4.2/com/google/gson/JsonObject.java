// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson;

import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import com.google.gson.internal.LinkedTreeMap;

public final class JsonObject extends JsonElement
{
    private final LinkedTreeMap<String, JsonElement> members;
    
    public JsonObject() {
        this.members = new LinkedTreeMap<String, JsonElement>();
    }
    
    private JsonElement createJsonElement(final Object o) {
        JsonElement instance;
        if (o != null) {
            instance = new JsonPrimitive(o);
        }
        else {
            instance = JsonNull.INSTANCE;
        }
        return instance;
    }
    
    public void add(final String s, JsonElement instance) {
        if (instance == null) {
            instance = JsonNull.INSTANCE;
        }
        this.members.put(s, instance);
    }
    
    public void addProperty(final String s, final Boolean b) {
        this.add(s, this.createJsonElement(b));
    }
    
    public void addProperty(final String s, final Character c) {
        this.add(s, this.createJsonElement(c));
    }
    
    public void addProperty(final String s, final Number n) {
        this.add(s, this.createJsonElement(n));
    }
    
    public void addProperty(final String s, final String s2) {
        this.add(s, this.createJsonElement(s2));
    }
    
    @Override
    JsonObject deepCopy() {
        final JsonObject jsonObject = new JsonObject();
        for (final Map.Entry<String, V> entry : this.members.entrySet()) {
            jsonObject.add(entry.getKey(), ((JsonElement)entry.getValue()).deepCopy());
        }
        return jsonObject;
    }
    
    public Set<Map.Entry<String, JsonElement>> entrySet() {
        return this.members.entrySet();
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = false;
        if (o != this) {
            boolean b2 = b;
            if (!(o instanceof JsonObject)) {
                return b2;
            }
            if (!((JsonObject)o).members.equals(this.members)) {
                b2 = b;
                return b2;
            }
        }
        return true;
    }
    
    public JsonElement get(final String s) {
        return this.members.get(s);
    }
    
    public JsonArray getAsJsonArray(final String s) {
        return this.members.get(s);
    }
    
    public JsonObject getAsJsonObject(final String s) {
        return this.members.get(s);
    }
    
    public JsonPrimitive getAsJsonPrimitive(final String s) {
        return this.members.get(s);
    }
    
    public boolean has(final String s) {
        return this.members.containsKey(s);
    }
    
    @Override
    public int hashCode() {
        return this.members.hashCode();
    }
    
    public Set<String> keySet() {
        return this.members.keySet();
    }
    
    public JsonElement remove(final String s) {
        return this.members.remove(s);
    }
    
    public int size() {
        return this.members.size();
    }
}
