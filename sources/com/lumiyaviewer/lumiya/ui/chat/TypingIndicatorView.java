package com.lumiyaviewer.lumiya.ui.chat;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.UUID;
import javax.annotation.Nullable;

public class TypingIndicatorView extends ImageView {
    @Nullable
    private ChatterID chatterID = null;
    @Nullable
    private Subscription<UUID, Boolean> subscription = null;

    public TypingIndicatorView(Context context) {
        super(context);
    }

    public TypingIndicatorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public TypingIndicatorView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @TargetApi(21)
    public TypingIndicatorView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: private */
    /* renamed from: onUserTypingStatus */
    public void m425com_lumiyaviewer_lumiya_ui_chat_TypingIndicatorViewmthref0(Boolean bool) {
        if (bool != null && this.subscription != null && (this.chatterID instanceof ChatterID.ChatterIDUser)) {
            if (bool.booleanValue() && getVisibility() != 0) {
                ((AnimationDrawable) getDrawable()).start();
            } else if (!bool.booleanValue() && getVisibility() == 0) {
                ((AnimationDrawable) getDrawable()).stop();
            }
            setVisibility(bool.booleanValue() ? 0 : 4);
        }
    }

    public void setChatterID(@Nullable ChatterID chatterID2) {
        if (!Objects.equal(chatterID2, this.chatterID)) {
            this.chatterID = chatterID2;
            if (this.subscription != null) {
                this.subscription.unsubscribe();
                this.subscription = null;
            }
            if ((chatterID2 instanceof ChatterID.ChatterIDUser) && chatterID2.getUserManager() != null) {
                this.subscription = chatterID2.getUserManager().getChatterList().getUserTypingStatus().subscribe(((ChatterID.ChatterIDUser) chatterID2).getChatterUUID(), UIThreadExecutor.getInstance(), new $Lambda$XDRgkFjVFoS0WpW8v6lPNgts7Q(this));
            }
            if (this.subscription == null) {
                setVisibility(4);
            }
        }
    }
}
