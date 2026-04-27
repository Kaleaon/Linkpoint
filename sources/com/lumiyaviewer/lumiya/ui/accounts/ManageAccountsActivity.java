// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.accounts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity;
import com.lumiyaviewer.lumiya.ui.grids.GridList;
import com.lumiyaviewer.lumiya.ui.grids.ManageGridsActivity;
import java.util.ArrayList;
import java.util.List;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.accounts:
//            AccountList, AccountEditDialog

public class ManageAccountsActivity extends ThemedActivity
    implements AccountEditDialog.OnAccountEditResultListener, android.widget.AdapterView.OnItemClickListener, android.view.View.OnClickListener
{
    private static class AccountListAdapter extends ArrayAdapter
    {

        private GridList gridList;

        public View getView(int i, View view, ViewGroup viewgroup)
        {
            Object obj = (LayoutInflater)getContext().getSystemService("layout_inflater");
            View view1 = view;
            if (view == null)
            {
                view1 = ((LayoutInflater) (obj)).inflate(0x7f04001c, viewgroup, false);
            }
            view = (TextView)view1.findViewById(0x7f1000b9);
            viewgroup = (TextView)view1.findViewById(0x7f1000ba);
            obj = getItem(i);
            if (obj != null)
            {
                obj = (AccountList.AccountInfo)obj;
                com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo gridinfo = gridList.getGridByUUID(((AccountList.AccountInfo) (obj)).getGridUUID());
                view.setText(((AccountList.AccountInfo) (obj)).getLoginName());
                if (gridinfo != null)
                {
                    view = gridinfo.getGridName();
                } else
                {
                    view = "";
                }
                viewgroup.setText(view);
            }
            return view1;
        }

        void updateGridList()
        {
            gridList.loadGrids();
        }

        void updateList()
        {
            super.notifyDataSetChanged();
        }

        AccountListAdapter(Context context, List list)
        {
            super(context, 0x7f04001c, list);
            gridList = new GridList(context);
        }
    }


    private AccountList accountList;
    private AccountListAdapter adapter;
    private List displayList;

    public ManageAccountsActivity()
    {
        accountList = null;
        displayList = new ArrayList();
    }

    private void deleteAccount(AccountList.AccountInfo accountinfo)
    {
        accountList.deleteAccount(accountinfo);
        accountList.savePreferences();
        accountList.getAccountList(displayList);
        adapter.updateList();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_accounts_ManageAccountsActivity_6299(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    private void showAccountDeleteDialog(AccountList.AccountInfo accountinfo)
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(getString(0x7f090039)).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.c901yk_brt0jPczBoAMr_2D_Jn1w74._cls1(this, accountinfo)).setNegativeButton("No", new _2D_.Lambda.c901yk_brt0jPczBoAMr_2D_Jn1w74());
        builder.create().show();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_accounts_ManageAccountsActivity_6140(AccountList.AccountInfo accountinfo, DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        deleteAccount(accountinfo);
    }

    public void onAccountEditCancelled()
    {
    }

    public void onAccountEdited(AccountList.AccountInfo accountinfo, boolean flag)
    {
        if (flag)
        {
            accountList.addNewAccount(accountinfo);
        }
        accountList.savePreferences();
        accountList.getAccountList(displayList);
        adapter.updateList();
        accountinfo = (ListView)findViewById(0x7f1001d9);
        if (accountinfo.getAdapter().getCount() > 0)
        {
            accountinfo.setSelection(accountinfo.getAdapter().getCount() - 1);
        }
    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
        default:
            return;

        case 2131755482: 
            view = new AccountEditDialog(this, null);
            break;
        }
        view.setOnAccountEditResultListener(this);
        view.show();
    }

    public boolean onContextItemSelected(MenuItem menuitem)
    {
        Object obj;
        obj = (android.widget.AdapterView.AdapterContextMenuInfo)menuitem.getMenuInfo();
        obj = adapter.getItem(((android.widget.AdapterView.AdapterContextMenuInfo) (obj)).position);
        if (obj == null) goto _L2; else goto _L1
_L1:
        obj = (AccountList.AccountInfo)obj;
        menuitem.getItemId();
        JVM INSTR tableswitch 2131755767 2131755768: default 60
    //                   2131755767 66
    //                   2131755768 87;
           goto _L2 _L3 _L4
_L2:
        return super.onContextItemSelected(menuitem);
_L3:
        menuitem = new AccountEditDialog(this, ((AccountList.AccountInfo) (obj)));
        menuitem.setOnAccountEditResultListener(this);
        menuitem.show();
        return true;
_L4:
        showAccountDeleteDialog(((AccountList.AccountInfo) (obj)));
        return true;
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(0x7f04005c);
        accountList = new AccountList(this);
        accountList.getAccountList(displayList);
        adapter = new AccountListAdapter(this, displayList);
        bundle = (ListView)findViewById(0x7f1001d9);
        bundle.setAdapter(adapter);
        bundle.setOnItemClickListener(this);
        findViewById(0x7f1001da).setOnClickListener(this);
        registerForContextMenu(bundle);
    }

    public void onCreateContextMenu(ContextMenu contextmenu, View view, android.view.ContextMenu.ContextMenuInfo contextmenuinfo)
    {
        super.onCreateContextMenu(contextmenu, view, contextmenuinfo);
        getMenuInflater().inflate(0x7f120000, contextmenu);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(0x7f120010, menu);
        return true;
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        adapterview = ((AdapterView) (adapterview.getItemAtPosition(i)));
        if (adapterview instanceof AccountList.AccountInfo)
        {
            adapterview = (AccountList.AccountInfo)adapterview;
            view = new Intent();
            view.putExtra("selected_account", adapterview);
            setResult(-1, view);
            finish();
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        switch (menuitem.getItemId())
        {
        default:
            return super.onOptionsItemSelected(menuitem);

        case 2131755821: 
            startActivity(new Intent(this, com/lumiyaviewer/lumiya/ui/grids/ManageGridsActivity));
            break;
        }
        return true;
    }

    public void onResume()
    {
        super.onResume();
        adapter.updateGridList();
    }
}
