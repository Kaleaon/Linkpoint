package android.support.v4.os;

import android.content.res.Configuration;
import android.os.Build;
/* loaded from: classes.dex */
public final class ConfigurationCompat {
    private ConfigurationCompat() {
    }

    public static LocaleListCompat getLocales(Configuration configuration) {
        return Build.VERSION.SDK_INT < 24 ? LocaleListCompat.create(configuration.locale) : LocaleListCompat.wrap(configuration.getLocales());
    }
}
