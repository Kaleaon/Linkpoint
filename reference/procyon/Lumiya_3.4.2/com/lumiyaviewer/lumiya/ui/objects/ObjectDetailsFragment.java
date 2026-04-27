// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.objects;

import android.content.Intent;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import android.text.Editable;
import android.text.TextWatcher;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.Debug;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.Menu;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import android.widget.Toast;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import android.widget.EditText;
import com.google.common.base.Strings;
import android.widget.Button;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.react.Subscribable;
import android.view.View;
import android.widget.TextView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.objects.PayInfo;
import android.support.v4.app.Fragment;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import android.os.Bundle;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.DialogInterface$OnClickListener;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.ui.common.ChatterNameDisplayer;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState;
import javax.annotation.Nullable;
import android.view.MenuItem;
import com.lumiyaviewer.lumiya.slproto.chat.generic.OnChatEventListener;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import android.view.View$OnClickListener;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;

public class ObjectDetailsFragment extends FragmentWithTitle implements ReloadableFragment, View$OnClickListener, OnLoadableDataChangedListener
{
    private static final String LOCAL_ID_KEY = "localID";
    private static final int[] objectPayButtons;
    private final SubscriptionData<SubscriptionSingleKey, Integer> balanceSubscription;
    private final OnChatEventListener chatEventListener;
    private final LoadableMonitor loadableMonitor;
    @Nullable
    private MenuItem menuItemObjectBlock;
    @Nullable
    private MenuItem menuItemObjectDelete;
    @Nullable
    private MenuItem menuItemObjectTake;
    @Nullable
    private MenuItem menuItemObjectTakeCopy;
    private final SubscriptionData<SubscriptionSingleKey, MyAvatarState> myAvatarState;
    private int objectLocalID;
    private final SubscriptionData<Integer, SLObjectProfileData> objectProfile;
    @Nullable
    private SLObjectProfileData objectProfileData;
    private final ChatterNameDisplayer ownerNameDisplayer;
    
    static {
        objectPayButtons = new int[] { 2131755556, 2131755557, 2131755558, 2131755559 };
    }
    
    public ObjectDetailsFragment() {
        this.objectProfileData = null;
        this.menuItemObjectTake = null;
        this.menuItemObjectTakeCopy = null;
        this.menuItemObjectDelete = null;
        this.menuItemObjectBlock = null;
        this.objectLocalID = 0;
        this.objectProfile = new SubscriptionData<Integer, SLObjectProfileData>(UIThreadExecutor.getInstance());
        this.balanceSubscription = new SubscriptionData<SubscriptionSingleKey, Integer>(UIThreadExecutor.getInstance());
        this.myAvatarState = new SubscriptionData<SubscriptionSingleKey, MyAvatarState>(UIThreadExecutor.getInstance());
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.objectProfile }).withOptionalLoadables(this.balanceSubscription, this.myAvatarState).withDataChangedListener((LoadableMonitor.OnLoadableDataChangedListener)this);
        this.ownerNameDisplayer = new ChatterNameDisplayer();
        this.chatEventListener = new _$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8$3(this);
    }
    
    private void buyObject() {
        final SLObjectProfileData objectProfileData = this.objectProfileData;
        final UserManager userManager = this.getUserManager();
        final int objectLocalID = this.objectLocalID;
        if (objectProfileData != null && userManager != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder((Context)this.getActivity());
            builder.setMessage(String.format(this.getString(2131296787), objectProfileData.name().or(this.getString(2131296825)), objectProfileData.salePrice())).setCancelable(false).setPositiveButton("Yes", (DialogInterface$OnClickListener)new _$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8$5(objectLocalID, userManager, objectProfileData)).setNegativeButton("No", (DialogInterface$OnClickListener)new _$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8());
            builder.show();
        }
    }
    
    @Nullable
    private UserManager getUserManager() {
        return ActivityUtils.getUserManager(this.getArguments());
    }
    
    public static Bundle makeSelection(final UUID uuid, final int n) {
        final Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        bundle.putInt("localID", n);
        return bundle;
    }
    
    private void openObjectContents() {
        final UserManager userManager = this.getUserManager();
        if (this.objectProfileData != null && this.objectLocalID != 0 && userManager != null) {
            DetailsActivity.showEmbeddedDetails(this.getActivity(), TaskInventoryFragment.class, TaskInventoryFragment.makeSelection(userManager.getUserID(), this.objectProfileData.objectUUID(), this.objectLocalID));
        }
    }
    
    private void payObject(final int i) {
        final SLObjectProfileData objectProfileData = this.objectProfileData;
        final UserManager userManager = this.getUserManager();
        if (objectProfileData != null && userManager != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder((Context)this.getActivity());
            builder.setMessage(String.format(this.getString(2131296832), objectProfileData.name().or(this.getString(2131296825)), i)).setCancelable(false).setPositiveButton("Yes", (DialogInterface$OnClickListener)new _$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8$6(i, userManager, objectProfileData)).setNegativeButton("No", (DialogInterface$OnClickListener)new _$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8$2());
            builder.show();
        }
    }
    
    private void payObjectQuick(int intValue) {
        if (this.objectProfileData != null) {
            final PayInfo payInfo = this.objectProfileData.payInfo();
            if (payInfo != null) {
                final ImmutableList<Integer> payPrices = payInfo.payPrices();
                if (payPrices != null && intValue >= 0 && intValue < payPrices.size()) {
                    intValue = (int)payPrices.get(intValue);
                    if (intValue != 0) {
                        this.payObject(intValue);
                    }
                }
            }
        }
    }
    
    private void showDeadObject() {
        final View view = this.getView();
        if (view != null) {
            view.findViewById(2131755532).setVisibility(8);
            view.findViewById(2131755533).setVisibility(0);
            ((TextView)view.findViewById(2131755533)).setText(2131296798);
            view.findViewById(2131755534).setVisibility(8);
        }
    }
    
    private void showObject(final int n) {
        this.objectLocalID = n;
        final UserManager userManager = this.getUserManager();
        if (userManager != null) {
            this.objectProfile.subscribe(userManager.getObjectsManager().getObjectProfile(), n);
            this.myAvatarState.subscribe(userManager.getObjectsManager().myAvatarState(), SubscriptionSingleKey.Value);
            this.balanceSubscription.subscribe(userManager.getBalanceManager().getBalance(), SubscriptionSingleKey.Value);
        }
    }
    
    private void showObjectNotLoaded() {
        final View view = this.getView();
        if (view != null) {
            view.findViewById(2131755532).setVisibility(8);
            view.findViewById(2131755533).setVisibility(0);
            ((TextView)view.findViewById(2131755533)).setText(2131296834);
            view.findViewById(2131755534).setVisibility(8);
        }
    }
    
    private void showObjectOwnerInfo() {
        final UserManager userManager = this.getUserManager();
        if (this.objectProfileData != null && userManager != null) {
            final UUID ownerUUID = this.objectProfileData.ownerUUID();
            if (ownerUUID != null) {
                DetailsActivity.showEmbeddedDetails(this.getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(userManager.getUserID(), ownerUUID)));
            }
        }
    }
    
    private void showObjectProfile(final SLObjectProfileData objectProfileData) {
        this.objectProfileData = objectProfileData;
        final View view = this.getView();
        final UserManager userManager = this.getUserManager();
        Label_0077: {
            if (view != null) {
                view.findViewById(2131755532).setVisibility(8);
                if (!objectProfileData.isDead()) {
                    view.findViewById(2131755534).setVisibility(0);
                    view.findViewById(2131755533).setVisibility(8);
                    final View viewById = view.findViewById(2131755268);
                    int visibility;
                    if (objectProfileData.isTouchable()) {
                        visibility = 0;
                    }
                    else {
                        visibility = 8;
                    }
                    viewById.setVisibility(visibility);
                    final Button button = (Button)view.findViewById(2131755268);
                    String text;
                    if (Strings.isNullOrEmpty(objectProfileData.touchName())) {
                        text = this.getString(2131296842);
                    }
                    else {
                        text = objectProfileData.touchName();
                    }
                    button.setText((CharSequence)text);
                    while (true) {
                        Label_1117: {
                            if (userManager == null) {
                                break Label_1117;
                            }
                            final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
                            if (activeAgentCircuit == null) {
                                break Label_1117;
                            }
                            final SLModules modules = activeAgentCircuit.getModules();
                            if (modules == null) {
                                break Label_1117;
                            }
                            int canSit = modules.rlvController.canSit() ? 1 : 0;
                            final MyAvatarState myAvatarState = this.myAvatarState.getData();
                            int n;
                            if (myAvatarState != null) {
                                if (myAvatarState.isSitting() && myAvatarState.sittingOn() == this.objectLocalID) {
                                    n = 1;
                                    canSit = 0;
                                }
                                else {
                                    n = 0;
                                }
                            }
                            else {
                                n = 0;
                            }
                            final View viewById2 = view.findViewById(2131755269);
                            int visibility2;
                            if (canSit != 0) {
                                visibility2 = 0;
                            }
                            else {
                                visibility2 = 8;
                            }
                            viewById2.setVisibility(visibility2);
                            final View viewById3 = view.findViewById(2131755538);
                            int visibility3;
                            if (n != 0) {
                                visibility3 = 0;
                            }
                            else {
                                visibility3 = 8;
                            }
                            viewById3.setVisibility(visibility3);
                            ((TextView)view.findViewById(2131755536)).setText((CharSequence)objectProfileData.name().or(this.getString(2131296825)));
                            ((TextView)view.findViewById(2131755537)).setText((CharSequence)objectProfileData.description().or(""));
                            final View viewById4 = view.findViewById(2131755540);
                            int visibility4;
                            if (objectProfileData.ownerUUID() != null) {
                                visibility4 = 0;
                            }
                            else {
                                visibility4 = 8;
                            }
                            viewById4.setVisibility(visibility4);
                            final View viewById5 = view.findViewById(2131755543);
                            int visibility5;
                            if (objectProfileData.floatingText().isPresent()) {
                                visibility5 = 0;
                            }
                            else {
                                visibility5 = 8;
                            }
                            viewById5.setVisibility(visibility5);
                            ((TextView)view.findViewById(2131755544)).setText((CharSequence)objectProfileData.floatingText().or(""));
                            final View viewById6 = view.findViewById(2131755545);
                            int visibility6;
                            if (objectProfileData.saleType() != 0) {
                                visibility6 = 0;
                            }
                            else {
                                visibility6 = 8;
                            }
                            viewById6.setVisibility(visibility6);
                            ((TextView)view.findViewById(2131755546)).setText((CharSequence)this.getString(2131296789, new Object[] { objectProfileData.salePrice() }));
                            final Integer n2 = this.balanceSubscription.getData();
                            if (n2 != null) {
                                ((TextView)view.findViewById(2131755548)).setText((CharSequence)this.getString(2131296783, new Object[] { n2 }));
                            }
                            else {
                                ((TextView)view.findViewById(2131755548)).setText((CharSequence)"");
                            }
                            PayInfo payInfo;
                            if (objectProfileData.isPayable()) {
                                payInfo = objectProfileData.payInfo();
                            }
                            else {
                                payInfo = null;
                            }
                            if (payInfo != null) {
                                final ImmutableList<Integer> payPrices = payInfo.payPrices();
                                if (payPrices != null) {
                                    int n3 = 0;
                                    int n4 = 0;
                                    while (n3 < ObjectDetailsFragment.objectPayButtons.length && n3 < payPrices.size()) {
                                        int n5 = (int)payPrices.get(n3);
                                        if (n5 == -2) {
                                            n5 = payInfo.defaultPayPrice();
                                        }
                                        if (n5 <= 0) {
                                            view.findViewById(ObjectDetailsFragment.objectPayButtons[n3]).setVisibility(8);
                                            view.findViewById(ObjectDetailsFragment.objectPayButtons[n3]).setTag(2131755040, (Object)0);
                                        }
                                        else {
                                            ((Button)view.findViewById(ObjectDetailsFragment.objectPayButtons[n3])).setText((CharSequence)String.format(this.getString(2131296867), n5));
                                            view.findViewById(ObjectDetailsFragment.objectPayButtons[n3]).setVisibility(0);
                                            view.findViewById(ObjectDetailsFragment.objectPayButtons[n3]).setTag(2131755040, (Object)n5);
                                            ++n4;
                                        }
                                        ++n3;
                                    }
                                    final View viewById7 = view.findViewById(2131755555);
                                    int visibility7;
                                    if (n4 != 0) {
                                        visibility7 = 0;
                                    }
                                    else {
                                        visibility7 = 8;
                                    }
                                    viewById7.setVisibility(visibility7);
                                }
                                else {
                                    view.findViewById(2131755555).setVisibility(8);
                                }
                                if (payInfo.defaultPayPrice() != -1) {
                                    if (((EditText)view.findViewById(2131755553)).getText().toString().equals("")) {
                                        if (payInfo.defaultPayPrice() > 0) {
                                            ((EditText)view.findViewById(2131755553)).setText((CharSequence)this.getString(2131296829, new Object[] { payInfo.defaultPayPrice() }));
                                        }
                                        else {
                                            ((EditText)view.findViewById(2131755553)).setText((CharSequence)"");
                                        }
                                    }
                                    view.findViewById(2131755551).setVisibility(0);
                                }
                                else {
                                    view.findViewById(2131755551).setVisibility(8);
                                }
                                view.findViewById(2131755549).setVisibility(0);
                                break Label_0077;
                            }
                            view.findViewById(2131755549).setVisibility(8);
                            break Label_0077;
                        }
                        int canSit = 0;
                        continue;
                    }
                }
                view.findViewById(2131755533).setVisibility(0);
                view.findViewById(2131755534).setVisibility(8);
                ((TextView)view.findViewById(2131755533)).setText(2131296798);
            }
        }
        if (userManager != null && (objectProfileData.isDead() ^ true)) {
            if (objectProfileData.isPayable() && objectProfileData.payInfo() == null) {
                final UUID objectUUID = objectProfileData.objectUUID();
                final SLAgentCircuit activeAgentCircuit2 = userManager.getActiveAgentCircuit();
                if (activeAgentCircuit2 != null && objectUUID != null) {
                    activeAgentCircuit2.DoRequestPayPrice(objectUUID);
                }
            }
            final UUID ownerUUID = objectProfileData.ownerUUID();
            if (ownerUUID != null) {
                this.ownerNameDisplayer.setChatterID(ChatterID.getUserChatterID(userManager.getUserID(), ownerUUID));
            }
        }
        this.updateOptionsMenu();
    }
    
    private void sitOnObject() {
        final UserManager userManager = this.getUserManager();
        if (userManager != null && this.objectProfileData != null) {
            final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null) {
                final SLModules modules = activeAgentCircuit.getModules();
                if (modules != null) {
                    modules.avatarControl.SitOnObject(this.objectProfileData.objectUUID());
                }
            }
        }
    }
    
    private void standUp() {
        final UserManager userManager = this.getUserManager();
        if (userManager != null && this.objectProfileData != null) {
            final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null) {
                final SLModules modules = activeAgentCircuit.getModules();
                if (modules != null) {
                    modules.avatarControl.Stand();
                }
            }
        }
    }
    
    private void touchObject() {
        final UserManager userManager = this.getUserManager();
        if (userManager != null && this.objectLocalID != 0) {
            final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null) {
                activeAgentCircuit.TouchObject(this.objectLocalID);
            }
        }
    }
    
    private void updateOptionsMenu() {
        boolean visible = false;
        boolean copyable = false;
        final UserManager userManager = this.getUserManager();
        boolean equals;
        boolean visible2;
        boolean visible3;
        if (userManager != null && this.objectProfileData != null) {
            if (this.objectProfileData.isDead() ^ true) {
                equals = userManager.getUserID().equals(this.objectProfileData.ownerUUID());
                if (userManager.getUserID().equals(this.objectProfileData.ownerUUID())) {
                    copyable = this.objectProfileData.isCopyable();
                }
                final boolean equals2 = userManager.getUserID().equals(this.objectProfileData.ownerUUID());
                final boolean b = true;
                visible2 = copyable;
                visible3 = equals2;
                visible = b;
            }
            else {
                visible3 = false;
                visible2 = false;
                equals = false;
            }
        }
        else {
            visible3 = false;
            visible2 = false;
            equals = false;
        }
        if (this.menuItemObjectTake != null) {
            this.menuItemObjectTake.setVisible(equals);
        }
        if (this.menuItemObjectTakeCopy != null) {
            this.menuItemObjectTakeCopy.setVisible(visible2);
        }
        if (this.menuItemObjectDelete != null) {
            this.menuItemObjectDelete.setVisible(visible3);
        }
        if (this.menuItemObjectBlock != null) {
            this.menuItemObjectBlock.setVisible(visible);
        }
    }
    
    public void onClick(View view) {
        final int id = view.getId();
        for (int i = 0; i < ObjectDetailsFragment.objectPayButtons.length; ++i) {
            if (ObjectDetailsFragment.objectPayButtons[i] == id) {
                this.payObjectQuick(i);
                return;
            }
        }
        switch (id) {
            case 2131755268: {
                this.touchObject();
                break;
            }
            case 2131755269: {
                this.sitOnObject();
                break;
            }
            case 2131755538: {
                this.standUp();
                break;
            }
            case 2131755542: {
                this.showObjectOwnerInfo();
                break;
            }
            case 2131755547: {
                this.buyObject();
                break;
            }
            case 2131755554: {
                try {
                    view = this.getView();
                    if (view != null) {
                        this.payObject(Integer.parseInt(((EditText)view.findViewById(2131755553)).getText().toString()));
                    }
                }
                catch (final Exception ex) {
                    ex.printStackTrace();
                }
                break;
            }
            case 2131755539: {
                this.openObjectContents();
                break;
            }
        }
    }
    
    @Override
    public void onCreate(@android.support.annotation.Nullable final Bundle bundle) {
        super.onCreate(bundle);
        this.setHasOptionsMenu(true);
    }
    
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(2131886101, menu);
        this.menuItemObjectTake = menu.findItem(2131755827);
        this.menuItemObjectTakeCopy = menu.findItem(2131755828);
        this.menuItemObjectDelete = menu.findItem(2131755829);
        this.menuItemObjectBlock = menu.findItem(2131755830);
        this.updateOptionsMenu();
    }
    
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        Debug.Log("ObjectDetailsFragment: onCreateView called");
        final View inflate = layoutInflater.inflate(2130968693, viewGroup, false);
        this.ownerNameDisplayer.bindViews((TextView)inflate.findViewById(2131755541), (ChatterPicView)inflate.findViewById(2131755327));
        inflate.findViewById(2131755532).setVisibility(0);
        inflate.findViewById(2131755533).setVisibility(8);
        inflate.findViewById(2131755534).setVisibility(8);
        inflate.findViewById(2131755268).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755269).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755538).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755542).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755547).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755554).setOnClickListener((View$OnClickListener)this);
        inflate.findViewById(2131755539).setOnClickListener((View$OnClickListener)this);
        final int[] objectPayButtons = ObjectDetailsFragment.objectPayButtons;
        for (int length = objectPayButtons.length, i = 0; i < length; ++i) {
            inflate.findViewById(objectPayButtons[i]).setOnClickListener((View$OnClickListener)this);
        }
        ((EditText)inflate.findViewById(2131755553)).addTextChangedListener((TextWatcher)new TextWatcher() {
            final /* synthetic */ Button val$objectPayButton = (Button)inflate.findViewById(2131755554);
            
            public void afterTextChanged(final Editable editable) {
                try {
                    Integer.parseInt(editable.toString());
                    this.val$objectPayButton.setEnabled(true);
                }
                catch (final NumberFormatException ex) {
                    this.val$objectPayButton.setEnabled(false);
                }
            }
            
            public void beforeTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
            }
            
            public void onTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
            }
        });
        return inflate;
    }
    
    public void onDestroyView() {
        this.ownerNameDisplayer.unbindViews();
        super.onDestroyView();
    }
    
    public void onLoadableDataChanged() {
        final Throwable error = this.objectProfile.getError();
        final SLObjectProfileData slObjectProfileData = this.objectProfile.getData();
        if (error instanceof ObjectsManager.ObjectDoesNotExistException) {
            this.showDeadObject();
        }
        else if (error != null || slObjectProfileData == null) {
            this.showObjectNotLoaded();
        }
        else {
            this.showObjectProfile(slObjectProfileData);
        }
    }
    
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        final Object o = null;
        final UserManager userManager = this.getUserManager();
        final int int1 = this.getArguments().getInt("localID");
        if (userManager != null && this.objectLocalID != 0) {
            switch (menuItem.getItemId()) {
                case 2131755827: {
                    ObjectDerezDialog.askForObjectDerez(this.getContext(), ObjectDerezDialog.DerezAction.Take, userManager.getUserID(), int1);
                    return true;
                }
                case 2131755828: {
                    ObjectDerezDialog.askForObjectDerez(this.getContext(), ObjectDerezDialog.DerezAction.TakeCopy, userManager.getUserID(), int1);
                    return true;
                }
                case 2131755829: {
                    ObjectDerezDialog.askForObjectDerez(this.getContext(), ObjectDerezDialog.DerezAction.Delete, userManager.getUserID(), int1);
                    return true;
                }
                case 2131755830: {
                    final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
                    final SLObjectProfileData slObjectProfileData = this.objectProfile.getData();
                    Object o2 = o;
                    if (slObjectProfileData != null) {
                        o2 = slObjectProfileData.name().orNull();
                    }
                    if (activeAgentCircuit != null && slObjectProfileData != null && o2 != null) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
                        builder.setMessage(2131296784);
                        builder.setPositiveButton("Yes", (DialogInterface$OnClickListener)new _$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8$4(activeAgentCircuit, slObjectProfileData, o2));
                        builder.setNegativeButton("No", (DialogInterface$OnClickListener)new _$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8$1());
                        builder.setCancelable(true);
                        builder.create().show();
                    }
                    return true;
                }
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }
    
    public void onPause() {
        final UserManager userManager = this.getUserManager();
        if (userManager != null) {
            userManager.getChatterList().getActiveChattersManager().removeObjectMessageListener(this.chatEventListener);
        }
        super.onPause();
    }
    
    public void onResume() {
        super.onResume();
        final UserManager userManager = this.getUserManager();
        if (userManager != null) {
            userManager.getChatterList().getActiveChattersManager().addObjectMessageListener(this.chatEventListener, UIThreadExecutor.getInstance());
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        this.setTitle(this.getString(2131296804), null);
        this.showObject(this.getArguments().getInt("localID"));
    }
    
    public void onStop() {
        this.loadableMonitor.unsubscribeAll();
        this.ownerNameDisplayer.setChatterID(null);
        super.onStop();
    }
    
    @Override
    public void setFragmentArgs(final Intent intent, final Bundle bundle) {
        if (bundle != null) {
            this.getArguments().putAll(bundle);
            final int int1 = bundle.getInt("localID");
            if (this.isFragmentStarted()) {
                this.showObject(int1);
            }
        }
    }
}
