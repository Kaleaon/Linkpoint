// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.annotation.TargetApi;
import android.os.Build$VERSION;
import android.view.View$OnClickListener;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup$LayoutParams;
import android.widget.RelativeLayout$LayoutParams;
import android.content.Context;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class UiLayer
{
    private RelativeLayout alignmentMarker;
    private ImageButton backButton;
    private volatile Runnable backButtonRunnable;
    private final Context context;
    private volatile boolean isAlignmentMarkerEnabled;
    private volatile boolean isSettingsButtonEnabled;
    private RelativeLayout rootLayout;
    private ImageButton settingsButton;
    private volatile Runnable settingsButtonRunnable;
    private TransitionView transitionView;
    private volatile boolean transitionViewEnabled;
    private volatile String viewerName;
    
    public UiLayer(final Context context) {
        this.isSettingsButtonEnabled = true;
        this.isAlignmentMarkerEnabled = true;
        this.backButtonRunnable = null;
        this.transitionViewEnabled = false;
        this.context = context;
        this.initializeViewsWithLayoutId(R.layout.ui_layer);
    }
    
    private static int computeVisibility(final boolean b) {
        if (!b) {
            return 8;
        }
        return 0;
    }
    
    private TransitionView getTransitionView() {
        if (this.transitionView == null) {
            (this.transitionView = new TransitionView(this.context)).setLayoutParams((ViewGroup$LayoutParams)new RelativeLayout$LayoutParams(-1, -1));
            this.transitionView.setVisibility(computeVisibility(this.transitionViewEnabled));
            if (this.viewerName != null) {
                this.transitionView.setViewerName(this.viewerName);
            }
            this.transitionView.setBackButtonListener(this.backButtonRunnable);
            this.rootLayout.addView((View)this.transitionView);
        }
        return this.transitionView;
    }
    
    private void initializeViewsWithLayoutId(final int n) {
        this.rootLayout = (RelativeLayout)LayoutInflater.from(this.context).inflate(n, (ViewGroup)null, false);
        this.settingsButtonRunnable = new Runnable() {
            @Override
            public void run() {
                UiUtils.launchOrInstallCardboard(UiLayer.this.context);
            }
        };
        (this.settingsButton = (ImageButton)this.rootLayout.findViewById(R.id.ui_settings_button)).setVisibility(computeVisibility(this.isSettingsButtonEnabled));
        this.settingsButton.setContentDescription((CharSequence)"Settings");
        this.settingsButton.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                final Runnable access$100 = UiLayer.this.settingsButtonRunnable;
                if (access$100 != null) {
                    access$100.run();
                }
            }
        });
        (this.backButton = (ImageButton)this.rootLayout.findViewById(R.id.ui_back_button)).setVisibility(computeVisibility(this.getBackButtonEnabled()));
        this.backButton.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                final Runnable access$200 = UiLayer.this.backButtonRunnable;
                if (access$200 != null) {
                    access$200.run();
                }
            }
        });
        (this.alignmentMarker = (RelativeLayout)this.rootLayout.findViewById(R.id.ui_alignment_marker)).setVisibility(computeVisibility(this.getAlignmentMarkerEnabled()));
    }
    
    public boolean getAlignmentMarkerEnabled() {
        return this.isAlignmentMarkerEnabled;
    }
    
    public boolean getBackButtonEnabled() {
        return this.backButtonRunnable != null;
    }
    
    public Runnable getBackButtonRunnable() {
        return this.backButtonRunnable;
    }
    
    public boolean getSettingsButtonEnabled() {
        return this.isSettingsButtonEnabled;
    }
    
    public boolean getTransitionViewEnabled() {
        return this.transitionViewEnabled;
    }
    
    public View getView() {
        return (View)this.rootLayout;
    }
    
    public String getViewerName() {
        return this.viewerName;
    }
    
    public void setAlignmentMarkerEnabled(final boolean isAlignmentMarkerEnabled) {
        this.isAlignmentMarkerEnabled = isAlignmentMarkerEnabled;
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UiLayer.this.alignmentMarker.setVisibility(computeVisibility(isAlignmentMarkerEnabled));
            }
        });
    }
    
    @TargetApi(23)
    public void setAlignmentMarkerScale(final float n) {
        if (Build$VERSION.SDK_INT >= 23) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final RelativeLayout$LayoutParams layoutParams = (RelativeLayout$LayoutParams)UiLayer.this.alignmentMarker.getLayoutParams();
                    final int n = (int)((int)UiLayer.this.context.getResources().getDimension(R.dimen.alignment_marker_height) * n);
                    if (layoutParams.getRule(15) != -1) {
                        layoutParams.height = n;
                    }
                    else {
                        layoutParams.width = n;
                    }
                    UiLayer.this.alignmentMarker.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
                }
            });
        }
    }
    
    public void setBackButtonListener(final Runnable backButtonRunnable) {
        this.backButtonRunnable = backButtonRunnable;
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UiLayer.this.backButton.setVisibility(computeVisibility(backButtonRunnable != null));
                if (UiLayer.this.transitionView != null) {
                    UiLayer.this.transitionView.setBackButtonListener(backButtonRunnable);
                }
            }
        });
    }
    
    public void setCustomTransitionLayout(final int n, final int n2) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UiLayer.this.getTransitionView().setCustomTransitionLayout(n, n2);
            }
        });
    }
    
    public void setEnabled(final boolean b) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UiLayer.this.rootLayout.setVisibility(computeVisibility(b));
            }
        });
    }
    
    public void setPortraitSupportEnabled(final boolean b) {
        int n;
        if (!b) {
            n = R.layout.ui_layer;
        }
        else {
            n = R.layout.ui_layer_with_portrait_support;
        }
        this.initializeViewsWithLayoutId(n);
    }
    
    public void setSettingsButtonEnabled(final boolean isSettingsButtonEnabled) {
        this.isSettingsButtonEnabled = isSettingsButtonEnabled;
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UiLayer.this.settingsButton.setVisibility(computeVisibility(isSettingsButtonEnabled));
            }
        });
    }
    
    public void setSettingsButtonRunnable(final Runnable settingsButtonRunnable) {
        this.settingsButtonRunnable = settingsButtonRunnable;
    }
    
    public void setTransitionViewEnabled(final boolean transitionViewEnabled) {
        this.transitionViewEnabled = transitionViewEnabled;
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!transitionViewEnabled && UiLayer.this.transitionView == null) {
                    return;
                }
                UiLayer.this.getTransitionView().setVisibility(computeVisibility(transitionViewEnabled));
            }
        });
    }
    
    public void setTransitionViewListener(final TransitionView.TransitionListener transitionListener) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (transitionListener == null && UiLayer.this.transitionView == null) {
                    return;
                }
                UiLayer.this.getTransitionView().setTransitionListener(transitionListener);
            }
        });
    }
    
    public void setViewerName(final String viewerName) {
        this.viewerName = viewerName;
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (UiLayer.this.transitionView != null) {
                    UiLayer.this.transitionView.setViewerName(viewerName);
                }
            }
        });
    }
}
