// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.objects;

import com.lumiyaviewer.lumiya.slproto.types.EDeRezDestination;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.DialogInterface;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import java.util.UUID;
import javax.annotation.Nonnull;
import android.content.Context;

public class ObjectDerezDialog
{
    public static void askForObjectDerez(final Context context, @Nonnull final DerezAction derezAction, final UUID uuid, final int n) {
        final int derezQuestionId = derezAction.derezQuestionId;
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(context);
        alertDialog$Builder.setMessage((CharSequence)context.getString(derezQuestionId)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                dialogInterface.dismiss();
                final UserManager userManager = UserManager.getUserManager(uuid);
                if (userManager != null) {
                    final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
                    if (activeAgentCircuit != null) {
                        activeAgentCircuit.DerezObject(n, derezAction.deRezDestination);
                    }
                }
            }
        }).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                dialogInterface.cancel();
            }
        });
        alertDialog$Builder.create().show();
    }
    
    public enum DerezAction
    {
        Delete("Delete", 2, 2131296482, EDeRezDestination.DRD_TRASH), 
        Take("Take", 0, 2131296483, EDeRezDestination.DRD_TAKE_INTO_AGENT_INVENTORY), 
        TakeCopy("TakeCopy", 1, 2131296484, EDeRezDestination.DRD_ACQUIRE_TO_AGENT_INVENTORY);
        
        public final EDeRezDestination deRezDestination;
        public final int derezQuestionId;
        
        private DerezAction(final String name, final int ordinal, final int derezQuestionId, final EDeRezDestination deRezDestination) {
            this.derezQuestionId = derezQuestionId;
            this.deRezDestination = deRezDestination;
        }
    }
}
