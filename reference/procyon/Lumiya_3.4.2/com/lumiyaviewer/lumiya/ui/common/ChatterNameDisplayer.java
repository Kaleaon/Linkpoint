// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.google.common.base.Objects;
import javax.annotation.Nonnull;
import android.content.Context;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import android.widget.TextView;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;

public class ChatterNameDisplayer implements OnChatterNameUpdated
{
    private boolean alreadyUpdated;
    @Nullable
    private ChatterID chatterID;
    @Nullable
    private ChatterNameRetriever nameRetriever;
    @Nullable
    private TextView nameTextView;
    @Nullable
    private ChatterPicView picView;
    
    public ChatterNameDisplayer() {
        this.nameRetriever = null;
        this.chatterID = null;
        this.nameTextView = null;
        this.picView = null;
        this.alreadyUpdated = false;
    }
    
    private void clearViews() {
        if (this.nameTextView != null) {
            this.nameTextView.setText((CharSequence)"");
        }
        if (this.picView != null) {
            this.picView.setChatterID(null, null);
        }
    }
    
    private void updateViews() {
        if (this.chatterID != null && this.nameRetriever != null) {
            final String resolvedName = this.nameRetriever.getResolvedName();
            if (this.nameTextView != null) {
                final TextView nameTextView = this.nameTextView;
                String string;
                if (resolvedName != null) {
                    string = resolvedName;
                }
                else {
                    string = this.nameTextView.getContext().getString(2131296712);
                }
                nameTextView.setText((CharSequence)string);
            }
            if (this.picView != null) {
                this.picView.setChatterID(this.chatterID, resolvedName);
            }
        }
        else {
            this.clearViews();
        }
    }
    
    public void bindViews(@Nullable final TextView nameTextView, @Nullable final ChatterPicView picView) {
        this.nameTextView = nameTextView;
        this.picView = picView;
        this.updateViews();
    }
    
    @Nullable
    public ChatterID getChatterID() {
        return this.chatterID;
    }
    
    @Nonnull
    public String getResolvedName(final Context context) {
        String s = null;
        if (this.nameRetriever != null) {
            s = this.nameRetriever.getResolvedName();
        }
        if (s == null) {
            s = context.getString(2131296712);
        }
        return s;
    }
    
    @Override
    public void onChatterNameUpdated(final ChatterNameRetriever chatterNameRetriever) {
        if (chatterNameRetriever == this.nameRetriever) {
            this.alreadyUpdated = true;
            this.updateViews();
        }
    }
    
    public void setChatterID(@Nullable final ChatterID chatterID) {
        if (!Objects.equal(chatterID, this.chatterID)) {
            if (this.nameRetriever != null) {
                this.nameRetriever.dispose();
                this.nameRetriever = null;
            }
            if ((this.chatterID = chatterID) != null) {
                this.alreadyUpdated = false;
                (this.nameRetriever = new ChatterNameRetriever(chatterID, (ChatterNameRetriever.OnChatterNameUpdated)this, UIThreadExecutor.getInstance(), false)).subscribe();
                if (!this.alreadyUpdated) {
                    if (this.nameTextView != null) {
                        this.nameTextView.setText(2131296712);
                    }
                    if (this.picView != null) {
                        this.picView.setChatterID(null, null);
                    }
                }
            }
            else {
                this.clearViews();
            }
        }
    }
    
    public void unbindViews() {
        this.nameTextView = null;
        this.picView = null;
    }
}
