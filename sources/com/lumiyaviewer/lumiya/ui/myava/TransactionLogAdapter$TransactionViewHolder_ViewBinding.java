// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.myava;

import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;

public class TextView
    implements Unbinder
{

    private TextView target;

    public void unbind()
    {
        TextView textview = target;
        if (textview == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            textview.target = null;
            textview.target = null;
            textview.tView = null;
            textview.ew = null;
            textview.TextView = null;
            return;
        }
    }

    public ( , View view)
    {
        target = ;
        .target = (TextView)Utils.findRequiredViewAsType(view, 0x7f10029e, "field 'userName'", android/widget/TextView);
        .target = (ChatterPicView)Utils.findRequiredViewAsType(view, 0x7f10013f, "field 'userPicView'", com/lumiyaviewer/lumiya/ui/chat/ChatterPicView);
        .tView = (TextView)Utils.findRequiredViewAsType(view, 0x7f10029d, "field 'timestampTextView'", android/widget/TextView);
        .ew = (TextView)Utils.findRequiredViewAsType(view, 0x7f10029f, "field 'amountTextView'", android/widget/TextView);
        .TextView = (TextView)Utils.findRequiredViewAsType(view, 0x7f1002a0, "field 'finalBalanceTextView'", android/widget/TextView);
    }
}
