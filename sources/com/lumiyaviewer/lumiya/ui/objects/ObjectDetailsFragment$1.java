// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objects;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.objects:
//            ObjectDetailsFragment

class val.objectPayButton
    implements TextWatcher
{

    final ObjectDetailsFragment this$0;
    final Button val$objectPayButton;

    public void afterTextChanged(Editable editable)
    {
        try
        {
            Integer.parseInt(editable.toString());
            val$objectPayButton.setEnabled(true);
            return;
        }
        // Misplaced declaration of an exception variable
        catch (Editable editable)
        {
            val$objectPayButton.setEnabled(false);
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
        this$0 = final_objectdetailsfragment;
        val$objectPayButton = Button.this;
        super();
    }
}
