package com.lumiyaviewer.lumiya.ui.objpopup
import java.util.*

import android.content.Context
import androidx.core.view.ActionProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lumiyaviewer.lumiya.R
import androidx.annotation.Nullable

class ObjectPopupsActionProvider : ActionProvider : View.OnClickListener {
    private Int objectPopupCount = 0
    @Nullable
    private ObjectPopupsClickListener objectPopupsClickListener = null
    @Nullable
    private TextView popupCountTextView = null

    interface ObjectPopupsClickListener {
        Unit onObjectPopupsClicked()
    }

    ObjectPopupsActionProvider(Context context) {
        super(context)
    }

    Boolean isVisible() {
        return this.objectPopupCount != 0
    }

    Unit onClick(View view) {
        if (this.objectPopupsClickListener != null) {
            this.objectPopupsClickListener.onObjectPopupsClicked()
        }
    }

    View onCreateActionView() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.object_popups_action_provider, (ViewGroup) null)
        this.popupCountTextView = (TextView) inflate.findViewById(R.id.popupCountTextView)
        if (this.popupCountTextView != null) {
            this.popupCountTextView.setText(Int.toString(this.objectPopupCount))
        }
        inflate.setOnClickListener(this)
        return inflate
    }

    Boolean overridesItemVisibility() {
        return true
    }

    Unit setObjectPopupCount(Int i) {
        if (this.objectPopupCount != i) {
            this.objectPopupCount = i
            if (this.popupCountTextView != null) {
                this.popupCountTextView.setText(Int.toString(i))
            }
            refreshVisibility()
        }
    }

    Unit setObjectPopupsClickListener(@Nullable ObjectPopupsClickListener objectPopupsClickListener2) {
        this.objectPopupsClickListener = objectPopupsClickListener2
    }
}
