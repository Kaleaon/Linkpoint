// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import java.util.Iterator;
import android.view.ViewTreeObserver$OnPreDrawListener;
import android.view.View$OnAttachStateChangeListener;
import java.util.Collection;
import android.view.View;
import android.support.v4.view.ViewCompat;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import java.lang.ref.WeakReference;
import android.view.ViewGroup;
import java.util.ArrayList;

public class TransitionManager
{
    private static final String LOG_TAG = "TransitionManager";
    private static Transition sDefaultTransition;
    private static ArrayList<ViewGroup> sPendingTransitions;
    private static ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>> sRunningTransitions;
    private ArrayMap<Scene, ArrayMap<Scene, Transition>> mScenePairTransitions;
    private ArrayMap<Scene, Transition> mSceneTransitions;
    
    static {
        TransitionManager.sDefaultTransition = new AutoTransition();
        TransitionManager.sRunningTransitions = new ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>>();
        TransitionManager.sPendingTransitions = new ArrayList<ViewGroup>();
    }
    
    public TransitionManager() {
        this.mSceneTransitions = new ArrayMap<Scene, Transition>();
        this.mScenePairTransitions = new ArrayMap<Scene, ArrayMap<Scene, Transition>>();
    }
    
    public static void beginDelayedTransition(@NonNull final ViewGroup viewGroup) {
        beginDelayedTransition(viewGroup, null);
    }
    
    public static void beginDelayedTransition(@NonNull final ViewGroup viewGroup, @Nullable Transition transition) {
        if (!TransitionManager.sPendingTransitions.contains(viewGroup) && ViewCompat.isLaidOut((View)viewGroup)) {
            TransitionManager.sPendingTransitions.add(viewGroup);
            if (transition == null) {
                transition = TransitionManager.sDefaultTransition;
            }
            transition = transition.clone();
            sceneChangeSetup(viewGroup, transition);
            Scene.setCurrentScene((View)viewGroup, null);
            sceneChangeRunTransition(viewGroup, transition);
        }
    }
    
    private static void changeScene(final Scene scene, final Transition transition) {
        final ViewGroup sceneRoot = scene.getSceneRoot();
        if (!TransitionManager.sPendingTransitions.contains(sceneRoot)) {
            if (transition != null) {
                TransitionManager.sPendingTransitions.add(sceneRoot);
                final Transition clone = transition.clone();
                clone.setSceneRoot(sceneRoot);
                final Scene currentScene = Scene.getCurrentScene((View)sceneRoot);
                if (currentScene != null && currentScene.isCreatedFromLayoutResource()) {
                    clone.setCanRemoveViews(true);
                }
                sceneChangeSetup(sceneRoot, clone);
                scene.enter();
                sceneChangeRunTransition(sceneRoot, clone);
            }
            else {
                scene.enter();
            }
        }
    }
    
    public static void endTransitions(final ViewGroup o) {
        TransitionManager.sPendingTransitions.remove(o);
        final ArrayList c = getRunningTransitions().get(o);
        if (c != null && !c.isEmpty()) {
            final ArrayList list = new ArrayList(c);
            for (int i = list.size() - 1; i >= 0; --i) {
                ((Transition)list.get(i)).forceToEnd(o);
            }
        }
    }
    
    static ArrayMap<ViewGroup, ArrayList<Transition>> getRunningTransitions() {
        WeakReference value = TransitionManager.sRunningTransitions.get();
        if (value == null || value.get() == null) {
            value = new WeakReference(new ArrayMap());
            TransitionManager.sRunningTransitions.set(value);
        }
        return (ArrayMap)value.get();
    }
    
    private Transition getTransition(final Scene scene) {
        final ViewGroup sceneRoot = scene.getSceneRoot();
        if (sceneRoot != null) {
            final Scene currentScene = Scene.getCurrentScene((View)sceneRoot);
            if (currentScene != null) {
                final ArrayMap arrayMap = this.mScenePairTransitions.get(scene);
                if (arrayMap != null) {
                    final Transition transition = (Transition)arrayMap.get(currentScene);
                    if (transition != null) {
                        return transition;
                    }
                }
            }
        }
        Transition sDefaultTransition;
        if ((sDefaultTransition = this.mSceneTransitions.get(scene)) == null) {
            sDefaultTransition = TransitionManager.sDefaultTransition;
        }
        return sDefaultTransition;
    }
    
    public static void go(@NonNull final Scene scene) {
        changeScene(scene, TransitionManager.sDefaultTransition);
    }
    
    public static void go(@NonNull final Scene scene, @Nullable final Transition transition) {
        changeScene(scene, transition);
    }
    
    private static void sceneChangeRunTransition(final ViewGroup viewGroup, final Transition transition) {
        if (transition != null && viewGroup != null) {
            final MultiListener multiListener = new MultiListener(transition, viewGroup);
            viewGroup.addOnAttachStateChangeListener((View$OnAttachStateChangeListener)multiListener);
            viewGroup.getViewTreeObserver().addOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)multiListener);
        }
    }
    
    private static void sceneChangeSetup(final ViewGroup viewGroup, final Transition transition) {
        final ArrayList list = getRunningTransitions().get(viewGroup);
        if (list != null && list.size() > 0) {
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                ((Transition)iterator.next()).pause((View)viewGroup);
            }
        }
        if (transition != null) {
            transition.captureValues(viewGroup, true);
        }
        final Scene currentScene = Scene.getCurrentScene((View)viewGroup);
        if (currentScene != null) {
            currentScene.exit();
        }
    }
    
    public void setTransition(@NonNull final Scene scene, @NonNull final Scene scene2, @Nullable final Transition transition) {
        final ArrayMap arrayMap = this.mScenePairTransitions.get(scene2);
        ArrayMap arrayMap2;
        if (arrayMap != null) {
            arrayMap2 = arrayMap;
        }
        else {
            final ArrayMap arrayMap3 = new ArrayMap();
            this.mScenePairTransitions.put(scene2, arrayMap3);
            arrayMap2 = arrayMap3;
        }
        arrayMap2.put(scene, transition);
    }
    
    public void setTransition(@NonNull final Scene scene, @Nullable final Transition transition) {
        this.mSceneTransitions.put(scene, transition);
    }
    
    public void transitionTo(@NonNull final Scene scene) {
        changeScene(scene, this.getTransition(scene));
    }
    
    private static class MultiListener implements ViewTreeObserver$OnPreDrawListener, View$OnAttachStateChangeListener
    {
        ViewGroup mSceneRoot;
        Transition mTransition;
        
        MultiListener(final Transition mTransition, final ViewGroup mSceneRoot) {
            this.mTransition = mTransition;
            this.mSceneRoot = mSceneRoot;
        }
        
        private void removeListeners() {
            this.mSceneRoot.getViewTreeObserver().removeOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)this);
            this.mSceneRoot.removeOnAttachStateChangeListener((View$OnAttachStateChangeListener)this);
        }
        
        public boolean onPreDraw() {
            this.removeListeners();
            if (TransitionManager.sPendingTransitions.remove(this.mSceneRoot)) {
                final ArrayMap<ViewGroup, ArrayList<Transition>> runningTransitions = TransitionManager.getRunningTransitions();
                ArrayList c = runningTransitions.get(this.mSceneRoot);
                ArrayList list;
                if (c != null) {
                    if (c.size() <= 0) {
                        list = null;
                    }
                    else {
                        list = new ArrayList(c);
                    }
                }
                else {
                    c = new ArrayList();
                    runningTransitions.put(this.mSceneRoot, c);
                    list = null;
                }
                c.add(this.mTransition);
                this.mTransition.addListener((Transition.TransitionListener)new TransitionListenerAdapter() {
                    @Override
                    public void onTransitionEnd(@NonNull final Transition o) {
                        ((ArrayList)runningTransitions.get(MultiListener.this.mSceneRoot)).remove(o);
                    }
                });
                this.mTransition.captureValues(this.mSceneRoot, false);
                if (list != null) {
                    final Iterator iterator = list.iterator();
                    while (iterator.hasNext()) {
                        ((Transition)iterator.next()).resume((View)this.mSceneRoot);
                    }
                }
                this.mTransition.playTransition(this.mSceneRoot);
                return true;
            }
            return true;
        }
        
        public void onViewAttachedToWindow(final View view) {
        }
        
        public void onViewDetachedFromWindow(final View view) {
            this.removeListeners();
            TransitionManager.sPendingTransitions.remove(this.mSceneRoot);
            final ArrayList list = TransitionManager.getRunningTransitions().get(this.mSceneRoot);
            if (list != null && list.size() > 0) {
                final Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    ((Transition)iterator.next()).resume((View)this.mSceneRoot);
                }
            }
            this.mTransition.clearValues(true);
        }
    }
}
