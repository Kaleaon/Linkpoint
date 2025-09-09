package com.google.gson;

import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class DefaultDateTypeAdapter extends TypeAdapter<Date> {
    private static final String SIMPLE_NAME = "DefaultDateTypeAdapter";
    private final Class<? extends Date> dateType;
    private final DateFormat enUsFormat;
    private final DateFormat localFormat;

    public DefaultDateTypeAdapter(int i, int i2) {
        this((Class<? extends Date>) Date.class, DateFormat.getDateTimeInstance(i, i2, Locale.US), DateFormat.getDateTimeInstance(i, i2));
    }

    DefaultDateTypeAdapter(Class<? extends Date> cls) {
        this(cls, DateFormat.getDateTimeInstance(2, 2, Locale.US), DateFormat.getDateTimeInstance(2, 2));
    }

    DefaultDateTypeAdapter(Class<? extends Date> cls, int i) {
        this(cls, DateFormat.getDateInstance(i, Locale.US), DateFormat.getDateInstance(i));
    }

    public DefaultDateTypeAdapter(Class<? extends Date> cls, int i, int i2) {
        this(cls, DateFormat.getDateTimeInstance(i, i2, Locale.US), DateFormat.getDateTimeInstance(i, i2));
    }

    DefaultDateTypeAdapter(Class<? extends Date> cls, String str) {
        this(cls, (DateFormat) new SimpleDateFormat(str, Locale.US), (DateFormat) new SimpleDateFormat(str));
    }

    DefaultDateTypeAdapter(Class<? extends Date> cls, DateFormat dateFormat, DateFormat dateFormat2) {
        if (cls == Date.class || cls == java.sql.Date.class || cls == Timestamp.class) {
            this.dateType = cls;
            this.enUsFormat = dateFormat;
            this.localFormat = dateFormat2;
            return;
        }
        throw new IllegalArgumentException("Date type must be one of " + Date.class + ", " + Timestamp.class + ", or " + java.sql.Date.class + " but was " + cls);
    }

    private Date deserializeToDate(String str) {
        Date parse;
        synchronized (this.localFormat) {
            try {
                parse = this.localFormat.parse(str);
            } catch (ParseException e) {
                try {
                    return this.enUsFormat.parse(str);
                } catch (ParseException e2) {
                    throw new JsonSyntaxException(str, e2);
                } catch (ParseException e3) {
                    return ISO8601Utils.parse(str, new ParsePosition(0));
                }
            }
        }
        return parse;
    }

    public Date read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.STRING) {
            Date deserializeToDate = deserializeToDate(jsonReader.nextString());
            if (this.dateType == Date.class) {
                return deserializeToDate;
            }
            if (this.dateType == Timestamp.class) {
                return new Timestamp(deserializeToDate.getTime());
            }
            if (this.dateType == java.sql.Date.class) {
                return new java.sql.Date(deserializeToDate.getTime());
            }
            throw new AssertionError();
        }
        throw new JsonParseException("The date should be a string value");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SIMPLE_NAME);
        sb.append('(').append(this.localFormat.getClass().getSimpleName()).append(')');
        return sb.toString();
    }

    public void write(JsonWriter jsonWriter, Date date) throws IOException {
        synchronized (this.localFormat) {
            jsonWriter.value(this.enUsFormat.format(date));
        }
    }
}
