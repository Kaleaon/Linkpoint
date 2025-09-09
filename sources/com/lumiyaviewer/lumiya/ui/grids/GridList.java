package com.lumiyaviewer.lumiya.ui.grids;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import com.lumiyaviewer.lumiya.R;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GridList {
    private Context context;
    private ArrayList<GridInfo> customGrids;
    private ArrayList<GridInfo> predefGrids = new ArrayList<>();

    public static class GridArrayAdapter extends ArrayAdapter<GridInfo> {
        public GridArrayAdapter(Context context, List<GridInfo> list) {
            super(context, 17367048, list);
            setDropDownViewResource(17367049);
        }
    }

    public static class GridInfo {
        private String GridName;
        private UUID GridUUID;
        private String LoginURL;
        private boolean predefinedGrid;

        public GridInfo(SharedPreferences sharedPreferences, String str) {
            this.GridName = sharedPreferences.getString(str + "_grid_name", "");
            this.LoginURL = sharedPreferences.getString(str + "_login_url", "");
            this.predefinedGrid = false;
            this.GridUUID = UUID.fromString(sharedPreferences.getString(str + "_grid", ""));
        }

        public GridInfo(String str, String str2, boolean z, UUID uuid) {
            this.GridName = str;
            this.LoginURL = str2;
            this.predefinedGrid = z;
            this.GridUUID = uuid;
        }

        public String getGridName() {
            return this.GridName;
        }

        public UUID getGridUUID() {
            return this.GridUUID;
        }

        public String getLoginURL() {
            return this.LoginURL;
        }

        public boolean isLindenGrid() {
            return this.GridUUID.equals(UUID.fromString("f14c5be7-0849-402c-946a-c80a52e9eccf"));
        }

        public boolean isPredefinedGrid() {
            return this.predefinedGrid;
        }

        public void saveToPreferences(SharedPreferences.Editor editor, String str) {
            editor.putString(str + "_grid_name", this.GridName);
            editor.putString(str + "_login_url", this.LoginURL);
            editor.putString(str + "_grid", this.GridUUID.toString());
        }

        public void setGridName(String str) {
            this.GridName = str;
        }

        public void setLoginURL(String str) {
            this.LoginURL = str;
        }

        public String toString() {
            return this.GridName;
        }
    }

    public GridList(Context context2) {
        this.context = context2;
        for (String split : context2.getResources().getStringArray(R.array.grids)) {
            String[] split2 = split.split(";");
            this.predefGrids.add(new GridInfo(split2[0], split2[1], true, UUID.fromString(split2[2])));
        }
        this.customGrids = new ArrayList<>();
        loadGrids();
    }

    public void addNewGrid(GridInfo gridInfo) {
        this.customGrids.add(gridInfo);
        savePreferences();
    }

    public void deleteGrid(GridInfo gridInfo) {
        this.customGrids.remove(gridInfo);
        savePreferences();
    }

    public GridInfo getDefaultGrid() {
        return this.predefGrids.get(0);
    }

    public GridInfo getGridByName(String str) {
        for (GridInfo gridInfo : this.predefGrids) {
            if (gridInfo.getGridName().equals(str)) {
                return gridInfo;
            }
        }
        for (GridInfo gridInfo2 : this.customGrids) {
            if (gridInfo2.getGridName().equals(str)) {
                return gridInfo2;
            }
        }
        return null;
    }

    public GridInfo getGridByUUID(UUID uuid) {
        for (GridInfo gridInfo : this.predefGrids) {
            if (gridInfo.getGridUUID().equals(uuid)) {
                return gridInfo;
            }
        }
        for (GridInfo gridInfo2 : this.customGrids) {
            if (gridInfo2.getGridUUID().equals(uuid)) {
                return gridInfo2;
            }
        }
        return null;
    }

    public int getGridIndex(UUID uuid) {
        int i = 0;
        for (GridInfo gridUUID : this.predefGrids) {
            if (gridUUID.getGridUUID().equals(uuid)) {
                return i;
            }
            i++;
        }
        for (GridInfo gridUUID2 : this.customGrids) {
            if (gridUUID2.getGridUUID().equals(uuid)) {
                return i;
            }
            i++;
        }
        return 0;
    }

    public List<GridInfo> getGridList(List<GridInfo> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.clear();
        list.addAll(this.predefGrids);
        list.addAll(this.customGrids);
        return list;
    }

    public List<GridInfo> getGridList(List<GridInfo> list, boolean z) {
        List<GridInfo> gridList = getGridList(list);
        if (z) {
            gridList.add(new GridInfo("Add another grid", (String) null, false, (UUID) null));
        }
        return gridList;
    }

    public void loadGrids() {
        this.customGrids.clear();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext());
        int i = defaultSharedPreferences.getInt("custom_grid_1_count", 0);
        for (int i2 = 0; i2 < i; i2++) {
            this.customGrids.add(new GridInfo(defaultSharedPreferences, "custom_grid_1_" + i2));
        }
    }

    public void savePreferences() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext()).edit();
        edit.putInt("custom_grid_1_count", this.customGrids.size());
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < this.customGrids.size()) {
                this.customGrids.get(i2).saveToPreferences(edit, "custom_grid_1_" + i2);
                i = i2 + 1;
            } else {
                edit.commit();
                return;
            }
        }
    }
}
