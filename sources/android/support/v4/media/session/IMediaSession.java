package android.support.v4.media.session;

import android.app.PendingIntent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import java.util.List;

public interface IMediaSession extends IInterface {

    public static abstract class Stub extends Binder implements IMediaSession {
        private static final String DESCRIPTOR = "android.support.v4.media.session.IMediaSession";
        static final int TRANSACTION_addQueueItem = 41;
        static final int TRANSACTION_addQueueItemAt = 42;
        static final int TRANSACTION_adjustVolume = 11;
        static final int TRANSACTION_fastForward = 22;
        static final int TRANSACTION_getExtras = 31;
        static final int TRANSACTION_getFlags = 9;
        static final int TRANSACTION_getLaunchPendingIntent = 8;
        static final int TRANSACTION_getMetadata = 27;
        static final int TRANSACTION_getPackageName = 6;
        static final int TRANSACTION_getPlaybackState = 28;
        static final int TRANSACTION_getQueue = 29;
        static final int TRANSACTION_getQueueTitle = 30;
        static final int TRANSACTION_getRatingType = 32;
        static final int TRANSACTION_getRepeatMode = 37;
        static final int TRANSACTION_getShuffleMode = 47;
        static final int TRANSACTION_getTag = 7;
        static final int TRANSACTION_getVolumeAttributes = 10;
        static final int TRANSACTION_isCaptioningEnabled = 45;
        static final int TRANSACTION_isShuffleModeEnabledDeprecated = 38;
        static final int TRANSACTION_isTransportControlEnabled = 5;
        static final int TRANSACTION_next = 20;
        static final int TRANSACTION_pause = 18;
        static final int TRANSACTION_play = 13;
        static final int TRANSACTION_playFromMediaId = 14;
        static final int TRANSACTION_playFromSearch = 15;
        static final int TRANSACTION_playFromUri = 16;
        static final int TRANSACTION_prepare = 33;
        static final int TRANSACTION_prepareFromMediaId = 34;
        static final int TRANSACTION_prepareFromSearch = 35;
        static final int TRANSACTION_prepareFromUri = 36;
        static final int TRANSACTION_previous = 21;
        static final int TRANSACTION_rate = 25;
        static final int TRANSACTION_rateWithExtras = 51;
        static final int TRANSACTION_registerCallbackListener = 3;
        static final int TRANSACTION_removeQueueItem = 43;
        static final int TRANSACTION_removeQueueItemAt = 44;
        static final int TRANSACTION_rewind = 23;
        static final int TRANSACTION_seekTo = 24;
        static final int TRANSACTION_sendCommand = 1;
        static final int TRANSACTION_sendCustomAction = 26;
        static final int TRANSACTION_sendMediaButton = 2;
        static final int TRANSACTION_setCaptioningEnabled = 46;
        static final int TRANSACTION_setRepeatMode = 39;
        static final int TRANSACTION_setShuffleMode = 48;
        static final int TRANSACTION_setShuffleModeEnabledDeprecated = 40;
        static final int TRANSACTION_setVolumeTo = 12;
        static final int TRANSACTION_skipToQueueItem = 17;
        static final int TRANSACTION_stop = 19;
        static final int TRANSACTION_unregisterCallbackListener = 4;

        private static class Proxy implements IMediaSession {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public void addQueueItem(MediaDescriptionCompat mediaDescriptionCompat) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (mediaDescriptionCompat == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        mediaDescriptionCompat.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(41, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void addQueueItemAt(MediaDescriptionCompat mediaDescriptionCompat, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (mediaDescriptionCompat == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        mediaDescriptionCompat.writeToParcel(obtain, 0);
                    }
                    obtain.writeInt(i);
                    this.mRemote.transact(42, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void adjustVolume(int i, int i2, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeString(str);
                    this.mRemote.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void fastForward() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(22, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Bundle getExtras() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() == 0 ? null : (Bundle) Bundle.CREATOR.createFromParcel(obtain2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public long getFlags() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readLong();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public PendingIntent getLaunchPendingIntent() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() == 0 ? null : (PendingIntent) PendingIntent.CREATOR.createFromParcel(obtain2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public MediaMetadataCompat getMetadata() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(27, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() == 0 ? null : MediaMetadataCompat.CREATOR.createFromParcel(obtain2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getPackageName() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public PlaybackStateCompat getPlaybackState() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(28, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() == 0 ? null : PlaybackStateCompat.CREATOR.createFromParcel(obtain2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<MediaSessionCompat.QueueItem> getQueue() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(29, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createTypedArrayList(MediaSessionCompat.QueueItem.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public CharSequence getQueueTitle() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(30, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() == 0 ? null : (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(obtain2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getRatingType() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(32, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getRepeatMode() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(37, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getShuffleMode() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(47, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getTag() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public ParcelableVolumeInfo getVolumeAttributes() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() == 0 ? null : ParcelableVolumeInfo.CREATOR.createFromParcel(obtain2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isCaptioningEnabled() throws RemoteException {
                boolean z = false;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(45, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isShuffleModeEnabledDeprecated() throws RemoteException {
                boolean z = false;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(38, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isTransportControlEnabled() throws RemoteException {
                boolean z = false;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void next() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void pause() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void play() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void playFromMediaId(String str, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (bundle == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(14, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void playFromSearch(String str, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (bundle == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(15, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void playFromUri(Uri uri, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (uri == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    }
                    if (bundle == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(16, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void prepare() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(33, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void prepareFromMediaId(String str, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (bundle == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(34, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void prepareFromSearch(String str, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (bundle == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(35, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void prepareFromUri(Uri uri, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (uri == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    }
                    if (bundle == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(36, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void previous() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(21, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void rate(RatingCompat ratingCompat) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (ratingCompat == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        ratingCompat.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(25, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void rateWithExtras(RatingCompat ratingCompat, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (ratingCompat == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        ratingCompat.writeToParcel(obtain, 0);
                    }
                    if (bundle == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(51, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void registerCallbackListener(IMediaControllerCallback iMediaControllerCallback) throws RemoteException {
                IBinder iBinder = null;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (iMediaControllerCallback != null) {
                        iBinder = iMediaControllerCallback.asBinder();
                    }
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void removeQueueItem(MediaDescriptionCompat mediaDescriptionCompat) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (mediaDescriptionCompat == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        mediaDescriptionCompat.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(43, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void removeQueueItemAt(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(44, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void rewind() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(23, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void seekTo(long j) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeLong(j);
                    this.mRemote.transact(24, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendCommand(String str, Bundle bundle, MediaSessionCompat.ResultReceiverWrapper resultReceiverWrapper) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (bundle == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    if (resultReceiverWrapper == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        resultReceiverWrapper.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void sendCustomAction(String str, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (bundle == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(26, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean sendMediaButton(KeyEvent keyEvent) throws RemoteException {
                boolean z = false;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (keyEvent == null) {
                        obtain.writeInt(0);
                    } else {
                        obtain.writeInt(1);
                        keyEvent.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setCaptioningEnabled(boolean z) throws RemoteException {
                int i = 0;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (z) {
                        i = 1;
                    }
                    obtain.writeInt(i);
                    this.mRemote.transact(46, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setRepeatMode(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(39, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setShuffleMode(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(48, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setShuffleModeEnabledDeprecated(boolean z) throws RemoteException {
                int i = 0;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (z) {
                        i = 1;
                    }
                    obtain.writeInt(i);
                    this.mRemote.transact(40, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setVolumeTo(int i, int i2, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeString(str);
                    this.mRemote.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void skipToQueueItem(long j) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeLong(j);
                    this.mRemote.transact(17, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void stop() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void unregisterCallbackListener(IMediaControllerCallback iMediaControllerCallback) throws RemoteException {
                IBinder iBinder = null;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (iMediaControllerCallback != null) {
                        iBinder = iMediaControllerCallback.asBinder();
                    }
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMediaSession asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface != null && (queryLocalInterface instanceof IMediaSession)) ? (IMediaSession) queryLocalInterface : new Proxy(iBinder);
        }

        public IBinder asBinder() {
            return this;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v6, resolved type: android.support.v4.media.RatingCompat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v8, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v10, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v12, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v14, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v16, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v18, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v20, resolved type: android.support.v4.media.MediaDescriptionCompat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v22, resolved type: android.support.v4.media.MediaDescriptionCompat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v24, resolved type: android.support.v4.media.MediaDescriptionCompat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v28, resolved type: android.view.KeyEvent} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v30, resolved type: android.support.v4.media.session.MediaSessionCompat$ResultReceiverWrapper} */
        /* JADX WARNING: type inference failed for: r2v0 */
        /* JADX WARNING: type inference failed for: r2v1 */
        /* JADX WARNING: type inference failed for: r2v3 */
        /* JADX WARNING: type inference failed for: r2v5 */
        /* JADX WARNING: type inference failed for: r2v7 */
        /* JADX WARNING: type inference failed for: r2v9 */
        /* JADX WARNING: type inference failed for: r2v11 */
        /* JADX WARNING: type inference failed for: r2v13 */
        /* JADX WARNING: type inference failed for: r2v15 */
        /* JADX WARNING: type inference failed for: r2v17 */
        /* JADX WARNING: type inference failed for: r2v19 */
        /* JADX WARNING: type inference failed for: r2v21 */
        /* JADX WARNING: type inference failed for: r2v23 */
        /* JADX WARNING: type inference failed for: r2v27 */
        /* JADX WARNING: type inference failed for: r2v29 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r6, android.os.Parcel r7, android.os.Parcel r8, int r9) throws android.os.RemoteException {
            /*
                r5 = this;
                r2 = 0
                r1 = 0
                r3 = 1
                switch(r6) {
                    case 1: goto L_0x0012;
                    case 2: goto L_0x0044;
                    case 3: goto L_0x006a;
                    case 4: goto L_0x007f;
                    case 5: goto L_0x0094;
                    case 6: goto L_0x00a9;
                    case 7: goto L_0x00ba;
                    case 8: goto L_0x00cb;
                    case 9: goto L_0x00e5;
                    case 10: goto L_0x00f6;
                    case 11: goto L_0x0110;
                    case 12: goto L_0x0129;
                    case 13: goto L_0x0301;
                    case 14: goto L_0x030e;
                    case 15: goto L_0x032f;
                    case 16: goto L_0x0350;
                    case 17: goto L_0x037e;
                    case 18: goto L_0x038f;
                    case 19: goto L_0x039c;
                    case 20: goto L_0x03a9;
                    case 21: goto L_0x03b6;
                    case 22: goto L_0x03c3;
                    case 23: goto L_0x03d0;
                    case 24: goto L_0x03dd;
                    case 25: goto L_0x03ee;
                    case 26: goto L_0x0485;
                    case 27: goto L_0x0142;
                    case 28: goto L_0x015c;
                    case 29: goto L_0x0176;
                    case 30: goto L_0x0187;
                    case 31: goto L_0x01a1;
                    case 32: goto L_0x01bb;
                    case 33: goto L_0x0284;
                    case 34: goto L_0x0291;
                    case 35: goto L_0x02b2;
                    case 36: goto L_0x02d3;
                    case 37: goto L_0x01e1;
                    case 38: goto L_0x01f2;
                    case 39: goto L_0x044e;
                    case 40: goto L_0x045f;
                    case 41: goto L_0x0218;
                    case 42: goto L_0x0235;
                    case 43: goto L_0x0256;
                    case 44: goto L_0x0273;
                    case 45: goto L_0x01cc;
                    case 46: goto L_0x0439;
                    case 47: goto L_0x0207;
                    case 48: goto L_0x0474;
                    case 51: goto L_0x040b;
                    case 1598968902: goto L_0x000b;
                    default: goto L_0x0006;
                }
            L_0x0006:
                boolean r0 = super.onTransact(r6, r7, r8, r9)
                return r0
            L_0x000b:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r8.writeString(r0)
                return r3
            L_0x0012:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.lang.String r4 = r7.readString()
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x0030
                r1 = r2
            L_0x0023:
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x003a
            L_0x0029:
                r5.sendCommand(r4, r1, r2)
                r8.writeNoException()
                return r3
            L_0x0030:
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.os.Bundle r0 = (android.os.Bundle) r0
                r1 = r0
                goto L_0x0023
            L_0x003a:
                android.os.Parcelable$Creator<android.support.v4.media.session.MediaSessionCompat$ResultReceiverWrapper> r0 = android.support.v4.media.session.MediaSessionCompat.ResultReceiverWrapper.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.support.v4.media.session.MediaSessionCompat$ResultReceiverWrapper r0 = (android.support.v4.media.session.MediaSessionCompat.ResultReceiverWrapper) r0
                r2 = r0
                goto L_0x0029
            L_0x0044:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x005e
            L_0x0050:
                boolean r0 = r5.sendMediaButton(r2)
                r8.writeNoException()
                if (r0 != 0) goto L_0x0068
                r0 = r1
            L_0x005a:
                r8.writeInt(r0)
                return r3
            L_0x005e:
                android.os.Parcelable$Creator r0 = android.view.KeyEvent.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.view.KeyEvent r0 = (android.view.KeyEvent) r0
                r2 = r0
                goto L_0x0050
            L_0x0068:
                r0 = r3
                goto L_0x005a
            L_0x006a:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                android.os.IBinder r0 = r7.readStrongBinder()
                android.support.v4.media.session.IMediaControllerCallback r0 = android.support.v4.media.session.IMediaControllerCallback.Stub.asInterface(r0)
                r5.registerCallbackListener(r0)
                r8.writeNoException()
                return r3
            L_0x007f:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                android.os.IBinder r0 = r7.readStrongBinder()
                android.support.v4.media.session.IMediaControllerCallback r0 = android.support.v4.media.session.IMediaControllerCallback.Stub.asInterface(r0)
                r5.unregisterCallbackListener(r0)
                r8.writeNoException()
                return r3
            L_0x0094:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                boolean r0 = r5.isTransportControlEnabled()
                r8.writeNoException()
                if (r0 != 0) goto L_0x00a7
            L_0x00a3:
                r8.writeInt(r1)
                return r3
            L_0x00a7:
                r1 = r3
                goto L_0x00a3
            L_0x00a9:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.lang.String r0 = r5.getPackageName()
                r8.writeNoException()
                r8.writeString(r0)
                return r3
            L_0x00ba:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.lang.String r0 = r5.getTag()
                r8.writeNoException()
                r8.writeString(r0)
                return r3
            L_0x00cb:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                android.app.PendingIntent r0 = r5.getLaunchPendingIntent()
                r8.writeNoException()
                if (r0 != 0) goto L_0x00de
                r8.writeInt(r1)
            L_0x00dd:
                return r3
            L_0x00de:
                r8.writeInt(r3)
                r0.writeToParcel(r8, r3)
                goto L_0x00dd
            L_0x00e5:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                long r0 = r5.getFlags()
                r8.writeNoException()
                r8.writeLong(r0)
                return r3
            L_0x00f6:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                android.support.v4.media.session.ParcelableVolumeInfo r0 = r5.getVolumeAttributes()
                r8.writeNoException()
                if (r0 != 0) goto L_0x0109
                r8.writeInt(r1)
            L_0x0108:
                return r3
            L_0x0109:
                r8.writeInt(r3)
                r0.writeToParcel(r8, r3)
                goto L_0x0108
            L_0x0110:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                int r1 = r7.readInt()
                java.lang.String r2 = r7.readString()
                r5.adjustVolume(r0, r1, r2)
                r8.writeNoException()
                return r3
            L_0x0129:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                int r1 = r7.readInt()
                java.lang.String r2 = r7.readString()
                r5.setVolumeTo(r0, r1, r2)
                r8.writeNoException()
                return r3
            L_0x0142:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                android.support.v4.media.MediaMetadataCompat r0 = r5.getMetadata()
                r8.writeNoException()
                if (r0 != 0) goto L_0x0155
                r8.writeInt(r1)
            L_0x0154:
                return r3
            L_0x0155:
                r8.writeInt(r3)
                r0.writeToParcel(r8, r3)
                goto L_0x0154
            L_0x015c:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                android.support.v4.media.session.PlaybackStateCompat r0 = r5.getPlaybackState()
                r8.writeNoException()
                if (r0 != 0) goto L_0x016f
                r8.writeInt(r1)
            L_0x016e:
                return r3
            L_0x016f:
                r8.writeInt(r3)
                r0.writeToParcel(r8, r3)
                goto L_0x016e
            L_0x0176:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.util.List r0 = r5.getQueue()
                r8.writeNoException()
                r8.writeTypedList(r0)
                return r3
            L_0x0187:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.lang.CharSequence r0 = r5.getQueueTitle()
                r8.writeNoException()
                if (r0 != 0) goto L_0x019a
                r8.writeInt(r1)
            L_0x0199:
                return r3
            L_0x019a:
                r8.writeInt(r3)
                android.text.TextUtils.writeToParcel(r0, r8, r3)
                goto L_0x0199
            L_0x01a1:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                android.os.Bundle r0 = r5.getExtras()
                r8.writeNoException()
                if (r0 != 0) goto L_0x01b4
                r8.writeInt(r1)
            L_0x01b3:
                return r3
            L_0x01b4:
                r8.writeInt(r3)
                r0.writeToParcel(r8, r3)
                goto L_0x01b3
            L_0x01bb:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r5.getRatingType()
                r8.writeNoException()
                r8.writeInt(r0)
                return r3
            L_0x01cc:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                boolean r0 = r5.isCaptioningEnabled()
                r8.writeNoException()
                if (r0 != 0) goto L_0x01df
            L_0x01db:
                r8.writeInt(r1)
                return r3
            L_0x01df:
                r1 = r3
                goto L_0x01db
            L_0x01e1:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r5.getRepeatMode()
                r8.writeNoException()
                r8.writeInt(r0)
                return r3
            L_0x01f2:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                boolean r0 = r5.isShuffleModeEnabledDeprecated()
                r8.writeNoException()
                if (r0 != 0) goto L_0x0205
            L_0x0201:
                r8.writeInt(r1)
                return r3
            L_0x0205:
                r1 = r3
                goto L_0x0201
            L_0x0207:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r5.getShuffleMode()
                r8.writeNoException()
                r8.writeInt(r0)
                return r3
            L_0x0218:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x022b
            L_0x0224:
                r5.addQueueItem(r2)
                r8.writeNoException()
                return r3
            L_0x022b:
                android.os.Parcelable$Creator<android.support.v4.media.MediaDescriptionCompat> r0 = android.support.v4.media.MediaDescriptionCompat.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.support.v4.media.MediaDescriptionCompat r0 = (android.support.v4.media.MediaDescriptionCompat) r0
                r2 = r0
                goto L_0x0224
            L_0x0235:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x024c
            L_0x0241:
                int r0 = r7.readInt()
                r5.addQueueItemAt(r2, r0)
                r8.writeNoException()
                return r3
            L_0x024c:
                android.os.Parcelable$Creator<android.support.v4.media.MediaDescriptionCompat> r0 = android.support.v4.media.MediaDescriptionCompat.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.support.v4.media.MediaDescriptionCompat r0 = (android.support.v4.media.MediaDescriptionCompat) r0
                r2 = r0
                goto L_0x0241
            L_0x0256:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x0269
            L_0x0262:
                r5.removeQueueItem(r2)
                r8.writeNoException()
                return r3
            L_0x0269:
                android.os.Parcelable$Creator<android.support.v4.media.MediaDescriptionCompat> r0 = android.support.v4.media.MediaDescriptionCompat.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.support.v4.media.MediaDescriptionCompat r0 = (android.support.v4.media.MediaDescriptionCompat) r0
                r2 = r0
                goto L_0x0262
            L_0x0273:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                r5.removeQueueItemAt(r0)
                r8.writeNoException()
                return r3
            L_0x0284:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                r5.prepare()
                r8.writeNoException()
                return r3
            L_0x0291:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.lang.String r1 = r7.readString()
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x02a8
            L_0x02a1:
                r5.prepareFromMediaId(r1, r2)
                r8.writeNoException()
                return r3
            L_0x02a8:
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.os.Bundle r0 = (android.os.Bundle) r0
                r2 = r0
                goto L_0x02a1
            L_0x02b2:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.lang.String r1 = r7.readString()
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x02c9
            L_0x02c2:
                r5.prepareFromSearch(r1, r2)
                r8.writeNoException()
                return r3
            L_0x02c9:
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.os.Bundle r0 = (android.os.Bundle) r0
                r2 = r0
                goto L_0x02c2
            L_0x02d3:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x02ed
                r1 = r2
            L_0x02e0:
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x02f7
            L_0x02e6:
                r5.prepareFromUri(r1, r2)
                r8.writeNoException()
                return r3
            L_0x02ed:
                android.os.Parcelable$Creator r0 = android.net.Uri.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.net.Uri r0 = (android.net.Uri) r0
                r1 = r0
                goto L_0x02e0
            L_0x02f7:
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.os.Bundle r0 = (android.os.Bundle) r0
                r2 = r0
                goto L_0x02e6
            L_0x0301:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                r5.play()
                r8.writeNoException()
                return r3
            L_0x030e:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.lang.String r1 = r7.readString()
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x0325
            L_0x031e:
                r5.playFromMediaId(r1, r2)
                r8.writeNoException()
                return r3
            L_0x0325:
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.os.Bundle r0 = (android.os.Bundle) r0
                r2 = r0
                goto L_0x031e
            L_0x032f:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.lang.String r1 = r7.readString()
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x0346
            L_0x033f:
                r5.playFromSearch(r1, r2)
                r8.writeNoException()
                return r3
            L_0x0346:
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.os.Bundle r0 = (android.os.Bundle) r0
                r2 = r0
                goto L_0x033f
            L_0x0350:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x036a
                r1 = r2
            L_0x035d:
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x0374
            L_0x0363:
                r5.playFromUri(r1, r2)
                r8.writeNoException()
                return r3
            L_0x036a:
                android.os.Parcelable$Creator r0 = android.net.Uri.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.net.Uri r0 = (android.net.Uri) r0
                r1 = r0
                goto L_0x035d
            L_0x0374:
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.os.Bundle r0 = (android.os.Bundle) r0
                r2 = r0
                goto L_0x0363
            L_0x037e:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                long r0 = r7.readLong()
                r5.skipToQueueItem(r0)
                r8.writeNoException()
                return r3
            L_0x038f:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                r5.pause()
                r8.writeNoException()
                return r3
            L_0x039c:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                r5.stop()
                r8.writeNoException()
                return r3
            L_0x03a9:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                r5.next()
                r8.writeNoException()
                return r3
            L_0x03b6:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                r5.previous()
                r8.writeNoException()
                return r3
            L_0x03c3:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                r5.fastForward()
                r8.writeNoException()
                return r3
            L_0x03d0:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                r5.rewind()
                r8.writeNoException()
                return r3
            L_0x03dd:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                long r0 = r7.readLong()
                r5.seekTo(r0)
                r8.writeNoException()
                return r3
            L_0x03ee:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x0401
            L_0x03fa:
                r5.rate(r2)
                r8.writeNoException()
                return r3
            L_0x0401:
                android.os.Parcelable$Creator<android.support.v4.media.RatingCompat> r0 = android.support.v4.media.RatingCompat.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.support.v4.media.RatingCompat r0 = (android.support.v4.media.RatingCompat) r0
                r2 = r0
                goto L_0x03fa
            L_0x040b:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x0425
                r1 = r2
            L_0x0418:
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x042f
            L_0x041e:
                r5.rateWithExtras(r1, r2)
                r8.writeNoException()
                return r3
            L_0x0425:
                android.os.Parcelable$Creator<android.support.v4.media.RatingCompat> r0 = android.support.v4.media.RatingCompat.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.support.v4.media.RatingCompat r0 = (android.support.v4.media.RatingCompat) r0
                r1 = r0
                goto L_0x0418
            L_0x042f:
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.os.Bundle r0 = (android.os.Bundle) r0
                r2 = r0
                goto L_0x041e
            L_0x0439:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x044c
            L_0x0445:
                r5.setCaptioningEnabled(r1)
                r8.writeNoException()
                return r3
            L_0x044c:
                r1 = r3
                goto L_0x0445
            L_0x044e:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                r5.setRepeatMode(r0)
                r8.writeNoException()
                return r3
            L_0x045f:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x0472
            L_0x046b:
                r5.setShuffleModeEnabledDeprecated(r1)
                r8.writeNoException()
                return r3
            L_0x0472:
                r1 = r3
                goto L_0x046b
            L_0x0474:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                r5.setShuffleMode(r0)
                r8.writeNoException()
                return r3
            L_0x0485:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.lang.String r1 = r7.readString()
                int r0 = r7.readInt()
                if (r0 != 0) goto L_0x049c
            L_0x0495:
                r5.sendCustomAction(r1, r2)
                r8.writeNoException()
                return r3
            L_0x049c:
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.os.Bundle r0 = (android.os.Bundle) r0
                r2 = r0
                goto L_0x0495
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v4.media.session.IMediaSession.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    void addQueueItem(MediaDescriptionCompat mediaDescriptionCompat) throws RemoteException;

    void addQueueItemAt(MediaDescriptionCompat mediaDescriptionCompat, int i) throws RemoteException;

    void adjustVolume(int i, int i2, String str) throws RemoteException;

    void fastForward() throws RemoteException;

    Bundle getExtras() throws RemoteException;

    long getFlags() throws RemoteException;

    PendingIntent getLaunchPendingIntent() throws RemoteException;

    MediaMetadataCompat getMetadata() throws RemoteException;

    String getPackageName() throws RemoteException;

    PlaybackStateCompat getPlaybackState() throws RemoteException;

    List<MediaSessionCompat.QueueItem> getQueue() throws RemoteException;

    CharSequence getQueueTitle() throws RemoteException;

    int getRatingType() throws RemoteException;

    int getRepeatMode() throws RemoteException;

    int getShuffleMode() throws RemoteException;

    String getTag() throws RemoteException;

    ParcelableVolumeInfo getVolumeAttributes() throws RemoteException;

    boolean isCaptioningEnabled() throws RemoteException;

    boolean isShuffleModeEnabledDeprecated() throws RemoteException;

    boolean isTransportControlEnabled() throws RemoteException;

    void next() throws RemoteException;

    void pause() throws RemoteException;

    void play() throws RemoteException;

    void playFromMediaId(String str, Bundle bundle) throws RemoteException;

    void playFromSearch(String str, Bundle bundle) throws RemoteException;

    void playFromUri(Uri uri, Bundle bundle) throws RemoteException;

    void prepare() throws RemoteException;

    void prepareFromMediaId(String str, Bundle bundle) throws RemoteException;

    void prepareFromSearch(String str, Bundle bundle) throws RemoteException;

    void prepareFromUri(Uri uri, Bundle bundle) throws RemoteException;

    void previous() throws RemoteException;

    void rate(RatingCompat ratingCompat) throws RemoteException;

    void rateWithExtras(RatingCompat ratingCompat, Bundle bundle) throws RemoteException;

    void registerCallbackListener(IMediaControllerCallback iMediaControllerCallback) throws RemoteException;

    void removeQueueItem(MediaDescriptionCompat mediaDescriptionCompat) throws RemoteException;

    void removeQueueItemAt(int i) throws RemoteException;

    void rewind() throws RemoteException;

    void seekTo(long j) throws RemoteException;

    void sendCommand(String str, Bundle bundle, MediaSessionCompat.ResultReceiverWrapper resultReceiverWrapper) throws RemoteException;

    void sendCustomAction(String str, Bundle bundle) throws RemoteException;

    boolean sendMediaButton(KeyEvent keyEvent) throws RemoteException;

    void setCaptioningEnabled(boolean z) throws RemoteException;

    void setRepeatMode(int i) throws RemoteException;

    void setShuffleMode(int i) throws RemoteException;

    void setShuffleModeEnabledDeprecated(boolean z) throws RemoteException;

    void setVolumeTo(int i, int i2, String str) throws RemoteException;

    void skipToQueueItem(long j) throws RemoteException;

    void stop() throws RemoteException;

    void unregisterCallbackListener(IMediaControllerCallback iMediaControllerCallback) throws RemoteException;
}
