// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.widget.Toast;
import android.content.ClipData;
import android.text.ClipboardManager;
import android.os.Build$VERSION;
import android.view.MenuItem;
import android.content.Context;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import android.content.res.Resources$Theme;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.Debug;
import android.widget.TextView$BufferType;
import java.text.DateFormat;
import android.text.style.StyleSpan;
import com.google.common.base.Strings;
import android.text.SpannableStringBuilder;
import com.lumiyaviewer.lumiya.GlobalOptions;
import android.graphics.PorterDuff$Mode;
import android.util.TypedValue;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.ui.chat.ChatEventTimestampUpdater;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.chat.SLMissedVoiceCallEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLVoiceUpgradeEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatSystemMessageEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatSessionMarkEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLEnableRLVOfferEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextBoxDialog;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatScriptDialog;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatPermissionRequestEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatOnlineOfflineEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatLureRequestEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatLureRequestedEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatLureEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatGroupInvitationSentEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatGroupInvitationEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatFriendshipResultEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatFriendshipOfferedEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedByYouEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedByGroupNoticeEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatBalanceChangedEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import java.util.Date;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import javax.annotation.Nonnull;
import java.util.UUID;
import android.view.View$OnLongClickListener;

public abstract class SLChatEvent implements View$OnLongClickListener
{
    public static final int CHAT_AUDIBLE_BARELY = 0;
    public static final int CHAT_AUDIBLE_FULLY = 1;
    public static final int CHAT_AUDIBLE_NOT = -1;
    public static final int CHAT_SOURCE_AGENT = 1;
    public static final int CHAT_SOURCE_OBJECT = 2;
    public static final int CHAT_SOURCE_SYSTEM = 0;
    public static final int CHAT_SOURCE_UNKNOWN = 3;
    public static final int CHAT_TYPE_DEBUG_MSG = 6;
    public static final int CHAT_TYPE_NORMAL = 1;
    public static final int CHAT_TYPE_OWNER = 8;
    public static final int CHAT_TYPE_REGION = 7;
    public static final int CHAT_TYPE_SHOUT = 2;
    public static final int CHAT_TYPE_START = 4;
    public static final int CHAT_TYPE_STOP = 5;
    public static final int CHAT_TYPE_WHISPER = 0;
    public static final int IM_BUSY_AUTO_RESPONSE = 20;
    public static final int IM_CONSOLE_AND_CHAT_HISTORY = 21;
    public static final int IM_FRIENDSHIP_ACCEPTED = 39;
    public static final int IM_FRIENDSHIP_DECLINED = 40;
    public static final int IM_FRIENDSHIP_OFFERED = 38;
    public static final int IM_FROM_TASK = 19;
    public static final int IM_FROM_TASK_AS_ALERT = 31;
    public static final int IM_GODLIKE_LURE_USER = 25;
    public static final int IM_GOTO_URL = 28;
    public static final int IM_GROUP_ELECTION_DEPRECATED = 27;
    public static final int IM_GROUP_INVITATION = 3;
    public static final int IM_GROUP_INVITATION_ACCEPT = 35;
    public static final int IM_GROUP_INVITATION_DECLINE = 36;
    public static final int IM_GROUP_MESSAGE_DEPRECATED = 8;
    public static final int IM_GROUP_NOTICE = 32;
    public static final int IM_GROUP_NOTICE_INVENTORY_ACCEPTED = 33;
    public static final int IM_GROUP_NOTICE_INVENTORY_DECLINED = 34;
    public static final int IM_GROUP_NOTICE_REQUESTED = 37;
    public static final int IM_GROUP_VOTE = 7;
    public static final int IM_INVENTORY_ACCEPTED = 5;
    public static final int IM_INVENTORY_DECLINED = 6;
    public static final int IM_INVENTORY_OFFERED = 4;
    public static final int IM_LURE_ACCEPTED = 23;
    public static final int IM_LURE_DECLINED = 24;
    public static final int IM_LURE_USER = 22;
    public static final int IM_MESSAGEBOX = 1;
    public static final int IM_MESSAGEBOX_COUNTDOWN = 2;
    public static final int IM_NEW_USER_DEFAULT = 12;
    public static final int IM_NOTHING_SPECIAL = 0;
    public static final int IM_SESSION_CONFERENCE_START = 16;
    public static final int IM_SESSION_GROUP_START = 15;
    public static final int IM_SESSION_INVITE = 13;
    public static final int IM_SESSION_LEAVE = 18;
    public static final int IM_SESSION_P2P_INVITE = 14;
    public static final int IM_SESSION_SEND = 17;
    public static final int IM_TASK_INVENTORY_ACCEPTED = 10;
    public static final int IM_TASK_INVENTORY_DECLINED = 11;
    public static final int IM_TASK_INVENTORY_OFFERED = 9;
    public static final int IM_TELEPORT_REQUEST = 26;
    public static final int IM_TYPING_START = 41;
    public static final int IM_TYPING_STOP = 42;
    @Nonnull
    protected final UUID agentUUID;
    @Nullable
    protected final ChatMessage dbMessage;
    private final boolean isOffline;
    private final Date originalTimestamp;
    @Nonnull
    protected final ChatMessageSource source;
    private final Date timestamp;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues() {
        if (SLChatEvent.-com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues != null) {
            return SLChatEvent.-com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues = new int[ChatMessageType.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.BalanceChanged.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.EnableRLVOffer.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.FriendshipOffered.ordinal()] = 3;
                        try {
                            -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.FriendshipResult.ordinal()] = 4;
                            try {
                                -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.GroupInvitation.ordinal()] = 5;
                                try {
                                    -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.GroupInvitationSent.ordinal()] = 6;
                                    try {
                                        -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.InventoryItemOffered.ordinal()] = 7;
                                        try {
                                            -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.InventoryItemOfferedByGroupNotice.ordinal()] = 8;
                                            try {
                                                -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.InventoryItemOfferedByYou.ordinal()] = 9;
                                                try {
                                                    -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.Lure.ordinal()] = 10;
                                                    try {
                                                        -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.LureRequest.ordinal()] = 11;
                                                        try {
                                                            -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.LureRequested.ordinal()] = 12;
                                                            try {
                                                                -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.MissedVoiceCall.ordinal()] = 13;
                                                                try {
                                                                    -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.PermissionRequest.ordinal()] = 14;
                                                                    try {
                                                                        -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.ScriptDialog.ordinal()] = 15;
                                                                        try {
                                                                            -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.SessionMark.ordinal()] = 16;
                                                                            try {
                                                                                -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.SystemMessage.ordinal()] = 17;
                                                                                try {
                                                                                    -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.Text.ordinal()] = 18;
                                                                                    try {
                                                                                        -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.TextBoxDialog.ordinal()] = 19;
                                                                                        try {
                                                                                            -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.VoiceUpgrade.ordinal()] = 20;
                                                                                            try {
                                                                                                -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.WentOffline.ordinal()] = 21;
                                                                                                try {
                                                                                                    -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues[ChatMessageType.WentOnline.ordinal()] = 22;
                                                                                                    return -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues = -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues;
                                                                                                }
                                                                                                catch (final NoSuchFieldError noSuchFieldError) {}
                                                                                            }
                                                                                            catch (final NoSuchFieldError noSuchFieldError2) {}
                                                                                        }
                                                                                        catch (final NoSuchFieldError noSuchFieldError3) {}
                                                                                    }
                                                                                    catch (final NoSuchFieldError noSuchFieldError4) {}
                                                                                }
                                                                                catch (final NoSuchFieldError noSuchFieldError5) {}
                                                                            }
                                                                            catch (final NoSuchFieldError noSuchFieldError6) {}
                                                                        }
                                                                        catch (final NoSuchFieldError noSuchFieldError7) {}
                                                                    }
                                                                    catch (final NoSuchFieldError noSuchFieldError8) {}
                                                                }
                                                                catch (final NoSuchFieldError noSuchFieldError9) {}
                                                            }
                                                            catch (final NoSuchFieldError noSuchFieldError10) {}
                                                        }
                                                        catch (final NoSuchFieldError noSuchFieldError11) {}
                                                    }
                                                    catch (final NoSuchFieldError noSuchFieldError12) {}
                                                }
                                                catch (final NoSuchFieldError noSuchFieldError13) {}
                                            }
                                            catch (final NoSuchFieldError noSuchFieldError14) {}
                                        }
                                        catch (final NoSuchFieldError noSuchFieldError15) {}
                                    }
                                    catch (final NoSuchFieldError noSuchFieldError16) {}
                                }
                                catch (final NoSuchFieldError noSuchFieldError17) {}
                            }
                            catch (final NoSuchFieldError noSuchFieldError18) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError19) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError20) {}
                }
                catch (final NoSuchFieldError noSuchFieldError21) {}
            }
            catch (final NoSuchFieldError noSuchFieldError22) {
                continue;
            }
            break;
        }
    }
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues() {
        if (SLChatEvent.-com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues != null) {
            return SLChatEvent.-com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues = new int[ChatMessageSource.ChatMessageSourceType.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues[ChatMessageSource.ChatMessageSourceType.Group.ordinal()] = 25;
                try {
                    -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues[ChatMessageSource.ChatMessageSourceType.Object.ordinal()] = 1;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues[ChatMessageSource.ChatMessageSourceType.System.ordinal()] = 26;
                        try {
                            -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues[ChatMessageSource.ChatMessageSourceType.Unknown.ordinal()] = 27;
                            try {
                                -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues[ChatMessageSource.ChatMessageSourceType.User.ordinal()] = 2;
                                return -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues = -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues;
                            }
                            catch (final NoSuchFieldError noSuchFieldError) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError2) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError3) {}
                }
                catch (final NoSuchFieldError noSuchFieldError4) {}
            }
            catch (final NoSuchFieldError noSuchFieldError5) {
                continue;
            }
            break;
        }
    }
    
    public SLChatEvent(@Nonnull final ChatMessage dbMessage, @Nonnull final UUID agentUUID) {
        this.dbMessage = dbMessage;
        this.timestamp = dbMessage.getTimestamp();
        this.isOffline = dbMessage.getIsOffline();
        this.originalTimestamp = dbMessage.getOrigTimestamp();
        this.source = ChatMessageSource.loadFrom(dbMessage);
        this.agentUUID = agentUUID;
    }
    
    public SLChatEvent(@Nullable final ImprovedInstantMessage improvedInstantMessage, @Nonnull final UUID agentUUID, @Nonnull final ChatMessageSource source) {
        this.timestamp = new Date();
        this.source = source;
        this.agentUUID = agentUUID;
        if (improvedInstantMessage != null) {
            if (improvedInstantMessage.MessageBlock_Field.Offline != 0 && improvedInstantMessage.MessageBlock_Field.Dialog != 9) {
                this.isOffline = true;
                this.originalTimestamp = new Date(improvedInstantMessage.MessageBlock_Field.Timestamp * 1000L);
            }
            else {
                this.isOffline = false;
                this.originalTimestamp = this.timestamp;
            }
        }
        else {
            this.isOffline = false;
            this.originalTimestamp = this.timestamp;
        }
        this.dbMessage = null;
    }
    
    public SLChatEvent(@Nonnull final ChatMessageSource source, @Nonnull final UUID agentUUID) {
        this.timestamp = new Date();
        this.originalTimestamp = this.timestamp;
        this.isOffline = false;
        this.source = source;
        this.agentUUID = agentUUID;
        this.dbMessage = null;
    }
    
    public static ChatEventViewHolder createViewHolder(final LayoutInflater layoutInflater, final int n, final ViewGroup viewGroup, final RecyclerView.Adapter adapter) {
        final ChatMessageViewType chatMessageViewType = ChatMessageViewType.VALUES[n];
        return chatMessageViewType.createViewHolder(layoutInflater.inflate(chatMessageViewType.getResourceId(), viewGroup, false), adapter);
    }
    
    @Nullable
    public static SLChatEvent loadFromDatabaseObject(@Nullable final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        if (chatMessage == null) {
            return null;
        }
        switch (-getcom-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues()[ChatMessageType.VALUES[chatMessage.getMessageType()].ordinal()]) {
            default: {
                return null;
            }
            case 18: {
                return new SLChatTextEvent(chatMessage, uuid);
            }
            case 1: {
                return new SLChatBalanceChangedEvent(chatMessage, uuid);
            }
            case 7: {
                return new SLChatInventoryItemOfferedEvent(chatMessage, uuid);
            }
            case 8: {
                return new SLChatInventoryItemOfferedByGroupNoticeEvent(chatMessage, uuid);
            }
            case 9: {
                return new SLChatInventoryItemOfferedByYouEvent(chatMessage, uuid);
            }
            case 3: {
                return new SLChatFriendshipOfferedEvent(chatMessage, uuid);
            }
            case 4: {
                return new SLChatFriendshipResultEvent(chatMessage, uuid);
            }
            case 5: {
                return new SLChatGroupInvitationEvent(chatMessage, uuid);
            }
            case 6: {
                return new SLChatGroupInvitationSentEvent(chatMessage, uuid);
            }
            case 10: {
                return new SLChatLureEvent(chatMessage, uuid);
            }
            case 12: {
                return new SLChatLureRequestedEvent(chatMessage, uuid);
            }
            case 11: {
                return new SLChatLureRequestEvent(chatMessage, uuid);
            }
            case 22: {
                return new SLChatOnlineOfflineEvent(chatMessage, uuid, true);
            }
            case 21: {
                return new SLChatOnlineOfflineEvent(chatMessage, uuid, false);
            }
            case 14: {
                return new SLChatPermissionRequestEvent(chatMessage, uuid);
            }
            case 15: {
                return new SLChatScriptDialog(chatMessage, uuid);
            }
            case 19: {
                return new SLChatTextBoxDialog(chatMessage, uuid);
            }
            case 2: {
                return new SLEnableRLVOfferEvent(chatMessage, uuid);
            }
            case 16: {
                return new SLChatSessionMarkEvent(chatMessage, uuid);
            }
            case 17: {
                return new SLChatSystemMessageEvent(chatMessage, uuid);
            }
            case 20: {
                return new SLVoiceUpgradeEvent(chatMessage, uuid);
            }
            case 13: {
                return new SLMissedVoiceCallEvent(chatMessage, uuid);
            }
        }
    }
    
    public void bindViewHolder(ChatEventViewHolder chatEventViewHolder, final UserManager userManager, @Nullable ChatEventTimestampUpdater textView) {
        final ChatterPicView chatSourceIcon = chatEventViewHolder.chatSourceIcon;
        Label_0589: {
            if (this.source.getSourceType() != ChatMessageSource.ChatMessageSourceType.User) {
                break Label_0589;
            }
            int equal = Objects.equal(this.source.getSourceUUID(), this.agentUUID) ? 1 : 0;
        Label_0201_Outer:
            while (true) {
                ChatterPicView chatSourceIconRight = chatSourceIcon;
                if (chatEventViewHolder.chatSourceIconRight != null) {
                    chatSourceIconRight = chatSourceIcon;
                    if (equal != 0) {
                        chatSourceIconRight = chatEventViewHolder.chatSourceIconRight;
                    }
                }
                if (chatEventViewHolder.chatSourceIcon != null && chatEventViewHolder.chatSourceIcon != chatSourceIconRight) {
                    chatEventViewHolder.chatSourceIcon.setChatterID(null, null);
                    chatEventViewHolder.chatSourceIcon.setDefaultIcon(-1, false);
                    chatEventViewHolder.chatSourceIcon.setForceIcon(-1);
                    chatEventViewHolder.chatSourceIcon.setVisibility(8);
                    chatEventViewHolder.chatSourceIcon.setAttachedMessageSource(null);
                }
                if (chatEventViewHolder.chatSourceIconRight != null && chatEventViewHolder.chatSourceIconRight != chatSourceIconRight) {
                    chatEventViewHolder.chatSourceIconRight.setChatterID(null, null);
                    chatEventViewHolder.chatSourceIconRight.setDefaultIcon(-1, false);
                    chatEventViewHolder.chatSourceIconRight.setForceIcon(-1);
                    chatEventViewHolder.chatSourceIconRight.setVisibility(8);
                    chatEventViewHolder.chatSourceIconRight.setAttachedMessageSource(null);
                }
            Label_0232_Outer:
                while (true) {
                Label_0353_Outer:
                    while (true) {
                        Label_0278: {
                            if (chatEventViewHolder.bubbleView == null) {
                                break Label_0278;
                            }
                            if (equal == 0) {
                                break Label_0232_Outer;
                            }
                            chatEventViewHolder.bubbleView.setBackgroundResource(2130837824);
                            final TypedValue typedValue = new TypedValue();
                            final Resources$Theme theme = chatEventViewHolder.bubbleView.getContext().getTheme();
                            if (equal == 0) {
                                break Label_0353_Outer;
                            }
                            final int n = 2130772059;
                            theme.resolveAttribute(n, typedValue, true);
                            final Drawable background = chatEventViewHolder.bubbleView.getBackground();
                            if (background != null) {
                                background.setColorFilter(typedValue.data, PorterDuff$Mode.MULTIPLY);
                            }
                            chatEventViewHolder.bubbleView.setOnLongClickListener((View$OnLongClickListener)this);
                        }
                        Label_0649: {
                            if (chatSourceIconRight != null) {
                                switch (-getcom-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues()[this.source.getSourceType().ordinal()]) {
                                    default: {
                                        chatSourceIconRight.setChatterID(null, null);
                                        chatSourceIconRight.setDefaultIcon(-1, false);
                                        chatSourceIconRight.setForceIcon(-1);
                                        chatSourceIconRight.setVisibility(8);
                                        chatSourceIconRight.setAttachedMessageSource(null);
                                        break;
                                    }
                                    case 1: {
                                        break Label_0649;
                                    }
                                    case 2: {
                                        break Label_0649;
                                    }
                                }
                            }
                        Label_0398_Outer:
                            while (true) {
                                final TextView timestampView = chatEventViewHolder.timestampView;
                                Label_0762: {
                                    if (timestampView != null) {
                                        if (!GlobalOptions.getInstance().getShowTimestamps()) {
                                            break Label_0762;
                                        }
                                        chatEventViewHolder.setupTimestampUpdate(timestampView.getContext(), this.timestamp.getTime());
                                        if (textView != null) {
                                            textView.addViewHolder(chatEventViewHolder);
                                        }
                                    }
                                Label_0475_Outer:
                                    while (true) {
                                        textView = (ChatEventTimestampUpdater)chatEventViewHolder.textView;
                                        if (textView == null) {
                                            return;
                                        }
                                        final String sourceName = this.source.getSourceName(userManager);
                                        final String text = this.getText(((TextView)textView).getContext(), userManager);
                                        chatEventViewHolder = (ChatEventViewHolder)new SpannableStringBuilder();
                                        Label_0783: {
                                            if (Strings.isNullOrEmpty(sourceName)) {
                                                break Label_0783;
                                            }
                                            ((SpannableStringBuilder)chatEventViewHolder).append((CharSequence)sourceName);
                                        Label_0502_Outer:
                                            while (true) {
                                                Label_0482: {
                                                    if (Strings.isNullOrEmpty(text)) {
                                                        break Label_0482;
                                                    }
                                                    if (!this.isActionMessage(userManager)) {
                                                        break Label_0502_Outer;
                                                    }
                                                    ((SpannableStringBuilder)chatEventViewHolder).append((CharSequence)" ");
                                                    ((SpannableStringBuilder)chatEventViewHolder).append((CharSequence)text);
                                                }
                                                ((SpannableStringBuilder)chatEventViewHolder).setSpan((Object)new StyleSpan(1), 0, sourceName.length(), 33);
                                            Block_23_Outer:
                                                while (true) {
                                                    if (this.isOffline) {
                                                        final String string = " (sent at " + DateFormat.getDateTimeInstance(2, 2).format(this.originalTimestamp) + ")";
                                                        ((SpannableStringBuilder)chatEventViewHolder).append((CharSequence)string);
                                                        ((SpannableStringBuilder)chatEventViewHolder).setSpan((Object)new StyleSpan(2), ((SpannableStringBuilder)chatEventViewHolder).length() - string.length(), ((SpannableStringBuilder)chatEventViewHolder).length(), 33);
                                                    }
                                                    try {
                                                        ((TextView)textView).setText((CharSequence)chatEventViewHolder, TextView$BufferType.SPANNABLE);
                                                        return;
                                                        Label_0732: {
                                                            chatSourceIconRight.setChatterID(null, null);
                                                        }
                                                        chatSourceIconRight.setDefaultIcon(-1, false);
                                                        chatSourceIconRight.setForceIcon(-1);
                                                        chatSourceIconRight.setVisibility(8);
                                                        continue Label_0398_Outer;
                                                        chatSourceIconRight.setChatterID(null, null);
                                                        chatSourceIconRight.setForceIcon(2130837702);
                                                        chatSourceIconRight.setVisibility(0);
                                                        chatSourceIconRight.setAttachedMessageSource(this.source);
                                                        continue Label_0398_Outer;
                                                        while (true) {
                                                            ((SpannableStringBuilder)chatEventViewHolder).append((CharSequence)text);
                                                            continue Block_23_Outer;
                                                            chatEventViewHolder.bubbleView.setBackgroundResource(2130837823);
                                                            continue Label_0232_Outer;
                                                            ((SpannableStringBuilder)chatEventViewHolder).append((CharSequence)": ");
                                                            continue Label_0502_Outer;
                                                            timestampView.setVisibility(8);
                                                            continue Label_0475_Outer;
                                                            iftrue(Label_0502:)(Strings.isNullOrEmpty(text));
                                                            continue;
                                                        }
                                                        equal = 0;
                                                        continue Label_0201_Outer;
                                                        final int n = 2130772058;
                                                        continue Label_0353_Outer;
                                                        final UUID sourceUUID = this.source.getSourceUUID();
                                                        iftrue(Label_0732:)(sourceUUID == null);
                                                        Debug.Printf("chatterBindPic: name %s, sourceUUID %s", this.source.getSourceName(userManager), sourceUUID.toString());
                                                        chatSourceIconRight.setChatterID(ChatterID.getUserChatterID(userManager.getUserID(), sourceUUID), this.source.getSourceName(userManager));
                                                        chatSourceIconRight.setVisibility(0);
                                                        chatSourceIconRight.setAttachedMessageSource(this.source);
                                                        continue Label_0398_Outer;
                                                    }
                                                    catch (final Exception ex) {
                                                        ((TextView)textView).setText((CharSequence)((SpannableStringBuilder)chatEventViewHolder).toString());
                                                    }
                                                    break;
                                                }
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                    break;
                }
                break;
            }
        }
    }
    
    @Nonnull
    public UUID getAgentUUID() {
        return this.agentUUID;
    }
    
    @Nonnull
    public ChatMessage getDatabaseObject() {
        ChatMessage dbMessage;
        if ((dbMessage = this.dbMessage) == null) {
            dbMessage = new ChatMessage();
        }
        this.serializeToDatabaseObject(dbMessage);
        return dbMessage;
    }
    
    @Nonnull
    protected abstract ChatMessageType getMessageType();
    
    public CharSequence getPlainTextMessage(final Context context, final UserManager userManager, final boolean b) {
        return this.getPlainTextMessage(context, userManager, b, ": ", " ");
    }
    
    public CharSequence getPlainTextMessage(final Context context, final UserManager userManager, final boolean b, final String s, final String s2) {
        String sourceName;
        if (b && (this.isActionMessage(userManager) ^ true)) {
            sourceName = null;
        }
        else {
            sourceName = this.source.getSourceName(userManager);
        }
        final String text = this.getText(context, userManager);
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (!Strings.isNullOrEmpty(sourceName)) {
            spannableStringBuilder.append((CharSequence)sourceName);
            if (!Strings.isNullOrEmpty(text)) {
                if (this.isActionMessage(userManager)) {
                    spannableStringBuilder.append((CharSequence)s2);
                }
                else {
                    spannableStringBuilder.append((CharSequence)s);
                }
                spannableStringBuilder.append((CharSequence)text);
            }
            spannableStringBuilder.setSpan((Object)new StyleSpan(1), 0, sourceName.length(), 33);
            return (CharSequence)spannableStringBuilder;
        }
        return text;
    }
    
    @Nonnull
    public ChatMessageSource getSource() {
        return this.source;
    }
    
    protected abstract String getText(final Context p0, @Nonnull final UserManager p1);
    
    public Date getTimestamp() {
        return this.timestamp;
    }
    
    public abstract ChatMessageViewType getViewType();
    
    protected abstract boolean isActionMessage(@Nonnull final UserManager p0);
    
    public boolean isObjectPopup() {
        return false;
    }
    
    protected void notifyEventUpdated(final UserManager userManager) {
        if (this.dbMessage != null) {
            userManager.getChatterList().getActiveChattersManager().notifyChatEventUpdated(this);
        }
    }
    
    public final boolean onLongClick(final View view) {
        final Context context = view.getContext();
        if (context != null) {
            final PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.inflate(2131886082);
            popupMenu.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener)new _$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls$6(this, context));
            popupMenu.show();
            return true;
        }
        return false;
    }
    
    public boolean opensNewChatter() {
        return true;
    }
    
    protected void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        chatMessage.setTimestamp(this.timestamp);
        chatMessage.setIsOffline(this.isOffline);
        chatMessage.setOrigTimestamp(this.originalTimestamp);
        chatMessage.setMessageType(this.getMessageType().ordinal());
        chatMessage.setViewType(this.getViewType().ordinal());
        this.source.serializeTo(chatMessage);
    }
    
    public enum ChatMessageType
    {
        BalanceChanged("BalanceChanged", 1), 
        EnableRLVOffer("EnableRLVOffer", 17), 
        FriendshipOffered("FriendshipOffered", 5), 
        FriendshipResult("FriendshipResult", 6), 
        GroupInvitation("GroupInvitation", 7), 
        GroupInvitationSent("GroupInvitationSent", 8), 
        InventoryItemOffered("InventoryItemOffered", 2), 
        InventoryItemOfferedByGroupNotice("InventoryItemOfferedByGroupNotice", 3), 
        InventoryItemOfferedByYou("InventoryItemOfferedByYou", 4), 
        Lure("Lure", 9), 
        LureRequest("LureRequest", 11), 
        LureRequested("LureRequested", 10), 
        MissedVoiceCall("MissedVoiceCall", 21), 
        PermissionRequest("PermissionRequest", 14), 
        ScriptDialog("ScriptDialog", 15), 
        SessionMark("SessionMark", 18), 
        SystemMessage("SystemMessage", 19), 
        Text("Text", 0), 
        TextBoxDialog("TextBoxDialog", 16);
        
        public static final ChatMessageType[] VALUES;
        
        VoiceUpgrade("VoiceUpgrade", 20), 
        WentOffline("WentOffline", 13), 
        WentOnline("WentOnline", 12);
        
        static {
            VALUES = values();
        }
        
        private ChatMessageType(final String name, final int ordinal) {
        }
    }
    
    public enum ChatMessageViewType implements Factory
    {
        public static final ChatMessageViewType[] VALUES;
        
        VIEW_TYPE_DIALOG("VIEW_TYPE_DIALOG", 2, 2130968615, false, (Factory)new _$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls$2()), 
        VIEW_TYPE_NORMAL("VIEW_TYPE_NORMAL", 0, 2130968614, false, (Factory)new _$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls()), 
        VIEW_TYPE_PLAIN("VIEW_TYPE_PLAIN", 5, 2130968616, false, (Factory)new _$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls$5()), 
        VIEW_TYPE_SESSION_MARK("VIEW_TYPE_SESSION_MARK", 4, 2130968617, false, (Factory)new _$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls$4()), 
        VIEW_TYPE_TEXTBOX("VIEW_TYPE_TEXTBOX", 3, 2130968618, true, (Factory)new _$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls$3()), 
        VIEW_TYPE_YESNO("VIEW_TYPE_YESNO", 1, 2130968619, false, (Factory)new _$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls$1());
        
        private final boolean alwaysInflate;
        private final int resourceId;
        private final Factory viewHolderFactory;
        
        static {
            VALUES = values();
        }
        
        private ChatMessageViewType(final String name, final int ordinal, final int resourceId, final boolean alwaysInflate, final Factory viewHolderFactory) {
            this.resourceId = resourceId;
            this.alwaysInflate = alwaysInflate;
            this.viewHolderFactory = viewHolderFactory;
        }
        
        @Override
        public final ChatEventViewHolder createViewHolder(final View view, final Adapter adapter) {
            return this.viewHolderFactory.createViewHolder(view, adapter);
        }
        
        public final boolean getAlwaysInflate() {
            return this.alwaysInflate;
        }
        
        public final int getResourceId() {
            return this.resourceId;
        }
    }
}
