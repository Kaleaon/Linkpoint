package com.google.common.io;

import java.io.IOException;

@Deprecated
public interface InputSupplier<T> {
    T getInput() throws IOException;
}
