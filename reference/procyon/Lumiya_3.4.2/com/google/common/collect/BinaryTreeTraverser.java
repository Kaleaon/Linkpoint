// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import com.google.common.base.Optional;
import java.util.Deque;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible(emulated = true)
public abstract class BinaryTreeTraverser<T> extends TreeTraverser<T>
{
    private static <T> void pushIfPresent(final Deque<T> deque, final Optional<T> optional) {
        if (optional.isPresent()) {
            deque.addLast(optional.get());
        }
    }
    
    @Override
    public final Iterable<T> children(final T t) {
        Preconditions.checkNotNull(t);
        return new FluentIterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new AbstractIterator<T>() {
                    boolean doneLeft;
                    boolean doneRight;
                    
                    @Override
                    protected T computeNext() {
                        if (!this.doneLeft) {
                            this.doneLeft = true;
                            final Optional<Object> leftChild = BinaryTreeTraverser.this.leftChild(t);
                            if (leftChild.isPresent()) {
                                return leftChild.get();
                            }
                        }
                        if (!this.doneRight) {
                            this.doneRight = true;
                            final Optional<Object> rightChild = BinaryTreeTraverser.this.rightChild(t);
                            if (rightChild.isPresent()) {
                                return rightChild.get();
                            }
                        }
                        return this.endOfData();
                    }
                };
            }
        };
    }
    
    public final FluentIterable<T> inOrderTraversal(final T t) {
        Preconditions.checkNotNull(t);
        return new FluentIterable<T>() {
            @Override
            public UnmodifiableIterator<T> iterator() {
                return new InOrderIterator(t);
            }
        };
    }
    
    public abstract Optional<T> leftChild(final T p0);
    
    @Override
    UnmodifiableIterator<T> postOrderIterator(final T t) {
        return new PostOrderIterator(t);
    }
    
    @Override
    UnmodifiableIterator<T> preOrderIterator(final T t) {
        return new PreOrderIterator(t);
    }
    
    public abstract Optional<T> rightChild(final T p0);
    
    private final class InOrderIterator extends AbstractIterator<T>
    {
        private final BitSet hasExpandedLeft;
        private final Deque<T> stack;
        
        InOrderIterator(final T t) {
            this.stack = new ArrayDeque<T>();
            this.hasExpandedLeft = new BitSet();
            this.stack.addLast(t);
        }
        
        @Override
        protected T computeNext() {
            while (!this.stack.isEmpty()) {
                final T last = this.stack.getLast();
                if (this.hasExpandedLeft.get(this.stack.size() - 1)) {
                    this.stack.removeLast();
                    this.hasExpandedLeft.clear(this.stack.size());
                    pushIfPresent(this.stack, (Optional<Object>)BinaryTreeTraverser.this.rightChild(last));
                    return last;
                }
                this.hasExpandedLeft.set(this.stack.size() - 1);
                pushIfPresent(this.stack, (Optional<Object>)BinaryTreeTraverser.this.leftChild(last));
            }
            return this.endOfData();
        }
    }
    
    private final class PostOrderIterator extends UnmodifiableIterator<T>
    {
        private final BitSet hasExpanded;
        private final Deque<T> stack;
        
        PostOrderIterator(final T t) {
            (this.stack = new ArrayDeque<T>()).addLast(t);
            this.hasExpanded = new BitSet();
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
            T last;
            while (true) {
                last = this.stack.getLast();
                if (this.hasExpanded.get(this.stack.size() - 1)) {
                    break;
                }
                this.hasExpanded.set(this.stack.size() - 1);
                pushIfPresent(this.stack, (Optional<Object>)BinaryTreeTraverser.this.rightChild(last));
                pushIfPresent(this.stack, (Optional<Object>)BinaryTreeTraverser.this.leftChild(last));
            }
            this.stack.removeLast();
            this.hasExpanded.clear(this.stack.size());
            return last;
        }
    }
    
    private final class PreOrderIterator extends UnmodifiableIterator<T> implements PeekingIterator<T>
    {
        private final Deque<T> stack;
        
        PreOrderIterator(final T t) {
            (this.stack = new ArrayDeque<T>()).addLast(t);
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
            final T removeLast = this.stack.removeLast();
            pushIfPresent(this.stack, (Optional<Object>)BinaryTreeTraverser.this.rightChild(removeLast));
            pushIfPresent(this.stack, (Optional<Object>)BinaryTreeTraverser.this.leftChild(removeLast));
            return removeLast;
        }
        
        @Override
        public T peek() {
            return this.stack.getLast();
        }
    }
}
