// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common.loadmon;

import com.lumiyaviewer.lumiya.react.UnsubscribableOne;
import com.lumiyaviewer.lumiya.react.RefreshableOne;
import com.google.common.base.Strings;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import java.util.List;
import javax.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

public class LoadableMonitor implements LoadableStatusListener, OnRefreshListener
{
    @Nullable
    private String emptyMessage;
    private boolean isExtraLoading;
    private final List<Loadable> loadables;
    @Nullable
    private String loadingErrorMessage;
    @Nullable
    private String loadingIdleMessage;
    @Nullable
    private LoadingLayout loadingLayout;
    @Nullable
    private OnLoadableDataChangedListener onLoadableDataChangedListener;
    private final List<Loadable> optionalLoadables;
    @Nonnull
    private Status status;
    @Nullable
    private SwipeRefreshLayout swipeRefreshLayout;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-ui-common-loadmon-Loadable$StatusSwitchesValues() {
        if (LoadableMonitor.-com-lumiyaviewer-lumiya-ui-common-loadmon-Loadable$StatusSwitchesValues != null) {
            return LoadableMonitor.-com-lumiyaviewer-lumiya-ui-common-loadmon-Loadable$StatusSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-ui-common-loadmon-Loadable$StatusSwitchesValues = new int[Status.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-ui-common-loadmon-Loadable$StatusSwitchesValues[Status.Error.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-ui-common-loadmon-Loadable$StatusSwitchesValues[Status.Idle.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-ui-common-loadmon-Loadable$StatusSwitchesValues[Status.Loaded.ordinal()] = 3;
                        try {
                            -com-lumiyaviewer-lumiya-ui-common-loadmon-Loadable$StatusSwitchesValues[Status.Loading.ordinal()] = 4;
                            return -com-lumiyaviewer-lumiya-ui-common-loadmon-Loadable$StatusSwitchesValues = -com-lumiyaviewer-lumiya-ui-common-loadmon-Loadable$StatusSwitchesValues;
                        }
                        catch (final NoSuchFieldError noSuchFieldError) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError2) {}
                }
                catch (final NoSuchFieldError noSuchFieldError3) {}
            }
            catch (final NoSuchFieldError noSuchFieldError4) {
                continue;
            }
            break;
        }
    }
    
    public LoadableMonitor(final Loadable... elements) {
        this.loadables = new ArrayList<Loadable>();
        this.optionalLoadables = new ArrayList<Loadable>();
        this.status = Status.Idle;
        this.onLoadableDataChangedListener = null;
        this.loadingLayout = null;
        this.swipeRefreshLayout = null;
        this.loadingIdleMessage = null;
        this.loadingErrorMessage = null;
        this.emptyMessage = null;
        this.isExtraLoading = false;
        Collections.addAll(this.loadables, elements);
        final Iterator<Object> iterator = this.loadables.iterator();
        while (iterator.hasNext()) {
            iterator.next().addLoadableStatusListener((Loadable.LoadableStatusListener)this);
        }
    }
    
    private void updateLoadingIndicator() {
        if (this.loadingLayout != null) {
            switch (-getcom-lumiyaviewer-lumiya-ui-common-loadmon-Loadable$StatusSwitchesValues()[this.status.ordinal()]) {
                case 2: {
                    this.loadingLayout.showMessage(Strings.nullToEmpty(this.loadingIdleMessage));
                    break;
                }
                case 4: {
                    this.loadingLayout.showLoading();
                    break;
                }
                case 3: {
                    this.loadingLayout.showContent(this.emptyMessage);
                    break;
                }
                case 1: {
                    this.loadingLayout.showMessage(Strings.nullToEmpty(this.loadingErrorMessage));
                    break;
                }
            }
        }
    }
    
    @Override
    public void onLoadableStatusChange(final Loadable loadable, Status loadableStatus) {
        final Iterator<Object> iterator = this.loadables.iterator();
        boolean b = false;
        int n = 0;
        int n2 = 0;
        while (iterator.hasNext()) {
            loadableStatus = iterator.next().getLoadableStatus();
            int n3 = n;
            int n4 = n2;
            Label_0096: {
                switch (-getcom-lumiyaviewer-lumiya-ui-common-loadmon-Loadable$StatusSwitchesValues()[loadableStatus.ordinal()]) {
                    default: {
                        n4 = n2;
                        n3 = n;
                        break Label_0096;
                    }
                    case 1: {
                        n3 = 1;
                        n4 = n2;
                        break Label_0096;
                    }
                    case 4: {
                        n4 = 1;
                        n3 = n;
                    }
                    case 2:
                    case 3: {
                        if (loadableStatus != Status.Loaded) {
                            b = true;
                        }
                        n = n3;
                        n2 = n4;
                        continue;
                    }
                }
            }
        }
        Loadable.Status status;
        if (n2 != 0 || this.isExtraLoading) {
            status = Status.Loading;
        }
        else if (n != 0) {
            status = Status.Error;
        }
        else if (!b) {
            status = Status.Loaded;
        }
        else {
            status = Status.Idle;
        }
        if (status != this.status) {
            this.status = status;
            this.updateLoadingIndicator();
        }
        if (n2 == 0 && this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setRefreshing(false);
        }
        if (this.status == Status.Loaded && this.onLoadableDataChangedListener != null) {
            this.onLoadableDataChangedListener.onLoadableDataChanged();
        }
    }
    
    @Override
    public void onRefresh() {
        for (final Loadable loadable : this.loadables) {
            if (loadable instanceof RefreshableOne) {
                ((RefreshableOne)loadable).requestRefresh();
            }
        }
        for (final Loadable loadable2 : this.optionalLoadables) {
            if (loadable2 instanceof RefreshableOne) {
                ((RefreshableOne)loadable2).requestRefresh();
            }
        }
    }
    
    public void setButteryProgressBar(final boolean butteryProgressBar) {
        if (this.loadingLayout != null) {
            this.loadingLayout.setButteryProgressBar(butteryProgressBar);
        }
    }
    
    public void setEmptyMessage(final boolean b, @Nullable String emptyMessage) {
        if (!b) {
            emptyMessage = null;
        }
        this.emptyMessage = emptyMessage;
        this.updateLoadingIndicator();
    }
    
    public void setExtraLoading(final boolean isExtraLoading) {
        this.isExtraLoading = isExtraLoading;
        this.onLoadableStatusChange(null, null);
    }
    
    public void setLoadingLayout(@Nullable final LoadingLayout loadingLayout, @Nullable final String loadingIdleMessage, @Nullable final String loadingErrorMessage) {
        this.loadingLayout = loadingLayout;
        this.loadingIdleMessage = loadingIdleMessage;
        this.loadingErrorMessage = loadingErrorMessage;
        this.updateLoadingIndicator();
    }
    
    public void setSwipeRefreshLayout(@Nullable final SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener)this);
        }
    }
    
    public void unsubscribeAll() {
        for (final Loadable loadable : this.loadables) {
            if (loadable instanceof UnsubscribableOne) {
                ((UnsubscribableOne)loadable).unsubscribe();
            }
        }
        for (final Loadable loadable2 : this.optionalLoadables) {
            if (loadable2 instanceof UnsubscribableOne) {
                ((UnsubscribableOne)loadable2).unsubscribe();
            }
        }
    }
    
    public LoadableMonitor withDataChangedListener(final OnLoadableDataChangedListener onLoadableDataChangedListener) {
        this.onLoadableDataChangedListener = onLoadableDataChangedListener;
        return this;
    }
    
    public LoadableMonitor withOptionalLoadables(final Loadable... elements) {
        Collections.addAll(this.optionalLoadables, elements);
        for (int i = 0; i < elements.length; ++i) {
            elements[i].addLoadableStatusListener((Loadable.LoadableStatusListener)this);
        }
        return this;
    }
    
    public interface OnLoadableDataChangedListener
    {
        void onLoadableDataChanged();
    }
}
