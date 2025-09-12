// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.text.Editable;
import android.text.TextWatcher;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            ChatFragment

class this._cls0
    implements TextWatcher
{

    final ChatFragment this$0;

    public void afterTextChanged(Editable editable)
    {
    }

    public void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
    {
    }

    public void onTextChanged(CharSequence charsequence, int i, int j, int k)
    {
        if (charsequence.length() != 0)
        {
            ChatFragment._2D_wrap0(ChatFragment.this, true);
            return;
        } else
        {
            ChatFragment._2D_wrap0(ChatFragment.this, false);
            return;
        }
    }

    ()
    {
        this$0 = ChatFragment.this;
        super();
    }
}
