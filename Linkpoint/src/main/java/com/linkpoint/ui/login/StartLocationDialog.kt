package com.linkpoint.ui.login

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.core.*

/**
 * Dialog for selecting a start location.
 * 
 * Shows options:
 * - Last Location
 * - Home
 * - Saved Landmarks (from cache)
 * - Featured Destinations
 * - Custom SLURL
 * 
 * Based on Second Life mobile app functionality.
 */
class StartLocationDialog : DialogFragment() {
    
    interface StartLocationListener {
        fun onStartLocationSelected(option: StartLocationOption)
        fun onCustomSLURLEntered(slurl: String)
    }
    
    private var listener: StartLocationListener? = null
    private lateinit var adapter: StartLocationAdapter
    private lateinit var searchEdit: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var customSlurlButton: Button
    
    private val app by lazy { LinkpointApp.getInstance() }
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? StartLocationListener
    }
    
    override fun onDetach() {
        super.onDetach()
        listener = null
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_start_location, null)
        
        setupViews(view)
        loadOptions()
        
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.select_start_location)
            .setView(view)
            .setNegativeButton(R.string.cancel, null)
            .create()
    }
    
    private fun setupViews(view: View) {
        searchEdit = view.findViewById(R.id.searchEdit)
        recyclerView = view.findViewById(R.id.optionsRecyclerView)
        emptyView = view.findViewById(R.id.emptyView)
        customSlurlButton = view.findViewById(R.id.customSlurlButton)
        
        // Setup adapter
        adapter = StartLocationAdapter { option ->
            if (option.type == StartLocationType.CUSTOM_SLURL) {
                showCustomSLURLDialog()
            } else if (option.type != StartLocationType.HEADER) {
                listener?.onStartLocationSelected(option)
                dismiss()
            }
        }
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        
        // Search functionality
        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterOptions(s?.toString() ?: "")
            }
        })
        
        // Custom SLURL button
        customSlurlButton.setOnClickListener {
            showCustomSLURLDialog()
        }
    }
    
    private fun loadOptions() {
        val options = mutableListOf<StartLocationOption>()
        
        // Add basic options
        options.addAll(app.startLocationManager.getAvailableOptions())
        
        // Add featured destinations
        val featured = app.destinationGuide.featuredDestinations.value
        if (featured.isNotEmpty()) {
            options.add(StartLocationOption(
                type = StartLocationType.HEADER,
                displayName = "Featured Destinations",
                description = "",
                iconType = StartLocationIconType.STAR
            ))
            featured.take(5).forEach { dest ->
                options.add(app.destinationGuide.toStartLocationOption(dest))
            }
        }
        
        updateList(options)
    }
    
    private fun filterOptions(query: String) {
        if (query.isBlank()) {
            loadOptions()
            return
        }
        
        val lowerQuery = query.lowercase()
        val allOptions = mutableListOf<StartLocationOption>()
        
        // Search in start location options
        allOptions.addAll(app.startLocationManager.getAvailableOptions().filter {
            it.type != StartLocationType.HEADER &&
            (it.displayName.lowercase().contains(lowerQuery) ||
             it.description.lowercase().contains(lowerQuery))
        })
        
        // Search in destinations
        val destinations = app.destinationGuide.searchDestinations(query)
        destinations.forEach { dest ->
            allOptions.add(app.destinationGuide.toStartLocationOption(dest))
        }
        
        updateList(allOptions)
    }
    
    private fun updateList(options: List<StartLocationOption>) {
        adapter.submitList(options)
        
        if (options.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
    
    private fun showCustomSLURLDialog() {
        val editText = EditText(requireContext()).apply {
            hint = "secondlife://RegionName/128/128/25"
            setSingleLine()
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.enter_slurl)
            .setView(editText)
            .setPositiveButton(R.string.ok) { _, _ ->
                val slurl = editText.text.toString().trim()
                if (slurl.isNotBlank()) {
                    listener?.onCustomSLURLEntered(slurl)
                    dismiss()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    companion object {
        const val TAG = "StartLocationDialog"
        
        fun newInstance(): StartLocationDialog {
            return StartLocationDialog()
        }
    }
}

/**
 * Adapter for start location options list.
 */
class StartLocationAdapter(
    private val onItemClick: (StartLocationOption) -> Unit
) : ListAdapter<StartLocationOption, RecyclerView.ViewHolder>(DiffCallback()) {
    
    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_OPTION = 1
    }
    
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).type) {
            StartLocationType.HEADER -> VIEW_TYPE_HEADER
            else -> VIEW_TYPE_OPTION
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = inflater.inflate(R.layout.item_start_location_header, parent, false)
                HeaderViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_start_location, parent, false)
                OptionViewHolder(view, onItemClick)
            }
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val option = getItem(position)
        when (holder) {
            is HeaderViewHolder -> holder.bind(option)
            is OptionViewHolder -> holder.bind(option)
        }
    }
    
    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.headerTitle)
        
        fun bind(option: StartLocationOption) {
            titleText.text = option.displayName
        }
    }
    
    class OptionViewHolder(
        itemView: View,
        private val onItemClick: (StartLocationOption) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val iconView: ImageView = itemView.findViewById(R.id.optionIcon)
        private val nameText: TextView = itemView.findViewById(R.id.optionName)
        private val descText: TextView = itemView.findViewById(R.id.optionDescription)
        
        fun bind(option: StartLocationOption) {
            nameText.text = option.displayName
            descText.text = option.description
            descText.visibility = if (option.description.isBlank()) View.GONE else View.VISIBLE
            
            // Set icon based on type
            val iconRes = when (option.iconType) {
                StartLocationIconType.HISTORY -> R.drawable.ic_history
                StartLocationIconType.HOME -> R.drawable.ic_home
                StartLocationIconType.LANDMARK -> R.drawable.ic_landmark
                StartLocationIconType.LOCATION -> R.drawable.ic_location
                StartLocationIconType.STAR -> R.drawable.ic_star
                StartLocationIconType.CATEGORY -> R.drawable.ic_category
            }
            iconView.setImageResource(iconRes)
            
            itemView.setOnClickListener { onItemClick(option) }
        }
    }
    
    class DiffCallback : DiffUtil.ItemCallback<StartLocationOption>() {
        override fun areItemsTheSame(oldItem: StartLocationOption, newItem: StartLocationOption): Boolean {
            return oldItem.displayName == newItem.displayName && oldItem.type == newItem.type
        }
        
        override fun areContentsTheSame(oldItem: StartLocationOption, newItem: StartLocationOption): Boolean {
            return oldItem == newItem
        }
    }
}
