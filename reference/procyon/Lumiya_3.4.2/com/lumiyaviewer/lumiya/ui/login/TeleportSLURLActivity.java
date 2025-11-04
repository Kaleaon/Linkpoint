// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.login;

import android.annotation.SuppressLint;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import android.content.DialogInterface$OnClickListener;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.GridConnectionService;
import android.view.View;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.slproto.SLURL;
import android.view.View$OnClickListener;
import android.support.v7.app.AppCompatActivity;

public class TeleportSLURLActivity extends AppCompatActivity implements View$OnClickListener
{
    private SLURL slurl;
    
    public TeleportSLURLActivity() {
        this.slurl = null;
    }
    
    public void onClick(final View view) {
        final UserManager userManager = null;
        switch (view.getId()) {
            case 2131755665: {
                int n2;
                final int n = n2 = 0;
                if (this.slurl != null) {
                    final SLGridConnection gridConnection = GridConnectionService.getGridConnection();
                    n2 = n;
                    if (gridConnection != null) {
                        final UUID activeAgentUUID = gridConnection.getActiveAgentUUID();
                        UserManager userManager2 = userManager;
                        if (activeAgentUUID != null) {
                            userManager2 = UserManager.getUserManager(activeAgentUUID);
                        }
                        n2 = n;
                        if (userManager2 != null) {
                            final SLAgentCircuit activeAgentCircuit = userManager2.getActiveAgentCircuit();
                            n2 = n;
                            if (activeAgentCircuit != null) {
                                n2 = n;
                                if (activeAgentCircuit.getModules().worldMap.TeleportToRegionByName(this.slurl.getLocationName(), this.slurl.getLocationX(), this.slurl.getLocationY(), this.slurl.getLocationZ())) {
                                    new TeleportProgressDialog((Context)this, userManager2, 2131297104).show();
                                    n2 = 1;
                                }
                            }
                        }
                    }
                }
                if (n2 == 0) {
                    new AlertDialog.Builder((Context)this).setMessage(2131297102).setCancelable(true).setPositiveButton("OK", (DialogInterface$OnClickListener)new _$Lambda$txy91ryZVkviKYu9VXLZHkYSvg0(this)).create().show();
                    break;
                }
                break;
            }
            case 2131755666: {
                this.finish();
                break;
            }
        }
    }
    
    @SuppressLint({ "DefaultLocale" })
    public void onCreate(final Bundle bundle) {
    Label_0075_Outer:
        while (true) {
            super.onCreate(bundle);
            this.setContentView(2130968744);
            while (true) {
            Label_0226:
                while (true) {
                    try {
                        this.slurl = new SLURL(this.getIntent());
                        try {
                            final SLGridConnection gridConnection = GridConnectionService.getGridConnection();
                            if (gridConnection == null || gridConnection.getConnectionState() != SLGridConnection.ConnectionState.Connected) {
                                break Label_0226;
                            }
                            final SLAgentCircuit agentCircuit = gridConnection.getAgentCircuit();
                            if (agentCircuit == null) {
                                break Label_0226;
                            }
                            if (!agentCircuit.getModules().rlvController.canTeleportToLocation()) {
                                this.finish();
                                return;
                            }
                            final int n = 1;
                            if (n == 0 || this.slurl == null) {
                                final Intent intent = new Intent(this.getIntent());
                                intent.setClass((Context)this, (Class)LoginActivity.class);
                                this.startActivity(intent);
                                this.finish();
                                return;
                            }
                        }
                        catch (final Exception ex) {
                            this.finish();
                            return;
                        }
                        this.findViewById(2131755665).setOnClickListener((View$OnClickListener)this);
                        this.findViewById(2131755666).setOnClickListener((View$OnClickListener)this);
                        this.findViewById(2131755663).setText((CharSequence)this.slurl.getLocationName());
                        this.findViewById(2131755664).setText((CharSequence)String.format("(%d, %d, %d)", this.slurl.getLocationX(), this.slurl.getLocationY(), this.slurl.getLocationZ()));
                        return;
                    }
                    catch (final Exception ex2) {
                        continue Label_0075_Outer;
                    }
                    break;
                }
                final int n = 0;
                continue;
            }
        }
    }
}
