// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.finance.SLFinancialInfo;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.BalanceManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.ChatterNameDisplayer;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            ChatterPicView

public class PayUserFragment extends ChatterFragment
{

    private final ChatterNameDisplayer chatterNameDisplayer = new ChatterNameDisplayer();
    private final SubscriptionData myBalance = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda._cls4LKDzNIyR_gCoOmHf_6XBk4qMJA._cls1(this));
    EditText payAmount;
    EditText payMessage;
    TextView paymentDetailsBalance;
    TextView receivingUserName;
    ChatterPicView receivingUserPic;
    private Unbinder unbinder;

    public PayUserFragment()
    {
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_PayUserFragment_4554(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    private void onMyBalance(Integer integer)
    {
label0:
        {
            if (unbinder != null)
            {
                if (integer == null)
                {
                    break label0;
                }
                paymentDetailsBalance.setText(getString(0x7f09020f, new Object[] {
                    integer
                }));
                paymentDetailsBalance.setVisibility(0);
            }
            return;
        }
        paymentDetailsBalance.setVisibility(8);
    }

    private void payUser(int i, String s)
    {
        ChatterID chatterid = chatterID;
        if (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)
        {
            String s1 = chatterNameDisplayer.getResolvedName(getContext());
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
            builder.setMessage(String.format(getString(0x7f09036e), new Object[] {
                s1, Integer.valueOf(i)
            })).setCancelable(false).setPositiveButton("Yes", new _2D_.Lambda._cls4LKDzNIyR_gCoOmHf_6XBk4qMJA._cls2(i, this, chatterid, s)).setNegativeButton("No", new _2D_.Lambda._cls4LKDzNIyR_gCoOmHf_6XBk4qMJA());
            builder.create().show();
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_PayUserFragment_2D_mthref_2D_0(Integer integer)
    {
        onMyBalance(integer);
    }

    protected String decorateFragmentTitle(String s)
    {
        return getString(0x7f090267, new Object[] {
            s
        });
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_PayUserFragment_3721(ChatterID chatterid, int i, String s, DialogInterface dialoginterface, int j)
    {
        Object obj = chatterid.getUserManager();
        if (obj != null)
        {
            obj = ((UserManager) (obj)).getActiveAgentCircuit();
            if (obj != null)
            {
                ((SLAgentCircuit) (obj)).getModules().financialInfo.DoPayUser(((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterid).getChatterUUID(), i, s);
            }
        }
        dialoginterface.dismiss();
        chatterid = getActivity();
        if (chatterid instanceof DetailsActivity)
        {
            ((DetailsActivity)chatterid).closeDetailsFragment(this);
        }
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        layoutinflater = layoutinflater.inflate(0x7f040082, viewgroup, false);
        unbinder = ButterKnife.bind(this, layoutinflater);
        chatterNameDisplayer.bindViews(receivingUserName, receivingUserPic);
        return layoutinflater;
    }

    public void onDestroyView()
    {
        chatterNameDisplayer.unbindViews();
        if (unbinder != null)
        {
            unbinder.unbind();
            unbinder = null;
        }
        super.onDestroyView();
    }

    public void onReceivingUserViewProfileClick()
    {
        if (chatterID != null)
        {
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment, UserProfileFragment.makeSelection(chatterID));
        }
    }

    protected void onShowUser(ChatterID chatterid)
    {
        chatterNameDisplayer.setChatterID(chatterid);
        if (chatterid != null)
        {
            chatterid = chatterid.getUserManager();
            if (chatterid != null)
            {
                myBalance.subscribe(chatterid.getBalanceManager().getBalance(), SubscriptionSingleKey.Value);
            }
            return;
        } else
        {
            myBalance.unsubscribe();
            return;
        }
    }

    public void onUserPayButton()
    {
        try
        {
            payUser(Integer.parseInt(payAmount.getText().toString()), payMessage.getText().toString());
            return;
        }
        catch (Exception exception)
        {
            return;
        }
    }
}
