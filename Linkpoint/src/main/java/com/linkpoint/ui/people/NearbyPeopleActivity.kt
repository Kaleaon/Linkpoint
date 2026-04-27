package com.linkpoint.ui.people

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.linkpoint.R

/**
 * Activity for viewing nearby people (avatars in the current region).
 * Hosts NearbyPeopleFragment and provides navigation.
 *
 * Legacy entry point retained during Compose migration.
 * Removal target: 2026.09.
 */
@Deprecated(
    message = "Legacy View/Fragment entry point. Use NearbyPeopleScreen-based Compose navigation.",
    replaceWith = ReplaceWith("NearbyPeopleScreen")
)
class NearbyPeopleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_people)
        
        // Setup toolbar with back button
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.nearby_people)
        }
        
        // Add nearby people fragment if not already added
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NearbyPeopleFragment.newInstance())
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
