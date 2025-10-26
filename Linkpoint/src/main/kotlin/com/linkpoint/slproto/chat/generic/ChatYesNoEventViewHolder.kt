package com.linkpoint.slproto.chat.generic
import java.util.*

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.linkpoint.R
import com.linkpoint.slproto.chat.generic.SLChatYesNoEvent
import com.linkpoint.slproto.users.manager.UserManager
import javax.annotation.Nullable

class ChatYesNoEventViewHolder : ChatEventViewHolder(), View.OnClickListener {
    final CardView cardView
    private Int cardViewDefaultBackground
    private Int cardViewDefaultText
    private Int cardViewDisabledBackground
    private Int cardViewDisabledText
    private Boolean cardViewFaded
    private AnimatorSet fadeAnimatorSet
    final Button noButton
    final TextView questionMsg
    final Button yesButton
    private SLChatYesNoEvent yesNoEvent = null

    ChatYesNoEventViewHolder(View view, RecyclerView.Adapter adapter) {
        super(view, adapter)
        val typedValue: TypedValue = TypedValue()
        view.getContext().getTheme().resolveAttribute(R.attr.CardViewDefaultBackground, typedValue, true)
        this.cardViewDefaultBackground = typedValue.data
        view.getContext().getTheme().resolveAttribute(R.attr.CardViewDefaultText, typedValue, true)
        this.cardViewDefaultText = typedValue.data
        view.getContext().getTheme().resolveAttribute(R.attr.CardViewDisabledBackground, typedValue, true)
        this.cardViewDisabledBackground = typedValue.data
        view.getContext().getTheme().resolveAttribute(R.attr.CardViewDisabledText, typedValue, true)
        this.cardViewDisabledText = typedValue.data
        this.yesButton = (Button) view.findViewById(R.id.buttonYesNoAccept)
        this.noButton = (Button) view.findViewById(R.id.buttonYesNoDecline)
        this.questionMsg = (TextView) view.findViewById(R.id.yesNoMessageTextView)
        this.cardView = (CardView) view.findViewById(R.id.chatMessageCardView)
        if (this.yesButton != null) {
            this.yesButton.setOnClickListener(this)
        }
        if (this.noButton != null) {
            this.noButton.setOnClickListener(this)
        }
    }

     private fun fadeCardView() {
        if (!this.cardViewFaded && Build.VERSION.SDK_INT >= 11) {
            if (this.fadeAnimatorSet == null) {
                val objectAnimator: ObjectAnimator = (ObjectAnimator) AnimatorInflater.loadAnimator(this.cardView.getContext(), R.animator.cardview_background_fade)
                val objectAnimator2: ObjectAnimator = (ObjectAnimator) AnimatorInflater.loadAnimator(this.cardView.getContext(), R.animator.cardview_text_unfade)
                val clone: ObjectAnimator = objectAnimator2.clone()
                objectAnimator.setTarget(this.cardView)
                objectAnimator2.setTarget(this.textView)
                clone.setTarget(this.questionMsg)
                this.fadeAnimatorSet = AnimatorSet()
                this.fadeAnimatorSet.playTogether(Array<Animator>{objectAnimator, objectAnimator2, clone})
            }
            this.fadeAnimatorSet.start()
            this.cardViewFaded = true
        }
    }

    fun makeCardViewDisabled() {
        if (!this.cardViewFaded) {
            this.cardView.setCardBackgroundColor(this.cardViewDisabledBackground)
            this.questionMsg.setTextColor(this.cardViewDisabledText)
            this.textView.setTextColor(this.cardViewDisabledText)
        }
    }

    fun makeCardViewEnabled() {
        if (this.cardViewFaded) {
            this.cardViewFaded = false
            if (this.fadeAnimatorSet != null && Build.VERSION.SDK_INT >= 11) {
                this.fadeAnimatorSet.cancel()
            }
        }
        this.cardView.setCardBackgroundColor(this.cardViewDefaultBackground)
        this.questionMsg.setTextColor(this.cardViewDefaultText)
        this.textView.setTextColor(this.cardViewDefaultText)
    }

    fun onClick(view: View) {
        switch (view.getId()) {
            case R.id.buttonYesNoAccept:
                if (this.yesNoEvent != null && this.yesNoEvent.getEventState() == SLChatYesNoEvent.EventState.EventNew) {
                    fadeCardView()
                    this.yesNoEvent.onYesAction(view.getContext(), UserManager.getUserManager(this.yesNoEvent.getAgentUUID()))
                    return
                }
                return
            case R.id.buttonYesNoDecline:
                if (this.yesNoEvent != null && this.yesNoEvent.getEventState() == SLChatYesNoEvent.EventState.EventNew) {
                    fadeCardView()
                    this.yesNoEvent.onNoAction(view.getContext(), UserManager.getUserManager(this.yesNoEvent.getAgentUUID()))
                    return
                }
                return
            default:
                return
        }
    }

    fun setEvent(sLChatYesNoEvent: SLChatYesNoEvent) {
        this.yesNoEvent = sLChatYesNoEvent
    }
}
