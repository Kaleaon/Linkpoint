package com.lumiyaviewer.lumiya.react;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class SubscriptionDataPool<K, T> extends SubscriptionGenericDataPool<K, T> {
    private final Map<K, SubscriptionList<K, T>> entries = new HashMap();

    @Override // com.lumiyaviewer.lumiya.react.SubscriptionGenericDataPool
    @Nullable
    protected SubscriptionList<K, T> getExistingSubscriptions(@Nonnull K k) {
        return this.entries.get(k);
    }

    @Override // com.lumiyaviewer.lumiya.react.SubscriptionGenericDataPool
    @Nonnull
    protected SubscriptionList<K, T> getSubscriptions(@Nonnull K k) {
        SubscriptionList<K, T> subscriptionList = this.entries.get(k);
        if (subscriptionList != null) {
            return subscriptionList;
        }
        SubscriptionList<K, T> subscriptionList2 = new SubscriptionList<>();
        this.entries.put(k, subscriptionList2);
        return subscriptionList2;
    }

    @Override // com.lumiyaviewer.lumiya.react.SubscriptionGenericDataPool
    public SubscriptionDataPool<K, T> setCanContainNulls(boolean z) {
        super.setCanContainNulls(z);
        return this;
    }
}
