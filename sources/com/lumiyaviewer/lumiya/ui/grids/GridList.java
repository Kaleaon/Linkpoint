// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.grids;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class GridList
{
    public static class GridArrayAdapter extends ArrayAdapter
    {

        public GridArrayAdapter(Context context1, List list)
        {
            super(context1, 0x1090008, list);
            setDropDownViewResource(0x1090009);
        }
    }

    public static class GridInfo
    {

        private String GridName;
        private UUID GridUUID;
        private String LoginURL;
        private boolean predefinedGrid;

        public String getGridName()
        {
            return GridName;
        }

        public UUID getGridUUID()
        {
            return GridUUID;
        }

        public String getLoginURL()
        {
            return LoginURL;
        }

        public boolean isLindenGrid()
        {
            return GridUUID.equals(UUID.fromString("f14c5be7-0849-402c-946a-c80a52e9eccf"));
        }

        public boolean isPredefinedGrid()
        {
            return predefinedGrid;
        }

        public void saveToPreferences(android.content.SharedPreferences.Editor editor, String s)
        {
            editor.putString((new StringBuilder()).append(s).append("_grid_name").toString(), GridName);
            editor.putString((new StringBuilder()).append(s).append("_login_url").toString(), LoginURL);
            editor.putString((new StringBuilder()).append(s).append("_grid").toString(), GridUUID.toString());
        }

        public void setGridName(String s)
        {
            GridName = s;
        }

        public void setLoginURL(String s)
        {
            LoginURL = s;
        }

        public String toString()
        {
            return GridName;
        }

        public GridInfo(SharedPreferences sharedpreferences, String s)
        {
            GridName = sharedpreferences.getString((new StringBuilder()).append(s).append("_grid_name").toString(), "");
            LoginURL = sharedpreferences.getString((new StringBuilder()).append(s).append("_login_url").toString(), "");
            predefinedGrid = false;
            GridUUID = UUID.fromString(sharedpreferences.getString((new StringBuilder()).append(s).append("_grid").toString(), ""));
        }

        public GridInfo(String s, String s1, boolean flag, UUID uuid)
        {
            GridName = s;
            LoginURL = s1;
            predefinedGrid = flag;
            GridUUID = uuid;
        }
    }


    private Context context;
    private ArrayList customGrids;
    private ArrayList predefGrids;

    public GridList(Context context1)
    {
        context = context1;
        predefGrids = new ArrayList();
        context1 = context1.getResources().getStringArray(0x7f0f0009);
        int j = context1.length;
        for (int i = 0; i < j; i++)
        {
            String as[] = context1[i].split(";");
            predefGrids.add(new GridInfo(as[0], as[1], true, UUID.fromString(as[2])));
        }

        customGrids = new ArrayList();
        loadGrids();
    }

    public void addNewGrid(GridInfo gridinfo)
    {
        customGrids.add(gridinfo);
        savePreferences();
    }

    public void deleteGrid(GridInfo gridinfo)
    {
        customGrids.remove(gridinfo);
        savePreferences();
    }

    public GridInfo getDefaultGrid()
    {
        return (GridInfo)predefGrids.get(0);
    }

    public GridInfo getGridByName(String s)
    {
        for (Iterator iterator = predefGrids.iterator(); iterator.hasNext();)
        {
            GridInfo gridinfo = (GridInfo)iterator.next();
            if (gridinfo.getGridName().equals(s))
            {
                return gridinfo;
            }
        }

        for (Iterator iterator1 = customGrids.iterator(); iterator1.hasNext();)
        {
            GridInfo gridinfo1 = (GridInfo)iterator1.next();
            if (gridinfo1.getGridName().equals(s))
            {
                return gridinfo1;
            }
        }

        return null;
    }

    public GridInfo getGridByUUID(UUID uuid)
    {
        for (Iterator iterator = predefGrids.iterator(); iterator.hasNext();)
        {
            GridInfo gridinfo = (GridInfo)iterator.next();
            if (gridinfo.getGridUUID().equals(uuid))
            {
                return gridinfo;
            }
        }

        for (Iterator iterator1 = customGrids.iterator(); iterator1.hasNext();)
        {
            GridInfo gridinfo1 = (GridInfo)iterator1.next();
            if (gridinfo1.getGridUUID().equals(uuid))
            {
                return gridinfo1;
            }
        }

        return null;
    }

    public int getGridIndex(UUID uuid)
    {
        Iterator iterator = predefGrids.iterator();
        int i;
        for (i = 0; iterator.hasNext(); i++)
        {
            if (((GridInfo)iterator.next()).getGridUUID().equals(uuid))
            {
                return i;
            }
        }

        for (Iterator iterator1 = customGrids.iterator(); iterator1.hasNext();)
        {
            if (((GridInfo)iterator1.next()).getGridUUID().equals(uuid))
            {
                return i;
            }
            i++;
        }

        return 0;
    }

    public List getGridList(List list)
    {
        Object obj = list;
        if (list == null)
        {
            obj = new ArrayList();
        }
        ((List) (obj)).clear();
        ((List) (obj)).addAll(predefGrids);
        ((List) (obj)).addAll(customGrids);
        return ((List) (obj));
    }

    public List getGridList(List list, boolean flag)
    {
        list = getGridList(list);
        if (flag)
        {
            list.add(new GridInfo("Add another grid", null, false, null));
        }
        return list;
    }

    public void loadGrids()
    {
        int i = 0;
        customGrids.clear();
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        for (int j = sharedpreferences.getInt("custom_grid_1_count", 0); i < j; i++)
        {
            customGrids.add(new GridInfo(sharedpreferences, (new StringBuilder()).append("custom_grid_1_").append(i).toString()));
        }

    }

    public void savePreferences()
    {
        android.content.SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
        editor.putInt("custom_grid_1_count", customGrids.size());
        for (int i = 0; i < customGrids.size(); i++)
        {
            ((GridInfo)customGrids.get(i)).saveToPreferences(editor, (new StringBuilder()).append("custom_grid_1_").append(i).toString());
        }

        editor.commit();
    }
}
