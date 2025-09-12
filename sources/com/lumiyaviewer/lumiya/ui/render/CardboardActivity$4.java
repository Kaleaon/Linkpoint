// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.view.View;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatScriptDialog;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            CardboardActivity

class this._cls0
    implements android.view.
{

    final CardboardActivity this$0;

    public void onClick(View view)
    {
        if (CardboardActivity._2D_get0(CardboardActivity.this) == null) goto _L2; else goto _L1
_L1:
        int i = 0;
_L5:
        if (i >= CardboardActivity._2D_get5().length)
        {
            break MISSING_BLOCK_LABEL_85;
        }
        if (view.getId() != CardboardActivity._2D_get5()[i]) goto _L4; else goto _L3
_L3:
        CardboardActivity._2D_get0(CardboardActivity.this).onDialogButton(CardboardActivity._2D_get29(CardboardActivity.this), i);
        CardboardActivity._2D_set0(CardboardActivity.this, null);
_L2:
        CardboardActivity._2D_wrap2(CardboardActivity.this, null);
        CardboardActivity._2D_wrap6(CardboardActivity.this, ntrolsPage.pageDefault);
        return;
_L4:
        i++;
          goto _L5
        i = -1;
          goto _L3
    }

    ntrolsPage()
    {
        this$0 = CardboardActivity.this;
        super();
    }
}
