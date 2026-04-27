// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListAdapter;
import android.content.Context;
import android.widget.ListView;
import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView$OnItemClickListener;

public class NavDrawerActivityHelper implements AdapterView$OnItemClickListener
{
    private final NavDrawerAdapter drawerAdapter;
    private final DrawerLayout drawerLayout;
    private final DrawerToggle drawerToggle;
    
    public NavDrawerActivityHelper(final Activity activity) {
        this.drawerLayout = (DrawerLayout)activity.findViewById(2131755650);
        if (this.drawerLayout != null) {
            this.drawerToggle = new DrawerToggle(activity, this.drawerLayout, 2131296850, 2131296448);
            this.drawerLayout.setDrawerListener((DrawerLayout.DrawerListener)this.drawerToggle);
            final ListView listView = (ListView)this.drawerLayout.findViewById(2131755651);
            if (listView != null) {
                listView.setAdapter((ListAdapter)(this.drawerAdapter = new NavDrawerAdapter((Context)activity)));
                listView.setOnItemClickListener((AdapterView$OnItemClickListener)this);
            }
            else {
                this.drawerAdapter = null;
            }
            if (activity instanceof AppCompatActivity) {
                final ActionBar supportActionBar = ((AppCompatActivity)activity).getSupportActionBar();
                if (supportActionBar != null) {
                    supportActionBar.setDisplayHomeAsUpEnabled(true);
                    supportActionBar.setHomeButtonEnabled(true);
                }
            }
        }
        else {
            this.drawerToggle = null;
            this.drawerAdapter = null;
        }
    }
    
    public boolean onBackPressed() {
        if (this.drawerLayout != null && this.drawerLayout.isDrawerOpen(this.drawerLayout.findViewById(2131755651))) {
            this.drawerLayout.closeDrawers();
            return true;
        }
        return false;
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        if (this.drawerToggle != null) {
            this.drawerToggle.onConfigurationChanged(configuration);
        }
    }
    
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
        if (this.drawerLayout != null) {
            this.drawerLayout.closeDrawers();
        }
        if (this.drawerAdapter != null) {
            this.drawerAdapter.onItemClick(adapterView, view, n, n2);
        }
    }
    
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        return this.drawerToggle != null && this.drawerToggle.onOptionsItemSelected(menuItem);
    }
    
    public void syncState() {
        if (this.drawerToggle != null) {
            this.drawerToggle.syncState();
        }
    }
    
    private static class DrawerToggle extends ActionBarDrawerToggle
    {
        public DrawerToggle(final Activity activity, final DrawerLayout drawerLayout, final int n, final int n2) {
            super(activity, drawerLayout, n, n2);
        }
    }
}
