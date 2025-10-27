package com.linkpoint.ui.accounts

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatDialog
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.linkpoint.R
import com.linkpoint.slproto.auth.SLAuth
import com.linkpoint.ui.accounts.AccountList
import com.linkpoint.ui.grids.GridList
import java.util.List
import java.util.UUID

class AccountEditDialog : AppCompatDialog(), View.OnClickListener, TextWatcher {
    private AccountList.AccountInfo editAccount = null
    private GridList gridList = null
    private OnAccountEditResultListener onAccountEditResultListener = null

    interface OnAccountEditResultListener {
         fun onAccountEditCancelled()

         fun onAccountEdited(AccountList.AccountInfo accountInfo, z: Boolean)
    }

    AccountEditDialog(Context context, AccountList.AccountInfo accountInfo) {
        super(context)
        this.gridList = GridList(context)
        this.editAccount = accountInfo
    }

     private fun prepare() {
        this.gridList.loadGrids()
        if (this.editAccount != null) {
            ((TextView) findViewById(R.id.loginNameText)).setText(this.editAccount.getLoginName())
            ((Spinner) findViewById(R.id.spinnerGrid)).setSelection(this.gridList.getGridIndex(this.editAccount.getGridUUID()))
            if (!this.editAccount.getPasswordHash().equals("")) {
                findViewById(R.id.loginPasswordText).setTag((Object) null)
                ((TextView) findViewById(R.id.loginPasswordText)).setText("(Saved password)")
                ((TextView) findViewById(R.id.loginPasswordText)).setInputType(1)
                ((EditText) findViewById(R.id.loginPasswordText)).setTransformationMethod(SingleLineTransformationMethod.getInstance())
                findViewById(R.id.loginPasswordText).setTag(1)
            } else {
                findViewById(R.id.loginPasswordText).setTag((Object) null)
                ((TextView) findViewById(R.id.loginPasswordText)).setText("")
                ((TextView) findViewById(R.id.loginPasswordText)).setInputType(129)
                ((EditText) findViewById(R.id.loginPasswordText)).setTransformationMethod(PasswordTransformationMethod.getInstance())
            }
            ((Button) findViewById(R.id.okButton)).setText(R.string.save_changes)
            setTitle((Int) R.string.edit_account_dialog_title)
        } else {
            findViewById(R.id.loginPasswordText).setTag((Object) null)
            ((TextView) findViewById(R.id.loginNameText)).setText("")
            ((TextView) findViewById(R.id.loginPasswordText)).setText("")
            ((TextView) findViewById(R.id.loginPasswordText)).setInputType(129)
            ((EditText) findViewById(R.id.loginPasswordText)).setTransformationMethod(PasswordTransformationMethod.getInstance())
            ((Spinner) findViewById(R.id.spinnerGrid)).setSelection(0)
            ((Button) findViewById(R.id.okButton)).setText(R.string.add_new_account)
            setTitle((Int) R.string.new_account_dialog_title)
        }
        ((TextView) findViewById(R.id.loginNameText)).requestFocus()
    }

    fun afterTextChanged(editable: Editable) {
    }

    fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
        val textView: TextView = (TextView) findViewById(R.id.loginPasswordText)
        if (textView.getTag() != null) {
            textView.setTag((Object) null)
            textView.setInputType(129)
            ((EditText) findViewById(R.id.loginPasswordText)).setTransformationMethod(PasswordTransformationMethod.getInstance())
            textView.setText("")
        }
    }

    override fun onClick(view: View) {
        AccountList.AccountInfo accountInfo
        val z2: Boolean = false
        switch (view.getId()) {
            case R.id.okButton:
                val charSequence: String = ((TextView) findViewById(R.id.loginNameText)).getText().toString()
                val charSequence2: String = ((TextView) findViewById(R.id.loginPasswordText)).getText().toString()
                val str: String = ""
                val selectedItem: Object = ((Spinner) findViewById(R.id.spinnerGrid)).getSelectedItem()
                val gridUUID: UUID = selectedItem instanceof GridList.GridInfo ? ((GridList.GridInfo) selectedItem).getGridUUID() : null
                if (charSequence.equals("")) {
                    Toast.makeText(getContext(), getContext().getString(R.string.login_name_empty_error), 0).show()
                    return
                }
                if (charSequence2.equals("(Saved password)")) {
                    z = true
                } else if (!charSequence2.equals("")) {
                    str = SLAuth.getPasswordHash(charSequence2)
                    z = false
                } else {
                    str = ""
                    z = false
                }
                dismiss()
                if (this.onAccountEditResultListener != null) {
                    AccountList.AccountInfo accountInfo2 = this.editAccount
                    if (accountInfo2 == null) {
                        z2 = true
                        accountInfo = AccountList.AccountInfo(charSequence, str, gridUUID)
                    } else {
                        accountInfo2.setLoginName(charSequence)
                        accountInfo2.setGridUUID(gridUUID)
                        if (!z) {
                            accountInfo2.setPasswordHash(str)
                            accountInfo = accountInfo2
                        } else {
                            accountInfo = accountInfo2
                        }
                    }
                    this.onAccountEditResultListener.onAccountEdited(accountInfo, z2)
                    return
                }
                return
            case R.id.cancelButton:
                dismiss()
                if (this.onAccountEditResultListener != null) {
                    this.onAccountEditResultListener.onAccountEditCancelled()
                    return
                }
                return
            default:
                return
        }
    }

    override fun onCreate(bundle: Bundle) {
        super.onCreate(bundle)
        setTitle((Int) R.string.new_account_dialog_title)
        setContentView((Int) R.layout.account_edit_dialog)
        findViewById(R.id.okButton).setOnClickListener(this)
        findViewById(R.id.cancelButton).setOnClickListener(this)
        ((EditText) findViewById(R.id.loginPasswordText)).addTextChangedListener(this)
        ((Spinner) findViewById(R.id.spinnerGrid)).setAdapter(GridList.GridArrayAdapter(getContext(), this.gridList.getGridList((List<GridList.GridInfo>) null, false)))
        prepare()
    }

    fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
    }

    /* access modifiers changed from: package-private */
    fun setOnAccountEditResultListener(onAccountEditResultListener2: OnAccountEditResultListener) {
        this.onAccountEditResultListener = onAccountEditResultListener2
    }
}
