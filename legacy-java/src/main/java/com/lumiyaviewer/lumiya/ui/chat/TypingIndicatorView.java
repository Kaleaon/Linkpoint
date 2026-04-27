package com.lumiyaviewer.lumiya.ui.chat;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.UUID;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class TypingIndicatorView extends ImageView {

    @Nullable
    private ChatterID chatterID;

    @Nullable
    private Subscription<UUID, Boolean> subscription;

    public TypingIndicatorView(Context context) {
        super(context);
        this.chatterID = null;
        this.subscription = null;
    }

    public TypingIndicatorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.chatterID = null;
        this.subscription = null;
    }

    public TypingIndicatorView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.chatterID = null;
        this.subscription = null;
    }

    @TargetApi(21)
    public TypingIndicatorView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.chatterID = null;
        this.subscription = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onUserTypingStatus, reason: merged with bridge method [inline-methods] */
    public void m435com_lumiyaviewer_lumiya_ui_chat_TypingIndicatorViewmthref0(Boolean bool) {
        if (bool == null || this.subscription == null || !(this.chatterID instanceof ChatterID.ChatterIDUser)) {
            return;
        }
        if (bool.booleanValue() && getVisibility() != 0) {
            ((AnimationDrawable) getDrawable()).start();
        } else if (!bool.booleanValue() && getVisibility() == 0) {
            ((AnimationDrawable) getDrawable()).stop();
        }
        setVisibility(bool.booleanValue() ? 0 : 4);
    }

    public void setChatterID(@Nullable ChatterID chatterID) {
        if (Objects.equal(chatterID, this.chatterID)) {
            return;
        }
        this.chatterID = chatterID;
        if (this.subscription != null) {
            this.subscription.unsubscribe();
            this.subscription = null;
        }
        if ((chatterID instanceof ChatterID.ChatterIDUser) && chatterID.getUserManager() != null) {
            this.subscription = chatterID.getUserManager().getChatterList().getUserTypingStatus().subscribe((Subscribable<UUID, Boolean>) ((ChatterID.ChatterIDUser) chatterID).getChatterUUID(), UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$XDRgkFjV-FoS0WpW8v6lPNgts7Q
                private final /* synthetic */ void $m$0(Object obj) {
                    ((TypingIndicatorView) this).m435com_lumiyaviewer_lumiya_ui_chat_TypingIndicatorViewmthref0((Boolean) obj);
                }

                @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
                public final void onData(Object obj) {
                    $m$0(obj);
                }
            });
        }
        if (this.subscription == null) {
            setVisibility(4);
        }
    }
}
