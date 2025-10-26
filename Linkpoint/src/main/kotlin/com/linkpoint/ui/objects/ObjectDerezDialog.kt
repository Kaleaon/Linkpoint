package com.linkpoint.ui.objects

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.linkpoint.R
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.types.EDeRezDestination
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull

class ObjectDerezDialog {

    enum class DerezAction {
        Take(R.string.derez_confirm_take, EDeRezDestination.DRD_TAKE_INTO_AGENT_INVENTORY),
        TakeCopy(R.string.derez_confirm_take_copy, EDeRezDestination.DRD_ACQUIRE_TO_AGENT_INVENTORY),
        Delete(R.string.derez_confirm_delete, EDeRezDestination.DRD_TRASH)
        
        val EDeRezDestination deRezDestination
        val Int derezQuestionId

        private DerezAction(Int i, EDeRezDestination eDeRezDestination) {
            this.derezQuestionId = i
            this.deRezDestination = eDeRezDestination
        }
    }

    @JvmStatic
     fun askForObjectDerez(context: Context, final DerezAction derezAction, final UUID uuid, final Int i) {
        val i2: Int = derezAction.derezQuestionId
        AlertDialog.Builder builder = AlertDialog.Builder(context)
        builder.setMessage(context.getString(i2)).setCancelable(true).setPositiveButton("Yes", DialogInterface.OnClickListener() {
            fun onClick(dialogInterface: DialogInterface, i: Int) {
                SLAgentCircuit activeAgentCircuit
                dialogInterface.dismiss()
                val userManager: UserManager = UserManager.getUserManager(uuid)
                if (userManager != null && (activeAgentCircuit = userManager.getActiveAgentCircuit()) != null) {
                    activeAgentCircuit.DerezObject(i, derezAction.deRezDestination)
                }
            }
        }).setNegativeButton("No", DialogInterface.OnClickListener() {
            fun onClick(dialogInterface: DialogInterface, i: Int) {
                dialogInterface.cancel()
            }
        builder.create().show()
    }
}
