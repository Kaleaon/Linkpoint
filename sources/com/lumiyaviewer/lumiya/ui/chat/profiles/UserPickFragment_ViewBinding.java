// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            UserPickFragment

public class UserPickFragment_ViewBinding
    implements Unbinder
{

    private UserPickFragment target;
    private View view2131755696;
    private View view2131755697;
    private View view2131755698;
    private View view2131755700;

    public UserPickFragment_ViewBinding(final UserPickFragment target, View view)
    {
        this.target = target;
        View view1 = Utils.findRequiredView(view, 0x7f1002b0, "field 'setLocationButton' and method 'onSetLocation'");
        target.setLocationButton = (Button)Utils.castView(view1, 0x7f1002b0, "field 'setLocationButton'", android/widget/Button);
        view2131755696 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final UserPickFragment_ViewBinding this$0;
            final UserPickFragment val$target;

            public void doClick(View view2)
            {
                target.onSetLocation(view2);
            }

            
            {
                this$0 = UserPickFragment_ViewBinding.this;
                target = userpickfragment;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f1002b2, "field 'changePicButton' and method 'onChangePic'");
        target.changePicButton = (Button)Utils.castView(view1, 0x7f1002b2, "field 'changePicButton'", android/widget/Button);
        view2131755698 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final UserPickFragment_ViewBinding this$0;
            final UserPickFragment val$target;

            public void doClick(View view2)
            {
                target.onChangePic(view2);
            }

            
            {
                this$0 = UserPickFragment_ViewBinding.this;
                target = userpickfragment;
                super();
            }
        });
        target.userPickImageView = (ImageAssetView)Utils.findRequiredViewAsType(view, 0x7f1002ae, "field 'userPickImageView'", com/lumiyaviewer/lumiya/ui/common/ImageAssetView);
        target.pickDescription = (TextView)Utils.findRequiredViewAsType(view, 0x7f1002b3, "field 'pickDescription'", android/widget/TextView);
        view1 = Utils.findRequiredView(view, 0x7f1002b4, "field 'userPickDescEditButton' and method 'onDescEdit'");
        target.userPickDescEditButton = (Button)Utils.castView(view1, 0x7f1002b4, "field 'userPickDescEditButton'", android/widget/Button);
        view2131755700 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final UserPickFragment_ViewBinding this$0;
            final UserPickFragment val$target;

            public void doClick(View view2)
            {
                target.onDescEdit(view2);
            }

            
            {
                this$0 = UserPickFragment_ViewBinding.this;
                target = userpickfragment;
                super();
            }
        });
        view = Utils.findRequiredView(view, 0x7f1002b1, "method 'onTeleportToPickClick'");
        view2131755697 = view;
        view.setOnClickListener(new DebouncingOnClickListener() {

            final UserPickFragment_ViewBinding this$0;
            final UserPickFragment val$target;

            public void doClick(View view2)
            {
                target.onTeleportToPickClick(view2);
            }

            
            {
                this$0 = UserPickFragment_ViewBinding.this;
                target = userpickfragment;
                super();
            }
        });
    }

    public void unbind()
    {
        UserPickFragment userpickfragment = target;
        if (userpickfragment == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            userpickfragment.setLocationButton = null;
            userpickfragment.changePicButton = null;
            userpickfragment.userPickImageView = null;
            userpickfragment.pickDescription = null;
            userpickfragment.userPickDescEditButton = null;
            view2131755696.setOnClickListener(null);
            view2131755696 = null;
            view2131755698.setOnClickListener(null);
            view2131755698 = null;
            view2131755700.setOnClickListener(null);
            view2131755700 = null;
            view2131755697.setOnClickListener(null);
            view2131755697 = null;
            return;
        }
    }
}
