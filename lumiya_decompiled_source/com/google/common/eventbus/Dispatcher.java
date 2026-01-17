package com.google.common.eventbus;

import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class Dispatcher {

    /* loaded from: classes.dex */
    private static final class ImmediateDispatcher extends Dispatcher {
        private static final ImmediateDispatcher INSTANCE = new ImmediateDispatcher();

        private ImmediateDispatcher() {
        }

        @Override // com.google.common.eventbus.Dispatcher
        void dispatch(Object obj, Iterator<Subscriber> it) {
            Preconditions.checkNotNull(obj);
            while (it.hasNext()) {
                it.next().dispatchEvent(obj);
            }
        }
    }

    /* loaded from: classes.dex */
    private static final class LegacyAsyncDispatcher extends Dispatcher {
        private final ConcurrentLinkedQueue<EventWithSubscriber> queue;

        /* loaded from: classes.dex */
        private static final class EventWithSubscriber {
            private final Object event;
            private final Subscriber subscriber;

            private EventWithSubscriber(Object obj, Subscriber subscriber) {
                this.event = obj;
                this.subscriber = subscriber;
            }
        }

        private LegacyAsyncDispatcher() {
            this.queue = Queues.newConcurrentLinkedQueue();
        }

        @Override // com.google.common.eventbus.Dispatcher
        void dispatch(Object obj, Iterator<Subscriber> it) {
            Preconditions.checkNotNull(obj);
            while (it.hasNext()) {
                this.queue.add(new EventWithSubscriber(obj, it.next()));
            }
            while (true) {
                EventWithSubscriber poll = this.queue.poll();
                if (poll == null) {
                    return;
                }
                poll.subscriber.dispatchEvent(poll.event);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class PerThreadQueuedDispatcher extends Dispatcher {
        private final ThreadLocal<Boolean> dispatching;
        private final ThreadLocal<Queue<Event>> queue;

        /* loaded from: classes.dex */
        private static final class Event {
            private final Object event;
            private final Iterator<Subscriber> subscribers;

            private Event(Object obj, Iterator<Subscriber> it) {
                this.event = obj;
                this.subscribers = it;
            }
        }

        private PerThreadQueuedDispatcher() {
            this.queue = new ThreadLocal<Queue<Event>>() { // from class: com.google.common.eventbus.Dispatcher.PerThreadQueuedDispatcher.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // java.lang.ThreadLocal
                public Queue<Event> initialValue() {
                    return Queues.newArrayDeque();
                }
            };
            this.dispatching = new ThreadLocal<Boolean>() { // from class: com.google.common.eventbus.Dispatcher.PerThreadQueuedDispatcher.2
                /* JADX INFO: Access modifiers changed from: protected */
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.lang.ThreadLocal
                public Boolean initialValue() {
                    return false;
                }
            };
        }

        /* JADX WARN: Removed duplicated region for block: B:10:0x0054 A[Catch: all -> 0x005f, LOOP:1: B:10:0x0054->B:9:0x0043, LOOP_START, TRY_LEAVE, TryCatch #0 {all -> 0x005f, blocks: (B:6:0x0030, B:10:0x0054, B:9:0x0043), top: B:16:0x0030 }] */
        /* JADX WARN: Removed duplicated region for block: B:18:0x0038 A[SYNTHETIC] */
        @Override // com.google.common.eventbus.Dispatcher
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        void dispatch(java.lang.Object r5, java.util.Iterator<com.google.common.eventbus.Subscriber> r6) {
            /*
                r4 = this;
                r2 = 0
                com.google.common.base.Preconditions.checkNotNull(r5)
                com.google.common.base.Preconditions.checkNotNull(r6)
                java.lang.ThreadLocal<java.util.Queue<com.google.common.eventbus.Dispatcher$PerThreadQueuedDispatcher$Event>> r0 = r4.queue
                java.lang.Object r0 = r0.get()
                java.util.Queue r0 = (java.util.Queue) r0
                com.google.common.eventbus.Dispatcher$PerThreadQueuedDispatcher$Event r1 = new com.google.common.eventbus.Dispatcher$PerThreadQueuedDispatcher$Event
                r1.<init>(r5, r6)
                r0.offer(r1)
                java.lang.ThreadLocal<java.lang.Boolean> r1 = r4.dispatching
                java.lang.Object r1 = r1.get()
                java.lang.Boolean r1 = (java.lang.Boolean) r1
                boolean r1 = r1.booleanValue()
                if (r1 == 0) goto L26
            L25:
                return
            L26:
                java.lang.ThreadLocal<java.lang.Boolean> r1 = r4.dispatching
                r2 = 1
                java.lang.Boolean r2 = java.lang.Boolean.valueOf(r2)
                r1.set(r2)
            L30:
                java.lang.Object r1 = r0.poll()     // Catch: java.lang.Throwable -> L5f
                com.google.common.eventbus.Dispatcher$PerThreadQueuedDispatcher$Event r1 = (com.google.common.eventbus.Dispatcher.PerThreadQueuedDispatcher.Event) r1     // Catch: java.lang.Throwable -> L5f
                if (r1 != 0) goto L54
                java.lang.ThreadLocal<java.lang.Boolean> r0 = r4.dispatching
                r0.remove()
                java.lang.ThreadLocal<java.util.Queue<com.google.common.eventbus.Dispatcher$PerThreadQueuedDispatcher$Event>> r0 = r4.queue
                r0.remove()
                goto L25
            L43:
                java.util.Iterator r2 = com.google.common.eventbus.Dispatcher.PerThreadQueuedDispatcher.Event.access$400(r1)     // Catch: java.lang.Throwable -> L5f
                java.lang.Object r2 = r2.next()     // Catch: java.lang.Throwable -> L5f
                com.google.common.eventbus.Subscriber r2 = (com.google.common.eventbus.Subscriber) r2     // Catch: java.lang.Throwable -> L5f
                java.lang.Object r3 = com.google.common.eventbus.Dispatcher.PerThreadQueuedDispatcher.Event.access$500(r1)     // Catch: java.lang.Throwable -> L5f
                r2.dispatchEvent(r3)     // Catch: java.lang.Throwable -> L5f
            L54:
                java.util.Iterator r2 = com.google.common.eventbus.Dispatcher.PerThreadQueuedDispatcher.Event.access$400(r1)     // Catch: java.lang.Throwable -> L5f
                boolean r2 = r2.hasNext()     // Catch: java.lang.Throwable -> L5f
                if (r2 != 0) goto L43
                goto L30
            L5f:
                r0 = move-exception
                java.lang.ThreadLocal<java.lang.Boolean> r1 = r4.dispatching
                r1.remove()
                java.lang.ThreadLocal<java.util.Queue<com.google.common.eventbus.Dispatcher$PerThreadQueuedDispatcher$Event>> r1 = r4.queue
                r1.remove()
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.common.eventbus.Dispatcher.PerThreadQueuedDispatcher.dispatch(java.lang.Object, java.util.Iterator):void");
        }
    }

    Dispatcher() {
    }

    static Dispatcher immediate() {
        return ImmediateDispatcher.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Dispatcher legacyAsync() {
        return new LegacyAsyncDispatcher();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Dispatcher perThreadDispatchQueue() {
        return new PerThreadQueuedDispatcher();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void dispatch(Object obj, Iterator<Subscriber> it);
}
