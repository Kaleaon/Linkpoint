// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson.internal;

import java.util.Collection;
import java.util.ArrayList;
import com.google.gson.FieldAttributes;
import com.google.gson.annotations.Expose;
import java.lang.reflect.Field;
import java.util.Iterator;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import com.google.gson.stream.JsonReader;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.annotations.Until;
import com.google.gson.annotations.Since;
import java.util.Collections;
import com.google.gson.ExclusionStrategy;
import java.util.List;
import com.google.gson.TypeAdapterFactory;

public final class Excluder implements TypeAdapterFactory, Cloneable
{
    public static final Excluder DEFAULT;
    private static final double IGNORE_VERSIONS = -1.0;
    private List<ExclusionStrategy> deserializationStrategies;
    private int modifiers;
    private boolean requireExpose;
    private List<ExclusionStrategy> serializationStrategies;
    private boolean serializeInnerClasses;
    private double version;
    
    static {
        DEFAULT = new Excluder();
    }
    
    public Excluder() {
        this.version = -1.0;
        this.modifiers = 136;
        this.serializeInnerClasses = true;
        this.serializationStrategies = Collections.emptyList();
        this.deserializationStrategies = Collections.emptyList();
    }
    
    private boolean isAnonymousOrLocal(final Class<?> clazz) {
        boolean b = false;
        if (!Enum.class.isAssignableFrom(clazz)) {
            if (clazz.isAnonymousClass() || clazz.isLocalClass()) {
                b = true;
            }
        }
        return b;
    }
    
    private boolean isInnerClass(final Class<?> clazz) {
        boolean b = false;
        if (clazz.isMemberClass() && !this.isStatic(clazz)) {
            b = true;
        }
        return b;
    }
    
    private boolean isStatic(final Class<?> clazz) {
        boolean b = false;
        if ((clazz.getModifiers() & 0x8) != 0x0) {
            b = true;
        }
        return b;
    }
    
    private boolean isValidSince(final Since since) {
        return since == null || since.value() <= this.version;
    }
    
    private boolean isValidUntil(final Until until) {
        return until == null || until.value() > this.version;
    }
    
    private boolean isValidVersion(final Since since, final Until until) {
        boolean b = false;
        if (this.isValidSince(since) && this.isValidUntil(until)) {
            b = true;
        }
        return b;
    }
    
    @Override
    protected Excluder clone() {
        try {
            return (Excluder)super.clone();
        }
        catch (final CloneNotSupportedException detailMessage) {
            throw new AssertionError((Object)detailMessage);
        }
    }
    
    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
        final Class<? super T> rawType = typeToken.getRawType();
        final boolean excludeClass = this.excludeClass(rawType, true);
        final boolean excludeClass2 = this.excludeClass(rawType, false);
        if (!excludeClass && !excludeClass2) {
            return null;
        }
        return new TypeAdapter<T>() {
            private TypeAdapter<T> delegate;
            
            private TypeAdapter<T> delegate() {
                TypeAdapter<T> delegate;
                if ((delegate = this.delegate) == null) {
                    delegate = gson.getDelegateAdapter(Excluder.this, typeToken);
                    this.delegate = delegate;
                }
                return delegate;
            }
            
            @Override
            public T read(final JsonReader jsonReader) throws IOException {
                if (!excludeClass2) {
                    return this.delegate().read(jsonReader);
                }
                jsonReader.skipValue();
                return null;
            }
            
            @Override
            public void write(final JsonWriter jsonWriter, final T t) throws IOException {
                if (!excludeClass) {
                    this.delegate().write(jsonWriter, t);
                    return;
                }
                jsonWriter.nullValue();
            }
        };
    }
    
    public Excluder disableInnerClassSerialization() {
        final Excluder clone = this.clone();
        clone.serializeInnerClasses = false;
        return clone;
    }
    
    public boolean excludeClass(final Class<?> clazz, final boolean b) {
        if (this.version != -1.0 && !this.isValidVersion(clazz.getAnnotation(Since.class), clazz.getAnnotation(Until.class))) {
            return true;
        }
        if (!this.serializeInnerClasses && this.isInnerClass(clazz)) {
            return true;
        }
        if (!this.isAnonymousOrLocal(clazz)) {
            List<ExclusionStrategy> list;
            if (!b) {
                list = this.deserializationStrategies;
            }
            else {
                list = this.serializationStrategies;
            }
            final Iterator<ExclusionStrategy> iterator = list.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().shouldSkipClass(clazz)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public boolean excludeField(final Field field, final boolean b) {
        if ((this.modifiers & field.getModifiers()) != 0x0) {
            return true;
        }
        if (this.version != -1.0 && !this.isValidVersion(field.getAnnotation(Since.class), field.getAnnotation(Until.class))) {
            return true;
        }
        if (field.isSynthetic()) {
            return true;
        }
        Label_0062: {
            if (this.requireExpose) {
                final Expose expose = field.getAnnotation(Expose.class);
                if (expose != null) {
                    if (!b) {
                        if (expose.deserialize()) {
                            break Label_0062;
                        }
                    }
                    else if (expose.serialize()) {
                        break Label_0062;
                    }
                }
                return true;
            }
        }
        if (!this.serializeInnerClasses && this.isInnerClass(field.getType())) {
            return true;
        }
        if (!this.isAnonymousOrLocal(field.getType())) {
            List<ExclusionStrategy> list;
            if (!b) {
                list = this.deserializationStrategies;
            }
            else {
                list = this.serializationStrategies;
            }
            if (!list.isEmpty()) {
                final FieldAttributes fieldAttributes = new FieldAttributes(field);
                final Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    if (((ExclusionStrategy)iterator.next()).shouldSkipField(fieldAttributes)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }
    
    public Excluder excludeFieldsWithoutExposeAnnotation() {
        final Excluder clone = this.clone();
        clone.requireExpose = true;
        return clone;
    }
    
    public Excluder withExclusionStrategy(final ExclusionStrategy exclusionStrategy, final boolean b, final boolean b2) {
        final Excluder clone = this.clone();
        if (b) {
            (clone.serializationStrategies = new ArrayList<ExclusionStrategy>(this.serializationStrategies)).add(exclusionStrategy);
        }
        if (b2) {
            (clone.deserializationStrategies = new ArrayList<ExclusionStrategy>(this.deserializationStrategies)).add(exclusionStrategy);
        }
        return clone;
    }
    
    public Excluder withModifiers(final int... array) {
        int i = 0;
        final Excluder clone = this.clone();
        clone.modifiers = 0;
        while (i < array.length) {
            clone.modifiers |= array[i];
            ++i;
        }
        return clone;
    }
    
    public Excluder withVersion(final double version) {
        final Excluder clone = this.clone();
        clone.version = version;
        return clone;
    }
}
