package com.lumiyaviewer.lumiya.ui.common.loadmon;
import java.util.*;

import javax.annotation.Nonnull;

public interface Loadable {

    public interface LoadableStatusListener {
        void onLoadableStatusChange(Loadable loadable, Status status);
    }

    public enum Status {
        Idle,
        Loading,
        Loaded,
        Error
    }

    void addLoadableStatusListener(LoadableStatusListener loadableStatusListener);

    @Nonnull
    Status getLoadableStatus();
}
