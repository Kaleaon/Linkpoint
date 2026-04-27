// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            NavDrawerAdapter

public class NavDrawerActivityHelper
    implements android.widget.AdapterView.OnItemClickListener
{
    private static class DrawerToggle extends ActionBarDrawerToggle
    {

        public DrawerToggle(Activity activity, DrawerLayout drawerlayout, int i, int j)
        {
            super(activity, drawerlayout, i, j);
        }
    }


    private final NavDrawerAdapter drawerAdapter;
    private final DrawerLayout drawerLayout;
    private final DrawerToggle drawerToggle;

    public NavDrawerActivityHelper(Activity activity)
    {
        drawerLayout = (DrawerLayout)activity.findViewById(0x7f100282);
        if (drawerLayout != null)
        {
            drawerToggle = new DrawerToggle(activity, drawerLayout, 0x7f090252, 0x7f0900c0);
            drawerLayout.setDrawerListener(drawerToggle);
            ListView listview = (ListView)drawerLayout.findViewById(0x7f100283);
            if (listview != null)
            {
                drawerAdapter = new NavDrawerAdapter(activity);
                listview.setAdapter(drawerAdapter);
                listview.setOnItemClickListener(this);
            } else
            {
                drawerAdapter = null;
            }
            if (activity instanceof AppCompatActivity)
            {
                activity = ((AppCompatActivity)activity).getSupportActionBar();
                if (activity != null)
                {
                    activity.setDisplayHomeAsUpEnabled(true);
                    activity.setHomeButtonEnabled(true);
                }
            }
            return;
        } else
        {
            drawerToggle = null;
            drawerAdapter = null;
            return;
        }
    }

    public boolean onBackPressed()
    {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(drawerLayout.findViewById(0x7f100283)))
        {
            drawerLayout.closeDrawers();
            return true;
        } else
        {
            return false;
        }
    }

    public void onConfigurationChanged(Configuration configuration)
    {
        if (drawerToggle != null)
        {
            drawerToggle.onConfigurationChanged(configuration);
        }
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        if (drawerLayout != null)
        {
            drawerLayout.closeDrawers();
        }
        if (drawerAdapter != null)
        {
            drawerAdapter.onItemClick(adapterview, view, i, l);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        return drawerToggle != null && drawerToggle.onOptionsItemSelected(menuitem);
    }

    public void syncState()
    {
        if (drawerToggle != null)
        {
            drawerToggle.syncState();
        }
    }
}
