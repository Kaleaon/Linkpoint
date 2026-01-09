package com.linkpoint.ui.search

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.SwipeRefreshLayout
import com.google.common.base.Strings
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.messages.ParcelInfoReply
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.ChatterNameRetriever
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatterPicView
import com.linkpoint.ui.chat.profiles.GroupProfileFragment
import com.linkpoint.ui.chat.profiles.UserProfileFragment
import com.linkpoint.ui.common.*
import com.linkpoint.ui.common.loadmon.LoadableMonitor
import com.linkpoint.utils.UUIDPool
import java.util.UUID

class ParcelInfoFragment : FragmentWithTitle(), ReloadableFragment, LoadableMonitor.OnLoadableDataChangedListener, ChatterNameRetriever.OnChatterNameUpdated {
    private val PARCEL_UUID_KEY = "parcelUUID"
    
    private val parcelInfoReply = SubscriptionData<UUID, ParcelInfoReply>(UIThreadExecutor.getInstance())
    private val loadableMonitor = LoadableMonitor(parcelInfoReply).withDataChangedListener(this)
    
    private var ownerGroupNameRetriever: ChatterNameRetriever? = null
    private var ownerNameRetriever: ChatterNameRetriever? = null
    
    // Views
    private var parcelDetailsDescription: TextView? = null
    private var parcelDetailsName: TextView? = null
    private var parcelImageView: ImageAssetView? = null
    private var parcelLocation: TextView? = null
    private var parcelOwnerName: TextView? = null
    private var parcelOwnerPic: ChatterPicView? = null
    private var parcelSimName: TextView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var loadingLayout: LoadingLayout? = null

    fun makeSelection(agentId: UUID, parcelId: UUID): Bundle {
        val bundle = Bundle()
        ActivityUtils.setActiveAgentID(bundle, agentId)
        bundle.putString(PARCEL_UUID_KEY, parcelId.toString())
        return bundle
    }

    private fun showParcelInfo(uuid: UUID?) {
        val userManager = ActivityUtils.getUserManager(arguments)
        if (userManager != null && uuid != null) {
            Debug.Printf("ParcelInfo: subscribing for UUID %s", uuid)
            // Assuming parcelInfoData() returns a Subscribable/Cache that can be used with subscribe
            // Decompiled: this.parcelInfoReply.subscribe(userManager.parcelInfoData().getPool(), uuid)
            // Checking UserManager.kt: parcelInfoData() returns SLMessageResponseCacher
            // SLMessageResponseCacher probably has getPool()
            this.parcelInfoReply.subscribe(userManager.parcelInfoData().pool, uuid)
        }
    }

    override fun onChatterNameUpdated(retriever: ChatterNameRetriever) {
        if ((retriever === ownerNameRetriever || retriever === ownerGroupNameRetriever) && 
            ownerGroupNameRetriever != null && ownerNameRetriever != null && isAdded) {
            
            val effectiveRetriever = if (ownerGroupNameRetriever?.resolvedName != null) ownerGroupNameRetriever else ownerNameRetriever
            val resolvedName = effectiveRetriever?.resolvedName
            
            parcelOwnerName?.text = resolvedName ?: getString(R.string.name_loading_title)
            parcelOwnerPic?.visibility = View.VISIBLE
            if (effectiveRetriever != null) {
                parcelOwnerPic?.setChatterID(effectiveRetriever.chatterID, resolvedName)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.parcel_info, container, false)
        
        // Bind Views
        parcelDetailsDescription = view.findViewById(R.id.parcel_details_description) // 2131755599
        parcelDetailsName = view.findViewById(R.id.parcel_details_name) // 2131755598
        parcelImageView = view.findViewById(R.id.parcel_image_view) // 2131755602
        parcelLocation = view.findViewById(R.id.parcel_location) // 2131755605
        parcelOwnerName = view.findViewById(R.id.parcel_owner_name) // 2131755606
        parcelOwnerPic = view.findViewById(R.id.parcel_owner_pic) // 2131755607
        parcelSimName = view.findViewById(R.id.parcel_sim_name) // 2131755604
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        loadingLayout = view.findViewById(R.id.loading_layout)
        
        // Button Listeners
        view.findViewById<View>(R.id.parcel_owner_profile_click_area)?.setOnClickListener { // 2131755608
            onParcelOwnerProfileClick()
        }
        view.findViewById<View>(R.id.parcel_teleport_button)?.setOnClickListener { // 2131755600
            onParcelTeleportButton()
        }

        loadableMonitor.setLoadingLayout(loadingLayout, getString(R.string.no_parcel_selected), getString(R.string.failed_to_load_parcel_data))
        loadableMonitor.setSwipeRefreshLayout(swipeRefreshLayout)
        
        parcelImageView?.setAlignTop(true)
        parcelImageView?.setVerticalFit(true)
        
        return view
    }

    override fun onDestroyView() {
        // Unbinder logic replacement: null out views to avoid leaks if fragment outlives view
        parcelDetailsDescription = null
        parcelDetailsName = null
        parcelImageView = null
        parcelLocation = null
        parcelOwnerName = null
        parcelOwnerPic = null
        parcelSimName = null
        swipeRefreshLayout = null
        loadingLayout = null
        super.onDestroyView()
    }

    override fun onLoadableDataChanged() {
        val data = parcelInfoReply.data
        Debug.Printf("ParcelInfo: loadable data %s", data)
        val activeAgentID = ActivityUtils.getActiveAgentID(arguments)
        
        if (isAdded && data != null && activeAgentID != null) {
            ownerNameRetriever?.dispose()
            ownerNameRetriever = null
            ownerGroupNameRetriever?.dispose()
            ownerGroupNameRetriever = null
            
            val name = SLMessage.stringFromVariableOEM(data.Data_Field.Name)
            setTitle(name, null)
            parcelDetailsName?.text = name
            
            var desc = SLMessage.stringFromVariableOEM(data.Data_Field.Desc).trim()
            if (Strings.isNullOrEmpty(desc)) {
                desc = getString(R.string.asset_no_description)
            }
            parcelDetailsDescription?.text = desc
            
            Debug.Printf("ParcelInfo: ownerID = %s", data.Data_Field.OwnerID)
            
            if (UUIDPool.ZeroUUID == data.Data_Field.OwnerID) {
                parcelOwnerName?.setText(R.string.group_owned)
                parcelOwnerPic?.visibility = View.GONE
            } else {
                // Initialize retrievers
                ownerNameRetriever = ChatterNameRetriever(ChatterID.getUserChatterID(activeAgentID, data.Data_Field.OwnerID), this, UIThreadExecutor.getSerialInstance())
                ownerGroupNameRetriever = ChatterNameRetriever(ChatterID.getGroupChatterID(activeAgentID, data.Data_Field.OwnerID), this, UIThreadExecutor.getSerialInstance())
            }
            
            parcelImageView?.setAssetID(data.Data_Field.SnapshotID)
            parcelSimName?.text = SLMessage.stringFromVariableOEM(data.Data_Field.SimName)
            
            parcelLocation?.text = getString(R.string.parcel_location_format, 
                data.Data_Field.GlobalX % 256.0f, 
                data.Data_Field.GlobalY % 256.0f, 
                data.Data_Field.GlobalZ)
        }
    }

    private fun onParcelOwnerProfileClick() {
        val activeAgentID = ActivityUtils.getActiveAgentID(arguments)
        val data = parcelInfoReply.data
        
        if (activeAgentID != null && data != null) {
            val activity = activity
            if (activity != null) {
                if (ownerGroupNameRetriever?.resolvedName != null) {
                    DetailsActivity.showEmbeddedDetails(activity, GroupProfileFragment::class.java, GroupProfileFragment.makeSelection(ownerGroupNameRetriever!!.chatterID))
                } else if (ownerNameRetriever?.resolvedName == null) {
                    // Fallback to user profile with basic ID if names not loaded or failed
                    DetailsActivity.showEmbeddedDetails(activity, UserProfileFragment::class.java, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(activeAgentID, data.Data_Field.OwnerID)))
                } else {
                    DetailsActivity.showEmbeddedDetails(activity, UserProfileFragment::class.java, UserProfileFragment.makeSelection(ownerNameRetriever!!.chatterID))
                }
            }
        }
    }

    private fun onParcelTeleportButton() {
        val userManager = ActivityUtils.getUserManager(arguments)
        val data = parcelInfoReply.data
        
        if (data != null && userManager != null) {
            val targetPos = LLVector3(data.Data_Field.GlobalX, data.Data_Field.GlobalY, data.Data_Field.GlobalZ)
            
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(getString(R.string.teleport_parcel_confirm_title))
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    val activeAgentCircuit = userManager.getActiveAgentCircuit()
                    if (activeAgentCircuit != null) {
                        TeleportProgressDialog(context, userManager, R.string.teleporting_progress_message).show()
                        activeAgentCircuit.TeleportToGlobalPosition(targetPos)
                    }
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            builder.create().show()
        }
    }

    override fun onStart() {
        super.onStart()
        val parcelIdStr = arguments?.getString(PARCEL_UUID_KEY)
        showParcelInfo(UUIDPool.getUUID(parcelIdStr))
    }

    override fun onStop() {
        loadableMonitor.unsubscribeAll()
        ownerNameRetriever?.dispose()
        ownerNameRetriever = null
        ownerGroupNameRetriever?.dispose()
        ownerGroupNameRetriever = null
        
        parcelOwnerPic?.setChatterID(null, null)
        parcelImageView?.setAssetID(null)
        
        super.onStop()
    }

    override fun setFragmentArgs(intent: Intent?, bundle: Bundle?) {
        if (bundle != null) {
            arguments?.putAll(bundle)
        }
        if (isFragmentStarted) {
            val parcelIdStr = bundle?.getString(PARCEL_UUID_KEY)
            showParcelInfo(UUIDPool.getUUID(parcelIdStr))
        }
    }
}
