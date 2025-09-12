// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.os.Handler;
import android.os.SystemClock;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            WorldViewActivity

class this._cls0
    implements Runnable
{

    final WorldViewActivity this$0;

    public void run()
    {
        long l;
label0:
        {
            WorldViewActivity._2D_set0(WorldViewActivity.this, false);
            if (!WorldViewActivity._2D_wrap0(WorldViewActivity.this) && WorldViewActivity._2D_get9(WorldViewActivity.this) ^ true && WorldViewActivity._2D_get0(WorldViewActivity.this).hasData())
            {
                VoiceChatInfo voicechatinfo = (VoiceChatInfo)WorldViewActivity._2D_get20(WorldViewActivity.this).getData();
                boolean flag;
                if (voicechatinfo != null && voicechatinfo.state == com.lumiyaviewer.lumiya.voice.common.model.hatState.Active)
                {
                    flag = voicechatinfo.localMicActive;
                } else
                {
                    flag = false;
                }
                if (!flag)
                {
                    l = SystemClock.uptimeMillis();
                    l = (WorldViewActivity._2D_get12(WorldViewActivity.this) + 7500L) - l;
                    Debug.Printf("ButtonsFade: remaining %d", new Object[] {
                        Long.valueOf(l)
                    });
                    if (l > 0L)
                    {
                        break label0;
                    }
                    WorldViewActivity._2D_wrap3(WorldViewActivity.this);
                }
            }
            return;
        }
        WorldViewActivity._2D_set0(WorldViewActivity.this, true);
        WorldViewActivity._2D_get15(WorldViewActivity.this).postDelayed(WorldViewActivity._2D_get3(WorldViewActivity.this), l);
    }

    fo.VoiceChatState()
    {
        this$0 = WorldViewActivity.this;
        super();
    }
}
