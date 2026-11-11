package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import javax.annotation.Nullable;
/* JADX INFO: Access modifiers changed from: package-private */
@GwtCompatible
/* loaded from: classes.dex */
public final class Count implements Serializable {
    private int value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Count(int i) {
        this.value = i;
    }

    public int addAndGet(int i) {
        int i2 = this.value + i;
        this.value = i2;
        return i2;
    }

    public boolean equals(@Nullable Object obj) {
        return (obj instanceof Count) && ((Count) obj).value == this.value;
    }

    public int get() {
        return this.value;
    }

    public int getAndAdd(int i) {
        int i2 = this.value;
        this.value = i2 + i;
        return i2;
    }

    public int getAndSet(int i) {
        int i2 = this.value;
        this.value = i;
        return i2;
    }

    public int hashCode() {
        return this.value;
    }

    public void set(int i) {
        this.value = i;
    }

    public String toString() {
        return Integer.toString(this.value);
    }
}
