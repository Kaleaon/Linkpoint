// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import android.transition.Transition$EpicenterCallback;
import android.transition.Transition$TransitionListener;
import java.util.Collection;
import android.graphics.Rect;
import java.util.Iterator;
import java.util.List;
import android.transition.TransitionManager;
import android.view.ViewGroup;
import android.transition.TransitionSet;
import java.util.ArrayList;
import android.transition.Transition;
import android.view.View;
import java.util.Map;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
class FragmentTransitionCompat21
{
    public static void addTarget(final Object o, final View view) {
        if (o != null) {
            ((Transition)o).addTarget(view);
        }
    }
    
    public static void addTargets(final Object o, final ArrayList<View> list) {
        int i = 0;
        final Transition transition = (Transition)o;
        if (transition != null) {
            if (!(transition instanceof TransitionSet)) {
                if (!hasSimpleTarget(transition) && isNullOrEmpty(transition.getTargets())) {
                    for (int size = list.size(), j = 0; j < size; ++j) {
                        transition.addTarget((View)list.get(j));
                    }
                }
            }
            else {
                for (TransitionSet set = (TransitionSet)transition; i < set.getTransitionCount(); ++i) {
                    addTargets(set.getTransitionAt(i), list);
                }
            }
        }
    }
    
    public static void beginDelayedTransition(final ViewGroup viewGroup, final Object o) {
        TransitionManager.beginDelayedTransition(viewGroup, (Transition)o);
    }
    
    private static void bfsAddViewChildren(final List<View> list, View child) {
        final int size = list.size();
        if (!containedBeforeIndex(list, child, size)) {
            list.add(child);
            for (int i = size; i < list.size(); ++i) {
                child = (View)list.get(i);
                if (child instanceof ViewGroup) {
                    final ViewGroup viewGroup = (ViewGroup)child;
                    for (int childCount = viewGroup.getChildCount(), j = 0; j < childCount; ++j) {
                        child = viewGroup.getChildAt(j);
                        if (!containedBeforeIndex(list, child, size)) {
                            list.add(child);
                        }
                    }
                }
            }
        }
    }
    
    public static void captureTransitioningViews(final ArrayList<View> list, final View e) {
        int i = 0;
        if (e.getVisibility() == 0) {
            if (!(e instanceof ViewGroup)) {
                list.add(e);
            }
            else {
                final ViewGroup e2 = (ViewGroup)e;
                if (!e2.isTransitionGroup()) {
                    while (i < e2.getChildCount()) {
                        captureTransitioningViews(list, e2.getChildAt(i));
                        ++i;
                    }
                }
                else {
                    list.add((View)e2);
                }
            }
        }
    }
    
    public static Object cloneTransition(final Object o) {
        final Object o2 = null;
        Object clone;
        if (o == null) {
            clone = o2;
        }
        else {
            clone = ((Transition)o).clone();
        }
        return clone;
    }
    
    private static boolean containedBeforeIndex(final List<View> list, final View view, final int n) {
        for (int i = 0; i < n; ++i) {
            if (list.get(i) == view) {
                return true;
            }
        }
        return false;
    }
    
    private static String findKeyForValue(final Map<String, String> map, final String s) {
        for (final Map.Entry<K, Object> entry : map.entrySet()) {
            if (s.equals(entry.getValue())) {
                return (String)entry.getKey();
            }
        }
        return null;
    }
    
    public static void findNamedViews(final Map<String, View> map, final View view) {
        int i = 0;
        if (view.getVisibility() == 0) {
            final String transitionName = view.getTransitionName();
            if (transitionName != null) {
                map.put(transitionName, view);
            }
            if (view instanceof ViewGroup) {
                for (ViewGroup viewGroup = (ViewGroup)view; i < viewGroup.getChildCount(); ++i) {
                    findNamedViews(map, viewGroup.getChildAt(i));
                }
            }
        }
    }
    
    public static void getBoundsOnScreen(final View view, final Rect rect) {
        final int[] array = new int[2];
        view.getLocationOnScreen(array);
        rect.set(array[0], array[1], array[0] + view.getWidth(), array[1] + view.getHeight());
    }
    
    private static boolean hasSimpleTarget(final Transition transition) {
        boolean b = false;
        if (!isNullOrEmpty(transition.getTargetIds()) || !isNullOrEmpty(transition.getTargetNames()) || !isNullOrEmpty(transition.getTargetTypes())) {
            b = true;
        }
        return b;
    }
    
    private static boolean isNullOrEmpty(final List list) {
        boolean b = false;
        if (list == null || list.isEmpty()) {
            b = true;
        }
        return b;
    }
    
    public static Object mergeTransitionsInSequence(final Object o, Object o2, final Object o3) {
        Object setOrdering = o;
        final Transition transition = (Transition)o2;
        final Transition transition2 = (Transition)o3;
        if (setOrdering != null && transition != null) {
            setOrdering = new TransitionSet().addTransition((Transition)setOrdering).addTransition(transition).setOrdering(1);
        }
        else if (setOrdering == null) {
            if ((setOrdering = transition) == null) {
                setOrdering = null;
            }
        }
        if (transition2 == null) {
            return setOrdering;
        }
        o2 = new TransitionSet();
        if (setOrdering != null) {
            ((TransitionSet)o2).addTransition((Transition)setOrdering);
        }
        ((TransitionSet)o2).addTransition(transition2);
        return o2;
    }
    
    public static Object mergeTransitionsTogether(final Object o, final Object o2, final Object o3) {
        final TransitionSet set = new TransitionSet();
        if (o != null) {
            set.addTransition((Transition)o);
        }
        if (o2 != null) {
            set.addTransition((Transition)o2);
        }
        if (o3 != null) {
            set.addTransition((Transition)o3);
        }
        return set;
    }
    
    public static ArrayList<String> prepareSetNameOverridesReordered(final ArrayList<View> list) {
        final ArrayList list2 = new ArrayList();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final View view = list.get(i);
            list2.add(view.getTransitionName());
            view.setTransitionName((String)null);
        }
        return list2;
    }
    
    public static void removeTarget(final Object o, final View view) {
        if (o != null) {
            ((Transition)o).removeTarget(view);
        }
    }
    
    public static void replaceTargets(final Object o, final ArrayList<View> list, final ArrayList<View> list2) {
        int i = 0;
        int j = 0;
        final Transition transition = (Transition)o;
        if (!(transition instanceof TransitionSet)) {
            if (!hasSimpleTarget(transition)) {
                final List targets = transition.getTargets();
                if (targets != null && targets.size() == list.size() && targets.containsAll(list)) {
                    int size;
                    if (list2 != null) {
                        size = list2.size();
                    }
                    else {
                        size = 0;
                    }
                    while (i < size) {
                        transition.addTarget((View)list2.get(i));
                        ++i;
                    }
                    for (int k = list.size() - 1; k >= 0; --k) {
                        transition.removeTarget((View)list.get(k));
                    }
                }
            }
        }
        else {
            for (TransitionSet set = (TransitionSet)transition; j < set.getTransitionCount(); ++j) {
                replaceTargets(set.getTransitionAt(j), list, list2);
            }
        }
    }
    
    public static void scheduleHideFragmentView(final Object o, final View view, final ArrayList<View> list) {
        ((Transition)o).addListener((Transition$TransitionListener)new Transition$TransitionListener() {
            public void onTransitionCancel(final Transition transition) {
            }
            
            public void onTransitionEnd(final Transition transition) {
                transition.removeListener((Transition$TransitionListener)this);
                view.setVisibility(8);
                for (int size = list.size(), i = 0; i < size; ++i) {
                    ((View)list.get(i)).setVisibility(0);
                }
            }
            
            public void onTransitionPause(final Transition transition) {
            }
            
            public void onTransitionResume(final Transition transition) {
            }
            
            public void onTransitionStart(final Transition transition) {
            }
        });
    }
    
    public static void scheduleNameReset(final ViewGroup viewGroup, final ArrayList<View> list, final Map<String, String> map) {
        OneShotPreDrawListener.add((View)viewGroup, new Runnable() {
            @Override
            public void run() {
                for (int size = list.size(), i = 0; i < size; ++i) {
                    final View view = list.get(i);
                    view.setTransitionName((String)map.get(view.getTransitionName()));
                }
            }
        });
    }
    
    public static void scheduleRemoveTargets(final Object o, final Object o2, final ArrayList<View> list, final Object o3, final ArrayList<View> list2, final Object o4, final ArrayList<View> list3) {
        ((Transition)o).addListener((Transition$TransitionListener)new Transition$TransitionListener() {
            public void onTransitionCancel(final Transition transition) {
            }
            
            public void onTransitionEnd(final Transition transition) {
            }
            
            public void onTransitionPause(final Transition transition) {
            }
            
            public void onTransitionResume(final Transition transition) {
            }
            
            public void onTransitionStart(final Transition transition) {
                if (o2 != null) {
                    FragmentTransitionCompat21.replaceTargets(o2, list, null);
                }
                if (o3 != null) {
                    FragmentTransitionCompat21.replaceTargets(o3, list2, null);
                }
                if (o4 != null) {
                    FragmentTransitionCompat21.replaceTargets(o4, list3, null);
                }
            }
        });
    }
    
    public static void setEpicenter(final Object o, final Rect rect) {
        if (o != null) {
            ((Transition)o).setEpicenterCallback((Transition$EpicenterCallback)new Transition$EpicenterCallback() {
                public Rect onGetEpicenter(final Transition transition) {
                    if (rect != null && !rect.isEmpty()) {
                        return rect;
                    }
                    return null;
                }
            });
        }
    }
    
    public static void setEpicenter(final Object o, final View view) {
        if (view != null) {
            final Transition transition = (Transition)o;
            final Rect rect = new Rect();
            getBoundsOnScreen(view, rect);
            transition.setEpicenterCallback((Transition$EpicenterCallback)new Transition$EpicenterCallback() {
                public Rect onGetEpicenter(final Transition transition) {
                    return rect;
                }
            });
        }
    }
    
    public static void setNameOverridesOrdered(final View view, final ArrayList<View> list, final Map<String, String> map) {
        OneShotPreDrawListener.add(view, new Runnable() {
            @Override
            public void run() {
                for (int size = list.size(), i = 0; i < size; ++i) {
                    final View view = list.get(i);
                    final String transitionName = view.getTransitionName();
                    if (transitionName != null) {
                        view.setTransitionName(findKeyForValue(map, transitionName));
                    }
                }
            }
        });
    }
    
    public static void setNameOverridesReordered(final View view, final ArrayList<View> list, final ArrayList<View> list2, final ArrayList<String> list3, final Map<String, String> map) {
        final int size = list2.size();
        final ArrayList list4 = new ArrayList();
        for (int i = 0; i < size; ++i) {
            final View view2 = list.get(i);
            final String transitionName = view2.getTransitionName();
            list4.add(transitionName);
            if (transitionName != null) {
                view2.setTransitionName((String)null);
                final String s = map.get(transitionName);
                for (int j = 0; j < size; ++j) {
                    if (s.equals(list3.get(j))) {
                        ((View)list2.get(j)).setTransitionName(transitionName);
                        break;
                    }
                }
            }
        }
        OneShotPreDrawListener.add(view, new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < size; ++i) {
                    ((View)list2.get(i)).setTransitionName((String)list3.get(i));
                    ((View)list.get(i)).setTransitionName((String)list4.get(i));
                }
            }
        });
    }
    
    public static void setSharedElementTargets(final Object o, final View e, final ArrayList<View> list) {
        final TransitionSet set = (TransitionSet)o;
        final List targets = set.getTargets();
        targets.clear();
        for (int size = list.size(), i = 0; i < size; ++i) {
            bfsAddViewChildren(targets, (View)list.get(i));
        }
        targets.add(e);
        list.add(e);
        addTargets(set, list);
    }
    
    public static void swapSharedElementTargets(final Object o, final ArrayList<View> list, final ArrayList<View> list2) {
        final TransitionSet set = (TransitionSet)o;
        if (set != null) {
            set.getTargets().clear();
            set.getTargets().addAll(list2);
            replaceTargets(set, list, list2);
        }
    }
    
    public static Object wrapTransitionInSet(final Object o) {
        if (o != null) {
            final TransitionSet set = new TransitionSet();
            set.addTransition((Transition)o);
            return set;
        }
        return null;
    }
}
