package com.linkpoint.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.groups.Group
import kotlinx.coroutines.launch

/**
 * Activity for managing groups list.
 */
class GroupsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)
        
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.groups)
        }
        
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, GroupsListFragment.newInstance())
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

/**
 * Fragment for displaying groups list.
 */
class GroupsListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GroupsAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: TextView

    private val groupsManager by lazy { 
        LinkpointApp.getInstance().groupsManager 
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_groups_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupAdapter()
        loadGroups()
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.groups_recyclerview)
        progressBar = view.findViewById(R.id.progress_bar)
        emptyView = view.findViewById(R.id.empty_view)
    }

    private fun setupAdapter() {
        adapter = GroupsAdapter(
            onGroupClick = { group -> showGroupDetails(group) },
            onGroupLongClick = { group -> showGroupOptions(group) }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun loadGroups() {
        progressBar.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
        recyclerView.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val groups = groupsManager.getAllGroups()
                
                if (groups.isNotEmpty()) {
                    adapter.submitList(groups)
                    recyclerView.visibility = View.VISIBLE
                } else {
                    emptyView.visibility = View.VISIBLE
                    emptyView.text = getString(R.string.no_groups)
                }
            } catch (e: Exception) {
                emptyView.visibility = View.VISIBLE
                emptyView.text = "Error: ${e.message}"
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showGroupDetails(group: Group) {
        // Show group details dialog
        GroupDetailsDialog.newInstance(group).show(
            childFragmentManager,
            "group_details"
        )
    }

    private fun showGroupOptions(group: Group) {
        // Show group options dialog
        GroupOptionsDialog.newInstance(group).show(
            childFragmentManager,
            "group_options"
        )
    }

    companion object {
        fun newInstance() = GroupsListFragment()
    }
}
