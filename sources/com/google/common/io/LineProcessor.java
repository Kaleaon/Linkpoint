package com.google.common.io;

import com.google.common.annotations.Beta;
import java.io.IOException;

@Beta
public interface LineProcessor<T> {
    T getResult();

    boolean processLine(String str) throws IOException;
}
