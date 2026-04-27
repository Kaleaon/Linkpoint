// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.chat.generic:
//            ChatEventViewHolder, SLChatYesNoEvent

class ChatYesNoEventViewHolder extends ChatEventViewHolder
    implements android.view.View.OnClickListener
{

    final CardView cardView;
    private int cardViewDefaultBackground;
    private int cardViewDefaultText;
    private int cardViewDisabledBackground;
    private int cardViewDisabledText;
    private boolean cardViewFaded;
    private AnimatorSet fadeAnimatorSet;
    final Button noButton;
    final TextView questionMsg;
    final Button yesButton;
    private SLChatYesNoEvent yesNoEvent;

    ChatYesNoEventViewHolder(View view, android.support.v7.widget.RecyclerView.Adapter adapter)
    {
        super(view, adapter);
        adapter = new TypedValue();
        view.getContext().getTheme().resolveAttribute(0x7f010000, adapter, true);
        cardViewDefaultBackground = ((TypedValue) (adapter)).data;
        view.getContext().getTheme().resolveAttribute(0x7f010001, adapter, true);
        cardViewDefaultText = ((TypedValue) (adapter)).data;
        view.getContext().getTheme().resolveAttribute(0x7f010003, adapter, true);
        cardViewDisabledBackground = ((TypedValue) (adapter)).data;
        view.getContext().getTheme().resolveAttribute(0x7f010004, adapter, true);
        cardViewDisabledText = ((TypedValue) (adapter)).data;
        yesButton = (Button)view.findViewById(0x7f10013c);
        noButton = (Button)view.findViewById(0x7f10013d);
        questionMsg = (TextView)view.findViewById(0x7f10013b);
        cardView = (CardView)view.findViewById(0x7f100127);
        yesNoEvent = null;
        if (yesButton != null)
        {
            yesButton.setOnClickListener(this);
        }
        if (noButton != null)
        {
            noButton.setOnClickListener(this);
        }
    }

    private void fadeCardView()
    {
        if (!cardViewFaded && android.os.Build.VERSION.SDK_INT >= 11)
        {
            if (fadeAnimatorSet == null)
            {
                ObjectAnimator objectanimator = (ObjectAnimator)AnimatorInflater.loadAnimator(cardView.getContext(), 0x7f060000);
                ObjectAnimator objectanimator1 = (ObjectAnimator)AnimatorInflater.loadAnimator(cardView.getContext(), 0x7f060001);
                ObjectAnimator objectanimator2 = objectanimator1.clone();
                objectanimator.setTarget(cardView);
                objectanimator1.setTarget(textView);
                objectanimator2.setTarget(questionMsg);
                fadeAnimatorSet = new AnimatorSet();
                fadeAnimatorSet.playTogether(new Animator[] {
                    objectanimator, objectanimator1, objectanimator2
                });
            }
            fadeAnimatorSet.start();
            cardViewFaded = true;
        }
    }

    public void makeCardViewDisabled()
    {
        if (!cardViewFaded)
        {
            cardView.setCardBackgroundColor(cardViewDisabledBackground);
            questionMsg.setTextColor(cardViewDisabledText);
            textView.setTextColor(cardViewDisabledText);
        }
    }

    public void makeCardViewEnabled()
    {
        if (cardViewFaded)
        {
            cardViewFaded = false;
            if (fadeAnimatorSet != null && android.os.Build.VERSION.SDK_INT >= 11)
            {
                fadeAnimatorSet.cancel();
            }
        }
        cardView.setCardBackgroundColor(cardViewDefaultBackground);
        questionMsg.setTextColor(cardViewDefaultText);
        textView.setTextColor(cardViewDefaultText);
    }

    public void onClick(View view)
    {
        view.getId();
        JVM INSTR tableswitch 2131755324 2131755325: default 28
    //                   2131755324 29
    //                   2131755325 75;
           goto _L1 _L2 _L3
_L1:
        return;
_L2:
        if (yesNoEvent != null && yesNoEvent.getEventState() == SLChatYesNoEvent.EventState.EventNew)
        {
            fadeCardView();
            yesNoEvent.onYesAction(view.getContext(), UserManager.getUserManager(yesNoEvent.getAgentUUID()));
            return;
        }
        continue; /* Loop/switch isn't completed */
_L3:
        if (yesNoEvent != null && yesNoEvent.getEventState() == SLChatYesNoEvent.EventState.EventNew)
        {
            fadeCardView();
            yesNoEvent.onNoAction(view.getContext(), UserManager.getUserManager(yesNoEvent.getAgentUUID()));
            return;
        }
        if (true) goto _L1; else goto _L4
_L4:
    }

    public void setEvent(SLChatYesNoEvent slchatyesnoevent)
    {
        yesNoEvent = slchatyesnoevent;
    }
}
