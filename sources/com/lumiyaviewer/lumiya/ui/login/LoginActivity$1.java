// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.login;

import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import com.lumiyaviewer.lumiya.ui.grids.GridEditDialog;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.login:
//            LoginActivity

class this._cls0
    implements android.widget.emSelectedListener
{

    final LoginActivity this$0;

    public void onItemSelected(AdapterView adapterview, View view, int i, long l)
    {
label0:
        {
            if (i != LoginActivity._2D_get1(LoginActivity.this))
            {
                adapterview = ((AdapterView) (adapterview.getAdapter().getItem(i)));
                if (adapterview instanceof com.lumiyaviewer.lumiya.ui.grids.o)
                {
                    adapterview = (com.lumiyaviewer.lumiya.ui.grids.o)adapterview;
                    if (adapterview.getLoginURL() != null)
                    {
                        break label0;
                    }
                    adapterview = new GridEditDialog(LoginActivity.this, LoginActivity._2D_get0(LoginActivity.this), null);
                    adapterview.setOnGridEditResultListener(LoginActivity.this);
                    adapterview.show();
                }
            }
            return;
        }
        LoginActivity._2D_set0(LoginActivity.this, i);
        LoginActivity._2D_set1(LoginActivity.this, adapterview.getGridUUID());
    }

    public void onNothingSelected(AdapterView adapterview)
    {
    }

    o()
    {
        this$0 = LoginActivity.this;
        super();
    }
}
