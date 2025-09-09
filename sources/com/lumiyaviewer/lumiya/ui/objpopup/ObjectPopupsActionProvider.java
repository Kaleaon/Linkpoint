package com.lumiyaviewer.lumiya.ui.objpopup;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.R;
import javax.annotation.Nullable;

public class ObjectPopupsActionProvider extends ActionProvider implements View.OnClickListener {
    private int objectPopupCount = 0;
    @Nullable
    private ObjectPopupsClickListener objectPopupsClickListener = null;
    @Nullable
    private TextView popupCountTextView = null;

    public interface ObjectPopupsClickListener {
        void onObjectPopupsClicked();
    }

    public ObjectPopupsActionProvider(Context context) {
        super(context);
    }

    public boolean isVisible() {
        return this.objectPopupCount != 0;
    }

    public void onClick(View view) {
        if (this.objectPopupsClickListener != null) {
            this.objectPopupsClickListener.onObjectPopupsClicked();
        }
    }

    public View onCreateActionView() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.object_popups_action_provider, (ViewGroup) null);
        this.popupCountTextView = (TextView) inflate.findViewById(R.id.popupCountTextView);
        if (this.popupCountTextView != null) {
            this.popupCountTextView.setText(Integer.toString(this.objectPopupCount));
        }
        inflate.setOnClickListener(this);
        return inflate;
    }

    public boolean overridesItemVisibility() {
        return true;
    }

    public void setObjectPopupCount(int i) {
        if (this.objectPopupCount != i) {
            this.objectPopupCount = i;
            if (this.popupCountTextView != null) {
                this.popupCountTextView.setText(Integer.toString(i));
            }
            refreshVisibility();
        }
    }

    public void setObjectPopupsClickListener(@Nullable ObjectPopupsClickListener objectPopupsClickListener2) {
        this.objectPopupsClickListener = objectPopupsClickListener2;
    }
}
