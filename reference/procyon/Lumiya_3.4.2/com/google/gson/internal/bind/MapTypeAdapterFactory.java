// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal.bind;

import java.util.Iterator;
import com.google.gson.internal.Streams;
import java.util.ArrayList;
import com.google.gson.stream.JsonWriter;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import com.google.gson.stream.JsonReader;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonElement;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.$Gson$Types;
import java.util.Map;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.TypeAdapterFactory;

public final class MapTypeAdapterFactory implements TypeAdapterFactory
{
    final boolean complexMapKeySerialization;
    private final ConstructorConstructor constructorConstructor;
    
    public MapTypeAdapterFactory(final ConstructorConstructor constructorConstructor, final boolean complexMapKeySerialization) {
        this.constructorConstructor = constructorConstructor;
        this.complexMapKeySerialization = complexMapKeySerialization;
    }
    
    private TypeAdapter<?> getKeyAdapter(final Gson gson, final Type type) {
        TypeAdapter<?> typeAdapter;
        if (type != Boolean.TYPE && type != Boolean.class) {
            typeAdapter = gson.getAdapter(TypeToken.get(type));
        }
        else {
            typeAdapter = TypeAdapters.BOOLEAN_AS_STRING;
        }
        return typeAdapter;
    }
    
    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
        final Type type = typeToken.getType();
        if (Map.class.isAssignableFrom(typeToken.getRawType())) {
            final Type[] mapKeyAndValueTypes = $Gson$Types.getMapKeyAndValueTypes(type, $Gson$Types.getRawType(type));
            return (TypeAdapter<T>)new Adapter(gson, mapKeyAndValueTypes[0], (TypeAdapter<Object>)this.getKeyAdapter(gson, mapKeyAndValueTypes[0]), mapKeyAndValueTypes[1], (TypeAdapter<Object>)gson.getAdapter(TypeToken.get(mapKeyAndValueTypes[1])), (ObjectConstructor<? extends Map<Object, Object>>)this.constructorConstructor.get(typeToken));
        }
        return null;
    }
    
    private final class Adapter<K, V> extends TypeAdapter<Map<K, V>>
    {
        private final ObjectConstructor<? extends Map<K, V>> constructor;
        private final TypeAdapter<K> keyTypeAdapter;
        private final TypeAdapter<V> valueTypeAdapter;
        
        public Adapter(final Gson gson, final Type type, final TypeAdapter<K> typeAdapter, final Type type2, final TypeAdapter<V> typeAdapter2, final ObjectConstructor<? extends Map<K, V>> constructor) {
            this.keyTypeAdapter = new TypeAdapterRuntimeTypeWrapper<K>(gson, typeAdapter, type);
            this.valueTypeAdapter = new TypeAdapterRuntimeTypeWrapper<V>(gson, typeAdapter2, type2);
            this.constructor = constructor;
        }
        
        private String keyToString(final JsonElement jsonElement) {
            if (!jsonElement.isJsonPrimitive()) {
                if (!jsonElement.isJsonNull()) {
                    throw new AssertionError();
                }
                return "null";
            }
            else {
                final JsonPrimitive asJsonPrimitive = jsonElement.getAsJsonPrimitive();
                if (asJsonPrimitive.isNumber()) {
                    return String.valueOf(asJsonPrimitive.getAsNumber());
                }
                if (asJsonPrimitive.isBoolean()) {
                    return Boolean.toString(asJsonPrimitive.getAsBoolean());
                }
                if (!asJsonPrimitive.isString()) {
                    throw new AssertionError();
                }
                return asJsonPrimitive.getAsString();
            }
        }
        
        @Override
        public Map<K, V> read(final JsonReader jsonReader) throws IOException {
            final JsonToken peek = jsonReader.peek();
            if (peek != JsonToken.NULL) {
                final Map map = (Map)this.constructor.construct();
                if (peek != JsonToken.BEGIN_ARRAY) {
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        JsonReaderInternalAccess.INSTANCE.promoteNameToValue(jsonReader);
                        final K read = this.keyTypeAdapter.read(jsonReader);
                        if (map.put(read, this.valueTypeAdapter.read(jsonReader)) != null) {
                            throw new JsonSyntaxException("duplicate key: " + read);
                        }
                    }
                    jsonReader.endObject();
                }
                else {
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        jsonReader.beginArray();
                        final K read2 = this.keyTypeAdapter.read(jsonReader);
                        if (map.put(read2, this.valueTypeAdapter.read(jsonReader)) != null) {
                            throw new JsonSyntaxException("duplicate key: " + read2);
                        }
                        jsonReader.endArray();
                    }
                    jsonReader.endArray();
                }
                return map;
            }
            jsonReader.nextNull();
            return null;
        }
        
        @Override
        public void write(final JsonWriter jsonWriter, final Map<K, V> map) throws IOException {
            final int n = 0;
            final int n2 = 0;
            if (map == null) {
                jsonWriter.nullValue();
                return;
            }
            if (MapTypeAdapterFactory.this.complexMapKeySerialization) {
                final ArrayList list = new ArrayList(map.size());
                final ArrayList list2 = new ArrayList(map.size());
                final Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
                boolean b = false;
                while (iterator.hasNext()) {
                    final Map.Entry<K, V> entry = (Map.Entry<K, V>)iterator.next();
                    final JsonElement jsonTree = this.keyTypeAdapter.toJsonTree(entry.getKey());
                    list.add(jsonTree);
                    list2.add(entry.getValue());
                    boolean b2;
                    if (!jsonTree.isJsonArray() && !jsonTree.isJsonObject()) {
                        b2 = false;
                    }
                    else {
                        b2 = true;
                    }
                    b |= b2;
                }
                if (!b) {
                    jsonWriter.beginObject();
                    for (int size = list.size(), i = n2; i < size; ++i) {
                        jsonWriter.name(this.keyToString((JsonElement)list.get(i)));
                        this.valueTypeAdapter.write(jsonWriter, (V)list2.get(i));
                    }
                    jsonWriter.endObject();
                }
                else {
                    jsonWriter.beginArray();
                    for (int size2 = list.size(), j = n; j < size2; ++j) {
                        jsonWriter.beginArray();
                        Streams.write((JsonElement)list.get(j), jsonWriter);
                        this.valueTypeAdapter.write(jsonWriter, (V)list2.get(j));
                        jsonWriter.endArray();
                    }
                    jsonWriter.endArray();
                }
                return;
            }
            jsonWriter.beginObject();
            for (final Map.Entry<Object, V> entry2 : map.entrySet()) {
                jsonWriter.name(String.valueOf(entry2.getKey()));
                this.valueTypeAdapter.write(jsonWriter, (V)entry2.getValue());
            }
            jsonWriter.endObject();
        }
    }
}
