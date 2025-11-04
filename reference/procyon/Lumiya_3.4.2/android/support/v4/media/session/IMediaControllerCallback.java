// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.media.session;

import android.text.TextUtils;
import android.os.Parcelable$Creator;
import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import java.util.List;
import android.support.v4.media.MediaMetadataCompat;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.IInterface;

public interface IMediaControllerCallback extends IInterface
{
    void onCaptioningEnabledChanged(final boolean p0) throws RemoteException;
    
    void onEvent(final String p0, final Bundle p1) throws RemoteException;
    
    void onExtrasChanged(final Bundle p0) throws RemoteException;
    
    void onMetadataChanged(final MediaMetadataCompat p0) throws RemoteException;
    
    void onPlaybackStateChanged(final PlaybackStateCompat p0) throws RemoteException;
    
    void onQueueChanged(final List<MediaSessionCompat.QueueItem> p0) throws RemoteException;
    
    void onQueueTitleChanged(final CharSequence p0) throws RemoteException;
    
    void onRepeatModeChanged(final int p0) throws RemoteException;
    
    void onSessionDestroyed() throws RemoteException;
    
    void onShuffleModeChanged(final int p0) throws RemoteException;
    
    void onShuffleModeChangedDeprecated(final boolean p0) throws RemoteException;
    
    void onVolumeInfoChanged(final ParcelableVolumeInfo p0) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IMediaControllerCallback
    {
        private static final String DESCRIPTOR = "android.support.v4.media.session.IMediaControllerCallback";
        static final int TRANSACTION_onCaptioningEnabledChanged = 11;
        static final int TRANSACTION_onEvent = 1;
        static final int TRANSACTION_onExtrasChanged = 7;
        static final int TRANSACTION_onMetadataChanged = 4;
        static final int TRANSACTION_onPlaybackStateChanged = 3;
        static final int TRANSACTION_onQueueChanged = 5;
        static final int TRANSACTION_onQueueTitleChanged = 6;
        static final int TRANSACTION_onRepeatModeChanged = 9;
        static final int TRANSACTION_onSessionDestroyed = 2;
        static final int TRANSACTION_onShuffleModeChanged = 12;
        static final int TRANSACTION_onShuffleModeChangedDeprecated = 10;
        static final int TRANSACTION_onVolumeInfoChanged = 8;
        
        public Stub() {
            this.attachInterface((IInterface)this, "android.support.v4.media.session.IMediaControllerCallback");
        }
        
        public static IMediaControllerCallback asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("android.support.v4.media.session.IMediaControllerCallback");
            if (queryLocalInterface != null && queryLocalInterface instanceof IMediaControllerCallback) {
                return (IMediaControllerCallback)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public IBinder asBinder() {
            return (IBinder)this;
        }
        
        public boolean onTransact(final int n, final Parcel parcel, final Parcel parcel2, final int n2) throws RemoteException {
            final PlaybackStateCompat playbackStateCompat = null;
            final MediaMetadataCompat mediaMetadataCompat = null;
            final CharSequence charSequence = null;
            final Bundle bundle = null;
            final ParcelableVolumeInfo parcelableVolumeInfo = null;
            final Bundle bundle2 = null;
            boolean b = false;
            switch (n) {
                default: {
                    return super.onTransact(n, parcel, parcel2, n2);
                }
                case 1598968902: {
                    parcel2.writeString("android.support.v4.media.session.IMediaControllerCallback");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    final String string = parcel.readString();
                    Bundle bundle3;
                    if (parcel.readInt() == 0) {
                        bundle3 = bundle2;
                    }
                    else {
                        bundle3 = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                    }
                    this.onEvent(string, bundle3);
                    return true;
                }
                case 2: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    this.onSessionDestroyed();
                    return true;
                }
                case 3: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    PlaybackStateCompat playbackStateCompat2;
                    if (parcel.readInt() == 0) {
                        playbackStateCompat2 = playbackStateCompat;
                    }
                    else {
                        playbackStateCompat2 = (PlaybackStateCompat)PlaybackStateCompat.CREATOR.createFromParcel(parcel);
                    }
                    this.onPlaybackStateChanged(playbackStateCompat2);
                    return true;
                }
                case 4: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    MediaMetadataCompat mediaMetadataCompat2;
                    if (parcel.readInt() == 0) {
                        mediaMetadataCompat2 = mediaMetadataCompat;
                    }
                    else {
                        mediaMetadataCompat2 = (MediaMetadataCompat)MediaMetadataCompat.CREATOR.createFromParcel(parcel);
                    }
                    this.onMetadataChanged(mediaMetadataCompat2);
                    return true;
                }
                case 5: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    this.onQueueChanged(parcel.createTypedArrayList((Parcelable$Creator)MediaSessionCompat.QueueItem.CREATOR));
                    return true;
                }
                case 6: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    CharSequence charSequence2;
                    if (parcel.readInt() == 0) {
                        charSequence2 = charSequence;
                    }
                    else {
                        charSequence2 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
                    }
                    this.onQueueTitleChanged(charSequence2);
                    return true;
                }
                case 7: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    Bundle bundle4;
                    if (parcel.readInt() == 0) {
                        bundle4 = bundle;
                    }
                    else {
                        bundle4 = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                    }
                    this.onExtrasChanged(bundle4);
                    return true;
                }
                case 8: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    ParcelableVolumeInfo parcelableVolumeInfo2;
                    if (parcel.readInt() == 0) {
                        parcelableVolumeInfo2 = parcelableVolumeInfo;
                    }
                    else {
                        parcelableVolumeInfo2 = (ParcelableVolumeInfo)ParcelableVolumeInfo.CREATOR.createFromParcel(parcel);
                    }
                    this.onVolumeInfoChanged(parcelableVolumeInfo2);
                    return true;
                }
                case 9: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    this.onRepeatModeChanged(parcel.readInt());
                    return true;
                }
                case 10: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    this.onShuffleModeChangedDeprecated(parcel.readInt() != 0);
                    return true;
                }
                case 11: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    if (parcel.readInt() != 0) {
                        b = true;
                    }
                    this.onCaptioningEnabledChanged(b);
                    return true;
                }
                case 12: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    this.onShuffleModeChanged(parcel.readInt());
                    return true;
                }
            }
        }
        
        private static class Proxy implements IMediaControllerCallback
        {
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            public String getInterfaceDescriptor() {
                return "android.support.v4.media.session.IMediaControllerCallback";
            }
            
            @Override
            public void onCaptioningEnabledChanged(final boolean b) throws RemoteException {
                int n = 0;
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaControllerCallback");
                    if (b) {
                        n = 1;
                    }
                    obtain.writeInt(n);
                    this.mRemote.transact(11, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onEvent(final String s, final Bundle bundle) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaControllerCallback");
                    obtain.writeString(s);
                    if (bundle == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(1, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onExtrasChanged(final Bundle bundle) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaControllerCallback");
                    if (bundle == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(7, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onMetadataChanged(final MediaMetadataCompat mediaMetadataCompat) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaControllerCallback");
                    if (mediaMetadataCompat == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        mediaMetadataCompat.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(4, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onPlaybackStateChanged(final PlaybackStateCompat playbackStateCompat) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaControllerCallback");
                    if (playbackStateCompat == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        playbackStateCompat.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(3, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onQueueChanged(final List<MediaSessionCompat.QueueItem> list) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaControllerCallback");
                    obtain.writeTypedList((List)list);
                    this.mRemote.transact(5, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onQueueTitleChanged(final CharSequence charSequence) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaControllerCallback");
                    if (charSequence == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        TextUtils.writeToParcel(charSequence, obtain, 0);
                    }
                    this.mRemote.transact(6, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onRepeatModeChanged(final int n) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaControllerCallback");
                    obtain.writeInt(n);
                    this.mRemote.transact(9, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onSessionDestroyed() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaControllerCallback");
                    this.mRemote.transact(2, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onShuffleModeChanged(final int n) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaControllerCallback");
                    obtain.writeInt(n);
                    this.mRemote.transact(12, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onShuffleModeChangedDeprecated(final boolean b) throws RemoteException {
                int n = 0;
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaControllerCallback");
                    if (b) {
                        n = 1;
                    }
                    obtain.writeInt(n);
                    this.mRemote.transact(10, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onVolumeInfoChanged(final ParcelableVolumeInfo parcelableVolumeInfo) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaControllerCallback");
                    if (parcelableVolumeInfo == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        parcelableVolumeInfo.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(8, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
        }
    }
}
