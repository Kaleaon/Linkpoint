package com.google.vr.cardboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.google.vr.cardboard.TransitionView;
/* loaded from: classes.dex */
public class UiLayer {
    private RelativeLayout alignmentMarker;
    private ImageButton backButton;
    private final Context context;
    private RelativeLayout rootLayout;
    private ImageButton settingsButton;
    private volatile Runnable settingsButtonRunnable;
    private TransitionView transitionView;
    private volatile String viewerName;
    private volatile boolean isSettingsButtonEnabled = true;
    private volatile boolean isAlignmentMarkerEnabled = true;
    private volatile Runnable backButtonRunnable = null;
    private volatile boolean transitionViewEnabled = false;

    public UiLayer(Context context) {
        this.context = context;
        initializeViewsWithLayoutId(R.layout.ui_layer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int computeVisibility(boolean z) {
        return !z ? 8 : 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TransitionView getTransitionView() {
        if (this.transitionView == null) {
            this.transitionView = new TransitionView(this.context);
            this.transitionView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
            this.transitionView.setVisibility(computeVisibility(this.transitionViewEnabled));
            if (this.viewerName != null) {
                this.transitionView.setViewerName(this.viewerName);
            }
            this.transitionView.setBackButtonListener(this.backButtonRunnable);
            this.rootLayout.addView(this.transitionView);
        }
        return this.transitionView;
    }

    private void initializeViewsWithLayoutId(int i) {
        this.rootLayout = (RelativeLayout) LayoutInflater.from(this.context).inflate(i, (ViewGroup) null, false);
        this.settingsButtonRunnable = new Runnable() { // from class: com.google.vr.cardboard.UiLayer.1
            @Override // java.lang.Runnable
            public void run() {
                UiUtils.launchOrInstallCardboard(UiLayer.this.context);
            }
        };
        this.settingsButton = (ImageButton) this.rootLayout.findViewById(R.id.ui_settings_button);
        this.settingsButton.setVisibility(computeVisibility(this.isSettingsButtonEnabled));
        this.settingsButton.setContentDescription("Settings");
        this.settingsButton.setOnClickListener(new View.OnClickListener() { // from class: com.google.vr.cardboard.UiLayer.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Runnable runnable = UiLayer.this.settingsButtonRunnable;
                if (runnable == null) {
                    return;
                }
                runnable.run();
            }
        });
        this.backButton = (ImageButton) this.rootLayout.findViewById(R.id.ui_back_button);
        this.backButton.setVisibility(computeVisibility(getBackButtonEnabled()));
        this.backButton.setOnClickListener(new View.OnClickListener() { // from class: com.google.vr.cardboard.UiLayer.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Runnable runnable = UiLayer.this.backButtonRunnable;
                if (runnable == null) {
                    return;
                }
                runnable.run();
            }
        });
        this.alignmentMarker = (RelativeLayout) this.rootLayout.findViewById(R.id.ui_alignment_marker);
        this.alignmentMarker.setVisibility(computeVisibility(getAlignmentMarkerEnabled()));
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
        return this.rootLayout;
    }

    public String getViewerName() {
        return this.viewerName;
    }

    public void setAlignmentMarkerEnabled(final boolean z) {
        this.isAlignmentMarkerEnabled = z;
        ThreadUtils.runOnUiThread(new Runnable() { // from class: com.google.vr.cardboard.UiLayer.7
            @Override // java.lang.Runnable
            public void run() {
                UiLayer.this.alignmentMarker.setVisibility(UiLayer.computeVisibility(z));
            }
        });
    }

    @TargetApi(23)
    public void setAlignmentMarkerScale(final float f) {
        if (Build.VERSION.SDK_INT >= 23) {
            ThreadUtils.runOnUiThread(new Runnable() { // from class: com.google.vr.cardboard.UiLayer.8
                @Override // java.lang.Runnable
                public void run() {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) UiLayer.this.alignmentMarker.getLayoutParams();
                    int dimension = (int) (((int) UiLayer.this.context.getResources().getDimension(R.dimen.alignment_marker_height)) * f);
                    if (layoutParams.getRule(15) != -1) {
                        layoutParams.height = dimension;
                    } else {
                        layoutParams.width = dimension;
                    }
                    UiLayer.this.alignmentMarker.setLayoutParams(layoutParams);
                }
            });
        }
    }

    public void setBackButtonListener(final Runnable runnable) {
        this.backButtonRunnable = runnable;
        ThreadUtils.runOnUiThread(new Runnable() { // from class: com.google.vr.cardboard.UiLayer.6
            @Override // java.lang.Runnable
            public void run() {
                UiLayer.this.backButton.setVisibility(UiLayer.computeVisibility(runnable != null));
                if (UiLayer.this.transitionView == null) {
                    return;
                }
                UiLayer.this.transitionView.setBackButtonListener(runnable);
            }
        });
    }

    public void setCustomTransitionLayout(final int i, final int i2) {
        ThreadUtils.runOnUiThread(new Runnable() { // from class: com.google.vr.cardboard.UiLayer.12
            @Override // java.lang.Runnable
            public void run() {
                UiLayer.this.getTransitionView().setCustomTransitionLayout(i, i2);
            }
        });
    }

    public void setEnabled(final boolean z) {
        ThreadUtils.runOnUiThread(new Runnable() { // from class: com.google.vr.cardboard.UiLayer.4
            @Override // java.lang.Runnable
            public void run() {
                UiLayer.this.rootLayout.setVisibility(UiLayer.computeVisibility(z));
            }
        });
    }

    public void setPortraitSupportEnabled(boolean z) {
        initializeViewsWithLayoutId(!z ? R.layout.ui_layer : R.layout.ui_layer_with_portrait_support);
    }

    public void setSettingsButtonEnabled(final boolean z) {
        this.isSettingsButtonEnabled = z;
        ThreadUtils.runOnUiThread(new Runnable() { // from class: com.google.vr.cardboard.UiLayer.5
            @Override // java.lang.Runnable
            public void run() {
                UiLayer.this.settingsButton.setVisibility(UiLayer.computeVisibility(z));
            }
        });
    }

    public void setSettingsButtonRunnable(Runnable runnable) {
        this.settingsButtonRunnable = runnable;
    }

    public void setTransitionViewEnabled(final boolean z) {
        this.transitionViewEnabled = z;
        ThreadUtils.runOnUiThread(new Runnable() { // from class: com.google.vr.cardboard.UiLayer.9
            @Override // java.lang.Runnable
            public void run() {
                if (!z && UiLayer.this.transitionView == null) {
                    return;
                }
                UiLayer.this.getTransitionView().setVisibility(UiLayer.computeVisibility(z));
            }
        });
    }

    public void setTransitionViewListener(final TransitionView.TransitionListener transitionListener) {
        ThreadUtils.runOnUiThread(new Runnable() { // from class: com.google.vr.cardboard.UiLayer.10
            @Override // java.lang.Runnable
            public void run() {
                if (transitionListener == null && UiLayer.this.transitionView == null) {
                    return;
                }
                UiLayer.this.getTransitionView().setTransitionListener(transitionListener);
            }
        });
    }

    public void setViewerName(final String str) {
        this.viewerName = str;
        ThreadUtils.runOnUiThread(new Runnable() { // from class: com.google.vr.cardboard.UiLayer.11
            @Override // java.lang.Runnable
            public void run() {
                if (UiLayer.this.transitionView == null) {
                    return;
                }
                UiLayer.this.transitionView.setViewerName(str);
            }
        });
    }
}
