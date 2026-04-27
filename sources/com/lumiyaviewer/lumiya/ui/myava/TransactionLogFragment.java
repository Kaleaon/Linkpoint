// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.myava;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.lumiyaviewer.lumiya.dao.MoneyTransaction;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.BalanceManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import de.greenrobot.dao.query.LazyList;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.myava:
//            TransactionLogAdapter

public class TransactionLogFragment extends FragmentWithTitle
    implements com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener, TransactionLogAdapter.OnTransactionClickListener
{

    private TransactionLogAdapter adapter;
    private final LoadableMonitor loadableMonitor;
    LoadingLayout loadingLayout;
    private final Handler mHandler = new Handler();
    private final SubscriptionData moneyTransactions = new SubscriptionData(UIThreadExecutor.getInstance());
    private final Runnable scrollToBottomRunnable = new Runnable() {

        final TransactionLogFragment this$0;

        public void run()
        {
label0:
            {
                TransactionLogFragment._2D_set0(TransactionLogFragment.this, false);
                if (TransactionLogFragment._2D_get3(TransactionLogFragment.this) != null)
                {
                    RecyclerView recyclerview = transactionLogView;
                    if (recyclerview.hasPendingAdapterUpdates())
                    {
                        break label0;
                    }
                    if (TransactionLogFragment._2D_get0(TransactionLogFragment.this) != null)
                    {
                        int i = TransactionLogFragment._2D_get0(TransactionLogFragment.this).getItemCount();
                        if (i > 0)
                        {
                            recyclerview.scrollToPosition(i - 1);
                        }
                    }
                }
                return;
            }
            TransactionLogFragment._2D_set0(TransactionLogFragment.this, true);
            TransactionLogFragment._2D_get1(TransactionLogFragment.this).post(TransactionLogFragment._2D_get2(TransactionLogFragment.this));
        }

            
            {
                this$0 = TransactionLogFragment.this;
                super();
            }
    };
    private boolean scrollToBottomRunnablePosted;
    RecyclerView transactionLogView;
    private Unbinder unbinder;

    static TransactionLogAdapter _2D_get0(TransactionLogFragment transactionlogfragment)
    {
        return transactionlogfragment.adapter;
    }

    static Handler _2D_get1(TransactionLogFragment transactionlogfragment)
    {
        return transactionlogfragment.mHandler;
    }

    static Runnable _2D_get2(TransactionLogFragment transactionlogfragment)
    {
        return transactionlogfragment.scrollToBottomRunnable;
    }

    static Unbinder _2D_get3(TransactionLogFragment transactionlogfragment)
    {
        return transactionlogfragment.unbinder;
    }

    static boolean _2D_set0(TransactionLogFragment transactionlogfragment, boolean flag)
    {
        transactionlogfragment.scrollToBottomRunnablePosted = flag;
        return flag;
    }

    public TransactionLogFragment()
    {
        loadableMonitor = (new LoadableMonitor(new Loadable[] {
            moneyTransactions
        })).withDataChangedListener(this);
        scrollToBottomRunnablePosted = false;
    }

    private void clearTransactionLog()
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage(0x7f0900be).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.N_xrT8AwWQ2OjPw50fSCa4Lhb58._cls1(this)).setNegativeButton("No", new _2D_.Lambda.N_xrT8AwWQ2OjPw50fSCa4Lhb58());
        builder.create().show();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_myava_TransactionLogFragment_4924(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    public static Bundle makeSelection(UUID uuid)
    {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        return bundle;
    }

    private void performClearTransactionLog()
    {
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        if (usermanager != null)
        {
            usermanager.getBalanceManager().clearMoneyTransactions();
        }
    }

    private void scrollToBottom()
    {
        if (!scrollToBottomRunnablePosted)
        {
            scrollToBottomRunnablePosted = true;
            mHandler.post(scrollToBottomRunnable);
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_myava_TransactionLogFragment_4757(DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        performClearTransactionLog();
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater)
    {
        super.onCreateOptionsMenu(menu, menuinflater);
        menuinflater.inflate(0x7f120021, menu);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        layoutinflater = layoutinflater.inflate(0x7f0400af, viewgroup, false);
        unbinder = ButterKnife.bind(this, layoutinflater);
        adapter = new TransactionLogAdapter(getContext(), ActivityUtils.getActiveAgentID(getArguments()), this);
        transactionLogView.setAdapter(adapter);
        loadableMonitor.setLoadingLayout(loadingLayout, null, getString(0x7f0900a9));
        return layoutinflater;
    }

    public void onDestroyView()
    {
        if (unbinder != null)
        {
            unbinder.unbind();
            unbinder = null;
        }
        super.onDestroyView();
    }

    public void onLoadableDataChanged()
    {
        LazyList lazylist = (LazyList)moneyTransactions.getData();
        if (lazylist != null)
        {
            loadableMonitor.setEmptyMessage(lazylist.isEmpty(), getString(0x7f0901f0));
            if (adapter != null)
            {
                adapter.setData(lazylist);
                scrollToBottom();
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        switch (menuitem.getItemId())
        {
        default:
            return super.onOptionsItemSelected(menuitem);

        case 2131755859: 
            clearTransactionLog();
            break;
        }
        return true;
    }

    public void onStart()
    {
        super.onStart();
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        if (usermanager != null)
        {
            moneyTransactions.subscribe(usermanager.getBalanceManager().moneyTransactions(), SubscriptionSingleKey.Value);
        }
    }

    public void onStop()
    {
        loadableMonitor.unsubscribeAll();
        super.onStop();
    }

    public void onTransactionClicked(MoneyTransaction moneytransaction)
    {
        UUID uuid = ActivityUtils.getActiveAgentID(getArguments());
        if (uuid != null)
        {
            moneytransaction = ChatterID.getUserChatterID(uuid, moneytransaction.getAgentUUID());
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment, UserProfileFragment.makeSelection(moneytransaction));
        }
    }
}
