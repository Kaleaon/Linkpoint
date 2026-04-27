// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.search;

import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;

public class Count
    implements Unbinder
{

    private Count target;

    public void unbind()
    {
        Count count = target;
        if (count == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            count.me = null;
            count.me = null;
            count.Count = null;
            return;
        }
    }

    public ( , View view)
    {
        target = ;
        .me = (TextView)Utils.findRequiredViewAsType(view, 0x7f10027f, "field 'resultItemName'", android/widget/TextView);
        .me = (ChatterPicView)Utils.findRequiredViewAsType(view, 0x7f10013f, "field 'userPicView'", com/lumiyaviewer/lumiya/ui/chat/ChatterPicView);
        .Count = (TextView)Utils.findRequiredViewAsType(view, 0x7f100280, "field 'resultMemberCount'", android/widget/TextView);
    }
}
