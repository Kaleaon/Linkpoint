// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.contacts;

import com.google.common.collect.Iterables;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
import android.view.ViewGroup;
import android.view.View;
import java.io.IOException;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType;
import com.google.common.base.Predicate;
import java.io.Closeable;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayData;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.react.Subscription;

public class ChatterListSubscriptionAdapter extends ChatterListSimpleAdapter implements OnData<ImmutableList<ChatterDisplayData>>, Closeable
{
    private final Predicate<ChatterDisplayData> predicate;
    private final Subscription<ChatterListType, ImmutableList<ChatterDisplayData>> subscription;
    
    public ChatterListSubscriptionAdapter(final Context context, final UserManager userManager, final ChatterListType chatterListType) {
        super(context, userManager);
        this.predicate = null;
        this.subscription = userManager.getChatterList().getChatterList().subscribe(chatterListType, UIThreadExecutor.getInstance(), this);
    }
    
    public ChatterListSubscriptionAdapter(final Context context, final UserManager userManager, final ChatterListType chatterListType, final Predicate<ChatterDisplayData> predicate) {
        super(context, userManager);
        this.predicate = predicate;
        this.subscription = userManager.getChatterList().getChatterList().subscribe(chatterListType, UIThreadExecutor.getInstance(), this);
    }
    
    @Override
    public void close() throws IOException {
        this.subscription.unsubscribe();
    }
    
    public void onData(final ImmutableList<ChatterDisplayData> data) {
        if (this.predicate == null) {
            this.setData(data);
        }
        else {
            this.setData(ImmutableList.copyOf((Iterable<? extends ChatterDisplayInfo>)Iterables.filter((Iterable<? extends E>)data, (Predicate<? super E>)this.predicate)));
        }
    }
}
