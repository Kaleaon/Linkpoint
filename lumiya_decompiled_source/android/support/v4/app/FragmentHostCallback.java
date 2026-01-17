package android.support.v4.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public abstract class FragmentHostCallback<E> extends FragmentContainer {
    private final Activity mActivity;
    private SimpleArrayMap<String, LoaderManager> mAllLoaderManagers;
    private boolean mCheckedForLoaderManager;
    final Context mContext;
    final FragmentManagerImpl mFragmentManager;
    private final Handler mHandler;
    private LoaderManagerImpl mLoaderManager;
    private boolean mLoadersStarted;
    private boolean mRetainLoaders;
    final int mWindowAnimations;

    FragmentHostCallback(Activity activity, Context context, Handler handler, int i) {
        this.mFragmentManager = new FragmentManagerImpl();
        this.mActivity = activity;
        this.mContext = context;
        this.mHandler = handler;
        this.mWindowAnimations = i;
    }

    public FragmentHostCallback(Context context, Handler handler, int i) {
        this(!(context instanceof Activity) ? null : (Activity) context, context, handler, i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FragmentHostCallback(FragmentActivity fragmentActivity) {
        this(fragmentActivity, fragmentActivity, fragmentActivity.mHandler, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doLoaderDestroy() {
        if (this.mLoaderManager != null) {
            this.mLoaderManager.doDestroy();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doLoaderRetain() {
        if (this.mLoaderManager != null) {
            this.mLoaderManager.doRetain();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doLoaderStart() {
        if (this.mLoadersStarted) {
            return;
        }
        this.mLoadersStarted = true;
        if (this.mLoaderManager != null) {
            this.mLoaderManager.doStart();
        } else if (!this.mCheckedForLoaderManager) {
            this.mLoaderManager = getLoaderManager("(root)", this.mLoadersStarted, false);
            if (this.mLoaderManager != null && !this.mLoaderManager.mStarted) {
                this.mLoaderManager.doStart();
            }
        }
        this.mCheckedForLoaderManager = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doLoaderStop(boolean z) {
        this.mRetainLoaders = z;
        if (this.mLoaderManager == null || !this.mLoadersStarted) {
            return;
        }
        this.mLoadersStarted = false;
        if (z) {
            this.mLoaderManager.doRetain();
        } else {
            this.mLoaderManager.doStop();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpLoaders(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print(str);
        printWriter.print("mLoadersStarted=");
        printWriter.println(this.mLoadersStarted);
        if (this.mLoaderManager == null) {
            return;
        }
        printWriter.print(str);
        printWriter.print("Loader Manager ");
        printWriter.print(Integer.toHexString(System.identityHashCode(this.mLoaderManager)));
        printWriter.println(":");
        this.mLoaderManager.dump(str + "  ", fileDescriptor, printWriter, strArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Activity getActivity() {
        return this.mActivity;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Context getContext() {
        return this.mContext;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FragmentManagerImpl getFragmentManagerImpl() {
        return this.mFragmentManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Handler getHandler() {
        return this.mHandler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LoaderManagerImpl getLoaderManager(String str, boolean z, boolean z2) {
        if (this.mAllLoaderManagers == null) {
            this.mAllLoaderManagers = new SimpleArrayMap<>();
        }
        LoaderManagerImpl loaderManagerImpl = (LoaderManagerImpl) this.mAllLoaderManagers.get(str);
        if (loaderManagerImpl == null && z2) {
            LoaderManagerImpl loaderManagerImpl2 = new LoaderManagerImpl(str, this, z);
            this.mAllLoaderManagers.put(str, loaderManagerImpl2);
            return loaderManagerImpl2;
        } else if (!z || loaderManagerImpl == null || loaderManagerImpl.mStarted) {
            return loaderManagerImpl;
        } else {
            loaderManagerImpl.doStart();
            return loaderManagerImpl;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LoaderManagerImpl getLoaderManagerImpl() {
        if (this.mLoaderManager == null) {
            this.mCheckedForLoaderManager = true;
            this.mLoaderManager = getLoaderManager("(root)", this.mLoadersStarted, true);
            return this.mLoaderManager;
        }
        return this.mLoaderManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getRetainLoaders() {
        return this.mRetainLoaders;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void inactivateFragment(String str) {
        LoaderManagerImpl loaderManagerImpl;
        if (this.mAllLoaderManagers == null || (loaderManagerImpl = (LoaderManagerImpl) this.mAllLoaderManagers.get(str)) == null || loaderManagerImpl.mRetaining) {
            return;
        }
        loaderManagerImpl.doDestroy();
        this.mAllLoaderManagers.remove(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onAttachFragment(Fragment fragment) {
    }

    public void onDump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    @Override // android.support.v4.app.FragmentContainer
    @Nullable
    public View onFindViewById(int i) {
        return null;
    }

    @Nullable
    public abstract E onGetHost();

    public LayoutInflater onGetLayoutInflater() {
        return (LayoutInflater) this.mContext.getSystemService("layout_inflater");
    }

    public int onGetWindowAnimations() {
        return this.mWindowAnimations;
    }

    @Override // android.support.v4.app.FragmentContainer
    public boolean onHasView() {
        return true;
    }

    public boolean onHasWindowAnimations() {
        return true;
    }

    public void onRequestPermissionsFromFragment(@NonNull Fragment fragment, @NonNull String[] strArr, int i) {
    }

    public boolean onShouldSaveFragmentState(Fragment fragment) {
        return true;
    }

    public boolean onShouldShowRequestPermissionRationale(@NonNull String str) {
        return false;
    }

    public void onStartActivityFromFragment(Fragment fragment, Intent intent, int i) {
        onStartActivityFromFragment(fragment, intent, i, null);
    }

    public void onStartActivityFromFragment(Fragment fragment, Intent intent, int i, @Nullable Bundle bundle) {
        if (i != -1) {
            throw new IllegalStateException("Starting activity with a requestCode requires a FragmentActivity host");
        }
        this.mContext.startActivity(intent);
    }

    public void onStartIntentSenderFromFragment(Fragment fragment, IntentSender intentSender, int i, @Nullable Intent intent, int i2, int i3, int i4, Bundle bundle) throws IntentSender.SendIntentException {
        if (i != -1) {
            throw new IllegalStateException("Starting intent sender with a requestCode requires a FragmentActivity host");
        }
        ActivityCompat.startIntentSenderForResult(this.mActivity, intentSender, i, intent, i2, i3, i4, bundle);
    }

    public void onSupportInvalidateOptionsMenu() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reportLoaderStart() {
        if (this.mAllLoaderManagers == null) {
            return;
        }
        int size = this.mAllLoaderManagers.size();
        LoaderManagerImpl[] loaderManagerImplArr = new LoaderManagerImpl[size];
        for (int i = size - 1; i >= 0; i--) {
            loaderManagerImplArr[i] = (LoaderManagerImpl) this.mAllLoaderManagers.valueAt(i);
        }
        for (int i2 = 0; i2 < size; i2++) {
            LoaderManagerImpl loaderManagerImpl = loaderManagerImplArr[i2];
            loaderManagerImpl.finishRetain();
            loaderManagerImpl.doReportStart();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void restoreLoaderNonConfig(SimpleArrayMap<String, LoaderManager> simpleArrayMap) {
        if (simpleArrayMap != null) {
            int size = simpleArrayMap.size();
            for (int i = 0; i < size; i++) {
                ((LoaderManagerImpl) simpleArrayMap.valueAt(i)).updateHostController(this);
            }
        }
        this.mAllLoaderManagers = simpleArrayMap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleArrayMap<String, LoaderManager> retainLoaderNonConfig() {
        boolean z;
        if (this.mAllLoaderManagers == null) {
            z = false;
        } else {
            int size = this.mAllLoaderManagers.size();
            LoaderManagerImpl[] loaderManagerImplArr = new LoaderManagerImpl[size];
            for (int i = size - 1; i >= 0; i--) {
                loaderManagerImplArr[i] = (LoaderManagerImpl) this.mAllLoaderManagers.valueAt(i);
            }
            boolean retainLoaders = getRetainLoaders();
            z = false;
            for (int i2 = 0; i2 < size; i2++) {
                LoaderManagerImpl loaderManagerImpl = loaderManagerImplArr[i2];
                if (!loaderManagerImpl.mRetaining && retainLoaders) {
                    if (!loaderManagerImpl.mStarted) {
                        loaderManagerImpl.doStart();
                    }
                    loaderManagerImpl.doRetain();
                }
                if (loaderManagerImpl.mRetaining) {
                    z = true;
                } else {
                    loaderManagerImpl.doDestroy();
                    this.mAllLoaderManagers.remove(loaderManagerImpl.mWho);
                }
            }
        }
        if (z) {
            return this.mAllLoaderManagers;
        }
        return null;
    }
}
