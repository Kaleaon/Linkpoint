package butterknife;

import android.support.annotation.UiThread;
/* loaded from: classes.dex */
public interface Unbinder {
    public static final Unbinder EMPTY = new Unbinder() { // from class: butterknife.Unbinder.1
        @Override // butterknife.Unbinder
        public void unbind() {
        }
    };

    @UiThread
    void unbind();
}
