// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import android.view.Window;
import android.view.LayoutInflater;
import android.content.IntentSender$SendIntentException;
import android.content.IntentSender;
import android.support.annotation.RequiresApi;
import android.app.Activity;
import android.support.v4.util.SimpleArrayMap;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.CallSuper;
import android.view.MenuItem;
import android.view.Menu;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.content.res.Configuration;
import android.os.Build$VERSION;
import android.util.Log;
import android.content.Intent;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import java.util.Iterator;
import android.arch.lifecycle.Lifecycle;
import android.os.Message;
import android.support.v4.util.SparseArrayCompat;
import android.os.Handler;

public class FragmentActivity extends BaseFragmentActivityApi16 implements OnRequestPermissionsResultCallback, RequestPermissionsRequestCodeValidator
{
    static final String ALLOCATED_REQUEST_INDICIES_TAG = "android:support:request_indicies";
    static final String FRAGMENTS_TAG = "android:support:fragments";
    static final int MAX_NUM_PENDING_FRAGMENT_ACTIVITY_RESULTS = 65534;
    static final int MSG_REALLY_STOPPED = 1;
    static final int MSG_RESUME_PENDING = 2;
    static final String NEXT_CANDIDATE_REQUEST_INDEX_TAG = "android:support:next_request_index";
    static final String REQUEST_FRAGMENT_WHO_TAG = "android:support:request_fragment_who";
    private static final String TAG = "FragmentActivity";
    boolean mCreated;
    final FragmentController mFragments;
    final Handler mHandler;
    int mNextCandidateRequestIndex;
    SparseArrayCompat<String> mPendingFragmentActivityResults;
    boolean mReallyStopped;
    boolean mRequestedPermissionsFromFragment;
    boolean mResumed;
    boolean mRetaining;
    boolean mStopped;
    
    public FragmentActivity() {
        this.mHandler = new Handler() {
            public void handleMessage(final Message message) {
                switch (message.what) {
                    default: {
                        super.handleMessage(message);
                        break;
                    }
                    case 1: {
                        if (FragmentActivity.this.mStopped) {
                            FragmentActivity.this.doReallyStop(false);
                            break;
                        }
                        break;
                    }
                    case 2: {
                        FragmentActivity.this.onResumeFragments();
                        FragmentActivity.this.mFragments.execPendingActions();
                        break;
                    }
                }
            }
        };
        this.mFragments = FragmentController.createController(new HostCallbacks());
        this.mStopped = true;
        this.mReallyStopped = true;
    }
    
    private int allocateRequestIndex(final Fragment fragment) {
        if (this.mPendingFragmentActivityResults.size() < 65534) {
            while (this.mPendingFragmentActivityResults.indexOfKey(this.mNextCandidateRequestIndex) >= 0) {
                this.mNextCandidateRequestIndex = (this.mNextCandidateRequestIndex + 1) % 65534;
            }
            final int mNextCandidateRequestIndex = this.mNextCandidateRequestIndex;
            this.mPendingFragmentActivityResults.put(mNextCandidateRequestIndex, fragment.mWho);
            this.mNextCandidateRequestIndex = (this.mNextCandidateRequestIndex + 1) % 65534;
            return mNextCandidateRequestIndex;
        }
        throw new IllegalStateException("Too many pending Fragment activity results.");
    }
    
    private static void markState(final FragmentManager fragmentManager, final Lifecycle.State state) {
        for (final Fragment fragment : fragmentManager.getFragments()) {
            if (fragment != null) {
                fragment.mLifecycleRegistry.markState(state);
                markState(fragment.getChildFragmentManager(), state);
            }
        }
    }
    
    @Override
    final View dispatchFragmentsOnCreateView(final View view, final String s, final Context context, final AttributeSet set) {
        return this.mFragments.onCreateView(view, s, context, set);
    }
    
    void doReallyStop(final boolean mRetaining) {
        if (this.mReallyStopped) {
            if (mRetaining) {
                this.mFragments.doLoaderStart();
                this.mFragments.doLoaderStop(true);
            }
        }
        else {
            this.mReallyStopped = true;
            this.mRetaining = mRetaining;
            this.mHandler.removeMessages(1);
            this.onReallyStop();
        }
    }
    
    public void dump(final String s, final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        super.dump(s, fileDescriptor, printWriter, array);
        printWriter.print(s);
        printWriter.print("Local FragmentActivity ");
        printWriter.print(Integer.toHexString(System.identityHashCode(this)));
        printWriter.println(" State:");
        final String string = s + "  ";
        printWriter.print(string);
        printWriter.print("mCreated=");
        printWriter.print(this.mCreated);
        printWriter.print("mResumed=");
        printWriter.print(this.mResumed);
        printWriter.print(" mStopped=");
        printWriter.print(this.mStopped);
        printWriter.print(" mReallyStopped=");
        printWriter.println(this.mReallyStopped);
        this.mFragments.dumpLoaders(string, fileDescriptor, printWriter, array);
        this.mFragments.getSupportFragmentManager().dump(s, fileDescriptor, printWriter, array);
    }
    
    public Object getLastCustomNonConfigurationInstance() {
        final NonConfigurationInstances nonConfigurationInstances = (NonConfigurationInstances)this.getLastNonConfigurationInstance();
        Object custom;
        if (nonConfigurationInstances == null) {
            custom = null;
        }
        else {
            custom = nonConfigurationInstances.custom;
        }
        return custom;
    }
    
    @Override
    public Lifecycle getLifecycle() {
        return super.getLifecycle();
    }
    
    public FragmentManager getSupportFragmentManager() {
        return this.mFragments.getSupportFragmentManager();
    }
    
    public LoaderManager getSupportLoaderManager() {
        return this.mFragments.getSupportLoaderManager();
    }
    
    protected void onActivityResult(final int n, final int n2, final Intent intent) {
        this.mFragments.noteStateNotSaved();
        int n3 = n >> 16;
        if (n3 == 0) {
            super.onActivityResult(n, n2, intent);
            return;
        }
        --n3;
        final String str = this.mPendingFragmentActivityResults.get(n3);
        this.mPendingFragmentActivityResults.remove(n3);
        if (str != null) {
            final Fragment fragmentByWho = this.mFragments.findFragmentByWho(str);
            if (fragmentByWho != null) {
                fragmentByWho.onActivityResult(0xFFFF & n, n2, intent);
            }
            else {
                Log.w("FragmentActivity", "Activity result no fragment exists for who: " + str);
            }
            return;
        }
        Log.w("FragmentActivity", "Activity result delivered for unknown Fragment.");
    }
    
    public void onAttachFragment(final Fragment fragment) {
    }
    
    public void onBackPressed() {
        final FragmentManager supportFragmentManager = this.mFragments.getSupportFragmentManager();
        final boolean stateSaved = supportFragmentManager.isStateSaved();
        if (stateSaved && Build$VERSION.SDK_INT <= 25) {
            return;
        }
        if (stateSaved || !supportFragmentManager.popBackStackImmediate()) {
            super.onBackPressed();
        }
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mFragments.dispatchConfigurationChanged(configuration);
    }
    
    @Override
    protected void onCreate(@Nullable final Bundle bundle) {
        this.mFragments.attachHost(null);
        super.onCreate(bundle);
        final NonConfigurationInstances nonConfigurationInstances = (NonConfigurationInstances)this.getLastNonConfigurationInstance();
        if (nonConfigurationInstances != null) {
            this.mFragments.restoreLoaderNonConfig(nonConfigurationInstances.loaders);
        }
        if (bundle != null) {
            final Parcelable parcelable = bundle.getParcelable("android:support:fragments");
            final FragmentController mFragments = this.mFragments;
            FragmentManagerNonConfig fragments;
            if (nonConfigurationInstances == null) {
                fragments = null;
            }
            else {
                fragments = nonConfigurationInstances.fragments;
            }
            mFragments.restoreAllState(parcelable, fragments);
            if (bundle.containsKey("android:support:next_request_index")) {
                this.mNextCandidateRequestIndex = bundle.getInt("android:support:next_request_index");
                final int[] intArray = bundle.getIntArray("android:support:request_indicies");
                final String[] stringArray = bundle.getStringArray("android:support:request_fragment_who");
                if (intArray != null && stringArray != null && intArray.length == stringArray.length) {
                    this.mPendingFragmentActivityResults = new SparseArrayCompat<String>(intArray.length);
                    for (int i = 0; i < intArray.length; ++i) {
                        this.mPendingFragmentActivityResults.put(intArray[i], stringArray[i]);
                    }
                }
                else {
                    Log.w("FragmentActivity", "Invalid requestCode mapping in savedInstanceState.");
                }
            }
        }
        if (this.mPendingFragmentActivityResults == null) {
            this.mPendingFragmentActivityResults = new SparseArrayCompat<String>();
            this.mNextCandidateRequestIndex = 0;
        }
        this.mFragments.dispatchCreate();
    }
    
    public boolean onCreatePanelMenu(final int n, final Menu menu) {
        if (n != 0) {
            return super.onCreatePanelMenu(n, menu);
        }
        return super.onCreatePanelMenu(n, menu) | this.mFragments.dispatchCreateOptionsMenu(menu, this.getMenuInflater());
    }
    
    protected void onDestroy() {
        super.onDestroy();
        this.doReallyStop(false);
        this.mFragments.dispatchDestroy();
        this.mFragments.doLoaderDestroy();
    }
    
    public void onLowMemory() {
        super.onLowMemory();
        this.mFragments.dispatchLowMemory();
    }
    
    public boolean onMenuItemSelected(final int n, final MenuItem menuItem) {
        if (super.onMenuItemSelected(n, menuItem)) {
            return true;
        }
        switch (n) {
            default: {
                return false;
            }
            case 0: {
                return this.mFragments.dispatchOptionsItemSelected(menuItem);
            }
            case 6: {
                return this.mFragments.dispatchContextItemSelected(menuItem);
            }
        }
    }
    
    @CallSuper
    public void onMultiWindowModeChanged(final boolean b) {
        this.mFragments.dispatchMultiWindowModeChanged(b);
    }
    
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        this.mFragments.noteStateNotSaved();
    }
    
    public void onPanelClosed(final int n, final Menu menu) {
        switch (n) {
            case 0: {
                this.mFragments.dispatchOptionsMenuClosed(menu);
                break;
            }
        }
        super.onPanelClosed(n, menu);
    }
    
    protected void onPause() {
        super.onPause();
        this.mResumed = false;
        if (this.mHandler.hasMessages(2)) {
            this.mHandler.removeMessages(2);
            this.onResumeFragments();
        }
        this.mFragments.dispatchPause();
    }
    
    @CallSuper
    public void onPictureInPictureModeChanged(final boolean b) {
        this.mFragments.dispatchPictureInPictureModeChanged(b);
    }
    
    protected void onPostResume() {
        super.onPostResume();
        this.mHandler.removeMessages(2);
        this.onResumeFragments();
        this.mFragments.execPendingActions();
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    protected boolean onPrepareOptionsPanel(final View view, final Menu menu) {
        return super.onPreparePanel(0, view, menu);
    }
    
    public boolean onPreparePanel(final int n, final View view, final Menu menu) {
        if (n == 0 && menu != null) {
            return this.onPrepareOptionsPanel(view, menu) | this.mFragments.dispatchPrepareOptionsMenu(menu);
        }
        return super.onPreparePanel(n, view, menu);
    }
    
    void onReallyStop() {
        this.mFragments.doLoaderStop(this.mRetaining);
        this.mFragments.dispatchReallyStop();
    }
    
    @Override
    public void onRequestPermissionsResult(final int n, @NonNull final String[] array, @NonNull final int[] array2) {
        int n2 = n >> 16 & 0xFFFF;
        if (n2 != 0) {
            --n2;
            final String str = this.mPendingFragmentActivityResults.get(n2);
            this.mPendingFragmentActivityResults.remove(n2);
            if (str == null) {
                Log.w("FragmentActivity", "Activity result delivered for unknown Fragment.");
                return;
            }
            final Fragment fragmentByWho = this.mFragments.findFragmentByWho(str);
            if (fragmentByWho != null) {
                fragmentByWho.onRequestPermissionsResult(n & 0xFFFF, array, array2);
            }
            else {
                Log.w("FragmentActivity", "Activity result no fragment exists for who: " + str);
            }
        }
    }
    
    protected void onResume() {
        super.onResume();
        this.mHandler.sendEmptyMessage(2);
        this.mResumed = true;
        this.mFragments.execPendingActions();
    }
    
    protected void onResumeFragments() {
        this.mFragments.dispatchResume();
    }
    
    public Object onRetainCustomNonConfigurationInstance() {
        return null;
    }
    
    public final Object onRetainNonConfigurationInstance() {
        if (this.mStopped) {
            this.doReallyStop(true);
        }
        final Object onRetainCustomNonConfigurationInstance = this.onRetainCustomNonConfigurationInstance();
        final FragmentManagerNonConfig retainNestedNonConfig = this.mFragments.retainNestedNonConfig();
        final SimpleArrayMap<String, LoaderManager> retainLoaderNonConfig = this.mFragments.retainLoaderNonConfig();
        if (retainNestedNonConfig == null && retainLoaderNonConfig == null && onRetainCustomNonConfigurationInstance == null) {
            return null;
        }
        final NonConfigurationInstances nonConfigurationInstances = new NonConfigurationInstances();
        nonConfigurationInstances.custom = onRetainCustomNonConfigurationInstance;
        nonConfigurationInstances.fragments = retainNestedNonConfig;
        nonConfigurationInstances.loaders = retainLoaderNonConfig;
        return nonConfigurationInstances;
    }
    
    @Override
    protected void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        markState(this.getSupportFragmentManager(), Lifecycle.State.CREATED);
        final Parcelable saveAllState = this.mFragments.saveAllState();
        if (saveAllState != null) {
            bundle.putParcelable("android:support:fragments", saveAllState);
        }
        if (this.mPendingFragmentActivityResults.size() > 0) {
            bundle.putInt("android:support:next_request_index", this.mNextCandidateRequestIndex);
            final int[] array = new int[this.mPendingFragmentActivityResults.size()];
            final String[] array2 = new String[this.mPendingFragmentActivityResults.size()];
            for (int i = 0; i < this.mPendingFragmentActivityResults.size(); ++i) {
                array[i] = this.mPendingFragmentActivityResults.keyAt(i);
                array2[i] = this.mPendingFragmentActivityResults.valueAt(i);
            }
            bundle.putIntArray("android:support:request_indicies", array);
            bundle.putStringArray("android:support:request_fragment_who", array2);
        }
    }
    
    protected void onStart() {
        super.onStart();
        this.mStopped = false;
        this.mReallyStopped = false;
        this.mHandler.removeMessages(1);
        if (!this.mCreated) {
            this.mCreated = true;
            this.mFragments.dispatchActivityCreated();
        }
        this.mFragments.noteStateNotSaved();
        this.mFragments.execPendingActions();
        this.mFragments.doLoaderStart();
        this.mFragments.dispatchStart();
        this.mFragments.reportLoaderStart();
    }
    
    public void onStateNotSaved() {
        this.mFragments.noteStateNotSaved();
    }
    
    protected void onStop() {
        super.onStop();
        this.mStopped = true;
        markState(this.getSupportFragmentManager(), Lifecycle.State.CREATED);
        this.mHandler.sendEmptyMessage(1);
        this.mFragments.dispatchStop();
    }
    
    void requestPermissionsFromFragment(final Fragment fragment, final String[] array, final int n) {
        Label_0041: {
            if (n == -1) {
                break Label_0041;
            }
            BaseFragmentActivityApi14.checkForValidRequestCode(n);
            try {
                this.mRequestedPermissionsFromFragment = true;
                ActivityCompat.requestPermissions(this, array, (this.allocateRequestIndex(fragment) + 1 << 16) + (0xFFFF & n));
                return;
                ActivityCompat.requestPermissions(this, array, n);
            }
            finally {
                this.mRequestedPermissionsFromFragment = false;
            }
        }
    }
    
    public void setEnterSharedElementCallback(final SharedElementCallback sharedElementCallback) {
        ActivityCompat.setEnterSharedElementCallback(this, sharedElementCallback);
    }
    
    public void setExitSharedElementCallback(final SharedElementCallback sharedElementCallback) {
        ActivityCompat.setExitSharedElementCallback(this, sharedElementCallback);
    }
    
    public void startActivityForResult(final Intent intent, final int n) {
        if (!this.mStartedActivityFromFragment && n != -1) {
            BaseFragmentActivityApi14.checkForValidRequestCode(n);
        }
        super.startActivityForResult(intent, n);
    }
    
    public void startActivityFromFragment(final Fragment fragment, final Intent intent, final int n) {
        this.startActivityFromFragment(fragment, intent, n, null);
    }
    
    public void startActivityFromFragment(final Fragment fragment, final Intent intent, final int n, @Nullable final Bundle bundle) {
        this.mStartedActivityFromFragment = true;
        Label_0043: {
            if (n == -1) {
                break Label_0043;
            }
            try {
                BaseFragmentActivityApi14.checkForValidRequestCode(n);
                ActivityCompat.startActivityForResult(this, intent, (this.allocateRequestIndex(fragment) + 1 << 16) + (0xFFFF & n), bundle);
                return;
                ActivityCompat.startActivityForResult(this, intent, -1, bundle);
            }
            finally {
                this.mStartedActivityFromFragment = false;
            }
        }
    }
    
    public void startIntentSenderFromFragment(final Fragment fragment, final IntentSender intentSender, final int n, @Nullable final Intent intent, final int n2, final int n3, final int n4, final Bundle bundle) throws IntentSender$SendIntentException {
        this.mStartedIntentSenderFromFragment = true;
        Label_0051: {
            if (n == -1) {
                break Label_0051;
            }
            try {
                BaseFragmentActivityApi14.checkForValidRequestCode(n);
                ActivityCompat.startIntentSenderForResult(this, intentSender, (this.allocateRequestIndex(fragment) + 1 << 16) + (0xFFFF & n), intent, n2, n3, n4, bundle);
                return;
                ActivityCompat.startIntentSenderForResult(this, intentSender, n, intent, n2, n3, n4, bundle);
            }
            finally {
                this.mStartedIntentSenderFromFragment = false;
            }
        }
    }
    
    public void supportFinishAfterTransition() {
        ActivityCompat.finishAfterTransition(this);
    }
    
    @Deprecated
    public void supportInvalidateOptionsMenu() {
        this.invalidateOptionsMenu();
    }
    
    public void supportPostponeEnterTransition() {
        ActivityCompat.postponeEnterTransition(this);
    }
    
    public void supportStartPostponedEnterTransition() {
        ActivityCompat.startPostponedEnterTransition(this);
    }
    
    @Override
    public final void validateRequestPermissionsRequestCode(final int n) {
        if (!this.mRequestedPermissionsFromFragment && n != -1) {
            BaseFragmentActivityApi14.checkForValidRequestCode(n);
        }
    }
    
    class HostCallbacks extends FragmentHostCallback<FragmentActivity>
    {
        public HostCallbacks() {
            super(FragmentActivity.this);
        }
        
        public void onAttachFragment(final Fragment fragment) {
            FragmentActivity.this.onAttachFragment(fragment);
        }
        
        @Override
        public void onDump(final String s, final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
            FragmentActivity.this.dump(s, fileDescriptor, printWriter, array);
        }
        
        @Nullable
        @Override
        public View onFindViewById(final int n) {
            return FragmentActivity.this.findViewById(n);
        }
        
        @Override
        public FragmentActivity onGetHost() {
            return FragmentActivity.this;
        }
        
        @Override
        public LayoutInflater onGetLayoutInflater() {
            return FragmentActivity.this.getLayoutInflater().cloneInContext((Context)FragmentActivity.this);
        }
        
        @Override
        public int onGetWindowAnimations() {
            final Window window = FragmentActivity.this.getWindow();
            int windowAnimations;
            if (window != null) {
                windowAnimations = window.getAttributes().windowAnimations;
            }
            else {
                windowAnimations = 0;
            }
            return windowAnimations;
        }
        
        @Override
        public boolean onHasView() {
            final Window window = FragmentActivity.this.getWindow();
            return window != null && window.peekDecorView() != null;
        }
        
        @Override
        public boolean onHasWindowAnimations() {
            return FragmentActivity.this.getWindow() != null;
        }
        
        @Override
        public void onRequestPermissionsFromFragment(@NonNull final Fragment fragment, @NonNull final String[] array, final int n) {
            FragmentActivity.this.requestPermissionsFromFragment(fragment, array, n);
        }
        
        @Override
        public boolean onShouldSaveFragmentState(final Fragment fragment) {
            boolean b = false;
            if (!FragmentActivity.this.isFinishing()) {
                b = true;
            }
            return b;
        }
        
        @Override
        public boolean onShouldShowRequestPermissionRationale(@NonNull final String s) {
            return ActivityCompat.shouldShowRequestPermissionRationale(FragmentActivity.this, s);
        }
        
        @Override
        public void onStartActivityFromFragment(final Fragment fragment, final Intent intent, final int n) {
            FragmentActivity.this.startActivityFromFragment(fragment, intent, n);
        }
        
        @Override
        public void onStartActivityFromFragment(final Fragment fragment, final Intent intent, final int n, @Nullable final Bundle bundle) {
            FragmentActivity.this.startActivityFromFragment(fragment, intent, n, bundle);
        }
        
        @Override
        public void onStartIntentSenderFromFragment(final Fragment fragment, final IntentSender intentSender, final int n, @Nullable final Intent intent, final int n2, final int n3, final int n4, final Bundle bundle) throws IntentSender$SendIntentException {
            FragmentActivity.this.startIntentSenderFromFragment(fragment, intentSender, n, intent, n2, n3, n4, bundle);
        }
        
        @Override
        public void onSupportInvalidateOptionsMenu() {
            FragmentActivity.this.supportInvalidateOptionsMenu();
        }
    }
    
    static final class NonConfigurationInstances
    {
        Object custom;
        FragmentManagerNonConfig fragments;
        SimpleArrayMap<String, LoaderManager> loaders;
    }
}
