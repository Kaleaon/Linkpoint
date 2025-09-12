// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.assets;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.assets:
//            SLNotecard

private static class dableTextForLink extends ReplacementSpan
    implements pan
{

    private SLInventoryEntry entry;
    private String linkText;

    public void draw(Canvas canvas, CharSequence charsequence, int i, int j, float f, int k, int l, 
            int i1, Paint paint)
    {
        if (i != j)
        {
            charsequence = new Paint(paint);
            charsequence.setUnderlineText(true);
            charsequence.setColor(Color.rgb(0, 50, 100));
            canvas.drawText(linkText, 0, linkText.length(), f, l, charsequence);
        }
    }

    public SLInventoryEntry getEntry()
    {
        return entry;
    }

    public int getSize(Paint paint, CharSequence charsequence, int i, int j, android.graphics.tecard.AttachmentSpan attachmentspan)
    {
        if (attachmentspan != null)
        {
            charsequence = paint.getFontMetricsInt();
            attachmentspan.t = ((android.graphics.t) (charsequence)).t;
            attachmentspan.m = ((android.graphics.m) (charsequence)).m;
            attachmentspan.nt = ((android.graphics.nt) (charsequence)).nt;
            attachmentspan.ng = ((android.graphics.ng) (charsequence)).ng;
            attachmentspan.ng = ((android.graphics.ng) (charsequence)).ng;
        }
        if (i != j)
        {
            return (int)paint.measureText(linkText, 0, linkText.length());
        } else
        {
            return 0;
        }
    }

    public pan(SLInventoryEntry slinventoryentry)
    {
        entry = slinventoryentry;
        linkText = slinventoryentry.getReadableTextForLink();
    }
}
