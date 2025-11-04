// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal.bind;

import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import com.google.gson.stream.JsonReader;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

final class TypeAdapterRuntimeTypeWrapper<T> extends TypeAdapter<T>
{
    private final Gson context;
    private final TypeAdapter<T> delegate;
    private final Type type;
    
    TypeAdapterRuntimeTypeWrapper(final Gson context, final TypeAdapter<T> delegate, final Type type) {
        this.context = context;
        this.delegate = delegate;
        this.type = type;
    }
    
    private Type getRuntimeTypeIfMoreSpecific(Type class1, final Object o) {
        if (o != null) {
            if (class1 == Object.class || class1 instanceof TypeVariable || class1 instanceof Class) {
                class1 = o.getClass();
            }
        }
        return class1;
    }
    
    @Override
    public T read(final JsonReader jsonReader) throws IOException {
        return this.delegate.read(jsonReader);
    }
    
    @Override
    public void write(final JsonWriter jsonWriter, final T t) throws IOException {
        TypeAdapter<?> typeAdapter = this.delegate;
        final Type runtimeTypeIfMoreSpecific = this.getRuntimeTypeIfMoreSpecific(this.type, t);
        if (runtimeTypeIfMoreSpecific != this.type) {
            final TypeAdapter<?> typeAdapter2 = typeAdapter = this.context.getAdapter(TypeToken.get(runtimeTypeIfMoreSpecific));
            if (typeAdapter2 instanceof ReflectiveTypeAdapterFactory.Adapter) {
                typeAdapter = typeAdapter2;
                if (!(this.delegate instanceof ReflectiveTypeAdapterFactory.Adapter)) {
                    typeAdapter = this.delegate;
                }
            }
        }
        typeAdapter.write(jsonWriter, t);
    }
}
