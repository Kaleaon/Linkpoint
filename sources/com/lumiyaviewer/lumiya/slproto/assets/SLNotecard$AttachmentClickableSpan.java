// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.assets;

import android.text.style.ClickableSpan;
import android.view.View;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.assets:
//            SLNotecard

private static class clickListener extends ClickableSpan
    implements clickListener
{

    private r clickListener;
    private SLInventoryEntry entry;

    public SLInventoryEntry getEntry()
    {
        return entry;
    }

    public void onClick(View view)
    {
        if (clickListener != null)
        {
            clickListener.onAttachmentClick(entry);
        }
    }

    public r(SLInventoryEntry slinventoryentry, r r)
    {
        entry = slinventoryentry;
        clickListener = r;
    }
}
