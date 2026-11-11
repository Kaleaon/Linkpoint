package butterknife.internal;

import java.util.AbstractList;
import java.util.RandomAccess;
/* loaded from: classes.dex */
final class ImmutableList<T> extends AbstractList<T> implements RandomAccess {
    private final T[] views;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ImmutableList(T[] tArr) {
        this.views = tArr;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean contains(Object obj) {
        for (T t : this.views) {
            if (t == obj) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.AbstractList, java.util.List
    public T get(int i) {
        return this.views[i];
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.views.length;
    }
}
