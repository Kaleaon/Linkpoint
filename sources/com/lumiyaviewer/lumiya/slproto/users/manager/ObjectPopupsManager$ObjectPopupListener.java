// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ObjectPopupsManager

public static interface 
{

    public abstract void onNewObjectPopup(SLChatEvent slchatevent);

    public abstract void onObjectPopupCountChanged(int i);
}
