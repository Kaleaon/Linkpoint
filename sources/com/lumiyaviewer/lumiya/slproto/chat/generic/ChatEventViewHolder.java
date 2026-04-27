// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.chat.HasUserPicClickHandler;
import java.lang.ref.WeakReference;

public class ChatEventViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder
{
    static interface Factory
    {

        public abstract ChatEventViewHolder createViewHolder(View view, android.support.v7.widget.RecyclerView.Adapter adapter1);
    }


    protected final WeakReference adapter;
    final View bubbleView;
    final ChatterPicView chatSourceIcon;
    final ChatterPicView chatSourceIconRight;
    final TextView textView;
    final TextView timestampView;
    private long updateTimestamp;

    public ChatEventViewHolder(View view, android.support.v7.widget.RecyclerView.Adapter adapter1)
    {
        super(view);
        updateTimestamp = 0L;
        adapter = new WeakReference(adapter1);
        timestampView = (TextView)view.findViewById(0x7f100125);
        textView = (TextView)view.findViewById(0x7f100124);
        bubbleView = view.findViewById(0x7f100123);
        chatSourceIcon = (ChatterPicView)view.findViewById(0x7f100122);
        chatSourceIconRight = (ChatterPicView)view.findViewById(0x7f100126);
        if (adapter1 instanceof HasUserPicClickHandler)
        {
            view = ((HasUserPicClickHandler)adapter1).getUserPicClickListener();
            if (view != null)
            {
                if (chatSourceIcon != null)
                {
                    chatSourceIcon.setOnClickListener(view);
                }
                if (chatSourceIconRight != null)
                {
                    chatSourceIconRight.setOnClickListener(view);
                }
            }
        }
    }

    void requestAdapterUpdate()
    {
        android.support.v7.widget.RecyclerView.Adapter adapter1 = (android.support.v7.widget.RecyclerView.Adapter)adapter.get();
        if (adapter1 != null)
        {
            adapter1.notifyItemChanged(getAdapterPosition());
        }
    }

    void setupTimestampUpdate(Context context, long l)
    {
        updateTimestamp = l;
        updateTimestamp(context);
    }

    public void updateTimestamp(Context context)
    {
label0:
        {
            if (timestampView != null)
            {
                if (updateTimestamp == 0L)
                {
                    break label0;
                }
                long l = System.currentTimeMillis();
                if (l < updateTimestamp + 60000L)
                {
                    context = context.getString(0x7f09020e);
                } else
                {
                    context = DateUtils.getRelativeTimeSpanString(updateTimestamp, l, 60000L, 0x40000);
                }
                timestampView.setText(context);
                timestampView.setVisibility(0);
            }
            return;
        }
        timestampView.setVisibility(8);
    }
}
