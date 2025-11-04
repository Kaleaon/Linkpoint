// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.objects;

import android.content.DialogInterface$OnShowListener;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import android.view.View;
import com.google.common.collect.ImmutableList;
import android.widget.EditText;
import android.widget.Button;
import android.view.View$OnClickListener;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.objects.PayInfo;
import android.support.v7.app.AlertDialog;

public class ObjectPayDialog
{
    public static void show(final Context context, final UserManager userManager, final SLObjectProfileData slObjectProfileData) {
        final PayInfo payInfo = slObjectProfileData.payInfo();
        if (payInfo != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(2131296833, new Object[] { slObjectProfileData.name().or(context.getString(2131296712)) }));
            builder.setCancelable(true);
            builder.setView(2130968696);
            final AlertDialog create = builder.create();
            create.setOnShowListener((DialogInterface$OnShowListener)new _$Lambda$X9q_n5C700PWS1S1Fm8NW_TXuec$2(create, payInfo, context, userManager, slObjectProfileData));
            create.show();
        }
    }
}
