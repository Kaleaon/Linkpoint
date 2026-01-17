package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Deque;
import java.util.Iterator;
@Beta
@GwtCompatible(emulated = true)
/* loaded from: classes.dex */
public abstract class BinaryTreeTraverser<T> extends TreeTraverser<T> {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class InOrderIterator extends AbstractIterator<T> {
        private final Deque<T> stack = new ArrayDeque();
        private final BitSet hasExpandedLeft = new BitSet();

        InOrderIterator(T t) {
            this.stack.addLast(t);
        }

        @Override // com.google.common.collect.AbstractIterator
        protected T computeNext() {
            while (!this.stack.isEmpty()) {
                T last = this.stack.getLast();
                if (this.hasExpandedLeft.get(this.stack.size() - 1)) {
                    this.stack.removeLast();
                    this.hasExpandedLeft.clear(this.stack.size());
                    BinaryTreeTraverser.pushIfPresent(this.stack, BinaryTreeTraverser.this.rightChild(last));
                    return last;
                }
                this.hasExpandedLeft.set(this.stack.size() - 1);
                BinaryTreeTraverser.pushIfPresent(this.stack, BinaryTreeTraverser.this.leftChild(last));
            }
            return endOfData();
        }
    }

    /* loaded from: classes.dex */
    private final class PostOrderIterator extends UnmodifiableIterator<T> {
        private final BitSet hasExpanded;
        private final Deque<T> stack = new ArrayDeque();

        PostOrderIterator(T t) {
            this.stack.addLast(t);
            this.hasExpanded = new BitSet();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return !this.stack.isEmpty();
        }

        @Override // java.util.Iterator
        public T next() {
            while (true) {
                T last = this.stack.getLast();
                if (this.hasExpanded.get(this.stack.size() - 1)) {
                    this.stack.removeLast();
                    this.hasExpanded.clear(this.stack.size());
                    return last;
                }
                this.hasExpanded.set(this.stack.size() - 1);
                BinaryTreeTraverser.pushIfPresent(this.stack, BinaryTreeTraverser.this.rightChild(last));
                BinaryTreeTraverser.pushIfPresent(this.stack, BinaryTreeTraverser.this.leftChild(last));
            }
        }
    }

    /* loaded from: classes.dex */
    private final class PreOrderIterator extends UnmodifiableIterator<T> implements PeekingIterator<T> {
        private final Deque<T> stack = new ArrayDeque();

        PreOrderIterator(T t) {
            this.stack.addLast(t);
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return !this.stack.isEmpty();
        }

        @Override // java.util.Iterator, com.google.common.collect.PeekingIterator
        public T next() {
            T removeLast = this.stack.removeLast();
            BinaryTreeTraverser.pushIfPresent(this.stack, BinaryTreeTraverser.this.rightChild(removeLast));
            BinaryTreeTraverser.pushIfPresent(this.stack, BinaryTreeTraverser.this.leftChild(removeLast));
            return removeLast;
        }

        @Override // com.google.common.collect.PeekingIterator
        public T peek() {
            return this.stack.getLast();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static <T> void pushIfPresent(Deque<T> deque, Optional<T> optional) {
        if (optional.isPresent()) {
            deque.addLast(optional.get());
        }
    }

    @Override // com.google.common.collect.TreeTraverser
    public final Iterable<T> children(final T t) {
        Preconditions.checkNotNull(t);
        return new FluentIterable<T>() { // from class: com.google.common.collect.BinaryTreeTraverser.1
            @Override // java.lang.Iterable
            public Iterator<T> iterator() {
                return new AbstractIterator<T>() { // from class: com.google.common.collect.BinaryTreeTraverser.1.1
                    boolean doneLeft;
                    boolean doneRight;

                    /* JADX WARN: Multi-variable type inference failed */
                    @Override // com.google.common.collect.AbstractIterator
                    protected T computeNext() {
                        if (!this.doneLeft) {
                            this.doneLeft = true;
                            Optional leftChild = BinaryTreeTraverser.this.leftChild(t);
                            if (leftChild.isPresent()) {
                                return (T) leftChild.get();
                            }
                        }
                        if (!this.doneRight) {
                            this.doneRight = true;
                            Optional rightChild = BinaryTreeTraverser.this.rightChild(t);
                            if (rightChild.isPresent()) {
                                return (T) rightChild.get();
                            }
                        }
                        return endOfData();
                    }
                };
            }
        };
    }

    public final FluentIterable<T> inOrderTraversal(final T t) {
        Preconditions.checkNotNull(t);
        return new FluentIterable<T>() { // from class: com.google.common.collect.BinaryTreeTraverser.2
            @Override // java.lang.Iterable
            public UnmodifiableIterator<T> iterator() {
                return new InOrderIterator(t);
            }
        };
    }

    public abstract Optional<T> leftChild(T t);

    @Override // com.google.common.collect.TreeTraverser
    UnmodifiableIterator<T> postOrderIterator(T t) {
        return new PostOrderIterator(t);
    }

    @Override // com.google.common.collect.TreeTraverser
    UnmodifiableIterator<T> preOrderIterator(T t) {
        return new PreOrderIterator(t);
    }

    public abstract Optional<T> rightChild(T t);
}
