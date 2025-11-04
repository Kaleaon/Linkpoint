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
import android.widget.ListAdapter;
import android.widget.ListView;
import com.lumiyaviewer.lumiya.R;

/* loaded from: classes.dex */
public class NavDrawerActivityHelper implements AdapterView.OnItemClickListener {
    private final NavDrawerAdapter drawerAdapter;
    private final DrawerLayout drawerLayout;
    private final DrawerToggle drawerToggle;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DrawerToggle extends ActionBarDrawerToggle {
        public DrawerToggle(Activity activity, DrawerLayout drawerLayout, int i, int i2) {
            super(activity, drawerLayout, i, i2);
        }
    }

    public NavDrawerActivityHelper(Activity activity) {
        ActionBar supportActionBar;
        this.drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        if (this.drawerLayout == null) {
            this.drawerToggle = null;
            this.drawerAdapter = null;
            return;
        }
        this.drawerToggle = new DrawerToggle(activity, this.drawerLayout, R.string.open_menu, R.string.close_menu);
        this.drawerLayout.setDrawerListener(this.drawerToggle);
        ListView listView = (ListView) this.drawerLayout.findViewById(R.id.left_drawer);
        if (listView != null) {
            this.drawerAdapter = new NavDrawerAdapter(activity);
            listView.setAdapter((ListAdapter) this.drawerAdapter);
            listView.setOnItemClickListener(this);
        } else {
            this.drawerAdapter = null;
        }
        if (!(activity instanceof AppCompatActivity) || (supportActionBar = ((AppCompatActivity) activity).getSupportActionBar()) == null) {
            return;
        }
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeButtonEnabled(true);
    }

    public boolean onBackPressed() {
        if (this.drawerLayout == null || !this.drawerLayout.isDrawerOpen(this.drawerLayout.findViewById(R.id.left_drawer))) {
            return false;
        }
        this.drawerLayout.closeDrawers();
        return true;
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (this.drawerToggle != null) {
            this.drawerToggle.onConfigurationChanged(configuration);
        }
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        if (this.drawerLayout != null) {
            this.drawerLayout.closeDrawers();
        }
        if (this.drawerAdapter != null) {
            this.drawerAdapter.onItemClick(adapterView, view, i, j);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        return this.drawerToggle != null && this.drawerToggle.onOptionsItemSelected(menuItem);
    }

    public void syncState() {
        if (this.drawerToggle != null) {
            this.drawerToggle.syncState();
        }
    }
}
