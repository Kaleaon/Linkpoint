package com.linkpoint.cloud

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
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            Debug.Printf("LinkpointCloud: bound to local service")
            serviceMessenger = Messenger(iBinder)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            serviceMessenger = null
        }
    }
    
    private var serviceMessenger: Messenger? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Debug.Printf("LinkpointCloud: got result code: %d", resultCode)
        
        when (requestCode) {
            RESOLVE_CONNECTION_REQUEST_CODE -> {
                serviceMessenger?.let { messenger ->
                    try {
                        val messageWhat = if (resultCode == RESULT_OK) 101 else 102
                        messenger.send(Message.obtain(null, messageWhat))
                    } catch (e: RemoteException) {
                        Debug.Warning(e)
                    }
                    
                    Debug.Printf("LinkpointCloud: unbinding from local service")
                    serviceMessenger = null
                    unbindService(serviceConnection)
                }
                finish()
            }
            
            RESOLVE_RESOLVABLE_REQUEST_CODE -> {
                val uuidString = intent.getStringExtra(RESOLVABLE_ERROR_TAG)
                val uuid = UUID.fromString(uuidString)
                val errorResolutionTracker = ErrorResolutionTracker.getInstance()
                
                if (errorResolutionTracker != null) {
                    val resolvableError = errorResolutionTracker.getError(uuid)
                    if (resolvableError != null) {
                        val resolved = resultCode == RESULT_OK
                        errorResolutionTracker.clearError(uuid, resolved)
                    }
                    errorResolutionTracker.clearNotification()
                }
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val intent = intent
        
        when {
            intent.hasExtra(CONNECTION_RESULT_TAG) -> {
                Debug.Printf("LinkpointCloud: binding to local service")
                
                if (serviceMessenger == null && !bindService(Intent(this, DriveSyncService::class.java), serviceConnection, 0)) {
                    finish()
                    return
                }
                
                val connectionResult = intent.getParcelableExtra<ConnectionResult>(CONNECTION_RESULT_TAG)
                try {
                    connectionResult?.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE)
                } catch (e: IntentSender.SendIntentException) {
                    Debug.Printf("ahhhh on connection failed completely %s", e.message)
                    Debug.Warning(e)
                }
            }
            
            intent.hasExtra(RESOLVABLE_ERROR_TAG) -> {
                val uuidString = intent.getStringExtra(RESOLVABLE_ERROR_TAG)
                val uuid = UUID.fromString(uuidString)
                val errorResolutionTracker = ErrorResolutionTracker.getInstance()
                
                if (errorResolutionTracker != null) {
                    val resolvableError = errorResolutionTracker.getError(uuid)
                    
                    if (resolvableError != null) {
                        if (resolvableError.status.hasResolution()) {
                            try {
                                resolvableError.status.startResolutionForResult(this, RESOLVE_RESOLVABLE_REQUEST_CODE)
                                return
                            } catch (e: IntentSender.SendIntentException) {
                                Debug.Warning(e)
                                finish()
                                return
                            }
                        }
                        errorResolutionTracker.clearError(uuid, true)
                        errorResolutionTracker.clearNotification()
                    }
                }
                finish()
            }
            
            else -> finish()
        }
    }

    override fun onDestroy() {
        Debug.Printf("LinkpointCloud: destroyed resolution activity")
        
        serviceMessenger?.let {
            Debug.Printf("LinkpointCloud: unbinding from local service")
            serviceMessenger = null
            unbindService(serviceConnection)
        }
        
        super.onDestroy()
    }

    companion object {
        private const val CONNECTION_RESULT_TAG = "connectionResult"
        private const val RESOLVABLE_ERROR_TAG = "resolvableError"
        private const val RESOLVE_CONNECTION_REQUEST_CODE = 1
        private const val RESOLVE_RESOLVABLE_REQUEST_CODE = 2

        @JvmStatic
        fun getResolvableErrorIntent(context: Context, uuid: UUID): Intent {
            return Intent(context, ConnectionResolutionActivity::class.java).apply {
                putExtra(RESOLVABLE_ERROR_TAG, uuid.toString())
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }

        @JvmStatic
        fun startForConnectionResolution(context: Context, connectionResult: ConnectionResult) {
            val intent = Intent(context, ConnectionResolutionActivity::class.java).apply {
                putExtra(CONNECTION_RESULT_TAG, connectionResult)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}