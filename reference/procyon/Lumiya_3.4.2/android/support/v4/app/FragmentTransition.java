// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import android.os.Build$VERSION;
import android.graphics.Rect;
import android.view.ViewGroup;
import android.support.annotation.RequiresApi;
import java.util.Map;
import java.util.List;
import android.util.SparseArray;
import android.support.v4.view.ViewCompat;
import java.util.Collection;
import android.support.v4.util.ArrayMap;
import android.view.View;
import java.util.ArrayList;

class FragmentTransition
{
    private static final int[] INVERSE_OPS;
    
    static {
        INVERSE_OPS = new int[] { 0, 3, 0, 1, 5, 4, 7, 6, 9, 8 };
    }
    
    private static void addSharedElementsWithMatchingNames(final ArrayList<View> list, final ArrayMap<String, View> arrayMap, final Collection<String> collection) {
        for (int i = arrayMap.size() - 1; i >= 0; --i) {
            final View e = (View)arrayMap.valueAt(i);
            if (collection.contains(ViewCompat.getTransitionName(e))) {
                list.add(e);
            }
        }
    }
    
    private static void addToFirstInLastOut(final BackStackRecord backStackRecord, final BackStackRecord.Op op, final SparseArray<FragmentContainerTransition> sparseArray, final boolean b, final boolean b2) {
        final Fragment fragment = op.fragment;
        if (fragment == null) {
            return;
        }
        final int mContainerId = fragment.mContainerId;
        if (mContainerId != 0) {
            int cmd;
            if (!b) {
                cmd = op.cmd;
            }
            else {
                cmd = FragmentTransition.INVERSE_OPS[op.cmd];
            }
            int n = 0;
            int n2 = 0;
            boolean b3 = false;
            int n3 = 0;
            switch (cmd) {
                default: {
                    n = 0;
                    n2 = 0;
                    b3 = false;
                    n3 = 0;
                    break;
                }
                case 5: {
                    if (!b2) {
                        n3 = (fragment.mHidden ? 1 : 0);
                    }
                    else if (fragment.mHiddenChanged && !fragment.mHidden && fragment.mAdded) {
                        n3 = 1;
                    }
                    else {
                        n3 = 0;
                    }
                    n = 1;
                    n2 = 0;
                    b3 = false;
                    break;
                }
                case 1:
                case 7: {
                    if (!b2) {
                        if (!fragment.mAdded && !fragment.mHidden) {
                            n3 = 1;
                        }
                        else {
                            n3 = 0;
                        }
                    }
                    else {
                        n3 = (fragment.mIsNewlyAdded ? 1 : 0);
                    }
                    n = 1;
                    n2 = 0;
                    b3 = false;
                    break;
                }
                case 4: {
                    int n4;
                    if (!b2) {
                        if (fragment.mAdded && !fragment.mHidden) {
                            n4 = 1;
                        }
                        else {
                            n4 = 0;
                        }
                    }
                    else if (fragment.mHiddenChanged && fragment.mAdded && fragment.mHidden) {
                        n4 = 1;
                    }
                    else {
                        n4 = 0;
                    }
                    final int n5 = 0;
                    n2 = n4;
                    b3 = true;
                    n3 = 0;
                    n = n5;
                    break;
                }
                case 3:
                case 6: {
                    int n6;
                    if (!b2) {
                        if (fragment.mAdded && !fragment.mHidden) {
                            n6 = 1;
                        }
                        else {
                            n6 = 0;
                        }
                    }
                    else if (!fragment.mAdded && fragment.mView != null && fragment.mView.getVisibility() == 0 && fragment.mPostponedAlpha >= 0.0f) {
                        n6 = 1;
                    }
                    else {
                        n6 = 0;
                    }
                    final int n7 = 0;
                    n2 = n6;
                    b3 = true;
                    n3 = 0;
                    n = n7;
                    break;
                }
            }
            FragmentContainerTransition ensureContainer = (FragmentContainerTransition)sparseArray.get(mContainerId);
            if (n3 != 0) {
                ensureContainer = ensureContainer(ensureContainer, sparseArray, mContainerId);
                ensureContainer.lastIn = fragment;
                ensureContainer.lastInIsPop = b;
                ensureContainer.lastInTransaction = backStackRecord;
            }
            if (!b2 && n != 0) {
                if (ensureContainer != null && ensureContainer.firstOut == fragment) {
                    ensureContainer.firstOut = null;
                }
                final FragmentManagerImpl mManager = backStackRecord.mManager;
                if (fragment.mState < 1 && mManager.mCurState >= 1 && !backStackRecord.mReorderingAllowed) {
                    mManager.makeActive(fragment);
                    mManager.moveToState(fragment, 1, 0, 0, false);
                }
            }
            FragmentContainerTransition fragmentContainerTransition;
            if (n2 != 0 && (ensureContainer == null || ensureContainer.firstOut == null)) {
                final FragmentContainerTransition ensureContainer2 = ensureContainer(ensureContainer, sparseArray, mContainerId);
                ensureContainer2.firstOut = fragment;
                ensureContainer2.firstOutIsPop = b;
                ensureContainer2.firstOutTransaction = backStackRecord;
                fragmentContainerTransition = ensureContainer2;
            }
            else {
                fragmentContainerTransition = ensureContainer;
            }
            if (!b2 && b3 && fragmentContainerTransition != null && fragmentContainerTransition.lastIn == fragment) {
                fragmentContainerTransition.lastIn = null;
            }
        }
    }
    
    public static void calculateFragments(final BackStackRecord backStackRecord, final SparseArray<FragmentContainerTransition> sparseArray, final boolean b) {
        for (int size = backStackRecord.mOps.size(), i = 0; i < size; ++i) {
            addToFirstInLastOut(backStackRecord, backStackRecord.mOps.get(i), sparseArray, false, b);
        }
    }
    
    private static ArrayMap<String, String> calculateNameOverrides(final int n, final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2, final int n2, int i) {
        final ArrayMap arrayMap = new ArrayMap();
        --i;
        while (i >= n2) {
            final BackStackRecord backStackRecord = list.get(i);
            if (backStackRecord.interactsWith(n)) {
                final boolean booleanValue = list2.get(i);
                if (backStackRecord.mSharedElementSourceNames != null) {
                    final int size = backStackRecord.mSharedElementSourceNames.size();
                    ArrayList<String> list3;
                    ArrayList<String> list4;
                    if (!booleanValue) {
                        list3 = backStackRecord.mSharedElementSourceNames;
                        list4 = backStackRecord.mSharedElementTargetNames;
                    }
                    else {
                        list4 = backStackRecord.mSharedElementSourceNames;
                        list3 = backStackRecord.mSharedElementTargetNames;
                    }
                    for (int j = 0; j < size; ++j) {
                        final String s = list3.get(j);
                        final String s2 = list4.get(j);
                        final String s3 = (String)arrayMap.remove(s2);
                        if (s3 == null) {
                            arrayMap.put(s, s2);
                        }
                        else {
                            arrayMap.put(s, s3);
                        }
                    }
                }
            }
            --i;
        }
        return arrayMap;
    }
    
    public static void calculatePopFragments(final BackStackRecord backStackRecord, final SparseArray<FragmentContainerTransition> sparseArray, final boolean b) {
        if (backStackRecord.mManager.mContainer.onHasView()) {
            for (int i = backStackRecord.mOps.size() - 1; i >= 0; --i) {
                addToFirstInLastOut(backStackRecord, backStackRecord.mOps.get(i), sparseArray, true, b);
            }
        }
    }
    
    private static void callSharedElementStartEnd(final Fragment fragment, final Fragment fragment2, final boolean b, final ArrayMap<String, View> arrayMap, final boolean b2) {
        int i = 0;
        SharedElementCallback sharedElementCallback;
        if (!b) {
            sharedElementCallback = fragment.getEnterTransitionCallback();
        }
        else {
            sharedElementCallback = fragment2.getEnterTransitionCallback();
        }
        if (sharedElementCallback != null) {
            final ArrayList<View> list = new ArrayList<View>();
            final ArrayList<String> list2 = new ArrayList<String>();
            int size;
            if (arrayMap != null) {
                size = arrayMap.size();
            }
            else {
                size = 0;
            }
            while (i < size) {
                list2.add((String)arrayMap.keyAt(i));
                list.add((View)arrayMap.valueAt(i));
                ++i;
            }
            if (!b2) {
                sharedElementCallback.onSharedElementEnd(list2, list, null);
            }
            else {
                sharedElementCallback.onSharedElementStart(list2, list, null);
            }
        }
    }
    
    @RequiresApi(21)
    private static ArrayMap<String, View> captureInSharedElements(final ArrayMap<String, String> arrayMap, final Object o, final FragmentContainerTransition fragmentContainerTransition) {
        final Fragment lastIn = fragmentContainerTransition.lastIn;
        final View view = lastIn.getView();
        if (!arrayMap.isEmpty() && o != null && view != null) {
            final ArrayMap arrayMap2 = new ArrayMap();
            FragmentTransitionCompat21.findNamedViews(arrayMap2, view);
            final BackStackRecord lastInTransaction = fragmentContainerTransition.lastInTransaction;
            SharedElementCallback sharedElementCallback;
            ArrayList<String> list;
            if (!fragmentContainerTransition.lastInIsPop) {
                sharedElementCallback = lastIn.getEnterTransitionCallback();
                list = lastInTransaction.mSharedElementTargetNames;
            }
            else {
                sharedElementCallback = lastIn.getExitTransitionCallback();
                list = lastInTransaction.mSharedElementSourceNames;
            }
            if (list != null) {
                arrayMap2.retainAll(list);
            }
            if (sharedElementCallback == null) {
                retainValues(arrayMap, arrayMap2);
            }
            else {
                sharedElementCallback.onMapSharedElements(list, arrayMap2);
                for (int i = list.size() - 1; i >= 0; --i) {
                    final String s = list.get(i);
                    final View view2 = (View)arrayMap2.get(s);
                    if (view2 != null) {
                        if (!s.equals(ViewCompat.getTransitionName(view2))) {
                            final String keyForValue = findKeyForValue(arrayMap, s);
                            if (keyForValue != null) {
                                arrayMap.put((Object)keyForValue, (Object)ViewCompat.getTransitionName(view2));
                            }
                        }
                    }
                    else {
                        final String keyForValue2 = findKeyForValue(arrayMap, s);
                        if (keyForValue2 != null) {
                            arrayMap.remove(keyForValue2);
                        }
                    }
                }
            }
            return arrayMap2;
        }
        arrayMap.clear();
        return null;
    }
    
    @RequiresApi(21)
    private static ArrayMap<String, View> captureOutSharedElements(final ArrayMap<String, String> arrayMap, final Object o, final FragmentContainerTransition fragmentContainerTransition) {
        if (!arrayMap.isEmpty() && o != null) {
            final Fragment firstOut = fragmentContainerTransition.firstOut;
            final ArrayMap arrayMap2 = new ArrayMap();
            FragmentTransitionCompat21.findNamedViews(arrayMap2, firstOut.getView());
            final BackStackRecord firstOutTransaction = fragmentContainerTransition.firstOutTransaction;
            SharedElementCallback sharedElementCallback;
            ArrayList<String> list;
            if (!fragmentContainerTransition.firstOutIsPop) {
                sharedElementCallback = firstOut.getExitTransitionCallback();
                list = firstOutTransaction.mSharedElementSourceNames;
            }
            else {
                sharedElementCallback = firstOut.getEnterTransitionCallback();
                list = firstOutTransaction.mSharedElementTargetNames;
            }
            arrayMap2.retainAll(list);
            if (sharedElementCallback == null) {
                arrayMap.retainAll(arrayMap2.keySet());
            }
            else {
                sharedElementCallback.onMapSharedElements(list, arrayMap2);
                for (int i = list.size() - 1; i >= 0; --i) {
                    final String s = list.get(i);
                    final View view = (View)arrayMap2.get(s);
                    if (view != null) {
                        if (!s.equals(ViewCompat.getTransitionName(view))) {
                            arrayMap.put((Object)ViewCompat.getTransitionName(view), (Object)arrayMap.remove(s));
                        }
                    }
                    else {
                        arrayMap.remove(s);
                    }
                }
            }
            return arrayMap2;
        }
        arrayMap.clear();
        return null;
    }
    
    @RequiresApi(21)
    private static ArrayList<View> configureEnteringExitingViews(final Object o, final Fragment fragment, final ArrayList<View> c, final View e) {
        final ArrayList<View> list = null;
        ArrayList<View> list2;
        if (o == null) {
            list2 = list;
        }
        else {
            final ArrayList list3 = new ArrayList();
            final View view = fragment.getView();
            if (view != null) {
                FragmentTransitionCompat21.captureTransitioningViews(list3, view);
            }
            if (c != null) {
                list3.removeAll(c);
            }
            list2 = list3;
            if (!list3.isEmpty()) {
                list3.add(e);
                FragmentTransitionCompat21.addTargets(o, list3);
                list2 = list3;
            }
        }
        return list2;
    }
    
    @RequiresApi(21)
    private static Object configureSharedElementsOrdered(final ViewGroup viewGroup, final View view, final ArrayMap<String, String> arrayMap, final FragmentContainerTransition fragmentContainerTransition, final ArrayList<View> list, final ArrayList<View> list2, final Object o, Object o2) {
        final Fragment lastIn = fragmentContainerTransition.lastIn;
        final Fragment firstOut = fragmentContainerTransition.firstOut;
        if (lastIn == null || firstOut == null) {
            return null;
        }
        final boolean lastInIsPop = fragmentContainerTransition.lastInIsPop;
        Object sharedElementTransition;
        if (!arrayMap.isEmpty()) {
            sharedElementTransition = getSharedElementTransition(lastIn, firstOut, lastInIsPop);
        }
        else {
            sharedElementTransition = null;
        }
        final ArrayMap<String, View> captureOutSharedElements = captureOutSharedElements(arrayMap, sharedElementTransition, fragmentContainerTransition);
        if (!arrayMap.isEmpty()) {
            list.addAll(captureOutSharedElements.values());
        }
        else {
            sharedElementTransition = null;
        }
        if (o == null && o2 == null && sharedElementTransition == null) {
            return null;
        }
        callSharedElementStartEnd(lastIn, firstOut, lastInIsPop, captureOutSharedElements, true);
        if (sharedElementTransition == null) {
            o2 = null;
        }
        else {
            final Rect rect = new Rect();
            FragmentTransitionCompat21.setSharedElementTargets(sharedElementTransition, view, list);
            setOutEpicenter(sharedElementTransition, o2, captureOutSharedElements, fragmentContainerTransition.firstOutIsPop, fragmentContainerTransition.firstOutTransaction);
            o2 = rect;
            if (o != null) {
                FragmentTransitionCompat21.setEpicenter(o, rect);
                o2 = rect;
            }
        }
        OneShotPreDrawListener.add((View)viewGroup, new Runnable() {
            @Override
            public void run() {
                final ArrayMap access$300 = captureInSharedElements(arrayMap, sharedElementTransition, fragmentContainerTransition);
                if (access$300 != null) {
                    list2.addAll(access$300.values());
                    list2.add(view);
                }
                callSharedElementStartEnd(lastIn, firstOut, lastInIsPop, access$300, false);
                if (sharedElementTransition != null) {
                    FragmentTransitionCompat21.swapSharedElementTargets(sharedElementTransition, list, list2);
                    final View access$301 = getInEpicenterView(access$300, fragmentContainerTransition, o, lastInIsPop);
                    if (access$301 != null) {
                        FragmentTransitionCompat21.getBoundsOnScreen(access$301, o2);
                    }
                }
            }
        });
        return sharedElementTransition;
    }
    
    @RequiresApi(21)
    private static Object configureSharedElementsReordered(final ViewGroup viewGroup, final View e, final ArrayMap<String, String> arrayMap, final FragmentContainerTransition fragmentContainerTransition, final ArrayList<View> list, final ArrayList<View> list2, final Object o, final Object o2) {
        final View view = null;
        final Fragment lastIn = fragmentContainerTransition.lastIn;
        final Fragment firstOut = fragmentContainerTransition.firstOut;
        if (lastIn != null) {
            lastIn.getView().setVisibility(0);
        }
        if (lastIn == null || firstOut == null) {
            return null;
        }
        final boolean lastInIsPop = fragmentContainerTransition.lastInIsPop;
        Object sharedElementTransition;
        if (!arrayMap.isEmpty()) {
            sharedElementTransition = getSharedElementTransition(lastIn, firstOut, lastInIsPop);
        }
        else {
            sharedElementTransition = null;
        }
        final ArrayMap<String, View> captureOutSharedElements = captureOutSharedElements(arrayMap, sharedElementTransition, fragmentContainerTransition);
        final ArrayMap<String, View> captureInSharedElements = captureInSharedElements(arrayMap, sharedElementTransition, fragmentContainerTransition);
        Object o3;
        if (!arrayMap.isEmpty()) {
            addSharedElementsWithMatchingNames(list, captureOutSharedElements, arrayMap.keySet());
            addSharedElementsWithMatchingNames(list2, captureInSharedElements, arrayMap.values());
            o3 = sharedElementTransition;
        }
        else {
            if (captureOutSharedElements != null) {
                captureOutSharedElements.clear();
            }
            if (captureInSharedElements == null) {
                o3 = null;
            }
            else {
                captureInSharedElements.clear();
                o3 = null;
            }
        }
        if (o == null && o2 == null && o3 == null) {
            return null;
        }
        callSharedElementStartEnd(lastIn, firstOut, lastInIsPop, captureOutSharedElements, true);
        Rect rect;
        View inEpicenterView;
        if (o3 == null) {
            rect = null;
            inEpicenterView = view;
        }
        else {
            list2.add(e);
            FragmentTransitionCompat21.setSharedElementTargets(o3, e, list);
            setOutEpicenter(o3, o2, captureOutSharedElements, fragmentContainerTransition.firstOutIsPop, fragmentContainerTransition.firstOutTransaction);
            final Rect rect2 = new Rect();
            final View view2 = inEpicenterView = getInEpicenterView(captureInSharedElements, fragmentContainerTransition, o, lastInIsPop);
            rect = rect2;
            if (view2 != null) {
                FragmentTransitionCompat21.setEpicenter(o, rect2);
                inEpicenterView = view2;
                rect = rect2;
            }
        }
        OneShotPreDrawListener.add((View)viewGroup, new Runnable() {
            @Override
            public void run() {
                callSharedElementStartEnd(lastIn, firstOut, lastInIsPop, captureInSharedElements, false);
                if (inEpicenterView != null) {
                    FragmentTransitionCompat21.getBoundsOnScreen(inEpicenterView, rect);
                }
            }
        });
        return o3;
    }
    
    @RequiresApi(21)
    private static void configureTransitionsOrdered(final FragmentManagerImpl fragmentManagerImpl, final int n, final FragmentContainerTransition fragmentContainerTransition, final View view, final ArrayMap<String, String> arrayMap) {
        final View view2 = null;
        ViewGroup viewGroup;
        if (!fragmentManagerImpl.mContainer.onHasView()) {
            viewGroup = (ViewGroup)view2;
        }
        else {
            viewGroup = (ViewGroup)fragmentManagerImpl.mContainer.onFindViewById(n);
        }
        if (viewGroup == null) {
            return;
        }
        final Fragment lastIn = fragmentContainerTransition.lastIn;
        final Fragment firstOut = fragmentContainerTransition.firstOut;
        final boolean lastInIsPop = fragmentContainerTransition.lastInIsPop;
        final boolean firstOutIsPop = fragmentContainerTransition.firstOutIsPop;
        final Object enterTransition = getEnterTransition(lastIn, lastInIsPop);
        Object exitTransition = getExitTransition(firstOut, firstOutIsPop);
        final ArrayList<View> list = new ArrayList<View>();
        final ArrayList<View> list2 = new ArrayList<View>();
        final Object configureSharedElementsOrdered = configureSharedElementsOrdered(viewGroup, view, arrayMap, fragmentContainerTransition, list, list2, enterTransition, exitTransition);
        if (enterTransition == null && configureSharedElementsOrdered == null && exitTransition == null) {
            return;
        }
        final ArrayList<View> configureEnteringExitingViews = configureEnteringExitingViews(exitTransition, firstOut, list, view);
        if (configureEnteringExitingViews == null || configureEnteringExitingViews.isEmpty()) {
            exitTransition = null;
        }
        FragmentTransitionCompat21.addTarget(enterTransition, view);
        final Object mergeTransitions = mergeTransitions(enterTransition, exitTransition, configureSharedElementsOrdered, lastIn, fragmentContainerTransition.lastInIsPop);
        if (mergeTransitions != null) {
            final ArrayList<View> list3 = new ArrayList<View>();
            FragmentTransitionCompat21.scheduleRemoveTargets(mergeTransitions, enterTransition, list3, exitTransition, configureEnteringExitingViews, configureSharedElementsOrdered, list2);
            scheduleTargetChange(viewGroup, lastIn, view, list2, enterTransition, list3, exitTransition, configureEnteringExitingViews);
            FragmentTransitionCompat21.setNameOverridesOrdered((View)viewGroup, list2, arrayMap);
            FragmentTransitionCompat21.beginDelayedTransition(viewGroup, mergeTransitions);
            FragmentTransitionCompat21.scheduleNameReset(viewGroup, list2, arrayMap);
        }
    }
    
    @RequiresApi(21)
    private static void configureTransitionsReordered(final FragmentManagerImpl fragmentManagerImpl, final int n, final FragmentContainerTransition fragmentContainerTransition, final View view, final ArrayMap<String, String> arrayMap) {
        final View view2 = null;
        Object o;
        if (!fragmentManagerImpl.mContainer.onHasView()) {
            o = view2;
        }
        else {
            o = fragmentManagerImpl.mContainer.onFindViewById(n);
        }
        if (o == null) {
            return;
        }
        final Fragment lastIn = fragmentContainerTransition.lastIn;
        final Fragment firstOut = fragmentContainerTransition.firstOut;
        final boolean lastInIsPop = fragmentContainerTransition.lastInIsPop;
        final boolean firstOutIsPop = fragmentContainerTransition.firstOutIsPop;
        final ArrayList<View> list = new ArrayList<View>();
        final ArrayList<View> list2 = new ArrayList<View>();
        final Object enterTransition = getEnterTransition(lastIn, lastInIsPop);
        final Object exitTransition = getExitTransition(firstOut, firstOutIsPop);
        final Object configureSharedElementsReordered = configureSharedElementsReordered((ViewGroup)o, view, arrayMap, fragmentContainerTransition, list2, list, enterTransition, exitTransition);
        if (enterTransition == null && configureSharedElementsReordered == null && exitTransition == null) {
            return;
        }
        final ArrayList<View> configureEnteringExitingViews = configureEnteringExitingViews(exitTransition, firstOut, list2, view);
        final ArrayList<View> configureEnteringExitingViews2 = configureEnteringExitingViews(enterTransition, lastIn, list, view);
        setViewVisibility(configureEnteringExitingViews2, 4);
        final Object mergeTransitions = mergeTransitions(enterTransition, exitTransition, configureSharedElementsReordered, lastIn, lastInIsPop);
        if (mergeTransitions != null) {
            replaceHide(exitTransition, firstOut, configureEnteringExitingViews);
            final ArrayList<String> prepareSetNameOverridesReordered = FragmentTransitionCompat21.prepareSetNameOverridesReordered(list);
            FragmentTransitionCompat21.scheduleRemoveTargets(mergeTransitions, enterTransition, configureEnteringExitingViews2, exitTransition, configureEnteringExitingViews, configureSharedElementsReordered, list);
            FragmentTransitionCompat21.beginDelayedTransition((ViewGroup)o, mergeTransitions);
            FragmentTransitionCompat21.setNameOverridesReordered((View)o, list2, list, prepareSetNameOverridesReordered, arrayMap);
            setViewVisibility(configureEnteringExitingViews2, 0);
            FragmentTransitionCompat21.swapSharedElementTargets(configureSharedElementsReordered, list2, list);
        }
    }
    
    private static FragmentContainerTransition ensureContainer(FragmentContainerTransition fragmentContainerTransition, final SparseArray<FragmentContainerTransition> sparseArray, final int n) {
        if (fragmentContainerTransition == null) {
            fragmentContainerTransition = new FragmentContainerTransition();
            sparseArray.put(n, (Object)fragmentContainerTransition);
        }
        return fragmentContainerTransition;
    }
    
    private static String findKeyForValue(final ArrayMap<String, String> arrayMap, final String s) {
        for (int i = 0; i < arrayMap.size(); ++i) {
            if (s.equals(arrayMap.valueAt(i))) {
                return (String)arrayMap.keyAt(i);
            }
        }
        return null;
    }
    
    @RequiresApi(21)
    private static Object getEnterTransition(final Fragment fragment, final boolean b) {
        if (fragment != null) {
            Object o;
            if (!b) {
                o = fragment.getEnterTransition();
            }
            else {
                o = fragment.getReenterTransition();
            }
            return FragmentTransitionCompat21.cloneTransition(o);
        }
        return null;
    }
    
    @RequiresApi(21)
    private static Object getExitTransition(final Fragment fragment, final boolean b) {
        if (fragment != null) {
            Object o;
            if (!b) {
                o = fragment.getExitTransition();
            }
            else {
                o = fragment.getReturnTransition();
            }
            return FragmentTransitionCompat21.cloneTransition(o);
        }
        return null;
    }
    
    private static View getInEpicenterView(final ArrayMap<String, View> arrayMap, final FragmentContainerTransition fragmentContainerTransition, final Object o, final boolean b) {
        final BackStackRecord lastInTransaction = fragmentContainerTransition.lastInTransaction;
        if (o != null && arrayMap != null && lastInTransaction.mSharedElementSourceNames != null && !lastInTransaction.mSharedElementSourceNames.isEmpty()) {
            String s;
            if (!b) {
                s = lastInTransaction.mSharedElementTargetNames.get(0);
            }
            else {
                s = lastInTransaction.mSharedElementSourceNames.get(0);
            }
            return arrayMap.get(s);
        }
        return null;
    }
    
    @RequiresApi(21)
    private static Object getSharedElementTransition(final Fragment fragment, final Fragment fragment2, final boolean b) {
        if (fragment != null && fragment2 != null) {
            Object o;
            if (!b) {
                o = fragment.getSharedElementEnterTransition();
            }
            else {
                o = fragment2.getSharedElementReturnTransition();
            }
            return FragmentTransitionCompat21.wrapTransitionInSet(FragmentTransitionCompat21.cloneTransition(o));
        }
        return null;
    }
    
    @RequiresApi(21)
    private static Object mergeTransitions(Object o, final Object o2, final Object o3, final Fragment fragment, final boolean b) {
        final boolean b2 = true;
        int n;
        if (o == null) {
            n = (b2 ? 1 : 0);
        }
        else {
            n = (b2 ? 1 : 0);
            if (o2 != null) {
                n = (b2 ? 1 : 0);
                if (fragment != null) {
                    if (!b) {
                        n = (fragment.getAllowEnterTransitionOverlap() ? 1 : 0);
                    }
                    else {
                        n = (fragment.getAllowReturnTransitionOverlap() ? 1 : 0);
                    }
                }
            }
        }
        if (n == 0) {
            o = FragmentTransitionCompat21.mergeTransitionsInSequence(o2, o, o3);
        }
        else {
            o = FragmentTransitionCompat21.mergeTransitionsTogether(o2, o, o3);
        }
        return o;
    }
    
    @RequiresApi(21)
    private static void replaceHide(final Object o, final Fragment fragment, final ArrayList<View> list) {
        if (fragment != null && o != null && fragment.mAdded && fragment.mHidden && fragment.mHiddenChanged) {
            fragment.setHideReplaced(true);
            FragmentTransitionCompat21.scheduleHideFragmentView(o, fragment.getView(), list);
            OneShotPreDrawListener.add((View)fragment.mContainer, new Runnable() {
                @Override
                public void run() {
                    setViewVisibility(list, 4);
                }
            });
        }
    }
    
    private static void retainValues(final ArrayMap<String, String> arrayMap, final ArrayMap<String, View> arrayMap2) {
        for (int i = arrayMap.size() - 1; i >= 0; --i) {
            if (!arrayMap2.containsKey(arrayMap.valueAt(i))) {
                arrayMap.removeAt(i);
            }
        }
    }
    
    @RequiresApi(21)
    private static void scheduleTargetChange(final ViewGroup viewGroup, final Fragment fragment, final View view, final ArrayList<View> list, final Object o, final ArrayList<View> list2, final Object o2, final ArrayList<View> list3) {
        OneShotPreDrawListener.add((View)viewGroup, new Runnable() {
            @Override
            public void run() {
                if (o != null) {
                    FragmentTransitionCompat21.removeTarget(o, view);
                    list2.addAll(configureEnteringExitingViews(o, fragment, list, view));
                }
                if (list3 != null) {
                    if (o2 != null) {
                        final ArrayList list = new ArrayList();
                        list.add(view);
                        FragmentTransitionCompat21.replaceTargets(o2, list3, list);
                    }
                    list3.clear();
                    list3.add(view);
                }
            }
        });
    }
    
    @RequiresApi(21)
    private static void setOutEpicenter(final Object o, final Object o2, final ArrayMap<String, View> arrayMap, final boolean b, final BackStackRecord backStackRecord) {
        if (backStackRecord.mSharedElementSourceNames != null && !backStackRecord.mSharedElementSourceNames.isEmpty()) {
            String s;
            if (!b) {
                s = backStackRecord.mSharedElementSourceNames.get(0);
            }
            else {
                s = backStackRecord.mSharedElementTargetNames.get(0);
            }
            final View view = arrayMap.get(s);
            FragmentTransitionCompat21.setEpicenter(o, view);
            if (o2 != null) {
                FragmentTransitionCompat21.setEpicenter(o2, view);
            }
        }
    }
    
    private static void setViewVisibility(final ArrayList<View> list, final int visibility) {
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; --i) {
                ((View)list.get(i)).setVisibility(visibility);
            }
        }
    }
    
    static void startTransitions(final FragmentManagerImpl fragmentManagerImpl, final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2, final int n, final int n2, final boolean b) {
        if (fragmentManagerImpl.mCurState >= 1) {
            if (Build$VERSION.SDK_INT >= 21) {
                final SparseArray sparseArray = new SparseArray();
                for (int i = n; i < n2; ++i) {
                    final BackStackRecord backStackRecord = list.get(i);
                    if (!list2.get(i)) {
                        calculateFragments(backStackRecord, (SparseArray<FragmentContainerTransition>)sparseArray, b);
                    }
                    else {
                        calculatePopFragments(backStackRecord, (SparseArray<FragmentContainerTransition>)sparseArray, b);
                    }
                }
                if (sparseArray.size() != 0) {
                    final View view = new View(fragmentManagerImpl.mHost.getContext());
                    for (int size = sparseArray.size(), j = 0; j < size; ++j) {
                        final int key = sparseArray.keyAt(j);
                        final ArrayMap<String, String> calculateNameOverrides = calculateNameOverrides(key, list, list2, n, n2);
                        final FragmentContainerTransition fragmentContainerTransition = (FragmentContainerTransition)sparseArray.valueAt(j);
                        if (!b) {
                            configureTransitionsOrdered(fragmentManagerImpl, key, fragmentContainerTransition, view, calculateNameOverrides);
                        }
                        else {
                            configureTransitionsReordered(fragmentManagerImpl, key, fragmentContainerTransition, view, calculateNameOverrides);
                        }
                    }
                }
            }
        }
    }
    
    static class FragmentContainerTransition
    {
        public Fragment firstOut;
        public boolean firstOutIsPop;
        public BackStackRecord firstOutTransaction;
        public Fragment lastIn;
        public boolean lastInIsPop;
        public BackStackRecord lastInTransaction;
    }
}
