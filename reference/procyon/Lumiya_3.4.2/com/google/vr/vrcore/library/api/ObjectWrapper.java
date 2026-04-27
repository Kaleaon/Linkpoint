// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.library.api;

import java.lang.reflect.Field;
import android.os.IBinder;
import com.google.vr.cardboard.annotations.UsedByReflection;

@UsedByReflection("ObjectWrapper.java")
public final class ObjectWrapper<T> extends Stub
{
    @UsedByReflection("ObjectWrapper.java")
    private final T wrappedObject;
    
    private ObjectWrapper(final T wrappedObject) {
        this.wrappedObject = wrappedObject;
    }
    
    public static <T> T unwrap(final IObjectWrapper objectWrapper, final Class<T> clazz) {
        if (objectWrapper instanceof ObjectWrapper) {
            return (T)((ObjectWrapper)objectWrapper).wrappedObject;
        }
        final IBinder binder = objectWrapper.asBinder();
        final Field[] declaredFields = binder.getClass().getDeclaredFields();
        if (declaredFields.length != 1) {
            throw new IllegalArgumentException("The concrete class implementing IObjectWrapper must have exactly *one* declared private field for the wrapped object.  Preferably, this is an instance of the ObjectWrapper<T> class.");
        }
        final Field field = declaredFields[0];
        if (field.isAccessible()) {
            throw new IllegalArgumentException("The concrete class implementing IObjectWrapper must have exactly one declared *private* field for the wrapped object. Preferably, this is an instance of the ObjectWrapper<T> class.");
        }
        field.setAccessible(true);
        try {
            final Object value = field.get(binder);
            if (clazz.isInstance(value)) {
                return (T)clazz.cast(value);
            }
            throw new IllegalArgumentException("remoteBinder is the wrong class.");
        }
        catch (final NullPointerException cause) {
            throw new IllegalArgumentException("Binder object is null.", cause);
        }
        catch (final IllegalArgumentException cause2) {
            throw new IllegalArgumentException("remoteBinder is the wrong class.", cause2);
        }
        catch (final IllegalAccessException cause3) {
            throw new IllegalArgumentException("Could not access the field in remoteBinder.", cause3);
        }
    }
    
    public static <T> IObjectWrapper wrap(final T t) {
        return new ObjectWrapper<Object>(t);
    }
}
