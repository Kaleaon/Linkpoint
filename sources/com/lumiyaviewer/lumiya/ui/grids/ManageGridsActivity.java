// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.grids;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity;
import java.util.ArrayList;
import java.util.List;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.grids:
//            GridList, GridEditDialog

public class ManageGridsActivity extends ThemedActivity
    implements GridEditDialog.OnGridEditResultListener, android.widget.AdapterView.OnItemClickListener
{
    private static class GridListAdapter extends ArrayAdapter
    {

        public View getView(int i, View view, ViewGroup viewgroup)
        {
            Object obj = LayoutInflater.from(getContext());
            View view1 = view;
            if (view == null)
            {
                view1 = ((LayoutInflater) (obj)).inflate(0x7f040040, viewgroup, false);
            }
            view = (TextView)view1.findViewById(0x7f1000ba);
            viewgroup = (TextView)view1.findViewById(0x7f100161);
            obj = getItem(i);
            if (obj != null)
            {
                obj = (GridList.GridInfo)obj;
                view.setText(((GridList.GridInfo) (obj)).getGridName());
                viewgroup.setText(((GridList.GridInfo) (obj)).getLoginURL());
                view = view1.findViewById(0x7f100162);
                if (((GridList.GridInfo) (obj)).isPredefinedGrid())
                {
                    i = 0;
                } else
                {
                    i = 4;
                }
                view.setVisibility(i);
            }
            return view1;
        }

        void updateList()
        {
            super.notifyDataSetChanged();
        }

        GridListAdapter(Context context, List list)
        {
            super(context, 0x7f040040, list);
        }
    }


    private GridListAdapter adapter;
    private List displayList;
    private GridList gridList;
    ListView gridListView;

    public ManageGridsActivity()
    {
        gridList = null;
        displayList = new ArrayList();
    }

    private void deleteGrid(GridList.GridInfo gridinfo)
    {
        gridList.deleteGrid(gridinfo);
        gridList.getGridList(displayList);
        adapter.updateList();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_grids_ManageGridsActivity_6231(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_grids_ManageGridsActivity_6020(GridList.GridInfo gridinfo, DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        deleteGrid(gridinfo);
    }

    public void onAddNewGridButton()
    {
        GridEditDialog grideditdialog = new GridEditDialog(this, gridList, null);
        grideditdialog.setOnGridEditResultListener(this);
        grideditdialog.show();
    }

    public boolean onContextItemSelected(MenuItem menuitem)
    {
        Object obj;
        obj = (android.widget.AdapterView.AdapterContextMenuInfo)menuitem.getMenuInfo();
        obj = adapter.getItem(((android.widget.AdapterView.AdapterContextMenuInfo) (obj)).position);
        if (obj == null) goto _L2; else goto _L1
_L1:
        obj = (GridList.GridInfo)obj;
        menuitem.getItemId();
        JVM INSTR tableswitch 2131755774 2131755775: default 60
    //                   2131755774 66
    //                   2131755775 91;
           goto _L2 _L3 _L4
_L2:
        return super.onContextItemSelected(menuitem);
_L3:
        menuitem = new GridEditDialog(this, gridList, ((GridList.GridInfo) (obj)));
        menuitem.setOnGridEditResultListener(this);
        menuitem.show();
        return true;
_L4:
        menuitem = new android.app.AlertDialog.Builder(this);
        menuitem.setMessage(getString(0x7f090130)).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.mB53054QosfH2NBejFMOD8VFF4s._cls1(this, obj)).setNegativeButton("No", new _2D_.Lambda.mB53054QosfH2NBejFMOD8VFF4s());
        menuitem.create().show();
        return true;
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(0x7f04005d);
        ButterKnife.bind(this);
        gridList = new GridList(this);
        gridList.getGridList(displayList);
        adapter = new GridListAdapter(this, displayList);
        gridListView.setAdapter(adapter);
        gridListView.setOnItemClickListener(this);
        registerForContextMenu(gridListView);
    }

    public void onCreateContextMenu(ContextMenu contextmenu, View view, android.view.ContextMenu.ContextMenuInfo contextmenuinfo)
    {
        super.onCreateContextMenu(contextmenu, view, contextmenuinfo);
        view = (android.widget.AdapterView.AdapterContextMenuInfo)contextmenuinfo;
        view = ((View) (adapter.getItem(((android.widget.AdapterView.AdapterContextMenuInfo) (view)).position)));
        if (view != null && !((GridList.GridInfo)view).isPredefinedGrid())
        {
            getMenuInflater().inflate(0x7f120004, contextmenu);
        }
    }

    public void onGridAdded(GridList.GridInfo gridinfo, boolean flag)
    {
        if (flag)
        {
            gridList.addNewGrid(gridinfo);
        } else
        {
            gridList.savePreferences();
        }
        gridList.getGridList(displayList);
        adapter.updateList();
        gridinfo = (ListView)findViewById(0x7f1001db);
        if (gridinfo.getAdapter().getCount() > 0)
        {
            gridinfo.setSelection(gridinfo.getAdapter().getCount() - 1);
        }
    }

    public void onGridDeleted(GridList.GridInfo gridinfo)
    {
        deleteGrid(gridinfo);
    }

    public void onGridEditCancelled()
    {
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        if (adapter != null)
        {
            adapterview = (GridList.GridInfo)adapter.getItem(i);
            if (adapterview != null && !adapterview.isPredefinedGrid())
            {
                adapterview = new GridEditDialog(this, gridList, adapterview);
                adapterview.setOnGridEditResultListener(this);
                adapterview.show();
            }
        }
    }
}
