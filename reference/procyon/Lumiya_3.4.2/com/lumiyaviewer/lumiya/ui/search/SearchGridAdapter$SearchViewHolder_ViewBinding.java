// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.search;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import butterknife.internal.Utils;
import android.widget.TextView;
import android.view.View;
import butterknife.Unbinder;

public class SearchGridAdapter$SearchViewHolder_ViewBinding implements Unbinder
{
    private SearchGridAdapter.SearchViewHolder target;
    
    @UiThread
    public SearchGridAdapter$SearchViewHolder_ViewBinding(final SearchGridAdapter.SearchViewHolder target, final View view) {
        this.target = target;
        target.resultItemName = Utils.findRequiredViewAsType(view, 2131755647, "field 'resultItemName'", TextView.class);
        target.userPicView = Utils.findRequiredViewAsType(view, 2131755327, "field 'userPicView'", ChatterPicView.class);
        target.resultMemberCount = Utils.findRequiredViewAsType(view, 2131755648, "field 'resultMemberCount'", TextView.class);
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final SearchGridAdapter.SearchViewHolder target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.resultItemName = null;
        target.userPicView = null;
        target.resultMemberCount = null;
    }
}
