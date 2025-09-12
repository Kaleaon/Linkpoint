// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.PopupMenu;
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
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatEventTimestampUpdater;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.chat.generic:
//            ChatEventViewHolder, ChatYesNoEventViewHolder, ChatScriptDialogViewHolder, ChatTextBoxViewHolder

public abstract class SLChatEvent
    implements android.view.View.OnLongClickListener
{
    public static final class ChatMessageType extends Enum
    {

        private static final ChatMessageType $VALUES[];
        public static final ChatMessageType BalanceChanged;
        public static final ChatMessageType EnableRLVOffer;
        public static final ChatMessageType FriendshipOffered;
        public static final ChatMessageType FriendshipResult;
        public static final ChatMessageType GroupInvitation;
        public static final ChatMessageType GroupInvitationSent;
        public static final ChatMessageType InventoryItemOffered;
        public static final ChatMessageType InventoryItemOfferedByGroupNotice;
        public static final ChatMessageType InventoryItemOfferedByYou;
        public static final ChatMessageType Lure;
        public static final ChatMessageType LureRequest;
        public static final ChatMessageType LureRequested;
        public static final ChatMessageType MissedVoiceCall;
        public static final ChatMessageType PermissionRequest;
        public static final ChatMessageType ScriptDialog;
        public static final ChatMessageType SessionMark;
        public static final ChatMessageType SystemMessage;
        public static final ChatMessageType Text;
        public static final ChatMessageType TextBoxDialog;
        public static final ChatMessageType VALUES[] = values();
        public static final ChatMessageType VoiceUpgrade;
        public static final ChatMessageType WentOffline;
        public static final ChatMessageType WentOnline;

        public static ChatMessageType valueOf(String s)
        {
            return (ChatMessageType)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/chat/generic/SLChatEvent$ChatMessageType, s);
        }

        public static ChatMessageType[] values()
        {
            return $VALUES;
        }

        static 
        {
            Text = new ChatMessageType("Text", 0);
            BalanceChanged = new ChatMessageType("BalanceChanged", 1);
            InventoryItemOffered = new ChatMessageType("InventoryItemOffered", 2);
            InventoryItemOfferedByGroupNotice = new ChatMessageType("InventoryItemOfferedByGroupNotice", 3);
            InventoryItemOfferedByYou = new ChatMessageType("InventoryItemOfferedByYou", 4);
            FriendshipOffered = new ChatMessageType("FriendshipOffered", 5);
            FriendshipResult = new ChatMessageType("FriendshipResult", 6);
            GroupInvitation = new ChatMessageType("GroupInvitation", 7);
            GroupInvitationSent = new ChatMessageType("GroupInvitationSent", 8);
            Lure = new ChatMessageType("Lure", 9);
            LureRequested = new ChatMessageType("LureRequested", 10);
            LureRequest = new ChatMessageType("LureRequest", 11);
            WentOnline = new ChatMessageType("WentOnline", 12);
            WentOffline = new ChatMessageType("WentOffline", 13);
            PermissionRequest = new ChatMessageType("PermissionRequest", 14);
            ScriptDialog = new ChatMessageType("ScriptDialog", 15);
            TextBoxDialog = new ChatMessageType("TextBoxDialog", 16);
            EnableRLVOffer = new ChatMessageType("EnableRLVOffer", 17);
            SessionMark = new ChatMessageType("SessionMark", 18);
            SystemMessage = new ChatMessageType("SystemMessage", 19);
            VoiceUpgrade = new ChatMessageType("VoiceUpgrade", 20);
            MissedVoiceCall = new ChatMessageType("MissedVoiceCall", 21);
            $VALUES = (new ChatMessageType[] {
                Text, BalanceChanged, InventoryItemOffered, InventoryItemOfferedByGroupNotice, InventoryItemOfferedByYou, FriendshipOffered, FriendshipResult, GroupInvitation, GroupInvitationSent, Lure, 
                LureRequested, LureRequest, WentOnline, WentOffline, PermissionRequest, ScriptDialog, TextBoxDialog, EnableRLVOffer, SessionMark, SystemMessage, 
                VoiceUpgrade, MissedVoiceCall
            });
        }

        private ChatMessageType(String s, int i)
        {
            super(s, i);
        }
    }

    public static final class ChatMessageViewType extends Enum
        implements ChatEventViewHolder.Factory
    {

        private static final ChatMessageViewType $VALUES[];
        public static final ChatMessageViewType VALUES[] = values();
        public static final ChatMessageViewType VIEW_TYPE_DIALOG;
        public static final ChatMessageViewType VIEW_TYPE_NORMAL;
        public static final ChatMessageViewType VIEW_TYPE_PLAIN;
        public static final ChatMessageViewType VIEW_TYPE_SESSION_MARK;
        public static final ChatMessageViewType VIEW_TYPE_TEXTBOX;
        public static final ChatMessageViewType VIEW_TYPE_YESNO;
        private final boolean alwaysInflate;
        private final int resourceId;
        private final ChatEventViewHolder.Factory viewHolderFactory;

        static ChatEventViewHolder _2D_com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType_2D_mthref_2D_0(View view, android.support.v7.widget.RecyclerView.Adapter adapter)
        {
            return new ChatEventViewHolder(view, adapter);
        }

        static ChatEventViewHolder _2D_com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType_2D_mthref_2D_1(View view, android.support.v7.widget.RecyclerView.Adapter adapter)
        {
            return new ChatYesNoEventViewHolder(view, adapter);
        }

        static ChatEventViewHolder _2D_com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType_2D_mthref_2D_2(View view, android.support.v7.widget.RecyclerView.Adapter adapter)
        {
            return new ChatScriptDialogViewHolder(view, adapter);
        }

        static ChatEventViewHolder _2D_com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType_2D_mthref_2D_3(View view, android.support.v7.widget.RecyclerView.Adapter adapter)
        {
            return new ChatTextBoxViewHolder(view, adapter);
        }

        static ChatEventViewHolder _2D_com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType_2D_mthref_2D_4(View view, android.support.v7.widget.RecyclerView.Adapter adapter)
        {
            return new ChatEventViewHolder(view, adapter);
        }

        static ChatEventViewHolder _2D_com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType_2D_mthref_2D_5(View view, android.support.v7.widget.RecyclerView.Adapter adapter)
        {
            return new ChatEventViewHolder(view, adapter);
        }

        public static ChatMessageViewType valueOf(String s)
        {
            return (ChatMessageViewType)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/chat/generic/SLChatEvent$ChatMessageViewType, s);
        }

        public static ChatMessageViewType[] values()
        {
            return $VALUES;
        }

        public final ChatEventViewHolder createViewHolder(View view, android.support.v7.widget.RecyclerView.Adapter adapter)
        {
            return viewHolderFactory.createViewHolder(view, adapter);
        }

        public final boolean getAlwaysInflate()
        {
            return alwaysInflate;
        }

        public final int getResourceId()
        {
            return resourceId;
        }

        static 
        {
            VIEW_TYPE_NORMAL = new ChatMessageViewType("VIEW_TYPE_NORMAL", 0, 0x7f040026, false, new _2D_.Lambda._cls2ey8fl8aDXV9bCTwS1nc4b06kls());
            VIEW_TYPE_YESNO = new ChatMessageViewType("VIEW_TYPE_YESNO", 1, 0x7f04002b, false, new _2D_.Lambda._cls2ey8fl8aDXV9bCTwS1nc4b06kls._cls1());
            VIEW_TYPE_DIALOG = new ChatMessageViewType("VIEW_TYPE_DIALOG", 2, 0x7f040027, false, new _2D_.Lambda._cls2ey8fl8aDXV9bCTwS1nc4b06kls._cls2());
            VIEW_TYPE_TEXTBOX = new ChatMessageViewType("VIEW_TYPE_TEXTBOX", 3, 0x7f04002a, true, new _2D_.Lambda._cls2ey8fl8aDXV9bCTwS1nc4b06kls._cls3());
            VIEW_TYPE_SESSION_MARK = new ChatMessageViewType("VIEW_TYPE_SESSION_MARK", 4, 0x7f040029, false, new _2D_.Lambda._cls2ey8fl8aDXV9bCTwS1nc4b06kls._cls4());
            VIEW_TYPE_PLAIN = new ChatMessageViewType("VIEW_TYPE_PLAIN", 5, 0x7f040028, false, new _2D_.Lambda._cls2ey8fl8aDXV9bCTwS1nc4b06kls._cls5());
            $VALUES = (new ChatMessageViewType[] {
                VIEW_TYPE_NORMAL, VIEW_TYPE_YESNO, VIEW_TYPE_DIALOG, VIEW_TYPE_TEXTBOX, VIEW_TYPE_SESSION_MARK, VIEW_TYPE_PLAIN
            });
        }

        private ChatMessageViewType(String s, int i, int j, boolean flag, ChatEventViewHolder.Factory factory)
        {
            super(s, i);
            resourceId = j;
            alwaysInflate = flag;
            viewHolderFactory = factory;
        }
    }


    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_chat_2D_generic_2D_SLChatEvent$ChatMessageTypeSwitchesValues[];
    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_chatsrc_2D_ChatMessageSource$ChatMessageSourceTypeSwitchesValues[];
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
    protected final UUID agentUUID;
    protected final ChatMessage dbMessage;
    private final boolean isOffline;
    private final Date originalTimestamp;
    protected final ChatMessageSource source;
    private final Date timestamp;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_chat_2D_generic_2D_SLChatEvent$ChatMessageTypeSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_chat_2D_generic_2D_SLChatEvent$ChatMessageTypeSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_chat_2D_generic_2D_SLChatEvent$ChatMessageTypeSwitchesValues;
        }
        int ai[] = new int[ChatMessageType.values().length];
        try
        {
            ai[ChatMessageType.BalanceChanged.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror21) { }
        try
        {
            ai[ChatMessageType.EnableRLVOffer.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror20) { }
        try
        {
            ai[ChatMessageType.FriendshipOffered.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror19) { }
        try
        {
            ai[ChatMessageType.FriendshipResult.ordinal()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror18) { }
        try
        {
            ai[ChatMessageType.GroupInvitation.ordinal()] = 5;
        }
        catch (NoSuchFieldError nosuchfielderror17) { }
        try
        {
            ai[ChatMessageType.GroupInvitationSent.ordinal()] = 6;
        }
        catch (NoSuchFieldError nosuchfielderror16) { }
        try
        {
            ai[ChatMessageType.InventoryItemOffered.ordinal()] = 7;
        }
        catch (NoSuchFieldError nosuchfielderror15) { }
        try
        {
            ai[ChatMessageType.InventoryItemOfferedByGroupNotice.ordinal()] = 8;
        }
        catch (NoSuchFieldError nosuchfielderror14) { }
        try
        {
            ai[ChatMessageType.InventoryItemOfferedByYou.ordinal()] = 9;
        }
        catch (NoSuchFieldError nosuchfielderror13) { }
        try
        {
            ai[ChatMessageType.Lure.ordinal()] = 10;
        }
        catch (NoSuchFieldError nosuchfielderror12) { }
        try
        {
            ai[ChatMessageType.LureRequest.ordinal()] = 11;
        }
        catch (NoSuchFieldError nosuchfielderror11) { }
        try
        {
            ai[ChatMessageType.LureRequested.ordinal()] = 12;
        }
        catch (NoSuchFieldError nosuchfielderror10) { }
        try
        {
            ai[ChatMessageType.MissedVoiceCall.ordinal()] = 13;
        }
        catch (NoSuchFieldError nosuchfielderror9) { }
        try
        {
            ai[ChatMessageType.PermissionRequest.ordinal()] = 14;
        }
        catch (NoSuchFieldError nosuchfielderror8) { }
        try
        {
            ai[ChatMessageType.ScriptDialog.ordinal()] = 15;
        }
        catch (NoSuchFieldError nosuchfielderror7) { }
        try
        {
            ai[ChatMessageType.SessionMark.ordinal()] = 16;
        }
        catch (NoSuchFieldError nosuchfielderror6) { }
        try
        {
            ai[ChatMessageType.SystemMessage.ordinal()] = 17;
        }
        catch (NoSuchFieldError nosuchfielderror5) { }
        try
        {
            ai[ChatMessageType.Text.ordinal()] = 18;
        }
        catch (NoSuchFieldError nosuchfielderror4) { }
        try
        {
            ai[ChatMessageType.TextBoxDialog.ordinal()] = 19;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[ChatMessageType.VoiceUpgrade.ordinal()] = 20;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[ChatMessageType.WentOffline.ordinal()] = 21;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[ChatMessageType.WentOnline.ordinal()] = 22;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_chat_2D_generic_2D_SLChatEvent$ChatMessageTypeSwitchesValues = ai;
        return ai;
    }

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_chatsrc_2D_ChatMessageSource$ChatMessageSourceTypeSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_chatsrc_2D_ChatMessageSource$ChatMessageSourceTypeSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_chatsrc_2D_ChatMessageSource$ChatMessageSourceTypeSwitchesValues;
        }
        int ai[] = new int[com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.values().length];
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.Group.ordinal()] = 25;
        }
        catch (NoSuchFieldError nosuchfielderror4) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.Object.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.System.ordinal()] = 26;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.Unknown.ordinal()] = 27;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.User.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_chatsrc_2D_ChatMessageSource$ChatMessageSourceTypeSwitchesValues = ai;
        return ai;
    }

    public SLChatEvent(ChatMessage chatmessage, UUID uuid)
    {
        dbMessage = chatmessage;
        timestamp = chatmessage.getTimestamp();
        isOffline = chatmessage.getIsOffline().booleanValue();
        originalTimestamp = chatmessage.getOrigTimestamp();
        source = ChatMessageSource.loadFrom(chatmessage);
        agentUUID = uuid;
    }

    public SLChatEvent(ImprovedInstantMessage improvedinstantmessage, UUID uuid, ChatMessageSource chatmessagesource)
    {
        timestamp = new Date();
        source = chatmessagesource;
        agentUUID = uuid;
        if (improvedinstantmessage != null)
        {
            if (improvedinstantmessage.MessageBlock_Field.Offline != 0 && improvedinstantmessage.MessageBlock_Field.Dialog != 9)
            {
                isOffline = true;
                originalTimestamp = new Date((long)improvedinstantmessage.MessageBlock_Field.Timestamp * 1000L);
            } else
            {
                isOffline = false;
                originalTimestamp = timestamp;
            }
        } else
        {
            isOffline = false;
            originalTimestamp = timestamp;
        }
        dbMessage = null;
    }

    public SLChatEvent(ChatMessageSource chatmessagesource, UUID uuid)
    {
        timestamp = new Date();
        originalTimestamp = timestamp;
        isOffline = false;
        source = chatmessagesource;
        agentUUID = uuid;
        dbMessage = null;
    }

    public static ChatEventViewHolder createViewHolder(LayoutInflater layoutinflater, int i, ViewGroup viewgroup, android.support.v7.widget.RecyclerView.Adapter adapter)
    {
        ChatMessageViewType chatmessageviewtype = ChatMessageViewType.VALUES[i];
        return chatmessageviewtype.createViewHolder(layoutinflater.inflate(chatmessageviewtype.getResourceId(), viewgroup, false), adapter);
    }

    public static SLChatEvent loadFromDatabaseObject(ChatMessage chatmessage, UUID uuid)
    {
        if (chatmessage != null)
        {
            ChatMessageType chatmessagetype = ChatMessageType.VALUES[chatmessage.getMessageType()];
            switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_chat_2D_generic_2D_SLChatEvent$ChatMessageTypeSwitchesValues()[chatmessagetype.ordinal()])
            {
            default:
                return null;

            case 18: // '\022'
                return new SLChatTextEvent(chatmessage, uuid);

            case 1: // '\001'
                return new SLChatBalanceChangedEvent(chatmessage, uuid);

            case 7: // '\007'
                return new SLChatInventoryItemOfferedEvent(chatmessage, uuid);

            case 8: // '\b'
                return new SLChatInventoryItemOfferedByGroupNoticeEvent(chatmessage, uuid);

            case 9: // '\t'
                return new SLChatInventoryItemOfferedByYouEvent(chatmessage, uuid);

            case 3: // '\003'
                return new SLChatFriendshipOfferedEvent(chatmessage, uuid);

            case 4: // '\004'
                return new SLChatFriendshipResultEvent(chatmessage, uuid);

            case 5: // '\005'
                return new SLChatGroupInvitationEvent(chatmessage, uuid);

            case 6: // '\006'
                return new SLChatGroupInvitationSentEvent(chatmessage, uuid);

            case 10: // '\n'
                return new SLChatLureEvent(chatmessage, uuid);

            case 12: // '\f'
                return new SLChatLureRequestedEvent(chatmessage, uuid);

            case 11: // '\013'
                return new SLChatLureRequestEvent(chatmessage, uuid);

            case 22: // '\026'
                return new SLChatOnlineOfflineEvent(chatmessage, uuid, true);

            case 21: // '\025'
                return new SLChatOnlineOfflineEvent(chatmessage, uuid, false);

            case 14: // '\016'
                return new SLChatPermissionRequestEvent(chatmessage, uuid);

            case 15: // '\017'
                return new SLChatScriptDialog(chatmessage, uuid);

            case 19: // '\023'
                return new SLChatTextBoxDialog(chatmessage, uuid);

            case 2: // '\002'
                return new SLEnableRLVOfferEvent(chatmessage, uuid);

            case 16: // '\020'
                return new SLChatSessionMarkEvent(chatmessage, uuid);

            case 17: // '\021'
                return new SLChatSystemMessageEvent(chatmessage, uuid);

            case 20: // '\024'
                return new SLVoiceUpgradeEvent(chatmessage, uuid);

            case 13: // '\r'
                return new SLMissedVoiceCallEvent(chatmessage, uuid);
            }
        } else
        {
            return null;
        }
    }

    public void bindViewHolder(ChatEventViewHolder chateventviewholder, UserManager usermanager, ChatEventTimestampUpdater chateventtimestampupdater)
    {
        Object obj;
        ChatterPicView chatterpicview = chateventviewholder.chatSourceIcon;
        boolean flag;
        if (source.getSourceType() == com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.User)
        {
            flag = Objects.equal(source.getSourceUUID(), agentUUID);
        } else
        {
            flag = false;
        }
        obj = chatterpicview;
        if (chateventviewholder.chatSourceIconRight != null)
        {
            obj = chatterpicview;
            if (flag)
            {
                obj = chateventviewholder.chatSourceIconRight;
            }
        }
        if (chateventviewholder.chatSourceIcon != null && chateventviewholder.chatSourceIcon != obj)
        {
            chateventviewholder.chatSourceIcon.setChatterID(null, null);
            chateventviewholder.chatSourceIcon.setDefaultIcon(-1, false);
            chateventviewholder.chatSourceIcon.setForceIcon(-1);
            chateventviewholder.chatSourceIcon.setVisibility(8);
            chateventviewholder.chatSourceIcon.setAttachedMessageSource(null);
        }
        if (chateventviewholder.chatSourceIconRight != null && chateventviewholder.chatSourceIconRight != obj)
        {
            chateventviewholder.chatSourceIconRight.setChatterID(null, null);
            chateventviewholder.chatSourceIconRight.setDefaultIcon(-1, false);
            chateventviewholder.chatSourceIconRight.setForceIcon(-1);
            chateventviewholder.chatSourceIconRight.setVisibility(8);
            chateventviewholder.chatSourceIconRight.setAttachedMessageSource(null);
        }
        if (chateventviewholder.bubbleView != null)
        {
            TypedValue typedvalue;
            Object obj2;
            int i;
            if (flag)
            {
                chateventviewholder.bubbleView.setBackgroundResource(0x7f020140);
            } else
            {
                chateventviewholder.bubbleView.setBackgroundResource(0x7f02013f);
            }
            typedvalue = new TypedValue();
            obj2 = chateventviewholder.bubbleView.getContext().getTheme();
            if (flag)
            {
                i = 0x7f01005b;
            } else
            {
                i = 0x7f01005a;
            }
            ((android.content.res.Resources.Theme) (obj2)).resolveAttribute(i, typedvalue, true);
            obj2 = chateventviewholder.bubbleView.getBackground();
            if (obj2 != null)
            {
                ((Drawable) (obj2)).setColorFilter(typedvalue.data, android.graphics.PorterDuff.Mode.MULTIPLY);
            }
            chateventviewholder.bubbleView.setOnLongClickListener(this);
        }
        if (obj == null) goto _L2; else goto _L1
_L1:
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_chatsrc_2D_ChatMessageSource$ChatMessageSourceTypeSwitchesValues()[source.getSourceType().ordinal()];
        JVM INSTR tableswitch 1 2: default 320
    //                   1 616
    //                   2 649;
           goto _L3 _L4 _L5
_L5:
        break MISSING_BLOCK_LABEL_649;
_L3:
        ((ChatterPicView) (obj)).setChatterID(null, null);
        ((ChatterPicView) (obj)).setDefaultIcon(-1, false);
        ((ChatterPicView) (obj)).setForceIcon(-1);
        ((ChatterPicView) (obj)).setVisibility(8);
        ((ChatterPicView) (obj)).setAttachedMessageSource(null);
_L2:
        obj = chateventviewholder.timestampView;
        Object obj1;
        if (obj != null)
        {
            if (GlobalOptions.getInstance().getShowTimestamps())
            {
                chateventviewholder.setupTimestampUpdate(((TextView) (obj)).getContext(), timestamp.getTime());
                if (chateventtimestampupdater != null)
                {
                    chateventtimestampupdater.addViewHolder(chateventviewholder);
                }
            } else
            {
                ((TextView) (obj)).setVisibility(8);
            }
        }
        chateventviewholder = chateventviewholder.textView;
        if (chateventviewholder == null)
        {
            break MISSING_BLOCK_LABEL_588;
        }
        obj = source.getSourceName(usermanager);
        obj1 = getText(chateventviewholder.getContext(), usermanager);
        chateventtimestampupdater = new SpannableStringBuilder();
        if (!Strings.isNullOrEmpty(((String) (obj))))
        {
            chateventtimestampupdater.append(((CharSequence) (obj)));
            if (!Strings.isNullOrEmpty(((String) (obj1))))
            {
                if (isActionMessage(usermanager))
                {
                    chateventtimestampupdater.append(" ");
                } else
                {
                    chateventtimestampupdater.append(": ");
                }
                chateventtimestampupdater.append(((CharSequence) (obj1)));
            }
            chateventtimestampupdater.setSpan(new StyleSpan(1), 0, ((String) (obj)).length(), 33);
        } else
        if (!Strings.isNullOrEmpty(((String) (obj1))))
        {
            chateventtimestampupdater.append(((CharSequence) (obj1)));
        }
        if (isOffline)
        {
            usermanager = (new StringBuilder()).append(" (sent at ").append(DateFormat.getDateTimeInstance(2, 2).format(originalTimestamp)).append(")").toString();
            chateventtimestampupdater.append(usermanager);
            chateventtimestampupdater.setSpan(new StyleSpan(2), chateventtimestampupdater.length() - usermanager.length(), chateventtimestampupdater.length(), 33);
        }
        chateventviewholder.setText(chateventtimestampupdater, android.widget.TextView.BufferType.SPANNABLE);
        return;
_L4:
        ((ChatterPicView) (obj)).setChatterID(null, null);
        ((ChatterPicView) (obj)).setForceIcon(0x7f0200c6);
        ((ChatterPicView) (obj)).setVisibility(0);
        ((ChatterPicView) (obj)).setAttachedMessageSource(source);
          goto _L2
        obj1 = source.getSourceUUID();
        if (obj1 != null)
        {
            Debug.Printf("chatterBindPic: name %s, sourceUUID %s", new Object[] {
                source.getSourceName(usermanager), ((UUID) (obj1)).toString()
            });
            ((ChatterPicView) (obj)).setChatterID(ChatterID.getUserChatterID(usermanager.getUserID(), ((UUID) (obj1))), source.getSourceName(usermanager));
            ((ChatterPicView) (obj)).setVisibility(0);
            ((ChatterPicView) (obj)).setAttachedMessageSource(source);
        } else
        {
            ((ChatterPicView) (obj)).setChatterID(null, null);
            ((ChatterPicView) (obj)).setDefaultIcon(-1, false);
            ((ChatterPicView) (obj)).setForceIcon(-1);
            ((ChatterPicView) (obj)).setVisibility(8);
        }
          goto _L2
        usermanager;
        chateventviewholder.setText(chateventtimestampupdater.toString());
        return;
    }

    public UUID getAgentUUID()
    {
        return agentUUID;
    }

    public ChatMessage getDatabaseObject()
    {
        ChatMessage chatmessage1 = dbMessage;
        ChatMessage chatmessage = chatmessage1;
        if (chatmessage1 == null)
        {
            chatmessage = new ChatMessage();
        }
        serializeToDatabaseObject(chatmessage);
        return chatmessage;
    }

    protected abstract ChatMessageType getMessageType();

    public CharSequence getPlainTextMessage(Context context, UserManager usermanager, boolean flag)
    {
        return getPlainTextMessage(context, usermanager, flag, ": ", " ");
    }

    public CharSequence getPlainTextMessage(Context context, UserManager usermanager, boolean flag, String s, String s1)
    {
        String s2;
        SpannableStringBuilder spannablestringbuilder;
        if (flag && isActionMessage(usermanager) ^ true)
        {
            s2 = null;
        } else
        {
            s2 = source.getSourceName(usermanager);
        }
        context = getText(context, usermanager);
        spannablestringbuilder = new SpannableStringBuilder();
        if (!Strings.isNullOrEmpty(s2))
        {
            spannablestringbuilder.append(s2);
            if (!Strings.isNullOrEmpty(context))
            {
                if (isActionMessage(usermanager))
                {
                    spannablestringbuilder.append(s1);
                } else
                {
                    spannablestringbuilder.append(s);
                }
                spannablestringbuilder.append(context);
            }
            spannablestringbuilder.setSpan(new StyleSpan(1), 0, s2.length(), 33);
            return spannablestringbuilder;
        } else
        {
            return context;
        }
    }

    public ChatMessageSource getSource()
    {
        return source;
    }

    protected abstract String getText(Context context, UserManager usermanager);

    public Date getTimestamp()
    {
        return timestamp;
    }

    public abstract ChatMessageViewType getViewType();

    protected abstract boolean isActionMessage(UserManager usermanager);

    public boolean isObjectPopup()
    {
        return false;
    }

    boolean lambda$_2D_com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent_21084(Context context, MenuItem menuitem)
    {
        switch (menuitem.getItemId())
        {
        default:
            return false;

        case 2131755771: 
            menuitem = UserManager.getUserManager(agentUUID);
            break;
        }
        if (menuitem != null)
        {
            menuitem = getPlainTextMessage(context, menuitem, true);
            if (android.os.Build.VERSION.SDK_INT < 11)
            {
                ((ClipboardManager)context.getSystemService("clipboard")).setText(menuitem);
            } else
            {
                ((android.content.ClipboardManager)context.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("Message", menuitem));
            }
            Toast.makeText(context, "Message copied to clipboard", 0).show();
        }
        return true;
    }

    protected void notifyEventUpdated(UserManager usermanager)
    {
        if (dbMessage != null)
        {
            usermanager.getChatterList().getActiveChattersManager().notifyChatEventUpdated(this);
        }
    }

    public final boolean onLongClick(View view)
    {
        Context context = view.getContext();
        if (context != null)
        {
            view = new PopupMenu(context, view);
            view.inflate(0x7f120002);
            view.setOnMenuItemClickListener(new _2D_.Lambda._cls2ey8fl8aDXV9bCTwS1nc4b06kls._cls6(this, context));
            view.show();
            return true;
        } else
        {
            return false;
        }
    }

    public boolean opensNewChatter()
    {
        return true;
    }

    protected void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        chatmessage.setTimestamp(timestamp);
        chatmessage.setIsOffline(Boolean.valueOf(isOffline));
        chatmessage.setOrigTimestamp(originalTimestamp);
        chatmessage.setMessageType(getMessageType().ordinal());
        chatmessage.setViewType(getViewType().ordinal());
        source.serializeTo(chatmessage);
    }
}
