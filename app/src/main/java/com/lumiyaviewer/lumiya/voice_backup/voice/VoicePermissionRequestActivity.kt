package com.lumiyaviewer.lumiya.voice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.support.annotation.Nullable
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.lumiyaviewer.lumiya.voice.Debug
import com.lumiyaviewer.lumiya.voice.VoiceService
import javax.annotation.Nonnull

class VoicePermissionRequestActivity : AppCompatActivity() {
    companion object {
        private const val PERMISSION_AUDIO_REQUEST_CODE: Int = 100
        const val VOICE_INIT_MESSAGE: String = "voiceInitMessage"
        const val VOICE_INIT_REPLY_TO: String = "voiceInitReplyTo"
    }
    private Boolean serviceBound = false
    private val ServiceConnection serviceConnection = ServiceConnection(this){
        final VoicePermissionRequestActivity this$0
        {
            this.this$0 = voicePermissionRequestActivity
        }

        fun onServiceConnected(ComponentName componentName, IBinder iBinder) {
            VoicePermissionRequestActivity.access$002(this.this$0, Messenger(iBinder))
            ActivityCompat.requestPermissions(this.this$0, String[]{"android.permission.RECORD_AUDIO"}, 100)
        }

        fun onServiceDisconnected(ComponentName componentName) {
            VoicePermissionRequestActivity.access$002(this.this$0, null)
        }
    }
    private Messenger serviceMessenger = null

    static /* synthetic */ Messenger access$002(VoicePermissionRequestActivity voicePermissionRequestActivity, Messenger messenger) {
        voicePermissionRequestActivity.serviceMessenger = messenger
        return messenger
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Unit handlePermissionGranted(Boolean bl) {
        Intent intent
        if (this.serviceMessenger != null && (intent = this.getIntent()).hasExtra(VOICE_INIT_REPLY_TO)) {
            Message message = Message.obtain()
            message.what = 300
            Int n = bl ? 1 : 0
            message.arg1 = n
            message.obj = intent.getParcelableExtra(VOICE_INIT_REPLY_TO)
            try {
                this.serviceMessenger.send(message)
            }
            catch (RemoteException remoteException) {
                Debug.Warning(remoteException)
            }
        }
        this.finish()
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    override protected Unit onCreate(Bundle bundle) {
        super.onCreate(bundle)
        if (ContextCompat.checkSelfPermission((Context)this, "android.permission.RECORD_AUDIO") != 0) {
            this.serviceBound = this.bindService(Intent((Context)this, VoiceService.class), this.serviceConnection, 1)
            return
        }
        this.handlePermissionGranted(true)
    }

    override protected Unit onDestroy() {
        if (this.serviceBound) {
            this.serviceBound = false
            this.unbindService(this.serviceConnection)
        }
        super.onDestroy()
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    override Unit onRequestPermissionsResult(Int n, String[] stringArray, Int[] nArray) {
        block4: {
            block3: {
                Debug.Printf("Cardboard: onRequestPermissionResult, code %d", n)
                if (n != 100) break block3
                if (nArray.length <= 0 || nArray[0] != 0) break block4
                this.handlePermissionGranted(true)
            }
            return
        }
        this.handlePermissionGranted(false)
    }
}

