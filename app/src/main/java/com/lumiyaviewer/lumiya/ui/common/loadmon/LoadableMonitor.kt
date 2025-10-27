package com.lumiyaviewer.lumiya.ui.common.loadmon

import android.support.v4.widget.SwipeRefreshLayout
import com.google.common.base.Strings
import com.lumiyaviewer.lumiya.react.RefreshableOne
import com.lumiyaviewer.lumiya.react.UnsubscribableOne
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable
import java.util.ArrayList
import java.util.Collections
import java.util.List
import javax.annotation.Nonnull
import javax.annotation.Nullable

class LoadableMonitor : Loadable.LoadableStatusListener, SwipeRefreshLayout.OnRefreshListener {

    /* renamed from: -com-lumiyaviewer-lumiya-ui-common-loadmon-Loadable$StatusSwitchesValues  reason: not valid java name */
    private /* synthetic */ IntArray f379comlumiyaviewerlumiyauicommonloadmonLoadable$StatusSwitchesValues = null
    @Nullable
    private val emptyMessage: String = null
    private Boolean isExtraLoading = false
    private List<Loadable> loadables = ArrayList()
    @Nullable
    private val loadingErrorMessage: String = null
    @Nullable
    private val loadingIdleMessage: String = null
    @Nullable
    private LoadingLayout loadingLayout = null
    @Nullable
    private OnLoadableDataChangedListener onLoadableDataChangedListener = null
    private List<Loadable> optionalLoadables = ArrayList()
    @Nonnull
    private Loadable.Status status = Loadable.Status.Idle
    @Nullable
    private SwipeRefreshLayout swipeRefreshLayout = null

    interface OnLoadableDataChangedListener {
        Unit onLoadableDataChanged()
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-ui-common-loadmon-Loadable$StatusSwitchesValues  reason: not valid java name */
    private /* synthetic */ IntArray m579getcomlumiyaviewerlumiyauicommonloadmonLoadable$StatusSwitchesValues() {
        if (f379comlumiyaviewerlumiyauicommonloadmonLoadable$StatusSwitchesValues != null) {
            return f379comlumiyaviewerlumiyauicommonloadmonLoadable$StatusSwitchesValues
        }
        IntArray iArr = Int[Loadable.Status.values().length]
        try {
            iArr[Loadable.Status.Error.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[Loadable.Status.Idle.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[Loadable.Status.Loaded.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[Loadable.Status.Loading.ordinal()] = 4
        } catch (NoSuchFieldError e4) {
        }
        f379comlumiyaviewerlumiyauicommonloadmonLoadable$StatusSwitchesValues = iArr
        return iArr
    }

    LoadableMonitor(Loadable... loadableArr) {
        Collections.addAll(this.loadables, loadableArr)
        for (Loadable addLoadableStatusListener : this.loadables) {
            addLoadableStatusListener.addLoadableStatusListener(this)
        }
    }

    private Unit updateLoadingIndicator() {
        if (this.loadingLayout != null) {
            switch (m579getcomlumiyaviewerlumiyauicommonloadmonLoadable$StatusSwitchesValues()[this.status.ordinal()]) {
                case 1:
                    this.loadingLayout.showMessage(Strings.nullToEmpty(this.loadingErrorMessage))
                    return
                case 2:
                    this.loadingLayout.showMessage(Strings.nullToEmpty(this.loadingIdleMessage))
                    return
                case 3:
                    this.loadingLayout.showContent(this.emptyMessage)
                    return
                case 4:
                    this.loadingLayout.showLoading()
                    return
                default:
                    return
            }
        }
    }

    Unit onLoadableStatusChange(Loadable loadable, Loadable.Status status2) {
        Boolean z = false
        Boolean z2 = false
        Boolean z3 = false
        for (Loadable loadableStatus : this.loadables) {
            Loadable.Status loadableStatus2 = loadableStatus.getLoadableStatus()
            switch (m579getcomlumiyaviewerlumiyauicommonloadmonLoadable$StatusSwitchesValues()[loadableStatus2.ordinal()]) {
                case 1:
                    z2 = true
                    break
                case 4:
                    z3 = true
                    break
            }
            z = loadableStatus2 != Loadable.Status.Loaded ? true : z
        }
        Loadable.Status status3 = (z3 || this.isExtraLoading) ? Loadable.Status.Loading : z2 ? Loadable.Status.Error : !z ? Loadable.Status.Loaded : Loadable.Status.Idle
        if (status3 != this.status) {
            this.status = status3
            updateLoadingIndicator()
        }
        if (!z3 && this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setRefreshing(false)
        }
        if (this.status == Loadable.Status.Loaded && this.onLoadableDataChangedListener != null) {
            this.onLoadableDataChangedListener.onLoadableDataChanged()
        }
    }

    Unit onRefresh() {
        for (Loadable loadable : this.loadables) {
            if (loadable instanceof RefreshableOne) {
                ((RefreshableOne) loadable).requestRefresh()
            }
        }
        for (Loadable loadable2 : this.optionalLoadables) {
            if (loadable2 instanceof RefreshableOne) {
                ((RefreshableOne) loadable2).requestRefresh()
            }
        }
    }

    Unit setButteryProgressBar(Boolean z) {
        if (this.loadingLayout != null) {
            this.loadingLayout.setButteryProgressBar(z)
        }
    }

    Unit setEmptyMessage(Boolean z, @Nullable String str) {
        if (!z) {
            str = null
        }
        this.emptyMessage = str
        updateLoadingIndicator()
    }

    Unit setExtraLoading(Boolean z) {
        this.isExtraLoading = z
        onLoadableStatusChange((Loadable) null, (Loadable.Status) null)
    }

    Unit setLoadingLayout(@Nullable LoadingLayout loadingLayout2, @Nullable String str, @Nullable String str2) {
        this.loadingLayout = loadingLayout2
        this.loadingIdleMessage = str
        this.loadingErrorMessage = str2
        updateLoadingIndicator()
    }

    Unit setSwipeRefreshLayout(@Nullable SwipeRefreshLayout swipeRefreshLayout2) {
        this.swipeRefreshLayout = swipeRefreshLayout2
        if (swipeRefreshLayout2 != null) {
            swipeRefreshLayout2.setOnRefreshListener(this)
        }
    }

    Unit unsubscribeAll() {
        for (Loadable loadable : this.loadables) {
            if (loadable instanceof UnsubscribableOne) {
                ((UnsubscribableOne) loadable).unsubscribe()
            }
        }
        for (Loadable loadable2 : this.optionalLoadables) {
            if (loadable2 instanceof UnsubscribableOne) {
                ((UnsubscribableOne) loadable2).unsubscribe()
            }
        }
    }

    LoadableMonitor withDataChangedListener(OnLoadableDataChangedListener onLoadableDataChangedListener2) {
        this.onLoadableDataChangedListener = onLoadableDataChangedListener2
        return this
    }

    LoadableMonitor withOptionalLoadables(Loadable... loadableArr) {
        Collections.addAll(this.optionalLoadables, loadableArr)
        for (Loadable addLoadableStatusListener : loadableArr) {
            addLoadableStatusListener.addLoadableStatusListener(this)
        }
        return this
    }
}
