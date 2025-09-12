// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.grids;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.grids:
//            GridList

public class GridEditDialog extends AppCompatDialog
    implements android.view.View.OnClickListener
{
    public static interface OnGridEditResultListener
    {

        public abstract void onGridAdded(GridList.GridInfo gridinfo, boolean flag);

        public abstract void onGridDeleted(GridList.GridInfo gridinfo);

        public abstract void onGridEditCancelled();
    }


    private GridList.GridInfo editGrid;
    private GridList gridList;
    private OnGridEditResultListener onGridEditResultListener;

    public GridEditDialog(Context context, GridList gridlist, GridList.GridInfo gridinfo)
    {
        super(context);
        onGridEditResultListener = null;
        gridList = null;
        editGrid = null;
        gridList = gridlist;
        editGrid = gridinfo;
    }

    private void prepare()
    {
        if (editGrid != null)
        {
            ((TextView)findViewById(0x7f10015c)).setText(editGrid.getGridName());
            ((TextView)findViewById(0x7f10015e)).setText(editGrid.getLoginURL());
            ((Button)findViewById(0x7f1000b6)).setText(0x7f0902df);
            findViewById(0x7f10015f).setVisibility(0);
            setTitle(0x7f090107);
        } else
        {
            ((TextView)findViewById(0x7f10015c)).setText("");
            ((TextView)findViewById(0x7f10015e)).setText("");
            ((Button)findViewById(0x7f1000b6)).setText(0x7f09003d);
            findViewById(0x7f10015f).setVisibility(8);
            setTitle(0x7f0901d8);
        }
        findViewById(0x7f10015c).requestFocus();
    }

    public void onClick(View view)
    {
        boolean flag = false;
        view.getId();
        JVM INSTR lookupswitch 3: default 40
    //                   2131755190: 41
    //                   2131755191: 274
    //                   2131755359: 242;
           goto _L1 _L2 _L3 _L4
_L1:
        return;
_L2:
        String s = ((TextView)findViewById(0x7f10015c)).getText().toString();
        String s1 = ((TextView)findViewById(0x7f10015e)).getText().toString();
        if (s.equals(""))
        {
            Toast.makeText(getContext(), getContext().getString(0x7f090134), 0).show();
            return;
        }
        if (s1.equals(""))
        {
            Toast.makeText(getContext(), getContext().getString(0x7f090139), 0).show();
            return;
        }
        view = gridList.getGridByName(s);
        if (view != null && view != editGrid)
        {
            Toast.makeText(getContext(), getContext().getString(0x7f090133), 0).show();
            return;
        }
        dismiss();
        if (onGridEditResultListener != null)
        {
            view = editGrid;
            if (view == null)
            {
                view = new GridList.GridInfo(s, s1, false, UUID.randomUUID());
                flag = true;
            } else
            {
                view.setGridName(s);
                view.setLoginURL(s1);
            }
            onGridEditResultListener.onGridAdded(view, flag);
            return;
        }
        continue; /* Loop/switch isn't completed */
_L4:
        dismiss();
        if (onGridEditResultListener != null && editGrid != null)
        {
            onGridEditResultListener.onGridDeleted(editGrid);
            return;
        }
        if (true) goto _L1; else goto _L3
_L3:
        dismiss();
        if (onGridEditResultListener != null)
        {
            onGridEditResultListener.onGridEditCancelled();
            return;
        }
        if (true) goto _L1; else goto _L5
_L5:
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setTitle(0x7f0901d8);
        setContentView(0x7f04003f);
        findViewById(0x7f1000b6).setOnClickListener(this);
        findViewById(0x7f10015f).setOnClickListener(this);
        findViewById(0x7f1000b7).setOnClickListener(this);
        prepare();
    }

    public void setOnGridEditResultListener(OnGridEditResultListener ongrideditresultlistener)
    {
        onGridEditResultListener = ongrideditresultlistener;
    }
}
