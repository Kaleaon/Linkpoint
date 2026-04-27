// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import android.support.annotation.CallSuper;
import android.support.v4.util.DebugUtils;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.content.res.Resources$NotFoundException;
import android.view.animation.AnimationUtils;
import android.animation.AnimatorInflater;
import java.util.Collections;
import java.util.Arrays;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.res.Configuration;
import java.io.FileDescriptor;
import java.io.Writer;
import java.io.PrintWriter;
import android.support.v4.util.LogWriter;
import android.support.v4.view.ViewCompat;
import android.os.Build$VERSION;
import java.util.Iterator;
import android.graphics.Paint;
import java.util.List;
import android.animation.PropertyValuesHolder;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.animation.ScaleAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.AlphaAnimation;
import android.content.Context;
import android.util.Log;
import java.util.Collection;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.Animation$AnimationListener;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.view.View;
import android.view.ViewGroup;
import android.animation.AnimatorListenerAdapter;
import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.util.Pair;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;
import android.util.SparseArray;
import java.lang.reflect.Field;
import android.view.animation.Interpolator;
import android.view.LayoutInflater$Factory2;

final class FragmentManagerImpl extends FragmentManager implements LayoutInflater$Factory2
{
    static final Interpolator ACCELERATE_CUBIC;
    static final Interpolator ACCELERATE_QUINT;
    static final int ANIM_DUR = 220;
    public static final int ANIM_STYLE_CLOSE_ENTER = 3;
    public static final int ANIM_STYLE_CLOSE_EXIT = 4;
    public static final int ANIM_STYLE_FADE_ENTER = 5;
    public static final int ANIM_STYLE_FADE_EXIT = 6;
    public static final int ANIM_STYLE_OPEN_ENTER = 1;
    public static final int ANIM_STYLE_OPEN_EXIT = 2;
    static boolean DEBUG = false;
    static final Interpolator DECELERATE_CUBIC;
    static final Interpolator DECELERATE_QUINT;
    static final String TAG = "FragmentManager";
    static final String TARGET_REQUEST_CODE_STATE_TAG = "android:target_req_state";
    static final String TARGET_STATE_TAG = "android:target_state";
    static final String USER_VISIBLE_HINT_TAG = "android:user_visible_hint";
    static final String VIEW_STATE_TAG = "android:view_state";
    static Field sAnimationListenerField;
    SparseArray<Fragment> mActive;
    final ArrayList<Fragment> mAdded;
    ArrayList<Integer> mAvailBackStackIndices;
    ArrayList<BackStackRecord> mBackStack;
    ArrayList<OnBackStackChangedListener> mBackStackChangeListeners;
    ArrayList<BackStackRecord> mBackStackIndices;
    FragmentContainer mContainer;
    ArrayList<Fragment> mCreatedMenus;
    int mCurState;
    boolean mDestroyed;
    Runnable mExecCommit;
    boolean mExecutingActions;
    boolean mHavePendingDeferredStart;
    FragmentHostCallback mHost;
    private final CopyOnWriteArrayList<Pair<FragmentLifecycleCallbacks, Boolean>> mLifecycleCallbacks;
    boolean mNeedMenuInvalidate;
    int mNextFragmentIndex;
    String mNoTransactionsBecause;
    Fragment mParent;
    ArrayList<OpGenerator> mPendingActions;
    ArrayList<StartEnterTransitionListener> mPostponedTransactions;
    Fragment mPrimaryNav;
    FragmentManagerNonConfig mSavedNonConfig;
    SparseArray<Parcelable> mStateArray;
    Bundle mStateBundle;
    boolean mStateSaved;
    ArrayList<Fragment> mTmpAddedFragments;
    ArrayList<Boolean> mTmpIsPop;
    ArrayList<BackStackRecord> mTmpRecords;
    
    static {
        FragmentManagerImpl.DEBUG = false;
        FragmentManagerImpl.sAnimationListenerField = null;
        DECELERATE_QUINT = (Interpolator)new DecelerateInterpolator(2.5f);
        DECELERATE_CUBIC = (Interpolator)new DecelerateInterpolator(1.5f);
        ACCELERATE_QUINT = (Interpolator)new AccelerateInterpolator(2.5f);
        ACCELERATE_CUBIC = (Interpolator)new AccelerateInterpolator(1.5f);
    }
    
    FragmentManagerImpl() {
        this.mNextFragmentIndex = 0;
        this.mAdded = new ArrayList<Fragment>();
        this.mLifecycleCallbacks = new CopyOnWriteArrayList<Pair<FragmentLifecycleCallbacks, Boolean>>();
        this.mCurState = 0;
        this.mStateBundle = null;
        this.mStateArray = null;
        this.mExecCommit = new Runnable() {
            @Override
            public void run() {
                FragmentManagerImpl.this.execPendingActions();
            }
        };
    }
    
    private void addAddedFragments(final ArraySet<Fragment> set) {
        if (this.mCurState >= 1) {
            final int min = Math.min(this.mCurState, 4);
            for (int size = this.mAdded.size(), i = 0; i < size; ++i) {
                final Fragment fragment = this.mAdded.get(i);
                if (fragment.mState < min) {
                    this.moveToState(fragment, min, fragment.getNextAnim(), fragment.getNextTransition(), false);
                    if (fragment.mView != null && !fragment.mHidden && fragment.mIsNewlyAdded) {
                        set.add(fragment);
                    }
                }
            }
        }
    }
    
    private void animateRemoveFragment(@NonNull final Fragment fragment, @NonNull final AnimationOrAnimator animationOrAnimator, final int stateAfterAnimating) {
        final View mView = fragment.mView;
        fragment.setStateAfterAnimating(stateAfterAnimating);
        if (animationOrAnimator.animation == null) {
            final Animator animator = animationOrAnimator.animator;
            fragment.setAnimator(animationOrAnimator.animator);
            final ViewGroup mContainer = fragment.mContainer;
            if (mContainer != null) {
                mContainer.startViewTransition(mView);
            }
            animator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    if (mContainer != null) {
                        mContainer.endViewTransition(mView);
                    }
                    if (fragment.getAnimator() != null) {
                        fragment.setAnimator(null);
                        FragmentManagerImpl.this.moveToState(fragment, fragment.getStateAfterAnimating(), 0, 0, false);
                    }
                }
            });
            animator.setTarget((Object)fragment.mView);
            setHWLayerAnimListenerIfAlpha(fragment.mView, animationOrAnimator);
            animator.start();
        }
        else {
            final Animation animation = animationOrAnimator.animation;
            fragment.setAnimatingAway(fragment.mView);
            animation.setAnimationListener((Animation$AnimationListener)new AnimationListenerWrapper(getAnimationListener(animation)) {
                @Override
                public void onAnimationEnd(final Animation animation) {
                    super.onAnimationEnd(animation);
                    if (fragment.getAnimatingAway() != null) {
                        fragment.setAnimatingAway(null);
                        FragmentManagerImpl.this.moveToState(fragment, fragment.getStateAfterAnimating(), 0, 0, false);
                    }
                }
            });
            setHWLayerAnimListenerIfAlpha(mView, animationOrAnimator);
            fragment.mView.startAnimation(animation);
        }
    }
    
    private void burpActive() {
        if (this.mActive != null) {
            int size = this.mActive.size();
            while (true) {
                final int n = size - 1;
                if (n < 0) {
                    break;
                }
                size = n;
                if (this.mActive.valueAt(n) != null) {
                    continue;
                }
                this.mActive.delete(this.mActive.keyAt(n));
                size = n;
            }
        }
    }
    
    private void checkStateLoss() {
        if (this.mStateSaved) {
            throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
        }
        if (this.mNoTransactionsBecause == null) {
            return;
        }
        throw new IllegalStateException("Can not perform this action inside of " + this.mNoTransactionsBecause);
    }
    
    private void cleanupExec() {
        this.mExecutingActions = false;
        this.mTmpIsPop.clear();
        this.mTmpRecords.clear();
    }
    
    private void completeExecute(final BackStackRecord e, final boolean b, final boolean b2, final boolean b3) {
        if (!b) {
            e.executeOps();
        }
        else {
            e.executePopOps(b3);
        }
        final ArrayList list = new ArrayList(1);
        final ArrayList list2 = new ArrayList(1);
        list.add(e);
        list2.add(b);
        if (b2) {
            FragmentTransition.startTransitions(this, list, list2, 0, 1, true);
        }
        if (b3) {
            this.moveToState(this.mCurState, true);
        }
        if (this.mActive != null) {
            for (int size = this.mActive.size(), i = 0; i < size; ++i) {
                final Fragment fragment = (Fragment)this.mActive.valueAt(i);
                if (fragment != null && fragment.mView != null && fragment.mIsNewlyAdded && e.interactsWith(fragment.mContainerId)) {
                    if (fragment.mPostponedAlpha > 0.0f) {
                        fragment.mView.setAlpha(fragment.mPostponedAlpha);
                    }
                    if (!b3) {
                        fragment.mPostponedAlpha = -1.0f;
                        fragment.mIsNewlyAdded = false;
                    }
                    else {
                        fragment.mPostponedAlpha = 0.0f;
                    }
                }
            }
        }
    }
    
    private void dispatchStateChange(final int n) {
        try {
            this.mExecutingActions = true;
            this.moveToState(n, false);
            this.mExecutingActions = false;
            this.execPendingActions();
        }
        finally {
            this.mExecutingActions = false;
        }
    }
    
    private void endAnimatingAwayFragments() {
        int size;
        if (this.mActive != null) {
            size = this.mActive.size();
        }
        else {
            size = 0;
        }
        for (int i = 0; i < size; ++i) {
            final Fragment fragment = (Fragment)this.mActive.valueAt(i);
            if (fragment != null) {
                if (fragment.getAnimatingAway() == null) {
                    if (fragment.getAnimator() != null) {
                        fragment.getAnimator().end();
                    }
                }
                else {
                    final int stateAfterAnimating = fragment.getStateAfterAnimating();
                    final View animatingAway = fragment.getAnimatingAway();
                    fragment.setAnimatingAway(null);
                    final Animation animation = animatingAway.getAnimation();
                    if (animation != null) {
                        animation.cancel();
                        animatingAway.clearAnimation();
                    }
                    this.moveToState(fragment, stateAfterAnimating, 0, 0, false);
                }
            }
        }
    }
    
    private void ensureExecReady(final boolean b) {
        while (true) {
            while (true) {
                Label_0051: {
                    if (this.mExecutingActions) {
                        break Label_0051;
                    }
                    Label_0062: {
                        if (Looper.myLooper() != this.mHost.getHandler().getLooper()) {
                            break Label_0062;
                        }
                        if (b) {
                            break Label_0051;
                        }
                        Label_0073: {
                            break Label_0073;
                            while (true) {
                                this.mExecutingActions = true;
                                try {
                                    this.executePostponedTransaction(null, null);
                                    return;
                                    throw new IllegalStateException("Must be called from main thread of fragment host");
                                    this.checkStateLoss();
                                    break;
                                    throw new IllegalStateException("FragmentManager is already executing transactions");
                                    this.mTmpRecords = new ArrayList<BackStackRecord>();
                                    this.mTmpIsPop = new ArrayList<Boolean>();
                                }
                                finally {
                                    this.mExecutingActions = false;
                                }
                            }
                        }
                    }
                }
                if (this.mTmpRecords != null) {
                    continue;
                }
                break;
            }
            continue;
        }
    }
    
    private static void executeOps(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2, int i, final int n) {
        while (i < n) {
            final BackStackRecord backStackRecord = list.get(i);
            if (!list2.get(i)) {
                backStackRecord.bumpBackStackNesting(1);
                backStackRecord.executeOps();
            }
            else {
                backStackRecord.bumpBackStackNesting(-1);
                backStackRecord.executePopOps(i == n - 1);
            }
            ++i;
        }
    }
    
    private void executeOpsTogether(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2, final int index, final int n) {
        final boolean mReorderingAllowed = list.get(index).mReorderingAllowed;
        if (this.mTmpAddedFragments != null) {
            this.mTmpAddedFragments.clear();
        }
        else {
            this.mTmpAddedFragments = new ArrayList<Fragment>();
        }
        this.mTmpAddedFragments.addAll(this.mAdded);
        Fragment fragment = this.getPrimaryNavigationFragment();
        int i = index;
        int n2 = 0;
        while (i < n) {
            final BackStackRecord backStackRecord = list.get(i);
            if (list2.get(i)) {
                fragment = backStackRecord.trackAddedFragmentsInPop(this.mTmpAddedFragments, fragment);
            }
            else {
                fragment = backStackRecord.expandOps(this.mTmpAddedFragments, fragment);
            }
            if (n2 == 0 && !backStackRecord.mAddToBackStack) {
                n2 = 0;
            }
            else {
                n2 = 1;
            }
            ++i;
        }
        this.mTmpAddedFragments.clear();
        if (!mReorderingAllowed) {
            FragmentTransition.startTransitions(this, list, list2, index, n, false);
        }
        executeOps(list, list2, index, n);
        int postponePostponableTransactions;
        if (!mReorderingAllowed) {
            postponePostponableTransactions = n;
        }
        else {
            final ArraySet<Fragment> set = new ArraySet<Fragment>();
            this.addAddedFragments(set);
            postponePostponableTransactions = this.postponePostponableTransactions(list, list2, index, n, set);
            this.makeRemovedFragmentsInvisible(set);
        }
        int j;
        if (postponePostponableTransactions == index) {
            j = index;
        }
        else {
            j = index;
            if (mReorderingAllowed) {
                FragmentTransition.startTransitions(this, list, list2, index, postponePostponableTransactions, true);
                this.moveToState(this.mCurState, true);
                j = index;
            }
        }
        while (j < n) {
            final BackStackRecord backStackRecord2 = list.get(j);
            if (list2.get(j) && backStackRecord2.mIndex >= 0) {
                this.freeBackStackIndex(backStackRecord2.mIndex);
                backStackRecord2.mIndex = -1;
            }
            backStackRecord2.runOnCommitRunnables();
            ++j;
        }
        if (n2 != 0) {
            this.reportBackStackChanged();
        }
    }
    
    private void executePostponedTransaction(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2) {
        int size;
        if (this.mPostponedTransactions != null) {
            size = this.mPostponedTransactions.size();
        }
        else {
            size = 0;
        }
        for (int i = 0; i < size; ++i) {
            final StartEnterTransitionListener startEnterTransitionListener = this.mPostponedTransactions.get(i);
            if (list != null && !startEnterTransitionListener.mIsBack) {
                final int index = list.indexOf(startEnterTransitionListener.mRecord);
                if (index != -1 && list2.get(index)) {
                    startEnterTransitionListener.cancelTransaction();
                    continue;
                }
            }
            if (!startEnterTransitionListener.isReady()) {
                if (list == null) {
                    continue;
                }
                if (!startEnterTransitionListener.mRecord.interactsWith(list, 0, list.size())) {
                    continue;
                }
            }
            this.mPostponedTransactions.remove(i);
            --i;
            --size;
            if (list != null && !startEnterTransitionListener.mIsBack) {
                final int index2 = list.indexOf(startEnterTransitionListener.mRecord);
                if (index2 != -1 && list2.get(index2)) {
                    startEnterTransitionListener.cancelTransaction();
                    continue;
                }
            }
            startEnterTransitionListener.completeTransaction();
        }
    }
    
    private Fragment findFragmentUnder(Fragment o) {
        final ViewGroup mContainer = o.mContainer;
        final View mView = o.mView;
        if (mContainer != null && mView != null) {
            for (int i = this.mAdded.indexOf(o) - 1; i >= 0; --i) {
                o = this.mAdded.get(i);
                if (o.mContainer == mContainer && o.mView != null) {
                    return o;
                }
            }
            return null;
        }
        return null;
    }
    
    private void forcePostponedTransactions() {
        if (this.mPostponedTransactions != null) {
            while (!this.mPostponedTransactions.isEmpty()) {
                this.mPostponedTransactions.remove(0).completeTransaction();
            }
        }
    }
    
    private boolean generateOpsForPendingActions(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2) {
        synchronized (this) {
            if (this.mPendingActions != null && this.mPendingActions.size() != 0) {
                final int size = this.mPendingActions.size();
                int i = 0;
                boolean b = false;
                while (i < size) {
                    b |= this.mPendingActions.get(i).generateOps(list, list2);
                    ++i;
                }
                this.mPendingActions.clear();
                this.mHost.getHandler().removeCallbacks(this.mExecCommit);
                return b;
            }
            return false;
        }
    }
    
    private static Animation$AnimationListener getAnimationListener(final Animation obj) {
        try {
            if (FragmentManagerImpl.sAnimationListenerField == null) {
                (FragmentManagerImpl.sAnimationListenerField = Animation.class.getDeclaredField("mListener")).setAccessible(true);
            }
            return (Animation$AnimationListener)FragmentManagerImpl.sAnimationListenerField.get(obj);
        }
        catch (final NoSuchFieldException ex) {
            Log.e("FragmentManager", "No field with the name mListener is found in Animation class", (Throwable)ex);
            return null;
        }
        catch (final IllegalAccessException ex2) {
            Log.e("FragmentManager", "Cannot access Animation's mListener field", (Throwable)ex2);
            return null;
        }
    }
    
    static AnimationOrAnimator makeFadeAnimation(final Context context, final float n, final float n2) {
        final AlphaAnimation alphaAnimation = new AlphaAnimation(n, n2);
        alphaAnimation.setInterpolator(FragmentManagerImpl.DECELERATE_CUBIC);
        alphaAnimation.setDuration(220L);
        return new AnimationOrAnimator((Animation)alphaAnimation);
    }
    
    static AnimationOrAnimator makeOpenCloseAnimation(final Context context, final float n, final float n2, final float n3, final float n4) {
        final AnimationSet set = new AnimationSet(false);
        final ScaleAnimation scaleAnimation = new ScaleAnimation(n, n2, n, n2, 1, 0.5f, 1, 0.5f);
        scaleAnimation.setInterpolator(FragmentManagerImpl.DECELERATE_QUINT);
        scaleAnimation.setDuration(220L);
        set.addAnimation((Animation)scaleAnimation);
        final AlphaAnimation alphaAnimation = new AlphaAnimation(n3, n4);
        alphaAnimation.setInterpolator(FragmentManagerImpl.DECELERATE_CUBIC);
        alphaAnimation.setDuration(220L);
        set.addAnimation((Animation)alphaAnimation);
        return new AnimationOrAnimator((Animation)set);
    }
    
    private void makeRemovedFragmentsInvisible(final ArraySet<Fragment> set) {
        for (int size = set.size(), i = 0; i < size; ++i) {
            final Fragment fragment = set.valueAt(i);
            if (!fragment.mAdded) {
                final View view = fragment.getView();
                fragment.mPostponedAlpha = view.getAlpha();
                view.setAlpha(0.0f);
            }
        }
    }
    
    static boolean modifiesAlpha(final Animator animator) {
        if (animator != null) {
            if (!(animator instanceof ValueAnimator)) {
                if (animator instanceof AnimatorSet) {
                    final ArrayList childAnimations = ((AnimatorSet)animator).getChildAnimations();
                    for (int i = 0; i < childAnimations.size(); ++i) {
                        if (modifiesAlpha((Animator)childAnimations.get(i))) {
                            return true;
                        }
                    }
                }
            }
            else {
                final PropertyValuesHolder[] values = ((ValueAnimator)animator).getValues();
                for (int j = 0; j < values.length; ++j) {
                    if ("alpha".equals(values[j].getPropertyName())) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }
    
    static boolean modifiesAlpha(final AnimationOrAnimator animationOrAnimator) {
        if (animationOrAnimator.animation instanceof AlphaAnimation) {
            return true;
        }
        if (!(animationOrAnimator.animation instanceof AnimationSet)) {
            return modifiesAlpha(animationOrAnimator.animator);
        }
        final List animations = ((AnimationSet)animationOrAnimator.animation).getAnimations();
        for (int i = 0; i < animations.size(); ++i) {
            if (animations.get(i) instanceof AlphaAnimation) {
                return true;
            }
        }
        return false;
    }
    
    private boolean popBackStackImmediate(final String s, final int n, final int n2) {
        this.execPendingActions();
        this.ensureExecReady(true);
        if (this.mPrimaryNav != null && n < 0 && s == null) {
            final FragmentManager peekChildFragmentManager = this.mPrimaryNav.peekChildFragmentManager();
            if (peekChildFragmentManager != null && peekChildFragmentManager.popBackStackImmediate()) {
                return true;
            }
        }
        final boolean popBackStackState = this.popBackStackState(this.mTmpRecords, this.mTmpIsPop, s, n, n2);
        if (popBackStackState) {
            this.mExecutingActions = true;
            try {
                this.removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
            }
            finally {
                this.cleanupExec();
            }
        }
        this.doPendingDeferredStart();
        this.burpActive();
        return popBackStackState;
    }
    
    private int postponePostponableTransactions(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2, final int n, final int n2, final ArraySet<Fragment> set) {
        int i = n2 - 1;
        int index = n2;
        while (i >= n) {
            final BackStackRecord element = list.get(i);
            final boolean booleanValue = list2.get(i);
            int n3;
            if (element.isPostponed() && !element.interactsWith(list, i + 1, n2)) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            if (n3 != 0) {
                if (this.mPostponedTransactions == null) {
                    this.mPostponedTransactions = new ArrayList<StartEnterTransitionListener>();
                }
                final StartEnterTransitionListener startEnterTransitionListener = new StartEnterTransitionListener(element, booleanValue);
                this.mPostponedTransactions.add(startEnterTransitionListener);
                element.setOnStartPostponedListener(startEnterTransitionListener);
                if (!booleanValue) {
                    element.executePopOps(false);
                }
                else {
                    element.executeOps();
                }
                --index;
                if (i != index) {
                    list.remove(i);
                    list.add(index, element);
                }
                this.addAddedFragments(set);
            }
            --i;
        }
        return index;
    }
    
    private void removeRedundantOperationsAndExecute(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2) {
        int i = 0;
        if (list == null || list.isEmpty()) {
            return;
        }
        if (list2 != null && list.size() == list2.size()) {
            this.executePostponedTransaction(list, list2);
            final int size = list.size();
            int n = 0;
            while (i < size) {
                int n2;
                if (((BackStackRecord)list.get(i)).mReorderingAllowed) {
                    n2 = i;
                }
                else {
                    if (n != i) {
                        this.executeOpsTogether(list, list2, n, i);
                    }
                    int j;
                    final int n3 = j = i + 1;
                    if (!(boolean)list2.get(i)) {
                        j = n3;
                    }
                    else {
                        while (j < size) {
                            if (!(boolean)list2.get(j)) {
                                break;
                            }
                            if (((BackStackRecord)list.get(j)).mReorderingAllowed) {
                                break;
                            }
                            ++j;
                        }
                    }
                    this.executeOpsTogether(list, list2, i, j);
                    final int n4 = j;
                    n2 = j - 1;
                    n = n4;
                }
                i = n2 + 1;
            }
            if (n != size) {
                this.executeOpsTogether(list, list2, n, size);
            }
            return;
        }
        throw new IllegalStateException("Internal error with the back stack records");
    }
    
    public static int reverseTransit(int n) {
        final int n2 = 0;
        switch (n) {
            default: {
                n = n2;
                break;
            }
            case 4097: {
                n = 8194;
                break;
            }
            case 8194: {
                n = 4097;
                break;
            }
            case 4099: {
                n = 4099;
                break;
            }
        }
        return n;
    }
    
    private void scheduleCommit() {
        boolean b = true;
        synchronized (this) {
            boolean b2;
            if (this.mPostponedTransactions != null && !this.mPostponedTransactions.isEmpty()) {
                b2 = true;
            }
            else {
                b2 = false;
            }
            if (this.mPendingActions == null || this.mPendingActions.size() != 1) {
                b = false;
            }
            if (b2 || b) {
                this.mHost.getHandler().removeCallbacks(this.mExecCommit);
                this.mHost.getHandler().post(this.mExecCommit);
            }
        }
    }
    
    private static void setHWLayerAnimListenerIfAlpha(final View view, final AnimationOrAnimator animationOrAnimator) {
        if (view != null && animationOrAnimator != null) {
            if (shouldRunOnHWLayer(view, animationOrAnimator)) {
                if (animationOrAnimator.animator == null) {
                    final Animation$AnimationListener animationListener = getAnimationListener(animationOrAnimator.animation);
                    view.setLayerType(2, (Paint)null);
                    animationOrAnimator.animation.setAnimationListener((Animation$AnimationListener)new AnimateOnHWLayerIfNeededListener(view, animationListener));
                }
                else {
                    animationOrAnimator.animator.addListener((Animator$AnimatorListener)new AnimatorOnHWLayerIfNeededListener(view));
                }
            }
        }
    }
    
    private static void setRetaining(final FragmentManagerNonConfig fragmentManagerNonConfig) {
        if (fragmentManagerNonConfig != null) {
            final List<Fragment> fragments = fragmentManagerNonConfig.getFragments();
            if (fragments != null) {
                final Iterator<Fragment> iterator = fragments.iterator();
                while (iterator.hasNext()) {
                    iterator.next().mRetaining = true;
                }
            }
            final List<FragmentManagerNonConfig> childNonConfigs = fragmentManagerNonConfig.getChildNonConfigs();
            if (childNonConfigs != null) {
                final Iterator<FragmentManagerNonConfig> iterator2 = childNonConfigs.iterator();
                while (iterator2.hasNext()) {
                    setRetaining(iterator2.next());
                }
            }
        }
    }
    
    static boolean shouldRunOnHWLayer(final View view, final AnimationOrAnimator animationOrAnimator) {
        final boolean b = false;
        if (view != null && animationOrAnimator != null) {
            boolean b2;
            if (Build$VERSION.SDK_INT < 19) {
                b2 = b;
            }
            else {
                b2 = b;
                if (view.getLayerType() == 0) {
                    b2 = b;
                    if (ViewCompat.hasOverlappingRendering(view)) {
                        b2 = b;
                        if (modifiesAlpha(animationOrAnimator)) {
                            b2 = true;
                        }
                    }
                }
            }
            return b2;
        }
        return false;
    }
    
    private void throwException(final RuntimeException ex) {
        Log.e("FragmentManager", ex.getMessage());
        Log.e("FragmentManager", "Activity state:");
        final PrintWriter printWriter = new PrintWriter(new LogWriter("FragmentManager"));
        Label_0058: {
            if (this.mHost != null) {
                break Label_0058;
            }
            try {
                this.dump("  ", null, printWriter, new String[0]);
                throw ex;
                try {
                    this.mHost.onDump("  ", null, printWriter, new String[0]);
                }
                catch (final Exception ex2) {
                    Log.e("FragmentManager", "Failed dumping state", (Throwable)ex2);
                }
                throw ex;
            }
            catch (final Exception ex3) {
                Log.e("FragmentManager", "Failed dumping state", (Throwable)ex3);
                throw ex;
            }
        }
    }
    
    public static int transitToStyleIndex(int n, final boolean b) {
        final int n2 = -1;
        switch (n) {
            default: {
                n = n2;
                break;
            }
            case 4097: {
                if (!b) {
                    n = 2;
                    break;
                }
                n = 1;
                break;
            }
            case 8194: {
                if (!b) {
                    n = 4;
                    break;
                }
                n = 3;
                break;
            }
            case 4099: {
                if (!b) {
                    n = 6;
                    break;
                }
                n = 5;
                break;
            }
        }
        return n;
    }
    
    void addBackStackState(final BackStackRecord e) {
        if (this.mBackStack == null) {
            this.mBackStack = new ArrayList<BackStackRecord>();
        }
        this.mBackStack.add(e);
    }
    
    public void addFragment(final Fragment p0, final boolean p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: ifne            19
        //     6: aload_0        
        //     7: aload_1        
        //     8: invokevirtual   android/support/v4/app/FragmentManagerImpl.makeActive:(Landroid/support/v4/app/Fragment;)V
        //    11: aload_1        
        //    12: getfield        android/support/v4/app/Fragment.mDetached:Z
        //    15: ifeq            48
        //    18: return         
        //    19: ldc             "FragmentManager"
        //    21: new             Ljava/lang/StringBuilder;
        //    24: dup            
        //    25: invokespecial   java/lang/StringBuilder.<init>:()V
        //    28: ldc_w           "add: "
        //    31: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    34: aload_1        
        //    35: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //    38: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    41: invokestatic    android/util/Log.v:(Ljava/lang/String;Ljava/lang/String;)I
        //    44: pop            
        //    45: goto            6
        //    48: aload_0        
        //    49: getfield        android/support/v4/app/FragmentManagerImpl.mAdded:Ljava/util/ArrayList;
        //    52: aload_1        
        //    53: invokevirtual   java/util/ArrayList.contains:(Ljava/lang/Object;)Z
        //    56: ifne            113
        //    59: aload_0        
        //    60: getfield        android/support/v4/app/FragmentManagerImpl.mAdded:Ljava/util/ArrayList;
        //    63: astore_3       
        //    64: aload_3        
        //    65: monitorenter   
        //    66: aload_0        
        //    67: getfield        android/support/v4/app/FragmentManagerImpl.mAdded:Ljava/util/ArrayList;
        //    70: aload_1        
        //    71: invokevirtual   java/util/ArrayList.add:(Ljava/lang/Object;)Z
        //    74: pop            
        //    75: aload_3        
        //    76: monitorexit    
        //    77: aload_1        
        //    78: iconst_1       
        //    79: putfield        android/support/v4/app/Fragment.mAdded:Z
        //    82: aload_1        
        //    83: iconst_0       
        //    84: putfield        android/support/v4/app/Fragment.mRemoving:Z
        //    87: aload_1        
        //    88: getfield        android/support/v4/app/Fragment.mView:Landroid/view/View;
        //    91: ifnull          146
        //    94: aload_1        
        //    95: getfield        android/support/v4/app/Fragment.mHasMenu:Z
        //    98: ifne            154
        //   101: iload_2        
        //   102: ifeq            18
        //   105: aload_0        
        //   106: aload_1        
        //   107: invokevirtual   android/support/v4/app/FragmentManagerImpl.moveToState:(Landroid/support/v4/app/Fragment;)V
        //   110: goto            18
        //   113: new             Ljava/lang/IllegalStateException;
        //   116: dup            
        //   117: new             Ljava/lang/StringBuilder;
        //   120: dup            
        //   121: invokespecial   java/lang/StringBuilder.<init>:()V
        //   124: ldc_w           "Fragment already added: "
        //   127: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   130: aload_1        
        //   131: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   134: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   137: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
        //   140: athrow         
        //   141: astore_1       
        //   142: aload_3        
        //   143: monitorexit    
        //   144: aload_1        
        //   145: athrow         
        //   146: aload_1        
        //   147: iconst_0       
        //   148: putfield        android/support/v4/app/Fragment.mHiddenChanged:Z
        //   151: goto            94
        //   154: aload_1        
        //   155: getfield        android/support/v4/app/Fragment.mMenuVisible:Z
        //   158: ifeq            101
        //   161: aload_0        
        //   162: iconst_1       
        //   163: putfield        android/support/v4/app/FragmentManagerImpl.mNeedMenuInvalidate:Z
        //   166: goto            101
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  66     77     141    146    Any
        //  142    144    141    146    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: getfield:boolean(Fragment::mMenuVisible, var_1_8D:Object[expected:Fragment])
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
        //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    @Override
    public void addOnBackStackChangedListener(final OnBackStackChangedListener e) {
        if (this.mBackStackChangeListeners == null) {
            this.mBackStackChangeListeners = new ArrayList<OnBackStackChangedListener>();
        }
        this.mBackStackChangeListeners.add(e);
    }
    
    public int allocBackStackIndex(final BackStackRecord backStackRecord) {
        while (true) {
            while (true) {
                int i = 0;
                Label_0158: {
                    while (true) {
                        synchronized (this) {
                            if (this.mAvailBackStackIndices != null && this.mAvailBackStackIndices.size() > 0) {
                                i = this.mAvailBackStackIndices.remove(this.mAvailBackStackIndices.size() - 1);
                                if (!FragmentManagerImpl.DEBUG) {
                                    this.mBackStackIndices.set(i, backStackRecord);
                                    return i;
                                }
                                break Label_0158;
                            }
                            else {
                                if (this.mBackStackIndices == null) {
                                    this.mBackStackIndices = new ArrayList<BackStackRecord>();
                                }
                                i = this.mBackStackIndices.size();
                                if (!FragmentManagerImpl.DEBUG) {
                                    this.mBackStackIndices.add(backStackRecord);
                                    return i;
                                }
                            }
                        }
                        Log.v("FragmentManager", "Setting back stack index " + i + " to " + backStackRecord);
                        continue;
                    }
                }
                Log.v("FragmentManager", "Adding back stack index " + i + " with " + backStackRecord);
                continue;
            }
        }
    }
    
    public void attachController(final FragmentHostCallback mHost, final FragmentContainer mContainer, final Fragment mParent) {
        if (this.mHost == null) {
            this.mHost = mHost;
            this.mContainer = mContainer;
            this.mParent = mParent;
            return;
        }
        throw new IllegalStateException("Already attached");
    }
    
    public void attachFragment(final Fragment obj) {
        if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "attach: " + obj);
        }
        if (obj.mDetached) {
            obj.mDetached = false;
            if (!obj.mAdded) {
                Label_0117: {
                    if (this.mAdded.contains(obj)) {
                        break Label_0117;
                    }
                    Label_0145: {
                        if (FragmentManagerImpl.DEBUG) {
                            break Label_0145;
                        }
                        while (true) {
                            synchronized (this.mAdded) {
                                this.mAdded.add(obj);
                                monitorexit(this.mAdded);
                                obj.mAdded = true;
                                if (obj.mHasMenu && obj.mMenuVisible) {
                                    this.mNeedMenuInvalidate = true;
                                    break;
                                }
                                break;
                                Log.v("FragmentManager", "add from attach: " + obj);
                                continue;
                                throw new IllegalStateException("Fragment already added: " + obj);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public FragmentTransaction beginTransaction() {
        return new BackStackRecord(this);
    }
    
    void completeShowHideFragment(final Fragment fragment) {
        if (fragment.mView != null) {
            final AnimationOrAnimator loadAnimation = this.loadAnimation(fragment, fragment.getNextTransition(), !fragment.mHidden, fragment.getNextTransitionStyle());
            if (loadAnimation != null && loadAnimation.animator != null) {
                loadAnimation.animator.setTarget((Object)fragment.mView);
                if (!fragment.mHidden) {
                    fragment.mView.setVisibility(0);
                }
                else if (!fragment.isHideReplaced()) {
                    final ViewGroup mContainer = fragment.mContainer;
                    final View mView = fragment.mView;
                    mContainer.startViewTransition(mView);
                    loadAnimation.animator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                        public void onAnimationEnd(final Animator animator) {
                            mContainer.endViewTransition(mView);
                            animator.removeListener((Animator$AnimatorListener)this);
                            if (fragment.mView != null) {
                                fragment.mView.setVisibility(8);
                            }
                        }
                    });
                }
                else {
                    fragment.setHideReplaced(false);
                }
                setHWLayerAnimListenerIfAlpha(fragment.mView, loadAnimation);
                loadAnimation.animator.start();
            }
            else {
                if (loadAnimation != null) {
                    setHWLayerAnimListenerIfAlpha(fragment.mView, loadAnimation);
                    fragment.mView.startAnimation(loadAnimation.animation);
                    loadAnimation.animation.start();
                }
                int visibility;
                if (fragment.mHidden && !fragment.isHideReplaced()) {
                    visibility = 8;
                }
                else {
                    visibility = 0;
                }
                fragment.mView.setVisibility(visibility);
                if (fragment.isHideReplaced()) {
                    fragment.setHideReplaced(false);
                }
            }
        }
        if (fragment.mAdded && fragment.mHasMenu && fragment.mMenuVisible) {
            this.mNeedMenuInvalidate = true;
        }
        fragment.mHiddenChanged = false;
        fragment.onHiddenChanged(fragment.mHidden);
    }
    
    public void detachFragment(final Fragment obj) {
        if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "detach: " + obj);
        }
        if (!obj.mDetached) {
            obj.mDetached = true;
            if (obj.mAdded) {
                Label_0094: {
                    if (FragmentManagerImpl.DEBUG) {
                        break Label_0094;
                    }
                Label_0086_Outer:
                    while (true) {
                        while (true) {
                            Label_0128: {
                                synchronized (this.mAdded) {
                                    this.mAdded.remove(obj);
                                    monitorexit(this.mAdded);
                                    if (!obj.mHasMenu) {
                                        obj.mAdded = false;
                                        break;
                                    }
                                    break Label_0128;
                                    Log.v("FragmentManager", "remove from detach: " + obj);
                                    continue Label_0086_Outer;
                                }
                            }
                            if (obj.mMenuVisible) {
                                this.mNeedMenuInvalidate = true;
                                continue;
                            }
                            continue;
                        }
                    }
                }
            }
        }
    }
    
    public void dispatchActivityCreated() {
        this.mStateSaved = false;
        this.dispatchStateChange(2);
    }
    
    public void dispatchConfigurationChanged(final Configuration configuration) {
        for (int i = 0; i < this.mAdded.size(); ++i) {
            final Fragment fragment = this.mAdded.get(i);
            if (fragment != null) {
                fragment.performConfigurationChanged(configuration);
            }
        }
    }
    
    public boolean dispatchContextItemSelected(final MenuItem menuItem) {
        for (int i = 0; i < this.mAdded.size(); ++i) {
            final Fragment fragment = this.mAdded.get(i);
            if (fragment != null && fragment.performContextItemSelected(menuItem)) {
                return true;
            }
        }
        return false;
    }
    
    public void dispatchCreate() {
        this.mStateSaved = false;
        this.dispatchStateChange(1);
    }
    
    public boolean dispatchCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        final int n = 0;
        int i = 0;
        ArrayList<Fragment> mCreatedMenus = null;
        boolean b = false;
        while (i < this.mAdded.size()) {
            final Fragment e = this.mAdded.get(i);
            ArrayList<Fragment> list;
            if (e == null) {
                list = mCreatedMenus;
            }
            else {
                list = mCreatedMenus;
                if (e.performCreateOptionsMenu(menu, menuInflater)) {
                    b = true;
                    if (mCreatedMenus == null) {
                        mCreatedMenus = new ArrayList<Fragment>();
                    }
                    mCreatedMenus.add(e);
                    list = mCreatedMenus;
                }
            }
            ++i;
            mCreatedMenus = list;
        }
        int j = n;
        if (this.mCreatedMenus != null) {
            while (j < this.mCreatedMenus.size()) {
                final Fragment o = this.mCreatedMenus.get(j);
                if (mCreatedMenus == null || !mCreatedMenus.contains(o)) {
                    o.onDestroyOptionsMenu();
                }
                ++j;
            }
        }
        this.mCreatedMenus = mCreatedMenus;
        return b;
    }
    
    public void dispatchDestroy() {
        this.mDestroyed = true;
        this.execPendingActions();
        this.dispatchStateChange(0);
        this.mHost = null;
        this.mContainer = null;
        this.mParent = null;
    }
    
    public void dispatchDestroyView() {
        this.dispatchStateChange(1);
    }
    
    public void dispatchLowMemory() {
        for (int i = 0; i < this.mAdded.size(); ++i) {
            final Fragment fragment = this.mAdded.get(i);
            if (fragment != null) {
                fragment.performLowMemory();
            }
        }
    }
    
    public void dispatchMultiWindowModeChanged(final boolean b) {
        for (int i = this.mAdded.size() - 1; i >= 0; --i) {
            final Fragment fragment = this.mAdded.get(i);
            if (fragment != null) {
                fragment.performMultiWindowModeChanged(b);
            }
        }
    }
    
    void dispatchOnFragmentActivityCreated(final Fragment fragment, final Bundle bundle, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentActivityCreated(fragment, bundle, true);
            }
        }
        for (final Pair pair : this.mLifecycleCallbacks) {
            if (b && !(boolean)pair.second) {
                continue;
            }
            ((FragmentLifecycleCallbacks)pair.first).onFragmentActivityCreated(this, fragment, bundle);
        }
    }
    
    void dispatchOnFragmentAttached(final Fragment fragment, final Context context, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentAttached(fragment, context, true);
            }
        }
        for (final Pair pair : this.mLifecycleCallbacks) {
            if (b && !(boolean)pair.second) {
                continue;
            }
            ((FragmentLifecycleCallbacks)pair.first).onFragmentAttached(this, fragment, context);
        }
    }
    
    void dispatchOnFragmentCreated(final Fragment fragment, final Bundle bundle, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentCreated(fragment, bundle, true);
            }
        }
        for (final Pair pair : this.mLifecycleCallbacks) {
            if (b && !(boolean)pair.second) {
                continue;
            }
            ((FragmentLifecycleCallbacks)pair.first).onFragmentCreated(this, fragment, bundle);
        }
    }
    
    void dispatchOnFragmentDestroyed(final Fragment fragment, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentDestroyed(fragment, true);
            }
        }
        for (final Pair pair : this.mLifecycleCallbacks) {
            if (b && !(boolean)pair.second) {
                continue;
            }
            ((FragmentLifecycleCallbacks)pair.first).onFragmentDestroyed(this, fragment);
        }
    }
    
    void dispatchOnFragmentDetached(final Fragment fragment, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentDetached(fragment, true);
            }
        }
        for (final Pair pair : this.mLifecycleCallbacks) {
            if (b && !(boolean)pair.second) {
                continue;
            }
            ((FragmentLifecycleCallbacks)pair.first).onFragmentDetached(this, fragment);
        }
    }
    
    void dispatchOnFragmentPaused(final Fragment fragment, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentPaused(fragment, true);
            }
        }
        for (final Pair pair : this.mLifecycleCallbacks) {
            if (b && !(boolean)pair.second) {
                continue;
            }
            ((FragmentLifecycleCallbacks)pair.first).onFragmentPaused(this, fragment);
        }
    }
    
    void dispatchOnFragmentPreAttached(final Fragment fragment, final Context context, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentPreAttached(fragment, context, true);
            }
        }
        for (final Pair pair : this.mLifecycleCallbacks) {
            if (b && !(boolean)pair.second) {
                continue;
            }
            ((FragmentLifecycleCallbacks)pair.first).onFragmentPreAttached(this, fragment, context);
        }
    }
    
    void dispatchOnFragmentPreCreated(final Fragment fragment, final Bundle bundle, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentPreCreated(fragment, bundle, true);
            }
        }
        for (final Pair pair : this.mLifecycleCallbacks) {
            if (b && !(boolean)pair.second) {
                continue;
            }
            ((FragmentLifecycleCallbacks)pair.first).onFragmentPreCreated(this, fragment, bundle);
        }
    }
    
    void dispatchOnFragmentResumed(final Fragment fragment, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentResumed(fragment, true);
            }
        }
        for (final Pair pair : this.mLifecycleCallbacks) {
            if (b && !(boolean)pair.second) {
                continue;
            }
            ((FragmentLifecycleCallbacks)pair.first).onFragmentResumed(this, fragment);
        }
    }
    
    void dispatchOnFragmentSaveInstanceState(final Fragment fragment, final Bundle bundle, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentSaveInstanceState(fragment, bundle, true);
            }
        }
        for (final Pair pair : this.mLifecycleCallbacks) {
            if (b && !(boolean)pair.second) {
                continue;
            }
            ((FragmentLifecycleCallbacks)pair.first).onFragmentSaveInstanceState(this, fragment, bundle);
        }
    }
    
    void dispatchOnFragmentStarted(final Fragment fragment, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentStarted(fragment, true);
            }
        }
        for (final Pair pair : this.mLifecycleCallbacks) {
            if (b && !(boolean)pair.second) {
                continue;
            }
            ((FragmentLifecycleCallbacks)pair.first).onFragmentStarted(this, fragment);
        }
    }
    
    void dispatchOnFragmentStopped(final Fragment fragment, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentStopped(fragment, true);
            }
        }
        for (final Pair pair : this.mLifecycleCallbacks) {
            if (b && !(boolean)pair.second) {
                continue;
            }
            ((FragmentLifecycleCallbacks)pair.first).onFragmentStopped(this, fragment);
        }
    }
    
    void dispatchOnFragmentViewCreated(final Fragment fragment, final View view, final Bundle bundle, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentViewCreated(fragment, view, bundle, true);
            }
        }
        for (final Pair pair : this.mLifecycleCallbacks) {
            if (b && !(boolean)pair.second) {
                continue;
            }
            ((FragmentLifecycleCallbacks)pair.first).onFragmentViewCreated(this, fragment, view, bundle);
        }
    }
    
    void dispatchOnFragmentViewDestroyed(final Fragment fragment, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentViewDestroyed(fragment, true);
            }
        }
        for (final Pair pair : this.mLifecycleCallbacks) {
            if (b && !(boolean)pair.second) {
                continue;
            }
            ((FragmentLifecycleCallbacks)pair.first).onFragmentViewDestroyed(this, fragment);
        }
    }
    
    public boolean dispatchOptionsItemSelected(final MenuItem menuItem) {
        for (int i = 0; i < this.mAdded.size(); ++i) {
            final Fragment fragment = this.mAdded.get(i);
            if (fragment != null && fragment.performOptionsItemSelected(menuItem)) {
                return true;
            }
        }
        return false;
    }
    
    public void dispatchOptionsMenuClosed(final Menu menu) {
        for (int i = 0; i < this.mAdded.size(); ++i) {
            final Fragment fragment = this.mAdded.get(i);
            if (fragment != null) {
                fragment.performOptionsMenuClosed(menu);
            }
        }
    }
    
    public void dispatchPause() {
        this.dispatchStateChange(4);
    }
    
    public void dispatchPictureInPictureModeChanged(final boolean b) {
        for (int i = this.mAdded.size() - 1; i >= 0; --i) {
            final Fragment fragment = this.mAdded.get(i);
            if (fragment != null) {
                fragment.performPictureInPictureModeChanged(b);
            }
        }
    }
    
    public boolean dispatchPrepareOptionsMenu(final Menu menu) {
        int i = 0;
        boolean b = false;
        while (i < this.mAdded.size()) {
            final Fragment fragment = this.mAdded.get(i);
            if (fragment != null && fragment.performPrepareOptionsMenu(menu)) {
                b = true;
            }
            ++i;
        }
        return b;
    }
    
    public void dispatchReallyStop() {
        this.dispatchStateChange(2);
    }
    
    public void dispatchResume() {
        this.mStateSaved = false;
        this.dispatchStateChange(5);
    }
    
    public void dispatchStart() {
        this.mStateSaved = false;
        this.dispatchStateChange(4);
    }
    
    public void dispatchStop() {
        this.mStateSaved = true;
        this.dispatchStateChange(3);
    }
    
    void doPendingDeferredStart() {
        if (this.mHavePendingDeferredStart) {
            int i = 0;
            int n = 0;
            while (i < this.mActive.size()) {
                final Fragment fragment = (Fragment)this.mActive.valueAt(i);
                boolean b;
                if (fragment == null) {
                    b = (n != 0);
                }
                else {
                    b = (n != 0);
                    if (fragment.mLoaderManager != null) {
                        b = ((n | (fragment.mLoaderManager.hasRunningLoaders() ? 1 : 0)) != 0x0);
                    }
                }
                ++i;
                n = (b ? 1 : 0);
            }
            if (n == 0) {
                this.mHavePendingDeferredStart = false;
                this.startPendingDeferredFragments();
            }
        }
    }
    
    @Override
    public void dump(final String s, FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final int n = 0;
        final String string = s + "    ";
        while (true) {
        Label_0409_Outer:
            while (true) {
            Label_0333_Outer:
                while (true) {
                    while (true) {
                        Label_0032: {
                            if (this.mActive == null) {
                                break Label_0032;
                            }
                            Label_0209: {
                                break Label_0209;
                                int size;
                                int n2;
                                int size2;
                                int j;
                                Fragment x;
                                int n3;
                                Fragment fragment;
                                int size3;
                                int size4;
                                int n4;
                                BackStackRecord backStackRecord;
                                final int size5;
                                Fragment fragment2;
                                int size6;
                                Label_0144_Outer:Label_0201_Outer:
                                while (true) {
                                Label_0861:
                                    while (true) {
                                    Label_0838:
                                        while (true) {
                                            Label_0815:Label_0256_Outer:Label_0438_Outer:
                                            while (true) {
                                                Label_0729: {
                                                    synchronized (this) {
                                                        if (this.mBackStackIndices != null) {
                                                            size = this.mBackStackIndices.size();
                                                            if (size > 0) {
                                                                printWriter.print(s);
                                                                printWriter.println("Back Stack Indices:");
                                                                for (int i = 0; i < size; ++i) {
                                                                    fileDescriptor = (FileDescriptor)this.mBackStackIndices.get(i);
                                                                    printWriter.print(s);
                                                                    printWriter.print("  #");
                                                                    printWriter.print(i);
                                                                    printWriter.print(": ");
                                                                    printWriter.println(fileDescriptor);
                                                                }
                                                            }
                                                        }
                                                        if (this.mAvailBackStackIndices != null && this.mAvailBackStackIndices.size() > 0) {
                                                            printWriter.print(s);
                                                            printWriter.print("mAvailBackStackIndices: ");
                                                            printWriter.println(Arrays.toString(this.mAvailBackStackIndices.toArray()));
                                                        }
                                                        monitorexit(this);
                                                        if (this.mPendingActions != null) {
                                                            break Label_0729;
                                                        }
                                                        printWriter.print(s);
                                                        printWriter.println("FragmentManager misc state:");
                                                        printWriter.print(s);
                                                        printWriter.print("  mHost=");
                                                        printWriter.println(this.mHost);
                                                        printWriter.print(s);
                                                        printWriter.print("  mContainer=");
                                                        printWriter.println(this.mContainer);
                                                        if (this.mParent != null) {
                                                            break Label_0815;
                                                        }
                                                        printWriter.print(s);
                                                        printWriter.print("  mCurState=");
                                                        printWriter.print(this.mCurState);
                                                        printWriter.print(" mStateSaved=");
                                                        printWriter.print(this.mStateSaved);
                                                        printWriter.print(" mDestroyed=");
                                                        printWriter.println(this.mDestroyed);
                                                        if (this.mNeedMenuInvalidate) {
                                                            break Label_0838;
                                                        }
                                                        if (this.mNoTransactionsBecause == null) {
                                                            return;
                                                        }
                                                        break Label_0861;
                                                    Label_0348:
                                                        while (true) {
                                                        Label_0438:
                                                            while (true) {
                                                            Block_8:
                                                                while (true) {
                                                                Label_0256:
                                                                    while (true) {
                                                                        Block_6_Outer:Block_5_Outer:
                                                                        while (true) {
                                                                            iftrue(Label_0060:)(n2 >= size2);
                                                                            Block_11: {
                                                                                break Block_11;
                                                                                while (true) {
                                                                                    while (true) {
                                                                                        x = (Fragment)this.mActive.valueAt(j);
                                                                                        printWriter.print(s);
                                                                                        printWriter.print("  #");
                                                                                        printWriter.print(j);
                                                                                        printWriter.print(": ");
                                                                                        printWriter.println(x);
                                                                                        iftrue(Label_0319:)(x != null);
                                                                                        break Block_6_Outer;
                                                                                        Label_0319: {
                                                                                            x.dump(string, fileDescriptor, printWriter, array);
                                                                                        }
                                                                                        break Block_6_Outer;
                                                                                        printWriter.print(s);
                                                                                        printWriter.print("Active Fragments in ");
                                                                                        printWriter.print(Integer.toHexString(System.identityHashCode(this)));
                                                                                        printWriter.println(":");
                                                                                        j = 0;
                                                                                        break Label_0256;
                                                                                        fragment = this.mCreatedMenus.get(n3);
                                                                                        printWriter.print(s);
                                                                                        printWriter.print("  #");
                                                                                        printWriter.print(n3);
                                                                                        printWriter.print(": ");
                                                                                        printWriter.println(fragment.toString());
                                                                                        ++n3;
                                                                                        break Label_0438;
                                                                                        size3 = this.mCreatedMenus.size();
                                                                                        iftrue(Label_0053:)(size3 <= 0);
                                                                                        break Block_8;
                                                                                        iftrue(Label_0032:)(j >= size4);
                                                                                        continue Block_5_Outer;
                                                                                    }
                                                                                    while (true) {
                                                                                        printWriter.print(s);
                                                                                        printWriter.println("Back Stack:");
                                                                                        n2 = 0;
                                                                                        continue Block_6_Outer;
                                                                                        size2 = this.mBackStack.size();
                                                                                        iftrue(Label_0060:)(size2 <= 0);
                                                                                        continue Label_0333_Outer;
                                                                                    }
                                                                                    printWriter.print(s);
                                                                                    printWriter.println("Added Fragments:");
                                                                                    n4 = 0;
                                                                                    break Label_0348;
                                                                                    size4 = this.mActive.size();
                                                                                    iftrue(Label_0032:)(size4 <= 0);
                                                                                    continue Label_0256_Outer;
                                                                                }
                                                                            }
                                                                            backStackRecord = this.mBackStack.get(n2);
                                                                            printWriter.print(s);
                                                                            printWriter.print("  #");
                                                                            printWriter.print(n2);
                                                                            printWriter.print(": ");
                                                                            printWriter.println(backStackRecord.toString());
                                                                            backStackRecord.dump(string, fileDescriptor, printWriter, array);
                                                                            ++n2;
                                                                            continue Label_0256_Outer;
                                                                        }
                                                                        ++j;
                                                                        continue Label_0256;
                                                                    }
                                                                    iftrue(Label_0046:)(n4 >= size5);
                                                                    break Label_0438;
                                                                    iftrue(Label_0053:)(n3 >= size3);
                                                                    continue Label_0438_Outer;
                                                                }
                                                                printWriter.print(s);
                                                                printWriter.println("Fragments Created Menus:");
                                                                n3 = 0;
                                                                continue Label_0438;
                                                            }
                                                            fragment2 = this.mAdded.get(n4);
                                                            printWriter.print(s);
                                                            printWriter.print("  #");
                                                            printWriter.print(n4);
                                                            printWriter.print(": ");
                                                            printWriter.println(fragment2.toString());
                                                            ++n4;
                                                            continue Label_0348;
                                                        }
                                                    }
                                                }
                                                size6 = this.mPendingActions.size();
                                                if (size6 > 0) {
                                                    printWriter.print(s);
                                                    printWriter.println("Pending Actions:");
                                                    for (int k = n; k < size6; ++k) {
                                                        fileDescriptor = (FileDescriptor)this.mPendingActions.get(k);
                                                        printWriter.print(s);
                                                        printWriter.print("  #");
                                                        printWriter.print(k);
                                                        printWriter.print(": ");
                                                        printWriter.println(fileDescriptor);
                                                    }
                                                    continue Label_0144_Outer;
                                                }
                                                continue Label_0144_Outer;
                                            }
                                            printWriter.print(s);
                                            printWriter.print("  mParent=");
                                            printWriter.println(this.mParent);
                                            continue Label_0201_Outer;
                                        }
                                        printWriter.print(s);
                                        printWriter.print("  mNeedMenuInvalidate=");
                                        printWriter.println(this.mNeedMenuInvalidate);
                                        continue Label_0409_Outer;
                                    }
                                    printWriter.print(s);
                                    printWriter.print("  mNoTransactionsBecause=");
                                    printWriter.println(this.mNoTransactionsBecause);
                                    return;
                                }
                            }
                        }
                        final int size5 = this.mAdded.size();
                        if (size5 > 0) {
                            continue;
                        }
                        break;
                    }
                    Label_0046: {
                        if (this.mCreatedMenus != null) {
                            continue Label_0333_Outer;
                        }
                    }
                    break;
                }
                Label_0053: {
                    if (this.mBackStack == null) {
                        continue Label_0409_Outer;
                    }
                }
                break;
            }
            continue;
        }
    }
    
    public void enqueueAction(final OpGenerator p0, final boolean p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ifeq            35
        //     4: aload_0        
        //     5: monitorenter   
        //     6: aload_0        
        //     7: getfield        android/support/v4/app/FragmentManagerImpl.mDestroyed:Z
        //    10: ifeq            42
        //    13: iload_2        
        //    14: ifne            72
        //    17: new             Ljava/lang/IllegalStateException;
        //    20: astore_1       
        //    21: aload_1        
        //    22: ldc_w           "Activity has been destroyed"
        //    25: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
        //    28: aload_1        
        //    29: athrow         
        //    30: astore_1       
        //    31: aload_0        
        //    32: monitorexit    
        //    33: aload_1        
        //    34: athrow         
        //    35: aload_0        
        //    36: invokespecial   android/support/v4/app/FragmentManagerImpl.checkStateLoss:()V
        //    39: goto            4
        //    42: aload_0        
        //    43: getfield        android/support/v4/app/FragmentManagerImpl.mHost:Landroid/support/v4/app/FragmentHostCallback;
        //    46: ifnull          13
        //    49: aload_0        
        //    50: getfield        android/support/v4/app/FragmentManagerImpl.mPendingActions:Ljava/util/ArrayList;
        //    53: ifnull          75
        //    56: aload_0        
        //    57: getfield        android/support/v4/app/FragmentManagerImpl.mPendingActions:Ljava/util/ArrayList;
        //    60: aload_1        
        //    61: invokevirtual   java/util/ArrayList.add:(Ljava/lang/Object;)Z
        //    64: pop            
        //    65: aload_0        
        //    66: invokespecial   android/support/v4/app/FragmentManagerImpl.scheduleCommit:()V
        //    69: aload_0        
        //    70: monitorexit    
        //    71: return         
        //    72: aload_0        
        //    73: monitorexit    
        //    74: return         
        //    75: new             Ljava/util/ArrayList;
        //    78: astore_3       
        //    79: aload_3        
        //    80: invokespecial   java/util/ArrayList.<init>:()V
        //    83: aload_0        
        //    84: aload_3        
        //    85: putfield        android/support/v4/app/FragmentManagerImpl.mPendingActions:Ljava/util/ArrayList;
        //    88: goto            56
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  6      13     30     35     Any
        //  17     30     30     35     Any
        //  31     33     30     35     Any
        //  42     56     30     35     Any
        //  56     71     30     35     Any
        //  72     74     30     35     Any
        //  75     88     30     35     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: cmpne:boolean(getfield:FragmentHostCallback(FragmentManagerImpl::mHost, this:FragmentManagerImpl), aconstnull:FragmentHostCallback())
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
        //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    void ensureInflatedFragmentView(final Fragment fragment) {
        if (fragment.mFromLayout && !fragment.mPerformedCreateView) {
            fragment.mView = fragment.performCreateView(fragment.performGetLayoutInflater(fragment.mSavedFragmentState), null, fragment.mSavedFragmentState);
            if (fragment.mView == null) {
                fragment.mInnerView = null;
            }
            else {
                fragment.mInnerView = fragment.mView;
                fragment.mView.setSaveFromParentEnabled(false);
                if (fragment.mHidden) {
                    fragment.mView.setVisibility(8);
                }
                fragment.onViewCreated(fragment.mView, fragment.mSavedFragmentState);
                this.dispatchOnFragmentViewCreated(fragment, fragment.mView, fragment.mSavedFragmentState, false);
            }
        }
    }
    
    public boolean execPendingActions() {
        boolean b = false;
        this.ensureExecReady(true);
        while (this.generateOpsForPendingActions(this.mTmpRecords, this.mTmpIsPop)) {
            this.mExecutingActions = true;
            try {
                this.removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
                this.cleanupExec();
                b = true;
            }
            finally {
                this.cleanupExec();
            }
        }
        this.doPendingDeferredStart();
        this.burpActive();
        return b;
    }
    
    public void execSingleAction(final OpGenerator opGenerator, final boolean b) {
        if (b && (this.mHost == null || this.mDestroyed)) {
            return;
        }
        this.ensureExecReady(b);
        if (opGenerator.generateOps(this.mTmpRecords, this.mTmpIsPop)) {
            this.mExecutingActions = true;
            try {
                this.removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
            }
            finally {
                this.cleanupExec();
            }
        }
        this.doPendingDeferredStart();
        this.burpActive();
    }
    
    @Override
    public boolean executePendingTransactions() {
        final boolean execPendingActions = this.execPendingActions();
        this.forcePostponedTransactions();
        return execPendingActions;
    }
    
    @Override
    public Fragment findFragmentById(final int n) {
        for (int i = this.mAdded.size() - 1; i >= 0; --i) {
            final Fragment fragment = this.mAdded.get(i);
            if (fragment != null && fragment.mFragmentId == n) {
                return fragment;
            }
        }
        if (this.mActive != null) {
            for (int j = this.mActive.size() - 1; j >= 0; --j) {
                final Fragment fragment2 = (Fragment)this.mActive.valueAt(j);
                if (fragment2 != null && fragment2.mFragmentId == n) {
                    return fragment2;
                }
            }
        }
        return null;
    }
    
    @Override
    public Fragment findFragmentByTag(final String s) {
        if (s != null) {
            for (int i = this.mAdded.size() - 1; i >= 0; --i) {
                final Fragment fragment = this.mAdded.get(i);
                if (fragment != null && s.equals(fragment.mTag)) {
                    return fragment;
                }
            }
        }
        if (this.mActive != null && s != null) {
            for (int j = this.mActive.size() - 1; j >= 0; --j) {
                final Fragment fragment2 = (Fragment)this.mActive.valueAt(j);
                if (fragment2 != null && s.equals(fragment2.mTag)) {
                    return fragment2;
                }
            }
        }
        return null;
    }
    
    public Fragment findFragmentByWho(final String s) {
        if (this.mActive != null && s != null) {
            for (int i = this.mActive.size() - 1; i >= 0; --i) {
                final Fragment fragment = (Fragment)this.mActive.valueAt(i);
                if (fragment != null) {
                    final Fragment fragmentByWho = fragment.findFragmentByWho(s);
                    if (fragmentByWho != null) {
                        return fragmentByWho;
                    }
                }
            }
        }
        return null;
    }
    
    public void freeBackStackIndex(final int i) {
        while (true) {
            while (true) {
                synchronized (this) {
                    this.mBackStackIndices.set(i, null);
                    if (this.mAvailBackStackIndices == null) {
                        this.mAvailBackStackIndices = new ArrayList<Integer>();
                    }
                    if (!FragmentManagerImpl.DEBUG) {
                        this.mAvailBackStackIndices.add(i);
                        return;
                    }
                }
                Log.v("FragmentManager", "Freeing back stack index " + i);
                continue;
            }
        }
    }
    
    int getActiveFragmentCount() {
        if (this.mActive != null) {
            return this.mActive.size();
        }
        return 0;
    }
    
    List<Fragment> getActiveFragments() {
        if (this.mActive != null) {
            final int size = this.mActive.size();
            final ArrayList list = new ArrayList<Object>(size);
            for (int i = 0; i < size; ++i) {
                list.add(this.mActive.valueAt(i));
            }
            return (List<Fragment>)list;
        }
        return null;
    }
    
    @Override
    public BackStackEntry getBackStackEntryAt(final int index) {
        return this.mBackStack.get(index);
    }
    
    @Override
    public int getBackStackEntryCount() {
        int size;
        if (this.mBackStack == null) {
            size = 0;
        }
        else {
            size = this.mBackStack.size();
        }
        return size;
    }
    
    @Override
    public Fragment getFragment(final Bundle bundle, final String str) {
        final int int1 = bundle.getInt(str, -1);
        if (int1 != -1) {
            final Fragment fragment = (Fragment)this.mActive.get(int1);
            if (fragment == null) {
                this.throwException(new IllegalStateException("Fragment no longer exists for key " + str + ": index " + int1));
            }
            return fragment;
        }
        return null;
    }
    
    @Override
    public List<Fragment> getFragments() {
        Label_0032: {
            if (this.mAdded.isEmpty()) {
                break Label_0032;
            }
            synchronized (this.mAdded) {
                return (List)this.mAdded.clone();
                return Collections.EMPTY_LIST;
            }
        }
    }
    
    LayoutInflater$Factory2 getLayoutInflaterFactory() {
        return (LayoutInflater$Factory2)this;
    }
    
    @Override
    public Fragment getPrimaryNavigationFragment() {
        return this.mPrimaryNav;
    }
    
    public void hideFragment(final Fragment obj) {
        boolean mHiddenChanged = false;
        if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "hide: " + obj);
        }
        if (!obj.mHidden) {
            obj.mHidden = true;
            if (!obj.mHiddenChanged) {
                mHiddenChanged = true;
            }
            obj.mHiddenChanged = mHiddenChanged;
        }
    }
    
    @Override
    public boolean isDestroyed() {
        return this.mDestroyed;
    }
    
    boolean isStateAtLeast(final int n) {
        return this.mCurState >= n;
    }
    
    @Override
    public boolean isStateSaved() {
        return this.mStateSaved;
    }
    
    AnimationOrAnimator loadAnimation(final Fragment fragment, int transitToStyleIndex, final boolean b, final int n) {
        int n2 = 0;
        final int nextAnim = fragment.getNextAnim();
        final Animation onCreateAnimation = fragment.onCreateAnimation(transitToStyleIndex, b, nextAnim);
        if (onCreateAnimation != null) {
            return new AnimationOrAnimator(onCreateAnimation);
        }
        final Animator onCreateAnimator = fragment.onCreateAnimator(transitToStyleIndex, b, nextAnim);
        if (onCreateAnimator != null) {
            return new AnimationOrAnimator(onCreateAnimator);
        }
        Label_0042: {
            if (nextAnim != 0) {
                Label_0191: {
                    if ("anim".equals(this.mHost.getContext().getResources().getResourceTypeName(nextAnim))) {
                        break Label_0191;
                    }
                    while (true) {
                        if (n2 != 0) {
                            break Label_0042;
                        }
                        try {
                            final Animator loadAnimator = AnimatorInflater.loadAnimator(this.mHost.getContext(), nextAnim);
                            if (loadAnimator != null) {
                                return new AnimationOrAnimator(loadAnimator);
                            }
                            break Label_0042;
                            try {
                                final Animation loadAnimation = AnimationUtils.loadAnimation(this.mHost.getContext(), nextAnim);
                                if (loadAnimation == null) {
                                    n2 = 1;
                                    continue;
                                }
                                return new AnimationOrAnimator(loadAnimation);
                            }
                            catch (final Resources$NotFoundException ex) {
                                throw ex;
                            }
                            catch (final RuntimeException ex2) {}
                        }
                        catch (final RuntimeException ex3) {}
                        break;
                    }
                }
            }
        }
        if (transitToStyleIndex == 0) {
            goto Label_0264;
        }
        transitToStyleIndex = transitToStyleIndex(transitToStyleIndex, b);
        if (transitToStyleIndex < 0) {
            goto Label_0266;
        }
        switch (transitToStyleIndex) {
            default: {
                if (n != 0 && n != 0) {
                    return null;
                }
                goto Label_0362;
            }
            case 1: {
                goto Label_0268;
                goto Label_0268;
            }
            case 2: {
                goto Label_0285;
                goto Label_0285;
            }
            case 3: {
                goto Label_0302;
                goto Label_0302;
            }
            case 4: {
                goto Label_0319;
                goto Label_0319;
            }
            case 5: {
                goto Label_0336;
                goto Label_0336;
            }
            case 6: {
                goto Label_0349;
                goto Label_0349;
            }
        }
    }
    
    void makeActive(final Fragment obj) {
        if (obj.mIndex < 0) {
            obj.setIndex(this.mNextFragmentIndex++, this.mParent);
            if (this.mActive == null) {
                this.mActive = (SparseArray<Fragment>)new SparseArray();
            }
            this.mActive.put(obj.mIndex, (Object)obj);
            if (FragmentManagerImpl.DEBUG) {
                Log.v("FragmentManager", "Allocated fragment index " + obj);
            }
        }
    }
    
    void makeInactive(final Fragment obj) {
        if (obj.mIndex >= 0) {
            if (FragmentManagerImpl.DEBUG) {
                Log.v("FragmentManager", "Freeing fragment index " + obj);
            }
            this.mActive.put(obj.mIndex, (Object)null);
            this.mHost.inactivateFragment(obj.mWho);
            obj.initState();
        }
    }
    
    void moveFragmentToExpectedState(final Fragment fragment) {
        if (fragment != null) {
            int n = this.mCurState;
            if (fragment.mRemoving) {
                if (!fragment.isInBackStack()) {
                    n = Math.min(n, 0);
                }
                else {
                    n = Math.min(n, 1);
                }
            }
            this.moveToState(fragment, n, fragment.getNextTransition(), fragment.getNextTransitionStyle(), false);
            if (fragment.mView != null) {
                final Fragment fragmentUnder = this.findFragmentUnder(fragment);
                if (fragmentUnder != null) {
                    final View mView = fragmentUnder.mView;
                    final ViewGroup mContainer = fragment.mContainer;
                    final int indexOfChild = mContainer.indexOfChild(mView);
                    final int indexOfChild2 = mContainer.indexOfChild(fragment.mView);
                    if (indexOfChild2 < indexOfChild) {
                        mContainer.removeViewAt(indexOfChild2);
                        mContainer.addView(fragment.mView, indexOfChild);
                    }
                }
                if (fragment.mIsNewlyAdded && fragment.mContainer != null) {
                    if (fragment.mPostponedAlpha > 0.0f) {
                        fragment.mView.setAlpha(fragment.mPostponedAlpha);
                    }
                    fragment.mPostponedAlpha = 0.0f;
                    fragment.mIsNewlyAdded = false;
                    final AnimationOrAnimator loadAnimation = this.loadAnimation(fragment, fragment.getNextTransition(), true, fragment.getNextTransitionStyle());
                    if (loadAnimation != null) {
                        setHWLayerAnimListenerIfAlpha(fragment.mView, loadAnimation);
                        if (loadAnimation.animation == null) {
                            loadAnimation.animator.setTarget((Object)fragment.mView);
                            loadAnimation.animator.start();
                        }
                        else {
                            fragment.mView.startAnimation(loadAnimation.animation);
                        }
                    }
                }
            }
            if (fragment.mHiddenChanged) {
                this.completeShowHideFragment(fragment);
            }
        }
    }
    
    void moveToState(int mCurState, final boolean b) {
        if (this.mHost == null && mCurState != 0) {
            throw new IllegalStateException("No activity");
        }
        if (!b && mCurState == this.mCurState) {
            return;
        }
        this.mCurState = mCurState;
        if (this.mActive != null) {
            final int size = this.mAdded.size();
            int i = 0;
            mCurState = 0;
            while (i < size) {
                final Fragment fragment = this.mAdded.get(i);
                this.moveFragmentToExpectedState(fragment);
                if (fragment.mLoaderManager != null) {
                    mCurState |= (fragment.mLoaderManager.hasRunningLoaders() ? 1 : 0);
                }
                ++i;
            }
            for (int size2 = this.mActive.size(), j = 0; j < size2; ++j) {
                final Fragment fragment2 = (Fragment)this.mActive.valueAt(j);
                if (fragment2 != null) {
                    if (fragment2.mRemoving || fragment2.mDetached) {
                        if (!fragment2.mIsNewlyAdded) {
                            this.moveFragmentToExpectedState(fragment2);
                            if (fragment2.mLoaderManager != null) {
                                mCurState |= (fragment2.mLoaderManager.hasRunningLoaders() ? 1 : 0);
                            }
                        }
                    }
                }
            }
            if (mCurState == 0) {
                this.startPendingDeferredFragments();
            }
            if (this.mNeedMenuInvalidate && this.mHost != null && this.mCurState == 5) {
                this.mHost.onSupportInvalidateOptionsMenu();
                this.mNeedMenuInvalidate = false;
            }
        }
    }
    
    void moveToState(final Fragment fragment) {
        this.moveToState(fragment, this.mCurState, 0, 0, false);
    }
    
    void moveToState(final Fragment fragment, int stateAfterAnimating, int n, int n2, final boolean b) {
        final boolean b2 = true;
        if (!fragment.mAdded || fragment.mDetached) {
            if (stateAfterAnimating > 1) {
                stateAfterAnimating = 1;
            }
        }
        int mState;
        if (!fragment.mRemoving) {
            mState = stateAfterAnimating;
        }
        else if ((mState = stateAfterAnimating) > fragment.mState) {
            if (fragment.mState == 0 && fragment.isInBackStack()) {
                mState = 1;
            }
            else {
                mState = fragment.mState;
            }
        }
        if (!fragment.mDeferStart) {
            stateAfterAnimating = mState;
        }
        else {
            stateAfterAnimating = mState;
            if (fragment.mState < 4 && (stateAfterAnimating = mState) > 3) {
                stateAfterAnimating = 3;
            }
        }
        if (fragment.mState > stateAfterAnimating) {
            if (fragment.mState <= stateAfterAnimating) {
                n2 = stateAfterAnimating;
            }
            else {
                switch (fragment.mState) {
                    default: {
                        n2 = stateAfterAnimating;
                        break;
                    }
                    case 3:
                        Label_1422: {
                            if (stateAfterAnimating >= 3) {
                                break Label_1422;
                            }
                            if (FragmentManagerImpl.DEBUG) {
                                Log.v("FragmentManager", "movefrom STOPPED: " + fragment);
                            }
                            fragment.performReallyStop();
                            break Label_1422;
                        }
                    case 2: {
                        if (stateAfterAnimating < 2) {
                            if (FragmentManagerImpl.DEBUG) {
                                Log.v("FragmentManager", "movefrom ACTIVITY_CREATED: " + fragment);
                            }
                            if (fragment.mView != null && this.mHost.onShouldSaveFragmentState(fragment) && fragment.mSavedViewState == null) {
                                this.saveFragmentViewState(fragment);
                            }
                            fragment.performDestroyView();
                            this.dispatchOnFragmentViewDestroyed(fragment, false);
                            if (fragment.mView != null && fragment.mContainer != null) {
                                fragment.mView.clearAnimation();
                                fragment.mContainer.endViewTransition(fragment.mView);
                                AnimationOrAnimator loadAnimation;
                                if (this.mCurState > 0 && !this.mDestroyed) {
                                    if (fragment.mView.getVisibility() == 0 && fragment.mPostponedAlpha >= 0.0f) {
                                        loadAnimation = this.loadAnimation(fragment, n, false, n2);
                                    }
                                    else {
                                        loadAnimation = null;
                                    }
                                }
                                else {
                                    loadAnimation = null;
                                }
                                fragment.mPostponedAlpha = 0.0f;
                                if (loadAnimation != null) {
                                    this.animateRemoveFragment(fragment, loadAnimation, stateAfterAnimating);
                                }
                                fragment.mContainer.removeView(fragment.mView);
                            }
                            fragment.mContainer = null;
                            fragment.mView = null;
                            fragment.mInnerView = null;
                            fragment.mInLayout = false;
                        }
                    }
                    case 1: {
                        if ((n2 = stateAfterAnimating) >= 1) {
                            break;
                        }
                        if (this.mDestroyed) {
                            if (fragment.getAnimatingAway() == null) {
                                if (fragment.getAnimator() != null) {
                                    final Animator animator = fragment.getAnimator();
                                    fragment.setAnimator(null);
                                    animator.cancel();
                                }
                            }
                            else {
                                final View animatingAway = fragment.getAnimatingAway();
                                fragment.setAnimatingAway(null);
                                animatingAway.clearAnimation();
                            }
                        }
                        if (fragment.getAnimatingAway() != null || fragment.getAnimator() != null) {
                            fragment.setStateAfterAnimating(stateAfterAnimating);
                            n2 = 1;
                            break;
                        }
                        if (FragmentManagerImpl.DEBUG) {
                            Log.v("FragmentManager", "movefrom CREATED: " + fragment);
                        }
                        if (fragment.mRetaining) {
                            fragment.mState = 0;
                        }
                        else {
                            fragment.performDestroy();
                            this.dispatchOnFragmentDestroyed(fragment, false);
                        }
                        fragment.performDetach();
                        this.dispatchOnFragmentDetached(fragment, false);
                        n2 = stateAfterAnimating;
                        if (b) {
                            break;
                        }
                        if (fragment.mRetaining) {
                            fragment.mHost = null;
                            fragment.mParentFragment = null;
                            fragment.mFragmentManager = null;
                            n2 = stateAfterAnimating;
                            break;
                        }
                        this.makeInactive(fragment);
                        n2 = stateAfterAnimating;
                        break;
                    }
                    case 5:
                        Label_1412: {
                            if (stateAfterAnimating >= 5) {
                                break Label_1412;
                            }
                            if (FragmentManagerImpl.DEBUG) {
                                Log.v("FragmentManager", "movefrom RESUMED: " + fragment);
                            }
                            fragment.performPause();
                            this.dispatchOnFragmentPaused(fragment, false);
                            break Label_1412;
                        }
                    case 4:
                        Label_1417: {
                            if (stateAfterAnimating >= 4) {
                                break Label_1417;
                            }
                            if (FragmentManagerImpl.DEBUG) {
                                Log.v("FragmentManager", "movefrom STARTED: " + fragment);
                            }
                            fragment.performStop();
                            this.dispatchOnFragmentStopped(fragment, false);
                            break Label_1417;
                        }
                }
            }
        }
        else {
            if (fragment.mFromLayout && !fragment.mInLayout) {
                return;
            }
            if (fragment.getAnimatingAway() != null || fragment.getAnimator() != null) {
                fragment.setAnimatingAway(null);
                fragment.setAnimator(null);
                this.moveToState(fragment, fragment.getStateAfterAnimating(), 0, 0, true);
            }
            int n3 = stateAfterAnimating;
            n2 = stateAfterAnimating;
            int n4 = stateAfterAnimating;
            n = stateAfterAnimating;
            FragmentManagerImpl mFragmentManager;
            ViewGroup viewGroup;
            ViewGroup mContainer = null;
            String resourceName;
            boolean mIsNewlyAdded = false;
            switch (fragment.mState) {
                default: {
                    n2 = stateAfterAnimating;
                    break;
                }
                case 0:
                    Label_0241: {
                        if (stateAfterAnimating <= 0) {
                            n3 = stateAfterAnimating;
                            break Label_0241;
                        }
                        if (FragmentManagerImpl.DEBUG) {
                            Log.v("FragmentManager", "moveto CREATED: " + fragment);
                        }
                        if (fragment.mSavedFragmentState == null) {
                            n3 = stateAfterAnimating;
                        }
                        else {
                            fragment.mSavedFragmentState.setClassLoader(this.mHost.getContext().getClassLoader());
                            fragment.mSavedViewState = (SparseArray<Parcelable>)fragment.mSavedFragmentState.getSparseParcelableArray("android:view_state");
                            fragment.mTarget = this.getFragment(fragment.mSavedFragmentState, "android:target_state");
                            if (fragment.mTarget != null) {
                                fragment.mTargetRequestCode = fragment.mSavedFragmentState.getInt("android:target_req_state", 0);
                            }
                            fragment.mUserVisibleHint = fragment.mSavedFragmentState.getBoolean("android:user_visible_hint", true);
                            n3 = stateAfterAnimating;
                            if (!fragment.mUserVisibleHint) {
                                fragment.mDeferStart = true;
                                if ((n3 = stateAfterAnimating) > 3) {
                                    n3 = 3;
                                }
                            }
                        }
                        fragment.mHost = this.mHost;
                        fragment.mParentFragment = this.mParent;
                        if (this.mParent == null) {
                            mFragmentManager = this.mHost.getFragmentManagerImpl();
                        }
                        else {
                            mFragmentManager = this.mParent.mChildFragmentManager;
                        }
                        fragment.mFragmentManager = mFragmentManager;
                        if (fragment.mTarget != null) {
                            if (this.mActive.get(fragment.mTarget.mIndex) != fragment.mTarget) {
                                throw new IllegalStateException("Fragment " + fragment + " declared target fragment " + fragment.mTarget + " that does not belong to this FragmentManager!");
                            }
                            if (fragment.mTarget.mState < 1) {
                                this.moveToState(fragment.mTarget, 1, 0, 0, true);
                            }
                        }
                        this.dispatchOnFragmentPreAttached(fragment, this.mHost.getContext(), false);
                        fragment.mCalled = false;
                        fragment.onAttach(this.mHost.getContext());
                        if (fragment.mCalled) {
                            if (fragment.mParentFragment != null) {
                                fragment.mParentFragment.onAttachFragment(fragment);
                            }
                            else {
                                this.mHost.onAttachFragment(fragment);
                            }
                            this.dispatchOnFragmentAttached(fragment, this.mHost.getContext(), false);
                            if (fragment.mIsCreated) {
                                fragment.restoreChildFragmentState(fragment.mSavedFragmentState);
                                fragment.mState = 1;
                            }
                            else {
                                this.dispatchOnFragmentPreCreated(fragment, fragment.mSavedFragmentState, false);
                                fragment.performCreate(fragment.mSavedFragmentState);
                                this.dispatchOnFragmentCreated(fragment, fragment.mSavedFragmentState, false);
                            }
                            fragment.mRetaining = false;
                            break Label_0241;
                        }
                        throw new SuperNotCalledException("Fragment " + fragment + " did not call through to super.onAttach()");
                    }
                case 1:
                    Label_0256: {
                        this.ensureInflatedFragmentView(fragment);
                        if (n3 <= 1) {
                            n2 = n3;
                            break Label_0256;
                        }
                        if (FragmentManagerImpl.DEBUG) {
                            Log.v("FragmentManager", "moveto ACTIVITY_CREATED: " + fragment);
                        }
                        Label_0821: {
                            if (!fragment.mFromLayout) {
                                Label_1112: {
                                    Label_0897: {
                                        if (fragment.mContainerId != 0) {
                                            Label_1062: {
                                                if (fragment.mContainerId == -1) {
                                                    break Label_1062;
                                                }
                                            Label_0997_Outer:
                                                while (true) {
                                                    viewGroup = (ViewGroup)this.mContainer.onFindViewById(fragment.mContainerId);
                                                    if ((mContainer = viewGroup) != null) {
                                                        break Label_0897;
                                                    }
                                                    mContainer = viewGroup;
                                                    if (fragment.mRestored) {
                                                        break Label_0897;
                                                    }
                                                    while (true) {
                                                        try {
                                                            resourceName = fragment.getResources().getResourceName(fragment.mContainerId);
                                                            this.throwException(new IllegalArgumentException("No view found for id 0x" + Integer.toHexString(fragment.mContainerId) + " (" + resourceName + ") for fragment " + fragment));
                                                            mContainer = viewGroup;
                                                            break Label_0897;
                                                            this.throwException(new IllegalArgumentException("Cannot create fragment " + fragment + " for a container view with no id"));
                                                            continue Label_0997_Outer;
                                                        }
                                                        catch (final Resources$NotFoundException ex) {
                                                            resourceName = "unknown";
                                                            continue;
                                                        }
                                                        break;
                                                    }
                                                    break;
                                                }
                                            }
                                            break Label_1112;
                                        }
                                        mContainer = null;
                                    }
                                    fragment.mContainer = mContainer;
                                    fragment.mView = fragment.performCreateView(fragment.performGetLayoutInflater(fragment.mSavedFragmentState), mContainer, fragment.mSavedFragmentState);
                                    if (fragment.mView == null) {
                                        fragment.mInnerView = null;
                                        break Label_0821;
                                    }
                                }
                                fragment.mInnerView = fragment.mView;
                                fragment.mView.setSaveFromParentEnabled(false);
                                if (mContainer != null) {
                                    mContainer.addView(fragment.mView);
                                }
                                if (fragment.mHidden) {
                                    fragment.mView.setVisibility(8);
                                }
                                fragment.onViewCreated(fragment.mView, fragment.mSavedFragmentState);
                                this.dispatchOnFragmentViewCreated(fragment, fragment.mView, fragment.mSavedFragmentState, false);
                                Label_1179: {
                                    if (fragment.mView.getVisibility() == 0) {
                                        mIsNewlyAdded = b2;
                                        if (fragment.mContainer != null) {
                                            break Label_1179;
                                        }
                                    }
                                    mIsNewlyAdded = false;
                                }
                                fragment.mIsNewlyAdded = mIsNewlyAdded;
                            }
                        }
                        fragment.performActivityCreated(fragment.mSavedFragmentState);
                        this.dispatchOnFragmentActivityCreated(fragment, fragment.mSavedFragmentState, false);
                        if (fragment.mView != null) {
                            fragment.restoreViewState(fragment.mSavedFragmentState);
                        }
                        fragment.mSavedFragmentState = null;
                        n2 = n3;
                        break Label_0256;
                    }
                case 2:
                    Label_0266: {
                        if (n2 <= 2) {
                            n4 = n2;
                            break Label_0266;
                        }
                        fragment.mState = 3;
                        n4 = n2;
                        break Label_0266;
                    }
                case 3:
                    Label_0275: {
                        if (n4 <= 3) {
                            n = n4;
                            break Label_0275;
                        }
                        if (FragmentManagerImpl.DEBUG) {
                            Log.v("FragmentManager", "moveto STARTED: " + fragment);
                        }
                        fragment.performStart();
                        this.dispatchOnFragmentStarted(fragment, false);
                        n = n4;
                        break Label_0275;
                    }
                case 4: {
                    n2 = n;
                    if (n > 4) {
                        if (FragmentManagerImpl.DEBUG) {
                            Log.v("FragmentManager", "moveto RESUMED: " + fragment);
                        }
                        fragment.performResume();
                        this.dispatchOnFragmentResumed(fragment, false);
                        fragment.mSavedFragmentState = null;
                        fragment.mSavedViewState = null;
                        n2 = n;
                        break;
                    }
                    break;
                }
            }
        }
        if (fragment.mState != n2) {
            Log.w("FragmentManager", "moveToState: Fragment state for " + fragment + " not updated inline; " + "expected state " + n2 + " found " + fragment.mState);
            fragment.mState = n2;
        }
    }
    
    public void noteStateNotSaved() {
        this.mSavedNonConfig = null;
        this.mStateSaved = false;
        for (int size = this.mAdded.size(), i = 0; i < size; ++i) {
            final Fragment fragment = this.mAdded.get(i);
            if (fragment != null) {
                fragment.noteStateNotSaved();
            }
        }
    }
    
    public View onCreateView(final View view, String str, final Context context, final AttributeSet set) {
        if (!"fragment".equals(str)) {
            return null;
        }
        str = set.getAttributeValue((String)null, "class");
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, FragmentTag.Fragment);
        if (str == null) {
            str = obtainStyledAttributes.getString(0);
        }
        final int resourceId = obtainStyledAttributes.getResourceId(1, -1);
        final String string = obtainStyledAttributes.getString(2);
        obtainStyledAttributes.recycle();
        if (!Fragment.isSupportFragmentClass(this.mHost.getContext(), str)) {
            return null;
        }
        int id;
        if (view == null) {
            id = 0;
        }
        else {
            id = view.getId();
        }
        if (id == -1 && resourceId == -1 && string == null) {
            throw new IllegalArgumentException(set.getPositionDescription() + ": Must specify unique android:id, android:tag, or have a parent with an id for " + str);
        }
        Fragment obj;
        if (resourceId == -1) {
            obj = null;
        }
        else {
            obj = this.findFragmentById(resourceId);
        }
        if (obj == null && string != null) {
            obj = this.findFragmentByTag(string);
        }
        if (obj == null && id != -1) {
            obj = this.findFragmentById(id);
        }
        if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "onCreateView: id=0x" + Integer.toHexString(resourceId) + " fname=" + str + " existing=" + obj);
        }
        if (obj != null) {
            if (obj.mInLayout) {
                throw new IllegalArgumentException(set.getPositionDescription() + ": Duplicate id 0x" + Integer.toHexString(resourceId) + ", tag " + string + ", or parent id 0x" + Integer.toHexString(id) + " with another fragment for " + str);
            }
            obj.mInLayout = true;
            obj.mHost = this.mHost;
            if (!obj.mRetaining) {
                obj.onInflate(this.mHost.getContext(), set, obj.mSavedFragmentState);
            }
        }
        else {
            obj = this.mContainer.instantiate(context, str, null);
            obj.mFromLayout = true;
            int mFragmentId;
            if (resourceId == 0) {
                mFragmentId = id;
            }
            else {
                mFragmentId = resourceId;
            }
            obj.mFragmentId = mFragmentId;
            obj.mContainerId = id;
            obj.mTag = string;
            obj.mInLayout = true;
            obj.mFragmentManager = this;
            obj.mHost = this.mHost;
            obj.onInflate(this.mHost.getContext(), set, obj.mSavedFragmentState);
            this.addFragment(obj, true);
        }
        if (this.mCurState < 1 && obj.mFromLayout) {
            this.moveToState(obj, 1, 0, 0, false);
        }
        else {
            this.moveToState(obj);
        }
        if (obj.mView != null) {
            if (resourceId != 0) {
                obj.mView.setId(resourceId);
            }
            if (obj.mView.getTag() == null) {
                obj.mView.setTag((Object)string);
            }
            return obj.mView;
        }
        throw new IllegalStateException("Fragment " + str + " did not create a view.");
    }
    
    public View onCreateView(final String s, final Context context, final AttributeSet set) {
        return this.onCreateView(null, s, context, set);
    }
    
    public void performPendingDeferredStart(final Fragment fragment) {
        if (fragment.mDeferStart) {
            if (this.mExecutingActions) {
                this.mHavePendingDeferredStart = true;
                return;
            }
            fragment.mDeferStart = false;
            this.moveToState(fragment, this.mCurState, 0, 0, false);
        }
    }
    
    @Override
    public void popBackStack() {
        this.enqueueAction((OpGenerator)new PopBackStackState(null, -1, 0), false);
    }
    
    @Override
    public void popBackStack(final int i, final int n) {
        if (i >= 0) {
            this.enqueueAction((OpGenerator)new PopBackStackState(null, i, n), false);
            return;
        }
        throw new IllegalArgumentException("Bad id: " + i);
    }
    
    @Override
    public void popBackStack(final String s, final int n) {
        this.enqueueAction((OpGenerator)new PopBackStackState(s, -1, n), false);
    }
    
    @Override
    public boolean popBackStackImmediate() {
        this.checkStateLoss();
        return this.popBackStackImmediate(null, -1, 0);
    }
    
    @Override
    public boolean popBackStackImmediate(final int i, final int n) {
        this.checkStateLoss();
        this.execPendingActions();
        if (i >= 0) {
            return this.popBackStackImmediate(null, i, n);
        }
        throw new IllegalArgumentException("Bad id: " + i);
    }
    
    @Override
    public boolean popBackStackImmediate(final String s, final int n) {
        this.checkStateLoss();
        return this.popBackStackImmediate(s, -1, n);
    }
    
    boolean popBackStackState(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2, final String s, int index, int i) {
        if (this.mBackStack != null) {
            if (s == null && index < 0 && (i & 0x1) == 0x0) {
                index = this.mBackStack.size() - 1;
                if (index < 0) {
                    return false;
                }
                list.add(this.mBackStack.remove(index));
                list2.add(true);
            }
            else {
                final int n = -1;
                Label_0050: {
                    if (s == null && index < 0) {
                        index = n;
                    }
                    else {
                        int j;
                        for (j = this.mBackStack.size() - 1; j >= 0; --j) {
                            final BackStackRecord backStackRecord = this.mBackStack.get(j);
                            if ((s != null && s.equals(backStackRecord.getName())) || (index >= 0 && index == backStackRecord.mIndex)) {
                                break;
                            }
                        }
                        if (j < 0) {
                            return false;
                        }
                        if ((i & 0x1) == 0x0) {
                            index = j;
                        }
                        else {
                            BackStackRecord backStackRecord2;
                            for (i = j - 1; i >= 0; --i) {
                                backStackRecord2 = this.mBackStack.get(i);
                                if ((s == null || !s.equals(backStackRecord2.getName())) && (index < 0 || index != backStackRecord2.mIndex)) {
                                    index = i;
                                    break Label_0050;
                                }
                            }
                            index = i;
                        }
                    }
                }
                if (index == this.mBackStack.size() - 1) {
                    return false;
                }
                i = this.mBackStack.size();
                while (--i > index) {
                    list.add(this.mBackStack.remove(i));
                    list2.add(true);
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void putFragment(final Bundle bundle, final String s, final Fragment obj) {
        if (obj.mIndex < 0) {
            this.throwException(new IllegalStateException("Fragment " + obj + " is not currently in the FragmentManager"));
        }
        bundle.putInt(s, obj.mIndex);
    }
    
    @Override
    public void registerFragmentLifecycleCallbacks(final FragmentLifecycleCallbacks fragmentLifecycleCallbacks, final boolean b) {
        this.mLifecycleCallbacks.add(new Pair<FragmentLifecycleCallbacks, Boolean>(fragmentLifecycleCallbacks, b));
    }
    
    public void removeFragment(final Fragment p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: ifne            58
        //     6: aload_1        
        //     7: invokevirtual   android/support/v4/app/Fragment.isInBackStack:()Z
        //    10: ifeq            100
        //    13: iconst_0       
        //    14: istore_2       
        //    15: aload_1        
        //    16: getfield        android/support/v4/app/Fragment.mDetached:Z
        //    19: ifne            105
        //    22: aload_0        
        //    23: getfield        android/support/v4/app/FragmentManagerImpl.mAdded:Ljava/util/ArrayList;
        //    26: astore_3       
        //    27: aload_3        
        //    28: monitorenter   
        //    29: aload_0        
        //    30: getfield        android/support/v4/app/FragmentManagerImpl.mAdded:Ljava/util/ArrayList;
        //    33: aload_1        
        //    34: invokevirtual   java/util/ArrayList.remove:(Ljava/lang/Object;)Z
        //    37: pop            
        //    38: aload_3        
        //    39: monitorexit    
        //    40: aload_1        
        //    41: getfield        android/support/v4/app/Fragment.mHasMenu:Z
        //    44: ifne            117
        //    47: aload_1        
        //    48: iconst_0       
        //    49: putfield        android/support/v4/app/Fragment.mAdded:Z
        //    52: aload_1        
        //    53: iconst_1       
        //    54: putfield        android/support/v4/app/Fragment.mRemoving:Z
        //    57: return         
        //    58: ldc             "FragmentManager"
        //    60: new             Ljava/lang/StringBuilder;
        //    63: dup            
        //    64: invokespecial   java/lang/StringBuilder.<init>:()V
        //    67: ldc_w           "remove: "
        //    70: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    73: aload_1        
        //    74: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //    77: ldc_w           " nesting="
        //    80: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    83: aload_1        
        //    84: getfield        android/support/v4/app/Fragment.mBackStackNesting:I
        //    87: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //    90: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    93: invokestatic    android/util/Log.v:(Ljava/lang/String;Ljava/lang/String;)I
        //    96: pop            
        //    97: goto            6
        //   100: iconst_1       
        //   101: istore_2       
        //   102: goto            15
        //   105: iload_2        
        //   106: ifne            22
        //   109: goto            57
        //   112: astore_1       
        //   113: aload_3        
        //   114: monitorexit    
        //   115: aload_1        
        //   116: athrow         
        //   117: aload_1        
        //   118: getfield        android/support/v4/app/Fragment.mMenuVisible:Z
        //   121: ifeq            47
        //   124: aload_0        
        //   125: iconst_1       
        //   126: putfield        android/support/v4/app/FragmentManagerImpl.mNeedMenuInvalidate:Z
        //   129: goto            47
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  29     40     112    117    Any
        //  113    115    112    117    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: var_2_0E:boolean
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    @Override
    public void removeOnBackStackChangedListener(final OnBackStackChangedListener o) {
        if (this.mBackStackChangeListeners != null) {
            this.mBackStackChangeListeners.remove(o);
        }
    }
    
    void reportBackStackChanged() {
        if (this.mBackStackChangeListeners != null) {
            for (int i = 0; i < this.mBackStackChangeListeners.size(); ++i) {
                this.mBackStackChangeListeners.get(i).onBackStackChanged();
            }
        }
    }
    
    void restoreAllState(Parcelable childNonConfigs, final FragmentManagerNonConfig fragmentManagerNonConfig) {
        if (childNonConfigs == null) {
            return;
        }
        final FragmentManagerState fragmentManagerState = (FragmentManagerState)childNonConfigs;
        if (fragmentManagerState.mActive != null) {
            if (fragmentManagerNonConfig == null) {
                childNonConfigs = null;
            }
            else {
                final List<Fragment> fragments = fragmentManagerNonConfig.getFragments();
                childNonConfigs = (Parcelable)fragmentManagerNonConfig.getChildNonConfigs();
                int size;
                if (fragments == null) {
                    size = 0;
                }
                else {
                    size = fragments.size();
                }
                for (int i = 0; i < size; ++i) {
                    final Fragment fragment = fragments.get(i);
                    if (FragmentManagerImpl.DEBUG) {
                        Log.v("FragmentManager", "restoreAllState: re-attaching retained " + fragment);
                    }
                    int n;
                    for (n = 0; n < fragmentManagerState.mActive.length && fragmentManagerState.mActive[n].mIndex != fragment.mIndex; ++n) {}
                    if (n == fragmentManagerState.mActive.length) {
                        this.throwException(new IllegalStateException("Could not find active fragment with index " + fragment.mIndex));
                    }
                    final FragmentState fragmentState = fragmentManagerState.mActive[n];
                    fragmentState.mInstance = fragment;
                    fragment.mSavedViewState = null;
                    fragment.mBackStackNesting = 0;
                    fragment.mInLayout = false;
                    fragment.mAdded = false;
                    fragment.mTarget = null;
                    if (fragmentState.mSavedFragmentState != null) {
                        fragmentState.mSavedFragmentState.setClassLoader(this.mHost.getContext().getClassLoader());
                        fragment.mSavedViewState = (SparseArray<Parcelable>)fragmentState.mSavedFragmentState.getSparseParcelableArray("android:view_state");
                        fragment.mSavedFragmentState = fragmentState.mSavedFragmentState;
                    }
                }
            }
            this.mActive = (SparseArray<Fragment>)new SparseArray(fragmentManagerState.mActive.length);
            for (int j = 0; j < fragmentManagerState.mActive.length; ++j) {
                final FragmentState fragmentState2 = fragmentManagerState.mActive[j];
                if (fragmentState2 != null) {
                    FragmentManagerNonConfig fragmentManagerNonConfig2;
                    if (childNonConfigs != null && j < ((List)childNonConfigs).size()) {
                        fragmentManagerNonConfig2 = ((List<FragmentManagerNonConfig>)childNonConfigs).get(j);
                    }
                    else {
                        fragmentManagerNonConfig2 = null;
                    }
                    final Fragment instantiate = fragmentState2.instantiate(this.mHost, this.mContainer, this.mParent, fragmentManagerNonConfig2);
                    if (FragmentManagerImpl.DEBUG) {
                        Log.v("FragmentManager", "restoreAllState: active #" + j + ": " + instantiate);
                    }
                    this.mActive.put(instantiate.mIndex, (Object)instantiate);
                    fragmentState2.mInstance = null;
                }
            }
            if (fragmentManagerNonConfig != null) {
                final List<Fragment> fragments2 = fragmentManagerNonConfig.getFragments();
                int size2;
                if (fragments2 == null) {
                    size2 = 0;
                }
                else {
                    size2 = fragments2.size();
                }
                for (int k = 0; k < size2; ++k) {
                    childNonConfigs = (Parcelable)fragments2.get(k);
                    if (((Fragment)childNonConfigs).mTargetIndex >= 0) {
                        ((Fragment)childNonConfigs).mTarget = (Fragment)this.mActive.get(((Fragment)childNonConfigs).mTargetIndex);
                        if (((Fragment)childNonConfigs).mTarget == null) {
                            Log.w("FragmentManager", "Re-attaching retained fragment " + childNonConfigs + " target no longer exists: " + ((Fragment)childNonConfigs).mTargetIndex);
                        }
                    }
                }
            }
            this.mAdded.clear();
            while (true) {
                Label_0831: {
                    if (fragmentManagerState.mAdded != null) {
                        int l = 0;
                    Label_0689_Outer:
                        while (l < fragmentManagerState.mAdded.length) {
                            final Object obj = this.mActive.get(fragmentManagerState.mAdded[l]);
                            Label_0735: {
                                if (obj == null) {
                                    break Label_0735;
                                }
                            Label_0700_Outer:
                                while (true) {
                                    ((Fragment)obj).mAdded = true;
                                    Label_0775: {
                                        if (FragmentManagerImpl.DEBUG) {
                                            break Label_0775;
                                        }
                                        while (true) {
                                            Label_0815: {
                                                if (this.mAdded.contains(obj)) {
                                                    break Label_0815;
                                                }
                                                synchronized (this.mAdded) {
                                                    this.mAdded.add((Fragment)obj);
                                                    monitorexit(this.mAdded);
                                                    ++l;
                                                    continue Label_0689_Outer;
                                                    Log.v("FragmentManager", "restoreAllState: added #" + l + ": " + obj);
                                                    continue;
                                                    throw new IllegalStateException("Already added!");
                                                    this.throwException(new IllegalStateException("No instantiated fragment for index #" + fragmentManagerState.mAdded[l]));
                                                    continue Label_0700_Outer;
                                                }
                                            }
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            break Label_0831;
                        }
                    }
                    if (fragmentManagerState.mBackStack != null) {
                        break Label_0831;
                    }
                    this.mBackStack = null;
                    if (fragmentManagerState.mPrimaryNavActiveIndex >= 0) {
                        this.mPrimaryNav = (Fragment)this.mActive.get(fragmentManagerState.mPrimaryNavActiveIndex);
                    }
                    this.mNextFragmentIndex = fragmentManagerState.mNextFragmentIndex;
                    return;
                }
                this.mBackStack = new ArrayList<BackStackRecord>(fragmentManagerState.mBackStack.length);
                for (int m = 0; m < fragmentManagerState.mBackStack.length; ++m) {
                    final BackStackRecord instantiate2 = fragmentManagerState.mBackStack[m].instantiate(this);
                    if (FragmentManagerImpl.DEBUG) {
                        Log.v("FragmentManager", "restoreAllState: back stack #" + m + " (index " + instantiate2.mIndex + "): " + instantiate2);
                        final PrintWriter printWriter = new PrintWriter(new LogWriter("FragmentManager"));
                        instantiate2.dump("  ", printWriter, false);
                        printWriter.close();
                    }
                    this.mBackStack.add(instantiate2);
                    if (instantiate2.mIndex >= 0) {
                        this.setBackStackIndex(instantiate2.mIndex, instantiate2);
                    }
                }
                continue;
            }
        }
    }
    
    FragmentManagerNonConfig retainNonConfig() {
        setRetaining(this.mSavedNonConfig);
        return this.mSavedNonConfig;
    }
    
    Parcelable saveAllState() {
        BackStackState[] mBackStack = null;
        this.forcePostponedTransactions();
        this.endAnimatingAwayFragments();
        this.execPendingActions();
        this.mStateSaved = true;
        this.mSavedNonConfig = null;
        if (this.mActive == null || this.mActive.size() <= 0) {
            return null;
        }
        final int size = this.mActive.size();
        final FragmentState[] mActive = new FragmentState[size];
        int i = 0;
        int n = 0;
        while (i < size) {
            final Fragment obj = (Fragment)this.mActive.valueAt(i);
            if (obj != null) {
                if (obj.mIndex < 0) {
                    this.throwException(new IllegalStateException("Failure saving state: active " + obj + " has cleared index: " + obj.mIndex));
                }
                final FragmentState fragmentState = new FragmentState(obj);
                mActive[i] = fragmentState;
                if (obj.mState > 0 && fragmentState.mSavedFragmentState == null) {
                    fragmentState.mSavedFragmentState = this.saveFragmentBasicState(obj);
                    if (obj.mTarget != null) {
                        if (obj.mTarget.mIndex < 0) {
                            this.throwException(new IllegalStateException("Failure saving state: " + obj + " has target not in fragment manager: " + obj.mTarget));
                        }
                        if (fragmentState.mSavedFragmentState == null) {
                            fragmentState.mSavedFragmentState = new Bundle();
                        }
                        this.putFragment(fragmentState.mSavedFragmentState, "android:target_state", obj.mTarget);
                        if (obj.mTargetRequestCode != 0) {
                            fragmentState.mSavedFragmentState.putInt("android:target_req_state", obj.mTargetRequestCode);
                        }
                    }
                }
                else {
                    fragmentState.mSavedFragmentState = obj.mSavedFragmentState;
                }
                if (FragmentManagerImpl.DEBUG) {
                    Log.v("FragmentManager", "Saved state of " + obj + ": " + fragmentState.mSavedFragmentState);
                }
                n = 1;
            }
            ++i;
        }
        if (n != 0) {
            final int size2 = this.mAdded.size();
            int[] mAdded;
            if (size2 <= 0) {
                mAdded = null;
            }
            else {
                final int[] array = new int[size2];
                int n2 = 0;
                while (true) {
                    mAdded = array;
                    if (n2 >= size2) {
                        break;
                    }
                    array[n2] = this.mAdded.get(n2).mIndex;
                    if (array[n2] < 0) {
                        this.throwException(new IllegalStateException("Failure saving state: active " + this.mAdded.get(n2) + " has cleared index: " + array[n2]));
                    }
                    if (FragmentManagerImpl.DEBUG) {
                        Log.v("FragmentManager", "saveAllState: adding fragment #" + n2 + ": " + this.mAdded.get(n2));
                    }
                    ++n2;
                }
            }
            if (this.mBackStack != null) {
                final int size3 = this.mBackStack.size();
                if (size3 > 0) {
                    final BackStackState[] array2 = new BackStackState[size3];
                    int index = 0;
                    while (true) {
                        mBackStack = array2;
                        if (index >= size3) {
                            break;
                        }
                        array2[index] = new BackStackState(this.mBackStack.get(index));
                        if (FragmentManagerImpl.DEBUG) {
                            Log.v("FragmentManager", "saveAllState: adding back stack #" + index + ": " + this.mBackStack.get(index));
                        }
                        ++index;
                    }
                }
            }
            final FragmentManagerState fragmentManagerState = new FragmentManagerState();
            fragmentManagerState.mActive = mActive;
            fragmentManagerState.mAdded = mAdded;
            fragmentManagerState.mBackStack = mBackStack;
            if (this.mPrimaryNav != null) {
                fragmentManagerState.mPrimaryNavActiveIndex = this.mPrimaryNav.mIndex;
            }
            fragmentManagerState.mNextFragmentIndex = this.mNextFragmentIndex;
            this.saveNonConfig();
            return (Parcelable)fragmentManagerState;
        }
        if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "saveAllState: no fragments!");
        }
        return null;
    }
    
    Bundle saveFragmentBasicState(final Fragment fragment) {
        Bundle mStateBundle = null;
        if (this.mStateBundle == null) {
            this.mStateBundle = new Bundle();
        }
        fragment.performSaveInstanceState(this.mStateBundle);
        this.dispatchOnFragmentSaveInstanceState(fragment, this.mStateBundle, false);
        if (!this.mStateBundle.isEmpty()) {
            mStateBundle = this.mStateBundle;
            this.mStateBundle = null;
        }
        if (fragment.mView != null) {
            this.saveFragmentViewState(fragment);
        }
        if (fragment.mSavedViewState != null) {
            if (mStateBundle == null) {
                mStateBundle = new Bundle();
            }
            mStateBundle.putSparseParcelableArray("android:view_state", (SparseArray)fragment.mSavedViewState);
        }
        if (!fragment.mUserVisibleHint) {
            if (mStateBundle == null) {
                mStateBundle = new Bundle();
            }
            mStateBundle.putBoolean("android:user_visible_hint", fragment.mUserVisibleHint);
        }
        return mStateBundle;
    }
    
    @Override
    public Fragment.SavedState saveFragmentInstanceState(final Fragment obj) {
        final Fragment.SavedState savedState = null;
        if (obj.mIndex < 0) {
            this.throwException(new IllegalStateException("Fragment " + obj + " is not currently in the FragmentManager"));
        }
        if (obj.mState <= 0) {
            return null;
        }
        final Bundle saveFragmentBasicState = this.saveFragmentBasicState(obj);
        Object o;
        if (saveFragmentBasicState == null) {
            o = savedState;
        }
        else {
            o = new Fragment.SavedState(saveFragmentBasicState);
        }
        return (Fragment.SavedState)o;
    }
    
    void saveFragmentViewState(final Fragment fragment) {
        if (fragment.mInnerView != null) {
            if (this.mStateArray != null) {
                this.mStateArray.clear();
            }
            else {
                this.mStateArray = (SparseArray<Parcelable>)new SparseArray();
            }
            fragment.mInnerView.saveHierarchyState((SparseArray)this.mStateArray);
            if (this.mStateArray.size() > 0) {
                fragment.mSavedViewState = this.mStateArray;
                this.mStateArray = null;
            }
        }
    }
    
    void saveNonConfig() {
        List<FragmentManagerNonConfig> list;
        List<Fragment> list2;
        if (this.mActive == null) {
            list = null;
            list2 = null;
        }
        else {
            int n = 0;
            ArrayList<FragmentManagerNonConfig> list3 = null;
            ArrayList<Fragment> list4 = null;
            while (true) {
                list = list3;
                list2 = list4;
                if (n >= this.mActive.size()) {
                    break;
                }
                final Fragment fragment = (Fragment)this.mActive.valueAt(n);
                ArrayList<Fragment> list5;
                ArrayList<FragmentManagerNonConfig> list6;
                if (fragment == null) {
                    list5 = list4;
                    list6 = list3;
                }
                else {
                    if (fragment.mRetainInstance) {
                        ArrayList<Fragment> list7;
                        if (list4 != null) {
                            list7 = list4;
                        }
                        else {
                            list7 = new ArrayList<Fragment>();
                        }
                        list7.add(fragment);
                        int mIndex;
                        if (fragment.mTarget == null) {
                            mIndex = -1;
                        }
                        else {
                            mIndex = fragment.mTarget.mIndex;
                        }
                        fragment.mTargetIndex = mIndex;
                        list4 = list7;
                        if (FragmentManagerImpl.DEBUG) {
                            Log.v("FragmentManager", "retainNonConfig: keeping retained " + fragment);
                            list4 = list7;
                        }
                    }
                    FragmentManagerNonConfig e;
                    if (fragment.mChildFragmentManager == null) {
                        e = fragment.mChildNonConfig;
                    }
                    else {
                        fragment.mChildFragmentManager.saveNonConfig();
                        e = fragment.mChildFragmentManager.mSavedNonConfig;
                    }
                    if (list3 == null && e != null) {
                        final ArrayList<FragmentManagerNonConfig> list8 = new ArrayList<FragmentManagerNonConfig>(this.mActive.size());
                        int n2 = 0;
                        while (true) {
                            list3 = list8;
                            if (n2 >= n) {
                                break;
                            }
                            list8.add(null);
                            ++n2;
                        }
                    }
                    list6 = list3;
                    list5 = list4;
                    if (list3 != null) {
                        list3.add(e);
                        list6 = list3;
                        list5 = list4;
                    }
                }
                ++n;
                list3 = list6;
                list4 = list5;
            }
        }
        if (list2 == null && list == null) {
            this.mSavedNonConfig = null;
        }
        else {
            this.mSavedNonConfig = new FragmentManagerNonConfig(list2, list);
        }
    }
    
    public void setBackStackIndex(final int i, final BackStackRecord backStackRecord) {
    Label_0033_Outer:
        while (true) {
            while (true) {
            Label_0225:
                while (true) {
                    int size = 0;
                    Label_0132: {
                        synchronized (this) {
                            if (this.mBackStackIndices == null) {
                                this.mBackStackIndices = new ArrayList<BackStackRecord>();
                            }
                            size = this.mBackStackIndices.size();
                            if (i >= size) {
                                if (size < i) {
                                    break Label_0132;
                                }
                                if (!FragmentManagerImpl.DEBUG) {
                                    this.mBackStackIndices.add(backStackRecord);
                                    return;
                                }
                                break Label_0225;
                            }
                        }
                        if (FragmentManagerImpl.DEBUG) {
                            Log.v("FragmentManager", "Setting back stack index " + i + " to " + backStackRecord);
                        }
                        this.mBackStackIndices.set(i, backStackRecord);
                        return;
                    }
                    this.mBackStackIndices.add(null);
                    if (this.mAvailBackStackIndices == null) {
                        this.mAvailBackStackIndices = new ArrayList<Integer>();
                    }
                    if (FragmentManagerImpl.DEBUG) {
                        Log.v("FragmentManager", "Adding available back stack index " + size);
                    }
                    this.mAvailBackStackIndices.add(size);
                    ++size;
                    continue Label_0033_Outer;
                }
                Log.v("FragmentManager", "Adding back stack index " + i + " with " + backStackRecord);
                continue;
            }
        }
    }
    
    public void setPrimaryNavigationFragment(final Fragment fragment) {
        Label_0004: {
            if (fragment != null) {
                if (this.mActive.get(fragment.mIndex) == fragment) {
                    if (fragment.mHost == null) {
                        break Label_0004;
                    }
                    if (fragment.getFragmentManager() == this) {
                        break Label_0004;
                    }
                }
                throw new IllegalArgumentException("Fragment " + fragment + " is not an active fragment of FragmentManager " + this);
            }
        }
        this.mPrimaryNav = fragment;
    }
    
    public void showFragment(final Fragment obj) {
        boolean mHiddenChanged = false;
        if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "show: " + obj);
        }
        if (obj.mHidden) {
            obj.mHidden = false;
            if (!obj.mHiddenChanged) {
                mHiddenChanged = true;
            }
            obj.mHiddenChanged = mHiddenChanged;
        }
    }
    
    void startPendingDeferredFragments() {
        if (this.mActive != null) {
            for (int i = 0; i < this.mActive.size(); ++i) {
                final Fragment fragment = (Fragment)this.mActive.valueAt(i);
                if (fragment != null) {
                    this.performPendingDeferredStart(fragment);
                }
            }
        }
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder(128);
        sb.append("FragmentManager{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" in ");
        if (this.mParent == null) {
            DebugUtils.buildShortClassTag(this.mHost, sb);
        }
        else {
            DebugUtils.buildShortClassTag(this.mParent, sb);
        }
        sb.append("}}");
        return sb.toString();
    }
    
    @Override
    public void unregisterFragmentLifecycleCallbacks(final FragmentLifecycleCallbacks fragmentLifecycleCallbacks) {
        final CopyOnWriteArrayList<Pair<FragmentLifecycleCallbacks, Boolean>> mLifecycleCallbacks = this.mLifecycleCallbacks;
        monitorenter(mLifecycleCallbacks);
        int i = 0;
        try {
            while (i < this.mLifecycleCallbacks.size()) {
                if (this.mLifecycleCallbacks.get(i).first == fragmentLifecycleCallbacks) {
                    this.mLifecycleCallbacks.remove(i);
                    break;
                }
                ++i;
            }
        }
        finally {
            monitorexit(mLifecycleCallbacks);
        }
    }
    
    private static class AnimateOnHWLayerIfNeededListener extends AnimationListenerWrapper
    {
        View mView;
        
        AnimateOnHWLayerIfNeededListener(final View mView, final Animation$AnimationListener animation$AnimationListener) {
            super(animation$AnimationListener);
            this.mView = mView;
        }
        
        @CallSuper
        @Override
        public void onAnimationEnd(final Animation animation) {
            if (!ViewCompat.isAttachedToWindow(this.mView) && Build$VERSION.SDK_INT < 24) {
                this.mView.setLayerType(0, (Paint)null);
            }
            else {
                this.mView.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        AnimateOnHWLayerIfNeededListener.this.mView.setLayerType(0, (Paint)null);
                    }
                });
            }
            super.onAnimationEnd(animation);
        }
    }
    
    private static class AnimationListenerWrapper implements Animation$AnimationListener
    {
        private final Animation$AnimationListener mWrapped;
        
        private AnimationListenerWrapper(final Animation$AnimationListener mWrapped) {
            this.mWrapped = mWrapped;
        }
        
        @CallSuper
        public void onAnimationEnd(final Animation animation) {
            if (this.mWrapped != null) {
                this.mWrapped.onAnimationEnd(animation);
            }
        }
        
        @CallSuper
        public void onAnimationRepeat(final Animation animation) {
            if (this.mWrapped != null) {
                this.mWrapped.onAnimationRepeat(animation);
            }
        }
        
        @CallSuper
        public void onAnimationStart(final Animation animation) {
            if (this.mWrapped != null) {
                this.mWrapped.onAnimationStart(animation);
            }
        }
    }
    
    private static class AnimationOrAnimator
    {
        public final Animation animation;
        public final Animator animator;
        
        private AnimationOrAnimator(final Animator animator) {
            this.animation = null;
            this.animator = animator;
            if (animator != null) {
                return;
            }
            throw new IllegalStateException("Animator cannot be null");
        }
        
        private AnimationOrAnimator(final Animation animation) {
            this.animation = animation;
            this.animator = null;
            if (animation != null) {
                return;
            }
            throw new IllegalStateException("Animation cannot be null");
        }
    }
    
    private static class AnimatorOnHWLayerIfNeededListener extends AnimatorListenerAdapter
    {
        View mView;
        
        AnimatorOnHWLayerIfNeededListener(final View mView) {
            this.mView = mView;
        }
        
        public void onAnimationEnd(final Animator animator) {
            this.mView.setLayerType(0, (Paint)null);
            animator.removeListener((Animator$AnimatorListener)this);
        }
        
        public void onAnimationStart(final Animator animator) {
            this.mView.setLayerType(2, (Paint)null);
        }
    }
    
    static class FragmentTag
    {
        public static final int[] Fragment;
        public static final int Fragment_id = 1;
        public static final int Fragment_name = 0;
        public static final int Fragment_tag = 2;
        
        static {
            Fragment = new int[] { 16842755, 16842960, 16842961 };
        }
    }
    
    interface OpGenerator
    {
        boolean generateOps(final ArrayList<BackStackRecord> p0, final ArrayList<Boolean> p1);
    }
    
    private class PopBackStackState implements OpGenerator
    {
        final int mFlags;
        final int mId;
        final String mName;
        
        PopBackStackState(final String mName, final int mId, final int mFlags) {
            this.mName = mName;
            this.mId = mId;
            this.mFlags = mFlags;
        }
        
        @Override
        public boolean generateOps(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2) {
            if (FragmentManagerImpl.this.mPrimaryNav != null && this.mId < 0 && this.mName == null) {
                final FragmentManager peekChildFragmentManager = FragmentManagerImpl.this.mPrimaryNav.peekChildFragmentManager();
                if (peekChildFragmentManager != null && peekChildFragmentManager.popBackStackImmediate()) {
                    return false;
                }
            }
            return FragmentManagerImpl.this.popBackStackState(list, list2, this.mName, this.mId, this.mFlags);
        }
    }
    
    static class StartEnterTransitionListener implements OnStartEnterTransitionListener
    {
        private final boolean mIsBack;
        private int mNumPostponed;
        private final BackStackRecord mRecord;
        
        StartEnterTransitionListener(final BackStackRecord mRecord, final boolean mIsBack) {
            this.mIsBack = mIsBack;
            this.mRecord = mRecord;
        }
        
        public void cancelTransaction() {
            this.mRecord.mManager.completeExecute(this.mRecord, this.mIsBack, false, false);
        }
        
        public void completeTransaction() {
            boolean b = false;
            boolean b2;
            if (this.mNumPostponed <= 0) {
                b2 = false;
            }
            else {
                b2 = true;
            }
            final FragmentManagerImpl mManager = this.mRecord.mManager;
            for (int size = mManager.mAdded.size(), i = 0; i < size; ++i) {
                final Fragment fragment = mManager.mAdded.get(i);
                fragment.setOnStartEnterTransitionListener(null);
                if (b2 && fragment.isPostponed()) {
                    fragment.startPostponedEnterTransition();
                }
            }
            final FragmentManagerImpl mManager2 = this.mRecord.mManager;
            final BackStackRecord mRecord = this.mRecord;
            final boolean mIsBack = this.mIsBack;
            if (!b2) {
                b = true;
            }
            mManager2.completeExecute(mRecord, mIsBack, b, true);
        }
        
        public boolean isReady() {
            boolean b = false;
            if (this.mNumPostponed == 0) {
                b = true;
            }
            return b;
        }
        
        @Override
        public void onStartEnterTransition() {
            --this.mNumPostponed;
            if (this.mNumPostponed == 0) {
                this.mRecord.mManager.scheduleCommit();
            }
        }
        
        @Override
        public void startListening() {
            ++this.mNumPostponed;
        }
    }
}
