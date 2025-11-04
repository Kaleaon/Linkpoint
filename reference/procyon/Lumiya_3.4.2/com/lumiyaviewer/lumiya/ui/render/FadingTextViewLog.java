// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.render;

import android.util.DisplayMetrics;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.util.TypedValue;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import java.util.Iterator;
import android.animation.Animator$AnimatorListener;
import android.view.View;
import android.animation.Animator;
import android.widget.TextView;
import android.animation.AnimatorListenerAdapter;
import android.os.Build$VERSION;
import android.os.SystemClock;
import java.util.LinkedHashMap;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.os.Handler;
import android.content.Context;
import android.widget.LinearLayout;
import java.util.Map;

public class FadingTextViewLog
{
    private static final long STALE_CHAT_TIMEOUT = 5000L;
    private final Runnable RemoveStaleChatsTask;
    private final Map<Long, ChatEventOverlay> chatEventOverlays;
    private final LinearLayout chatsOverlayLayout;
    private final Context context;
    private final int logBackgroundColor;
    private final int logTextColor;
    private final Handler mHandler;
    private boolean removeStaleChatsPosted;
    private final UserManager userManager;
    
    FadingTextViewLog(final UserManager userManager, final Context context, final LinearLayout chatsOverlayLayout, final int logTextColor, final int logBackgroundColor) {
        this.mHandler = new Handler();
        this.chatEventOverlays = new LinkedHashMap<Long, ChatEventOverlay>();
        this.removeStaleChatsPosted = false;
        this.RemoveStaleChatsTask = new Runnable() {
            @Override
            public void run() {
                FadingTextViewLog.this.removeStaleChatsPosted = false;
                if (FadingTextViewLog.this.chatsOverlayLayout != null) {
                    final long uptimeMillis = SystemClock.uptimeMillis();
                    final Iterator iterator = FadingTextViewLog.this.chatEventOverlays.entrySet().iterator();
                    while (iterator.hasNext()) {
                        final Map.Entry<K, ChatEventOverlay> entry = (Map.Entry<K, ChatEventOverlay>)iterator.next();
                        if (entry != null) {
                            if (uptimeMillis < entry.getValue().timestamp + 5000L) {
                                break;
                            }
                            final TextView textView = entry.getValue().textView;
                            if (Build$VERSION.SDK_INT >= 14) {
                                textView.animate().alpha(0.0f).setDuration(1000L).setListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(final Animator animator) {
                                        FadingTextViewLog.this.chatsOverlayLayout.removeView((View)textView);
                                    }
                                }).start();
                            }
                            else {
                                FadingTextViewLog.this.chatsOverlayLayout.removeView((View)textView);
                            }
                            iterator.remove();
                        }
                    }
                }
                FadingTextViewLog.this.postRemovingStaleChats();
            }
        };
        this.userManager = userManager;
        this.context = context;
        this.chatsOverlayLayout = chatsOverlayLayout;
        this.logTextColor = logTextColor;
        this.logBackgroundColor = logBackgroundColor;
    }
    
    void clearChatEvents() {
        if (this.chatsOverlayLayout != null) {
            this.chatsOverlayLayout.removeAllViews();
        }
        this.chatEventOverlays.clear();
    }
    
    void handleChatEvent(final ActiveChattersManager.ChatMessageEvent chatMessageEvent) {
        final TextView textView = null;
        final SLChatEvent loadFromDatabaseObject = SLChatEvent.loadFromDatabaseObject(chatMessageEvent.chatMessage, this.userManager.getUserID());
        if (loadFromDatabaseObject != null) {
            final CharSequence plainTextMessage = loadFromDatabaseObject.getPlainTextMessage(this.context, this.userManager, false);
            String s;
            if (plainTextMessage != null) {
                s = plainTextMessage.toString();
            }
            else {
                s = null;
            }
            if (!Strings.isNullOrEmpty(s)) {
                if (chatMessageEvent.isPrivate) {
                    s = "[IM] " + s;
                }
                TextView textView3;
                if (chatMessageEvent.isNewMessage) {
                    final DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
                    final int n = (int)TypedValue.applyDimension(1, 10.0f, displayMetrics);
                    final int n2 = (int)TypedValue.applyDimension(1, 5.0f, displayMetrics);
                    final int n3 = (int)TypedValue.applyDimension(1, 10.0f, displayMetrics);
                    final int n4 = (int)TypedValue.applyDimension(1, 5.0f, displayMetrics);
                    final LinearLayout$LayoutParams layoutParams = new LinearLayout$LayoutParams(-2, -2);
                    layoutParams.setMargins(n, n2, n, n2);
                    final TextView textView2 = new TextView(this.context);
                    textView2.setBackgroundColor(this.logBackgroundColor);
                    textView2.setTextColor(this.logTextColor);
                    textView2.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
                    textView2.setPadding(n3, n4, n3, n4);
                    this.chatsOverlayLayout.addView((View)textView2);
                    this.chatEventOverlays.put(chatMessageEvent.chatMessage.getId(), new ChatEventOverlay(SystemClock.uptimeMillis(), textView2));
                    this.postRemovingStaleChats();
                    textView3 = textView2;
                }
                else {
                    final ChatEventOverlay chatEventOverlay = this.chatEventOverlays.get(chatMessageEvent.chatMessage.getId());
                    textView3 = textView;
                    if (chatEventOverlay != null) {
                        textView3 = chatEventOverlay.textView;
                    }
                }
                if (textView3 != null) {
                    textView3.setText((CharSequence)s);
                }
            }
        }
    }
    
    void postRemovingStaleChats() {
        if (!this.removeStaleChatsPosted) {
            final Iterator<Map.Entry<Long, ChatEventOverlay>> iterator = this.chatEventOverlays.entrySet().iterator();
            if (iterator.hasNext()) {
                final Map.Entry<K, ChatEventOverlay> entry = (Map.Entry<K, ChatEventOverlay>)iterator.next();
                if (entry != null) {
                    this.removeStaleChatsPosted = true;
                    this.mHandler.postAtTime(this.RemoveStaleChatsTask, entry.getValue().timestamp + 5000L);
                }
            }
        }
    }
}
