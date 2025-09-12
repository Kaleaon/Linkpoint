// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GPUDetection
{

    public static final String GPU_FAMILY_ADRENO = "Adreno";
    public static final String GPU_FAMILY_TEGRA = "Tegra";
    public static final int INVALID_VERSION = -1;
    public final Optional detectedFamily;
    public final int detectedNumericVersion;
    public final Optional detectedVersion;

    public GPUDetection(String s)
    {
        s = Strings.nullToEmpty(s);
        if (s.toLowerCase().contains("adreno"))
        {
            detectedFamily = Optional.of("Adreno");
            s = Pattern.compile(".*?Adreno.*?([0-9]+).*?", 2).matcher(s);
            if (s.matches())
            {
                detectedVersion = Optional.fromNullable(Strings.emptyToNull(s.group(1)));
                int i;
                try
                {
                    i = Integer.parseInt((String)detectedVersion.or(""));
                }
                // Misplaced declaration of an exception variable
                catch (String s)
                {
                    i = -1;
                }
                detectedNumericVersion = i;
                return;
            } else
            {
                detectedVersion = Optional.absent();
                detectedNumericVersion = -1;
                return;
            }
        }
        if (s.toLowerCase().contains("tegra"))
        {
            detectedFamily = Optional.of("Tegra");
            detectedVersion = Optional.absent();
            detectedNumericVersion = -1;
            return;
        } else
        {
            detectedFamily = Optional.absent();
            detectedVersion = Optional.absent();
            detectedNumericVersion = -1;
            return;
        }
    }
}
