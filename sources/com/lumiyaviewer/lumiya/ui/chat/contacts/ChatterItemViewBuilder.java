// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.contacts;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.chat.TypingIndicatorView;

public class ChatterItemViewBuilder
{

    private float distance;
    private boolean distanceSet;
    private boolean isActiveGroup;
    private boolean isOnline;
    private String label;
    private String lastMessage;
    private boolean onlineVisible;
    private ChatterID thumbnailChatterID;
    private int thumbnailDefaultIcon;
    private String thumbnailLabel;
    private int unreadCount;
    private boolean voiceActive;

    public ChatterItemViewBuilder()
    {
        onlineVisible = false;
        distanceSet = false;
        reset();
    }

    public View getView(LayoutInflater layoutinflater, View view, ViewGroup viewgroup, boolean flag)
    {
label0:
        {
label1:
            {
label2:
                {
                    int j = 0x7f100142;
                    byte byte0 = 8;
                    int i;
                    if (view == null || view.getId() != 0x7f10013e)
                    {
                        view = null;
                    }
                    if (view == null)
                    {
                        layoutinflater = layoutinflater.inflate(0x7f04002c, viewgroup, false);
                    } else
                    {
                        layoutinflater = view;
                    }
                    if (layoutinflater == null)
                    {
                        break label0;
                    }
                    ((TextView)layoutinflater.findViewById(0x7f100140)).setText(label);
                    view = layoutinflater.findViewById(0x7f100145);
                    if (view != null)
                    {
                        if (onlineVisible)
                        {
                            view.setVisibility(0);
                        } else
                        {
                            view.setVisibility(8);
                        }
                    }
                    view = layoutinflater.findViewById(0x7f100143);
                    if (view != null)
                    {
                        if (voiceActive)
                        {
                            i = 0;
                        } else
                        {
                            i = 8;
                        }
                        view.setVisibility(i);
                    }
                    if (flag)
                    {
                        i = 0x7f100142;
                    } else
                    {
                        i = 0x7f100147;
                    }
                    viewgroup = (TextView)layoutinflater.findViewById(i);
                    if (viewgroup != null)
                    {
                        if (distanceSet)
                        {
                            if (distance >= 9.5F)
                            {
                                view = Integer.toString(Math.round(distance));
                            } else
                            {
                                view = String.format("%.1f", new Object[] {
                                    Float.valueOf(distance)
                                });
                            }
                            viewgroup.setText((new StringBuilder()).append(view).append(" m").toString());
                            if (distance <= 20F)
                            {
                                viewgroup.setTypeface(viewgroup.getTypeface(), 1);
                            } else
                            {
                                viewgroup.setTypeface(Typeface.create(viewgroup.getTypeface(), 0));
                            }
                            viewgroup.setVisibility(0);
                        } else
                        {
                            viewgroup.setText(null);
                            if (flag)
                            {
                                i = 8;
                            } else
                            {
                                i = 4;
                            }
                            viewgroup.setVisibility(i);
                        }
                    }
                    if (!flag)
                    {
                        i = j;
                    } else
                    {
                        i = 0x7f100147;
                    }
                    view = layoutinflater.findViewById(i);
                    if (view != null)
                    {
                        view.setVisibility(8);
                    }
                    view = (TextView)layoutinflater.findViewById(0x7f100144);
                    if (view != null)
                    {
                        view.setText(Integer.toString(unreadCount));
                        if (unreadCount != 0)
                        {
                            view.setVisibility(0);
                        } else
                        {
                            view.setVisibility(8);
                        }
                    }
                    view = (TextView)layoutinflater.findViewById(0x7f100141);
                    if (view != null)
                    {
                        if (lastMessage != null)
                        {
                            view.setText(lastMessage);
                            view.setVisibility(0);
                        } else
                        {
                            view.setVisibility(8);
                        }
                    }
                    view = layoutinflater.findViewById(0x7f100146);
                    if (view != null)
                    {
                        if (isActiveGroup)
                        {
                            i = 0;
                        } else
                        {
                            i = 8;
                        }
                        view.setVisibility(i);
                    }
                    view = (ChatterPicView)layoutinflater.findViewById(0x7f10013f);
                    if (view == null)
                    {
                        break label1;
                    }
                    view.setDefaultIcon(thumbnailDefaultIcon, false);
                    view.setChatterID(thumbnailChatterID, thumbnailLabel);
                    if (thumbnailChatterID == null)
                    {
                        i = byte0;
                        if (thumbnailDefaultIcon == -1)
                        {
                            break label2;
                        }
                    }
                    i = 0;
                }
                view.setVisibility(i);
            }
            view = (TypingIndicatorView)layoutinflater.findViewById(0x7f100120);
            if (view != null)
            {
                view.setChatterID(thumbnailChatterID);
            }
        }
        return layoutinflater;
    }

    public void reset()
    {
        label = null;
        onlineVisible = false;
        distanceSet = false;
        unreadCount = 0;
        lastMessage = null;
        isActiveGroup = false;
        thumbnailChatterID = null;
        thumbnailLabel = null;
        thumbnailDefaultIcon = -1;
        voiceActive = false;
    }

    public void setActiveGroup(boolean flag)
    {
        isActiveGroup = flag;
    }

    public void setDistance(float f)
    {
        if (Float.isNaN(f))
        {
            distanceSet = false;
            return;
        } else
        {
            distanceSet = true;
            distance = f;
            return;
        }
    }

    public void setLabel(String s)
    {
        label = s;
    }

    public void setLastMessage(String s)
    {
        lastMessage = s;
    }

    public void setOnlineStatusIcon(boolean flag, boolean flag1)
    {
        onlineVisible = flag;
        isOnline = flag1;
    }

    public void setThumbnailChatterID(ChatterID chatterid, String s)
    {
        thumbnailChatterID = chatterid;
        thumbnailLabel = s;
    }

    public void setThumbnailDefaultIcon(int i)
    {
        thumbnailDefaultIcon = i;
    }

    public void setUnreadCount(int i)
    {
        unreadCount = i;
    }

    public void setVoiceActive(boolean flag)
    {
        voiceActive = flag;
    }
}
