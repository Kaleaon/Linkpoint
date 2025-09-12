// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common.loadmon;

import android.support.v4.widget.SwipeRefreshLayout;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.react.RefreshableOne;
import com.lumiyaviewer.lumiya.react.UnsubscribableOne;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common.loadmon:
//            Loadable

public class LoadableMonitor
    implements Loadable.LoadableStatusListener, android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
{
    public static interface OnLoadableDataChangedListener
    {

        public abstract void onLoadableDataChanged();
    }


    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_common_2D_loadmon_2D_Loadable$StatusSwitchesValues[];
    private String emptyMessage;
    private boolean isExtraLoading;
    private final List loadables = new ArrayList();
    private String loadingErrorMessage;
    private String loadingIdleMessage;
    private LoadingLayout loadingLayout;
    private OnLoadableDataChangedListener onLoadableDataChangedListener;
    private final List optionalLoadables = new ArrayList();
    private Loadable.Status status;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_common_2D_loadmon_2D_Loadable$StatusSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_common_2D_loadmon_2D_Loadable$StatusSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_common_2D_loadmon_2D_Loadable$StatusSwitchesValues;
        }
        int ai[] = new int[Loadable.Status.values().length];
        try
        {
            ai[Loadable.Status.Error.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[Loadable.Status.Idle.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[Loadable.Status.Loaded.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[Loadable.Status.Loading.ordinal()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_common_2D_loadmon_2D_Loadable$StatusSwitchesValues = ai;
        return ai;
    }

    public transient LoadableMonitor(Loadable aloadable[])
    {
        status = Loadable.Status.Idle;
        onLoadableDataChangedListener = null;
        loadingLayout = null;
        swipeRefreshLayout = null;
        loadingIdleMessage = null;
        loadingErrorMessage = null;
        emptyMessage = null;
        isExtraLoading = false;
        Collections.addAll(loadables, aloadable);
        for (aloadable = loadables.iterator(); aloadable.hasNext(); ((Loadable)aloadable.next()).addLoadableStatusListener(this)) { }
    }

    private void updateLoadingIndicator()
    {
        if (loadingLayout == null) goto _L2; else goto _L1
_L1:
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_common_2D_loadmon_2D_Loadable$StatusSwitchesValues()[status.ordinal()];
        JVM INSTR tableswitch 1 4: default 48
    //                   1 84
    //                   2 49
    //                   3 72
    //                   4 64;
           goto _L2 _L3 _L4 _L5 _L6
_L2:
        return;
_L4:
        loadingLayout.showMessage(Strings.nullToEmpty(loadingIdleMessage));
        return;
_L6:
        loadingLayout.showLoading();
        return;
_L5:
        loadingLayout.showContent(emptyMessage);
        return;
_L3:
        loadingLayout.showMessage(Strings.nullToEmpty(loadingErrorMessage));
        return;
    }

    public void onLoadableStatusChange(Loadable loadable, Loadable.Status status1)
    {
        boolean flag;
        boolean flag3;
        boolean flag4;
        loadable = loadables.iterator();
        flag = false;
        flag4 = false;
        flag3 = false;
_L2:
        boolean flag1;
        boolean flag2;
        if (!loadable.hasNext())
        {
            break MISSING_BLOCK_LABEL_136;
        }
        status1 = ((Loadable)loadable.next()).getLoadableStatus();
        flag1 = flag4;
        flag2 = flag3;
        switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_common_2D_loadmon_2D_Loadable$StatusSwitchesValues()[status1.ordinal()])
        {
        default:
            flag2 = flag3;
            flag1 = flag4;
            break;

        case 1: // '\001'
            break MISSING_BLOCK_LABEL_126;

        case 2: // '\002'
        case 3: // '\003'
            break;

        case 4: // '\004'
            break; /* Loop/switch isn't completed */
        }
_L3:
        if (status1 != Loadable.Status.Loaded)
        {
            flag = true;
        }
        flag4 = flag1;
        flag3 = flag2;
        if (true) goto _L2; else goto _L1
_L1:
        flag2 = true;
        flag1 = flag4;
          goto _L3
        flag1 = true;
        flag2 = flag3;
          goto _L3
        if (flag3 || isExtraLoading)
        {
            loadable = Loadable.Status.Loading;
        } else
        if (flag4)
        {
            loadable = Loadable.Status.Error;
        } else
        if (!flag)
        {
            loadable = Loadable.Status.Loaded;
        } else
        {
            loadable = Loadable.Status.Idle;
        }
        if (loadable != status)
        {
            status = loadable;
            updateLoadingIndicator();
        }
        if (!flag3 && swipeRefreshLayout != null)
        {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (status == Loadable.Status.Loaded && onLoadableDataChangedListener != null)
        {
            onLoadableDataChangedListener.onLoadableDataChanged();
        }
        return;
    }

    public void onRefresh()
    {
        Iterator iterator = loadables.iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            Loadable loadable = (Loadable)iterator.next();
            if (loadable instanceof RefreshableOne)
            {
                ((RefreshableOne)loadable).requestRefresh();
            }
        } while (true);
        iterator = optionalLoadables.iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            Loadable loadable1 = (Loadable)iterator.next();
            if (loadable1 instanceof RefreshableOne)
            {
                ((RefreshableOne)loadable1).requestRefresh();
            }
        } while (true);
    }

    public void setButteryProgressBar(boolean flag)
    {
        if (loadingLayout != null)
        {
            loadingLayout.setButteryProgressBar(flag);
        }
    }

    public void setEmptyMessage(boolean flag, String s)
    {
        if (!flag)
        {
            s = null;
        }
        emptyMessage = s;
        updateLoadingIndicator();
    }

    public void setExtraLoading(boolean flag)
    {
        isExtraLoading = flag;
        onLoadableStatusChange(null, null);
    }

    public void setLoadingLayout(LoadingLayout loadinglayout, String s, String s1)
    {
        loadingLayout = loadinglayout;
        loadingIdleMessage = s;
        loadingErrorMessage = s1;
        updateLoadingIndicator();
    }

    public void setSwipeRefreshLayout(SwipeRefreshLayout swiperefreshlayout)
    {
        swipeRefreshLayout = swiperefreshlayout;
        if (swiperefreshlayout != null)
        {
            swiperefreshlayout.setOnRefreshListener(this);
        }
    }

    public void unsubscribeAll()
    {
        Iterator iterator = loadables.iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            Loadable loadable = (Loadable)iterator.next();
            if (loadable instanceof UnsubscribableOne)
            {
                ((UnsubscribableOne)loadable).unsubscribe();
            }
        } while (true);
        iterator = optionalLoadables.iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            Loadable loadable1 = (Loadable)iterator.next();
            if (loadable1 instanceof UnsubscribableOne)
            {
                ((UnsubscribableOne)loadable1).unsubscribe();
            }
        } while (true);
    }

    public LoadableMonitor withDataChangedListener(OnLoadableDataChangedListener onloadabledatachangedlistener)
    {
        onLoadableDataChangedListener = onloadabledatachangedlistener;
        return this;
    }

    public transient LoadableMonitor withOptionalLoadables(Loadable aloadable[])
    {
        Collections.addAll(optionalLoadables, aloadable);
        int i = 0;
        for (int j = aloadable.length; i < j; i++)
        {
            aloadable[i].addLoadableStatusListener(this);
        }

        return this;
    }
}
