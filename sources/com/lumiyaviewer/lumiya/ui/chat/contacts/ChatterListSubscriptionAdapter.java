// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.contacts;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.io.Closeable;
import java.io.IOException;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.contacts:
//            ChatterListSimpleAdapter

public class ChatterListSubscriptionAdapter extends ChatterListSimpleAdapter
    implements com.lumiyaviewer.lumiya.react.Subscription.OnData, Closeable
{

    private final Predicate predicate;
    private final Subscription subscription;

    public ChatterListSubscriptionAdapter(Context context, UserManager usermanager, ChatterListType chatterlisttype)
    {
        super(context, usermanager);
        predicate = null;
        subscription = usermanager.getChatterList().getChatterList().subscribe(chatterlisttype, UIThreadExecutor.getInstance(), this);
    }

    public ChatterListSubscriptionAdapter(Context context, UserManager usermanager, ChatterListType chatterlisttype, Predicate predicate1)
    {
        super(context, usermanager);
        predicate = predicate1;
        subscription = usermanager.getChatterList().getChatterList().subscribe(chatterlisttype, UIThreadExecutor.getInstance(), this);
    }

    public volatile boolean areAllItemsEnabled()
    {
        return super.areAllItemsEnabled();
    }

    public void close()
        throws IOException
    {
        subscription.unsubscribe();
    }

    public volatile int getCount()
    {
        return super.getCount();
    }

    public volatile Object getItem(int i)
    {
        return super.getItem(i);
    }

    public volatile long getItemId(int i)
    {
        return super.getItemId(i);
    }

    public volatile View getView(int i, View view, ViewGroup viewgroup)
    {
        return super.getView(i, view, viewgroup);
    }

    public volatile boolean hasStableIds()
    {
        return super.hasStableIds();
    }

    public volatile boolean isEmpty()
    {
        return super.isEmpty();
    }

    public volatile boolean isEnabled(int i)
    {
        return super.isEnabled(i);
    }

    public void onData(ImmutableList immutablelist)
    {
        if (predicate == null)
        {
            setData(immutablelist);
            return;
        } else
        {
            setData(ImmutableList.copyOf(Iterables.filter(immutablelist, predicate)));
            return;
        }
    }

    public volatile void onData(Object obj)
    {
        onData((ImmutableList)obj);
    }
}
