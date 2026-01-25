package com.linkpoint.ui.objects

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.linkpoint.R
import com.linkpoint.objects.SceneObject
import java.util.*

/**
 * Dialog showing object properties
 */
class ObjectPropertiesDialog : DialogFragment() {

    private lateinit var obj: SceneObject

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        obj = requireArguments().getParcelable<SceneObject>("object")
            ?: throw IllegalArgumentException("SceneObject argument is required")

        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_object_properties, null)

        setupView(view)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.object_properties)
            .setView(view)
            .setPositiveButton(R.string.close) { _, _ -> dismiss() }
            .setNeutralButton(R.string.edit) { _, _ ->
                // Open edit mode
                onEditRequested()
            }
            .create()
    }

    private fun setupView(view: View) {
        view.findViewById<TextView>(R.id.obj_name).text = obj.name
        view.findViewById<TextView>(R.id.obj_description).text = 
            obj.description.ifEmpty { getString(R.string.no_description) }
        view.findViewById<TextView>(R.id.obj_position).text = 
            "X: ${"%.2f".format(obj.position.x)}, Y: ${"%.2f".format(obj.position.y)}, Z: ${"%.2f".format(obj.position.z)}"
        view.findViewById<TextView>(R.id.obj_rotation).text = 
            "X: ${"%.2f".format(obj.rotation.x)}, Y: ${"%.2f".format(obj.rotation.y)}, Z: ${"%.2f".format(obj.rotation.z)}, W: ${"%.2f".format(obj.rotation.w)}"
        view.findViewById<TextView>(R.id.obj_scale).text = 
            "X: ${"%.2f".format(obj.scale.x)}, Y: ${"%.2f".format(obj.scale.y)}, Z: ${"%.2f".format(obj.scale.z)}"
        view.findViewById<TextView>(R.id.obj_full_id).text = obj.fullId.toString()
        view.findViewById<TextView>(R.id.obj_owner_id).text = obj.ownerId.toString()
        
        // Flags
        val flagsText = buildString {
            if (obj.updateFlags and com.linkpoint.objects.ObjectManager.FLAG_PHANTOM != 0) append("Phantom, ")
            if (obj.updateFlags and com.linkpoint.objects.ObjectManager.FLAG_USE_PHYSICS != 0) append("Physics, ")
            if (obj.updateFlags and com.linkpoint.objects.ObjectManager.FLAG_SCRIPTED != 0) append("Scripted, ")
            if (obj.updateFlags and com.linkpoint.objects.ObjectManager.FLAG_INVENTORY_EMPTY == 0) append("Has Inventory, ")
        }.dropLastWhile { it == ',' || it == ' ' }
        
        view.findViewById<TextView>(R.id.obj_flags).text = 
            flagsText.ifEmpty { "None" }
    }

    private fun onEditRequested() {
        // Notify parent to open edit mode
        (parentFragment as? ObjectPropertiesDialog.Listener)?.onObjectEditRequested(obj)
    }

    interface Listener {
        fun onObjectEditRequested(obj: SceneObject)
    }

    companion object {
        fun newInstance(obj: SceneObject): ObjectPropertiesDialog {
            return ObjectPropertiesDialog().apply {
                arguments = Bundle().apply {
                    putParcelable("object", obj)
                }
            }
        }
    }
}