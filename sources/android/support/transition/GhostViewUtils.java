package android.support.transition;

import android.graphics.Matrix;
import android.os.Build;
import android.support.transition.GhostViewApi14;
import android.support.transition.GhostViewApi21;
import android.support.transition.GhostViewImpl;
import android.view.View;
import android.view.ViewGroup;

class GhostViewUtils {
    private static final GhostViewImpl.Creator CREATOR;

    static {
        if (Build.VERSION.SDK_INT < 21) {
            CREATOR = new GhostViewApi14.Creator();
        } else {
            CREATOR = new GhostViewApi21.Creator();
        }
    }

    GhostViewUtils() {
    }

    static GhostViewImpl addGhost(View view, ViewGroup viewGroup, Matrix matrix) {
        return CREATOR.addGhost(view, viewGroup, matrix);
    }

    static void removeGhost(View view) {
        CREATOR.removeGhost(view);
    }
}
