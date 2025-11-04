// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Set;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.Collection;
import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.EnumMap;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class EnumMultiset<E extends Enum<E>> extends AbstractMapBasedMultiset<E>
{
    @GwtIncompatible("Not needed in emulated source")
    private static final long serialVersionUID = 0L;
    private transient Class<E> type;
    
    private EnumMultiset(final Class<E> clazz) {
        super((Map<Object, Count>)WellBehavedMap.wrap(new EnumMap<Object, Object>((Class<Object>)clazz)));
        this.type = clazz;
    }
    
    public static <E extends Enum<E>> EnumMultiset<E> create(final Class<E> clazz) {
        return new EnumMultiset<E>(clazz);
    }
    
    public static <E extends Enum<E>> EnumMultiset<E> create(final Iterable<E> iterable) {
        final Iterator<? extends T> iterator = (Iterator<? extends T>)iterable.iterator();
        Preconditions.checkArgument(iterator.hasNext(), (Object)"EnumMultiset constructor passed empty Iterable");
        final EnumMultiset enumMultiset = new EnumMultiset<E>((Class<E>)iterator.next().getDeclaringClass());
        Iterables.addAll((Collection<E>)enumMultiset, (Iterable<? extends E>)iterable);
        return (EnumMultiset<E>)enumMultiset;
    }
    
    public static <E extends Enum<E>> EnumMultiset<E> create(final Iterable<E> iterable, final Class<E> clazz) {
        final EnumMultiset<E> create = create(clazz);
        Iterables.addAll(create, (Iterable<? extends E>)iterable);
        return create;
    }
    
    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.type = (Class)objectInputStream.readObject();
        this.setBackingMap((Map<E, Count>)WellBehavedMap.wrap(new EnumMap<Object, Object>((Class<Object>)this.type)));
        Serialization.populateMultiset((Multiset<Object>)this, objectInputStream);
    }
    
    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.type);
        Serialization.writeMultiset((Multiset<Object>)this, objectOutputStream);
    }
}
