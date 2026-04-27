package com.linkpoint.utils.wlist

import com.google.common.collect.Lists
import com.linkpoint.Debug
import com.linkpoint.utils.Identifiable
import com.linkpoint.utils.wlist.ChunkedList
import java.util.AbstractList
import java.util.ArrayList
import java.util.Comparator
import java.util.HashMap
import java.util.Iterator
import java.util.List
import java.util.Map
import java.util.Queue
import java.util.RandomAccess
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean
import javax.annotation.Nonnull
import javax.annotation.Nullable

class ChunkedListLoader<E : Identifiable<Long>>, AbstractList<E> : ChunkedList.ChunkFactory<E>, RandomAccess {
    /* access modifiers changed from: private */
    val Queue<E> addedElements = ConcurrentLinkedQueue()
    /* access modifiers changed from: private */
    val Comparator<E> chatMessageComparator = $Lambda$QDlX9uefQr1Wq8gtt1O6M2wUNME()
    private val Executor executor
    /* access modifiers changed from: private */
    public Boolean hasAbove
    /* access modifiers changed from: private */
    public Boolean hasBelow
    /* access modifiers changed from: private */
    public ChunkedList<E> items
    /* access modifiers changed from: private */
    val EventListener listener
    private val Executor listenerExecutor
    /* access modifiers changed from: private */
    public LoadResult<E> loadAboveResult = null
    /* access modifiers changed from: private */
    public Long loadAboveTopmostId
    /* access modifiers changed from: private */
    public Boolean loadAboveWanted = false
    /* access modifiers changed from: private */
    public Long loadBelowLastId
    /* access modifiers changed from: private */
    public LoadResult<E> loadBelowResult = null
    /* access modifiers changed from: private */
    public Boolean loadBelowWanted = false
    private val Runnable loadMoreData = Runnable() {
        override fun run() {
            Long r4
            Long r6
            val z4: Boolean = true
            Debug.Printf("ChatView: processing loadMoreData(), reloadRequested %b", Boolean.valueOf(ChunkedListLoader.this.reloadRequested.get()))
            ChunkedListLoader.this.loadRequested.set(false)
            synchronized (ChunkedListLoader.this.lock) {
                z = (!ChunkedListLoader.this.loadAboveWanted || ChunkedListLoader.this.loadAboveResult != null) ? false : !ChunkedListLoader.this.reloadRequested.get()
                r4 = ChunkedListLoader.this.loadAboveTopmostId
            }
            if (z) {
                val loadInBackground: LoadResult = ChunkedListLoader.this.loadInBackground(ChunkedListLoader.this.windowSize, r4, false)
                synchronized (ChunkedListLoader.this.lock) {
                    val unused: LoadResult = ChunkedListLoader.this.loadAboveResult = loadInBackground
                }
                z2 = true
            } else {
                z2 = false
            }
            synchronized (ChunkedListLoader.this.lock) {
                z3 = (!ChunkedListLoader.this.loadBelowWanted || ChunkedListLoader.this.loadBelowResult != null) ? false : !ChunkedListLoader.this.reloadRequested.get()
                r6 = ChunkedListLoader.this.loadBelowLastId
            }
            if (z3) {
                val loadInBackground2: LoadResult = ChunkedListLoader.this.loadInBackground(ChunkedListLoader.this.windowSize, r6, true)
                synchronized (ChunkedListLoader.this.lock) {
                    val unused2: LoadResult = ChunkedListLoader.this.loadBelowResult = loadInBackground2
                }
                z2 = true
            }
            if (ChunkedListLoader.this.reloadRequested.getAndSet(false)) {
                ChunkedListLoader.this.reloadAccepted.set(true)
            } else {
                z4 = z2
            }
            if (z4) {
                ChunkedListLoader.this.postUpdate()
            }
        }
    }
    /* access modifiers changed from: private */
    val AtomicBoolean loadRequested = AtomicBoolean()
    /* access modifiers changed from: private */
    val Object lock = Object()
    private val Runnable processUpdate = Runnable() {
        override fun run() {
            LoadResult r3
            LoadResult r32
            Identifiable identifiable
            ChunkedListLoader.this.updatePosted.set(false)
            Debug.Printf("ChatView: processUpdate, reloadAccepted: %b", Boolean.valueOf(ChunkedListLoader.this.reloadAccepted.get()))
            if (ChunkedListLoader.this.reloadAccepted.getAndSet(false)) {
                synchronized (ChunkedListLoader.this.lock) {
                    val unused: Boolean = ChunkedListLoader.this.loadAboveWanted = false
                    val unused2: LoadResult = ChunkedListLoader.this.loadAboveResult = null
                    val unused3: LoadResult = ChunkedListLoader.this.loadBelowResult = null
                    val unused4: Boolean = ChunkedListLoader.this.loadBelowWanted = false
                }
                val unused5: Boolean = ChunkedListLoader.this.hasAbove = true
                val unused6: Boolean = ChunkedListLoader.this.hasBelow = true
                ChunkedListLoader.this.addedElements.clear()
                ChunkedListLoader.this.items.clear()
                ChunkedListLoader.this.listener.onListReloaded()
                return
            }
            synchronized (ChunkedListLoader.this.lock) {
                r3 = ChunkedListLoader.this.loadAboveResult
                if (r3 != null) {
                    val unused7: LoadResult = ChunkedListLoader.this.loadAboveResult = null
                    val unused8: Boolean = ChunkedListLoader.this.loadAboveWanted = false
                }
            }
            if (r3 != null) {
                i = r3.entries.size() + 0
                ChunkedListLoader.this.items.addChunkAtStart(Lists.reverse(r3.entries))
                val unused9: Boolean = ChunkedListLoader.this.hasAbove = r3.hasMore
                if (r3.fromId == Long.MAX_VALUE) {
                    val unused10: Boolean = ChunkedListLoader.this.hasBelow = false
                }
            } else {
                i = 0
            }
            if (i != 0) {
                ChunkedListLoader.this.listener.onListItemsAdded(0, i)
            }
            val size: Int = ChunkedListLoader.this.items.size()
            synchronized (ChunkedListLoader.this.lock) {
                r32 = ChunkedListLoader.this.loadBelowResult
                if (r32 != null) {
                    val unused11: LoadResult = ChunkedListLoader.this.loadBelowResult = null
                    val unused12: Boolean = ChunkedListLoader.this.loadBelowWanted = false
                }
            }
            if (r32 != null) {
                i2 = r32.entries.size()
                ChunkedListLoader.this.items.addChunkAtEnd(r32.entries)
                val unused13: Boolean = ChunkedListLoader.this.hasBelow = r32.hasMore
                if (r32.fromId == 0) {
                    val unused14: Boolean = ChunkedListLoader.this.hasAbove = false
                }
            } else {
                i2 = 0
            }
            val z2: Boolean = false
            val i3: Int = i2
            while (true) {
                val identifiable2: Identifiable = (Identifiable) ChunkedListLoader.this.addedElements.poll()
                if (identifiable2 == null) {
                    break
                }
                val longValue: Long = ChunkedListLoader.this.items.size() > 0 ? ((Long) ((Identifiable) ChunkedListLoader.this.items.get(ChunkedListLoader.this.items.size() - 1)).getId()).longValue() : -1
                Debug.Printf("ChatView: added element: id %d, lastId %d, hasBelow %b", identifiable2.getId(), Long.valueOf(longValue), Boolean.valueOf(ChunkedListLoader.this.hasBelow))
                if (ChunkedListLoader.this.hasBelow || ((Long) identifiable2.getId()).longValue() <= longValue) {
                    z = z2
                } else {
                    ChunkedListLoader.this.items.addElement(identifiable2, ChunkedListLoader.this.windowSize, ChunkedListLoader.this)
                    i3++
                    z = true
                }
                z2 = z
                i3 = i3
            }
            if (i3 != 0) {
                ChunkedListLoader.this.listener.onListItemsAdded(size, i3)
            }
            if (z2) {
                ChunkedListLoader.this.listener.onListItemAddedAtEnd()
            }
            while (true) {
                synchronized (ChunkedListLoader.this.lock) {
                    val it: Iterator = ChunkedListLoader.this.updatedElements.entrySet().iterator()
                    if (it.hasNext()) {
                        identifiable = (Identifiable) ((Map.Entry) it.next()).getValue()
                        it.remove()
                    } else {
                        identifiable = null
                    }
                }
                if (identifiable != null) {
                    val replaceElement: Int = ChunkedListLoader.this.items.replaceElement(identifiable, ChunkedListLoader.this.chatMessageComparator)
                    Debug.Printf("ChunkedListLoader: replace: replacedIndex is %d", Integer.valueOf(replaceElement))
                    if (replaceElement >= 0) {
                        ChunkedListLoader.this.listener.onListItemChanged(replaceElement)
                    }
                } else {
                    return
                }
            }
        }
    }
    /* access modifiers changed from: private */
    val AtomicBoolean reloadAccepted = AtomicBoolean()
    /* access modifiers changed from: private */
    val AtomicBoolean reloadRequested = AtomicBoolean()
    private val Boolean startFromStart
    /* access modifiers changed from: private */
    val AtomicBoolean updatePosted = AtomicBoolean()
    /* access modifiers changed from: private */
    val Map<Long, E> updatedElements = HashMap()
    /* access modifiers changed from: private */
    val Int windowSize

    interface EventListener {
         fun getListEventsExecutor(): Executor)

         fun onListItemAddedAtEnd()

         fun onListItemChanged(i: Int)

         fun onListItemsAdded(i: Int, i2: Int)

         fun onListItemsRemoved(i: Int, i2: Int)

         fun onListReloaded()
    }

    @JvmStatic
protected class LoadResult<E> {
        val List<E> entries
        final Long fromId
        final Boolean hasMore

        public LoadResult(List<E> list, Boolean z, Long j) {
            this.entries = list
            this.hasMore = z
            this.fromId = j
        }
    }

    public ChunkedListLoader(Int i, Executor executor2, Boolean z, EventListener eventListener) {
        this.windowSize = i
        this.executor = executor2
        this.startFromStart = z
        this.listener = eventListener
        this.listenerExecutor = eventListener.getListEventsExecutor()
        this.items = ChunkedList<>()
        this.hasAbove = true
        this.hasBelow = true
    }

    /* access modifiers changed from: private */
    fun postUpdate() {
        if (this.updatePosted.compareAndSet(false, true)) {
            Debug.Printf("ChatView: requesting processUpdate ()", Object[0])
            this.listenerExecutor.execute(this.processUpdate)
            return
        }
        Debug.Printf("ChatView: processUpdate () already requested", Object[0])
    }

    fun addElement(E e) {
        Debug.Printf("ChatView: addElement: adding element with id %d", e.getId())
        this.addedElements.add(e)
        postUpdate()
    }

    public List<E> createEmptyChunk() {
        return ArrayList(this.windowSize)
    }

    public E get(Int i) {
        return (Identifiable) this.items.get(i)
    }

     public fun hasMoreItemsAtBottom(): Boolean {
        return this.hasBelow
    }

    /* access modifiers changed from: protected */
    public LoadResult<E> loadInBackground(Int i, Long j, Boolean z) {
        return LoadResult<>(ArrayList(0), false, j)
    }

    fun reload() {
        this.reloadRequested.set(true)
        if (this.loadRequested.compareAndSet(false, true)) {
            this.executor.execute(this.loadMoreData)
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:135:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00d9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    fun setVisibleRange(r9: Int, r10: Int) {
        /*
            r8 = this
            r1 = 1
            r2 = 0
            java.lang.Object r3 = r8.lock
            monitor-enter(r3)
            java.lang.String r4 = "ChatView: visible range %d, %d size %d above possible %s below possible %s"
            r0 = 5
            java.lang.Array<Any> r5 = java.lang.Object[r0]     // Catch:{ all -> 0x00fb }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x00fb }
            r6 = 0
            r5[r6] = r0     // Catch:{ all -> 0x00fb }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r10)     // Catch:{ all -> 0x00fb }
            r6 = 1
            r5[r6] = r0     // Catch:{ all -> 0x00fb }
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedList<E> r0 = r8.items     // Catch:{ all -> 0x00fb }
            val r0: Int = r0.size()     // Catch:{ all -> 0x00fb }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x00fb }
            r6 = 2
            r5[r6] = r0     // Catch:{ all -> 0x00fb }
            val r0: Boolean = r8.loadAboveWanted     // Catch:{ all -> 0x00fb }
            if (r0 != 0) goto L_0x00f1
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader$LoadResult<E> r0 = r8.loadAboveResult     // Catch:{ all -> 0x00fb }
            if (r0 != 0) goto L_0x00f1
            java.lang.String r0 = "yes"
        L_0x0031:
            r6 = 3
            r5[r6] = r0     // Catch:{ all -> 0x00fb }
            val r0: Boolean = r8.loadBelowWanted     // Catch:{ all -> 0x00fb }
            if (r0 != 0) goto L_0x00f6
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader$LoadResult<E> r0 = r8.loadBelowResult     // Catch:{ all -> 0x00fb }
            if (r0 != 0) goto L_0x00f6
            java.lang.String r0 = "yes"
        L_0x003f:
            r6 = 4
            r5[r6] = r0     // Catch:{ all -> 0x00fb }
            com.lumiyaviewer.lumiya.Debug.Printf(r4, r5)     // Catch:{ all -> 0x00fb }
            monitor-exit(r3)
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedList<E> r0 = r8.items
            val r0: Int = r0.size()
            if (r0 <= 0) goto L_0x0166
            if (r9 > 0) goto L_0x0103
            val r0: Boolean = r8.hasAbove
            if (r0 == 0) goto L_0x0103
            java.lang.Object r3 = r8.lock
            monitor-enter(r3)
            val r0: Boolean = r8.loadAboveWanted     // Catch:{ all -> 0x0100 }
            if (r0 != 0) goto L_0x00fe
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader$LoadResult<E> r0 = r8.loadAboveResult     // Catch:{ all -> 0x0100 }
            if (r0 != 0) goto L_0x00fe
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedList<E> r0 = r8.items     // Catch:{ all -> 0x0100 }
            r4 = 0
            java.lang.Object r0 = r0.get(r4)     // Catch:{ all -> 0x0100 }
            com.lumiyaviewer.lumiya.utils.Identifiable r0 = (com.lumiyaviewer.lumiya.utils.Identifiable) r0     // Catch:{ all -> 0x0100 }
            java.lang.Object r0 = r0.getId()     // Catch:{ all -> 0x0100 }
            java.lang.Long r0 = (java.lang.Long) r0     // Catch:{ all -> 0x0100 }
            val r4: Long = r0.longValue()     // Catch:{ all -> 0x0100 }
            r8.loadAboveTopmostId = r4     // Catch:{ all -> 0x0100 }
            r0 = 1
            r8.loadAboveWanted = r0     // Catch:{ all -> 0x0100 }
            java.lang.String r0 = "ChatView: requesting load above id %d"
            r4 = 1
            java.lang.Array<Any> r4 = java.lang.Object[r4]     // Catch:{ all -> 0x0100 }
            val r6: Long = r8.loadAboveTopmostId     // Catch:{ all -> 0x0100 }
            java.lang.Long r5 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0100 }
            r6 = 0
            r4[r6] = r5     // Catch:{ all -> 0x0100 }
            com.lumiyaviewer.lumiya.Debug.Printf(r0, r4)     // Catch:{ all -> 0x0100 }
            r0 = r1
        L_0x008a:
            monitor-exit(r3)
        L_0x008b:
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedList<E> r3 = r8.items
            val r3: Int = r3.size()
            val r3: Int = r3 + -1
            if (r10 < r3) goto L_0x012e
            val r3: Boolean = r8.hasBelow
            if (r3 == 0) goto L_0x012e
            java.lang.Object r3 = r8.lock
            monitor-enter(r3)
            val r4: Boolean = r8.loadBelowWanted     // Catch:{ all -> 0x012b }
            if (r4 != 0) goto L_0x00d6
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader$LoadResult<E> r4 = r8.loadBelowResult     // Catch:{ all -> 0x012b }
            if (r4 != 0) goto L_0x00d6
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedList<E> r0 = r8.items     // Catch:{ all -> 0x012b }
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedList<E> r4 = r8.items     // Catch:{ all -> 0x012b }
            val r4: Int = r4.size()     // Catch:{ all -> 0x012b }
            val r4: Int = r4 + -1
            java.lang.Object r0 = r0.get(r4)     // Catch:{ all -> 0x012b }
            com.lumiyaviewer.lumiya.utils.Identifiable r0 = (com.lumiyaviewer.lumiya.utils.Identifiable) r0     // Catch:{ all -> 0x012b }
            java.lang.Object r0 = r0.getId()     // Catch:{ all -> 0x012b }
            java.lang.Long r0 = (java.lang.Long) r0     // Catch:{ all -> 0x012b }
            val r4: Long = r0.longValue()     // Catch:{ all -> 0x012b }
            r8.loadBelowLastId = r4     // Catch:{ all -> 0x012b }
            r0 = 1
            r8.loadBelowWanted = r0     // Catch:{ all -> 0x012b }
            java.lang.String r0 = "ChatView: requesting load below id %d"
            r4 = 1
            java.lang.Array<Any> r4 = java.lang.Object[r4]     // Catch:{ all -> 0x012b }
            val r6: Long = r8.loadBelowLastId     // Catch:{ all -> 0x012b }
            java.lang.Long r5 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x012b }
            r6 = 0
            r4[r6] = r5     // Catch:{ all -> 0x012b }
            com.lumiyaviewer.lumiya.Debug.Printf(r0, r4)     // Catch:{ all -> 0x012b }
            r0 = r1
        L_0x00d6:
            monitor-exit(r3)
        L_0x00d7:
            if (r0 == 0) goto L_0x00f0
            java.util.concurrent.atomic.AtomicBoolean r0 = r8.loadRequested
            val r0: Boolean = r0.compareAndSet(r2, r1)
            if (r0 == 0) goto L_0x01cf
            java.lang.String r0 = "ChatView: requesting loadMoreData ()"
            java.lang.Array<Any> r1 = java.lang.Object[r2]
            com.lumiyaviewer.lumiya.Debug.Printf(r0, r1)
            java.util.concurrent.Executor r0 = r8.executor
            java.lang.Runnable r1 = r8.loadMoreData
            r0.execute(r1)
        L_0x00f0:
            return
        L_0x00f1:
            java.lang.String r0 = "no"
            goto L_0x0031
        L_0x00f6:
            java.lang.String r0 = "no"
            goto L_0x003f
        L_0x00fb:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        L_0x00fe:
            r0 = r2
            goto L_0x008a
        L_0x0100:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        L_0x0103:
            if (r9 <= 0) goto L_0x01dc
            java.lang.Object r3 = r8.lock
            monitor-enter(r3)
            val r0: Boolean = r8.loadAboveWanted     // Catch:{ all -> 0x0128 }
            if (r0 != 0) goto L_0x0126
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader$LoadResult<E> r0 = r8.loadAboveResult     // Catch:{ all -> 0x0128 }
            if (r0 != 0) goto L_0x0126
            r0 = r1
        L_0x0111:
            monitor-exit(r3)
            if (r0 == 0) goto L_0x01dc
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedList<E> r0 = r8.items
            val r0: Int = r0.removeElementsBefore(r9)
            if (r0 == 0) goto L_0x01dc
            r8.hasAbove = r1
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader$EventListener r3 = r8.listener
            r3.onListItemsRemoved(r2, r0)
            r0 = r2
            goto L_0x008b
        L_0x0126:
            r0 = r2
            goto L_0x0111
        L_0x0128:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        L_0x012b:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        L_0x012e:
            if (r10 < 0) goto L_0x00d7
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedList<E> r3 = r8.items
            val r3: Int = r3.size()
            val r3: Int = r3 + -1
            if (r10 >= r3) goto L_0x00d7
            java.lang.Object r4 = r8.lock
            monitor-enter(r4)
            val r3: Boolean = r8.loadBelowWanted     // Catch:{ all -> 0x0163 }
            if (r3 != 0) goto L_0x0161
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader$LoadResult<E> r3 = r8.loadBelowResult     // Catch:{ all -> 0x0163 }
            if (r3 != 0) goto L_0x0161
            r3 = r1
        L_0x0146:
            monitor-exit(r4)
            if (r3 == 0) goto L_0x00d7
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedList<E> r3 = r8.items
            val r3: Int = r3.size()
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedList<E> r4 = r8.items
            val r4: Int = r4.removeElementsAfter(r10)
            if (r4 == 0) goto L_0x00d7
            r8.hasBelow = r1
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader$EventListener r5 = r8.listener
            val r3: Int = r3 - r4
            r5.onListItemsRemoved(r3, r4)
            goto L_0x00d7
        L_0x0161:
            r3 = r2
            goto L_0x0146
        L_0x0163:
            r0 = move-exception
            monitor-exit(r4)
            throw r0
        L_0x0166:
            val r0: Boolean = r8.startFromStart
            if (r0 == 0) goto L_0x019b
            val r0: Boolean = r8.hasBelow
            if (r0 == 0) goto L_0x01d9
            java.lang.Object r3 = r8.lock
            monitor-enter(r3)
            val r0: Boolean = r8.loadBelowWanted     // Catch:{ all -> 0x0198 }
            if (r0 != 0) goto L_0x0195
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader$LoadResult<E> r0 = r8.loadBelowResult     // Catch:{ all -> 0x0198 }
            if (r0 != 0) goto L_0x0195
            r4 = 0
            r8.loadBelowLastId = r4     // Catch:{ all -> 0x0198 }
            r0 = 1
            r8.loadBelowWanted = r0     // Catch:{ all -> 0x0198 }
            java.lang.String r0 = "ChatView: requesting load below id %d"
            r4 = 1
            java.lang.Array<Any> r4 = java.lang.Object[r4]     // Catch:{ all -> 0x0198 }
            val r6: Long = r8.loadBelowLastId     // Catch:{ all -> 0x0198 }
            java.lang.Long r5 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0198 }
            r6 = 0
            r4[r6] = r5     // Catch:{ all -> 0x0198 }
            com.lumiyaviewer.lumiya.Debug.Printf(r0, r4)     // Catch:{ all -> 0x0198 }
            r0 = r1
            goto L_0x00d6
        L_0x0195:
            r0 = r2
            goto L_0x00d6
        L_0x0198:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        L_0x019b:
            val r0: Boolean = r8.hasAbove
            if (r0 == 0) goto L_0x01d9
            java.lang.Object r3 = r8.lock
            monitor-enter(r3)
            val r0: Boolean = r8.loadAboveWanted     // Catch:{ all -> 0x01cc }
            if (r0 != 0) goto L_0x01c9
            com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader$LoadResult<E> r0 = r8.loadAboveResult     // Catch:{ all -> 0x01cc }
            if (r0 != 0) goto L_0x01c9
            r4 = 9223372036854775807(0x7fffffffffffffff, Double:NaN)
            r8.loadAboveTopmostId = r4     // Catch:{ all -> 0x01cc }
            r0 = 1
            r8.loadAboveWanted = r0     // Catch:{ all -> 0x01cc }
            java.lang.String r0 = "ChatView: requesting load above id %d"
            r4 = 1
            java.lang.Array<Any> r4 = java.lang.Object[r4]     // Catch:{ all -> 0x01cc }
            val r6: Long = r8.loadAboveTopmostId     // Catch:{ all -> 0x01cc }
            java.lang.Long r5 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x01cc }
            r6 = 0
            r4[r6] = r5     // Catch:{ all -> 0x01cc }
            com.lumiyaviewer.lumiya.Debug.Printf(r0, r4)     // Catch:{ all -> 0x01cc }
            r0 = r1
            goto L_0x00d6
        L_0x01c9:
            r0 = r2
            goto L_0x00d6
        L_0x01cc:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        L_0x01cf:
            java.lang.String r0 = "ChatView: loadMoreData() already requested"
            java.lang.Array<Any> r1 = java.lang.Object[r2]
            com.lumiyaviewer.lumiya.Debug.Printf(r0, r1)
            goto L_0x00f0
        L_0x01d9:
            r0 = r2
            goto L_0x00d7
        L_0x01dc:
            r0 = r2
            goto L_0x008b
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader.setVisibleRange(Int, Int):Unit")
    }

     public fun size(): Int {
        return this.items.size()
    }

    fun updateElement(E e) {
        Debug.Printf("ChatView: addElement: updated element with id %d", e.getId())
        synchronized (this.lock) {
            this.updatedElements.put((Long) e.getId(), e)
        }
        postUpdate()
    }
}
