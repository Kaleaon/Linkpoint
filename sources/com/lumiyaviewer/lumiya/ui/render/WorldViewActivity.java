// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.SLParcelInfo;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.events.SLBakingProgressEvent;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.modules.SLDrawDistance;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.ThemeMapper;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.ContactsFragment;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.ScriptDialogHandler;
import com.lumiyaviewer.lumiya.ui.objects.ObjectDetailsFragment;
import com.lumiyaviewer.lumiya.ui.objects.ObjectPayDialog;
import com.lumiyaviewer.lumiya.ui.objects.TouchableObjectsFragment;
import com.lumiyaviewer.lumiya.ui.outfits.OutfitsFragment;
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            WorldSurfaceView, CardboardTransitionActivity, FadingTextViewLog, RenderSettings

public class WorldViewActivity extends DetailsActivity
    implements android.view.View.OnTouchListener, ThemeMapper, ScriptDialogHandler, com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationManager.NotifyCapture
{
    private static class SelectableAttachment
    {

        private String attachmentName;
        private int localID;

        public int getLocalID()
        {
            return localID;
        }

        public String toString()
        {
            return attachmentName;
        }

        public SelectableAttachment(int i, String s)
        {
            localID = i;
            attachmentName = s;
        }
    }


    private static final long BUTTONS_FADE_TIMEOUT_MILLIS = 7500L;
    private static final String FROM_NOTIFICATION_TAG = "fromNotification";
    private static final long OBJECT_DESELECT_TIMEOUT_MILLIS = 6000L;
    private static final int PERMISSION_AUDIO_REQUEST_CODE = 100;
    private static final float TURNING_SPEED = 50F;
    private final SubscriptionData agentCircuit = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.YnTWxJEMPymM_sHfAdAKQ7gcDf8._cls3(this));
    private boolean arrowsToTurn;
    private SLAvatarControl avatarControl;
    ImageView avatarIconView;
    ImageButton buttonCamOff;
    ImageButton buttonCamOn;
    ImageButton buttonFlyDownward;
    ImageButton buttonFlyUpward;
    Button buttonHUD;
    ImageButton buttonMoveBackward;
    ImageButton buttonMoveForward;
    ImageButton buttonStandUp;
    ImageButton buttonStopFlying;
    ImageButton buttonTurnLeft;
    ImageButton buttonTurnRight;
    private ValueAnimator buttonsFadeAnimator;
    private final Runnable buttonsFadeTask = new Runnable() {

        final WorldViewActivity this$0;

        public void run()
        {
            long l;
label0:
            {
                WorldViewActivity._2D_set0(WorldViewActivity.this, false);
                if (!WorldViewActivity._2D_wrap0(WorldViewActivity.this) && WorldViewActivity._2D_get9(WorldViewActivity.this) ^ true && WorldViewActivity._2D_get0(WorldViewActivity.this).hasData())
                {
                    VoiceChatInfo voicechatinfo = (VoiceChatInfo)WorldViewActivity._2D_get20(WorldViewActivity.this).getData();
                    boolean flag;
                    if (voicechatinfo != null && voicechatinfo.state == com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.Active)
                    {
                        flag = voicechatinfo.localMicActive;
                    } else
                    {
                        flag = false;
                    }
                    if (!flag)
                    {
                        l = SystemClock.uptimeMillis();
                        l = (WorldViewActivity._2D_get12(WorldViewActivity.this) + 7500L) - l;
                        Debug.Printf("ButtonsFade: remaining %d", new Object[] {
                            Long.valueOf(l)
                        });
                        if (l > 0L)
                        {
                            break label0;
                        }
                        WorldViewActivity._2D_wrap3(WorldViewActivity.this);
                    }
                }
                return;
            }
            WorldViewActivity._2D_set0(WorldViewActivity.this, true);
            WorldViewActivity._2D_get15(WorldViewActivity.this).postDelayed(WorldViewActivity._2D_get3(WorldViewActivity.this), l);
        }

            
            {
                this$0 = WorldViewActivity.this;
                super();
            }
    };
    private boolean buttonsFadeTimerStarted;
    private final Runnable buttonsRestoreTask = new Runnable() {

        final WorldViewActivity this$0;

        public void run()
        {
            if (android.os.Build.VERSION.SDK_INT >= 11)
            {
                if (WorldViewActivity._2D_get2(WorldViewActivity.this) != null)
                {
                    WorldViewActivity._2D_get2(WorldViewActivity.this).cancel();
                }
                insetsBackground.setAlpha(1.0F);
            }
        }

            
            {
                this$0 = WorldViewActivity.this;
                super();
            }
    };
    private boolean camButtonEnabled;
    private final Object chatEventHandler = new Object() {

        final WorldViewActivity this$0;

        void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_WorldViewActivity$1_50098(com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager.ChatMessageEvent chatmessageevent)
        {
            handleChatEvent(chatmessageevent);
        }

        public void onChatMessage(com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager.ChatMessageEvent chatmessageevent)
        {
            WorldViewActivity._2D_get15(WorldViewActivity.this).post(new _2D_.Lambda.YnTWxJEMPymM_sHfAdAKQ7gcDf8._cls11(this, chatmessageevent));
        }

            
            {
                this$0 = WorldViewActivity.this;
                super();
            }
    };
    private boolean chatOver3D;
    LinearLayout chatsOverlayLayout;
    private final SubscriptionData currentLocationInfo = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.YnTWxJEMPymM_sHfAdAKQ7gcDf8._cls6(this));
    View detailsContainer;
    private int displayedHUDid;
    View dragPointer;
    ViewGroup dragPointerLayout;
    private SLDrawDistance drawDistance;
    private FadingTextViewLog fadingTextViewLog;
    LinearLayout flyButtonsLayout;
    private GestureDetectorCompat gestureDetector;
    private final android.view.GestureDetector.OnGestureListener gestureListener = new android.view.GestureDetector.SimpleOnGestureListener() {

        final WorldViewActivity this$0;

        public boolean onFling(MotionEvent motionevent, MotionEvent motionevent1, float f, float f1)
        {
            if (!WorldViewActivity._2D_get10(WorldViewActivity.this) && WorldViewActivity._2D_get21(WorldViewActivity.this) ^ true && WorldViewActivity._2D_get9(WorldViewActivity.this) ^ true)
            {
                f = (f * 60F) / (float)worldViewHolder.getHeight();
                f1 = (-f1 * 60F) / (float)worldViewHolder.getHeight();
                if (WorldViewActivity._2D_get1(WorldViewActivity.this) != null)
                {
                    WorldViewActivity._2D_get1(WorldViewActivity.this).processCameraFling(f / 1.5F, f1 / 2.5F);
                }
                return true;
            } else
            {
                return false;
            }
        }

        public void onLongPress(MotionEvent motionevent)
        {
            float f = motionevent.getRawX();
            float f1 = motionevent.getRawY();
            if (WorldViewActivity._2D_get9(WorldViewActivity.this))
            {
                WorldViewActivity._2D_wrap1(WorldViewActivity.this, (int)f, (int)f1);
            } else
            if (!WorldViewActivity._2D_get10(WorldViewActivity.this) && WorldViewActivity._2D_get21(WorldViewActivity.this) ^ true)
            {
                motionevent = new int[2];
                worldViewHolder.getLocationOnScreen(motionevent);
                WorldViewActivity._2D_get14(WorldViewActivity.this).pickObjectHover(f - (float)motionevent[0], f1 - (float)motionevent[1]);
                return;
            }
        }

        public boolean onScroll(MotionEvent motionevent, MotionEvent motionevent1, float f, float f1)
        {
            if (WorldViewActivity._2D_get9(WorldViewActivity.this))
            {
                motionevent = (android.widget.AbsoluteLayout.LayoutParams)dragPointer.getLayoutParams();
                if (motionevent != null)
                {
                    motionevent.x = Math.max(Math.min((int)((float)((android.widget.AbsoluteLayout.LayoutParams) (motionevent)).x - f), dragPointerLayout.getWidth() - dragPointer.getWidth()), 0);
                    motionevent.y = Math.max(Math.min((int)((float)((android.widget.AbsoluteLayout.LayoutParams) (motionevent)).y - f1), dragPointerLayout.getHeight() - dragPointer.getHeight()), 0);
                    dragPointer.setLayoutParams(motionevent);
                    WorldViewActivity._2D_wrap2(WorldViewActivity.this, ((android.widget.AbsoluteLayout.LayoutParams) (motionevent)).x, ((android.widget.AbsoluteLayout.LayoutParams) (motionevent)).y);
                }
                return true;
            }
            if (WorldViewActivity._2D_get10(WorldViewActivity.this) || !(WorldViewActivity._2D_get21(WorldViewActivity.this) ^ true))
            {
                break MISSING_BLOCK_LABEL_318;
            }
            if (WorldViewActivity._2D_get4(WorldViewActivity.this) == 0) goto _L2; else goto _L1
_L1:
            motionevent = WorldViewActivity.this;
            WorldViewActivity._2D_set1(motionevent, WorldViewActivity._2D_get6(motionevent) + f / (float)worldViewHolder.getHeight() / 2.0F);
            motionevent = WorldViewActivity.this;
            WorldViewActivity._2D_set2(motionevent, WorldViewActivity._2D_get7(motionevent) + f1 / (float)worldViewHolder.getHeight() / 2.0F);
            WorldViewActivity._2D_get14(WorldViewActivity.this).setHUDOffset(WorldViewActivity._2D_get6(WorldViewActivity.this), WorldViewActivity._2D_get7(WorldViewActivity.this));
_L4:
            return true;
_L2:
            f = (-f * 60F) / (float)worldViewHolder.getHeight();
            f1 = (f1 * 60F) / (float)worldViewHolder.getHeight();
            if (WorldViewActivity._2D_get1(WorldViewActivity.this) == null) goto _L4; else goto _L3
_L3:
            WorldViewActivity._2D_get1(WorldViewActivity.this).processCameraRotate(f, f1);
            return true;
            return false;
        }

        public boolean onSingleTapUp(MotionEvent motionevent)
        {
            if (WorldViewActivity._2D_get9(WorldViewActivity.this))
            {
                WorldViewActivity._2D_wrap1(WorldViewActivity.this, (int)motionevent.getRawX(), (int)motionevent.getRawY());
                return true;
            }
            if (WorldViewActivity._2D_get4(WorldViewActivity.this) != 0)
            {
                int ai[] = new int[2];
                worldViewHolder.getLocationOnScreen(ai);
                WorldViewActivity._2D_get14(WorldViewActivity.this).touchHUD(motionevent.getRawX() - (float)ai[0], motionevent.getRawY() - (float)ai[1]);
                return true;
            } else
            {
                handlePickedObject(null);
                return true;
            }
        }

            
            {
                this$0 = WorldViewActivity.this;
                super();
            }
    };
    private float hudOffsetX;
    private float hudOffsetY;
    private float hudScaleFactor;
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
    View moveButtonsLayout;
    private final SubscriptionData myAvatarState = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.YnTWxJEMPymM_sHfAdAKQ7gcDf8._cls4(this));
    ImageButton objectChatButton;
    View objectControlsPanel;
    private boolean objectDeselectTimerStarted;
    private final Runnable objectDeselectTimerTask = new Runnable() {

        final WorldViewActivity this$0;

        public void run()
        {
            long l;
label0:
            {
                WorldViewActivity._2D_set6(WorldViewActivity.this, false);
                if (!WorldViewActivity._2D_wrap0(WorldViewActivity.this) && WorldViewActivity._2D_get9(WorldViewActivity.this) ^ true)
                {
                    l = SystemClock.uptimeMillis();
                    l = (WorldViewActivity._2D_get13(WorldViewActivity.this) + 6000L) - l;
                    Debug.Printf("ObjectDeselect: remaining %d", new Object[] {
                        Long.valueOf(l)
                    });
                    if (l > 0L)
                    {
                        break label0;
                    }
                    handlePickedObject(null);
                }
                return;
            }
            WorldViewActivity._2D_set6(WorldViewActivity.this, true);
            WorldViewActivity._2D_get15(WorldViewActivity.this).postDelayed(WorldViewActivity._2D_get16(WorldViewActivity.this), l);
        }

            
            {
                this$0 = WorldViewActivity.this;
                super();
            }
    };
    ImageButton objectMoreButton;
    TextView objectNameTextView;
    ImageButton objectPayButton;
    View objectPopupLeftSpacer;
    ImageButton objectSitButton;
    ImageButton objectStandButton;
    ImageButton objectTouchButton;
    private float oldScaleFocusX;
    private float oldScaleFocusY;
    private ChatterNameRetriever pickedAvatarNameRetriever;
    private ObjectIntersectInfo pickedIntersectInfo;
    private SLObjectInfo pickedObject;
    private int prefDrawDistance;
    private int prevDisplayedHUDid;
    private ScaleGestureDetector scaleGestureDetector;
    private final android.view.ScaleGestureDetector.OnScaleGestureListener scaleGestureListener = new android.view.ScaleGestureDetector.SimpleOnScaleGestureListener() {

        final WorldViewActivity this$0;

        public boolean onScale(ScaleGestureDetector scalegesturedetector)
        {
            Debug.Printf("Gesture: scale factor: %f", new Object[] {
                Float.valueOf(scalegesturedetector.getScaleFactor())
            });
            if (WorldViewActivity._2D_get4(WorldViewActivity.this) != 0)
            {
                WorldViewActivity._2D_set3(WorldViewActivity.this, Math.max(0.1F, Math.min(WorldViewActivity._2D_get8(WorldViewActivity.this) * scalegesturedetector.getScaleFactor(), 10F)));
                WorldViewActivity._2D_get14(WorldViewActivity.this).setHUDScaleFactor(WorldViewActivity._2D_get8(WorldViewActivity.this));
            } else
            {
                float f4 = worldViewTouchReceiver.getWidth();
                float f3 = worldViewTouchReceiver.getHeight();
                float f = scalegesturedetector.getFocusX();
                float f1 = scalegesturedetector.getFocusY();
                float f2 = f / f4;
                f4 = f3 / f4;
                float f5 = f1 / f3;
                float f6 = (f - WorldViewActivity._2D_get17(WorldViewActivity.this)) / f3;
                f3 = (f1 - WorldViewActivity._2D_get18(WorldViewActivity.this)) / f3;
                WorldViewActivity._2D_set7(WorldViewActivity.this, f);
                WorldViewActivity._2D_set8(WorldViewActivity.this, f1);
                if (WorldViewActivity._2D_get1(WorldViewActivity.this) != null)
                {
                    WorldViewActivity._2D_get1(WorldViewActivity.this).processCameraZoom(scalegesturedetector.getScaleFactor(), -((f2 - 0.5F) * f4) * 2.0F, -(f5 - 0.5F) * 2.0F, f6, f3);
                    return true;
                }
            }
            return true;
        }

        public boolean onScaleBegin(ScaleGestureDetector scalegesturedetector)
        {
            WorldViewActivity._2D_set4(WorldViewActivity.this, true);
            WorldViewActivity._2D_set7(WorldViewActivity.this, scalegesturedetector.getFocusX());
            WorldViewActivity._2D_set8(WorldViewActivity.this, scalegesturedetector.getFocusY());
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector scalegesturedetector)
        {
            WorldViewActivity._2D_set4(WorldViewActivity.this, false);
        }

            
            {
                this$0 = WorldViewActivity.this;
                super();
            }
    };
    private final SubscriptionData selectedObjectProfile = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.YnTWxJEMPymM_sHfAdAKQ7gcDf8._cls5(this));
    private UserManager userManager;
    private final SubscriptionData voiceActiveChatter = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.YnTWxJEMPymM_sHfAdAKQ7gcDf8._cls7(this));
    private final SubscriptionData voiceChatInfo = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.YnTWxJEMPymM_sHfAdAKQ7gcDf8._cls8(this));
    VoiceStatusView voiceStatusView;
    private boolean wasInScaling;
    ViewGroup worldOverlaysContainer;
    FrameLayout worldViewHolder;
    private final android.view.View.OnTouchListener worldViewTouchListener = new android.view.View.OnTouchListener() {

        final WorldViewActivity this$0;

        public boolean onTouch(View view, MotionEvent motionevent)
        {
            boolean flag1 = WorldViewActivity._2D_get11(WorldViewActivity.this);
            motionevent.getActionMasked();
            JVM INSTR tableswitch 0 1: default 36
        //                       0 143
        //                       1 157;
               goto _L1 _L2 _L3
_L1:
            boolean flag = false;
_L5:
            if (WorldViewActivity._2D_get11(WorldViewActivity.this) && flag1 ^ true)
            {
                WorldViewActivity._2D_get14(WorldViewActivity.this).setIsInteracting(true);
            }
            WorldViewActivity._2D_set9(WorldViewActivity.this, WorldViewActivity._2D_get10(WorldViewActivity.this));
            boolean flag2 = WorldViewActivity._2D_get19(WorldViewActivity.this).onTouchEvent(motionevent);
            boolean flag3 = WorldViewActivity._2D_get5(WorldViewActivity.this).onTouchEvent(motionevent);
            if (flag1 && WorldViewActivity._2D_get11(WorldViewActivity.this) ^ true)
            {
                WorldViewActivity._2D_get14(WorldViewActivity.this).setIsInteracting(false);
            }
            return flag | flag2 | flag3;
_L2:
            WorldViewActivity._2D_set5(WorldViewActivity.this, true);
            flag = true;
            continue; /* Loop/switch isn't completed */
_L3:
            WorldViewActivity._2D_set5(WorldViewActivity.this, false);
            flag = true;
            if (true) goto _L5; else goto _L4
_L4:
        }

            
            {
                this$0 = WorldViewActivity.this;
                super();
            }
    };
    View worldViewTouchReceiver;

    static SubscriptionData _2D_get0(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.agentCircuit;
    }

    static SLAvatarControl _2D_get1(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.avatarControl;
    }

    static boolean _2D_get10(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.isInScaling;
    }

    static boolean _2D_get11(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.isInteracting;
    }

    static long _2D_get12(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.lastActivityTime;
    }

    static long _2D_get13(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.lastObjectActivityTime;
    }

    static WorldSurfaceView _2D_get14(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.mGLView;
    }

    static Handler _2D_get15(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.mHandler;
    }

    static Runnable _2D_get16(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.objectDeselectTimerTask;
    }

    static float _2D_get17(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.oldScaleFocusX;
    }

    static float _2D_get18(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.oldScaleFocusY;
    }

    static ScaleGestureDetector _2D_get19(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.scaleGestureDetector;
    }

    static ValueAnimator _2D_get2(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.buttonsFadeAnimator;
    }

    static SubscriptionData _2D_get20(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.voiceChatInfo;
    }

    static boolean _2D_get21(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.wasInScaling;
    }

    static Runnable _2D_get3(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.buttonsFadeTask;
    }

    static int _2D_get4(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.displayedHUDid;
    }

    static GestureDetectorCompat _2D_get5(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.gestureDetector;
    }

    static float _2D_get6(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.hudOffsetX;
    }

    static float _2D_get7(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.hudOffsetY;
    }

    static float _2D_get8(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.hudScaleFactor;
    }

    static boolean _2D_get9(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.isDragging;
    }

    static boolean _2D_set0(WorldViewActivity worldviewactivity, boolean flag)
    {
        worldviewactivity.buttonsFadeTimerStarted = flag;
        return flag;
    }

    static float _2D_set1(WorldViewActivity worldviewactivity, float f)
    {
        worldviewactivity.hudOffsetX = f;
        return f;
    }

    static float _2D_set2(WorldViewActivity worldviewactivity, float f)
    {
        worldviewactivity.hudOffsetY = f;
        return f;
    }

    static float _2D_set3(WorldViewActivity worldviewactivity, float f)
    {
        worldviewactivity.hudScaleFactor = f;
        return f;
    }

    static boolean _2D_set4(WorldViewActivity worldviewactivity, boolean flag)
    {
        worldviewactivity.isInScaling = flag;
        return flag;
    }

    static boolean _2D_set5(WorldViewActivity worldviewactivity, boolean flag)
    {
        worldviewactivity.isInteracting = flag;
        return flag;
    }

    static boolean _2D_set6(WorldViewActivity worldviewactivity, boolean flag)
    {
        worldviewactivity.objectDeselectTimerStarted = flag;
        return flag;
    }

    static float _2D_set7(WorldViewActivity worldviewactivity, float f)
    {
        worldviewactivity.oldScaleFocusX = f;
        return f;
    }

    static float _2D_set8(WorldViewActivity worldviewactivity, float f)
    {
        worldviewactivity.oldScaleFocusY = f;
        return f;
    }

    static boolean _2D_set9(WorldViewActivity worldviewactivity, boolean flag)
    {
        worldviewactivity.wasInScaling = flag;
        return flag;
    }

    static boolean _2D_wrap0(WorldViewActivity worldviewactivity)
    {
        return worldviewactivity.detailsVisible();
    }

    static void _2D_wrap1(WorldViewActivity worldviewactivity, int i, int j)
    {
        worldviewactivity.dragSelectorSetRawPosition(i, j);
    }

    static void _2D_wrap2(WorldViewActivity worldviewactivity, int i, int j)
    {
        worldviewactivity.selectByDragPointer(i, j);
    }

    static void _2D_wrap3(WorldViewActivity worldviewactivity)
    {
        worldviewactivity.startFadingButtons();
    }

    public WorldViewActivity()
    {
        pickedObject = null;
        pickedIntersectInfo = null;
        pickedAvatarNameRetriever = null;
        mHandler = new Handler();
        prefDrawDistance = 20;
        chatOver3D = false;
        lastTouchUUID = null;
        displayedHUDid = 0;
        prevDisplayedHUDid = 0;
        hudScaleFactor = 1.0F;
        hudOffsetX = 0.0F;
        hudOffsetY = 0.0F;
        arrowsToTurn = false;
        camButtonEnabled = false;
        manualCamMode = false;
        localDrawingEnabled = false;
        lastActivityTime = SystemClock.uptimeMillis();
        buttonsFadeTimerStarted = false;
        buttonsFadeAnimator = null;
        lastObjectActivityTime = SystemClock.uptimeMillis();
        objectDeselectTimerStarted = false;
        isInScaling = false;
        oldScaleFocusX = (0.0F / 0.0F);
        oldScaleFocusY = (0.0F / 0.0F);
        wasInScaling = false;
        isInteracting = false;
        isDragging = false;
    }

    private void beginCountingButtonsFade()
    {
        lastActivityTime = SystemClock.uptimeMillis();
        lastObjectActivityTime = lastActivityTime;
        startFadingButtonsTimer();
    }

    private void beginCountingObjectDeselect()
    {
        if (pickedObject != null)
        {
            lastObjectActivityTime = SystemClock.uptimeMillis();
            if (!objectDeselectTimerStarted)
            {
                objectDeselectTimerStarted = true;
                mHandler.postDelayed(objectDeselectTimerTask, 6000L);
            }
        }
    }

    private void beginDragSelection()
    {
        isDragging = true;
        removeAllDetails();
        android.widget.AbsoluteLayout.LayoutParams layoutparams = (android.widget.AbsoluteLayout.LayoutParams)dragPointer.getLayoutParams();
        layoutparams.x = (dragPointerLayout.getWidth() - dragPointer.getWidth()) / 2;
        layoutparams.y = (dragPointerLayout.getHeight() - dragPointer.getHeight()) / 2;
        dragPointer.setLayoutParams(layoutparams);
        selectByDragPointer(layoutparams.x, layoutparams.y);
        mGLView.setOwnAvatarHidden(true);
        updateObjectPanel();
    }

    private void chatWithObject(SLObjectInfo slobjectinfo)
    {
        if ((slobjectinfo instanceof SLObjectAvatarInfo) && !((SLObjectAvatarInfo)slobjectinfo).isMyAvatar() && slobjectinfo.getId() != null)
        {
            DetailsActivity.showEmbeddedDetails(this, com/lumiyaviewer/lumiya/ui/chat/ChatFragment, ChatFragment.makeSelection(ChatterID.getUserChatterID(userManager.getUserID(), slobjectinfo.getId())));
        }
    }

    private boolean detailsVisible()
    {
        Object obj = getSupportFragmentManager();
        if (obj != null)
        {
            obj = ((FragmentManager) (obj)).findFragmentById(0x7f100114);
            if (obj != null && ((Fragment) (obj)).isVisible())
            {
                return true;
            }
        }
        return false;
    }

    private void displayHUD(int i)
    {
        Debug.Printf("Displaying HUD with ID %d", new Object[] {
            Integer.valueOf(i)
        });
        displayedHUDid = i;
        mGLView.setDisplayedHUDid(i);
        if (displayedHUDid != prevDisplayedHUDid)
        {
            hudScaleFactor = 1.0F;
            hudOffsetX = 0.0F;
            hudOffsetY = 0.0F;
            prevDisplayedHUDid = displayedHUDid;
        }
        mGLView.setHUDScaleFactor(hudScaleFactor);
        mGLView.setHUDOffset(hudOffsetX, hudOffsetY);
        if (displayedHUDid != 0)
        {
            handlePickedObject(null);
        }
        updateObjectPanel();
    }

    private void dragSelectorSetRawPosition(int i, int j)
    {
        int ai[] = new int[2];
        dragPointerLayout.getLocationOnScreen(ai);
        int k = dragPointer.getWidth() / 2;
        int l = dragPointer.getHeight() / 2;
        android.widget.AbsoluteLayout.LayoutParams layoutparams = (android.widget.AbsoluteLayout.LayoutParams)dragPointer.getLayoutParams();
        if (layoutparams != null)
        {
            layoutparams.x = Math.max(Math.min(i - k - ai[0], dragPointerLayout.getWidth() - dragPointer.getWidth()), 0);
            layoutparams.y = Math.max(Math.min(j - l - ai[1], dragPointerLayout.getHeight() - dragPointer.getHeight()), 0);
            dragPointer.setLayoutParams(layoutparams);
            selectByDragPointer(layoutparams.x, layoutparams.y);
        }
    }

    private void endDragSelection()
    {
        isDragging = false;
        mGLView.setOwnAvatarHidden(false);
        updateObjectPanel();
        beginCountingButtonsFade();
        beginCountingObjectDeselect();
    }

    private void enterCardboardView()
    {
        if (ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") != 0)
        {
            Debug.Printf("Cardboard: audio permission not yet granted", new Object[0]);
            ActivityCompat.requestPermissions(this, new String[] {
                "android.permission.RECORD_AUDIO"
            }, 100);
            return;
        } else
        {
            Debug.Printf("Cardboard: audio permission already granted", new Object[0]);
            startCardboardActivity();
            return;
        }
    }

    private void initContentView()
    {
        setContentView(0x7f0400bd);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar)findViewById(0x7f1002e1));
        mGLView = new WorldSurfaceView(this, userManager);
        worldViewHolder.addView(mGLView);
        buttonMoveForward.setOnTouchListener(this);
        buttonMoveBackward.setOnTouchListener(this);
        buttonTurnLeft.setOnTouchListener(this);
        buttonTurnRight.setOnTouchListener(this);
        buttonMoveForward.setFocusable(false);
        buttonMoveBackward.setFocusable(false);
        buttonTurnLeft.setFocusable(false);
        buttonTurnRight.setFocusable(false);
        buttonFlyUpward.setOnTouchListener(this);
        buttonFlyDownward.setOnTouchListener(this);
        buttonFlyUpward.setFocusable(false);
        buttonFlyDownward.setFocusable(false);
        voiceStatusView.setShowActiveChatterName(true);
        worldViewTouchReceiver.setOnTouchListener(worldViewTouchListener);
        objectControlsPanel.setVisibility(8);
        View view = findViewById(0x7f100242);
        if (view != null)
        {
            view.setBackgroundColor(Color.argb(128, 0, 0, 0));
            int i = (int)TypedValue.applyDimension(1, 10F, getResources().getDisplayMetrics());
            view.setPadding(i, i, i, i);
        }
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_43728(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    private void onAgentCircuit(SLAgentCircuit slagentcircuit)
    {
        if (slagentcircuit != null)
        {
            avatarControl = slagentcircuit.getModules().avatarControl;
            drawDistance = slagentcircuit.getModules().drawDistance;
            if (localDrawingEnabled)
            {
                drawDistance.Enable3DView(prefDrawDistance);
            }
            if (camButtonEnabled)
            {
                manualCamMode = avatarControl.getIsManualCamming();
            }
        } else
        {
            handlePickedObject(null);
            avatarControl = null;
            drawDistance = null;
        }
        mHandler.post(buttonsRestoreTask);
        beginCountingButtonsFade();
        beginCountingObjectDeselect();
        updateObjectPanel();
    }

    private void onCurrentLocation(CurrentLocationInfo currentlocationinfo)
    {
        if (currentlocationinfo != null)
        {
            currentlocationinfo = currentlocationinfo.parcelData();
        } else
        {
            currentlocationinfo = null;
        }
        if (currentlocationinfo != null)
        {
            currentlocationinfo = currentlocationinfo.getName();
        } else
        {
            currentlocationinfo = null;
        }
        if (currentlocationinfo == null)
        {
            currentlocationinfo = getString(0x7f0901c8);
        }
        setDefaultTitle(currentlocationinfo, null);
    }

    private void onMyAvatarState(MyAvatarState myavatarstate)
    {
        updateObjectPanel();
    }

    private void onPickedAvatarNameUpdated(ChatterNameRetriever chatternameretriever)
    {
        if (chatternameretriever == pickedAvatarNameRetriever)
        {
            updateObjectPanel();
        }
    }

    private void onSelectedObjectProfile(SLObjectProfileData slobjectprofiledata)
    {
        Debug.Printf("got selected object profile: %s", new Object[] {
            slobjectprofiledata
        });
        updateObjectPanel();
        if (slobjectprofiledata != null)
        {
            SLAgentCircuit slagentcircuit = (SLAgentCircuit)agentCircuit.getData();
            if (slobjectprofiledata.isPayable() && slobjectprofiledata.payInfo() == null && slagentcircuit != null)
            {
                slagentcircuit.DoRequestPayPrice(slobjectprofiledata.objectUUID());
            }
        }
    }

    private void onVoiceActiveChatter(ChatterID chatterid)
    {
        if (voiceStatusView != null)
        {
            voiceStatusView.setChatterID(chatterid);
        }
        if (chatterid != null && userManager != null)
        {
            voiceChatInfo.subscribe(userManager.getVoiceChatInfo(), chatterid);
            return;
        } else
        {
            voiceChatInfo.unsubscribe();
            return;
        }
    }

    private void onVoiceChatInfo(VoiceChatInfo voicechatinfo)
    {
    }

    private void selectByDragPointer(int i, int j)
    {
        int ai[] = new int[2];
        dragPointerLayout.getLocationOnScreen(ai);
        int k = ai[0];
        int l = dragPointer.getWidth() / 2;
        int i1 = ai[1];
        int j1 = dragPointer.getHeight() / 2;
        ai = new int[2];
        worldViewHolder.getLocationOnScreen(ai);
        mGLView.pickObjectHover((k + l + i) - ai[0], (i1 + j1 + j) - ai[1]);
    }

    private void selectHUDtoDisplay()
    {
        ArrayList arraylist;
        Object obj;
        arraylist = new ArrayList();
        obj = (SLAgentCircuit)agentCircuit.getData();
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_188;
        }
        obj = ((SLAgentCircuit) (obj)).getGridConnection().parcelInfo.getAgentAvatar();
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_152;
        }
        obj = ((SLObjectAvatarInfo) (obj)).treeNode.iterator();
_L2:
        SLObjectInfo slobjectinfo;
        int i;
        do
        {
            do
            {
                if (!((Iterator) (obj)).hasNext())
                {
                    break MISSING_BLOCK_LABEL_152;
                }
                slobjectinfo = (SLObjectInfo)((Iterator) (obj)).next();
            } while (Strings.nullToEmpty(slobjectinfo.getName()).startsWith("#"));
            i = slobjectinfo.attachmentID;
        } while (i < 0 || i >= 56);
        SLAttachmentPoint slattachmentpoint = SLAttachmentPoint.attachmentPoints[i];
        if (slattachmentpoint == null) goto _L2; else goto _L1
_L1:
        if (!slattachmentpoint.isHUD) goto _L2; else goto _L3
_L3:
        arraylist.add(new SelectableAttachment(slobjectinfo.localID, slobjectinfo.name));
          goto _L2
        NoSuchElementException nosuchelementexception;
        nosuchelementexception;
        Debug.Warning(nosuchelementexception);
        if (!arraylist.isEmpty())
        {
            if (arraylist.size() != 1)
            {
                break MISSING_BLOCK_LABEL_189;
            }
            displayHUD(((SelectableAttachment)arraylist.get(0)).getLocalID());
        }
        return;
        ArrayAdapter arrayadapter = new ArrayAdapter(this, 0x1090003, arraylist);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(0x7f0902f3);
        builder.setAdapter(arrayadapter, new _2D_.Lambda.YnTWxJEMPymM_sHfAdAKQ7gcDf8._cls10(this, arraylist));
        builder.create().show();
        return;
    }

    private void showObjectInfo(SLObjectInfo slobjectinfo)
    {
        if (slobjectinfo.getId() != null && userManager != null)
        {
            if (!slobjectinfo.isAvatar())
            {
                DetailsActivity.showEmbeddedDetails(this, com/lumiyaviewer/lumiya/ui/objects/ObjectDetailsFragment, ObjectDetailsFragment.makeSelection(userManager.getUserID(), slobjectinfo.localID));
            } else
            if (slobjectinfo instanceof SLObjectAvatarInfo)
            {
                DetailsActivity.showEmbeddedDetails(this, com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(userManager.getUserID(), slobjectinfo.getId())));
                return;
            }
        }
    }

    private void startCardboardActivity()
    {
        if (userManager != null)
        {
            Intent intent = new Intent(this, com/lumiyaviewer/lumiya/ui/render/CardboardTransitionActivity);
            ActivityUtils.setActiveAgentID(intent, userManager.getUserID());
            intent.addFlags(0x1000000);
            startActivity(intent);
        }
        finish();
    }

    private void startFadingButtons()
    {
        if (android.os.Build.VERSION.SDK_INT >= 11 && buttonsFadeAnimator != null)
        {
            buttonsFadeAnimator.start();
        }
    }

    private void startFadingButtonsTimer()
    {
        if (!buttonsFadeTimerStarted)
        {
            Debug.Printf("ButtonsFade: starting timer", new Object[0]);
            buttonsFadeTimerStarted = true;
            mHandler.postDelayed(buttonsFadeTask, 7500L);
        }
    }

    private void stopAvatarAnimations()
    {
        if (avatarControl != null)
        {
            avatarControl.StopAvatarAnimations();
        }
    }

    private void takeScreenshot()
    {
        Toast.makeText(this, 0x7f090338, 0).show();
        mGLView.takeScreenshot();
    }

    private void touchObject(SLObjectInfo slobjectinfo, ObjectIntersectInfo objectintersectinfo)
    {
        SLAgentCircuit slagentcircuit = (SLAgentCircuit)agentCircuit.getData();
        if (slagentcircuit == null || slobjectinfo == null) goto _L2; else goto _L1
_L1:
        if (slobjectinfo.isAvatar()) goto _L4; else goto _L3
_L3:
        boolean flag;
        lastTouchUUID = slobjectinfo.getId();
        flag = false;
        if (objectintersectinfo != null)
        {
            flag = objectintersectinfo.intersectInfo.faceKnown;
        }
        if (!flag) goto _L6; else goto _L5
_L5:
        LLVector3 llvector3 = slobjectinfo.getAbsolutePosition();
        slagentcircuit.TouchObjectFace(slobjectinfo, objectintersectinfo.intersectInfo.faceID, llvector3.x, llvector3.y, llvector3.z, objectintersectinfo.intersectInfo.u, objectintersectinfo.intersectInfo.v, objectintersectinfo.intersectInfo.s, objectintersectinfo.intersectInfo.t);
_L2:
        return;
_L6:
        slagentcircuit.TouchObject(slobjectinfo.localID);
        return;
_L4:
        if (slobjectinfo.hasTouchableChildren())
        {
            DetailsActivity.showEmbeddedDetails(this, com/lumiyaviewer/lumiya/ui/objects/TouchableObjectsFragment, TouchableObjectsFragment.makeSelection(userManager.getUserID(), slobjectinfo.getId()));
            return;
        }
        if (true) goto _L2; else goto _L7
_L7:
    }

    private void updateObjectPanel()
    {
        Object obj;
        int j;
        int l;
        boolean flag1;
        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag8;
        obj = (MyAvatarState)myAvatarState.getData();
        Object obj1 = (SLAgentCircuit)agentCircuit.getData();
        boolean flag;
        boolean flag6;
        if (obj1 != null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (obj != null)
        {
            flag1 = ((MyAvatarState) (obj)).isSitting();
        } else
        {
            flag1 = false;
        }
        if (obj != null)
        {
            flag4 = ((MyAvatarState) (obj)).hasHUDs();
        } else
        {
            flag4 = false;
        }
        if (obj != null)
        {
            flag6 = ((MyAvatarState) (obj)).isFlying();
        } else
        {
            flag6 = false;
        }
        if (obj1 != null)
        {
            flag2 = ((SLAgentCircuit) (obj1)).getModules().rlvController.canStandUp();
        } else
        {
            flag2 = false;
        }
        if (obj1 != null)
        {
            flag3 = ((SLAgentCircuit) (obj1)).getModules().rlvController.canSit();
        } else
        {
            flag3 = false;
        }
        if (pickedObject != null)
        {
            j = 1;
        } else
        {
            j = 0;
        }
        Debug.Printf("isSitting %b, isFlying %b, hasHUDs %b, isDragging %b", new Object[] {
            Boolean.valueOf(flag1), Boolean.valueOf(flag6), Boolean.valueOf(flag4), Boolean.valueOf(isDragging)
        });
        obj1 = dragPointerLayout;
        if (isDragging)
        {
            l = 0;
        } else
        {
            l = 4;
        }
        ((ViewGroup) (obj1)).setVisibility(l);
        obj1 = dragPointer;
        if (isDragging)
        {
            l = 0;
        } else
        {
            l = 4;
        }
        ((View) (obj1)).setVisibility(l);
        obj1 = flyButtonsLayout;
        if (!flag1 && !(flag ^ true)) goto _L2; else goto _L1
_L1:
        if (camButtonEnabled)
        {
            flag8 = manualCamMode;
        } else
        {
            flag8 = false;
        }
        if (flag8 ^ true) goto _L3; else goto _L2
_L2:
        if (!isDragging && j == 0) goto _L4; else goto _L3
_L3:
        l = 8;
_L10:
        ((LinearLayout) (obj1)).setVisibility(l);
        obj1 = moveButtonsLayout;
        if (!flag1 && !(flag ^ true)) goto _L6; else goto _L5
_L5:
        if (camButtonEnabled)
        {
            flag8 = manualCamMode;
        } else
        {
            flag8 = false;
        }
        if (flag8 ^ true) goto _L7; else goto _L6
_L6:
        if (!isDragging && j == 0) goto _L8; else goto _L7
_L7:
        l = 4;
        break MISSING_BLOCK_LABEL_295;
_L4:
        l = 0;
        continue; /* Loop/switch isn't completed */
_L8:
        l = 0;
        ((View) (obj1)).setVisibility(l);
        obj1 = buttonStandUp;
        if (flag2 && flag1 && isDragging ^ true)
        {
            l = 0;
        } else
        {
            l = 8;
        }
        ((ImageButton) (obj1)).setVisibility(l);
        obj1 = buttonHUD;
        if (flag4 && isDragging ^ true && flag)
        {
            l = 0;
        } else
        {
            l = 8;
        }
        ((Button) (obj1)).setVisibility(l);
        obj1 = buttonFlyDownward;
        if (flag6 && flag || camButtonEnabled && manualCamMode)
        {
            l = 0;
        } else
        {
            l = 8;
        }
        ((ImageButton) (obj1)).setVisibility(l);
        obj1 = buttonStopFlying;
        if (flag6 && flag)
        {
label0:
            {
                if (camButtonEnabled)
                {
                    flag4 = manualCamMode;
                } else
                {
                    flag4 = false;
                }
                if (flag4 ^ true)
                {
                    l = 0;
                    break label0;
                }
            }
        }
        l = 8;
        ((ImageButton) (obj1)).setVisibility(l);
        obj1 = buttonCamOn;
        if (camButtonEnabled && manualCamMode ^ true && isDragging ^ true && j ^ true)
        {
            l = 0;
        } else
        {
            l = 8;
        }
        ((ImageButton) (obj1)).setVisibility(l);
        obj1 = buttonCamOff;
        if (camButtonEnabled && manualCamMode && isDragging ^ true && j ^ true)
        {
            j = 0;
        } else
        {
            j = 8;
        }
        ((ImageButton) (obj1)).setVisibility(j);
        if (pickedObject == null || flag ^ true)
        {
            objectControlsPanel.setVisibility(8);
            return;
        }
label1:
        {
            {
                objectControlsPanel.setVisibility(0);
                boolean flag7 = pickedObject.isTouchable();
                boolean flag5 = flag7;
                if (pickedObject.isAvatar())
                {
                    flag5 = flag7 | pickedObject.hasTouchableChildren();
                }
                Object obj2 = objectTouchButton;
                int i;
                int k;
                if (flag5)
                {
                    i = 0;
                } else
                {
                    i = 8;
                }
                ((ImageButton) (obj2)).setVisibility(i);
                flag5 = pickedObject.isAvatar();
                if (flag1 && pickedObject.localID == ((MyAvatarState) (obj)).sittingOn())
                {
                    i = 1;
                } else
                {
                    i = 0;
                }
                if (!flag5)
                {
                    k = i ^ true;
                } else
                {
                    k = 0;
                }
                if (flag5)
                {
                    i = 0;
                }
                obj = objectSitButton;
                if (k != 0 && flag3)
                {
                    k = 0;
                } else
                {
                    k = 8;
                }
                ((ImageButton) (obj)).setVisibility(k);
                obj = objectStandButton;
                if (i != 0 && flag2)
                {
                    i = 0;
                } else
                {
                    i = 8;
                }
                ((ImageButton) (obj)).setVisibility(i);
                obj = objectChatButton;
                if (flag5)
                {
                    i = 0;
                } else
                {
                    i = 8;
                }
                ((ImageButton) (obj)).setVisibility(i);
                obj = avatarIconView;
                if (flag5)
                {
                    i = 0;
                } else
                {
                    i = 8;
                }
                ((ImageView) (obj)).setVisibility(i);
                obj = objectPayButton;
                if (pickedObject.isPayable() || pickedObject.saleType != 0)
                {
                    i = 0;
                } else
                {
                    i = 8;
                }
                ((ImageButton) (obj)).setVisibility(i);
                if (!pickedObject.isAvatar())
                {
                    break label1;
                }
                if (pickedAvatarNameRetriever != null)
                {
                    obj = pickedAvatarNameRetriever.getResolvedName();
                } else
                {
                    obj = null;
                }
            }
            obj2 = obj;
            if (obj == null)
            {
                obj2 = getString(0x7f090239);
            }
            objectNameTextView.setText(((CharSequence) (obj2)));
            return;
        }
        obj = (SLObjectProfileData)selectedObjectProfile.getData();
        if (obj != null && Objects.equal(((SLObjectProfileData) (obj)).objectUUID(), pickedObject.getId()))
        {
            obj2 = (String)((SLObjectProfileData) (obj)).name().orNull();
        } else
        {
            obj2 = null;
        }
        obj = obj2;
        if (obj2 == null)
        {
            obj = pickedObject.name;
        }
        if (false)
        {
        } else
        {
            break MISSING_BLOCK_LABEL_925;
        }
        if (true) goto _L10; else goto _L9
_L9:
    }

    private void updateSimTimeOverride()
    {
        if (mGLView != null)
        {
            GlobalOptions globaloptions = GlobalOptions.getInstance();
            boolean flag = globaloptions.getForceDaylightTime();
            float f = globaloptions.getForceDaylightHour();
            mGLView.setForcedTime(flag, f);
        }
    }

    private void updateSplitScreenLayout()
    {
        boolean flag = false;
        Fragment fragment = getSupportFragmentManager().findFragmentById(0x7f100114);
        boolean flag3 = isSplitScreen;
        int i;
        boolean flag1;
        boolean flag2;
        if (fragment != null)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        if (fragment != null)
        {
            flag2 = fragment.isDetached();
        } else
        {
            flag2 = false;
        }
        Debug.Printf("updateSplitScreenLayout: isSplitScreen now %b details has %b detached %b", new Object[] {
            Boolean.valueOf(flag3), Boolean.valueOf(flag1), Boolean.valueOf(flag2)
        });
        if (fragment != null && fragment.isDetached() ^ true)
        {
            detailsContainer.setVisibility(0);
            Object obj = worldOverlaysContainer;
            if (isSplitScreen)
            {
                i = 0;
            } else
            {
                i = 8;
            }
            ((ViewGroup) (obj)).setVisibility(i);
        } else
        {
            worldOverlaysContainer.setVisibility(0);
        }
        obj = objectPopupLeftSpacer;
        if (isSplitScreen)
        {
            i = ((flag) ? 1 : 0);
        } else
        {
            i = 8;
        }
        ((View) (obj)).setVisibility(i);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_2D_mthref_2D_0(SLAgentCircuit slagentcircuit)
    {
        onAgentCircuit(slagentcircuit);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_2D_mthref_2D_1(MyAvatarState myavatarstate)
    {
        onMyAvatarState(myavatarstate);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_2D_mthref_2D_2(SLObjectProfileData slobjectprofiledata)
    {
        onSelectedObjectProfile(slobjectprofiledata);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_2D_mthref_2D_3(CurrentLocationInfo currentlocationinfo)
    {
        onCurrentLocation(currentlocationinfo);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_2D_mthref_2D_4(ChatterID chatterid)
    {
        onVoiceActiveChatter(chatterid);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_2D_mthref_2D_5(VoiceChatInfo voicechatinfo)
    {
        onVoiceChatInfo(voicechatinfo);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_2D_mthref_2D_6(ChatterNameRetriever chatternameretriever)
    {
        onPickedAvatarNameUpdated(chatternameretriever);
    }

    public boolean dispatchKeyEvent(KeyEvent keyevent)
    {
        if (detailsContainer.getVisibility() == 0)
        {
            return super.dispatchKeyEvent(keyevent);
        }
        keyevent.getKeyCode();
        JVM INSTR lookupswitch 6: default 80
    //                   19: 171
    //                   20: 219
    //                   21: 86
    //                   22: 128
    //                   92: 267
    //                   93: 316;
           goto _L1 _L2 _L3 _L4 _L5 _L6 _L7
_L1:
        return super.dispatchKeyEvent(keyevent);
_L4:
        if (avatarControl == null) goto _L9; else goto _L8
_L8:
        if (keyevent.getAction() != 0) goto _L11; else goto _L10
_L10:
        avatarControl.startTurning(50F);
_L9:
        return true;
_L11:
        if (keyevent.getAction() != 1) goto _L9; else goto _L12
_L12:
        avatarControl.stopTurning();
        return true;
_L5:
        if (avatarControl == null) goto _L14; else goto _L13
_L13:
        if (keyevent.getAction() != 0) goto _L16; else goto _L15
_L15:
        avatarControl.startTurning(-50F);
_L14:
        return true;
_L16:
        if (keyevent.getAction() != 1) goto _L14; else goto _L17
_L17:
        avatarControl.stopTurning();
        return true;
_L2:
        if (avatarControl == null) goto _L19; else goto _L18
_L18:
        if (keyevent.getAction() != 0) goto _L21; else goto _L20
_L20:
        avatarControl.stopCamming();
        avatarControl.StartAgentMotion(2);
_L19:
        return true;
_L21:
        if (keyevent.getAction() != 1) goto _L19; else goto _L22
_L22:
        avatarControl.StopAgentMotion();
        return true;
_L3:
        if (avatarControl == null) goto _L24; else goto _L23
_L23:
        if (keyevent.getAction() != 0) goto _L26; else goto _L25
_L25:
        avatarControl.stopCamming();
        avatarControl.StartAgentMotion(4);
_L24:
        return true;
_L26:
        if (keyevent.getAction() != 1) goto _L24; else goto _L27
_L27:
        avatarControl.StopAgentMotion();
        return true;
_L6:
        if (avatarControl == null) goto _L29; else goto _L28
_L28:
        if (keyevent.getAction() != 0) goto _L31; else goto _L30
_L30:
        avatarControl.stopCamming();
        avatarControl.StartAgentMotion(8);
_L29:
        return true;
_L31:
        if (keyevent.getAction() != 1) goto _L29; else goto _L32
_L32:
        avatarControl.StopAgentMotion();
        return true;
_L7:
        if (avatarControl != null)
        {
            if (keyevent.getAction() == 0)
            {
                avatarControl.stopCamming();
                avatarControl.StartAgentMotion(16);
            } else
            if (keyevent.getAction() == 1)
            {
                avatarControl.StopAgentMotion();
                return true;
            }
        }
        return true;
    }

    public void handleBakingProgressEvent(SLBakingProgressEvent slbakingprogressevent)
    {
        if (slbakingprogressevent.first)
        {
            Toast.makeText(this, "Updating avatar appearance...", 0).show();
        }
    }

    public void handleChatEvent(com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager.ChatMessageEvent chatmessageevent)
    {
        while (!chatOver3D || detailsVisible() || userManager == null || chatsOverlayLayout == null || fadingTextViewLog == null) 
        {
            return;
        }
        fadingTextViewLog.handleChatEvent(chatmessageevent);
    }

    public void handlePickedObject(ObjectIntersectInfo objectintersectinfo)
    {
        pickedIntersectInfo = objectintersectinfo;
        com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser;
        if (objectintersectinfo != null)
        {
            objectintersectinfo = objectintersectinfo.objInfo;
        } else
        {
            objectintersectinfo = null;
        }
        pickedObject = objectintersectinfo;
        if (pickedObject == null) goto _L2; else goto _L1
_L1:
        if (pickedObject.isAvatar())
        {
            chatteriduser = ChatterID.getUserChatterID(userManager.getUserID(), pickedObject.getId());
            if (pickedAvatarNameRetriever != null)
            {
                objectintersectinfo = pickedAvatarNameRetriever.chatterID;
            } else
            {
                objectintersectinfo = null;
            }
            if (!Objects.equal(objectintersectinfo, chatteriduser))
            {
                if (pickedAvatarNameRetriever != null)
                {
                    pickedAvatarNameRetriever.dispose();
                    pickedAvatarNameRetriever = null;
                }
                pickedAvatarNameRetriever = new ChatterNameRetriever(chatteriduser, new _2D_.Lambda.YnTWxJEMPymM_sHfAdAKQ7gcDf8._cls9(this), UIThreadExecutor.getInstance());
            }
        } else
        {
            Debug.Printf("ObjectPick: picked object %d", new Object[] {
                Integer.valueOf(pickedObject.localID)
            });
            selectedObjectProfile.subscribe(userManager.getObjectsManager().getObjectProfile(), Integer.valueOf(pickedObject.localID));
        }
        updateObjectPanel();
        beginCountingObjectDeselect();
_L4:
        mGLView.setDrawPickedObject(pickedObject);
        updateObjectPanel();
        return;
_L2:
        selectedObjectProfile.unsubscribe();
        if (pickedAvatarNameRetriever != null)
        {
            pickedAvatarNameRetriever.dispose();
            pickedAvatarNameRetriever = null;
        }
        if (true) goto _L4; else goto _L3
_L3:
    }

    protected boolean isRootDetailsFragment(Class class1)
    {
        while (class1 == com/lumiyaviewer/lumiya/ui/chat/ContactsFragment || class1 == com/lumiyaviewer/lumiya/ui/chat/ChatFragment || class1 == com/lumiyaviewer/lumiya/ui/objects/ObjectDetailsFragment || class1 == com/lumiyaviewer/lumiya/ui/outfits/OutfitsFragment || class1 == com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment) 
        {
            return true;
        }
        return false;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_10269(ValueAnimator valueanimator)
    {
        insetsBackground.setAlpha(1.0F - valueanimator.getAnimatedFraction());
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_43183(DialogInterface dialoginterface, int i)
    {
        if (userManager != null)
        {
            SLAgentCircuit slagentcircuit = userManager.getActiveAgentCircuit();
            if (slagentcircuit != null && pickedObject != null)
            {
                slagentcircuit.BuyObject(pickedObject.localID, pickedObject.saleType, pickedObject.salePrice);
            }
        }
        dialoginterface.dismiss();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_WorldViewActivity_48476(List list, DialogInterface dialoginterface, int i)
    {
        if (i >= 0 && i < list.size())
        {
            displayHUD(((SelectableAttachment)list.get(i)).getLocalID());
        }
    }

    public int mapThemeResourceId(int i)
    {
        if (i == 0x7f0b002c)
        {
            return 0x7f0b002d;
        }
        return i != 0x7f0b002f ? 0x7f0b002e : 0x7f0b0030;
    }

    public void moveTouchEvent(int i, MotionEvent motionevent)
    {
        float f3;
        float f4;
        f4 = 1.0F;
        f3 = -1F;
        if (avatarControl == null) goto _L2; else goto _L1
_L1:
        if (motionevent.getAction() != 0) goto _L4; else goto _L3
_L3:
        if (!manualCamMode || !camButtonEnabled) goto _L6; else goto _L5
_L5:
        float f;
        float f1;
        float f2;
        if ((i & 8) != 0)
        {
            f = 1.0F;
        } else
        {
            f = 0.0F;
        }
        if ((i & 0x10) != 0)
        {
            f1 = -1F;
        } else
        {
            f1 = f;
        }
        if ((i & 2) != 0)
        {
            f = 1.0F;
        } else
        {
            f = 0.0F;
        }
        f2 = f;
        if ((i & 4) != 0)
        {
            f2 = -1F;
        }
        if ((i & 0x20) != 0)
        {
            f = f4;
        } else
        {
            f = 0.0F;
        }
        if ((i & 0x40) != 0)
        {
            f = f3;
        }
        avatarControl.startCameraManualControl(0.0F, f2, f1, f);
_L2:
        return;
_L6:
        avatarControl.stopCamming();
        avatarControl.StartAgentMotion(i);
        if ((i & 8) != 0 || (i & 0x10) != 0)
        {
            updateObjectPanel();
            return;
        }
        continue; /* Loop/switch isn't completed */
_L4:
        if (motionevent.getAction() == 1)
        {
            if (manualCamMode && camButtonEnabled)
            {
                avatarControl.stopCameraManualControl();
                return;
            } else
            {
                avatarControl.StopAgentMotion();
                return;
            }
        }
        if (true) goto _L2; else goto _L7
_L7:
    }

    public void onCamOffButton()
    {
        if (camButtonEnabled)
        {
            manualCamMode = false;
            if (avatarControl != null)
            {
                avatarControl.setCameraManualControl(false);
            }
            updateObjectPanel();
        }
    }

    public void onCamOnButton()
    {
        if (camButtonEnabled)
        {
            manualCamMode = true;
            if (avatarControl != null)
            {
                avatarControl.setCameraManualControl(true);
            }
            updateObjectPanel();
        }
    }

    public void onConfigurationChanged(Configuration configuration)
    {
        super.onConfigurationChanged(configuration);
        isSplitScreen = LumiyaApp.isSplitScreenNeeded(this);
        updateSplitScreenLayout();
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        userManager = ActivityUtils.getUserManager(getIntent());
        if (userManager == null)
        {
            finish();
            return;
        }
        isSplitScreen = LumiyaApp.isSplitScreenNeeded(this);
        scaleGestureDetector = new ScaleGestureDetector(this, scaleGestureListener);
        if (android.os.Build.VERSION.SDK_INT >= 19)
        {
            scaleGestureDetector.setQuickScaleEnabled(true);
        }
        gestureDetector = new GestureDetectorCompat(this, gestureListener);
        initContentView();
        fadingTextViewLog = new FadingTextViewLog(userManager, this, chatsOverlayLayout, Color.rgb(192, 192, 192), Color.argb(160, 0, 0, 0));
        if (android.os.Build.VERSION.SDK_INT >= 12)
        {
            buttonsFadeAnimator = ValueAnimator.ofFloat(new float[] {
                0.0F, 1.0F
            });
            buttonsFadeAnimator.setDuration(1000L);
            buttonsFadeAnimator.addUpdateListener(new _2D_.Lambda.YnTWxJEMPymM_sHfAdAKQ7gcDf8._cls1(this));
        }
        updateSplitScreenLayout();
        startFadingButtonsTimer();
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(0x7f120027, menu);
        return true;
    }

    protected boolean onDetailsStackEmpty()
    {
        if (!super.onDetailsStackEmpty())
        {
            detailsContainer.setVisibility(8);
            if (!isSplitScreen)
            {
                worldOverlaysContainer.setVisibility(0);
            }
            beginCountingButtonsFade();
            beginCountingObjectDeselect();
            return false;
        } else
        {
            return true;
        }
    }

    public Intent onGetNotifyCaptureIntent(UnreadNotificationInfo unreadnotificationinfo, Intent intent)
    {
        Debug.Printf("NotifyCapture: returning new intent", new Object[0]);
        unreadnotificationinfo = new Intent(this, com/lumiyaviewer/lumiya/ui/render/WorldViewActivity);
        unreadnotificationinfo.putExtras(intent);
        unreadnotificationinfo.putExtra("fromNotification", true);
        unreadnotificationinfo.addFlags(0x20000000);
        return unreadnotificationinfo;
    }

    public void onGlobalOptionsChanged(com.lumiyaviewer.lumiya.GlobalOptions.GlobalOptionsChangedEvent globaloptionschangedevent)
    {
        updateSimTimeOverride();
    }

    public void onHUDButton()
    {
        if (displayedHUDid != 0)
        {
            displayedHUDid = 0;
            mGLView.setDisplayedHUDid(displayedHUDid);
            updateObjectPanel();
            return;
        } else
        {
            selectHUDtoDisplay();
            return;
        }
    }

    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Debug.Printf("NotifyCapture: got newIntent: %s", new Object[] {
            intent
        });
        mHandler.post(buttonsRestoreTask);
        beginCountingButtonsFade();
        beginCountingObjectDeselect();
        if (intent.hasExtra("fromNotification"))
        {
            if (intent.hasExtra("selection"))
            {
                intent = intent.getBundleExtra("selection");
            } else
            {
                intent = null;
            }
            if (intent != null)
            {
                DetailsActivity.showEmbeddedDetails(this, com/lumiyaviewer/lumiya/ui/chat/ChatFragment, intent);
            } else
            if (userManager != null)
            {
                DetailsActivity.showEmbeddedDetails(this, com/lumiyaviewer/lumiya/ui/chat/ContactsFragment, ActivityUtils.makeFragmentArguments(userManager.getUserID(), null));
                return;
            }
        }
    }

    public void onObjectChatButton()
    {
        if (pickedObject != null)
        {
            chatWithObject(pickedObject);
        }
    }

    public void onObjectMoreButton()
    {
        if (pickedObject != null)
        {
            showObjectInfo(pickedObject);
        }
    }

    public void onObjectPayButton()
    {
        if (pickedObject != null)
        {
            String s1 = pickedObject.getName();
            String s = s1;
            if (Strings.isNullOrEmpty(s1))
            {
                s = getString(0x7f090239);
            }
            if (pickedObject.saleType != 0)
            {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setMessage(String.format(getString(0x7f090213), new Object[] {
                    s, Integer.valueOf(pickedObject.salePrice)
                })).setCancelable(false).setPositiveButton("Yes", new _2D_.Lambda.YnTWxJEMPymM_sHfAdAKQ7gcDf8._cls2(this)).setNegativeButton("No", new _2D_.Lambda.YnTWxJEMPymM_sHfAdAKQ7gcDf8());
                builder.show();
            } else
            if (pickedObject.isPayable())
            {
                SLObjectProfileData slobjectprofiledata = (SLObjectProfileData)selectedObjectProfile.getData();
                if (slobjectprofiledata != null && userManager != null)
                {
                    ObjectPayDialog.show(this, userManager, slobjectprofiledata);
                    return;
                }
            }
        }
    }

    public void onObjectSitButton()
    {
        if (pickedObject != null && avatarControl != null)
        {
            avatarControl.SitOnObject(pickedObject.getId());
        }
    }

    public void onObjectStandButton()
    {
        if (avatarControl != null)
        {
            avatarControl.Stand();
        }
    }

    public void onObjectTouchButton()
    {
        if (pickedObject != null)
        {
            touchObject(pickedObject, pickedIntersectInfo);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        switch (menuitem.getItemId())
        {
        default:
            return super.onOptionsItemSelected(menuitem);

        case 2131755881: 
            DetailsActivity.showEmbeddedDetails(this, com/lumiyaviewer/lumiya/ui/chat/ContactsFragment, ActivityUtils.makeFragmentArguments(userManager.getUserID(), null));
            return true;

        case 2131755882: 
            DetailsActivity.showEmbeddedDetails(this, com/lumiyaviewer/lumiya/ui/outfits/OutfitsFragment, OutfitsFragment.makeSelection(userManager.getUserID(), null));
            return true;

        case 2131755886: 
            stopAvatarAnimations();
            return true;

        case 2131755883: 
            if (!isDragging)
            {
                beginDragSelection();
                return true;
            } else
            {
                endDragSelection();
                return true;
            }

        case 2131755885: 
            takeScreenshot();
            return true;

        case 2131755884: 
            enterCardboardView();
            return true;
        }
    }

    public void onPause()
    {
        Debug.Printf("WorldViewActivity: onPause", new Object[0]);
        if (userManager != null)
        {
            userManager.getUnreadNotificationManager().clearNotifyCapture(this);
            try
            {
                userManager.getChatterList().getActiveChattersManager().getChatEventBus().unregister(chatEventHandler);
            }
            catch (IllegalArgumentException illegalargumentexception) { }
            if (fadingTextViewLog != null)
            {
                fadingTextViewLog.clearChatEvents();
            }
        }
        localDrawingEnabled = false;
        if (drawDistance != null)
        {
            drawDistance.Disable3DView();
        }
        mGLView.onPause();
        mHandler.removeCallbacks(objectDeselectTimerTask);
        mHandler.removeCallbacks(buttonsFadeTask);
        objectDeselectTimerStarted = false;
        buttonsFadeTimerStarted = false;
        myAvatarState.unsubscribe();
        selectedObjectProfile.unsubscribe();
        currentLocationInfo.unsubscribe();
        System.gc();
        super.onPause();
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem menuitem = menu.findItem(0x7f10036c);
        if (menuitem != null)
        {
            boolean flag;
            if (android.os.Build.VERSION.SDK_INT >= 23)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            menuitem.setVisible(flag);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void onRequestPermissionsResult(int i, String as[], int ai[])
    {
        Debug.Printf("Cardboard: onRequestPermissionResult, code %d", new Object[] {
            Integer.valueOf(i)
        });
        if (i == 100)
        {
            startCardboardActivity();
        }
    }

    public void onResume()
    {
        super.onResume();
        Debug.Printf("WorldViewActivity: onResume", new Object[0]);
        Object obj = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        chatOver3D = ((SharedPreferences) (obj)).getBoolean("chat_over_3d", true);
        arrowsToTurn = ((SharedPreferences) (obj)).getBoolean("arrows_rotate_avatar", false);
        camButtonEnabled = ((SharedPreferences) (obj)).getBoolean("cam_button_enabled", false);
        if (userManager != null)
        {
            userManager.getUnreadNotificationManager().setNotifyCapture(this);
            myAvatarState.subscribe(userManager.getObjectsManager().myAvatarState(), SubscriptionSingleKey.Value);
            currentLocationInfo.subscribe(userManager.getCurrentLocationInfo(), SubscriptionSingleKey.Value);
            userManager.getChatterList().getActiveChattersManager().getChatEventBus().register(chatEventHandler);
        }
        obj = new RenderSettings(((SharedPreferences) (obj)));
        prefDrawDistance = ((RenderSettings) (obj)).drawDistance;
        localDrawingEnabled = true;
        if (drawDistance != null)
        {
            drawDistance.Enable3DView(prefDrawDistance);
        }
        if (camButtonEnabled && avatarControl != null)
        {
            manualCamMode = avatarControl.getIsManualCamming();
        }
        mGLView.setDrawDistance(prefDrawDistance);
        mGLView.setAvatarCountLimit(((RenderSettings) (obj)).avatarCountLimit);
        mGLView.onResume();
        updateObjectPanel();
        if (fadingTextViewLog != null)
        {
            fadingTextViewLog.postRemovingStaleChats();
        }
    }

    protected void onStart()
    {
        super.onStart();
        if (userManager != null)
        {
            agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID());
            voiceActiveChatter.subscribe(userManager.getVoiceActiveChatter(), SubscriptionSingleKey.Value);
        }
        updateSimTimeOverride();
    }

    protected void onStop()
    {
        agentCircuit.unsubscribe();
        voiceActiveChatter.unsubscribe();
        voiceChatInfo.unsubscribe();
        voiceStatusView.setChatterID(null);
        super.onStop();
    }

    public void onStopFlyingButton()
    {
        if (avatarControl != null)
        {
            avatarControl.stopFlying();
            updateObjectPanel();
        }
    }

    public boolean onTouch(View view, MotionEvent motionevent)
    {
        view.getId();
        JVM INSTR lookupswitch 6: default 64
    //                   2131755260: 66
    //                   2131755261: 100
    //                   2131755262: 74
    //                   2131755263: 210
    //                   2131755756: 82
    //                   2131755757: 91;
           goto _L1 _L2 _L3 _L4 _L5 _L6 _L7
_L1:
        return false;
_L2:
        moveTouchEvent(2, motionevent);
        return false;
_L4:
        moveTouchEvent(4, motionevent);
        return false;
_L6:
        moveTouchEvent(8, motionevent);
        return false;
_L7:
        moveTouchEvent(16, motionevent);
        return false;
_L3:
        if (arrowsToTurn)
        {
            if (avatarControl != null)
            {
                if (manualCamMode && camButtonEnabled)
                {
                    if (motionevent.getActionMasked() == 0)
                    {
                        avatarControl.startCameraManualControl(50F, 0.0F, 0.0F, 0.0F);
                        return false;
                    }
                    if (motionevent.getActionMasked() == 1)
                    {
                        avatarControl.stopCameraManualControl();
                        return false;
                    }
                } else
                {
                    if (motionevent.getActionMasked() == 0)
                    {
                        avatarControl.startTurning(50F);
                        return false;
                    }
                    if (motionevent.getActionMasked() == 1)
                    {
                        avatarControl.stopTurning();
                        return false;
                    }
                }
            }
        } else
        {
            moveTouchEvent(32, motionevent);
            return false;
        }
        continue; /* Loop/switch isn't completed */
_L5:
        if (arrowsToTurn)
        {
            if (avatarControl != null)
            {
                if (manualCamMode && camButtonEnabled)
                {
                    if (motionevent.getActionMasked() == 0)
                    {
                        avatarControl.startCameraManualControl(-50F, 0.0F, 0.0F, 0.0F);
                        return false;
                    }
                    if (motionevent.getActionMasked() == 1)
                    {
                        avatarControl.stopCameraManualControl();
                        return false;
                    }
                } else
                {
                    if (motionevent.getActionMasked() == 0)
                    {
                        avatarControl.startTurning(-50F);
                        return false;
                    }
                    if (motionevent.getActionMasked() == 1)
                    {
                        avatarControl.stopTurning();
                        return false;
                    }
                }
            }
        } else
        {
            moveTouchEvent(64, motionevent);
            return false;
        }
        if (true) goto _L1; else goto _L8
_L8:
    }

    public void onUserInteraction()
    {
        super.onUserInteraction();
        Debug.Printf("ButtonsFade: some user interaction", new Object[0]);
        mHandler.post(buttonsRestoreTask);
        beginCountingButtonsFade();
        beginCountingObjectDeselect();
    }

    public void processScreenshot(Bitmap bitmap)
    {
        Object obj;
        try
        {
            obj = new File(getCacheDir(), "screenshots");
            ((File) (obj)).mkdirs();
            Object obj1 = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.US);
            obj = new File(((File) (obj)), (new StringBuilder()).append("Lumiya-").append(((SimpleDateFormat) (obj1)).format(new Date())).append(".jpg").toString());
            obj1 = new FileOutputStream(((File) (obj)));
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, ((java.io.OutputStream) (obj1)));
            ((FileOutputStream) (obj1)).close();
            obj = FileProvider.getUriForFile(this, "com.lumiyaviewer.files", ((File) (obj)));
            bitmap = (SLAgentCircuit)agentCircuit.getData();
        }
        // Misplaced declaration of an exception variable
        catch (Bitmap bitmap)
        {
            Toast.makeText(this, 0x7f09011e, 0).show();
            return;
        }
        if (bitmap == null) goto _L2; else goto _L1
_L1:
        bitmap = bitmap.getAgentSLURL();
_L5:
        if (bitmap == null)
        {
            break MISSING_BLOCK_LABEL_227;
        }
        bitmap = getString(0x7f0902e4, new Object[] {
            bitmap
        });
_L3:
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.TEXT", bitmap);
        intent.putExtra("android.intent.extra.STREAM", ((android.os.Parcelable) (obj)));
        intent.putExtra("android.intent.extra.SUBJECT", "Screenshot from Lumiya");
        intent.setType("image/jpeg");
        intent.setFlags(1);
        startActivity(Intent.createChooser(intent, getString(0x7f090118)));
        return;
        bitmap = getString(0x7f0902e3);
        if (true) goto _L3; else goto _L2
_L2:
        bitmap = null;
        if (true) goto _L5; else goto _L4
_L4:
    }

    public void rendererAdvancedRenderingChanged()
    {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    void rendererShaderCompileError()
    {
        Toast.makeText(this, "Advanced rendering is not available on your hardware. Falling back to basic rendering.", 1).show();
        android.content.SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        editor.putBoolean("advanced_rendering", false);
        editor.commit();
        finish();
        startActivity(new Intent(this, getClass()));
    }

    void rendererSurfaceCreated()
    {
        if (drawDistance != null)
        {
            drawDistance.Enable3DView(prefDrawDistance);
        }
    }

    public void setLastTouchUUID(UUID uuid)
    {
        lastTouchUUID = uuid;
    }

    public void setTouchedObject(SLObjectInfo slobjectinfo)
    {
        if (slobjectinfo != null)
        {
            lastTouchUUID = slobjectinfo.getId();
            if (lastTouchUUID != null)
            {
                Debug.Log((new StringBuilder()).append("Touch: Last touched object set to ").append(lastTouchUUID).toString());
            }
        }
    }

    public Fragment showDetailsFragment(Class class1, Intent intent, Bundle bundle)
    {
        detailsContainer.setVisibility(0);
        if (!isSplitScreen)
        {
            worldOverlaysContainer.setVisibility(8);
            voiceStatusView.disableMic();
        }
        if (fadingTextViewLog != null)
        {
            fadingTextViewLog.clearChatEvents();
        }
        return super.showDetailsFragment(class1, intent, bundle);
    }
}
