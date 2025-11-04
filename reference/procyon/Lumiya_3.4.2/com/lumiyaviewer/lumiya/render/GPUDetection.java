// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.common.base.Strings;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import com.google.common.base.Optional;

public class GPUDetection
{
    public static final String GPU_FAMILY_ADRENO = "Adreno";
    public static final String GPU_FAMILY_TEGRA = "Tegra";
    public static final int INVALID_VERSION = -1;
    @Nonnull
    public final Optional<String> detectedFamily;
    public final int detectedNumericVersion;
    @Nonnull
    public final Optional<String> detectedVersion;
    
    public GPUDetection(@Nullable String nullToEmpty) {
        nullToEmpty = Strings.nullToEmpty(nullToEmpty);
        Label_0091: {
            Matcher matcher;
            if (nullToEmpty.toLowerCase().contains("adreno")) {
                this.detectedFamily = Optional.of("Adreno");
                matcher = Pattern.compile(".*?Adreno.*?([0-9]+).*?", 2).matcher(nullToEmpty);
                if (!matcher.matches()) {
                    break Label_0091;
                }
            }
            else {
                if (nullToEmpty.toLowerCase().contains("tegra")) {
                    this.detectedFamily = Optional.of("Tegra");
                    this.detectedVersion = Optional.absent();
                    this.detectedNumericVersion = -1;
                    return;
                }
                this.detectedFamily = Optional.absent();
                this.detectedVersion = Optional.absent();
                this.detectedNumericVersion = -1;
                return;
            }
            this.detectedVersion = Optional.fromNullable(Strings.emptyToNull(matcher.group(1)));
            while (true) {
                try {
                    final int int1 = Integer.parseInt(this.detectedVersion.or(""));
                    this.detectedNumericVersion = int1;
                    return;
                }
                catch (final NumberFormatException ex) {
                    final int int1 = -1;
                    continue;
                }
                break;
            }
        }
        this.detectedVersion = Optional.absent();
        this.detectedNumericVersion = -1;
    }
}
