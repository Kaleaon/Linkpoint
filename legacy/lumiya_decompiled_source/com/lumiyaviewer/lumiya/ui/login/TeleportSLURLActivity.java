package com.lumiyaviewer.lumiya.ui.login;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.SLURL;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog;
import java.util.UUID;
/* loaded from: classes.dex */
public class TeleportSLURLActivity extends AppCompatActivity implements View.OnClickListener {
    private SLURL slurl = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_login_TeleportSLURLActivity_4598  reason: not valid java name */
    public /* synthetic */ void m649xe44220a6(DialogInterface dialogInterface, int i) {
        dialogInterface.cancel();
        finish();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        SLGridConnection gridConnection;
        SLAgentCircuit activeAgentCircuit;
        switch (view.getId()) {
            case R.id.buttonTeleport /* 2131755665 */:
                boolean z = false;
                if (this.slurl != null && (gridConnection = GridConnectionService.getGridConnection()) != null) {
                    UUID activeAgentUUID = gridConnection.getActiveAgentUUID();
                    UserManager userManager = activeAgentUUID != null ? UserManager.getUserManager(activeAgentUUID) : null;
                    if (userManager != null && (activeAgentCircuit = userManager.getActiveAgentCircuit()) != null && activeAgentCircuit.getModules().worldMap.TeleportToRegionByName(this.slurl.getLocationName(), this.slurl.getLocationX(), this.slurl.getLocationY(), this.slurl.getLocationZ())) {
                        new TeleportProgressDialog(this, userManager, R.string.teleporting_progress_message).show();
                        z = true;
                    }
                }
                if (z) {
                    return;
                }
                new AlertDialog.Builder(this).setMessage(R.string.teleport_unable).setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.login.-$Lambda$txy91ryZVkviKYu9VXLZHkYSvg0
                    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                        ((TeleportSLURLActivity) this).m649xe44220a6(dialogInterface, i);
                    }

                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        $m$0(dialogInterface, i);
                    }
                }).create().show();
                return;
            case R.id.buttonTeleportCancel /* 2131755666 */:
                finish();
                return;
            default:
                return;
        }
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    @SuppressLint({"DefaultLocale"})
    public void onCreate(Bundle bundle) {
        boolean z;
        SLAgentCircuit agentCircuit;
        super.onCreate(bundle);
        setContentView(R.layout.teleport_slurl);
        try {
            this.slurl = new SLURL(getIntent());
        } catch (Exception e) {
        }
        try {
            SLGridConnection gridConnection = GridConnectionService.getGridConnection();
            if (gridConnection == null || gridConnection.getConnectionState() != SLGridConnection.ConnectionState.Connected || (agentCircuit = gridConnection.getAgentCircuit()) == null) {
                z = false;
            } else if (!agentCircuit.getModules().rlvController.canTeleportToLocation()) {
                finish();
                return;
            } else {
                z = true;
            }
            if (!z || this.slurl == null) {
                Intent intent = new Intent(getIntent());
                intent.setClass(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
            findViewById(R.id.buttonTeleport).setOnClickListener(this);
            findViewById(R.id.buttonTeleportCancel).setOnClickListener(this);
            ((TextView) findViewById(R.id.teleportTargetName)).setText(this.slurl.getLocationName());
            ((TextView) findViewById(R.id.teleportTargetCoords)).setText(String.format("(%d, %d, %d)", Integer.valueOf(this.slurl.getLocationX()), Integer.valueOf(this.slurl.getLocationY()), Integer.valueOf(this.slurl.getLocationZ())));
        } catch (Exception e2) {
            finish();
        }
    }
}
