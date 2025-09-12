// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objects;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.types.EDeRezDestination;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

public class ObjectDerezDialog
{
    public static final class DerezAction extends Enum
    {

        private static final DerezAction $VALUES[];
        public static final DerezAction Delete;
        public static final DerezAction Take;
        public static final DerezAction TakeCopy;
        public final EDeRezDestination deRezDestination;
        public final int derezQuestionId;

        public static DerezAction valueOf(String s)
        {
            return (DerezAction)Enum.valueOf(com/lumiyaviewer/lumiya/ui/objects/ObjectDerezDialog$DerezAction, s);
        }

        public static DerezAction[] values()
        {
            return $VALUES;
        }

        static 
        {
            Take = new DerezAction("Take", 0, 0x7f0900e3, EDeRezDestination.DRD_TAKE_INTO_AGENT_INVENTORY);
            TakeCopy = new DerezAction("TakeCopy", 1, 0x7f0900e4, EDeRezDestination.DRD_ACQUIRE_TO_AGENT_INVENTORY);
            Delete = new DerezAction("Delete", 2, 0x7f0900e2, EDeRezDestination.DRD_TRASH);
            $VALUES = (new DerezAction[] {
                Take, TakeCopy, Delete
            });
        }

        private DerezAction(String s, int i, int j, EDeRezDestination ederezdestination)
        {
            super(s, i);
            derezQuestionId = j;
            deRezDestination = ederezdestination;
        }
    }


    public ObjectDerezDialog()
    {
    }

    public static void askForObjectDerez(Context context, DerezAction derezaction, UUID uuid, int i)
    {
        int j = derezaction.derezQuestionId;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(context.getString(j)).setCancelable(true).setPositiveButton("Yes", new android.content.DialogInterface.OnClickListener(uuid, i, derezaction) {

            final UUID val$agentID;
            final DerezAction val$derezAction;
            final int val$localID;

            public void onClick(DialogInterface dialoginterface, int k)
            {
                dialoginterface.dismiss();
                dialoginterface = UserManager.getUserManager(agentID);
                if (dialoginterface != null)
                {
                    dialoginterface = dialoginterface.getActiveAgentCircuit();
                    if (dialoginterface != null)
                    {
                        dialoginterface.DerezObject(localID, derezAction.deRezDestination);
                    }
                }
            }

            
            {
                agentID = uuid;
                localID = i;
                derezAction = derezaction;
                super();
            }
        }).setNegativeButton("No", new android.content.DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialoginterface, int k)
            {
                dialoginterface.cancel();
            }

        });
        builder.create().show();
    }
}
