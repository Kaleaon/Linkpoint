package com.lumiyaviewer.lumiya.ui.objects;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.objects.PayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
/* loaded from: classes.dex */
public class ObjectPayDialog {
    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectPayDialog_1356  reason: not valid java name */
    public static /* synthetic */ void m688lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectPayDialog_1356(final AlertDialog alertDialog, PayInfo payInfo, Context context, final UserManager userManager, final SLObjectProfileData sLObjectProfileData, final DialogInterface dialogInterface) {
        alertDialog.findViewById(R.id.object_pay_cancel).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$X9q_n5C700PWS1S1Fm8NW-TXuec
            private final /* synthetic */ void $m$0(View view) {
                ((DialogInterface) dialogInterface).dismiss();
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        int[] iArr = {R.id.object_pay_button1, R.id.object_pay_button2, R.id.object_pay_button3, R.id.object_pay_button4};
        ImmutableList<Integer> payPrices = payInfo.payPrices();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= iArr.length) {
                break;
            }
            int intValue = (payPrices == null || i2 > payPrices.size()) ? -1 : payPrices.get(i2).intValue();
            final int defaultPayPrice = intValue == -2 ? payInfo.defaultPayPrice() : intValue;
            if (defaultPayPrice <= 0) {
                alertDialog.findViewById(iArr[i2]).setVisibility(8);
                alertDialog.findViewById(iArr[i2]).setTag(R.id.object_pay_price_tag, 0);
            } else {
                ((Button) alertDialog.findViewById(iArr[i2])).setText(String.format(context.getString(R.string.pay_button_format), Integer.valueOf(defaultPayPrice)));
                alertDialog.findViewById(iArr[i2]).setVisibility(0);
                alertDialog.findViewById(iArr[i2]).setTag(R.id.object_pay_price_tag, Integer.valueOf(defaultPayPrice));
                alertDialog.findViewById(iArr[i2]).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$X9q_n5C700PWS1S1Fm8NW-TXuec.3
                    private final /* synthetic */ void $m$0(View view) {
                        ObjectPayDialog.m690lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectPayDialog_2986((UserManager) userManager, (SLObjectProfileData) sLObjectProfileData, defaultPayPrice, (DialogInterface) dialogInterface, view);
                    }

                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        $m$0(view);
                    }
                });
            }
            i = i2 + 1;
        }
        if (payInfo.defaultPayPrice() != -1) {
            if (((EditText) alertDialog.findViewById(R.id.object_pay_amount)).getText().toString().equals("")) {
                if (payInfo.defaultPayPrice() > 0) {
                    ((EditText) alertDialog.findViewById(R.id.object_pay_amount)).setText(context.getString(R.string.object_pay_amount_format, Integer.valueOf(payInfo.defaultPayPrice())));
                } else {
                    ((EditText) alertDialog.findViewById(R.id.object_pay_amount)).setText("");
                }
            }
            alertDialog.findViewById(R.id.object_normal_pay_layout).setVisibility(0);
        } else {
            alertDialog.findViewById(R.id.object_normal_pay_layout).setVisibility(8);
        }
        alertDialog.findViewById(R.id.object_pay_button).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$X9q_n5C700PWS1S1Fm8NW-TXuec.1
            private final /* synthetic */ void $m$0(View view) {
                ObjectPayDialog.m691lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectPayDialog_4340((AlertDialog) alertDialog, (UserManager) userManager, (SLObjectProfileData) sLObjectProfileData, (DialogInterface) dialogInterface, view);
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectPayDialog_2986  reason: not valid java name */
    public static /* synthetic */ void m690lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectPayDialog_2986(UserManager userManager, SLObjectProfileData sLObjectProfileData, int i, DialogInterface dialogInterface, View view) {
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (activeAgentCircuit != null) {
            activeAgentCircuit.getModules().financialInfo.DoPayObject(sLObjectProfileData.objectUUID(), i);
        }
        dialogInterface.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectPayDialog_4340  reason: not valid java name */
    public static /* synthetic */ void m691lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectPayDialog_4340(AlertDialog alertDialog, UserManager userManager, SLObjectProfileData sLObjectProfileData, DialogInterface dialogInterface, View view) {
        try {
            int parseInt = Integer.parseInt(((EditText) alertDialog.findViewById(R.id.object_pay_amount)).getText().toString());
            SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null) {
                activeAgentCircuit.getModules().financialInfo.DoPayObject(sLObjectProfileData.objectUUID(), parseInt);
            }
            dialogInterface.dismiss();
        } catch (Exception e) {
            Debug.Warning(e);
        }
    }

    public static void show(final Context context, final UserManager userManager, final SLObjectProfileData sLObjectProfileData) {
        final PayInfo payInfo = sLObjectProfileData.payInfo();
        if (payInfo != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.object_pay_dialog_caption, sLObjectProfileData.name().or((Optional<String>) context.getString(R.string.name_loading_title))));
            builder.setCancelable(true);
            builder.setView(R.layout.object_pay_dialog);
            final AlertDialog create = builder.create();
            create.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$X9q_n5C700PWS1S1Fm8NW-TXuec.2
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface) {
                    ObjectPayDialog.m688lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectPayDialog_1356((AlertDialog) create, (PayInfo) payInfo, (Context) context, (UserManager) userManager, (SLObjectProfileData) sLObjectProfileData, dialogInterface);
                }

                @Override // android.content.DialogInterface.OnShowListener
                public final void onShow(DialogInterface dialogInterface) {
                    $m$0(dialogInterface);
                }
            });
            create.show();
        }
    }
}
