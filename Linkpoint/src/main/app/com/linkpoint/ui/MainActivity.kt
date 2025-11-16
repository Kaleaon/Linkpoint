package com.linkpoint.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.navigation.NavigationBarView
import com.linkpoint.R
import com.linkpoint.databinding.ActivityLinkpointMainBinding
import com.linkpoint.ui.chat.ChatFragment
import com.linkpoint.ui.world.WorldFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLinkpointMainBinding

    private val fragments: Map<Int, () -> Fragment> = mapOf(
        R.id.menu_world to { WorldFragment() },
        R.id.menu_chat to { ChatFragment() }
    )

    private var currentItemId: Int = R.id.menu_world

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLinkpointMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.linkpointToolbar)

        if (savedInstanceState == null) {
            showFragment(R.id.menu_world)
        } else {
            currentItemId = savedInstanceState.getInt(KEY_SELECTED_ITEM, R.id.menu_world)
            showFragment(currentItemId)
        }

        binding.bottomNavigation.selectedItemId = currentItemId
        binding.bottomNavigation.setOnItemSelectedListener(onNavigationItemSelectedListener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_ITEM, currentItemId)
    }

    private val onNavigationItemSelectedListener =
        NavigationBarView.OnItemSelectedListener { item ->
            if (item.itemId == currentItemId) {
                return@OnItemSelectedListener true
            }
            showFragment(item.itemId)
            true
        }

    private fun showFragment(itemId: Int) {
        val fragmentTag = itemId.toString()
        val fragmentManager = supportFragmentManager
        val existing = fragmentManager.findFragmentByTag(fragmentTag)
        fragmentManager.commit {
            fragmentManager.fragments.forEach { fragment ->
                if (fragment.isVisible) {
                    hide(fragment)
                }
            }
            if (existing == null) {
                fragments[itemId]?.invoke()?.let { newFragment ->
                    add(R.id.contentContainer, newFragment, fragmentTag)
                }
            } else {
                show(existing)
            }
        }
        currentItemId = itemId
        updateTitle(itemId)
    }

    private fun updateTitle(itemId: Int) {
        val titleRes = when (itemId) {
            R.id.menu_chat -> R.string.linkpoint_chat_title
            else -> R.string.linkpoint_world_title
        }
        binding.linkpointToolbar.setTitle(titleRes)
    }

    companion object {
        private const val KEY_SELECTED_ITEM = "linkpoint:selected_item"
    }
}
