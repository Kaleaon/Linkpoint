package com.lumiyaviewer.lumiya.ui.common.loadmon;

import android.support.v4.widget.SwipeRefreshLayout;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.react.RefreshableOne;
import com.lumiyaviewer.lumiya.react.UnsubscribableOne;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class LoadableMonitor implements Loadable.LoadableStatusListener, SwipeRefreshLayout.OnRefreshListener {

    /* renamed from: -com-lumiyaviewer-lumiya-ui-common-loadmon-Loadable$StatusSwitchesValues, reason: not valid java name */
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

    /* renamed from: -getcom-lumiyaviewer-lumiya-ui-common-loadmon-Loadable$StatusSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m589x4f18d023() {
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
        Iterator<T> it = this.loadables.iterator();
        while (it.hasNext()) {
            ((Loadable) it.next()).addLoadableStatusListener(this);
        }
    }

    private void updateLoadingIndicator() {
        if (this.loadingLayout != null) {
            switch (m589x4f18d023()[this.status.ordinal()]) {
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

    /* JADX WARN: Removed duplicated region for block: B:11:0x006c  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x002c  */
    @Override // com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable.LoadableStatusListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onLoadableStatusChange(com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable r10, com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable.Status r11) {
        /*
            r9 = this;
            r5 = 1
            r2 = 0
            java.util.List<com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable> r0 = r9.loadables
            java.util.Iterator r6 = r0.iterator()
            r1 = r2
            r3 = r2
            r4 = r2
        Lb:
            boolean r0 = r6.hasNext()
            if (r0 == 0) goto L33
            java.lang.Object r0 = r6.next()
            com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable r0 = (com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable) r0
            com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable$Status r0 = r0.getLoadableStatus()
            int[] r7 = m589x4f18d023()
            int r8 = r0.ordinal()
            r7 = r7[r8]
            switch(r7) {
                case 1: goto L31;
                case 2: goto L28;
                case 3: goto L28;
                case 4: goto L2f;
                default: goto L28;
            }
        L28:
            com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable$Status r7 = com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable.Status.Loaded
            if (r0 == r7) goto L6c
            r0 = r5
        L2d:
            r1 = r0
            goto Lb
        L2f:
            r4 = r5
            goto L28
        L31:
            r3 = r5
            goto L28
        L33:
            if (r4 != 0) goto L39
            boolean r0 = r9.isExtraLoading
            if (r0 == 0) goto L5f
        L39:
            com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable$Status r0 = com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable.Status.Loading
        L3b:
            com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable$Status r1 = r9.status
            if (r0 == r1) goto L44
            r9.status = r0
            r9.updateLoadingIndicator()
        L44:
            if (r4 != 0) goto L4f
            android.support.v4.widget.SwipeRefreshLayout r0 = r9.swipeRefreshLayout
            if (r0 == 0) goto L4f
            android.support.v4.widget.SwipeRefreshLayout r0 = r9.swipeRefreshLayout
            r0.setRefreshing(r2)
        L4f:
            com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable$Status r0 = r9.status
            com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable$Status r1 = com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable.Status.Loaded
            if (r0 != r1) goto L5e
            com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor$OnLoadableDataChangedListener r0 = r9.onLoadableDataChangedListener
            if (r0 == 0) goto L5e
            com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor$OnLoadableDataChangedListener r0 = r9.onLoadableDataChangedListener
            r0.onLoadableDataChanged()
        L5e:
            return
        L5f:
            if (r3 == 0) goto L64
            com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable$Status r0 = com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable.Status.Error
            goto L3b
        L64:
            if (r1 != 0) goto L69
            com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable$Status r0 = com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable.Status.Loaded
            goto L3b
        L69:
            com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable$Status r0 = com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable.Status.Idle
            goto L3b
        L6c:
            r0 = r1
            goto L2d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.onLoadableStatusChange(com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable, com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable$Status):void");
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
