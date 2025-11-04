package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class SubscriptionSingleDataPool<T> extends SubscriptionGenericDataPool<SubscriptionSingleKey, T> {
    private final SubscriptionList<SubscriptionSingleKey, T> entry = new SubscriptionList<>();

    public static SubscriptionSingleKey getSingleDataKey() {
        return SubscriptionSingleKey.Value;
    }

    public T getData() {
        return this.entry.getData();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.react.SubscriptionGenericDataPool
    @Nullable
    public SubscriptionList<SubscriptionSingleKey, T> getExistingSubscriptions(@Nonnull SubscriptionSingleKey subscriptionSingleKey) {
        return this.entry;
    }

    public SubscriptionSingleKey getKey() {
        return SubscriptionSingleKey.Value;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.react.SubscriptionGenericDataPool
    @Nonnull
    public SubscriptionList<SubscriptionSingleKey, T> getSubscriptions(@Nonnull SubscriptionSingleKey subscriptionSingleKey) {
        return this.entry;
    }
}
