package com.google.common.io;

import java.io.IOException;
@Deprecated
/* loaded from: classes.dex */
public interface InputSupplier<T> {
    T getInput() throws IOException;
}
