package com.lumiyaviewer.lumiya.ui.grids;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity;
import com.lumiyaviewer.lumiya.ui.grids.GridEditDialog;
import com.lumiyaviewer.lumiya.ui.grids.GridList;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ManageGridsActivity extends ThemedActivity implements GridEditDialog.OnGridEditResultListener, AdapterView.OnItemClickListener {
    private GridListAdapter adapter;
    @BindView(R.id.gridList)
    ListView gridListView;
    private GridList gridList = null;
    private List<GridList.GridInfo> displayList = new ArrayList();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class GridListAdapter extends ArrayAdapter<GridList.GridInfo> {
        GridListAdapter(Context context, List<GridList.GridInfo> list) {
            super(context, (int) R.layout.grid_list_item, list);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater from = LayoutInflater.from(getContext());
            if (view == null) {
                view = from.inflate(R.layout.grid_list_item, viewGroup, false);
            }
            TextView textView = (TextView) view.findViewById(R.id.gridNameTextView);
            TextView textView2 = (TextView) view.findViewById(R.id.gridURLTextView);
            GridList.GridInfo item = getItem(i);
            if (item != null) {
                GridList.GridInfo gridInfo = item;
                textView.setText(gridInfo.getGridName());
                textView2.setText(gridInfo.getLoginURL());
                view.findViewById(R.id.gridLockedIcon).setVisibility(gridInfo.isPredefinedGrid() ? 0 : 4);
            }
            return view;
        }

        void updateList() {
            super.notifyDataSetChanged();
        }
    }

    private void deleteGrid(GridList.GridInfo gridInfo) {
        this.gridList.deleteGrid(gridInfo);
        this.gridList.getGridList(this.displayList);
        this.adapter.updateList();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_grids_ManageGridsActivity_6020  reason: not valid java name */
    public /* synthetic */ void m592x6c23f39d(GridList.GridInfo gridInfo, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        deleteGrid(gridInfo);
    }

    @OnClick({R.id.add_new_grid_button})
    public void onAddNewGridButton() {
        GridEditDialog gridEditDialog = new GridEditDialog(this, this.gridList, null);
        gridEditDialog.setOnGridEditResultListener(this);
        gridEditDialog.show();
    }

    @Override // android.app.Activity
    public boolean onContextItemSelected(MenuItem menuItem) {
        GridList.GridInfo item = this.adapter.getItem(((AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo()).position);
        if (item != null) {
            final GridList.GridInfo gridInfo = item;
            switch (menuItem.getItemId()) {
                case R.id.item_grid_edit /* 2131755774 */:
                    GridEditDialog gridEditDialog = new GridEditDialog(this, this.gridList, gridInfo);
                    gridEditDialog.setOnGridEditResultListener(this);
                    gridEditDialog.show();
                    return true;
                case R.id.item_grid_delete /* 2131755775 */:
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getString(R.string.grid_delete_confirm_title)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.grids.-$Lambda$mB53054QosfH2NBejFMOD8VFF4s.1
                        private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                            ((ManageGridsActivity) this).m592x6c23f39d((GridList.GridInfo) gridInfo, dialogInterface, i);
                        }

                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            $m$0(dialogInterface, i);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.grids.-$Lambda$mB53054QosfH2NBejFMOD8VFF4s
                        private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }

                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            $m$0(dialogInterface, i);
                        }
                    });
                    builder.create().show();
                    return true;
            }
        }
        return super.onContextItemSelected(menuItem);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ThemedActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.manage_grids);
        ButterKnife.bind(this);
        this.gridList = new GridList(this);
        this.gridList.getGridList(this.displayList);
        this.adapter = new GridListAdapter(this, this.displayList);
        this.gridListView.setAdapter((ListAdapter) this.adapter);
        this.gridListView.setOnItemClickListener(this);
        registerForContextMenu(this.gridListView);
    }

    @Override // android.app.Activity, android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        GridList.GridInfo item = this.adapter.getItem(((AdapterView.AdapterContextMenuInfo) contextMenuInfo).position);
        if (item == null || item.isPredefinedGrid()) {
            return;
        }
        getMenuInflater().inflate(R.menu.grid_list_context_menu, contextMenu);
    }

    @Override // com.lumiyaviewer.lumiya.ui.grids.GridEditDialog.OnGridEditResultListener
    public void onGridAdded(GridList.GridInfo gridInfo, boolean z) {
        if (z) {
            this.gridList.addNewGrid(gridInfo);
        } else {
            this.gridList.savePreferences();
        }
        this.gridList.getGridList(this.displayList);
        this.adapter.updateList();
        ListView listView = (ListView) findViewById(R.id.gridList);
        if (listView.getAdapter().getCount() > 0) {
            listView.setSelection(listView.getAdapter().getCount() - 1);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.grids.GridEditDialog.OnGridEditResultListener
    public void onGridDeleted(GridList.GridInfo gridInfo) {
        deleteGrid(gridInfo);
    }

    @Override // com.lumiyaviewer.lumiya.ui.grids.GridEditDialog.OnGridEditResultListener
    public void onGridEditCancelled() {
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        GridList.GridInfo item;
        if (this.adapter == null || (item = this.adapter.getItem(i)) == null || item.isPredefinedGrid()) {
            return;
        }
        GridEditDialog gridEditDialog = new GridEditDialog(this, this.gridList, item);
        gridEditDialog.setOnGridEditResultListener(this);
        gridEditDialog.show();
    }
}
