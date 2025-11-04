// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.accounts;

import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.lumiyaviewer.lumiya.ui.grids.GridList;
import android.widget.ArrayAdapter;
import com.lumiyaviewer.lumiya.ui.grids.ManageGridsActivity;
import android.os.Parcelable;
import android.content.Intent;
import android.widget.AdapterView;
import android.view.Menu;
import android.view.ContextMenu$ContextMenuInfo;
import android.view.ContextMenu;
import android.widget.ListAdapter;
import android.os.Bundle;
import android.widget.AdapterView$AdapterContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.content.DialogInterface$OnClickListener;
import android.content.Context;
import android.app.AlertDialog$Builder;
import android.content.DialogInterface;
import java.util.ArrayList;
import java.util.List;
import android.view.View$OnClickListener;
import android.widget.AdapterView$OnItemClickListener;
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity;

public class ManageAccountsActivity extends ThemedActivity implements OnAccountEditResultListener, AdapterView$OnItemClickListener, View$OnClickListener
{
    private AccountList accountList;
    private AccountListAdapter adapter;
    private List<AccountList.AccountInfo> displayList;
    
    public ManageAccountsActivity() {
        this.accountList = null;
        this.displayList = new ArrayList<AccountList.AccountInfo>();
    }
    
    private void deleteAccount(final AccountList.AccountInfo accountInfo) {
        this.accountList.deleteAccount(accountInfo);
        this.accountList.savePreferences();
        this.accountList.getAccountList(this.displayList);
        this.adapter.updateList();
    }
    
    private void showAccountDeleteDialog(final AccountList.AccountInfo accountInfo) {
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder((Context)this);
        alertDialog$Builder.setMessage((CharSequence)this.getString(2131296313)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$c901yk_brt0jPczBoAMr_Jn1w74$1(this, accountInfo)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$c901yk_brt0jPczBoAMr_Jn1w74());
        alertDialog$Builder.create().show();
    }
    
    @Override
    public void onAccountEditCancelled() {
    }
    
    @Override
    public void onAccountEdited(final AccountList.AccountInfo accountInfo, final boolean b) {
        if (b) {
            this.accountList.addNewAccount(accountInfo);
        }
        this.accountList.savePreferences();
        this.accountList.getAccountList(this.displayList);
        this.adapter.updateList();
        final ListView listView = this.findViewById(2131755481);
        if (listView.getAdapter().getCount() > 0) {
            listView.setSelection(listView.getAdapter().getCount() - 1);
        }
    }
    
    public void onClick(final View view) {
        switch (view.getId()) {
            case 2131755482: {
                final AccountEditDialog accountEditDialog = new AccountEditDialog((Context)this, null);
                accountEditDialog.setOnAccountEditResultListener((AccountEditDialog.OnAccountEditResultListener)this);
                accountEditDialog.show();
                break;
            }
        }
    }
    
    public boolean onContextItemSelected(final MenuItem menuItem) {
        final Object item = this.adapter.getItem(((AdapterView$AdapterContextMenuInfo)menuItem.getMenuInfo()).position);
        if (item != null) {
            final AccountList.AccountInfo accountInfo = (AccountList.AccountInfo)item;
            switch (menuItem.getItemId()) {
                case 2131755767: {
                    final AccountEditDialog accountEditDialog = new AccountEditDialog((Context)this, accountInfo);
                    accountEditDialog.setOnAccountEditResultListener((AccountEditDialog.OnAccountEditResultListener)this);
                    accountEditDialog.show();
                    return true;
                }
                case 2131755768: {
                    this.showAccountDeleteDialog(accountInfo);
                    return true;
                }
            }
        }
        return super.onContextItemSelected(menuItem);
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(2130968668);
        (this.accountList = new AccountList((Context)this)).getAccountList(this.displayList);
        this.adapter = new AccountListAdapter((Context)this, this.displayList);
        final ListView listView = this.findViewById(2131755481);
        listView.setAdapter((ListAdapter)this.adapter);
        listView.setOnItemClickListener((AdapterView$OnItemClickListener)this);
        this.findViewById(2131755482).setOnClickListener((View$OnClickListener)this);
        this.registerForContextMenu((View)listView);
    }
    
    public void onCreateContextMenu(final ContextMenu contextMenu, final View view, final ContextMenu$ContextMenuInfo contextMenu$ContextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenu$ContextMenuInfo);
        this.getMenuInflater().inflate(2131886080, (Menu)contextMenu);
    }
    
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.getMenuInflater().inflate(2131886096, menu);
        return true;
    }
    
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
        final Object itemAtPosition = adapterView.getItemAtPosition(n);
        if (itemAtPosition instanceof AccountList.AccountInfo) {
            final AccountList.AccountInfo accountInfo = (AccountList.AccountInfo)itemAtPosition;
            final Intent intent = new Intent();
            intent.putExtra("selected_account", (Parcelable)accountInfo);
            this.setResult(-1, intent);
            this.finish();
        }
    }
    
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            default: {
                return super.onOptionsItemSelected(menuItem);
            }
            case 2131755821: {
                this.startActivity(new Intent((Context)this, (Class)ManageGridsActivity.class));
                return true;
            }
        }
    }
    
    public void onResume() {
        super.onResume();
        this.adapter.updateGridList();
    }
    
    private static class AccountListAdapter extends ArrayAdapter<AccountList.AccountInfo>
    {
        private GridList gridList;
        
        AccountListAdapter(final Context context, final List<AccountList.AccountInfo> list) {
            super(context, 2130968604, (List)list);
            this.gridList = new GridList(context);
        }
        
        public View getView(final int n, final View view, final ViewGroup viewGroup) {
            final LayoutInflater layoutInflater = (LayoutInflater)this.getContext().getSystemService("layout_inflater");
            View inflate = view;
            if (view == null) {
                inflate = layoutInflater.inflate(2130968604, viewGroup, false);
            }
            final TextView textView = (TextView)inflate.findViewById(2131755193);
            final TextView textView2 = (TextView)inflate.findViewById(2131755194);
            final Object item = this.getItem(n);
            if (item != null) {
                final AccountList.AccountInfo accountInfo = (AccountList.AccountInfo)item;
                final GridList.GridInfo gridByUUID = this.gridList.getGridByUUID(accountInfo.getGridUUID());
                textView.setText((CharSequence)accountInfo.getLoginName());
                String gridName;
                if (gridByUUID != null) {
                    gridName = gridByUUID.getGridName();
                }
                else {
                    gridName = "";
                }
                textView2.setText((CharSequence)gridName);
            }
            return inflate;
        }
        
        void updateGridList() {
            this.gridList.loadGrids();
        }
        
        void updateList() {
            super.notifyDataSetChanged();
        }
    }
}
