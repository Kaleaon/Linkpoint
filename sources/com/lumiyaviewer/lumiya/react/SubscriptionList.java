package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.react.Subscription;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public class SubscriptionList<K, T> {
    private T lastData = null;
    private Throwable lastError = null;
    private final Set<Subscription.SubscriptionReference<K, T>> subscriptions = new HashSet();

    public final void addSubscription(Subscription<K, T> subscription) {
        this.subscriptions.add(subscription.getReference());
    }

    public final T getData() {
        return this.lastData;
    }

    public final Throwable getError() {
        return this.lastError;
    }

    @Nullable
    public final List<Subscription<K, T>> getSubscriptions(boolean z) {
        ArrayList arrayList = null;
        Iterator<Subscription.SubscriptionReference<K, T>> it = this.subscriptions.iterator();
        while (true) {
            ArrayList arrayList2 = arrayList;
            if (!it.hasNext()) {
                return arrayList2;
            }
            Subscription subscription = (Subscription) it.next().get();
            if (subscription != null) {
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList(this.subscriptions.size());
                }
                arrayList2.add(subscription);
            } else if (z) {
                it.remove();
            }
            arrayList = arrayList2;
        }
    }

    public final boolean isEmpty() {
        return this.subscriptions.isEmpty();
    }

    public final void removeByReference(Subscription.SubscriptionReference subscriptionReference) {
        this.subscriptions.remove(subscriptionReference);
    }

    public final void removeSubscription(Subscription<K, T> subscription) {
        this.subscriptions.remove(subscription.getReference());
    }

    public final void setData(T t) {
        this.lastData = t;
        this.lastError = null;
    }

    public final void setError(Throwable th) {
        this.lastData = null;
        this.lastError = th;
    }
}
