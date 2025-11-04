// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.grids;

import android.widget.ArrayAdapter;
import android.content.SharedPreferences$Editor;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.UUID;
import java.util.ArrayList;
import android.content.Context;

public class GridList
{
    private Context context;
    private ArrayList<GridInfo> customGrids;
    private ArrayList<GridInfo> predefGrids;
    
    public GridList(final Context context) {
        this.context = context;
        this.predefGrids = new ArrayList<GridInfo>();
        final String[] stringArray = context.getResources().getStringArray(2131689481);
        for (int length = stringArray.length, i = 0; i < length; ++i) {
            final String[] split = stringArray[i].split(";");
            this.predefGrids.add(new GridInfo(split[0], split[1], true, UUID.fromString(split[2])));
        }
        this.customGrids = new ArrayList<GridInfo>();
        this.loadGrids();
    }
    
    public void addNewGrid(final GridInfo e) {
        this.customGrids.add(e);
        this.savePreferences();
    }
    
    public void deleteGrid(final GridInfo o) {
        this.customGrids.remove(o);
        this.savePreferences();
    }
    
    public GridInfo getDefaultGrid() {
        return this.predefGrids.get(0);
    }
    
    public GridInfo getGridByName(final String s) {
        for (final GridInfo gridInfo : this.predefGrids) {
            if (gridInfo.getGridName().equals(s)) {
                return gridInfo;
            }
        }
        for (final GridInfo gridInfo2 : this.customGrids) {
            if (gridInfo2.getGridName().equals(s)) {
                return gridInfo2;
            }
        }
        return null;
    }
    
    public GridInfo getGridByUUID(final UUID uuid) {
        for (final GridInfo gridInfo : this.predefGrids) {
            if (gridInfo.getGridUUID().equals(uuid)) {
                return gridInfo;
            }
        }
        for (final GridInfo gridInfo2 : this.customGrids) {
            if (gridInfo2.getGridUUID().equals(uuid)) {
                return gridInfo2;
            }
        }
        return null;
    }
    
    public int getGridIndex(final UUID uuid) {
        final Iterator<Object> iterator = this.predefGrids.iterator();
        int n = 0;
        while (iterator.hasNext()) {
            if (iterator.next().getGridUUID().equals(uuid)) {
                return n;
            }
            ++n;
        }
        final Iterator<Object> iterator2 = this.customGrids.iterator();
        while (iterator2.hasNext()) {
            if (iterator2.next().getGridUUID().equals(uuid)) {
                return n;
            }
            ++n;
        }
        return 0;
    }
    
    public List<GridInfo> getGridList(final List<GridInfo> list) {
        List<GridInfo> list2 = list;
        if (list == null) {
            list2 = new ArrayList<GridInfo>();
        }
        list2.clear();
        list2.addAll(this.predefGrids);
        list2.addAll(this.customGrids);
        return list2;
    }
    
    public List<GridInfo> getGridList(final List<GridInfo> list, final boolean b) {
        final List<GridInfo> gridList = this.getGridList(list);
        if (b) {
            gridList.add(new GridInfo("Add another grid", null, false, null));
        }
        return gridList;
    }
    
    public void loadGrids() {
        int i = 0;
        this.customGrids.clear();
        for (SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext()); i < defaultSharedPreferences.getInt("custom_grid_1_count", 0); ++i) {
            this.customGrids.add(new GridInfo(defaultSharedPreferences, "custom_grid_1_" + i));
        }
    }
    
    public void savePreferences() {
        final SharedPreferences$Editor edit = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext()).edit();
        edit.putInt("custom_grid_1_count", this.customGrids.size());
        for (int i = 0; i < this.customGrids.size(); ++i) {
            this.customGrids.get(i).saveToPreferences(edit, "custom_grid_1_" + i);
        }
        edit.commit();
    }
    
    public static class GridArrayAdapter extends ArrayAdapter<GridInfo>
    {
        public GridArrayAdapter(final Context context, final List<GridInfo> list) {
            super(context, 17367048, (List)list);
            this.setDropDownViewResource(17367049);
        }
    }
    
    public static class GridInfo
    {
        private String GridName;
        private UUID GridUUID;
        private String LoginURL;
        private boolean predefinedGrid;
        
        public GridInfo(final SharedPreferences sharedPreferences, final String str) {
            this.GridName = sharedPreferences.getString(str + "_grid_name", "");
            this.LoginURL = sharedPreferences.getString(str + "_login_url", "");
            this.predefinedGrid = false;
            this.GridUUID = UUID.fromString(sharedPreferences.getString(str + "_grid", ""));
        }
        
        public GridInfo(final String gridName, final String loginURL, final boolean predefinedGrid, final UUID gridUUID) {
            this.GridName = gridName;
            this.LoginURL = loginURL;
            this.predefinedGrid = predefinedGrid;
            this.GridUUID = gridUUID;
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
        
        public void saveToPreferences(final SharedPreferences$Editor sharedPreferences$Editor, final String str) {
            sharedPreferences$Editor.putString(str + "_grid_name", this.GridName);
            sharedPreferences$Editor.putString(str + "_login_url", this.LoginURL);
            sharedPreferences$Editor.putString(str + "_grid", this.GridUUID.toString());
        }
        
        public void setGridName(final String gridName) {
            this.GridName = gridName;
        }
        
        public void setLoginURL(final String loginURL) {
            this.LoginURL = loginURL;
        }
        
        @Override
        public String toString() {
            return this.GridName;
        }
    }
}
