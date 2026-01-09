package com.linkpoint.ui.common

import android.content.Context
import android.widget.TextView
import com.google.common.base.Objects
import com.linkpoint.R
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.ChatterNameRetriever
import com.linkpoint.ui.chat.ChatterPicView
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class ChatterNameDisplayer : ChatterNameRetriever.OnChatterNameUpdated {
    private Boolean alreadyUpdated = false
    @Nullable
    private ChatterID chatterID = null
    @Nullable
    private ChatterNameRetriever nameRetriever = null
    @Nullable
    private TextView nameTextView = null
    @Nullable
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
        var resolvedName: String = this.nameRetriever.getResolvedName()
        if (this.nameTextView != null) {
            this.nameTextView.setText(resolvedName != null ? resolvedName : this.nameTextView.getContext().getString(R.string.name_loading_title))
        }
        if (this.picView != null) {
            this.picView.setChatterID(this.chatterID, resolvedName)
        }
    }

    fun bindViews(@Nullable TextView textView, @Nullable ChatterPicView chatterPicView)  {
        this.nameTextView = textView
        this.picView = chatterPicView
        updateViews()
    }

    @Nullable
    fun getChatterID(): ChatterID {
        return this.chatterID
    }

    @NonNull
    fun getResolvedName(Context context): String {
        var str: String = null
        if (this.nameRetriever != null) {
            str = this.nameRetriever.getResolvedName()
        }
        return str != null ? str : context.getString(R.string.name_loading_title)
    }

    fun onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever)  {
        if (chatterNameRetriever == this.nameRetriever) {
            this.alreadyUpdated = true
            updateViews()
        }
    }

    fun setChatterID(@Nullable ChatterID chatterID2)  {
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

    fun unbindViews()  {
        this.nameTextView = null
        this.picView = null
    }
}
