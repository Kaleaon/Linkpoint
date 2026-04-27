// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson;

import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.internal.Streams;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.HashMap;
import java.io.StringReader;
import java.io.Reader;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.Primitives;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import com.google.gson.stream.MalformedJsonException;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.internal.bind.MapTypeAdapterFactory;
import com.google.gson.internal.bind.CollectionTypeAdapterFactory;
import com.google.gson.internal.bind.ArrayTypeAdapter;
import com.google.gson.internal.bind.SqlDateTypeAdapter;
import com.google.gson.internal.bind.TimeTypeAdapter;
import com.google.gson.internal.bind.DateTypeAdapter;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Collection;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Type;
import java.util.Collections;
import com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory;
import java.util.List;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ConstructorConstructor;
import java.util.Map;
import com.google.gson.reflect.TypeToken;

public final class Gson
{
    static final boolean DEFAULT_COMPLEX_MAP_KEYS = false;
    static final boolean DEFAULT_ESCAPE_HTML = true;
    static final boolean DEFAULT_JSON_NON_EXECUTABLE = false;
    static final boolean DEFAULT_LENIENT = false;
    static final boolean DEFAULT_PRETTY_PRINT = false;
    static final boolean DEFAULT_SERIALIZE_NULLS = false;
    static final boolean DEFAULT_SPECIALIZE_FLOAT_VALUES = false;
    private static final String JSON_NON_EXECUTABLE_PREFIX = ")]}'\n";
    private static final TypeToken<?> NULL_KEY_SURROGATE;
    private final ThreadLocal<Map<TypeToken<?>, FutureTypeAdapter<?>>> calls;
    private final ConstructorConstructor constructorConstructor;
    private final Excluder excluder;
    private final List<TypeAdapterFactory> factories;
    private final FieldNamingStrategy fieldNamingStrategy;
    private final boolean generateNonExecutableJson;
    private final boolean htmlSafe;
    private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;
    private final boolean lenient;
    private final boolean prettyPrinting;
    private final boolean serializeNulls;
    private final Map<TypeToken<?>, TypeAdapter<?>> typeTokenCache;
    
    static {
        NULL_KEY_SURROGATE = TypeToken.get((Class<?>)Object.class);
    }
    
    public Gson() {
        this(Excluder.DEFAULT, FieldNamingPolicy.IDENTITY, Collections.emptyMap(), false, false, false, true, false, false, false, LongSerializationPolicy.DEFAULT, Collections.emptyList());
    }
    
    Gson(final Excluder excluder, final FieldNamingStrategy fieldNamingStrategy, final Map<Type, InstanceCreator<?>> map, final boolean serializeNulls, final boolean b, final boolean generateNonExecutableJson, final boolean htmlSafe, final boolean prettyPrinting, final boolean lenient, final boolean b2, final LongSerializationPolicy longSerializationPolicy, final List<TypeAdapterFactory> list) {
        this.calls = new ThreadLocal<Map<TypeToken<?>, FutureTypeAdapter<?>>>();
        this.typeTokenCache = new ConcurrentHashMap<TypeToken<?>, TypeAdapter<?>>();
        this.constructorConstructor = new ConstructorConstructor(map);
        this.excluder = excluder;
        this.fieldNamingStrategy = fieldNamingStrategy;
        this.serializeNulls = serializeNulls;
        this.generateNonExecutableJson = generateNonExecutableJson;
        this.htmlSafe = htmlSafe;
        this.prettyPrinting = prettyPrinting;
        this.lenient = lenient;
        final ArrayList list2 = new ArrayList();
        list2.add(TypeAdapters.JSON_ELEMENT_FACTORY);
        list2.add(ObjectTypeAdapter.FACTORY);
        list2.add(excluder);
        list2.addAll(list);
        list2.add(TypeAdapters.STRING_FACTORY);
        list2.add(TypeAdapters.INTEGER_FACTORY);
        list2.add(TypeAdapters.BOOLEAN_FACTORY);
        list2.add(TypeAdapters.BYTE_FACTORY);
        list2.add(TypeAdapters.SHORT_FACTORY);
        final TypeAdapter<Number> longAdapter = longAdapter(longSerializationPolicy);
        list2.add(TypeAdapters.newFactory(Long.TYPE, Long.class, longAdapter));
        list2.add(TypeAdapters.newFactory(Double.TYPE, Double.class, this.doubleAdapter(b2)));
        list2.add(TypeAdapters.newFactory(Float.TYPE, Float.class, this.floatAdapter(b2)));
        list2.add(TypeAdapters.NUMBER_FACTORY);
        list2.add(TypeAdapters.ATOMIC_INTEGER_FACTORY);
        list2.add(TypeAdapters.ATOMIC_BOOLEAN_FACTORY);
        list2.add(TypeAdapters.newFactory(AtomicLong.class, atomicLongAdapter(longAdapter)));
        list2.add(TypeAdapters.newFactory(AtomicLongArray.class, atomicLongArrayAdapter(longAdapter)));
        list2.add(TypeAdapters.ATOMIC_INTEGER_ARRAY_FACTORY);
        list2.add(TypeAdapters.CHARACTER_FACTORY);
        list2.add(TypeAdapters.STRING_BUILDER_FACTORY);
        list2.add(TypeAdapters.STRING_BUFFER_FACTORY);
        list2.add(TypeAdapters.newFactory(BigDecimal.class, TypeAdapters.BIG_DECIMAL));
        list2.add(TypeAdapters.newFactory(BigInteger.class, TypeAdapters.BIG_INTEGER));
        list2.add(TypeAdapters.URL_FACTORY);
        list2.add(TypeAdapters.URI_FACTORY);
        list2.add(TypeAdapters.UUID_FACTORY);
        list2.add(TypeAdapters.CURRENCY_FACTORY);
        list2.add(TypeAdapters.LOCALE_FACTORY);
        list2.add(TypeAdapters.INET_ADDRESS_FACTORY);
        list2.add(TypeAdapters.BIT_SET_FACTORY);
        list2.add(DateTypeAdapter.FACTORY);
        list2.add(TypeAdapters.CALENDAR_FACTORY);
        list2.add(TimeTypeAdapter.FACTORY);
        list2.add(SqlDateTypeAdapter.FACTORY);
        list2.add(TypeAdapters.TIMESTAMP_FACTORY);
        list2.add(ArrayTypeAdapter.FACTORY);
        list2.add(TypeAdapters.CLASS_FACTORY);
        list2.add(new CollectionTypeAdapterFactory(this.constructorConstructor));
        list2.add(new MapTypeAdapterFactory(this.constructorConstructor, b));
        list2.add(this.jsonAdapterFactory = new JsonAdapterAnnotationTypeAdapterFactory(this.constructorConstructor));
        list2.add(TypeAdapters.ENUM_FACTORY);
        list2.add(new ReflectiveTypeAdapterFactory(this.constructorConstructor, fieldNamingStrategy, excluder, this.jsonAdapterFactory));
        this.factories = (List<TypeAdapterFactory>)Collections.unmodifiableList((List<?>)list2);
    }
    
    private static void assertFullConsumption(Object o, final JsonReader jsonReader) {
        if (o != null) {
            try {
                if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                    o = new JsonIOException("JSON document was not fully consumed.");
                    throw o;
                }
            }
            catch (final MalformedJsonException ex) {
                throw new JsonSyntaxException(ex);
            }
            catch (final IOException ex2) {
                throw new JsonIOException(ex2);
            }
        }
    }
    
    private static TypeAdapter<AtomicLong> atomicLongAdapter(final TypeAdapter<Number> typeAdapter) {
        return new TypeAdapter<AtomicLong>() {
            @Override
            public AtomicLong read(final JsonReader jsonReader) throws IOException {
                return new AtomicLong(typeAdapter.read(jsonReader).longValue());
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final AtomicLong atomicLong) throws IOException {
                typeAdapter.write(jsonWriter, atomicLong.get());
            }
        }.nullSafe();
    }
    
    private static TypeAdapter<AtomicLongArray> atomicLongArrayAdapter(final TypeAdapter<Number> typeAdapter) {
        return new TypeAdapter<AtomicLongArray>() {
            @Override
            public AtomicLongArray read(final JsonReader jsonReader) throws IOException {
                int i = 0;
                final ArrayList list = new ArrayList();
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    list.add(typeAdapter.read(jsonReader).longValue());
                }
                jsonReader.endArray();
                final int size = list.size();
                final AtomicLongArray atomicLongArray = new AtomicLongArray(size);
                while (i < size) {
                    atomicLongArray.set(i, (long)list.get(i));
                    ++i;
                }
                return atomicLongArray;
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final AtomicLongArray atomicLongArray) throws IOException {
                jsonWriter.beginArray();
                for (int i = 0; i < atomicLongArray.length(); ++i) {
                    typeAdapter.write(jsonWriter, atomicLongArray.get(i));
                }
                jsonWriter.endArray();
            }
        }.nullSafe();
    }
    
    static void checkValidFloatingPoint(final double d) {
        if (!Double.isNaN(d) && !Double.isInfinite(d)) {
            return;
        }
        throw new IllegalArgumentException(d + " is not a valid double value as per JSON specification. To override this" + " behavior, use GsonBuilder.serializeSpecialFloatingPointValues() method.");
    }
    
    private TypeAdapter<Number> doubleAdapter(final boolean b) {
        if (!b) {
            return new TypeAdapter<Number>() {
                @Override
                public Double read(final JsonReader jsonReader) throws IOException {
                    if (jsonReader.peek() != JsonToken.NULL) {
                        return jsonReader.nextDouble();
                    }
                    jsonReader.nextNull();
                    return null;
                }
                
                @Override
                public void write(final JsonWriter jsonWriter, final Number n) throws IOException {
                    if (n != null) {
                        Gson.checkValidFloatingPoint(n.doubleValue());
                        jsonWriter.value(n);
                        return;
                    }
                    jsonWriter.nullValue();
                }
            };
        }
        return TypeAdapters.DOUBLE;
    }
    
    private TypeAdapter<Number> floatAdapter(final boolean b) {
        if (!b) {
            return new TypeAdapter<Number>() {
                @Override
                public Float read(final JsonReader jsonReader) throws IOException {
                    if (jsonReader.peek() != JsonToken.NULL) {
                        return (float)jsonReader.nextDouble();
                    }
                    jsonReader.nextNull();
                    return null;
                }
                
                @Override
                public void write(final JsonWriter jsonWriter, final Number n) throws IOException {
                    if (n != null) {
                        Gson.checkValidFloatingPoint(n.floatValue());
                        jsonWriter.value(n);
                        return;
                    }
                    jsonWriter.nullValue();
                }
            };
        }
        return TypeAdapters.FLOAT;
    }
    
    private static TypeAdapter<Number> longAdapter(final LongSerializationPolicy longSerializationPolicy) {
        if (longSerializationPolicy != LongSerializationPolicy.DEFAULT) {
            return new TypeAdapter<Number>() {
                @Override
                public Number read(final JsonReader jsonReader) throws IOException {
                    if (jsonReader.peek() != JsonToken.NULL) {
                        return jsonReader.nextLong();
                    }
                    jsonReader.nextNull();
                    return null;
                }
                
                @Override
                public void write(final JsonWriter jsonWriter, final Number n) throws IOException {
                    if (n != null) {
                        jsonWriter.value(n.toString());
                        return;
                    }
                    jsonWriter.nullValue();
                }
            };
        }
        return TypeAdapters.LONG;
    }
    
    public Excluder excluder() {
        return this.excluder;
    }
    
    public FieldNamingStrategy fieldNamingStrategy() {
        return this.fieldNamingStrategy;
    }
    
    public <T> T fromJson(final JsonElement jsonElement, final Class<T> clazz) throws JsonSyntaxException {
        return Primitives.wrap(clazz).cast(this.fromJson(jsonElement, (Type)clazz));
    }
    
    public <T> T fromJson(final JsonElement jsonElement, final Type type) throws JsonSyntaxException {
        if (jsonElement != null) {
            return this.fromJson(new JsonTreeReader(jsonElement), type);
        }
        return null;
    }
    
    public <T> T fromJson(final JsonReader p0, final Type p1) throws JsonIOException, JsonSyntaxException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore_3       
        //     2: aload_1        
        //     3: invokevirtual   com/google/gson/stream/JsonReader.isLenient:()Z
        //     6: istore          4
        //     8: aload_1        
        //     9: iconst_1       
        //    10: invokevirtual   com/google/gson/stream/JsonReader.setLenient:(Z)V
        //    13: aload_1        
        //    14: invokevirtual   com/google/gson/stream/JsonReader.peek:()Lcom/google/gson/stream/JsonToken;
        //    17: pop            
        //    18: aload_0        
        //    19: aload_2        
        //    20: invokestatic    com/google/gson/reflect/TypeToken.get:(Ljava/lang/reflect/Type;)Lcom/google/gson/reflect/TypeToken;
        //    23: invokevirtual   com/google/gson/Gson.getAdapter:(Lcom/google/gson/reflect/TypeToken;)Lcom/google/gson/TypeAdapter;
        //    26: aload_1        
        //    27: invokevirtual   com/google/gson/TypeAdapter.read:(Lcom/google/gson/stream/JsonReader;)Ljava/lang/Object;
        //    30: astore_2       
        //    31: aload_1        
        //    32: iload           4
        //    34: invokevirtual   com/google/gson/stream/JsonReader.setLenient:(Z)V
        //    37: aload_2        
        //    38: areturn        
        //    39: astore_2       
        //    40: iconst_1       
        //    41: istore_3       
        //    42: iload_3        
        //    43: ifne            69
        //    46: new             Lcom/google/gson/JsonSyntaxException;
        //    49: astore          5
        //    51: aload           5
        //    53: aload_2        
        //    54: invokespecial   com/google/gson/JsonSyntaxException.<init>:(Ljava/lang/Throwable;)V
        //    57: aload           5
        //    59: athrow         
        //    60: astore_2       
        //    61: aload_1        
        //    62: iload           4
        //    64: invokevirtual   com/google/gson/stream/JsonReader.setLenient:(Z)V
        //    67: aload_2        
        //    68: athrow         
        //    69: aload_1        
        //    70: iload           4
        //    72: invokevirtual   com/google/gson/stream/JsonReader.setLenient:(Z)V
        //    75: aconst_null    
        //    76: areturn        
        //    77: astore_2       
        //    78: new             Lcom/google/gson/JsonSyntaxException;
        //    81: astore          5
        //    83: aload           5
        //    85: aload_2        
        //    86: invokespecial   com/google/gson/JsonSyntaxException.<init>:(Ljava/lang/Throwable;)V
        //    89: aload           5
        //    91: athrow         
        //    92: astore          5
        //    94: new             Lcom/google/gson/JsonSyntaxException;
        //    97: astore_2       
        //    98: aload_2        
        //    99: aload           5
        //   101: invokespecial   com/google/gson/JsonSyntaxException.<init>:(Ljava/lang/Throwable;)V
        //   104: aload_2        
        //   105: athrow         
        //   106: astore_2       
        //   107: goto            42
        //    Exceptions:
        //  throws com.google.gson.JsonIOException
        //  throws com.google.gson.JsonSyntaxException
        //    Signature:
        //  <T:Ljava/lang/Object;>(Lcom/google/gson/stream/JsonReader;Ljava/lang/reflect/Type;)TT;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                             
        //  -----  -----  -----  -----  ---------------------------------
        //  13     18     39     42     Ljava/io/EOFException;
        //  13     18     77     92     Ljava/lang/IllegalStateException;
        //  13     18     92     106    Ljava/io/IOException;
        //  13     18     60     69     Any
        //  18     31     106    110    Ljava/io/EOFException;
        //  18     31     77     92     Ljava/lang/IllegalStateException;
        //  18     31     92     106    Ljava/io/IOException;
        //  18     31     60     69     Any
        //  46     60     60     69     Any
        //  78     92     60     69     Any
        //  94     106    60     69     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0042:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public <T> T fromJson(final Reader reader, final Class<T> clazz) throws JsonSyntaxException, JsonIOException {
        final JsonReader jsonReader = this.newJsonReader(reader);
        final Object fromJson = this.fromJson(jsonReader, clazz);
        assertFullConsumption(fromJson, jsonReader);
        return Primitives.wrap(clazz).cast(fromJson);
    }
    
    public <T> T fromJson(final Reader reader, final Type type) throws JsonIOException, JsonSyntaxException {
        final JsonReader jsonReader = this.newJsonReader(reader);
        final Object fromJson = this.fromJson(jsonReader, type);
        assertFullConsumption(fromJson, jsonReader);
        return (T)fromJson;
    }
    
    public <T> T fromJson(final String s, final Class<T> clazz) throws JsonSyntaxException {
        return Primitives.wrap(clazz).cast(this.fromJson(s, (Type)clazz));
    }
    
    public <T> T fromJson(final String s, final Type type) throws JsonSyntaxException {
        if (s != null) {
            return this.fromJson(new StringReader(s), type);
        }
        return null;
    }
    
    public <T> TypeAdapter<T> getAdapter(final TypeToken<T> obj) {
        boolean b = false;
        final Map<TypeToken<?>, TypeAdapter<?>> typeTokenCache = this.typeTokenCache;
        while (true) {
            Label_0156: {
                if (obj == null) {
                    break Label_0156;
                }
                final TypeToken<?> null_KEY_SURROGATE = obj;
                final TypeAdapter typeAdapter = typeTokenCache.get(null_KEY_SURROGATE);
                if (typeAdapter != null) {
                    return typeAdapter;
                }
                Map value = this.calls.get();
                if (value == null) {
                    value = new HashMap();
                    this.calls.set(value);
                    b = true;
                }
                final FutureTypeAdapter futureTypeAdapter = (FutureTypeAdapter)value.get(obj);
                if (futureTypeAdapter == null) {
                    FutureTypeAdapter<T> futureTypeAdapter2 = null;
                    TypeAdapter<T> create = null;
                Label_0154_Outer:
                    while (true) {
                        while (true) {
                        Label_0261:
                            while (true) {
                                Iterator<TypeAdapterFactory> iterator = null;
                                Label_0192: {
                                    try {
                                        futureTypeAdapter2 = new FutureTypeAdapter<T>();
                                        value.put(obj, futureTypeAdapter2);
                                        iterator = this.factories.iterator();
                                        if (!iterator.hasNext()) {
                                            throw new IllegalArgumentException("GSON cannot handle " + obj);
                                        }
                                        break Label_0192;
                                    }
                                    finally {
                                        value.remove(obj);
                                        if (b) {
                                            break Label_0261;
                                        }
                                    }
                                    break Label_0156;
                                }
                                create = iterator.next().create(this, obj);
                                if (create != null) {
                                    break;
                                }
                                continue Label_0154_Outer;
                            }
                            this.calls.remove();
                            continue;
                        }
                    }
                    futureTypeAdapter2.setDelegate(create);
                    this.typeTokenCache.put(obj, create);
                    value.remove(obj);
                    if (b) {
                        this.calls.remove();
                    }
                    return create;
                }
                return futureTypeAdapter;
            }
            final TypeToken<?> null_KEY_SURROGATE = Gson.NULL_KEY_SURROGATE;
            continue;
        }
    }
    
    public <T> TypeAdapter<T> getAdapter(final Class<T> clazz) {
        return this.getAdapter((TypeToken<T>)TypeToken.get((Class<T>)clazz));
    }
    
    public <T> TypeAdapter<T> getDelegateAdapter(TypeAdapterFactory jsonAdapterFactory, final TypeToken<T> obj) {
        if (!this.factories.contains(jsonAdapterFactory)) {
            jsonAdapterFactory = this.jsonAdapterFactory;
        }
        final Iterator<TypeAdapterFactory> iterator = this.factories.iterator();
        int n = 0;
        while (iterator.hasNext()) {
            final TypeAdapterFactory typeAdapterFactory = iterator.next();
            if (n != 0) {
                final TypeAdapter<T> create = typeAdapterFactory.create(this, obj);
                if (create != null) {
                    return create;
                }
                continue;
            }
            else {
                if (typeAdapterFactory != jsonAdapterFactory) {
                    continue;
                }
                n = 1;
            }
        }
        throw new IllegalArgumentException("GSON cannot serialize " + obj);
    }
    
    public boolean htmlSafe() {
        return this.htmlSafe;
    }
    
    public JsonReader newJsonReader(final Reader reader) {
        final JsonReader jsonReader = new JsonReader(reader);
        jsonReader.setLenient(this.lenient);
        return jsonReader;
    }
    
    public JsonWriter newJsonWriter(final Writer writer) throws IOException {
        if (this.generateNonExecutableJson) {
            writer.write(")]}'\n");
        }
        final JsonWriter jsonWriter = new JsonWriter(writer);
        if (this.prettyPrinting) {
            jsonWriter.setIndent("  ");
        }
        jsonWriter.setSerializeNulls(this.serializeNulls);
        return jsonWriter;
    }
    
    public boolean serializeNulls() {
        return this.serializeNulls;
    }
    
    public String toJson(final JsonElement jsonElement) {
        final StringWriter stringWriter = new StringWriter();
        this.toJson(jsonElement, stringWriter);
        return stringWriter.toString();
    }
    
    public String toJson(final Object o) {
        if (o != null) {
            return this.toJson(o, o.getClass());
        }
        return this.toJson(JsonNull.INSTANCE);
    }
    
    public String toJson(final Object o, final Type type) {
        final StringWriter stringWriter = new StringWriter();
        this.toJson(o, type, stringWriter);
        return stringWriter.toString();
    }
    
    public void toJson(final JsonElement jsonElement, final JsonWriter jsonWriter) throws JsonIOException {
        final boolean lenient = jsonWriter.isLenient();
        jsonWriter.setLenient(true);
        final boolean htmlSafe = jsonWriter.isHtmlSafe();
        jsonWriter.setHtmlSafe(this.htmlSafe);
        final boolean serializeNulls = jsonWriter.getSerializeNulls();
        jsonWriter.setSerializeNulls(this.serializeNulls);
        try {
            Streams.write(jsonElement, jsonWriter);
        }
        catch (final IOException ex) {
            throw new JsonIOException(ex);
        }
        finally {
            jsonWriter.setLenient(lenient);
            jsonWriter.setHtmlSafe(htmlSafe);
            jsonWriter.setSerializeNulls(serializeNulls);
        }
    }
    
    public void toJson(final JsonElement jsonElement, final Appendable appendable) throws JsonIOException {
        try {
            this.toJson(jsonElement, this.newJsonWriter(Streams.writerForAppendable(appendable)));
        }
        catch (final IOException ex) {
            throw new JsonIOException(ex);
        }
    }
    
    public void toJson(final Object o, final Appendable appendable) throws JsonIOException {
        if (o == null) {
            this.toJson(JsonNull.INSTANCE, appendable);
        }
        else {
            this.toJson(o, o.getClass(), appendable);
        }
    }
    
    public void toJson(Object o, final Type type, final JsonWriter jsonWriter) throws JsonIOException {
        final TypeAdapter<?> adapter = this.getAdapter(TypeToken.get(type));
        final boolean lenient = jsonWriter.isLenient();
        jsonWriter.setLenient(true);
        final boolean htmlSafe = jsonWriter.isHtmlSafe();
        jsonWriter.setHtmlSafe(this.htmlSafe);
        final boolean serializeNulls = jsonWriter.getSerializeNulls();
        jsonWriter.setSerializeNulls(this.serializeNulls);
        try {
            adapter.write(jsonWriter, o);
        }
        catch (final IOException ex) {
            o = new JsonIOException(ex);
            throw o;
        }
        finally {
            jsonWriter.setLenient(lenient);
            jsonWriter.setHtmlSafe(htmlSafe);
            jsonWriter.setSerializeNulls(serializeNulls);
        }
    }
    
    public void toJson(final Object o, final Type type, final Appendable appendable) throws JsonIOException {
        try {
            this.toJson(o, type, this.newJsonWriter(Streams.writerForAppendable(appendable)));
        }
        catch (final IOException ex) {
            throw new JsonIOException(ex);
        }
    }
    
    public JsonElement toJsonTree(final Object o) {
        if (o != null) {
            return this.toJsonTree(o, o.getClass());
        }
        return JsonNull.INSTANCE;
    }
    
    public JsonElement toJsonTree(final Object o, final Type type) {
        final JsonTreeWriter jsonTreeWriter = new JsonTreeWriter();
        this.toJson(o, type, jsonTreeWriter);
        return jsonTreeWriter.get();
    }
    
    @Override
    public String toString() {
        return "{serializeNulls:" + this.serializeNulls + ",factories:" + this.factories + ",instanceCreators:" + this.constructorConstructor + "}";
    }
    
    static class FutureTypeAdapter<T> extends TypeAdapter<T>
    {
        private TypeAdapter<T> delegate;
        
        @Override
        public T read(final JsonReader jsonReader) throws IOException {
            if (this.delegate != null) {
                return this.delegate.read(jsonReader);
            }
            throw new IllegalStateException();
        }
        
        public void setDelegate(final TypeAdapter<T> delegate) {
            if (this.delegate == null) {
                this.delegate = delegate;
                return;
            }
            throw new AssertionError();
        }
        
        @Override
        public void write(final JsonWriter jsonWriter, final T t) throws IOException {
            if (this.delegate != null) {
                this.delegate.write(jsonWriter, t);
                return;
            }
            throw new IllegalStateException();
        }
    }
}
