// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterItemViewBuilder;

public interface ChatterDisplayInfo
{

    public abstract void buildView(Context context, ChatterItemViewBuilder chatteritemviewbuilder, UserManager usermanager);

    public abstract ChatterID getChatterID(UserManager usermanager);

    public abstract String getDisplayName();
}
