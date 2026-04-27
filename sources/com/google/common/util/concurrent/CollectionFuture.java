package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible
abstract class CollectionFuture<V, C> extends AggregateFuture<V, C> {

    abstract class CollectionFutureRunningState extends AggregateFuture<V, C>.RunningState {
        private List<Optional<V>> values;

        CollectionFutureRunningState(ImmutableCollection<? extends ListenableFuture<? extends V>> immutableCollection, boolean z) {
            super(immutableCollection, z, true);
            this.values = !immutableCollection.isEmpty() ? Lists.newArrayListWithCapacity(immutableCollection.size()) : ImmutableList.of();
            for (int i = 0; i < immutableCollection.size(); i++) {
                this.values.add((Object) null);
            }
        }

        /* access modifiers changed from: package-private */
        public final void collectOneValue(boolean z, int i, @Nullable V v) {
            boolean z2 = false;
            List<Optional<V>> list = this.values;
            if (list == null) {
                if (z || CollectionFuture.this.isCancelled()) {
                    z2 = true;
                }
                Preconditions.checkState(z2, "Future was done before all dependencies completed");
                return;
            }
            list.set(i, Optional.fromNullable(v));
        }

        /* access modifiers changed from: package-private */
        public abstract C combine(List<Optional<V>> list);

        /* access modifiers changed from: package-private */
        public final void handleAllCompleted() {
            List<Optional<V>> list = this.values;
            if (list == null) {
                Preconditions.checkState(CollectionFuture.this.isDone());
            } else {
                CollectionFuture.this.set(combine(list));
            }
        }

        /* access modifiers changed from: package-private */
        public void releaseResourcesAfterFailure() {
            super.releaseResourcesAfterFailure();
            this.values = null;
        }
    }

    CollectionFuture() {
    }
}
