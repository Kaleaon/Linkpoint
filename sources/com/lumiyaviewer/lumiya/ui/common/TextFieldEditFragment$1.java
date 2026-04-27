// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.common.base.Objects;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            TextFieldEditFragment

class val.fieldEditText
    implements TextWatcher
{

    final TextFieldEditFragment this$0;
    final TextView val$fieldEditText;

    public void afterTextChanged(Editable editable)
    {
        boolean flag = Objects.equal(val$fieldEditText.getText().toString(), TextFieldEditFragment._2D_get1(TextFieldEditFragment.this)) ^ true;
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

    ()
    {
        this$0 = final_textfieldeditfragment;
        val$fieldEditText = TextView.this;
        super();
    }
}
