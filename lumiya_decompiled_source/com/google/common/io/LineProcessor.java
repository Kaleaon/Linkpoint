package com.google.common.io;

import com.google.common.annotations.Beta;
import java.io.IOException;
@Beta
/* loaded from: classes.dex */
public interface LineProcessor<T> {
    T getResult();

    boolean processLine(String str) throws IOException;
}
