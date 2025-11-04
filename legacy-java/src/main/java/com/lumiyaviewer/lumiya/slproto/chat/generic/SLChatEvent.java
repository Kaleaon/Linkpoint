package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.content.ClipData;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.ClipboardManager;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatBalanceChangedEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatFriendshipOfferedEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatFriendshipResultEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatGroupInvitationEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatGroupInvitationSentEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedByGroupNoticeEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedByYouEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatLureEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatLureRequestEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatLureRequestedEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatOnlineOfflineEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatPermissionRequestEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatScriptDialog;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatSessionMarkEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatSystemMessageEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextBoxDialog;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLEnableRLVOfferEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLMissedVoiceCallEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLVoiceUpgradeEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatEventTimestampUpdater;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public abstract class SLChatEvent implements View.OnLongClickListener {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f72x73d6fc70 = null;

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f73xbd9aa5ff = null;
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

    /* loaded from: classes.dex */
    public enum ChatMessageType {
        Text,
        BalanceChanged,
        InventoryItemOffered,
        InventoryItemOfferedByGroupNotice,
        InventoryItemOfferedByYou,
        FriendshipOffered,
        FriendshipResult,
        GroupInvitation,
        GroupInvitationSent,
        Lure,
        LureRequested,
        LureRequest,
        WentOnline,
        WentOffline,
        PermissionRequest,
        ScriptDialog,
        TextBoxDialog,
        EnableRLVOffer,
        SessionMark,
        SystemMessage,
        VoiceUpgrade,
        MissedVoiceCall;

        public static final ChatMessageType[] VALUES = valuesCustom();

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static ChatMessageType[] valuesCustom() {
            return values();
        }
    }

    /* loaded from: classes.dex */
    public enum ChatMessageViewType implements ChatEventViewHolder.Factory {
        VIEW_TYPE_NORMAL(R.layout.chat_message, false, new ChatEventViewHolder.Factory() { // from class: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls
            private final /* synthetic */ ChatEventViewHolder $m$0(View view, RecyclerView.Adapter adapter) {
                return SLChatEvent.ChatMessageViewType.m161x24dc691(view, adapter);
            }

            @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder.Factory
            public final ChatEventViewHolder createViewHolder(View view, RecyclerView.Adapter adapter) {
                return $m$0(view, adapter);
            }
        }),
        VIEW_TYPE_YESNO(R.layout.chat_message_yesno, false, new ChatEventViewHolder.Factory() { // from class: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.1
            private final /* synthetic */ ChatEventViewHolder $m$0(View view, RecyclerView.Adapter adapter) {
                return SLChatEvent.ChatMessageViewType.m162x24dc692(view, adapter);
            }

            @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder.Factory
            public final ChatEventViewHolder createViewHolder(View view, RecyclerView.Adapter adapter) {
                return $m$0(view, adapter);
            }
        }),
        VIEW_TYPE_DIALOG(R.layout.chat_message_dialog, false, new ChatEventViewHolder.Factory() { // from class: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.2
            private final /* synthetic */ ChatEventViewHolder $m$0(View view, RecyclerView.Adapter adapter) {
                return SLChatEvent.ChatMessageViewType.m163x24dc693(view, adapter);
            }

            @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder.Factory
            public final ChatEventViewHolder createViewHolder(View view, RecyclerView.Adapter adapter) {
                return $m$0(view, adapter);
            }
        }),
        VIEW_TYPE_TEXTBOX(R.layout.chat_message_textbox, true, new ChatEventViewHolder.Factory() { // from class: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.3
            private final /* synthetic */ ChatEventViewHolder $m$0(View view, RecyclerView.Adapter adapter) {
                return SLChatEvent.ChatMessageViewType.m164x24dc694(view, adapter);
            }

            @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder.Factory
            public final ChatEventViewHolder createViewHolder(View view, RecyclerView.Adapter adapter) {
                return $m$0(view, adapter);
            }
        }),
        VIEW_TYPE_SESSION_MARK(R.layout.chat_message_session_mark, false, new ChatEventViewHolder.Factory() { // from class: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.4
            private final /* synthetic */ ChatEventViewHolder $m$0(View view, RecyclerView.Adapter adapter) {
                return SLChatEvent.ChatMessageViewType.m165x24dc695(view, adapter);
            }

            @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder.Factory
            public final ChatEventViewHolder createViewHolder(View view, RecyclerView.Adapter adapter) {
                return $m$0(view, adapter);
            }
        }),
        VIEW_TYPE_PLAIN(R.layout.chat_message_plain, false, new ChatEventViewHolder.Factory() { // from class: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.5
            private final /* synthetic */ ChatEventViewHolder $m$0(View view, RecyclerView.Adapter adapter) {
                return SLChatEvent.ChatMessageViewType.m166x24dc696(view, adapter);
            }

            @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder.Factory
            public final ChatEventViewHolder createViewHolder(View view, RecyclerView.Adapter adapter) {
                return $m$0(view, adapter);
            }
        });

        public static final ChatMessageViewType[] VALUES = valuesCustom();
        private final boolean alwaysInflate;
        private final int resourceId;
        private final ChatEventViewHolder.Factory viewHolderFactory;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: -com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType-mthref-0, reason: not valid java name */
        public static /* synthetic */ ChatEventViewHolder m161x24dc691(View view, RecyclerView.Adapter adapter) {
            return new ChatEventViewHolder(view, adapter);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: -com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType-mthref-1, reason: not valid java name */
        public static /* synthetic */ ChatEventViewHolder m162x24dc692(View view, RecyclerView.Adapter adapter) {
            return new ChatYesNoEventViewHolder(view, adapter);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: -com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType-mthref-2, reason: not valid java name */
        public static /* synthetic */ ChatEventViewHolder m163x24dc693(View view, RecyclerView.Adapter adapter) {
            return new ChatScriptDialogViewHolder(view, adapter);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: -com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType-mthref-3, reason: not valid java name */
        public static /* synthetic */ ChatEventViewHolder m164x24dc694(View view, RecyclerView.Adapter adapter) {
            return new ChatTextBoxViewHolder(view, adapter);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: -com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType-mthref-4, reason: not valid java name */
        public static /* synthetic */ ChatEventViewHolder m165x24dc695(View view, RecyclerView.Adapter adapter) {
            return new ChatEventViewHolder(view, adapter);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: -com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType-mthref-5, reason: not valid java name */
        public static /* synthetic */ ChatEventViewHolder m166x24dc696(View view, RecyclerView.Adapter adapter) {
            return new ChatEventViewHolder(view, adapter);
        }

        ChatMessageViewType(int i, boolean z, ChatEventViewHolder.Factory factory) {
            this.resourceId = i;
            this.alwaysInflate = z;
            this.viewHolderFactory = factory;
        }

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static ChatMessageViewType[] valuesCustom() {
            return values();
        }

        @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder.Factory
        public final ChatEventViewHolder createViewHolder(View view, RecyclerView.Adapter adapter) {
            return this.viewHolderFactory.createViewHolder(view, adapter);
        }

        public final boolean getAlwaysInflate() {
            return this.alwaysInflate;
        }

        public final int getResourceId() {
            return this.resourceId;
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-chat-generic-SLChatEvent$ChatMessageTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m158x80b3934c() {
        if (f72x73d6fc70 != null) {
            return f72x73d6fc70;
        }
        int[] iArr = new int[ChatMessageType.valuesCustom().length];
        try {
            iArr[ChatMessageType.BalanceChanged.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ChatMessageType.EnableRLVOffer.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ChatMessageType.FriendshipOffered.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ChatMessageType.FriendshipResult.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ChatMessageType.GroupInvitation.ordinal()] = 5;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ChatMessageType.GroupInvitationSent.ordinal()] = 6;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ChatMessageType.InventoryItemOffered.ordinal()] = 7;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[ChatMessageType.InventoryItemOfferedByGroupNotice.ordinal()] = 8;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[ChatMessageType.InventoryItemOfferedByYou.ordinal()] = 9;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[ChatMessageType.Lure.ordinal()] = 10;
        } catch (NoSuchFieldError e10) {
        }
        try {
            iArr[ChatMessageType.LureRequest.ordinal()] = 11;
        } catch (NoSuchFieldError e11) {
        }
        try {
            iArr[ChatMessageType.LureRequested.ordinal()] = 12;
        } catch (NoSuchFieldError e12) {
        }
        try {
            iArr[ChatMessageType.MissedVoiceCall.ordinal()] = 13;
        } catch (NoSuchFieldError e13) {
        }
        try {
            iArr[ChatMessageType.PermissionRequest.ordinal()] = 14;
        } catch (NoSuchFieldError e14) {
        }
        try {
            iArr[ChatMessageType.ScriptDialog.ordinal()] = 15;
        } catch (NoSuchFieldError e15) {
        }
        try {
            iArr[ChatMessageType.SessionMark.ordinal()] = 16;
        } catch (NoSuchFieldError e16) {
        }
        try {
            iArr[ChatMessageType.SystemMessage.ordinal()] = 17;
        } catch (NoSuchFieldError e17) {
        }
        try {
            iArr[ChatMessageType.Text.ordinal()] = 18;
        } catch (NoSuchFieldError e18) {
        }
        try {
            iArr[ChatMessageType.TextBoxDialog.ordinal()] = 19;
        } catch (NoSuchFieldError e19) {
        }
        try {
            iArr[ChatMessageType.VoiceUpgrade.ordinal()] = 20;
        } catch (NoSuchFieldError e20) {
        }
        try {
            iArr[ChatMessageType.WentOffline.ordinal()] = 21;
        } catch (NoSuchFieldError e21) {
        }
        try {
            iArr[ChatMessageType.WentOnline.ordinal()] = 22;
        } catch (NoSuchFieldError e22) {
        }
        f72x73d6fc70 = iArr;
        return iArr;
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m159x84fb14a3() {
        if (f73xbd9aa5ff != null) {
            return f73xbd9aa5ff;
        }
        int[] iArr = new int[ChatMessageSource.ChatMessageSourceType.valuesCustom().length];
        try {
            iArr[ChatMessageSource.ChatMessageSourceType.Group.ordinal()] = 25;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ChatMessageSource.ChatMessageSourceType.Object.ordinal()] = 1;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ChatMessageSource.ChatMessageSourceType.System.ordinal()] = 26;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ChatMessageSource.ChatMessageSourceType.Unknown.ordinal()] = 27;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ChatMessageSource.ChatMessageSourceType.User.ordinal()] = 2;
        } catch (NoSuchFieldError e5) {
        }
        f73xbd9aa5ff = iArr;
        return iArr;
    }

    public SLChatEvent(@Nonnull ChatMessage chatMessage, @Nonnull UUID uuid) {
        this.dbMessage = chatMessage;
        this.timestamp = chatMessage.getTimestamp();
        this.isOffline = chatMessage.getIsOffline().booleanValue();
        this.originalTimestamp = chatMessage.getOrigTimestamp();
        this.source = ChatMessageSource.loadFrom(chatMessage);
        this.agentUUID = uuid;
    }

    public SLChatEvent(@Nullable ImprovedInstantMessage improvedInstantMessage, @Nonnull UUID uuid, @Nonnull ChatMessageSource chatMessageSource) {
        this.timestamp = new Date();
        this.source = chatMessageSource;
        this.agentUUID = uuid;
        if (improvedInstantMessage == null) {
            this.isOffline = false;
            this.originalTimestamp = this.timestamp;
        } else if (improvedInstantMessage.MessageBlock_Field.Offline == 0 || improvedInstantMessage.MessageBlock_Field.Dialog == 9) {
            this.isOffline = false;
            this.originalTimestamp = this.timestamp;
        } else {
            this.isOffline = true;
            this.originalTimestamp = new Date(improvedInstantMessage.MessageBlock_Field.Timestamp * 1000);
        }
        this.dbMessage = null;
    }

    public SLChatEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid) {
        this.timestamp = new Date();
        this.originalTimestamp = this.timestamp;
        this.isOffline = false;
        this.source = chatMessageSource;
        this.agentUUID = uuid;
        this.dbMessage = null;
    }

    public static ChatEventViewHolder createViewHolder(LayoutInflater layoutInflater, int i, ViewGroup viewGroup, RecyclerView.Adapter adapter) {
        ChatMessageViewType chatMessageViewType = ChatMessageViewType.VALUES[i];
        return chatMessageViewType.createViewHolder(layoutInflater.inflate(chatMessageViewType.getResourceId(), viewGroup, false), adapter);
    }

    @Nullable
    public static SLChatEvent loadFromDatabaseObject(@Nullable ChatMessage chatMessage, @Nonnull UUID uuid) {
        if (chatMessage == null) {
            return null;
        }
        switch (m158x80b3934c()[ChatMessageType.VALUES[chatMessage.getMessageType()].ordinal()]) {
            case 1:
                return new SLChatBalanceChangedEvent(chatMessage, uuid);
            case 2:
                return new SLEnableRLVOfferEvent(chatMessage, uuid);
            case 3:
                return new SLChatFriendshipOfferedEvent(chatMessage, uuid);
            case 4:
                return new SLChatFriendshipResultEvent(chatMessage, uuid);
            case 5:
                return new SLChatGroupInvitationEvent(chatMessage, uuid);
            case 6:
                return new SLChatGroupInvitationSentEvent(chatMessage, uuid);
            case 7:
                return new SLChatInventoryItemOfferedEvent(chatMessage, uuid);
            case 8:
                return new SLChatInventoryItemOfferedByGroupNoticeEvent(chatMessage, uuid);
            case 9:
                return new SLChatInventoryItemOfferedByYouEvent(chatMessage, uuid);
            case 10:
                return new SLChatLureEvent(chatMessage, uuid);
            case 11:
                return new SLChatLureRequestEvent(chatMessage, uuid);
            case 12:
                return new SLChatLureRequestedEvent(chatMessage, uuid);
            case 13:
                return new SLMissedVoiceCallEvent(chatMessage, uuid);
            case 14:
                return new SLChatPermissionRequestEvent(chatMessage, uuid);
            case 15:
                return new SLChatScriptDialog(chatMessage, uuid);
            case 16:
                return new SLChatSessionMarkEvent(chatMessage, uuid);
            case 17:
                return new SLChatSystemMessageEvent(chatMessage, uuid);
            case 18:
                return new SLChatTextEvent(chatMessage, uuid);
            case 19:
                return new SLChatTextBoxDialog(chatMessage, uuid);
            case 20:
                return new SLVoiceUpgradeEvent(chatMessage, uuid);
            case 21:
                return new SLChatOnlineOfflineEvent(chatMessage, uuid, false);
            case 22:
                return new SLChatOnlineOfflineEvent(chatMessage, uuid, true);
            default:
                return null;
        }
    }

    public void bindViewHolder(ChatEventViewHolder chatEventViewHolder, UserManager userManager, @Nullable ChatEventTimestampUpdater chatEventTimestampUpdater) {
        ChatterPicView chatterPicView = chatEventViewHolder.chatSourceIcon;
        boolean equal = this.source.getSourceType() == ChatMessageSource.ChatMessageSourceType.User ? Objects.equal(this.source.getSourceUUID(), this.agentUUID) : false;
        if (chatEventViewHolder.chatSourceIconRight != null && equal) {
            chatterPicView = chatEventViewHolder.chatSourceIconRight;
        }
        if (chatEventViewHolder.chatSourceIcon != null && chatEventViewHolder.chatSourceIcon != chatterPicView) {
            chatEventViewHolder.chatSourceIcon.setChatterID(null, null);
            chatEventViewHolder.chatSourceIcon.setDefaultIcon(-1, false);
            chatEventViewHolder.chatSourceIcon.setForceIcon(-1);
            chatEventViewHolder.chatSourceIcon.setVisibility(8);
            chatEventViewHolder.chatSourceIcon.setAttachedMessageSource(null);
        }
        if (chatEventViewHolder.chatSourceIconRight != null && chatEventViewHolder.chatSourceIconRight != chatterPicView) {
            chatEventViewHolder.chatSourceIconRight.setChatterID(null, null);
            chatEventViewHolder.chatSourceIconRight.setDefaultIcon(-1, false);
            chatEventViewHolder.chatSourceIconRight.setForceIcon(-1);
            chatEventViewHolder.chatSourceIconRight.setVisibility(8);
            chatEventViewHolder.chatSourceIconRight.setAttachedMessageSource(null);
        }
        if (chatEventViewHolder.bubbleView != null) {
            if (equal) {
                chatEventViewHolder.bubbleView.setBackgroundResource(R.drawable.msg_bubble_right);
            } else {
                chatEventViewHolder.bubbleView.setBackgroundResource(R.drawable.msg_bubble_left);
            }
            TypedValue typedValue = new TypedValue();
            chatEventViewHolder.bubbleView.getContext().getTheme().resolveAttribute(equal ? R.attr.chatBubbleMyBackground : R.attr.chatBubbleBackground, typedValue, true);
            Drawable background = chatEventViewHolder.bubbleView.getBackground();
            if (background != null) {
                background.setColorFilter(typedValue.data, PorterDuff.Mode.MULTIPLY);
            }
            chatEventViewHolder.bubbleView.setOnLongClickListener(this);
        }
        if (chatterPicView != null) {
            switch (m159x84fb14a3()[this.source.getSourceType().ordinal()]) {
                case 1:
                    chatterPicView.setChatterID(null, null);
                    chatterPicView.setForceIcon(R.drawable.inv_object);
                    chatterPicView.setVisibility(0);
                    chatterPicView.setAttachedMessageSource(this.source);
                    break;
                case 2:
                    UUID sourceUUID = this.source.getSourceUUID();
                    if (sourceUUID == null) {
                        chatterPicView.setChatterID(null, null);
                        chatterPicView.setDefaultIcon(-1, false);
                        chatterPicView.setForceIcon(-1);
                        chatterPicView.setVisibility(8);
                        break;
                    } else {
                        Debug.Printf("chatterBindPic: name %s, sourceUUID %s", this.source.getSourceName(userManager), sourceUUID.toString());
                        chatterPicView.setChatterID(ChatterID.getUserChatterID(userManager.getUserID(), sourceUUID), this.source.getSourceName(userManager));
                        chatterPicView.setVisibility(0);
                        chatterPicView.setAttachedMessageSource(this.source);
                        break;
                    }
                default:
                    chatterPicView.setChatterID(null, null);
                    chatterPicView.setDefaultIcon(-1, false);
                    chatterPicView.setForceIcon(-1);
                    chatterPicView.setVisibility(8);
                    chatterPicView.setAttachedMessageSource(null);
                    break;
            }
        }
        TextView textView = chatEventViewHolder.timestampView;
        if (textView != null) {
            if (GlobalOptions.getInstance().getShowTimestamps()) {
                chatEventViewHolder.setupTimestampUpdate(textView.getContext(), this.timestamp.getTime());
                if (chatEventTimestampUpdater != null) {
                    chatEventTimestampUpdater.addViewHolder(chatEventViewHolder);
                }
            } else {
                textView.setVisibility(8);
            }
        }
        TextView textView2 = chatEventViewHolder.textView;
        if (textView2 != null) {
            String sourceName = this.source.getSourceName(userManager);
            String text = getText(textView2.getContext(), userManager);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            if (!Strings.isNullOrEmpty(sourceName)) {
                spannableStringBuilder.append((CharSequence) sourceName);
                if (!Strings.isNullOrEmpty(text)) {
                    if (isActionMessage(userManager)) {
                        spannableStringBuilder.append((CharSequence) " ");
                    } else {
                        spannableStringBuilder.append((CharSequence) ": ");
                    }
                    spannableStringBuilder.append((CharSequence) text);
                }
                spannableStringBuilder.setSpan(new StyleSpan(1), 0, sourceName.length(), 33);
            } else if (!Strings.isNullOrEmpty(text)) {
                spannableStringBuilder.append((CharSequence) text);
            }
            if (this.isOffline) {
                String str = " (sent at " + DateFormat.getDateTimeInstance(2, 2).format(this.originalTimestamp) + ")";
                spannableStringBuilder.append((CharSequence) str);
                spannableStringBuilder.setSpan(new StyleSpan(2), spannableStringBuilder.length() - str.length(), spannableStringBuilder.length(), 33);
            }
            try {
                textView2.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
            } catch (Exception e) {
                textView2.setText(spannableStringBuilder.toString());
            }
        }
    }

    @Nonnull
    public UUID getAgentUUID() {
        return this.agentUUID;
    }

    @Nonnull
    public ChatMessage getDatabaseObject() {
        ChatMessage chatMessage = this.dbMessage;
        if (chatMessage == null) {
            chatMessage = new ChatMessage();
        }
        serializeToDatabaseObject(chatMessage);
        return chatMessage;
    }

    @Nonnull
    protected abstract ChatMessageType getMessageType();

    public CharSequence getPlainTextMessage(Context context, UserManager userManager, boolean z) {
        return getPlainTextMessage(context, userManager, z, ": ", " ");
    }

    public CharSequence getPlainTextMessage(Context context, UserManager userManager, boolean z, String str, String str2) {
        String sourceName = (z && (isActionMessage(userManager) ^ true)) ? null : this.source.getSourceName(userManager);
        String text = getText(context, userManager);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (Strings.isNullOrEmpty(sourceName)) {
            return text;
        }
        spannableStringBuilder.append((CharSequence) sourceName);
        if (!Strings.isNullOrEmpty(text)) {
            if (isActionMessage(userManager)) {
                spannableStringBuilder.append((CharSequence) str2);
            } else {
                spannableStringBuilder.append((CharSequence) str);
            }
            spannableStringBuilder.append((CharSequence) text);
        }
        spannableStringBuilder.setSpan(new StyleSpan(1), 0, sourceName.length(), 33);
        return spannableStringBuilder;
    }

    @Nonnull
    public ChatMessageSource getSource() {
        return this.source;
    }

    protected abstract String getText(Context context, @Nonnull UserManager userManager);

    public Date getTimestamp() {
        return this.timestamp;
    }

    public abstract ChatMessageViewType getViewType();

    protected abstract boolean isActionMessage(@Nonnull UserManager userManager);

    public boolean isObjectPopup() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent_21084, reason: not valid java name */
    public /* synthetic */ boolean m160xda67b1b8(Context context, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_copy_message_text /* 2131755771 */:
                UserManager userManager = UserManager.getUserManager(this.agentUUID);
                if (userManager != null) {
                    CharSequence plainTextMessage = getPlainTextMessage(context, userManager, true);
                    if (Build.VERSION.SDK_INT < 11) {
                        ((ClipboardManager) context.getSystemService("clipboard")).setText(plainTextMessage);
                    } else {
                        ((android.content.ClipboardManager) context.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("Message", plainTextMessage));
                    }
                    Toast.makeText(context, "Message copied to clipboard", 0).show();
                }
                return true;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyEventUpdated(UserManager userManager) {
        if (this.dbMessage != null) {
            userManager.getChatterList().getActiveChattersManager().notifyChatEventUpdated(this);
        }
    }

    @Override // android.view.View.OnLongClickListener
    public final boolean onLongClick(View view) {
        final Context context = view.getContext();
        if (context == null) {
            return false;
        }
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.chat_messages_context_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { // from class: com.lumiyaviewer.lumiya.slproto.chat.generic.-$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls.6
            private final /* synthetic */ boolean $m$0(MenuItem menuItem) {
                return ((SLChatEvent) this).m160xda67b1b8((Context) context, menuItem);
            }

            @Override // android.support.v7.widget.PopupMenu.OnMenuItemClickListener
            public final boolean onMenuItemClick(MenuItem menuItem) {
                return $m$0(menuItem);
            }
        });
        popupMenu.show();
        return true;
    }

    public boolean opensNewChatter() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        chatMessage.setTimestamp(this.timestamp);
        chatMessage.setIsOffline(Boolean.valueOf(this.isOffline));
        chatMessage.setOrigTimestamp(this.originalTimestamp);
        chatMessage.setMessageType(getMessageType().ordinal());
        chatMessage.setViewType(getViewType().ordinal());
        this.source.serializeTo(chatMessage);
    }
}
