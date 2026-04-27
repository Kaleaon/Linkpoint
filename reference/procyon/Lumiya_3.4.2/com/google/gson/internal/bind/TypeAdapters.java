// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal.bind;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import java.util.StringTokenizer;
import java.util.GregorianCalendar;
import java.util.Date;
import java.sql.Timestamp;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import java.net.URISyntaxException;
import com.google.gson.JsonIOException;
import com.google.gson.internal.LazilyParsedNumber;
import java.util.ArrayList;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import com.google.gson.stream.JsonReader;
import java.util.UUID;
import java.net.URL;
import java.net.URI;
import java.util.Locale;
import com.google.gson.JsonElement;
import java.net.InetAddress;
import java.util.Currency;
import java.util.Calendar;
import java.util.BitSet;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.gson.TypeAdapterFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import com.google.gson.TypeAdapter;

public final class TypeAdapters
{
    public static final TypeAdapter<AtomicBoolean> ATOMIC_BOOLEAN;
    public static final TypeAdapterFactory ATOMIC_BOOLEAN_FACTORY;
    public static final TypeAdapter<AtomicInteger> ATOMIC_INTEGER;
    public static final TypeAdapter<AtomicIntegerArray> ATOMIC_INTEGER_ARRAY;
    public static final TypeAdapterFactory ATOMIC_INTEGER_ARRAY_FACTORY;
    public static final TypeAdapterFactory ATOMIC_INTEGER_FACTORY;
    public static final TypeAdapter<BigDecimal> BIG_DECIMAL;
    public static final TypeAdapter<BigInteger> BIG_INTEGER;
    public static final TypeAdapter<BitSet> BIT_SET;
    public static final TypeAdapterFactory BIT_SET_FACTORY;
    public static final TypeAdapter<Boolean> BOOLEAN;
    public static final TypeAdapter<Boolean> BOOLEAN_AS_STRING;
    public static final TypeAdapterFactory BOOLEAN_FACTORY;
    public static final TypeAdapter<Number> BYTE;
    public static final TypeAdapterFactory BYTE_FACTORY;
    public static final TypeAdapter<Calendar> CALENDAR;
    public static final TypeAdapterFactory CALENDAR_FACTORY;
    public static final TypeAdapter<Character> CHARACTER;
    public static final TypeAdapterFactory CHARACTER_FACTORY;
    public static final TypeAdapter<Class> CLASS;
    public static final TypeAdapterFactory CLASS_FACTORY;
    public static final TypeAdapter<Currency> CURRENCY;
    public static final TypeAdapterFactory CURRENCY_FACTORY;
    public static final TypeAdapter<Number> DOUBLE;
    public static final TypeAdapterFactory ENUM_FACTORY;
    public static final TypeAdapter<Number> FLOAT;
    public static final TypeAdapter<InetAddress> INET_ADDRESS;
    public static final TypeAdapterFactory INET_ADDRESS_FACTORY;
    public static final TypeAdapter<Number> INTEGER;
    public static final TypeAdapterFactory INTEGER_FACTORY;
    public static final TypeAdapter<JsonElement> JSON_ELEMENT;
    public static final TypeAdapterFactory JSON_ELEMENT_FACTORY;
    public static final TypeAdapter<Locale> LOCALE;
    public static final TypeAdapterFactory LOCALE_FACTORY;
    public static final TypeAdapter<Number> LONG;
    public static final TypeAdapter<Number> NUMBER;
    public static final TypeAdapterFactory NUMBER_FACTORY;
    public static final TypeAdapter<Number> SHORT;
    public static final TypeAdapterFactory SHORT_FACTORY;
    public static final TypeAdapter<String> STRING;
    public static final TypeAdapter<StringBuffer> STRING_BUFFER;
    public static final TypeAdapterFactory STRING_BUFFER_FACTORY;
    public static final TypeAdapter<StringBuilder> STRING_BUILDER;
    public static final TypeAdapterFactory STRING_BUILDER_FACTORY;
    public static final TypeAdapterFactory STRING_FACTORY;
    public static final TypeAdapterFactory TIMESTAMP_FACTORY;
    public static final TypeAdapter<URI> URI;
    public static final TypeAdapterFactory URI_FACTORY;
    public static final TypeAdapter<URL> URL;
    public static final TypeAdapterFactory URL_FACTORY;
    public static final TypeAdapter<UUID> UUID;
    public static final TypeAdapterFactory UUID_FACTORY;
    
    static {
        CLASS = new TypeAdapter<Class>() {
            @Override
            public Class read(final JsonReader jsonReader) throws IOException {
                throw new UnsupportedOperationException("Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final Class clazz) throws IOException {
                throw new UnsupportedOperationException("Attempted to serialize java.lang.Class: " + clazz.getName() + ". Forgot to register a type adapter?");
            }
        }.nullSafe();
        CLASS_FACTORY = newFactory(Class.class, TypeAdapters.CLASS);
        BIT_SET = new TypeAdapter<BitSet>() {
            @Override
            public BitSet read(final JsonReader jsonReader) throws IOException {
                final BitSet set = new BitSet();
                jsonReader.beginArray();
                JsonToken obj = jsonReader.peek();
                int bitIndex = 0;
            Label_0110_Outer:
                while (obj != JsonToken.END_ARRAY) {
                    while (true) {
                        Label_0192: {
                            int nextBoolean = 0;
                            switch (obj) {
                                default: {
                                    throw new JsonSyntaxException("Invalid bitset value type: " + obj);
                                }
                                case NUMBER: {
                                    if (jsonReader.nextInt() == 0) {
                                        nextBoolean = 0;
                                        break;
                                    }
                                    nextBoolean = 1;
                                    break;
                                }
                                case BOOLEAN: {
                                    nextBoolean = (jsonReader.nextBoolean() ? 1 : 0);
                                    break;
                                }
                                case STRING: {
                                    final String nextString = jsonReader.nextString();
                                    try {
                                        if (Integer.parseInt(nextString) == 0) {
                                            nextBoolean = 0;
                                            break;
                                        }
                                        nextBoolean = 1;
                                        break;
                                    }
                                    catch (final NumberFormatException ex) {
                                        throw new JsonSyntaxException("Error: Expecting: bitset number value (1, 0), Found: " + nextString);
                                    }
                                    break Label_0192;
                                }
                            }
                            if (nextBoolean != 0) {
                                break Label_0192;
                            }
                            ++bitIndex;
                            obj = jsonReader.peek();
                            continue Label_0110_Outer;
                        }
                        set.set(bitIndex);
                        continue;
                    }
                }
                jsonReader.endArray();
                return set;
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final BitSet set) throws IOException {
                jsonWriter.beginArray();
                for (int length = set.length(), i = 0; i < length; ++i) {
                    int n;
                    if (!set.get(i)) {
                        n = 0;
                    }
                    else {
                        n = 1;
                    }
                    jsonWriter.value(n);
                }
                jsonWriter.endArray();
            }
        }.nullSafe();
        BIT_SET_FACTORY = newFactory(BitSet.class, TypeAdapters.BIT_SET);
        BOOLEAN = new TypeAdapter<Boolean>() {
            @Override
            public Boolean read(final JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull();
                    return null;
                }
                if (jsonReader.peek() != JsonToken.STRING) {
                    return jsonReader.nextBoolean();
                }
                return Boolean.parseBoolean(jsonReader.nextString());
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final Boolean b) throws IOException {
                jsonWriter.value(b);
            }
        };
        BOOLEAN_AS_STRING = new TypeAdapter<Boolean>() {
            @Override
            public Boolean read(final JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() != JsonToken.NULL) {
                    return Boolean.valueOf(jsonReader.nextString());
                }
                jsonReader.nextNull();
                return null;
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final Boolean b) throws IOException {
                String string;
                if (b != null) {
                    string = b.toString();
                }
                else {
                    string = "null";
                }
                jsonWriter.value(string);
            }
        };
        BOOLEAN_FACTORY = newFactory(Boolean.TYPE, Boolean.class, TypeAdapters.BOOLEAN);
        BYTE = new TypeAdapter<Number>() {
            @Override
            public Number read(final JsonReader jsonReader) throws IOException {
                Label_0021: {
                    if (jsonReader.peek() == JsonToken.NULL) {
                        break Label_0021;
                    }
                    try {
                        return (byte)jsonReader.nextInt();
                        jsonReader.nextNull();
                        return null;
                    }
                    catch (final NumberFormatException ex) {
                        throw new JsonSyntaxException(ex);
                    }
                }
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final Number n) throws IOException {
                jsonWriter.value(n);
            }
        };
        BYTE_FACTORY = newFactory(Byte.TYPE, Byte.class, TypeAdapters.BYTE);
        SHORT = new TypeAdapter<Number>() {
            @Override
            public Number read(final JsonReader jsonReader) throws IOException {
                Label_0021: {
                    if (jsonReader.peek() == JsonToken.NULL) {
                        break Label_0021;
                    }
                    try {
                        return (short)jsonReader.nextInt();
                        jsonReader.nextNull();
                        return null;
                    }
                    catch (final NumberFormatException ex) {
                        throw new JsonSyntaxException(ex);
                    }
                }
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final Number n) throws IOException {
                jsonWriter.value(n);
            }
        };
        SHORT_FACTORY = newFactory(Short.TYPE, Short.class, TypeAdapters.SHORT);
        INTEGER = new TypeAdapter<Number>() {
            @Override
            public Number read(final JsonReader jsonReader) throws IOException {
                Label_0020: {
                    if (jsonReader.peek() == JsonToken.NULL) {
                        break Label_0020;
                    }
                    try {
                        return jsonReader.nextInt();
                        jsonReader.nextNull();
                        return null;
                    }
                    catch (final NumberFormatException ex) {
                        throw new JsonSyntaxException(ex);
                    }
                }
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final Number n) throws IOException {
                jsonWriter.value(n);
            }
        };
        INTEGER_FACTORY = newFactory(Integer.TYPE, Integer.class, TypeAdapters.INTEGER);
        ATOMIC_INTEGER = new TypeAdapter<AtomicInteger>() {
            @Override
            public AtomicInteger read(final JsonReader jsonReader) throws IOException {
                try {
                    return new AtomicInteger(jsonReader.nextInt());
                }
                catch (final NumberFormatException ex) {
                    throw new JsonSyntaxException(ex);
                }
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final AtomicInteger atomicInteger) throws IOException {
                jsonWriter.value(atomicInteger.get());
            }
        }.nullSafe();
        ATOMIC_INTEGER_FACTORY = newFactory(AtomicInteger.class, TypeAdapters.ATOMIC_INTEGER);
        ATOMIC_BOOLEAN = new TypeAdapter<AtomicBoolean>() {
            @Override
            public AtomicBoolean read(final JsonReader jsonReader) throws IOException {
                return new AtomicBoolean(jsonReader.nextBoolean());
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final AtomicBoolean atomicBoolean) throws IOException {
                jsonWriter.value(atomicBoolean.get());
            }
        }.nullSafe();
        ATOMIC_BOOLEAN_FACTORY = newFactory(AtomicBoolean.class, TypeAdapters.ATOMIC_BOOLEAN);
        ATOMIC_INTEGER_ARRAY = new TypeAdapter<AtomicIntegerArray>() {
            @Override
            public AtomicIntegerArray read(final JsonReader jsonReader) throws IOException {
                final ArrayList list = new ArrayList();
                jsonReader.beginArray();
                while (true) {
                Label_0077_Outer:
                    while (jsonReader.hasNext()) {
                        try {
                            list.add(jsonReader.nextInt());
                            continue Label_0077_Outer;
                        }
                        catch (final NumberFormatException ex) {
                            throw new JsonSyntaxException(ex);
                        }
                        while (true) {
                            final AtomicIntegerArray atomicIntegerArray;
                            int i = 0;
                            atomicIntegerArray.set(i, (int)list.get(i));
                            ++i;
                            final int size;
                            if (i >= size) {
                                return atomicIntegerArray;
                            }
                            continue;
                        }
                    }
                    jsonReader.endArray();
                    final int size = list.size();
                    final AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(size);
                    int i = 0;
                    continue;
                }
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final AtomicIntegerArray atomicIntegerArray) throws IOException {
                jsonWriter.beginArray();
                for (int i = 0; i < atomicIntegerArray.length(); ++i) {
                    jsonWriter.value(atomicIntegerArray.get(i));
                }
                jsonWriter.endArray();
            }
        }.nullSafe();
        ATOMIC_INTEGER_ARRAY_FACTORY = newFactory(AtomicIntegerArray.class, TypeAdapters.ATOMIC_INTEGER_ARRAY);
        LONG = new TypeAdapter<Number>() {
            @Override
            public Number read(final JsonReader jsonReader) throws IOException {
                Label_0020: {
                    if (jsonReader.peek() == JsonToken.NULL) {
                        break Label_0020;
                    }
                    try {
                        return jsonReader.nextLong();
                        jsonReader.nextNull();
                        return null;
                    }
                    catch (final NumberFormatException ex) {
                        throw new JsonSyntaxException(ex);
                    }
                }
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final Number n) throws IOException {
                jsonWriter.value(n);
            }
        };
        FLOAT = new TypeAdapter<Number>() {
            @Override
            public Number read(final JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() != JsonToken.NULL) {
                    return (float)jsonReader.nextDouble();
                }
                jsonReader.nextNull();
                return null;
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final Number n) throws IOException {
                jsonWriter.value(n);
            }
        };
        DOUBLE = new TypeAdapter<Number>() {
            @Override
            public Number read(final JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() != JsonToken.NULL) {
                    return jsonReader.nextDouble();
                }
                jsonReader.nextNull();
                return null;
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final Number n) throws IOException {
                jsonWriter.value(n);
            }
        };
        NUMBER = new TypeAdapter<Number>() {
            @Override
            public Number read(final JsonReader jsonReader) throws IOException {
                final JsonToken peek = jsonReader.peek();
                switch (peek) {
                    default: {
                        throw new JsonSyntaxException("Expecting number, got: " + peek);
                    }
                    case NULL: {
                        jsonReader.nextNull();
                        return null;
                    }
                    case NUMBER:
                    case STRING: {
                        return new LazilyParsedNumber(jsonReader.nextString());
                    }
                }
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final Number n) throws IOException {
                jsonWriter.value(n);
            }
        };
        NUMBER_FACTORY = newFactory(Number.class, TypeAdapters.NUMBER);
        CHARACTER = new TypeAdapter<Character>() {
            @Override
            public Character read(final JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull();
                    return null;
                }
                final String nextString = jsonReader.nextString();
                if (nextString.length() == 1) {
                    return nextString.charAt(0);
                }
                throw new JsonSyntaxException("Expecting character, got: " + nextString);
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final Character obj) throws IOException {
                String value = null;
                if (obj != null) {
                    value = String.valueOf(obj);
                }
                jsonWriter.value(value);
            }
        };
        CHARACTER_FACTORY = newFactory(Character.TYPE, Character.class, TypeAdapters.CHARACTER);
        STRING = new TypeAdapter<String>() {
            @Override
            public String read(final JsonReader jsonReader) throws IOException {
                final JsonToken peek = jsonReader.peek();
                if (peek == JsonToken.NULL) {
                    jsonReader.nextNull();
                    return null;
                }
                if (peek != JsonToken.BOOLEAN) {
                    return jsonReader.nextString();
                }
                return Boolean.toString(jsonReader.nextBoolean());
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final String s) throws IOException {
                jsonWriter.value(s);
            }
        };
        BIG_DECIMAL = new TypeAdapter<BigDecimal>() {
            @Override
            public BigDecimal read(final JsonReader jsonReader) throws IOException {
                Label_0024: {
                    if (jsonReader.peek() == JsonToken.NULL) {
                        break Label_0024;
                    }
                    try {
                        return new BigDecimal(jsonReader.nextString());
                        jsonReader.nextNull();
                        return null;
                    }
                    catch (final NumberFormatException ex) {
                        throw new JsonSyntaxException(ex);
                    }
                }
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final BigDecimal bigDecimal) throws IOException {
                jsonWriter.value(bigDecimal);
            }
        };
        BIG_INTEGER = new TypeAdapter<BigInteger>() {
            @Override
            public BigInteger read(final JsonReader jsonReader) throws IOException {
                Label_0024: {
                    if (jsonReader.peek() == JsonToken.NULL) {
                        break Label_0024;
                    }
                    try {
                        return new BigInteger(jsonReader.nextString());
                        jsonReader.nextNull();
                        return null;
                    }
                    catch (final NumberFormatException ex) {
                        throw new JsonSyntaxException(ex);
                    }
                }
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final BigInteger bigInteger) throws IOException {
                jsonWriter.value(bigInteger);
            }
        };
        STRING_FACTORY = newFactory(String.class, TypeAdapters.STRING);
        STRING_BUILDER = new TypeAdapter<StringBuilder>() {
            @Override
            public StringBuilder read(final JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() != JsonToken.NULL) {
                    return new StringBuilder(jsonReader.nextString());
                }
                jsonReader.nextNull();
                return null;
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final StringBuilder sb) throws IOException {
                String string = null;
                if (sb != null) {
                    string = sb.toString();
                }
                jsonWriter.value(string);
            }
        };
        STRING_BUILDER_FACTORY = newFactory(StringBuilder.class, TypeAdapters.STRING_BUILDER);
        STRING_BUFFER = new TypeAdapter<StringBuffer>() {
            @Override
            public StringBuffer read(final JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() != JsonToken.NULL) {
                    return new StringBuffer(jsonReader.nextString());
                }
                jsonReader.nextNull();
                return null;
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final StringBuffer sb) throws IOException {
                String string = null;
                if (sb != null) {
                    string = sb.toString();
                }
                jsonWriter.value(string);
            }
        };
        STRING_BUFFER_FACTORY = newFactory(StringBuffer.class, TypeAdapters.STRING_BUFFER);
        URL = new TypeAdapter<URL>() {
            @Override
            public URL read(final JsonReader jsonReader) throws IOException {
                final URL url = null;
                if (jsonReader.peek() != JsonToken.NULL) {
                    final String nextString = jsonReader.nextString();
                    URL url2 = url;
                    if (!"null".equals(nextString)) {
                        url2 = new URL(nextString);
                    }
                    return url2;
                }
                jsonReader.nextNull();
                return null;
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final URL url) throws IOException {
                String externalForm = null;
                if (url != null) {
                    externalForm = url.toExternalForm();
                }
                jsonWriter.value(externalForm);
            }
        };
        URL_FACTORY = newFactory(URL.class, TypeAdapters.URL);
        URI = new TypeAdapter<URI>() {
            @Override
            public URI read(final JsonReader jsonReader) throws IOException {
                final URI uri = null;
                Label_0039: {
                    if (jsonReader.peek() == JsonToken.NULL) {
                        break Label_0039;
                    }
                    try {
                        final String nextString = jsonReader.nextString();
                        URI uri2 = uri;
                        if (!"null".equals(nextString)) {
                            uri2 = new URI(nextString);
                        }
                        return uri2;
                        jsonReader.nextNull();
                        return null;
                    }
                    catch (final URISyntaxException ex) {
                        throw new JsonIOException(ex);
                    }
                }
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final URI uri) throws IOException {
                String asciiString = null;
                if (uri != null) {
                    asciiString = uri.toASCIIString();
                }
                jsonWriter.value(asciiString);
            }
        };
        URI_FACTORY = newFactory(URI.class, TypeAdapters.URI);
        INET_ADDRESS = new TypeAdapter<InetAddress>() {
            @Override
            public InetAddress read(final JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() != JsonToken.NULL) {
                    return InetAddress.getByName(jsonReader.nextString());
                }
                jsonReader.nextNull();
                return null;
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final InetAddress inetAddress) throws IOException {
                String hostAddress = null;
                if (inetAddress != null) {
                    hostAddress = inetAddress.getHostAddress();
                }
                jsonWriter.value(hostAddress);
            }
        };
        INET_ADDRESS_FACTORY = newTypeHierarchyFactory(InetAddress.class, TypeAdapters.INET_ADDRESS);
        UUID = new TypeAdapter<UUID>() {
            @Override
            public UUID read(final JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() != JsonToken.NULL) {
                    return java.util.UUID.fromString(jsonReader.nextString());
                }
                jsonReader.nextNull();
                return null;
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final UUID uuid) throws IOException {
                String string = null;
                if (uuid != null) {
                    string = uuid.toString();
                }
                jsonWriter.value(string);
            }
        };
        UUID_FACTORY = newFactory(UUID.class, TypeAdapters.UUID);
        CURRENCY = new TypeAdapter<Currency>() {
            @Override
            public Currency read(final JsonReader jsonReader) throws IOException {
                return Currency.getInstance(jsonReader.nextString());
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final Currency currency) throws IOException {
                jsonWriter.value(currency.getCurrencyCode());
            }
        }.nullSafe();
        CURRENCY_FACTORY = newFactory(Currency.class, TypeAdapters.CURRENCY);
        TIMESTAMP_FACTORY = new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
                if (typeToken.getRawType() == Timestamp.class) {
                    return (TypeAdapter<T>)new TypeAdapter<Timestamp>() {
                        final /* synthetic */ TypeAdapter val$dateTypeAdapter = gson.getAdapter(Date.class);
                        
                        @Override
                        public Timestamp read(final JsonReader jsonReader) throws IOException {
                            final Date date = this.val$dateTypeAdapter.read(jsonReader);
                            Timestamp timestamp;
                            if (date == null) {
                                timestamp = null;
                            }
                            else {
                                timestamp = new Timestamp(date.getTime());
                            }
                            return timestamp;
                        }
                        
                        @Override
                        public void write(final JsonWriter jsonWriter, final Timestamp timestamp) throws IOException {
                            this.val$dateTypeAdapter.write(jsonWriter, timestamp);
                        }
                    };
                }
                return null;
            }
        };
        CALENDAR = new TypeAdapter<Calendar>() {
            private static final String DAY_OF_MONTH = "dayOfMonth";
            private static final String HOUR_OF_DAY = "hourOfDay";
            private static final String MINUTE = "minute";
            private static final String MONTH = "month";
            private static final String SECOND = "second";
            private static final String YEAR = "year";
            
            @Override
            public Calendar read(final JsonReader jsonReader) throws IOException {
                int second = 0;
                if (jsonReader.peek() != JsonToken.NULL) {
                    jsonReader.beginObject();
                    int minute = 0;
                    int hourOfDay = 0;
                    int dayOfMonth = 0;
                    int month = 0;
                    int year = 0;
                    while (jsonReader.peek() != JsonToken.END_OBJECT) {
                        final String nextName = jsonReader.nextName();
                        final int nextInt = jsonReader.nextInt();
                        if (!"year".equals(nextName)) {
                            if (!"month".equals(nextName)) {
                                if (!"dayOfMonth".equals(nextName)) {
                                    if (!"hourOfDay".equals(nextName)) {
                                        if (!"minute".equals(nextName)) {
                                            if (!"second".equals(nextName)) {
                                                continue;
                                            }
                                            second = nextInt;
                                        }
                                        else {
                                            minute = nextInt;
                                        }
                                    }
                                    else {
                                        hourOfDay = nextInt;
                                    }
                                }
                                else {
                                    dayOfMonth = nextInt;
                                }
                            }
                            else {
                                month = nextInt;
                            }
                        }
                        else {
                            year = nextInt;
                        }
                    }
                    jsonReader.endObject();
                    return new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
                }
                jsonReader.nextNull();
                return null;
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final Calendar calendar) throws IOException {
                if (calendar != null) {
                    jsonWriter.beginObject();
                    jsonWriter.name("year");
                    jsonWriter.value(calendar.get(1));
                    jsonWriter.name("month");
                    jsonWriter.value(calendar.get(2));
                    jsonWriter.name("dayOfMonth");
                    jsonWriter.value(calendar.get(5));
                    jsonWriter.name("hourOfDay");
                    jsonWriter.value(calendar.get(11));
                    jsonWriter.name("minute");
                    jsonWriter.value(calendar.get(12));
                    jsonWriter.name("second");
                    jsonWriter.value(calendar.get(13));
                    jsonWriter.endObject();
                    return;
                }
                jsonWriter.nullValue();
            }
        };
        CALENDAR_FACTORY = newFactoryForMultipleTypes(Calendar.class, GregorianCalendar.class, TypeAdapters.CALENDAR);
        LOCALE = new TypeAdapter<Locale>() {
            @Override
            public Locale read(final JsonReader jsonReader) throws IOException {
                String nextToken = null;
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull();
                    return null;
                }
                final StringTokenizer stringTokenizer = new StringTokenizer(jsonReader.nextString(), "_");
                String nextToken2;
                if (!stringTokenizer.hasMoreElements()) {
                    nextToken2 = null;
                }
                else {
                    nextToken2 = stringTokenizer.nextToken();
                }
                String nextToken3;
                if (!stringTokenizer.hasMoreElements()) {
                    nextToken3 = null;
                }
                else {
                    nextToken3 = stringTokenizer.nextToken();
                }
                if (stringTokenizer.hasMoreElements()) {
                    nextToken = stringTokenizer.nextToken();
                }
                if (nextToken3 == null && nextToken == null) {
                    return new Locale(nextToken2);
                }
                if (nextToken != null) {
                    return new Locale(nextToken2, nextToken3, nextToken);
                }
                return new Locale(nextToken2, nextToken3);
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final Locale locale) throws IOException {
                String string = null;
                if (locale != null) {
                    string = locale.toString();
                }
                jsonWriter.value(string);
            }
        };
        LOCALE_FACTORY = newFactory(Locale.class, TypeAdapters.LOCALE);
        JSON_ELEMENT = new TypeAdapter<JsonElement>() {
            @Override
            public JsonElement read(final JsonReader jsonReader) throws IOException {
                switch (jsonReader.peek()) {
                    default: {
                        throw new IllegalArgumentException();
                    }
                    case STRING: {
                        return new JsonPrimitive(jsonReader.nextString());
                    }
                    case NUMBER: {
                        return new JsonPrimitive(new LazilyParsedNumber(jsonReader.nextString()));
                    }
                    case BOOLEAN: {
                        return new JsonPrimitive(jsonReader.nextBoolean());
                    }
                    case NULL: {
                        jsonReader.nextNull();
                        return JsonNull.INSTANCE;
                    }
                    case BEGIN_ARRAY: {
                        final JsonArray jsonArray = new JsonArray();
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            jsonArray.add(this.read(jsonReader));
                        }
                        jsonReader.endArray();
                        return jsonArray;
                    }
                    case BEGIN_OBJECT: {
                        final JsonObject jsonObject = new JsonObject();
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            jsonObject.add(jsonReader.nextName(), this.read(jsonReader));
                        }
                        jsonReader.endObject();
                        return jsonObject;
                    }
                }
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final JsonElement jsonElement) throws IOException {
                if (jsonElement != null && !jsonElement.isJsonNull()) {
                    if (!jsonElement.isJsonPrimitive()) {
                        if (!jsonElement.isJsonArray()) {
                            if (!jsonElement.isJsonObject()) {
                                throw new IllegalArgumentException("Couldn't write " + jsonElement.getClass());
                            }
                            jsonWriter.beginObject();
                            for (final Map.Entry<String, V> entry : jsonElement.getAsJsonObject().entrySet()) {
                                jsonWriter.name(entry.getKey());
                                this.write(jsonWriter, (JsonElement)entry.getValue());
                            }
                            jsonWriter.endObject();
                        }
                        else {
                            jsonWriter.beginArray();
                            final Iterator<JsonElement> iterator2 = jsonElement.getAsJsonArray().iterator();
                            while (iterator2.hasNext()) {
                                this.write(jsonWriter, iterator2.next());
                            }
                            jsonWriter.endArray();
                        }
                    }
                    else {
                        final JsonPrimitive asJsonPrimitive = jsonElement.getAsJsonPrimitive();
                        if (!asJsonPrimitive.isNumber()) {
                            if (!asJsonPrimitive.isBoolean()) {
                                jsonWriter.value(asJsonPrimitive.getAsString());
                            }
                            else {
                                jsonWriter.value(asJsonPrimitive.getAsBoolean());
                            }
                        }
                        else {
                            jsonWriter.value(asJsonPrimitive.getAsNumber());
                        }
                    }
                }
                else {
                    jsonWriter.nullValue();
                }
            }
        };
        JSON_ELEMENT_FACTORY = newTypeHierarchyFactory(JsonElement.class, TypeAdapters.JSON_ELEMENT);
        ENUM_FACTORY = new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
                Class<? super T> clazz = typeToken.getRawType();
                if (Enum.class.isAssignableFrom(clazz) && clazz != Enum.class) {
                    if (!clazz.isEnum()) {
                        clazz = clazz.getSuperclass();
                    }
                    return new EnumTypeAdapter<T>((Class<T>)clazz);
                }
                return null;
            }
        };
    }
    
    private TypeAdapters() {
        throw new UnsupportedOperationException();
    }
    
    public static <TT> TypeAdapterFactory newFactory(final TypeToken<TT> typeToken, final TypeAdapter<TT> typeAdapter) {
        return new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
                TypeAdapter<T> val$typeAdapter;
                if (!typeToken.equals(typeToken)) {
                    val$typeAdapter = null;
                }
                else {
                    val$typeAdapter = (TypeAdapter<T>)typeAdapter;
                }
                return val$typeAdapter;
            }
        };
    }
    
    public static <TT> TypeAdapterFactory newFactory(final Class<TT> clazz, final TypeAdapter<TT> typeAdapter) {
        return new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
                TypeAdapter<T> val$typeAdapter;
                if (typeToken.getRawType() != clazz) {
                    val$typeAdapter = null;
                }
                else {
                    val$typeAdapter = (TypeAdapter<T>)typeAdapter;
                }
                return val$typeAdapter;
            }
            
            @Override
            public String toString() {
                return "Factory[type=" + clazz.getName() + ",adapter=" + typeAdapter + "]";
            }
        };
    }
    
    public static <TT> TypeAdapterFactory newFactory(final Class<TT> clazz, final Class<TT> clazz2, final TypeAdapter<? super TT> typeAdapter) {
        return new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
                final Class<? super T> rawType = typeToken.getRawType();
                TypeAdapter val$typeAdapter;
                if (rawType != clazz && rawType != clazz2) {
                    val$typeAdapter = null;
                }
                else {
                    val$typeAdapter = typeAdapter;
                }
                return val$typeAdapter;
            }
            
            @Override
            public String toString() {
                return "Factory[type=" + clazz2.getName() + "+" + clazz.getName() + ",adapter=" + typeAdapter + "]";
            }
        };
    }
    
    public static <TT> TypeAdapterFactory newFactoryForMultipleTypes(final Class<TT> clazz, final Class<? extends TT> clazz2, final TypeAdapter<? super TT> typeAdapter) {
        return new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
                final Class<? super T> rawType = typeToken.getRawType();
                TypeAdapter val$typeAdapter;
                if (rawType != clazz && rawType != clazz2) {
                    val$typeAdapter = null;
                }
                else {
                    val$typeAdapter = typeAdapter;
                }
                return val$typeAdapter;
            }
            
            @Override
            public String toString() {
                return "Factory[type=" + clazz.getName() + "+" + clazz2.getName() + ",adapter=" + typeAdapter + "]";
            }
        };
    }
    
    public static <T1> TypeAdapterFactory newTypeHierarchyFactory(final Class<T1> clazz, final TypeAdapter<T1> typeAdapter) {
        return new TypeAdapterFactory() {
            @Override
            public <T2> TypeAdapter<T2> create(final Gson gson, final TypeToken<T2> typeToken) {
                final Class<? super T2> rawType = typeToken.getRawType();
                if (clazz.isAssignableFrom(rawType)) {
                    return (TypeAdapter<T2>)new TypeAdapter<T1>() {
                        @Override
                        public T1 read(final JsonReader jsonReader) throws IOException {
                            final T1 read = typeAdapter.read(jsonReader);
                            if (read != null && !rawType.isInstance(read)) {
                                throw new JsonSyntaxException("Expected a " + rawType.getName() + " but was " + read.getClass().getName());
                            }
                            return read;
                        }
                        
                        @Override
                        public void write(final JsonWriter jsonWriter, final T1 t1) throws IOException {
                            typeAdapter.write(jsonWriter, t1);
                        }
                    };
                }
                return null;
            }
            
            @Override
            public String toString() {
                return "Factory[typeHierarchy=" + clazz.getName() + ",adapter=" + typeAdapter + "]";
            }
        };
    }
    
    private static final class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T>
    {
        private final Map<T, String> constantToName;
        private final Map<String, T> nameToConstant;
        
        public EnumTypeAdapter(final Class<T> clazz) {
            this.nameToConstant = new HashMap<String, T>();
            this.constantToName = new HashMap<T, String>();
            try {
                for (final Enum<T> enum1 : clazz.getEnumConstants()) {
                    String name = enum1.name();
                    final SerializedName serializedName = clazz.getField(name).getAnnotation(SerializedName.class);
                    if (serializedName != null) {
                        name = serializedName.value();
                        final String[] alternate = serializedName.alternate();
                        for (int length2 = alternate.length, j = 0; j < length2; ++j) {
                            this.nameToConstant.put(alternate[j], (T)enum1);
                        }
                    }
                    this.nameToConstant.put(name, (T)enum1);
                    this.constantToName.put((T)enum1, name);
                }
            }
            catch (final NoSuchFieldException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        @Override
        public T read(final JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() != JsonToken.NULL) {
                return this.nameToConstant.get(jsonReader.nextString());
            }
            jsonReader.nextNull();
            return null;
        }
        
        @Override
        public void write(final JsonWriter jsonWriter, final T t) throws IOException {
            String s = null;
            if (t != null) {
                s = this.constantToName.get(t);
            }
            jsonWriter.value(s);
        }
    }
}
