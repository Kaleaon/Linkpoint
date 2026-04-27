// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.voice;

import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.voice:
//            VoiceStatusView

public class VoiceStatusView_ViewBinding
    implements Unbinder
{

    private VoiceStatusView target;
    private View view2131755728;
    private View view2131755729;
    private View view2131755730;
    private View view2131755731;
    private View view2131755736;
    private View view2131755739;
    private View view2131755740;

    public VoiceStatusView_ViewBinding(VoiceStatusView voicestatusview)
    {
        this(voicestatusview, ((View) (voicestatusview)));
    }

    public VoiceStatusView_ViewBinding(final VoiceStatusView target, View view)
    {
        this.target = target;
        View view1 = Utils.findRequiredView(view, 0x7f1002d3, "field 'voiceAnswerButton' and method 'onVoiceAnswerButton'");
        target.voiceAnswerButton = (ImageButton)Utils.castView(view1, 0x7f1002d3, "field 'voiceAnswerButton'", android/widget/ImageButton);
        view2131755731 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final VoiceStatusView_ViewBinding this$0;
            final VoiceStatusView val$target;

            public void doClick(View view2)
            {
                target.onVoiceAnswerButton();
            }

            
            {
                this$0 = VoiceStatusView_ViewBinding.this;
                target = voicestatusview;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f1002d8, "field 'voiceTerminateButton' and method 'onVoiceTerminateButton'");
        target.voiceTerminateButton = (ImageButton)Utils.castView(view1, 0x7f1002d8, "field 'voiceTerminateButton'", android/widget/ImageButton);
        view2131755736 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final VoiceStatusView_ViewBinding this$0;
            final VoiceStatusView val$target;

            public void doClick(View view2)
            {
                target.onVoiceTerminateButton();
            }

            
            {
                this$0 = VoiceStatusView_ViewBinding.this;
                target = voicestatusview;
                super();
            }
        });
        target.voiceStatusSmallText = (TextView)Utils.findRequiredViewAsType(view, 0x7f1002d6, "field 'voiceStatusSmallText'", android/widget/TextView);
        view1 = Utils.findRequiredView(view, 0x7f1002dc, "field 'voiceBluetoothButton' and method 'onVoiceBluetoothButton'");
        target.voiceBluetoothButton = (Button)Utils.castView(view1, 0x7f1002dc, "field 'voiceBluetoothButton'", android/widget/Button);
        view2131755740 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final VoiceStatusView_ViewBinding this$0;
            final VoiceStatusView val$target;

            public void doClick(View view2)
            {
                target.onVoiceBluetoothButton();
            }

            
            {
                this$0 = VoiceStatusView_ViewBinding.this;
                target = voicestatusview;
                super();
            }
        });
        target.voiceSpeakIndicatorLeft = (ImageView)Utils.findRequiredViewAsType(view, 0x7f1002d4, "field 'voiceSpeakIndicatorLeft'", android/widget/ImageView);
        view1 = Utils.findRequiredView(view, 0x7f1002d1, "field 'voiceMicOnButton' and method 'onVoiceMicOnButton'");
        target.voiceMicOnButton = (ImageButton)Utils.castView(view1, 0x7f1002d1, "field 'voiceMicOnButton'", android/widget/ImageButton);
        view2131755729 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final VoiceStatusView_ViewBinding this$0;
            final VoiceStatusView val$target;

            public void doClick(View view2)
            {
                target.onVoiceMicOnButton();
            }

            
            {
                this$0 = VoiceStatusView_ViewBinding.this;
                target = voicestatusview;
                super();
            }
        });
        target.voiceStatusControls = (ViewGroup)Utils.findRequiredViewAsType(view, 0x7f1002d9, "field 'voiceStatusControls'", android/view/ViewGroup);
        view1 = Utils.findRequiredView(view, 0x7f1002db, "field 'voiceLoudspeakerButton' and method 'onLoudspeakerButton'");
        target.voiceLoudspeakerButton = (Button)Utils.castView(view1, 0x7f1002db, "field 'voiceLoudspeakerButton'", android/widget/Button);
        view2131755739 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final VoiceStatusView_ViewBinding this$0;
            final VoiceStatusView val$target;

            public void doClick(View view2)
            {
                target.onLoudspeakerButton();
            }

            
            {
                this$0 = VoiceStatusView_ViewBinding.this;
                target = voicestatusview;
                super();
            }
        });
        target.voiceStatusText = (TextView)Utils.findRequiredViewAsType(view, 0x7f1002d5, "field 'voiceStatusText'", android/widget/TextView);
        target.voiceSpeakIndicatorRight = (ImageView)Utils.findRequiredViewAsType(view, 0x7f1002d7, "field 'voiceSpeakIndicatorRight'", android/widget/ImageView);
        view1 = Utils.findRequiredView(view, 0x7f1002d2, "field 'voiceMicOffButton' and method 'onVoiceMicOffButton'");
        target.voiceMicOffButton = (ImageButton)Utils.castView(view1, 0x7f1002d2, "field 'voiceMicOffButton'", android/widget/ImageButton);
        view2131755730 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final VoiceStatusView_ViewBinding this$0;
            final VoiceStatusView val$target;

            public void doClick(View view2)
            {
                target.onVoiceMicOffButton();
            }

            
            {
                this$0 = VoiceStatusView_ViewBinding.this;
                target = voicestatusview;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f1002d0, "field 'voiceStatusCardView' and method 'onVoiceStatusCardClick'");
        target.voiceStatusCardView = (CardView)Utils.castView(view1, 0x7f1002d0, "field 'voiceStatusCardView'", android/support/v7/widget/CardView);
        view2131755728 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final VoiceStatusView_ViewBinding this$0;
            final VoiceStatusView val$target;

            public void doClick(View view2)
            {
                target.onVoiceStatusCardClick();
            }

            
            {
                this$0 = VoiceStatusView_ViewBinding.this;
                target = voicestatusview;
                super();
            }
        });
        target.voiceSpeakerVolumeControl = (SeekBar)Utils.findRequiredViewAsType(view, 0x7f1002da, "field 'voiceSpeakerVolumeControl'", android/widget/SeekBar);
    }

    public void unbind()
    {
        VoiceStatusView voicestatusview = target;
        if (voicestatusview == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            voicestatusview.voiceAnswerButton = null;
            voicestatusview.voiceTerminateButton = null;
            voicestatusview.voiceStatusSmallText = null;
            voicestatusview.voiceBluetoothButton = null;
            voicestatusview.voiceSpeakIndicatorLeft = null;
            voicestatusview.voiceMicOnButton = null;
            voicestatusview.voiceStatusControls = null;
            voicestatusview.voiceLoudspeakerButton = null;
            voicestatusview.voiceStatusText = null;
            voicestatusview.voiceSpeakIndicatorRight = null;
            voicestatusview.voiceMicOffButton = null;
            voicestatusview.voiceStatusCardView = null;
            voicestatusview.voiceSpeakerVolumeControl = null;
            view2131755731.setOnClickListener(null);
            view2131755731 = null;
            view2131755736.setOnClickListener(null);
            view2131755736 = null;
            view2131755740.setOnClickListener(null);
            view2131755740 = null;
            view2131755729.setOnClickListener(null);
            view2131755729 = null;
            view2131755739.setOnClickListener(null);
            view2131755739 = null;
            view2131755730.setOnClickListener(null);
            view2131755730 = null;
            view2131755728.setOnClickListener(null);
            view2131755728 = null;
            return;
        }
    }
}
