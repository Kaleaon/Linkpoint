package com.linkpoint.slproto.chat.generic
import java.util.*

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.format.DateUtils
import android.view.View
import android.widget.TextView
import com.linkpoint.R
import com.linkpoint.render.avatar.AnimationSequenceInfo
import com.linkpoint.ui.chat.ChatterPicView
import com.linkpoint.ui.chat.HasUserPicClickHandler
import java.lang.ref.WeakReference

class ChatEventViewHolder : RecyclerView.ViewHolder {
    protected WeakReference<RecyclerView.Adapter> adapter
    View bubbleView
    ChatterPicView chatSourceIcon
    ChatterPicView chatSourceIconRight
    TextView textView
    TextView timestampView
    private Long updateTimestamp = 0

    interface Factory {
        ChatEventViewHolder createViewHolder(View view, RecyclerView.Adapter adapter)
    }

    ChatEventViewHolder(View view, RecyclerView.Adapter adapter2) {
        super(view)
        View.OnClickListener userPicClickListener
        this.adapter = WeakReference<>(adapter2)
        this.timestampView = (view as TextView).findViewById(R.id.chatMessageTimestamp)
        this.textView = (view as TextView).findViewById(R.id.chatMessageTextView)
        this.bubbleView = view.findViewById(R.id.chatMessageBubble)
        this.chatSourceIcon = (view as ChatterPicView).findViewById(R.id.chatMessageSourceIcon)
        this.chatSourceIconRight = (view as ChatterPicView).findViewById(R.id.chatMessageSourceIconRight)
        if ((adapter2 instanceof HasUserPicClickHandler) && (userPicClickListener = ((HasUserPicClickHandler) adapter2).getUserPicClickListener()) != null) {
            if (this.chatSourceIcon != null) {
                this.chatSourceIcon.setOnClickListener(userPicClickListener)
            }
            if (this.chatSourceIconRight != null) {
                this.chatSourceIconRight.setOnClickListener(userPicClickListener)
            }
        }
    }

    /* access modifiers changed from: package-private */
    fun requestAdapterUpdate(): Unit {
        RecyclerView.Adapter adapter2 = (RecyclerView.Adapter) this.adapter.get()
        if (adapter2 != null) {
            adapter2.notifyItemChanged(getAdapterPosition())
        }
    }

    /* access modifiers changed from: package-private */
    fun setupTimestampUpdate(Context context, Long j): Unit {
        this.updateTimestamp = j
        updateTimestamp(context)
    }

    fun updateTimestamp(Context context): Unit {
        if (this.timestampView == null) {
            return
        }
        if (this.updateTimestamp != 0) {
            Long currentTimeMillis = System.currentTimeMillis()
            this.timestampView.setText(currentTimeMillis < this.updateTimestamp + AnimationSequenceInfo.MAX_ANIMATION_LENGTH ? context.getString(R.string.now) : DateUtils.getRelativeTimeSpanString(this.updateTimestamp, currentTimeMillis, AnimationSequenceInfo.MAX_ANIMATION_LENGTH, 262144))
            this.timestampView.setVisibility(0)
            return
        }
        this.timestampView.setVisibility(8)
    }
}
