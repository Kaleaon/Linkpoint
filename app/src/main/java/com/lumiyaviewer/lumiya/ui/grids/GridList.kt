package com.lumiyaviewer.lumiya.ui.grids

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.widget.ArrayAdapter
import com.lumiyaviewer.lumiya.R
import java.util.ArrayList
import java.util.List
import java.util.UUID

class GridList {
    private Context context
    private ArrayList<GridInfo> customGrids
    private ArrayList<GridInfo> predefGrids = ArrayList<>()

    class GridArrayAdapter : ArrayAdapter<GridInfo> {
        GridArrayAdapter(Context context, List<GridInfo> list) {
            super(context, 17367048, list)
            setDropDownViewResource(17367049)
        }
    }

    class GridInfo {
        private String GridName
        private UUID GridUUID
        private String LoginURL
        private Boolean predefinedGrid

        GridInfo(SharedPreferences sharedPreferences, String str) {
            this.GridName = sharedPreferences.getString(str + "_grid_name", "")
            this.LoginURL = sharedPreferences.getString(str + "_login_url", "")
            this.predefinedGrid = false
            this.GridUUID = UUID.fromString(sharedPreferences.getString(str + "_grid", ""))
        }

        GridInfo(String str, String str2, Boolean z, UUID uuid) {
            this.GridName = str
            this.LoginURL = str2
            this.predefinedGrid = z
            this.GridUUID = uuid
        }

        String getGridName() {
            return this.GridName
        }

        UUID getGridUUID() {
            return this.GridUUID
        }

        String getLoginURL() {
            return this.LoginURL
        }

        Boolean isLindenGrid() {
            return this.GridUUID.equals(UUID.fromString("f14c5be7-0849-402c-946a-c80a52e9eccf"))
        }

        Boolean isPredefinedGrid() {
            return this.predefinedGrid
        }

        Unit saveToPreferences(SharedPreferences.Editor editor, String str) {
            editor.putString(str + "_grid_name", this.GridName)
            editor.putString(str + "_login_url", this.LoginURL)
            editor.putString(str + "_grid", this.GridUUID.toString())
        }

        Unit setGridName(String str) {
            this.GridName = str
        }

        Unit setLoginURL(String str) {
            this.LoginURL = str
        }

        String toString() {
            return this.GridName
        }
    }

    GridList(Context context2) {
        this.context = context2
        for (String split : context2.getResources().getStringArray(R.array.grids)) {
            String[] split2 = split.split(";")
            this.predefGrids.add(GridInfo(split2[0], split2[1], true, UUID.fromString(split2[2])))
        }
        this.customGrids = ArrayList<>()
        loadGrids()
    }

    Unit addNewGrid(GridInfo gridInfo) {
        this.customGrids.add(gridInfo)
        savePreferences()
    }

    Unit deleteGrid(GridInfo gridInfo) {
        this.customGrids.remove(gridInfo)
        savePreferences()
    }

    GridInfo getDefaultGrid() {
        return this.predefGrids.get(0)
    }

    GridInfo getGridByName(String str) {
        for (GridInfo gridInfo : this.predefGrids) {
            if (gridInfo.getGridName().equals(str)) {
                return gridInfo
            }
        }
        for (GridInfo gridInfo2 : this.customGrids) {
            if (gridInfo2.getGridName().equals(str)) {
                return gridInfo2
            }
        }
        return null
    }

    GridInfo getGridByUUID(UUID uuid) {
        for (GridInfo gridInfo : this.predefGrids) {
            if (gridInfo.getGridUUID().equals(uuid)) {
                return gridInfo
            }
        }
        for (GridInfo gridInfo2 : this.customGrids) {
            if (gridInfo2.getGridUUID().equals(uuid)) {
                return gridInfo2
            }
        }
        return null
    }

    Int getGridIndex(UUID uuid) {
        Int i = 0
        for (GridInfo gridUUID : this.predefGrids) {
            if (gridUUID.getGridUUID().equals(uuid)) {
                return i
            }
            i++
        }
        for (GridInfo gridUUID2 : this.customGrids) {
            if (gridUUID2.getGridUUID().equals(uuid)) {
                return i
            }
            i++
        }
        return 0
    }

    List<GridInfo> getGridList(List<GridInfo> list) {
        if (list == null) {
            list = ArrayList<>()
        }
        list.clear()
        list.addAll(this.predefGrids)
        list.addAll(this.customGrids)
        return list
    }

    List<GridInfo> getGridList(List<GridInfo> list, Boolean z) {
        List<GridInfo> gridList = getGridList(list)
        if (z) {
            gridList.add(GridInfo("Add another grid", (String) null, false, (UUID) null))
        }
        return gridList
    }

    Unit loadGrids() {
        this.customGrids.clear()
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext())
        Int i = defaultSharedPreferences.getInt("custom_grid_1_count", 0)
        for (Int i2 = 0; i2 < i; i2++) {
            this.customGrids.add(GridInfo(defaultSharedPreferences, "custom_grid_1_" + i2))
        }
    }

    Unit savePreferences() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext()).edit()
        edit.putInt("custom_grid_1_count", this.customGrids.size())
        Int i = 0
        while (true) {
            Int i2 = i
            if (i2 < this.customGrids.size()) {
                this.customGrids.get(i2).saveToPreferences(edit, "custom_grid_1_" + i2)
                i = i2 + 1
            } else {
                edit.commit()
                return
            }
        }
    }
}
