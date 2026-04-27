// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.view.View;
import com.lumiyaviewer.lumiya.Debug;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            OnHoverListenerCompat, CardboardActivity

class this._cls0
    implements OnHoverListenerCompat
{

    final CardboardActivity this$0;

    public boolean onHoverEnter(View view)
    {
        view.setAlpha(1.0F);
        Debug.Printf("Cardboard: hovering enter %d", new Object[] {
            Integer.valueOf(view.getId())
        });
        CardboardActivity._2D_set2(CardboardActivity.this, view);
        CardboardActivity._2D_wrap5(CardboardActivity.this);
        return false;
    }

    public boolean onHoverExit(View view)
    {
        view.setAlpha(0.5F);
        Debug.Printf("Cardboard: hovering exit %d", new Object[] {
            Integer.valueOf(view.getId())
        });
        if (CardboardActivity._2D_get13(CardboardActivity.this) == view)
        {
            CardboardActivity._2D_set2(CardboardActivity.this, null);
        }
        CardboardActivity._2D_wrap5(CardboardActivity.this);
        return false;
    }

    t()
    {
        this$0 = CardboardActivity.this;
        super();
    }
}
