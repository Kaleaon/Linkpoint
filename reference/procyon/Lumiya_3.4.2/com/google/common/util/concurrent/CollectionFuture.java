// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import java.util.RandomAccess;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableCollection;
import com.google.common.base.Optional;
import java.util.List;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
abstract class CollectionFuture<V, C> extends AggregateFuture<V, C>
{
    abstract class CollectionFutureRunningState extends RunningState
    {
        private List<Optional<V>> values;
        
        CollectionFutureRunningState(final ImmutableCollection<? extends ListenableFuture<? extends V>> collection, final boolean b) {
            super((ImmutableCollection<? extends ListenableFuture<? extends InputT>>)collection, b, true);
            RandomAccess randomAccess;
            if (!collection.isEmpty()) {
                randomAccess = Lists.newArrayListWithCapacity(collection.size());
            }
            else {
                randomAccess = ImmutableList.of();
            }
            this.values = (List<Optional<V>>)randomAccess;
            for (int i = 0; i < collection.size(); ++i) {
                this.values.add(null);
            }
        }
        
        @Override
        final void collectOneValue(final boolean b, final int n, @Nullable final V v) {
            final boolean b2 = false;
            final List<Optional<V>> values = this.values;
            if (values == null) {
                Preconditions.checkState(b || CollectionFuture.this.isCancelled() || b2, (Object)"Future was done before all dependencies completed");
            }
            else {
                values.set(n, Optional.fromNullable(v));
            }
        }
        
        abstract C combine(final List<Optional<V>> p0);
        
        @Override
        final void handleAllCompleted() {
            final List<Optional<V>> values = this.values;
            if (values == null) {
                Preconditions.checkState(CollectionFuture.this.isDone());
            }
            else {
                CollectionFuture.this.set(this.combine(values));
            }
        }
        
        @Override
        void releaseResourcesAfterFailure() {
            super.releaseResourcesAfterFailure();
            this.values = null;
        }
    }
}
