package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.base.Objects;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public abstract class HoverText {
    public static HoverText create(String str, int i) {
        return new AutoValue_HoverText(str, i);
    }

    public abstract int color();

    public final boolean sameText(@Nullable HoverText hoverText) {
        return Objects.equal(text(), hoverText != null ? hoverText.text() : null);
    }

    public abstract String text();
}
