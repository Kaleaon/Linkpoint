package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Serialization;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;
@GwtCompatible(emulated = true)
/* loaded from: classes.dex */
public final class TreeMultiset<E> extends AbstractSortedMultiset<E> implements Serializable {
    @GwtIncompatible("not needed in emulated source")
    private static final long serialVersionUID = 1;
    private final transient AvlNode<E> header;
    private final transient GeneralRange<E> range;
    private final transient Reference<AvlNode<E>> rootReference;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum Aggregate {
        SIZE { // from class: com.google.common.collect.TreeMultiset.Aggregate.1
            @Override // com.google.common.collect.TreeMultiset.Aggregate
            int nodeAggregate(AvlNode<?> avlNode) {
                return ((AvlNode) avlNode).elemCount;
            }

            @Override // com.google.common.collect.TreeMultiset.Aggregate
            long treeAggregate(@Nullable AvlNode<?> avlNode) {
                if (avlNode != null) {
                    return ((AvlNode) avlNode).totalCount;
                }
                return 0L;
            }
        },
        DISTINCT { // from class: com.google.common.collect.TreeMultiset.Aggregate.2
            @Override // com.google.common.collect.TreeMultiset.Aggregate
            int nodeAggregate(AvlNode<?> avlNode) {
                return 1;
            }

            @Override // com.google.common.collect.TreeMultiset.Aggregate
            long treeAggregate(@Nullable AvlNode<?> avlNode) {
                if (avlNode != null) {
                    return ((AvlNode) avlNode).distinctElements;
                }
                return 0L;
            }
        };

        abstract int nodeAggregate(AvlNode<?> avlNode);

        abstract long treeAggregate(@Nullable AvlNode<?> avlNode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class AvlNode<E> extends Multisets.AbstractEntry<E> {
        private int distinctElements;
        @Nullable
        private final E elem;
        private int elemCount;
        private int height;
        private AvlNode<E> left;
        private AvlNode<E> pred;
        private AvlNode<E> right;
        private AvlNode<E> succ;
        private long totalCount;

        AvlNode(@Nullable E e, int i) {
            Preconditions.checkArgument(i > 0);
            this.elem = e;
            this.elemCount = i;
            this.totalCount = i;
            this.distinctElements = 1;
            this.height = 1;
            this.left = null;
            this.right = null;
        }

        private AvlNode<E> addLeftChild(E e, int i) {
            this.left = new AvlNode<>(e, i);
            TreeMultiset.successor(this.pred, this.left, this);
            this.height = Math.max(2, this.height);
            this.distinctElements++;
            this.totalCount += i;
            return this;
        }

        private AvlNode<E> addRightChild(E e, int i) {
            this.right = new AvlNode<>(e, i);
            TreeMultiset.successor(this, this.right, this.succ);
            this.height = Math.max(2, this.height);
            this.distinctElements++;
            this.totalCount += i;
            return this;
        }

        private int balanceFactor() {
            return height(this.left) - height(this.right);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Multi-variable type inference failed */
        @Nullable
        public AvlNode<E> ceiling(Comparator<? super E> comparator, E e) {
            int compare = comparator.compare(e, (E) this.elem);
            if (compare < 0) {
                return this.left != null ? (AvlNode) MoreObjects.firstNonNull(this.left.ceiling(comparator, e), this) : this;
            } else if (compare != 0) {
                if (this.right != null) {
                    return this.right.ceiling(comparator, e);
                }
                return null;
            } else {
                return this;
            }
        }

        private AvlNode<E> deleteMe() {
            int i = this.elemCount;
            this.elemCount = 0;
            TreeMultiset.successor(this.pred, this.succ);
            if (this.left != null) {
                if (this.right != null) {
                    if (this.left.height < this.right.height) {
                        AvlNode<E> avlNode = this.succ;
                        avlNode.right = this.right.removeMin(avlNode);
                        avlNode.left = this.left;
                        avlNode.distinctElements = this.distinctElements - 1;
                        avlNode.totalCount = this.totalCount - i;
                        return avlNode.rebalance();
                    }
                    AvlNode<E> avlNode2 = this.pred;
                    avlNode2.left = this.left.removeMax(avlNode2);
                    avlNode2.right = this.right;
                    avlNode2.distinctElements = this.distinctElements - 1;
                    avlNode2.totalCount = this.totalCount - i;
                    return avlNode2.rebalance();
                }
                return this.left;
            }
            return this.right;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Multi-variable type inference failed */
        @Nullable
        public AvlNode<E> floor(Comparator<? super E> comparator, E e) {
            int compare = comparator.compare(e, (E) this.elem);
            if (compare > 0) {
                return this.right != null ? (AvlNode) MoreObjects.firstNonNull(this.right.floor(comparator, e), this) : this;
            } else if (compare != 0) {
                if (this.left != null) {
                    return this.left.floor(comparator, e);
                }
                return null;
            } else {
                return this;
            }
        }

        private static int height(@Nullable AvlNode<?> avlNode) {
            if (avlNode != null) {
                return ((AvlNode) avlNode).height;
            }
            return 0;
        }

        private AvlNode<E> rebalance() {
            switch (balanceFactor()) {
                case -2:
                    if (this.right.balanceFactor() > 0) {
                        this.right = this.right.rotateRight();
                    }
                    return rotateLeft();
                case 2:
                    if (this.left.balanceFactor() < 0) {
                        this.left = this.left.rotateLeft();
                    }
                    return rotateRight();
                default:
                    recomputeHeight();
                    return this;
            }
        }

        private void recompute() {
            recomputeMultiset();
            recomputeHeight();
        }

        private void recomputeHeight() {
            this.height = Math.max(height(this.left), height(this.right)) + 1;
        }

        private void recomputeMultiset() {
            this.distinctElements = TreeMultiset.distinctElements(this.left) + 1 + TreeMultiset.distinctElements(this.right);
            this.totalCount = this.elemCount + totalCount(this.left) + totalCount(this.right);
        }

        private AvlNode<E> removeMax(AvlNode<E> avlNode) {
            if (this.right != null) {
                this.right = this.right.removeMax(avlNode);
                this.distinctElements--;
                this.totalCount -= avlNode.elemCount;
                return rebalance();
            }
            return this.left;
        }

        private AvlNode<E> removeMin(AvlNode<E> avlNode) {
            if (this.left != null) {
                this.left = this.left.removeMin(avlNode);
                this.distinctElements--;
                this.totalCount -= avlNode.elemCount;
                return rebalance();
            }
            return this.right;
        }

        private AvlNode<E> rotateLeft() {
            Preconditions.checkState(this.right != null);
            AvlNode<E> avlNode = this.right;
            this.right = avlNode.left;
            avlNode.left = this;
            avlNode.totalCount = this.totalCount;
            avlNode.distinctElements = this.distinctElements;
            recompute();
            avlNode.recomputeHeight();
            return avlNode;
        }

        private AvlNode<E> rotateRight() {
            Preconditions.checkState(this.left != null);
            AvlNode<E> avlNode = this.left;
            this.left = avlNode.right;
            avlNode.right = this;
            avlNode.totalCount = this.totalCount;
            avlNode.distinctElements = this.distinctElements;
            recompute();
            avlNode.recomputeHeight();
            return avlNode;
        }

        private static long totalCount(@Nullable AvlNode<?> avlNode) {
            if (avlNode != null) {
                return ((AvlNode) avlNode).totalCount;
            }
            return 0L;
        }

        /* JADX WARN: Multi-variable type inference failed */
        AvlNode<E> add(Comparator<? super E> comparator, @Nullable E e, int i, int[] iArr) {
            int compare = comparator.compare(e, (E) this.elem);
            if (compare < 0) {
                AvlNode<E> avlNode = this.left;
                if (avlNode == null) {
                    iArr[0] = 0;
                    return addLeftChild(e, i);
                }
                int i2 = avlNode.height;
                this.left = avlNode.add(comparator, e, i, iArr);
                if (iArr[0] == 0) {
                    this.distinctElements++;
                }
                this.totalCount += i;
                return this.left.height != i2 ? rebalance() : this;
            } else if (compare <= 0) {
                iArr[0] = this.elemCount;
                Preconditions.checkArgument(((((long) this.elemCount) + ((long) i)) > 2147483647L ? 1 : ((((long) this.elemCount) + ((long) i)) == 2147483647L ? 0 : -1)) > 0 ? false : true);
                this.elemCount += i;
                this.totalCount += i;
                return this;
            } else {
                AvlNode<E> avlNode2 = this.right;
                if (avlNode2 == null) {
                    iArr[0] = 0;
                    return addRightChild(e, i);
                }
                int i3 = avlNode2.height;
                this.right = avlNode2.add(comparator, e, i, iArr);
                if (iArr[0] == 0) {
                    this.distinctElements++;
                }
                this.totalCount += i;
                return this.right.height != i3 ? rebalance() : this;
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        public int count(Comparator<? super E> comparator, E e) {
            int compare = comparator.compare(e, (E) this.elem);
            if (compare < 0) {
                if (this.left != null) {
                    return this.left.count(comparator, e);
                }
                return 0;
            } else if (compare <= 0) {
                return this.elemCount;
            } else {
                if (this.right != null) {
                    return this.right.count(comparator, e);
                }
                return 0;
            }
        }

        @Override // com.google.common.collect.Multiset.Entry
        public int getCount() {
            return this.elemCount;
        }

        @Override // com.google.common.collect.Multiset.Entry
        public E getElement() {
            return this.elem;
        }

        /* JADX WARN: Multi-variable type inference failed */
        AvlNode<E> remove(Comparator<? super E> comparator, @Nullable E e, int i, int[] iArr) {
            int compare = comparator.compare(e, (E) this.elem);
            if (compare < 0) {
                AvlNode<E> avlNode = this.left;
                if (avlNode == null) {
                    iArr[0] = 0;
                    return this;
                }
                this.left = avlNode.remove(comparator, e, i, iArr);
                if (iArr[0] > 0) {
                    if (i < iArr[0]) {
                        this.totalCount -= i;
                    } else {
                        this.distinctElements--;
                        this.totalCount -= iArr[0];
                    }
                }
                return iArr[0] != 0 ? rebalance() : this;
            } else if (compare <= 0) {
                iArr[0] = this.elemCount;
                if (i < this.elemCount) {
                    this.elemCount -= i;
                    this.totalCount -= i;
                    return this;
                }
                return deleteMe();
            } else {
                AvlNode<E> avlNode2 = this.right;
                if (avlNode2 == null) {
                    iArr[0] = 0;
                    return this;
                }
                this.right = avlNode2.remove(comparator, e, i, iArr);
                if (iArr[0] > 0) {
                    if (i < iArr[0]) {
                        this.totalCount -= i;
                    } else {
                        this.distinctElements--;
                        this.totalCount -= iArr[0];
                    }
                }
                return rebalance();
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        AvlNode<E> setCount(Comparator<? super E> comparator, @Nullable E e, int i, int i2, int[] iArr) {
            int compare = comparator.compare(e, (E) this.elem);
            if (compare < 0) {
                AvlNode<E> avlNode = this.left;
                if (avlNode == null) {
                    iArr[0] = 0;
                    return (i == 0 && i2 > 0) ? addLeftChild(e, i2) : this;
                }
                this.left = avlNode.setCount(comparator, e, i, i2, iArr);
                if (iArr[0] == i) {
                    if (i2 == 0 && iArr[0] != 0) {
                        this.distinctElements--;
                    } else if (i2 > 0 && iArr[0] == 0) {
                        this.distinctElements++;
                    }
                    this.totalCount += i2 - iArr[0];
                }
                return rebalance();
            } else if (compare <= 0) {
                iArr[0] = this.elemCount;
                if (i == this.elemCount) {
                    if (i2 == 0) {
                        return deleteMe();
                    }
                    this.totalCount += i2 - this.elemCount;
                    this.elemCount = i2;
                }
                return this;
            } else {
                AvlNode<E> avlNode2 = this.right;
                if (avlNode2 == null) {
                    iArr[0] = 0;
                    return (i == 0 && i2 > 0) ? addRightChild(e, i2) : this;
                }
                this.right = avlNode2.setCount(comparator, e, i, i2, iArr);
                if (iArr[0] == i) {
                    if (i2 == 0 && iArr[0] != 0) {
                        this.distinctElements--;
                    } else if (i2 > 0 && iArr[0] == 0) {
                        this.distinctElements++;
                    }
                    this.totalCount += i2 - iArr[0];
                }
                return rebalance();
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        AvlNode<E> setCount(Comparator<? super E> comparator, @Nullable E e, int i, int[] iArr) {
            int compare = comparator.compare(e, (E) this.elem);
            if (compare < 0) {
                AvlNode<E> avlNode = this.left;
                if (avlNode == null) {
                    iArr[0] = 0;
                    return i <= 0 ? this : addLeftChild(e, i);
                }
                this.left = avlNode.setCount(comparator, e, i, iArr);
                if (i == 0 && iArr[0] != 0) {
                    this.distinctElements--;
                } else if (i > 0 && iArr[0] == 0) {
                    this.distinctElements++;
                }
                this.totalCount += i - iArr[0];
                return rebalance();
            } else if (compare <= 0) {
                iArr[0] = this.elemCount;
                if (i != 0) {
                    this.totalCount += i - this.elemCount;
                    this.elemCount = i;
                    return this;
                }
                return deleteMe();
            } else {
                AvlNode<E> avlNode2 = this.right;
                if (avlNode2 == null) {
                    iArr[0] = 0;
                    return i <= 0 ? this : addRightChild(e, i);
                }
                this.right = avlNode2.setCount(comparator, e, i, iArr);
                if (i == 0 && iArr[0] != 0) {
                    this.distinctElements--;
                } else if (i > 0 && iArr[0] == 0) {
                    this.distinctElements++;
                }
                this.totalCount += i - iArr[0];
                return rebalance();
            }
        }

        @Override // com.google.common.collect.Multisets.AbstractEntry, com.google.common.collect.Multiset.Entry
        public String toString() {
            return Multisets.immutableEntry(getElement(), getCount()).toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class Reference<T> {
        @Nullable
        private T value;

        private Reference() {
        }

        public void checkAndSet(@Nullable T t, T t2) {
            if (this.value != t) {
                throw new ConcurrentModificationException();
            }
            this.value = t2;
        }

        @Nullable
        public T get() {
            return this.value;
        }
    }

    TreeMultiset(Reference<AvlNode<E>> reference, GeneralRange<E> generalRange, AvlNode<E> avlNode) {
        super(generalRange.comparator());
        this.rootReference = reference;
        this.range = generalRange;
        this.header = avlNode;
    }

    TreeMultiset(Comparator<? super E> comparator) {
        super(comparator);
        this.range = GeneralRange.all(comparator);
        this.header = new AvlNode<>(null, 1);
        successor(this.header, this.header);
        this.rootReference = new Reference<>();
    }

    private long aggregateAboveRange(Aggregate aggregate, @Nullable AvlNode<E> avlNode) {
        if (avlNode != null) {
            int compare = comparator().compare(this.range.getUpperEndpoint(), ((AvlNode) avlNode).elem);
            if (compare <= 0) {
                if (compare != 0) {
                    return aggregate.treeAggregate(((AvlNode) avlNode).right) + aggregate.nodeAggregate(avlNode) + aggregateAboveRange(aggregate, ((AvlNode) avlNode).left);
                }
                switch (this.range.getUpperBoundType()) {
                    case OPEN:
                        return aggregate.nodeAggregate(avlNode) + aggregate.treeAggregate(((AvlNode) avlNode).right);
                    case CLOSED:
                        return aggregate.treeAggregate(((AvlNode) avlNode).right);
                    default:
                        throw new AssertionError();
                }
            }
            return aggregateAboveRange(aggregate, ((AvlNode) avlNode).right);
        }
        return 0L;
    }

    private long aggregateBelowRange(Aggregate aggregate, @Nullable AvlNode<E> avlNode) {
        if (avlNode != null) {
            int compare = comparator().compare(this.range.getLowerEndpoint(), ((AvlNode) avlNode).elem);
            if (compare >= 0) {
                if (compare != 0) {
                    return aggregate.treeAggregate(((AvlNode) avlNode).left) + aggregate.nodeAggregate(avlNode) + aggregateBelowRange(aggregate, ((AvlNode) avlNode).right);
                }
                switch (this.range.getLowerBoundType()) {
                    case OPEN:
                        return aggregate.nodeAggregate(avlNode) + aggregate.treeAggregate(((AvlNode) avlNode).left);
                    case CLOSED:
                        return aggregate.treeAggregate(((AvlNode) avlNode).left);
                    default:
                        throw new AssertionError();
                }
            }
            return aggregateBelowRange(aggregate, ((AvlNode) avlNode).left);
        }
        return 0L;
    }

    private long aggregateForEntries(Aggregate aggregate) {
        AvlNode<E> avlNode = this.rootReference.get();
        long treeAggregate = aggregate.treeAggregate(avlNode);
        if (this.range.hasLowerBound()) {
            treeAggregate -= aggregateBelowRange(aggregate, avlNode);
        }
        return !this.range.hasUpperBound() ? treeAggregate : treeAggregate - aggregateAboveRange(aggregate, avlNode);
    }

    public static <E extends Comparable> TreeMultiset<E> create() {
        return new TreeMultiset<>(Ordering.natural());
    }

    public static <E extends Comparable> TreeMultiset<E> create(Iterable<? extends E> iterable) {
        TreeMultiset<E> create = create();
        Iterables.addAll(create, iterable);
        return create;
    }

    public static <E> TreeMultiset<E> create(@Nullable Comparator<? super E> comparator) {
        return comparator != null ? new TreeMultiset<>(comparator) : new TreeMultiset<>(Ordering.natural());
    }

    static int distinctElements(@Nullable AvlNode<?> avlNode) {
        if (avlNode != null) {
            return ((AvlNode) avlNode).distinctElements;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Nullable
    public AvlNode<E> firstNode() {
        AvlNode<E> ceiling;
        if (this.rootReference.get() != null) {
            if (this.range.hasLowerBound()) {
                E lowerEndpoint = this.range.getLowerEndpoint();
                ceiling = this.rootReference.get().ceiling(comparator(), lowerEndpoint);
                if (ceiling == null) {
                    return null;
                }
                if (this.range.getLowerBoundType() == BoundType.OPEN && comparator().compare(lowerEndpoint, ceiling.getElement()) == 0) {
                    ceiling = ((AvlNode) ceiling).succ;
                }
            } else {
                ceiling = ((AvlNode) this.header).succ;
            }
            if (ceiling != this.header && this.range.contains(ceiling.getElement())) {
                return ceiling;
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Nullable
    public AvlNode<E> lastNode() {
        AvlNode<E> floor;
        if (this.rootReference.get() != null) {
            if (this.range.hasUpperBound()) {
                E upperEndpoint = this.range.getUpperEndpoint();
                floor = this.rootReference.get().floor(comparator(), upperEndpoint);
                if (floor == null) {
                    return null;
                }
                if (this.range.getUpperBoundType() == BoundType.OPEN && comparator().compare(upperEndpoint, floor.getElement()) == 0) {
                    floor = ((AvlNode) floor).pred;
                }
            } else {
                floor = ((AvlNode) this.header).pred;
            }
            if (floor != this.header && this.range.contains(floor.getElement())) {
                return floor;
            }
            return null;
        }
        return null;
    }

    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        Comparator comparator = (Comparator) objectInputStream.readObject();
        Serialization.getFieldSetter(AbstractSortedMultiset.class, "comparator").set((Serialization.FieldSetter) this, (Object) comparator);
        Serialization.getFieldSetter(TreeMultiset.class, "range").set((Serialization.FieldSetter) this, (Object) GeneralRange.all(comparator));
        Serialization.getFieldSetter(TreeMultiset.class, "rootReference").set((Serialization.FieldSetter) this, (Object) new Reference());
        AvlNode avlNode = new AvlNode(null, 1);
        Serialization.getFieldSetter(TreeMultiset.class, "header").set((Serialization.FieldSetter) this, (Object) avlNode);
        successor(avlNode, avlNode);
        Serialization.populateMultiset(this, objectInputStream);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static <T> void successor(AvlNode<T> avlNode, AvlNode<T> avlNode2) {
        ((AvlNode) avlNode).succ = avlNode2;
        ((AvlNode) avlNode2).pred = avlNode;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static <T> void successor(AvlNode<T> avlNode, AvlNode<T> avlNode2, AvlNode<T> avlNode3) {
        successor(avlNode, avlNode2);
        successor(avlNode2, avlNode3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Multiset.Entry<E> wrapEntry(final AvlNode<E> avlNode) {
        return new Multisets.AbstractEntry<E>() { // from class: com.google.common.collect.TreeMultiset.1
            @Override // com.google.common.collect.Multiset.Entry
            public int getCount() {
                int count = avlNode.getCount();
                return count != 0 ? count : TreeMultiset.this.count(getElement());
            }

            @Override // com.google.common.collect.Multiset.Entry
            public E getElement() {
                return (E) avlNode.getElement();
            }
        };
    }

    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(elementSet().comparator());
        Serialization.writeMultiset(this, objectOutputStream);
    }

    @Override // com.google.common.collect.AbstractMultiset, com.google.common.collect.Multiset
    public int add(@Nullable E e, int i) {
        CollectPreconditions.checkNonnegative(i, "occurrences");
        if (i != 0) {
            Preconditions.checkArgument(this.range.contains(e));
            AvlNode<E> avlNode = this.rootReference.get();
            if (avlNode != null) {
                int[] iArr = new int[1];
                this.rootReference.checkAndSet(avlNode, avlNode.add(comparator(), e, i, iArr));
                return iArr[0];
            }
            comparator().compare(e, e);
            AvlNode<E> avlNode2 = new AvlNode<>(e, i);
            successor(this.header, avlNode2, this.header);
            this.rootReference.checkAndSet(avlNode, avlNode2);
            return 0;
        }
        return count(e);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.common.collect.AbstractMultiset, java.util.AbstractCollection, java.util.Collection, com.google.common.collect.Multiset
    public /* bridge */ /* synthetic */ boolean add(Object obj) {
        return super.add(obj);
    }

    @Override // com.google.common.collect.AbstractMultiset, java.util.AbstractCollection, java.util.Collection
    public /* bridge */ /* synthetic */ boolean addAll(Collection collection) {
        return super.addAll(collection);
    }

    @Override // com.google.common.collect.AbstractMultiset, java.util.AbstractCollection, java.util.Collection
    public /* bridge */ /* synthetic */ void clear() {
        super.clear();
    }

    @Override // com.google.common.collect.AbstractSortedMultiset, com.google.common.collect.SortedMultiset, com.google.common.collect.SortedIterable
    public /* bridge */ /* synthetic */ Comparator comparator() {
        return super.comparator();
    }

    @Override // com.google.common.collect.AbstractMultiset, java.util.AbstractCollection, java.util.Collection, com.google.common.collect.Multiset
    public /* bridge */ /* synthetic */ boolean contains(Object obj) {
        return super.contains(obj);
    }

    @Override // com.google.common.collect.AbstractMultiset, com.google.common.collect.Multiset
    public int count(@Nullable Object obj) {
        try {
            AvlNode<E> avlNode = this.rootReference.get();
            if (this.range.contains(obj) && avlNode != null) {
                return avlNode.count(comparator(), obj);
            }
            return 0;
        } catch (ClassCastException e) {
            return 0;
        } catch (NullPointerException e2) {
            return 0;
        }
    }

    @Override // com.google.common.collect.AbstractSortedMultiset
    Iterator<Multiset.Entry<E>> descendingEntryIterator() {
        return new Iterator<Multiset.Entry<E>>() { // from class: com.google.common.collect.TreeMultiset.3
            AvlNode<E> current;
            Multiset.Entry<E> prevEntry = null;

            {
                this.current = TreeMultiset.this.lastNode();
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                if (this.current != null) {
                    if (TreeMultiset.this.range.tooLow(this.current.getElement())) {
                        this.current = null;
                        return false;
                    }
                    return true;
                }
                return false;
            }

            @Override // java.util.Iterator
            public Multiset.Entry<E> next() {
                if (hasNext()) {
                    Multiset.Entry<E> wrapEntry = TreeMultiset.this.wrapEntry(this.current);
                    this.prevEntry = wrapEntry;
                    if (((AvlNode) this.current).pred != TreeMultiset.this.header) {
                        this.current = ((AvlNode) this.current).pred;
                    } else {
                        this.current = null;
                    }
                    return wrapEntry;
                }
                throw new NoSuchElementException();
            }

            @Override // java.util.Iterator
            public void remove() {
                CollectPreconditions.checkRemove(this.prevEntry != null);
                TreeMultiset.this.setCount(this.prevEntry.getElement(), 0);
                this.prevEntry = null;
            }
        };
    }

    @Override // com.google.common.collect.AbstractSortedMultiset, com.google.common.collect.SortedMultiset
    public /* bridge */ /* synthetic */ SortedMultiset descendingMultiset() {
        return super.descendingMultiset();
    }

    @Override // com.google.common.collect.AbstractMultiset
    int distinctElements() {
        return Ints.saturatedCast(aggregateForEntries(Aggregate.DISTINCT));
    }

    @Override // com.google.common.collect.AbstractSortedMultiset, com.google.common.collect.AbstractMultiset, com.google.common.collect.Multiset
    public /* bridge */ /* synthetic */ NavigableSet elementSet() {
        return super.elementSet();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.common.collect.AbstractMultiset
    public Iterator<Multiset.Entry<E>> entryIterator() {
        return new Iterator<Multiset.Entry<E>>() { // from class: com.google.common.collect.TreeMultiset.2
            AvlNode<E> current;
            Multiset.Entry<E> prevEntry;

            {
                this.current = TreeMultiset.this.firstNode();
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                if (this.current != null) {
                    if (TreeMultiset.this.range.tooHigh(this.current.getElement())) {
                        this.current = null;
                        return false;
                    }
                    return true;
                }
                return false;
            }

            @Override // java.util.Iterator
            public Multiset.Entry<E> next() {
                if (hasNext()) {
                    Multiset.Entry<E> wrapEntry = TreeMultiset.this.wrapEntry(this.current);
                    this.prevEntry = wrapEntry;
                    if (((AvlNode) this.current).succ != TreeMultiset.this.header) {
                        this.current = ((AvlNode) this.current).succ;
                    } else {
                        this.current = null;
                    }
                    return wrapEntry;
                }
                throw new NoSuchElementException();
            }

            @Override // java.util.Iterator
            public void remove() {
                CollectPreconditions.checkRemove(this.prevEntry != null);
                TreeMultiset.this.setCount(this.prevEntry.getElement(), 0);
                this.prevEntry = null;
            }
        };
    }

    @Override // com.google.common.collect.AbstractMultiset, com.google.common.collect.Multiset
    public /* bridge */ /* synthetic */ Set entrySet() {
        return super.entrySet();
    }

    @Override // com.google.common.collect.AbstractMultiset, java.util.Collection, com.google.common.collect.Multiset
    public /* bridge */ /* synthetic */ boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override // com.google.common.collect.AbstractSortedMultiset, com.google.common.collect.SortedMultiset
    public /* bridge */ /* synthetic */ Multiset.Entry firstEntry() {
        return super.firstEntry();
    }

    @Override // com.google.common.collect.AbstractMultiset, java.util.Collection, com.google.common.collect.Multiset
    public /* bridge */ /* synthetic */ int hashCode() {
        return super.hashCode();
    }

    @Override // com.google.common.collect.SortedMultiset
    public SortedMultiset<E> headMultiset(@Nullable E e, BoundType boundType) {
        return new TreeMultiset(this.rootReference, this.range.intersect(GeneralRange.upTo(comparator(), e, boundType)), this.header);
    }

    @Override // com.google.common.collect.AbstractMultiset, java.util.AbstractCollection, java.util.Collection
    public /* bridge */ /* synthetic */ boolean isEmpty() {
        return super.isEmpty();
    }

    @Override // com.google.common.collect.AbstractMultiset, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, com.google.common.collect.Multiset
    public /* bridge */ /* synthetic */ Iterator iterator() {
        return super.iterator();
    }

    @Override // com.google.common.collect.AbstractSortedMultiset, com.google.common.collect.SortedMultiset
    public /* bridge */ /* synthetic */ Multiset.Entry lastEntry() {
        return super.lastEntry();
    }

    @Override // com.google.common.collect.AbstractSortedMultiset, com.google.common.collect.SortedMultiset
    public /* bridge */ /* synthetic */ Multiset.Entry pollFirstEntry() {
        return super.pollFirstEntry();
    }

    @Override // com.google.common.collect.AbstractSortedMultiset, com.google.common.collect.SortedMultiset
    public /* bridge */ /* synthetic */ Multiset.Entry pollLastEntry() {
        return super.pollLastEntry();
    }

    @Override // com.google.common.collect.AbstractMultiset, com.google.common.collect.Multiset
    public int remove(@Nullable Object obj, int i) {
        CollectPreconditions.checkNonnegative(i, "occurrences");
        if (i != 0) {
            AvlNode<E> avlNode = this.rootReference.get();
            int[] iArr = new int[1];
            try {
                if (this.range.contains(obj) && avlNode != null) {
                    this.rootReference.checkAndSet(avlNode, avlNode.remove(comparator(), obj, i, iArr));
                    return iArr[0];
                }
                return 0;
            } catch (ClassCastException e) {
                return 0;
            } catch (NullPointerException e2) {
                return 0;
            }
        }
        return count(obj);
    }

    @Override // com.google.common.collect.AbstractMultiset, java.util.AbstractCollection, java.util.Collection, com.google.common.collect.Multiset
    public /* bridge */ /* synthetic */ boolean remove(Object obj) {
        return super.remove(obj);
    }

    @Override // com.google.common.collect.AbstractMultiset, java.util.AbstractCollection, java.util.Collection, com.google.common.collect.Multiset
    public /* bridge */ /* synthetic */ boolean removeAll(Collection collection) {
        return super.removeAll(collection);
    }

    @Override // com.google.common.collect.AbstractMultiset, java.util.AbstractCollection, java.util.Collection, com.google.common.collect.Multiset
    public /* bridge */ /* synthetic */ boolean retainAll(Collection collection) {
        return super.retainAll(collection);
    }

    @Override // com.google.common.collect.AbstractMultiset, com.google.common.collect.Multiset
    public int setCount(@Nullable E e, int i) {
        CollectPreconditions.checkNonnegative(i, "count");
        if (!this.range.contains(e)) {
            Preconditions.checkArgument(i == 0);
            return 0;
        }
        AvlNode<E> avlNode = this.rootReference.get();
        if (avlNode != null) {
            int[] iArr = new int[1];
            this.rootReference.checkAndSet(avlNode, avlNode.setCount(comparator(), e, i, iArr));
            return iArr[0];
        }
        if (i > 0) {
            add(e, i);
        }
        return 0;
    }

    @Override // com.google.common.collect.AbstractMultiset, com.google.common.collect.Multiset
    public boolean setCount(@Nullable E e, int i, int i2) {
        CollectPreconditions.checkNonnegative(i2, "newCount");
        CollectPreconditions.checkNonnegative(i, "oldCount");
        Preconditions.checkArgument(this.range.contains(e));
        AvlNode<E> avlNode = this.rootReference.get();
        if (avlNode != null) {
            int[] iArr = new int[1];
            this.rootReference.checkAndSet(avlNode, avlNode.setCount(comparator(), e, i, i2, iArr));
            return iArr[0] == i;
        } else if (i != 0) {
            return false;
        } else {
            if (i2 > 0) {
                add(e, i2);
            }
            return true;
        }
    }

    @Override // com.google.common.collect.AbstractMultiset, java.util.AbstractCollection, java.util.Collection
    public int size() {
        return Ints.saturatedCast(aggregateForEntries(Aggregate.SIZE));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.common.collect.AbstractSortedMultiset, com.google.common.collect.SortedMultiset
    public /* bridge */ /* synthetic */ SortedMultiset subMultiset(Object obj, BoundType boundType, Object obj2, BoundType boundType2) {
        return super.subMultiset(obj, boundType, obj2, boundType2);
    }

    @Override // com.google.common.collect.SortedMultiset
    public SortedMultiset<E> tailMultiset(@Nullable E e, BoundType boundType) {
        return new TreeMultiset(this.rootReference, this.range.intersect(GeneralRange.downTo(comparator(), e, boundType)), this.header);
    }

    @Override // com.google.common.collect.AbstractMultiset, java.util.AbstractCollection, com.google.common.collect.Multiset
    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }
}
