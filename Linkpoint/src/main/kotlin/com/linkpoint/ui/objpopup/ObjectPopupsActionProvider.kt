package com.linkpoint.ui.objpopup
import java.util.*

import android.content.Context
import android.support.v4.view.ActionProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.linkpoint.R
import javax.annotation.Nullable

class ObjectPopupsActionProvider : ActionProvider(), View.OnClickListener {
    private Int objectPopupCount = 0
    private ObjectPopupsClickListener objectPopupsClickListener = null
    private TextView popupCountTextView = null

    interface ObjectPopupsClickListener {
         fun onObjectPopupsClicked()
    }

    public ObjectPopupsActionProvider(Context context) {
        super(context)
    }

     public fun isVisible(): Boolean {
        return this.objectPopupCount != 0
    }

    override fun onClick(view: View) {
        if (this.objectPopupsClickListener != null) {
            this.objectPopupsClickListener.onObjectPopupsClicked()
        }
    }

     public fun onCreateActionView(): View {
        val inflate: View = LayoutInflater.from(getContext()).inflate(R.layout.object_popups_action_provider, (ViewGroup) null)
        this.popupCountTextView = (TextView) inflate.findViewById(R.id.popupCountTextView)
        if (this.popupCountTextView != null) {
            this.popupCountTextView.setText(Integer.toString(this.objectPopupCount))
        }
        inflate.setOnClickListener(this)
        return inflate
    }

     public fun overridesItemVisibility(): Boolean {
        return true
    }

    fun setObjectPopupCount(i: Int) {
        if (this.objectPopupCount != i) {
            this.objectPopupCount = i
            if (this.popupCountTextView != null) {
                this.popupCountTextView.setText(Integer.toString(i))
            }
            refreshVisibility()
        }
    }

    fun setObjectPopupsClickListener(objectPopupsClickListener2: ObjectPopupsClickListener) {
        this.objectPopupsClickListener = objectPopupsClickListener2
    }
}
