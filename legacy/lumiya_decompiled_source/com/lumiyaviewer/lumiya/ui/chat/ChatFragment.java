package com.lumiyaviewer.lumiya.ui.chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
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
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatRecyclerAdapter;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.UserFunctionsFragment;
import com.lumiyaviewer.lumiya.ui.objects.ObjectDetailsFragment;
import com.lumiyaviewer.lumiya.ui.render.CardboardActivity;
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import java.util.UUID;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class ChatFragment extends UserFunctionsFragment implements View.OnClickListener, View.OnKeyListener, ChatRecyclerAdapter.OnAdapterDataChanged, ChatRecyclerAdapter.OnUserPicClickedListener {
    private static final int PERMISSION_REQUEST_CODE = 500;
    private static final long typingTimeout = 5000;
    private long lastTypingEventSent;
    private boolean vrMode = false;
    @Nullable
    private ChatLayoutManager layoutManager = null;
    @Nullable
    private ChatRecyclerAdapter adapter = null;
    private boolean scrollToBottomNeeded = false;
    private boolean hasMoreItems = false;
    @Nullable
    private ChatterID typingNotifiedChatter = null;
    @Nullable
    private ChatterID markDisplayedChatterID = null;
    @Nullable
    private MenuItem exportChatHistoryMenuItem = null;
    @Nullable
    private MenuItem clearChatHistoryMenuItem = null;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.2
        private final /* synthetic */ void $m$0(Object obj) {
            ((ChatFragment) this).m421com_lumiyaviewer_lumiya_ui_chat_ChatFragmentmthref0((SLAgentCircuit) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final SubscriptionData<SubscriptionSingleKey, ChatterID> voiceActiveChatter = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.3
        private final /* synthetic */ void $m$0(Object obj) {
            ((ChatFragment) this).m422com_lumiyaviewer_lumiya_ui_chat_ChatFragmentmthref1((ChatterID) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final SubscriptionData<ChatterID, VoiceChatInfo> voiceChatInfo = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.4
        private final /* synthetic */ void $m$0(Object obj) {
            ((ChatFragment) this).m423com_lumiyaviewer_lumiya_ui_chat_ChatFragmentmthref2((VoiceChatInfo) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.ChatFragment.1
        @Override // android.support.v7.widget.RecyclerView.OnScrollListener
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 0 || i == 1) {
                ChatFragment.this.scrollToBottomNeeded = false;
            }
        }

        @Override // android.support.v7.widget.RecyclerView.OnScrollListener
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            ChatFragment.this.updateVisibleRange();
        }
    };
    private boolean updateRunnablePosted = false;
    private final Handler mHandler = new Handler();
    private final Runnable updateVisibleRangeRunnable = new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.chat.ChatFragment.2
        @Override // java.lang.Runnable
        public void run() {
            ChatFragment.this.updateRunnablePosted = false;
            View view = ChatFragment.this.getView();
            if (ChatFragment.this.adapter == null || ChatFragment.this.layoutManager == null || view == null) {
                return;
            }
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.chatLogView);
            Debug.Printf("UpdateVisibleRange: pending %b, first %d, last %d", Boolean.valueOf(recyclerView.hasPendingAdapterUpdates()), Integer.valueOf(ChatFragment.this.layoutManager.findFirstVisibleItemPosition()), Integer.valueOf(ChatFragment.this.layoutManager.findLastVisibleItemPosition()));
            if (recyclerView.hasPendingAdapterUpdates()) {
                ChatFragment.this.updateRunnablePosted = true;
                ChatFragment.this.mHandler.post(ChatFragment.this.updateVisibleRangeRunnable);
                return;
            }
            ChatFragment.this.updateHasMoreItems();
            int findFirstVisibleItemPosition = ChatFragment.this.layoutManager.findFirstVisibleItemPosition();
            int findLastVisibleItemPosition = ChatFragment.this.layoutManager.findLastVisibleItemPosition();
            if (ChatFragment.this.scrollToBottomNeeded) {
                findLastVisibleItemPosition = ChatFragment.this.adapter.getItemCount() - 1;
            }
            ChatFragment.this.adapter.setVisibleRange(findFirstVisibleItemPosition, findLastVisibleItemPosition);
        }
    };
    private boolean scrollToBottomRunnablePosted = false;
    private boolean scrollToBottomForceDown = false;
    private final Runnable scrollToBottomRunnable = new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.chat.ChatFragment.3
        @Override // java.lang.Runnable
        public void run() {
            ChatFragment.this.scrollToBottomRunnablePosted = false;
            if (ChatFragment.this.getView() != null) {
                RecyclerView recyclerView = (RecyclerView) ChatFragment.this.getView().findViewById(R.id.chatLogView);
                if (recyclerView.hasPendingAdapterUpdates()) {
                    ChatFragment.this.scrollToBottomRunnablePosted = true;
                    ChatFragment.this.mHandler.post(ChatFragment.this.scrollToBottomRunnable);
                } else if (ChatFragment.this.adapter == null || ChatFragment.this.layoutManager == null) {
                } else {
                    if (ChatFragment.this.scrollToBottomForceDown && ChatFragment.this.adapter.hasMoreItemsAtBottom()) {
                        ChatFragment.this.scrollToBottomNeeded = false;
                        ChatFragment.this.adapter.restartAtBottom();
                    }
                    int itemCount = ChatFragment.this.adapter.getItemCount();
                    if (itemCount > 0) {
                        ChatFragment.this.layoutManager.setScrollMode(ChatFragment.this.scrollToBottomForceDown);
                        recyclerView.smoothScrollToPosition(itemCount - 1);
                        ChatFragment.this.scrollToBottomNeeded = true;
                        ChatFragment.this.scrollToBottomForceDown = false;
                    }
                }
            }
        }
    };
    private final TextWatcher textWatcher = new TextWatcher() { // from class: com.lumiyaviewer.lumiya.ui.chat.ChatFragment.4
        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (charSequence.length() != 0) {
                ChatFragment.this.setTypingNotify(true);
            } else {
                ChatFragment.this.setTypingNotify(false);
            }
        }
    };

    private void clearChatHistory() {
        new AlertDialog.Builder(getContext()).setMessage(R.string.clear_chat_history_confirm).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg.1
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((ChatFragment) this).m424lambda$com_lumiyaviewer_lumiya_ui_chat_ChatFragment_22290(dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).create().show();
    }

    private void exportChatHistory() {
        if (this.chatterID != null) {
            if (ContextCompat.checkSelfPermission(getContext(), "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 500);
            } else {
                new ExportChatHistoryTask(getActivity()).execute(this.chatterID);
            }
        }
    }

    public static Bundle makeSelection(ChatterID chatterID) {
        Bundle makeSelection = ChatterFragment.makeSelection(chatterID);
        makeSelection.putParcelable(ChatterFragment.CHATTER_ID_KEY, chatterID);
        return makeSelection;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onAgentCircuit */
    public void m421com_lumiyaviewer_lumiya_ui_chat_ChatFragmentmthref0(SLAgentCircuit sLAgentCircuit) {
        int i = 0;
        View view = getView();
        if (view != null) {
            Object[] objArr = new Object[1];
            objArr[0] = sLAgentCircuit != null ? "present" : "not present";
            Debug.Printf("agentCircuit is now %s", objArr);
            view.findViewById(R.id.sendMessageButton).setVisibility((sLAgentCircuit == null || !(this.vrMode ^ true)) ? 8 : 0);
            View findViewById = view.findViewById(R.id.chat_speak_button);
            if (sLAgentCircuit == null || !this.vrMode) {
                i = 8;
            }
            findViewById.setVisibility(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onVoiceActiveChatter */
    public void m422com_lumiyaviewer_lumiya_ui_chat_ChatFragmentmthref1(ChatterID chatterID) {
        if (chatterID == null || this.userManager == null || !this.vrMode) {
            this.voiceChatInfo.unsubscribe();
        } else {
            this.voiceChatInfo.subscribe(this.userManager.getVoiceChatInfo(), chatterID);
        }
        updateVrModeControls();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onVoiceChatInfo */
    public void m423com_lumiyaviewer_lumiya_ui_chat_ChatFragmentmthref2(VoiceChatInfo voiceChatInfo) {
        updateVrModeControls();
    }

    private void scrollToBottom(boolean z) {
        if (getView() == null || !(!this.scrollToBottomRunnablePosted)) {
            return;
        }
        this.scrollToBottomNeeded = true;
        this.scrollToBottomRunnablePosted = true;
        this.scrollToBottomForceDown |= z;
        this.mHandler.post(this.scrollToBottomRunnable);
    }

    private void sendMessage() {
        SLAgentCircuit activeAgentCircuit;
        setTypingNotify(false);
        View view = getView();
        if (this.chatterID == null || view == null) {
            return;
        }
        String charSequence = ((TextView) view.findViewById(R.id.sendMessageText)).getText().toString();
        if (charSequence.equals("")) {
            return;
        }
        if (this.userManager != null && (activeAgentCircuit = this.userManager.getActiveAgentCircuit()) != null) {
            activeAgentCircuit.SendChatMessage(this.chatterID, charSequence);
            ((TextView) getView().findViewById(R.id.sendMessageText)).setText("");
        }
        scrollToBottom(false);
    }

    private boolean sendTypingNotify(@Nullable ChatterID chatterID, boolean z) {
        UserManager userManager;
        SLAgentCircuit activeAgentCircuit;
        if (!(chatterID instanceof ChatterID.ChatterIDUser) || (userManager = chatterID.getUserManager()) == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null) {
            return false;
        }
        activeAgentCircuit.sendTypingNotify(((ChatterID.ChatterIDUser) chatterID).getChatterUUID(), z);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTypingNotify(boolean z) {
        if (!z) {
            if (this.typingNotifiedChatter != null) {
                sendTypingNotify(this.typingNotifiedChatter, false);
                this.typingNotifiedChatter = null;
                return;
            }
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (Objects.equal(this.chatterID, this.typingNotifiedChatter)) {
            if (this.typingNotifiedChatter == null || currentTimeMillis < this.lastTypingEventSent + typingTimeout) {
                return;
            }
            this.lastTypingEventSent = currentTimeMillis;
            sendTypingNotify(this.typingNotifiedChatter, true);
            return;
        }
        if (this.typingNotifiedChatter != null) {
            sendTypingNotify(this.typingNotifiedChatter, false);
            this.typingNotifiedChatter = null;
        }
        if (sendTypingNotify(this.chatterID, true)) {
            this.typingNotifiedChatter = this.chatterID;
            this.lastTypingEventSent = currentTimeMillis;
        }
    }

    private void updateChatHistoryExists() {
        boolean z = this.adapter != null ? this.adapter.getItemCount() != 0 : false;
        Debug.Printf("ChatHistory: chat history exists %b", Boolean.valueOf(z));
        if (this.clearChatHistoryMenuItem != null) {
            this.clearChatHistoryMenuItem.setVisible(z);
        }
        if (this.exportChatHistoryMenuItem != null) {
            this.exportChatHistoryMenuItem.setVisible(z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateHasMoreItems() {
        View view = getView();
        if (view != null) {
            this.hasMoreItems = false;
            if (this.layoutManager != null && this.adapter != null) {
                int itemCount = this.adapter.getItemCount();
                int findLastVisibleItemPosition = this.layoutManager.findLastVisibleItemPosition();
                if (this.scrollToBottomNeeded) {
                    findLastVisibleItemPosition = this.adapter.getItemCount() - 1;
                }
                if (itemCount > 1 && findLastVisibleItemPosition != -1) {
                    this.hasMoreItems = findLastVisibleItemPosition >= itemCount + (-2) ? this.adapter.hasMoreItemsAtBottom() : true;
                }
            }
            view.findViewById(R.id.scroll_to_bottom_btn).setVisibility(this.hasMoreItems ? 0 : 8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateVisibleRange() {
        if (this.adapter == null || this.layoutManager == null || getView() == null || !(!this.updateRunnablePosted)) {
            return;
        }
        this.updateRunnablePosted = true;
        this.mHandler.post(this.updateVisibleRangeRunnable);
    }

    private void updateVrModeControls() {
        boolean z;
        CurrentLocationInfo currentLocationInfoSnapshot;
        boolean z2 = true;
        View view = getView();
        if (view != null) {
            if (!this.vrMode) {
                view.findViewById(R.id.chat_vr_mode_controls).setVisibility(8);
                return;
            }
            view.findViewById(R.id.chat_vr_mode_controls).setVisibility(0);
            if (isVoiceLoggedIn()) {
                if (this.voiceActiveChatter.getData() != null) {
                    VoiceChatInfo data = this.voiceChatInfo.getData();
                    z = (data == null || data.state == VoiceChatInfo.VoiceChatState.None) ? false : true;
                } else {
                    z = false;
                }
                if (z) {
                    z2 = false;
                } else if (this.chatterID == null || this.userManager == null) {
                    z2 = false;
                } else if ((this.chatterID instanceof ChatterID.ChatterIDLocal) && ((currentLocationInfoSnapshot = this.userManager.getCurrentLocationInfoSnapshot()) == null || currentLocationInfoSnapshot.parcelVoiceChannel() == null)) {
                    z2 = false;
                }
            } else {
                z = false;
                z2 = false;
            }
            view.findViewById(R.id.chat_speak_button).setVisibility(z ? 8 : 0);
            view.findViewById(R.id.chat_voice_call_button).setVisibility(z2 ? 0 : 8);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_ChatFragment_22290  reason: not valid java name */
    public /* synthetic */ void m424lambda$com_lumiyaviewer_lumiya_ui_chat_ChatFragment_22290(DialogInterface dialogInterface, int i) {
        if (this.chatterID != null) {
            UserManager userManager = this.chatterID.getUserManager();
            if (userManager != null) {
                userManager.getChatterList().getActiveChattersManager().clearChatHistory(this.chatterID);
            }
            dialogInterface.dismiss();
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.chat.ChatRecyclerAdapter.OnAdapterDataChanged
    public void onAdapterDataAddedAtEnd() {
        if (this.layoutManager != null && (!this.hasMoreItems || this.layoutManager.isSmoothScrolling() || this.scrollToBottomNeeded)) {
            scrollToBottom(false);
        }
        updateChatHistoryExists();
    }

    @Override // com.lumiyaviewer.lumiya.ui.chat.ChatRecyclerAdapter.OnAdapterDataChanged
    public void onAdapterDataChanged() {
        updateVisibleRange();
        updateChatHistoryExists();
    }

    @Override // com.lumiyaviewer.lumiya.ui.chat.ChatRecyclerAdapter.OnAdapterDataChanged
    public void onAdapterDataReloaded() {
        this.scrollToBottomNeeded = false;
        updateVisibleRange();
        updateChatHistoryExists();
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment, com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
    public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        super.onChatterNameUpdated(chatterNameRetriever);
        View view = getView();
        if (view != null) {
            String resolvedName = this.chatterID instanceof ChatterID.ChatterIDLocal ? "local chat" : chatterNameRetriever.getResolvedName();
            ((EditText) view.findViewById(R.id.sendMessageText)).setHint(resolvedName != null ? getString(R.string.chat_hint_message_format, resolvedName) : null);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scroll_to_bottom_btn /* 2131755290 */:
                scrollToBottom(true);
                return;
            case R.id.chat_vr_mode_controls /* 2131755291 */:
            case R.id.sendMessageText /* 2131755294 */:
            default:
                return;
            case R.id.chat_speak_button /* 2131755292 */:
                FragmentActivity activity = getActivity();
                if (!(activity instanceof CardboardActivity) || this.chatterID == null) {
                    return;
                }
                ((CardboardActivity) activity).startDictation(this.chatterID);
                return;
            case R.id.chat_voice_call_button /* 2131755293 */:
                if (this.chatterID != null) {
                    handleStartVoice(this.chatterID);
                    return;
                }
                return;
            case R.id.sendMessageButton /* 2131755295 */:
                sendMessage();
                return;
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.UserFunctionsFragment, com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, android.support.v4.app.Fragment
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.getBoolean(CardboardActivity.VR_MODE_TAG)) {
            return;
        }
        this.vrMode = true;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.UserFunctionsFragment, android.support.v4.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.chat_history_menu, menu);
        this.exportChatHistoryMenuItem = menu.findItem(R.id.item_chat_history_export);
        this.clearChatHistoryMenuItem = menu.findItem(R.id.item_chat_history_clear);
        updateChatHistoryExists();
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.chat, viewGroup, false);
        inflate.findViewById(R.id.scroll_to_bottom_btn).setOnClickListener(this);
        this.layoutManager = new ChatLayoutManager(viewGroup.getContext(), 1, false);
        this.layoutManager.setStackFromEnd(true);
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.chatLogView);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(this.scrollListener);
        recyclerView.setLayoutManager(this.layoutManager);
        inflate.findViewById(R.id.sendMessageButton).setOnClickListener(this);
        inflate.findViewById(R.id.chat_speak_button).setOnClickListener(this);
        inflate.findViewById(R.id.chat_voice_call_button).setOnClickListener(this);
        EditText editText = (EditText) inflate.findViewById(R.id.sendMessageText);
        editText.setOnKeyListener(this);
        editText.addTextChangedListener(this.textWatcher);
        editText.setVisibility(this.vrMode ? 8 : 0);
        inflate.findViewById(R.id.sendMessageButton).setVisibility(this.vrMode ? 8 : 0);
        inflate.findViewById(R.id.chat_vr_mode_controls).setVisibility(this.vrMode ? 0 : 8);
        return inflate;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.ui.common.UserFunctionsFragment
    public void onCurrentLocationChanged(CurrentLocationInfo currentLocationInfo) {
        super.m585xeb75d0e9(currentLocationInfo);
        updateVrModeControls();
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0 && i == 66) {
            sendMessage();
            return true;
        }
        if (view instanceof TextView) {
            if (((TextView) view).getText().length() != 0) {
                setTypingNotify(true);
            } else {
                setTypingNotify(false);
            }
        }
        return false;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.UserFunctionsFragment, android.support.v4.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_chat_history_export /* 2131755769 */:
                exportChatHistory();
                return true;
            case R.id.item_chat_history_clear /* 2131755770 */:
                clearChatHistory();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onPause() {
        if (this.markDisplayedChatterID != null) {
            UserManager userManager = this.markDisplayedChatterID.getUserManager();
            if (userManager != null) {
                userManager.getChatterList().getActiveChattersManager().removeDisplayedChatter(this.markDisplayedChatterID);
            }
            this.markDisplayedChatterID = null;
        }
        setTypingNotify(false);
        super.onPause();
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.UserFunctionsFragment, android.support.v4.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        updateChatHistoryExists();
    }

    @Override // android.support.v4.app.Fragment
    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (i == 500 && iArr.length > 0 && iArr[0] == 0) {
            new ExportChatHistoryTask(getActivity()).execute(this.chatterID);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onResume() {
        UserManager userManager;
        super.onResume();
        this.markDisplayedChatterID = this.chatterID;
        if (this.markDisplayedChatterID != null && (userManager = this.markDisplayedChatterID.getUserManager()) != null) {
            userManager.getChatterList().getActiveChattersManager().addDisplayedChatter(this.markDisplayedChatterID);
        }
        updateVisibleRange();
        updateChatHistoryExists();
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment
    protected void onShowUser(@Nullable ChatterID chatterID) {
        UserManager userManager;
        UserManager userManager2;
        Debug.Printf("ChatFragment: displaying for user %s", chatterID);
        if (!Objects.equal(this.markDisplayedChatterID, chatterID) && isFragmentVisible()) {
            if (this.markDisplayedChatterID != null && (userManager2 = this.markDisplayedChatterID.getUserManager()) != null) {
                userManager2.getChatterList().getActiveChattersManager().removeDisplayedChatter(this.markDisplayedChatterID);
            }
            this.markDisplayedChatterID = chatterID;
            if (this.markDisplayedChatterID != null && (userManager = this.markDisplayedChatterID.getUserManager()) != null) {
                userManager.getChatterList().getActiveChattersManager().addDisplayedChatter(this.markDisplayedChatterID);
            }
        }
        if (this.adapter != null) {
            this.adapter.stopLoading();
            this.adapter = null;
        }
        if (chatterID == null || this.userManager == null) {
            this.agentCircuit.unsubscribe();
            this.voiceActiveChatter.unsubscribe();
            this.voiceChatInfo.unsubscribe();
        } else {
            this.agentCircuit.subscribe(UserManager.agentCircuits(), chatterID.agentUUID);
            this.adapter = new ChatRecyclerAdapter(getActivity(), this.userManager, chatterID);
            this.adapter.setOnUserPicClickedListener(this);
            if (this.vrMode) {
                this.voiceActiveChatter.subscribe(this.userManager.getVoiceActiveChatter(), SubscriptionSingleKey.Value);
            } else {
                this.voiceActiveChatter.unsubscribe();
            }
        }
        View view = getView();
        if (view != null) {
            Debug.Printf("ChatFragment: setting adapter to %s", this.adapter);
            ((RecyclerView) view.findViewById(R.id.chatLogView)).setAdapter(this.adapter);
            if (this.adapter != null) {
                this.adapter.startLoading(this);
            }
            TypingIndicatorView typingIndicatorView = (TypingIndicatorView) view.findViewById(R.id.typing_indicator);
            if (typingIndicatorView != null) {
                typingIndicatorView.setChatterID(chatterID);
            }
            VoiceStatusView voiceStatusView = (VoiceStatusView) view.findViewById(R.id.voice_status_view);
            if (voiceStatusView != null) {
                voiceStatusView.setChatterID(chatterID);
            }
            updateVisibleRange();
            updateChatHistoryExists();
            updateVrModeControls();
        }
        setTypingNotify(false);
    }

    @Override // com.lumiyaviewer.lumiya.ui.chat.ChatRecyclerAdapter.OnUserPicClickedListener
    public void onUserPicClicked(ChatMessageSource chatMessageSource) {
        int objectLocalID;
        if (this.userManager != null) {
            if (chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.User || chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.Group) {
                handleUserViewProfile(chatMessageSource.getDefaultChatter(this.userManager.getUserID()));
            } else if (chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.Object && (chatMessageSource instanceof ChatMessageSourceObject)) {
                ChatMessageSourceObject chatMessageSourceObject = (ChatMessageSourceObject) chatMessageSource;
                SLAgentCircuit data = this.agentCircuit.getData();
                if (data == null || (objectLocalID = data.getGridConnection().parcelInfo.getObjectLocalID(chatMessageSourceObject.uuid)) == -1) {
                    return;
                }
                DetailsActivity.showEmbeddedDetails(getActivity(), ObjectDetailsFragment.class, ObjectDetailsFragment.makeSelection(this.userManager.getUserID(), objectLocalID));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.ui.common.UserFunctionsFragment
    public void onVoiceLoginStatusChanged(Boolean bool) {
        super.m584xeb75d0e8(bool);
        updateVrModeControls();
    }
}
