// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.grids;

import android.content.SharedPreferences;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.grids:
//            GridList

public static class GridUUID
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

    public void saveToPreferences(android.content.Editor editor, String s)
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

    public (SharedPreferences sharedpreferences, String s)
    {
        GridName = sharedpreferences.getString((new StringBuilder()).append(s).append("_grid_name").toString(), "");
        LoginURL = sharedpreferences.getString((new StringBuilder()).append(s).append("_login_url").toString(), "");
        predefinedGrid = false;
        GridUUID = UUID.fromString(sharedpreferences.getString((new StringBuilder()).append(s).append("_grid").toString(), ""));
    }

    public GridUUID(String s, String s1, boolean flag, UUID uuid)
    {
        GridName = s;
        LoginURL = s1;
        predefinedGrid = flag;
        GridUUID = uuid;
    }
}
