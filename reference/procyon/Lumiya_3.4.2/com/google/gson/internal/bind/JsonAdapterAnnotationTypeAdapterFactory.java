// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal.bind;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.TypeAdapterFactory;

public final class JsonAdapterAnnotationTypeAdapterFactory implements TypeAdapterFactory
{
    private final ConstructorConstructor constructorConstructor;
    
    public JsonAdapterAnnotationTypeAdapterFactory(final ConstructorConstructor constructorConstructor) {
        this.constructorConstructor = constructorConstructor;
    }
    
    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
        final JsonAdapter jsonAdapter = typeToken.getRawType().getAnnotation(JsonAdapter.class);
        if (jsonAdapter != null) {
            return (TypeAdapter<T>)this.getTypeAdapter(this.constructorConstructor, gson, typeToken, jsonAdapter);
        }
        return null;
    }
    
    TypeAdapter<?> getTypeAdapter(final ConstructorConstructor constructorConstructor, final Gson gson, final TypeToken<?> typeToken, final JsonAdapter jsonAdapter) {
        final TypeAdapter<?> construct = constructorConstructor.get((TypeToken<TypeAdapter<?>>)TypeToken.get(jsonAdapter.value())).construct();
        TypeAdapter<?> create;
        if (!(construct instanceof TypeAdapter)) {
            if (!(construct instanceof TypeAdapterFactory)) {
                if (!(construct instanceof JsonSerializer) && !(construct instanceof JsonDeserializer)) {
                    throw new IllegalArgumentException("Invalid attempt to bind an instance of " + construct.getClass().getName() + " as a @JsonAdapter for " + typeToken.toString() + ". @JsonAdapter value must be a TypeAdapter, TypeAdapterFactory," + " JsonSerializer or JsonDeserializer.");
                }
                JsonSerializer<Object> jsonSerializer;
                if (!(construct instanceof JsonSerializer)) {
                    jsonSerializer = null;
                }
                else {
                    jsonSerializer = (JsonSerializer<Object>)construct;
                }
                JsonDeserializer<Object> jsonDeserializer;
                if (!(construct instanceof JsonDeserializer)) {
                    jsonDeserializer = null;
                }
                else {
                    jsonDeserializer = (JsonDeserializer<Object>)construct;
                }
                create = new TreeTypeAdapter<Object>(jsonSerializer, jsonDeserializer, gson, typeToken, null);
            }
            else {
                create = ((TypeAdapterFactory)construct).create(gson, typeToken);
            }
        }
        else {
            create = construct;
        }
        TypeAdapter<?> nullSafe;
        if (create == null) {
            nullSafe = create;
        }
        else {
            nullSafe = create;
            if (jsonAdapter.nullSafe()) {
                nullSafe = create.nullSafe();
            }
        }
        return nullSafe;
    }
}
