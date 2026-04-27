// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.myava;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.myava:
//            MyAvatarFragment

public class MyAvatarFragment_ViewBinding
    implements Unbinder
{

    private MyAvatarFragment target;

    public MyAvatarFragment_ViewBinding(MyAvatarFragment myavatarfragment, View view)
    {
        target = myavatarfragment;
        myavatarfragment.myAvatarPic = (ChatterPicView)Utils.findRequiredViewAsType(view, 0x7f1001ea, "field 'myAvatarPic'", com/lumiyaviewer/lumiya/ui/chat/ChatterPicView);
        myavatarfragment.myAvatarName = (TextView)Utils.findRequiredViewAsType(view, 0x7f1001e9, "field 'myAvatarName'", android/widget/TextView);
        myavatarfragment.myAvatarOptionsList = (ListView)Utils.findRequiredViewAsType(view, 0x7f1001eb, "field 'myAvatarOptionsList'", android/widget/ListView);
    }

    public void unbind()
    {
        MyAvatarFragment myavatarfragment = target;
        if (myavatarfragment == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            myavatarfragment.myAvatarPic = null;
            myavatarfragment.myAvatarName = null;
            myavatarfragment.myAvatarOptionsList = null;
            return;
        }
    }
}
