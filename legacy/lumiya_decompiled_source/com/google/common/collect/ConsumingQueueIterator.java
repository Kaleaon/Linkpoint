package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Queue;
/* JADX INFO: Access modifiers changed from: package-private */
@GwtCompatible
/* loaded from: classes.dex */
public class ConsumingQueueIterator<T> extends AbstractIterator<T> {
    private final Queue<T> queue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConsumingQueueIterator(Queue<T> queue) {
        this.queue = (Queue) Preconditions.checkNotNull(queue);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConsumingQueueIterator(T... tArr) {
        this.queue = Lists.newLinkedList();
        Collections.addAll(this.queue, tArr);
    }

    @Override // com.google.common.collect.AbstractIterator
    public T computeNext() {
        return !this.queue.isEmpty() ? this.queue.remove() : endOfData();
    }
}
