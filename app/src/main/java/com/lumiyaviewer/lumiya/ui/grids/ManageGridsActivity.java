package com.lumiyaviewer.lumiya.ui.grids;

import android.app.Activity;
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

public class ManageGridsActivity extends ThemedActivity implements GridEditDialog.OnGridEditResultListener, AdapterView.OnItemClickListener {
    private GridListAdapter adapter;
    private List<GridList.GridInfo> displayList = new ArrayList();
    private GridList gridList = null;
    @BindView(2131755483)
    ListView gridListView;

    private static class GridListAdapter extends ArrayAdapter<GridList.GridInfo> {
        GridListAdapter(Context context, List<GridList.GridInfo> list) {
            super(context, R.layout.grid_list_item, list);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater from = LayoutInflater.from(getContext());
            if (view == null) {
                view = from.inflate(R.layout.grid_list_item, viewGroup, false);
            }
            TextView textView = (TextView) view.findViewById(R.id.gridNameTextView);
            TextView textView2 = (TextView) view.findViewById(R.id.gridURLTextView);
            Object item = getItem(i);
            if (item != null) {
                GridList.GridInfo gridInfo = (GridList.GridInfo) item;
                textView.setText(gridInfo.getGridName());
                textView2.setText(gridInfo.getLoginURL());
                view.findViewById(R.id.gridLockedIcon).setVisibility(gridInfo.isPredefinedGrid() ? 0 : 4);
            }
            return view;
        }

        /* access modifiers changed from: package-private */
        public void updateList() {
            super.notifyDataSetChanged();
        }
    }

    private void deleteGrid(GridList.GridInfo gridInfo) {
        this.gridList.deleteGrid(gridInfo);
        this.gridList.getGridList(this.displayList);
        this.adapter.updateList();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_grids_ManageGridsActivity_6020  reason: not valid java name */
    public /* synthetic */ void m581lambda$com_lumiyaviewer_lumiya_ui_grids_ManageGridsActivity_6020(GridList.GridInfo gridInfo, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        deleteGrid(gridInfo);
    }

    @OnClick({2131755484})
    public void onAddNewGridButton() {
        GridEditDialog gridEditDialog = new GridEditDialog(this, this.gridList, (GridList.GridInfo) null);
        gridEditDialog.setOnGridEditResultListener(this);
        gridEditDialog.show();
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        Object item = this.adapter.getItem(((AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo()).position);
        if (item != null) {
            GridList.GridInfo gridInfo = (GridList.GridInfo) item;
            switch (menuItem.getItemId()) {
                case R.id.item_grid_edit:
                    GridEditDialog gridEditDialog = new GridEditDialog(this, this.gridList, gridInfo);
                    gridEditDialog.setOnGridEditResultListener(this);
                    gridEditDialog.show();
                    return true;
                case R.id.item_grid_delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getString(R.string.grid_delete_confirm_title)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener(this, gridInfo) {

                        /* renamed from: -$f0 */
                        private final /* synthetic */ Object f380$f0;

                        /* renamed from: -$f1 */
                        private final /* synthetic */ Object f381$f1;

                        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.grids.-$Lambda$mB53054QosfH2NBejFMOD8VFF4s.1.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
                        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.grids.-$Lambda$mB53054QosfH2NBejFMOD8VFF4s.1.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
                        	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
                        	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                        	at jadx.core.codegen.InsnGen.addArgDot(InsnGen.java:91)
                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:697)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeSwitch(RegionGen.java:298)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:64)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                        
*/

                    }).setNegativeButton("No", new $Lambda$mB53054QosfH2NBejFMOD8VFF4s());
                    builder.create().show();
                    return true;
            }
        }
        return super.onContextItemSelected(menuItem);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.manage_grids);
        ButterKnife.bind((Activity) this);
        this.gridList = new GridList(this);
        this.gridList.getGridList(this.displayList);
        this.adapter = new GridListAdapter(this, this.displayList);
        this.gridListView.setAdapter(this.adapter);
        this.gridListView.setOnItemClickListener(this);
        registerForContextMenu(this.gridListView);
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        Object item = this.adapter.getItem(((AdapterView.AdapterContextMenuInfo) contextMenuInfo).position);
        if (item != null && !((GridList.GridInfo) item).isPredefinedGrid()) {
            getMenuInflater().inflate(R.menu.grid_list_context_menu, contextMenu);
        }
    }

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

    public void onGridDeleted(GridList.GridInfo gridInfo) {
        deleteGrid(gridInfo);
    }

    public void onGridEditCancelled() {
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        GridList.GridInfo gridInfo;
        if (this.adapter != null && (gridInfo = (GridList.GridInfo) this.adapter.getItem(i)) != null && !gridInfo.isPredefinedGrid()) {
            GridEditDialog gridEditDialog = new GridEditDialog(this, this.gridList, gridInfo);
            gridEditDialog.setOnGridEditResultListener(this);
            gridEditDialog.show();
        }
    }
}
