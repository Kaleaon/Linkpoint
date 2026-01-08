package com.lumiyaviewer.lumiya.cloud

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import com.google.android.gms.common.ConnectionResult
import java.util.UUID

class ConnectionResolutionActivity : Activity() {
    private val CONNECTION_RESULT_TAG = "connectionResult"
    private val RESOLVABLE_ERROR_TAG = "resolvableError"
    private val RESOLVE_CONNECTION_REQUEST_CODE = 1
    private val RESOLVE_RESOLVABLE_REQUEST_CODE = 2
    
    private var serviceMessenger: Messenger? = null
    
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Debug.Printf("LumiyaCloud: bound to local service")
            serviceMessenger = Messenger(service)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceMessenger = null
        }
    }

    companion object {
        @JvmStatic
        fun getResolvableErrorIntent(context: Context, uuid: UUID): Intent {
            val intent = Intent(context, ConnectionResolutionActivity::class.java)
            intent.putExtra("resolvableError", uuid.toString())
            intent.flags = 0x10000000
            return intent
        }

        @JvmStatic
        fun startForConnectionResolution(context: Context, connectionResult: ConnectionResult) {
            val intent = Intent(context, ConnectionResolutionActivity::class.java)
            intent.putExtra("connectionResult", connectionResult)
            intent.flags = 0x10000000
            context.startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Debug.Printf("LumiyaCloud: got result code: %d", resultCode)
        
        when (requestCode) {
            RESOLVE_CONNECTION_REQUEST_CODE -> {
                if (serviceMessenger != null) {
                    try {
                        val msg = Message.obtain(null, if (resultCode == RESULT_OK) 101 else 102)
                        serviceMessenger?.send(msg)
                    } catch (e: RemoteException) {
                        Debug.Warning(e)
                    }
                    Debug.Printf("LumiyaCloud: unbinding from local service")
                    serviceMessenger = null
                    try {
                        unbindService(serviceConnection)
                    } catch(e: IllegalArgumentException) {}
                }
                finish()
            }
            RESOLVE_RESOLVABLE_REQUEST_CODE -> {
                val uuidStr = intent.getStringExtra(RESOLVABLE_ERROR_TAG) ?: return
                val uuid = try { UUID.fromString(uuidStr) } catch(e:Exception) { return }
                
                val tracker = ErrorResolutionTracker.getInstance()
                if (tracker != null) {
                    tracker.clearError(uuid, resultCode == RESULT_OK)
                    tracker.clearNotification()
                }
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (intent.hasExtra(CONNECTION_RESULT_TAG)) {
            Debug.Printf("LumiyaCloud: binding to local service")
            if (serviceMessenger == null) {
                val bindIntent = Intent(this, DriveSyncService::class.java)
                if (!bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE)) {
                    finish()
                    return
                }
            }
            
            val result = intent.getParcelableExtra<ConnectionResult>(CONNECTION_RESULT_TAG)
            try {
                result?.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE)
            } catch (e: IntentSender.SendIntentException) {
                Debug.Printf("ahhhh on connection failed completely %s", e.message ?: "")
                Debug.Warning(e)
            }
            return
        }
        
        if (intent.hasExtra(RESOLVABLE_ERROR_TAG)) {
            val uuidStr = intent.getStringExtra(RESOLVABLE_ERROR_TAG) ?: return
            val uuid = try { UUID.fromString(uuidStr) } catch(e:Exception) { return }
            
            val tracker = ErrorResolutionTracker.getInstance() ?: return
            val error = tracker.getError(uuid) ?: return
            
            if (error.status.hasResolution()) {
                try {
                    error.status.startResolutionForResult(this, RESOLVE_RESOLVABLE_REQUEST_CODE)
                } catch (e: IntentSender.SendIntentException) {
                    Debug.Warning(e)
                    finish()
                }
                return
            }
            
            tracker.clearError(uuid, true)
            tracker.clearNotification()
            finish()
            return
        }
        
        finish()
    }

    override fun onDestroy() {
        Debug.Printf("LumiyaCloud: destroyed resolution activity")
        if (serviceMessenger != null) {
            Debug.Printf("LumiyaCloud: unbinding from local service")
            serviceMessenger = null
            try {
                unbindService(serviceConnection)
            } catch(e: IllegalArgumentException) {}
        }
        super.onDestroy()
    }
}
