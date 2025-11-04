// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson;

import java.io.EOFException;
import java.util.NoSuchElementException;
import com.google.gson.internal.Streams;
import java.io.IOException;
import com.google.gson.stream.MalformedJsonException;
import com.google.gson.stream.JsonToken;
import java.io.StringReader;
import java.io.Reader;
import com.google.gson.stream.JsonReader;
import java.util.Iterator;

public final class JsonStreamParser implements Iterator<JsonElement>
{
    private final Object lock;
    private final JsonReader parser;
    
    public JsonStreamParser(final Reader reader) {
        (this.parser = new JsonReader(reader)).setLenient(true);
        this.lock = new Object();
    }
    
    public JsonStreamParser(final String s) {
        this(new StringReader(s));
    }
    
    @Override
    public boolean hasNext() {
        synchronized (this.lock) {
            try {
                return this.parser.peek() != JsonToken.END_DOCUMENT;
            }
            catch (final MalformedJsonException ex) {
                throw new JsonSyntaxException(ex);
            }
            catch (final IOException ex2) {
                throw new JsonIOException(ex2);
            }
        }
    }
    
    @Override
    public JsonElement next() throws JsonParseException {
        Label_0017: {
            if (!this.hasNext()) {
                break Label_0017;
            }
            try {
                return Streams.parse(this.parser);
                throw new NoSuchElementException();
            }
            catch (final StackOverflowError stackOverflowError) {
                throw new JsonParseException("Failed parsing JSON source to Json", stackOverflowError);
            }
            catch (final OutOfMemoryError outOfMemoryError) {
                throw new JsonParseException("Failed parsing JSON source to Json", outOfMemoryError);
            }
            catch (final JsonParseException ex) {
                if (ex.getCause() instanceof EOFException) {
                    ex = new NoSuchElementException();
                }
                throw ex;
            }
        }
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
