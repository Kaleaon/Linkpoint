// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.Debug;
import java.util.ArrayList;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            CardboardActivity

class this._cls0
    implements RecognitionListener
{

    final CardboardActivity this$0;

    public void onBeginningOfSpeech()
    {
        Debug.Printf("Cardboard: beginning of speech", new Object[0]);
    }

    public void onBufferReceived(byte abyte0[])
    {
    }

    public void onEndOfSpeech()
    {
        Debug.Printf("Cardboard: end of speech", new Object[0]);
        speakLevelIndicator.setVisibility(4);
        speakNowText.setVisibility(4);
    }

    public void onError(int i)
    {
        Debug.Printf("Cardboard: speech error %d", new Object[] {
            Integer.valueOf(i)
        });
        i;
        JVM INSTR tableswitch 1 9: default 68
    //                   1 113
    //                   2 113
    //                   3 87
    //                   4 152
    //                   5 68
    //                   6 165
    //                   7 126
    //                   8 139
    //                   9 100;
           goto _L1 _L2 _L2 _L3 _L4 _L1 _L5 _L6 _L7 _L8
_L1:
        String s = getString(0x7f090315);
_L10:
        CardboardActivity._2D_wrap7(CardboardActivity.this, s);
        return;
_L3:
        s = getString(0x7f090314);
        continue; /* Loop/switch isn't completed */
_L8:
        s = getString(0x7f090317);
        continue; /* Loop/switch isn't completed */
_L2:
        s = getString(0x7f09031a);
        continue; /* Loop/switch isn't completed */
_L6:
        s = getString(0x7f09031b);
        continue; /* Loop/switch isn't completed */
_L7:
        s = getString(0x7f090316);
        continue; /* Loop/switch isn't completed */
_L4:
        s = getString(0x7f090318);
        continue; /* Loop/switch isn't completed */
_L5:
        s = getString(0x7f090319);
        if (true) goto _L10; else goto _L9
_L9:
    }

    public void onEvent(int i, Bundle bundle)
    {
    }

    public void onPartialResults(Bundle bundle)
    {
        Debug.Printf("Cardboard: speech recognition: got partial results", new Object[0]);
        bundle = bundle.getStringArrayList("results_recognition");
        if (bundle != null && bundle.size() > 0)
        {
            bundle = (String)bundle.get(0);
            CardboardActivity._2D_set4(CardboardActivity.this, bundle);
            speechRecognitionResults.setText(bundle);
            if (!Strings.isNullOrEmpty(bundle))
            {
                buttonSpeechSend.setVisibility(0);
            }
        }
    }

    public void onReadyForSpeech(Bundle bundle)
    {
        speakNowText.setVisibility(0);
    }

    public void onResults(Bundle bundle)
    {
        Debug.Printf("Cardboard: speech recognition: got some results", new Object[0]);
        bundle = bundle.getStringArrayList("results_recognition");
        if (bundle != null && bundle.size() > 0)
        {
            bundle = (String)bundle.get(0);
            speechRecognitionResults.setText(bundle);
            CardboardActivity._2D_set4(CardboardActivity.this, bundle);
            if (!Strings.isNullOrEmpty(bundle))
            {
                buttonSpeechSend.setVisibility(0);
                speakLevelIndicator.setVisibility(4);
                CardboardActivity._2D_set3(CardboardActivity.this, true);
            }
        }
    }

    public void onRmsChanged(float f)
    {
        if (!CardboardActivity._2D_get15(CardboardActivity.this))
        {
            if (Float.isNaN(CardboardActivity._2D_get27(CardboardActivity.this)) || f < CardboardActivity._2D_get27(CardboardActivity.this))
            {
                CardboardActivity._2D_set9(CardboardActivity.this, f);
            }
            if (Float.isNaN(CardboardActivity._2D_get26(CardboardActivity.this)) || f > CardboardActivity._2D_get26(CardboardActivity.this))
            {
                CardboardActivity._2D_set8(CardboardActivity.this, f);
            }
            float f2 = CardboardActivity._2D_get26(CardboardActivity.this);
            float f1 = f2;
            if (f2 - CardboardActivity._2D_get27(CardboardActivity.this) < 1.0F)
            {
                f1 = CardboardActivity._2D_get27(CardboardActivity.this) + 1.0F;
            }
            int j = Math.round(((f - CardboardActivity._2D_get27(CardboardActivity.this)) * 100F) / (f1 - CardboardActivity._2D_get27(CardboardActivity.this)));
            int i = j;
            if (j < 0)
            {
                i = 0;
            }
            j = i;
            if (i > 100)
            {
                j = 100;
            }
            Debug.Printf("Cardboard: speech recognition: RMS %f", new Object[] {
                Float.valueOf(f)
            });
            speakLevelIndicator.setVisibility(0);
            speakLevelIndicator.setProgress(j);
        }
    }

    ()
    {
        this$0 = CardboardActivity.this;
        super();
    }
}
