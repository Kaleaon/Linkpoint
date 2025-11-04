// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Collection;
import java.util.Collections;
import com.google.common.base.Preconditions;
import java.util.Queue;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
class ConsumingQueueIterator<T> extends AbstractIterator<T>
{
    private final Queue<T> queue;
    
    ConsumingQueueIterator(final Queue<T> queue) {
        this.queue = Preconditions.checkNotNull(queue);
    }
    
    ConsumingQueueIterator(final T... elements) {
        Collections.addAll(this.queue = (Queue<T>)Lists.newLinkedList(), elements);
    }
    
    public T computeNext() {
        T t;
        if (!this.queue.isEmpty()) {
            t = this.queue.remove();
        }
        else {
            t = this.endOfData();
        }
        return t;
    }
}
