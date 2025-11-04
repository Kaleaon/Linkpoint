// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import javax.annotation.Nullable;
import butterknife.OnClick;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import butterknife.ButterKnife;
import android.view.View;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.support.v4.app.FragmentActivity;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import android.content.DialogInterface$OnClickListener;
import android.content.Context;
import android.app.AlertDialog$Builder;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import butterknife.Unbinder;
import android.widget.TextView;
import butterknife.BindView;
import android.widget.EditText;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.ui.common.ChatterNameDisplayer;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;

public class PayUserFragment extends ChatterFragment
{
    private final ChatterNameDisplayer chatterNameDisplayer;
    private final SubscriptionData<SubscriptionSingleKey, Integer> myBalance;
    @BindView(2131755624)
    EditText payAmount;
    @BindView(2131755625)
    EditText payMessage;
    @BindView(2131755623)
    TextView paymentDetailsBalance;
    @BindView(2131755620)
    TextView receivingUserName;
    @BindView(2131755621)
    ChatterPicView receivingUserPic;
    private Unbinder unbinder;
    
    public PayUserFragment() {
        this.chatterNameDisplayer = new ChatterNameDisplayer();
        this.myBalance = new SubscriptionData<SubscriptionSingleKey, Integer>(UIThreadExecutor.getInstance(), new _$Lambda$4LKDzNIyR_gCoOmHf_6XBk4qMJA$1(this));
    }
    
    private void onMyBalance(final Integer n) {
        if (this.unbinder != null) {
            if (n != null) {
                this.paymentDetailsBalance.setText((CharSequence)this.getString(2131296783, n));
                this.paymentDetailsBalance.setVisibility(0);
            }
            else {
                this.paymentDetailsBalance.setVisibility(8);
            }
        }
    }
    
    private void payUser(final int i, final String s) {
        final ChatterID chatterID = this.chatterID;
        if (chatterID instanceof ChatterID.ChatterIDUser) {
            final String resolvedName = this.chatterNameDisplayer.getResolvedName(this.getContext());
            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder((Context)this.getActivity());
            alertDialog$Builder.setMessage((CharSequence)String.format(this.getString(2131297134), resolvedName, i)).setCancelable(false).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$4LKDzNIyR_gCoOmHf_6XBk4qMJA$2(i, this, chatterID, s)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$4LKDzNIyR_gCoOmHf_6XBk4qMJA());
            alertDialog$Builder.create().show();
        }
    }
    
    @Override
    protected String decorateFragmentTitle(final String s) {
        return this.getString(2131296871, s);
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968706, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        this.chatterNameDisplayer.bindViews(this.receivingUserName, this.receivingUserPic);
        return inflate;
    }
    
    @Override
    public void onDestroyView() {
        this.chatterNameDisplayer.unbindViews();
        if (this.unbinder != null) {
            this.unbinder.unbind();
            this.unbinder = null;
        }
        super.onDestroyView();
    }
    
    @OnClick({ 2131755622 })
    public void onReceivingUserViewProfileClick() {
        if (this.chatterID != null) {
            DetailsActivity.showEmbeddedDetails(this.getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(this.chatterID));
        }
    }
    
    @Override
    protected void onShowUser(@Nullable final ChatterID chatterID) {
        this.chatterNameDisplayer.setChatterID(chatterID);
        if (chatterID != null) {
            final UserManager userManager = chatterID.getUserManager();
            if (userManager != null) {
                this.myBalance.subscribe(userManager.getBalanceManager().getBalance(), SubscriptionSingleKey.Value);
            }
        }
        else {
            this.myBalance.unsubscribe();
        }
    }
    
    @OnClick({ 2131755626 })
    public void onUserPayButton() {
        try {
            this.payUser(Integer.parseInt(this.payAmount.getText().toString()), this.payMessage.getText().toString());
        }
        catch (final Exception ex) {}
    }
}
