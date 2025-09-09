package android.support.v4.text;

import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import java.util.Locale;

public final class ICUCompat {
    private static final ICUCompatBaseImpl IMPL;

    @RequiresApi(21)
    static class ICUCompatApi21Impl extends ICUCompatBaseImpl {
        ICUCompatApi21Impl() {
        }

        public String maximizeAndGetScript(Locale locale) {
            return ICUCompatApi21.maximizeAndGetScript(locale);
        }
    }

    static class ICUCompatBaseImpl {
        ICUCompatBaseImpl() {
        }

        public String maximizeAndGetScript(Locale locale) {
            return ICUCompatIcs.maximizeAndGetScript(locale);
        }
    }

    static {
        if (Build.VERSION.SDK_INT < 21) {
            IMPL = new ICUCompatBaseImpl();
        } else {
            IMPL = new ICUCompatApi21Impl();
        }
    }

    private ICUCompat() {
    }

    @Nullable
    public static String maximizeAndGetScript(Locale locale) {
        return IMPL.maximizeAndGetScript(locale);
    }
}
