// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            ParcelPropertiesFragment

public class ParcelPropertiesFragment_ViewBinding
    implements Unbinder
{

    private ParcelPropertiesFragment target;
    private View view2131755608;
    private View view2131755611;
    private View view2131755614;
    private View view2131755615;
    private View view2131755617;

    public ParcelPropertiesFragment_ViewBinding(final ParcelPropertiesFragment target, View view)
    {
        this.target = target;
        target.parcelMediaURL = (TextView)Utils.findRequiredViewAsType(view, 0x7f10025d, "field 'parcelMediaURL'", android/widget/TextView);
        View view1 = Utils.findRequiredView(view, 0x7f10025f, "field 'mediaStopButton' and method 'onParcelMediaStop'");
        target.mediaStopButton = (Button)Utils.castView(view1, 0x7f10025f, "field 'mediaStopButton'", android/widget/Button);
        view2131755615 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final ParcelPropertiesFragment_ViewBinding this$0;
            final ParcelPropertiesFragment val$target;

            public void doClick(View view2)
            {
                target.onParcelMediaStop();
            }

            
            {
                this$0 = ParcelPropertiesFragment_ViewBinding.this;
                target = parcelpropertiesfragment;
                super();
            }
        });
        target.parcelMediaCardView = (CardView)Utils.findRequiredViewAsType(view, 0x7f10025c, "field 'parcelMediaCardView'", android/support/v7/widget/CardView);
        target.simRestartCardView = (CardView)Utils.findRequiredViewAsType(view, 0x7f100260, "field 'simRestartCardView'", android/support/v7/widget/CardView);
        target.parcelName = (TextView)Utils.findRequiredViewAsType(view, 0x7f100259, "field 'parcelName'", android/widget/TextView);
        target.parcelOwnerPic = (ChatterPicView)Utils.findRequiredViewAsType(view, 0x7f100257, "field 'parcelOwnerPic'", com/lumiyaviewer/lumiya/ui/chat/ChatterPicView);
        target.parcelArea = (TextView)Utils.findRequiredViewAsType(view, 0x7f10025a, "field 'parcelArea'", android/widget/TextView);
        target.parcelOwnerName = (TextView)Utils.findRequiredViewAsType(view, 0x7f100256, "field 'parcelOwnerName'", android/widget/TextView);
        view1 = Utils.findRequiredView(view, 0x7f10025e, "field 'mediaPlayButton' and method 'onParcelMediaPlay'");
        target.mediaPlayButton = (Button)Utils.castView(view1, 0x7f10025e, "field 'mediaPlayButton'", android/widget/Button);
        view2131755614 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final ParcelPropertiesFragment_ViewBinding this$0;
            final ParcelPropertiesFragment val$target;

            public void doClick(View view2)
            {
                target.onParcelMediaPlay();
            }

            
            {
                this$0 = ParcelPropertiesFragment_ViewBinding.this;
                target = parcelpropertiesfragment;
                super();
            }
        });
        target.parcelImageView = (ImageAssetView)Utils.findRequiredViewAsType(view, 0x7f100252, "field 'parcelImageView'", com/lumiyaviewer/lumiya/ui/common/ImageAssetView);
        target.parcelDescription = (TextView)Utils.findRequiredViewAsType(view, 0x7f10024f, "field 'parcelDescription'", android/widget/TextView);
        view1 = Utils.findRequiredView(view, 0x7f100258, "method 'onOwnerProfileButton'");
        view2131755608 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final ParcelPropertiesFragment_ViewBinding this$0;
            final ParcelPropertiesFragment val$target;

            public void doClick(View view2)
            {
                target.onOwnerProfileButton();
            }

            
            {
                this$0 = ParcelPropertiesFragment_ViewBinding.this;
                target = parcelpropertiesfragment;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f100261, "method 'onSimRestartButton'");
        view2131755617 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final ParcelPropertiesFragment_ViewBinding this$0;
            final ParcelPropertiesFragment val$target;

            public void doClick(View view2)
            {
                target.onSimRestartButton();
            }

            
            {
                this$0 = ParcelPropertiesFragment_ViewBinding.this;
                target = parcelpropertiesfragment;
                super();
            }
        });
        view = Utils.findRequiredView(view, 0x7f10025b, "method 'onSetHomeButton'");
        view2131755611 = view;
        view.setOnClickListener(new DebouncingOnClickListener() {

            final ParcelPropertiesFragment_ViewBinding this$0;
            final ParcelPropertiesFragment val$target;

            public void doClick(View view2)
            {
                target.onSetHomeButton();
            }

            
            {
                this$0 = ParcelPropertiesFragment_ViewBinding.this;
                target = parcelpropertiesfragment;
                super();
            }
        });
    }

    public void unbind()
    {
        ParcelPropertiesFragment parcelpropertiesfragment = target;
        if (parcelpropertiesfragment == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            parcelpropertiesfragment.parcelMediaURL = null;
            parcelpropertiesfragment.mediaStopButton = null;
            parcelpropertiesfragment.parcelMediaCardView = null;
            parcelpropertiesfragment.simRestartCardView = null;
            parcelpropertiesfragment.parcelName = null;
            parcelpropertiesfragment.parcelOwnerPic = null;
            parcelpropertiesfragment.parcelArea = null;
            parcelpropertiesfragment.parcelOwnerName = null;
            parcelpropertiesfragment.mediaPlayButton = null;
            parcelpropertiesfragment.parcelImageView = null;
            parcelpropertiesfragment.parcelDescription = null;
            view2131755615.setOnClickListener(null);
            view2131755615 = null;
            view2131755614.setOnClickListener(null);
            view2131755614 = null;
            view2131755608.setOnClickListener(null);
            view2131755608 = null;
            view2131755617.setOnClickListener(null);
            view2131755617 = null;
            view2131755611.setOnClickListener(null);
            view2131755611 = null;
            return;
        }
    }
}
