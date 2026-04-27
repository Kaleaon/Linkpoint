// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.google.common.base.Objects;
import android.graphics.drawable.AnimationDrawable;
import android.annotation.TargetApi;
import android.util.AttributeSet;
import android.content.Context;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.Subscription;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.widget.ImageView;

public class TypingIndicatorView extends ImageView
{
    @Nullable
    private ChatterID chatterID;
    @Nullable
    private Subscription<UUID, Boolean> subscription;
    
    public TypingIndicatorView(final Context context) {
        super(context);
        this.chatterID = null;
        this.subscription = null;
    }
    
    public TypingIndicatorView(final Context context, final AttributeSet set) {
        super(context, set);
        this.chatterID = null;
        this.subscription = null;
    }
    
    public TypingIndicatorView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.chatterID = null;
        this.subscription = null;
    }
    
    @TargetApi(21)
    public TypingIndicatorView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.chatterID = null;
        this.subscription = null;
    }
    
    private void onUserTypingStatus(final Boolean b) {
        if (b != null && this.subscription != null && this.chatterID instanceof ChatterID.ChatterIDUser) {
            if (b && this.getVisibility() != 0) {
                ((AnimationDrawable)this.getDrawable()).start();
            }
            else if (!b && this.getVisibility() == 0) {
                ((AnimationDrawable)this.getDrawable()).stop();
            }
            int visibility;
            if (b) {
                visibility = 0;
            }
            else {
                visibility = 4;
            }
            this.setVisibility(visibility);
        }
    }
    
    public void setChatterID(@Nullable final ChatterID chatterID) {
        if (!Objects.equal(chatterID, this.chatterID)) {
            this.chatterID = chatterID;
            if (this.subscription != null) {
                this.subscription.unsubscribe();
                this.subscription = null;
            }
            if (chatterID instanceof ChatterID.ChatterIDUser && chatterID.getUserManager() != null) {
                this.subscription = chatterID.getUserManager().getChatterList().getUserTypingStatus().subscribe(((ChatterID.ChatterIDUser)chatterID).getChatterUUID(), UIThreadExecutor.getInstance(), new _$Lambda$XDRgkFjV_FoS0WpW8v6lPNgts7Q(this));
            }
            if (this.subscription == null) {
                this.setVisibility(4);
            }
        }
    }
}
