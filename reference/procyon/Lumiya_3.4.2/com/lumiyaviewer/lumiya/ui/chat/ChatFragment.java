// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.objects.ObjectDetailsFragment;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.Menu;
import android.support.v4.app.FragmentActivity;
import com.lumiyaviewer.lumiya.ui.render.CardboardActivity;
import android.widget.EditText;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.widget.TextView;
import android.os.Parcelable;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Context;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import android.text.Editable;
import android.view.View;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import android.text.TextWatcher;
import android.support.v7.widget.RecyclerView;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.os.Handler;
import android.view.MenuItem;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import javax.annotation.Nullable;
import android.view.View$OnKeyListener;
import android.view.View$OnClickListener;
import com.lumiyaviewer.lumiya.ui.common.UserFunctionsFragment;

public class ChatFragment extends UserFunctionsFragment implements View$OnClickListener, View$OnKeyListener, OnAdapterDataChanged, OnUserPicClickedListener
{
    private static final int PERMISSION_REQUEST_CODE = 500;
    private static final long typingTimeout = 5000L;
    @Nullable
    private ChatRecyclerAdapter adapter;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit;
    @Nullable
    private MenuItem clearChatHistoryMenuItem;
    @Nullable
    private MenuItem exportChatHistoryMenuItem;
    private boolean hasMoreItems;
    private long lastTypingEventSent;
    @Nullable
    private ChatLayoutManager layoutManager;
    private final Handler mHandler;
    @Nullable
    private ChatterID markDisplayedChatterID;
    private final OnScrollListener scrollListener;
    private boolean scrollToBottomForceDown;
    private boolean scrollToBottomNeeded;
    private final Runnable scrollToBottomRunnable;
    private boolean scrollToBottomRunnablePosted;
    private final TextWatcher textWatcher;
    @Nullable
    private ChatterID typingNotifiedChatter;
    private boolean updateRunnablePosted;
    private final Runnable updateVisibleRangeRunnable;
    private final SubscriptionData<SubscriptionSingleKey, ChatterID> voiceActiveChatter;
    private final SubscriptionData<ChatterID, VoiceChatInfo> voiceChatInfo;
    private boolean vrMode;
    
    public ChatFragment() {
        this.vrMode = false;
        this.layoutManager = null;
        this.adapter = null;
        this.scrollToBottomNeeded = false;
        this.hasMoreItems = false;
        this.typingNotifiedChatter = null;
        this.markDisplayedChatterID = null;
        this.exportChatHistoryMenuItem = null;
        this.clearChatHistoryMenuItem = null;
        this.agentCircuit = new SubscriptionData<UUID, SLAgentCircuit>(UIThreadExecutor.getInstance(), new _$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg$2(this));
        this.voiceActiveChatter = new SubscriptionData<SubscriptionSingleKey, ChatterID>(UIThreadExecutor.getInstance(), new _$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg$3(this));
        this.voiceChatInfo = new SubscriptionData<ChatterID, VoiceChatInfo>(UIThreadExecutor.getInstance(), new _$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg$4(this));
        this.scrollListener = new OnScrollListener() {
            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, final int n) {
                if (n == 0 || n == 1) {
                    ChatFragment.this.scrollToBottomNeeded = false;
                }
            }
            
            @Override
            public void onScrolled(final RecyclerView recyclerView, final int n, final int n2) {
                ChatFragment.this.updateVisibleRange();
            }
        };
        this.updateRunnablePosted = false;
        this.mHandler = new Handler();
        this.updateVisibleRangeRunnable = new Runnable() {
            @Override
            public void run() {
                ChatFragment.this.updateRunnablePosted = false;
                final View view = ChatFragment.this.getView();
                if (ChatFragment.this.adapter != null && ChatFragment.this.layoutManager != null && view != null) {
                    final RecyclerView recyclerView = (RecyclerView)view.findViewById(2131755288);
                    Debug.Printf("UpdateVisibleRange: pending %b, first %d, last %d", recyclerView.hasPendingAdapterUpdates(), ChatFragment.this.layoutManager.findFirstVisibleItemPosition(), ChatFragment.this.layoutManager.findLastVisibleItemPosition());
                    if (!recyclerView.hasPendingAdapterUpdates()) {
                        ChatFragment.this.updateHasMoreItems();
                        final int firstVisibleItemPosition = ChatFragment.this.layoutManager.findFirstVisibleItemPosition();
                        int lastVisibleItemPosition = ChatFragment.this.layoutManager.findLastVisibleItemPosition();
                        if (ChatFragment.this.scrollToBottomNeeded) {
                            lastVisibleItemPosition = ChatFragment.this.adapter.getItemCount() - 1;
                        }
                        ChatFragment.this.adapter.setVisibleRange(firstVisibleItemPosition, lastVisibleItemPosition);
                    }
                    else {
                        ChatFragment.this.updateRunnablePosted = true;
                        ChatFragment.this.mHandler.post(ChatFragment.this.updateVisibleRangeRunnable);
                    }
                }
            }
        };
        this.scrollToBottomRunnablePosted = false;
        this.scrollToBottomForceDown = false;
        this.scrollToBottomRunnable = new Runnable() {
            @Override
            public void run() {
                ChatFragment.this.scrollToBottomRunnablePosted = false;
                if (ChatFragment.this.getView() != null) {
                    final RecyclerView recyclerView = (RecyclerView)ChatFragment.this.getView().findViewById(2131755288);
                    if (!recyclerView.hasPendingAdapterUpdates()) {
                        if (ChatFragment.this.adapter != null && ChatFragment.this.layoutManager != null) {
                            if (ChatFragment.this.scrollToBottomForceDown && ChatFragment.this.adapter.hasMoreItemsAtBottom()) {
                                ChatFragment.this.scrollToBottomNeeded = false;
                                ChatFragment.this.adapter.restartAtBottom();
                            }
                            final int itemCount = ChatFragment.this.adapter.getItemCount();
                            if (itemCount > 0) {
                                ChatFragment.this.layoutManager.setScrollMode(ChatFragment.this.scrollToBottomForceDown);
                                recyclerView.smoothScrollToPosition(itemCount - 1);
                                ChatFragment.this.scrollToBottomNeeded = true;
                                ChatFragment.this.scrollToBottomForceDown = false;
                            }
                        }
                    }
                    else {
                        ChatFragment.this.scrollToBottomRunnablePosted = true;
                        ChatFragment.this.mHandler.post(ChatFragment.this.scrollToBottomRunnable);
                    }
                }
            }
        };
        this.textWatcher = (TextWatcher)new TextWatcher() {
            public void afterTextChanged(final Editable editable) {
            }
            
            public void beforeTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
            }
            
            public void onTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
                if (charSequence.length() != 0) {
                    ChatFragment.this.setTypingNotify(true);
                }
                else {
                    ChatFragment.this.setTypingNotify(false);
                }
            }
        };
    }
    
    private void clearChatHistory() {
        new AlertDialog$Builder(this.getContext()).setMessage(2131296444).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg$1(this)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg()).create().show();
    }
    
    private void exportChatHistory() {
        if (this.chatterID != null) {
            if (ContextCompat.checkSelfPermission(this.getContext(), "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                ActivityCompat.requestPermissions(this.getActivity(), new String[] { "android.permission.WRITE_EXTERNAL_STORAGE" }, 500);
            }
            else {
                new ExportChatHistoryTask((Context)this.getActivity()).execute((Object[])new ChatterID[] { this.chatterID });
            }
        }
    }
    
    public static Bundle makeSelection(final ChatterID chatterID) {
        final Bundle selection = ChatterFragment.makeSelection(chatterID);
        selection.putParcelable("chatterID", (Parcelable)chatterID);
        return selection;
    }
    
    private void onAgentCircuit(final SLAgentCircuit slAgentCircuit) {
        final int n = 0;
        final View view = this.getView();
        if (view != null) {
            String s;
            if (slAgentCircuit != null) {
                s = "present";
            }
            else {
                s = "not present";
            }
            Debug.Printf("agentCircuit is now %s", s);
            final View viewById = view.findViewById(2131755295);
            int visibility;
            if (slAgentCircuit != null && (this.vrMode ^ true)) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            viewById.setVisibility(visibility);
            final View viewById2 = view.findViewById(2131755292);
            int visibility2;
            if (slAgentCircuit != null && this.vrMode) {
                visibility2 = n;
            }
            else {
                visibility2 = 8;
            }
            viewById2.setVisibility(visibility2);
        }
    }
    
    private void onVoiceActiveChatter(final ChatterID chatterID) {
        if (chatterID != null && this.userManager != null && this.vrMode) {
            this.voiceChatInfo.subscribe(this.userManager.getVoiceChatInfo(), chatterID);
        }
        else {
            this.voiceChatInfo.unsubscribe();
        }
        this.updateVrModeControls();
    }
    
    private void onVoiceChatInfo(final VoiceChatInfo voiceChatInfo) {
        this.updateVrModeControls();
    }
    
    private void scrollToBottom(final boolean b) {
        if (this.getView() != null && (this.scrollToBottomRunnablePosted ^ true)) {
            this.scrollToBottomNeeded = true;
            this.scrollToBottomRunnablePosted = true;
            this.scrollToBottomForceDown |= b;
            this.mHandler.post(this.scrollToBottomRunnable);
        }
    }
    
    private void sendMessage() {
        this.setTypingNotify(false);
        final View view = this.getView();
        if (this.chatterID == null || view == null) {
            return;
        }
        final String string = ((TextView)view.findViewById(2131755294)).getText().toString();
        if (string.equals("")) {
            return;
        }
        if (this.userManager != null) {
            final SLAgentCircuit activeAgentCircuit = this.userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null) {
                activeAgentCircuit.SendChatMessage(this.chatterID, string);
                ((TextView)this.getView().findViewById(2131755294)).setText((CharSequence)"");
            }
        }
        this.scrollToBottom(false);
    }
    
    private boolean sendTypingNotify(@Nullable final ChatterID chatterID, final boolean b) {
        if (chatterID instanceof ChatterID.ChatterIDUser) {
            final UserManager userManager = chatterID.getUserManager();
            if (userManager != null) {
                final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
                if (activeAgentCircuit != null) {
                    activeAgentCircuit.sendTypingNotify(((ChatterID.ChatterIDUser)chatterID).getChatterUUID(), b);
                    return true;
                }
            }
        }
        return false;
    }
    
    private void setTypingNotify(final boolean b) {
        if (b) {
            final long currentTimeMillis = System.currentTimeMillis();
            if (!Objects.equal(this.chatterID, this.typingNotifiedChatter)) {
                if (this.typingNotifiedChatter != null) {
                    this.sendTypingNotify(this.typingNotifiedChatter, false);
                    this.typingNotifiedChatter = null;
                }
                if (this.sendTypingNotify(this.chatterID, true)) {
                    this.typingNotifiedChatter = this.chatterID;
                    this.lastTypingEventSent = currentTimeMillis;
                }
            }
            else if (this.typingNotifiedChatter != null && currentTimeMillis >= this.lastTypingEventSent + 5000L) {
                this.lastTypingEventSent = currentTimeMillis;
                this.sendTypingNotify(this.typingNotifiedChatter, true);
            }
        }
        else if (this.typingNotifiedChatter != null) {
            this.sendTypingNotify(this.typingNotifiedChatter, false);
            this.typingNotifiedChatter = null;
        }
    }
    
    private void updateChatHistoryExists() {
        final boolean visible = this.adapter != null && this.adapter.getItemCount() != 0;
        Debug.Printf("ChatHistory: chat history exists %b", visible);
        if (this.clearChatHistoryMenuItem != null) {
            this.clearChatHistoryMenuItem.setVisible(visible);
        }
        if (this.exportChatHistoryMenuItem != null) {
            this.exportChatHistoryMenuItem.setVisible(visible);
        }
    }
    
    private void updateHasMoreItems() {
        boolean hasMoreItemsAtBottom = true;
        final View view = this.getView();
        if (view != null) {
            this.hasMoreItems = false;
            if (this.layoutManager != null && this.adapter != null) {
                final int itemCount = this.adapter.getItemCount();
                int lastVisibleItemPosition = this.layoutManager.findLastVisibleItemPosition();
                if (this.scrollToBottomNeeded) {
                    lastVisibleItemPosition = this.adapter.getItemCount() - 1;
                }
                if (itemCount > 1 && lastVisibleItemPosition != -1) {
                    if (lastVisibleItemPosition >= itemCount - 2) {
                        hasMoreItemsAtBottom = this.adapter.hasMoreItemsAtBottom();
                    }
                    this.hasMoreItems = hasMoreItemsAtBottom;
                }
            }
            final View viewById = view.findViewById(2131755290);
            int visibility;
            if (this.hasMoreItems) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            viewById.setVisibility(visibility);
        }
    }
    
    private void updateVisibleRange() {
        if (this.adapter != null && this.layoutManager != null && this.getView() != null && (this.updateRunnablePosted ^ true)) {
            this.updateRunnablePosted = true;
            this.mHandler.post(this.updateVisibleRangeRunnable);
        }
    }
    
    private void updateVrModeControls() {
        final boolean b = true;
        final int n = 0;
        final View view = this.getView();
        if (view != null) {
            if (this.vrMode) {
                view.findViewById(2131755291).setVisibility(0);
                int n2;
                int n3;
                if (this.isVoiceLoggedIn()) {
                    boolean b2;
                    if (this.voiceActiveChatter.getData() != null) {
                        final VoiceChatInfo voiceChatInfo = this.voiceChatInfo.getData();
                        if (voiceChatInfo != null && voiceChatInfo.state != VoiceChatInfo.VoiceChatState.None) {
                            b2 = true;
                        }
                        else {
                            b2 = false;
                        }
                    }
                    else {
                        b2 = false;
                    }
                    if (!b2) {
                        if (this.chatterID != null && this.userManager != null) {
                            n2 = (b2 ? 1 : 0);
                            n3 = (b ? 1 : 0);
                            if (this.chatterID instanceof ChatterID.ChatterIDLocal) {
                                final CurrentLocationInfo currentLocationInfoSnapshot = this.userManager.getCurrentLocationInfoSnapshot();
                                if (currentLocationInfoSnapshot != null && currentLocationInfoSnapshot.parcelVoiceChannel() != null) {
                                    n3 = (b ? 1 : 0);
                                    n2 = (b2 ? 1 : 0);
                                }
                                else {
                                    n3 = 0;
                                    n2 = (b2 ? 1 : 0);
                                }
                            }
                        }
                        else {
                            n3 = 0;
                            n2 = (b2 ? 1 : 0);
                        }
                    }
                    else {
                        n3 = 0;
                        n2 = (b2 ? 1 : 0);
                    }
                }
                else {
                    n2 = 0;
                    n3 = 0;
                }
                final View viewById = view.findViewById(2131755292);
                int visibility;
                if (n2 != 0) {
                    visibility = 8;
                }
                else {
                    visibility = 0;
                }
                viewById.setVisibility(visibility);
                final View viewById2 = view.findViewById(2131755293);
                int visibility2;
                if (n3 != 0) {
                    visibility2 = n;
                }
                else {
                    visibility2 = 8;
                }
                viewById2.setVisibility(visibility2);
            }
            else {
                view.findViewById(2131755291).setVisibility(8);
            }
        }
    }
    
    public void onAdapterDataAddedAtEnd() {
        if (this.layoutManager != null && (!this.hasMoreItems || ((RecyclerView.LayoutManager)this.layoutManager).isSmoothScrolling() || this.scrollToBottomNeeded)) {
            this.scrollToBottom(false);
        }
        this.updateChatHistoryExists();
    }
    
    public void onAdapterDataChanged() {
        this.updateVisibleRange();
        this.updateChatHistoryExists();
    }
    
    public void onAdapterDataReloaded() {
        this.scrollToBottomNeeded = false;
        this.updateVisibleRange();
        this.updateChatHistoryExists();
    }
    
    public void onChatterNameUpdated(final ChatterNameRetriever chatterNameRetriever) {
        super.onChatterNameUpdated(chatterNameRetriever);
        final View view = this.getView();
        if (view != null) {
            String resolvedName;
            if (this.chatterID instanceof ChatterID.ChatterIDLocal) {
                resolvedName = "local chat";
            }
            else {
                resolvedName = chatterNameRetriever.getResolvedName();
            }
            final EditText editText = (EditText)view.findViewById(2131755294);
            String string;
            if (resolvedName != null) {
                string = this.getString(2131296428, new Object[] { resolvedName });
            }
            else {
                string = null;
            }
            editText.setHint((CharSequence)string);
        }
    }
    
    public void onClick(final View view) {
        switch (view.getId()) {
            case 2131755295: {
                this.sendMessage();
                break;
            }
            case 2131755290: {
                this.scrollToBottom(true);
                break;
            }
            case 2131755292: {
                final FragmentActivity activity = this.getActivity();
                if (activity instanceof CardboardActivity && this.chatterID != null) {
                    ((CardboardActivity)activity).startDictation(this.chatterID);
                    break;
                }
                break;
            }
            case 2131755293: {
                if (this.chatterID != null) {
                    this.handleStartVoice(this.chatterID);
                    break;
                }
                break;
            }
        }
    }
    
    @Override
    public void onCreate(@Nullable Bundle arguments) {
        super.onCreate(arguments);
        this.setHasOptionsMenu(true);
        arguments = this.getArguments();
        if (arguments != null && arguments.getBoolean("vrMode")) {
            this.vrMode = true;
        }
    }
    
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(2131886081, menu);
        this.exportChatHistoryMenuItem = menu.findItem(2131755769);
        this.clearChatHistoryMenuItem = menu.findItem(2131755770);
        this.updateChatHistoryExists();
    }
    
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        final int n = 0;
        super.onCreateView(layoutInflater, viewGroup, bundle);
        final View inflate = layoutInflater.inflate(2130968612, viewGroup, false);
        inflate.findViewById(2131755290).setOnClickListener((View$OnClickListener)this);
        (this.layoutManager = new ChatLayoutManager(viewGroup.getContext(), 1, false)).setStackFromEnd(true);
        final RecyclerView recyclerView = (RecyclerView)inflate.findViewById(2131755288);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(this.scrollListener);
        recyclerView.setLayoutManager((RecyclerView.LayoutManager)this.layoutManager);
        inflate.findViewById(2131755295).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755292).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755293).setOnClickListener((View$OnClickListener)this);
        final EditText editText = (EditText)inflate.findViewById(2131755294);
        editText.setOnKeyListener((View$OnKeyListener)this);
        editText.addTextChangedListener(this.textWatcher);
        int visibility;
        if (this.vrMode) {
            visibility = 8;
        }
        else {
            visibility = 0;
        }
        editText.setVisibility(visibility);
        final View viewById = inflate.findViewById(2131755295);
        int visibility2;
        if (this.vrMode) {
            visibility2 = 8;
        }
        else {
            visibility2 = 0;
        }
        viewById.setVisibility(visibility2);
        final View viewById2 = inflate.findViewById(2131755291);
        int visibility3;
        if (this.vrMode) {
            visibility3 = n;
        }
        else {
            visibility3 = 8;
        }
        viewById2.setVisibility(visibility3);
        return inflate;
    }
    
    @Override
    protected void onCurrentLocationChanged(final CurrentLocationInfo currentLocationInfo) {
        super.onCurrentLocationChanged(currentLocationInfo);
        this.updateVrModeControls();
    }
    
    public boolean onKey(final View view, final int n, final KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0 && n == 66) {
            this.sendMessage();
            return true;
        }
        if (view instanceof TextView) {
            if (((TextView)view).getText().length() != 0) {
                this.setTypingNotify(true);
            }
            else {
                this.setTypingNotify(false);
            }
        }
        return false;
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            default: {
                return super.onOptionsItemSelected(menuItem);
            }
            case 2131755769: {
                this.exportChatHistory();
                return true;
            }
            case 2131755770: {
                this.clearChatHistory();
                return true;
            }
        }
    }
    
    public void onPause() {
        if (this.markDisplayedChatterID != null) {
            final UserManager userManager = this.markDisplayedChatterID.getUserManager();
            if (userManager != null) {
                userManager.getChatterList().getActiveChattersManager().removeDisplayedChatter(this.markDisplayedChatterID);
            }
            this.markDisplayedChatterID = null;
        }
        this.setTypingNotify(false);
        super.onPause();
    }
    
    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.updateChatHistoryExists();
    }
    
    public void onRequestPermissionsResult(final int n, @NonNull final String[] array, @NonNull final int[] array2) {
        if (n == 500 && array2.length > 0 && array2[0] == 0) {
            new ExportChatHistoryTask((Context)this.getActivity()).execute((Object[])new ChatterID[] { this.chatterID });
        }
    }
    
    public void onResume() {
        super.onResume();
        this.markDisplayedChatterID = this.chatterID;
        if (this.markDisplayedChatterID != null) {
            final UserManager userManager = this.markDisplayedChatterID.getUserManager();
            if (userManager != null) {
                userManager.getChatterList().getActiveChattersManager().addDisplayedChatter(this.markDisplayedChatterID);
            }
        }
        this.updateVisibleRange();
        this.updateChatHistoryExists();
    }
    
    protected void onShowUser(@Nullable final ChatterID chatterID) {
        Debug.Printf("ChatFragment: displaying for user %s", chatterID);
        if (!Objects.equal(this.markDisplayedChatterID, chatterID) && this.isFragmentVisible()) {
            if (this.markDisplayedChatterID != null) {
                final UserManager userManager = this.markDisplayedChatterID.getUserManager();
                if (userManager != null) {
                    userManager.getChatterList().getActiveChattersManager().removeDisplayedChatter(this.markDisplayedChatterID);
                }
            }
            this.markDisplayedChatterID = chatterID;
            if (this.markDisplayedChatterID != null) {
                final UserManager userManager2 = this.markDisplayedChatterID.getUserManager();
                if (userManager2 != null) {
                    userManager2.getChatterList().getActiveChattersManager().addDisplayedChatter(this.markDisplayedChatterID);
                }
            }
        }
        if (this.adapter != null) {
            this.adapter.stopLoading();
            this.adapter = null;
        }
        if (chatterID != null && this.userManager != null) {
            this.agentCircuit.subscribe(UserManager.agentCircuits(), chatterID.agentUUID);
            (this.adapter = new ChatRecyclerAdapter((Context)this.getActivity(), this.userManager, chatterID)).setOnUserPicClickedListener((ChatRecyclerAdapter.OnUserPicClickedListener)this);
            if (this.vrMode) {
                this.voiceActiveChatter.subscribe(this.userManager.getVoiceActiveChatter(), SubscriptionSingleKey.Value);
            }
            else {
                this.voiceActiveChatter.unsubscribe();
            }
        }
        else {
            this.agentCircuit.unsubscribe();
            this.voiceActiveChatter.unsubscribe();
            this.voiceChatInfo.unsubscribe();
        }
        final View view = this.getView();
        if (view != null) {
            final RecyclerView recyclerView = (RecyclerView)view.findViewById(2131755288);
            Debug.Printf("ChatFragment: setting adapter to %s", this.adapter);
            recyclerView.setAdapter((RecyclerView.Adapter)this.adapter);
            if (this.adapter != null) {
                this.adapter.startLoading((ChatRecyclerAdapter.OnAdapterDataChanged)this);
            }
            final TypingIndicatorView typingIndicatorView = (TypingIndicatorView)view.findViewById(2131755296);
            if (typingIndicatorView != null) {
                typingIndicatorView.setChatterID(chatterID);
            }
            final VoiceStatusView voiceStatusView = (VoiceStatusView)view.findViewById(2131755289);
            if (voiceStatusView != null) {
                voiceStatusView.setChatterID(chatterID);
            }
            this.updateVisibleRange();
            this.updateChatHistoryExists();
            this.updateVrModeControls();
        }
        this.setTypingNotify(false);
    }
    
    public void onUserPicClicked(final ChatMessageSource chatMessageSource) {
        if (this.userManager != null) {
            if (chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.User || chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.Group) {
                this.handleUserViewProfile(chatMessageSource.getDefaultChatter(this.userManager.getUserID()));
            }
            else if (chatMessageSource.getSourceType() == ChatMessageSource.ChatMessageSourceType.Object && chatMessageSource instanceof ChatMessageSourceObject) {
                final ChatMessageSourceObject chatMessageSourceObject = (ChatMessageSourceObject)chatMessageSource;
                final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
                if (slAgentCircuit != null) {
                    final int objectLocalID = slAgentCircuit.getGridConnection().parcelInfo.getObjectLocalID(chatMessageSourceObject.uuid);
                    if (objectLocalID != -1) {
                        DetailsActivity.showEmbeddedDetails(this.getActivity(), ObjectDetailsFragment.class, ObjectDetailsFragment.makeSelection(this.userManager.getUserID(), objectLocalID));
                    }
                }
            }
        }
    }
    
    @Override
    protected void onVoiceLoginStatusChanged(final Boolean b) {
        super.onVoiceLoginStatusChanged(b);
        this.updateVrModeControls();
    }
}
