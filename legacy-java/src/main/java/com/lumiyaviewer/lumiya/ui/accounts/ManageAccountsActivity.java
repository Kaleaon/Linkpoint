package com.lumiyaviewer.lumiya.ui.accounts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.accounts.AccountEditDialog;
import com.lumiyaviewer.lumiya.ui.accounts.AccountList;
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity;
import com.lumiyaviewer.lumiya.ui.grids.GridList;
import com.lumiyaviewer.lumiya.ui.grids.ManageGridsActivity;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ManageAccountsActivity extends ThemedActivity implements AccountEditDialog.OnAccountEditResultListener, AdapterView.OnItemClickListener, View.OnClickListener {
    private AccountListAdapter adapter;
    private AccountList accountList = null;
    private List<AccountList.AccountInfo> displayList = new ArrayList();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AccountListAdapter extends ArrayAdapter<AccountList.AccountInfo> {
        private GridList gridList;

        AccountListAdapter(Context context, List<AccountList.AccountInfo> list) {
            super(context, R.layout.account_list_item, list);
            this.gridList = new GridList(context);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
            if (view == null) {
                view = layoutInflater.inflate(R.layout.account_list_item, viewGroup, false);
            }
            TextView textView = (TextView) view.findViewById(R.id.accountNameTextView);
            TextView textView2 = (TextView) view.findViewById(R.id.gridNameTextView);
            AccountList.AccountInfo item = getItem(i);
            if (item != null) {
                AccountList.AccountInfo accountInfo = item;
                GridList.GridInfo gridByUUID = this.gridList.getGridByUUID(accountInfo.getGridUUID());
                textView.setText(accountInfo.getLoginName());
                textView2.setText(gridByUUID != null ? gridByUUID.getGridName() : "");
            }
            return view;
        }

        void updateGridList() {
            this.gridList.loadGrids();
        }

        void updateList() {
            super.notifyDataSetChanged();
        }
    }

    private void deleteAccount(AccountList.AccountInfo accountInfo) {
        this.accountList.deleteAccount(accountInfo);
        this.accountList.savePreferences();
        this.accountList.getAccountList(this.displayList);
        this.adapter.updateList();
    }

    private void showAccountDeleteDialog(final AccountList.AccountInfo accountInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.account_delete_confirm_title)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.accounts.-$Lambda$c901yk_brt0jPczBoAMr-Jn1w74.1
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((ManageAccountsActivity) this).m396xf58c202e((AccountList.AccountInfo) accountInfo, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.accounts.-$Lambda$c901yk_brt0jPczBoAMr-Jn1w74
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.create().show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_accounts_ManageAccountsActivity_6140, reason: not valid java name */
    public /* synthetic */ void m396xf58c202e(AccountList.AccountInfo accountInfo, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        deleteAccount(accountInfo);
    }

    @Override // com.lumiyaviewer.lumiya.ui.accounts.AccountEditDialog.OnAccountEditResultListener
    public void onAccountEditCancelled() {
    }

    @Override // com.lumiyaviewer.lumiya.ui.accounts.AccountEditDialog.OnAccountEditResultListener
    public void onAccountEdited(AccountList.AccountInfo accountInfo, boolean z) {
        if (z) {
            this.accountList.addNewAccount(accountInfo);
        }
        this.accountList.savePreferences();
        this.accountList.getAccountList(this.displayList);
        this.adapter.updateList();
        ListView listView = (ListView) findViewById(R.id.accountList);
        if (listView.getAdapter().getCount() > 0) {
            listView.setSelection(listView.getAdapter().getCount() - 1);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_new_account_button /* 2131755482 */:
                AccountEditDialog accountEditDialog = new AccountEditDialog(this, null);
                accountEditDialog.setOnAccountEditResultListener(this);
                accountEditDialog.show();
                return;
            default:
                return;
        }
    }

    @Override // android.app.Activity
    public boolean onContextItemSelected(MenuItem menuItem) {
        AccountList.AccountInfo item = this.adapter.getItem(((AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo()).position);
        if (item != null) {
            AccountList.AccountInfo accountInfo = item;
            switch (menuItem.getItemId()) {
                case R.id.item_account_edit /* 2131755767 */:
                    AccountEditDialog accountEditDialog = new AccountEditDialog(this, accountInfo);
                    accountEditDialog.setOnAccountEditResultListener(this);
                    accountEditDialog.show();
                    return true;
                case R.id.item_account_delete /* 2131755768 */:
                    showAccountDeleteDialog(accountInfo);
                    return true;
            }
        }
        return super.onContextItemSelected(menuItem);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ThemedActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.manage_accounts);
        this.accountList = new AccountList(this);
        this.accountList.getAccountList(this.displayList);
        this.adapter = new AccountListAdapter(this, this.displayList);
        ListView listView = (ListView) findViewById(R.id.accountList);
        listView.setAdapter((ListAdapter) this.adapter);
        listView.setOnItemClickListener(this);
        findViewById(R.id.add_new_account_button).setOnClickListener(this);
        registerForContextMenu(listView);
    }

    @Override // android.app.Activity, android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        getMenuInflater().inflate(R.menu.account_list_context_menu, contextMenu);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_accounts_menu, menu);
        return true;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        Object itemAtPosition = adapterView.getItemAtPosition(i);
        if (itemAtPosition instanceof AccountList.AccountInfo) {
            Intent intent = new Intent();
            intent.putExtra("selected_account", (AccountList.AccountInfo) itemAtPosition);
            setResult(-1, intent);
            finish();
        }
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_manage_grids /* 2131755821 */:
                startActivity(new Intent(this, (Class<?>) ManageGridsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ThemedActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        this.adapter.updateGridList();
    }
}
