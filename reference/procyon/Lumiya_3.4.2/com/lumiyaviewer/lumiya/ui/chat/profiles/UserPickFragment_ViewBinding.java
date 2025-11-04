// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import android.view.View$OnClickListener;
import butterknife.internal.DebouncingOnClickListener;
import android.widget.Button;
import butterknife.internal.Utils;
import android.view.View;
import butterknife.Unbinder;

public class UserPickFragment_ViewBinding implements Unbinder
{
    private UserPickFragment target;
    private View view2131755696;
    private View view2131755697;
    private View view2131755698;
    private View view2131755700;
    
    @UiThread
    public UserPickFragment_ViewBinding(final UserPickFragment target, View requiredView) {
        this.target = target;
        final View requiredView2 = Utils.findRequiredView(requiredView, 2131755696, "field 'setLocationButton' and method 'onSetLocation'");
        target.setLocationButton = Utils.castView(requiredView2, 2131755696, "field 'setLocationButton'", Button.class);
        (this.view2131755696 = requiredView2).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onSetLocation(view);
            }
        });
        final View requiredView3 = Utils.findRequiredView(requiredView, 2131755698, "field 'changePicButton' and method 'onChangePic'");
        target.changePicButton = Utils.castView(requiredView3, 2131755698, "field 'changePicButton'", Button.class);
        (this.view2131755698 = requiredView3).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onChangePic(view);
            }
        });
        target.userPickImageView = Utils.findRequiredViewAsType(requiredView, 2131755694, "field 'userPickImageView'", ImageAssetView.class);
        target.pickDescription = Utils.findRequiredViewAsType(requiredView, 2131755699, "field 'pickDescription'", TextView.class);
        final View requiredView4 = Utils.findRequiredView(requiredView, 2131755700, "field 'userPickDescEditButton' and method 'onDescEdit'");
        target.userPickDescEditButton = Utils.castView(requiredView4, 2131755700, "field 'userPickDescEditButton'", Button.class);
        (this.view2131755700 = requiredView4).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onDescEdit(view);
            }
        });
        requiredView = Utils.findRequiredView(requiredView, 2131755697, "method 'onTeleportToPickClick'");
        (this.view2131755697 = requiredView).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onTeleportToPickClick(view);
            }
        });
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final UserPickFragment target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.setLocationButton = null;
        target.changePicButton = null;
        target.userPickImageView = null;
        target.pickDescription = null;
        target.userPickDescEditButton = null;
        this.view2131755696.setOnClickListener((View$OnClickListener)null);
        this.view2131755696 = null;
        this.view2131755698.setOnClickListener((View$OnClickListener)null);
        this.view2131755698 = null;
        this.view2131755700.setOnClickListener((View$OnClickListener)null);
        this.view2131755700 = null;
        this.view2131755697.setOnClickListener((View$OnClickListener)null);
        this.view2131755697 = null;
    }
}
