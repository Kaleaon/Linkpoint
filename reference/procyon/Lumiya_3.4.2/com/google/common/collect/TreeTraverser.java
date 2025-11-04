// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Deque;
import java.util.Collection;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible(emulated = true)
public abstract class TreeTraverser<T>
{
    public final FluentIterable<T> breadthFirstTraversal(final T t) {
        Preconditions.checkNotNull(t);
        return new FluentIterable<T>() {
            @Override
            public UnmodifiableIterator<T> iterator() {
                return new BreadthFirstIterator(t);
            }
        };
    }
    
    public abstract Iterable<T> children(final T p0);
    
    UnmodifiableIterator<T> postOrderIterator(final T t) {
        return new PostOrderIterator(t);
    }
    
    public final FluentIterable<T> postOrderTraversal(final T t) {
        Preconditions.checkNotNull(t);
        return new FluentIterable<T>() {
            @Override
            public UnmodifiableIterator<T> iterator() {
                return (UnmodifiableIterator<T>)TreeTraverser.this.postOrderIterator(t);
            }
        };
    }
    
    UnmodifiableIterator<T> preOrderIterator(final T t) {
        return new PreOrderIterator(t);
    }
    
    public final FluentIterable<T> preOrderTraversal(final T t) {
        Preconditions.checkNotNull(t);
        return new FluentIterable<T>() {
            @Override
            public UnmodifiableIterator<T> iterator() {
                return (UnmodifiableIterator<T>)TreeTraverser.this.preOrderIterator(t);
            }
        };
    }
    
    private final class BreadthFirstIterator extends UnmodifiableIterator<T> implements PeekingIterator<T>
    {
        private final Queue<T> queue;
        
        BreadthFirstIterator(final T t) {
            (this.queue = new ArrayDeque<T>()).add(t);
        }
        
        @Override
        public boolean hasNext() {
            boolean b = false;
            if (!this.queue.isEmpty()) {
                b = true;
            }
            return b;
        }
        
        @Override
        public T next() {
            final T remove = this.queue.remove();
            Iterables.addAll(this.queue, (Iterable<? extends T>)TreeTraverser.this.children(remove));
            return remove;
        }
        
        @Override
        public T peek() {
            return this.queue.element();
        }
    }
    
    private final class PostOrderIterator extends AbstractIterator<T>
    {
        private final ArrayDeque<PostOrderNode<T>> stack;
        
        PostOrderIterator(final T t) {
            (this.stack = new ArrayDeque<PostOrderNode<T>>()).addLast(this.expand(t));
        }
        
        private PostOrderNode<T> expand(final T t) {
            return (PostOrderNode<T>)new PostOrderNode(t, (Iterator<Object>)TreeTraverser.this.children(t).iterator());
        }
        
        @Override
        protected T computeNext() {
            while (!this.stack.isEmpty()) {
                final PostOrderNode postOrderNode = this.stack.getLast();
                if (!postOrderNode.childIterator.hasNext()) {
                    this.stack.removeLast();
                    return (T)postOrderNode.root;
                }
                this.stack.addLast((PostOrderNode<T>)this.expand(postOrderNode.childIterator.next()));
            }
            return this.endOfData();
        }
    }
    
    private static final class PostOrderNode<T>
    {
        final Iterator<T> childIterator;
        final T root;
        
        PostOrderNode(final T t, final Iterator<T> iterator) {
            this.root = Preconditions.checkNotNull(t);
            this.childIterator = Preconditions.checkNotNull(iterator);
        }
    }
    
    private final class PreOrderIterator extends UnmodifiableIterator<T>
    {
        private final Deque<Iterator<T>> stack;
        
        PreOrderIterator(final T t) {
            (this.stack = new ArrayDeque<Iterator<T>>()).addLast((Iterator<T>)Iterators.singletonIterator((Object)Preconditions.checkNotNull((T)t)));
        }
        
        @Override
        public boolean hasNext() {
            boolean b = false;
            if (!this.stack.isEmpty()) {
                b = true;
            }
            return b;
        }
        
        @Override
        public T next() {
            final Iterator iterator = this.stack.getLast();
            final T checkNotNull = Preconditions.checkNotNull(iterator.next());
            if (!iterator.hasNext()) {
                this.stack.removeLast();
            }
            final Iterator<T> iterator2 = TreeTraverser.this.children(checkNotNull).iterator();
            if (iterator2.hasNext()) {
                this.stack.addLast(iterator2);
            }
            return checkNotNull;
        }
    }
}
