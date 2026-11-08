package android.support.v4.media.session;

import android.media.session.MediaSession;
import android.support.annotation.RequiresApi;
@RequiresApi(22)
/* loaded from: classes.dex */
class MediaSessionCompatApi22 {
    MediaSessionCompatApi22() {
    }

    public static void setRatingType(Object obj, int i) {
        ((MediaSession) obj).setRatingType(i);
    }
}
