package com.lumiyaviewer.lumiya.ui.chat.contacts;
import java.util.*;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayData;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.io.Closeable;
import java.io.IOException;

public class ChatterListSubscriptionAdapter extends ChatterListSimpleAdapter implements Subscription.OnData<ImmutableList<ChatterDisplayData>>, Closeable {
    private final Predicate<ChatterDisplayData> predicate;
    private final Subscription<ChatterListType, ImmutableList<ChatterDisplayData>> subscription;

    public ChatterListSubscriptionAdapter(Context context, UserManager userManager, ChatterListType chatterListType) {
        super(context, userManager);
        this.predicate = null;
        this.subscription = userManager.getChatterList().getChatterList().subscribe(chatterListType, UIThreadExecutor.getInstance(), this);
    }

    public ChatterListSubscriptionAdapter(Context context, UserManager userManager, ChatterListType chatterListType, Predicate<ChatterDisplayData> predicate2) {
        super(context, userManager);
        this.predicate = predicate2;
        this.subscription = userManager.getChatterList().getChatterList().subscribe(chatterListType, UIThreadExecutor.getInstance(), this);
    }

    public /* bridge */ /* synthetic */ boolean areAllItemsEnabled() {
        return super.areAllItemsEnabled();
    }

    public void close() throws IOException {
        this.subscription.unsubscribe();
    }

    public /* bridge */ /* synthetic */ int getCount() {
        return super.getCount();
    }

    public /* bridge */ /* synthetic */ Object getItem(int i) {
        return super.getItem(i);
    }

    public /* bridge */ /* synthetic */ long getItemId(int i) {
        return super.getItemId(i);
    }

    public /* bridge */ /* synthetic */ View getView(int i, View view, ViewGroup viewGroup) {
        return super.getView(i, view, viewGroup);
    }

    public /* bridge */ /* synthetic */ boolean hasStableIds() {
        return super.hasStableIds();
    }

    public /* bridge */ /* synthetic */ boolean isEmpty() {
        return super.isEmpty();
    }

    public /* bridge */ /* synthetic */ boolean isEnabled(int i) {
        return super.isEnabled(i);
    }

    public void onData(ImmutableList<ChatterDisplayData> immutableList) {
        if (this.predicate == null) {
            setData(immutableList);
        } else {
            setData(ImmutableList.copyOf(Iterables.filter(immutableList, this.predicate)));
        }
    }
}
