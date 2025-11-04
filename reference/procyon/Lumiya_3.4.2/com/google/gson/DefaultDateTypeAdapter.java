// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson;

import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import com.google.gson.stream.JsonReader;
import java.text.ParseException;
import com.google.gson.internal.bind.util.ISO8601Utils;
import java.text.ParsePosition;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.text.DateFormat;
import java.util.Date;

final class DefaultDateTypeAdapter extends TypeAdapter<Date>
{
    private static final String SIMPLE_NAME = "DefaultDateTypeAdapter";
    private final Class<? extends Date> dateType;
    private final DateFormat enUsFormat;
    private final DateFormat localFormat;
    
    public DefaultDateTypeAdapter(final int n, final int n2) {
        this(Date.class, DateFormat.getDateTimeInstance(n, n2, Locale.US), DateFormat.getDateTimeInstance(n, n2));
    }
    
    DefaultDateTypeAdapter(final Class<? extends Date> clazz) {
        this(clazz, DateFormat.getDateTimeInstance(2, 2, Locale.US), DateFormat.getDateTimeInstance(2, 2));
    }
    
    DefaultDateTypeAdapter(final Class<? extends Date> clazz, final int n) {
        this(clazz, DateFormat.getDateInstance(n, Locale.US), DateFormat.getDateInstance(n));
    }
    
    public DefaultDateTypeAdapter(final Class<? extends Date> clazz, final int n, final int n2) {
        this(clazz, DateFormat.getDateTimeInstance(n, n2, Locale.US), DateFormat.getDateTimeInstance(n, n2));
    }
    
    DefaultDateTypeAdapter(final Class<? extends Date> clazz, final String s) {
        this(clazz, new SimpleDateFormat(s, Locale.US), new SimpleDateFormat(s));
    }
    
    DefaultDateTypeAdapter(final Class<? extends Date> clazz, final DateFormat enUsFormat, final DateFormat localFormat) {
        if (clazz != Date.class && clazz != java.sql.Date.class && clazz != Timestamp.class) {
            throw new IllegalArgumentException("Date type must be one of " + Date.class + ", " + Timestamp.class + ", or " + java.sql.Date.class + " but was " + clazz);
        }
        this.dateType = clazz;
        this.enUsFormat = enUsFormat;
        this.localFormat = localFormat;
    }
    
    private Date deserializeToDate(final String s) {
        final DateFormat localFormat = this.localFormat;
        monitorenter(localFormat);
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
                    monitorexit(localFormat);
                    return parse;
                }
                catch (final ParseException ex3) {
                    throw new JsonSyntaxException(s, ex3);
                    try {}
                    finally {
                        monitorexit(localFormat);
                    }
                }
            }
        }
    }
    
    @Override
    public Date read(final JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() != JsonToken.STRING) {
            throw new JsonParseException("The date should be a string value");
        }
        final Date deserializeToDate = this.deserializeToDate(jsonReader.nextString());
        if (this.dateType == Date.class) {
            return deserializeToDate;
        }
        if (this.dateType == Timestamp.class) {
            return new Timestamp(deserializeToDate.getTime());
        }
        if (this.dateType != java.sql.Date.class) {
            throw new AssertionError();
        }
        return new java.sql.Date(deserializeToDate.getTime());
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DefaultDateTypeAdapter");
        sb.append('(').append(this.localFormat.getClass().getSimpleName()).append(')');
        return sb.toString();
    }
    
    @Override
    public void write(final JsonWriter jsonWriter, final Date date) throws IOException {
        synchronized (this.localFormat) {
            jsonWriter.value(this.enUsFormat.format(date));
        }
    }
}
