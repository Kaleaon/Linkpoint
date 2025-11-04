// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal.bind;

import java.util.Iterator;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import com.google.gson.stream.JsonReader;
import java.lang.reflect.Type;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.$Gson$Types;
import java.util.Collection;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.TypeAdapterFactory;

public final class CollectionTypeAdapterFactory implements TypeAdapterFactory
{
    private final ConstructorConstructor constructorConstructor;
    
    public CollectionTypeAdapterFactory(final ConstructorConstructor constructorConstructor) {
        this.constructorConstructor = constructorConstructor;
    }
    
    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
        final Type type = typeToken.getType();
        final Class rawType = typeToken.getRawType();
        if (Collection.class.isAssignableFrom(rawType)) {
            final Type collectionElementType = $Gson$Types.getCollectionElementType(type, rawType);
            return (TypeAdapter<T>)new Adapter(gson, collectionElementType, (TypeAdapter<Object>)gson.getAdapter(TypeToken.get(collectionElementType)), (ObjectConstructor<? extends Collection<Object>>)this.constructorConstructor.get((TypeToken<? extends Collection<E>>)typeToken));
        }
        return null;
    }
    
    private static final class Adapter<E> extends TypeAdapter<Collection<E>>
    {
        private final ObjectConstructor<? extends Collection<E>> constructor;
        private final TypeAdapter<E> elementTypeAdapter;
        
        public Adapter(final Gson gson, final Type type, final TypeAdapter<E> typeAdapter, final ObjectConstructor<? extends Collection<E>> constructor) {
            this.elementTypeAdapter = new TypeAdapterRuntimeTypeWrapper<E>(gson, typeAdapter, type);
            this.constructor = constructor;
        }
        
        @Override
        public Collection<E> read(final JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() != JsonToken.NULL) {
                final Collection collection = (Collection)this.constructor.construct();
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    collection.add(this.elementTypeAdapter.read(jsonReader));
                }
                jsonReader.endArray();
                return collection;
            }
            jsonReader.nextNull();
            return null;
        }
        
        @Override
        public void write(final JsonWriter jsonWriter, final Collection<E> collection) throws IOException {
            if (collection != null) {
                jsonWriter.beginArray();
                final Iterator<E> iterator = collection.iterator();
                while (iterator.hasNext()) {
                    this.elementTypeAdapter.write(jsonWriter, iterator.next());
                }
                jsonWriter.endArray();
                return;
            }
            jsonWriter.nullValue();
        }
    }
}
