package com.linkpoint.ui.common.loadmon

import androidx.core.widget.SwipeRefreshLayout
import com.google.common.base.Strings
import com.linkpoint.react.RefreshableOne
import com.linkpoint.react.UnsubscribableOne
import com.linkpoint.ui.common.LoadingLayout
import com.linkpoint.ui.common.loadmon.Loadable
import java.util.ArrayList
import java.util.Collections
import java.util.List
import androidx.annotation.NonNull
import androidx.annotation.Nullable

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
    @NonNull
    private Loadable.Status status = Loadable.Status.Idle
    @Nullable
    private SwipeRefreshLayout swipeRefreshLayout = null

    interface OnLoadableDataChangedListener {
        fun onLoadableDataChanged()
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

    fun onLoadableStatusChange(Loadable loadable, Loadable.Status status2): Unit {
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

    fun onRefresh(): Unit {
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

    fun setButteryProgressBar(Boolean z): Unit {
        if (this.loadingLayout != null) {
            this.loadingLayout.setButteryProgressBar(z)
        }
    }

    fun setEmptyMessage(Boolean z, @Nullable String str): Unit {
        if (!z) {
            str = null
        }
        this.emptyMessage = str
        updateLoadingIndicator()
    }

    fun setExtraLoading(Boolean z): Unit {
        this.isExtraLoading = z
        onLoadableStatusChange((Loadable) null, (Loadable.Status) null)
    }

    fun setLoadingLayout(@Nullable LoadingLayout loadingLayout2, @Nullable String str, @Nullable String str2): Unit {
        this.loadingLayout = loadingLayout2
        this.loadingIdleMessage = str
        this.loadingErrorMessage = str2
        updateLoadingIndicator()
    }

    fun setSwipeRefreshLayout(@Nullable SwipeRefreshLayout swipeRefreshLayout2): Unit {
        this.swipeRefreshLayout = swipeRefreshLayout2
        if (swipeRefreshLayout2 != null) {
            swipeRefreshLayout2.setOnRefreshListener(this)
        }
    }

    fun unsubscribeAll(): Unit {
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

    fun withDataChangedListener(OnLoadableDataChangedListener onLoadableDataChangedListener2): LoadableMonitor {
        this.onLoadableDataChangedListener = onLoadableDataChangedListener2
        return this
    }

    fun withOptionalLoadables(Loadable... loadableArr): LoadableMonitor {
        Collections.addAll(this.optionalLoadables, loadableArr)
        for (Loadable addLoadableStatusListener : loadableArr) {
            addLoadableStatusListener.addLoadableStatusListener(this)
        }
        return this
    }
}
