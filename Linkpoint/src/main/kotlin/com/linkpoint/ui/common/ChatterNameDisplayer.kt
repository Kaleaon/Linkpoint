package com.linkpoint.ui.common

import android.content.Context
import android.widget.TextView
import com.google.common.base.Objects
import com.linkpoint.R
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.ChatterNameRetriever
import com.linkpoint.ui.chat.ChatterPicView
import javax.annotation.Nonnull
import javax.annotation.Nullable

class ChatterNameDisplayer : ChatterNameRetriever.OnChatterNameUpdated {
    private Boolean alreadyUpdated = false
    private ChatterID chatterID = null
    private ChatterNameRetriever nameRetriever = null
    private TextView nameTextView = null
    private ChatterPicView picView = null

    private Unit clearViews() {
        if (this.nameTextView != null) {
            this.nameTextView.setText("")
        }
        if (this.picView != null) {
            this.picView.setChatterID((ChatterID) null, (String) null)
        }
    }

    private Unit updateViews() {
        if (this.chatterID == null || this.nameRetriever == null) {
            clearViews()
            return
        }
        String resolvedName = this.nameRetriever.getResolvedName()
        if (this.nameTextView != null) {
            this.nameTextView.setText(resolvedName != null ? resolvedName : this.nameTextView.getContext().getString(R.string.name_loading_title))
        }
        if (this.picView != null) {
            this.picView.setChatterID(this.chatterID, resolvedName)
        }
    }

    fun bindViews(TextView textView, ChatterPicView chatterPicView) {
        this.nameTextView = textView
        this.picView = chatterPicView
        updateViews()
    }

    public ChatterID getChatterID() {
        return this.chatterID
    }

    public String getResolvedName(Context context) {
        String str = null
        if (this.nameRetriever != null) {
            str = this.nameRetriever.getResolvedName()
        }
        return str != null ? str : context.getString(R.string.name_loading_title)
    }

    fun onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        if (chatterNameRetriever == this.nameRetriever) {
            this.alreadyUpdated = true
            updateViews()
        }
    }

    fun setChatterID(ChatterID chatterID2) {
        if (!Objects.equal(chatterID2, this.chatterID)) {
            if (this.nameRetriever != null) {
                this.nameRetriever.dispose()
                this.nameRetriever = null
            }
            this.chatterID = chatterID2
            if (chatterID2 != null) {
                this.alreadyUpdated = false
                this.nameRetriever = ChatterNameRetriever(chatterID2, this, UIThreadExecutor.getInstance(), false)
                this.nameRetriever.subscribe()
                if (!this.alreadyUpdated) {
                    if (this.nameTextView != null) {
                        this.nameTextView.setText(R.string.name_loading_title)
                    }
                    if (this.picView != null) {
                        this.picView.setChatterID((ChatterID) null, (String) null)
                        return
                    }
                    return
                }
                return
            }
            clearViews()
        }
    }

    fun unbindViews() {
        this.nameTextView = null
        this.picView = null
    }
}
