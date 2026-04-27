// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.voice;

import android.widget.SeekBar;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceSetAudioProperties;
import com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.voice:
//            VoiceStatusView

class this._cls0
    implements android.widget.hangeListener
{

    final VoiceStatusView this$0;

    public void onProgressChanged(SeekBar seekbar, int i, boolean flag)
    {
        if (flag && VoiceStatusView._2D_get0(VoiceStatusView.this) ^ true)
        {
            float f = (float)i / (float)seekbar.getMax();
            seekbar = GridConnectionService.getServiceInstance();
            if (seekbar != null)
            {
                seekbar = seekbar.getVoicePluginServiceConnection();
                if (seekbar != null)
                {
                    seekbar.setVoiceAudioProperties(new VoiceSetAudioProperties(f, true, null));
                }
            }
        }
    }

    public void onStartTrackingTouch(SeekBar seekbar)
    {
    }

    public void onStopTrackingTouch(SeekBar seekbar)
    {
    }

    eConnection()
    {
        this$0 = VoiceStatusView.this;
        super();
    }
}
