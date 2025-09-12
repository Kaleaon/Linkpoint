// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objects;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.finance.SLFinancialInfo;
import com.lumiyaviewer.lumiya.slproto.objects.PayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;

public class ObjectPayDialog
{

    public ObjectPayDialog()
    {
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_objects_ObjectPayDialog_1356(AlertDialog alertdialog, PayInfo payinfo, Context context, UserManager usermanager, SLObjectProfileData slobjectprofiledata, DialogInterface dialoginterface)
    {
        alertdialog.findViewById(0x7f10023d).setOnClickListener(new _2D_.Lambda.X9q_n5C700PWS1S1Fm8NW_2D_TXuec(dialoginterface));
        int ai[] = new int[4];
        ai[0] = 0x7f100224;
        ai[1] = 0x7f100225;
        ai[2] = 0x7f100226;
        ai[3] = 0x7f100227;
        ImmutableList immutablelist = payinfo.payPrices();
        int j = 0;
        while (j < ai.length) 
        {
            int i;
            if (immutablelist != null && j <= immutablelist.size())
            {
                i = ((Integer)immutablelist.get(j)).intValue();
            } else
            {
                i = -1;
            }
            if (i == -2)
            {
                i = payinfo.defaultPayPrice();
            }
            if (i <= 0)
            {
                alertdialog.findViewById(ai[j]).setVisibility(8);
                alertdialog.findViewById(ai[j]).setTag(0x7f100020, Integer.valueOf(0));
            } else
            {
                ((Button)alertdialog.findViewById(ai[j])).setText(String.format(context.getString(0x7f090263), new Object[] {
                    Integer.valueOf(i)
                }));
                alertdialog.findViewById(ai[j]).setVisibility(0);
                alertdialog.findViewById(ai[j]).setTag(0x7f100020, Integer.valueOf(i));
                alertdialog.findViewById(ai[j]).setOnClickListener(new _2D_.Lambda.X9q_n5C700PWS1S1Fm8NW_2D_TXuec._cls3(i, usermanager, slobjectprofiledata, dialoginterface));
            }
            j++;
        }
        if (payinfo.defaultPayPrice() != -1)
        {
            if (((EditText)alertdialog.findViewById(0x7f100221)).getText().toString().equals(""))
            {
                if (payinfo.defaultPayPrice() > 0)
                {
                    ((EditText)alertdialog.findViewById(0x7f100221)).setText(context.getString(0x7f09023d, new Object[] {
                        Integer.valueOf(payinfo.defaultPayPrice())
                    }));
                } else
                {
                    ((EditText)alertdialog.findViewById(0x7f100221)).setText("");
                }
            }
            alertdialog.findViewById(0x7f10021f).setVisibility(0);
        } else
        {
            alertdialog.findViewById(0x7f10021f).setVisibility(8);
        }
        alertdialog.findViewById(0x7f100222).setOnClickListener(new _2D_.Lambda.X9q_n5C700PWS1S1Fm8NW_2D_TXuec._cls1(alertdialog, usermanager, slobjectprofiledata, dialoginterface));
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_objects_ObjectPayDialog_1464(DialogInterface dialoginterface, View view)
    {
        dialoginterface.dismiss();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_objects_ObjectPayDialog_2986(UserManager usermanager, SLObjectProfileData slobjectprofiledata, int i, DialogInterface dialoginterface, View view)
    {
        usermanager = usermanager.getActiveAgentCircuit();
        if (usermanager != null)
        {
            usermanager.getModules().financialInfo.DoPayObject(slobjectprofiledata.objectUUID(), i);
        }
        dialoginterface.dismiss();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_objects_ObjectPayDialog_4340(AlertDialog alertdialog, UserManager usermanager, SLObjectProfileData slobjectprofiledata, DialogInterface dialoginterface, View view)
    {
        int i;
        try
        {
            i = Integer.parseInt(((EditText)alertdialog.findViewById(0x7f100221)).getText().toString());
            alertdialog = usermanager.getActiveAgentCircuit();
        }
        // Misplaced declaration of an exception variable
        catch (AlertDialog alertdialog)
        {
            Debug.Warning(alertdialog);
            return;
        }
        if (alertdialog == null)
        {
            break MISSING_BLOCK_LABEL_47;
        }
        alertdialog.getModules().financialInfo.DoPayObject(slobjectprofiledata.objectUUID(), i);
        dialoginterface.dismiss();
        return;
    }

    public static void show(Context context, UserManager usermanager, SLObjectProfileData slobjectprofiledata)
    {
        PayInfo payinfo = slobjectprofiledata.payInfo();
        if (payinfo != null)
        {
            Object obj = new android.support.v7.app.AlertDialog.Builder(context);
            ((android.support.v7.app.AlertDialog.Builder) (obj)).setTitle(context.getString(0x7f090241, new Object[] {
                slobjectprofiledata.name().or(context.getString(0x7f0901c8))
            }));
            ((android.support.v7.app.AlertDialog.Builder) (obj)).setCancelable(true);
            ((android.support.v7.app.AlertDialog.Builder) (obj)).setView(0x7f040078);
            obj = ((android.support.v7.app.AlertDialog.Builder) (obj)).create();
            ((AlertDialog) (obj)).setOnShowListener(new _2D_.Lambda.X9q_n5C700PWS1S1Fm8NW_2D_TXuec._cls2(obj, payinfo, context, usermanager, slobjectprofiledata));
            ((AlertDialog) (obj)).show();
        }
    }
}
