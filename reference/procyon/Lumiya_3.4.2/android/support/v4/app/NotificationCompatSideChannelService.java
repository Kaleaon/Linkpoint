// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import android.os.RemoteException;
import android.os.Build$VERSION;
import android.os.IBinder;
import android.content.Intent;
import android.app.Notification;
import android.app.Service;

public abstract class NotificationCompatSideChannelService extends Service
{
    public abstract void cancel(final String p0, final int p1, final String p2);
    
    public abstract void cancelAll(final String p0);
    
    void checkPermission(final int i, final String s) {
        int j = 0;
        for (String[] packagesForUid = this.getPackageManager().getPackagesForUid(i); j < packagesForUid.length; ++j) {
            if (packagesForUid[j].equals(s)) {
                return;
            }
        }
        throw new SecurityException("NotificationSideChannelService: Uid " + i + " is not authorized for package " + s);
    }
    
    public abstract void notify(final String p0, final int p1, final String p2, final Notification p3);
    
    public IBinder onBind(final Intent intent) {
        if (!intent.getAction().equals("android.support.BIND_NOTIFICATION_SIDE_CHANNEL")) {
            return null;
        }
        if (Build$VERSION.SDK_INT <= 19) {
            return (IBinder)new NotificationSideChannelStub();
        }
        return null;
    }
    
    private class NotificationSideChannelStub extends Stub
    {
        NotificationSideChannelStub() {
        }
        
        public void cancel(final String s, final int n, final String s2) throws RemoteException {
            NotificationCompatSideChannelService.this.checkPermission(getCallingUid(), s);
            final long clearCallingIdentity = clearCallingIdentity();
            try {
                NotificationCompatSideChannelService.this.cancel(s, n, s2);
            }
            finally {
                restoreCallingIdentity(clearCallingIdentity);
            }
        }
        
        public void cancelAll(final String s) {
            NotificationCompatSideChannelService.this.checkPermission(getCallingUid(), s);
            final long clearCallingIdentity = clearCallingIdentity();
            try {
                NotificationCompatSideChannelService.this.cancelAll(s);
            }
            finally {
                restoreCallingIdentity(clearCallingIdentity);
            }
        }
        
        public void notify(final String s, final int n, final String s2, final Notification notification) throws RemoteException {
            NotificationCompatSideChannelService.this.checkPermission(getCallingUid(), s);
            final long clearCallingIdentity = clearCallingIdentity();
            try {
                NotificationCompatSideChannelService.this.notify(s, n, s2, notification);
            }
            finally {
                restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }
}
