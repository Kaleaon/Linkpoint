package com.lumiyaviewer.lumiya.ui.accounts

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity
import com.lumiyaviewer.lumiya.ui.grids.GridList

class ManageAccountsActivity :
    ThemedActivity(),
    AccountEditDialog.OnAccountEditResultListener,
    AdapterView.OnItemClickListener,
    View.OnClickListener {
    private lateinit var accountList: AccountList
    private lateinit var adapter: AccountListAdapter
    private val displayList = ArrayList<AccountList.AccountInfo>()
    private lateinit var accountListView: ListView

    private class AccountListAdapter(
        context: Context,
        list: List<AccountList.AccountInfo>,
    ) : ArrayAdapter<AccountList.AccountInfo>(context, R.layout.account_list_item, list) {
        private val gridList: GridList = GridList(context)

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup,
        ): View {
            val view =
                convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.account_list_item, parent, false)

            val accountNameTextView = view.findViewById<TextView>(R.id.accountNameTextView)
            val gridNameTextView = view.findViewById<TextView>(R.id.gridNameTextView)
            val item = getItem(position)

            item?.let {
                val grid = gridList.getGridByUUID(it.getGridUUID())
                accountNameTextView.text = it.getLoginName()
                gridNameTextView.text = grid?.getGridName() ?: ""
            }
            return view
        }

        fun updateGridList() {
            gridList.loadGrids()
        }

        fun updateList() {
            notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manage_accounts)

        accountList = AccountList(this)
        accountList.getAccountList(displayList)
        adapter = AccountListAdapter(this, displayList)

        accountListView = findViewById(R.id.accountList)
        accountListView.adapter = adapter
        accountListView.onItemClickListener = this
        findViewById<Button>(R.id.add_new_account_button).setOnClickListener(this)
        registerForContextMenu(accountListView)
    }

    override fun onResume() {
        super.onResume()
        adapter.updateGridList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.manage_accounts_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_manage_grids -> {
                startActivity(Intent(this, ManageGridsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo,
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.account_list_context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val accountInfo = adapter.getItem(info.position)
        accountInfo?.let {
            when (item.itemId) {
                R.id.item_account_edit -> {
                    AccountEditDialog(this, it).apply {
                        setOnAccountEditResultListener(this@ManageAccountsActivity)
                        show()
                    }
                    return true
                }
                R.id.item_account_delete -> {
                    showAccountDeleteDialog(it)
                    return true
                }
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.add_new_account_button -> {
                AccountEditDialog(this, null).apply {
                    setOnAccountEditResultListener(this@ManageAccountsActivity)
                    show()
                }
            }
        }
    }

    override fun onItemClick(
        parent: AdapterView<*>,
        view: View,
        position: Int,
        id: Long,
    ) {
        val item = parent.getItemAtPosition(position)
        if (item is AccountList.AccountInfo) {
            val resultIntent =
                Intent().apply {
                    putExtra("selected_account", item)
                }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun onAccountEditCancelled() {
        // No action needed
    }

    override fun onAccountEdited(
        accountInfo: AccountList.AccountInfo,
        isNew: Boolean,
    ) {
        if (isNew) {
            accountList.addNewAccount(accountInfo)
        }
        accountList.savePreferences()
        accountList.getAccountList(displayList)
        adapter.updateList()
        if (adapter.count > 0) {
            accountListView.setSelection(adapter.count - 1)
        }
    }

    private fun showAccountDeleteDialog(accountInfo: AccountList.AccountInfo) {
        AlertDialog.Builder(this)
            .setMessage(R.string.account_delete_confirm_title)
            .setCancelable(true)
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()
                deleteAccount(accountInfo)
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun deleteAccount(accountInfo: AccountList.AccountInfo) {
        accountList.deleteAccount(accountInfo)
        accountList.savePreferences()
        accountList.getAccountList(displayList)
        adapter.updateList()
    }
}
