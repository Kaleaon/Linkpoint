// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.SortedSet;
import java.util.Set;
import java.util.RandomAccess;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Collection;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
final class Constraints
{
    private Constraints() {
    }
    
    private static <E> Collection<E> checkElements(final Collection<E> collection, final Constraint<? super E> constraint) {
        final ArrayList<Object> arrayList = Lists.newArrayList((Iterable<?>)collection);
        final Iterator<Object> iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            constraint.checkElement((Object)iterator.next());
        }
        return (Collection<E>)arrayList;
    }
    
    public static <E> Collection<E> constrainedCollection(final Collection<E> collection, final Constraint<? super E> constraint) {
        return new ConstrainedCollection<E>(collection, constraint);
    }
    
    public static <E> List<E> constrainedList(final List<E> list, final Constraint<? super E> constraint) {
        ForwardingObject forwardingObject;
        if (!(list instanceof RandomAccess)) {
            forwardingObject = new ConstrainedList<Object>(list, constraint);
        }
        else {
            forwardingObject = new ConstrainedRandomAccessList<Object>(list, constraint);
        }
        return (List<E>)forwardingObject;
    }
    
    private static <E> ListIterator<E> constrainedListIterator(final ListIterator<E> listIterator, final Constraint<? super E> constraint) {
        return new ConstrainedListIterator<E>(listIterator, constraint);
    }
    
    public static <E> Set<E> constrainedSet(final Set<E> set, final Constraint<? super E> constraint) {
        return new ConstrainedSet<E>(set, constraint);
    }
    
    public static <E> SortedSet<E> constrainedSortedSet(final SortedSet<E> set, final Constraint<? super E> constraint) {
        return new ConstrainedSortedSet<E>(set, constraint);
    }
    
    static <E> Collection<E> constrainedTypePreservingCollection(final Collection<E> collection, final Constraint<E> constraint) {
        if (collection instanceof SortedSet) {
            return (Collection<E>)constrainedSortedSet((SortedSet<Object>)collection, (Constraint<? super Object>)constraint);
        }
        if (collection instanceof Set) {
            return (Collection<E>)constrainedSet((Set<Object>)collection, (Constraint<? super Object>)constraint);
        }
        if (!(collection instanceof List)) {
            return (Collection<E>)constrainedCollection((Collection<Object>)collection, (Constraint<? super Object>)constraint);
        }
        return (Collection<E>)constrainedList((List<Object>)collection, (Constraint<? super Object>)constraint);
    }
    
    static class ConstrainedCollection<E> extends ForwardingCollection<E>
    {
        private final Constraint<? super E> constraint;
        private final Collection<E> delegate;
        
        public ConstrainedCollection(final Collection<E> collection, final Constraint<? super E> constraint) {
            this.delegate = Preconditions.checkNotNull(collection);
            this.constraint = Preconditions.checkNotNull(constraint);
        }
        
        @Override
        public boolean add(final E e) {
            this.constraint.checkElement(e);
            return this.delegate.add(e);
        }
        
        @Override
        public boolean addAll(final Collection<? extends E> collection) {
            return this.delegate.addAll(checkElements((Collection<Object>)collection, this.constraint));
        }
        
        @Override
        protected Collection<E> delegate() {
            return this.delegate;
        }
    }
    
    @GwtCompatible
    private static class ConstrainedList<E> extends ForwardingList<E>
    {
        final Constraint<? super E> constraint;
        final List<E> delegate;
        
        ConstrainedList(final List<E> list, final Constraint<? super E> constraint) {
            this.delegate = Preconditions.checkNotNull(list);
            this.constraint = Preconditions.checkNotNull(constraint);
        }
        
        @Override
        public void add(final int n, final E e) {
            this.constraint.checkElement(e);
            this.delegate.add(n, e);
        }
        
        @Override
        public boolean add(final E e) {
            this.constraint.checkElement(e);
            return this.delegate.add(e);
        }
        
        @Override
        public boolean addAll(final int n, final Collection<? extends E> collection) {
            return this.delegate.addAll(n, checkElements((Collection<Object>)collection, this.constraint));
        }
        
        @Override
        public boolean addAll(final Collection<? extends E> collection) {
            return this.delegate.addAll(checkElements((Collection<Object>)collection, this.constraint));
        }
        
        @Override
        protected List<E> delegate() {
            return this.delegate;
        }
        
        @Override
        public ListIterator<E> listIterator() {
            return (ListIterator<E>)constrainedListIterator((ListIterator<Object>)this.delegate.listIterator(), this.constraint);
        }
        
        @Override
        public ListIterator<E> listIterator(final int n) {
            return (ListIterator<E>)constrainedListIterator((ListIterator<Object>)this.delegate.listIterator(n), this.constraint);
        }
        
        @Override
        public E set(final int n, final E e) {
            this.constraint.checkElement(e);
            return this.delegate.set(n, e);
        }
        
        @Override
        public List<E> subList(final int n, final int n2) {
            return Constraints.constrainedList(this.delegate.subList(n, n2), this.constraint);
        }
    }
    
    static class ConstrainedListIterator<E> extends ForwardingListIterator<E>
    {
        private final Constraint<? super E> constraint;
        private final ListIterator<E> delegate;
        
        public ConstrainedListIterator(final ListIterator<E> delegate, final Constraint<? super E> constraint) {
            this.delegate = delegate;
            this.constraint = constraint;
        }
        
        @Override
        public void add(final E e) {
            this.constraint.checkElement(e);
            this.delegate.add(e);
        }
        
        @Override
        protected ListIterator<E> delegate() {
            return this.delegate;
        }
        
        @Override
        public void set(final E e) {
            this.constraint.checkElement(e);
            this.delegate.set(e);
        }
    }
    
    static class ConstrainedRandomAccessList<E> extends ConstrainedList<E> implements RandomAccess
    {
        ConstrainedRandomAccessList(final List<E> list, final Constraint<? super E> constraint) {
            super(list, constraint);
        }
    }
    
    static class ConstrainedSet<E> extends ForwardingSet<E>
    {
        private final Constraint<? super E> constraint;
        private final Set<E> delegate;
        
        public ConstrainedSet(final Set<E> set, final Constraint<? super E> constraint) {
            this.delegate = Preconditions.checkNotNull(set);
            this.constraint = Preconditions.checkNotNull(constraint);
        }
        
        @Override
        public boolean add(final E e) {
            this.constraint.checkElement(e);
            return this.delegate.add(e);
        }
        
        @Override
        public boolean addAll(final Collection<? extends E> collection) {
            return this.delegate.addAll(checkElements((Collection<Object>)collection, this.constraint));
        }
        
        @Override
        protected Set<E> delegate() {
            return this.delegate;
        }
    }
    
    private static class ConstrainedSortedSet<E> extends ForwardingSortedSet<E>
    {
        final Constraint<? super E> constraint;
        final SortedSet<E> delegate;
        
        ConstrainedSortedSet(final SortedSet<E> set, final Constraint<? super E> constraint) {
            this.delegate = Preconditions.checkNotNull(set);
            this.constraint = Preconditions.checkNotNull(constraint);
        }
        
        @Override
        public boolean add(final E e) {
            this.constraint.checkElement(e);
            return this.delegate.add(e);
        }
        
        @Override
        public boolean addAll(final Collection<? extends E> collection) {
            return this.delegate.addAll((Collection<?>)checkElements((Collection<Object>)collection, this.constraint));
        }
        
        @Override
        protected SortedSet<E> delegate() {
            return this.delegate;
        }
        
        @Override
        public SortedSet<E> headSet(final E e) {
            return Constraints.constrainedSortedSet(this.delegate.headSet(e), this.constraint);
        }
        
        @Override
        public SortedSet<E> subSet(final E e, final E e2) {
            return Constraints.constrainedSortedSet(this.delegate.subSet(e, e2), this.constraint);
        }
        
        @Override
        public SortedSet<E> tailSet(final E e) {
            return Constraints.constrainedSortedSet(this.delegate.tailSet(e), this.constraint);
        }
    }
}
