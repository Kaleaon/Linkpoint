package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import javax.annotation.Nullable;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ChatYesNoEventViewHolder extends ChatEventViewHolder implements View.OnClickListener {
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChatYesNoEventViewHolder(View view, RecyclerView.Adapter adapter) {
        super(view, adapter);
        TypedValue typedValue = new TypedValue();
        view.getContext().getTheme().resolveAttribute(R.attr.CardViewDefaultBackground, typedValue, true);
        this.cardViewDefaultBackground = typedValue.data;
        view.getContext().getTheme().resolveAttribute(R.attr.CardViewDefaultText, typedValue, true);
        this.cardViewDefaultText = typedValue.data;
        view.getContext().getTheme().resolveAttribute(R.attr.CardViewDisabledBackground, typedValue, true);
        this.cardViewDisabledBackground = typedValue.data;
        view.getContext().getTheme().resolveAttribute(R.attr.CardViewDisabledText, typedValue, true);
        this.cardViewDisabledText = typedValue.data;
        this.yesButton = (Button) view.findViewById(R.id.buttonYesNoAccept);
        this.noButton = (Button) view.findViewById(R.id.buttonYesNoDecline);
        this.questionMsg = (TextView) view.findViewById(R.id.yesNoMessageTextView);
        this.cardView = (CardView) view.findViewById(R.id.chatMessageCardView);
        this.yesNoEvent = null;
        if (this.yesButton != null) {
            this.yesButton.setOnClickListener(this);
        }
        if (this.noButton != null) {
            this.noButton.setOnClickListener(this);
        }
    }

    private void fadeCardView() {
        if (this.cardViewFaded || Build.VERSION.SDK_INT < 11) {
            return;
        }
        if (this.fadeAnimatorSet == null) {
            ObjectAnimator objectAnimator = (ObjectAnimator) AnimatorInflater.loadAnimator(this.cardView.getContext(), R.animator.cardview_background_fade);
            ObjectAnimator objectAnimator2 = (ObjectAnimator) AnimatorInflater.loadAnimator(this.cardView.getContext(), R.animator.cardview_text_unfade);
            ObjectAnimator clone = objectAnimator2.clone();
            objectAnimator.setTarget(this.cardView);
            objectAnimator2.setTarget(this.textView);
            clone.setTarget(this.questionMsg);
            this.fadeAnimatorSet = new AnimatorSet();
            this.fadeAnimatorSet.playTogether(objectAnimator, objectAnimator2, clone);
        }
        this.fadeAnimatorSet.start();
        this.cardViewFaded = true;
    }

    public void makeCardViewDisabled() {
        if (this.cardViewFaded) {
            return;
        }
        this.cardView.setCardBackgroundColor(this.cardViewDisabledBackground);
        this.questionMsg.setTextColor(this.cardViewDisabledText);
        this.textView.setTextColor(this.cardViewDisabledText);
    }

    public void makeCardViewEnabled() {
        if (this.cardViewFaded) {
            this.cardViewFaded = false;
            if (this.fadeAnimatorSet != null && Build.VERSION.SDK_INT >= 11) {
                this.fadeAnimatorSet.cancel();
            }
        }
        this.cardView.setCardBackgroundColor(this.cardViewDefaultBackground);
        this.questionMsg.setTextColor(this.cardViewDefaultText);
        this.textView.setTextColor(this.cardViewDefaultText);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonYesNoAccept /* 2131755324 */:
                if (this.yesNoEvent == null || this.yesNoEvent.getEventState() != SLChatYesNoEvent.EventState.EventNew) {
                    return;
                }
                fadeCardView();
                this.yesNoEvent.onYesAction(view.getContext(), UserManager.getUserManager(this.yesNoEvent.getAgentUUID()));
                return;
            case R.id.buttonYesNoDecline /* 2131755325 */:
                if (this.yesNoEvent == null || this.yesNoEvent.getEventState() != SLChatYesNoEvent.EventState.EventNew) {
                    return;
                }
                fadeCardView();
                this.yesNoEvent.onNoAction(view.getContext(), UserManager.getUserManager(this.yesNoEvent.getAgentUUID()));
                return;
            default:
                return;
        }
    }

    public void setEvent(SLChatYesNoEvent sLChatYesNoEvent) {
        this.yesNoEvent = sLChatYesNoEvent;
    }
}
