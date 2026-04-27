// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

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
import com.lumiyaviewer.lumiya.slproto.auth.SLAuth;
import com.lumiyaviewer.lumiya.ui.grids.GridList;

class AccountEditDialog extends AppCompatDialog
    implements android.view.View.OnClickListener, TextWatcher
{
    static interface OnAccountEditResultListener
    {

        public abstract void onAccountEditCancelled();

        public abstract void onAccountEdited(AccountList.AccountInfo accountinfo, boolean flag);
    }


    private AccountList.AccountInfo editAccount;
    private GridList gridList;
    private OnAccountEditResultListener onAccountEditResultListener;

    AccountEditDialog(Context context, AccountList.AccountInfo accountinfo)
    {
        super(context);
        onAccountEditResultListener = null;
        editAccount = null;
        gridList = null;
        gridList = new GridList(context);
        editAccount = accountinfo;
    }

    private void prepare()
    {
        gridList.loadGrids();
        if (editAccount != null)
        {
            ((TextView)findViewById(0x7f1000b3)).setText(editAccount.getLoginName());
            ((Spinner)findViewById(0x7f1000b5)).setSelection(gridList.getGridIndex(editAccount.getGridUUID()));
            if (!editAccount.getPasswordHash().equals(""))
            {
                findViewById(0x7f1000b4).setTag(null);
                ((TextView)findViewById(0x7f1000b4)).setText("(Saved password)");
                ((TextView)findViewById(0x7f1000b4)).setInputType(1);
                ((EditText)findViewById(0x7f1000b4)).setTransformationMethod(SingleLineTransformationMethod.getInstance());
                findViewById(0x7f1000b4).setTag(Integer.valueOf(1));
            } else
            {
                findViewById(0x7f1000b4).setTag(null);
                ((TextView)findViewById(0x7f1000b4)).setText("");
                ((TextView)findViewById(0x7f1000b4)).setInputType(129);
                ((EditText)findViewById(0x7f1000b4)).setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            ((Button)findViewById(0x7f1000b6)).setText(0x7f0902df);
            setTitle(0x7f090106);
        } else
        {
            findViewById(0x7f1000b4).setTag(null);
            ((TextView)findViewById(0x7f1000b3)).setText("");
            ((TextView)findViewById(0x7f1000b4)).setText("");
            ((TextView)findViewById(0x7f1000b4)).setInputType(129);
            ((EditText)findViewById(0x7f1000b4)).setTransformationMethod(PasswordTransformationMethod.getInstance());
            ((Spinner)findViewById(0x7f1000b5)).setSelection(0);
            ((Button)findViewById(0x7f1000b6)).setText(0x7f09003c);
            setTitle(0x7f0901d6);
        }
        ((TextView)findViewById(0x7f1000b3)).requestFocus();
    }

    public void afterTextChanged(Editable editable)
    {
    }

    public void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
    {
        charsequence = (TextView)findViewById(0x7f1000b4);
        if (charsequence.getTag() != null)
        {
            charsequence.setTag(null);
            charsequence.setInputType(129);
            ((EditText)findViewById(0x7f1000b4)).setTransformationMethod(PasswordTransformationMethod.getInstance());
            charsequence.setText("");
        }
    }

    public void onClick(View view)
    {
        boolean flag1 = false;
        view.getId();
        JVM INSTR tableswitch 2131755190 2131755191: default 28
    //                   2131755190 29
    //                   2131755191 237;
           goto _L1 _L2 _L3
_L1:
        return;
_L2:
        String s1 = ((TextView)findViewById(0x7f1000b3)).getText().toString();
        String s = ((TextView)findViewById(0x7f1000b4)).getText().toString();
        view = "";
        Object obj = ((Spinner)findViewById(0x7f1000b5)).getSelectedItem();
        boolean flag;
        if (obj instanceof com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo)
        {
            obj = ((com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo)obj).getGridUUID();
        } else
        {
            obj = null;
        }
        if (s1.equals(""))
        {
            Toast.makeText(getContext(), getContext().getString(0x7f090197), 0).show();
            return;
        }
        if (!s.equals("(Saved password)"))
        {
            if (!s.equals(""))
            {
                view = SLAuth.getPasswordHash(s);
                flag = false;
            } else
            {
                view = "";
                flag = false;
            }
        } else
        {
            flag = true;
        }
        dismiss();
        if (onAccountEditResultListener != null)
        {
            AccountList.AccountInfo accountinfo = editAccount;
            if (accountinfo == null)
            {
                view = new AccountList.AccountInfo(s1, view, ((java.util.UUID) (obj)));
                flag1 = true;
            } else
            {
                accountinfo.setLoginName(s1);
                accountinfo.setGridUUID(((java.util.UUID) (obj)));
                if (!flag)
                {
                    accountinfo.setPasswordHash(view);
                    view = accountinfo;
                } else
                {
                    view = accountinfo;
                }
            }
            onAccountEditResultListener.onAccountEdited(view, flag1);
            return;
        }
        continue; /* Loop/switch isn't completed */
_L3:
        dismiss();
        if (onAccountEditResultListener != null)
        {
            onAccountEditResultListener.onAccountEditCancelled();
            return;
        }
        if (true) goto _L1; else goto _L4
_L4:
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setTitle(0x7f0901d6);
        setContentView(0x7f04001b);
        findViewById(0x7f1000b6).setOnClickListener(this);
        findViewById(0x7f1000b7).setOnClickListener(this);
        ((EditText)findViewById(0x7f1000b4)).addTextChangedListener(this);
        ((Spinner)findViewById(0x7f1000b5)).setAdapter(new com.lumiyaviewer.lumiya.ui.grids.GridList.GridArrayAdapter(getContext(), gridList.getGridList(null, false)));
        prepare();
    }

    public void onTextChanged(CharSequence charsequence, int i, int j, int k)
    {
    }

    void setOnAccountEditResultListener(OnAccountEditResultListener onaccounteditresultlistener)
    {
        onAccountEditResultListener = onaccounteditresultlistener;
    }
}
