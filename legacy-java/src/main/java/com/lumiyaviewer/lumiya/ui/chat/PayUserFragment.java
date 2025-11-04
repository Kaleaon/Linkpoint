package com.lumiyaviewer.lumiya.ui.chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.ChatterNameDisplayer;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class PayUserFragment extends ChatterFragment {
    private final ChatterNameDisplayer chatterNameDisplayer = new ChatterNameDisplayer();
    private final SubscriptionData<SubscriptionSingleKey, Integer> myBalance = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$4LKDzNIyR_gCoOmHf_6XBk4qMJA.1
        private final /* synthetic */ void $m$0(Object obj) {
            ((PayUserFragment) this).m433com_lumiyaviewer_lumiya_ui_chat_PayUserFragmentmthref0((Integer) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });

    @BindView(R.id.pay_amount)
    EditText payAmount;

    @BindView(R.id.user_pay_message)
    EditText payMessage;

    @BindView(R.id.payment_details_balance)
    TextView paymentDetailsBalance;

    @BindView(R.id.receiving_user_name)
    TextView receivingUserName;

    @BindView(R.id.receiving_user_pic)
    ChatterPicView receivingUserPic;
    private Unbinder unbinder;

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onMyBalance, reason: merged with bridge method [inline-methods] */
    public void m433com_lumiyaviewer_lumiya_ui_chat_PayUserFragmentmthref0(Integer num) {
        if (this.unbinder != null) {
            if (num == null) {
                this.paymentDetailsBalance.setVisibility(8);
            } else {
                this.paymentDetailsBalance.setText(getString(R.string.object_balance_format, num));
                this.paymentDetailsBalance.setVisibility(0);
            }
        }
    }

    private void payUser(final int i, final String str) {
        final ChatterID chatterID = this.chatterID;
        if (chatterID instanceof ChatterID.ChatterIDUser) {
            String resolvedName = this.chatterNameDisplayer.getResolvedName(getContext());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(String.format(getString(R.string.user_pay_confirm), resolvedName, Integer.valueOf(i))).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$4LKDzNIyR_gCoOmHf_6XBk4qMJA.2
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i2) {
                    ((PayUserFragment) this).m434lambda$com_lumiyaviewer_lumiya_ui_chat_PayUserFragment_3721((ChatterID) chatterID, i, (String) str, dialogInterface, i2);
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    $m$0(dialogInterface, i2);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$4LKDzNIyR_gCoOmHf_6XBk4qMJA
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i2) {
                    dialogInterface.cancel();
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    $m$0(dialogInterface, i2);
                }
            });
            builder.create().show();
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment
    protected String decorateFragmentTitle(String str) {
        return getString(R.string.pay_user_title, str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_PayUserFragment_3721, reason: not valid java name */
    public /* synthetic */ void m434lambda$com_lumiyaviewer_lumiya_ui_chat_PayUserFragment_3721(ChatterID chatterID, int i, String str, DialogInterface dialogInterface, int i2) {
        SLAgentCircuit activeAgentCircuit;
        UserManager userManager = chatterID.getUserManager();
        if (userManager != null && (activeAgentCircuit = userManager.getActiveAgentCircuit()) != null) {
            activeAgentCircuit.getModules().financialInfo.DoPayUser(((ChatterID.ChatterIDUser) chatterID).getChatterUUID(), i, str);
        }
        dialogInterface.dismiss();
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).closeDetailsFragment(this);
        }
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.pay_user, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        this.chatterNameDisplayer.bindViews(this.receivingUserName, this.receivingUserPic);
        return inflate;
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroyView() {
        this.chatterNameDisplayer.unbindViews();
        if (this.unbinder != null) {
            this.unbinder.unbind();
            this.unbinder = null;
        }
        super.onDestroyView();
    }

    @OnClick({R.id.receiving_user_profile_button})
    public void onReceivingUserViewProfileClick() {
        if (this.chatterID != null) {
            DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(this.chatterID));
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment
    protected void onShowUser(@Nullable ChatterID chatterID) {
        this.chatterNameDisplayer.setChatterID(chatterID);
        if (chatterID == null) {
            this.myBalance.unsubscribe();
            return;
        }
        UserManager userManager = chatterID.getUserManager();
        if (userManager != null) {
            this.myBalance.subscribe(userManager.getBalanceManager().getBalance(), SubscriptionSingleKey.Value);
        }
    }

    @OnClick({R.id.user_pay_button})
    public void onUserPayButton() {
        try {
            payUser(Integer.parseInt(this.payAmount.getText().toString()), this.payMessage.getText().toString());
        } catch (Exception e) {
        }
    }
}
