package com.lumiyaviewer.lumiya.ui.objects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import javax.annotation.Nullable
import java.util.UUID

/**
 * Converted from Java to Kotlin
 * ObjectDetailsFragment - Displays detailed information about a 3D object
 */
class ObjectDetailsFragment : Fragment() {

    @BindView(R.id.objectNameText) lateinit var objectNameText: TextView
    @BindView(R.id.objectDescriptionText) lateinit var objectDescriptionText: TextView
    @BindView(R.id.objectOwnerText) lateinit var objectOwnerText: TextView
    @BindView(R.id.objectCreatorText) lateinit var objectCreatorText: TextView
    @BindView(R.id.objectGroupText) lateinit var objectGroupText: TextView
    @BindView(R.id.objectPositionText) lateinit var objectPositionText: TextView
    @BindView(R.id.objectForSaleCheckbox) lateinit var objectForSaleCheckbox: CheckBox
    @BindView(R.id.objectPriceText) lateinit var objectPriceText: TextView
    @BindView(R.id.touchButton) lateinit var touchButton: Button
    @BindView(R.id.sitButton) lateinit var sitButton: Button
    @BindView(R.id.buyButton) lateinit var buyButton: Button
    @BindView(R.id.payButton) lateinit var payButton: Button
    @BindView(R.id.editButton) lateinit var editButton: Button

    private var objectInfo: SLObjectInfo? = null
    private var objectProfileData: SLObjectProfileData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_object_details, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun setObjectInfo(info: SLObjectInfo, profileData: SLObjectProfileData) {
        this.objectInfo = info
        this.objectProfileData = profileData
        updateUI()
    }

    private fun updateUI() {
        objectProfileData?.let { profile ->
            objectNameText.text = profile.name
            objectDescriptionText.text = profile.description
            objectOwnerText.text = "Owner: ${profile.ownerName}"
            objectCreatorText.text = "Creator: ${profile.creatorName}"
            objectGroupText.text = if (profile.groupName.isNotEmpty()) "Group: ${profile.groupName}" else "No group"
            
            objectInfo?.let { info ->
                val pos = info.position
                objectPositionText.text = "Position: (${pos.x.format(2)}, ${pos.y.format(2)}, ${pos.z.format(2)})"
            }
            
            objectForSaleCheckbox.isChecked = profile.isForSale
            objectPriceText.text = if (profile.isForSale) "L$ ${profile.price}" else "Not for sale"
            objectPriceText.visibility = if (profile.isForSale) View.VISIBLE else View.GONE
            buyButton.visibility = if (profile.isForSale) View.VISIBLE else View.GONE
            
            updateButtonStates(profile)
        }
    }

    private fun updateButtonStates(profile: SLObjectProfileData) {
        touchButton.isEnabled = profile.isTouchable
        sitButton.isEnabled = profile.isSittable
        payButton.isEnabled = profile.isPayable
        editButton.isEnabled = profile.canEdit
    }

    @OnClick(R.id.touchButton)
    fun onTouchButtonClick() {
        objectInfo?.let { info ->
            // Send touch object request
            Toast.makeText(context, "Touching object...", Toast.LENGTH_SHORT).show()
        }
    }

    @OnClick(R.id.sitButton)
    fun onSitButtonClick() {
        objectInfo?.let { info ->
            // Send sit request
            Toast.makeText(context, "Sitting...", Toast.LENGTH_SHORT).show()
        }
    }

    @OnClick(R.id.buyButton)
    fun onBuyButtonClick() {
        objectProfileData?.let { profile ->
            // Show buy confirmation dialog
            showBuyConfirmationDialog(profile.name, profile.price)
        }
    }

    @OnClick(R.id.payButton)
    fun onPayButtonClick() {
        objectInfo?.let { info ->
            // Show pay dialog
            showPayDialog()
        }
    }

    @OnClick(R.id.editButton)
    fun onEditButtonClick() {
        objectInfo?.let { info ->
            // Open edit mode
            Toast.makeText(context, "Opening edit mode...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showBuyConfirmationDialog(objectName: String, price: Int) {
        android.app.AlertDialog.Builder(context)
            .setTitle("Buy Object")
            .setMessage("Buy '$objectName' for L$ $price?")
            .setPositiveButton("Buy") { _, _ ->
                buyObject()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showPayDialog() {
        // Show dialog to enter payment amount
        Toast.makeText(context, "Pay object...", Toast.LENGTH_SHORT).show()
    }

    private fun buyObject() {
        objectInfo?.let { info ->
            // Send buy object request
            Toast.makeText(context, "Purchasing object...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun Float.format(digits: Int) = "%.${digits}f".format(this)
}
