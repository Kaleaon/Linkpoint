// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.myava;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.myava:
//            MyAvatarActivity, MyProfileFragment

class this._cls0
    implements FragmentActivityFactory
{

    final MyAvatarActivity this$0;

    public Intent createIntent(Context context, Bundle bundle)
    {
        return null;
    }

    public Class getFragmentClass()
    {
        return com/lumiyaviewer/lumiya/ui/myava/MyProfileFragment;
    }

    ctory()
    {
        this$0 = MyAvatarActivity.this;
        super();
    }
}
