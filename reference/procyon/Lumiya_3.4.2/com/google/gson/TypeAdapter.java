// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson;

import com.google.gson.internal.bind.JsonTreeWriter;
import java.io.Writer;
import java.io.StringWriter;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;
import com.google.gson.internal.bind.JsonTreeReader;
import java.io.StringReader;
import java.io.IOException;
import com.google.gson.stream.JsonReader;
import java.io.Reader;

public abstract class TypeAdapter<T>
{
    public final T fromJson(final Reader reader) throws IOException {
        return this.read(new JsonReader(reader));
    }
    
    public final T fromJson(final String s) throws IOException {
        return this.fromJson(new StringReader(s));
    }
    
    public final T fromJsonTree(final JsonElement jsonElement) {
        try {
            return this.read(new JsonTreeReader(jsonElement));
        }
        catch (final IOException ex) {
            throw new JsonIOException(ex);
        }
    }
    
    public final TypeAdapter<T> nullSafe() {
        return new TypeAdapter<T>() {
            @Override
            public T read(final JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() != JsonToken.NULL) {
                    return TypeAdapter.this.read(jsonReader);
                }
                jsonReader.nextNull();
                return null;
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final T t) throws IOException {
                if (t != null) {
                    TypeAdapter.this.write(jsonWriter, t);
                }
                else {
                    jsonWriter.nullValue();
                }
            }
        };
    }
    
    public abstract T read(final JsonReader p0) throws IOException;
    
    public final String toJson(final T t) {
        final StringWriter stringWriter = new StringWriter();
        try {
            this.toJson(stringWriter, t);
            return stringWriter.toString();
        }
        catch (final IOException detailMessage) {
            throw new AssertionError((Object)detailMessage);
        }
    }
    
    public final void toJson(final Writer writer, final T t) throws IOException {
        this.write(new JsonWriter(writer), t);
    }
    
    public final JsonElement toJsonTree(final T t) {
        try {
            final JsonTreeWriter jsonTreeWriter = new JsonTreeWriter();
            this.write(jsonTreeWriter, t);
            return jsonTreeWriter.get();
        }
        catch (final IOException ex) {
            throw new JsonIOException(ex);
        }
    }
    
    public abstract void write(final JsonWriter p0, final T p1) throws IOException;
}
