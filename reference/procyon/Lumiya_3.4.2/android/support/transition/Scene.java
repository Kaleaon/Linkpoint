// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.util.SparseArray;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.view.View;
import android.content.Context;

public class Scene
{
    private Context mContext;
    private Runnable mEnterAction;
    private Runnable mExitAction;
    private View mLayout;
    private int mLayoutId;
    private ViewGroup mSceneRoot;
    
    public Scene(@NonNull final ViewGroup mSceneRoot) {
        this.mLayoutId = -1;
        this.mSceneRoot = mSceneRoot;
    }
    
    private Scene(final ViewGroup mSceneRoot, final int mLayoutId, final Context mContext) {
        this.mLayoutId = -1;
        this.mContext = mContext;
        this.mSceneRoot = mSceneRoot;
        this.mLayoutId = mLayoutId;
    }
    
    public Scene(@NonNull final ViewGroup mSceneRoot, @NonNull final View mLayout) {
        this.mLayoutId = -1;
        this.mSceneRoot = mSceneRoot;
        this.mLayout = mLayout;
    }
    
    static Scene getCurrentScene(final View view) {
        return (Scene)view.getTag(R.id.transition_current_scene);
    }
    
    @NonNull
    public static Scene getSceneForLayout(@NonNull final ViewGroup viewGroup, @LayoutRes final int n, @NonNull final Context context) {
        SparseArray sparseArray = (SparseArray)viewGroup.getTag(R.id.transition_scene_layoutid_cache);
        if (sparseArray == null) {
            sparseArray = new SparseArray();
            viewGroup.setTag(R.id.transition_scene_layoutid_cache, (Object)sparseArray);
        }
        final Scene scene = (Scene)sparseArray.get(n);
        if (scene == null) {
            final Scene scene2 = new Scene(viewGroup, n, context);
            sparseArray.put(n, (Object)scene2);
            return scene2;
        }
        return scene;
    }
    
    static void setCurrentScene(final View view, final Scene scene) {
        view.setTag(R.id.transition_current_scene, (Object)scene);
    }
    
    public void enter() {
        if (this.mLayoutId > 0 || this.mLayout != null) {
            this.getSceneRoot().removeAllViews();
            if (this.mLayoutId <= 0) {
                this.mSceneRoot.addView(this.mLayout);
            }
            else {
                LayoutInflater.from(this.mContext).inflate(this.mLayoutId, this.mSceneRoot);
            }
        }
        if (this.mEnterAction != null) {
            this.mEnterAction.run();
        }
        setCurrentScene((View)this.mSceneRoot, this);
    }
    
    public void exit() {
        if (getCurrentScene((View)this.mSceneRoot) == this && this.mExitAction != null) {
            this.mExitAction.run();
        }
    }
    
    @NonNull
    public ViewGroup getSceneRoot() {
        return this.mSceneRoot;
    }
    
    boolean isCreatedFromLayoutResource() {
        boolean b = false;
        if (this.mLayoutId > 0) {
            b = true;
        }
        return b;
    }
    
    public void setEnterAction(@Nullable final Runnable mEnterAction) {
        this.mEnterAction = mEnterAction;
    }
    
    public void setExitAction(@Nullable final Runnable mExitAction) {
        this.mExitAction = mExitAction;
    }
}
