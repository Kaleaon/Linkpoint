// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.grids;

import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.Menu;
import android.view.ContextMenu$ContextMenuInfo;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ListAdapter;
import android.app.Activity;
import butterknife.ButterKnife;
import android.os.Bundle;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import android.widget.AdapterView$AdapterContextMenuInfo;
import android.view.MenuItem;
import butterknife.OnClick;
import android.content.Context;
import android.content.DialogInterface;
import java.util.ArrayList;
import butterknife.BindView;
import android.widget.ListView;
import java.util.List;
import android.widget.AdapterView$OnItemClickListener;
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity;

public class ManageGridsActivity extends ThemedActivity implements OnGridEditResultListener, AdapterView$OnItemClickListener
{
    private GridListAdapter adapter;
    private List<GridList.GridInfo> displayList;
    private GridList gridList;
    @BindView(2131755483)
    ListView gridListView;
    
    public ManageGridsActivity() {
        this.gridList = null;
        this.displayList = new ArrayList<GridList.GridInfo>();
    }
    
    private void deleteGrid(final GridList.GridInfo gridInfo) {
        this.gridList.deleteGrid(gridInfo);
        this.gridList.getGridList(this.displayList);
        this.adapter.updateList();
    }
    
    @OnClick({ 2131755484 })
    public void onAddNewGridButton() {
        final GridEditDialog gridEditDialog = new GridEditDialog((Context)this, this.gridList, null);
        gridEditDialog.setOnGridEditResultListener((GridEditDialog.OnGridEditResultListener)this);
        gridEditDialog.show();
    }
    
    public boolean onContextItemSelected(final MenuItem menuItem) {
        final Object item = this.adapter.getItem(((AdapterView$AdapterContextMenuInfo)menuItem.getMenuInfo()).position);
        if (item != null) {
            final GridList.GridInfo gridInfo = (GridList.GridInfo)item;
            switch (menuItem.getItemId()) {
                case 2131755774: {
                    final GridEditDialog gridEditDialog = new GridEditDialog((Context)this, this.gridList, gridInfo);
                    gridEditDialog.setOnGridEditResultListener((GridEditDialog.OnGridEditResultListener)this);
                    gridEditDialog.show();
                    return true;
                }
                case 2131755775: {
                    final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder((Context)this);
                    alertDialog$Builder.setMessage((CharSequence)this.getString(2131296560)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$mB53054QosfH2NBejFMOD8VFF4s$1(this, gridInfo)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$mB53054QosfH2NBejFMOD8VFF4s());
                    alertDialog$Builder.create().show();
                    return true;
                }
            }
        }
        return super.onContextItemSelected(menuItem);
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(2130968669);
        ButterKnife.bind(this);
        (this.gridList = new GridList((Context)this)).getGridList(this.displayList);
        this.adapter = new GridListAdapter((Context)this, this.displayList);
        this.gridListView.setAdapter((ListAdapter)this.adapter);
        this.gridListView.setOnItemClickListener((AdapterView$OnItemClickListener)this);
        this.registerForContextMenu((View)this.gridListView);
    }
    
    public void onCreateContextMenu(final ContextMenu contextMenu, final View view, final ContextMenu$ContextMenuInfo contextMenu$ContextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenu$ContextMenuInfo);
        final Object item = this.adapter.getItem(((AdapterView$AdapterContextMenuInfo)contextMenu$ContextMenuInfo).position);
        if (item != null && !((GridList.GridInfo)item).isPredefinedGrid()) {
            this.getMenuInflater().inflate(2131886084, (Menu)contextMenu);
        }
    }
    
    @Override
    public void onGridAdded(final GridList.GridInfo gridInfo, final boolean b) {
        if (b) {
            this.gridList.addNewGrid(gridInfo);
        }
        else {
            this.gridList.savePreferences();
        }
        this.gridList.getGridList(this.displayList);
        this.adapter.updateList();
        final ListView listView = this.findViewById(2131755483);
        if (listView.getAdapter().getCount() > 0) {
            listView.setSelection(listView.getAdapter().getCount() - 1);
        }
    }
    
    @Override
    public void onGridDeleted(final GridList.GridInfo gridInfo) {
        this.deleteGrid(gridInfo);
    }
    
    @Override
    public void onGridEditCancelled() {
    }
    
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
        if (this.adapter != null) {
            final GridList.GridInfo gridInfo = (GridList.GridInfo)this.adapter.getItem(n);
            if (gridInfo != null && !gridInfo.isPredefinedGrid()) {
                final GridEditDialog gridEditDialog = new GridEditDialog((Context)this, this.gridList, gridInfo);
                gridEditDialog.setOnGridEditResultListener((GridEditDialog.OnGridEditResultListener)this);
                gridEditDialog.show();
            }
        }
    }
    
    private static class GridListAdapter extends ArrayAdapter<GridList.GridInfo>
    {
        GridListAdapter(final Context context, final List<GridList.GridInfo> list) {
            super(context, 2130968640, (List)list);
        }
        
        public View getView(int visibility, View viewById, final ViewGroup viewGroup) {
            final LayoutInflater from = LayoutInflater.from(this.getContext());
            View inflate = viewById;
            if (viewById == null) {
                inflate = from.inflate(2130968640, viewGroup, false);
            }
            final TextView textView = (TextView)inflate.findViewById(2131755194);
            final TextView textView2 = (TextView)inflate.findViewById(2131755361);
            final Object item = this.getItem(visibility);
            if (item != null) {
                final GridList.GridInfo gridInfo = (GridList.GridInfo)item;
                textView.setText((CharSequence)gridInfo.getGridName());
                textView2.setText((CharSequence)gridInfo.getLoginURL());
                viewById = inflate.findViewById(2131755362);
                if (gridInfo.isPredefinedGrid()) {
                    visibility = 0;
                }
                else {
                    visibility = 4;
                }
                viewById.setVisibility(visibility);
            }
            return inflate;
        }
        
        void updateList() {
            super.notifyDataSetChanged();
        }
    }
}
