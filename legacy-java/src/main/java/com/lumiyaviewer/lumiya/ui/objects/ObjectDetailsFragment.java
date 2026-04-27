package com.lumiyaviewer.lumiya.ui.objects;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.generic.OnChatEventListener;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType;
import com.lumiyaviewer.lumiya.slproto.objects.PayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.ChatterNameDisplayer;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.objects.ObjectDerezDialog;
import java.util.UUID;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class ObjectDetailsFragment extends FragmentWithTitle implements ReloadableFragment, View.OnClickListener, LoadableMonitor.OnLoadableDataChangedListener {
    private static final String LOCAL_ID_KEY = "localID";
    private static final int[] objectPayButtons = {R.id.object_pay_button1, R.id.object_pay_button2, R.id.object_pay_button3, R.id.object_pay_button4};

    @Nullable
    private SLObjectProfileData objectProfileData = null;

    @Nullable
    private MenuItem menuItemObjectTake = null;

    @Nullable
    private MenuItem menuItemObjectTakeCopy = null;

    @Nullable
    private MenuItem menuItemObjectDelete = null;

    @Nullable
    private MenuItem menuItemObjectBlock = null;
    private int objectLocalID = 0;
    private final SubscriptionData<Integer, SLObjectProfileData> objectProfile = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<SubscriptionSingleKey, Integer> balanceSubscription = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<SubscriptionSingleKey, MyAvatarState> myAvatarState = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.objectProfile).withOptionalLoadables(this.balanceSubscription, this.myAvatarState).withDataChangedListener(this);
    private final ChatterNameDisplayer ownerNameDisplayer = new ChatterNameDisplayer();
    private final OnChatEventListener chatEventListener = new OnChatEventListener() { // from class: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.3
        private final /* synthetic */ void $m$0(SLChatEvent sLChatEvent) {
            ((ObjectDetailsFragment) this).m685xa60fd782(sLChatEvent);
        }

        @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.OnChatEventListener
        public final void onChatEvent(SLChatEvent sLChatEvent) {
            $m$0(sLChatEvent);
        }
    };

    private void buyObject() {
        final SLObjectProfileData sLObjectProfileData = this.objectProfileData;
        final UserManager userManager = getUserManager();
        final int i = this.objectLocalID;
        if (sLObjectProfileData == null || userManager == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(String.format(getString(R.string.object_buy_confirm), sLObjectProfileData.name().or((Optional<String>) getString(R.string.object_name_loading)), Integer.valueOf(sLObjectProfileData.salePrice()))).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.5
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i2) {
                ObjectDetailsFragment.m681x1bb1c6ad((UserManager) userManager, i, (SLObjectProfileData) sLObjectProfileData, dialogInterface, i2);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                $m$0(dialogInterface, i2);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i2) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                $m$0(dialogInterface, i2);
            }
        });
        builder.show();
    }

    @Nullable
    private UserManager getUserManager() {
        return ActivityUtils.getUserManager(getArguments());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_11291, reason: not valid java name */
    public static /* synthetic */ void m679x1ba25a88(SLAgentCircuit sLAgentCircuit, SLObjectProfileData sLObjectProfileData, String str, DialogInterface dialogInterface, int i) {
        sLAgentCircuit.getModules().muteList.Block(new MuteListEntry(MuteType.OBJECT, sLObjectProfileData.objectUUID(), str, 15));
        dialogInterface.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_24012, reason: not valid java name */
    public static /* synthetic */ void m681x1bb1c6ad(UserManager userManager, int i, SLObjectProfileData sLObjectProfileData, DialogInterface dialogInterface, int i2) {
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (activeAgentCircuit != null) {
            activeAgentCircuit.BuyObject(i, sLObjectProfileData.saleType(), sLObjectProfileData.salePrice());
        }
        dialogInterface.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_25106, reason: not valid java name */
    public static /* synthetic */ void m683x1bb23eb2(UserManager userManager, SLObjectProfileData sLObjectProfileData, int i, DialogInterface dialogInterface, int i2) {
        SLModules modules;
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (activeAgentCircuit != null && (modules = activeAgentCircuit.getModules()) != null) {
            modules.financialInfo.DoPayObject(sLObjectProfileData.objectUUID(), i);
        }
        dialogInterface.dismiss();
    }

    public static Bundle makeSelection(UUID uuid, int i) {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        bundle.putInt(LOCAL_ID_KEY, i);
        return bundle;
    }

    private void openObjectContents() {
        UserManager userManager = getUserManager();
        if (this.objectProfileData == null || this.objectLocalID == 0 || userManager == null) {
            return;
        }
        DetailsActivity.showEmbeddedDetails(getActivity(), TaskInventoryFragment.class, TaskInventoryFragment.makeSelection(userManager.getUserID(), this.objectProfileData.objectUUID(), this.objectLocalID));
    }

    private void payObject(final int i) {
        final SLObjectProfileData sLObjectProfileData = this.objectProfileData;
        final UserManager userManager = getUserManager();
        if (sLObjectProfileData == null || userManager == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(String.format(getString(R.string.object_pay_confirm), sLObjectProfileData.name().or((Optional<String>) getString(R.string.object_name_loading)), Integer.valueOf(i))).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.6
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i2) {
                ObjectDetailsFragment.m683x1bb23eb2((UserManager) userManager, (SLObjectProfileData) sLObjectProfileData, i, dialogInterface, i2);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                $m$0(dialogInterface, i2);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.2
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i2) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                $m$0(dialogInterface, i2);
            }
        });
        builder.show();
    }

    private void payObjectQuick(int i) {
        PayInfo payInfo;
        ImmutableList<Integer> payPrices;
        int intValue;
        if (this.objectProfileData == null || (payInfo = this.objectProfileData.payInfo()) == null || (payPrices = payInfo.payPrices()) == null || i < 0 || i >= payPrices.size() || (intValue = payPrices.get(i).intValue()) == 0) {
            return;
        }
        payObject(intValue);
    }

    private void showDeadObject() {
        View view = getView();
        if (view != null) {
            view.findViewById(R.id.no_object_selected).setVisibility(8);
            view.findViewById(R.id.object_fail_to_load).setVisibility(0);
            ((TextView) view.findViewById(R.id.object_fail_to_load)).setText(R.string.object_deleted);
            view.findViewById(R.id.object_details).setVisibility(8);
        }
    }

    private void showObject(int i) {
        this.objectLocalID = i;
        UserManager userManager = getUserManager();
        if (userManager != null) {
            this.objectProfile.subscribe(userManager.getObjectsManager().getObjectProfile(), Integer.valueOf(i));
            this.myAvatarState.subscribe(userManager.getObjectsManager().myAvatarState(), SubscriptionSingleKey.Value);
            this.balanceSubscription.subscribe(userManager.getBalanceManager().getBalance(), SubscriptionSingleKey.Value);
        }
    }

    private void showObjectNotLoaded() {
        View view = getView();
        if (view != null) {
            view.findViewById(R.id.no_object_selected).setVisibility(8);
            view.findViewById(R.id.object_fail_to_load).setVisibility(0);
            ((TextView) view.findViewById(R.id.object_fail_to_load)).setText(R.string.object_profile_cannot_be_loaded);
            view.findViewById(R.id.object_details).setVisibility(8);
        }
    }

    private void showObjectOwnerInfo() {
        UUID ownerUUID;
        UserManager userManager = getUserManager();
        if (this.objectProfileData == null || userManager == null || (ownerUUID = this.objectProfileData.ownerUUID()) == null) {
            return;
        }
        DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(userManager.getUserID(), ownerUUID)));
    }

    private void showObjectProfile(SLObjectProfileData sLObjectProfileData) {
        boolean z;
        SLAgentCircuit activeAgentCircuit;
        SLModules modules;
        this.objectProfileData = sLObjectProfileData;
        View view = getView();
        UserManager userManager = getUserManager();
        if (view != null) {
            view.findViewById(R.id.no_object_selected).setVisibility(8);
            if (sLObjectProfileData.isDead()) {
                view.findViewById(R.id.object_fail_to_load).setVisibility(0);
                view.findViewById(R.id.object_details).setVisibility(8);
                ((TextView) view.findViewById(R.id.object_fail_to_load)).setText(R.string.object_deleted);
            } else {
                view.findViewById(R.id.object_details).setVisibility(0);
                view.findViewById(R.id.object_fail_to_load).setVisibility(8);
                view.findViewById(R.id.object_touch_button).setVisibility(sLObjectProfileData.isTouchable() ? 0 : 8);
                ((Button) view.findViewById(R.id.object_touch_button)).setText(Strings.isNullOrEmpty(sLObjectProfileData.touchName()) ? getString(R.string.object_touch_button) : sLObjectProfileData.touchName());
                boolean canSit = (userManager == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null || (modules = activeAgentCircuit.getModules()) == null) ? false : modules.rlvController.canSit();
                MyAvatarState data = this.myAvatarState.getData();
                if (data == null) {
                    z = false;
                } else if (data.isSitting() && data.sittingOn() == this.objectLocalID) {
                    z = true;
                    canSit = false;
                } else {
                    z = false;
                }
                view.findViewById(R.id.object_sit_button).setVisibility(canSit ? 0 : 8);
                view.findViewById(R.id.object_stand_button).setVisibility(z ? 0 : 8);
                ((TextView) view.findViewById(R.id.object_details_name)).setText(sLObjectProfileData.name().or((Optional<String>) getString(R.string.object_name_loading)));
                ((TextView) view.findViewById(R.id.object_details_desc)).setText(sLObjectProfileData.description().or((Optional<String>) ""));
                view.findViewById(R.id.object_owner_card_view).setVisibility(sLObjectProfileData.ownerUUID() != null ? 0 : 8);
                view.findViewById(R.id.floating_text_card_view).setVisibility(sLObjectProfileData.floatingText().isPresent() ? 0 : 8);
                ((TextView) view.findViewById(R.id.object_hover_text)).setText(sLObjectProfileData.floatingText().or((Optional<String>) ""));
                view.findViewById(R.id.buy_object_card_view).setVisibility(sLObjectProfileData.saleType() != 0 ? 0 : 8);
                ((TextView) view.findViewById(R.id.object_buy_details)).setText(getString(R.string.object_buy_price_format, Integer.valueOf(sLObjectProfileData.salePrice())));
                Integer data2 = this.balanceSubscription.getData();
                if (data2 != null) {
                    ((TextView) view.findViewById(R.id.object_buy_details_balance)).setText(getString(R.string.object_balance_format, data2));
                } else {
                    ((TextView) view.findViewById(R.id.object_buy_details_balance)).setText("");
                }
                PayInfo payInfo = sLObjectProfileData.isPayable() ? sLObjectProfileData.payInfo() : null;
                if (payInfo != null) {
                    ImmutableList<Integer> payPrices = payInfo.payPrices();
                    if (payPrices != null) {
                        int i = 0;
                        for (int i2 = 0; i2 < objectPayButtons.length && i2 < payPrices.size(); i2++) {
                            int intValue = payPrices.get(i2).intValue();
                            int defaultPayPrice = intValue == -2 ? payInfo.defaultPayPrice() : intValue;
                            if (defaultPayPrice <= 0) {
                                view.findViewById(objectPayButtons[i2]).setVisibility(8);
                                view.findViewById(objectPayButtons[i2]).setTag(R.id.object_pay_price_tag, 0);
                            } else {
                                ((Button) view.findViewById(objectPayButtons[i2])).setText(String.format(getString(R.string.pay_button_format), Integer.valueOf(defaultPayPrice)));
                                view.findViewById(objectPayButtons[i2]).setVisibility(0);
                                view.findViewById(objectPayButtons[i2]).setTag(R.id.object_pay_price_tag, Integer.valueOf(defaultPayPrice));
                                i++;
                            }
                        }
                        view.findViewById(R.id.object_quick_pay_layout).setVisibility(i != 0 ? 0 : 8);
                    } else {
                        view.findViewById(R.id.object_quick_pay_layout).setVisibility(8);
                    }
                    if (payInfo.defaultPayPrice() != -1) {
                        if (((EditText) view.findViewById(R.id.object_pay_amount)).getText().toString().equals("")) {
                            if (payInfo.defaultPayPrice() > 0) {
                                ((EditText) view.findViewById(R.id.object_pay_amount)).setText(getString(R.string.object_pay_amount_format, Integer.valueOf(payInfo.defaultPayPrice())));
                            } else {
                                ((EditText) view.findViewById(R.id.object_pay_amount)).setText("");
                            }
                        }
                        view.findViewById(R.id.object_normal_pay_layout).setVisibility(0);
                    } else {
                        view.findViewById(R.id.object_normal_pay_layout).setVisibility(8);
                    }
                    view.findViewById(R.id.pay_object_card_view).setVisibility(0);
                } else {
                    view.findViewById(R.id.pay_object_card_view).setVisibility(8);
                }
            }
        }
        if (userManager != null && (!sLObjectProfileData.isDead())) {
            if (sLObjectProfileData.isPayable() && sLObjectProfileData.payInfo() == null) {
                UUID objectUUID = sLObjectProfileData.objectUUID();
                SLAgentCircuit activeAgentCircuit2 = userManager.getActiveAgentCircuit();
                if (activeAgentCircuit2 != null && objectUUID != null) {
                    activeAgentCircuit2.DoRequestPayPrice(objectUUID);
                }
            }
            UUID ownerUUID = sLObjectProfileData.ownerUUID();
            if (ownerUUID != null) {
                this.ownerNameDisplayer.setChatterID(ChatterID.getUserChatterID(userManager.getUserID(), ownerUUID));
            }
        }
        updateOptionsMenu();
    }

    private void sitOnObject() {
        SLAgentCircuit activeAgentCircuit;
        SLModules modules;
        UserManager userManager = getUserManager();
        if (userManager == null || this.objectProfileData == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null || (modules = activeAgentCircuit.getModules()) == null) {
            return;
        }
        modules.avatarControl.SitOnObject(this.objectProfileData.objectUUID());
    }

    private void standUp() {
        SLAgentCircuit activeAgentCircuit;
        SLModules modules;
        UserManager userManager = getUserManager();
        if (userManager == null || this.objectProfileData == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null || (modules = activeAgentCircuit.getModules()) == null) {
            return;
        }
        modules.avatarControl.Stand();
    }

    private void touchObject() {
        SLAgentCircuit activeAgentCircuit;
        UserManager userManager = getUserManager();
        if (userManager == null || this.objectLocalID == 0 || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null) {
            return;
        }
        activeAgentCircuit.TouchObject(this.objectLocalID);
    }

    private void updateOptionsMenu() {
        boolean z;
        boolean z2;
        boolean z3;
        UserManager userManager = getUserManager();
        if (userManager == null || this.objectProfileData == null) {
            z = false;
            z2 = false;
            z3 = false;
        } else if (!this.objectProfileData.isDead()) {
            z3 = userManager.getUserID().equals(this.objectProfileData.ownerUUID());
            r0 = userManager.getUserID().equals(this.objectProfileData.ownerUUID()) ? this.objectProfileData.isCopyable() : false;
            z = userManager.getUserID().equals(this.objectProfileData.ownerUUID());
            z2 = r0;
            r0 = true;
        } else {
            z = false;
            z2 = false;
            z3 = false;
        }
        if (this.menuItemObjectTake != null) {
            this.menuItemObjectTake.setVisible(z3);
        }
        if (this.menuItemObjectTakeCopy != null) {
            this.menuItemObjectTakeCopy.setVisible(z2);
        }
        if (this.menuItemObjectDelete != null) {
            this.menuItemObjectDelete.setVisible(z);
        }
        if (this.menuItemObjectBlock != null) {
            this.menuItemObjectBlock.setVisible(r0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_6633, reason: not valid java name */
    public /* synthetic */ void m685xa60fd782(SLChatEvent sLChatEvent) {
        if (isFragmentVisible()) {
            UserManager userManager = getUserManager();
            ChatMessageSource source = sLChatEvent.getSource();
            if (!(source instanceof ChatMessageSourceObject) || this.objectProfileData == null || userManager == null || !Objects.equal(source.getSourceUUID(), this.objectProfileData.objectUUID())) {
                return;
            }
            Toast.makeText(getContext(), sLChatEvent.getPlainTextMessage(getContext(), userManager, false), 1).show();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        for (int i = 0; i < objectPayButtons.length; i++) {
            if (objectPayButtons[i] == id) {
                payObjectQuick(i);
                return;
            }
        }
        switch (id) {
            case R.id.object_touch_button /* 2131755268 */:
                touchObject();
                return;
            case R.id.object_sit_button /* 2131755269 */:
                sitOnObject();
                return;
            case R.id.object_stand_button /* 2131755538 */:
                standUp();
                return;
            case R.id.object_contents_button /* 2131755539 */:
                openObjectContents();
                return;
            case R.id.object_owner_button /* 2131755542 */:
                showObjectOwnerInfo();
                return;
            case R.id.object_button_buy /* 2131755547 */:
                buyObject();
                return;
            case R.id.object_pay_button /* 2131755554 */:
                try {
                    View view2 = getView();
                    if (view2 != null) {
                        payObject(Integer.parseInt(((EditText) view2.findViewById(R.id.object_pay_amount)).getText().toString()));
                        return;
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            default:
                return;
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, android.support.v4.app.Fragment
    public void onCreate(@android.support.annotation.Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @Override // android.support.v4.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.object_details_menu, menu);
        this.menuItemObjectTake = menu.findItem(R.id.item_object_take);
        this.menuItemObjectTakeCopy = menu.findItem(R.id.item_object_take_copy);
        this.menuItemObjectDelete = menu.findItem(R.id.item_object_delete);
        this.menuItemObjectBlock = menu.findItem(R.id.item_object_block);
        updateOptionsMenu();
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        Debug.Log("ObjectDetailsFragment: onCreateView called");
        View inflate = layoutInflater.inflate(R.layout.object_details, viewGroup, false);
        this.ownerNameDisplayer.bindViews((TextView) inflate.findViewById(R.id.object_details_owner), (ChatterPicView) inflate.findViewById(R.id.userPicView));
        inflate.findViewById(R.id.no_object_selected).setVisibility(0);
        inflate.findViewById(R.id.object_fail_to_load).setVisibility(8);
        inflate.findViewById(R.id.object_details).setVisibility(8);
        inflate.findViewById(R.id.object_touch_button).setOnClickListener(this);
        inflate.findViewById(R.id.object_sit_button).setOnClickListener(this);
        inflate.findViewById(R.id.object_stand_button).setOnClickListener(this);
        inflate.findViewById(R.id.object_owner_button).setOnClickListener(this);
        inflate.findViewById(R.id.object_button_buy).setOnClickListener(this);
        inflate.findViewById(R.id.object_pay_button).setOnClickListener(this);
        inflate.findViewById(R.id.object_contents_button).setOnClickListener(this);
        for (int i : objectPayButtons) {
            inflate.findViewById(i).setOnClickListener(this);
        }
        final Button button = (Button) inflate.findViewById(R.id.object_pay_button);
        ((EditText) inflate.findViewById(R.id.object_pay_amount)).addTextChangedListener(new TextWatcher() { // from class: com.lumiyaviewer.lumiya.ui.objects.ObjectDetailsFragment.1
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                try {
                    Integer.parseInt(editable.toString());
                    button.setEnabled(true);
                } catch (NumberFormatException e) {
                    button.setEnabled(false);
                }
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }
        });
        return inflate;
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroyView() {
        this.ownerNameDisplayer.unbindViews();
        super.onDestroyView();
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
    public void onLoadableDataChanged() {
        Throwable error = this.objectProfile.getError();
        SLObjectProfileData data = this.objectProfile.getData();
        if (error instanceof ObjectsManager.ObjectDoesNotExistException) {
            showDeadObject();
        } else if (error != null || data == null) {
            showObjectNotLoaded();
        } else {
            showObjectProfile(data);
        }
    }

    @Override // android.support.v4.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        UserManager userManager = getUserManager();
        int i = getArguments().getInt(LOCAL_ID_KEY);
        if (userManager != null && this.objectLocalID != 0) {
            switch (menuItem.getItemId()) {
                case R.id.item_object_take /* 2131755827 */:
                    ObjectDerezDialog.askForObjectDerez(getContext(), ObjectDerezDialog.DerezAction.Take, userManager.getUserID(), i);
                    return true;
                case R.id.item_object_take_copy /* 2131755828 */:
                    ObjectDerezDialog.askForObjectDerez(getContext(), ObjectDerezDialog.DerezAction.TakeCopy, userManager.getUserID(), i);
                    return true;
                case R.id.item_object_delete /* 2131755829 */:
                    ObjectDerezDialog.askForObjectDerez(getContext(), ObjectDerezDialog.DerezAction.Delete, userManager.getUserID(), i);
                    return true;
                case R.id.item_object_block /* 2131755830 */:
                    final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
                    final SLObjectProfileData data = this.objectProfile.getData();
                    final String orNull = data != null ? data.name().orNull() : null;
                    if (activeAgentCircuit != null && data != null && orNull != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(R.string.object_block_question);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.4
                            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i2) {
                                ObjectDetailsFragment.m679x1ba25a88((SLAgentCircuit) activeAgentCircuit, (SLObjectProfileData) data, (String) orNull, dialogInterface, i2);
                            }

                            @Override // android.content.DialogInterface.OnClickListener
                            public final void onClick(DialogInterface dialogInterface, int i2) {
                                $m$0(dialogInterface, i2);
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8.1
                            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i2) {
                                dialogInterface.cancel();
                            }

                            @Override // android.content.DialogInterface.OnClickListener
                            public final void onClick(DialogInterface dialogInterface, int i2) {
                                $m$0(dialogInterface, i2);
                            }
                        });
                        builder.setCancelable(true);
                        builder.create().show();
                    }
                    return true;
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onPause() {
        UserManager userManager = getUserManager();
        if (userManager != null) {
            userManager.getChatterList().getActiveChattersManager().removeObjectMessageListener(this.chatEventListener);
        }
        super.onPause();
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        UserManager userManager = getUserManager();
        if (userManager != null) {
            userManager.getChatterList().getActiveChattersManager().addObjectMessageListener(this.chatEventListener, UIThreadExecutor.getInstance());
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        setTitle(getString(R.string.object_details_caption), null);
        showObject(getArguments().getInt(LOCAL_ID_KEY));
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStop() {
        this.loadableMonitor.unsubscribeAll();
        this.ownerNameDisplayer.setChatterID(null);
        super.onStop();
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ReloadableFragment
    public void setFragmentArgs(Intent intent, Bundle bundle) {
        if (bundle != null) {
            getArguments().putAll(bundle);
            int i = bundle.getInt(LOCAL_ID_KEY);
            if (isFragmentStarted()) {
                showObject(i);
            }
        }
    }
}
