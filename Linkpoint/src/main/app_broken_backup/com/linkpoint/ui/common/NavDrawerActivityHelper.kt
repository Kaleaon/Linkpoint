package com.linkpoint.ui.common
import java.util.*

import android.app.Activity
import android.content.res.Configuration
import androidx.core.widget.DrawerLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.linkpoint.R

class NavDrawerActivityHelper : AdapterView.OnItemClickListener {
    private NavDrawerAdapter drawerAdapter
    private DrawerLayout drawerLayout
    private DrawerToggle drawerToggle

    private class DrawerToggle : ActionBarDrawerToggle {
        DrawerToggle(Activity activity, DrawerLayout drawerLayout, Int i, Int i2) {
            super(activity, drawerLayout, i, i2)
        }
    }

    NavDrawerActivityHelper(Activity activity) {
        ActionBar supportActionBar
        this.drawerLayout = (activity as DrawerLayout).findViewById(R.id.drawer_layout)
        if (this.drawerLayout != null) {
            this.drawerToggle = DrawerToggle(activity, this.drawerLayout, R.string.open_menu, R.string.close_menu)
            this.drawerLayout.setDrawerListener(this.drawerToggle)
            ListView listView = (this as ListView).drawerLayout.findViewById(R.id.left_drawer)
            if (listView != null) {
                this.drawerAdapter = NavDrawerAdapter(activity)
                listView.setAdapter(this.drawerAdapter)
                listView.setOnItemClickListener(this)
            } else {
                this.drawerAdapter = null
            }
            if ((activity is AppCompatActivity) && (supportActionBar = ((AppCompatActivity) activity).getSupportActionBar()) != null) {
                supportActionBar.setDisplayHomeAsUpEnabled(true)
                supportActionBar.setHomeButtonEnabled(true)
                return
            }
            return
        }
        this.drawerToggle = null
        this.drawerAdapter = null
    }

    fun onBackPressed(): Boolean {
        if (this.drawerLayout == null || !this.drawerLayout.isDrawerOpen(this.drawerLayout.findViewById(R.id.left_drawer))) {
            return false
        }
        this.drawerLayout.closeDrawers()
        return true
    }

    fun onConfigurationChanged(Configuration configuration)  {
        if (this.drawerToggle != null) {
            this.drawerToggle.onConfigurationChanged(configuration)
        }
    }

    fun onItemClick(AdapterView<?> adapterView, View view, Int i, Long j)  {
        if (this.drawerLayout != null) {
            this.drawerLayout.closeDrawers()
        }
        if (this.drawerAdapter != null) {
            this.drawerAdapter.onItemClick(adapterView, view, i, j)
        }
    }

    fun onOptionsItemSelected(MenuItem menuItem): Boolean {
        return this.drawerToggle != null && this.drawerToggle.onOptionsItemSelected(menuItem)
    }

    fun syncState()  {
        if (this.drawerToggle != null) {
            this.drawerToggle.syncState()
        }
    }
}
