// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal.bind;

import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import com.google.gson.JsonSerializationContext;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.JsonSerializer;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.TypeAdapter;

public final class TreeTypeAdapter<T> extends TypeAdapter<T>
{
    private final GsonContextImpl context;
    private TypeAdapter<T> delegate;
    private final JsonDeserializer<T> deserializer;
    final Gson gson;
    private final JsonSerializer<T> serializer;
    private final TypeAdapterFactory skipPast;
    private final TypeToken<T> typeToken;
    
    public TreeTypeAdapter(final JsonSerializer<T> serializer, final JsonDeserializer<T> deserializer, final Gson gson, final TypeToken<T> typeToken, final TypeAdapterFactory skipPast) {
        this.context = new GsonContextImpl();
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.gson = gson;
        this.typeToken = typeToken;
        this.skipPast = skipPast;
    }
    
    private TypeAdapter<T> delegate() {
        TypeAdapter<T> delegate;
        if ((delegate = this.delegate) == null) {
            delegate = this.gson.getDelegateAdapter(this.skipPast, this.typeToken);
            this.delegate = delegate;
        }
        return delegate;
    }
    
    public static TypeAdapterFactory newFactory(final TypeToken<?> typeToken, final Object o) {
        return new SingleTypeFactory(o, typeToken, false, null);
    }
    
    public static TypeAdapterFactory newFactoryWithMatchRawType(final TypeToken<?> typeToken, final Object o) {
        return new SingleTypeFactory(o, typeToken, typeToken.getType() == typeToken.getRawType(), null);
    }
    
    public static TypeAdapterFactory newTypeHierarchyFactory(final Class<?> clazz, final Object o) {
        return new SingleTypeFactory(o, null, false, clazz);
    }
    
    @Override
    public T read(final JsonReader jsonReader) throws IOException {
        if (this.deserializer == null) {
            return this.delegate().read(jsonReader);
        }
        final JsonElement parse = Streams.parse(jsonReader);
        if (!parse.isJsonNull()) {
            return this.deserializer.deserialize(parse, this.typeToken.getType(), this.context);
        }
        return null;
    }
    
    @Override
    public void write(final JsonWriter jsonWriter, final T t) throws IOException {
        if (this.serializer == null) {
            this.delegate().write(jsonWriter, t);
            return;
        }
        if (t != null) {
            Streams.write(this.serializer.serialize(t, this.typeToken.getType(), this.context), jsonWriter);
            return;
        }
        jsonWriter.nullValue();
    }
    
    private final class GsonContextImpl implements JsonSerializationContext, JsonDeserializationContext
    {
        @Override
        public <R> R deserialize(final JsonElement jsonElement, final Type type) throws JsonParseException {
            return TreeTypeAdapter.this.gson.fromJson(jsonElement, type);
        }
        
        @Override
        public JsonElement serialize(final Object o) {
            return TreeTypeAdapter.this.gson.toJsonTree(o);
        }
        
        @Override
        public JsonElement serialize(final Object o, final Type type) {
            return TreeTypeAdapter.this.gson.toJsonTree(o, type);
        }
    }
    
    private static final class SingleTypeFactory implements TypeAdapterFactory
    {
        private final JsonDeserializer<?> deserializer;
        private final TypeToken<?> exactType;
        private final Class<?> hierarchyType;
        private final boolean matchRawType;
        private final JsonSerializer<?> serializer;
        
        SingleTypeFactory(final Object o, final TypeToken<?> exactType, final boolean matchRawType, final Class<?> hierarchyType) {
            final JsonDeserializer<?> jsonDeserializer = null;
            JsonSerializer<?> serializer;
            if (!(o instanceof JsonSerializer)) {
                serializer = null;
            }
            else {
                serializer = (JsonSerializer)o;
            }
            this.serializer = serializer;
            JsonDeserializer<?> deserializer;
            if (!(o instanceof JsonDeserializer)) {
                deserializer = jsonDeserializer;
            }
            else {
                deserializer = (JsonDeserializer)o;
            }
            this.deserializer = deserializer;
            $Gson$Preconditions.checkArgument(this.serializer != null || this.deserializer != null);
            this.exactType = exactType;
            this.matchRawType = matchRawType;
            this.hierarchyType = hierarchyType;
        }
        
        @Override
        public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
            final boolean b = false;
            int assignable = 0;
            Label_0022: {
                if (this.exactType == null) {
                    assignable = (this.hierarchyType.isAssignableFrom(typeToken.getRawType()) ? 1 : 0);
                }
                else {
                    if (!this.exactType.equals(typeToken)) {
                        assignable = (b ? 1 : 0);
                        if (!this.matchRawType) {
                            break Label_0022;
                        }
                        if (this.exactType.getType() != typeToken.getRawType()) {
                            assignable = (b ? 1 : 0);
                            break Label_0022;
                        }
                    }
                    assignable = 1;
                }
            }
            TypeAdapter<T> typeAdapter;
            if (assignable == 0) {
                typeAdapter = null;
            }
            else {
                typeAdapter = new TreeTypeAdapter<T>((JsonSerializer<T>)this.serializer, (JsonDeserializer<T>)this.deserializer, gson, typeToken, this);
            }
            return typeAdapter;
        }
    }
}
