// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.search;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View$OnClickListener;
import butterknife.internal.DebouncingOnClickListener;
import android.widget.RadioGroup;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.widget.TextView$OnEditorActionListener;
import android.widget.TextView;
import android.widget.EditText;
import butterknife.internal.Utils;
import android.view.View;
import butterknife.Unbinder;

public class SearchGridFragment_ViewBinding implements Unbinder
{
    private SearchGridFragment target;
    private View view2131755639;
    private View view2131755640;
    
    @UiThread
    public SearchGridFragment_ViewBinding(final SearchGridFragment target, View requiredView) {
        this.target = target;
        final View requiredView2 = Utils.findRequiredView(requiredView, 2131755639, "field 'searchString' and method 'onSearchTextAction'");
        target.searchString = Utils.castView(requiredView2, 2131755639, "field 'searchString'", EditText.class);
        this.view2131755639 = requiredView2;
        ((TextView)requiredView2).setOnEditorActionListener((TextView$OnEditorActionListener)new TextView$OnEditorActionListener() {
            public boolean onEditorAction(final TextView textView, final int n, final KeyEvent keyEvent) {
                return target.onSearchTextAction(n, keyEvent);
            }
        });
        target.searchResultsList = Utils.findRequiredViewAsType(requiredView, 2131755645, "field 'searchResultsList'", RecyclerView.class);
        target.radioGroupSearchType = Utils.findRequiredViewAsType(requiredView, 2131755641, "field 'radioGroupSearchType'", RadioGroup.class);
        requiredView = Utils.findRequiredView(requiredView, 2131755640, "method 'onSearchButtonClicked'");
        (this.view2131755640 = requiredView).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onSearchButtonClicked();
            }
        });
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final SearchGridFragment target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.searchString = null;
        target.searchResultsList = null;
        target.radioGroupSearchType = null;
        ((TextView)this.view2131755639).setOnEditorActionListener((TextView$OnEditorActionListener)null);
        this.view2131755639 = null;
        this.view2131755640.setOnClickListener((View$OnClickListener)null);
        this.view2131755640 = null;
    }
}
