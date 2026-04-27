// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            ChatterFragment, BackButtonHandler, DetailsActivity

public abstract class TextFieldEditFragment extends ChatterFragment
    implements BackButtonHandler
{

    private boolean hasChanged;
    private String originalText;
    private MenuItem undoMenuItem;

    static boolean _2D_get0(TextFieldEditFragment textfieldeditfragment)
    {
        return textfieldeditfragment.hasChanged;
    }

    static String _2D_get1(TextFieldEditFragment textfieldeditfragment)
    {
        return textfieldeditfragment.originalText;
    }

    static MenuItem _2D_get2(TextFieldEditFragment textfieldeditfragment)
    {
        return textfieldeditfragment.undoMenuItem;
    }

    static boolean _2D_set0(TextFieldEditFragment textfieldeditfragment, boolean flag)
    {
        textfieldeditfragment.hasChanged = flag;
        return flag;
    }

    public TextFieldEditFragment()
    {
        originalText = "";
        hasChanged = false;
    }

    private void closeFragment()
    {
        android.support.v4.app.FragmentActivity fragmentactivity = getActivity();
        if (fragmentactivity instanceof DetailsActivity)
        {
            ((DetailsActivity)fragmentactivity).closeDetailsFragment(this);
        }
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_TextFieldEditFragment_2137(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    protected abstract String getFieldHint(Context context);

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_TextFieldEditFragment_1854(View view, DialogInterface dialoginterface, int i)
    {
        ((TextView)view.findViewById(0x7f1002cf)).setText(originalText);
        dialoginterface.dismiss();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_TextFieldEditFragment_4960(String s, DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        if (userManager != null && chatterID != null)
        {
            dialoginterface = userManager.getActiveAgentCircuit();
            if (dialoginterface != null)
            {
                saveEditedText(dialoginterface, chatterID, s);
            }
        }
        closeFragment();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_TextFieldEditFragment_5518(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
        closeFragment();
    }

    public boolean onBackButtonPressed()
    {
        Object obj = getView();
        if (obj != null)
        {
            obj = ((TextView)((View) (obj)).findViewById(0x7f1002cf)).getText().toString();
            if (!Objects.equal(obj, originalText))
            {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                builder.setMessage(getString(0x7f0902e0)).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.DtZUcoBgRyVu_2D_s24uOe08hwsuHo._cls2(this, obj)).setNegativeButton("No", new _2D_.Lambda.DtZUcoBgRyVu_2D_s24uOe08hwsuHo._cls1(this));
                builder.create().show();
                return true;
            }
        }
        return false;
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater)
    {
        super.onCreateOptionsMenu(menu, menuinflater);
        menuinflater.inflate(0x7f120025, menu);
        undoMenuItem = menu.findItem(0x7f100302);
        undoMenuItem.setVisible(hasChanged);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, final Bundle fieldEditText)
    {
        viewgroup = layoutinflater.inflate(0x7f0400ba, viewgroup, false);
        fieldEditText = (TextView)viewgroup.findViewById(0x7f1002cf);
        fieldEditText.setHint(getFieldHint(layoutinflater.getContext()));
        fieldEditText.addTextChangedListener(new TextWatcher() {

            final TextFieldEditFragment this$0;
            final TextView val$fieldEditText;

            public void afterTextChanged(Editable editable)
            {
                boolean flag = Objects.equal(fieldEditText.getText().toString(), TextFieldEditFragment._2D_get1(TextFieldEditFragment.this)) ^ true;
                if (flag != TextFieldEditFragment._2D_get0(TextFieldEditFragment.this))
                {
                    TextFieldEditFragment._2D_set0(TextFieldEditFragment.this, flag);
                    if (TextFieldEditFragment._2D_get2(TextFieldEditFragment.this) != null)
                    {
                        TextFieldEditFragment._2D_get2(TextFieldEditFragment.this).setVisible(TextFieldEditFragment._2D_get0(TextFieldEditFragment.this));
                    }
                }
            }

            public void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
            {
            }

            public void onTextChanged(CharSequence charsequence, int i, int j, int k)
            {
            }

            
            {
                this$0 = TextFieldEditFragment.this;
                fieldEditText = textview;
                super();
            }
        });
        return viewgroup;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        switch (menuitem.getItemId())
        {
        default:
            return super.onOptionsItemSelected(menuitem);

        case 2131755778: 
            menuitem = getView();
            break;
        }
        if (menuitem != null && !Objects.equal(((TextView)menuitem.findViewById(0x7f1002cf)).getText().toString(), originalText))
        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            builder.setMessage(getString(0x7f090100)).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.DtZUcoBgRyVu_2D_s24uOe08hwsuHo._cls3(this, menuitem)).setNegativeButton("No", new _2D_.Lambda.DtZUcoBgRyVu_2D_s24uOe08hwsuHo());
            builder.create().show();
        }
        return true;
    }

    protected abstract void saveEditedText(SLAgentCircuit slagentcircuit, ChatterID chatterid, String s);

    protected void setOriginalText(String s)
    {
        originalText = s;
        View view = getView();
        if (view != null)
        {
            ((TextView)view.findViewById(0x7f1002cf)).setText(s);
        }
    }
}
