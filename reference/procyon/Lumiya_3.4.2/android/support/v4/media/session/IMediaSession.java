// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.media.session;

import android.os.Parcelable$Creator;
import android.text.TextUtils;
import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.view.KeyEvent;
import android.support.v4.media.RatingCompat;
import android.net.Uri;
import java.util.List;
import android.support.v4.media.MediaMetadataCompat;
import android.app.PendingIntent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaDescriptionCompat;
import android.os.IInterface;

public interface IMediaSession extends IInterface
{
    void addQueueItem(final MediaDescriptionCompat p0) throws RemoteException;
    
    void addQueueItemAt(final MediaDescriptionCompat p0, final int p1) throws RemoteException;
    
    void adjustVolume(final int p0, final int p1, final String p2) throws RemoteException;
    
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
    
    void playFromMediaId(final String p0, final Bundle p1) throws RemoteException;
    
    void playFromSearch(final String p0, final Bundle p1) throws RemoteException;
    
    void playFromUri(final Uri p0, final Bundle p1) throws RemoteException;
    
    void prepare() throws RemoteException;
    
    void prepareFromMediaId(final String p0, final Bundle p1) throws RemoteException;
    
    void prepareFromSearch(final String p0, final Bundle p1) throws RemoteException;
    
    void prepareFromUri(final Uri p0, final Bundle p1) throws RemoteException;
    
    void previous() throws RemoteException;
    
    void rate(final RatingCompat p0) throws RemoteException;
    
    void rateWithExtras(final RatingCompat p0, final Bundle p1) throws RemoteException;
    
    void registerCallbackListener(final IMediaControllerCallback p0) throws RemoteException;
    
    void removeQueueItem(final MediaDescriptionCompat p0) throws RemoteException;
    
    void removeQueueItemAt(final int p0) throws RemoteException;
    
    void rewind() throws RemoteException;
    
    void seekTo(final long p0) throws RemoteException;
    
    void sendCommand(final String p0, final Bundle p1, final MediaSessionCompat.ResultReceiverWrapper p2) throws RemoteException;
    
    void sendCustomAction(final String p0, final Bundle p1) throws RemoteException;
    
    boolean sendMediaButton(final KeyEvent p0) throws RemoteException;
    
    void setCaptioningEnabled(final boolean p0) throws RemoteException;
    
    void setRepeatMode(final int p0) throws RemoteException;
    
    void setShuffleMode(final int p0) throws RemoteException;
    
    void setShuffleModeEnabledDeprecated(final boolean p0) throws RemoteException;
    
    void setVolumeTo(final int p0, final int p1, final String p2) throws RemoteException;
    
    void skipToQueueItem(final long p0) throws RemoteException;
    
    void stop() throws RemoteException;
    
    void unregisterCallbackListener(final IMediaControllerCallback p0) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IMediaSession
    {
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
        
        public Stub() {
            this.attachInterface((IInterface)this, "android.support.v4.media.session.IMediaSession");
        }
        
        public static IMediaSession asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("android.support.v4.media.session.IMediaSession");
            if (queryLocalInterface != null && queryLocalInterface instanceof IMediaSession) {
                return (IMediaSession)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public IBinder asBinder() {
            return (IBinder)this;
        }
        
        public boolean onTransact(int n, final Parcel parcel, final Parcel parcel2, final int n2) throws RemoteException {
            final KeyEvent keyEvent = null;
            final MediaDescriptionCompat mediaDescriptionCompat = null;
            final MediaDescriptionCompat mediaDescriptionCompat2 = null;
            final MediaDescriptionCompat mediaDescriptionCompat3 = null;
            final Bundle bundle = null;
            final Bundle bundle2 = null;
            final Bundle bundle3 = null;
            final Bundle bundle4 = null;
            final Bundle bundle5 = null;
            final Bundle bundle6 = null;
            final RatingCompat ratingCompat = null;
            final Bundle bundle7 = null;
            final Bundle bundle8 = null;
            final MediaSessionCompat.ResultReceiverWrapper resultReceiverWrapper = null;
            final int n3 = 0;
            final int n4 = 0;
            boolean captioningEnabled = false;
            final boolean b = false;
            final int n5 = 0;
            switch (n) {
                default: {
                    return super.onTransact(n, parcel, parcel2, n2);
                }
                case 1598968902: {
                    parcel2.writeString("android.support.v4.media.session.IMediaSession");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final String string = parcel.readString();
                    Bundle bundle9;
                    if (parcel.readInt() == 0) {
                        bundle9 = null;
                    }
                    else {
                        bundle9 = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                    }
                    MediaSessionCompat.ResultReceiverWrapper resultReceiverWrapper2;
                    if (parcel.readInt() == 0) {
                        resultReceiverWrapper2 = resultReceiverWrapper;
                    }
                    else {
                        resultReceiverWrapper2 = (MediaSessionCompat.ResultReceiverWrapper)MediaSessionCompat.ResultReceiverWrapper.CREATOR.createFromParcel(parcel);
                    }
                    this.sendCommand(string, bundle9, resultReceiverWrapper2);
                    parcel2.writeNoException();
                    return true;
                }
                case 2: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    KeyEvent keyEvent2;
                    if (parcel.readInt() == 0) {
                        keyEvent2 = keyEvent;
                    }
                    else {
                        keyEvent2 = (KeyEvent)KeyEvent.CREATOR.createFromParcel(parcel);
                    }
                    final boolean sendMediaButton = this.sendMediaButton(keyEvent2);
                    parcel2.writeNoException();
                    if (!sendMediaButton) {
                        n = 0;
                    }
                    else {
                        n = 1;
                    }
                    parcel2.writeInt(n);
                    return true;
                }
                case 3: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.registerCallbackListener(IMediaControllerCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                }
                case 4: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.unregisterCallbackListener(IMediaControllerCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                }
                case 5: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final boolean transportControlEnabled = this.isTransportControlEnabled();
                    parcel2.writeNoException();
                    if (!transportControlEnabled) {
                        n = n5;
                    }
                    else {
                        n = 1;
                    }
                    parcel2.writeInt(n);
                    return true;
                }
                case 6: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final String packageName = this.getPackageName();
                    parcel2.writeNoException();
                    parcel2.writeString(packageName);
                    return true;
                }
                case 7: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final String tag = this.getTag();
                    parcel2.writeNoException();
                    parcel2.writeString(tag);
                    return true;
                }
                case 8: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final PendingIntent launchPendingIntent = this.getLaunchPendingIntent();
                    parcel2.writeNoException();
                    if (launchPendingIntent == null) {
                        parcel2.writeInt(0);
                    }
                    else {
                        parcel2.writeInt(1);
                        launchPendingIntent.writeToParcel(parcel2, 1);
                    }
                    return true;
                }
                case 9: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final long flags = this.getFlags();
                    parcel2.writeNoException();
                    parcel2.writeLong(flags);
                    return true;
                }
                case 10: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final ParcelableVolumeInfo volumeAttributes = this.getVolumeAttributes();
                    parcel2.writeNoException();
                    if (volumeAttributes == null) {
                        parcel2.writeInt(0);
                    }
                    else {
                        parcel2.writeInt(1);
                        volumeAttributes.writeToParcel(parcel2, 1);
                    }
                    return true;
                }
                case 11: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.adjustVolume(parcel.readInt(), parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                }
                case 12: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.setVolumeTo(parcel.readInt(), parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                }
                case 27: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final MediaMetadataCompat metadata = this.getMetadata();
                    parcel2.writeNoException();
                    if (metadata == null) {
                        parcel2.writeInt(0);
                    }
                    else {
                        parcel2.writeInt(1);
                        metadata.writeToParcel(parcel2, 1);
                    }
                    return true;
                }
                case 28: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final PlaybackStateCompat playbackState = this.getPlaybackState();
                    parcel2.writeNoException();
                    if (playbackState == null) {
                        parcel2.writeInt(0);
                    }
                    else {
                        parcel2.writeInt(1);
                        playbackState.writeToParcel(parcel2, 1);
                    }
                    return true;
                }
                case 29: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final List<MediaSessionCompat.QueueItem> queue = this.getQueue();
                    parcel2.writeNoException();
                    parcel2.writeTypedList((List)queue);
                    return true;
                }
                case 30: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final CharSequence queueTitle = this.getQueueTitle();
                    parcel2.writeNoException();
                    if (queueTitle == null) {
                        parcel2.writeInt(0);
                    }
                    else {
                        parcel2.writeInt(1);
                        TextUtils.writeToParcel(queueTitle, parcel2, 1);
                    }
                    return true;
                }
                case 31: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final Bundle extras = this.getExtras();
                    parcel2.writeNoException();
                    if (extras == null) {
                        parcel2.writeInt(0);
                    }
                    else {
                        parcel2.writeInt(1);
                        extras.writeToParcel(parcel2, 1);
                    }
                    return true;
                }
                case 32: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    n = this.getRatingType();
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    return true;
                }
                case 45: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final boolean captioningEnabled2 = this.isCaptioningEnabled();
                    parcel2.writeNoException();
                    if (!captioningEnabled2) {
                        n = n3;
                    }
                    else {
                        n = 1;
                    }
                    parcel2.writeInt(n);
                    return true;
                }
                case 37: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    n = this.getRepeatMode();
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    return true;
                }
                case 38: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final boolean shuffleModeEnabledDeprecated = this.isShuffleModeEnabledDeprecated();
                    parcel2.writeNoException();
                    if (!shuffleModeEnabledDeprecated) {
                        n = n4;
                    }
                    else {
                        n = 1;
                    }
                    parcel2.writeInt(n);
                    return true;
                }
                case 47: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    n = this.getShuffleMode();
                    parcel2.writeNoException();
                    parcel2.writeInt(n);
                    return true;
                }
                case 41: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    MediaDescriptionCompat mediaDescriptionCompat4;
                    if (parcel.readInt() == 0) {
                        mediaDescriptionCompat4 = mediaDescriptionCompat;
                    }
                    else {
                        mediaDescriptionCompat4 = (MediaDescriptionCompat)MediaDescriptionCompat.CREATOR.createFromParcel(parcel);
                    }
                    this.addQueueItem(mediaDescriptionCompat4);
                    parcel2.writeNoException();
                    return true;
                }
                case 42: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    MediaDescriptionCompat mediaDescriptionCompat5;
                    if (parcel.readInt() == 0) {
                        mediaDescriptionCompat5 = mediaDescriptionCompat2;
                    }
                    else {
                        mediaDescriptionCompat5 = (MediaDescriptionCompat)MediaDescriptionCompat.CREATOR.createFromParcel(parcel);
                    }
                    this.addQueueItemAt(mediaDescriptionCompat5, parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 43: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    MediaDescriptionCompat mediaDescriptionCompat6;
                    if (parcel.readInt() == 0) {
                        mediaDescriptionCompat6 = mediaDescriptionCompat3;
                    }
                    else {
                        mediaDescriptionCompat6 = (MediaDescriptionCompat)MediaDescriptionCompat.CREATOR.createFromParcel(parcel);
                    }
                    this.removeQueueItem(mediaDescriptionCompat6);
                    parcel2.writeNoException();
                    return true;
                }
                case 44: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.removeQueueItemAt(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 33: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.prepare();
                    parcel2.writeNoException();
                    return true;
                }
                case 34: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final String string2 = parcel.readString();
                    Bundle bundle10;
                    if (parcel.readInt() == 0) {
                        bundle10 = bundle;
                    }
                    else {
                        bundle10 = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                    }
                    this.prepareFromMediaId(string2, bundle10);
                    parcel2.writeNoException();
                    return true;
                }
                case 35: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final String string3 = parcel.readString();
                    Bundle bundle11;
                    if (parcel.readInt() == 0) {
                        bundle11 = bundle2;
                    }
                    else {
                        bundle11 = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                    }
                    this.prepareFromSearch(string3, bundle11);
                    parcel2.writeNoException();
                    return true;
                }
                case 36: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    Uri uri;
                    if (parcel.readInt() == 0) {
                        uri = null;
                    }
                    else {
                        uri = (Uri)Uri.CREATOR.createFromParcel(parcel);
                    }
                    Bundle bundle12;
                    if (parcel.readInt() == 0) {
                        bundle12 = bundle3;
                    }
                    else {
                        bundle12 = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                    }
                    this.prepareFromUri(uri, bundle12);
                    parcel2.writeNoException();
                    return true;
                }
                case 13: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.play();
                    parcel2.writeNoException();
                    return true;
                }
                case 14: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final String string4 = parcel.readString();
                    Bundle bundle13;
                    if (parcel.readInt() == 0) {
                        bundle13 = bundle4;
                    }
                    else {
                        bundle13 = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                    }
                    this.playFromMediaId(string4, bundle13);
                    parcel2.writeNoException();
                    return true;
                }
                case 15: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final String string5 = parcel.readString();
                    Bundle bundle14;
                    if (parcel.readInt() == 0) {
                        bundle14 = bundle5;
                    }
                    else {
                        bundle14 = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                    }
                    this.playFromSearch(string5, bundle14);
                    parcel2.writeNoException();
                    return true;
                }
                case 16: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    Uri uri2;
                    if (parcel.readInt() == 0) {
                        uri2 = null;
                    }
                    else {
                        uri2 = (Uri)Uri.CREATOR.createFromParcel(parcel);
                    }
                    Bundle bundle15;
                    if (parcel.readInt() == 0) {
                        bundle15 = bundle6;
                    }
                    else {
                        bundle15 = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                    }
                    this.playFromUri(uri2, bundle15);
                    parcel2.writeNoException();
                    return true;
                }
                case 17: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.skipToQueueItem(parcel.readLong());
                    parcel2.writeNoException();
                    return true;
                }
                case 18: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.pause();
                    parcel2.writeNoException();
                    return true;
                }
                case 19: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.stop();
                    parcel2.writeNoException();
                    return true;
                }
                case 20: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.next();
                    parcel2.writeNoException();
                    return true;
                }
                case 21: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.previous();
                    parcel2.writeNoException();
                    return true;
                }
                case 22: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.fastForward();
                    parcel2.writeNoException();
                    return true;
                }
                case 23: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.rewind();
                    parcel2.writeNoException();
                    return true;
                }
                case 24: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.seekTo(parcel.readLong());
                    parcel2.writeNoException();
                    return true;
                }
                case 25: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    RatingCompat ratingCompat2;
                    if (parcel.readInt() == 0) {
                        ratingCompat2 = ratingCompat;
                    }
                    else {
                        ratingCompat2 = (RatingCompat)RatingCompat.CREATOR.createFromParcel(parcel);
                    }
                    this.rate(ratingCompat2);
                    parcel2.writeNoException();
                    return true;
                }
                case 51: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    RatingCompat ratingCompat3;
                    if (parcel.readInt() == 0) {
                        ratingCompat3 = null;
                    }
                    else {
                        ratingCompat3 = (RatingCompat)RatingCompat.CREATOR.createFromParcel(parcel);
                    }
                    Bundle bundle16;
                    if (parcel.readInt() == 0) {
                        bundle16 = bundle7;
                    }
                    else {
                        bundle16 = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                    }
                    this.rateWithExtras(ratingCompat3, bundle16);
                    parcel2.writeNoException();
                    return true;
                }
                case 46: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    if (parcel.readInt() != 0) {
                        captioningEnabled = true;
                    }
                    this.setCaptioningEnabled(captioningEnabled);
                    parcel2.writeNoException();
                    return true;
                }
                case 39: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.setRepeatMode(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 40: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.setShuffleModeEnabledDeprecated(parcel.readInt() != 0 || b);
                    parcel2.writeNoException();
                    return true;
                }
                case 48: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.setShuffleMode(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 26: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    final String string6 = parcel.readString();
                    Bundle bundle17;
                    if (parcel.readInt() == 0) {
                        bundle17 = bundle8;
                    }
                    else {
                        bundle17 = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                    }
                    this.sendCustomAction(string6, bundle17);
                    parcel2.writeNoException();
                    return true;
                }
            }
        }
        
        private static class Proxy implements IMediaSession
        {
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            @Override
            public void addQueueItem(final MediaDescriptionCompat mediaDescriptionCompat) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    if (mediaDescriptionCompat == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        mediaDescriptionCompat.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(41, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void addQueueItemAt(final MediaDescriptionCompat mediaDescriptionCompat, final int n) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    if (mediaDescriptionCompat == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        mediaDescriptionCompat.writeToParcel(obtain, 0);
                    }
                    obtain.writeInt(n);
                    this.mRemote.transact(42, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void adjustVolume(final int n, final int n2, final String s) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    obtain.writeInt(n);
                    obtain.writeInt(n2);
                    obtain.writeString(s);
                    this.mRemote.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            @Override
            public void fastForward() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(22, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public Bundle getExtras() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(31, obtain, obtain2, 0);
                    obtain2.readException();
                    Bundle bundle;
                    if (obtain2.readInt() == 0) {
                        bundle = null;
                    }
                    else {
                        bundle = (Bundle)Bundle.CREATOR.createFromParcel(obtain2);
                    }
                    return bundle;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public long getFlags() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readLong();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            public String getInterfaceDescriptor() {
                return "android.support.v4.media.session.IMediaSession";
            }
            
            @Override
            public PendingIntent getLaunchPendingIntent() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                    PendingIntent pendingIntent;
                    if (obtain2.readInt() == 0) {
                        pendingIntent = null;
                    }
                    else {
                        pendingIntent = (PendingIntent)PendingIntent.CREATOR.createFromParcel(obtain2);
                    }
                    return pendingIntent;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public MediaMetadataCompat getMetadata() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(27, obtain, obtain2, 0);
                    obtain2.readException();
                    MediaMetadataCompat mediaMetadataCompat;
                    if (obtain2.readInt() == 0) {
                        mediaMetadataCompat = null;
                    }
                    else {
                        mediaMetadataCompat = (MediaMetadataCompat)MediaMetadataCompat.CREATOR.createFromParcel(obtain2);
                    }
                    return mediaMetadataCompat;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public String getPackageName() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public PlaybackStateCompat getPlaybackState() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(28, obtain, obtain2, 0);
                    obtain2.readException();
                    PlaybackStateCompat playbackStateCompat;
                    if (obtain2.readInt() == 0) {
                        playbackStateCompat = null;
                    }
                    else {
                        playbackStateCompat = (PlaybackStateCompat)PlaybackStateCompat.CREATOR.createFromParcel(obtain2);
                    }
                    return playbackStateCompat;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public List<MediaSessionCompat.QueueItem> getQueue() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(29, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createTypedArrayList((Parcelable$Creator)MediaSessionCompat.QueueItem.CREATOR);
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public CharSequence getQueueTitle() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(30, obtain, obtain2, 0);
                    obtain2.readException();
                    CharSequence charSequence;
                    if (obtain2.readInt() == 0) {
                        charSequence = null;
                    }
                    else {
                        charSequence = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(obtain2);
                    }
                    return charSequence;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public int getRatingType() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(32, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public int getRepeatMode() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(37, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public int getShuffleMode() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(47, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public String getTag() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public ParcelableVolumeInfo getVolumeAttributes() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                    ParcelableVolumeInfo parcelableVolumeInfo;
                    if (obtain2.readInt() == 0) {
                        parcelableVolumeInfo = null;
                    }
                    else {
                        parcelableVolumeInfo = (ParcelableVolumeInfo)ParcelableVolumeInfo.CREATOR.createFromParcel(obtain2);
                    }
                    return parcelableVolumeInfo;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public boolean isCaptioningEnabled() throws RemoteException {
                boolean b = false;
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(45, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        b = true;
                    }
                    return b;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public boolean isShuffleModeEnabledDeprecated() throws RemoteException {
                boolean b = false;
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(38, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        b = true;
                    }
                    return b;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public boolean isTransportControlEnabled() throws RemoteException {
                boolean b = false;
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        b = true;
                    }
                    return b;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void next() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(20, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void pause() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(18, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void play() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(13, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void playFromMediaId(final String s, final Bundle bundle) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    obtain.writeString(s);
                    if (bundle == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(14, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void playFromSearch(final String s, final Bundle bundle) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    obtain.writeString(s);
                    if (bundle == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(15, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void playFromUri(final Uri uri, final Bundle bundle) throws RemoteException {
                while (true) {
                    final Parcel obtain = Parcel.obtain();
                    final Parcel obtain2 = Parcel.obtain();
                    while (true) {
                        try {
                            obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                            if (uri == null) {
                                obtain.writeInt(0);
                            }
                            else {
                                obtain.writeInt(1);
                                uri.writeToParcel(obtain, 0);
                            }
                            if (bundle == null) {
                                obtain.writeInt(0);
                                this.mRemote.transact(16, obtain, obtain2, 0);
                                obtain2.readException();
                                return;
                            }
                        }
                        finally {
                            obtain2.recycle();
                            obtain.recycle();
                        }
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                        continue;
                    }
                }
            }
            
            @Override
            public void prepare() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(33, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void prepareFromMediaId(final String s, final Bundle bundle) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    obtain.writeString(s);
                    if (bundle == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(34, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void prepareFromSearch(final String s, final Bundle bundle) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    obtain.writeString(s);
                    if (bundle == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(35, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void prepareFromUri(final Uri uri, final Bundle bundle) throws RemoteException {
                while (true) {
                    final Parcel obtain = Parcel.obtain();
                    final Parcel obtain2 = Parcel.obtain();
                    while (true) {
                        try {
                            obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                            if (uri == null) {
                                obtain.writeInt(0);
                            }
                            else {
                                obtain.writeInt(1);
                                uri.writeToParcel(obtain, 0);
                            }
                            if (bundle == null) {
                                obtain.writeInt(0);
                                this.mRemote.transact(36, obtain, obtain2, 0);
                                obtain2.readException();
                                return;
                            }
                        }
                        finally {
                            obtain2.recycle();
                            obtain.recycle();
                        }
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                        continue;
                    }
                }
            }
            
            @Override
            public void previous() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(21, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void rate(final RatingCompat ratingCompat) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    if (ratingCompat == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        ratingCompat.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(25, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void rateWithExtras(final RatingCompat ratingCompat, final Bundle bundle) throws RemoteException {
                while (true) {
                    final Parcel obtain = Parcel.obtain();
                    final Parcel obtain2 = Parcel.obtain();
                    while (true) {
                        try {
                            obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                            if (ratingCompat == null) {
                                obtain.writeInt(0);
                            }
                            else {
                                obtain.writeInt(1);
                                ratingCompat.writeToParcel(obtain, 0);
                            }
                            if (bundle == null) {
                                obtain.writeInt(0);
                                this.mRemote.transact(51, obtain, obtain2, 0);
                                obtain2.readException();
                                return;
                            }
                        }
                        finally {
                            obtain2.recycle();
                            obtain.recycle();
                        }
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                        continue;
                    }
                }
            }
            
            @Override
            public void registerCallbackListener(final IMediaControllerCallback mediaControllerCallback) throws RemoteException {
                final IBinder binder = null;
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    IBinder binder2;
                    if (mediaControllerCallback == null) {
                        binder2 = binder;
                    }
                    else {
                        binder2 = mediaControllerCallback.asBinder();
                    }
                    obtain.writeStrongBinder(binder2);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void removeQueueItem(final MediaDescriptionCompat mediaDescriptionCompat) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    if (mediaDescriptionCompat == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        mediaDescriptionCompat.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(43, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void removeQueueItemAt(final int n) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    obtain.writeInt(n);
                    this.mRemote.transact(44, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void rewind() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(23, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void seekTo(final long n) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    obtain.writeLong(n);
                    this.mRemote.transact(24, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void sendCommand(final String s, final Bundle bundle, final MediaSessionCompat.ResultReceiverWrapper resultReceiverWrapper) throws RemoteException {
                while (true) {
                    final Parcel obtain = Parcel.obtain();
                    final Parcel obtain2 = Parcel.obtain();
                    while (true) {
                        try {
                            obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                            obtain.writeString(s);
                            if (bundle == null) {
                                obtain.writeInt(0);
                            }
                            else {
                                obtain.writeInt(1);
                                bundle.writeToParcel(obtain, 0);
                            }
                            if (resultReceiverWrapper == null) {
                                obtain.writeInt(0);
                                this.mRemote.transact(1, obtain, obtain2, 0);
                                obtain2.readException();
                                return;
                            }
                        }
                        finally {
                            obtain2.recycle();
                            obtain.recycle();
                        }
                        obtain.writeInt(1);
                        resultReceiverWrapper.writeToParcel(obtain, 0);
                        continue;
                    }
                }
            }
            
            @Override
            public void sendCustomAction(final String s, final Bundle bundle) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    obtain.writeString(s);
                    if (bundle == null) {
                        obtain.writeInt(0);
                    }
                    else {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    this.mRemote.transact(26, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public boolean sendMediaButton(final KeyEvent keyEvent) throws RemoteException {
                while (true) {
                    boolean b = false;
                    final Parcel obtain = Parcel.obtain();
                    final Parcel obtain2 = Parcel.obtain();
                    try {
                        obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                        if (keyEvent == null) {
                            obtain.writeInt(0);
                        }
                        else {
                            obtain.writeInt(1);
                            keyEvent.writeToParcel(obtain, 0);
                        }
                        this.mRemote.transact(2, obtain, obtain2, 0);
                        obtain2.readException();
                        if (obtain2.readInt() == 0) {
                            return b;
                        }
                    }
                    finally {
                        obtain2.recycle();
                        obtain.recycle();
                    }
                    b = true;
                    return b;
                }
            }
            
            @Override
            public void setCaptioningEnabled(final boolean b) throws RemoteException {
                int n = 0;
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    if (b) {
                        n = 1;
                    }
                    obtain.writeInt(n);
                    this.mRemote.transact(46, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void setRepeatMode(final int n) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    obtain.writeInt(n);
                    this.mRemote.transact(39, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void setShuffleMode(final int n) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    obtain.writeInt(n);
                    this.mRemote.transact(48, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void setShuffleModeEnabledDeprecated(final boolean b) throws RemoteException {
                int n = 0;
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    if (b) {
                        n = 1;
                    }
                    obtain.writeInt(n);
                    this.mRemote.transact(40, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void setVolumeTo(final int n, final int n2, final String s) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    obtain.writeInt(n);
                    obtain.writeInt(n2);
                    obtain.writeString(s);
                    this.mRemote.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void skipToQueueItem(final long n) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    obtain.writeLong(n);
                    this.mRemote.transact(17, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void stop() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(19, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void unregisterCallbackListener(final IMediaControllerCallback mediaControllerCallback) throws RemoteException {
                final IBinder binder = null;
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    IBinder binder2;
                    if (mediaControllerCallback == null) {
                        binder2 = binder;
                    }
                    else {
                        binder2 = mediaControllerCallback.asBinder();
                    }
                    obtain.writeStrongBinder(binder2);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
