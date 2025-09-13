package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.base.Objects;
import javax.annotation.Nullable;

public abstract class HoverText {
    public static HoverText create(String str, int i) {
        return new AutoValue_HoverText(str, i);
    }

    public abstract int color();

    public final boolean sameText(@Nullable HoverText hoverText) {
        String str = null;
        String text = text();
        if (hoverText != null) {
            str = hoverText.text();
        }
        return Objects.equal(text, str);
    }

    public abstract String text();
}
