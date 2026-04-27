// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.SystemClock;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            FadingTextViewLog, ChatEventOverlay

class val.textView extends AnimatorListenerAdapter
{

    final val.textView this$1;
    final TextView val$textView;

    public void onAnimationEnd(Animator animator)
    {
        FadingTextViewLog._2D_get1(_fld0).removeView(val$textView);
    }

    is._cls0()
    {
        this$1 = final__pcls0;
        val$textView = TextView.this;
        super();
    }

    // Unreferenced inner class com/lumiyaviewer/lumiya/ui/render/FadingTextViewLog$1

/* anonymous class */
    class FadingTextViewLog._cls1
        implements Runnable
    {

        final FadingTextViewLog this$0;

        public void run()
        {
            FadingTextViewLog._2D_set0(FadingTextViewLog.this, false);
            if (FadingTextViewLog._2D_get1(FadingTextViewLog.this) != null)
            {
                long l = SystemClock.uptimeMillis();
                Iterator iterator = FadingTextViewLog._2D_get0(FadingTextViewLog.this).entrySet().iterator();
                do
                {
                    if (!iterator.hasNext())
                    {
                        break;
                    }
                    Object obj = (java.util.Map.Entry)iterator.next();
                    if (obj == null)
                    {
                        continue;
                    }
                    if (l < ((ChatEventOverlay)((java.util.Map.Entry) (obj)).getValue()).timestamp + 5000L)
                    {
                        break;
                    }
                    obj = ((ChatEventOverlay)((java.util.Map.Entry) (obj)).getValue()).textView;
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        ((TextView) (obj)).animate().alpha(0.0F).setDuration(1000L).setListener(((FadingTextViewLog._cls1._cls1) (obj)). new FadingTextViewLog._cls1._cls1()).start();
                    } else
                    {
                        FadingTextViewLog._2D_get1(FadingTextViewLog.this).removeView(((android.view.View) (obj)));
                    }
                    iterator.remove();
                } while (true);
            }
            postRemovingStaleChats();
        }

            
            {
                this$0 = FadingTextViewLog.this;
                super();
            }
    }

}
