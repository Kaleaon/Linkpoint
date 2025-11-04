// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.render;

import android.annotation.SuppressLint;
import android.content.SharedPreferences$Editor;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.content.FileProvider;
import java.io.OutputStream;
import android.graphics.Bitmap$CompressFormat;
import java.io.FileOutputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.io.File;
import android.graphics.Bitmap;
import android.content.SharedPreferences;
import com.lumiyaviewer.lumiya.react.Subscribable;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import com.lumiyaviewer.lumiya.ui.objects.ObjectPayDialog;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo;
import android.view.Menu;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.LumiyaApp;
import android.content.res.Configuration;
import butterknife.OnClick;
import com.lumiyaviewer.lumiya.ui.outfits.OutfitsFragment;
import com.lumiyaviewer.lumiya.ui.chat.ContactsFragment;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.slproto.events.SLBakingProgressEvent;
import android.view.KeyEvent;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.ui.objects.TouchableObjectsFragment;
import android.widget.Toast;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.content.Intent;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.objects.ObjectDetailsFragment;
import java.util.Iterator;
import android.content.DialogInterface$OnClickListener;
import android.widget.ListAdapter;
import android.support.v7.app.AlertDialog;
import java.util.List;
import android.widget.ArrayAdapter;
import java.util.NoSuchElementException;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.google.common.base.Strings;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import butterknife.ButterKnife;
import android.support.v4.app.ActivityCompat;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import android.view.ScaleGestureDetector$SimpleOnScaleGestureListener;
import android.view.ViewGroup$LayoutParams;
import android.widget.AbsoluteLayout$LayoutParams;
import android.view.GestureDetector$SimpleOnGestureListener;
import android.view.MotionEvent;
import android.os.Build$VERSION;
import com.lumiyaviewer.lumiya.Debug;
import com.google.common.eventbus.Subscribe;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import android.os.SystemClock;
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import android.view.ScaleGestureDetector$OnScaleGestureListener;
import android.view.ScaleGestureDetector;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState;
import android.os.Handler;
import android.widget.FrameLayout;
import android.view.GestureDetector$OnGestureListener;
import android.support.v4.view.GestureDetectorCompat;
import com.lumiyaviewer.lumiya.slproto.modules.SLDrawDistance;
import android.view.ViewGroup;
import android.view.View;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import android.widget.LinearLayout;
import android.animation.ValueAnimator;
import android.widget.Button;
import android.widget.ImageButton;
import butterknife.BindView;
import android.widget.ImageView;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationManager;
import com.lumiyaviewer.lumiya.ui.common.ScriptDialogHandler;
import com.lumiyaviewer.lumiya.ui.ThemeMapper;
import android.view.View$OnTouchListener;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;

public class WorldViewActivity extends DetailsActivity implements View$OnTouchListener, ThemeMapper, ScriptDialogHandler, NotifyCapture
{
    private static final long BUTTONS_FADE_TIMEOUT_MILLIS = 7500L;
    private static final String FROM_NOTIFICATION_TAG = "fromNotification";
    private static final long OBJECT_DESELECT_TIMEOUT_MILLIS = 6000L;
    private static final int PERMISSION_AUDIO_REQUEST_CODE = 100;
    private static final float TURNING_SPEED = 50.0f;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit;
    private boolean arrowsToTurn;
    @Nullable
    private SLAvatarControl avatarControl;
    @BindView(2131755576)
    ImageView avatarIconView;
    @BindView(2131755762)
    ImageButton buttonCamOff;
    @BindView(2131755761)
    ImageButton buttonCamOn;
    @BindView(2131755757)
    ImageButton buttonFlyDownward;
    @BindView(2131755756)
    ImageButton buttonFlyUpward;
    @BindView(2131755763)
    Button buttonHUD;
    @BindView(2131755262)
    ImageButton buttonMoveBackward;
    @BindView(2131755260)
    ImageButton buttonMoveForward;
    @BindView(2131755264)
    ImageButton buttonStandUp;
    @BindView(2131755758)
    ImageButton buttonStopFlying;
    @BindView(2131755261)
    ImageButton buttonTurnLeft;
    @BindView(2131755263)
    ImageButton buttonTurnRight;
    private ValueAnimator buttonsFadeAnimator;
    private final Runnable buttonsFadeTask;
    private boolean buttonsFadeTimerStarted;
    private final Runnable buttonsRestoreTask;
    private boolean camButtonEnabled;
    private final Object chatEventHandler;
    private boolean chatOver3D;
    @BindView(2131755759)
    LinearLayout chatsOverlayLayout;
    private final SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo> currentLocationInfo;
    @BindView(2131755284)
    View detailsContainer;
    private int displayedHUDid;
    @BindView(2131755748)
    View dragPointer;
    @BindView(2131755747)
    ViewGroup dragPointerLayout;
    @Nullable
    private SLDrawDistance drawDistance;
    private FadingTextViewLog fadingTextViewLog;
    @BindView(2131755755)
    LinearLayout flyButtonsLayout;
    private GestureDetectorCompat gestureDetector;
    private final GestureDetector$OnGestureListener gestureListener;
    private float hudOffsetX;
    private float hudOffsetY;
    private float hudScaleFactor;
    @BindView(2131755744)
    FrameLayout insetsBackground;
    private boolean isDragging;
    private boolean isInScaling;
    private boolean isInteracting;
    private boolean isSplitScreen;
    private long lastActivityTime;
    private long lastObjectActivityTime;
    private UUID lastTouchUUID;
    private boolean localDrawingEnabled;
    private WorldSurfaceView mGLView;
    private Handler mHandler;
    private boolean manualCamMode;
    @BindView(2131755259)
    View moveButtonsLayout;
    private final SubscriptionData<SubscriptionSingleKey, MyAvatarState> myAvatarState;
    @BindView(2131755270)
    ImageButton objectChatButton;
    @BindView(2131755753)
    View objectControlsPanel;
    private boolean objectDeselectTimerStarted;
    private final Runnable objectDeselectTimerTask;
    @BindView(2131755754)
    ImageButton objectMoreButton;
    @BindView(2131755577)
    TextView objectNameTextView;
    @BindView(2131755554)
    ImageButton objectPayButton;
    @BindView(2131755764)
    View objectPopupLeftSpacer;
    @BindView(2131755269)
    ImageButton objectSitButton;
    @BindView(2131755538)
    ImageButton objectStandButton;
    @BindView(2131755268)
    ImageButton objectTouchButton;
    private float oldScaleFocusX;
    private float oldScaleFocusY;
    private ChatterNameRetriever pickedAvatarNameRetriever;
    private ObjectIntersectInfo pickedIntersectInfo;
    private SLObjectInfo pickedObject;
    private int prefDrawDistance;
    private int prevDisplayedHUDid;
    private ScaleGestureDetector scaleGestureDetector;
    private final ScaleGestureDetector$OnScaleGestureListener scaleGestureListener;
    private final SubscriptionData<Integer, SLObjectProfileData> selectedObjectProfile;
    private UserManager userManager;
    private final SubscriptionData<SubscriptionSingleKey, ChatterID> voiceActiveChatter;
    private final SubscriptionData<ChatterID, VoiceChatInfo> voiceChatInfo;
    @BindView(2131755752)
    VoiceStatusView voiceStatusView;
    private boolean wasInScaling;
    @BindView(2131755750)
    ViewGroup worldOverlaysContainer;
    @BindView(2131755743)
    FrameLayout worldViewHolder;
    private final View$OnTouchListener worldViewTouchListener;
    @BindView(2131755746)
    View worldViewTouchReceiver;
    
    public WorldViewActivity() {
        this.pickedObject = null;
        this.pickedIntersectInfo = null;
        this.pickedAvatarNameRetriever = null;
        this.mHandler = new Handler();
        this.prefDrawDistance = 20;
        this.chatOver3D = false;
        this.lastTouchUUID = null;
        this.displayedHUDid = 0;
        this.prevDisplayedHUDid = 0;
        this.hudScaleFactor = 1.0f;
        this.hudOffsetX = 0.0f;
        this.hudOffsetY = 0.0f;
        this.arrowsToTurn = false;
        this.camButtonEnabled = false;
        this.manualCamMode = false;
        this.localDrawingEnabled = false;
        this.lastActivityTime = SystemClock.uptimeMillis();
        this.buttonsFadeTimerStarted = false;
        this.buttonsFadeAnimator = null;
        this.lastObjectActivityTime = SystemClock.uptimeMillis();
        this.objectDeselectTimerStarted = false;
        this.isInScaling = false;
        this.oldScaleFocusX = Float.NaN;
        this.oldScaleFocusY = Float.NaN;
        this.wasInScaling = false;
        this.isInteracting = false;
        this.isDragging = false;
        this.agentCircuit = new SubscriptionData<UUID, SLAgentCircuit>(UIThreadExecutor.getInstance(), new _$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8$3(this));
        this.myAvatarState = new SubscriptionData<SubscriptionSingleKey, MyAvatarState>(UIThreadExecutor.getInstance(), new _$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8$4(this));
        this.selectedObjectProfile = new SubscriptionData<Integer, SLObjectProfileData>(UIThreadExecutor.getInstance(), new _$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8$5(this));
        this.currentLocationInfo = new SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo>(UIThreadExecutor.getInstance(), new _$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8$6(this));
        this.voiceActiveChatter = new SubscriptionData<SubscriptionSingleKey, ChatterID>(UIThreadExecutor.getInstance(), new _$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8$7(this));
        this.voiceChatInfo = new SubscriptionData<ChatterID, VoiceChatInfo>(UIThreadExecutor.getInstance(), new _$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8$8(this));
        this.chatEventHandler = new Object() {
            @Subscribe
            public void onChatMessage(final ActiveChattersManager.ChatMessageEvent chatMessageEvent) {
                WorldViewActivity.this.mHandler.post((Runnable)new _$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8$11(this, chatMessageEvent));
            }
        };
        this.objectDeselectTimerTask = new Runnable() {
            @Override
            public void run() {
                WorldViewActivity.this.objectDeselectTimerStarted = false;
                if (!WorldViewActivity.this.detailsVisible() && (WorldViewActivity.this.isDragging ^ true)) {
                    final long l = WorldViewActivity.this.lastObjectActivityTime + 6000L - SystemClock.uptimeMillis();
                    Debug.Printf("ObjectDeselect: remaining %d", l);
                    if (l <= 0L) {
                        WorldViewActivity.this.handlePickedObject(null);
                    }
                    else {
                        WorldViewActivity.this.objectDeselectTimerStarted = true;
                        WorldViewActivity.this.mHandler.postDelayed(WorldViewActivity.this.objectDeselectTimerTask, l);
                    }
                }
            }
        };
        this.buttonsFadeTask = new Runnable() {
            @Override
            public void run() {
                WorldViewActivity.this.buttonsFadeTimerStarted = false;
                if (!WorldViewActivity.this.detailsVisible() && (WorldViewActivity.this.isDragging ^ true) && WorldViewActivity.this.agentCircuit.hasData()) {
                    final VoiceChatInfo voiceChatInfo = WorldViewActivity.this.voiceChatInfo.getData();
                    if (voiceChatInfo == null || voiceChatInfo.state != VoiceChatInfo.VoiceChatState.Active || !voiceChatInfo.localMicActive) {
                        final long l = WorldViewActivity.this.lastActivityTime + 7500L - SystemClock.uptimeMillis();
                        Debug.Printf("ButtonsFade: remaining %d", l);
                        if (l <= 0L) {
                            WorldViewActivity.this.startFadingButtons();
                        }
                        else {
                            WorldViewActivity.this.buttonsFadeTimerStarted = true;
                            WorldViewActivity.this.mHandler.postDelayed(WorldViewActivity.this.buttonsFadeTask, l);
                        }
                    }
                }
            }
        };
        this.buttonsRestoreTask = new Runnable() {
            @Override
            public void run() {
                if (Build$VERSION.SDK_INT >= 11) {
                    if (WorldViewActivity.this.buttonsFadeAnimator != null) {
                        WorldViewActivity.this.buttonsFadeAnimator.cancel();
                    }
                    WorldViewActivity.this.insetsBackground.setAlpha(1.0f);
                }
            }
        };
        this.worldViewTouchListener = (View$OnTouchListener)new View$OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                final boolean -get11 = WorldViewActivity.this.isInteracting;
                boolean b = false;
                switch (motionEvent.getActionMasked()) {
                    default: {
                        b = false;
                        break;
                    }
                    case 0: {
                        WorldViewActivity.this.isInteracting = true;
                        b = true;
                        break;
                    }
                    case 1: {
                        WorldViewActivity.this.isInteracting = false;
                        b = true;
                        break;
                    }
                }
                if (WorldViewActivity.this.isInteracting && (-get11 ^ true)) {
                    WorldViewActivity.this.mGLView.setIsInteracting(true);
                }
                WorldViewActivity.this.wasInScaling = WorldViewActivity.this.isInScaling;
                final boolean onTouchEvent = WorldViewActivity.this.scaleGestureDetector.onTouchEvent(motionEvent);
                final boolean onTouchEvent2 = WorldViewActivity.this.gestureDetector.onTouchEvent(motionEvent);
                if (-get11 && (WorldViewActivity.this.isInteracting ^ true)) {
                    WorldViewActivity.this.mGLView.setIsInteracting(false);
                }
                return b | onTouchEvent | onTouchEvent2;
            }
        };
        this.gestureListener = (GestureDetector$OnGestureListener)new GestureDetector$SimpleOnGestureListener() {
            public boolean onFling(final MotionEvent motionEvent, final MotionEvent motionEvent2, float n, float n2) {
                if (!WorldViewActivity.this.isInScaling && (WorldViewActivity.this.wasInScaling ^ true) && (WorldViewActivity.this.isDragging ^ true)) {
                    n = n * 60.0f / WorldViewActivity.this.worldViewHolder.getHeight();
                    n2 = -n2 * 60.0f / WorldViewActivity.this.worldViewHolder.getHeight();
                    if (WorldViewActivity.this.avatarControl != null) {
                        WorldViewActivity.this.avatarControl.processCameraFling(n / 1.5f, n2 / 2.5f);
                    }
                    return true;
                }
                return false;
            }
            
            public void onLongPress(final MotionEvent motionEvent) {
                final float rawX = motionEvent.getRawX();
                final float rawY = motionEvent.getRawY();
                if (WorldViewActivity.this.isDragging) {
                    WorldViewActivity.this.dragSelectorSetRawPosition((int)rawX, (int)rawY);
                }
                else if (!WorldViewActivity.this.isInScaling && (WorldViewActivity.this.wasInScaling ^ true)) {
                    final int[] array = new int[2];
                    WorldViewActivity.this.worldViewHolder.getLocationOnScreen(array);
                    WorldViewActivity.this.mGLView.pickObjectHover(rawX - array[0], rawY - array[1]);
                }
            }
            
            public boolean onScroll(final MotionEvent motionEvent, final MotionEvent motionEvent2, float n, float n2) {
                if (WorldViewActivity.this.isDragging) {
                    final AbsoluteLayout$LayoutParams layoutParams = (AbsoluteLayout$LayoutParams)WorldViewActivity.this.dragPointer.getLayoutParams();
                    if (layoutParams != null) {
                        layoutParams.x = Math.max(Math.min((int)(layoutParams.x - n), WorldViewActivity.this.dragPointerLayout.getWidth() - WorldViewActivity.this.dragPointer.getWidth()), 0);
                        layoutParams.y = Math.max(Math.min((int)(layoutParams.y - n2), WorldViewActivity.this.dragPointerLayout.getHeight() - WorldViewActivity.this.dragPointer.getHeight()), 0);
                        WorldViewActivity.this.dragPointer.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
                        WorldViewActivity.this.selectByDragPointer(layoutParams.x, layoutParams.y);
                    }
                    return true;
                }
                if (!WorldViewActivity.this.isInScaling && (WorldViewActivity.this.wasInScaling ^ true)) {
                    if (WorldViewActivity.this.displayedHUDid != 0) {
                        final WorldViewActivity this$0 = WorldViewActivity.this;
                        this$0.hudOffsetX += n / WorldViewActivity.this.worldViewHolder.getHeight() / 2.0f;
                        final WorldViewActivity this$2 = WorldViewActivity.this;
                        this$2.hudOffsetY += n2 / WorldViewActivity.this.worldViewHolder.getHeight() / 2.0f;
                        WorldViewActivity.this.mGLView.setHUDOffset(WorldViewActivity.this.hudOffsetX, WorldViewActivity.this.hudOffsetY);
                    }
                    else {
                        n = -n * 60.0f / WorldViewActivity.this.worldViewHolder.getHeight();
                        n2 = n2 * 60.0f / WorldViewActivity.this.worldViewHolder.getHeight();
                        if (WorldViewActivity.this.avatarControl != null) {
                            WorldViewActivity.this.avatarControl.processCameraRotate(n, n2);
                        }
                    }
                    return true;
                }
                return false;
            }
            
            public boolean onSingleTapUp(final MotionEvent motionEvent) {
                if (WorldViewActivity.this.isDragging) {
                    WorldViewActivity.this.dragSelectorSetRawPosition((int)motionEvent.getRawX(), (int)motionEvent.getRawY());
                }
                else if (WorldViewActivity.this.displayedHUDid != 0) {
                    final int[] array = new int[2];
                    WorldViewActivity.this.worldViewHolder.getLocationOnScreen(array);
                    WorldViewActivity.this.mGLView.touchHUD(motionEvent.getRawX() - array[0], motionEvent.getRawY() - array[1]);
                }
                else {
                    WorldViewActivity.this.handlePickedObject(null);
                }
                return true;
            }
        };
        this.scaleGestureListener = (ScaleGestureDetector$OnScaleGestureListener)new ScaleGestureDetector$SimpleOnScaleGestureListener() {
            public boolean onScale(final ScaleGestureDetector scaleGestureDetector) {
                Debug.Printf("Gesture: scale factor: %f", scaleGestureDetector.getScaleFactor());
                if (WorldViewActivity.this.displayedHUDid != 0) {
                    WorldViewActivity.this.hudScaleFactor = Math.max(0.1f, Math.min(WorldViewActivity.this.hudScaleFactor * scaleGestureDetector.getScaleFactor(), 10.0f));
                    WorldViewActivity.this.mGLView.setHUDScaleFactor(WorldViewActivity.this.hudScaleFactor);
                }
                else {
                    final float n = (float)WorldViewActivity.this.worldViewTouchReceiver.getWidth();
                    final float n2 = (float)WorldViewActivity.this.worldViewTouchReceiver.getHeight();
                    final float focusX = scaleGestureDetector.getFocusX();
                    final float focusY = scaleGestureDetector.getFocusY();
                    final float n3 = focusX / n;
                    final float n4 = n2 / n;
                    final float n5 = focusY / n2;
                    final float n6 = (focusX - WorldViewActivity.this.oldScaleFocusX) / n2;
                    final float n7 = (focusY - WorldViewActivity.this.oldScaleFocusY) / n2;
                    WorldViewActivity.this.oldScaleFocusX = focusX;
                    WorldViewActivity.this.oldScaleFocusY = focusY;
                    if (WorldViewActivity.this.avatarControl != null) {
                        WorldViewActivity.this.avatarControl.processCameraZoom(scaleGestureDetector.getScaleFactor(), -((n3 - 0.5f) * n4) * 2.0f, -(n5 - 0.5f) * 2.0f, n6, n7);
                    }
                }
                return true;
            }
            
            public boolean onScaleBegin(final ScaleGestureDetector scaleGestureDetector) {
                WorldViewActivity.this.isInScaling = true;
                WorldViewActivity.this.oldScaleFocusX = scaleGestureDetector.getFocusX();
                WorldViewActivity.this.oldScaleFocusY = scaleGestureDetector.getFocusY();
                return true;
            }
            
            public void onScaleEnd(final ScaleGestureDetector scaleGestureDetector) {
                WorldViewActivity.this.isInScaling = false;
            }
        };
    }
    
    private void beginCountingButtonsFade() {
        this.lastActivityTime = SystemClock.uptimeMillis();
        this.lastObjectActivityTime = this.lastActivityTime;
        this.startFadingButtonsTimer();
    }
    
    private void beginCountingObjectDeselect() {
        if (this.pickedObject != null) {
            this.lastObjectActivityTime = SystemClock.uptimeMillis();
            if (!this.objectDeselectTimerStarted) {
                this.objectDeselectTimerStarted = true;
                this.mHandler.postDelayed(this.objectDeselectTimerTask, 6000L);
            }
        }
    }
    
    private void beginDragSelection() {
        this.isDragging = true;
        this.removeAllDetails();
        final AbsoluteLayout$LayoutParams layoutParams = (AbsoluteLayout$LayoutParams)this.dragPointer.getLayoutParams();
        layoutParams.x = (this.dragPointerLayout.getWidth() - this.dragPointer.getWidth()) / 2;
        layoutParams.y = (this.dragPointerLayout.getHeight() - this.dragPointer.getHeight()) / 2;
        this.dragPointer.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        this.selectByDragPointer(layoutParams.x, layoutParams.y);
        this.mGLView.setOwnAvatarHidden(true);
        this.updateObjectPanel();
    }
    
    private void chatWithObject(final SLObjectInfo slObjectInfo) {
        if (slObjectInfo instanceof SLObjectAvatarInfo && !((SLObjectAvatarInfo)slObjectInfo).isMyAvatar() && slObjectInfo.getId() != null) {
            DetailsActivity.showEmbeddedDetails(this, ChatFragment.class, ChatFragment.makeSelection(ChatterID.getUserChatterID(this.userManager.getUserID(), slObjectInfo.getId())));
        }
    }
    
    private boolean detailsVisible() {
        final FragmentManager supportFragmentManager = this.getSupportFragmentManager();
        if (supportFragmentManager != null) {
            final Fragment fragmentById = supportFragmentManager.findFragmentById(2131755284);
            if (fragmentById != null && fragmentById.isVisible()) {
                return true;
            }
        }
        return false;
    }
    
    private void displayHUD(final int displayedHUDid) {
        Debug.Printf("Displaying HUD with ID %d", displayedHUDid);
        this.displayedHUDid = displayedHUDid;
        this.mGLView.setDisplayedHUDid(displayedHUDid);
        if (this.displayedHUDid != this.prevDisplayedHUDid) {
            this.hudScaleFactor = 1.0f;
            this.hudOffsetX = 0.0f;
            this.hudOffsetY = 0.0f;
            this.prevDisplayedHUDid = this.displayedHUDid;
        }
        this.mGLView.setHUDScaleFactor(this.hudScaleFactor);
        this.mGLView.setHUDOffset(this.hudOffsetX, this.hudOffsetY);
        if (this.displayedHUDid != 0) {
            this.handlePickedObject(null);
        }
        this.updateObjectPanel();
    }
    
    private void dragSelectorSetRawPosition(final int n, final int n2) {
        final int[] array = new int[2];
        this.dragPointerLayout.getLocationOnScreen(array);
        final int n3 = this.dragPointer.getWidth() / 2;
        final int n4 = this.dragPointer.getHeight() / 2;
        final AbsoluteLayout$LayoutParams layoutParams = (AbsoluteLayout$LayoutParams)this.dragPointer.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.x = Math.max(Math.min(n - n3 - array[0], this.dragPointerLayout.getWidth() - this.dragPointer.getWidth()), 0);
            layoutParams.y = Math.max(Math.min(n2 - n4 - array[1], this.dragPointerLayout.getHeight() - this.dragPointer.getHeight()), 0);
            this.dragPointer.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
            this.selectByDragPointer(layoutParams.x, layoutParams.y);
        }
    }
    
    private void endDragSelection() {
        this.isDragging = false;
        this.mGLView.setOwnAvatarHidden(false);
        this.updateObjectPanel();
        this.beginCountingButtonsFade();
        this.beginCountingObjectDeselect();
    }
    
    private void enterCardboardView() {
        if (ContextCompat.checkSelfPermission((Context)this, "android.permission.RECORD_AUDIO") != 0) {
            Debug.Printf("Cardboard: audio permission not yet granted", new Object[0]);
            ActivityCompat.requestPermissions(this, new String[] { "android.permission.RECORD_AUDIO" }, 100);
        }
        else {
            Debug.Printf("Cardboard: audio permission already granted", new Object[0]);
            this.startCardboardActivity();
        }
    }
    
    private void initContentView() {
        this.setContentView(2130968765);
        ButterKnife.bind(this);
        this.setSupportActionBar(this.findViewById(2131755745));
        this.mGLView = new WorldSurfaceView(this, this.userManager);
        this.worldViewHolder.addView((View)this.mGLView);
        this.buttonMoveForward.setOnTouchListener((View$OnTouchListener)this);
        this.buttonMoveBackward.setOnTouchListener((View$OnTouchListener)this);
        this.buttonTurnLeft.setOnTouchListener((View$OnTouchListener)this);
        this.buttonTurnRight.setOnTouchListener((View$OnTouchListener)this);
        this.buttonMoveForward.setFocusable(false);
        this.buttonMoveBackward.setFocusable(false);
        this.buttonTurnLeft.setFocusable(false);
        this.buttonTurnRight.setFocusable(false);
        this.buttonFlyUpward.setOnTouchListener((View$OnTouchListener)this);
        this.buttonFlyDownward.setOnTouchListener((View$OnTouchListener)this);
        this.buttonFlyUpward.setFocusable(false);
        this.buttonFlyDownward.setFocusable(false);
        this.voiceStatusView.setShowActiveChatterName(true);
        this.worldViewTouchReceiver.setOnTouchListener(this.worldViewTouchListener);
        this.objectControlsPanel.setVisibility(8);
        final View viewById = this.findViewById(2131755586);
        if (viewById != null) {
            viewById.setBackgroundColor(Color.argb(128, 0, 0, 0));
            final int n = (int)TypedValue.applyDimension(1, 10.0f, this.getResources().getDisplayMetrics());
            viewById.setPadding(n, n, n, n);
        }
    }
    
    private void onAgentCircuit(final SLAgentCircuit slAgentCircuit) {
        if (slAgentCircuit != null) {
            this.avatarControl = slAgentCircuit.getModules().avatarControl;
            this.drawDistance = slAgentCircuit.getModules().drawDistance;
            if (this.localDrawingEnabled) {
                this.drawDistance.Enable3DView(this.prefDrawDistance);
            }
            if (this.camButtonEnabled) {
                this.manualCamMode = this.avatarControl.getIsManualCamming();
            }
        }
        else {
            this.handlePickedObject(null);
            this.avatarControl = null;
            this.drawDistance = null;
        }
        this.mHandler.post(this.buttonsRestoreTask);
        this.beginCountingButtonsFade();
        this.beginCountingObjectDeselect();
        this.updateObjectPanel();
    }
    
    private void onCurrentLocation(final CurrentLocationInfo currentLocationInfo) {
        ParcelData parcelData;
        if (currentLocationInfo != null) {
            parcelData = currentLocationInfo.parcelData();
        }
        else {
            parcelData = null;
        }
        String s;
        if (parcelData != null) {
            s = parcelData.getName();
        }
        else {
            s = null;
        }
        if (s == null) {
            s = this.getString(2131296712);
        }
        this.setDefaultTitle(s, null);
    }
    
    private void onMyAvatarState(final MyAvatarState myAvatarState) {
        this.updateObjectPanel();
    }
    
    private void onPickedAvatarNameUpdated(final ChatterNameRetriever chatterNameRetriever) {
        if (chatterNameRetriever == this.pickedAvatarNameRetriever) {
            this.updateObjectPanel();
        }
    }
    
    private void onSelectedObjectProfile(final SLObjectProfileData slObjectProfileData) {
        Debug.Printf("got selected object profile: %s", slObjectProfileData);
        this.updateObjectPanel();
        if (slObjectProfileData != null) {
            final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
            if (slObjectProfileData.isPayable() && slObjectProfileData.payInfo() == null && slAgentCircuit != null) {
                slAgentCircuit.DoRequestPayPrice(slObjectProfileData.objectUUID());
            }
        }
    }
    
    private void onVoiceActiveChatter(final ChatterID chatterID) {
        if (this.voiceStatusView != null) {
            this.voiceStatusView.setChatterID(chatterID);
        }
        if (chatterID != null && this.userManager != null) {
            this.voiceChatInfo.subscribe(this.userManager.getVoiceChatInfo(), chatterID);
        }
        else {
            this.voiceChatInfo.unsubscribe();
        }
    }
    
    private void onVoiceChatInfo(final VoiceChatInfo voiceChatInfo) {
    }
    
    private void selectByDragPointer(final int n, final int n2) {
        final int[] array = new int[2];
        this.dragPointerLayout.getLocationOnScreen(array);
        final int n3 = array[0];
        final int n4 = this.dragPointer.getWidth() / 2;
        final int n5 = array[1];
        final int n6 = this.dragPointer.getHeight() / 2;
        final int[] array2 = new int[2];
        this.worldViewHolder.getLocationOnScreen(array2);
        this.mGLView.pickObjectHover((float)(n3 + n4 + n - array2[0]), (float)(n5 + n6 + n2 - array2[1]));
    }
    
    private void selectHUDtoDisplay() {
        final ArrayList list = new ArrayList();
        final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
        if (slAgentCircuit != null) {
            final SLObjectAvatarInfo agentAvatar = slAgentCircuit.getGridConnection().parcelInfo.getAgentAvatar();
            if (agentAvatar != null) {
                try {
                    for (final SLObjectInfo slObjectInfo : agentAvatar.treeNode) {
                        if (!Strings.nullToEmpty(slObjectInfo.getName()).startsWith("#")) {
                            final int attachmentID = slObjectInfo.attachmentID;
                            if (attachmentID < 0 || attachmentID >= 56) {
                                continue;
                            }
                            final SLAttachmentPoint slAttachmentPoint = SLAttachmentPoint.attachmentPoints[attachmentID];
                            if (slAttachmentPoint == null || !slAttachmentPoint.isHUD) {
                                continue;
                            }
                            list.add(new SelectableAttachment(slObjectInfo.localID, slObjectInfo.name));
                        }
                    }
                }
                catch (final NoSuchElementException ex) {
                    Debug.Warning(ex);
                }
            }
            if (!list.isEmpty()) {
                if (list.size() == 1) {
                    this.displayHUD(((SelectableAttachment)list.get(0)).getLocalID());
                }
                else {
                    final ArrayAdapter arrayAdapter = new ArrayAdapter((Context)this, 17367043, (List)list);
                    final AlertDialog.Builder builder = new AlertDialog.Builder((Context)this);
                    builder.setTitle(2131297011);
                    builder.setAdapter((ListAdapter)arrayAdapter, (DialogInterface$OnClickListener)new _$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8$10(this, list));
                    builder.create().show();
                }
            }
        }
    }
    
    private void showObjectInfo(final SLObjectInfo slObjectInfo) {
        if (slObjectInfo.getId() != null && this.userManager != null) {
            if (!slObjectInfo.isAvatar()) {
                DetailsActivity.showEmbeddedDetails(this, ObjectDetailsFragment.class, ObjectDetailsFragment.makeSelection(this.userManager.getUserID(), slObjectInfo.localID));
            }
            else if (slObjectInfo instanceof SLObjectAvatarInfo) {
                DetailsActivity.showEmbeddedDetails(this, UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(this.userManager.getUserID(), slObjectInfo.getId())));
            }
        }
    }
    
    private void startCardboardActivity() {
        if (this.userManager != null) {
            final Intent intent = new Intent((Context)this, (Class)CardboardTransitionActivity.class);
            ActivityUtils.setActiveAgentID(intent, this.userManager.getUserID());
            intent.addFlags(16777216);
            this.startActivity(intent);
        }
        this.finish();
    }
    
    private void startFadingButtons() {
        if (Build$VERSION.SDK_INT >= 11 && this.buttonsFadeAnimator != null) {
            this.buttonsFadeAnimator.start();
        }
    }
    
    private void startFadingButtonsTimer() {
        if (!this.buttonsFadeTimerStarted) {
            Debug.Printf("ButtonsFade: starting timer", new Object[0]);
            this.buttonsFadeTimerStarted = true;
            this.mHandler.postDelayed(this.buttonsFadeTask, 7500L);
        }
    }
    
    private void stopAvatarAnimations() {
        if (this.avatarControl != null) {
            this.avatarControl.StopAvatarAnimations();
        }
    }
    
    private void takeScreenshot() {
        Toast.makeText((Context)this, 2131297080, 0).show();
        this.mGLView.takeScreenshot();
    }
    
    private void touchObject(final SLObjectInfo slObjectInfo, final ObjectIntersectInfo objectIntersectInfo) {
        final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
        if (slAgentCircuit != null && slObjectInfo != null) {
            if (!slObjectInfo.isAvatar()) {
                this.lastTouchUUID = slObjectInfo.getId();
                boolean faceKnown = false;
                if (objectIntersectInfo != null) {
                    faceKnown = objectIntersectInfo.intersectInfo.faceKnown;
                }
                if (faceKnown) {
                    final LLVector3 absolutePosition = slObjectInfo.getAbsolutePosition();
                    slAgentCircuit.TouchObjectFace(slObjectInfo, objectIntersectInfo.intersectInfo.faceID, absolutePosition.x, absolutePosition.y, absolutePosition.z, objectIntersectInfo.intersectInfo.u, objectIntersectInfo.intersectInfo.v, objectIntersectInfo.intersectInfo.s, objectIntersectInfo.intersectInfo.t);
                }
                else {
                    slAgentCircuit.TouchObject(slObjectInfo.localID);
                }
            }
            else if (slObjectInfo.hasTouchableChildren()) {
                DetailsActivity.showEmbeddedDetails(this, TouchableObjectsFragment.class, TouchableObjectsFragment.makeSelection(this.userManager.getUserID(), slObjectInfo.getId()));
            }
        }
    }
    
    private void updateObjectPanel() {
        final MyAvatarState myAvatarState = this.myAvatarState.getData();
        final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
        boolean b;
        if (slAgentCircuit != null) {
            b = true;
        }
        else {
            b = false;
        }
        final boolean b2 = myAvatarState != null && myAvatarState.isSitting();
        final boolean b3 = myAvatarState != null && myAvatarState.hasHUDs();
        final boolean b4 = myAvatarState != null && myAvatarState.isFlying();
        final boolean b5 = slAgentCircuit != null && slAgentCircuit.getModules().rlvController.canStandUp();
        final boolean b6 = slAgentCircuit != null && slAgentCircuit.getModules().rlvController.canSit();
        boolean b7;
        if (this.pickedObject != null) {
            b7 = true;
        }
        else {
            b7 = false;
        }
        Debug.Printf("isSitting %b, isFlying %b, hasHUDs %b, isDragging %b", b2, b4, b3, this.isDragging);
        final ViewGroup dragPointerLayout = this.dragPointerLayout;
        int visibility;
        if (this.isDragging) {
            visibility = 0;
        }
        else {
            visibility = 4;
        }
        dragPointerLayout.setVisibility(visibility);
        final View dragPointer = this.dragPointer;
        int visibility2;
        if (this.isDragging) {
            visibility2 = 0;
        }
        else {
            visibility2 = 4;
        }
        dragPointer.setVisibility(visibility2);
        final LinearLayout flyButtonsLayout = this.flyButtonsLayout;
        int visibility3;
        if (((b2 || (b ^ true)) && ((this.camButtonEnabled && this.manualCamMode) ^ true)) || this.isDragging || b7) {
            visibility3 = 8;
        }
        else {
            visibility3 = 0;
        }
        flyButtonsLayout.setVisibility(visibility3);
        final View moveButtonsLayout = this.moveButtonsLayout;
        int visibility4;
        if (((b2 || (b ^ true)) && ((this.camButtonEnabled && this.manualCamMode) ^ true)) || this.isDragging || b7) {
            visibility4 = 4;
        }
        else {
            visibility4 = 0;
        }
        moveButtonsLayout.setVisibility(visibility4);
        final ImageButton buttonStandUp = this.buttonStandUp;
        int visibility5;
        if (b5 && b2 && (this.isDragging ^ true)) {
            visibility5 = 0;
        }
        else {
            visibility5 = 8;
        }
        buttonStandUp.setVisibility(visibility5);
        final Button buttonHUD = this.buttonHUD;
        int visibility6;
        if (b3 && (this.isDragging ^ true) && b) {
            visibility6 = 0;
        }
        else {
            visibility6 = 8;
        }
        buttonHUD.setVisibility(visibility6);
        final ImageButton buttonFlyDownward = this.buttonFlyDownward;
        int visibility7;
        if ((b4 && b) || (this.camButtonEnabled && this.manualCamMode)) {
            visibility7 = 0;
        }
        else {
            visibility7 = 8;
        }
        buttonFlyDownward.setVisibility(visibility7);
        final ImageButton buttonStopFlying = this.buttonStopFlying;
        int visibility8;
        if (b4 && b && ((this.camButtonEnabled && this.manualCamMode) ^ true)) {
            visibility8 = 0;
        }
        else {
            visibility8 = 8;
        }
        buttonStopFlying.setVisibility(visibility8);
        final ImageButton buttonCamOn = this.buttonCamOn;
        int visibility9;
        if (this.camButtonEnabled && (this.manualCamMode ^ true) && (this.isDragging ^ true) && (b7 ^ true)) {
            visibility9 = 0;
        }
        else {
            visibility9 = 8;
        }
        buttonCamOn.setVisibility(visibility9);
        final ImageButton buttonCamOff = this.buttonCamOff;
        int visibility10;
        if (this.camButtonEnabled && this.manualCamMode && (this.isDragging ^ true) && (b7 ^ true)) {
            visibility10 = 0;
        }
        else {
            visibility10 = 8;
        }
        buttonCamOff.setVisibility(visibility10);
        if (this.pickedObject == null || (b ^ true)) {
            this.objectControlsPanel.setVisibility(8);
        }
        else {
            this.objectControlsPanel.setVisibility(0);
            boolean touchable;
            final boolean b8 = touchable = this.pickedObject.isTouchable();
            if (this.pickedObject.isAvatar()) {
                touchable = (b8 | this.pickedObject.hasTouchableChildren());
            }
            final ImageButton objectTouchButton = this.objectTouchButton;
            int visibility11;
            if (touchable) {
                visibility11 = 0;
            }
            else {
                visibility11 = 8;
            }
            objectTouchButton.setVisibility(visibility11);
            final boolean avatar = this.pickedObject.isAvatar();
            int n;
            if (b2 && this.pickedObject.localID == myAvatarState.sittingOn()) {
                n = 1;
            }
            else {
                n = 0;
            }
            int n2;
            if (!avatar) {
                n2 = (n ^ 0x1);
            }
            else {
                n2 = 0;
            }
            if (avatar) {
                n = 0;
            }
            final ImageButton objectSitButton = this.objectSitButton;
            int visibility12;
            if (n2 != 0 && b6) {
                visibility12 = 0;
            }
            else {
                visibility12 = 8;
            }
            objectSitButton.setVisibility(visibility12);
            final ImageButton objectStandButton = this.objectStandButton;
            int visibility13;
            if (n != 0 && b5) {
                visibility13 = 0;
            }
            else {
                visibility13 = 8;
            }
            objectStandButton.setVisibility(visibility13);
            final ImageButton objectChatButton = this.objectChatButton;
            int visibility14;
            if (avatar) {
                visibility14 = 0;
            }
            else {
                visibility14 = 8;
            }
            objectChatButton.setVisibility(visibility14);
            final ImageView avatarIconView = this.avatarIconView;
            int visibility15;
            if (avatar) {
                visibility15 = 0;
            }
            else {
                visibility15 = 8;
            }
            avatarIconView.setVisibility(visibility15);
            final ImageButton objectPayButton = this.objectPayButton;
            int visibility16;
            if (this.pickedObject.isPayable() || this.pickedObject.saleType != 0) {
                visibility16 = 0;
            }
            else {
                visibility16 = 8;
            }
            objectPayButton.setVisibility(visibility16);
            String s;
            if (this.pickedObject.isAvatar()) {
                if (this.pickedAvatarNameRetriever != null) {
                    s = this.pickedAvatarNameRetriever.getResolvedName();
                }
                else {
                    s = null;
                }
            }
            else {
                final SLObjectProfileData slObjectProfileData = this.selectedObjectProfile.getData();
                String s2;
                if (slObjectProfileData != null && Objects.equal(slObjectProfileData.objectUUID(), this.pickedObject.getId())) {
                    s2 = slObjectProfileData.name().orNull();
                }
                else {
                    s2 = null;
                }
                s = s2;
                if (s2 == null) {
                    s = this.pickedObject.name;
                }
            }
            String string;
            if ((string = s) == null) {
                string = this.getString(2131296825);
            }
            this.objectNameTextView.setText((CharSequence)string);
        }
    }
    
    private void updateSimTimeOverride() {
        if (this.mGLView != null) {
            final GlobalOptions instance = GlobalOptions.getInstance();
            this.mGLView.setForcedTime(instance.getForceDaylightTime(), instance.getForceDaylightHour());
        }
    }
    
    private void updateSplitScreenLayout() {
        final int n = 0;
        final Fragment fragmentById = this.getSupportFragmentManager().findFragmentById(2131755284);
        Debug.Printf("updateSplitScreenLayout: isSplitScreen now %b details has %b detached %b", this.isSplitScreen, fragmentById != null, fragmentById != null && fragmentById.isDetached());
        if (fragmentById != null && (fragmentById.isDetached() ^ true)) {
            this.detailsContainer.setVisibility(0);
            final ViewGroup worldOverlaysContainer = this.worldOverlaysContainer;
            int visibility;
            if (this.isSplitScreen) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            worldOverlaysContainer.setVisibility(visibility);
        }
        else {
            this.worldOverlaysContainer.setVisibility(0);
        }
        final View objectPopupLeftSpacer = this.objectPopupLeftSpacer;
        int visibility2;
        if (this.isSplitScreen) {
            visibility2 = n;
        }
        else {
            visibility2 = 8;
        }
        objectPopupLeftSpacer.setVisibility(visibility2);
    }
    
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        if (this.detailsContainer.getVisibility() == 0) {
            return super.dispatchKeyEvent(keyEvent);
        }
        switch (keyEvent.getKeyCode()) {
            default: {
                return super.dispatchKeyEvent(keyEvent);
            }
            case 21: {
                if (this.avatarControl != null) {
                    if (keyEvent.getAction() == 0) {
                        this.avatarControl.startTurning(50.0f);
                    }
                    else if (keyEvent.getAction() == 1) {
                        this.avatarControl.stopTurning();
                    }
                }
                return true;
            }
            case 22: {
                if (this.avatarControl != null) {
                    if (keyEvent.getAction() == 0) {
                        this.avatarControl.startTurning(-50.0f);
                    }
                    else if (keyEvent.getAction() == 1) {
                        this.avatarControl.stopTurning();
                    }
                }
                return true;
            }
            case 19: {
                if (this.avatarControl != null) {
                    if (keyEvent.getAction() == 0) {
                        this.avatarControl.stopCamming();
                        this.avatarControl.StartAgentMotion(2);
                    }
                    else if (keyEvent.getAction() == 1) {
                        this.avatarControl.StopAgentMotion();
                    }
                }
                return true;
            }
            case 20: {
                if (this.avatarControl != null) {
                    if (keyEvent.getAction() == 0) {
                        this.avatarControl.stopCamming();
                        this.avatarControl.StartAgentMotion(4);
                    }
                    else if (keyEvent.getAction() == 1) {
                        this.avatarControl.StopAgentMotion();
                    }
                }
                return true;
            }
            case 92: {
                if (this.avatarControl != null) {
                    if (keyEvent.getAction() == 0) {
                        this.avatarControl.stopCamming();
                        this.avatarControl.StartAgentMotion(8);
                    }
                    else if (keyEvent.getAction() == 1) {
                        this.avatarControl.StopAgentMotion();
                    }
                }
                return true;
            }
            case 93: {
                if (this.avatarControl != null) {
                    if (keyEvent.getAction() == 0) {
                        this.avatarControl.stopCamming();
                        this.avatarControl.StartAgentMotion(16);
                    }
                    else if (keyEvent.getAction() == 1) {
                        this.avatarControl.StopAgentMotion();
                    }
                }
                return true;
            }
        }
    }
    
    @EventHandler
    public void handleBakingProgressEvent(final SLBakingProgressEvent slBakingProgressEvent) {
        if (slBakingProgressEvent.first) {
            Toast.makeText((Context)this, (CharSequence)"Updating avatar appearance...", 0).show();
        }
    }
    
    public void handleChatEvent(final ActiveChattersManager.ChatMessageEvent chatMessageEvent) {
        if (this.chatOver3D && !this.detailsVisible() && this.userManager != null && this.chatsOverlayLayout != null && this.fadingTextViewLog != null) {
            this.fadingTextViewLog.handleChatEvent(chatMessageEvent);
        }
    }
    
    public void handlePickedObject(final ObjectIntersectInfo pickedIntersectInfo) {
        this.pickedIntersectInfo = pickedIntersectInfo;
        SLObjectInfo objInfo;
        if (pickedIntersectInfo != null) {
            objInfo = pickedIntersectInfo.objInfo;
        }
        else {
            objInfo = null;
        }
        this.pickedObject = objInfo;
        if (this.pickedObject != null) {
            if (this.pickedObject.isAvatar()) {
                final ChatterID.ChatterIDUser userChatterID = ChatterID.getUserChatterID(this.userManager.getUserID(), this.pickedObject.getId());
                ChatterID chatterID;
                if (this.pickedAvatarNameRetriever != null) {
                    chatterID = this.pickedAvatarNameRetriever.chatterID;
                }
                else {
                    chatterID = null;
                }
                if (!Objects.equal(chatterID, userChatterID)) {
                    if (this.pickedAvatarNameRetriever != null) {
                        this.pickedAvatarNameRetriever.dispose();
                        this.pickedAvatarNameRetriever = null;
                    }
                    this.pickedAvatarNameRetriever = new ChatterNameRetriever(userChatterID, (ChatterNameRetriever.OnChatterNameUpdated)new _$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8$9(this), UIThreadExecutor.getInstance());
                }
            }
            else {
                Debug.Printf("ObjectPick: picked object %d", this.pickedObject.localID);
                this.selectedObjectProfile.subscribe(this.userManager.getObjectsManager().getObjectProfile(), this.pickedObject.localID);
            }
            this.updateObjectPanel();
            this.beginCountingObjectDeselect();
        }
        else {
            this.selectedObjectProfile.unsubscribe();
            if (this.pickedAvatarNameRetriever != null) {
                this.pickedAvatarNameRetriever.dispose();
                this.pickedAvatarNameRetriever = null;
            }
        }
        this.mGLView.setDrawPickedObject(this.pickedObject);
        this.updateObjectPanel();
    }
    
    @Override
    protected boolean isRootDetailsFragment(final Class<? extends Fragment> clazz) {
        boolean b2;
        final boolean b = b2 = true;
        if (clazz != ContactsFragment.class) {
            if (clazz == ChatFragment.class) {
                b2 = b;
            }
            else {
                b2 = b;
                if (clazz != ObjectDetailsFragment.class) {
                    b2 = b;
                    if (clazz != OutfitsFragment.class) {
                        b2 = b;
                        if (clazz != UserProfileFragment.class) {
                            b2 = false;
                        }
                    }
                }
            }
        }
        return b2;
    }
    
    public int mapThemeResourceId(final int n) {
        if (n == 2131427372) {
            return 2131427373;
        }
        if (n == 2131427375) {
            return 2131427376;
        }
        return 2131427374;
    }
    
    public void moveTouchEvent(final int n, final MotionEvent motionEvent) {
        final float n2 = 1.0f;
        final float n3 = -1.0f;
        if (this.avatarControl != null) {
            if (motionEvent.getAction() == 0) {
                if (this.manualCamMode && this.camButtonEnabled) {
                    float n4;
                    if ((n & 0x8) != 0x0) {
                        n4 = 1.0f;
                    }
                    else {
                        n4 = 0.0f;
                    }
                    float n5;
                    if ((n & 0x10) != 0x0) {
                        n5 = -1.0f;
                    }
                    else {
                        n5 = n4;
                    }
                    float n6;
                    if ((n & 0x2) != 0x0) {
                        n6 = 1.0f;
                    }
                    else {
                        n6 = 0.0f;
                    }
                    float n7 = n6;
                    if ((n & 0x4) != 0x0) {
                        n7 = -1.0f;
                    }
                    float n8;
                    if ((n & 0x20) != 0x0) {
                        n8 = n2;
                    }
                    else {
                        n8 = 0.0f;
                    }
                    if ((n & 0x40) != 0x0) {
                        n8 = n3;
                    }
                    this.avatarControl.startCameraManualControl(0.0f, n7, n5, n8);
                }
                else {
                    this.avatarControl.stopCamming();
                    this.avatarControl.StartAgentMotion(n);
                    if ((n & 0x8) != 0x0 || (n & 0x10) != 0x0) {
                        this.updateObjectPanel();
                    }
                }
            }
            else if (motionEvent.getAction() == 1) {
                if (this.manualCamMode && this.camButtonEnabled) {
                    this.avatarControl.stopCameraManualControl();
                }
                else {
                    this.avatarControl.StopAgentMotion();
                }
            }
        }
    }
    
    @OnClick({ 2131755762 })
    public void onCamOffButton() {
        if (this.camButtonEnabled) {
            this.manualCamMode = false;
            if (this.avatarControl != null) {
                this.avatarControl.setCameraManualControl(false);
            }
            this.updateObjectPanel();
        }
    }
    
    @OnClick({ 2131755761 })
    public void onCamOnButton() {
        if (this.camButtonEnabled) {
            this.manualCamMode = true;
            if (this.avatarControl != null) {
                this.avatarControl.setCameraManualControl(true);
            }
            this.updateObjectPanel();
        }
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.isSplitScreen = LumiyaApp.isSplitScreenNeeded((Context)this);
        this.updateSplitScreenLayout();
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.userManager = ActivityUtils.getUserManager(this.getIntent());
        if (this.userManager == null) {
            this.finish();
            return;
        }
        this.isSplitScreen = LumiyaApp.isSplitScreenNeeded((Context)this);
        this.scaleGestureDetector = new ScaleGestureDetector((Context)this, this.scaleGestureListener);
        if (Build$VERSION.SDK_INT >= 19) {
            this.scaleGestureDetector.setQuickScaleEnabled(true);
        }
        this.gestureDetector = new GestureDetectorCompat((Context)this, this.gestureListener);
        this.initContentView();
        this.fadingTextViewLog = new FadingTextViewLog(this.userManager, (Context)this, this.chatsOverlayLayout, Color.rgb(192, 192, 192), Color.argb(160, 0, 0, 0));
        if (Build$VERSION.SDK_INT >= 12) {
            (this.buttonsFadeAnimator = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f })).setDuration(1000L);
            this.buttonsFadeAnimator.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8$1(this));
        }
        this.updateSplitScreenLayout();
        this.startFadingButtonsTimer();
    }
    
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.getMenuInflater().inflate(2131886119, menu);
        return true;
    }
    
    @Override
    protected boolean onDetailsStackEmpty() {
        if (!super.onDetailsStackEmpty()) {
            this.detailsContainer.setVisibility(8);
            if (!this.isSplitScreen) {
                this.worldOverlaysContainer.setVisibility(0);
            }
            this.beginCountingButtonsFade();
            this.beginCountingObjectDeselect();
            return false;
        }
        return true;
    }
    
    @Nullable
    public Intent onGetNotifyCaptureIntent(@Nonnull final UnreadNotificationInfo unreadNotificationInfo, final Intent intent) {
        Debug.Printf("NotifyCapture: returning new intent", new Object[0]);
        final Intent intent2 = new Intent((Context)this, (Class)WorldViewActivity.class);
        intent2.putExtras(intent);
        intent2.putExtra("fromNotification", true);
        intent2.addFlags(536870912);
        return intent2;
    }
    
    @EventHandler
    public void onGlobalOptionsChanged(final GlobalOptions.GlobalOptionsChangedEvent globalOptionsChangedEvent) {
        this.updateSimTimeOverride();
    }
    
    @OnClick({ 2131755763 })
    public void onHUDButton() {
        if (this.displayedHUDid != 0) {
            this.displayedHUDid = 0;
            this.mGLView.setDisplayedHUDid(this.displayedHUDid);
            this.updateObjectPanel();
        }
        else {
            this.selectHUDtoDisplay();
        }
    }
    
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        Debug.Printf("NotifyCapture: got newIntent: %s", intent);
        this.mHandler.post(this.buttonsRestoreTask);
        this.beginCountingButtonsFade();
        this.beginCountingObjectDeselect();
        if (intent.hasExtra("fromNotification")) {
            Bundle bundleExtra;
            if (intent.hasExtra("selection")) {
                bundleExtra = intent.getBundleExtra("selection");
            }
            else {
                bundleExtra = null;
            }
            if (bundleExtra != null) {
                DetailsActivity.showEmbeddedDetails(this, ChatFragment.class, bundleExtra);
            }
            else if (this.userManager != null) {
                DetailsActivity.showEmbeddedDetails(this, ContactsFragment.class, ActivityUtils.makeFragmentArguments(this.userManager.getUserID(), null));
            }
        }
    }
    
    @OnClick({ 2131755270 })
    public void onObjectChatButton() {
        if (this.pickedObject != null) {
            this.chatWithObject(this.pickedObject);
        }
    }
    
    @OnClick({ 2131755754 })
    public void onObjectMoreButton() {
        if (this.pickedObject != null) {
            this.showObjectInfo(this.pickedObject);
        }
    }
    
    @OnClick({ 2131755554 })
    public void onObjectPayButton() {
        if (this.pickedObject != null) {
            String s;
            if (Strings.isNullOrEmpty(s = this.pickedObject.getName())) {
                s = this.getString(2131296825);
            }
            if (this.pickedObject.saleType != 0) {
                final AlertDialog.Builder builder = new AlertDialog.Builder((Context)this);
                builder.setMessage(String.format(this.getString(2131296787), s, this.pickedObject.salePrice)).setCancelable(false).setPositiveButton("Yes", (DialogInterface$OnClickListener)new _$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8$2(this)).setNegativeButton("No", (DialogInterface$OnClickListener)new _$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8());
                builder.show();
            }
            else if (this.pickedObject.isPayable()) {
                final SLObjectProfileData slObjectProfileData = this.selectedObjectProfile.getData();
                if (slObjectProfileData != null && this.userManager != null) {
                    ObjectPayDialog.show((Context)this, this.userManager, slObjectProfileData);
                }
            }
        }
    }
    
    @OnClick({ 2131755269 })
    public void onObjectSitButton() {
        if (this.pickedObject != null && this.avatarControl != null) {
            this.avatarControl.SitOnObject(this.pickedObject.getId());
        }
    }
    
    @OnClick({ 2131755264, 2131755538 })
    public void onObjectStandButton() {
        if (this.avatarControl != null) {
            this.avatarControl.Stand();
        }
    }
    
    @OnClick({ 2131755268 })
    public void onObjectTouchButton() {
        if (this.pickedObject != null) {
            this.touchObject(this.pickedObject, this.pickedIntersectInfo);
        }
    }
    
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            default: {
                return super.onOptionsItemSelected(menuItem);
            }
            case 2131755881: {
                DetailsActivity.showEmbeddedDetails(this, ContactsFragment.class, ActivityUtils.makeFragmentArguments(this.userManager.getUserID(), null));
                return true;
            }
            case 2131755882: {
                DetailsActivity.showEmbeddedDetails(this, OutfitsFragment.class, OutfitsFragment.makeSelection(this.userManager.getUserID(), null));
                return true;
            }
            case 2131755886: {
                this.stopAvatarAnimations();
                return true;
            }
            case 2131755883: {
                if (!this.isDragging) {
                    this.beginDragSelection();
                }
                else {
                    this.endDragSelection();
                }
                return true;
            }
            case 2131755885: {
                this.takeScreenshot();
                return true;
            }
            case 2131755884: {
                this.enterCardboardView();
                return true;
            }
        }
    }
    
    public void onPause() {
        Debug.Printf("WorldViewActivity: onPause", new Object[0]);
        Label_0062: {
            if (this.userManager == null) {
                break Label_0062;
            }
            this.userManager.getUnreadNotificationManager().clearNotifyCapture((UnreadNotificationManager.NotifyCapture)this);
            while (true) {
                try {
                    this.userManager.getChatterList().getActiveChattersManager().getChatEventBus().unregister(this.chatEventHandler);
                    if (this.fadingTextViewLog != null) {
                        this.fadingTextViewLog.clearChatEvents();
                    }
                    this.localDrawingEnabled = false;
                    if (this.drawDistance != null) {
                        this.drawDistance.Disable3DView();
                    }
                    this.mGLView.onPause();
                    this.mHandler.removeCallbacks(this.objectDeselectTimerTask);
                    this.mHandler.removeCallbacks(this.buttonsFadeTask);
                    this.objectDeselectTimerStarted = false;
                    this.buttonsFadeTimerStarted = false;
                    this.myAvatarState.unsubscribe();
                    this.selectedObjectProfile.unsubscribe();
                    this.currentLocationInfo.unsubscribe();
                    System.gc();
                    super.onPause();
                }
                catch (final IllegalArgumentException ex) {
                    continue;
                }
                break;
            }
        }
    }
    
    public boolean onPrepareOptionsMenu(final Menu menu) {
        final MenuItem item = menu.findItem(2131755884);
        if (item != null) {
            item.setVisible(Build$VERSION.SDK_INT >= 23);
        }
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public void onRequestPermissionsResult(final int i, @Nonnull final String[] array, @Nonnull final int[] array2) {
        Debug.Printf("Cardboard: onRequestPermissionResult, code %d", i);
        if (i == 100) {
            this.startCardboardActivity();
        }
    }
    
    public void onResume() {
        super.onResume();
        Debug.Printf("WorldViewActivity: onResume", new Object[0]);
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        this.chatOver3D = defaultSharedPreferences.getBoolean("chat_over_3d", true);
        this.arrowsToTurn = defaultSharedPreferences.getBoolean("arrows_rotate_avatar", false);
        this.camButtonEnabled = defaultSharedPreferences.getBoolean("cam_button_enabled", false);
        if (this.userManager != null) {
            this.userManager.getUnreadNotificationManager().setNotifyCapture((UnreadNotificationManager.NotifyCapture)this);
            this.myAvatarState.subscribe(this.userManager.getObjectsManager().myAvatarState(), SubscriptionSingleKey.Value);
            this.currentLocationInfo.subscribe(this.userManager.getCurrentLocationInfo(), SubscriptionSingleKey.Value);
            this.userManager.getChatterList().getActiveChattersManager().getChatEventBus().register(this.chatEventHandler);
        }
        final RenderSettings renderSettings = new RenderSettings(defaultSharedPreferences);
        this.prefDrawDistance = renderSettings.drawDistance;
        this.localDrawingEnabled = true;
        if (this.drawDistance != null) {
            this.drawDistance.Enable3DView(this.prefDrawDistance);
        }
        if (this.camButtonEnabled && this.avatarControl != null) {
            this.manualCamMode = this.avatarControl.getIsManualCamming();
        }
        this.mGLView.setDrawDistance(this.prefDrawDistance);
        this.mGLView.setAvatarCountLimit(renderSettings.avatarCountLimit);
        this.mGLView.onResume();
        this.updateObjectPanel();
        if (this.fadingTextViewLog != null) {
            this.fadingTextViewLog.postRemovingStaleChats();
        }
    }
    
    protected void onStart() {
        super.onStart();
        if (this.userManager != null) {
            this.agentCircuit.subscribe(UserManager.agentCircuits(), this.userManager.getUserID());
            this.voiceActiveChatter.subscribe(this.userManager.getVoiceActiveChatter(), SubscriptionSingleKey.Value);
        }
        this.updateSimTimeOverride();
    }
    
    protected void onStop() {
        this.agentCircuit.unsubscribe();
        this.voiceActiveChatter.unsubscribe();
        this.voiceChatInfo.unsubscribe();
        this.voiceStatusView.setChatterID(null);
        super.onStop();
    }
    
    @OnClick({ 2131755758 })
    public void onStopFlyingButton() {
        if (this.avatarControl != null) {
            this.avatarControl.stopFlying();
            this.updateObjectPanel();
        }
    }
    
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        switch (view.getId()) {
            case 2131755260: {
                this.moveTouchEvent(2, motionEvent);
                break;
            }
            case 2131755262: {
                this.moveTouchEvent(4, motionEvent);
                break;
            }
            case 2131755756: {
                this.moveTouchEvent(8, motionEvent);
                break;
            }
            case 2131755757: {
                this.moveTouchEvent(16, motionEvent);
                break;
            }
            case 2131755261: {
                if (!this.arrowsToTurn) {
                    this.moveTouchEvent(32, motionEvent);
                    break;
                }
                if (this.avatarControl == null) {
                    break;
                }
                if (this.manualCamMode && this.camButtonEnabled) {
                    if (motionEvent.getActionMasked() == 0) {
                        this.avatarControl.startCameraManualControl(50.0f, 0.0f, 0.0f, 0.0f);
                        break;
                    }
                    if (motionEvent.getActionMasked() == 1) {
                        this.avatarControl.stopCameraManualControl();
                        break;
                    }
                    break;
                }
                else {
                    if (motionEvent.getActionMasked() == 0) {
                        this.avatarControl.startTurning(50.0f);
                        break;
                    }
                    if (motionEvent.getActionMasked() == 1) {
                        this.avatarControl.stopTurning();
                        break;
                    }
                    break;
                }
                break;
            }
            case 2131755263: {
                if (!this.arrowsToTurn) {
                    this.moveTouchEvent(64, motionEvent);
                    break;
                }
                if (this.avatarControl == null) {
                    break;
                }
                if (this.manualCamMode && this.camButtonEnabled) {
                    if (motionEvent.getActionMasked() == 0) {
                        this.avatarControl.startCameraManualControl(-50.0f, 0.0f, 0.0f, 0.0f);
                        break;
                    }
                    if (motionEvent.getActionMasked() == 1) {
                        this.avatarControl.stopCameraManualControl();
                        break;
                    }
                    break;
                }
                else {
                    if (motionEvent.getActionMasked() == 0) {
                        this.avatarControl.startTurning(-50.0f);
                        break;
                    }
                    if (motionEvent.getActionMasked() == 1) {
                        this.avatarControl.stopTurning();
                        break;
                    }
                    break;
                }
                break;
            }
        }
        return false;
    }
    
    public void onUserInteraction() {
        super.onUserInteraction();
        Debug.Printf("ButtonsFade: some user interaction", new Object[0]);
        this.mHandler.post(this.buttonsRestoreTask);
        this.beginCountingButtonsFade();
        this.beginCountingObjectDeselect();
    }
    
    public void processScreenshot(final Bitmap bitmap) {
        try {
            final File parent = new File(this.getCacheDir(), "screenshots");
            parent.mkdirs();
            final File file = new File(parent, "Lumiya-" + new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.US).format(new Date()) + ".jpg");
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap$CompressFormat.JPEG, 80, (OutputStream)fileOutputStream);
            fileOutputStream.close();
            final Uri uriForFile = FileProvider.getUriForFile((Context)this, "com.lumiyaviewer.files", file);
            final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
            String agentSLURL;
            if (slAgentCircuit != null) {
                agentSLURL = slAgentCircuit.getAgentSLURL();
            }
            else {
                agentSLURL = null;
            }
            String s;
            if (agentSLURL != null) {
                s = this.getString(2131296996, new Object[] { agentSLURL });
            }
            else {
                s = this.getString(2131296995);
            }
            final Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.putExtra("android.intent.extra.TEXT", s);
            intent.putExtra("android.intent.extra.STREAM", (Parcelable)uriForFile);
            intent.putExtra("android.intent.extra.SUBJECT", "Screenshot from Lumiya");
            intent.setType("image/jpeg");
            intent.setFlags(1);
            this.startActivity(Intent.createChooser(intent, (CharSequence)this.getString(2131296536)));
        }
        catch (final Exception ex) {
            Toast.makeText((Context)this, 2131296542, 0).show();
        }
    }
    
    public void rendererAdvancedRenderingChanged() {
        final Intent intent = this.getIntent();
        this.finish();
        this.startActivity(intent);
    }
    
    @SuppressLint({ "CommitPrefEdits" })
    void rendererShaderCompileError() {
        Toast.makeText((Context)this, (CharSequence)"Advanced rendering is not available on your hardware. Falling back to basic rendering.", 1).show();
        final SharedPreferences$Editor edit = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext()).edit();
        edit.putBoolean("advanced_rendering", false);
        edit.commit();
        this.finish();
        this.startActivity(new Intent((Context)this, (Class)this.getClass()));
    }
    
    void rendererSurfaceCreated() {
        if (this.drawDistance != null) {
            this.drawDistance.Enable3DView(this.prefDrawDistance);
        }
    }
    
    public void setLastTouchUUID(final UUID lastTouchUUID) {
        this.lastTouchUUID = lastTouchUUID;
    }
    
    public void setTouchedObject(final SLObjectInfo slObjectInfo) {
        if (slObjectInfo != null) {
            this.lastTouchUUID = slObjectInfo.getId();
            if (this.lastTouchUUID != null) {
                Debug.Log("Touch: Last touched object set to " + this.lastTouchUUID);
            }
        }
    }
    
    @Nullable
    @Override
    public Fragment showDetailsFragment(final Class<? extends Fragment> clazz, final Intent intent, final Bundle bundle) {
        this.detailsContainer.setVisibility(0);
        if (!this.isSplitScreen) {
            this.worldOverlaysContainer.setVisibility(8);
            this.voiceStatusView.disableMic();
        }
        if (this.fadingTextViewLog != null) {
            this.fadingTextViewLog.clearChatEvents();
        }
        return super.showDetailsFragment(clazz, intent, bundle);
    }
    
    private static class SelectableAttachment
    {
        private String attachmentName;
        private int localID;
        
        public SelectableAttachment(final int localID, final String attachmentName) {
            this.localID = localID;
            this.attachmentName = attachmentName;
        }
        
        public int getLocalID() {
            return this.localID;
        }
        
        @Override
        public String toString() {
            return this.attachmentName;
        }
    }
}
