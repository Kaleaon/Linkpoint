package com.lumiyaviewer.lumiya.render;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GPUDetection {
    public static final String GPU_FAMILY_ADRENO = "Adreno";
    public static final String GPU_FAMILY_TEGRA = "Tegra";
    public static final int INVALID_VERSION = -1;
    @Nonnull
    public final Optional<String> detectedFamily;
    public final int detectedNumericVersion;
    @Nonnull
    public final Optional<String> detectedVersion;

    public GPUDetection(@Nullable String str) {
        int i;
        String nullToEmpty = Strings.nullToEmpty(str);
        if (nullToEmpty.toLowerCase().contains("adreno")) {
            this.detectedFamily = Optional.of(GPU_FAMILY_ADRENO);
            Matcher matcher = Pattern.compile(".*?Adreno.*?([0-9]+).*?", 2).matcher(nullToEmpty);
            if (matcher.matches()) {
                this.detectedVersion = Optional.fromNullable(Strings.emptyToNull(matcher.group(1)));
                try {
                    i = Integer.parseInt(this.detectedVersion.or(""));
                } catch (NumberFormatException e) {
                    i = -1;
                }
                this.detectedNumericVersion = i;
                return;
            }
            this.detectedVersion = Optional.absent();
            this.detectedNumericVersion = -1;
        } else if (nullToEmpty.toLowerCase().contains("tegra")) {
            this.detectedFamily = Optional.of(GPU_FAMILY_TEGRA);
            this.detectedVersion = Optional.absent();
            this.detectedNumericVersion = -1;
        } else {
            this.detectedFamily = Optional.absent();
            this.detectedVersion = Optional.absent();
            this.detectedNumericVersion = -1;
        }
    }
}
