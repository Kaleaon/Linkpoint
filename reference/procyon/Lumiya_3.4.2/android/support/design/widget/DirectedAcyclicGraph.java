// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.support.annotation.Nullable;
import java.util.List;
import android.support.annotation.NonNull;
import java.util.HashSet;
import android.support.v4.util.Pools;
import java.util.ArrayList;
import android.support.v4.util.SimpleArrayMap;

final class DirectedAcyclicGraph<T>
{
    private final SimpleArrayMap<T, ArrayList<T>> mGraph;
    private final Pools.Pool<ArrayList<T>> mListPool;
    private final ArrayList<T> mSortResult;
    private final HashSet<T> mSortTmpMarked;
    
    DirectedAcyclicGraph() {
        this.mListPool = new Pools.SimplePool<ArrayList<T>>(10);
        this.mGraph = new SimpleArrayMap<T, ArrayList<T>>();
        this.mSortResult = new ArrayList<T>();
        this.mSortTmpMarked = new HashSet<T>();
    }
    
    private void dfs(final T e, final ArrayList<T> list, final HashSet<T> set) {
        int i = 0;
        if (list.contains(e)) {
            return;
        }
        if (!set.contains(e)) {
            set.add(e);
            final ArrayList list2 = this.mGraph.get(e);
            if (list2 != null) {
                while (i < list2.size()) {
                    this.dfs(list2.get(i), (ArrayList<Object>)list, (HashSet<Object>)set);
                    ++i;
                }
            }
            set.remove(e);
            list.add(e);
            return;
        }
        throw new RuntimeException("This graph contains cyclic dependencies");
    }
    
    @NonNull
    private ArrayList<T> getEmptyList() {
        ArrayList list = this.mListPool.acquire();
        if (list == null) {
            list = new ArrayList();
        }
        return list;
    }
    
    private void poolList(@NonNull final ArrayList<T> list) {
        list.clear();
        this.mListPool.release(list);
    }
    
    void addEdge(@NonNull final T t, @NonNull final T e) {
        if (this.mGraph.containsKey(t) && this.mGraph.containsKey(e)) {
            final ArrayList list = this.mGraph.get(t);
            ArrayList<T> list2;
            if (list != null) {
                list2 = list;
            }
            else {
                final ArrayList<T> emptyList = this.getEmptyList();
                this.mGraph.put(t, emptyList);
                list2 = emptyList;
            }
            list2.add(e);
            return;
        }
        throw new IllegalArgumentException("All nodes must be present in the graph before being added as an edge");
    }
    
    void addNode(@NonNull final T t) {
        if (!this.mGraph.containsKey(t)) {
            this.mGraph.put(t, null);
        }
    }
    
    void clear() {
        for (int size = this.mGraph.size(), i = 0; i < size; ++i) {
            final ArrayList list = this.mGraph.valueAt(i);
            if (list != null) {
                this.poolList(list);
            }
        }
        this.mGraph.clear();
    }
    
    boolean contains(@NonNull final T t) {
        return this.mGraph.containsKey(t);
    }
    
    @Nullable
    List getIncomingEdges(@NonNull final T t) {
        return this.mGraph.get(t);
    }
    
    @Nullable
    List<T> getOutgoingEdges(@NonNull final T o) {
        ArrayList<T> list = null;
        ArrayList<T> list3;
        for (int size = this.mGraph.size(), i = 0; i < size; ++i, list = list3) {
            final ArrayList list2 = this.mGraph.valueAt(i);
            if (list2 == null) {
                list3 = list;
            }
            else {
                list3 = list;
                if (list2.contains(o)) {
                    if (list == null) {
                        list = new ArrayList<T>();
                    }
                    list.add(this.mGraph.keyAt(i));
                    list3 = list;
                }
            }
        }
        return list;
    }
    
    @NonNull
    ArrayList<T> getSortedList() {
        this.mSortResult.clear();
        this.mSortTmpMarked.clear();
        for (int i = 0; i < this.mGraph.size(); ++i) {
            this.dfs(this.mGraph.keyAt(i), this.mSortResult, this.mSortTmpMarked);
        }
        return this.mSortResult;
    }
    
    boolean hasOutgoingEdges(@NonNull final T o) {
        for (int size = this.mGraph.size(), i = 0; i < size; ++i) {
            final ArrayList list = this.mGraph.valueAt(i);
            if (list != null && list.contains(o)) {
                return true;
            }
        }
        return false;
    }
    
    int size() {
        return this.mGraph.size();
    }
}
