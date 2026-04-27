package com.lumiyaviewer.lumiya.slproto.chat.generic;
import java.util.*;

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

public class ChatEventViewHolder extends RecyclerView.ViewHolder {
    protected final WeakReference<RecyclerView.Adapter> adapter;
    final View bubbleView;
    final ChatterPicView chatSourceIcon;
    final ChatterPicView chatSourceIconRight;
    final TextView textView;
    final TextView timestampView;
    private long updateTimestamp = 0;

    interface Factory {
        ChatEventViewHolder createViewHolder(View view, RecyclerView.Adapter adapter);
    }

    public ChatEventViewHolder(View view, RecyclerView.Adapter adapter2) {
        super(view);
        View.OnClickListener userPicClickListener;
        this.adapter = new WeakReference<>(adapter2);
        this.timestampView = (TextView) view.findViewById(R.id.chatMessageTimestamp);
        this.textView = (TextView) view.findViewById(R.id.chatMessageTextView);
        this.bubbleView = view.findViewById(R.id.chatMessageBubble);
        this.chatSourceIcon = (ChatterPicView) view.findViewById(R.id.chatMessageSourceIcon);
        this.chatSourceIconRight = (ChatterPicView) view.findViewById(R.id.chatMessageSourceIconRight);
        if ((adapter2 instanceof HasUserPicClickHandler) && (userPicClickListener = ((HasUserPicClickHandler) adapter2).getUserPicClickListener()) != null) {
            if (this.chatSourceIcon != null) {
                this.chatSourceIcon.setOnClickListener(userPicClickListener);
            }
            if (this.chatSourceIconRight != null) {
                this.chatSourceIconRight.setOnClickListener(userPicClickListener);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void requestAdapterUpdate() {
        RecyclerView.Adapter adapter2 = (RecyclerView.Adapter) this.adapter.get();
        if (adapter2 != null) {
            adapter2.notifyItemChanged(getAdapterPosition());
        }
    }

    /* access modifiers changed from: package-private */
    public void setupTimestampUpdate(Context context, long j) {
        this.updateTimestamp = j;
        updateTimestamp(context);
    }

    public void updateTimestamp(Context context) {
        if (this.timestampView == null) {
            return;
        }
        if (this.updateTimestamp != 0) {
            long currentTimeMillis = System.currentTimeMillis();
            this.timestampView.setText(currentTimeMillis < this.updateTimestamp + AnimationSequenceInfo.MAX_ANIMATION_LENGTH ? context.getString(R.string.now) : DateUtils.getRelativeTimeSpanString(this.updateTimestamp, currentTimeMillis, AnimationSequenceInfo.MAX_ANIMATION_LENGTH, 262144));
            this.timestampView.setVisibility(0);
            return;
        }
        this.timestampView.setVisibility(8);
    }
}
