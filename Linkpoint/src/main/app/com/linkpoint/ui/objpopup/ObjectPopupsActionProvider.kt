package com.linkpoint.ui.objpopup
import java.util.*

import android.content.Context
import androidx.core.view.ActionProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.linkpoint.R
import androidx.annotation.Nullable

class ObjectPopupsActionProvider : ActionProvider : View.OnClickListener {
    private Int objectPopupCount = 0
    @Nullable
    private ObjectPopupsClickListener objectPopupsClickListener = null
    @Nullable
    private TextView popupCountTextView = null

    interface ObjectPopupsClickListener {
        fun onObjectPopupsClicked()
    }

    ObjectPopupsActionProvider(Context context) {
        super(context)
    }

    fun isVisible(): Boolean {
        return this.objectPopupCount != 0
    }

    fun onClick(View view): Unit {
        if (this.objectPopupsClickListener != null) {
            this.objectPopupsClickListener.onObjectPopupsClicked()
        }
    }

    fun onCreateActionView(): View {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.object_popups_action_provider, (ViewGroup) null)
        this.popupCountTextView = (inflate as TextView).findViewById(R.id.popupCountTextView)
        if (this.popupCountTextView != null) {
            this.popupCountTextView.setText(Int.toString(this.objectPopupCount))
        }
        inflate.setOnClickListener(this)
        return inflate
    }

    fun overridesItemVisibility(): Boolean {
        return true
    }

    fun setObjectPopupCount(Int i): Unit {
        if (this.objectPopupCount != i) {
            this.objectPopupCount = i
            if (this.popupCountTextView != null) {
                this.popupCountTextView.setText(Int.toString(i))
            }
            refreshVisibility()
        }
    }

    fun setObjectPopupsClickListener(@Nullable ObjectPopupsClickListener objectPopupsClickListener2): Unit {
        this.objectPopupsClickListener = objectPopupsClickListener2
    }
}
