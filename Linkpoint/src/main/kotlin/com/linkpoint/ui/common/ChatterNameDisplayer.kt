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

     private fun clearViews() {
        if (this.nameTextView != null) {
            this.nameTextView.setText("")
        }
        if (this.picView != null) {
            this.picView.setChatterID((ChatterID) null, (String) null)
        }
    }

     private fun updateViews() {
        if (this.chatterID == null || this.nameRetriever == null) {
            clearViews()
            return
        }
        val resolvedName: String = this.nameRetriever.getResolvedName()
        if (this.nameTextView != null) {
            this.nameTextView.setText(resolvedName != null ? resolvedName : this.nameTextView.getContext().getString(R.string.name_loading_title))
        }
        if (this.picView != null) {
            this.picView.setChatterID(this.chatterID, resolvedName)
        }
    }

    fun bindViews(textView: TextView, chatterPicView: ChatterPicView) {
        this.nameTextView = textView
        this.picView = chatterPicView
        updateViews()
    }

     public fun getChatterID(): ChatterID {
        return this.chatterID
    }

     public fun getResolvedName(context: Context): String {
        val str: String = null
        if (this.nameRetriever != null) {
            str = this.nameRetriever.getResolvedName()
        }
        return str != null ? str : context.getString(R.string.name_loading_title)
    }

    fun onChatterNameUpdated(chatterNameRetriever: ChatterNameRetriever) {
        if (chatterNameRetriever == this.nameRetriever) {
            this.alreadyUpdated = true
            updateViews()
        }
    }

    fun setChatterID(chatterID2: ChatterID) {
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
