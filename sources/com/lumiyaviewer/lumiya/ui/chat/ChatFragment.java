// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.SLParcelInfo;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.UserFunctionsFragment;
import com.lumiyaviewer.lumiya.ui.objects.ObjectDetailsFragment;
import com.lumiyaviewer.lumiya.ui.render.CardboardActivity;
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            ExportChatHistoryTask, ChatRecyclerAdapter, ChatLayoutManager, TypingIndicatorView

public class ChatFragment extends UserFunctionsFragment
    implements android.view.View.OnClickListener, android.view.View.OnKeyListener, ChatRecyclerAdapter.OnAdapterDataChanged, ChatRecyclerAdapter.OnUserPicClickedListener
{

    private static final int PERMISSION_REQUEST_CODE = 500;
    private static final long typingTimeout = 5000L;
    private ChatRecyclerAdapter adapter;
    private final SubscriptionData agentCircuit = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.yqEv_Il5ub7IaZ99Gwjf4YWSeKg._cls2(this));
    private MenuItem clearChatHistoryMenuItem;
    private MenuItem exportChatHistoryMenuItem;
    private boolean hasMoreItems;
    private long lastTypingEventSent;
    private ChatLayoutManager layoutManager;
    private final Handler mHandler = new Handler();
    private ChatterID markDisplayedChatterID;
    private final android.support.v7.widget.RecyclerView.OnScrollListener scrollListener = new android.support.v7.widget.RecyclerView.OnScrollListener() {

        final ChatFragment this$0;

        public void onScrollStateChanged(RecyclerView recyclerview, int i)
        {
            if (i == 0 || i == 1)
            {
                ChatFragment._2D_set1(ChatFragment.this, false);
            }
        }

        public void onScrolled(RecyclerView recyclerview, int i, int j)
        {
            ChatFragment._2D_wrap2(ChatFragment.this);
        }

            
            {
                this$0 = ChatFragment.this;
                super();
            }
    };
    private boolean scrollToBottomForceDown;
    private boolean scrollToBottomNeeded;
    private final Runnable scrollToBottomRunnable = new Runnable() {

        final ChatFragment this$0;

        public void run()
        {
label0:
            {
                ChatFragment._2D_set2(ChatFragment.this, false);
                if (getView() != null)
                {
                    RecyclerView recyclerview = (RecyclerView)getView().findViewById(0x7f100118);
                    if (recyclerview.hasPendingAdapterUpdates())
                    {
                        break label0;
                    }
                    if (ChatFragment._2D_get0(ChatFragment.this) != null && ChatFragment._2D_get1(ChatFragment.this) != null)
                    {
                        if (ChatFragment._2D_get3(ChatFragment.this) && ChatFragment._2D_get0(ChatFragment.this).hasMoreItemsAtBottom())
                        {
                            ChatFragment._2D_set1(ChatFragment.this, false);
                            ChatFragment._2D_get0(ChatFragment.this).restartAtBottom();
                        }
                        int i = ChatFragment._2D_get0(ChatFragment.this).getItemCount();
                        if (i > 0)
                        {
                            ChatFragment._2D_get1(ChatFragment.this).setScrollMode(ChatFragment._2D_get3(ChatFragment.this));
                            recyclerview.smoothScrollToPosition(i - 1);
                            ChatFragment._2D_set1(ChatFragment.this, true);
                            ChatFragment._2D_set0(ChatFragment.this, false);
                        }
                    }
                }
                return;
            }
            ChatFragment._2D_set2(ChatFragment.this, true);
            ChatFragment._2D_get2(ChatFragment.this).post(ChatFragment._2D_get5(ChatFragment.this));
        }

            
            {
                this$0 = ChatFragment.this;
                super();
            }
    };
    private boolean scrollToBottomRunnablePosted;
    private final TextWatcher textWatcher = new TextWatcher() {

        final ChatFragment this$0;

        public void afterTextChanged(Editable editable)
        {
        }

        public void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
        {
        }

        public void onTextChanged(CharSequence charsequence, int i, int j, int k)
        {
            if (charsequence.length() != 0)
            {
                ChatFragment._2D_wrap0(ChatFragment.this, true);
                return;
            } else
            {
                ChatFragment._2D_wrap0(ChatFragment.this, false);
                return;
            }
        }

            
            {
                this$0 = ChatFragment.this;
                super();
            }
    };
    private ChatterID typingNotifiedChatter;
    private boolean updateRunnablePosted;
    private final Runnable updateVisibleRangeRunnable = new Runnable() {

        final ChatFragment this$0;

        public void run()
        {
label0:
            {
                ChatFragment._2D_set3(ChatFragment.this, false);
                Object obj = getView();
                if (ChatFragment._2D_get0(ChatFragment.this) != null && ChatFragment._2D_get1(ChatFragment.this) != null && obj != null)
                {
                    obj = (RecyclerView)((View) (obj)).findViewById(0x7f100118);
                    Debug.Printf("UpdateVisibleRange: pending %b, first %d, last %d", new Object[] {
                        Boolean.valueOf(((RecyclerView) (obj)).hasPendingAdapterUpdates()), Integer.valueOf(ChatFragment._2D_get1(ChatFragment.this).findFirstVisibleItemPosition()), Integer.valueOf(ChatFragment._2D_get1(ChatFragment.this).findLastVisibleItemPosition())
                    });
                    if (((RecyclerView) (obj)).hasPendingAdapterUpdates())
                    {
                        break label0;
                    }
                    ChatFragment._2D_wrap1(ChatFragment.this);
                    int j = ChatFragment._2D_get1(ChatFragment.this).findFirstVisibleItemPosition();
                    int i = ChatFragment._2D_get1(ChatFragment.this).findLastVisibleItemPosition();
                    if (ChatFragment._2D_get4(ChatFragment.this))
                    {
                        i = ChatFragment._2D_get0(ChatFragment.this).getItemCount() - 1;
                    }
                    ChatFragment._2D_get0(ChatFragment.this).setVisibleRange(j, i);
                }
                return;
            }
            ChatFragment._2D_set3(ChatFragment.this, true);
            ChatFragment._2D_get2(ChatFragment.this).post(ChatFragment._2D_get6(ChatFragment.this));
        }

            
            {
                this$0 = ChatFragment.this;
                super();
            }
    };
    private final SubscriptionData voiceActiveChatter = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.yqEv_Il5ub7IaZ99Gwjf4YWSeKg._cls3(this));
    private final SubscriptionData voiceChatInfo = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.yqEv_Il5ub7IaZ99Gwjf4YWSeKg._cls4(this));
    private boolean vrMode;

    static ChatRecyclerAdapter _2D_get0(ChatFragment chatfragment)
    {
        return chatfragment.adapter;
    }

    static ChatLayoutManager _2D_get1(ChatFragment chatfragment)
    {
        return chatfragment.layoutManager;
    }

    static Handler _2D_get2(ChatFragment chatfragment)
    {
        return chatfragment.mHandler;
    }

    static boolean _2D_get3(ChatFragment chatfragment)
    {
        return chatfragment.scrollToBottomForceDown;
    }

    static boolean _2D_get4(ChatFragment chatfragment)
    {
        return chatfragment.scrollToBottomNeeded;
    }

    static Runnable _2D_get5(ChatFragment chatfragment)
    {
        return chatfragment.scrollToBottomRunnable;
    }

    static Runnable _2D_get6(ChatFragment chatfragment)
    {
        return chatfragment.updateVisibleRangeRunnable;
    }

    static boolean _2D_set0(ChatFragment chatfragment, boolean flag)
    {
        chatfragment.scrollToBottomForceDown = flag;
        return flag;
    }

    static boolean _2D_set1(ChatFragment chatfragment, boolean flag)
    {
        chatfragment.scrollToBottomNeeded = flag;
        return flag;
    }

    static boolean _2D_set2(ChatFragment chatfragment, boolean flag)
    {
        chatfragment.scrollToBottomRunnablePosted = flag;
        return flag;
    }

    static boolean _2D_set3(ChatFragment chatfragment, boolean flag)
    {
        chatfragment.updateRunnablePosted = flag;
        return flag;
    }

    static void _2D_wrap0(ChatFragment chatfragment, boolean flag)
    {
        chatfragment.setTypingNotify(flag);
    }

    static void _2D_wrap1(ChatFragment chatfragment)
    {
        chatfragment.updateHasMoreItems();
    }

    static void _2D_wrap2(ChatFragment chatfragment)
    {
        chatfragment.updateVisibleRange();
    }

    public ChatFragment()
    {
        vrMode = false;
        layoutManager = null;
        adapter = null;
        scrollToBottomNeeded = false;
        hasMoreItems = false;
        typingNotifiedChatter = null;
        markDisplayedChatterID = null;
        exportChatHistoryMenuItem = null;
        clearChatHistoryMenuItem = null;
        updateRunnablePosted = false;
        scrollToBottomRunnablePosted = false;
        scrollToBottomForceDown = false;
    }

    private void clearChatHistory()
    {
        (new android.app.AlertDialog.Builder(getContext())).setMessage(0x7f0900bc).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.yqEv_Il5ub7IaZ99Gwjf4YWSeKg._cls1(this)).setNegativeButton("No", new _2D_.Lambda.yqEv_Il5ub7IaZ99Gwjf4YWSeKg()).create().show();
    }

    private void exportChatHistory()
    {
label0:
        {
            if (chatterID != null)
            {
                if (ContextCompat.checkSelfPermission(getContext(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0)
                {
                    break label0;
                }
                ActivityCompat.requestPermissions(getActivity(), new String[] {
                    "android.permission.WRITE_EXTERNAL_STORAGE"
                }, 500);
            }
            return;
        }
        (new ExportChatHistoryTask(getActivity())).execute(new ChatterID[] {
            chatterID
        });
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_ChatFragment_22748(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    public static Bundle makeSelection(ChatterID chatterid)
    {
        Bundle bundle = ChatterFragment.makeSelection(chatterid);
        bundle.putParcelable("chatterID", chatterid);
        return bundle;
    }

    private void onAgentCircuit(SLAgentCircuit slagentcircuit)
    {
        boolean flag = false;
        View view = getView();
        if (view != null)
        {
            Object obj;
            int i;
            if (slagentcircuit != null)
            {
                obj = "present";
            } else
            {
                obj = "not present";
            }
            Debug.Printf("agentCircuit is now %s", new Object[] {
                obj
            });
            obj = view.findViewById(0x7f10011f);
            if (slagentcircuit != null && vrMode ^ true)
            {
                i = 0;
            } else
            {
                i = 8;
            }
            ((View) (obj)).setVisibility(i);
            obj = view.findViewById(0x7f10011c);
            if (slagentcircuit != null && vrMode)
            {
                i = ((flag) ? 1 : 0);
            } else
            {
                i = 8;
            }
            ((View) (obj)).setVisibility(i);
        }
    }

    private void onVoiceActiveChatter(ChatterID chatterid)
    {
        if (chatterid != null && userManager != null && vrMode)
        {
            voiceChatInfo.subscribe(userManager.getVoiceChatInfo(), chatterid);
        } else
        {
            voiceChatInfo.unsubscribe();
        }
        updateVrModeControls();
    }

    private void onVoiceChatInfo(VoiceChatInfo voicechatinfo)
    {
        updateVrModeControls();
    }

    private void scrollToBottom(boolean flag)
    {
        if (getView() != null && scrollToBottomRunnablePosted ^ true)
        {
            scrollToBottomNeeded = true;
            scrollToBottomRunnablePosted = true;
            scrollToBottomForceDown = scrollToBottomForceDown | flag;
            mHandler.post(scrollToBottomRunnable);
        }
    }

    private void sendMessage()
    {
        setTypingNotify(false);
        Object obj = getView();
        if (chatterID == null || obj == null)
        {
            return;
        }
        obj = ((TextView)((View) (obj)).findViewById(0x7f10011e)).getText().toString();
        if (((String) (obj)).equals(""))
        {
            return;
        }
        if (userManager != null)
        {
            SLAgentCircuit slagentcircuit = userManager.getActiveAgentCircuit();
            if (slagentcircuit != null)
            {
                slagentcircuit.SendChatMessage(chatterID, ((String) (obj)));
                ((TextView)getView().findViewById(0x7f10011e)).setText("");
            }
        }
        scrollToBottom(false);
    }

    private boolean sendTypingNotify(ChatterID chatterid, boolean flag)
    {
        if (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)
        {
            Object obj = chatterid.getUserManager();
            if (obj != null)
            {
                obj = ((UserManager) (obj)).getActiveAgentCircuit();
                if (obj != null)
                {
                    ((SLAgentCircuit) (obj)).sendTypingNotify(((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterid).getChatterUUID(), flag);
                    return true;
                }
            }
        }
        return false;
    }

    private void setTypingNotify(boolean flag)
    {
        if (!flag) goto _L2; else goto _L1
_L1:
        long l = System.currentTimeMillis();
        if (Objects.equal(chatterID, typingNotifiedChatter)) goto _L4; else goto _L3
_L3:
        if (typingNotifiedChatter != null)
        {
            sendTypingNotify(typingNotifiedChatter, false);
            typingNotifiedChatter = null;
        }
        if (sendTypingNotify(chatterID, true))
        {
            typingNotifiedChatter = chatterID;
            lastTypingEventSent = l;
        }
_L6:
        return;
_L4:
        if (typingNotifiedChatter != null && l >= lastTypingEventSent + 5000L)
        {
            lastTypingEventSent = l;
            sendTypingNotify(typingNotifiedChatter, true);
            return;
        }
        continue; /* Loop/switch isn't completed */
_L2:
        if (typingNotifiedChatter != null)
        {
            sendTypingNotify(typingNotifiedChatter, false);
            typingNotifiedChatter = null;
            return;
        }
        if (true) goto _L6; else goto _L5
_L5:
    }

    private void updateChatHistoryExists()
    {
        boolean flag;
        if (adapter != null)
        {
            if (adapter.getItemCount() != 0)
            {
                flag = true;
            } else
            {
                flag = false;
            }
        } else
        {
            flag = false;
        }
        Debug.Printf("ChatHistory: chat history exists %b", new Object[] {
            Boolean.valueOf(flag)
        });
        if (clearChatHistoryMenuItem != null)
        {
            clearChatHistoryMenuItem.setVisible(flag);
        }
        if (exportChatHistoryMenuItem != null)
        {
            exportChatHistoryMenuItem.setVisible(flag);
        }
    }

    private void updateHasMoreItems()
    {
        boolean flag = true;
        View view = getView();
        if (view != null)
        {
            hasMoreItems = false;
            if (layoutManager != null && adapter != null)
            {
                int k = adapter.getItemCount();
                int i = layoutManager.findLastVisibleItemPosition();
                if (scrollToBottomNeeded)
                {
                    i = adapter.getItemCount() - 1;
                }
                if (k > 1 && i != -1)
                {
                    if (i >= k - 2)
                    {
                        flag = adapter.hasMoreItemsAtBottom();
                    }
                    hasMoreItems = flag;
                }
            }
            view = view.findViewById(0x7f10011a);
            int j;
            if (hasMoreItems)
            {
                j = 0;
            } else
            {
                j = 8;
            }
            view.setVisibility(j);
        }
    }

    private void updateVisibleRange()
    {
        if (adapter != null && layoutManager != null && getView() != null && updateRunnablePosted ^ true)
        {
            updateRunnablePosted = true;
            mHandler.post(updateVisibleRangeRunnable);
        }
    }

    private void updateVrModeControls()
    {
label0:
        {
            boolean flag1 = true;
            boolean flag = false;
            View view = getView();
            if (view != null)
            {
                if (!vrMode)
                {
                    break label0;
                }
                view.findViewById(0x7f10011b).setVisibility(0);
                int i;
                byte byte1;
                if (isVoiceLoggedIn())
                {
                    byte byte0;
                    if (voiceActiveChatter.getData() != null)
                    {
                        Object obj = (VoiceChatInfo)voiceChatInfo.getData();
                        if (obj != null && ((VoiceChatInfo) (obj)).state != com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.None)
                        {
                            byte0 = 1;
                        } else
                        {
                            byte0 = 0;
                        }
                    } else
                    {
                        byte0 = 0;
                    }
                    if (byte0 == 0)
                    {
                        if (chatterID != null && userManager != null)
                        {
                            byte1 = byte0;
                            i = ((flag1) ? 1 : 0);
                            if (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDLocal)
                            {
                                obj = userManager.getCurrentLocationInfoSnapshot();
                                if (obj != null && ((CurrentLocationInfo) (obj)).parcelVoiceChannel() != null)
                                {
                                    i = ((flag1) ? 1 : 0);
                                    byte1 = byte0;
                                } else
                                {
                                    i = 0;
                                    byte1 = byte0;
                                }
                            }
                        } else
                        {
                            i = 0;
                            byte1 = byte0;
                        }
                    } else
                    {
                        i = 0;
                        byte1 = byte0;
                    }
                } else
                {
                    byte1 = 0;
                    i = 0;
                }
                obj = view.findViewById(0x7f10011c);
                if (byte1 != 0)
                {
                    byte0 = 8;
                } else
                {
                    byte0 = 0;
                }
                ((View) (obj)).setVisibility(byte0);
                view = view.findViewById(0x7f10011d);
                if (i != 0)
                {
                    i = ((flag) ? 1 : 0);
                } else
                {
                    i = 8;
                }
                view.setVisibility(i);
            }
            return;
        }
        view.findViewById(0x7f10011b).setVisibility(8);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_ChatFragment_2D_mthref_2D_0(SLAgentCircuit slagentcircuit)
    {
        onAgentCircuit(slagentcircuit);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_ChatFragment_2D_mthref_2D_1(ChatterID chatterid)
    {
        onVoiceActiveChatter(chatterid);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_ChatFragment_2D_mthref_2D_2(VoiceChatInfo voicechatinfo)
    {
        onVoiceChatInfo(voicechatinfo);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_ChatFragment_22290(DialogInterface dialoginterface, int i)
    {
        if (chatterID != null)
        {
            UserManager usermanager = chatterID.getUserManager();
            if (usermanager != null)
            {
                usermanager.getChatterList().getActiveChattersManager().clearChatHistory(chatterID);
            }
            dialoginterface.dismiss();
        }
    }

    public void onAdapterDataAddedAtEnd()
    {
        if (layoutManager != null && (!hasMoreItems || layoutManager.isSmoothScrolling() || scrollToBottomNeeded))
        {
            scrollToBottom(false);
        }
        updateChatHistoryExists();
    }

    public void onAdapterDataChanged()
    {
        updateVisibleRange();
        updateChatHistoryExists();
    }

    public void onAdapterDataReloaded()
    {
        scrollToBottomNeeded = false;
        updateVisibleRange();
        updateChatHistoryExists();
    }

    public void onChatterNameUpdated(ChatterNameRetriever chatternameretriever)
    {
        super.onChatterNameUpdated(chatternameretriever);
        Object obj = getView();
        if (obj != null)
        {
            if (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDLocal)
            {
                chatternameretriever = "local chat";
            } else
            {
                chatternameretriever = chatternameretriever.getResolvedName();
            }
            obj = (EditText)((View) (obj)).findViewById(0x7f10011e);
            if (chatternameretriever != null)
            {
                chatternameretriever = getString(0x7f0900ac, new Object[] {
                    chatternameretriever
                });
            } else
            {
                chatternameretriever = null;
            }
            ((EditText) (obj)).setHint(chatternameretriever);
        }
    }

    public void onClick(View view)
    {
        view.getId();
        JVM INSTR tableswitch 2131755290 2131755295: default 44
    //                   2131755290 50
    //                   2131755291 44
    //                   2131755292 56
    //                   2131755293 87
    //                   2131755294 44
    //                   2131755295 45;
           goto _L1 _L2 _L1 _L3 _L4 _L1 _L5
_L1:
        return;
_L5:
        sendMessage();
        return;
_L2:
        scrollToBottom(true);
        return;
_L3:
        view = getActivity();
        if ((view instanceof CardboardActivity) && chatterID != null)
        {
            ((CardboardActivity)view).startDictation(chatterID);
            return;
        }
        continue; /* Loop/switch isn't completed */
_L4:
        if (chatterID != null)
        {
            handleStartVoice(chatterID);
            return;
        }
        if (true) goto _L1; else goto _L6
_L6:
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        bundle = getArguments();
        if (bundle != null && bundle.getBoolean("vrMode"))
        {
            vrMode = true;
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater)
    {
        super.onCreateOptionsMenu(menu, menuinflater);
        menuinflater.inflate(0x7f120001, menu);
        exportChatHistoryMenuItem = menu.findItem(0x7f1002f9);
        clearChatHistoryMenuItem = menu.findItem(0x7f1002fa);
        updateChatHistoryExists();
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        boolean flag = false;
        super.onCreateView(layoutinflater, viewgroup, bundle);
        layoutinflater = layoutinflater.inflate(0x7f040024, viewgroup, false);
        layoutinflater.findViewById(0x7f10011a).setOnClickListener(this);
        layoutManager = new ChatLayoutManager(viewgroup.getContext(), 1, false);
        layoutManager.setStackFromEnd(true);
        viewgroup = (RecyclerView)layoutinflater.findViewById(0x7f100118);
        viewgroup.setHasFixedSize(true);
        viewgroup.addOnScrollListener(scrollListener);
        viewgroup.setLayoutManager(layoutManager);
        layoutinflater.findViewById(0x7f10011f).setOnClickListener(this);
        layoutinflater.findViewById(0x7f10011c).setOnClickListener(this);
        layoutinflater.findViewById(0x7f10011d).setOnClickListener(this);
        viewgroup = (EditText)layoutinflater.findViewById(0x7f10011e);
        viewgroup.setOnKeyListener(this);
        viewgroup.addTextChangedListener(textWatcher);
        int i;
        if (vrMode)
        {
            i = 8;
        } else
        {
            i = 0;
        }
        viewgroup.setVisibility(i);
        viewgroup = layoutinflater.findViewById(0x7f10011f);
        if (vrMode)
        {
            i = 8;
        } else
        {
            i = 0;
        }
        viewgroup.setVisibility(i);
        viewgroup = layoutinflater.findViewById(0x7f10011b);
        if (vrMode)
        {
            i = ((flag) ? 1 : 0);
        } else
        {
            i = 8;
        }
        viewgroup.setVisibility(i);
        return layoutinflater;
    }

    protected void onCurrentLocationChanged(CurrentLocationInfo currentlocationinfo)
    {
        super.onCurrentLocationChanged(currentlocationinfo);
        updateVrModeControls();
    }

    public boolean onKey(View view, int i, KeyEvent keyevent)
    {
label0:
        {
            if (keyevent.getAction() == 0 && i == 66)
            {
                sendMessage();
                return true;
            }
            if (view instanceof TextView)
            {
                if (((TextView)view).getText().length() == 0)
                {
                    break label0;
                }
                setTypingNotify(true);
            }
            return false;
        }
        setTypingNotify(false);
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        switch (menuitem.getItemId())
        {
        default:
            return super.onOptionsItemSelected(menuitem);

        case 2131755769: 
            exportChatHistory();
            return true;

        case 2131755770: 
            clearChatHistory();
            break;
        }
        return true;
    }

    public void onPause()
    {
        if (markDisplayedChatterID != null)
        {
            UserManager usermanager = markDisplayedChatterID.getUserManager();
            if (usermanager != null)
            {
                usermanager.getChatterList().getActiveChattersManager().removeDisplayedChatter(markDisplayedChatterID);
            }
            markDisplayedChatterID = null;
        }
        setTypingNotify(false);
        super.onPause();
    }

    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        updateChatHistoryExists();
    }

    public void onRequestPermissionsResult(int i, String as[], int ai[])
    {
        if (i == 500 && ai.length > 0 && ai[0] == 0)
        {
            (new ExportChatHistoryTask(getActivity())).execute(new ChatterID[] {
                chatterID
            });
        }
    }

    public void onResume()
    {
        super.onResume();
        markDisplayedChatterID = chatterID;
        if (markDisplayedChatterID != null)
        {
            UserManager usermanager = markDisplayedChatterID.getUserManager();
            if (usermanager != null)
            {
                usermanager.getChatterList().getActiveChattersManager().addDisplayedChatter(markDisplayedChatterID);
            }
        }
        updateVisibleRange();
        updateChatHistoryExists();
    }

    protected void onShowUser(ChatterID chatterid)
    {
        Debug.Printf("ChatFragment: displaying for user %s", new Object[] {
            chatterid
        });
        if (!Objects.equal(markDisplayedChatterID, chatterid) && isFragmentVisible())
        {
            if (markDisplayedChatterID != null)
            {
                UserManager usermanager = markDisplayedChatterID.getUserManager();
                if (usermanager != null)
                {
                    usermanager.getChatterList().getActiveChattersManager().removeDisplayedChatter(markDisplayedChatterID);
                }
            }
            markDisplayedChatterID = chatterid;
            if (markDisplayedChatterID != null)
            {
                UserManager usermanager1 = markDisplayedChatterID.getUserManager();
                if (usermanager1 != null)
                {
                    usermanager1.getChatterList().getActiveChattersManager().addDisplayedChatter(markDisplayedChatterID);
                }
            }
        }
        if (adapter != null)
        {
            adapter.stopLoading();
            adapter = null;
        }
        if (chatterid != null && userManager != null)
        {
            agentCircuit.subscribe(UserManager.agentCircuits(), chatterid.agentUUID);
            adapter = new ChatRecyclerAdapter(getActivity(), userManager, chatterid);
            adapter.setOnUserPicClickedListener(this);
            Object obj;
            Object obj1;
            if (vrMode)
            {
                voiceActiveChatter.subscribe(userManager.getVoiceActiveChatter(), SubscriptionSingleKey.Value);
            } else
            {
                voiceActiveChatter.unsubscribe();
            }
        } else
        {
            agentCircuit.unsubscribe();
            voiceActiveChatter.unsubscribe();
            voiceChatInfo.unsubscribe();
        }
        obj = getView();
        if (obj != null)
        {
            obj1 = (RecyclerView)((View) (obj)).findViewById(0x7f100118);
            Debug.Printf("ChatFragment: setting adapter to %s", new Object[] {
                adapter
            });
            ((RecyclerView) (obj1)).setAdapter(adapter);
            if (adapter != null)
            {
                adapter.startLoading(this);
            }
            obj1 = (TypingIndicatorView)((View) (obj)).findViewById(0x7f100120);
            if (obj1 != null)
            {
                ((TypingIndicatorView) (obj1)).setChatterID(chatterid);
            }
            obj = (VoiceStatusView)((View) (obj)).findViewById(0x7f100119);
            if (obj != null)
            {
                ((VoiceStatusView) (obj)).setChatterID(chatterid);
            }
            updateVisibleRange();
            updateChatHistoryExists();
            updateVrModeControls();
        }
        setTypingNotify(false);
    }

    public void onUserPicClicked(ChatMessageSource chatmessagesource)
    {
        if (userManager != null)
        {
            if (chatmessagesource.getSourceType() == com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.User || chatmessagesource.getSourceType() == com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.Group)
            {
                handleUserViewProfile(chatmessagesource.getDefaultChatter(userManager.getUserID()));
            } else
            if (chatmessagesource.getSourceType() == com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource.ChatMessageSourceType.Object && (chatmessagesource instanceof ChatMessageSourceObject))
            {
                chatmessagesource = (ChatMessageSourceObject)chatmessagesource;
                SLAgentCircuit slagentcircuit = (SLAgentCircuit)agentCircuit.getData();
                if (slagentcircuit != null)
                {
                    int i = slagentcircuit.getGridConnection().parcelInfo.getObjectLocalID(((ChatMessageSourceObject) (chatmessagesource)).uuid);
                    if (i != -1)
                    {
                        DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/objects/ObjectDetailsFragment, ObjectDetailsFragment.makeSelection(userManager.getUserID(), i));
                        return;
                    }
                }
            }
        }
    }

    protected void onVoiceLoginStatusChanged(Boolean boolean1)
    {
        super.onVoiceLoginStatusChanged(boolean1);
        updateVrModeControls();
    }
}
