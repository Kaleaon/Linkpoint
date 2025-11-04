// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import java.util.List;
import java.util.Collection;
import java.util.Collections;
import android.support.v4.util.Pools;
import java.util.ArrayList;

class AdapterHelper implements OpReorderer.Callback
{
    private static final boolean DEBUG = false;
    static final int POSITION_TYPE_INVISIBLE = 0;
    static final int POSITION_TYPE_NEW_OR_LAID_OUT = 1;
    private static final String TAG = "AHT";
    final Callback mCallback;
    final boolean mDisableRecycler;
    private int mExistingUpdateTypes;
    Runnable mOnItemProcessedCallback;
    final OpReorderer mOpReorderer;
    final ArrayList<UpdateOp> mPendingUpdates;
    final ArrayList<UpdateOp> mPostponedList;
    private Pools.Pool<UpdateOp> mUpdateOpPool;
    
    AdapterHelper(final Callback callback) {
        this(callback, false);
    }
    
    AdapterHelper(final Callback mCallback, final boolean mDisableRecycler) {
        this.mUpdateOpPool = new Pools.SimplePool<UpdateOp>(30);
        this.mPendingUpdates = new ArrayList<UpdateOp>();
        this.mPostponedList = new ArrayList<UpdateOp>();
        this.mExistingUpdateTypes = 0;
        this.mCallback = mCallback;
        this.mDisableRecycler = mDisableRecycler;
        this.mOpReorderer = new OpReorderer((OpReorderer.Callback)this);
    }
    
    private void applyAdd(final UpdateOp updateOp) {
        this.postponeAndUpdateViewHolders(updateOp);
    }
    
    private void applyMove(final UpdateOp updateOp) {
        this.postponeAndUpdateViewHolders(updateOp);
    }
    
    private void applyRemove(UpdateOp obtainUpdateOp) {
        final int positionStart = obtainUpdateOp.positionStart;
        int n = obtainUpdateOp.positionStart + obtainUpdateOp.itemCount;
        int n2 = -1;
        int i = obtainUpdateOp.positionStart;
        int n3 = 0;
        while (i < n) {
            int n6;
            if (this.mCallback.findViewHolder(i) == null && !this.canFindInPreLayout(i)) {
                int n4;
                if (n2 != 1) {
                    n4 = 0;
                }
                else {
                    this.postponeAndUpdateViewHolders(this.obtainUpdateOp(2, positionStart, n3, null));
                    n4 = 1;
                }
                final int n5 = 0;
                n6 = n4;
                n2 = n5;
            }
            else {
                if (n2 != 0) {
                    n6 = 0;
                }
                else {
                    this.dispatchAndUpdateViewHolders(this.obtainUpdateOp(2, positionStart, n3, null));
                    n6 = 1;
                }
                n2 = 1;
            }
            int n8;
            int n10;
            int n11;
            if (n6 == 0) {
                final int n7 = n;
                n8 = n3 + 1;
                final int n9 = i;
                n10 = n7;
                n11 = n9;
            }
            else {
                n11 = i - n3;
                n10 = n - n3;
                n8 = 1;
            }
            n3 = n8;
            n = n10;
            i = n11 + 1;
        }
        if (n3 != obtainUpdateOp.itemCount) {
            this.recycleUpdateOp(obtainUpdateOp);
            obtainUpdateOp = this.obtainUpdateOp(2, positionStart, n3, null);
        }
        if (n2 != 0) {
            this.postponeAndUpdateViewHolders(obtainUpdateOp);
        }
        else {
            this.dispatchAndUpdateViewHolders(obtainUpdateOp);
        }
    }
    
    private void applyUpdate(UpdateOp obtainUpdateOp) {
        int positionStart = obtainUpdateOp.positionStart;
        final int positionStart2 = obtainUpdateOp.positionStart;
        final int itemCount = obtainUpdateOp.itemCount;
        int i = obtainUpdateOp.positionStart;
        int n = -1;
        int n2 = 0;
        while (i < positionStart2 + itemCount) {
            int n3;
            int n4;
            if (this.mCallback.findViewHolder(i) == null && !this.canFindInPreLayout(i)) {
                if (n == 1) {
                    this.postponeAndUpdateViewHolders(this.obtainUpdateOp(4, positionStart, n2, obtainUpdateOp.payload));
                    n2 = 0;
                    positionStart = i;
                }
                n3 = n2;
                n4 = 0;
            }
            else {
                if (n == 0) {
                    this.dispatchAndUpdateViewHolders(this.obtainUpdateOp(4, positionStart, n2, obtainUpdateOp.payload));
                    n2 = 0;
                    positionStart = i;
                }
                final int n5 = 1;
                n3 = n2;
                n4 = n5;
            }
            ++i;
            final int n6 = n3 + 1;
            n = n4;
            n2 = n6;
        }
        if (n2 != obtainUpdateOp.itemCount) {
            final Object payload = obtainUpdateOp.payload;
            this.recycleUpdateOp(obtainUpdateOp);
            obtainUpdateOp = this.obtainUpdateOp(4, positionStart, n2, payload);
        }
        if (n != 0) {
            this.postponeAndUpdateViewHolders(obtainUpdateOp);
        }
        else {
            this.dispatchAndUpdateViewHolders(obtainUpdateOp);
        }
    }
    
    private boolean canFindInPreLayout(final int n) {
        for (int size = this.mPostponedList.size(), i = 0; i < size; ++i) {
            final UpdateOp updateOp = this.mPostponedList.get(i);
            if (updateOp.cmd != 8) {
                if (updateOp.cmd == 1) {
                    for (int positionStart = updateOp.positionStart, itemCount = updateOp.itemCount, j = updateOp.positionStart; j < positionStart + itemCount; ++j) {
                        if (this.findPositionOffset(j, i + 1) == n) {
                            return true;
                        }
                    }
                }
            }
            else if (this.findPositionOffset(updateOp.itemCount, i + 1) == n) {
                return true;
            }
        }
        return false;
    }
    
    private void dispatchAndUpdateViewHolders(UpdateOp obtainUpdateOp) {
        if (obtainUpdateOp.cmd != 1 && obtainUpdateOp.cmd != 8) {
            int updatePositionWithPostponed = this.updatePositionWithPostponed(obtainUpdateOp.positionStart, obtainUpdateOp.cmd);
            int positionStart = obtainUpdateOp.positionStart;
            int n = 0;
            switch (obtainUpdateOp.cmd) {
                default: {
                    throw new IllegalArgumentException("op should be remove or update." + obtainUpdateOp);
                }
                case 4: {
                    n = 1;
                    break;
                }
                case 2: {
                    n = 0;
                    break;
                }
            }
            int n2 = 1;
            int n4;
            for (int i = 1; i < obtainUpdateOp.itemCount; ++i, n2 = n4) {
                final int updatePositionWithPostponed2 = this.updatePositionWithPostponed(obtainUpdateOp.positionStart + n * i, obtainUpdateOp.cmd);
                int n3 = 0;
                switch (obtainUpdateOp.cmd) {
                    default: {
                        n3 = 0;
                        break;
                    }
                    case 4: {
                        if (updatePositionWithPostponed2 != updatePositionWithPostponed + 1) {
                            n3 = 0;
                            break;
                        }
                        n3 = 1;
                        break;
                    }
                    case 2: {
                        if (updatePositionWithPostponed2 != updatePositionWithPostponed) {
                            n3 = 0;
                            break;
                        }
                        n3 = 1;
                        break;
                    }
                }
                if (n3 == 0) {
                    final UpdateOp obtainUpdateOp2 = this.obtainUpdateOp(obtainUpdateOp.cmd, updatePositionWithPostponed, n2, obtainUpdateOp.payload);
                    this.dispatchFirstPassAndUpdateViewHolders(obtainUpdateOp2, positionStart);
                    this.recycleUpdateOp(obtainUpdateOp2);
                    if (obtainUpdateOp.cmd == 4) {
                        positionStart += n2;
                    }
                    n4 = 1;
                    updatePositionWithPostponed = updatePositionWithPostponed2;
                }
                else {
                    n4 = n2 + 1;
                }
            }
            final Object payload = obtainUpdateOp.payload;
            this.recycleUpdateOp(obtainUpdateOp);
            if (n2 > 0) {
                obtainUpdateOp = this.obtainUpdateOp(obtainUpdateOp.cmd, updatePositionWithPostponed, n2, payload);
                this.dispatchFirstPassAndUpdateViewHolders(obtainUpdateOp, positionStart);
                this.recycleUpdateOp(obtainUpdateOp);
            }
            return;
        }
        throw new IllegalArgumentException("should not dispatch add or move for pre layout");
    }
    
    private void postponeAndUpdateViewHolders(final UpdateOp updateOp) {
        this.mPostponedList.add(updateOp);
        switch (updateOp.cmd) {
            default: {
                throw new IllegalArgumentException("Unknown update op type for " + updateOp);
            }
            case 1: {
                this.mCallback.offsetPositionsForAdd(updateOp.positionStart, updateOp.itemCount);
                break;
            }
            case 8: {
                this.mCallback.offsetPositionsForMove(updateOp.positionStart, updateOp.itemCount);
                break;
            }
            case 2: {
                this.mCallback.offsetPositionsForRemovingLaidOutOrNewView(updateOp.positionStart, updateOp.itemCount);
                break;
            }
            case 4: {
                this.mCallback.markViewHoldersUpdated(updateOp.positionStart, updateOp.itemCount, updateOp.payload);
                break;
            }
        }
    }
    
    private int updatePositionWithPostponed(int n, int i) {
        int n2;
        for (int j = this.mPostponedList.size() - 1; j >= 0; --j, n = n2) {
            final UpdateOp updateOp = this.mPostponedList.get(j);
            if (updateOp.cmd != 8) {
                if (updateOp.positionStart > n) {
                    if (i != 1) {
                        if (i != 2) {
                            n2 = n;
                        }
                        else {
                            --updateOp.positionStart;
                            n2 = n;
                        }
                    }
                    else {
                        ++updateOp.positionStart;
                        n2 = n;
                    }
                }
                else if (updateOp.cmd != 1) {
                    n2 = n;
                    if (updateOp.cmd == 2) {
                        n2 = n + updateOp.itemCount;
                    }
                }
                else {
                    n2 = n - updateOp.itemCount;
                }
            }
            else {
                int n3;
                int n4;
                if (updateOp.positionStart >= updateOp.itemCount) {
                    n3 = updateOp.itemCount;
                    n4 = updateOp.positionStart;
                }
                else {
                    n3 = updateOp.positionStart;
                    n4 = updateOp.itemCount;
                }
                if (n >= n3 && n <= n4) {
                    if (n3 != updateOp.positionStart) {
                        if (i != 1) {
                            if (i == 2) {
                                --updateOp.positionStart;
                            }
                        }
                        else {
                            ++updateOp.positionStart;
                        }
                        --n;
                    }
                    else {
                        if (i != 1) {
                            if (i == 2) {
                                --updateOp.itemCount;
                            }
                        }
                        else {
                            ++updateOp.itemCount;
                        }
                        ++n;
                    }
                }
                else if (n < updateOp.positionStart) {
                    if (i != 1) {
                        if (i == 2) {
                            --updateOp.positionStart;
                            --updateOp.itemCount;
                        }
                    }
                    else {
                        ++updateOp.positionStart;
                        ++updateOp.itemCount;
                    }
                }
                n2 = n;
            }
        }
        UpdateOp updateOp2;
        for (i = this.mPostponedList.size() - 1; i >= 0; --i) {
            updateOp2 = this.mPostponedList.get(i);
            if (updateOp2.cmd != 8) {
                if (updateOp2.itemCount <= 0) {
                    this.mPostponedList.remove(i);
                    this.recycleUpdateOp(updateOp2);
                }
            }
            else if (updateOp2.itemCount == updateOp2.positionStart || updateOp2.itemCount < 0) {
                this.mPostponedList.remove(i);
                this.recycleUpdateOp(updateOp2);
            }
        }
        return n;
    }
    
    AdapterHelper addUpdateOp(final UpdateOp... elements) {
        Collections.addAll(this.mPendingUpdates, elements);
        return this;
    }
    
    public int applyPendingUpdatesToPosition(int itemCount) {
        final int size = this.mPendingUpdates.size();
        int i = 0;
        int n = itemCount;
        while (i < size) {
            final UpdateOp updateOp = this.mPendingUpdates.get(i);
            switch (updateOp.cmd) {
                default: {
                    itemCount = n;
                    break;
                }
                case 1: {
                    itemCount = n;
                    if (updateOp.positionStart <= n) {
                        itemCount = n + updateOp.itemCount;
                        break;
                    }
                    break;
                }
                case 2: {
                    itemCount = n;
                    if (updateOp.positionStart > n) {
                        break;
                    }
                    if (updateOp.positionStart + updateOp.itemCount <= n) {
                        itemCount = n - updateOp.itemCount;
                        break;
                    }
                    return -1;
                }
                case 8: {
                    if (updateOp.positionStart == n) {
                        itemCount = updateOp.itemCount;
                        break;
                    }
                    if (updateOp.positionStart < n) {
                        --n;
                    }
                    if (updateOp.itemCount <= (itemCount = n)) {
                        itemCount = n + 1;
                        break;
                    }
                    break;
                }
            }
            ++i;
            n = itemCount;
        }
        return n;
    }
    
    void consumePostponedUpdates() {
        for (int size = this.mPostponedList.size(), i = 0; i < size; ++i) {
            this.mCallback.onDispatchSecondPass(this.mPostponedList.get(i));
        }
        this.recycleUpdateOpsAndClearList(this.mPostponedList);
        this.mExistingUpdateTypes = 0;
    }
    
    void consumeUpdatesInOnePass() {
        this.consumePostponedUpdates();
        for (int size = this.mPendingUpdates.size(), i = 0; i < size; ++i) {
            final UpdateOp updateOp = this.mPendingUpdates.get(i);
            switch (updateOp.cmd) {
                case 1: {
                    this.mCallback.onDispatchSecondPass(updateOp);
                    this.mCallback.offsetPositionsForAdd(updateOp.positionStart, updateOp.itemCount);
                    break;
                }
                case 2: {
                    this.mCallback.onDispatchSecondPass(updateOp);
                    this.mCallback.offsetPositionsForRemovingInvisible(updateOp.positionStart, updateOp.itemCount);
                    break;
                }
                case 4: {
                    this.mCallback.onDispatchSecondPass(updateOp);
                    this.mCallback.markViewHoldersUpdated(updateOp.positionStart, updateOp.itemCount, updateOp.payload);
                    break;
                }
                case 8: {
                    this.mCallback.onDispatchSecondPass(updateOp);
                    this.mCallback.offsetPositionsForMove(updateOp.positionStart, updateOp.itemCount);
                    break;
                }
            }
            if (this.mOnItemProcessedCallback != null) {
                this.mOnItemProcessedCallback.run();
            }
        }
        this.recycleUpdateOpsAndClearList(this.mPendingUpdates);
        this.mExistingUpdateTypes = 0;
    }
    
    void dispatchFirstPassAndUpdateViewHolders(final UpdateOp updateOp, final int n) {
        this.mCallback.onDispatchFirstPass(updateOp);
        switch (updateOp.cmd) {
            default: {
                throw new IllegalArgumentException("only remove and update ops can be dispatched in first pass");
            }
            case 2: {
                this.mCallback.offsetPositionsForRemovingInvisible(n, updateOp.itemCount);
                break;
            }
            case 4: {
                this.mCallback.markViewHoldersUpdated(n, updateOp.itemCount, updateOp.payload);
                break;
            }
        }
    }
    
    int findPositionOffset(final int n) {
        return this.findPositionOffset(n, 0);
    }
    
    int findPositionOffset(int itemCount, int n) {
        final int size = this.mPostponedList.size();
        int i = n;
        n = itemCount;
        while (i < size) {
            final UpdateOp updateOp = this.mPostponedList.get(i);
            if (updateOp.cmd != 8) {
                if (updateOp.positionStart > n) {
                    itemCount = n;
                }
                else if (updateOp.cmd != 2) {
                    itemCount = n;
                    if (updateOp.cmd == 1) {
                        itemCount = n + updateOp.itemCount;
                    }
                }
                else {
                    if (n < updateOp.positionStart + updateOp.itemCount) {
                        return -1;
                    }
                    itemCount = n - updateOp.itemCount;
                }
            }
            else if (updateOp.positionStart != n) {
                if (updateOp.positionStart < n) {
                    --n;
                }
                if (updateOp.itemCount <= (itemCount = n)) {
                    itemCount = n + 1;
                }
            }
            else {
                itemCount = updateOp.itemCount;
            }
            ++i;
            n = itemCount;
        }
        return n;
    }
    
    boolean hasAnyUpdateTypes(final int n) {
        boolean b = false;
        if ((this.mExistingUpdateTypes & n) != 0x0) {
            b = true;
        }
        return b;
    }
    
    boolean hasPendingUpdates() {
        boolean b = false;
        if (this.mPendingUpdates.size() > 0) {
            b = true;
        }
        return b;
    }
    
    boolean hasUpdates() {
        boolean b = false;
        if (!this.mPostponedList.isEmpty() && !this.mPendingUpdates.isEmpty()) {
            b = true;
        }
        return b;
    }
    
    @Override
    public UpdateOp obtainUpdateOp(final int cmd, final int positionStart, final int itemCount, final Object payload) {
        final UpdateOp updateOp = this.mUpdateOpPool.acquire();
        UpdateOp updateOp2;
        if (updateOp != null) {
            updateOp.cmd = cmd;
            updateOp.positionStart = positionStart;
            updateOp.itemCount = itemCount;
            updateOp.payload = payload;
            updateOp2 = updateOp;
        }
        else {
            updateOp2 = new UpdateOp(cmd, positionStart, itemCount, payload);
        }
        return updateOp2;
    }
    
    boolean onItemRangeChanged(final int n, final int n2, final Object o) {
        boolean b = false;
        if (n2 >= 1) {
            this.mPendingUpdates.add(this.obtainUpdateOp(4, n, n2, o));
            this.mExistingUpdateTypes |= 0x4;
            if (this.mPendingUpdates.size() == 1) {
                b = true;
            }
            return b;
        }
        return false;
    }
    
    boolean onItemRangeInserted(final int n, final int n2) {
        boolean b = false;
        if (n2 >= 1) {
            this.mPendingUpdates.add(this.obtainUpdateOp(1, n, n2, null));
            this.mExistingUpdateTypes |= 0x1;
            if (this.mPendingUpdates.size() == 1) {
                b = true;
            }
            return b;
        }
        return false;
    }
    
    boolean onItemRangeMoved(final int n, final int n2, final int n3) {
        boolean b = false;
        if (n == n2) {
            return false;
        }
        if (n3 == 1) {
            this.mPendingUpdates.add(this.obtainUpdateOp(8, n, n2, null));
            this.mExistingUpdateTypes |= 0x8;
            if (this.mPendingUpdates.size() == 1) {
                b = true;
            }
            return b;
        }
        throw new IllegalArgumentException("Moving more than 1 item is not supported yet");
    }
    
    boolean onItemRangeRemoved(final int n, final int n2) {
        boolean b = false;
        if (n2 >= 1) {
            this.mPendingUpdates.add(this.obtainUpdateOp(2, n, n2, null));
            this.mExistingUpdateTypes |= 0x2;
            if (this.mPendingUpdates.size() == 1) {
                b = true;
            }
            return b;
        }
        return false;
    }
    
    void preProcess() {
        this.mOpReorderer.reorderOps(this.mPendingUpdates);
        for (int size = this.mPendingUpdates.size(), i = 0; i < size; ++i) {
            final UpdateOp updateOp = this.mPendingUpdates.get(i);
            switch (updateOp.cmd) {
                case 1: {
                    this.applyAdd(updateOp);
                    break;
                }
                case 2: {
                    this.applyRemove(updateOp);
                    break;
                }
                case 4: {
                    this.applyUpdate(updateOp);
                    break;
                }
                case 8: {
                    this.applyMove(updateOp);
                    break;
                }
            }
            if (this.mOnItemProcessedCallback != null) {
                this.mOnItemProcessedCallback.run();
            }
        }
        this.mPendingUpdates.clear();
    }
    
    @Override
    public void recycleUpdateOp(final UpdateOp updateOp) {
        if (!this.mDisableRecycler) {
            updateOp.payload = null;
            this.mUpdateOpPool.release(updateOp);
        }
    }
    
    void recycleUpdateOpsAndClearList(final List<UpdateOp> list) {
        for (int size = list.size(), i = 0; i < size; ++i) {
            this.recycleUpdateOp((UpdateOp)list.get(i));
        }
        list.clear();
    }
    
    void reset() {
        this.recycleUpdateOpsAndClearList(this.mPendingUpdates);
        this.recycleUpdateOpsAndClearList(this.mPostponedList);
        this.mExistingUpdateTypes = 0;
    }
    
    interface Callback
    {
        RecyclerView.ViewHolder findViewHolder(final int p0);
        
        void markViewHoldersUpdated(final int p0, final int p1, final Object p2);
        
        void offsetPositionsForAdd(final int p0, final int p1);
        
        void offsetPositionsForMove(final int p0, final int p1);
        
        void offsetPositionsForRemovingInvisible(final int p0, final int p1);
        
        void offsetPositionsForRemovingLaidOutOrNewView(final int p0, final int p1);
        
        void onDispatchFirstPass(final UpdateOp p0);
        
        void onDispatchSecondPass(final UpdateOp p0);
    }
    
    static class UpdateOp
    {
        static final int ADD = 1;
        static final int MOVE = 8;
        static final int POOL_SIZE = 30;
        static final int REMOVE = 2;
        static final int UPDATE = 4;
        int cmd;
        int itemCount;
        Object payload;
        int positionStart;
        
        UpdateOp(final int cmd, final int positionStart, final int itemCount, final Object payload) {
            this.cmd = cmd;
            this.positionStart = positionStart;
            this.itemCount = itemCount;
            this.payload = payload;
        }
        
        String cmdToString() {
            switch (this.cmd) {
                default: {
                    return "??";
                }
                case 1: {
                    return "add";
                }
                case 2: {
                    return "rm";
                }
                case 4: {
                    return "up";
                }
                case 8: {
                    return "mv";
                }
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final UpdateOp updateOp = (UpdateOp)o;
            if (this.cmd != updateOp.cmd) {
                return false;
            }
            if (this.cmd == 8 && Math.abs(this.itemCount - this.positionStart) == 1 && this.itemCount == updateOp.positionStart && this.positionStart == updateOp.itemCount) {
                return true;
            }
            if (this.itemCount != updateOp.itemCount) {
                return false;
            }
            if (this.positionStart == updateOp.positionStart) {
                if (this.payload == null) {
                    if (updateOp.payload != null) {
                        return false;
                    }
                }
                else if (!this.payload.equals(updateOp.payload)) {
                    return false;
                }
                return true;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return (this.cmd * 31 + this.positionStart) * 31 + this.itemCount;
        }
        
        @Override
        public String toString() {
            return Integer.toHexString(System.identityHashCode(this)) + "[" + this.cmdToString() + ",s:" + this.positionStart + "c:" + this.itemCount + ",p:" + this.payload + "]";
        }
    }
}
