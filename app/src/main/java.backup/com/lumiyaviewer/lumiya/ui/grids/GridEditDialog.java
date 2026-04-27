package com.lumiyaviewer.lumiya.ui.grids;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.grids.GridList;
import java.util.UUID;

public class GridEditDialog extends AppCompatDialog implements View.OnClickListener {
    private GridList.GridInfo editGrid = null;
    private GridList gridList = null;
    private OnGridEditResultListener onGridEditResultListener = null;

    public interface OnGridEditResultListener {
        void onGridAdded(GridList.GridInfo gridInfo, boolean z);

        void onGridDeleted(GridList.GridInfo gridInfo);

        void onGridEditCancelled();
    }

    public GridEditDialog(Context context, GridList gridList2, GridList.GridInfo gridInfo) {
        super(context);
        this.gridList = gridList2;
        this.editGrid = gridInfo;
    }

    private void prepare() {
        if (this.editGrid != null) {
            ((TextView) findViewById(R.id.gridNameText)).setText(this.editGrid.getGridName());
            ((TextView) findViewById(R.id.gridLoginURIText)).setText(this.editGrid.getLoginURL());
            ((Button) findViewById(R.id.okButton)).setText(R.string.save_changes);
            findViewById(R.id.deleteButton).setVisibility(0);
            setTitle((int) R.string.edit_grid_dialog_title);
        } else {
            ((TextView) findViewById(R.id.gridNameText)).setText("");
            ((TextView) findViewById(R.id.gridLoginURIText)).setText("");
            ((Button) findViewById(R.id.okButton)).setText(R.string.add_new_grid);
            findViewById(R.id.deleteButton).setVisibility(8);
            setTitle((int) R.string.new_grid_dialog_title);
        }
        findViewById(R.id.gridNameText).requestFocus();
    }

    public void onClick(View view) {
        boolean z = false;
        switch (view.getId()) {
            case R.id.okButton:
                String charSequence = ((TextView) findViewById(R.id.gridNameText)).getText().toString();
                String charSequence2 = ((TextView) findViewById(R.id.gridLoginURIText)).getText().toString();
                if (charSequence.equals("")) {
                    Toast.makeText(getContext(), getContext().getString(R.string.grid_name_empty_error), 0).show();
                    return;
                } else if (charSequence2.equals("")) {
                    Toast.makeText(getContext(), getContext().getString(R.string.grid_uri_empty_error), 0).show();
                    return;
                } else {
                    GridList.GridInfo gridByName = this.gridList.getGridByName(charSequence);
                    if (gridByName == null || gridByName == this.editGrid) {
                        dismiss();
                        if (this.onGridEditResultListener != null) {
                            GridList.GridInfo gridInfo = this.editGrid;
                            if (gridInfo == null) {
                                gridInfo = new GridList.GridInfo(charSequence, charSequence2, false, UUID.randomUUID());
                                z = true;
                            } else {
                                gridInfo.setGridName(charSequence);
                                gridInfo.setLoginURL(charSequence2);
                            }
                            this.onGridEditResultListener.onGridAdded(gridInfo, z);
                            return;
                        }
                        return;
                    }
                    Toast.makeText(getContext(), getContext().getString(R.string.grid_exists_error), 0).show();
                    return;
                }
            case R.id.cancelButton:
                dismiss();
                if (this.onGridEditResultListener != null) {
                    this.onGridEditResultListener.onGridEditCancelled();
                    return;
                }
                return;
            case R.id.deleteButton:
                dismiss();
                if (this.onGridEditResultListener != null && this.editGrid != null) {
                    this.onGridEditResultListener.onGridDeleted(this.editGrid);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle((int) R.string.new_grid_dialog_title);
        setContentView((int) R.layout.grid_edit_dialog);
        findViewById(R.id.okButton).setOnClickListener(this);
        findViewById(R.id.deleteButton).setOnClickListener(this);
        findViewById(R.id.cancelButton).setOnClickListener(this);
        prepare();
    }

    public void setOnGridEditResultListener(OnGridEditResultListener onGridEditResultListener2) {
        this.onGridEditResultListener = onGridEditResultListener2;
    }
}
