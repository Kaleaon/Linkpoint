// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.grids;

import android.os.Bundle;
import java.util.UUID;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.view.View$OnClickListener;
import android.support.v7.app.AppCompatDialog;

public class GridEditDialog extends AppCompatDialog implements View$OnClickListener
{
    private GridList.GridInfo editGrid;
    private GridList gridList;
    private OnGridEditResultListener onGridEditResultListener;
    
    public GridEditDialog(final Context context, final GridList gridList, final GridList.GridInfo editGrid) {
        super(context);
        this.onGridEditResultListener = null;
        this.gridList = null;
        this.editGrid = null;
        this.gridList = gridList;
        this.editGrid = editGrid;
    }
    
    private void prepare() {
        if (this.editGrid != null) {
            this.findViewById(2131755356).setText((CharSequence)this.editGrid.getGridName());
            this.findViewById(2131755358).setText((CharSequence)this.editGrid.getLoginURL());
            this.findViewById(2131755190).setText(2131296991);
            this.findViewById(2131755359).setVisibility(0);
            this.setTitle(2131296519);
        }
        else {
            this.findViewById(2131755356).setText((CharSequence)"");
            this.findViewById(2131755358).setText((CharSequence)"");
            this.findViewById(2131755190).setText(2131296317);
            this.findViewById(2131755359).setVisibility(8);
            this.setTitle(2131296728);
        }
        this.findViewById(2131755356).requestFocus();
    }
    
    public void onClick(final View view) {
        boolean b = false;
        switch (view.getId()) {
            case 2131755190: {
                final String string = this.findViewById(2131755356).getText().toString();
                final String string2 = this.findViewById(2131755358).getText().toString();
                if (string.equals("")) {
                    Toast.makeText(this.getContext(), (CharSequence)this.getContext().getString(2131296564), 0).show();
                    break;
                }
                if (string2.equals("")) {
                    Toast.makeText(this.getContext(), (CharSequence)this.getContext().getString(2131296569), 0).show();
                    break;
                }
                final GridList.GridInfo gridByName = this.gridList.getGridByName(string);
                if (gridByName != null && gridByName != this.editGrid) {
                    Toast.makeText(this.getContext(), (CharSequence)this.getContext().getString(2131296563), 0).show();
                    break;
                }
                this.dismiss();
                if (this.onGridEditResultListener != null) {
                    Object editGrid = this.editGrid;
                    if (editGrid == null) {
                        editGrid = new GridList.GridInfo(string, string2, false, UUID.randomUUID());
                        b = true;
                    }
                    else {
                        ((GridList.GridInfo)editGrid).setGridName(string);
                        ((GridList.GridInfo)editGrid).setLoginURL(string2);
                    }
                    this.onGridEditResultListener.onGridAdded((GridList.GridInfo)editGrid, b);
                    break;
                }
                break;
            }
            case 2131755359: {
                this.dismiss();
                if (this.onGridEditResultListener != null && this.editGrid != null) {
                    this.onGridEditResultListener.onGridDeleted(this.editGrid);
                    break;
                }
                break;
            }
            case 2131755191: {
                this.dismiss();
                if (this.onGridEditResultListener != null) {
                    this.onGridEditResultListener.onGridEditCancelled();
                    break;
                }
                break;
            }
        }
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setTitle(2131296728);
        this.setContentView(2130968639);
        this.findViewById(2131755190).setOnClickListener((View$OnClickListener)this);
        this.findViewById(2131755359).setOnClickListener((View$OnClickListener)this);
        this.findViewById(2131755191).setOnClickListener((View$OnClickListener)this);
        this.prepare();
    }
    
    public void setOnGridEditResultListener(final OnGridEditResultListener onGridEditResultListener) {
        this.onGridEditResultListener = onGridEditResultListener;
    }
    
    public interface OnGridEditResultListener
    {
        void onGridAdded(final GridList.GridInfo p0, final boolean p1);
        
        void onGridDeleted(final GridList.GridInfo p0);
        
        void onGridEditCancelled();
    }
}
