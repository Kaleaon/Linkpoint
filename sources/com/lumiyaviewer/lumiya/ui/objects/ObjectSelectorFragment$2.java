// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objects;

import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.objects:
//            ObjectSelectorFragment

class this._cls0
    implements android.support.v4.view.pandListener
{

    final ObjectSelectorFragment this$0;

    public boolean onMenuItemActionCollapse(MenuItem menuitem)
    {
        menuitem = getView();
        if (menuitem != null)
        {
            menuitem.findViewById(0x7f100229).setVisibility(8);
            menuitem = menuitem.findViewById(0x7f100229).getAnimation();
            if (menuitem != null)
            {
                menuitem.cancel();
            }
        }
        ObjectSelectorFragment._2D_wrap0(ObjectSelectorFragment.this);
        return true;
    }

    public boolean onMenuItemActionExpand(MenuItem menuitem)
    {
        menuitem = getView();
        if (menuitem != null)
        {
            menuitem.findViewById(0x7f100229).setVisibility(0);
            Animation animation = AnimationUtils.loadAnimation(getContext(), 0x7f05000f);
            menuitem.findViewById(0x7f100229).startAnimation(animation);
        }
        ObjectSelectorFragment._2D_wrap0(ObjectSelectorFragment.this);
        return true;
    }

    r()
    {
        this$0 = ObjectSelectorFragment.this;
        super();
    }
}
