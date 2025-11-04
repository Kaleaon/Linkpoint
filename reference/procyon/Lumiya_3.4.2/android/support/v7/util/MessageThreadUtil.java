// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.util;

import android.os.Looper;
import android.os.Handler;
import android.util.Log;
import android.os.AsyncTask;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

class MessageThreadUtil<T> implements ThreadUtil<T>
{
    @Override
    public BackgroundCallback<T> getBackgroundProxy(final BackgroundCallback<T> backgroundCallback) {
        return new BackgroundCallback<T>() {
            static final int LOAD_TILE = 3;
            static final int RECYCLE_TILE = 4;
            static final int REFRESH = 1;
            static final int UPDATE_RANGE = 2;
            private Runnable mBackgroundRunnable = new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        final SyncQueueItem next = BackgroundCallback.this.mQueue.next();
                        if (next == null) {
                            break;
                        }
                        switch (next.what) {
                            default: {
                                Log.e("ThreadUtil", "Unsupported message, what=" + next.what);
                                continue;
                            }
                            case 1: {
                                BackgroundCallback.this.mQueue.removeMessages(1);
                                backgroundCallback.refresh(next.arg1);
                                continue;
                            }
                            case 2: {
                                BackgroundCallback.this.mQueue.removeMessages(2);
                                BackgroundCallback.this.mQueue.removeMessages(3);
                                backgroundCallback.updateRange(next.arg1, next.arg2, next.arg3, next.arg4, next.arg5);
                                continue;
                            }
                            case 3: {
                                backgroundCallback.loadTile(next.arg1, next.arg2);
                                continue;
                            }
                            case 4: {
                                backgroundCallback.recycleTile((TileList.Tile)next.data);
                                continue;
                            }
                        }
                    }
                    BackgroundCallback.this.mBackgroundRunning.set(false);
                }
            };
            AtomicBoolean mBackgroundRunning = new AtomicBoolean(false);
            private final Executor mExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
            final MessageQueue mQueue = new MessageQueue();
            
            private void maybeExecuteBackgroundRunnable() {
                if (this.mBackgroundRunning.compareAndSet(false, true)) {
                    this.mExecutor.execute(this.mBackgroundRunnable);
                }
            }
            
            private void sendMessage(final SyncQueueItem syncQueueItem) {
                this.mQueue.sendMessage(syncQueueItem);
                this.maybeExecuteBackgroundRunnable();
            }
            
            private void sendMessageAtFrontOfQueue(final SyncQueueItem syncQueueItem) {
                this.mQueue.sendMessageAtFrontOfQueue(syncQueueItem);
                this.maybeExecuteBackgroundRunnable();
            }
            
            @Override
            public void loadTile(final int n, final int n2) {
                this.sendMessage(SyncQueueItem.obtainMessage(3, n, n2));
            }
            
            @Override
            public void recycleTile(final TileList.Tile<T> tile) {
                this.sendMessage(SyncQueueItem.obtainMessage(4, 0, tile));
            }
            
            @Override
            public void refresh(final int n) {
                this.sendMessageAtFrontOfQueue(SyncQueueItem.obtainMessage(1, n, null));
            }
            
            @Override
            public void updateRange(final int n, final int n2, final int n3, final int n4, final int n5) {
                this.sendMessageAtFrontOfQueue(SyncQueueItem.obtainMessage(2, n, n2, n3, n4, n5, null));
            }
        };
    }
    
    @Override
    public MainThreadCallback<T> getMainThreadProxy(final MainThreadCallback<T> mainThreadCallback) {
        return new MainThreadCallback<T>() {
            static final int ADD_TILE = 2;
            static final int REMOVE_TILE = 3;
            static final int UPDATE_ITEM_COUNT = 1;
            private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
            private Runnable mMainThreadRunnable = new Runnable() {
                @Override
                public void run() {
                    for (SyncQueueItem syncQueueItem = MainThreadCallback.this.mQueue.next(); syncQueueItem != null; syncQueueItem = MainThreadCallback.this.mQueue.next()) {
                        switch (syncQueueItem.what) {
                            default: {
                                Log.e("ThreadUtil", "Unsupported message, what=" + syncQueueItem.what);
                                break;
                            }
                            case 1: {
                                mainThreadCallback.updateItemCount(syncQueueItem.arg1, syncQueueItem.arg2);
                                break;
                            }
                            case 2: {
                                mainThreadCallback.addTile(syncQueueItem.arg1, (TileList.Tile)syncQueueItem.data);
                                break;
                            }
                            case 3: {
                                mainThreadCallback.removeTile(syncQueueItem.arg1, syncQueueItem.arg2);
                                break;
                            }
                        }
                    }
                }
            };
            final MessageQueue mQueue = new MessageQueue();
            
            private void sendMessage(final SyncQueueItem syncQueueItem) {
                this.mQueue.sendMessage(syncQueueItem);
                this.mMainThreadHandler.post(this.mMainThreadRunnable);
            }
            
            @Override
            public void addTile(final int n, final TileList.Tile<T> tile) {
                this.sendMessage(SyncQueueItem.obtainMessage(2, n, tile));
            }
            
            @Override
            public void removeTile(final int n, final int n2) {
                this.sendMessage(SyncQueueItem.obtainMessage(3, n, n2));
            }
            
            @Override
            public void updateItemCount(final int n, final int n2) {
                this.sendMessage(SyncQueueItem.obtainMessage(1, n, n2));
            }
        };
    }
    
    static class MessageQueue
    {
        private SyncQueueItem mRoot;
        
        SyncQueueItem next() {
            synchronized (this) {
                if (this.mRoot != null) {
                    final SyncQueueItem mRoot = this.mRoot;
                    this.mRoot = this.mRoot.next;
                    return mRoot;
                }
                return null;
            }
        }
        
        void removeMessages(final int n) {
            monitorenter(this);
            while (true) {
                try {
                    while (this.mRoot != null && this.mRoot.what == n) {
                        final SyncQueueItem mRoot = this.mRoot;
                        this.mRoot = this.mRoot.next;
                        mRoot.recycle();
                    }
                    if (this.mRoot == null) {
                        return;
                    }
                }
                finally {
                    monitorexit(this);
                }
                Object mRoot2 = this.mRoot;
                SyncQueueItem access$2;
                for (SyncQueueItem access$000 = ((SyncQueueItem)mRoot2).next; access$000 != null; access$000 = access$2) {
                    access$2 = access$000.next;
                    if (access$000.what == n) {
                        ((SyncQueueItem)mRoot2).next = access$2;
                        access$000.recycle();
                        access$000 = (SyncQueueItem)mRoot2;
                    }
                    mRoot2 = access$000;
                }
            }
        }
        
        void sendMessage(final SyncQueueItem mRoot) {
            synchronized (this) {
                if (this.mRoot != null) {
                    Object o;
                    for (o = this.mRoot; ((SyncQueueItem)o).next != null; o = ((SyncQueueItem)o).next) {}
                    ((SyncQueueItem)o).next = mRoot;
                    return;
                }
                this.mRoot = mRoot;
            }
        }
        
        void sendMessageAtFrontOfQueue(final SyncQueueItem mRoot) {
            synchronized (this) {
                mRoot.next = this.mRoot;
                this.mRoot = mRoot;
            }
        }
    }
    
    static class SyncQueueItem
    {
        private static SyncQueueItem sPool;
        private static final Object sPoolLock;
        public int arg1;
        public int arg2;
        public int arg3;
        public int arg4;
        public int arg5;
        public Object data;
        private SyncQueueItem next;
        public int what;
        
        static {
            sPoolLock = new Object();
        }
        
        static SyncQueueItem obtainMessage(final int n, final int n2, final int n3) {
            return obtainMessage(n, n2, n3, 0, 0, 0, null);
        }
        
        static SyncQueueItem obtainMessage(final int what, final int arg1, final int arg2, final int arg3, final int arg4, final int arg5, final Object data) {
            synchronized (SyncQueueItem.sPoolLock) {
                SyncQueueItem sPool;
                if (SyncQueueItem.sPool != null) {
                    sPool = SyncQueueItem.sPool;
                    SyncQueueItem.sPool = SyncQueueItem.sPool.next;
                    sPool.next = null;
                }
                else {
                    sPool = new SyncQueueItem();
                }
                sPool.what = what;
                sPool.arg1 = arg1;
                sPool.arg2 = arg2;
                sPool.arg3 = arg3;
                sPool.arg4 = arg4;
                sPool.arg5 = arg5;
                sPool.data = data;
                return sPool;
            }
        }
        
        static SyncQueueItem obtainMessage(final int n, final int n2, final Object o) {
            return obtainMessage(n, n2, 0, 0, 0, 0, o);
        }
        
        void recycle() {
            this.next = null;
            this.arg5 = 0;
            this.arg4 = 0;
            this.arg3 = 0;
            this.arg2 = 0;
            this.arg1 = 0;
            this.what = 0;
            this.data = null;
            synchronized (SyncQueueItem.sPoolLock) {
                if (SyncQueueItem.sPool != null) {
                    this.next = SyncQueueItem.sPool;
                }
                SyncQueueItem.sPool = this;
            }
        }
    }
}
