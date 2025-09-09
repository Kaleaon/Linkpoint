package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Queue;

@GwtCompatible
class ConsumingQueueIterator<T> extends AbstractIterator<T> {
    private final Queue<T> queue;

    ConsumingQueueIterator(Queue<T> queue2) {
        this.queue = (Queue) Preconditions.checkNotNull(queue2);
    }

    ConsumingQueueIterator(T... tArr) {
        this.queue = Lists.newLinkedList();
        Collections.addAll(this.queue, tArr);
    }

    public T computeNext() {
        return !this.queue.isEmpty() ? this.queue.remove() : endOfData();
    }
}
