// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat.generic;

import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.os.Build$VERSION;
import android.util.TypedValue;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import javax.annotation.Nullable;
import android.animation.AnimatorSet;
import android.support.v7.widget.CardView;
import android.view.View$OnClickListener;

class ChatYesNoEventViewHolder extends ChatEventViewHolder implements View$OnClickListener
{
    final CardView cardView;
    private int cardViewDefaultBackground;
    private int cardViewDefaultText;
    private int cardViewDisabledBackground;
    private int cardViewDisabledText;
    private boolean cardViewFaded;
    @Nullable
    private AnimatorSet fadeAnimatorSet;
    final Button noButton;
    final TextView questionMsg;
    final Button yesButton;
    @Nullable
    private SLChatYesNoEvent yesNoEvent;
    
    ChatYesNoEventViewHolder(final View view, final Adapter adapter) {
        super(view, adapter);
        final TypedValue typedValue = new TypedValue();
        view.getContext().getTheme().resolveAttribute(2130771968, typedValue, true);
        this.cardViewDefaultBackground = typedValue.data;
        view.getContext().getTheme().resolveAttribute(2130771969, typedValue, true);
        this.cardViewDefaultText = typedValue.data;
        view.getContext().getTheme().resolveAttribute(2130771971, typedValue, true);
        this.cardViewDisabledBackground = typedValue.data;
        view.getContext().getTheme().resolveAttribute(2130771972, typedValue, true);
        this.cardViewDisabledText = typedValue.data;
        this.yesButton = (Button)view.findViewById(2131755324);
        this.noButton = (Button)view.findViewById(2131755325);
        this.questionMsg = (TextView)view.findViewById(2131755323);
        this.cardView = (CardView)view.findViewById(2131755303);
        this.yesNoEvent = null;
        if (this.yesButton != null) {
            this.yesButton.setOnClickListener((View$OnClickListener)this);
        }
        if (this.noButton != null) {
            this.noButton.setOnClickListener((View$OnClickListener)this);
        }
    }
    
    private void fadeCardView() {
        if (!this.cardViewFaded && Build$VERSION.SDK_INT >= 11) {
            if (this.fadeAnimatorSet == null) {
                final ObjectAnimator objectAnimator = (ObjectAnimator)AnimatorInflater.loadAnimator(this.cardView.getContext(), 2131099648);
                final ObjectAnimator objectAnimator2 = (ObjectAnimator)AnimatorInflater.loadAnimator(this.cardView.getContext(), 2131099649);
                final ObjectAnimator clone = objectAnimator2.clone();
                objectAnimator.setTarget((Object)this.cardView);
                objectAnimator2.setTarget((Object)this.textView);
                clone.setTarget((Object)this.questionMsg);
                (this.fadeAnimatorSet = new AnimatorSet()).playTogether(new Animator[] { (Animator)objectAnimator, (Animator)objectAnimator2, (Animator)clone });
            }
            this.fadeAnimatorSet.start();
            this.cardViewFaded = true;
        }
    }
    
    public void makeCardViewDisabled() {
        if (!this.cardViewFaded) {
            this.cardView.setCardBackgroundColor(this.cardViewDisabledBackground);
            this.questionMsg.setTextColor(this.cardViewDisabledText);
            this.textView.setTextColor(this.cardViewDisabledText);
        }
    }
    
    public void makeCardViewEnabled() {
        if (this.cardViewFaded) {
            this.cardViewFaded = false;
            if (this.fadeAnimatorSet != null && Build$VERSION.SDK_INT >= 11) {
                this.fadeAnimatorSet.cancel();
            }
        }
        this.cardView.setCardBackgroundColor(this.cardViewDefaultBackground);
        this.questionMsg.setTextColor(this.cardViewDefaultText);
        this.textView.setTextColor(this.cardViewDefaultText);
    }
    
    public void onClick(final View view) {
        switch (view.getId()) {
            case 2131755324: {
                if (this.yesNoEvent != null && this.yesNoEvent.getEventState() == SLChatYesNoEvent.EventState.EventNew) {
                    this.fadeCardView();
                    this.yesNoEvent.onYesAction(view.getContext(), UserManager.getUserManager(this.yesNoEvent.getAgentUUID()));
                    break;
                }
                break;
            }
            case 2131755325: {
                if (this.yesNoEvent != null && this.yesNoEvent.getEventState() == SLChatYesNoEvent.EventState.EventNew) {
                    this.fadeCardView();
                    this.yesNoEvent.onNoAction(view.getContext(), UserManager.getUserManager(this.yesNoEvent.getAgentUUID()));
                    break;
                }
                break;
            }
        }
    }
    
    public void setEvent(final SLChatYesNoEvent yesNoEvent) {
        this.yesNoEvent = yesNoEvent;
    }
}
