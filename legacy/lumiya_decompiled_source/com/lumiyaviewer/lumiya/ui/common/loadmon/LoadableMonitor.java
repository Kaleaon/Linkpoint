package com.lumiyaviewer.lumiya.ui.common.loadmon;

import android.support.v4.widget.SwipeRefreshLayout;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.react.RefreshableOne;
import com.lumiyaviewer.lumiya.react.UnsubscribableOne;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class LoadableMonitor implements Loadable.LoadableStatusListener, SwipeRefreshLayout.OnRefreshListener {

    /* renamed from: -com-lumiyaviewer-lumiya-ui-common-loadmon-Loadable$StatusSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f379xeb9cc37f = null;
    private final List<Loadable> loadables = new ArrayList();
    private final List<Loadable> optionalLoadables = new ArrayList();
    @Nonnull
    private Loadable.Status status = Loadable.Status.Idle;
    @Nullable
    private OnLoadableDataChangedListener onLoadableDataChangedListener = null;
    @Nullable
    private LoadingLayout loadingLayout = null;
    @Nullable
    private SwipeRefreshLayout swipeRefreshLayout = null;
    @Nullable
    private String loadingIdleMessage = null;
    @Nullable
    private String loadingErrorMessage = null;
    @Nullable
    private String emptyMessage = null;
    private boolean isExtraLoading = false;

    /* loaded from: classes.dex */
    public interface OnLoadableDataChangedListener {
        void onLoadableDataChanged();
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-ui-common-loadmon-Loadable$StatusSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m590x4f18d023() {
        if (f379xeb9cc37f != null) {
            return f379xeb9cc37f;
        }
        int[] iArr = new int[Loadable.Status.valuesCustom().length];
        try {
            iArr[Loadable.Status.Error.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[Loadable.Status.Idle.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[Loadable.Status.Loaded.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[Loadable.Status.Loading.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        f379xeb9cc37f = iArr;
        return iArr;
    }

    public LoadableMonitor(Loadable... loadableArr) {
        Collections.addAll(this.loadables, loadableArr);
        for (Loadable loadable : this.loadables) {
            loadable.addLoadableStatusListener(this);
        }
    }

    private void updateLoadingIndicator() {
        if (this.loadingLayout != null) {
            switch (m590x4f18d023()[this.status.ordinal()]) {
                case 1:
                    this.loadingLayout.showMessage(Strings.nullToEmpty(this.loadingErrorMessage));
                    return;
                case 2:
                    this.loadingLayout.showMessage(Strings.nullToEmpty(this.loadingIdleMessage));
                    return;
                case 3:
                    this.loadingLayout.showContent(this.emptyMessage);
                    return;
                case 4:
                    this.loadingLayout.showLoading();
                    return;
                default:
                    return;
            }
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable.LoadableStatusListener
    public void onLoadableStatusChange(Loadable loadable, Loadable.Status status) {
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        for (Loadable loadable2 : this.loadables) {
            Loadable.Status loadableStatus = loadable2.getLoadableStatus();
            switch (m590x4f18d023()[loadableStatus.ordinal()]) {
                case 1:
                    z2 = true;
                    break;
                case 4:
                    z3 = true;
                    break;
            }
            z = loadableStatus != Loadable.Status.Loaded ? true : z;
        }
        Loadable.Status status2 = (z3 || this.isExtraLoading) ? Loadable.Status.Loading : z2 ? Loadable.Status.Error : !z ? Loadable.Status.Loaded : Loadable.Status.Idle;
        if (status2 != this.status) {
            this.status = status2;
            updateLoadingIndicator();
        }
        if (!z3 && this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setRefreshing(false);
        }
        if (this.status != Loadable.Status.Loaded || this.onLoadableDataChangedListener == null) {
            return;
        }
        this.onLoadableDataChangedListener.onLoadableDataChanged();
    }

    @Override // android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
    public void onRefresh() {
        for (Loadable loadable : this.loadables) {
            if (loadable instanceof RefreshableOne) {
                ((RefreshableOne) loadable).requestRefresh();
            }
        }
        for (Loadable loadable2 : this.optionalLoadables) {
            if (loadable2 instanceof RefreshableOne) {
                ((RefreshableOne) loadable2).requestRefresh();
            }
        }
    }

    public void setButteryProgressBar(boolean z) {
        if (this.loadingLayout != null) {
            this.loadingLayout.setButteryProgressBar(z);
        }
    }

    public void setEmptyMessage(boolean z, @Nullable String str) {
        if (!z) {
            str = null;
        }
        this.emptyMessage = str;
        updateLoadingIndicator();
    }

    public void setExtraLoading(boolean z) {
        this.isExtraLoading = z;
        onLoadableStatusChange(null, null);
    }

    public void setLoadingLayout(@Nullable LoadingLayout loadingLayout, @Nullable String str, @Nullable String str2) {
        this.loadingLayout = loadingLayout;
        this.loadingIdleMessage = str;
        this.loadingErrorMessage = str2;
        updateLoadingIndicator();
    }

    public void setSwipeRefreshLayout(@Nullable SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(this);
        }
    }

    public void unsubscribeAll() {
        for (Loadable loadable : this.loadables) {
            if (loadable instanceof UnsubscribableOne) {
                ((UnsubscribableOne) loadable).unsubscribe();
            }
        }
        for (Loadable loadable2 : this.optionalLoadables) {
            if (loadable2 instanceof UnsubscribableOne) {
                ((UnsubscribableOne) loadable2).unsubscribe();
            }
        }
    }

    public LoadableMonitor withDataChangedListener(OnLoadableDataChangedListener onLoadableDataChangedListener) {
        this.onLoadableDataChangedListener = onLoadableDataChangedListener;
        return this;
    }

    public LoadableMonitor withOptionalLoadables(Loadable... loadableArr) {
        Collections.addAll(this.optionalLoadables, loadableArr);
        for (Loadable loadable : loadableArr) {
            loadable.addLoadableStatusListener(this);
        }
        return this;
    }
}
