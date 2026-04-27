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
package com.lumiyaviewer.lumiya.cloud;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.common.ConnectionResult;
import com.lumiyaviewer.lumiya.cloud.Debug;
import com.lumiyaviewer.lumiya.cloud.DriveSyncService;
import com.lumiyaviewer.lumiya.cloud.ErrorResolutionTracker;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConnectionResolutionActivity
extends Activity {
    private static final String CONNECTION_RESULT_TAG = "connectionResult";
    private static final String RESOLVABLE_ERROR_TAG = "resolvableError";
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 1;
    private static final int RESOLVE_RESOLVABLE_REQUEST_CODE = 2;
    private final ServiceConnection serviceConnection = new ServiceConnection(this){
        final ConnectionResolutionActivity this$0;
        {
            this.this$0 = connectionResolutionActivity;
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Debug.Printf("LumiyaCloud: bound to local service", new Object[0]);
            ConnectionResolutionActivity.access$002(this.this$0, new Messenger(iBinder));
        }

        public void onServiceDisconnected(ComponentName componentName) {
            ConnectionResolutionActivity.access$002(this.this$0, null);
        }
    };
    @Nullable
    private Messenger serviceMessenger = null;

    static /* synthetic */ Messenger access$002(ConnectionResolutionActivity connectionResolutionActivity, Messenger messenger) {
        connectionResolutionActivity.serviceMessenger = messenger;
        return messenger;
    }

    static Intent getResolvableErrorIntent(Context context, @Nonnull UUID uUID) {
        context = new Intent(context, ConnectionResolutionActivity.class);
        context.putExtra(RESOLVABLE_ERROR_TAG, uUID.toString());
        context.setFlags(0x10000000);
        return context;
    }

    static void startForConnectionResolution(Context context, @Nonnull ConnectionResult connectionResult) {
        Intent intent = new Intent(context, ConnectionResolutionActivity.class);
        intent.putExtra(CONNECTION_RESULT_TAG, (Parcelable)connectionResult);
        intent.setFlags(0x10000000);
        context.startActivity(intent);
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected void onActivityResult(int n, int n2, Intent object) {
        void var3_8;
        boolean bl = true;
        Object var3_4 = null;
        Debug.Printf("LumiyaCloud: got result code: %d", n2);
        switch (n) {
            case 1: {
                if (this.serviceMessenger != null) {
                    if (n2 == -1) {
                        try {
                            this.serviceMessenger.send(Message.obtain(null, (int)101));
                        }
                        catch (RemoteException remoteException) {
                            Debug.Warning(remoteException);
                        }
                    } else {
                        try {
                            this.serviceMessenger.send(Message.obtain(null, (int)102));
                        }
                        catch (RemoteException remoteException) {
                            Debug.Warning(remoteException);
                        }
                    }
                    Debug.Printf("LumiyaCloud: unbinding from local service", new Object[0]);
                    this.serviceMessenger = null;
                    this.unbindService(this.serviceConnection);
                }
                this.finish();
            }
            default: {
                return;
            }
            case 2: 
        }
        UUID uUID = UUID.fromString(this.getIntent().getStringExtra(RESOLVABLE_ERROR_TAG));
        ErrorResolutionTracker errorResolutionTracker = ErrorResolutionTracker.getInstance();
        if (errorResolutionTracker != null) {
            ErrorResolutionTracker.ResolvableError resolvableError = errorResolutionTracker.getError(uUID);
        }
        if (var3_8 != null) {
            if (n2 != -1) {
                bl = false;
            }
            errorResolutionTracker.clearError(uUID, bl);
        }
        if (errorResolutionTracker != null) {
            errorResolutionTracker.clearNotification();
        }
        this.finish();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected void onCreate(@Nullable Bundle object) {
        super.onCreate((Bundle)object);
        object = this.getIntent();
        if (object.hasExtra(CONNECTION_RESULT_TAG)) {
            Debug.Printf("LumiyaCloud: binding to local service", new Object[0]);
            if (this.serviceMessenger == null && !this.bindService(new Intent((Context)this, DriveSyncService.class), this.serviceConnection, 0)) {
                this.finish();
                return;
            }
            object = (ConnectionResult)object.getParcelableExtra(CONNECTION_RESULT_TAG);
            try {
                ((ConnectionResult)object).startResolutionForResult(this, 1);
                return;
            }
            catch (IntentSender.SendIntentException sendIntentException) {
                Debug.Printf("ahhhh on connection failed completely %s", sendIntentException.getMessage());
                Debug.Warning(sendIntentException);
            }
            return;
        }
        if (object.hasExtra(RESOLVABLE_ERROR_TAG)) {
            UUID uUID = UUID.fromString(object.getStringExtra(RESOLVABLE_ERROR_TAG));
            ErrorResolutionTracker errorResolutionTracker = ErrorResolutionTracker.getInstance();
            if (errorResolutionTracker == null) return;
            object = errorResolutionTracker.getError(uUID);
            if (object == null) return;
            if (((ErrorResolutionTracker.ResolvableError)object).status.hasResolution()) {
                try {
                    ((ErrorResolutionTracker.ResolvableError)object).status.startResolutionForResult(this, 2);
                    return;
                }
                catch (IntentSender.SendIntentException sendIntentException) {
                    Debug.Warning(sendIntentException);
                    this.finish();
                }
                return;
            }
            errorResolutionTracker.clearError(uUID, true);
            errorResolutionTracker.clearNotification();
            this.finish();
            return;
        }
        this.finish();
    }

    protected void onDestroy() {
        Debug.Printf("LumiyaCloud: destroyed resolution activity", new Object[0]);
        if (this.serviceMessenger != null) {
            Debug.Printf("LumiyaCloud: unbinding from local service", new Object[0]);
            this.serviceMessenger = null;
            this.unbindService(this.serviceConnection);
        }
        super.onDestroy();
    }
}

