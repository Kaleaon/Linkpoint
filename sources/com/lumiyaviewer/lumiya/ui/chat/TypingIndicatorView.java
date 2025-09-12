// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;

public class TypingIndicatorView extends ImageView
{

    private ChatterID chatterID;
    private Subscription subscription;

    public TypingIndicatorView(Context context)
    {
        super(context);
        chatterID = null;
        subscription = null;
    }

    public TypingIndicatorView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        chatterID = null;
        subscription = null;
    }

    public TypingIndicatorView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        chatterID = null;
        subscription = null;
    }

    public TypingIndicatorView(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
        chatterID = null;
        subscription = null;
    }

    private void onUserTypingStatus(Boolean boolean1)
    {
        if (boolean1 != null && subscription != null && (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser))
        {
            int i;
            if (boolean1.booleanValue() && getVisibility() != 0)
            {
                ((AnimationDrawable)getDrawable()).start();
            } else
            if (!boolean1.booleanValue() && getVisibility() == 0)
            {
                ((AnimationDrawable)getDrawable()).stop();
            }
            if (boolean1.booleanValue())
            {
                i = 0;
            } else
            {
                i = 4;
            }
            setVisibility(i);
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_TypingIndicatorView_2D_mthref_2D_0(Boolean boolean1)
    {
        onUserTypingStatus(boolean1);
    }

    public void setChatterID(ChatterID chatterid)
    {
        if (!Objects.equal(chatterid, chatterID))
        {
            chatterID = chatterid;
            if (subscription != null)
            {
                subscription.unsubscribe();
                subscription = null;
            }
            if ((chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) && chatterid.getUserManager() != null)
            {
                subscription = chatterid.getUserManager().getChatterList().getUserTypingStatus().subscribe(((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterid).getChatterUUID(), UIThreadExecutor.getInstance(), new _2D_.Lambda.XDRgkFjV_2D_FoS0WpW8v6lPNgts7Q(this));
            }
            if (subscription == null)
            {
                setVisibility(4);
            }
        }
    }
}
