// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.content.Context;
import android.widget.TextView;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;

public class ChatterNameDisplayer
    implements com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
{

    private boolean alreadyUpdated;
    private ChatterID chatterID;
    private ChatterNameRetriever nameRetriever;
    private TextView nameTextView;
    private ChatterPicView picView;

    public ChatterNameDisplayer()
    {
        nameRetriever = null;
        chatterID = null;
        nameTextView = null;
        picView = null;
        alreadyUpdated = false;
    }

    private void clearViews()
    {
        if (nameTextView != null)
        {
            nameTextView.setText("");
        }
        if (picView != null)
        {
            picView.setChatterID(null, null);
        }
    }

    private void updateViews()
    {
        if (chatterID != null && nameRetriever != null)
        {
            String s1 = nameRetriever.getResolvedName();
            if (nameTextView != null)
            {
                TextView textview = nameTextView;
                String s;
                if (s1 != null)
                {
                    s = s1;
                } else
                {
                    s = nameTextView.getContext().getString(0x7f0901c8);
                }
                textview.setText(s);
            }
            if (picView != null)
            {
                picView.setChatterID(chatterID, s1);
            }
            return;
        } else
        {
            clearViews();
            return;
        }
    }

    public void bindViews(TextView textview, ChatterPicView chatterpicview)
    {
        nameTextView = textview;
        picView = chatterpicview;
        updateViews();
    }

    public ChatterID getChatterID()
    {
        return chatterID;
    }

    public String getResolvedName(Context context)
    {
        String s = null;
        if (nameRetriever != null)
        {
            s = nameRetriever.getResolvedName();
        }
        if (s != null)
        {
            return s;
        } else
        {
            return context.getString(0x7f0901c8);
        }
    }

    public void onChatterNameUpdated(ChatterNameRetriever chatternameretriever)
    {
        if (chatternameretriever == nameRetriever)
        {
            alreadyUpdated = true;
            updateViews();
        }
    }

    public void setChatterID(ChatterID chatterid)
    {
label0:
        {
            if (!Objects.equal(chatterid, chatterID))
            {
                if (nameRetriever != null)
                {
                    nameRetriever.dispose();
                    nameRetriever = null;
                }
                chatterID = chatterid;
                if (chatterid == null)
                {
                    break label0;
                }
                alreadyUpdated = false;
                nameRetriever = new ChatterNameRetriever(chatterid, this, UIThreadExecutor.getInstance(), false);
                nameRetriever.subscribe();
                if (!alreadyUpdated)
                {
                    if (nameTextView != null)
                    {
                        nameTextView.setText(0x7f0901c8);
                    }
                    if (picView != null)
                    {
                        picView.setChatterID(null, null);
                    }
                }
            }
            return;
        }
        clearViews();
    }

    public void unbindViews()
    {
        nameTextView = null;
        picView = null;
    }
}
