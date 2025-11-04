package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.render.avatar.AnimationSequenceInfo;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.chat.HasUserPicClickHandler;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class ChatEventViewHolder extends RecyclerView.ViewHolder {
    protected final WeakReference<RecyclerView.Adapter> adapter;
    final View bubbleView;
    final ChatterPicView chatSourceIcon;
    final ChatterPicView chatSourceIconRight;
    final TextView textView;
    final TextView timestampView;
    private long updateTimestamp;

    /* loaded from: classes.dex */
    interface Factory {
        ChatEventViewHolder createViewHolder(View view, RecyclerView.Adapter adapter);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public ChatEventViewHolder(View view, RecyclerView.Adapter adapter) {
        super(view);
        View.OnClickListener userPicClickListener;
        this.updateTimestamp = 0L;
        this.adapter = new WeakReference<>(adapter);
        this.timestampView = (TextView) view.findViewById(R.id.chatMessageTimestamp);
        this.textView = (TextView) view.findViewById(R.id.chatMessageTextView);
        this.bubbleView = view.findViewById(R.id.chatMessageBubble);
        this.chatSourceIcon = (ChatterPicView) view.findViewById(R.id.chatMessageSourceIcon);
        this.chatSourceIconRight = (ChatterPicView) view.findViewById(R.id.chatMessageSourceIconRight);
        if (!(adapter instanceof HasUserPicClickHandler) || (userPicClickListener = ((HasUserPicClickHandler) adapter).getUserPicClickListener()) == null) {
            return;
        }
        if (this.chatSourceIcon != null) {
            this.chatSourceIcon.setOnClickListener(userPicClickListener);
        }
        if (this.chatSourceIconRight != null) {
            this.chatSourceIconRight.setOnClickListener(userPicClickListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void requestAdapterUpdate() {
        RecyclerView.Adapter adapter = this.adapter.get();
        if (adapter != null) {
            adapter.notifyItemChanged(getAdapterPosition());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setupTimestampUpdate(Context context, long j) {
        this.updateTimestamp = j;
        updateTimestamp(context);
    }

    public void updateTimestamp(Context context) {
        if (this.timestampView != null) {
            if (this.updateTimestamp == 0) {
                this.timestampView.setVisibility(8);
                return;
            }
            long currentTimeMillis = System.currentTimeMillis();
            this.timestampView.setText(currentTimeMillis < this.updateTimestamp + AnimationSequenceInfo.MAX_ANIMATION_LENGTH ? context.getString(R.string.now) : DateUtils.getRelativeTimeSpanString(this.updateTimestamp, currentTimeMillis, AnimationSequenceInfo.MAX_ANIMATION_LENGTH, 262144));
            this.timestampView.setVisibility(0);
        }
    }
}
