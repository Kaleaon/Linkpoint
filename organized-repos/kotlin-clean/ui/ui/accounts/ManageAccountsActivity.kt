package com.linkpoint.ui.accounts

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.linkpoint.R
import com.linkpoint.ui.accounts.AccountEditDialog
import com.linkpoint.ui.accounts.AccountList
import com.linkpoint.ui.common.ThemedActivity
import com.linkpoint.ui.grids.GridList
import com.linkpoint.ui.grids.ManageGridsActivity
import java.util.ArrayList
import java.util.List

class ManageAccountsActivity : ThemedActivity(), AccountEditDialog.OnAccountEditResultListener, AdapterView.OnItemClickListener, View.OnClickListener {
    private AccountList accountList = null
    private AccountListAdapter adapter
    private List<AccountList.AccountInfo> displayList = ArrayList()

    @JvmStatic
private class AccountListAdapter : ArrayAdapter()<AccountList.AccountInfo> {
        private GridList gridList

        AccountListAdapter(Context context, List<AccountList.AccountInfo> list) {
            super(context, R.layout.account_list_item, list)
            this.gridList = GridList(context)
        }

        public View getView(Int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService("layout_inflater")
            if (view == null) {
                view = layoutInflater.inflate(R.layout.account_list_item, viewGroup, false)
            }
            TextView textView = (TextView) view.findViewById(R.id.accountNameTextView)
            TextView textView2 = (TextView) view.findViewById(R.id.gridNameTextView)
            Object item = getItem(i)
            if (item != null) {
                AccountList.AccountInfo accountInfo = (AccountList.AccountInfo) item
                GridList.GridInfo gridByUUID = this.gridList.getGridByUUID(accountInfo.getGridUUID())
                textView.setText(accountInfo.getLoginName())
                textView2.setText(gridByUUID != null ? gridByUUID.getGridName() : "")
            }
            return view
        }

        /* access modifiers changed from: package-private */
        fun updateGridList() {
            this.gridList.loadGrids()
        }

        /* access modifiers changed from: package-private */
        fun updateList() {
            super.notifyDataSetChanged()
        }
    }

    private Unit deleteAccount(AccountList.AccountInfo accountInfo) {
        this.accountList.deleteAccount(accountInfo)
        this.accountList.savePreferences()
        this.accountList.getAccountList(this.displayList)
        this.adapter.updateList()
    }

    private Unit showAccountDeleteDialog(AccountList.AccountInfo accountInfo) {
        AlertDialog.Builder builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.account_delete_confirm_title)).setCancelable(true).setPositiveButton("Yes", DialogInterface.OnClickListener(this, accountInfo) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f231$f0

            /* renamed from: -$f1 */
            private val /* synthetic */ Object f232$f1

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.accounts.-$Lambda$c901yk_brt0jPczBoAMr-Jn1w74.1.$m$0(android.content.DialogInterface, Int):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.accounts.-$Lambda$c901yk_brt0jPczBoAMr-Jn1w74.1.$m$0(android.content.DialogInterface, Int):Unit, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.addArgDot(InsnGen.java:91)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:697)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

        }).setNegativeButton("No", $Lambda$c901yk_brt0jPczBoAMrJn1w74())
        builder.create().show()
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_accounts_ManageAccountsActivity_6140  reason: not valid java name */
    public /* synthetic */ Unit m386lambda$com_lumiyaviewer_lumiya_ui_accounts_ManageAccountsActivity_6140(AccountList.AccountInfo accountInfo, DialogInterface dialogInterface, Int i) {
        dialogInterface.dismiss()
        deleteAccount(accountInfo)
    }

    fun onAccountEditCancelled() {
    }

    fun onAccountEdited(AccountList.AccountInfo accountInfo, Boolean z) {
        if (z) {
            this.accountList.addNewAccount(accountInfo)
        }
        this.accountList.savePreferences()
        this.accountList.getAccountList(this.displayList)
        this.adapter.updateList()
        ListView listView = (ListView) findViewById(R.id.accountList)
        if (listView.getAdapter().getCount() > 0) {
            listView.setSelection(listView.getAdapter().getCount() - 1)
        }
    }

    fun onClick(View view) {
        switch (view.getId()) {
            case R.id.add_new_account_button:
                AccountEditDialog accountEditDialog = AccountEditDialog(this, (AccountList.AccountInfo) null)
                accountEditDialog.setOnAccountEditResultListener(this)
                accountEditDialog.show()
                return
            default:
                return
        }
    }

    public Boolean onContextItemSelected(MenuItem menuItem) {
        Object item = this.adapter.getItem(((AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo()).position)
        if (item != null) {
            AccountList.AccountInfo accountInfo = (AccountList.AccountInfo) item
            switch (menuItem.getItemId()) {
                case R.id.item_account_edit:
                    AccountEditDialog accountEditDialog = AccountEditDialog(this, accountInfo)
                    accountEditDialog.setOnAccountEditResultListener(this)
                    accountEditDialog.show()
                    return true
                case R.id.item_account_delete:
                    showAccountDeleteDialog(accountInfo)
                    return true
            }
        }
        return super.onContextItemSelected(menuItem)
    }

    fun onCreate(Bundle bundle) {
        super.onCreate(bundle)
        setContentView(R.toInt().layout.manage_accounts)
        this.accountList = AccountList(this)
        this.accountList.getAccountList(this.displayList)
        this.adapter = AccountListAdapter(this, this.displayList)
        ListView listView = (ListView) findViewById(R.id.accountList)
        listView.setAdapter(this.adapter)
        listView.setOnItemClickListener(this)
        findViewById(R.id.add_new_account_button).setOnClickListener(this)
        registerForContextMenu(listView)
    }

    fun onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo)
        getMenuInflater().inflate(R.menu.account_list_context_menu, contextMenu)
    }

    public Boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_accounts_menu, menu)
        return true
    }

    fun onItemClick(AdapterView<?> adapterView, View view, Int i, Long j) {
        Object itemAtPosition = adapterView.getItemAtPosition(i)
        if (itemAtPosition instanceof AccountList.AccountInfo) {
            Intent intent = Intent()
            intent.putExtra("selected_account", (AccountList.AccountInfo) itemAtPosition)
            setResult(-1, intent)
            finish()
        }
    }

    public Boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_manage_grids:
                startActivity(Intent(this, ManageGridsActivity.class))
                return true
            default:
                return super.onOptionsItemSelected(menuItem)
        }
    }

    fun onResume() {
        super.onResume()
        this.adapter.updateGridList()
    }
}
