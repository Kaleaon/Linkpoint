package com.lumiyaviewer.lumiya.ui.common;

import android.content.Context;
import android.widget.TextView;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChatterNameDisplayer implements ChatterNameRetriever.OnChatterNameUpdated {
    private boolean alreadyUpdated = false;
    @Nullable
    private ChatterID chatterID = null;
    @Nullable
    private ChatterNameRetriever nameRetriever = null;
    @Nullable
    private TextView nameTextView = null;
    @Nullable
    private ChatterPicView picView = null;

    private void clearViews() {
        if (this.nameTextView != null) {
            this.nameTextView.setText("");
        }
        if (this.picView != null) {
            this.picView.setChatterID((ChatterID) null, (String) null);
        }
    }

    private void updateViews() {
        if (this.chatterID == null || this.nameRetriever == null) {
            clearViews();
            return;
        }
        String resolvedName = this.nameRetriever.getResolvedName();
        if (this.nameTextView != null) {
            this.nameTextView.setText(resolvedName != null ? resolvedName : this.nameTextView.getContext().getString(R.string.name_loading_title));
        }
        if (this.picView != null) {
            this.picView.setChatterID(this.chatterID, resolvedName);
        }
    }

    public void bindViews(@Nullable TextView textView, @Nullable ChatterPicView chatterPicView) {
        this.nameTextView = textView;
        this.picView = chatterPicView;
        updateViews();
    }

    @Nullable
    public ChatterID getChatterID() {
        return this.chatterID;
    }

    @Nonnull
    public String getResolvedName(Context context) {
        String str = null;
        if (this.nameRetriever != null) {
            str = this.nameRetriever.getResolvedName();
        }
        return str != null ? str : context.getString(R.string.name_loading_title);
    }

    public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        if (chatterNameRetriever == this.nameRetriever) {
            this.alreadyUpdated = true;
            updateViews();
        }
    }

    public void setChatterID(@Nullable ChatterID chatterID2) {
        if (!Objects.equal(chatterID2, this.chatterID)) {
            if (this.nameRetriever != null) {
                this.nameRetriever.dispose();
                this.nameRetriever = null;
            }
            this.chatterID = chatterID2;
            if (chatterID2 != null) {
                this.alreadyUpdated = false;
                this.nameRetriever = new ChatterNameRetriever(chatterID2, this, UIThreadExecutor.getInstance(), false);
                this.nameRetriever.subscribe();
                if (!this.alreadyUpdated) {
                    if (this.nameTextView != null) {
                        this.nameTextView.setText(R.string.name_loading_title);
                    }
                    if (this.picView != null) {
                        this.picView.setChatterID((ChatterID) null, (String) null);
                        return;
                    }
                    return;
                }
                return;
            }
            clearViews();
        }
    }

    public void unbindViews() {
        this.nameTextView = null;
        this.picView = null;
    }
}
