// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal.bind;

import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import com.google.gson.stream.JsonReader;
import java.text.ParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.bind.util.ISO8601Utils;
import java.text.ParsePosition;
import java.util.Locale;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import java.text.DateFormat;
import com.google.gson.TypeAdapterFactory;
import java.util.Date;
import com.google.gson.TypeAdapter;

public final class DateTypeAdapter extends TypeAdapter<Date>
{
    public static final TypeAdapterFactory FACTORY;
    private final DateFormat enUsFormat;
    private final DateFormat localFormat;
    
    static {
        FACTORY = new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
                Object o;
                if (typeToken.getRawType() != Date.class) {
                    o = null;
                }
                else {
                    o = new DateTypeAdapter();
                }
                return (TypeAdapter<T>)o;
            }
        };
    }
    
    public DateTypeAdapter() {
        this.enUsFormat = DateFormat.getDateTimeInstance(2, 2, Locale.US);
        this.localFormat = DateFormat.getDateTimeInstance(2, 2);
    }
    
    private Date deserializeToDate(final String s) {
        synchronized (this) {
            try {
                return this.localFormat.parse(s);
            }
            catch (final ParseException ex) {
                try {
                    return this.enUsFormat.parse(s);
                }
                catch (final ParseException ex2) {
                    try {
                        final Date parse = ISO8601Utils.parse(s, new ParsePosition(0));
                        monitorexit(this);
                        return parse;
                    }
                    catch (final ParseException ex3) {
                        throw new JsonSyntaxException(s, ex3);
                    }
                }
            }
        }
    }
    
    @Override
    public Date read(final JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() != JsonToken.NULL) {
            return this.deserializeToDate(jsonReader.nextString());
        }
        jsonReader.nextNull();
        return null;
    }
    
    @Override
    public void write(final JsonWriter jsonWriter, final Date date) throws IOException {
        monitorenter(this);
        Label_0022: {
            if (date == null) {
                break Label_0022;
            }
            try {
                jsonWriter.value(this.enUsFormat.format(date));
                return;
                jsonWriter.nullValue();
            }
            finally {
                monitorexit(this);
            }
        }
    }
}
