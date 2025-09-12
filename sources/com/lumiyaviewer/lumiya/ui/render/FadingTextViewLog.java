// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            ChatEventOverlay

public class FadingTextViewLog
{

    private static final long STALE_CHAT_TIMEOUT = 5000L;
    private final Runnable RemoveStaleChatsTask = new Runnable() {

        final FadingTextViewLog this$0;

        public void run()
        {
            FadingTextViewLog._2D_set0(FadingTextViewLog.this, false);
            if (FadingTextViewLog._2D_get1(FadingTextViewLog.this) != null)
            {
                long l = SystemClock.uptimeMillis();
                Iterator iterator = FadingTextViewLog._2D_get0(FadingTextViewLog.this).entrySet().iterator();
                do
                {
                    if (!iterator.hasNext())
                    {
                        break;
                    }
                    Object obj = (java.util.Map.Entry)iterator.next();
                    if (obj == null)
                    {
                        continue;
                    }
                    if (l < ((ChatEventOverlay)((java.util.Map.Entry) (obj)).getValue()).timestamp + 5000L)
                    {
                        break;
                    }
                    obj = ((ChatEventOverlay)((java.util.Map.Entry) (obj)).getValue()).textView;
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        ((TextView) (obj)).animate().alpha(0.0F).setDuration(1000L).setListener(((_cls1) (obj)). new AnimatorListenerAdapter() {

                            final _cls1 this$1;
                            final TextView val$textView;

                            public void onAnimationEnd(Animator animator)
                            {
                                FadingTextViewLog._2D_get1(_fld0).removeView(textView);
                            }

            
            {
                this$1 = final__pcls1;
                textView = TextView.this;
                super();
            }
                        }).start();
                    } else
                    {
                        FadingTextViewLog._2D_get1(FadingTextViewLog.this).removeView(((android.view.View) (obj)));
                    }
                    iterator.remove();
                } while (true);
            }
            postRemovingStaleChats();
        }

            
            {
                this$0 = FadingTextViewLog.this;
                super();
            }
    };
    private final Map chatEventOverlays = new LinkedHashMap();
    private final LinearLayout chatsOverlayLayout;
    private final Context context;
    private final int logBackgroundColor;
    private final int logTextColor;
    private final Handler mHandler = new Handler();
    private boolean removeStaleChatsPosted;
    private final UserManager userManager;

    static Map _2D_get0(FadingTextViewLog fadingtextviewlog)
    {
        return fadingtextviewlog.chatEventOverlays;
    }

    static LinearLayout _2D_get1(FadingTextViewLog fadingtextviewlog)
    {
        return fadingtextviewlog.chatsOverlayLayout;
    }

    static boolean _2D_set0(FadingTextViewLog fadingtextviewlog, boolean flag)
    {
        fadingtextviewlog.removeStaleChatsPosted = flag;
        return flag;
    }

    FadingTextViewLog(UserManager usermanager, Context context1, LinearLayout linearlayout, int i, int j)
    {
        removeStaleChatsPosted = false;
        userManager = usermanager;
        context = context1;
        chatsOverlayLayout = linearlayout;
        logTextColor = i;
        logBackgroundColor = j;
    }

    void clearChatEvents()
    {
        if (chatsOverlayLayout != null)
        {
            chatsOverlayLayout.removeAllViews();
        }
        chatEventOverlays.clear();
    }

    void handleChatEvent(com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager.ChatMessageEvent chatmessageevent)
    {
        Object obj;
        Object obj1;
        obj1 = null;
        obj = SLChatEvent.loadFromDatabaseObject(chatmessageevent.chatMessage, userManager.getUserID());
        if (obj == null) goto _L2; else goto _L1
_L1:
        obj = ((SLChatEvent) (obj)).getPlainTextMessage(context, userManager, false);
        android.widget.LinearLayout.LayoutParams layoutparams;
        int i;
        int j;
        int k;
        int l;
        if (obj != null)
        {
            obj = ((CharSequence) (obj)).toString();
        } else
        {
            obj = null;
        }
        if (Strings.isNullOrEmpty(((String) (obj)))) goto _L2; else goto _L3
_L3:
        if (chatmessageevent.isPrivate)
        {
            obj = (new StringBuilder()).append("[IM] ").append(((String) (obj))).toString();
        }
        if (!chatmessageevent.isNewMessage) goto _L5; else goto _L4
_L4:
        obj1 = context.getResources().getDisplayMetrics();
        i = (int)TypedValue.applyDimension(1, 10F, ((android.util.DisplayMetrics) (obj1)));
        j = (int)TypedValue.applyDimension(1, 5F, ((android.util.DisplayMetrics) (obj1)));
        k = (int)TypedValue.applyDimension(1, 10F, ((android.util.DisplayMetrics) (obj1)));
        l = (int)TypedValue.applyDimension(1, 5F, ((android.util.DisplayMetrics) (obj1)));
        layoutparams = new android.widget.LinearLayout.LayoutParams(-2, -2);
        layoutparams.setMargins(i, j, i, j);
        obj1 = new TextView(context);
        ((TextView) (obj1)).setBackgroundColor(logBackgroundColor);
        ((TextView) (obj1)).setTextColor(logTextColor);
        ((TextView) (obj1)).setLayoutParams(layoutparams);
        ((TextView) (obj1)).setPadding(k, l, k, l);
        chatsOverlayLayout.addView(((android.view.View) (obj1)));
        chatEventOverlays.put(chatmessageevent.chatMessage.getId(), new ChatEventOverlay(SystemClock.uptimeMillis(), ((TextView) (obj1))));
        postRemovingStaleChats();
        chatmessageevent = ((com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager.ChatMessageEvent) (obj1));
_L7:
        if (chatmessageevent != null)
        {
            chatmessageevent.setText(((CharSequence) (obj)));
        }
_L2:
        return;
_L5:
        ChatEventOverlay chateventoverlay = (ChatEventOverlay)chatEventOverlays.get(chatmessageevent.chatMessage.getId());
        chatmessageevent = ((com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager.ChatMessageEvent) (obj1));
        if (chateventoverlay != null)
        {
            chatmessageevent = chateventoverlay.textView;
        }
        if (true) goto _L7; else goto _L6
_L6:
    }

    void postRemovingStaleChats()
    {
        if (!removeStaleChatsPosted)
        {
            Object obj = chatEventOverlays.entrySet().iterator();
            if (((Iterator) (obj)).hasNext())
            {
                obj = (java.util.Map.Entry)((Iterator) (obj)).next();
                if (obj != null)
                {
                    removeStaleChatsPosted = true;
                    mHandler.postAtTime(RemoveStaleChatsTask, ((ChatEventOverlay)((java.util.Map.Entry) (obj)).getValue()).timestamp + 5000L);
                }
            }
        }
    }
}
