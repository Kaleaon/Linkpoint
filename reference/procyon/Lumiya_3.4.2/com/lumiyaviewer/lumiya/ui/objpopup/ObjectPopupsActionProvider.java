// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.objpopup;

import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Context;
import android.widget.TextView;
import javax.annotation.Nullable;
import android.view.View$OnClickListener;
import android.support.v4.view.ActionProvider;

public class ObjectPopupsActionProvider extends ActionProvider implements View$OnClickListener
{
    private int objectPopupCount;
    @Nullable
    private ObjectPopupsClickListener objectPopupsClickListener;
    @Nullable
    private TextView popupCountTextView;
    
    public ObjectPopupsActionProvider(final Context context) {
        super(context);
        this.objectPopupCount = 0;
        this.popupCountTextView = null;
        this.objectPopupsClickListener = null;
    }
    
    @Override
    public boolean isVisible() {
        boolean b = false;
        if (this.objectPopupCount != 0) {
            b = true;
        }
        return b;
    }
    
    public void onClick(final View view) {
        if (this.objectPopupsClickListener != null) {
            this.objectPopupsClickListener.onObjectPopupsClicked();
        }
    }
    
    @Override
    public View onCreateActionView() {
        final View inflate = LayoutInflater.from(this.getContext()).inflate(2130968697, (ViewGroup)null);
        this.popupCountTextView = (TextView)inflate.findViewById(2131755582);
        if (this.popupCountTextView != null) {
            this.popupCountTextView.setText((CharSequence)Integer.toString(this.objectPopupCount));
        }
        inflate.setOnClickListener((View$OnClickListener)this);
        return inflate;
    }
    
    @Override
    public boolean overridesItemVisibility() {
        return true;
    }
    
    public void setObjectPopupCount(final int n) {
        if (this.objectPopupCount != n) {
            this.objectPopupCount = n;
            if (this.popupCountTextView != null) {
                this.popupCountTextView.setText((CharSequence)Integer.toString(n));
            }
            this.refreshVisibility();
        }
    }
    
    public void setObjectPopupsClickListener(@Nullable final ObjectPopupsClickListener objectPopupsClickListener) {
        this.objectPopupsClickListener = objectPopupsClickListener;
    }
    
    public interface ObjectPopupsClickListener
    {
        void onObjectPopupsClicked();
    }
}
