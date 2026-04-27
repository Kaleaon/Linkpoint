// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.myava;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import android.view.View;
import butterknife.Unbinder;

public class MyAvatarFragment_ViewBinding implements Unbinder
{
    private MyAvatarFragment target;
    
    @UiThread
    public MyAvatarFragment_ViewBinding(final MyAvatarFragment target, final View view) {
        this.target = target;
        target.myAvatarPic = Utils.findRequiredViewAsType(view, 2131755498, "field 'myAvatarPic'", ChatterPicView.class);
        target.myAvatarName = Utils.findRequiredViewAsType(view, 2131755497, "field 'myAvatarName'", TextView.class);
        target.myAvatarOptionsList = Utils.findRequiredViewAsType(view, 2131755499, "field 'myAvatarOptionsList'", ListView.class);
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final MyAvatarFragment target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.myAvatarPic = null;
        target.myAvatarName = null;
        target.myAvatarOptionsList = null;
    }
}
