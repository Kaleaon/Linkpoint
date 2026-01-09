/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.ComponentName
 *  android.content.Context
 *  android.content.Intent
 *  android.content.IntentSender$SendIntentException
 *  android.content.ServiceConnection
 *  android.os.Bundle
 *  android.os.IBinder
 *  android.os.Message
 *  android.os.Messenger
 *  android.os.Parcelable
 *  android.os.RemoteException
 */
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
import android.os.Parcelable
import android.os.RemoteException
import com.google.android.gms.common.ConnectionResult
import com.linkpoint.cloud.Debug
import com.linkpoint.cloud.DriveSyncService
import com.linkpoint.cloud.ErrorResolutionTracker
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class ConnectionResolutionActivity
: Activity {
    private val CONNECTION_RESULT_TAG: String = "connectionResult"
    private val RESOLVABLE_ERROR_TAG: String = "resolvableError"
    private int RESOLVE_CONNECTION_REQUEST_CODE = 1
    private int RESOLVE_RESOLVABLE_REQUEST_CODE = 2
    private val serviceConnection: ServiceConnection = ServiceConnection(this){
        ConnectionResolutionActivity this$0
        {
            this.this$0 = connectionResolutionActivity
        }

        fun onServiceConnected(componentName: ComponentName, iBinder: IBinder): Unit {
            Debug.Printf("LumiyaCloud: bound to local service", Array<Object>(0))
            ConnectionResolutionActivity.access$002(this.this$0, Messenger(iBinder))
        }

        fun onServiceDisconnected(componentName: ComponentName): Unit {
            ConnectionResolutionActivity.access$002(this.this$0, null)
        }
    }
    @Nullable
    private var serviceMessenger: Messenger = null

    /* synthetic */ Messenger access$002(ConnectionResolutionActivity connectionResolutionActivity, Messenger messenger) {
        connectionResolutionActivity.serviceMessenger = messenger
        return messenger
    }

    fun getResolvableErrorIntent(Context context, @NonNull UUID uUID): Intent {
        context = Intent(context, ConnectionResolutionActivity.class)
        context.putExtra(RESOLVABLE_ERROR_TAG, uUID.toString())
        context.setFlags(0x10000000)
        return context
    }

    void startForConnectionResolution(Context context, @NonNull ConnectionResult connectionResult) {
        Intent intent = Intent(context, ConnectionResolutionActivity.class)
        intent.putExtra(CONNECTION_RESULT_TAG, (Parcelable)connectionResult)
        intent.setFlags(0x10000000)
        context.startActivity(intent)
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected fun onActivityResult(n: Int, n2: Int, object: Intent): Unit {
        void var3_8
        boolean bl = true
        Object var3_4 = null
        Debug.Printf("LumiyaCloud: got result code: %d", n2)
        switch (n) {
            case 1: {
                if (this.serviceMessenger != null) {
                    if (n2 == -1) {
                        try {
                            this.serviceMessenger.send(Message.obtain(null, (int)101))
                        }
                        catch (RemoteException remoteException) {
                            Debug.Warning(remoteException)
                        }
                    } else {
                        try {
                            this.serviceMessenger.send(Message.obtain(null, (int)102))
                        }
                        catch (RemoteException remoteException) {
                            Debug.Warning(remoteException)
                        }
                    }
                    Debug.Printf("LumiyaCloud: unbinding from local service", Array<Object>(0))
                    this.serviceMessenger = null
                    this.unbindService(this.serviceConnection)
                }
                this.finish()
            }
            default: {
                return
            }
            case 2: 
        }
        UUID uUID = UUID.fromString(this.getIntent().getStringExtra(RESOLVABLE_ERROR_TAG))
        ErrorResolutionTracker errorResolutionTracker = ErrorResolutionTracker.getInstance()
        if (errorResolutionTracker != null) {
            ErrorResolutionTracker.ResolvableError resolvableError = errorResolutionTracker.getError(uUID)
        }
        if (var3_8 != null) {
            if (n2 != -1) {
                bl = false
            }
            errorResolutionTracker.clearError(uUID, bl)
        }
        if (errorResolutionTracker != null) {
            errorResolutionTracker.clearNotification()
        }
        this.finish()
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected fun onCreate(object: Bundle): Unit {
        super.onCreate((Bundle)object)
        object = this.getIntent()
        if (object.hasExtra(CONNECTION_RESULT_TAG)) {
            Debug.Printf("LumiyaCloud: binding to local service", Array<Object>(0))
            if (this.serviceMessenger == null && !this.bindService(Intent((Context)this, DriveSyncService.class), this.serviceConnection, 0)) {
                this.finish()
                return
            }
            object = (ConnectionResult)object.getParcelableExtra(CONNECTION_RESULT_TAG)
            try {
                ((ConnectionResult)object).startResolutionForResult(this, 1)
                return
            }
            catch (IntentSender.SendIntentException sendIntentException) {
                Debug.Printf("ahhhh on connection failed completely %s", sendIntentException.getMessage())
                Debug.Warning(sendIntentException)
            }
            return
        }
        if (object.hasExtra(RESOLVABLE_ERROR_TAG)) {
            UUID uUID = UUID.fromString(object.getStringExtra(RESOLVABLE_ERROR_TAG))
            ErrorResolutionTracker errorResolutionTracker = ErrorResolutionTracker.getInstance()
            if (errorResolutionTracker == null) return
            object = errorResolutionTracker.getError(uUID)
            if (object == null) return
            if (((ErrorResolutionTracker.ResolvableError)object).status.hasResolution()) {
                try {
                    ((ErrorResolutionTracker.ResolvableError)object).status.startResolutionForResult(this, 2)
                    return
                }
                catch (IntentSender.SendIntentException sendIntentException) {
                    Debug.Warning(sendIntentException)
                    this.finish()
                }
                return
            }
            errorResolutionTracker.clearError(uUID, true)
            errorResolutionTracker.clearNotification()
            this.finish()
            return
        }
        this.finish()
    }

    protected fun onDestroy(): Unit {
        Debug.Printf("LumiyaCloud: destroyed resolution activity", Array<Object>(0))
        if (this.serviceMessenger != null) {
            Debug.Printf("LumiyaCloud: unbinding from local service", Array<Object>(0))
            this.serviceMessenger = null
            this.unbindService(this.serviceConnection)
        }
        super.onDestroy()
    }
}

