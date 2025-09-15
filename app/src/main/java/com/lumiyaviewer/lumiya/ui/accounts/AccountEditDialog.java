package com.lumiyaviewer.lumiya.ui.accounts;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuth;
import com.lumiyaviewer.lumiya.ui.accounts.AccountList;
import com.lumiyaviewer.lumiya.ui.grids.GridList;
import java.util.List;
import java.util.UUID;

class AccountEditDialog extends AppCompatDialog implements View.OnClickListener, TextWatcher {
    private AccountList.AccountInfo editAccount = null;
    private GridList gridList = null;
    private OnAccountEditResultListener onAccountEditResultListener = null;

    interface OnAccountEditResultListener {
        void onAccountEditCancelled();

        void onAccountEdited(AccountList.AccountInfo accountInfo, boolean z);
    }

    AccountEditDialog(Context context, AccountList.AccountInfo accountInfo) {
        super(context);
        this.gridList = new GridList(context);
        this.editAccount = accountInfo;
    }

    private void prepare() {
        this.gridList.loadGrids();
        if (this.editAccount != null) {
            ((TextView) findViewById(R.id.loginNameText)).setText(this.editAccount.getLoginName());
            ((Spinner) findViewById(R.id.spinnerGrid)).setSelection(this.gridList.getGridIndex(this.editAccount.getGridUUID()));
            if (!this.editAccount.getPasswordHash().equals("")) {
                findViewById(R.id.loginPasswordText).setTag((Object) null);
                ((TextView) findViewById(R.id.loginPasswordText)).setText("(Saved password)");
                ((TextView) findViewById(R.id.loginPasswordText)).setInputType(1);
                ((EditText) findViewById(R.id.loginPasswordText)).setTransformationMethod(SingleLineTransformationMethod.getInstance());
                findViewById(R.id.loginPasswordText).setTag(1);
            } else {
                findViewById(R.id.loginPasswordText).setTag((Object) null);
                ((TextView) findViewById(R.id.loginPasswordText)).setText("");
                ((TextView) findViewById(R.id.loginPasswordText)).setInputType(129);
                ((EditText) findViewById(R.id.loginPasswordText)).setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            ((Button) findViewById(R.id.okButton)).setText(R.string.save_changes);
            setTitle((int) R.string.edit_account_dialog_title);
        } else {
            findViewById(R.id.loginPasswordText).setTag((Object) null);
            ((TextView) findViewById(R.id.loginNameText)).setText("");
            ((TextView) findViewById(R.id.loginPasswordText)).setText("");
            ((TextView) findViewById(R.id.loginPasswordText)).setInputType(129);
            ((EditText) findViewById(R.id.loginPasswordText)).setTransformationMethod(PasswordTransformationMethod.getInstance());
            ((Spinner) findViewById(R.id.spinnerGrid)).setSelection(0);
            ((Button) findViewById(R.id.okButton)).setText(R.string.add_new_account);
            setTitle((int) R.string.new_account_dialog_title);
        }
        ((TextView) findViewById(R.id.loginNameText)).requestFocus();
    }

    public void afterTextChanged(Editable editable) {
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        TextView textView = (TextView) findViewById(R.id.loginPasswordText);
        if (textView.getTag() != null) {
            textView.setTag((Object) null);
            textView.setInputType(129);
            ((EditText) findViewById(R.id.loginPasswordText)).setTransformationMethod(PasswordTransformationMethod.getInstance());
            textView.setText("");
        }
    }

    public void onClick(View view) {
        AccountList.AccountInfo accountInfo;
        boolean z2 = false;
        switch (view.getId()) {
            case R.id.okButton:
                String charSequence = ((TextView) findViewById(R.id.loginNameText)).getText().toString();
                String charSequence2 = ((TextView) findViewById(R.id.loginPasswordText)).getText().toString();
                String str = "";
                Object selectedItem = ((Spinner) findViewById(R.id.spinnerGrid)).getSelectedItem();
                UUID gridUUID = selectedItem instanceof GridList.GridInfo ? ((GridList.GridInfo) selectedItem).getGridUUID() : null;
                if (charSequence.equals("")) {
                    Toast.makeText(getContext(), getContext().getString(R.string.login_name_empty_error), 0).show();
                    return;
                }
                if (charSequence2.equals("(Saved password)")) {
                    z = true;
                } else if (!charSequence2.equals("")) {
                    str = SLAuth.getPasswordHash(charSequence2);
                    z = false;
                } else {
                    str = "";
                    z = false;
                }
                dismiss();
                if (this.onAccountEditResultListener != null) {
                    AccountList.AccountInfo accountInfo2 = this.editAccount;
                    if (accountInfo2 == null) {
                        z2 = true;
                        accountInfo = new AccountList.AccountInfo(charSequence, str, gridUUID);
                    } else {
                        accountInfo2.setLoginName(charSequence);
                        accountInfo2.setGridUUID(gridUUID);
                        if (!z) {
                            accountInfo2.setPasswordHash(str);
                            accountInfo = accountInfo2;
                        } else {
                            accountInfo = accountInfo2;
                        }
                    }
                    this.onAccountEditResultListener.onAccountEdited(accountInfo, z2);
                    return;
                }
                return;
            case R.id.cancelButton:
                dismiss();
                if (this.onAccountEditResultListener != null) {
                    this.onAccountEditResultListener.onAccountEditCancelled();
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle((int) R.string.new_account_dialog_title);
        setContentView((int) R.layout.account_edit_dialog);
        findViewById(R.id.okButton).setOnClickListener(this);
        findViewById(R.id.cancelButton).setOnClickListener(this);
        ((EditText) findViewById(R.id.loginPasswordText)).addTextChangedListener(this);
        ((Spinner) findViewById(R.id.spinnerGrid)).setAdapter(new GridList.GridArrayAdapter(getContext(), this.gridList.getGridList((List<GridList.GridInfo>) null, false)));
        prepare();
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    /* access modifiers changed from: package-private */
    public void setOnAccountEditResultListener(OnAccountEditResultListener onAccountEditResultListener2) {
        this.onAccountEditResultListener = onAccountEditResultListener2;
    }
}
