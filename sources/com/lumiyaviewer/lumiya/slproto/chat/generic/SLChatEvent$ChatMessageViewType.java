// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.view.View;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.chat.generic:
//            SLChatEvent, ChatEventViewHolder, ChatYesNoEventViewHolder, ChatScriptDialogViewHolder, 
//            ChatTextBoxViewHolder

public static final class viewHolderFactory extends Enum
    implements viewHolderFactory
{

    private static final VIEW_TYPE_PLAIN $VALUES[];
    public static final VIEW_TYPE_PLAIN VALUES[] = values();
    public static final values VIEW_TYPE_DIALOG;
    public static final values VIEW_TYPE_NORMAL;
    public static final values VIEW_TYPE_PLAIN;
    public static final values VIEW_TYPE_SESSION_MARK;
    public static final values VIEW_TYPE_TEXTBOX;
    public static final values VIEW_TYPE_YESNO;
    private final boolean alwaysInflate;
    private final int resourceId;
    private final values viewHolderFactory;

    static ChatEventViewHolder _2D_com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType_2D_mthref_2D_0(View view, android.support.v7.widget.Holder.Factory factory)
    {
        return new ChatEventViewHolder(view, factory);
    }

    static ChatEventViewHolder _2D_com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType_2D_mthref_2D_1(View view, android.support.v7.widget.Holder holder)
    {
        return new ChatYesNoEventViewHolder(view, holder);
    }

    static ChatEventViewHolder _2D_com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType_2D_mthref_2D_2(View view, android.support.v7.widget.tViewHolder tviewholder)
    {
        return new ChatScriptDialogViewHolder(view, tviewholder);
    }

    static ChatEventViewHolder _2D_com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType_2D_mthref_2D_3(View view, android.support.v7.widget.logViewHolder logviewholder)
    {
        return new ChatTextBoxViewHolder(view, logviewholder);
    }

    static ChatEventViewHolder _2D_com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType_2D_mthref_2D_4(View view, android.support.v7.widget.ewHolder ewholder)
    {
        return new ChatEventViewHolder(view, ewholder);
    }

    static ChatEventViewHolder _2D_com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType_2D_mthref_2D_5(View view, android.support.v7.widget.Holder holder)
    {
        return new ChatEventViewHolder(view, holder);
    }

    public static _2D_mthref_2D_5 valueOf(String s)
    {
        return (_2D_mthref_2D_5)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/chat/generic/SLChatEvent$ChatMessageViewType, s);
    }

    public static _2D_mthref_2D_5[] values()
    {
        return $VALUES;
    }

    public final ChatEventViewHolder createViewHolder(View view, android.support.v7.widget.hatMessageViewType hatmessageviewtype)
    {
        return viewHolderFactory.teViewHolder(view, hatmessageviewtype);
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
        VIEW_TYPE_NORMAL = new <init>("VIEW_TYPE_NORMAL", 0, 0x7f040026, false, new _cls6kls());
        VIEW_TYPE_YESNO = new <init>("VIEW_TYPE_YESNO", 1, 0x7f04002b, false, new _cls1());
        VIEW_TYPE_DIALOG = new <init>("VIEW_TYPE_DIALOG", 2, 0x7f040027, false, new _cls2());
        VIEW_TYPE_TEXTBOX = new <init>("VIEW_TYPE_TEXTBOX", 3, 0x7f04002a, true, new _cls3());
        VIEW_TYPE_SESSION_MARK = new <init>("VIEW_TYPE_SESSION_MARK", 4, 0x7f040029, false, new _cls4());
        VIEW_TYPE_PLAIN = new <init>("VIEW_TYPE_PLAIN", 5, 0x7f040028, false, new _cls5());
        $VALUES = (new .VALUES[] {
            VIEW_TYPE_NORMAL, VIEW_TYPE_YESNO, VIEW_TYPE_DIALOG, VIEW_TYPE_TEXTBOX, VIEW_TYPE_SESSION_MARK, VIEW_TYPE_PLAIN
        });
    }

    private _cls6kls._cls5(String s, int i, int j, boolean flag, _cls6kls._cls5 _pcls5)
    {
        super(s, i);
        resourceId = j;
        alwaysInflate = flag;
        viewHolderFactory = _pcls5;
    }
}
