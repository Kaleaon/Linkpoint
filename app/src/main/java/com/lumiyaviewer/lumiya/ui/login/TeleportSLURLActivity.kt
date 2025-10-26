package com.lumiyaviewer.lumiya.ui.login

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.lumiyaviewer.lumiya.GridConnectionService
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.SLGridConnection
import com.lumiyaviewer.lumiya.slproto.SLURL
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog
import java.util.UUID

class TeleportSLURLActivity : AppCompatActivity : View.OnClickListener {
    private SLURL slurl = null

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_login_TeleportSLURLActivity_4598  reason: not valid java name */
    /* synthetic */ Unit m638lambda$com_lumiyaviewer_lumiya_ui_login_TeleportSLURLActivity_4598(DialogInterface dialogInterface, Int i) {
        dialogInterface.cancel()
        finish()
    }

    Unit onClick(View view) {
        SLGridConnection gridConnection
        SLAgentCircuit activeAgentCircuit
        UserManager userManager = null
        switch (view.getId()) {
            case R.id.buttonTeleport:
                Boolean z = false
                if (!(this.slurl == null || (gridConnection = GridConnectionService.getGridConnection()) == null)) {
                    UUID activeAgentUUID = gridConnection.getActiveAgentUUID()
                    if (activeAgentUUID != null) {
                        userManager = UserManager.getUserManager(activeAgentUUID)
                    }
                    if (!(userManager == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null || !activeAgentCircuit.getModules().worldMap.TeleportToRegionByName(this.slurl.getLocationName(), this.slurl.getLocationX(), this.slurl.getLocationY(), this.slurl.getLocationZ()))) {
                        TeleportProgressDialog(this, userManager, R.string.teleporting_progress_message).show()
                        z = true
                    }
                }
                if (!z) {
                    AlertDialog.Builder(this).setMessage((Int) R.string.teleport_unable).setCancelable(true).setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) $Lambda$txy91ryZVkviKYu9VXLZHkYSvg0(this)).create().show()
                    return
                }
                return
            case R.id.buttonTeleportCancel:
                finish()
                return
            default:
                return
        }
    }

    @SuppressLint({"DefaultLocale"})
    Unit onCreate(Bundle bundle) {
        SLAgentCircuit agentCircuit
        super.onCreate(bundle)
        setContentView((Int) R.layout.teleport_slurl)
        try {
            this.slurl = SLURL(getIntent())
        } catch (Exception e) {
        }
        try {
            SLGridConnection gridConnection = GridConnectionService.getGridConnection()
            if (gridConnection == null || gridConnection.getConnectionState() != SLGridConnection.ConnectionState.Connected || (agentCircuit = gridConnection.getAgentCircuit()) == null) {
                z = false
            } else if (!agentCircuit.getModules().rlvController.canTeleportToLocation()) {
                finish()
                return
            } else {
                z = true
            }
            if (!z || this.slurl == null) {
                Intent intent = Intent(getIntent())
                intent.setClass(this, LoginActivity.class)
                startActivity(intent)
                finish()
                return
            }
            findViewById(R.id.buttonTeleport).setOnClickListener(this)
            findViewById(R.id.buttonTeleportCancel).setOnClickListener(this)
            ((TextView) findViewById(R.id.teleportTargetName)).setText(this.slurl.getLocationName())
            ((TextView) findViewById(R.id.teleportTargetCoords)).setText(String.format("(%d, %d, %d)", Any[]{Int.valueOf(this.slurl.getLocationX()), Int.valueOf(this.slurl.getLocationY()), Int.valueOf(this.slurl.getLocationZ())}))
        } catch (Exception e2) {
            finish()
        }
    }
}
