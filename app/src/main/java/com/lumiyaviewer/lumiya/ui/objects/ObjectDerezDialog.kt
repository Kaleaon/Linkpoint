package com.lumiyaviewer.lumiya.ui.objects

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.types.EDeRezDestination
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID
import androidx.annotation.NonNull

class ObjectDerezDialog {

    enum DerezAction {
        Take(R.string.derez_confirm_take, EDeRezDestination.DRD_TAKE_INTO_AGENT_INVENTORY),
        TakeCopy(R.string.derez_confirm_take_copy, EDeRezDestination.DRD_ACQUIRE_TO_AGENT_INVENTORY),
        Delete(R.string.derez_confirm_delete, EDeRezDestination.DRD_TRASH)
        
        EDeRezDestination deRezDestination
        Int derezQuestionId

        private DerezAction(Int i, EDeRezDestination eDeRezDestination) {
            this.derezQuestionId = i
            this.deRezDestination = eDeRezDestination
        }
    }

    Unit askForObjectDerez(Context context, @NonNull DerezAction derezAction, UUID uuid, Int i) {
        Int i2 = derezAction.derezQuestionId
        AlertDialog.Builder builder = AlertDialog.Builder(context)
        builder.setMessage(context.getString(i2)).setCancelable(true).setPositiveButton("Yes", DialogInterface.OnClickListener() {
            Unit onClick(DialogInterface dialogInterface, Int i) {
                SLAgentCircuit activeAgentCircuit
                dialogInterface.dismiss()
                UserManager userManager = UserManager.getUserManager(uuid)
                if (userManager != null && (activeAgentCircuit = userManager.getActiveAgentCircuit()) != null) {
                    activeAgentCircuit.DerezObject(i, derezAction.deRezDestination)
                }
            }
        }).setNegativeButton("No", DialogInterface.OnClickListener() {
            Unit onClick(DialogInterface dialogInterface, Int i) {
                dialogInterface.cancel()
            }
        builder.create().show()
    }
}
