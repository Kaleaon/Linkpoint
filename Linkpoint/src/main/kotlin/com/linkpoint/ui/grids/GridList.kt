package com.linkpoint.ui.grids

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.widget.ArrayAdapter
import com.linkpoint.R
import java.util.ArrayList
import java.util.List
import java.util.UUID

class GridList {
    private Context context
    private ArrayList<GridInfo> customGrids
    private ArrayList<GridInfo> predefGrids = ArrayList<>()

    @JvmStatic
    class GridArrayAdapter : ArrayAdapter()<GridInfo> {
        public GridArrayAdapter(Context context, List<GridInfo> list) {
            super(context, 17367048, list)
            setDropDownViewResource(17367049)
        }
    }

    @JvmStatic
    class GridInfo {
        private String GridName
        private UUID GridUUID
        private String LoginURL
        private Boolean predefinedGrid

        public GridInfo(SharedPreferences sharedPreferences, String str) {
            this.GridName = sharedPreferences.getString(str + "_grid_name", "")
            this.LoginURL = sharedPreferences.getString(str + "_login_url", "")
            this.predefinedGrid = false
            this.GridUUID = UUID.fromString(sharedPreferences.getString(str + "_grid", ""))
        }

        public GridInfo(String str, String str2, Boolean z, UUID uuid) {
            this.GridName = str
            this.LoginURL = str2
            this.predefinedGrid = z
            this.GridUUID = uuid
        }

         public fun getGridName(): String {
            return this.GridName
        }

         public fun getGridUUID(): UUID {
            return this.GridUUID
        }

         public fun getLoginURL(): String {
            return this.LoginURL
        }

         public fun isLindenGrid(): Boolean {
            return this.GridUUID.equals(UUID.fromString("f14c5be7-0849-402c-946a-c80a52e9eccf"))
        }

         public fun isPredefinedGrid(): Boolean {
            return this.predefinedGrid
        }

        fun saveToPreferences(SharedPreferences.Editor editor, str: String) {
            editor.putString(str + "_grid_name", this.GridName)
            editor.putString(str + "_login_url", this.LoginURL)
            editor.putString(str + "_grid", this.GridUUID.toString())
        }

        fun setGridName(str: String) {
            this.GridName = str
        }

        fun setLoginURL(str: String) {
            this.LoginURL = str
        }

         public fun toString(): String {
            return this.GridName
        }
    }

    public GridList(Context context2) {
        this.context = context2
        for (String split : context2.getResources().getStringArray(R.array.grids)) {
            val split2: Array<String> = split.split(";")
            this.predefGrids.add(GridInfo(split2[0], split2[1], true, UUID.fromString(split2[2])))
        }
        this.customGrids = ArrayList<>()
        loadGrids()
    }

    fun addNewGrid(gridInfo: GridInfo) {
        this.customGrids.add(gridInfo)
        savePreferences()
    }

    fun deleteGrid(gridInfo: GridInfo) {
        this.customGrids.remove(gridInfo)
        savePreferences()
    }

     public fun getDefaultGrid(): GridInfo {
        return this.predefGrids.get(0)
    }

     public fun getGridByName(str: String): GridInfo {
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

     public fun getGridByUUID(uuid: UUID): GridInfo {
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

     public fun getGridIndex(uuid: UUID): Int {
        val i: Int = 0
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

    public List<GridInfo> getGridList(List<GridInfo> list) {
        if (list == null) {
            list = ArrayList<>()
        }
        list.clear()
        list.addAll(this.predefGrids)
        list.addAll(this.customGrids)
        return list
    }

    public List<GridInfo> getGridList(List<GridInfo> list, Boolean z) {
        val gridList: List<GridInfo> = getGridList(list)
        if (z) {
            gridList.add(GridInfo("Add another grid", (String) null, false, (UUID) null))
        }
        return gridList
    }

    fun loadGrids() {
        this.customGrids.clear()
        val defaultSharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext())
        val i: Int = defaultSharedPreferences.getInt("custom_grid_1_count", 0)
        for (Int i2 = 0; i2 < i; i2++) {
            this.customGrids.add(GridInfo(defaultSharedPreferences, "custom_grid_1_" + i2))
        }
    }

    fun savePreferences() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext()).edit()
        edit.putInt("custom_grid_1_count", this.customGrids.size())
        val i: Int = 0
        while (true) {
            val i2: Int = i
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
