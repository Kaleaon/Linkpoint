// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.vr.cardboard.FullscreenMode;
import com.google.vr.sdk.base.AndroidCompat;
import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.GvrViewerParams;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;
import com.google.vr.sdk.controller.Controller;
import com.google.vr.sdk.controller.ControllerManager;
import com.google.vrtoolkit.cardboard.ScreenOnFlagHelper;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.render.HeadTransformCompat;
import com.lumiyaviewer.lumiya.render.WorldViewRenderer;
import com.lumiyaviewer.lumiya.render.glres.textures.GLExternalTexture;
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatPermissionRequestEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatScriptDialog;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.modules.SLDrawDistance;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.voice.SLVoice;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.types.CameraParams;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.ContactsFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.microedition.khronos.egl.EGLConfig;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            MoveControl, CardboardControlsPlaceholder, RenderSettings, OnHoverListenerCompat, 
//            FadingTextViewLog

public class CardboardActivity extends DetailsActivity
    implements com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager.ObjectPopupListener
{
    private static final class ControlsPage extends Enum
    {

        private static final ControlsPage $VALUES[];
        public static final ControlsPage pageDefault;
        public static final ControlsPage pageDetails;
        public static final ControlsPage pageObject;
        public static final ControlsPage pageScriptDialog;
        public static final ControlsPage pageSpeech;
        public static final ControlsPage pageTouchAim;
        public static final ControlsPage pageYesNo;
        final int pageViewId;

        public static ControlsPage valueOf(String s)
        {
            return (ControlsPage)Enum.valueOf(com/lumiyaviewer/lumiya/ui/render/CardboardActivity$ControlsPage, s);
        }

        public static ControlsPage[] values()
        {
            return $VALUES;
        }

        static 
        {
            pageDefault = new ControlsPage("pageDefault", 0, 0x7f1000f5);
            pageSpeech = new ControlsPage("pageSpeech", 1, 0x7f10010d);
            pageTouchAim = new ControlsPage("pageTouchAim", 2, 0x7f100101);
            pageObject = new ControlsPage("pageObject", 3, 0x7f100102);
            pageScriptDialog = new ControlsPage("pageScriptDialog", 4, 0x7f100112);
            pageYesNo = new ControlsPage("pageYesNo", 5, 0x7f100108);
            pageDetails = new ControlsPage("pageDetails", 6, 0x7f100113);
            $VALUES = (new ControlsPage[] {
                pageDefault, pageSpeech, pageTouchAim, pageObject, pageScriptDialog, pageYesNo, pageDetails
            });
        }

        private ControlsPage(String s, int i, int j)
        {
            super(s, i);
            pageViewId = j;
        }
    }

    private class WorldStereoRenderer
        implements com.google.vr.sdk.base.GvrView.StereoRenderer
    {

        private static final float TURN_DEGREES = 35F;
        private static final float TURN_DEGREES_PER_MS = 0.02F;
        private static final float YAW_AVERAGE_FACTOR = 1E-04F;
        private boolean agentHeadingAcquired;
        private boolean crosshairVisible;
        private final float extTextureMatrixUV[] = new float[16];
        private GLExternalTexture externalTexture;
        private final float eyeHitTests[] = new float[4];
        private final float eyeOffset[] = new float[4];
        private final float eyeOffsetMatrix[] = new float[16];
        private final float eyeProjection[] = new float[32];
        private final boolean eyeProjectionValid[] = new boolean[2];
        private float eyeSeparation;
        private final int eyeViewport[] = new int[4];
        private final HeadTransformCompat headTransformCompat = new HeadTransformCompat();
        private long lastFrameTime;
        final CardboardActivity this$0;
        private int viewportHeight;
        private int viewportWidth;

        void _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity$WorldStereoRenderer_2D_mthref_2D_0()
        {
            CardboardActivity._2D_wrap8(CardboardActivity.this);
        }

        void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity$WorldStereoRenderer_58554()
        {
            CardboardActivity._2D_wrap4(CardboardActivity.this, externalTexture);
            CardboardActivity._2D_wrap0(CardboardActivity.this);
        }

        public void onDrawEye(Eye eye)
        {
            int k = eye.getType();
            float f;
            if (k == 1)
            {
                f = -0.5F;
            } else
            {
                f = 0.5F;
            }
            f *= eyeSeparation;
            for (int i = 0; i < 4; i++)
            {
                eyeOffset[i] = headTransformCompat.rightVector[i] * f;
            }

            eye.getViewport().getAsArray(eyeViewport, 0);
            eye.getEyeView();
            int j;
            if (k == 1)
            {
                j = 0;
            } else
            {
                j = 1;
            }
            if (CardboardActivity._2D_get24(CardboardActivity.this) != null && (!eyeProjectionValid[j] || eye.getProjectionChanged()))
            {
                System.arraycopy(eye.getPerspective(0.5F, CardboardActivity._2D_get24(CardboardActivity.this).drawDistance), 0, eyeProjection, j * 16, 16);
            }
            CardboardActivity._2D_get25(CardboardActivity.this).onDrawFrame(null, headTransformCompat, eyeOffset, eyeViewport, null, null, 0);
            if (externalTexture != null)
            {
                if (k == 1)
                {
                    j = 0;
                } else
                {
                    j = 2;
                }
                CardboardActivity._2D_get25(CardboardActivity.this).drawExternalTexture(externalTexture, extTextureMatrixUV, f, headTransformCompat.pitchDegrees, headTransformCompat.useButtonsYaw, 1.5F, 1.125F, eyeHitTests, j);
                if (crosshairVisible)
                {
                    CardboardActivity._2D_get25(CardboardActivity.this).drawCrosshair(0.1F, f);
                }
            }
        }

        public void onFinishFrame(Viewport viewport)
        {
            int i;
            int j;
            CardboardActivity._2D_get25(CardboardActivity.this).onFinishFrame();
            if (externalTexture == null)
            {
                break MISSING_BLOCK_LABEL_147;
            }
            float f = (eyeHitTests[0] + eyeHitTests[2]) / 2.0F;
            float f1 = (eyeHitTests[1] + eyeHitTests[3]) / 2.0F;
            i = (int)((f * 2.0F + 0.5F) * (float)externalTexture.getWidth());
            j = (int)((-(f1 * 2.0F) + 0.5F) * (float)externalTexture.getHeight());
            viewport = ((Viewport) (CardboardActivity._2D_get10(CardboardActivity.this)));
            viewport;
            JVM INSTR monitorenter ;
            CardboardActivity._2D_set6(CardboardActivity.this, i);
            CardboardActivity._2D_set7(CardboardActivity.this, j);
            viewport;
            JVM INSTR monitorexit ;
            if (!CardboardActivity._2D_get11(CardboardActivity.this).getAndSet(true))
            {
                runOnUiThread(new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls22(this));
            }
            return;
            Exception exception;
            exception;
            throw exception;
        }

        public void onNewFrame(HeadTransform headtransform)
        {
            long l;
            boolean flag5;
            Object obj = (SLAvatarControl)CardboardActivity._2D_get1(CardboardActivity.this).get();
            l = SystemClock.uptimeMillis();
            headtransform.getQuaternion(headTransformCompat.rotationQuat, 0);
            headtransform.getTranslation(headTransformCompat.translationVector, 0);
            headtransform.getHeadView(headTransformCompat.headTransformMatrix, 0);
            headtransform.getEulerAngles(headTransformCompat.eulerAngles, 0);
            headtransform.getRightVector(headTransformCompat.rightVectorRaw, 0);
            Matrix.multiplyMV(headTransformCompat.rightVector, 0, eyeOffsetMatrix, 0, headTransformCompat.rightVectorRaw, 0);
            headTransformCompat.yawDegrees = CameraParams.wrapAngle(headTransformCompat.eulerAngles[1] / 0.01745329F);
            if (obj != null && agentHeadingAcquired ^ true)
            {
                headTransformCompat.viewExtraYaw = ((SLAvatarControl) (obj)).getAgentHeading();
                agentHeadingAcquired = true;
                Debug.Printf("Cardboard: agent heading acquired: %.2f", new Object[] {
                    Float.valueOf(headTransformCompat.viewExtraYaw)
                });
            }
            if (CardboardActivity._2D_get4(CardboardActivity.this) == ControlsPage.pageTouchAim)
            {
                headTransformCompat.pitchDegrees = 0.0F;
                headTransformCompat.useButtonsYaw = 0.0F;
                headTransformCompat.lastYaw = headTransformCompat.eulerAngles[1];
            } else
            {
label0:
                {
                    if (CardboardActivity._2D_get4(CardboardActivity.this) != ControlsPage.pageObject && CardboardActivity._2D_get4(CardboardActivity.this) != ControlsPage.pageYesNo)
                    {
                        break label0;
                    }
                    headTransformCompat.pitchDegrees = 0.0F;
                    headTransformCompat.useButtonsYaw = (headTransformCompat.eulerAngles[1] - headTransformCompat.lastYaw) / 0.01745329F;
                }
            }
_L3:
            if (!headTransformCompat.neutralYawValid)
            {
                headTransformCompat.neutralYawValid = true;
                headTransformCompat.neutralYaw = headTransformCompat.yawDegrees;
            } else
            {
                long l1 = l - lastFrameTime;
                float f1 = CameraParams.angleMinusAngle(headTransformCompat.yawDegrees, headTransformCompat.neutralYaw);
                boolean flag1;
                boolean flag3;
                boolean flag4;
                if (CardboardActivity._2D_get17(CardboardActivity.this).get() || CardboardActivity._2D_get3(CardboardActivity.this).get() == 3)
                {
                    flag4 = true;
                } else
                {
                    flag4 = false;
                }
                flag1 = false;
                flag3 = false;
                f = 1.0F;
                if (flag4)
                {
                    double d = CardboardActivity._2D_get18(CardboardActivity.this).get();
                    if (d < 0.0D)
                    {
                        flag1 = true;
                    } else
                    {
                        flag1 = false;
                    }
                    if (d > 0.0D)
                    {
                        flag3 = true;
                    } else
                    {
                        flag3 = false;
                    }
                    f = (float)Math.abs(d);
                }
                if (f1 < -35F && flag4 ^ true || flag3)
                {
                    headTransformCompat.viewExtraYaw = CameraParams.wrapAngle(headTransformCompat.viewExtraYaw - f * ((float)l1 * 0.02F));
                } else
                if (f1 > 35F && flag4 ^ true || flag1)
                {
                    headtransform = headTransformCompat;
                    f1 = headTransformCompat.viewExtraYaw;
                    headtransform.viewExtraYaw = CameraParams.wrapAngle(f * ((float)l1 * 0.02F) + f1);
                } else
                {
                    headTransformCompat.neutralYaw = CameraParams.wrapAngle(headTransformCompat.neutralYaw + 1E-04F * f1 * (float)l1);
                }
            }
            if (headTransformCompat.viewExtraYaw != CardboardActivity._2D_get20(CardboardActivity.this) || Float.isNaN(CardboardActivity._2D_get20(CardboardActivity.this)))
            {
                CardboardActivity._2D_set5(CardboardActivity.this, CameraParams.wrapAngle(headTransformCompat.viewExtraYaw));
            }
            CardboardActivity._2D_set1(CardboardActivity.this, CameraParams.wrapAngle(headTransformCompat.yawDegrees + headTransformCompat.viewExtraYaw));
            if (!Float.isNaN(CardboardActivity._2D_get20(CardboardActivity.this)) && obj != null)
            {
                float f;
                boolean flag;
                boolean flag2;
                if (CardboardActivity._2D_get16(CardboardActivity.this))
                {
                    f = CardboardActivity._2D_get9(CardboardActivity.this);
                } else
                {
                    f = CardboardActivity._2D_get20(CardboardActivity.this);
                }
                ((SLAvatarControl) (obj)).setAgentHeading(f);
            }
            lastFrameTime = l;
            CardboardActivity._2D_get25(CardboardActivity.this).setOwnAvatarHidden(CardboardActivity._2D_get21(CardboardActivity.this) ^ true);
            CardboardActivity._2D_get25(CardboardActivity.this).onPrepareFrame(headTransformCompat);
            if (CardboardActivity._2D_get28(CardboardActivity.this).getAndSet(false))
            {
                CardboardActivity._2D_get25(CardboardActivity.this).pickObject(viewportWidth / 2, viewportHeight / 2, CardboardActivity._2D_get8(CardboardActivity.this));
            }
            headtransform = (ObjectIntersectInfo)CardboardActivity._2D_get22(CardboardActivity.this).get();
            obj = CardboardActivity._2D_get25(CardboardActivity.this);
            if (headtransform != null)
            {
                headtransform = ((ObjectIntersectInfo) (headtransform)).objInfo;
            } else
            {
                headtransform = null;
            }
            ((WorldViewRenderer) (obj)).setDrawPickedObject(headtransform);
            if (CardboardActivity._2D_get4(CardboardActivity.this) == ControlsPage.pageDetails)
            {
                flag5 = CardboardActivity._2D_get14(CardboardActivity.this);
            } else
            {
                flag5 = false;
            }
            crosshairVisible = flag5;
            if (externalTexture != null)
            {
                externalTexture.update(extTextureMatrixUV);
            }
            return;
            headTransformCompat.pitchDegrees = headTransformCompat.eulerAngles[0] / 0.01745329F;
            flag = false;
            if (CardboardActivity._2D_get4(CardboardActivity.this) != ControlsPage.pageSpeech) goto _L2; else goto _L1
_L1:
            flag = true;
_L5:
            if (flag)
            {
                headTransformCompat.lastYaw = headTransformCompat.eulerAngles[1];
                headTransformCompat.useButtonsYaw = 0.0F;
            } else
            {
                headTransformCompat.useButtonsYaw = (headTransformCompat.eulerAngles[1] - headTransformCompat.lastYaw) / 0.01745329F;
            }
              goto _L3
_L2:
            if (CardboardActivity._2D_get4(CardboardActivity.this) != ControlsPage.pageDefault) goto _L5; else goto _L4
_L4:
            flag2 = true;
            if (!CardboardActivity._2D_get14(CardboardActivity.this) || CardboardActivity._2D_get12(CardboardActivity.this) < CardboardActivity._2D_get23(CardboardActivity.this))
            {
                flag2 = false;
            }
            if (!CardboardActivity._2D_get14(CardboardActivity.this)) goto _L7; else goto _L6
_L6:
            flag = flag2;
            if (CardboardActivity._2D_get12(CardboardActivity.this) <= CardboardActivity._2D_get19(CardboardActivity.this)) goto _L5; else goto _L7
_L7:
            flag = flag2;
            if (CardboardActivity._2D_get21(CardboardActivity.this))
            {
                flag = false;
            }
              goto _L5
        }

        public void onRendererShutdown()
        {
            externalTexture.release();
            externalTexture = null;
            CardboardActivity._2D_get25(CardboardActivity.this).onRendererShutdown();
        }

        public void onSurfaceChanged(int i, int j)
        {
            viewportWidth = i;
            viewportHeight = j;
            CardboardActivity._2D_get25(CardboardActivity.this).onSurfaceChanged(null, i, j);
            if (externalTexture != null)
            {
                externalTexture.release();
            }
            externalTexture = new GLExternalTexture((int)((float)i * 1.0F), (int)((float)j * 0.75F) + CardboardActivity._2D_get30(CardboardActivity.this));
            eyeSeparation = CardboardActivity._2D_get7(CardboardActivity.this).getInterpupillaryDistance();
            headTransformCompat.neutralYawValid = false;
            runOnUiThread(new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls23(this));
        }

        public void onSurfaceCreated(EGLConfig eglconfig)
        {
            CardboardActivity._2D_get25(CardboardActivity.this).onSurfaceCreated(null, eglconfig, true);
        }

        WorldStereoRenderer()
        {
            this$0 = CardboardActivity.this;
            super();
            viewportWidth = 0;
            viewportHeight = 0;
            agentHeadingAcquired = false;
            eyeSeparation = 0.0F;
            lastFrameTime = 0L;
            crosshairVisible = false;
            Matrix.setIdentityM(eyeOffsetMatrix, 0);
            Matrix.rotateM(eyeOffsetMatrix, 0, -90F, 1.0F, 0.0F, 0.0F);
        }
    }


    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_render_2D_MoveControlSwitchesValues[];
    private static final int DEFAULT_FONT_SIZE_SP = 16;
    private static final int LISTVIEW_SCROLL_DURATION = 500;
    private static final int LISTVIEW_SCROLL_OFFSET = 100;
    private static final int RECYCLERVIEW_SCROLL_OFFSET = 100;
    private static final float VOICE_VIEW_HEIGHT_ALLOWANCE_DP = 60F;
    public static final String VR_MODE_TAG = "vrMode";
    private static final float controlDrawSizeFactor = 1.5F;
    private static final float controlSizeFactorX = 1F;
    private static final float controlSizeFactorY = 0.75F;
    private static final float crosshairSize = 0.1F;
    private static final int dialogButtonIds[] = {
        0x7f100135, 0x7f100136, 0x7f100137, 0x7f100132, 0x7f100133, 0x7f100134, 0x7f10012f, 0x7f100130, 0x7f100131, 0x7f10012c, 
        0x7f10012d, 0x7f10012e
    };
    private SLChatScriptDialog activeScriptDialog;
    private SLChatYesNoEvent activeYesNoEvent;
    private final SubscriptionData agentCircuit = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls11(this));
    private final AtomicReference avatarControl = new AtomicReference();
    ImageButton buttonChat;
    ImageButton buttonMoveBackward;
    ImageButton buttonMoveForward;
    ImageButton buttonObjectChat;
    ImageButton buttonSit;
    ImageButton buttonSpeak;
    ImageButton buttonSpeechSend;
    ImageButton buttonStandUp;
    ImageButton buttonTouch;
    ImageButton buttonTouchObject;
    ImageButton buttonTurnLeft;
    ImageButton buttonTurnRight;
    ViewGroup cardboardAimControls;
    ViewGroup cardboardDetailsPage;
    ViewGroup cardboardObjectControls;
    ViewGroup cardboardPrimaryControls;
    ViewGroup cardboardScriptDialog;
    ViewGroup cardboardSpeakControls;
    private final Object chatEventHandler = new Object() {

        final CardboardActivity this$0;

        void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity$5_68853(com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager.ChatMessageEvent chatmessageevent)
        {
            if (CardboardActivity._2D_get6(CardboardActivity.this) != null)
            {
                CardboardActivity._2D_get6(CardboardActivity.this).handleChatEvent(chatmessageevent);
            }
        }

        public void onChatMessage(com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager.ChatMessageEvent chatmessageevent)
        {
            runOnUiThread(new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls26(this, chatmessageevent));
        }

            
            {
                this$0 = CardboardActivity.this;
                super();
            }
    };
    LinearLayout chatsOverlayLayout;
    private Controller controller;
    private final AtomicInteger controllerConnectionState = new AtomicInteger(0);
    private final com.google.vr.sdk.controller.Controller.EventListener controllerEventListener = new com.google.vr.sdk.controller.Controller.EventListener() {

        private MoveControl activeMoveControl;
        private boolean appButtonPressed;
        final CardboardActivity this$0;

        void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_78808()
        {
            CardboardActivity._2D_wrap3(CardboardActivity.this, true);
        }

        void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_78948()
        {
            CardboardActivity._2D_wrap3(CardboardActivity.this, false);
        }

        void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_80726(MoveControl movecontrol, float f)
        {
            CardboardActivity._2D_wrap1(CardboardActivity.this, movecontrol, f);
        }

        void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_80931(MoveControl movecontrol)
        {
            CardboardActivity._2D_wrap1(CardboardActivity.this, movecontrol, 0.0F);
        }

        void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_81145(MoveControl movecontrol, float f)
        {
            if (CardboardActivity._2D_get21(CardboardActivity.this) || movecontrol == MoveControl.Right || movecontrol == MoveControl.Left)
            {
                CardboardActivity._2D_wrap1(CardboardActivity.this, movecontrol, f);
            }
        }

        void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity$7_81581(MoveControl movecontrol)
        {
            CardboardActivity._2D_wrap1(CardboardActivity.this, movecontrol, 0.0F);
        }

        public void onConnectionStateChanged(int i)
        {
            super.onConnectionStateChanged(i);
            String s;
            if (i == 3)
            {
                s = "connected";
            } else
            {
                s = "disconnected";
            }
            Debug.Printf("Cardboard: Daydream controller is now %s", new Object[] {
                s
            });
            CardboardActivity._2D_get3(CardboardActivity.this).set(i);
        }

        public void onUpdate()
        {
            float f2;
            f2 = 0.0F;
            super.onUpdate();
            CardboardActivity._2D_get2(CardboardActivity.this).update();
            if (!CardboardActivity._2D_get2(CardboardActivity.this).appButtonState || !(appButtonPressed ^ true)) goto _L2; else goto _L1
_L1:
            runOnUiThread(new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls20(this));
_L13:
            appButtonPressed = CardboardActivity._2D_get2(CardboardActivity.this).appButtonState;
            if (!CardboardActivity._2D_get2(CardboardActivity.this).isTouching) goto _L4; else goto _L3
_L3:
            float f;
            MoveControl movecontrol;
            float f1 = CardboardActivity._2D_get2(CardboardActivity.this).touch.x * 2.0F - 1.0F;
            float f3 = -(CardboardActivity._2D_get2(CardboardActivity.this).touch.y * 2.0F - 1.0F);
            f = f1;
            if (Math.abs(f1) < 0.5F)
            {
                f = 0.0F;
            }
            f1 = f3;
            if (Math.abs(f3) < 0.5F)
            {
                f1 = 0.0F;
            }
            if (Math.abs(f) >= Math.abs(f1))
            {
                if (f > 0.0F)
                {
                    movecontrol = MoveControl.Right;
                    f *= 2.0F;
                } else
                if (f < 0.0F)
                {
                    movecontrol = MoveControl.Left;
                    f = -f * 2.0F;
                } else
                {
                    movecontrol = null;
                    f = f2;
                }
            } else
            if (f1 > 0.0F)
            {
                movecontrol = MoveControl.Forward;
                f = f1 * 2.0F;
            } else
            if (f1 < 0.0F)
            {
                movecontrol = MoveControl.Backward;
                f = -f1 * 2.0F;
            } else
            {
                movecontrol = null;
                f = f2;
            }
            if (movecontrol == activeMoveControl) goto _L6; else goto _L5
_L5:
            if (movecontrol == null) goto _L8; else goto _L7
_L7:
            activeMoveControl = movecontrol;
            runOnUiThread(new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls29(f, this, movecontrol));
_L10:
            return;
_L2:
            if (!CardboardActivity._2D_get2(CardboardActivity.this).appButtonState && appButtonPressed)
            {
                runOnUiThread(new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls21(this));
            }
            continue; /* Loop/switch isn't completed */
_L8:
            movecontrol = activeMoveControl;
            runOnUiThread(new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls27(this, movecontrol));
            activeMoveControl = null;
            return;
_L6:
            if (movecontrol == null) goto _L10; else goto _L9
_L9:
            runOnUiThread(new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls30(f, this, movecontrol));
            return;
_L4:
            if (activeMoveControl == null) goto _L10; else goto _L11
_L11:
            MoveControl movecontrol1 = activeMoveControl;
            runOnUiThread(new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls28(this, movecontrol1));
            activeMoveControl = null;
            return;
            if (true) goto _L13; else goto _L12
_L12:
        }

            
            {
                this$0 = CardboardActivity.this;
                super();
                appButtonPressed = false;
                activeMoveControl = null;
            }
    };
    private ControllerManager controllerManager;
    private final com.google.vr.sdk.controller.ControllerManager.EventListener controllerManagerEventListener = new com.google.vr.sdk.controller.ControllerManager.EventListener() {

        final CardboardActivity this$0;

        public void onApiStatusChanged(int i)
        {
            Debug.Printf("Cardboard: controller API status: %d", new Object[] {
                Integer.valueOf(i)
            });
        }

        public void onRecentered()
        {
            if (CardboardActivity._2D_get7(CardboardActivity.this) != null)
            {
                CardboardActivity._2D_get7(CardboardActivity.this).recenterHeadTracker();
            }
        }

            
            {
                this$0 = CardboardActivity.this;
                super();
            }
    };
    private volatile ControlsPage currentControlsPage;
    private final SubscriptionData currentLocationInfo = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls15(this));
    TextView dialogQuestionText;
    private ChatterID dictationChatterID;
    private final AtomicReference externalTextureRef = new AtomicReference(null);
    private FadingTextViewLog fadingTextViewLog;
    private FullscreenMode fullscreenMode;
    private GvrView gvrView;
    private final Handler handler = new Handler() {

        final CardboardActivity this$0;

        public void handleMessage(Message message)
        {
            boolean flag = false;
            message.what;
            JVM INSTR tableswitch 1 2: default 28
        //                       1 29
        //                       2 123;
               goto _L1 _L2 _L3
_L1:
            return;
_L2:
            if (message.obj != null && (message.obj instanceof ObjectIntersectInfo))
            {
                message = (ObjectIntersectInfo)message.obj;
                Debug.Printf("Cardboard: PICKED OBJECT isAvatar %b localID %d", new Object[] {
                    Boolean.valueOf(((ObjectIntersectInfo) (message)).objInfo.isAvatar()), Integer.valueOf(((ObjectIntersectInfo) (message)).objInfo.localID)
                });
                if (((ObjectIntersectInfo) (message)).objInfo instanceof SLObjectAvatarInfo)
                {
                    flag = ((SLObjectAvatarInfo)((ObjectIntersectInfo) (message)).objInfo).isMyAvatar();
                }
                if (!flag)
                {
                    CardboardActivity._2D_wrap2(CardboardActivity.this, message);
                    return;
                }
            }
            continue; /* Loop/switch isn't completed */
_L3:
            if (message.obj != null && (message.obj instanceof SLObjectInfo))
            {
                message = (SLObjectInfo)message.obj;
                Debug.Printf("Cardboard: touched object isAvatar %b localID %d", new Object[] {
                    Boolean.valueOf(message.isAvatar()), Integer.valueOf(((SLObjectInfo) (message)).localID)
                });
                return;
            }
            if (true) goto _L1; else goto _L4
_L4:
        }

            
            {
                this$0 = CardboardActivity.this;
                super();
            }
    };
    private volatile float headAgentHeading;
    private final Object hitPointLock = new Object();
    private final AtomicBoolean hitPointUpdatePosted = new AtomicBoolean(false);
    private boolean hitPointValid;
    private int hitPointX;
    private int hitPointY;
    private final OnHoverListenerCompat hoverListener = new OnHoverListenerCompat() {

        final CardboardActivity this$0;

        public boolean onHoverEnter(View view)
        {
            view.setAlpha(1.0F);
            Debug.Printf("Cardboard: hovering enter %d", new Object[] {
                Integer.valueOf(view.getId())
            });
            CardboardActivity._2D_set2(CardboardActivity.this, view);
            CardboardActivity._2D_wrap5(CardboardActivity.this);
            return false;
        }

        public boolean onHoverExit(View view)
        {
            view.setAlpha(0.5F);
            Debug.Printf("Cardboard: hovering exit %d", new Object[] {
                Integer.valueOf(view.getId())
            });
            if (CardboardActivity._2D_get13(CardboardActivity.this) == view)
            {
                CardboardActivity._2D_set2(CardboardActivity.this, null);
            }
            CardboardActivity._2D_wrap5(CardboardActivity.this);
            return false;
        }

            
            {
                this$0 = CardboardActivity.this;
                super();
            }
    };
    private View hoveringOverButton;
    private View hoveringPressedButton;
    private volatile boolean insideControls;
    private long insideSince;
    private boolean isResumed;
    private boolean isSpeechFinished;
    private volatile boolean isWalking;
    private final AtomicBoolean keypadActive = new AtomicBoolean(false);
    private final AtomicDouble keypadTurning = new AtomicDouble(0.0D);
    private String lastSpeechRecognitionResults;
    private final int locationInWindow[] = new int[2];
    ViewGroup moveButtonsLayout;
    private int moveButtonsTop;
    private final SubscriptionData myAvatarState = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls12(this));
    private volatile float neutralAgentHeading;
    ImageButton noButton;
    TextView objectNameView;
    private final android.view.View.OnClickListener onDialogButtonClick = new android.view.View.OnClickListener() {

        final CardboardActivity this$0;

        public void onClick(View view)
        {
            if (CardboardActivity._2D_get0(CardboardActivity.this) == null) goto _L2; else goto _L1
_L1:
            int i = 0;
_L5:
            if (i >= CardboardActivity._2D_get5().length)
            {
                break MISSING_BLOCK_LABEL_85;
            }
            if (view.getId() != CardboardActivity._2D_get5()[i]) goto _L4; else goto _L3
_L3:
            CardboardActivity._2D_get0(CardboardActivity.this).onDialogButton(CardboardActivity._2D_get29(CardboardActivity.this), i);
            CardboardActivity._2D_set0(CardboardActivity.this, null);
_L2:
            CardboardActivity._2D_wrap2(CardboardActivity.this, null);
            CardboardActivity._2D_wrap6(CardboardActivity.this, ControlsPage.pageDefault);
            return;
_L4:
            i++;
              goto _L5
            i = -1;
              goto _L3
        }

            
            {
                this$0 = CardboardActivity.this;
                super();
            }
    };
    private final android.view.View.OnHoverListener onHoverListener = new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls1(this);
    private ViewGroup onScreenControlsLayout;
    private final android.view.View.OnClickListener onVoiceCallButtonListener = new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA(this);
    private final ImmutableMap outsideTouchListeners;
    private volatile boolean ownAvatarVisible;
    private ChatterNameRetriever pickedAvatarNameRetriever;
    private final AtomicReference pickedObject = new AtomicReference();
    private int postedHitPointX;
    private int postedHitPointY;
    private int primaryButtonsViewBottom;
    private final RecognitionListener recognitionListener = new RecognitionListener() {

        final CardboardActivity this$0;

        public void onBeginningOfSpeech()
        {
            Debug.Printf("Cardboard: beginning of speech", new Object[0]);
        }

        public void onBufferReceived(byte abyte0[])
        {
        }

        public void onEndOfSpeech()
        {
            Debug.Printf("Cardboard: end of speech", new Object[0]);
            speakLevelIndicator.setVisibility(4);
            speakNowText.setVisibility(4);
        }

        public void onError(int i)
        {
            Debug.Printf("Cardboard: speech error %d", new Object[] {
                Integer.valueOf(i)
            });
            i;
            JVM INSTR tableswitch 1 9: default 68
        //                       1 113
        //                       2 113
        //                       3 87
        //                       4 152
        //                       5 68
        //                       6 165
        //                       7 126
        //                       8 139
        //                       9 100;
               goto _L1 _L2 _L2 _L3 _L4 _L1 _L5 _L6 _L7 _L8
_L1:
            String s = getString(0x7f090315);
_L10:
            CardboardActivity._2D_wrap7(CardboardActivity.this, s);
            return;
_L3:
            s = getString(0x7f090314);
            continue; /* Loop/switch isn't completed */
_L8:
            s = getString(0x7f090317);
            continue; /* Loop/switch isn't completed */
_L2:
            s = getString(0x7f09031a);
            continue; /* Loop/switch isn't completed */
_L6:
            s = getString(0x7f09031b);
            continue; /* Loop/switch isn't completed */
_L7:
            s = getString(0x7f090316);
            continue; /* Loop/switch isn't completed */
_L4:
            s = getString(0x7f090318);
            continue; /* Loop/switch isn't completed */
_L5:
            s = getString(0x7f090319);
            if (true) goto _L10; else goto _L9
_L9:
        }

        public void onEvent(int i, Bundle bundle)
        {
        }

        public void onPartialResults(Bundle bundle)
        {
            Debug.Printf("Cardboard: speech recognition: got partial results", new Object[0]);
            bundle = bundle.getStringArrayList("results_recognition");
            if (bundle != null && bundle.size() > 0)
            {
                bundle = (String)bundle.get(0);
                CardboardActivity._2D_set4(CardboardActivity.this, bundle);
                speechRecognitionResults.setText(bundle);
                if (!Strings.isNullOrEmpty(bundle))
                {
                    buttonSpeechSend.setVisibility(0);
                }
            }
        }

        public void onReadyForSpeech(Bundle bundle)
        {
            speakNowText.setVisibility(0);
        }

        public void onResults(Bundle bundle)
        {
            Debug.Printf("Cardboard: speech recognition: got some results", new Object[0]);
            bundle = bundle.getStringArrayList("results_recognition");
            if (bundle != null && bundle.size() > 0)
            {
                bundle = (String)bundle.get(0);
                speechRecognitionResults.setText(bundle);
                CardboardActivity._2D_set4(CardboardActivity.this, bundle);
                if (!Strings.isNullOrEmpty(bundle))
                {
                    buttonSpeechSend.setVisibility(0);
                    speakLevelIndicator.setVisibility(4);
                    CardboardActivity._2D_set3(CardboardActivity.this, true);
                }
            }
        }

        public void onRmsChanged(float f)
        {
            if (!CardboardActivity._2D_get15(CardboardActivity.this))
            {
                if (Float.isNaN(CardboardActivity._2D_get27(CardboardActivity.this)) || f < CardboardActivity._2D_get27(CardboardActivity.this))
                {
                    CardboardActivity._2D_set9(CardboardActivity.this, f);
                }
                if (Float.isNaN(CardboardActivity._2D_get26(CardboardActivity.this)) || f > CardboardActivity._2D_get26(CardboardActivity.this))
                {
                    CardboardActivity._2D_set8(CardboardActivity.this, f);
                }
                float f2 = CardboardActivity._2D_get26(CardboardActivity.this);
                float f1 = f2;
                if (f2 - CardboardActivity._2D_get27(CardboardActivity.this) < 1.0F)
                {
                    f1 = CardboardActivity._2D_get27(CardboardActivity.this) + 1.0F;
                }
                int j = Math.round(((f - CardboardActivity._2D_get27(CardboardActivity.this)) * 100F) / (f1 - CardboardActivity._2D_get27(CardboardActivity.this)));
                int i = j;
                if (j < 0)
                {
                    i = 0;
                }
                j = i;
                if (i > 100)
                {
                    j = 100;
                }
                Debug.Printf("Cardboard: speech recognition: RMS %f", new Object[] {
                    Float.valueOf(f)
                });
                speakLevelIndicator.setVisibility(0);
                speakLevelIndicator.setProgress(j);
            }
        }

            
            {
                this$0 = CardboardActivity.this;
                super();
            }
    };
    private RenderSettings renderSettings;
    private WorldViewRenderer renderer;
    private final ScreenOnFlagHelper screenOnFlagHelper = new ScreenOnFlagHelper(this);
    private Point scrollableViewPoint;
    private final SubscriptionData selectedObjectProfile = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls13(this));
    ProgressBar speakLevelIndicator;
    TextView speakNowText;
    TextView speechRecognitionResults;
    private SpeechRecognizer speechRecognizer;
    private float speechRmsMax;
    private float speechRmsMin;
    private Handler stateHandler;
    private final com.google.vr.sdk.base.GvrView.StereoRenderer stereoRenderer = new WorldStereoRenderer();
    private final Set touchActivatedButtons = new HashSet();
    private final AtomicBoolean touchRequested = new AtomicBoolean(false);
    private UserManager userManager;
    private final AtomicBoolean viewDrawPosted = new AtomicBoolean(false);
    private final SubscriptionData voiceActiveChatter = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls16(this));
    private final SubscriptionData voiceChatInfo = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls17(this));
    private boolean voiceEnabled;
    private final SubscriptionData voiceLoggedIn = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls14(this));
    VoiceStatusView voiceStatusView;
    private int voiceViewHeightAllowance;
    ImageButton yesButton;
    TextView yesNoText;

    static SLChatScriptDialog _2D_get0(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.activeScriptDialog;
    }

    static AtomicReference _2D_get1(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.avatarControl;
    }

    static Object _2D_get10(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.hitPointLock;
    }

    static AtomicBoolean _2D_get11(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.hitPointUpdatePosted;
    }

    static int _2D_get12(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.hitPointY;
    }

    static View _2D_get13(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.hoveringOverButton;
    }

    static boolean _2D_get14(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.insideControls;
    }

    static boolean _2D_get15(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.isSpeechFinished;
    }

    static boolean _2D_get16(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.isWalking;
    }

    static AtomicBoolean _2D_get17(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.keypadActive;
    }

    static AtomicDouble _2D_get18(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.keypadTurning;
    }

    static int _2D_get19(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.moveButtonsTop;
    }

    static Controller _2D_get2(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.controller;
    }

    static float _2D_get20(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.neutralAgentHeading;
    }

    static boolean _2D_get21(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.ownAvatarVisible;
    }

    static AtomicReference _2D_get22(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.pickedObject;
    }

    static int _2D_get23(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.primaryButtonsViewBottom;
    }

    static RenderSettings _2D_get24(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.renderSettings;
    }

    static WorldViewRenderer _2D_get25(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.renderer;
    }

    static float _2D_get26(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.speechRmsMax;
    }

    static float _2D_get27(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.speechRmsMin;
    }

    static AtomicBoolean _2D_get28(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.touchRequested;
    }

    static UserManager _2D_get29(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.userManager;
    }

    static AtomicInteger _2D_get3(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.controllerConnectionState;
    }

    static int _2D_get30(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.voiceViewHeightAllowance;
    }

    static ControlsPage _2D_get4(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.currentControlsPage;
    }

    static int[] _2D_get5()
    {
        return dialogButtonIds;
    }

    static FadingTextViewLog _2D_get6(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.fadingTextViewLog;
    }

    static GvrView _2D_get7(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.gvrView;
    }

    static Handler _2D_get8(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.handler;
    }

    static float _2D_get9(CardboardActivity cardboardactivity)
    {
        return cardboardactivity.headAgentHeading;
    }

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_render_2D_MoveControlSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_render_2D_MoveControlSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_render_2D_MoveControlSwitchesValues;
        }
        int ai[] = new int[MoveControl.values().length];
        try
        {
            ai[MoveControl.Backward.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[MoveControl.Forward.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[MoveControl.Left.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[MoveControl.Right.ordinal()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_render_2D_MoveControlSwitchesValues = ai;
        return ai;
    }

    static SLChatScriptDialog _2D_set0(CardboardActivity cardboardactivity, SLChatScriptDialog slchatscriptdialog)
    {
        cardboardactivity.activeScriptDialog = slchatscriptdialog;
        return slchatscriptdialog;
    }

    static float _2D_set1(CardboardActivity cardboardactivity, float f)
    {
        cardboardactivity.headAgentHeading = f;
        return f;
    }

    static View _2D_set2(CardboardActivity cardboardactivity, View view)
    {
        cardboardactivity.hoveringOverButton = view;
        return view;
    }

    static boolean _2D_set3(CardboardActivity cardboardactivity, boolean flag)
    {
        cardboardactivity.isSpeechFinished = flag;
        return flag;
    }

    static String _2D_set4(CardboardActivity cardboardactivity, String s)
    {
        cardboardactivity.lastSpeechRecognitionResults = s;
        return s;
    }

    static float _2D_set5(CardboardActivity cardboardactivity, float f)
    {
        cardboardactivity.neutralAgentHeading = f;
        return f;
    }

    static int _2D_set6(CardboardActivity cardboardactivity, int i)
    {
        cardboardactivity.postedHitPointX = i;
        return i;
    }

    static int _2D_set7(CardboardActivity cardboardactivity, int i)
    {
        cardboardactivity.postedHitPointY = i;
        return i;
    }

    static float _2D_set8(CardboardActivity cardboardactivity, float f)
    {
        cardboardactivity.speechRmsMax = f;
        return f;
    }

    static float _2D_set9(CardboardActivity cardboardactivity, float f)
    {
        cardboardactivity.speechRmsMin = f;
        return f;
    }

    static void _2D_wrap0(CardboardActivity cardboardactivity)
    {
        cardboardactivity.draw2DUI();
    }

    static void _2D_wrap1(CardboardActivity cardboardactivity, MoveControl movecontrol, float f)
    {
        cardboardactivity.handleMoveControl(movecontrol, f);
    }

    static void _2D_wrap2(CardboardActivity cardboardactivity, ObjectIntersectInfo objectintersectinfo)
    {
        cardboardactivity.handlePickedObject(objectintersectinfo);
    }

    static void _2D_wrap3(CardboardActivity cardboardactivity, boolean flag)
    {
        cardboardactivity.onExternalButtonAction(flag);
    }

    static void _2D_wrap4(CardboardActivity cardboardactivity, GLExternalTexture glexternaltexture)
    {
        cardboardactivity.onNewExternalTexture(glexternaltexture);
    }

    static void _2D_wrap5(CardboardActivity cardboardactivity)
    {
        cardboardactivity.onViewsInvalidated();
    }

    static void _2D_wrap6(CardboardActivity cardboardactivity, ControlsPage controlspage)
    {
        cardboardactivity.setControlsPage(controlspage);
    }

    static void _2D_wrap7(CardboardActivity cardboardactivity, String s)
    {
        cardboardactivity.showSpeechRecognitionError(s);
    }

    static void _2D_wrap8(CardboardActivity cardboardactivity)
    {
        cardboardactivity.updateExternalTexturePointer();
    }

    public CardboardActivity()
    {
        isResumed = false;
        voiceEnabled = false;
        postedHitPointX = 0;
        postedHitPointY = 0;
        outsideTouchListeners = ImmutableMap.of(ControlsPage.pageSpeech, new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls3(this), ControlsPage.pageObject, new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls4(this), ControlsPage.pageScriptDialog, new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls5(this), ControlsPage.pageYesNo, new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls6(this), ControlsPage.pageDetails, new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls2(this));
        currentControlsPage = ControlsPage.pageDefault;
        fadingTextViewLog = null;
        lastSpeechRecognitionResults = "";
        speechRmsMin = (0.0F / 0.0F);
        speechRmsMax = (0.0F / 0.0F);
        isSpeechFinished = false;
        dictationChatterID = null;
        headAgentHeading = 0.0F;
        neutralAgentHeading = (0.0F / 0.0F);
        hoveringOverButton = null;
        hoveringPressedButton = null;
        activeScriptDialog = null;
        activeYesNoEvent = null;
        ownAvatarVisible = false;
        isWalking = false;
        primaryButtonsViewBottom = 0;
        moveButtonsTop = 0;
        pickedAvatarNameRetriever = null;
        insideControls = false;
        hitPointValid = false;
        insideSince = 0L;
        hitPointX = 0;
        hitPointY = 0;
        scrollableViewPoint = new Point();
    }

    private void closeSpeechControls()
    {
        if (speechRecognizer != null)
        {
            speechRecognizer.stopListening();
        }
        if (getCurrentDetailsFragment() != null)
        {
            setControlsPage(ControlsPage.pageDetails);
            return;
        } else
        {
            setControlsPage(ControlsPage.pageDefault);
            return;
        }
    }

    private void draw2DUI()
    {
        GLExternalTexture glexternaltexture;
        glexternaltexture = (GLExternalTexture)externalTextureRef.get();
        if (glexternaltexture == null)
        {
            break MISSING_BLOCK_LABEL_30;
        }
        Canvas canvas = glexternaltexture.getCanvas();
        drawExternalViews(canvas);
        glexternaltexture.postCanvas(canvas);
        return;
        IllegalStateException illegalstateexception;
        illegalstateexception;
    }

    private void drawExternalViews(Canvas canvas)
    {
        canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
        onScreenControlsLayout.draw(canvas);
    }

    private void drawViews()
    {
        viewDrawPosted.set(false);
        Debug.Printf("Cardboard: drawing 2D UI", new Object[0]);
        draw2DUI();
    }

    private View findMatchingView(ViewGroup viewgroup, int i, int j, int k, int l, Predicate predicate, Point point)
    {
        for (k = 0; k < viewgroup.getChildCount(); k++)
        {
            View view = viewgroup.getChildAt(k);
            if (view.getVisibility() != 0 || !view.isAttachedToWindow() || !predicate.apply(view))
            {
                continue;
            }
            view.getLocationInWindow(locationInWindow);
            l = locationInWindow[0];
            int i1 = locationInWindow[1];
            if ((new Rect(l, i1, view.getWidth() + l, view.getHeight() + i1)).contains(i, j))
            {
                point.set(i - l, j - i1);
                return view;
            }
        }

        for (k = 0; k < viewgroup.getChildCount(); k++)
        {
            View view1 = viewgroup.getChildAt(k);
            if (view1.getVisibility() != 0 || !view1.isAttachedToWindow() || !(view1 instanceof ViewGroup))
            {
                continue;
            }
            view1 = findMatchingView((ViewGroup)view1, i, j, 0, 0, predicate, point);
            if (view1 != null)
            {
                return view1;
            }
        }

        return null;
    }

    private void handleMoveControl(MoveControl movecontrol, float f)
    {
        SLAvatarControl slavatarcontrol = (SLAvatarControl)avatarControl.get();
        if (slavatarcontrol == null) goto _L2; else goto _L1
_L1:
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_render_2D_MoveControlSwitchesValues()[movecontrol.ordinal()];
        JVM INSTR tableswitch 1 4: default 52
    //                   1 175
    //                   2 120
    //                   3 53
    //                   4 87;
           goto _L2 _L3 _L4 _L5 _L6
_L2:
        return;
_L5:
        if (f != 0.0F)
        {
            keypadActive.set(true);
            keypadTurning.set(-f);
            return;
        } else
        {
            keypadTurning.set(0.0D);
            return;
        }
_L6:
        if (f != 0.0F)
        {
            keypadActive.set(true);
            keypadTurning.set(f);
            return;
        } else
        {
            keypadTurning.set(0.0D);
            return;
        }
_L4:
        if (f != 0.0F)
        {
            keypadActive.set(true);
            if (ownAvatarVisible)
            {
                slavatarcontrol.startCameraManualControl(0.0F, 1.0F * f, 0.0F, 0.0F);
                return;
            } else
            {
                startWalking(true);
                return;
            }
        }
        if (ownAvatarVisible)
        {
            slavatarcontrol.stopCameraManualControl();
            return;
        } else
        {
            stopWalking();
            return;
        }
_L3:
        if (f != 0.0F)
        {
            keypadActive.set(true);
            if (ownAvatarVisible)
            {
                slavatarcontrol.startCameraManualControl(0.0F, -1F * f, 0.0F, 0.0F);
                return;
            } else
            {
                startWalking(false);
                return;
            }
        }
        if (ownAvatarVisible)
        {
            slavatarcontrol.stopCameraManualControl();
            return;
        } else
        {
            stopWalking();
            return;
        }
    }

    private void handlePickedObject(ObjectIntersectInfo objectintersectinfo)
    {
        Object obj;
        if (objectintersectinfo != null)
        {
            obj = objectintersectinfo.objInfo;
        } else
        {
            obj = null;
        }
        if (obj != null)
        {
            pickedObject.set(objectintersectinfo);
            if (((SLObjectInfo) (obj)).isAvatar())
            {
                obj = ChatterID.getUserChatterID(userManager.getUserID(), ((SLObjectInfo) (obj)).getId());
                if (pickedAvatarNameRetriever != null)
                {
                    objectintersectinfo = pickedAvatarNameRetriever.chatterID;
                } else
                {
                    objectintersectinfo = null;
                }
                if (!Objects.equal(objectintersectinfo, obj))
                {
                    if (pickedAvatarNameRetriever != null)
                    {
                        pickedAvatarNameRetriever.dispose();
                        pickedAvatarNameRetriever = null;
                    }
                    pickedAvatarNameRetriever = new ChatterNameRetriever(((ChatterID) (obj)), new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls18(this), UIThreadExecutor.getInstance());
                }
            } else
            {
                Debug.Printf("ObjectPick: picked object %d", new Object[] {
                    Integer.valueOf(((SLObjectInfo) (obj)).localID)
                });
                selectedObjectProfile.subscribe(userManager.getObjectsManager().getObjectProfile(), Integer.valueOf(((SLObjectInfo) (obj)).localID));
            }
            setControlsPage(ControlsPage.pageObject);
            updateObjectPanel();
            return;
        }
        selectedObjectProfile.unsubscribe();
        if (pickedAvatarNameRetriever != null)
        {
            pickedAvatarNameRetriever.dispose();
            pickedAvatarNameRetriever = null;
        }
        pickedObject.set(null);
        setControlsPage(ControlsPage.pageDefault);
    }

    private boolean isViewScrollable(View view)
    {
        if (!(view instanceof ListView))
        {
            return view instanceof RecyclerView;
        } else
        {
            return true;
        }
    }

    private boolean isVoiceLoggedIn()
    {
        Boolean boolean1 = (Boolean)voiceLoggedIn.getData();
        if (boolean1 != null)
        {
            return boolean1.booleanValue();
        } else
        {
            return false;
        }
    }

    private void onAgentCircuit(SLAgentCircuit slagentcircuit)
    {
        SLAvatarControl slavatarcontrol = null;
        updateDrawingEnabled();
        AtomicReference atomicreference = avatarControl;
        if (slagentcircuit != null)
        {
            slavatarcontrol = slagentcircuit.getModules().avatarControl;
        }
        atomicreference.set(slavatarcontrol);
    }

    private void onCardboardTrigger()
    {
        int i;
        int j;
        if (hoveringOverButton != null)
        {
            i = hoveringOverButton.getId();
        } else
        {
            i = -1;
        }
        if (hoveringPressedButton != null)
        {
            j = hoveringPressedButton.getId();
        } else
        {
            j = -1;
        }
        Debug.Printf("Cardboard: trigger, hover over %d, hover pressed %d, hitPointValid %b", new Object[] {
            Integer.valueOf(i), Integer.valueOf(j), Boolean.valueOf(hitPointValid)
        });
        if (hoveringPressedButton == null && hitPointValid)
        {
            long l = SystemClock.uptimeMillis();
            if (insideControls)
            {
                if (hoveringOverButton == null || touchActivatedButtons.contains(hoveringOverButton) ^ true)
                {
                    MotionEvent motionevent = MotionEvent.obtain(insideSince, l, 0, hitPointX, hitPointY, 0);
                    motionevent.setSource(2);
                    onScreenControlsLayout.dispatchTouchEvent(motionevent);
                    motionevent = MotionEvent.obtain(insideSince, l + 1L, 1, hitPointX, hitPointY, 0);
                    motionevent.setSource(2);
                    onScreenControlsLayout.dispatchTouchEvent(motionevent);
                }
            } else
            {
                android.view.View.OnTouchListener ontouchlistener = (android.view.View.OnTouchListener)outsideTouchListeners.get(currentControlsPage);
                Debug.Printf("Cardboard: outside touch, listener %s", new Object[] {
                    ontouchlistener
                });
                if (ontouchlistener != null)
                {
                    ontouchlistener.onTouch(findViewById(currentControlsPage.pageViewId), MotionEvent.obtain(l, l + 100L, 1, 0.0F, 0.0F, 0));
                    return;
                }
            }
        }
    }

    private void onCurrentLocationChanged(CurrentLocationInfo currentlocationinfo)
    {
        updateVoiceIndication();
    }

    private boolean onDetailsOutsideTouch(View view, MotionEvent motionevent)
    {
        motionevent.getActionMasked();
        JVM INSTR tableswitch 1 1: default 24
    //                   1 26;
           goto _L1 _L2
_L1:
        return true;
_L2:
        handleBackPressed();
        if (true) goto _L1; else goto _L3
_L3:
    }

    private void onExternalButtonAction(boolean flag)
    {
        long l = SystemClock.uptimeMillis();
        if (flag)
        {
            MotionEvent motionevent = MotionEvent.obtain(l, l, 0, hitPointX, hitPointY, 0);
            motionevent.setSource(2);
            onVrTouchInternal(motionevent, true);
            onCardboardTrigger();
            return;
        } else
        {
            MotionEvent motionevent1 = MotionEvent.obtain(l, l, 1, hitPointX, hitPointY, 0);
            motionevent1.setSource(2);
            onVrTouchInternal(motionevent1, true);
            return;
        }
    }

    private void onExternalTexturePointer(int i, int j)
    {
        Object obj;
        hitPointValid = true;
        hitPointX = i;
        hitPointY = j;
        obj = (GLExternalTexture)externalTextureRef.get();
        if (obj == null) goto _L2; else goto _L1
_L1:
        boolean flag;
        if (i >= 0 && i < ((GLExternalTexture) (obj)).getWidth() && j >= 0 && j < ((GLExternalTexture) (obj)).getHeight())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (!flag) goto _L4; else goto _L3
_L3:
        if (!insideControls)
        {
            insideControls = true;
            insideSince = SystemClock.uptimeMillis();
            obj = MotionEvent.obtain(insideSince, insideSince, 9, i, j, 0);
            ((MotionEvent) (obj)).setSource(2);
            onScreenControlsLayout.dispatchGenericMotionEvent(((MotionEvent) (obj)));
        }
        obj = MotionEvent.obtain(insideSince, SystemClock.uptimeMillis(), 7, i, j, 0);
        ((MotionEvent) (obj)).setSource(2);
        onScreenControlsLayout.dispatchGenericMotionEvent(((MotionEvent) (obj)));
        if (currentControlsPage != ControlsPage.pageDetails) goto _L2; else goto _L5
_L5:
        obj = findMatchingView(cardboardDetailsPage, i, j, 0, 0, new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls10(this), scrollableViewPoint);
        if (obj == null) goto _L2; else goto _L6
_L6:
        i = ((View) (obj)).getHeight();
        if (scrollableViewPoint.y >= i / 4) goto _L8; else goto _L7
_L7:
        Debug.Printf("Cardboard: want to scroll up %s", new Object[] {
            obj
        });
        if (!(obj instanceof ListView)) goto _L10; else goto _L9
_L9:
        ((ListView)obj).smoothScrollBy(-100, 500);
_L2:
        return;
_L10:
        if (obj instanceof RecyclerView)
        {
            ((RecyclerView)obj).smoothScrollBy(0, -100);
            return;
        }
        continue; /* Loop/switch isn't completed */
_L8:
        if (scrollableViewPoint.y > (i * 3) / 4)
        {
            Debug.Printf("Cardboard: want to scroll down %s", new Object[] {
                obj
            });
            if (obj instanceof ListView)
            {
                ((ListView)obj).smoothScrollBy(100, 500);
                return;
            }
            if (obj instanceof RecyclerView)
            {
                ((RecyclerView)obj).smoothScrollBy(0, 100);
                return;
            }
        }
        continue; /* Loop/switch isn't completed */
_L4:
        if (insideControls)
        {
            insideControls = false;
            MotionEvent motionevent = MotionEvent.obtain(insideSince, SystemClock.uptimeMillis(), 10, i, j, 0);
            motionevent.setSource(2);
            onScreenControlsLayout.dispatchGenericMotionEvent(motionevent);
            return;
        }
        if (true) goto _L2; else goto _L11
_L11:
    }

    private void onMyAvatarState(MyAvatarState myavatarstate)
    {
        if (myavatarstate.isSitting())
        {
            moveButtonsLayout.setVisibility(0);
            buttonStandUp.setVisibility(0);
            ownAvatarVisible = true;
            return;
        }
        if (ownAvatarVisible)
        {
            myavatarstate = (SLAvatarControl)avatarControl.get();
            if (myavatarstate != null)
            {
                myavatarstate.setCameraManualControl(false);
                myavatarstate.setAgentHeading(neutralAgentHeading);
            }
        }
        moveButtonsLayout.setVisibility(8);
        buttonStandUp.setVisibility(8);
        ownAvatarVisible = false;
    }

    private void onNewExternalTexture(GLExternalTexture glexternaltexture)
    {
        externalTextureRef.set(glexternaltexture);
        int i = glexternaltexture.getWidth();
        int j = glexternaltexture.getHeight();
        ((CardboardControlsPlaceholder)findViewById(0x7f100116)).setFixedSize(i, j);
    }

    private void onPickedAvatarNameUpdated(ChatterNameRetriever chatternameretriever)
    {
        objectNameView.setText(chatternameretriever.getResolvedName());
    }

    private void onSelectedObjectProfile(SLObjectProfileData slobjectprofiledata)
    {
        objectNameView.setText((CharSequence)slobjectprofiledata.name().or(getString(0x7f090239)));
    }

    private void onViewsInvalidated()
    {
        if (!viewDrawPosted.getAndSet(true))
        {
            Debug.Printf("Cardboard: posting draw views", new Object[0]);
            handler.post(new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls25(this));
        }
    }

    private void onVoiceActiveChatter(ChatterID chatterid)
    {
        if (voiceStatusView != null)
        {
            voiceStatusView.setChatterID(chatterid);
        }
        if (userManager != null && chatterid != null)
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
        updateVoiceIndication();
    }

    private void onVoiceLoginStatusChanged(Boolean boolean1)
    {
        updateVoiceIndication();
    }

    private boolean onVrTouch(View view, MotionEvent motionevent)
    {
        return onVrTouchInternal(motionevent, false);
    }

    private boolean onVrTouchInternal(MotionEvent motionevent, boolean flag)
    {
        int j = -1;
        int k = motionevent.getActionMasked();
        String s;
        if (k == 0)
        {
            s = "down";
        } else
        if (k == 1)
        {
            s = "up";
        } else
        {
            s = null;
        }
        if (s != null)
        {
            int i;
            if (hoveringOverButton != null)
            {
                i = hoveringOverButton.getId();
            } else
            {
                i = -1;
            }
            if (hoveringPressedButton != null)
            {
                j = hoveringPressedButton.getId();
            }
            Debug.Printf("Cardboard: vr touch %s, hover over %d, hover pressed %d", new Object[] {
                s, Integer.valueOf(i), Integer.valueOf(j)
            });
        }
        if (hoveringPressedButton != null && k == 1)
        {
            Debug.Printf("Cardboard: touch act: release on button %s", new Object[] {
                hoveringPressedButton
            });
            hoveringPressedButton.dispatchTouchEvent(motionevent);
            hoveringPressedButton = null;
            return true;
        }
        if (currentControlsPage != ControlsPage.pageDefault) goto _L2; else goto _L1
_L1:
        if (hoveringOverButton != null) goto _L4; else goto _L3
_L3:
        if (flag) goto _L2; else goto _L5
_L5:
        k;
        JVM INSTR tableswitch 0 1: default 196
    //                   0 215
    //                   1 222;
           goto _L6 _L7 _L8
_L6:
        Debug.Printf("Cardboard: MotionEvent: %s", new Object[] {
            motionevent.toString()
        });
_L2:
        return false;
_L7:
        startWalking(true);
        return false;
_L8:
        stopWalking();
        return false;
_L4:
        if (touchActivatedButtons.contains(hoveringOverButton) && k == 0)
        {
            Debug.Printf("Cardboard: touch act: press on button %s", new Object[] {
                hoveringOverButton
            });
            hoveringPressedButton = hoveringOverButton;
            hoveringOverButton.dispatchTouchEvent(motionevent);
            return false;
        }
        if (true) goto _L2; else goto _L9
_L9:
    }

    private void setControlsPage(ControlsPage controlspage)
    {
        ControlsPage acontrolspage[] = ControlsPage.values();
        int k = acontrolspage.length;
        int i = 0;
        while (i < k) 
        {
            ControlsPage controlspage1 = acontrolspage[i];
            View view = findViewById(controlspage1.pageViewId);
            int j;
            if (controlspage1 == controlspage)
            {
                j = 0;
            } else
            {
                j = 4;
            }
            view.setVisibility(j);
            i++;
        }
        currentControlsPage = controlspage;
    }

    private void showSpeechRecognitionError(String s)
    {
        speakNowText.setVisibility(4);
        speakLevelIndicator.setVisibility(4);
        buttonSpeechSend.setVisibility(4);
        speechRecognitionResults.setText(s);
        isSpeechFinished = true;
    }

    private void startWalking(boolean flag)
    {
        Object obj = (SLAgentCircuit)agentCircuit.getData();
        if (obj != null)
        {
            isWalking = true;
            obj = ((SLAgentCircuit) (obj)).getModules().avatarControl;
            ((SLAvatarControl) (obj)).setAgentHeading(headAgentHeading);
            byte byte0;
            if (flag)
            {
                byte0 = 2;
            } else
            {
                byte0 = 4;
            }
            ((SLAvatarControl) (obj)).StartAgentMotion(byte0);
        }
    }

    private void stopWalking()
    {
        SLAgentCircuit slagentcircuit = (SLAgentCircuit)agentCircuit.getData();
        if (slagentcircuit != null)
        {
            slagentcircuit.getModules().avatarControl.StopAgentMotion();
            isWalking = false;
        }
    }

    private void updateDrawingEnabled()
    {
        SLAgentCircuit slagentcircuit;
label0:
        {
            slagentcircuit = (SLAgentCircuit)agentCircuit.getData();
            if (slagentcircuit != null && renderSettings != null)
            {
                if (!isResumed)
                {
                    break label0;
                }
                slagentcircuit.getModules().drawDistance.Enable3DView(renderSettings.drawDistance);
            }
            return;
        }
        slagentcircuit.getModules().drawDistance.Disable3DView();
    }

    private void updateExternalTexturePointer()
    {
        if (!hitPointUpdatePosted.getAndSet(false))
        {
            break MISSING_BLOCK_LABEL_38;
        }
        Object obj = hitPointLock;
        obj;
        JVM INSTR monitorenter ;
        int i;
        int j;
        i = postedHitPointX;
        j = postedHitPointY;
        obj;
        JVM INSTR monitorexit ;
        onExternalTexturePointer(i, j);
        return;
        Exception exception;
        exception;
        throw exception;
    }

    private void updateObjectPanel()
    {
        Object obj = (ObjectIntersectInfo)pickedObject.get();
        if (obj != null)
        {
            obj = ((ObjectIntersectInfo) (obj)).objInfo;
        } else
        {
            obj = null;
        }
        if (obj == null)
        {
            setControlsPage(ControlsPage.pageDefault);
            return;
        }
        if (((SLObjectInfo) (obj)).isAvatar())
        {
            buttonSit.setVisibility(8);
            buttonTouchObject.setVisibility(8);
            buttonObjectChat.setVisibility(0);
            return;
        }
        buttonSit.setVisibility(0);
        ImageButton imagebutton = buttonTouchObject;
        int i;
        if (((SLObjectInfo) (obj)).isTouchable())
        {
            i = 0;
        } else
        {
            i = 8;
        }
        imagebutton.setVisibility(i);
        buttonObjectChat.setVisibility(8);
    }

    private void updateVoiceIndication()
    {
        boolean flag = isVoiceLoggedIn();
        Object obj = (CurrentLocationInfo)currentLocationInfo.getData();
        Object obj1 = voiceStatusView;
        if (flag && obj != null && ((CurrentLocationInfo) (obj)).parcelVoiceChannel() != null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        ((VoiceStatusView) (obj1)).setCanConnect(flag);
        obj = (ChatterID)voiceActiveChatter.getData();
        obj1 = (VoiceChatInfo)voiceChatInfo.getData();
        if (obj != null && obj1 != null && ((VoiceChatInfo) (obj1)).state != com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.None)
        {
            buttonSpeak.setVisibility(4);
            return;
        } else
        {
            buttonSpeak.setVisibility(0);
            return;
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_2D_mthref_2D_0(SLAgentCircuit slagentcircuit)
    {
        onAgentCircuit(slagentcircuit);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_2D_mthref_2D_1(MyAvatarState myavatarstate)
    {
        onMyAvatarState(myavatarstate);
    }

    boolean _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_2D_mthref_2D_10(View view, MotionEvent motionevent)
    {
        return onDetailsOutsideTouch(view, motionevent);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_2D_mthref_2D_11(SLObjectProfileData slobjectprofiledata)
    {
        onSelectedObjectProfile(slobjectprofiledata);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_2D_mthref_2D_12()
    {
        onViewsInvalidated();
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_2D_mthref_2D_13()
    {
        onCardboardTrigger();
    }

    boolean _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_2D_mthref_2D_14(View view, MotionEvent motionevent)
    {
        return onVrTouch(view, motionevent);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_2D_mthref_2D_15()
    {
        drawViews();
    }

    boolean _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_2D_mthref_2D_16(View view)
    {
        return isViewScrollable(view);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_2D_mthref_2D_17(ChatterNameRetriever chatternameretriever)
    {
        onPickedAvatarNameUpdated(chatternameretriever);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_2D_mthref_2D_2(Boolean boolean1)
    {
        onVoiceLoginStatusChanged(boolean1);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_2D_mthref_2D_3(CurrentLocationInfo currentlocationinfo)
    {
        onCurrentLocationChanged(currentlocationinfo);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_2D_mthref_2D_4(ChatterID chatterid)
    {
        onVoiceActiveChatter(chatterid);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_2D_mthref_2D_5(VoiceChatInfo voicechatinfo)
    {
        onVoiceChatInfo(voicechatinfo);
    }

    boolean _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_2D_mthref_2D_6(View view, MotionEvent motionevent)
    {
        return onSpeakControlsTouch(view, motionevent);
    }

    boolean _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_2D_mthref_2D_7(View view, MotionEvent motionevent)
    {
        return onObjectControlsTouch(view, motionevent);
    }

    boolean _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_2D_mthref_2D_8(View view, MotionEvent motionevent)
    {
        return onScriptDialogOutsideTouch(view, motionevent);
    }

    boolean _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_2D_mthref_2D_9(View view, MotionEvent motionevent)
    {
        return onYesNoOutsideTouch(view, motionevent);
    }

    protected boolean acceptsDetailFragment(Class class1)
    {
        if (!com/lumiyaviewer/lumiya/ui/chat/ContactsFragment.isAssignableFrom(class1))
        {
            return com/lumiyaviewer/lumiya/ui/chat/ChatFragment.isAssignableFrom(class1);
        } else
        {
            return true;
        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyevent)
    {
        Debug.Printf("Cardboard: dispatch key event: keycode %d", new Object[] {
            Integer.valueOf(keyevent.getKeyCode())
        });
        if ((SLAvatarControl)avatarControl.get() == null) goto _L2; else goto _L1
_L1:
        keyevent.getKeyCode();
        JVM INSTR lookupswitch 7: default 104
    //                   19: 188
    //                   20: 231
    //                   21: 110
    //                   22: 145
    //                   23: 303
    //                   62: 274
    //                   96: 274;
           goto _L2 _L3 _L4 _L5 _L6 _L7 _L8 _L8
_L2:
        return super.dispatchKeyEvent(keyevent);
_L5:
        if (keyevent.getAction() != 0) goto _L10; else goto _L9
_L9:
        handleMoveControl(MoveControl.Left, 1.0F);
_L12:
        return true;
_L10:
        if (keyevent.getAction() != 1) goto _L12; else goto _L11
_L11:
        handleMoveControl(MoveControl.Left, 0.0F);
        return true;
_L6:
        if (keyevent.getAction() != 0) goto _L14; else goto _L13
_L13:
        keypadActive.set(true);
        handleMoveControl(MoveControl.Right, 1.0F);
_L16:
        return true;
_L14:
        if (keyevent.getAction() != 1) goto _L16; else goto _L15
_L15:
        handleMoveControl(MoveControl.Right, 0.0F);
        return true;
_L3:
        if (keyevent.getAction() != 0) goto _L18; else goto _L17
_L17:
        keypadActive.set(true);
        handleMoveControl(MoveControl.Forward, 1.0F);
_L20:
        return true;
_L18:
        if (keyevent.getAction() != 1) goto _L20; else goto _L19
_L19:
        handleMoveControl(MoveControl.Forward, 0.0F);
        return true;
_L4:
        if (keyevent.getAction() != 0) goto _L22; else goto _L21
_L21:
        keypadActive.set(true);
        handleMoveControl(MoveControl.Backward, 1.0F);
_L24:
        return true;
_L22:
        if (keyevent.getAction() != 1) goto _L24; else goto _L23
_L23:
        handleMoveControl(MoveControl.Backward, 0.0F);
        return true;
_L8:
        if (keyevent.getAction() != 0) goto _L26; else goto _L25
_L25:
        onExternalButtonAction(true);
_L28:
        return true;
_L26:
        if (keyevent.getAction() != 1) goto _L28; else goto _L27
_L27:
        onExternalButtonAction(false);
        return true;
_L7:
        return true;
    }

    public boolean handleBackPressed()
    {
        if (currentControlsPage != ControlsPage.pageDetails)
        {
            return false;
        } else
        {
            return super.handleBackPressed();
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_16797()
    {
        int i = 1;
        int j = speechRecognitionResults.getLineHeight();
        j = speechRecognitionResults.getHeight() / j;
        Debug.Printf("Cardboard: setting max lines = %d", new Object[] {
            Integer.valueOf(j)
        });
        if (j >= 1)
        {
            i = j;
        }
        if (speechRecognitionResults.getMaxLines() != i)
        {
            speechRecognitionResults.setMaxLines(i);
            speechRecognitionResults.setEllipsize(android.text.TextUtils.TruncateAt.END);
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_17418()
    {
        primaryButtonsViewBottom = buttonTouch.getTop() + buttonTouch.getHeight();
        moveButtonsTop = moveButtonsLayout.getTop();
    }

    boolean lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_28338(View view, MotionEvent motionevent)
    {
        switch (motionevent.getActionMasked())
        {
        default:
            return false;

        case 9: // '\t'
            return hoverListener.onHoverEnter(view);

        case 10: // '\n'
            return hoverListener.onHoverExit(view);
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity_71435(View view)
    {
        view = (SLAgentCircuit)agentCircuit.getData();
        CurrentLocationInfo currentlocationinfo = (CurrentLocationInfo)currentLocationInfo.getData();
        if (voiceEnabled && view != null && isVoiceLoggedIn() && currentlocationinfo != null && currentlocationinfo.parcelVoiceChannel() != null)
        {
            view.getModules().voice.nearbyVoiceChatRequest(currentlocationinfo.parcelVoiceChannel());
        }
    }

    public boolean onAimControlsTouch(View view, MotionEvent motionevent)
    {
        Debug.Printf("Cardboard: aim controls touched, view %s", new Object[] {
            view
        });
        switch (motionevent.getActionMasked())
        {
        default:
            return true;

        case 1: // '\001'
            touchRequested.set(true);
            break;
        }
        return true;
    }

    public boolean onCamButtonTouch(View view, MotionEvent motionevent)
    {
        float f;
        float f1;
        SLAvatarControl slavatarcontrol;
        f = 1.0F;
        f1 = -1F;
        Debug.Printf("Cardboard: cam button: event %s button %s", new Object[] {
            motionevent, view
        });
        slavatarcontrol = (SLAvatarControl)avatarControl.get();
        if (slavatarcontrol == null) goto _L2; else goto _L1
_L1:
        if (motionevent.getActionMasked() != 0) goto _L4; else goto _L3
_L3:
        view.getId();
        JVM INSTR tableswitch 2131755260 2131755263: default 84
    //                   2131755260 101
    //                   2131755261 114
    //                   2131755262 109
    //                   2131755263 120;
           goto _L5 _L6 _L7 _L8 _L9
_L5:
        f = 0.0F;
        f1 = 0.0F;
_L12:
        slavatarcontrol.startCameraManualControl(0.0F, f1, 0.0F, f);
_L2:
        return true;
_L6:
        f1 = 1.0F;
        f = 0.0F;
        continue; /* Loop/switch isn't completed */
_L8:
        f = 0.0F;
        continue; /* Loop/switch isn't completed */
_L7:
        f1 = 0.0F;
        continue; /* Loop/switch isn't completed */
_L9:
        f = -1F;
        f1 = 0.0F;
        continue; /* Loop/switch isn't completed */
_L4:
        if (motionevent.getActionMasked() != 1) goto _L2; else goto _L10
_L10:
        slavatarcontrol.stopCameraManualControl();
        return true;
        if (true) goto _L12; else goto _L11
_L11:
    }

    public void onChatButton()
    {
        if (userManager != null)
        {
            setControlsPage(ControlsPage.pageDetails);
            Bundle bundle = ActivityUtils.makeFragmentArguments(userManager.getUserID(), null);
            bundle.putBoolean("vrMode", true);
            DetailsActivity.showEmbeddedDetails(this, com/lumiyaviewer/lumiya/ui/chat/ContactsFragment, bundle);
        }
    }

    protected void onCreate(Bundle bundle)
    {
        setTheme(0x7f0b002c);
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        fullscreenMode = new FullscreenMode(getWindow());
        setContentView(0x7f040022);
        userManager = ActivityUtils.getUserManager(getIntent());
        if (userManager == null)
        {
            finish();
            return;
        }
        AndroidCompat.trySetVrModeEnabled(this, true);
        AndroidCompat.setSustainedPerformanceMode(this, true);
        renderSettings = new RenderSettings(PreferenceManager.getDefaultSharedPreferences(getBaseContext()));
        stateHandler = new Handler();
        Debug.Printf("Cardboard: creating VR view", new Object[0]);
        bundle = GlobalOptions.getInstance();
        CardboardControlsPlaceholder cardboardcontrolsplaceholder = (CardboardControlsPlaceholder)findViewById(0x7f100116);
        onScreenControlsLayout = (ViewGroup)getLayoutInflater().inflate(0x7f040021, cardboardcontrolsplaceholder, true);
        cardboardcontrolsplaceholder.setOnViewInvalidateListener(new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls19(this));
        int k = (int)TypedValue.applyDimension(2, 16F, getResources().getDisplayMetrics());
        float f;
        int i;
        boolean flag;
        if (bundle.getVoiceEnabled())
        {
            i = (int)TypedValue.applyDimension(1, 60F, getResources().getDisplayMetrics());
        } else
        {
            i = 0;
        }
        voiceViewHeightAllowance = i;
        renderer = new WorldViewRenderer(stateHandler, true, userManager, k);
        renderer.setDrawDistance(renderSettings.drawDistance);
        renderer.setAvatarCountLimit(renderSettings.avatarCountLimit);
        flag = bundle.getForceDaylightTime();
        f = bundle.getForceDaylightHour();
        renderer.setForcedTime(flag, f);
        gvrView = new GvrView(this);
        gvrView.setDistortionCorrectionEnabled(true);
        gvrView.setAsyncReprojectionEnabled(true);
        gvrView.setRenderer(stereoRenderer);
        controllerManager = new ControllerManager(this, controllerManagerEventListener);
        Debug.Printf("Cardboard: has magnet: %b", new Object[] {
            Boolean.valueOf(gvrView.getGvrViewerParams().getHasMagnet())
        });
        ((FrameLayout)findViewById(0x7f100117)).addView(gvrView, new android.widget.FrameLayout.LayoutParams(-1, -1));
        gvrView.setOnCardboardTriggerListener(new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls24(this));
        ButterKnife.bind(this, onScreenControlsLayout);
        bundle = new View[14];
        bundle[0] = buttonTouch;
        bundle[1] = buttonSpeak;
        bundle[2] = buttonChat;
        bundle[3] = buttonSpeechSend;
        bundle[4] = buttonSit;
        bundle[5] = buttonTouchObject;
        bundle[6] = buttonObjectChat;
        bundle[7] = buttonMoveForward;
        bundle[8] = buttonMoveBackward;
        bundle[9] = buttonTurnLeft;
        bundle[10] = buttonTurnRight;
        bundle[11] = buttonStandUp;
        bundle[12] = yesButton;
        bundle[13] = noButton;
        k = bundle.length;
        for (i = 0; i < k; i++)
        {
            View view = bundle[i];
            view.setAlpha(0.5F);
            view.setOnHoverListener(onHoverListener);
        }

        touchActivatedButtons.add(buttonMoveForward);
        touchActivatedButtons.add(buttonMoveBackward);
        touchActivatedButtons.add(buttonTurnLeft);
        touchActivatedButtons.add(buttonTurnRight);
        buttonStandUp.setVisibility(8);
        moveButtonsLayout.setVisibility(8);
        voiceStatusView.setShowActiveChatterName(true);
        voiceStatusView.hideBackground();
        voiceStatusView.setLightTheme();
        voiceStatusView.enableHover(hoverListener);
        voiceStatusView.setOnCallButtonListener(onVoiceCallButtonListener);
        k = (int)TypedValue.applyDimension(1, 1.0F, getResources().getDisplayMetrics());
        bundle = dialogButtonIds;
        int l = bundle.length;
        for (int j = 0; j < l; j++)
        {
            Button button = (Button)findViewById(bundle[j]);
            button.setAlpha(0.5F);
            button.setOnHoverListener(onHoverListener);
            button.setOnClickListener(onDialogButtonClick);
            button.setPadding(k, k, k, k);
            button.setCompoundDrawablePadding(k);
        }

        dialogQuestionText.setTextColor(-1);
        fadingTextViewLog = new FadingTextViewLog(userManager, this, chatsOverlayLayout, -1, 0);
        setControlsPage(ControlsPage.pageDefault);
        speechRecognitionResults.getViewTreeObserver().addOnGlobalLayoutListener(new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls8(this));
        cardboardPrimaryControls.getViewTreeObserver().addOnGlobalLayoutListener(new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls9(this));
        gvrView.setOnTouchListener(new _2D_.Lambda.yhBpPTpVtOAhPHTLXL5B0hI4gXA._cls7(this));
    }

    protected void onDestroy()
    {
        if (gvrView != null)
        {
            gvrView.setOnCardboardTriggerListener(null);
            gvrView.shutdown();
            gvrView = null;
        }
        super.onDestroy();
    }

    protected boolean onDetailsStackEmpty()
    {
        super.onDetailsStackEmpty();
        setControlsPage(ControlsPage.pageDefault);
        return true;
    }

    public void onNewObjectPopup(SLChatEvent slchatevent)
    {
        if (slchatevent instanceof SLChatScriptDialog)
        {
            if (currentControlsPage != ControlsPage.pageYesNo)
            {
                slchatevent = (SLChatScriptDialog)slchatevent;
                activeScriptDialog = slchatevent;
                setControlsPage(ControlsPage.pageScriptDialog);
                String as[] = slchatevent.getButtons();
                int i = 0;
                while (i < dialogButtonIds.length) 
                {
                    Button button = (Button)findViewById(dialogButtonIds[i]);
                    if (i < as.length)
                    {
                        button.setVisibility(0);
                        button.setText(as[i]);
                    } else
                    {
                        button.setVisibility(8);
                    }
                    i++;
                }
                dialogQuestionText.setText(slchatevent.getRawText());
            }
        } else
        if (slchatevent instanceof SLChatPermissionRequestEvent)
        {
            activeYesNoEvent = (SLChatYesNoEvent)slchatevent;
            yesNoText.setText(((SLChatPermissionRequestEvent)slchatevent).getQuestion(this));
            setControlsPage(ControlsPage.pageYesNo);
            return;
        }
    }

    public void onNoButton()
    {
        if (activeYesNoEvent != null && userManager != null)
        {
            activeYesNoEvent.onYesAction(this, userManager);
            activeYesNoEvent = null;
        }
        handlePickedObject(null);
        setControlsPage(ControlsPage.pageDefault);
    }

    public void onObjectChat()
    {
        SLAgentCircuit slagentcircuit = (SLAgentCircuit)agentCircuit.getData();
        Object obj = (ObjectIntersectInfo)pickedObject.get();
        if (obj != null)
        {
            obj = ((ObjectIntersectInfo) (obj)).objInfo;
        } else
        {
            obj = null;
        }
        if (slagentcircuit != null && obj != null && userManager != null && ((SLObjectInfo) (obj)).isAvatar())
        {
            startDictation(ChatterID.getUserChatterID(userManager.getUserID(), ((SLObjectInfo) (obj)).getId()));
        }
    }

    public boolean onObjectControlsTouch(View view, MotionEvent motionevent)
    {
        motionevent.getActionMasked();
        JVM INSTR tableswitch 1 1: default 24
    //                   1 26;
           goto _L1 _L2
_L1:
        return true;
_L2:
        handlePickedObject(null);
        setControlsPage(ControlsPage.pageDefault);
        if (true) goto _L1; else goto _L3
_L3:
    }

    public void onObjectPopupCountChanged(int i)
    {
    }

    public void onObjectSit()
    {
        SLAgentCircuit slagentcircuit = (SLAgentCircuit)agentCircuit.getData();
        Object obj = (ObjectIntersectInfo)pickedObject.get();
        if (obj != null)
        {
            obj = ((ObjectIntersectInfo) (obj)).objInfo;
        } else
        {
            obj = null;
        }
        if (slagentcircuit != null && obj != null)
        {
            slagentcircuit.getModules().avatarControl.SitOnObject(((SLObjectInfo) (obj)).getId());
            handlePickedObject(null);
            setControlsPage(ControlsPage.pageDefault);
        }
    }

    public void onObjectTouch()
    {
        SLObjectInfo slobjectinfo;
        SLAgentCircuit slagentcircuit;
label0:
        {
            slagentcircuit = (SLAgentCircuit)agentCircuit.getData();
            ObjectIntersectInfo objectintersectinfo = (ObjectIntersectInfo)pickedObject.get();
            if (objectintersectinfo != null)
            {
                slobjectinfo = objectintersectinfo.objInfo;
            } else
            {
                slobjectinfo = null;
            }
            if (slagentcircuit != null && slobjectinfo != null && !slobjectinfo.isAvatar())
            {
                if (!objectintersectinfo.intersectInfo.faceKnown)
                {
                    break label0;
                }
                LLVector3 llvector3 = slobjectinfo.getAbsolutePosition();
                slagentcircuit.TouchObjectFace(slobjectinfo, objectintersectinfo.intersectInfo.faceID, llvector3.x, llvector3.y, llvector3.z, objectintersectinfo.intersectInfo.u, objectintersectinfo.intersectInfo.v, objectintersectinfo.intersectInfo.s, objectintersectinfo.intersectInfo.t);
            }
            return;
        }
        slagentcircuit.TouchObject(slobjectinfo.localID);
    }

    protected void onPause()
    {
        if (speechRecognizer != null)
        {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
        myAvatarState.unsubscribe();
        if (userManager != null)
        {
            userManager.getObjectPopupsManager().removeObjectPopupListener(this);
            userManager.getObjectPopupsManager().removePopupWatcher(this);
            try
            {
                userManager.getChatterList().getActiveChattersManager().getChatEventBus().unregister(chatEventHandler);
            }
            catch (IllegalArgumentException illegalargumentexception) { }
        }
        isResumed = false;
        updateDrawingEnabled();
        if (gvrView != null)
        {
            gvrView.onPause();
        }
        screenOnFlagHelper.stop();
        super.onPause();
    }

    protected void onResume()
    {
        super.onResume();
        if (gvrView != null)
        {
            gvrView.onResume();
        }
        fullscreenMode.goFullscreen();
        screenOnFlagHelper.start();
        if (speechRecognizer == null)
        {
            if (SpeechRecognizer.isRecognitionAvailable(this))
            {
                Debug.Printf("Cardboard: speech recognition is available", new Object[0]);
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
                speechRecognizer.setRecognitionListener(recognitionListener);
            } else
            {
                Debug.Printf("Cardboard: speech recognition is not available", new Object[0]);
                speechRecognizer = null;
            }
        }
        isResumed = true;
        updateDrawingEnabled();
        if (userManager != null)
        {
            userManager.getObjectPopupsManager().addPopupWatcher(this);
            userManager.getObjectPopupsManager().setObjectPopupListener(this, UIThreadExecutor.getInstance());
            myAvatarState.subscribe(userManager.getObjectsManager().myAvatarState(), SubscriptionSingleKey.Value);
            userManager.getChatterList().getActiveChattersManager().getChatEventBus().register(chatEventHandler);
        }
    }

    public boolean onScriptDialogOutsideTouch(View view, MotionEvent motionevent)
    {
        motionevent.getActionMasked();
        JVM INSTR tableswitch 1 1: default 24
    //                   1 26;
           goto _L1 _L2
_L1:
        return true;
_L2:
        if (activeScriptDialog != null)
        {
            activeScriptDialog.onDialogIgnored(userManager);
            activeScriptDialog = null;
        }
        handlePickedObject(null);
        setControlsPage(ControlsPage.pageDefault);
        if (true) goto _L1; else goto _L3
_L3:
    }

    public void onSpeakButton()
    {
        if (userManager != null)
        {
            startDictation(ChatterID.getLocalChatterID(userManager.getUserID()));
        }
    }

    public boolean onSpeakControlsTouch(View view, MotionEvent motionevent)
    {
        Debug.Printf("Cardboard: speak controls touched, view %s", new Object[] {
            view
        });
        switch (motionevent.getActionMasked())
        {
        default:
            return true;

        case 1: // '\001'
            closeSpeechControls();
            break;
        }
        return true;
    }

    public void onSpeechSendButton()
    {
        if (!Strings.isNullOrEmpty(lastSpeechRecognitionResults))
        {
            if (userManager != null)
            {
                SLAgentCircuit slagentcircuit = userManager.getActiveAgentCircuit();
                if (slagentcircuit != null && dictationChatterID != null)
                {
                    slagentcircuit.SendChatMessage(dictationChatterID, lastSpeechRecognitionResults);
                }
            }
            lastSpeechRecognitionResults = "";
        }
        closeSpeechControls();
    }

    public void onStandUpButton()
    {
        SLAvatarControl slavatarcontrol = (SLAvatarControl)avatarControl.get();
        if (slavatarcontrol != null)
        {
            slavatarcontrol.Stand();
        }
    }

    protected void onStart()
    {
        super.onStart();
        voiceEnabled = GlobalOptions.getInstance().getVoiceEnabled();
        controllerManager.start();
        controller = controllerManager.getController();
        if (controller != null)
        {
            controller.setEventListener(controllerEventListener);
        }
        if (userManager != null)
        {
            agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID());
            voiceLoggedIn.subscribe(userManager.getVoiceLoggedIn(), SubscriptionSingleKey.Value);
            voiceActiveChatter.subscribe(userManager.getVoiceActiveChatter(), SubscriptionSingleKey.Value);
            currentLocationInfo.subscribe(userManager.getCurrentLocationInfo(), SubscriptionSingleKey.Value);
        }
        if (voiceEnabled)
        {
            voiceStatusView.setShowWhenInactive(true);
            return;
        } else
        {
            voiceStatusView.setShowWhenInactive(false);
            return;
        }
    }

    protected void onStop()
    {
        voiceActiveChatter.unsubscribe();
        voiceLoggedIn.unsubscribe();
        currentLocationInfo.unsubscribe();
        agentCircuit.unsubscribe();
        voiceStatusView.setChatterID(null);
        keypadActive.set(false);
        controllerManager.stop();
        super.onStop();
    }

    public void onTouchButton()
    {
        setControlsPage(ControlsPage.pageTouchAim);
    }

    public void onWindowFocusChanged(boolean flag)
    {
        super.onWindowFocusChanged(flag);
        fullscreenMode.onWindowFocusChanged(flag);
    }

    public void onYesButton()
    {
        if (activeYesNoEvent != null && userManager != null)
        {
            activeYesNoEvent.onYesAction(this, userManager);
            activeYesNoEvent = null;
        }
        handlePickedObject(null);
        setControlsPage(ControlsPage.pageDefault);
    }

    public boolean onYesNoOutsideTouch(View view, MotionEvent motionevent)
    {
        motionevent.getActionMasked();
        JVM INSTR tableswitch 1 1: default 24
    //                   1 26;
           goto _L1 _L2
_L1:
        return true;
_L2:
        activeYesNoEvent = null;
        handlePickedObject(null);
        setControlsPage(ControlsPage.pageDefault);
        if (true) goto _L1; else goto _L3
_L3:
    }

    public void startDictation(ChatterID chatterid)
    {
        setControlsPage(ControlsPage.pageSpeech);
        dictationChatterID = chatterid;
        speakNowText.setVisibility(4);
        speakLevelIndicator.setVisibility(4);
        buttonSpeechSend.setVisibility(4);
        speechRecognitionResults.setText("");
        if (speechRecognizer != null)
        {
            isSpeechFinished = false;
            chatterid = new Intent();
            chatterid.putExtra("android.speech.extra.PARTIAL_RESULTS", true);
            speechRecognizer.startListening(chatterid);
            return;
        } else
        {
            showSpeechRecognitionError(getString(0x7f09031c));
            return;
        }
    }

}
