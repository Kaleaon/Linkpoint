// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.render;

import javax.microedition.khronos.egl.EGLConfig;
import com.lumiyaviewer.lumiya.slproto.types.CameraParams;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;
import javax.microedition.khronos.opengles.GL10;
import com.google.vr.sdk.base.Eye;
import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.render.HeadTransformCompat;
import android.content.Intent;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatPermissionRequestEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import android.view.ViewTreeObserver$OnGlobalLayoutListener;
import android.widget.Button;
import butterknife.ButterKnife;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import android.widget.FrameLayout;
import android.content.Context;
import android.util.TypedValue;
import com.lumiyaviewer.lumiya.GlobalOptions;
import android.preference.PreferenceManager;
import com.google.vr.sdk.base.AndroidCompat;
import butterknife.OnClick;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import butterknife.OnTouch;
import android.text.TextUtils$TruncateAt;
import android.view.KeyEvent;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.ContactsFragment;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;
import com.google.common.base.Objects;
import android.graphics.Rect;
import com.google.common.base.Predicate;
import android.graphics.PorterDuff$Mode;
import android.graphics.Canvas;
import javax.annotation.Nullable;
import com.google.common.eventbus.Subscribe;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import android.os.Message;
import java.util.ArrayList;
import com.google.common.base.Strings;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.Debug;
import java.util.HashSet;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.Set;
import android.speech.SpeechRecognizer;
import android.widget.ProgressBar;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import android.graphics.Point;
import com.google.vrtoolkit.cardboard.ScreenOnFlagHelper;
import com.lumiyaviewer.lumiya.render.WorldViewRenderer;
import android.speech.RecognitionListener;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import android.view.View$OnTouchListener;
import com.google.common.collect.ImmutableMap;
import android.view.View$OnHoverListener;
import android.view.View$OnClickListener;
import com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState;
import com.google.common.util.concurrent.AtomicDouble;
import android.view.View;
import java.util.concurrent.atomic.AtomicBoolean;
import android.annotation.SuppressLint;
import android.os.Handler;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.cardboard.FullscreenMode;
import com.lumiyaviewer.lumiya.render.glres.textures.GLExternalTexture;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import javax.annotation.Nonnull;
import com.google.vr.sdk.controller.ControllerManager;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.vr.sdk.controller.Controller;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import butterknife.BindView;
import android.widget.ImageButton;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import java.util.concurrent.atomic.AtomicReference;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatScriptDialog;
import android.annotation.TargetApi;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;

@TargetApi(16)
public class CardboardActivity extends DetailsActivity implements ObjectPopupListener
{
    private static final int DEFAULT_FONT_SIZE_SP = 16;
    private static final int LISTVIEW_SCROLL_DURATION = 500;
    private static final int LISTVIEW_SCROLL_OFFSET = 100;
    private static final int RECYCLERVIEW_SCROLL_OFFSET = 100;
    private static final float VOICE_VIEW_HEIGHT_ALLOWANCE_DP = 60.0f;
    public static final String VR_MODE_TAG = "vrMode";
    private static final float controlDrawSizeFactor = 1.5f;
    private static final float controlSizeFactorX = 1.0f;
    private static final float controlSizeFactorY = 0.75f;
    private static final float crosshairSize = 0.1f;
    private static final int[] dialogButtonIds;
    private SLChatScriptDialog activeScriptDialog;
    private SLChatYesNoEvent activeYesNoEvent;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit;
    private final AtomicReference<SLAvatarControl> avatarControl;
    @BindView(2131755257)
    ImageButton buttonChat;
    @BindView(2131755262)
    ImageButton buttonMoveBackward;
    @BindView(2131755260)
    ImageButton buttonMoveForward;
    @BindView(2131755270)
    ImageButton buttonObjectChat;
    @BindView(2131755269)
    ImageButton buttonSit;
    @BindView(2131755256)
    ImageButton buttonSpeak;
    @BindView(2131755281)
    ImageButton buttonSpeechSend;
    @BindView(2131755264)
    ImageButton buttonStandUp;
    @BindView(2131755255)
    ImageButton buttonTouch;
    @BindView(2131755268)
    ImageButton buttonTouchObject;
    @BindView(2131755261)
    ImageButton buttonTurnLeft;
    @BindView(2131755263)
    ImageButton buttonTurnRight;
    @BindView(2131755265)
    ViewGroup cardboardAimControls;
    @BindView(2131755283)
    ViewGroup cardboardDetailsPage;
    @BindView(2131755266)
    ViewGroup cardboardObjectControls;
    @BindView(2131755253)
    ViewGroup cardboardPrimaryControls;
    @BindView(2131755282)
    ViewGroup cardboardScriptDialog;
    @BindView(2131755277)
    ViewGroup cardboardSpeakControls;
    private final Object chatEventHandler;
    @BindView(2131755258)
    LinearLayout chatsOverlayLayout;
    private Controller controller;
    private final AtomicInteger controllerConnectionState;
    private final Controller.EventListener controllerEventListener;
    private ControllerManager controllerManager;
    private final ControllerManager.EventListener controllerManagerEventListener;
    @Nonnull
    private volatile ControlsPage currentControlsPage;
    private final SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo> currentLocationInfo;
    @BindView(2131755636)
    TextView dialogQuestionText;
    private ChatterID dictationChatterID;
    private final AtomicReference<GLExternalTexture> externalTextureRef;
    private FadingTextViewLog fadingTextViewLog;
    private FullscreenMode fullscreenMode;
    private GvrView gvrView;
    @SuppressLint({ "HandlerLeak" })
    private final Handler handler;
    private volatile float headAgentHeading;
    private final Object hitPointLock;
    private final AtomicBoolean hitPointUpdatePosted;
    private boolean hitPointValid;
    private int hitPointX;
    private int hitPointY;
    private final OnHoverListenerCompat hoverListener;
    private View hoveringOverButton;
    private View hoveringPressedButton;
    private volatile boolean insideControls;
    private long insideSince;
    private boolean isResumed;
    private boolean isSpeechFinished;
    private volatile boolean isWalking;
    private final AtomicBoolean keypadActive;
    private final AtomicDouble keypadTurning;
    private String lastSpeechRecognitionResults;
    private final int[] locationInWindow;
    @BindView(2131755259)
    ViewGroup moveButtonsLayout;
    private int moveButtonsTop;
    private final SubscriptionData<SubscriptionSingleKey, MyAvatarState> myAvatarState;
    private volatile float neutralAgentHeading;
    @BindView(2131755275)
    ImageButton noButton;
    @BindView(2131755271)
    TextView objectNameView;
    private final View$OnClickListener onDialogButtonClick;
    private final View$OnHoverListener onHoverListener;
    private ViewGroup onScreenControlsLayout;
    private final View$OnClickListener onVoiceCallButtonListener;
    private final ImmutableMap<ControlsPage, View$OnTouchListener> outsideTouchListeners;
    private volatile boolean ownAvatarVisible;
    private ChatterNameRetriever pickedAvatarNameRetriever;
    private final AtomicReference<ObjectIntersectInfo> pickedObject;
    private int postedHitPointX;
    private int postedHitPointY;
    private int primaryButtonsViewBottom;
    private final RecognitionListener recognitionListener;
    private RenderSettings renderSettings;
    private WorldViewRenderer renderer;
    private final ScreenOnFlagHelper screenOnFlagHelper;
    private Point scrollableViewPoint;
    private final SubscriptionData<Integer, SLObjectProfileData> selectedObjectProfile;
    @BindView(2131755279)
    ProgressBar speakLevelIndicator;
    @BindView(2131755278)
    TextView speakNowText;
    @BindView(2131755280)
    TextView speechRecognitionResults;
    private SpeechRecognizer speechRecognizer;
    private float speechRmsMax;
    private float speechRmsMin;
    private Handler stateHandler;
    private final GvrView.StereoRenderer stereoRenderer;
    private final Set<View> touchActivatedButtons;
    private final AtomicBoolean touchRequested;
    private UserManager userManager;
    private final AtomicBoolean viewDrawPosted;
    private final SubscriptionData<SubscriptionSingleKey, ChatterID> voiceActiveChatter;
    private final SubscriptionData<ChatterID, VoiceChatInfo> voiceChatInfo;
    private boolean voiceEnabled;
    private final SubscriptionData<SubscriptionSingleKey, Boolean> voiceLoggedIn;
    @BindView(2131755254)
    VoiceStatusView voiceStatusView;
    private int voiceViewHeightAllowance;
    @BindView(2131755274)
    ImageButton yesButton;
    @BindView(2131755276)
    TextView yesNoText;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-ui-render-MoveControlSwitchesValues() {
        if (CardboardActivity.-com-lumiyaviewer-lumiya-ui-render-MoveControlSwitchesValues != null) {
            return CardboardActivity.-com-lumiyaviewer-lumiya-ui-render-MoveControlSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-ui-render-MoveControlSwitchesValues = new int[MoveControl.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-ui-render-MoveControlSwitchesValues[MoveControl.Backward.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-ui-render-MoveControlSwitchesValues[MoveControl.Forward.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-ui-render-MoveControlSwitchesValues[MoveControl.Left.ordinal()] = 3;
                        try {
                            -com-lumiyaviewer-lumiya-ui-render-MoveControlSwitchesValues[MoveControl.Right.ordinal()] = 4;
                            return -com-lumiyaviewer-lumiya-ui-render-MoveControlSwitchesValues = -com-lumiyaviewer-lumiya-ui-render-MoveControlSwitchesValues;
                        }
                        catch (final NoSuchFieldError noSuchFieldError) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError2) {}
                }
                catch (final NoSuchFieldError noSuchFieldError3) {}
            }
            catch (final NoSuchFieldError noSuchFieldError4) {
                continue;
            }
            break;
        }
    }
    
    static {
        dialogButtonIds = new int[] { 2131755317, 2131755318, 2131755319, 2131755314, 2131755315, 2131755316, 2131755311, 2131755312, 2131755313, 2131755308, 2131755309, 2131755310 };
    }
    
    public CardboardActivity() {
        this.stereoRenderer = new WorldStereoRenderer();
        this.screenOnFlagHelper = new ScreenOnFlagHelper(this);
        this.isResumed = false;
        this.viewDrawPosted = new AtomicBoolean(false);
        this.voiceEnabled = false;
        this.agentCircuit = new SubscriptionData<UUID, SLAgentCircuit>(UIThreadExecutor.getInstance(), new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$11(this));
        this.myAvatarState = new SubscriptionData<SubscriptionSingleKey, MyAvatarState>(UIThreadExecutor.getInstance(), new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$12(this));
        this.voiceLoggedIn = new SubscriptionData<SubscriptionSingleKey, Boolean>(UIThreadExecutor.getInstance(), new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$14(this));
        this.currentLocationInfo = new SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo>(UIThreadExecutor.getInstance(), new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$15(this));
        this.voiceActiveChatter = new SubscriptionData<SubscriptionSingleKey, ChatterID>(UIThreadExecutor.getInstance(), new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$16(this));
        this.voiceChatInfo = new SubscriptionData<ChatterID, VoiceChatInfo>(UIThreadExecutor.getInstance(), new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$17(this));
        this.externalTextureRef = new AtomicReference<GLExternalTexture>(null);
        this.hitPointUpdatePosted = new AtomicBoolean(false);
        this.hitPointLock = new Object();
        this.postedHitPointX = 0;
        this.postedHitPointY = 0;
        this.keypadActive = new AtomicBoolean(false);
        this.keypadTurning = new AtomicDouble(0.0);
        this.controllerConnectionState = new AtomicInteger(0);
        this.outsideTouchListeners = ImmutableMap.of(ControlsPage.pageSpeech, (View$OnTouchListener)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$3(this), ControlsPage.pageObject, (View$OnTouchListener)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$4(this), ControlsPage.pageScriptDialog, (View$OnTouchListener)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$5(this), ControlsPage.pageYesNo, (View$OnTouchListener)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$6(this), ControlsPage.pageDetails, (View$OnTouchListener)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$2(this));
        this.currentControlsPage = ControlsPage.pageDefault;
        this.fadingTextViewLog = null;
        this.lastSpeechRecognitionResults = "";
        this.speechRmsMin = Float.NaN;
        this.speechRmsMax = Float.NaN;
        this.isSpeechFinished = false;
        this.dictationChatterID = null;
        this.headAgentHeading = 0.0f;
        this.neutralAgentHeading = Float.NaN;
        this.hoveringOverButton = null;
        this.hoveringPressedButton = null;
        this.activeScriptDialog = null;
        this.activeYesNoEvent = null;
        this.ownAvatarVisible = false;
        this.isWalking = false;
        this.primaryButtonsViewBottom = 0;
        this.moveButtonsTop = 0;
        this.touchActivatedButtons = new HashSet<View>();
        this.avatarControl = new AtomicReference<SLAvatarControl>();
        this.touchRequested = new AtomicBoolean(false);
        this.pickedObject = new AtomicReference<ObjectIntersectInfo>();
        this.selectedObjectProfile = new SubscriptionData<Integer, SLObjectProfileData>(UIThreadExecutor.getInstance(), new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$13(this));
        this.pickedAvatarNameRetriever = null;
        this.hoverListener = new OnHoverListenerCompat() {
            @Override
            public boolean onHoverEnter(final View view) {
                view.setAlpha(1.0f);
                Debug.Printf("Cardboard: hovering enter %d", view.getId());
                CardboardActivity.this.hoveringOverButton = view;
                CardboardActivity.this.onViewsInvalidated();
                return false;
            }
            
            @Override
            public boolean onHoverExit(final View view) {
                view.setAlpha(0.5f);
                Debug.Printf("Cardboard: hovering exit %d", view.getId());
                if (CardboardActivity.this.hoveringOverButton == view) {
                    CardboardActivity.this.hoveringOverButton = null;
                }
                CardboardActivity.this.onViewsInvalidated();
                return false;
            }
        };
        this.onHoverListener = (View$OnHoverListener)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$1(this);
        this.recognitionListener = (RecognitionListener)new RecognitionListener() {
            public void onBeginningOfSpeech() {
                Debug.Printf("Cardboard: beginning of speech", new Object[0]);
            }
            
            public void onBufferReceived(final byte[] array) {
            }
            
            public void onEndOfSpeech() {
                Debug.Printf("Cardboard: end of speech", new Object[0]);
                CardboardActivity.this.speakLevelIndicator.setVisibility(4);
                CardboardActivity.this.speakNowText.setVisibility(4);
            }
            
            public void onError(final int i) {
                Debug.Printf("Cardboard: speech error %d", i);
                String s = null;
                switch (i) {
                    default: {
                        s = CardboardActivity.this.getString(2131297045);
                        break;
                    }
                    case 3: {
                        s = CardboardActivity.this.getString(2131297044);
                        break;
                    }
                    case 9: {
                        s = CardboardActivity.this.getString(2131297047);
                        break;
                    }
                    case 1:
                    case 2: {
                        s = CardboardActivity.this.getString(2131297050);
                        break;
                    }
                    case 7: {
                        s = CardboardActivity.this.getString(2131297051);
                        break;
                    }
                    case 8: {
                        s = CardboardActivity.this.getString(2131297046);
                        break;
                    }
                    case 4: {
                        s = CardboardActivity.this.getString(2131297048);
                        break;
                    }
                    case 6: {
                        s = CardboardActivity.this.getString(2131297049);
                        break;
                    }
                }
                CardboardActivity.this.showSpeechRecognitionError(s);
            }
            
            public void onEvent(final int n, final Bundle bundle) {
            }
            
            public void onPartialResults(final Bundle bundle) {
                Debug.Printf("Cardboard: speech recognition: got partial results", new Object[0]);
                final ArrayList stringArrayList = bundle.getStringArrayList("results_recognition");
                if (stringArrayList != null && stringArrayList.size() > 0) {
                    final String text = stringArrayList.get(0);
                    CardboardActivity.this.lastSpeechRecognitionResults = text;
                    CardboardActivity.this.speechRecognitionResults.setText((CharSequence)text);
                    if (!Strings.isNullOrEmpty(text)) {
                        CardboardActivity.this.buttonSpeechSend.setVisibility(0);
                    }
                }
            }
            
            public void onReadyForSpeech(final Bundle bundle) {
                CardboardActivity.this.speakNowText.setVisibility(0);
            }
            
            public void onResults(final Bundle bundle) {
                Debug.Printf("Cardboard: speech recognition: got some results", new Object[0]);
                final ArrayList stringArrayList = bundle.getStringArrayList("results_recognition");
                if (stringArrayList != null && stringArrayList.size() > 0) {
                    final String text = stringArrayList.get(0);
                    CardboardActivity.this.speechRecognitionResults.setText((CharSequence)text);
                    CardboardActivity.this.lastSpeechRecognitionResults = text;
                    if (!Strings.isNullOrEmpty(text)) {
                        CardboardActivity.this.buttonSpeechSend.setVisibility(0);
                        CardboardActivity.this.speakLevelIndicator.setVisibility(4);
                        CardboardActivity.this.isSpeechFinished = true;
                    }
                }
            }
            
            public void onRmsChanged(final float f) {
                if (!CardboardActivity.this.isSpeechFinished) {
                    if (Float.isNaN(CardboardActivity.this.speechRmsMin) || f < CardboardActivity.this.speechRmsMin) {
                        CardboardActivity.this.speechRmsMin = f;
                    }
                    if (Float.isNaN(CardboardActivity.this.speechRmsMax) || f > CardboardActivity.this.speechRmsMax) {
                        CardboardActivity.this.speechRmsMax = f;
                    }
                    float -get26;
                    if ((-get26 = CardboardActivity.this.speechRmsMax) - CardboardActivity.this.speechRmsMin < 1.0f) {
                        -get26 = CardboardActivity.this.speechRmsMin + 1.0f;
                    }
                    int round;
                    if ((round = Math.round((f - CardboardActivity.this.speechRmsMin) * 100.0f / (-get26 - CardboardActivity.this.speechRmsMin))) < 0) {
                        round = 0;
                    }
                    int progress;
                    if ((progress = round) > 100) {
                        progress = 100;
                    }
                    Debug.Printf("Cardboard: speech recognition: RMS %f", f);
                    CardboardActivity.this.speakLevelIndicator.setVisibility(0);
                    CardboardActivity.this.speakLevelIndicator.setProgress(progress);
                }
            }
        };
        this.insideControls = false;
        this.hitPointValid = false;
        this.insideSince = 0L;
        this.hitPointX = 0;
        this.hitPointY = 0;
        this.locationInWindow = new int[2];
        this.scrollableViewPoint = new Point();
        this.handler = new Handler() {
            public void handleMessage(final Message message) {
                boolean myAvatar = false;
                switch (message.what) {
                    case 1: {
                        if (message.obj == null || !(message.obj instanceof ObjectIntersectInfo)) {
                            break;
                        }
                        final ObjectIntersectInfo objectIntersectInfo = (ObjectIntersectInfo)message.obj;
                        Debug.Printf("Cardboard: PICKED OBJECT isAvatar %b localID %d", objectIntersectInfo.objInfo.isAvatar(), objectIntersectInfo.objInfo.localID);
                        if (objectIntersectInfo.objInfo instanceof SLObjectAvatarInfo) {
                            myAvatar = ((SLObjectAvatarInfo)objectIntersectInfo.objInfo).isMyAvatar();
                        }
                        if (!myAvatar) {
                            CardboardActivity.this.handlePickedObject(objectIntersectInfo);
                            break;
                        }
                        break;
                    }
                    case 2: {
                        if (message.obj != null && message.obj instanceof SLObjectInfo) {
                            final SLObjectInfo slObjectInfo = (SLObjectInfo)message.obj;
                            Debug.Printf("Cardboard: touched object isAvatar %b localID %d", slObjectInfo.isAvatar(), slObjectInfo.localID);
                            break;
                        }
                        break;
                    }
                }
            }
        };
        this.onDialogButtonClick = (View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                Label_0059: {
                    if (CardboardActivity.this.activeScriptDialog != null) {
                        while (true) {
                            for (int i = 0; i < CardboardActivity.dialogButtonIds.length; ++i) {
                                if (view.getId() == CardboardActivity.dialogButtonIds[i]) {
                                    CardboardActivity.this.activeScriptDialog.onDialogButton(CardboardActivity.this.userManager, i);
                                    CardboardActivity.this.activeScriptDialog = null;
                                    break Label_0059;
                                }
                            }
                            int i = -1;
                            continue;
                        }
                    }
                }
                CardboardActivity.this.handlePickedObject(null);
                CardboardActivity.this.setControlsPage(ControlsPage.pageDefault);
            }
        };
        this.chatEventHandler = new Object() {
            @Subscribe
            public void onChatMessage(final ActiveChattersManager.ChatMessageEvent chatMessageEvent) {
                CardboardActivity.this.runOnUiThread((Runnable)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$26(this, chatMessageEvent));
            }
        };
        this.onVoiceCallButtonListener = (View$OnClickListener)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA(this);
        this.controllerManagerEventListener = new ControllerManager.EventListener() {
            @Override
            public void onApiStatusChanged(final int i) {
                Debug.Printf("Cardboard: controller API status: %d", i);
            }
            
            @Override
            public void onRecentered() {
                if (CardboardActivity.this.gvrView != null) {
                    CardboardActivity.this.gvrView.recenterHeadTracker();
                }
            }
        };
        this.controllerEventListener = new Controller.EventListener() {
            @Nullable
            private MoveControl activeMoveControl = null;
            private boolean appButtonPressed = false;
            
            @Override
            public void onConnectionStateChanged(final int newValue) {
                super.onConnectionStateChanged(newValue);
                String s;
                if (newValue == 3) {
                    s = "connected";
                }
                else {
                    s = "disconnected";
                }
                Debug.Printf("Cardboard: Daydream controller is now %s", s);
                CardboardActivity.this.controllerConnectionState.set(newValue);
            }
            
            @Override
            public void onUpdate() {
                final float n = 0.0f;
                super.onUpdate();
                CardboardActivity.this.controller.update();
                if (CardboardActivity.this.controller.appButtonState && (this.appButtonPressed ^ true)) {
                    CardboardActivity.this.runOnUiThread((Runnable)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$20(this));
                }
                else if (!CardboardActivity.this.controller.appButtonState && this.appButtonPressed) {
                    CardboardActivity.this.runOnUiThread((Runnable)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$21(this));
                }
                this.appButtonPressed = CardboardActivity.this.controller.appButtonState;
                if (CardboardActivity.this.controller.isTouching) {
                    final float a = CardboardActivity.this.controller.touch.x * 2.0f - 1.0f;
                    final float a2 = -(CardboardActivity.this.controller.touch.y * 2.0f - 1.0f);
                    float a3 = a;
                    if (Math.abs(a) < 0.5f) {
                        a3 = 0.0f;
                    }
                    float a4 = a2;
                    if (Math.abs(a2) < 0.5f) {
                        a4 = 0.0f;
                    }
                    MoveControl activeMoveControl;
                    float n2;
                    if (Math.abs(a3) >= Math.abs(a4)) {
                        if (a3 > 0.0f) {
                            activeMoveControl = MoveControl.Right;
                            n2 = a3 * 2.0f;
                        }
                        else if (a3 < 0.0f) {
                            activeMoveControl = MoveControl.Left;
                            n2 = -a3 * 2.0f;
                        }
                        else {
                            activeMoveControl = null;
                            n2 = n;
                        }
                    }
                    else if (a4 > 0.0f) {
                        activeMoveControl = MoveControl.Forward;
                        n2 = a4 * 2.0f;
                    }
                    else if (a4 < 0.0f) {
                        activeMoveControl = MoveControl.Backward;
                        n2 = -a4 * 2.0f;
                    }
                    else {
                        activeMoveControl = null;
                        n2 = n;
                    }
                    if (activeMoveControl != this.activeMoveControl) {
                        if (activeMoveControl != null) {
                            this.activeMoveControl = activeMoveControl;
                            CardboardActivity.this.runOnUiThread((Runnable)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$29(n2, this, activeMoveControl));
                        }
                        else {
                            CardboardActivity.this.runOnUiThread((Runnable)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$27(this, this.activeMoveControl));
                            this.activeMoveControl = null;
                        }
                    }
                    else if (activeMoveControl != null) {
                        CardboardActivity.this.runOnUiThread((Runnable)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$30(n2, this, activeMoveControl));
                    }
                }
                else if (this.activeMoveControl != null) {
                    CardboardActivity.this.runOnUiThread((Runnable)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$28(this, this.activeMoveControl));
                    this.activeMoveControl = null;
                }
            }
        };
    }
    
    private void closeSpeechControls() {
        if (this.speechRecognizer != null) {
            this.speechRecognizer.stopListening();
        }
        if (this.getCurrentDetailsFragment() != null) {
            this.setControlsPage(ControlsPage.pageDetails);
        }
        else {
            this.setControlsPage(ControlsPage.pageDefault);
        }
    }
    
    private void draw2DUI() {
        final GLExternalTexture glExternalTexture = this.externalTextureRef.get();
        if (glExternalTexture == null) {
            return;
        }
        try {
            final Canvas canvas = glExternalTexture.getCanvas();
            this.drawExternalViews(canvas);
            glExternalTexture.postCanvas(canvas);
        }
        catch (final IllegalStateException ex) {}
    }
    
    private void drawExternalViews(final Canvas canvas) {
        canvas.drawColor(0, PorterDuff$Mode.CLEAR);
        this.onScreenControlsLayout.draw(canvas);
    }
    
    private void drawViews() {
        this.viewDrawPosted.set(false);
        Debug.Printf("Cardboard: drawing 2D UI", new Object[0]);
        this.draw2DUI();
    }
    
    @TargetApi(19)
    private View findMatchingView(final ViewGroup viewGroup, final int n, final int n2, int i, int n3, final Predicate<View> predicate, final Point point) {
        View child;
        int n4;
        for (i = 0; i < viewGroup.getChildCount(); ++i) {
            child = viewGroup.getChildAt(i);
            if (child.getVisibility() == 0 && child.isAttachedToWindow() && predicate.apply(child)) {
                child.getLocationInWindow(this.locationInWindow);
                n4 = this.locationInWindow[0];
                n3 = this.locationInWindow[1];
                if (new Rect(n4, n3, child.getWidth() + n4, child.getHeight() + n3).contains(n, n2)) {
                    point.set(n - n4, n2 - n3);
                    return child;
                }
            }
        }
        View child2;
        View matchingView;
        for (i = 0; i < viewGroup.getChildCount(); ++i) {
            child2 = viewGroup.getChildAt(i);
            if (child2.getVisibility() == 0 && child2.isAttachedToWindow() && child2 instanceof ViewGroup) {
                matchingView = this.findMatchingView((ViewGroup)child2, n, n2, 0, 0, predicate, point);
                if (matchingView != null) {
                    return matchingView;
                }
            }
        }
        return null;
    }
    
    private void handleMoveControl(@Nonnull final MoveControl moveControl, final float n) {
        final SLAvatarControl slAvatarControl = this.avatarControl.get();
        if (slAvatarControl != null) {
            switch (-getcom-lumiyaviewer-lumiya-ui-render-MoveControlSwitchesValues()[moveControl.ordinal()]) {
                case 3: {
                    if (n != 0.0f) {
                        this.keypadActive.set(true);
                        this.keypadTurning.set(-n);
                        break;
                    }
                    this.keypadTurning.set(0.0);
                    break;
                }
                case 4: {
                    if (n != 0.0f) {
                        this.keypadActive.set(true);
                        this.keypadTurning.set(n);
                        break;
                    }
                    this.keypadTurning.set(0.0);
                    break;
                }
                case 2: {
                    if (n != 0.0f) {
                        this.keypadActive.set(true);
                        if (this.ownAvatarVisible) {
                            slAvatarControl.startCameraManualControl(0.0f, 1.0f * n, 0.0f, 0.0f);
                            break;
                        }
                        this.startWalking(true);
                        break;
                    }
                    else {
                        if (this.ownAvatarVisible) {
                            slAvatarControl.stopCameraManualControl();
                            break;
                        }
                        this.stopWalking();
                        break;
                    }
                    break;
                }
                case 1: {
                    if (n != 0.0f) {
                        this.keypadActive.set(true);
                        if (this.ownAvatarVisible) {
                            slAvatarControl.startCameraManualControl(0.0f, -1.0f * n, 0.0f, 0.0f);
                            break;
                        }
                        this.startWalking(false);
                        break;
                    }
                    else {
                        if (this.ownAvatarVisible) {
                            slAvatarControl.stopCameraManualControl();
                            break;
                        }
                        this.stopWalking();
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    private void handlePickedObject(final ObjectIntersectInfo newValue) {
        SLObjectInfo objInfo;
        if (newValue != null) {
            objInfo = newValue.objInfo;
        }
        else {
            objInfo = null;
        }
        if (objInfo != null) {
            this.pickedObject.set(newValue);
            if (objInfo.isAvatar()) {
                final ChatterID.ChatterIDUser userChatterID = ChatterID.getUserChatterID(this.userManager.getUserID(), objInfo.getId());
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
                    this.pickedAvatarNameRetriever = new ChatterNameRetriever(userChatterID, (ChatterNameRetriever.OnChatterNameUpdated)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$18(this), UIThreadExecutor.getInstance());
                }
            }
            else {
                Debug.Printf("ObjectPick: picked object %d", objInfo.localID);
                this.selectedObjectProfile.subscribe(this.userManager.getObjectsManager().getObjectProfile(), objInfo.localID);
            }
            this.setControlsPage(ControlsPage.pageObject);
            this.updateObjectPanel();
        }
        else {
            this.selectedObjectProfile.unsubscribe();
            if (this.pickedAvatarNameRetriever != null) {
                this.pickedAvatarNameRetriever.dispose();
                this.pickedAvatarNameRetriever = null;
            }
            this.pickedObject.set(null);
            this.setControlsPage(ControlsPage.pageDefault);
        }
    }
    
    private boolean isViewScrollable(final View view) {
        return view instanceof ListView || view instanceof RecyclerView;
    }
    
    private boolean isVoiceLoggedIn() {
        final Boolean b = this.voiceLoggedIn.getData();
        return b != null && b;
    }
    
    private void onAgentCircuit(final SLAgentCircuit slAgentCircuit) {
        SLAvatarControl avatarControl = null;
        this.updateDrawingEnabled();
        final AtomicReference<SLAvatarControl> avatarControl2 = this.avatarControl;
        if (slAgentCircuit != null) {
            avatarControl = slAgentCircuit.getModules().avatarControl;
        }
        avatarControl2.set(avatarControl);
    }
    
    private void onCardboardTrigger() {
        int id;
        if (this.hoveringOverButton != null) {
            id = this.hoveringOverButton.getId();
        }
        else {
            id = -1;
        }
        int id2;
        if (this.hoveringPressedButton != null) {
            id2 = this.hoveringPressedButton.getId();
        }
        else {
            id2 = -1;
        }
        Debug.Printf("Cardboard: trigger, hover over %d, hover pressed %d, hitPointValid %b", id, id2, this.hitPointValid);
        if (this.hoveringPressedButton == null && this.hitPointValid) {
            final long uptimeMillis = SystemClock.uptimeMillis();
            if (this.insideControls) {
                if (this.hoveringOverButton == null || (this.touchActivatedButtons.contains(this.hoveringOverButton) ^ true)) {
                    final MotionEvent obtain = MotionEvent.obtain(this.insideSince, uptimeMillis, 0, (float)this.hitPointX, (float)this.hitPointY, 0);
                    obtain.setSource(2);
                    this.onScreenControlsLayout.dispatchTouchEvent(obtain);
                    final MotionEvent obtain2 = MotionEvent.obtain(this.insideSince, uptimeMillis + 1L, 1, (float)this.hitPointX, (float)this.hitPointY, 0);
                    obtain2.setSource(2);
                    this.onScreenControlsLayout.dispatchTouchEvent(obtain2);
                }
            }
            else {
                final View$OnTouchListener view$OnTouchListener = this.outsideTouchListeners.get(this.currentControlsPage);
                Debug.Printf("Cardboard: outside touch, listener %s", view$OnTouchListener);
                if (view$OnTouchListener != null) {
                    view$OnTouchListener.onTouch(this.findViewById(this.currentControlsPage.pageViewId), MotionEvent.obtain(uptimeMillis, uptimeMillis + 100L, 1, 0.0f, 0.0f, 0));
                }
            }
        }
    }
    
    private void onCurrentLocationChanged(final CurrentLocationInfo currentLocationInfo) {
        this.updateVoiceIndication();
    }
    
    private boolean onDetailsOutsideTouch(final View view, final MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case 1: {
                this.handleBackPressed();
                break;
            }
        }
        return true;
    }
    
    private void onExternalButtonAction(final boolean b) {
        final long uptimeMillis = SystemClock.uptimeMillis();
        if (b) {
            final MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 0, (float)this.hitPointX, (float)this.hitPointY, 0);
            obtain.setSource(2);
            this.onVrTouchInternal(obtain, true);
            this.onCardboardTrigger();
        }
        else {
            final MotionEvent obtain2 = MotionEvent.obtain(uptimeMillis, uptimeMillis, 1, (float)this.hitPointX, (float)this.hitPointY, 0);
            obtain2.setSource(2);
            this.onVrTouchInternal(obtain2, true);
        }
    }
    
    @TargetApi(14)
    private void onExternalTexturePointer(int height, final int hitPointY) {
        this.hitPointValid = true;
        this.hitPointX = height;
        this.hitPointY = hitPointY;
        final GLExternalTexture glExternalTexture = this.externalTextureRef.get();
        if (glExternalTexture != null) {
            int n;
            if (height >= 0 && height < glExternalTexture.getWidth() && hitPointY >= 0 && hitPointY < glExternalTexture.getHeight()) {
                n = 1;
            }
            else {
                n = 0;
            }
            if (n != 0) {
                if (!this.insideControls) {
                    this.insideControls = true;
                    this.insideSince = SystemClock.uptimeMillis();
                    final MotionEvent obtain = MotionEvent.obtain(this.insideSince, this.insideSince, 9, (float)height, (float)hitPointY, 0);
                    obtain.setSource(2);
                    this.onScreenControlsLayout.dispatchGenericMotionEvent(obtain);
                }
                final MotionEvent obtain2 = MotionEvent.obtain(this.insideSince, SystemClock.uptimeMillis(), 7, (float)height, (float)hitPointY, 0);
                obtain2.setSource(2);
                this.onScreenControlsLayout.dispatchGenericMotionEvent(obtain2);
                if (this.currentControlsPage == ControlsPage.pageDetails) {
                    final View matchingView = this.findMatchingView(this.cardboardDetailsPage, height, hitPointY, 0, 0, new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$10(this), this.scrollableViewPoint);
                    if (matchingView != null) {
                        height = matchingView.getHeight();
                        if (this.scrollableViewPoint.y < height / 4) {
                            Debug.Printf("Cardboard: want to scroll up %s", matchingView);
                            if (matchingView instanceof ListView) {
                                ((ListView)matchingView).smoothScrollBy(-100, 500);
                            }
                            else if (matchingView instanceof RecyclerView) {
                                ((RecyclerView)matchingView).smoothScrollBy(0, -100);
                            }
                        }
                        else if (this.scrollableViewPoint.y > height * 3 / 4) {
                            Debug.Printf("Cardboard: want to scroll down %s", matchingView);
                            if (matchingView instanceof ListView) {
                                ((ListView)matchingView).smoothScrollBy(100, 500);
                            }
                            else if (matchingView instanceof RecyclerView) {
                                ((RecyclerView)matchingView).smoothScrollBy(0, 100);
                            }
                        }
                    }
                }
            }
            else if (this.insideControls) {
                this.insideControls = false;
                final MotionEvent obtain3 = MotionEvent.obtain(this.insideSince, SystemClock.uptimeMillis(), 10, (float)height, (float)hitPointY, 0);
                obtain3.setSource(2);
                this.onScreenControlsLayout.dispatchGenericMotionEvent(obtain3);
            }
        }
    }
    
    private void onMyAvatarState(final MyAvatarState myAvatarState) {
        if (myAvatarState.isSitting()) {
            this.moveButtonsLayout.setVisibility(0);
            this.buttonStandUp.setVisibility(0);
            this.ownAvatarVisible = true;
        }
        else {
            if (this.ownAvatarVisible) {
                final SLAvatarControl slAvatarControl = this.avatarControl.get();
                if (slAvatarControl != null) {
                    slAvatarControl.setCameraManualControl(false);
                    slAvatarControl.setAgentHeading(this.neutralAgentHeading);
                }
            }
            this.moveButtonsLayout.setVisibility(8);
            this.buttonStandUp.setVisibility(8);
            this.ownAvatarVisible = false;
        }
    }
    
    private void onNewExternalTexture(final GLExternalTexture newValue) {
        this.externalTextureRef.set(newValue);
        this.findViewById(2131755286).setFixedSize(newValue.getWidth(), newValue.getHeight());
    }
    
    private void onPickedAvatarNameUpdated(final ChatterNameRetriever chatterNameRetriever) {
        this.objectNameView.setText((CharSequence)chatterNameRetriever.getResolvedName());
    }
    
    private void onSelectedObjectProfile(final SLObjectProfileData slObjectProfileData) {
        this.objectNameView.setText((CharSequence)slObjectProfileData.name().or(this.getString(2131296825)));
    }
    
    private void onViewsInvalidated() {
        if (!this.viewDrawPosted.getAndSet(true)) {
            Debug.Printf("Cardboard: posting draw views", new Object[0]);
            this.handler.post((Runnable)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$25(this));
        }
    }
    
    private void onVoiceActiveChatter(final ChatterID chatterID) {
        if (this.voiceStatusView != null) {
            this.voiceStatusView.setChatterID(chatterID);
        }
        if (this.userManager != null && chatterID != null) {
            this.voiceChatInfo.subscribe(this.userManager.getVoiceChatInfo(), chatterID);
        }
        else {
            this.voiceChatInfo.unsubscribe();
        }
    }
    
    private void onVoiceChatInfo(final VoiceChatInfo voiceChatInfo) {
        this.updateVoiceIndication();
    }
    
    private void onVoiceLoginStatusChanged(final Boolean b) {
        this.updateVoiceIndication();
    }
    
    private boolean onVrTouch(final View view, final MotionEvent motionEvent) {
        return this.onVrTouchInternal(motionEvent, false);
    }
    
    private boolean onVrTouchInternal(final MotionEvent motionEvent, final boolean b) {
        int id = -1;
        final int actionMasked = motionEvent.getActionMasked();
        String s;
        if (actionMasked == 0) {
            s = "down";
        }
        else if (actionMasked == 1) {
            s = "up";
        }
        else {
            s = null;
        }
        if (s != null) {
            int id2;
            if (this.hoveringOverButton != null) {
                id2 = this.hoveringOverButton.getId();
            }
            else {
                id2 = -1;
            }
            if (this.hoveringPressedButton != null) {
                id = this.hoveringPressedButton.getId();
            }
            Debug.Printf("Cardboard: vr touch %s, hover over %d, hover pressed %d", s, id2, id);
        }
        if (this.hoveringPressedButton != null && actionMasked == 1) {
            Debug.Printf("Cardboard: touch act: release on button %s", this.hoveringPressedButton);
            this.hoveringPressedButton.dispatchTouchEvent(motionEvent);
            this.hoveringPressedButton = null;
            return true;
        }
        if (this.currentControlsPage == ControlsPage.pageDefault) {
            if (this.hoveringOverButton == null) {
                if (!b) {
                    switch (actionMasked) {
                        default: {
                            Debug.Printf("Cardboard: MotionEvent: %s", motionEvent.toString());
                            break;
                        }
                        case 0: {
                            this.startWalking(true);
                            break;
                        }
                        case 1: {
                            this.stopWalking();
                            break;
                        }
                    }
                }
            }
            else if (this.touchActivatedButtons.contains(this.hoveringOverButton) && actionMasked == 0) {
                Debug.Printf("Cardboard: touch act: press on button %s", this.hoveringOverButton);
                this.hoveringPressedButton = this.hoveringOverButton;
                this.hoveringOverButton.dispatchTouchEvent(motionEvent);
            }
        }
        return false;
    }
    
    private void setControlsPage(@Nonnull final ControlsPage currentControlsPage) {
        for (final ControlsPage controlsPage : ControlsPage.values()) {
            final View viewById = this.findViewById(controlsPage.pageViewId);
            int visibility;
            if (controlsPage == currentControlsPage) {
                visibility = 0;
            }
            else {
                visibility = 4;
            }
            viewById.setVisibility(visibility);
        }
        this.currentControlsPage = currentControlsPage;
    }
    
    private void showSpeechRecognitionError(final String text) {
        this.speakNowText.setVisibility(4);
        this.speakLevelIndicator.setVisibility(4);
        this.buttonSpeechSend.setVisibility(4);
        this.speechRecognitionResults.setText((CharSequence)text);
        this.isSpeechFinished = true;
    }
    
    private void startWalking(final boolean b) {
        final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
        if (slAgentCircuit != null) {
            this.isWalking = true;
            final SLAvatarControl avatarControl = slAgentCircuit.getModules().avatarControl;
            avatarControl.setAgentHeading(this.headAgentHeading);
            int n;
            if (b) {
                n = 2;
            }
            else {
                n = 4;
            }
            avatarControl.StartAgentMotion(n);
        }
    }
    
    private void stopWalking() {
        final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
        if (slAgentCircuit != null) {
            slAgentCircuit.getModules().avatarControl.StopAgentMotion();
            this.isWalking = false;
        }
    }
    
    private void updateDrawingEnabled() {
        final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
        if (slAgentCircuit != null && this.renderSettings != null) {
            if (this.isResumed) {
                slAgentCircuit.getModules().drawDistance.Enable3DView(this.renderSettings.drawDistance);
            }
            else {
                slAgentCircuit.getModules().drawDistance.Disable3DView();
            }
        }
    }
    
    private void updateExternalTexturePointer() {
        if (!this.hitPointUpdatePosted.getAndSet(false)) {
            return;
        }
        synchronized (this.hitPointLock) {
            final int postedHitPointX = this.postedHitPointX;
            final int postedHitPointY = this.postedHitPointY;
            monitorexit(this.hitPointLock);
            this.onExternalTexturePointer(postedHitPointX, postedHitPointY);
        }
    }
    
    private void updateObjectPanel() {
        final ObjectIntersectInfo objectIntersectInfo = this.pickedObject.get();
        SLObjectInfo objInfo;
        if (objectIntersectInfo != null) {
            objInfo = objectIntersectInfo.objInfo;
        }
        else {
            objInfo = null;
        }
        if (objInfo == null) {
            this.setControlsPage(ControlsPage.pageDefault);
        }
        else if (objInfo.isAvatar()) {
            this.buttonSit.setVisibility(8);
            this.buttonTouchObject.setVisibility(8);
            this.buttonObjectChat.setVisibility(0);
        }
        else {
            this.buttonSit.setVisibility(0);
            final ImageButton buttonTouchObject = this.buttonTouchObject;
            int visibility;
            if (objInfo.isTouchable()) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            buttonTouchObject.setVisibility(visibility);
            this.buttonObjectChat.setVisibility(8);
        }
    }
    
    private void updateVoiceIndication() {
        final boolean voiceLoggedIn = this.isVoiceLoggedIn();
        final CurrentLocationInfo currentLocationInfo = this.currentLocationInfo.getData();
        this.voiceStatusView.setCanConnect(voiceLoggedIn && currentLocationInfo != null && currentLocationInfo.parcelVoiceChannel() != null);
        final ChatterID chatterID = this.voiceActiveChatter.getData();
        final VoiceChatInfo voiceChatInfo = this.voiceChatInfo.getData();
        if (chatterID != null && voiceChatInfo != null && voiceChatInfo.state != VoiceChatInfo.VoiceChatState.None) {
            this.buttonSpeak.setVisibility(4);
        }
        else {
            this.buttonSpeak.setVisibility(0);
        }
    }
    
    @Override
    protected boolean acceptsDetailFragment(final Class<? extends Fragment> clazz) {
        return ContactsFragment.class.isAssignableFrom(clazz) || ChatFragment.class.isAssignableFrom(clazz);
    }
    
    @Override
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        Debug.Printf("Cardboard: dispatch key event: keycode %d", keyEvent.getKeyCode());
        if (this.avatarControl.get() != null) {
            switch (keyEvent.getKeyCode()) {
                case 21: {
                    if (keyEvent.getAction() == 0) {
                        this.handleMoveControl(MoveControl.Left, 1.0f);
                    }
                    else if (keyEvent.getAction() == 1) {
                        this.handleMoveControl(MoveControl.Left, 0.0f);
                    }
                    return true;
                }
                case 22: {
                    if (keyEvent.getAction() == 0) {
                        this.keypadActive.set(true);
                        this.handleMoveControl(MoveControl.Right, 1.0f);
                    }
                    else if (keyEvent.getAction() == 1) {
                        this.handleMoveControl(MoveControl.Right, 0.0f);
                    }
                    return true;
                }
                case 19: {
                    if (keyEvent.getAction() == 0) {
                        this.keypadActive.set(true);
                        this.handleMoveControl(MoveControl.Forward, 1.0f);
                    }
                    else if (keyEvent.getAction() == 1) {
                        this.handleMoveControl(MoveControl.Forward, 0.0f);
                    }
                    return true;
                }
                case 20: {
                    if (keyEvent.getAction() == 0) {
                        this.keypadActive.set(true);
                        this.handleMoveControl(MoveControl.Backward, 1.0f);
                    }
                    else if (keyEvent.getAction() == 1) {
                        this.handleMoveControl(MoveControl.Backward, 0.0f);
                    }
                    return true;
                }
                case 62:
                case 96: {
                    if (keyEvent.getAction() == 0) {
                        this.onExternalButtonAction(true);
                    }
                    else if (keyEvent.getAction() == 1) {
                        this.onExternalButtonAction(false);
                    }
                    return true;
                }
                case 23: {
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(keyEvent);
    }
    
    @Override
    public boolean handleBackPressed() {
        return this.currentControlsPage == ControlsPage.pageDetails && super.handleBackPressed();
    }
    
    @OnTouch({ 2131755265 })
    public boolean onAimControlsTouch(final View view, final MotionEvent motionEvent) {
        Debug.Printf("Cardboard: aim controls touched, view %s", view);
        switch (motionEvent.getActionMasked()) {
            case 1: {
                this.touchRequested.set(true);
                break;
            }
        }
        return true;
    }
    
    @OnTouch({ 2131755260, 2131755262, 2131755261, 2131755263 })
    public boolean onCamButtonTouch(final View view, final MotionEvent motionEvent) {
        float n = 1.0f;
        float n2 = -1.0f;
        Debug.Printf("Cardboard: cam button: event %s button %s", motionEvent, view);
        final SLAvatarControl slAvatarControl = this.avatarControl.get();
        if (slAvatarControl != null) {
            if (motionEvent.getActionMasked() == 0) {
                switch (view.getId()) {
                    default: {
                        n = 0.0f;
                        n2 = 0.0f;
                        break;
                    }
                    case 2131755260: {
                        n2 = 1.0f;
                        n = 0.0f;
                        break;
                    }
                    case 2131755262: {
                        n = 0.0f;
                        break;
                    }
                    case 2131755261: {
                        n2 = 0.0f;
                        break;
                    }
                    case 2131755263: {
                        n = -1.0f;
                        n2 = 0.0f;
                        break;
                    }
                }
                slAvatarControl.startCameraManualControl(0.0f, n2, 0.0f, n);
            }
            else if (motionEvent.getActionMasked() == 1) {
                slAvatarControl.stopCameraManualControl();
            }
        }
        return true;
    }
    
    @OnClick({ 2131755257 })
    public void onChatButton() {
        if (this.userManager != null) {
            this.setControlsPage(ControlsPage.pageDetails);
            final Bundle fragmentArguments = ActivityUtils.makeFragmentArguments(this.userManager.getUserID(), null);
            fragmentArguments.putBoolean("vrMode", true);
            DetailsActivity.showEmbeddedDetails(this, ContactsFragment.class, fragmentArguments);
        }
    }
    
    @Override
    protected void onCreate(final Bundle bundle) {
        this.setTheme(2131427372);
        super.onCreate(bundle);
        this.requestWindowFeature(1);
        this.getWindow().setFlags(1024, 1024);
        this.fullscreenMode = new FullscreenMode(this.getWindow());
        this.setContentView(2130968610);
        this.userManager = ActivityUtils.getUserManager(this.getIntent());
        if (this.userManager == null) {
            this.finish();
            return;
        }
        AndroidCompat.trySetVrModeEnabled(this, true);
        AndroidCompat.setSustainedPerformanceMode(this, true);
        this.renderSettings = new RenderSettings(PreferenceManager.getDefaultSharedPreferences(this.getBaseContext()));
        this.stateHandler = new Handler();
        Debug.Printf("Cardboard: creating VR view", new Object[0]);
        final GlobalOptions instance = GlobalOptions.getInstance();
        final CardboardControlsPlaceholder cardboardControlsPlaceholder = this.findViewById(2131755286);
        this.onScreenControlsLayout = (ViewGroup)this.getLayoutInflater().inflate(2130968609, (ViewGroup)cardboardControlsPlaceholder, true);
        cardboardControlsPlaceholder.setOnViewInvalidateListener((CardboardControlsPlaceholder.OnViewInvalidateListener)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$19(this));
        final int n = (int)TypedValue.applyDimension(2, 16.0f, this.getResources().getDisplayMetrics());
        int voiceViewHeightAllowance;
        if (instance.getVoiceEnabled()) {
            voiceViewHeightAllowance = (int)TypedValue.applyDimension(1, 60.0f, this.getResources().getDisplayMetrics());
        }
        else {
            voiceViewHeightAllowance = 0;
        }
        this.voiceViewHeightAllowance = voiceViewHeightAllowance;
        (this.renderer = new WorldViewRenderer(this.stateHandler, true, this.userManager, n)).setDrawDistance(this.renderSettings.drawDistance);
        this.renderer.setAvatarCountLimit(this.renderSettings.avatarCountLimit);
        this.renderer.setForcedTime(instance.getForceDaylightTime(), instance.getForceDaylightHour());
        (this.gvrView = new GvrView((Context)this)).setDistortionCorrectionEnabled(true);
        this.gvrView.setAsyncReprojectionEnabled(true);
        this.gvrView.setRenderer(this.stereoRenderer);
        this.controllerManager = new ControllerManager((Context)this, this.controllerManagerEventListener);
        Debug.Printf("Cardboard: has magnet: %b", this.gvrView.getGvrViewerParams().getHasMagnet());
        this.findViewById(2131755287).addView((View)this.gvrView, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -1));
        this.gvrView.setOnCardboardTriggerListener(new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$24(this));
        ButterKnife.bind(this, (View)this.onScreenControlsLayout);
        for (final View view : new View[] { (View)this.buttonTouch, (View)this.buttonSpeak, (View)this.buttonChat, (View)this.buttonSpeechSend, (View)this.buttonSit, (View)this.buttonTouchObject, (View)this.buttonObjectChat, (View)this.buttonMoveForward, (View)this.buttonMoveBackward, (View)this.buttonTurnLeft, (View)this.buttonTurnRight, (View)this.buttonStandUp, (View)this.yesButton, (View)this.noButton }) {
            view.setAlpha(0.5f);
            view.setOnHoverListener(this.onHoverListener);
        }
        this.touchActivatedButtons.add((View)this.buttonMoveForward);
        this.touchActivatedButtons.add((View)this.buttonMoveBackward);
        this.touchActivatedButtons.add((View)this.buttonTurnLeft);
        this.touchActivatedButtons.add((View)this.buttonTurnRight);
        this.buttonStandUp.setVisibility(8);
        this.moveButtonsLayout.setVisibility(8);
        this.voiceStatusView.setShowActiveChatterName(true);
        this.voiceStatusView.hideBackground();
        this.voiceStatusView.setLightTheme();
        this.voiceStatusView.enableHover(this.hoverListener);
        this.voiceStatusView.setOnCallButtonListener(this.onVoiceCallButtonListener);
        final int compoundDrawablePadding = (int)TypedValue.applyDimension(1, 1.0f, this.getResources().getDisplayMetrics());
        final int[] dialogButtonIds = CardboardActivity.dialogButtonIds;
        for (int length2 = dialogButtonIds.length, j = 0; j < length2; ++j) {
            final Button button = this.findViewById(dialogButtonIds[j]);
            button.setAlpha(0.5f);
            button.setOnHoverListener(this.onHoverListener);
            button.setOnClickListener(this.onDialogButtonClick);
            button.setPadding(compoundDrawablePadding, compoundDrawablePadding, compoundDrawablePadding, compoundDrawablePadding);
            button.setCompoundDrawablePadding(compoundDrawablePadding);
        }
        this.dialogQuestionText.setTextColor(-1);
        this.fadingTextViewLog = new FadingTextViewLog(this.userManager, (Context)this, this.chatsOverlayLayout, -1, 0);
        this.setControlsPage(ControlsPage.pageDefault);
        this.speechRecognitionResults.getViewTreeObserver().addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$8(this));
        this.cardboardPrimaryControls.getViewTreeObserver().addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$9(this));
        this.gvrView.setOnTouchListener((View$OnTouchListener)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$7(this));
    }
    
    @Override
    protected void onDestroy() {
        if (this.gvrView != null) {
            this.gvrView.setOnCardboardTriggerListener(null);
            this.gvrView.shutdown();
            this.gvrView = null;
        }
        super.onDestroy();
    }
    
    @Override
    protected boolean onDetailsStackEmpty() {
        super.onDetailsStackEmpty();
        this.setControlsPage(ControlsPage.pageDefault);
        return true;
    }
    
    @Override
    public void onNewObjectPopup(final SLChatEvent slChatEvent) {
        if (slChatEvent instanceof SLChatScriptDialog) {
            if (this.currentControlsPage != ControlsPage.pageYesNo) {
                final SLChatScriptDialog activeScriptDialog = (SLChatScriptDialog)slChatEvent;
                this.activeScriptDialog = activeScriptDialog;
                this.setControlsPage(ControlsPage.pageScriptDialog);
                final String[] buttons = activeScriptDialog.getButtons();
                for (int i = 0; i < CardboardActivity.dialogButtonIds.length; ++i) {
                    final Button button = this.findViewById(CardboardActivity.dialogButtonIds[i]);
                    if (i < buttons.length) {
                        button.setVisibility(0);
                        button.setText((CharSequence)buttons[i]);
                    }
                    else {
                        button.setVisibility(8);
                    }
                }
                this.dialogQuestionText.setText((CharSequence)activeScriptDialog.getRawText());
            }
        }
        else if (slChatEvent instanceof SLChatPermissionRequestEvent) {
            this.activeYesNoEvent = (SLChatYesNoEvent)slChatEvent;
            this.yesNoText.setText((CharSequence)((SLChatPermissionRequestEvent)slChatEvent).getQuestion((Context)this));
            this.setControlsPage(ControlsPage.pageYesNo);
        }
    }
    
    @OnClick({ 2131755275 })
    public void onNoButton() {
        if (this.activeYesNoEvent != null && this.userManager != null) {
            this.activeYesNoEvent.onYesAction((Context)this, this.userManager);
            this.activeYesNoEvent = null;
        }
        this.handlePickedObject(null);
        this.setControlsPage(ControlsPage.pageDefault);
    }
    
    @OnClick({ 2131755270 })
    public void onObjectChat() {
        final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
        final ObjectIntersectInfo objectIntersectInfo = this.pickedObject.get();
        SLObjectInfo objInfo;
        if (objectIntersectInfo != null) {
            objInfo = objectIntersectInfo.objInfo;
        }
        else {
            objInfo = null;
        }
        if (slAgentCircuit != null && objInfo != null && this.userManager != null && objInfo.isAvatar()) {
            this.startDictation(ChatterID.getUserChatterID(this.userManager.getUserID(), objInfo.getId()));
        }
    }
    
    @OnTouch({ 2131755266 })
    public boolean onObjectControlsTouch(final View view, final MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case 1: {
                this.handlePickedObject(null);
                this.setControlsPage(ControlsPage.pageDefault);
                break;
            }
        }
        return true;
    }
    
    @Override
    public void onObjectPopupCountChanged(final int n) {
    }
    
    @OnClick({ 2131755269 })
    public void onObjectSit() {
        final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
        final ObjectIntersectInfo objectIntersectInfo = this.pickedObject.get();
        SLObjectInfo objInfo;
        if (objectIntersectInfo != null) {
            objInfo = objectIntersectInfo.objInfo;
        }
        else {
            objInfo = null;
        }
        if (slAgentCircuit != null && objInfo != null) {
            slAgentCircuit.getModules().avatarControl.SitOnObject(objInfo.getId());
            this.handlePickedObject(null);
            this.setControlsPage(ControlsPage.pageDefault);
        }
    }
    
    @OnClick({ 2131755268 })
    public void onObjectTouch() {
        final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
        final ObjectIntersectInfo objectIntersectInfo = this.pickedObject.get();
        SLObjectInfo objInfo;
        if (objectIntersectInfo != null) {
            objInfo = objectIntersectInfo.objInfo;
        }
        else {
            objInfo = null;
        }
        if (slAgentCircuit != null && objInfo != null && !objInfo.isAvatar()) {
            if (objectIntersectInfo.intersectInfo.faceKnown) {
                final LLVector3 absolutePosition = objInfo.getAbsolutePosition();
                slAgentCircuit.TouchObjectFace(objInfo, objectIntersectInfo.intersectInfo.faceID, absolutePosition.x, absolutePosition.y, absolutePosition.z, objectIntersectInfo.intersectInfo.u, objectIntersectInfo.intersectInfo.v, objectIntersectInfo.intersectInfo.s, objectIntersectInfo.intersectInfo.t);
            }
            else {
                slAgentCircuit.TouchObject(objInfo.localID);
            }
        }
    }
    
    @Override
    protected void onPause() {
        if (this.speechRecognizer != null) {
            this.speechRecognizer.destroy();
            this.speechRecognizer = null;
        }
        this.myAvatarState.unsubscribe();
        while (true) {
            if (this.userManager == null) {
                break Label_0075;
            }
            this.userManager.getObjectPopupsManager().removeObjectPopupListener((ObjectPopupsManager.ObjectPopupListener)this);
            this.userManager.getObjectPopupsManager().removePopupWatcher(this);
            try {
                this.userManager.getChatterList().getActiveChattersManager().getChatEventBus().unregister(this.chatEventHandler);
                this.isResumed = false;
                this.updateDrawingEnabled();
                if (this.gvrView != null) {
                    this.gvrView.onPause();
                }
                this.screenOnFlagHelper.stop();
                super.onPause();
            }
            catch (final IllegalArgumentException ex) {
                continue;
            }
            break;
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (this.gvrView != null) {
            this.gvrView.onResume();
        }
        this.fullscreenMode.goFullscreen();
        this.screenOnFlagHelper.start();
        if (this.speechRecognizer == null) {
            if (SpeechRecognizer.isRecognitionAvailable((Context)this)) {
                Debug.Printf("Cardboard: speech recognition is available", new Object[0]);
                (this.speechRecognizer = SpeechRecognizer.createSpeechRecognizer((Context)this)).setRecognitionListener(this.recognitionListener);
            }
            else {
                Debug.Printf("Cardboard: speech recognition is not available", new Object[0]);
                this.speechRecognizer = null;
            }
        }
        this.isResumed = true;
        this.updateDrawingEnabled();
        if (this.userManager != null) {
            this.userManager.getObjectPopupsManager().addPopupWatcher(this);
            this.userManager.getObjectPopupsManager().setObjectPopupListener((ObjectPopupsManager.ObjectPopupListener)this, UIThreadExecutor.getInstance());
            this.myAvatarState.subscribe(this.userManager.getObjectsManager().myAvatarState(), SubscriptionSingleKey.Value);
            this.userManager.getChatterList().getActiveChattersManager().getChatEventBus().register(this.chatEventHandler);
        }
    }
    
    @OnTouch({ 2131755282 })
    public boolean onScriptDialogOutsideTouch(final View view, final MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case 1: {
                if (this.activeScriptDialog != null) {
                    this.activeScriptDialog.onDialogIgnored(this.userManager);
                    this.activeScriptDialog = null;
                }
                this.handlePickedObject(null);
                this.setControlsPage(ControlsPage.pageDefault);
                break;
            }
        }
        return true;
    }
    
    @OnClick({ 2131755256 })
    public void onSpeakButton() {
        if (this.userManager != null) {
            this.startDictation(ChatterID.getLocalChatterID(this.userManager.getUserID()));
        }
    }
    
    @OnTouch({ 2131755277 })
    public boolean onSpeakControlsTouch(final View view, final MotionEvent motionEvent) {
        Debug.Printf("Cardboard: speak controls touched, view %s", view);
        switch (motionEvent.getActionMasked()) {
            case 1: {
                this.closeSpeechControls();
                break;
            }
        }
        return true;
    }
    
    @OnClick({ 2131755281 })
    public void onSpeechSendButton() {
        if (!Strings.isNullOrEmpty(this.lastSpeechRecognitionResults)) {
            if (this.userManager != null) {
                final SLAgentCircuit activeAgentCircuit = this.userManager.getActiveAgentCircuit();
                if (activeAgentCircuit != null && this.dictationChatterID != null) {
                    activeAgentCircuit.SendChatMessage(this.dictationChatterID, this.lastSpeechRecognitionResults);
                }
            }
            this.lastSpeechRecognitionResults = "";
        }
        this.closeSpeechControls();
    }
    
    @OnClick({ 2131755264 })
    public void onStandUpButton() {
        final SLAvatarControl slAvatarControl = this.avatarControl.get();
        if (slAvatarControl != null) {
            slAvatarControl.Stand();
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        this.voiceEnabled = GlobalOptions.getInstance().getVoiceEnabled();
        this.controllerManager.start();
        this.controller = this.controllerManager.getController();
        if (this.controller != null) {
            this.controller.setEventListener(this.controllerEventListener);
        }
        if (this.userManager != null) {
            this.agentCircuit.subscribe(UserManager.agentCircuits(), this.userManager.getUserID());
            this.voiceLoggedIn.subscribe(this.userManager.getVoiceLoggedIn(), SubscriptionSingleKey.Value);
            this.voiceActiveChatter.subscribe(this.userManager.getVoiceActiveChatter(), SubscriptionSingleKey.Value);
            this.currentLocationInfo.subscribe(this.userManager.getCurrentLocationInfo(), SubscriptionSingleKey.Value);
        }
        if (this.voiceEnabled) {
            this.voiceStatusView.setShowWhenInactive(true);
        }
        else {
            this.voiceStatusView.setShowWhenInactive(false);
        }
    }
    
    @Override
    protected void onStop() {
        this.voiceActiveChatter.unsubscribe();
        this.voiceLoggedIn.unsubscribe();
        this.currentLocationInfo.unsubscribe();
        this.agentCircuit.unsubscribe();
        this.voiceStatusView.setChatterID(null);
        this.keypadActive.set(false);
        this.controllerManager.stop();
        super.onStop();
    }
    
    @OnClick({ 2131755255 })
    public void onTouchButton() {
        this.setControlsPage(ControlsPage.pageTouchAim);
    }
    
    public void onWindowFocusChanged(final boolean b) {
        super.onWindowFocusChanged(b);
        this.fullscreenMode.onWindowFocusChanged(b);
    }
    
    @OnClick({ 2131755274 })
    public void onYesButton() {
        if (this.activeYesNoEvent != null && this.userManager != null) {
            this.activeYesNoEvent.onYesAction((Context)this, this.userManager);
            this.activeYesNoEvent = null;
        }
        this.handlePickedObject(null);
        this.setControlsPage(ControlsPage.pageDefault);
    }
    
    @OnTouch({ 2131755272 })
    public boolean onYesNoOutsideTouch(final View view, final MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case 1: {
                this.activeYesNoEvent = null;
                this.handlePickedObject(null);
                this.setControlsPage(ControlsPage.pageDefault);
                break;
            }
        }
        return true;
    }
    
    public void startDictation(final ChatterID dictationChatterID) {
        this.setControlsPage(ControlsPage.pageSpeech);
        this.dictationChatterID = dictationChatterID;
        this.speakNowText.setVisibility(4);
        this.speakLevelIndicator.setVisibility(4);
        this.buttonSpeechSend.setVisibility(4);
        this.speechRecognitionResults.setText((CharSequence)"");
        if (this.speechRecognizer != null) {
            this.isSpeechFinished = false;
            final Intent intent = new Intent();
            intent.putExtra("android.speech.extra.PARTIAL_RESULTS", true);
            this.speechRecognizer.startListening(intent);
        }
        else {
            this.showSpeechRecognitionError(this.getString(2131297052));
        }
    }
    
    private enum ControlsPage
    {
        pageDefault("pageDefault", 0, 2131755253), 
        pageDetails("pageDetails", 6, 2131755283), 
        pageObject("pageObject", 3, 2131755266), 
        pageScriptDialog("pageScriptDialog", 4, 2131755282), 
        pageSpeech("pageSpeech", 1, 2131755277), 
        pageTouchAim("pageTouchAim", 2, 2131755265), 
        pageYesNo("pageYesNo", 5, 2131755272);
        
        final int pageViewId;
        
        private ControlsPage(final String name, final int ordinal, final int pageViewId) {
            this.pageViewId = pageViewId;
        }
    }
    
    private class WorldStereoRenderer implements StereoRenderer
    {
        private static final float TURN_DEGREES = 35.0f;
        private static final float TURN_DEGREES_PER_MS = 0.02f;
        private static final float YAW_AVERAGE_FACTOR = 1.0E-4f;
        private boolean agentHeadingAcquired;
        private boolean crosshairVisible;
        private final float[] extTextureMatrixUV;
        private GLExternalTexture externalTexture;
        private final float[] eyeHitTests;
        private final float[] eyeOffset;
        private final float[] eyeOffsetMatrix;
        private final float[] eyeProjection;
        private final boolean[] eyeProjectionValid;
        private float eyeSeparation;
        private final int[] eyeViewport;
        private final HeadTransformCompat headTransformCompat;
        private long lastFrameTime;
        private int viewportHeight;
        private int viewportWidth;
        
        WorldStereoRenderer() {
            this.viewportWidth = 0;
            this.viewportHeight = 0;
            this.headTransformCompat = new HeadTransformCompat();
            this.agentHeadingAcquired = false;
            this.eyeHitTests = new float[4];
            this.extTextureMatrixUV = new float[16];
            this.eyeSeparation = 0.0f;
            this.eyeOffset = new float[4];
            this.eyeOffsetMatrix = new float[16];
            this.eyeViewport = new int[4];
            this.eyeProjection = new float[32];
            this.eyeProjectionValid = new boolean[2];
            this.lastFrameTime = 0L;
            this.crosshairVisible = false;
            Matrix.setIdentityM(this.eyeOffsetMatrix, 0);
            Matrix.rotateM(this.eyeOffsetMatrix, 0, -90.0f, 1.0f, 0.0f, 0.0f);
        }
        
        @Override
        public void onDrawEye(final Eye eye) {
            final int type = eye.getType();
            float n;
            if (type == 1) {
                n = -0.5f;
            }
            else {
                n = 0.5f;
            }
            final float n2 = n * this.eyeSeparation;
            for (int i = 0; i < 4; ++i) {
                this.eyeOffset[i] = this.headTransformCompat.rightVector[i] * n2;
            }
            eye.getViewport().getAsArray(this.eyeViewport, 0);
            eye.getEyeView();
            int n3;
            if (type == 1) {
                n3 = 0;
            }
            else {
                n3 = 1;
            }
            if (CardboardActivity.this.renderSettings != null && (!this.eyeProjectionValid[n3] || eye.getProjectionChanged())) {
                System.arraycopy(eye.getPerspective(0.5f, (float)CardboardActivity.this.renderSettings.drawDistance), 0, this.eyeProjection, n3 * 16, 16);
            }
            CardboardActivity.this.renderer.onDrawFrame(null, this.headTransformCompat, this.eyeOffset, this.eyeViewport, null, null, 0);
            if (this.externalTexture != null) {
                int n4;
                if (type == 1) {
                    n4 = 0;
                }
                else {
                    n4 = 2;
                }
                CardboardActivity.this.renderer.drawExternalTexture(this.externalTexture, this.extTextureMatrixUV, n2, this.headTransformCompat.pitchDegrees, this.headTransformCompat.useButtonsYaw, 1.5f, 1.125f, this.eyeHitTests, n4);
                if (this.crosshairVisible) {
                    CardboardActivity.this.renderer.drawCrosshair(0.1f, n2);
                }
            }
        }
        
        @Override
        public void onFinishFrame(final Viewport viewport) {
            CardboardActivity.this.renderer.onFinishFrame();
            if (this.externalTexture == null) {
                return;
            }
            final float n = (this.eyeHitTests[0] + this.eyeHitTests[2]) / 2.0f;
            final float n2 = (this.eyeHitTests[1] + this.eyeHitTests[3]) / 2.0f;
            final int n3 = (int)((n * 2.0f + 0.5f) * this.externalTexture.getWidth());
            final int n4 = (int)((-(n2 * 2.0f) + 0.5f) * this.externalTexture.getHeight());
            synchronized (CardboardActivity.this.hitPointLock) {
                CardboardActivity.this.postedHitPointX = n3;
                CardboardActivity.this.postedHitPointY = n4;
                monitorexit(CardboardActivity.this.hitPointLock);
                if (!CardboardActivity.this.hitPointUpdatePosted.getAndSet(true)) {
                    CardboardActivity.this.runOnUiThread((Runnable)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$22(this));
                }
            }
        }
        
        @Override
        public void onNewFrame(final HeadTransform headTransform) {
            final SLAvatarControl slAvatarControl = CardboardActivity.this.avatarControl.get();
            final long uptimeMillis = SystemClock.uptimeMillis();
            headTransform.getQuaternion(this.headTransformCompat.rotationQuat, 0);
            headTransform.getTranslation(this.headTransformCompat.translationVector, 0);
            headTransform.getHeadView(this.headTransformCompat.headTransformMatrix, 0);
            headTransform.getEulerAngles(this.headTransformCompat.eulerAngles, 0);
            headTransform.getRightVector(this.headTransformCompat.rightVectorRaw, 0);
            Matrix.multiplyMV(this.headTransformCompat.rightVector, 0, this.eyeOffsetMatrix, 0, this.headTransformCompat.rightVectorRaw, 0);
            this.headTransformCompat.yawDegrees = CameraParams.wrapAngle(this.headTransformCompat.eulerAngles[1] / 0.017453292f);
            if (slAvatarControl != null && (this.agentHeadingAcquired ^ true)) {
                this.headTransformCompat.viewExtraYaw = slAvatarControl.getAgentHeading();
                this.agentHeadingAcquired = true;
                Debug.Printf("Cardboard: agent heading acquired: %.2f", this.headTransformCompat.viewExtraYaw);
            }
            if (CardboardActivity.this.currentControlsPage == ControlsPage.pageTouchAim) {
                this.headTransformCompat.pitchDegrees = 0.0f;
                this.headTransformCompat.useButtonsYaw = 0.0f;
                this.headTransformCompat.lastYaw = this.headTransformCompat.eulerAngles[1];
            }
            else if (CardboardActivity.this.currentControlsPage == ControlsPage.pageObject || CardboardActivity.this.currentControlsPage == ControlsPage.pageYesNo) {
                this.headTransformCompat.pitchDegrees = 0.0f;
                this.headTransformCompat.useButtonsYaw = (this.headTransformCompat.eulerAngles[1] - this.headTransformCompat.lastYaw) / 0.017453292f;
            }
            else {
                this.headTransformCompat.pitchDegrees = this.headTransformCompat.eulerAngles[0] / 0.017453292f;
                int n = 0;
                Label_0641: {
                    if (CardboardActivity.this.currentControlsPage == ControlsPage.pageSpeech) {
                        n = 1;
                    }
                    else if (CardboardActivity.this.currentControlsPage == ControlsPage.pageDefault) {
                        int n2 = 1;
                        if (!CardboardActivity.this.insideControls || CardboardActivity.this.hitPointY < CardboardActivity.this.primaryButtonsViewBottom) {
                            n2 = 0;
                        }
                        if (CardboardActivity.this.insideControls) {
                            n = n2;
                            if (CardboardActivity.this.hitPointY <= CardboardActivity.this.moveButtonsTop) {
                                break Label_0641;
                            }
                        }
                        n = n2;
                        if (CardboardActivity.this.ownAvatarVisible) {
                            n = 0;
                        }
                    }
                }
                if (n != 0) {
                    this.headTransformCompat.lastYaw = this.headTransformCompat.eulerAngles[1];
                    this.headTransformCompat.useButtonsYaw = 0.0f;
                }
                else {
                    this.headTransformCompat.useButtonsYaw = (this.headTransformCompat.eulerAngles[1] - this.headTransformCompat.lastYaw) / 0.017453292f;
                }
            }
            if (!this.headTransformCompat.neutralYawValid) {
                this.headTransformCompat.neutralYawValid = true;
                this.headTransformCompat.neutralYaw = this.headTransformCompat.yawDegrees;
            }
            else {
                final long n3 = uptimeMillis - this.lastFrameTime;
                final float angleMinusAngle = CameraParams.angleMinusAngle(this.headTransformCompat.yawDegrees, this.headTransformCompat.neutralYaw);
                boolean b;
                if (CardboardActivity.this.keypadActive.get() || CardboardActivity.this.controllerConnectionState.get() == 3) {
                    b = true;
                }
                else {
                    b = false;
                }
                boolean b2 = false;
                boolean b3 = false;
                float n4 = 1.0f;
                if (b) {
                    final double value = CardboardActivity.this.keypadTurning.get();
                    b2 = (value < 0.0);
                    if (value > 0.0) {
                        b3 = true;
                    }
                    else {
                        b3 = false;
                    }
                    n4 = (float)Math.abs(value);
                }
                if ((angleMinusAngle < -35.0f && (b ^ true)) || b3) {
                    this.headTransformCompat.viewExtraYaw = CameraParams.wrapAngle(this.headTransformCompat.viewExtraYaw - n4 * (n3 * 0.02f));
                }
                else if ((angleMinusAngle > 35.0f && (b ^ true)) || b2) {
                    this.headTransformCompat.viewExtraYaw = CameraParams.wrapAngle(n4 * (n3 * 0.02f) + this.headTransformCompat.viewExtraYaw);
                }
                else {
                    this.headTransformCompat.neutralYaw = CameraParams.wrapAngle(this.headTransformCompat.neutralYaw + 1.0E-4f * angleMinusAngle * n3);
                }
            }
            if (this.headTransformCompat.viewExtraYaw != CardboardActivity.this.neutralAgentHeading || Float.isNaN(CardboardActivity.this.neutralAgentHeading)) {
                CardboardActivity.this.neutralAgentHeading = CameraParams.wrapAngle(this.headTransformCompat.viewExtraYaw);
            }
            CardboardActivity.this.headAgentHeading = CameraParams.wrapAngle(this.headTransformCompat.yawDegrees + this.headTransformCompat.viewExtraYaw);
            if (!Float.isNaN(CardboardActivity.this.neutralAgentHeading) && slAvatarControl != null) {
                float agentHeading;
                if (CardboardActivity.this.isWalking) {
                    agentHeading = CardboardActivity.this.headAgentHeading;
                }
                else {
                    agentHeading = CardboardActivity.this.neutralAgentHeading;
                }
                slAvatarControl.setAgentHeading(agentHeading);
            }
            this.lastFrameTime = uptimeMillis;
            CardboardActivity.this.renderer.setOwnAvatarHidden(CardboardActivity.this.ownAvatarVisible ^ true);
            CardboardActivity.this.renderer.onPrepareFrame(this.headTransformCompat);
            if (CardboardActivity.this.touchRequested.getAndSet(false)) {
                CardboardActivity.this.renderer.pickObject((float)(this.viewportWidth / 2), (float)(this.viewportHeight / 2), CardboardActivity.this.handler);
            }
            final ObjectIntersectInfo objectIntersectInfo = CardboardActivity.this.pickedObject.get();
            final WorldViewRenderer -get25 = CardboardActivity.this.renderer;
            SLObjectInfo objInfo;
            if (objectIntersectInfo != null) {
                objInfo = objectIntersectInfo.objInfo;
            }
            else {
                objInfo = null;
            }
            -get25.setDrawPickedObject(objInfo);
            this.crosshairVisible = (CardboardActivity.this.currentControlsPage == ControlsPage.pageDetails && CardboardActivity.this.insideControls);
            if (this.externalTexture != null) {
                this.externalTexture.update(this.extTextureMatrixUV);
            }
        }
        
        @Override
        public void onRendererShutdown() {
            this.externalTexture.release();
            this.externalTexture = null;
            CardboardActivity.this.renderer.onRendererShutdown();
        }
        
        @Override
        public void onSurfaceChanged(final int viewportWidth, final int viewportHeight) {
            this.viewportWidth = viewportWidth;
            this.viewportHeight = viewportHeight;
            CardboardActivity.this.renderer.onSurfaceChanged(null, viewportWidth, viewportHeight);
            if (this.externalTexture != null) {
                this.externalTexture.release();
            }
            this.externalTexture = new GLExternalTexture((int)(viewportWidth * 1.0f), (int)(viewportHeight * 0.75f) + CardboardActivity.this.voiceViewHeightAllowance);
            this.eyeSeparation = CardboardActivity.this.gvrView.getInterpupillaryDistance();
            this.headTransformCompat.neutralYawValid = false;
            CardboardActivity.this.runOnUiThread((Runnable)new _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$23(this));
        }
        
        @Override
        public void onSurfaceCreated(final EGLConfig eglConfig) {
            CardboardActivity.this.renderer.onSurfaceCreated(null, eglConfig, true);
        }
    }
}
