package com.google.gson.internal.bind;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ReflectiveTypeAdapterFactory implements TypeAdapterFactory {
    private final ConstructorConstructor constructorConstructor;
    private final Excluder excluder;
    private final FieldNamingStrategy fieldNamingPolicy;
    private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;

    public static final class Adapter<T> extends TypeAdapter<T> {
        private final Map<String, BoundField> boundFields;
        private final ObjectConstructor<T> constructor;

        Adapter(ObjectConstructor<T> objectConstructor, Map<String, BoundField> map) {
            this.constructor = objectConstructor;
            this.boundFields = map;
        }

        public T read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() != JsonToken.NULL) {
                T construct = this.constructor.construct();
                try {
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        BoundField boundField = this.boundFields.get(jsonReader.nextName());
                        if (boundField != null) {
                            if (boundField.deserialized) {
                                boundField.read(jsonReader, construct);
                            }
                        }
                        jsonReader.skipValue();
                    }
                    jsonReader.endObject();
                    return construct;
                } catch (IllegalStateException e) {
                    throw new JsonSyntaxException((Throwable) e);
                } catch (IllegalAccessException e2) {
                    throw new AssertionError(e2);
                }
            } else {
                jsonReader.nextNull();
                return null;
            }
        }

        public void write(JsonWriter jsonWriter, T t) throws IOException {
            if (t != null) {
                jsonWriter.beginObject();
                try {
                    for (BoundField next : this.boundFields.values()) {
                        if (next.writeField(t)) {
                            jsonWriter.name(next.name);
                            next.write(jsonWriter, t);
                        }
                    }
                    jsonWriter.endObject();
                } catch (IllegalAccessException e) {
                    throw new AssertionError(e);
                }
            } else {
                jsonWriter.nullValue();
            }
        }
    }

    static abstract class BoundField {
        final boolean deserialized;
        final String name;
        final boolean serialized;

        protected BoundField(String str, boolean z, boolean z2) {
            this.name = str;
            this.serialized = z;
            this.deserialized = z2;
        }

        /* access modifiers changed from: package-private */
        public abstract void read(JsonReader jsonReader, Object obj) throws IOException, IllegalAccessException;

        /* access modifiers changed from: package-private */
        public abstract void write(JsonWriter jsonWriter, Object obj) throws IOException, IllegalAccessException;

        /* access modifiers changed from: package-private */
        public abstract boolean writeField(Object obj) throws IOException, IllegalAccessException;
    }

    public ReflectiveTypeAdapterFactory(ConstructorConstructor constructorConstructor2, FieldNamingStrategy fieldNamingStrategy, Excluder excluder2, JsonAdapterAnnotationTypeAdapterFactory jsonAdapterAnnotationTypeAdapterFactory) {
        this.constructorConstructor = constructorConstructor2;
        this.fieldNamingPolicy = fieldNamingStrategy;
        this.excluder = excluder2;
        this.jsonAdapterFactory = jsonAdapterAnnotationTypeAdapterFactory;
    }

    private BoundField createBoundField(Gson gson, Field field, String str, TypeToken<?> typeToken, boolean z, boolean z2) {
        final boolean isPrimitive = Primitives.isPrimitive(typeToken.getRawType());
        JsonAdapter jsonAdapter = (JsonAdapter) field.getAnnotation(JsonAdapter.class);
        final TypeAdapter<?> typeAdapter = null;
        if (jsonAdapter != null) {
            typeAdapter = this.jsonAdapterFactory.getTypeAdapter(this.constructorConstructor, gson, typeToken, jsonAdapter);
        }
        final boolean z3 = typeAdapter != null;
        if (typeAdapter == null) {
            typeAdapter = gson.getAdapter(typeToken);
        }
        final Field field2 = field;
        final Gson gson2 = gson;
        final TypeToken<?> typeToken2 = typeToken;
        return new BoundField(str, z, z2) {
            /* access modifiers changed from: package-private */
            public void read(JsonReader jsonReader, Object obj) throws IOException, IllegalAccessException {
                Object read = typeAdapter.read(jsonReader);
                if (read != null || !isPrimitive) {
                    field2.set(obj, read);
                }
            }

            /* access modifiers changed from: package-private */
            public void write(JsonWriter jsonWriter, Object obj) throws IOException, IllegalAccessException {
                (!z3 ? new TypeAdapterRuntimeTypeWrapper(gson2, typeAdapter, typeToken2.getType()) : typeAdapter).write(jsonWriter, field2.get(obj));
            }

            public boolean writeField(Object obj) throws IOException, IllegalAccessException {
                return this.serialized && field2.get(obj) != obj;
            }
        };
    }

    static boolean excludeField(Field field, boolean z, Excluder excluder2) {
        return !excluder2.excludeClass(field.getType(), z) && !excluder2.excludeField(field, z);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: java.lang.Class<java.lang.Object>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v2, resolved type: com.google.gson.reflect.TypeToken<?>} */
    /* JADX WARNING: Incorrect type for immutable var: ssa=java.lang.Class<?>, code=java.lang.Class, for r21v0, types: [java.lang.Class<?>, java.lang.Class] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Map<java.lang.String, com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.BoundField> getBoundFields(com.google.gson.Gson r19, com.google.gson.reflect.TypeToken<?> r20, java.lang.Class r21) {
        /*
            r18 = this;
            java.util.LinkedHashMap r11 = new java.util.LinkedHashMap
            r11.<init>()
            boolean r1 = r21.isInterface()
            if (r1 != 0) goto L_0x0016
            java.lang.reflect.Type r12 = r20.getType()
        L_0x000f:
            java.lang.Class<java.lang.Object> r1 = java.lang.Object.class
            r0 = r21
            if (r0 != r1) goto L_0x0017
            return r11
        L_0x0016:
            return r11
        L_0x0017:
            java.lang.reflect.Field[] r13 = r21.getDeclaredFields()
            int r14 = r13.length
            r1 = 0
            r10 = r1
        L_0x001e:
            if (r10 < r14) goto L_0x0037
            java.lang.reflect.Type r1 = r20.getType()
            java.lang.reflect.Type r2 = r21.getGenericSuperclass()
            r0 = r21
            java.lang.reflect.Type r1 = com.google.gson.internal.C$Gson$Types.resolve(r1, r0, r2)
            com.google.gson.reflect.TypeToken r20 = com.google.gson.reflect.TypeToken.get((java.lang.reflect.Type) r1)
            java.lang.Class r21 = r20.getRawType()
            goto L_0x000f
        L_0x0037:
            r3 = r13[r10]
            r1 = 1
            r0 = r18
            boolean r6 = r0.excludeField(r3, r1)
            r1 = 0
            r0 = r18
            boolean r7 = r0.excludeField(r3, r1)
            if (r6 == 0) goto L_0x0072
        L_0x0049:
            r1 = 1
            r3.setAccessible(r1)
            java.lang.reflect.Type r1 = r20.getType()
            java.lang.reflect.Type r2 = r3.getGenericType()
            r0 = r21
            java.lang.reflect.Type r15 = com.google.gson.internal.C$Gson$Types.resolve(r1, r0, r2)
            r0 = r18
            java.util.List r16 = r0.getFieldNames(r3)
            r8 = 0
            r1 = 0
            int r17 = r16.size()
            r9 = r1
        L_0x0068:
            r0 = r17
            if (r9 < r0) goto L_0x0075
            if (r8 != 0) goto L_0x009b
        L_0x006e:
            int r1 = r10 + 1
            r10 = r1
            goto L_0x001e
        L_0x0072:
            if (r7 == 0) goto L_0x006e
            goto L_0x0049
        L_0x0075:
            r0 = r16
            java.lang.Object r4 = r0.get(r9)
            java.lang.String r4 = (java.lang.String) r4
            if (r9 != 0) goto L_0x0099
        L_0x007f:
            com.google.gson.reflect.TypeToken r5 = com.google.gson.reflect.TypeToken.get((java.lang.reflect.Type) r15)
            r1 = r18
            r2 = r19
            com.google.gson.internal.bind.ReflectiveTypeAdapterFactory$BoundField r1 = r1.createBoundField(r2, r3, r4, r5, r6, r7)
            java.lang.Object r1 = r11.put(r4, r1)
            com.google.gson.internal.bind.ReflectiveTypeAdapterFactory$BoundField r1 = (com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.BoundField) r1
            if (r8 == 0) goto L_0x0094
            r1 = r8
        L_0x0094:
            int r2 = r9 + 1
            r9 = r2
            r8 = r1
            goto L_0x0068
        L_0x0099:
            r6 = 0
            goto L_0x007f
        L_0x009b:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.StringBuilder r2 = r2.append(r12)
            java.lang.String r3 = " declares multiple JSON fields named "
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = r8.name
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.getBoundFields(com.google.gson.Gson, com.google.gson.reflect.TypeToken, java.lang.Class):java.util.Map");
    }

    private List<String> getFieldNames(Field field) {
        SerializedName serializedName = (SerializedName) field.getAnnotation(SerializedName.class);
        if (serializedName == null) {
            return Collections.singletonList(this.fieldNamingPolicy.translateName(field));
        }
        String value = serializedName.value();
        String[] alternate = serializedName.alternate();
        if (alternate.length == 0) {
            return Collections.singletonList(value);
        }
        ArrayList arrayList = new ArrayList(alternate.length + 1);
        arrayList.add(value);
        for (String add : alternate) {
            arrayList.add(add);
        }
        return arrayList;
    }

    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Class<? super T> rawType = typeToken.getRawType();
        if (Object.class.isAssignableFrom(rawType)) {
            return new Adapter(this.constructorConstructor.get(typeToken), getBoundFields(gson, typeToken, rawType));
        }
        return null;
    }

    public boolean excludeField(Field field, boolean z) {
        return excludeField(field, z, this.excluder);
    }
}
