package com.lumiyaviewer.lumiya.ui.accounts

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
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.slproto.auth.SLAuth
import com.lumiyaviewer.lumiya.ui.grids.GridList

internal class AccountEditDialog(
    context: Context,
    private var editAccount: AccountList.AccountInfo?,
) : AppCompatDialog(context), View.OnClickListener, TextWatcher {
    private lateinit var gridList: GridList
    private var onAccountEditResultListener: OnAccountEditResultListener? = null

    private lateinit var loginNameText: TextView
    private lateinit var loginPasswordText: EditText
    private lateinit var spinnerGrid: Spinner
    private lateinit var okButton: Button

    internal interface OnAccountEditResultListener {
        fun onAccountEditCancelled()

        fun onAccountEdited(
            accountInfo: AccountList.AccountInfo,
            isNew: Boolean,
        )
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setTitle(R.string.new_account_dialog_title)
        setContentView(R.layout.account_edit_dialog)

        loginNameText = findViewById(R.id.loginNameText)!!
        loginPasswordText = findViewById(R.id.loginPasswordText)!!
        spinnerGrid = findViewById(R.id.spinnerGrid)!!
        okButton = findViewById(R.id.okButton)!!

        findViewById<View>(R.id.okButton)?.setOnClickListener(this)
        findViewById<View>(R.id.cancelButton)?.setOnClickListener(this)
        loginPasswordText.addTextChangedListener(this)

        gridList = GridList(context)
        spinnerGrid.adapter = GridList.GridArrayAdapter(context, gridList.getGridList(null, false))

        prepare()
    }

    private fun prepare() {
        gridList.loadGrids()
        editAccount?.let {
            loginNameText.text = it.getLoginName()
            spinnerGrid.setSelection(gridList.getGridIndex(it.getGridUUID()))

            if (it.getPasswordHash().isNotEmpty()) {
                loginPasswordText.tag = 1
                loginPasswordText.setText("(Saved password)")
                loginPasswordText.transformationMethod = SingleLineTransformationMethod.getInstance()
            } else {
                loginPasswordText.tag = null
                loginPasswordText.setText("")
                loginPasswordText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            okButton.setText(R.string.save_changes)
            setTitle(R.string.edit_account_dialog_title)
        } ?: run {
            loginPasswordText.tag = null
            loginNameText.text = ""
            loginPasswordText.setText("")
            loginPasswordText.transformationMethod = PasswordTransformationMethod.getInstance()
            spinnerGrid.setSelection(0)
            okButton.setText(R.string.add_new_account)
            setTitle(R.string.new_account_dialog_title)
        }
        loginNameText.requestFocus()
    }

    override fun afterTextChanged(editable: Editable) {}

    override fun beforeTextChanged(
        charSequence: CharSequence,
        i: Int,
        i2: Int,
        i3: Int,
    ) {
        if (loginPasswordText.tag != null) {
            loginPasswordText.tag = null
            loginPasswordText.transformationMethod = PasswordTransformationMethod.getInstance()
            loginPasswordText.setText("")
        }
    }

    override fun onTextChanged(
        charSequence: CharSequence,
        i: Int,
        i2: Int,
        i3: Int,
    ) {}

    override fun onClick(view: View) {
        when (view.id) {
            R.id.okButton -> {
                val loginName = loginNameText.text.toString()
                val password = loginPasswordText.text.toString()
                val selectedItem = spinnerGrid.selectedItem
                val gridUUID = (selectedItem as? GridList.GridInfo)?.getGridUUID()

                if (loginName.isEmpty()) {
                    Toast.makeText(context, R.string.login_name_empty_error, Toast.LENGTH_SHORT).show()
                    return
                }

                val isPasswordSaved = password == "(Saved password)"
                val passwordHash =
                    if (isPasswordSaved) {
                        editAccount?.getPasswordHash() ?: ""
                    } else if (password.isNotEmpty()) {
                        SLAuth.getPasswordHash(password)
                    } else {
                        ""
                    }

                dismiss()

                val accountInfo =
                    editAccount?.also {
                        it.setLoginName(loginName)
                        it.setGridUUID(gridUUID)
                        if (!isPasswordSaved) {
                            it.setPasswordHash(passwordHash)
                        }
                    } ?: AccountList.AccountInfo(loginName, passwordHash, gridUUID)

                onAccountEditResultListener?.onAccountEdited(accountInfo, editAccount == null)
            }
            R.id.cancelButton -> {
                dismiss()
                onAccountEditResultListener?.onAccountEditCancelled()
            }
        }
    }

    fun setOnAccountEditResultListener(listener: OnAccountEditResultListener) {
        this.onAccountEditResultListener = listener
    }
}
