package com.google.common.io;

import java.io.IOException;
@Deprecated
/* loaded from: classes.dex */
public interface OutputSupplier<T> {
    T getOutput() throws IOException;
}
