package com.lumiyaviewer.lumiya.ui.groups

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.slproto.groups.SLGroupInfo
import com.lumiyaviewer.lumiya.slproto.groups.SLGroupProfileData
import javax.annotation.Nullable

/**
 * Converted from Java to Kotlin
 * GroupMainProfileTab - Displays main profile information for a group
 */
class GroupMainProfileTab : Fragment() {

    @BindView(R.id.groupNameText) lateinit var groupNameText: TextView
    @BindView(R.id.groupCharterText) lateinit var groupCharterText: TextView
    @BindView(R.id.groupMemberCountText) lateinit var groupMemberCountText: TextView
    @BindView(R.id.groupFoundedDateText) lateinit var groupFoundedDateText: TextView
    @BindView(R.id.groupOpenEnrollmentCheckbox) lateinit var groupOpenEnrollmentCheckbox: CheckBox
    @BindView(R.id.groupMatureCheckbox) lateinit var groupMatureCheckbox: CheckBox
    @BindView(R.id.groupShowInSearchCheckbox) lateinit var groupShowInSearchCheckbox: CheckBox
    @BindView(R.id.groupInsigniaImageView) lateinit var groupInsigniaImageView: ImageView
    @BindView(R.id.groupJoinButton) lateinit var groupJoinButton: Button
    @BindView(R.id.groupLeaveButton) lateinit var groupLeaveButton: Button

    private var groupInfo: SLGroupInfo? = null
    private var groupProfileData: SLGroupProfileData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_group_main_profile, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        groupJoinButton.setOnClickListener {
            joinGroup()
        }
        
        groupLeaveButton.setOnClickListener {
            leaveGroup()
        }
    }

    fun setGroupInfo(info: SLGroupInfo, profileData: SLGroupProfileData) {
        this.groupInfo = info
        this.groupProfileData = profileData
        updateUI()
    }

    private fun updateUI() {
        groupProfileData?.let { profile ->
            groupNameText.text = profile.name
            groupCharterText.text = profile.charter
            groupMemberCountText.text = "Members: ${profile.memberCount}"
            groupFoundedDateText.text = "Founded: ${profile.foundedDate}"
            groupOpenEnrollmentCheckbox.isChecked = profile.isOpenEnrollment
            groupMatureCheckbox.isChecked = profile.isMature
            groupShowInSearchCheckbox.isChecked = profile.showInSearch
            
            // Update button visibility based on membership
            updateButtonVisibility(profile.isMember)
        }
    }

    private fun updateButtonVisibility(isMember: Boolean) {
        if (isMember) {
            groupJoinButton.visibility = View.GONE
            groupLeaveButton.visibility = View.VISIBLE
        } else {
            groupJoinButton.visibility = View.VISIBLE
            groupLeaveButton.visibility = View.GONE
        }
    }

    private fun joinGroup() {
        groupInfo?.let { info ->
            // Send join group request
            Toast.makeText(context, "Joining group...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun leaveGroup() {
        groupInfo?.let { info ->
            // Send leave group request
            Toast.makeText(context, "Leaving group...", Toast.LENGTH_SHORT).show()
        }
    }
}
