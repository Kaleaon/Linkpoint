// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.util.Iterator;
import java.util.List;
import android.graphics.Rect;
import android.util.SparseIntArray;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.view.InflateException;
import java.util.StringTokenizer;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.util.LongSparseArray;
import android.util.SparseArray;
import android.widget.ListView;
import android.support.v4.view.ViewCompat;
import android.content.res.TypedArray;
import android.view.animation.AnimationUtils;
import org.xmlpull.v1.XmlPullParser;
import android.support.v4.content.res.TypedArrayUtils;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Path;
import android.view.View;
import android.view.ViewGroup;
import android.animation.TimeInterpolator;
import java.util.ArrayList;
import android.animation.Animator;
import android.support.v4.util.ArrayMap;

public abstract class Transition implements Cloneable
{
    static final boolean DBG = false;
    private static final int[] DEFAULT_MATCH_ORDER;
    private static final String LOG_TAG = "Transition";
    private static final int MATCH_FIRST = 1;
    public static final int MATCH_ID = 3;
    private static final String MATCH_ID_STR = "id";
    public static final int MATCH_INSTANCE = 1;
    private static final String MATCH_INSTANCE_STR = "instance";
    public static final int MATCH_ITEM_ID = 4;
    private static final String MATCH_ITEM_ID_STR = "itemId";
    private static final int MATCH_LAST = 4;
    public static final int MATCH_NAME = 2;
    private static final String MATCH_NAME_STR = "name";
    private static final PathMotion STRAIGHT_PATH_MOTION;
    private static ThreadLocal<ArrayMap<Animator, AnimationInfo>> sRunningAnimators;
    private ArrayList<Animator> mAnimators;
    boolean mCanRemoveViews;
    private ArrayList<Animator> mCurrentAnimators;
    long mDuration;
    private TransitionValuesMaps mEndValues;
    private ArrayList<TransitionValues> mEndValuesList;
    private boolean mEnded;
    private EpicenterCallback mEpicenterCallback;
    private TimeInterpolator mInterpolator;
    private ArrayList<TransitionListener> mListeners;
    private int[] mMatchOrder;
    private String mName;
    private ArrayMap<String, String> mNameOverrides;
    private int mNumInstances;
    TransitionSet mParent;
    private PathMotion mPathMotion;
    private boolean mPaused;
    TransitionPropagation mPropagation;
    private ViewGroup mSceneRoot;
    private long mStartDelay;
    private TransitionValuesMaps mStartValues;
    private ArrayList<TransitionValues> mStartValuesList;
    private ArrayList<View> mTargetChildExcludes;
    private ArrayList<View> mTargetExcludes;
    private ArrayList<Integer> mTargetIdChildExcludes;
    private ArrayList<Integer> mTargetIdExcludes;
    ArrayList<Integer> mTargetIds;
    private ArrayList<String> mTargetNameExcludes;
    private ArrayList<String> mTargetNames;
    private ArrayList<Class> mTargetTypeChildExcludes;
    private ArrayList<Class> mTargetTypeExcludes;
    private ArrayList<Class> mTargetTypes;
    ArrayList<View> mTargets;
    
    static {
        DEFAULT_MATCH_ORDER = new int[] { 2, 1, 3, 4 };
        STRAIGHT_PATH_MOTION = new PathMotion() {
            @Override
            public Path getPath(final float n, final float n2, final float n3, final float n4) {
                final Path path = new Path();
                path.moveTo(n, n2);
                path.lineTo(n3, n4);
                return path;
            }
        };
        Transition.sRunningAnimators = new ThreadLocal<ArrayMap<Animator, AnimationInfo>>();
    }
    
    public Transition() {
        this.mName = this.getClass().getName();
        this.mStartDelay = -1L;
        this.mDuration = -1L;
        this.mInterpolator = null;
        this.mTargetIds = new ArrayList<Integer>();
        this.mTargets = new ArrayList<View>();
        this.mTargetNames = null;
        this.mTargetTypes = null;
        this.mTargetIdExcludes = null;
        this.mTargetExcludes = null;
        this.mTargetTypeExcludes = null;
        this.mTargetNameExcludes = null;
        this.mTargetIdChildExcludes = null;
        this.mTargetChildExcludes = null;
        this.mTargetTypeChildExcludes = null;
        this.mStartValues = new TransitionValuesMaps();
        this.mEndValues = new TransitionValuesMaps();
        this.mParent = null;
        this.mMatchOrder = Transition.DEFAULT_MATCH_ORDER;
        this.mSceneRoot = null;
        this.mCanRemoveViews = false;
        this.mCurrentAnimators = new ArrayList<Animator>();
        this.mNumInstances = 0;
        this.mPaused = false;
        this.mEnded = false;
        this.mListeners = null;
        this.mAnimators = new ArrayList<Animator>();
        this.mPathMotion = Transition.STRAIGHT_PATH_MOTION;
    }
    
    public Transition(final Context context, final AttributeSet set) {
        final int n = 1;
        this.mName = this.getClass().getName();
        this.mStartDelay = -1L;
        this.mDuration = -1L;
        this.mInterpolator = null;
        this.mTargetIds = new ArrayList<Integer>();
        this.mTargets = new ArrayList<View>();
        this.mTargetNames = null;
        this.mTargetTypes = null;
        this.mTargetIdExcludes = null;
        this.mTargetExcludes = null;
        this.mTargetTypeExcludes = null;
        this.mTargetNameExcludes = null;
        this.mTargetIdChildExcludes = null;
        this.mTargetChildExcludes = null;
        this.mTargetTypeChildExcludes = null;
        this.mStartValues = new TransitionValuesMaps();
        this.mEndValues = new TransitionValuesMaps();
        this.mParent = null;
        this.mMatchOrder = Transition.DEFAULT_MATCH_ORDER;
        this.mSceneRoot = null;
        this.mCanRemoveViews = false;
        this.mCurrentAnimators = new ArrayList<Animator>();
        this.mNumInstances = 0;
        this.mPaused = false;
        this.mEnded = false;
        this.mListeners = null;
        this.mAnimators = new ArrayList<Animator>();
        this.mPathMotion = Transition.STRAIGHT_PATH_MOTION;
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, Styleable.TRANSITION);
        final XmlResourceParser xmlResourceParser = (XmlResourceParser)set;
        final long duration = TypedArrayUtils.getNamedInt(obtainStyledAttributes, (XmlPullParser)xmlResourceParser, "duration", 1, -1);
        int n2;
        if (duration < 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            this.setDuration(duration);
        }
        final long startDelay = TypedArrayUtils.getNamedInt(obtainStyledAttributes, (XmlPullParser)xmlResourceParser, "startDelay", 2, -1);
        int n3;
        if (startDelay <= 0L) {
            n3 = n;
        }
        else {
            n3 = 0;
        }
        if (n3 == 0) {
            this.setStartDelay(startDelay);
        }
        final int namedResourceId = TypedArrayUtils.getNamedResourceId(obtainStyledAttributes, (XmlPullParser)xmlResourceParser, "interpolator", 0, 0);
        if (namedResourceId > 0) {
            this.setInterpolator((TimeInterpolator)AnimationUtils.loadInterpolator(context, namedResourceId));
        }
        final String namedString = TypedArrayUtils.getNamedString(obtainStyledAttributes, (XmlPullParser)xmlResourceParser, "matchOrder", 3);
        if (namedString != null) {
            this.setMatchOrder(parseMatchOrder(namedString));
        }
        obtainStyledAttributes.recycle();
    }
    
    private void addUnmatched(final ArrayMap<View, TransitionValues> arrayMap, final ArrayMap<View, TransitionValues> arrayMap2) {
        final int n = 0;
        for (int i = 0; i < arrayMap.size(); ++i) {
            final TransitionValues e = (TransitionValues)arrayMap.valueAt(i);
            if (this.isValidTarget(e.view)) {
                this.mStartValuesList.add(e);
                this.mEndValuesList.add(null);
            }
        }
        for (int j = n; j < arrayMap2.size(); ++j) {
            final TransitionValues e2 = (TransitionValues)arrayMap2.valueAt(j);
            if (this.isValidTarget(e2.view)) {
                this.mEndValuesList.add(e2);
                this.mStartValuesList.add(null);
            }
        }
    }
    
    private static void addViewValues(final TransitionValuesMaps transitionValuesMaps, View view, final TransitionValues transitionValues) {
        transitionValuesMaps.mViewValues.put(view, transitionValues);
        final int id = view.getId();
        if (id >= 0) {
            if (transitionValuesMaps.mIdValues.indexOfKey(id) < 0) {
                transitionValuesMaps.mIdValues.put(id, (Object)view);
            }
            else {
                transitionValuesMaps.mIdValues.put(id, (Object)null);
            }
        }
        final String transitionName = ViewCompat.getTransitionName(view);
        if (transitionName != null) {
            if (!transitionValuesMaps.mNameValues.containsKey(transitionName)) {
                transitionValuesMaps.mNameValues.put(transitionName, view);
            }
            else {
                transitionValuesMaps.mNameValues.put(transitionName, null);
            }
        }
        if (view.getParent() instanceof ListView) {
            final ListView listView = (ListView)view.getParent();
            if (listView.getAdapter().hasStableIds()) {
                final long itemIdAtPosition = listView.getItemIdAtPosition(listView.getPositionForView(view));
                if (transitionValuesMaps.mItemIdValues.indexOfKey(itemIdAtPosition) < 0) {
                    ViewCompat.setHasTransientState(view, true);
                    transitionValuesMaps.mItemIdValues.put(itemIdAtPosition, view);
                }
                else {
                    view = transitionValuesMaps.mItemIdValues.get(itemIdAtPosition);
                    if (view != null) {
                        ViewCompat.setHasTransientState(view, false);
                        transitionValuesMaps.mItemIdValues.put(itemIdAtPosition, null);
                    }
                }
            }
        }
    }
    
    private static boolean alreadyContains(final int[] array, final int n) {
        final int n2 = array[n];
        for (int i = 0; i < n; ++i) {
            if (array[i] == n2) {
                return true;
            }
        }
        return false;
    }
    
    private void captureHierarchy(final View o, final boolean b) {
        if (o == null) {
            return;
        }
        final int id = o.getId();
        if (this.mTargetIdExcludes != null && this.mTargetIdExcludes.contains(id)) {
            return;
        }
        if (this.mTargetExcludes != null && this.mTargetExcludes.contains(o)) {
            return;
        }
        if (this.mTargetTypeExcludes != null) {
            for (int size = this.mTargetTypeExcludes.size(), i = 0; i < size; ++i) {
                if (this.mTargetTypeExcludes.get(i).isInstance(o)) {
                    return;
                }
            }
        }
        if (o.getParent() instanceof ViewGroup) {
            final TransitionValues transitionValues = new TransitionValues();
            transitionValues.view = o;
            if (!b) {
                this.captureEndValues(transitionValues);
            }
            else {
                this.captureStartValues(transitionValues);
            }
            transitionValues.mTargetedTransitions.add(this);
            this.capturePropagationValues(transitionValues);
            if (!b) {
                addViewValues(this.mEndValues, o, transitionValues);
            }
            else {
                addViewValues(this.mStartValues, o, transitionValues);
            }
        }
        if (o instanceof ViewGroup) {
            if (this.mTargetIdChildExcludes != null && this.mTargetIdChildExcludes.contains(id)) {
                return;
            }
            if (this.mTargetChildExcludes != null && this.mTargetChildExcludes.contains(o)) {
                return;
            }
            if (this.mTargetTypeChildExcludes != null) {
                for (int size2 = this.mTargetTypeChildExcludes.size(), j = 0; j < size2; ++j) {
                    if (this.mTargetTypeChildExcludes.get(j).isInstance(o)) {
                        return;
                    }
                }
            }
            final ViewGroup viewGroup = (ViewGroup)o;
            for (int k = 0; k < viewGroup.getChildCount(); ++k) {
                this.captureHierarchy(viewGroup.getChildAt(k), b);
            }
        }
    }
    
    private ArrayList<Integer> excludeId(ArrayList<Integer> list, final int n, final boolean b) {
        if (n > 0) {
            if (!b) {
                list = ArrayListManager.remove(list, n);
            }
            else {
                list = ArrayListManager.add(list, n);
            }
        }
        return list;
    }
    
    private static <T> ArrayList<T> excludeObject(ArrayList<T> list, final T t, final boolean b) {
        if (t != null) {
            if (!b) {
                list = ArrayListManager.remove(list, t);
            }
            else {
                list = ArrayListManager.add(list, t);
            }
        }
        return list;
    }
    
    private ArrayList<Class> excludeType(ArrayList<Class> list, final Class clazz, final boolean b) {
        if (clazz != null) {
            if (!b) {
                list = ArrayListManager.remove(list, clazz);
            }
            else {
                list = ArrayListManager.add(list, clazz);
            }
        }
        return (ArrayList<Class>)list;
    }
    
    private ArrayList<View> excludeView(ArrayList<View> list, final View view, final boolean b) {
        if (view != null) {
            if (!b) {
                list = ArrayListManager.remove(list, view);
            }
            else {
                list = ArrayListManager.add(list, view);
            }
        }
        return list;
    }
    
    private static ArrayMap<Animator, AnimationInfo> getRunningAnimators() {
        ArrayMap value = Transition.sRunningAnimators.get();
        if (value == null) {
            value = new ArrayMap();
            Transition.sRunningAnimators.set(value);
        }
        return value;
    }
    
    private static boolean isValidMatch(final int n) {
        boolean b = true;
        if (n < 1 || n > 4) {
            b = false;
        }
        return b;
    }
    
    private static boolean isValueChanged(final TransitionValues transitionValues, final TransitionValues transitionValues2, final String s) {
        final boolean b = false;
        final Object value = transitionValues.values.get(s);
        final Object value2 = transitionValues2.values.get(s);
        if (value == null) {
            final boolean b2 = b;
            if (value2 == null) {
                return b2;
            }
        }
        boolean b2;
        if (value != null && value2 != null) {
            b2 = b;
            if (!value.equals(value2)) {
                b2 = true;
            }
        }
        else {
            b2 = true;
        }
        return b2;
    }
    
    private void matchIds(final ArrayMap<View, TransitionValues> arrayMap, final ArrayMap<View, TransitionValues> arrayMap2, final SparseArray<View> sparseArray, final SparseArray<View> sparseArray2) {
        for (int size = sparseArray.size(), i = 0; i < size; ++i) {
            final View view = (View)sparseArray.valueAt(i);
            if (view != null && this.isValidTarget(view)) {
                final View view2 = (View)sparseArray2.get(sparseArray.keyAt(i));
                if (view2 != null && this.isValidTarget(view2)) {
                    final TransitionValues e = arrayMap.get(view);
                    final TransitionValues e2 = arrayMap2.get(view2);
                    if (e != null && e2 != null) {
                        this.mStartValuesList.add(e);
                        this.mEndValuesList.add(e2);
                        arrayMap.remove(view);
                        arrayMap2.remove(view2);
                    }
                }
            }
        }
    }
    
    private void matchInstances(final ArrayMap<View, TransitionValues> arrayMap, final ArrayMap<View, TransitionValues> arrayMap2) {
        for (int i = arrayMap.size() - 1; i >= 0; --i) {
            final View view = (View)arrayMap.keyAt(i);
            if (view != null && this.isValidTarget(view)) {
                final TransitionValues e = arrayMap2.remove(view);
                if (e != null && e.view != null && this.isValidTarget(e.view)) {
                    this.mStartValuesList.add((TransitionValues)arrayMap.removeAt(i));
                    this.mEndValuesList.add(e);
                }
            }
        }
    }
    
    private void matchItemIds(final ArrayMap<View, TransitionValues> arrayMap, final ArrayMap<View, TransitionValues> arrayMap2, final LongSparseArray<View> longSparseArray, final LongSparseArray<View> longSparseArray2) {
        for (int size = longSparseArray.size(), i = 0; i < size; ++i) {
            final View view = longSparseArray.valueAt(i);
            if (view != null && this.isValidTarget(view)) {
                final View view2 = longSparseArray2.get(longSparseArray.keyAt(i));
                if (view2 != null && this.isValidTarget(view2)) {
                    final TransitionValues e = arrayMap.get(view);
                    final TransitionValues e2 = arrayMap2.get(view2);
                    if (e != null && e2 != null) {
                        this.mStartValuesList.add(e);
                        this.mEndValuesList.add(e2);
                        arrayMap.remove(view);
                        arrayMap2.remove(view2);
                    }
                }
            }
        }
    }
    
    private void matchNames(final ArrayMap<View, TransitionValues> arrayMap, final ArrayMap<View, TransitionValues> arrayMap2, final ArrayMap<String, View> arrayMap3, final ArrayMap<String, View> arrayMap4) {
        for (int size = arrayMap3.size(), i = 0; i < size; ++i) {
            final View view = (View)arrayMap3.valueAt(i);
            if (view != null && this.isValidTarget(view)) {
                final View view2 = arrayMap4.get(arrayMap3.keyAt(i));
                if (view2 != null && this.isValidTarget(view2)) {
                    final TransitionValues e = arrayMap.get(view);
                    final TransitionValues e2 = arrayMap2.get(view2);
                    if (e != null && e2 != null) {
                        this.mStartValuesList.add(e);
                        this.mEndValuesList.add(e2);
                        arrayMap.remove(view);
                        arrayMap2.remove(view2);
                    }
                }
            }
        }
    }
    
    private void matchStartAndEnd(final TransitionValuesMaps transitionValuesMaps, final TransitionValuesMaps transitionValuesMaps2) {
        final ArrayMap arrayMap = new ArrayMap(transitionValuesMaps.mViewValues);
        final ArrayMap arrayMap2 = new ArrayMap(transitionValuesMaps2.mViewValues);
        for (int i = 0; i < this.mMatchOrder.length; ++i) {
            switch (this.mMatchOrder[i]) {
                case 1: {
                    this.matchInstances(arrayMap, arrayMap2);
                    break;
                }
                case 2: {
                    this.matchNames(arrayMap, arrayMap2, transitionValuesMaps.mNameValues, transitionValuesMaps2.mNameValues);
                    break;
                }
                case 3: {
                    this.matchIds(arrayMap, arrayMap2, transitionValuesMaps.mIdValues, transitionValuesMaps2.mIdValues);
                    break;
                }
                case 4: {
                    this.matchItemIds(arrayMap, arrayMap2, transitionValuesMaps.mItemIdValues, transitionValuesMaps2.mItemIdValues);
                    break;
                }
            }
        }
        this.addUnmatched(arrayMap, arrayMap2);
    }
    
    private static int[] parseMatchOrder(final String str) {
        final StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
        int[] array = new int[stringTokenizer.countTokens()];
        int n = 0;
        while (stringTokenizer.hasMoreTokens()) {
            final String trim = stringTokenizer.nextToken().trim();
            if (!"id".equalsIgnoreCase(trim)) {
                if (!"instance".equalsIgnoreCase(trim)) {
                    if (!"name".equalsIgnoreCase(trim)) {
                        if (!"itemId".equalsIgnoreCase(trim)) {
                            if (!trim.isEmpty()) {
                                throw new InflateException("Unknown match type in matchOrder: '" + trim + "'");
                            }
                            final int[] array2 = new int[array.length - 1];
                            System.arraycopy(array, 0, array2, 0, n);
                            --n;
                            array = array2;
                        }
                        else {
                            array[n] = 4;
                        }
                    }
                    else {
                        array[n] = 2;
                    }
                }
                else {
                    array[n] = 1;
                }
            }
            else {
                array[n] = 3;
            }
            ++n;
        }
        return array;
    }
    
    private void runAnimator(final Animator animator, final ArrayMap<Animator, AnimationInfo> arrayMap) {
        if (animator != null) {
            animator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator o) {
                    arrayMap.remove(o);
                    Transition.this.mCurrentAnimators.remove(o);
                }
                
                public void onAnimationStart(final Animator e) {
                    Transition.this.mCurrentAnimators.add(e);
                }
            });
            this.animate(animator);
        }
    }
    
    @NonNull
    public Transition addListener(@NonNull final TransitionListener e) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<TransitionListener>();
        }
        this.mListeners.add(e);
        return this;
    }
    
    @NonNull
    public Transition addTarget(@IdRes final int i) {
        if (i > 0) {
            this.mTargetIds.add(i);
        }
        return this;
    }
    
    @NonNull
    public Transition addTarget(@NonNull final View e) {
        this.mTargets.add(e);
        return this;
    }
    
    @NonNull
    public Transition addTarget(@NonNull final Class e) {
        if (this.mTargetTypes == null) {
            this.mTargetTypes = new ArrayList<Class>();
        }
        this.mTargetTypes.add(e);
        return this;
    }
    
    @NonNull
    public Transition addTarget(@NonNull final String e) {
        if (this.mTargetNames == null) {
            this.mTargetNames = new ArrayList<String>();
        }
        this.mTargetNames.add(e);
        return this;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    protected void animate(final Animator animator) {
        final int n = 1;
        if (animator != null) {
            int n2;
            if (this.getDuration() < 0L) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 == 0) {
                animator.setDuration(this.getDuration());
            }
            int n3;
            if (this.getStartDelay() < 0L) {
                n3 = n;
            }
            else {
                n3 = 0;
            }
            if (n3 == 0) {
                animator.setStartDelay(this.getStartDelay());
            }
            if (this.getInterpolator() != null) {
                animator.setInterpolator(this.getInterpolator());
            }
            animator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                public void onAnimationEnd(final Animator animator) {
                    Transition.this.end();
                    animator.removeListener((Animator$AnimatorListener)this);
                }
            });
            animator.start();
        }
        else {
            this.end();
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    protected void cancel() {
        for (int i = this.mCurrentAnimators.size() - 1; i >= 0; --i) {
            this.mCurrentAnimators.get(i).cancel();
        }
        if (this.mListeners != null && this.mListeners.size() > 0) {
            final ArrayList list = (ArrayList)this.mListeners.clone();
            for (int size = list.size(), j = 0; j < size; ++j) {
                ((TransitionListener)list.get(j)).onTransitionCancel(this);
            }
        }
    }
    
    public abstract void captureEndValues(@NonNull final TransitionValues p0);
    
    void capturePropagationValues(final TransitionValues transitionValues) {
        final int n = 0;
        if (this.mPropagation != null && !transitionValues.values.isEmpty()) {
            final String[] propagationProperties = this.mPropagation.getPropagationProperties();
            if (propagationProperties != null) {
                int i = 0;
                while (true) {
                    while (i < propagationProperties.length) {
                        final int n2 = n;
                        if (transitionValues.values.containsKey(propagationProperties[i])) {
                            ++i;
                        }
                        else {
                            if (n2 == 0) {
                                this.mPropagation.captureValues(transitionValues);
                            }
                            return;
                        }
                    }
                    final int n2 = 1;
                    continue;
                }
            }
        }
    }
    
    public abstract void captureStartValues(@NonNull final TransitionValues p0);
    
    void captureValues(final ViewGroup viewGroup, final boolean b) {
        final int n = 0;
        this.clearValues(b);
        while (true) {
            Label_0076: {
                if (this.mTargetIds.size() <= 0 && this.mTargets.size() <= 0) {
                    break Label_0076;
                }
                if (this.mTargetNames != null && !this.mTargetNames.isEmpty()) {
                    break Label_0076;
                }
                if (this.mTargetTypes != null && !this.mTargetTypes.isEmpty()) {
                    break Label_0076;
                }
                for (int i = 0; i < this.mTargetIds.size(); ++i) {
                    final View viewById = viewGroup.findViewById((int)this.mTargetIds.get(i));
                    if (viewById != null) {
                        final TransitionValues transitionValues = new TransitionValues();
                        transitionValues.view = viewById;
                        if (!b) {
                            this.captureEndValues(transitionValues);
                        }
                        else {
                            this.captureStartValues(transitionValues);
                        }
                        transitionValues.mTargetedTransitions.add(this);
                        this.capturePropagationValues(transitionValues);
                        if (!b) {
                            addViewValues(this.mEndValues, viewById, transitionValues);
                        }
                        else {
                            addViewValues(this.mStartValues, viewById, transitionValues);
                        }
                    }
                }
                for (int j = 0; j < this.mTargets.size(); ++j) {
                    final View view = this.mTargets.get(j);
                    final TransitionValues transitionValues2 = new TransitionValues();
                    transitionValues2.view = view;
                    if (!b) {
                        this.captureEndValues(transitionValues2);
                    }
                    else {
                        this.captureStartValues(transitionValues2);
                    }
                    transitionValues2.mTargetedTransitions.add(this);
                    this.capturePropagationValues(transitionValues2);
                    if (!b) {
                        addViewValues(this.mEndValues, view, transitionValues2);
                    }
                    else {
                        addViewValues(this.mStartValues, view, transitionValues2);
                    }
                }
                if (!b && this.mNameOverrides != null) {
                    final int size = this.mNameOverrides.size();
                    final ArrayList list = new ArrayList<View>(size);
                    for (int k = 0; k < size; ++k) {
                        list.add(this.mStartValues.mNameValues.remove(this.mNameOverrides.keyAt(k)));
                    }
                    for (int l = n; l < size; ++l) {
                        final View view2 = list.get(l);
                        if (view2 != null) {
                            this.mStartValues.mNameValues.put(this.mNameOverrides.valueAt(l), view2);
                        }
                    }
                }
                return;
            }
            this.captureHierarchy((View)viewGroup, b);
            continue;
        }
    }
    
    void clearValues(final boolean b) {
        if (!b) {
            this.mEndValues.mViewValues.clear();
            this.mEndValues.mIdValues.clear();
            this.mEndValues.mItemIdValues.clear();
        }
        else {
            this.mStartValues.mViewValues.clear();
            this.mStartValues.mIdValues.clear();
            this.mStartValues.mItemIdValues.clear();
        }
    }
    
    public Transition clone() {
        try {
            final Transition transition = (Transition)super.clone();
            transition.mAnimators = new ArrayList<Animator>();
            transition.mStartValues = new TransitionValuesMaps();
            transition.mEndValues = new TransitionValuesMaps();
            transition.mStartValuesList = null;
            transition.mEndValuesList = null;
            return transition;
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
    
    @Nullable
    public Animator createAnimator(@NonNull final ViewGroup viewGroup, @Nullable final TransitionValues transitionValues, @Nullable final TransitionValues transitionValues2) {
        return null;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    protected void createAnimators(final ViewGroup viewGroup, final TransitionValuesMaps transitionValuesMaps, final TransitionValuesMaps transitionValuesMaps2, final ArrayList<TransitionValues> list, final ArrayList<TransitionValues> list2) {
        final ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        long min = Long.MAX_VALUE;
        final SparseIntArray sparseIntArray = new SparseIntArray();
        long n;
        for (int size = list.size(), i = 0; i < size; ++i, min = n) {
            TransitionValues transitionValues = list.get(i);
            TransitionValues transitionValues2 = list2.get(i);
            if (transitionValues != null && !transitionValues.mTargetedTransitions.contains(this)) {
                transitionValues = null;
            }
            if (transitionValues2 != null && !transitionValues2.mTargetedTransitions.contains(this)) {
                transitionValues2 = null;
            }
            if (transitionValues == null) {
                n = min;
                if (transitionValues2 == null) {
                    continue;
                }
            }
            int n2;
            if (transitionValues != null && transitionValues2 != null && !this.isTransitionRequired(transitionValues, transitionValues2)) {
                n2 = 0;
            }
            else {
                n2 = 1;
            }
            if (n2 == 0) {
                n = min;
            }
            else {
                Animator animator = this.createAnimator(viewGroup, transitionValues, transitionValues2);
                n = min;
                if (animator != null) {
                    TransitionValues transitionValues3 = null;
                    View view;
                    if (transitionValues2 == null) {
                        view = transitionValues.view;
                        transitionValues3 = null;
                    }
                    else {
                        view = transitionValues2.view;
                        final String[] transitionProperties = this.getTransitionProperties();
                        if (view != null && transitionProperties != null && transitionProperties.length > 0) {
                            transitionValues3 = new TransitionValues();
                            transitionValues3.view = view;
                            final TransitionValues transitionValues4 = transitionValuesMaps2.mViewValues.get(view);
                            if (transitionValues4 != null) {
                                for (int j = 0; j < transitionProperties.length; ++j) {
                                    transitionValues3.values.put(transitionProperties[j], transitionValues4.values.get(transitionProperties[j]));
                                }
                            }
                            for (int size2 = runningAnimators.size(), k = 0; k < size2; ++k) {
                                final AnimationInfo animationInfo = (AnimationInfo)runningAnimators.get(runningAnimators.keyAt(k));
                                if (animationInfo.mValues != null && animationInfo.mView == view && animationInfo.mName.equals(this.getName()) && animationInfo.mValues.equals(transitionValues3)) {
                                    animator = null;
                                    break;
                                }
                            }
                        }
                    }
                    n = min;
                    if (animator != null) {
                        if (this.mPropagation != null) {
                            final long startDelay = this.mPropagation.getStartDelay(viewGroup, this, transitionValues, transitionValues2);
                            sparseIntArray.put(this.mAnimators.size(), (int)startDelay);
                            min = Math.min(startDelay, min);
                        }
                        runningAnimators.put((Object)animator, (Object)new AnimationInfo(view, this.getName(), this, ViewUtils.getWindowId((View)viewGroup), transitionValues3));
                        this.mAnimators.add(animator);
                        n = min;
                    }
                }
            }
        }
        if (min != 0L) {
            for (int l = 0; l < sparseIntArray.size(); ++l) {
                final Animator animator2 = this.mAnimators.get(sparseIntArray.keyAt(l));
                animator2.setStartDelay(sparseIntArray.valueAt(l) - min + animator2.getStartDelay());
            }
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    protected void end() {
        --this.mNumInstances;
        if (this.mNumInstances == 0) {
            if (this.mListeners != null && this.mListeners.size() > 0) {
                final ArrayList list = (ArrayList)this.mListeners.clone();
                for (int size = list.size(), i = 0; i < size; ++i) {
                    ((TransitionListener)list.get(i)).onTransitionEnd(this);
                }
            }
            for (int j = 0; j < this.mStartValues.mItemIdValues.size(); ++j) {
                final View view = this.mStartValues.mItemIdValues.valueAt(j);
                if (view != null) {
                    ViewCompat.setHasTransientState(view, false);
                }
            }
            for (int k = 0; k < this.mEndValues.mItemIdValues.size(); ++k) {
                final View view2 = this.mEndValues.mItemIdValues.valueAt(k);
                if (view2 != null) {
                    ViewCompat.setHasTransientState(view2, false);
                }
            }
            this.mEnded = true;
        }
    }
    
    @NonNull
    public Transition excludeChildren(@IdRes final int n, final boolean b) {
        this.mTargetIdChildExcludes = this.excludeId(this.mTargetIdChildExcludes, n, b);
        return this;
    }
    
    @NonNull
    public Transition excludeChildren(@NonNull final View view, final boolean b) {
        this.mTargetChildExcludes = this.excludeView(this.mTargetChildExcludes, view, b);
        return this;
    }
    
    @NonNull
    public Transition excludeChildren(@NonNull final Class clazz, final boolean b) {
        this.mTargetTypeChildExcludes = this.excludeType(this.mTargetTypeChildExcludes, clazz, b);
        return this;
    }
    
    @NonNull
    public Transition excludeTarget(@IdRes final int n, final boolean b) {
        this.mTargetIdExcludes = this.excludeId(this.mTargetIdExcludes, n, b);
        return this;
    }
    
    @NonNull
    public Transition excludeTarget(@NonNull final View view, final boolean b) {
        this.mTargetExcludes = this.excludeView(this.mTargetExcludes, view, b);
        return this;
    }
    
    @NonNull
    public Transition excludeTarget(@NonNull final Class clazz, final boolean b) {
        this.mTargetTypeExcludes = this.excludeType(this.mTargetTypeExcludes, clazz, b);
        return this;
    }
    
    @NonNull
    public Transition excludeTarget(@NonNull final String s, final boolean b) {
        this.mTargetNameExcludes = excludeObject(this.mTargetNameExcludes, s, b);
        return this;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    void forceToEnd(final ViewGroup viewGroup) {
        final ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        int i = runningAnimators.size();
        if (viewGroup != null) {
            final WindowIdImpl windowId = ViewUtils.getWindowId((View)viewGroup);
            --i;
            while (i >= 0) {
                final AnimationInfo animationInfo = (AnimationInfo)runningAnimators.valueAt(i);
                if (animationInfo.mView != null && windowId != null && windowId.equals(animationInfo.mWindowId)) {
                    ((Animator)runningAnimators.keyAt(i)).end();
                }
                --i;
            }
        }
    }
    
    public long getDuration() {
        return this.mDuration;
    }
    
    @Nullable
    public Rect getEpicenter() {
        if (this.mEpicenterCallback != null) {
            return this.mEpicenterCallback.onGetEpicenter(this);
        }
        return null;
    }
    
    @Nullable
    public EpicenterCallback getEpicenterCallback() {
        return this.mEpicenterCallback;
    }
    
    @Nullable
    public TimeInterpolator getInterpolator() {
        return this.mInterpolator;
    }
    
    TransitionValues getMatchedTransitionValues(final View view, final boolean b) {
        int i = 0;
        if (this.mParent != null) {
            return this.mParent.getMatchedTransitionValues(view, b);
        }
        ArrayList<TransitionValues> list;
        if (!b) {
            list = this.mEndValuesList;
        }
        else {
            list = this.mStartValuesList;
        }
        if (list != null) {
            while (true) {
                while (i < list.size()) {
                    final TransitionValues transitionValues = list.get(i);
                    if (transitionValues == null) {
                        return null;
                    }
                    if (transitionValues.view == view) {
                        TransitionValues transitionValues2;
                        if (i < 0) {
                            transitionValues2 = null;
                        }
                        else {
                            ArrayList<TransitionValues> list2;
                            if (!b) {
                                list2 = this.mStartValuesList;
                            }
                            else {
                                list2 = this.mEndValuesList;
                            }
                            transitionValues2 = list2.get(i);
                        }
                        return transitionValues2;
                    }
                    ++i;
                }
                i = -1;
                continue;
            }
        }
        return null;
    }
    
    @NonNull
    public String getName() {
        return this.mName;
    }
    
    @NonNull
    public PathMotion getPathMotion() {
        return this.mPathMotion;
    }
    
    @Nullable
    public TransitionPropagation getPropagation() {
        return this.mPropagation;
    }
    
    public long getStartDelay() {
        return this.mStartDelay;
    }
    
    @NonNull
    public List<Integer> getTargetIds() {
        return this.mTargetIds;
    }
    
    @Nullable
    public List<String> getTargetNames() {
        return this.mTargetNames;
    }
    
    @Nullable
    public List<Class> getTargetTypes() {
        return this.mTargetTypes;
    }
    
    @NonNull
    public List<View> getTargets() {
        return this.mTargets;
    }
    
    @Nullable
    public String[] getTransitionProperties() {
        return null;
    }
    
    @Nullable
    public TransitionValues getTransitionValues(@NonNull final View view, final boolean b) {
        if (this.mParent == null) {
            TransitionValuesMaps transitionValuesMaps;
            if (!b) {
                transitionValuesMaps = this.mEndValues;
            }
            else {
                transitionValuesMaps = this.mStartValues;
            }
            return (TransitionValues)transitionValuesMaps.mViewValues.get(view);
        }
        return this.mParent.getTransitionValues(view, b);
    }
    
    public boolean isTransitionRequired(@Nullable final TransitionValues transitionValues, @Nullable final TransitionValues transitionValues2) {
        final boolean b = false;
        boolean b2;
        if (transitionValues == null) {
            b2 = b;
        }
        else {
            b2 = b;
            if (transitionValues2 != null) {
                final String[] transitionProperties = this.getTransitionProperties();
                if (transitionProperties != null) {
                    for (int length = transitionProperties.length, i = 0; i < length; ++i) {
                        if (isValueChanged(transitionValues, transitionValues2, transitionProperties[i])) {
                            b2 = true;
                            return b2;
                        }
                    }
                    b2 = false;
                    return b2;
                }
                final Iterator<String> iterator = transitionValues.values.keySet().iterator();
                do {
                    b2 = b;
                    if (iterator.hasNext()) {
                        continue;
                    }
                    return b2;
                } while (!isValueChanged(transitionValues, transitionValues2, iterator.next()));
                b2 = true;
            }
        }
        return b2;
    }
    
    boolean isValidTarget(final View view) {
        final int id = view.getId();
        if (this.mTargetIdExcludes != null && this.mTargetIdExcludes.contains(id)) {
            return false;
        }
        if (this.mTargetExcludes != null && this.mTargetExcludes.contains(view)) {
            return false;
        }
        if (this.mTargetTypeExcludes != null) {
            for (int size = this.mTargetTypeExcludes.size(), i = 0; i < size; ++i) {
                if (this.mTargetTypeExcludes.get(i).isInstance(view)) {
                    return false;
                }
            }
        }
        if (this.mTargetNameExcludes != null && ViewCompat.getTransitionName(view) != null && this.mTargetNameExcludes.contains(ViewCompat.getTransitionName(view))) {
            return false;
        }
        if (this.mTargetIds.size() == 0 && this.mTargets.size() == 0) {
            if (this.mTargetTypes == null || this.mTargetTypes.isEmpty()) {
                if (this.mTargetNames == null || this.mTargetNames.isEmpty()) {
                    return true;
                }
            }
        }
        if (this.mTargetIds.contains(id) || this.mTargets.contains(view)) {
            return true;
        }
        if (this.mTargetNames != null && this.mTargetNames.contains(ViewCompat.getTransitionName(view))) {
            return true;
        }
        if (this.mTargetTypes != null) {
            for (int j = 0; j < this.mTargetTypes.size(); ++j) {
                if (this.mTargetTypes.get(j).isInstance(view)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void pause(final View view) {
        if (!this.mEnded) {
            final ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
            int i = runningAnimators.size();
            final WindowIdImpl windowId = ViewUtils.getWindowId(view);
            --i;
            while (i >= 0) {
                final AnimationInfo animationInfo = (AnimationInfo)runningAnimators.valueAt(i);
                if (animationInfo.mView != null && windowId.equals(animationInfo.mWindowId)) {
                    AnimatorUtils.pause((Animator)runningAnimators.keyAt(i));
                }
                --i;
            }
            if (this.mListeners != null && this.mListeners.size() > 0) {
                final ArrayList list = (ArrayList)this.mListeners.clone();
                for (int size = list.size(), j = 0; j < size; ++j) {
                    ((TransitionListener)list.get(j)).onTransitionPause(this);
                }
            }
            this.mPaused = true;
        }
    }
    
    void playTransition(final ViewGroup viewGroup) {
        this.mStartValuesList = new ArrayList<TransitionValues>();
        this.mEndValuesList = new ArrayList<TransitionValues>();
        this.matchStartAndEnd(this.mStartValues, this.mEndValues);
        final ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        int i = runningAnimators.size();
        final WindowIdImpl windowId = ViewUtils.getWindowId((View)viewGroup);
        --i;
        while (i >= 0) {
            final Animator animator = (Animator)runningAnimators.keyAt(i);
            if (animator != null) {
                final AnimationInfo animationInfo = (AnimationInfo)runningAnimators.get(animator);
                if (animationInfo != null && animationInfo.mView != null && windowId.equals(animationInfo.mWindowId)) {
                    final TransitionValues mValues = animationInfo.mValues;
                    final View mView = animationInfo.mView;
                    final TransitionValues transitionValues = this.getTransitionValues(mView, true);
                    final TransitionValues matchedTransitionValues = this.getMatchedTransitionValues(mView, true);
                    int n = 0;
                    Label_0195: {
                        if (transitionValues != null || matchedTransitionValues != null) {
                            if (animationInfo.mTransition.isTransitionRequired(mValues, matchedTransitionValues)) {
                                n = 1;
                                break Label_0195;
                            }
                        }
                        n = 0;
                    }
                    if (n != 0) {
                        if (!animator.isRunning() && !animator.isStarted()) {
                            runningAnimators.remove(animator);
                        }
                        else {
                            animator.cancel();
                        }
                    }
                }
            }
            --i;
        }
        this.createAnimators(viewGroup, this.mStartValues, this.mEndValues, this.mStartValuesList, this.mEndValuesList);
        this.runAnimators();
    }
    
    @NonNull
    public Transition removeListener(@NonNull final TransitionListener o) {
        if (this.mListeners != null) {
            this.mListeners.remove(o);
            if (this.mListeners.size() == 0) {
                this.mListeners = null;
            }
            return this;
        }
        return this;
    }
    
    @NonNull
    public Transition removeTarget(@IdRes final int i) {
        if (i > 0) {
            this.mTargetIds.remove((Object)i);
        }
        return this;
    }
    
    @NonNull
    public Transition removeTarget(@NonNull final View o) {
        this.mTargets.remove(o);
        return this;
    }
    
    @NonNull
    public Transition removeTarget(@NonNull final Class o) {
        if (this.mTargetTypes != null) {
            this.mTargetTypes.remove(o);
        }
        return this;
    }
    
    @NonNull
    public Transition removeTarget(@NonNull final String o) {
        if (this.mTargetNames != null) {
            this.mTargetNames.remove(o);
        }
        return this;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void resume(final View view) {
        if (this.mPaused) {
            if (!this.mEnded) {
                final ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
                int i = runningAnimators.size();
                final WindowIdImpl windowId = ViewUtils.getWindowId(view);
                --i;
                while (i >= 0) {
                    final AnimationInfo animationInfo = (AnimationInfo)runningAnimators.valueAt(i);
                    if (animationInfo.mView != null && windowId.equals(animationInfo.mWindowId)) {
                        AnimatorUtils.resume((Animator)runningAnimators.keyAt(i));
                    }
                    --i;
                }
                if (this.mListeners != null && this.mListeners.size() > 0) {
                    final ArrayList list = (ArrayList)this.mListeners.clone();
                    for (int size = list.size(), j = 0; j < size; ++j) {
                        ((TransitionListener)list.get(j)).onTransitionResume(this);
                    }
                }
            }
            this.mPaused = false;
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    protected void runAnimators() {
        this.start();
        final ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        for (final Animator animator : this.mAnimators) {
            if (runningAnimators.containsKey(animator)) {
                this.start();
                this.runAnimator(animator, runningAnimators);
            }
        }
        this.mAnimators.clear();
        this.end();
    }
    
    void setCanRemoveViews(final boolean mCanRemoveViews) {
        this.mCanRemoveViews = mCanRemoveViews;
    }
    
    @NonNull
    public Transition setDuration(final long mDuration) {
        this.mDuration = mDuration;
        return this;
    }
    
    public void setEpicenterCallback(@Nullable final EpicenterCallback mEpicenterCallback) {
        this.mEpicenterCallback = mEpicenterCallback;
    }
    
    @NonNull
    public Transition setInterpolator(@Nullable final TimeInterpolator mInterpolator) {
        this.mInterpolator = mInterpolator;
        return this;
    }
    
    public void setMatchOrder(final int... array) {
        int i = 0;
        if (array != null && array.length != 0) {
            while (i < array.length) {
                if (!isValidMatch(array[i])) {
                    throw new IllegalArgumentException("matches contains invalid value");
                }
                if (alreadyContains(array, i)) {
                    throw new IllegalArgumentException("matches contains a duplicate value");
                }
                ++i;
            }
            this.mMatchOrder = array.clone();
        }
        else {
            this.mMatchOrder = Transition.DEFAULT_MATCH_ORDER;
        }
    }
    
    public void setPathMotion(@Nullable final PathMotion mPathMotion) {
        if (mPathMotion != null) {
            this.mPathMotion = mPathMotion;
        }
        else {
            this.mPathMotion = Transition.STRAIGHT_PATH_MOTION;
        }
    }
    
    public void setPropagation(@Nullable final TransitionPropagation mPropagation) {
        this.mPropagation = mPropagation;
    }
    
    Transition setSceneRoot(final ViewGroup mSceneRoot) {
        this.mSceneRoot = mSceneRoot;
        return this;
    }
    
    @NonNull
    public Transition setStartDelay(final long mStartDelay) {
        this.mStartDelay = mStartDelay;
        return this;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    protected void start() {
        if (this.mNumInstances == 0) {
            if (this.mListeners != null && this.mListeners.size() > 0) {
                final ArrayList list = (ArrayList)this.mListeners.clone();
                for (int size = list.size(), i = 0; i < size; ++i) {
                    ((TransitionListener)list.get(i)).onTransitionStart(this);
                }
            }
            this.mEnded = false;
        }
        ++this.mNumInstances;
    }
    
    @Override
    public String toString() {
        return this.toString("");
    }
    
    String toString(String str) {
        final int n = 0;
        final String str2 = str = str + this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode()) + ": ";
        if (this.mDuration != -1L) {
            str = str2 + "dur(" + this.mDuration + ") ";
        }
        String s = str;
        if (this.mStartDelay != -1L) {
            s = str + "dly(" + this.mStartDelay + ") ";
        }
        if (this.mInterpolator != null) {
            s = s + "interp(" + this.mInterpolator + ") ";
        }
        if (this.mTargetIds.size() > 0 || this.mTargets.size() > 0) {
            String string = s + "tgts(";
            if (this.mTargetIds.size() > 0) {
                str = string;
                for (int i = 0; i < this.mTargetIds.size(); ++i) {
                    if (i > 0) {
                        str += ", ";
                    }
                    str += this.mTargetIds.get(i);
                }
                string = str;
            }
            str = string;
            int j = n;
            if (this.mTargets.size() <= 0) {
                str = string;
            }
            else {
                while (j < this.mTargets.size()) {
                    if (j > 0) {
                        str += ", ";
                    }
                    str += this.mTargets.get(j);
                    ++j;
                }
            }
            s = str + ")";
        }
        return s;
    }
    
    private static class AnimationInfo
    {
        String mName;
        Transition mTransition;
        TransitionValues mValues;
        View mView;
        WindowIdImpl mWindowId;
        
        AnimationInfo(final View mView, final String mName, final Transition mTransition, final WindowIdImpl mWindowId, final TransitionValues mValues) {
            this.mView = mView;
            this.mName = mName;
            this.mValues = mValues;
            this.mWindowId = mWindowId;
            this.mTransition = mTransition;
        }
    }
    
    private static class ArrayListManager
    {
        static <T> ArrayList<T> add(ArrayList<T> list, final T t) {
            if (list == null) {
                list = new ArrayList<T>();
            }
            if (!list.contains(t)) {
                list.add(t);
            }
            return list;
        }
        
        static <T> ArrayList<T> remove(final ArrayList<T> list, final T o) {
            ArrayList<T> list2;
            if (list == null) {
                list2 = list;
            }
            else {
                list.remove(o);
                list2 = list;
                if (list.isEmpty()) {
                    list2 = null;
                }
            }
            return list2;
        }
    }
    
    public abstract static class EpicenterCallback
    {
        public abstract Rect onGetEpicenter(@NonNull final Transition p0);
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public @interface MatchOrder {
    }
    
    public interface TransitionListener
    {
        void onTransitionCancel(@NonNull final Transition p0);
        
        void onTransitionEnd(@NonNull final Transition p0);
        
        void onTransitionPause(@NonNull final Transition p0);
        
        void onTransitionResume(@NonNull final Transition p0);
        
        void onTransitionStart(@NonNull final Transition p0);
    }
}
