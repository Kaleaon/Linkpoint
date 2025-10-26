package com.lumiyaviewer.lumiya.ui.render

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import com.google.common.base.Strings
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.Iterator
import java.util.LinkedHashMap
import java.util.Map

class FadingTextViewLog {
    private val STALE_CHAT_TIMEOUT: Long = 5000
    private Runnable RemoveStaleChatsTask = Runnable() {
        Unit run() {
            Boolean unused = FadingTextViewLog.this.removeStaleChatsPosted = false
            if (FadingTextViewLog.this.chatsOverlayLayout != null) {
                Long uptimeMillis = SystemClock.uptimeMillis()
                Iterator it = FadingTextViewLog.this.chatEventOverlays.entrySet().iterator()
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next()
                    if (entry != null) {
                        if (uptimeMillis < ((ChatEventOverlay) entry.getValue()).timestamp + FadingTextViewLog.STALE_CHAT_TIMEOUT) {
                            break
                        }
                        TextView textView = ((ChatEventOverlay) entry.getValue()).textView
                        if (Build.VERSION.SDK_INT >= 14) {
                            textView.animate().alpha(0.0f).setDuration(1000).setListener(AnimatorListenerAdapter() {
                                Unit onAnimationEnd(Animator animator) {
                                    FadingTextViewLog.this.chatsOverlayLayout.removeView(textView)
                                }
                            }).start()
                        } else {
                            FadingTextViewLog.this.chatsOverlayLayout.removeView(textView)
                        }
                        it.remove()
                    }
                }
            }
            FadingTextViewLog.this.postRemovingStaleChats()
        }
    }
    /* access modifiers changed from: private */
    Map<Long, ChatEventOverlay> chatEventOverlays = LinkedHashMap()
    /* access modifiers changed from: private */
    LinearLayout chatsOverlayLayout
    private Context context
    private Int logBackgroundColor
    private Int logTextColor
    private val mHandler: Handler = Handler()
    /* access modifiers changed from: private */
    Boolean removeStaleChatsPosted = false
    private UserManager userManager

    FadingTextViewLog(UserManager userManager2, Context context2, LinearLayout linearLayout, Int i, Int i2) {
        this.userManager = userManager2
        this.context = context2
        this.chatsOverlayLayout = linearLayout
        this.logTextColor = i
        this.logBackgroundColor = i2
    }

    /* access modifiers changed from: package-private */
    Unit clearChatEvents() {
        if (this.chatsOverlayLayout != null) {
            this.chatsOverlayLayout.removeAllViews()
        }
        this.chatEventOverlays.clear()
    }

    /* access modifiers changed from: package-private */
    Unit handleChatEvent(ActiveChattersManager.ChatMessageEvent chatMessageEvent) {
        TextView textView = null
        SLChatEvent loadFromDatabaseObject = SLChatEvent.loadFromDatabaseObject(chatMessageEvent.chatMessage, this.userManager.getUserID())
        if (loadFromDatabaseObject != null) {
            CharSequence plainTextMessage = loadFromDatabaseObject.getPlainTextMessage(this.context, this.userManager, false)
            String charSequence = plainTextMessage != null ? plainTextMessage.toString() : null
            if (!Strings.isNullOrEmpty(charSequence)) {
                String str = chatMessageEvent.isPrivate ? "[IM] " + charSequence : charSequence
                if (chatMessageEvent.isNewMessage) {
                    DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics()
                    Int applyDimension = (Int) TypedValue.applyDimension(1, 10.0f, displayMetrics)
                    Int applyDimension2 = (Int) TypedValue.applyDimension(1, 5.0f, displayMetrics)
                    Int applyDimension3 = (Int) TypedValue.applyDimension(1, 10.0f, displayMetrics)
                    Int applyDimension4 = (Int) TypedValue.applyDimension(1, 5.0f, displayMetrics)
                    LinearLayout.LayoutParams layoutParams = LinearLayout.LayoutParams(-2, -2)
                    layoutParams.setMargins(applyDimension, applyDimension2, applyDimension, applyDimension2)
                    textView = TextView(this.context)
                    textView.setBackgroundColor(this.logBackgroundColor)
                    textView.setTextColor(this.logTextColor)
                    textView.setLayoutParams(layoutParams)
                    textView.setPadding(applyDimension3, applyDimension4, applyDimension3, applyDimension4)
                    this.chatsOverlayLayout.addView(textView)
                    this.chatEventOverlays.put(chatMessageEvent.chatMessage.getId(), ChatEventOverlay(SystemClock.uptimeMillis(), textView))
                    postRemovingStaleChats()
                } else {
                    ChatEventOverlay chatEventOverlay = this.chatEventOverlays.get(chatMessageEvent.chatMessage.getId())
                    if (chatEventOverlay != null) {
                        textView = chatEventOverlay.textView
                    }
                }
                if (textView != null) {
                    textView.setText(str)
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    Unit postRemovingStaleChats() {
        Map.Entry next
        if (!this.removeStaleChatsPosted) {
            Iterator<Map.Entry<Long, ChatEventOverlay>> it = this.chatEventOverlays.entrySet().iterator()
            if (it.hasNext() && (next = it.next()) != null) {
                this.removeStaleChatsPosted = true
                this.mHandler.postAtTime(this.RemoveStaleChatsTask, ((ChatEventOverlay) next.getValue()).timestamp + STALE_CHAT_TIMEOUT)
            }
        }
    }
}
