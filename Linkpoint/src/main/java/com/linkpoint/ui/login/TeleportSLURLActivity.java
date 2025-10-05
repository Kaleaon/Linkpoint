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

public class TeleportSLURLActivity extends AppCompatActivity implements View.OnClickListener {
    private SLURL slurl = null;

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_login_TeleportSLURLActivity_4598  reason: not valid java name */
    public /* synthetic */ void m638lambda$com_lumiyaviewer_lumiya_ui_login_TeleportSLURLActivity_4598(DialogInterface dialogInterface, int i) {
        dialogInterface.cancel();
        finish();
    }

    public void onClick(View view) {
        SLGridConnection gridConnection;
        SLAgentCircuit activeAgentCircuit;
        UserManager userManager = null;
        switch (view.getId()) {
            case R.id.buttonTeleport:
                boolean z = false;
                if (!(this.slurl == null || (gridConnection = GridConnectionService.getGridConnection()) == null)) {
                    UUID activeAgentUUID = gridConnection.getActiveAgentUUID();
                    if (activeAgentUUID != null) {
                        userManager = UserManager.getUserManager(activeAgentUUID);
                    }
                    if (!(userManager == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null || !activeAgentCircuit.getModules().worldMap.TeleportToRegionByName(this.slurl.getLocationName(), this.slurl.getLocationX(), this.slurl.getLocationY(), this.slurl.getLocationZ()))) {
                        new TeleportProgressDialog(this, userManager, R.string.teleporting_progress_message).show();
                        z = true;
                    }
                }
                if (!z) {
                    new AlertDialog.Builder(this).setMessage((int) R.string.teleport_unable).setCancelable(true).setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new $Lambda$txy91ryZVkviKYu9VXLZHkYSvg0(this)).create().show();
                    return;
                }
                return;
            case R.id.buttonTeleportCancel:
                finish();
                return;
            default:
                return;
        }
    }

    @SuppressLint({"DefaultLocale"})
    public void onCreate(Bundle bundle) {
        SLAgentCircuit agentCircuit;
        super.onCreate(bundle);
        setContentView((int) R.layout.teleport_slurl);
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
            ((TextView) findViewById(R.id.teleportTargetCoords)).setText(String.format("(%d, %d, %d)", new Object[]{Integer.valueOf(this.slurl.getLocationX()), Integer.valueOf(this.slurl.getLocationY()), Integer.valueOf(this.slurl.getLocationZ())}));
        } catch (Exception e2) {
            finish();
        }
    }
}
