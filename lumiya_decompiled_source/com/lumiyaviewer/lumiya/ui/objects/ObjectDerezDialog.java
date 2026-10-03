package com.lumiyaviewer.lumiya.ui.objects;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.types.EDeRezDestination;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;
/* loaded from: classes.dex */
public class ObjectDerezDialog {

    /* loaded from: classes.dex */
    public enum DerezAction {
        Take(R.string.derez_confirm_take, EDeRezDestination.DRD_TAKE_INTO_AGENT_INVENTORY),
        TakeCopy(R.string.derez_confirm_take_copy, EDeRezDestination.DRD_ACQUIRE_TO_AGENT_INVENTORY),
        Delete(R.string.derez_confirm_delete, EDeRezDestination.DRD_TRASH);
        
        public final EDeRezDestination deRezDestination;
        public final int derezQuestionId;

        DerezAction(int i, EDeRezDestination eDeRezDestination) {
            this.derezQuestionId = i;
            this.deRezDestination = eDeRezDestination;
        }

        /* renamed from: values  reason: to resolve conflict with enum method */
        public static DerezAction[] valuesCustom() {
            return values();
        }
    }

    public static void askForObjectDerez(Context context, @Nonnull final DerezAction derezAction, final UUID uuid, final int i) {
        int i2 = derezAction.derezQuestionId;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(i2)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.objects.ObjectDerezDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i3) {
                SLAgentCircuit activeAgentCircuit;
                dialogInterface.dismiss();
                UserManager userManager = UserManager.getUserManager(uuid);
                if (userManager == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null) {
                    return;
                }
                activeAgentCircuit.DerezObject(i, derezAction.deRezDestination);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.objects.ObjectDerezDialog.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i3) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }
}
