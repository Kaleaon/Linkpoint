package com.linkpoint.ui.common
import java.util.*

import android.app.Activity
import android.content.res.Configuration
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.linkpoint.R

class NavDrawerActivityHelper : AdapterView.OnItemClickListener {
    private val NavDrawerAdapter drawerAdapter
    private val DrawerLayout drawerLayout
    private val DrawerToggle drawerToggle

    @JvmStatic
private class DrawerToggle : ActionBarDrawerToggle() {
        public DrawerToggle(Activity activity, DrawerLayout drawerLayout, Int i, Int i2) {
            super(activity, drawerLayout, i, i2)
        }
    }

    public NavDrawerActivityHelper(Activity activity) {
        ActionBar supportActionBar
        this.drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout)
        if (this.drawerLayout != null) {
            this.drawerToggle = DrawerToggle(activity, this.drawerLayout, R.string.open_menu, R.string.close_menu)
            this.drawerLayout.setDrawerListener(this.drawerToggle)
            val listView: ListView = (ListView) this.drawerLayout.findViewById(R.id.left_drawer)
            if (listView != null) {
                this.drawerAdapter = NavDrawerAdapter(activity)
                listView.setAdapter(this.drawerAdapter)
                listView.setOnItemClickListener(this)
            } else {
                this.drawerAdapter = null
            }
            if ((activity instanceof AppCompatActivity) && (supportActionBar = ((AppCompatActivity) activity).getSupportActionBar()) != null) {
                supportActionBar.setDisplayHomeAsUpEnabled(true)
                supportActionBar.setHomeButtonEnabled(true)
                return
            }
            return
        }
        this.drawerToggle = null
        this.drawerAdapter = null
    }

     public fun onBackPressed(): Boolean {
        if (this.drawerLayout == null || !this.drawerLayout.isDrawerOpen(this.drawerLayout.findViewById(R.id.left_drawer))) {
            return false
        }
        this.drawerLayout.closeDrawers()
        return true
    }

    fun onConfigurationChanged(configuration: Configuration) {
        if (this.drawerToggle != null) {
            this.drawerToggle.onConfigurationChanged(configuration)
        }
    }

    fun onItemClick(adapterView: AdapterView<?>, view: View, i: Int, j: Long) {
        if (this.drawerLayout != null) {
            this.drawerLayout.closeDrawers()
        }
        if (this.drawerAdapter != null) {
            this.drawerAdapter.onItemClick(adapterView, view, i, j)
        }
    }

     public fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return this.drawerToggle != null && this.drawerToggle.onOptionsItemSelected(menuItem)
    }

    fun syncState() {
        if (this.drawerToggle != null) {
            this.drawerToggle.syncState()
        }
    }
}
