// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.search;

import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.search:
//            ParcelInfoFragment

public class ParcelInfoFragment_ViewBinding
    implements Unbinder
{

    private ParcelInfoFragment target;
    private View view2131755600;
    private View view2131755608;

    public ParcelInfoFragment_ViewBinding(final ParcelInfoFragment target, View view)
    {
        this.target = target;
        target.parcelImageView = (ImageAssetView)Utils.findRequiredViewAsType(view, 0x7f100252, "field 'parcelImageView'", com/lumiyaviewer/lumiya/ui/common/ImageAssetView);
        target.parcelDetailsDescription = (TextView)Utils.findRequiredViewAsType(view, 0x7f10024f, "field 'parcelDetailsDescription'", android/widget/TextView);
        target.parcelOwnerName = (TextView)Utils.findRequiredViewAsType(view, 0x7f100256, "field 'parcelOwnerName'", android/widget/TextView);
        target.parcelOwnerPic = (ChatterPicView)Utils.findRequiredViewAsType(view, 0x7f100257, "field 'parcelOwnerPic'", com/lumiyaviewer/lumiya/ui/chat/ChatterPicView);
        target.parcelSimName = (TextView)Utils.findRequiredViewAsType(view, 0x7f100254, "field 'parcelSimName'", android/widget/TextView);
        target.parcelDetailsName = (TextView)Utils.findRequiredViewAsType(view, 0x7f10024e, "field 'parcelDetailsName'", android/widget/TextView);
        target.parcelLocation = (TextView)Utils.findRequiredViewAsType(view, 0x7f100255, "field 'parcelLocation'", android/widget/TextView);
        View view1 = Utils.findRequiredView(view, 0x7f100250, "method 'onParcelTeleportButton'");
        view2131755600 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final ParcelInfoFragment_ViewBinding this$0;
            final ParcelInfoFragment val$target;

            public void doClick(View view2)
            {
                target.onParcelTeleportButton();
            }

            
            {
                this$0 = ParcelInfoFragment_ViewBinding.this;
                target = parcelinfofragment;
                super();
            }
        });
        view = Utils.findRequiredView(view, 0x7f100258, "method 'onParcelOwnerProfileClick'");
        view2131755608 = view;
        view.setOnClickListener(new DebouncingOnClickListener() {

            final ParcelInfoFragment_ViewBinding this$0;
            final ParcelInfoFragment val$target;

            public void doClick(View view2)
            {
                target.onParcelOwnerProfileClick();
            }

            
            {
                this$0 = ParcelInfoFragment_ViewBinding.this;
                target = parcelinfofragment;
                super();
            }
        });
    }

    public void unbind()
    {
        ParcelInfoFragment parcelinfofragment = target;
        if (parcelinfofragment == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            parcelinfofragment.parcelImageView = null;
            parcelinfofragment.parcelDetailsDescription = null;
            parcelinfofragment.parcelOwnerName = null;
            parcelinfofragment.parcelOwnerPic = null;
            parcelinfofragment.parcelSimName = null;
            parcelinfofragment.parcelDetailsName = null;
            parcelinfofragment.parcelLocation = null;
            view2131755600.setOnClickListener(null);
            view2131755600 = null;
            view2131755608.setOnClickListener(null);
            view2131755608 = null;
            return;
        }
    }
}
