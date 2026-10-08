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
/* loaded from: classes.dex */
abstract class CollectionFuture<V, C> extends AggregateFuture<V, C> {

    /* loaded from: classes.dex */
    abstract class CollectionFutureRunningState extends AggregateFuture<V, C>.RunningState {
        private List<Optional<V>> values;

        /* JADX INFO: Access modifiers changed from: package-private */
        public CollectionFutureRunningState(ImmutableCollection<? extends ListenableFuture<? extends V>> immutableCollection, boolean z) {
            super(immutableCollection, z, true);
            this.values = !immutableCollection.isEmpty() ? Lists.newArrayListWithCapacity(immutableCollection.size()) : ImmutableList.of();
            for (int i = 0; i < immutableCollection.size(); i++) {
                this.values.add(null);
            }
        }

        @Override // com.google.common.util.concurrent.AggregateFuture.RunningState
        final void collectOneValue(boolean z, int i, @Nullable V v) {
            List<Optional<V>> list = this.values;
            if (list == null) {
                Preconditions.checkState(z || CollectionFuture.this.isCancelled(), "Future was done before all dependencies completed");
            } else {
                list.set(i, Optional.fromNullable(v));
            }
        }

        abstract C combine(List<Optional<V>> list);

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.google.common.util.concurrent.AggregateFuture.RunningState
        final void handleAllCompleted() {
            List<Optional<V>> list = this.values;
            if (list == null) {
                Preconditions.checkState(CollectionFuture.this.isDone());
            } else {
                CollectionFuture.this.set(combine(list));
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.google.common.util.concurrent.AggregateFuture.RunningState
        public void releaseResourcesAfterFailure() {
            super.releaseResourcesAfterFailure();
            this.values = null;
        }
    }
}
