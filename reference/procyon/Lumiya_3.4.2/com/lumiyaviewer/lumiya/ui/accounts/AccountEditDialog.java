// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.accounts;

import android.widget.SpinnerAdapter;
import java.util.List;
import android.os.Bundle;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuth;
import android.widget.Toast;
import android.view.View;
import android.text.Editable;
import android.widget.Button;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Context;
import com.lumiyaviewer.lumiya.ui.grids.GridList;
import android.text.TextWatcher;
import android.view.View$OnClickListener;
import android.support.v7.app.AppCompatDialog;

class AccountEditDialog extends AppCompatDialog implements View$OnClickListener, TextWatcher
{
    private AccountList.AccountInfo editAccount;
    private GridList gridList;
    private OnAccountEditResultListener onAccountEditResultListener;
    
    AccountEditDialog(final Context context, final AccountList.AccountInfo editAccount) {
        super(context);
        this.onAccountEditResultListener = null;
        this.editAccount = null;
        this.gridList = null;
        this.gridList = new GridList(context);
        this.editAccount = editAccount;
    }
    
    private void prepare() {
        this.gridList.loadGrids();
        if (this.editAccount != null) {
            this.findViewById(2131755187).setText((CharSequence)this.editAccount.getLoginName());
            this.findViewById(2131755189).setSelection(this.gridList.getGridIndex(this.editAccount.getGridUUID()));
            if (!this.editAccount.getPasswordHash().equals("")) {
                this.findViewById(2131755188).setTag((Object)null);
                this.findViewById(2131755188).setText((CharSequence)"(Saved password)");
                this.findViewById(2131755188).setInputType(1);
                this.findViewById(2131755188).setTransformationMethod((TransformationMethod)SingleLineTransformationMethod.getInstance());
                this.findViewById(2131755188).setTag((Object)1);
            }
            else {
                this.findViewById(2131755188).setTag((Object)null);
                this.findViewById(2131755188).setText((CharSequence)"");
                this.findViewById(2131755188).setInputType(129);
                this.findViewById(2131755188).setTransformationMethod((TransformationMethod)PasswordTransformationMethod.getInstance());
            }
            this.findViewById(2131755190).setText(2131296991);
            this.setTitle(2131296518);
        }
        else {
            this.findViewById(2131755188).setTag((Object)null);
            this.findViewById(2131755187).setText((CharSequence)"");
            this.findViewById(2131755188).setText((CharSequence)"");
            this.findViewById(2131755188).setInputType(129);
            this.findViewById(2131755188).setTransformationMethod((TransformationMethod)PasswordTransformationMethod.getInstance());
            this.findViewById(2131755189).setSelection(0);
            this.findViewById(2131755190).setText(2131296316);
            this.setTitle(2131296726);
        }
        this.findViewById(2131755187).requestFocus();
    }
    
    public void afterTextChanged(final Editable editable) {
    }
    
    public void beforeTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
        final TextView textView = this.findViewById(2131755188);
        if (textView.getTag() != null) {
            textView.setTag((Object)null);
            textView.setInputType(129);
            this.findViewById(2131755188).setTransformationMethod((TransformationMethod)PasswordTransformationMethod.getInstance());
            textView.setText((CharSequence)"");
        }
    }
    
    public void onClick(final View view) {
        boolean b = false;
        switch (view.getId()) {
            case 2131755190: {
                final String string = this.findViewById(2131755187).getText().toString();
                final String string2 = this.findViewById(2131755188).getText().toString();
                String passwordHash = "";
                final Object selectedItem = this.findViewById(2131755189).getSelectedItem();
                UUID gridUUID;
                if (selectedItem instanceof GridList.GridInfo) {
                    gridUUID = ((GridList.GridInfo)selectedItem).getGridUUID();
                }
                else {
                    gridUUID = null;
                }
                if (string.equals("")) {
                    Toast.makeText(this.getContext(), (CharSequence)this.getContext().getString(2131296663), 0).show();
                    break;
                }
                int n;
                if (!string2.equals("(Saved password)")) {
                    if (!string2.equals("")) {
                        passwordHash = SLAuth.getPasswordHash(string2);
                        n = 0;
                    }
                    else {
                        passwordHash = "";
                        n = 0;
                    }
                }
                else {
                    n = 1;
                }
                this.dismiss();
                if (this.onAccountEditResultListener != null) {
                    final AccountList.AccountInfo editAccount = this.editAccount;
                    Object o;
                    if (editAccount == null) {
                        o = new AccountList.AccountInfo(string, passwordHash, gridUUID);
                        b = true;
                    }
                    else {
                        editAccount.setLoginName(string);
                        editAccount.setGridUUID(gridUUID);
                        if (n == 0) {
                            editAccount.setPasswordHash(passwordHash);
                            o = editAccount;
                        }
                        else {
                            o = editAccount;
                        }
                    }
                    this.onAccountEditResultListener.onAccountEdited((AccountList.AccountInfo)o, b);
                    break;
                }
                break;
            }
            case 2131755191: {
                this.dismiss();
                if (this.onAccountEditResultListener != null) {
                    this.onAccountEditResultListener.onAccountEditCancelled();
                    break;
                }
                break;
            }
        }
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setTitle(2131296726);
        this.setContentView(2130968603);
        this.findViewById(2131755190).setOnClickListener((View$OnClickListener)this);
        this.findViewById(2131755191).setOnClickListener((View$OnClickListener)this);
        this.findViewById(2131755188).addTextChangedListener((TextWatcher)this);
        this.findViewById(2131755189).setAdapter((SpinnerAdapter)new GridList.GridArrayAdapter(this.getContext(), this.gridList.getGridList(null, false)));
        this.prepare();
    }
    
    public void onTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
    }
    
    void setOnAccountEditResultListener(final OnAccountEditResultListener onAccountEditResultListener) {
        this.onAccountEditResultListener = onAccountEditResultListener;
    }
    
    interface OnAccountEditResultListener
    {
        void onAccountEditCancelled();
        
        void onAccountEdited(final AccountList.AccountInfo p0, final boolean p1);
    }
}
