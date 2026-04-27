// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.animation;

import android.view.animation.Interpolator;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;

public final class AnimatorSet extends Animator
{
    private ValueAnimator mDelayAnim;
    private long mDuration;
    private boolean mNeedsSort;
    private HashMap<Animator, Node> mNodeMap;
    private ArrayList<Node> mNodes;
    private ArrayList<Animator> mPlayingSet;
    private AnimatorSetListener mSetListener;
    private ArrayList<Node> mSortedNodes;
    private long mStartDelay;
    private boolean mStarted;
    boolean mTerminated;
    
    public AnimatorSet() {
        this.mPlayingSet = new ArrayList<Animator>();
        this.mNodeMap = new HashMap<Animator, Node>();
        this.mNodes = new ArrayList<Node>();
        this.mSortedNodes = new ArrayList<Node>();
        this.mNeedsSort = true;
        this.mSetListener = null;
        this.mTerminated = false;
        this.mStarted = false;
        this.mStartDelay = 0L;
        this.mDelayAnim = null;
        this.mDuration = -1L;
    }
    
    private void sortNodes() {
        if (!this.mNeedsSort) {
            for (int size = this.mNodes.size(), i = 0; i < size; ++i) {
                final Node node = this.mNodes.get(i);
                if (node.dependencies != null && node.dependencies.size() > 0) {
                    for (int size2 = node.dependencies.size(), j = 0; j < size2; ++j) {
                        final Dependency dependency = node.dependencies.get(j);
                        if (node.nodeDependencies == null) {
                            node.nodeDependencies = new ArrayList<Node>();
                        }
                        if (!node.nodeDependencies.contains(dependency.node)) {
                            node.nodeDependencies.add(dependency.node);
                        }
                    }
                }
                node.done = false;
            }
        }
        else {
            this.mSortedNodes.clear();
            final ArrayList list = new ArrayList();
            for (int size3 = this.mNodes.size(), k = 0; k < size3; ++k) {
                final Node e = this.mNodes.get(k);
                if (e.dependencies == null || e.dependencies.size() == 0) {
                    list.add(e);
                }
            }
            final ArrayList<Node> c = new ArrayList<Node>();
            while (list.size() > 0) {
                for (int size4 = list.size(), l = 0; l < size4; ++l) {
                    final Node node2 = list.get(l);
                    this.mSortedNodes.add(node2);
                    if (node2.nodeDependents != null) {
                        for (int size5 = node2.nodeDependents.size(), index = 0; index < size5; ++index) {
                            final Node e2 = node2.nodeDependents.get(index);
                            e2.nodeDependencies.remove(node2);
                            if (e2.nodeDependencies.size() == 0) {
                                c.add(e2);
                            }
                        }
                    }
                }
                list.clear();
                list.addAll(c);
                c.clear();
            }
            this.mNeedsSort = false;
            if (this.mSortedNodes.size() != this.mNodes.size()) {
                throw new IllegalStateException("Circular dependencies cannot exist in AnimatorSet");
            }
        }
    }
    
    @Override
    public void cancel() {
        this.mTerminated = true;
        if (this.isStarted()) {
            ArrayList list;
            if (this.mListeners == null) {
                list = null;
            }
            else {
                list = (ArrayList)this.mListeners.clone();
                final Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    ((AnimatorListener)iterator.next()).onAnimationCancel(this);
                }
            }
            if (this.mDelayAnim != null && this.mDelayAnim.isRunning()) {
                this.mDelayAnim.cancel();
            }
            else if (this.mSortedNodes.size() > 0) {
                final Iterator<Node> iterator2 = this.mSortedNodes.iterator();
                while (iterator2.hasNext()) {
                    iterator2.next().animation.cancel();
                }
            }
            if (list != null) {
                final Iterator iterator3 = list.iterator();
                while (iterator3.hasNext()) {
                    ((AnimatorListener)iterator3.next()).onAnimationEnd(this);
                }
            }
            this.mStarted = false;
        }
    }
    
    @Override
    public AnimatorSet clone() {
        final AnimatorSet set = (AnimatorSet)super.clone();
        set.mNeedsSort = true;
        set.mTerminated = false;
        set.mStarted = false;
        set.mPlayingSet = new ArrayList<Animator>();
        set.mNodeMap = new HashMap<Animator, Node>();
        set.mNodes = new ArrayList<Node>();
        set.mSortedNodes = new ArrayList<Node>();
        final HashMap hashMap = new HashMap();
        for (final Node key : this.mNodes) {
            final Node clone = key.clone();
            hashMap.put(key, clone);
            set.mNodes.add(clone);
            set.mNodeMap.put(clone.animation, clone);
            clone.dependencies = null;
            clone.tmpDependencies = null;
            clone.nodeDependents = null;
            clone.nodeDependencies = null;
            final ArrayList<AnimatorListener> listeners = clone.animation.getListeners();
            if (listeners != null) {
                final Iterator<AnimatorListener> iterator2 = listeners.iterator();
                ArrayList<AnimatorListener> list = null;
                while (iterator2.hasNext()) {
                    final AnimatorListener e = iterator2.next();
                    if (!(e instanceof AnimatorSetListener)) {
                        continue;
                    }
                    if (list == null) {
                        list = new ArrayList<AnimatorListener>();
                    }
                    list.add(e);
                }
                if (list == null) {
                    continue;
                }
                final Iterator<AnimatorListener> iterator3 = list.iterator();
                while (iterator3.hasNext()) {
                    listeners.remove(iterator3.next());
                }
            }
        }
        for (final Node key2 : this.mNodes) {
            final Node node = hashMap.get(key2);
            if (key2.dependencies != null) {
                for (final Dependency dependency : key2.dependencies) {
                    node.addDependency(new Dependency((Node)hashMap.get(dependency.node), dependency.rule));
                }
            }
        }
        return set;
    }
    
    @Override
    public void end() {
        this.mTerminated = true;
        if (this.isStarted()) {
            if (this.mSortedNodes.size() != this.mNodes.size()) {
                this.sortNodes();
                for (final Node node : this.mSortedNodes) {
                    if (this.mSetListener == null) {
                        this.mSetListener = new AnimatorSetListener(this);
                    }
                    node.animation.addListener((AnimatorListener)this.mSetListener);
                }
            }
            if (this.mDelayAnim != null) {
                this.mDelayAnim.cancel();
            }
            if (this.mSortedNodes.size() > 0) {
                final Iterator<Node> iterator2 = this.mSortedNodes.iterator();
                while (iterator2.hasNext()) {
                    iterator2.next().animation.end();
                }
            }
            if (this.mListeners != null) {
                final Iterator iterator3 = ((ArrayList)this.mListeners.clone()).iterator();
                while (iterator3.hasNext()) {
                    ((AnimatorListener)iterator3.next()).onAnimationEnd(this);
                }
            }
            this.mStarted = false;
        }
    }
    
    public ArrayList<Animator> getChildAnimations() {
        final ArrayList list = new ArrayList();
        final Iterator<Node> iterator = this.mNodes.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().animation);
        }
        return list;
    }
    
    @Override
    public long getDuration() {
        return this.mDuration;
    }
    
    @Override
    public long getStartDelay() {
        return this.mStartDelay;
    }
    
    @Override
    public boolean isRunning() {
        final Iterator<Node> iterator = this.mNodes.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().animation.isRunning()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isStarted() {
        return this.mStarted;
    }
    
    public Builder play(final Animator animator) {
        if (animator == null) {
            return null;
        }
        this.mNeedsSort = true;
        return new Builder(animator);
    }
    
    public void playSequentially(final List<Animator> list) {
        if (list != null && list.size() > 0) {
            this.mNeedsSort = true;
            if (list.size() != 1) {
                for (int i = 0; i < list.size() - 1; ++i) {
                    this.play((Animator)list.get(i)).before((Animator)list.get(i + 1));
                }
            }
            else {
                this.play((Animator)list.get(0));
            }
        }
    }
    
    public void playSequentially(final Animator... array) {
        int i = 0;
        if (array != null) {
            this.mNeedsSort = true;
            if (array.length != 1) {
                while (i < array.length - 1) {
                    this.play(array[i]).before(array[i + 1]);
                    ++i;
                }
            }
            else {
                this.play(array[0]);
            }
        }
    }
    
    public void playTogether(final Collection<Animator> collection) {
        if (collection != null && collection.size() > 0) {
            this.mNeedsSort = true;
            final Iterator iterator = collection.iterator();
            Builder play = null;
            while (iterator.hasNext()) {
                final Animator animator = (Animator)iterator.next();
                if (play != null) {
                    play.with(animator);
                }
                else {
                    play = this.play(animator);
                }
            }
        }
    }
    
    public void playTogether(final Animator... array) {
        int i = 1;
        if (array != null) {
            this.mNeedsSort = true;
            final Builder play = this.play(array[0]);
            while (i < array.length) {
                play.with(array[i]);
                ++i;
            }
        }
    }
    
    @Override
    public AnimatorSet setDuration(final long n) {
        boolean b = false;
        if (n >= 0L) {
            b = true;
        }
        if (!b) {
            throw new IllegalArgumentException("duration must be a value of zero or greater");
        }
        final Iterator<Node> iterator = this.mNodes.iterator();
        while (iterator.hasNext()) {
            iterator.next().animation.setDuration(n);
        }
        this.mDuration = n;
        return this;
    }
    
    @Override
    public void setInterpolator(final Interpolator interpolator) {
        final Iterator<Node> iterator = this.mNodes.iterator();
        while (iterator.hasNext()) {
            iterator.next().animation.setInterpolator(interpolator);
        }
    }
    
    @Override
    public void setStartDelay(final long mStartDelay) {
        this.mStartDelay = mStartDelay;
    }
    
    @Override
    public void setTarget(final Object o) {
        final Iterator<Node> iterator = this.mNodes.iterator();
        while (iterator.hasNext()) {
            final Animator animation = iterator.next().animation;
            if (!(animation instanceof AnimatorSet)) {
                if (!(animation instanceof ObjectAnimator)) {
                    continue;
                }
                ((ObjectAnimator)animation).setTarget(o);
            }
            else {
                ((AnimatorSet)animation).setTarget(o);
            }
        }
    }
    
    @Override
    public void setupEndValues() {
        final Iterator<Node> iterator = this.mNodes.iterator();
        while (iterator.hasNext()) {
            iterator.next().animation.setupEndValues();
        }
    }
    
    @Override
    public void setupStartValues() {
        final Iterator<Node> iterator = this.mNodes.iterator();
        while (iterator.hasNext()) {
            iterator.next().animation.setupStartValues();
        }
    }
    
    @Override
    public void start() {
        final int n = 0;
        this.mTerminated = false;
        this.mStarted = true;
        this.sortNodes();
        final int size = this.mSortedNodes.size();
        for (int i = 0; i < size; ++i) {
            final Node node = this.mSortedNodes.get(i);
            final ArrayList<AnimatorListener> listeners = node.animation.getListeners();
            if (listeners != null && listeners.size() > 0) {
                for (final AnimatorListener animatorListener : new ArrayList(listeners)) {
                    if (!(animatorListener instanceof DependencyListener) && !(animatorListener instanceof AnimatorSetListener)) {
                        continue;
                    }
                    node.animation.removeListener(animatorListener);
                }
            }
        }
        final ArrayList<Node> list = new ArrayList<Node>();
        for (int j = 0; j < size; ++j) {
            final Node e = this.mSortedNodes.get(j);
            if (this.mSetListener == null) {
                this.mSetListener = new AnimatorSetListener(this);
            }
            if (e.dependencies != null && e.dependencies.size() != 0) {
                for (int size2 = e.dependencies.size(), k = 0; k < size2; ++k) {
                    final Dependency dependency = e.dependencies.get(k);
                    dependency.node.animation.addListener((AnimatorListener)new DependencyListener(this, e, dependency.rule));
                }
                e.tmpDependencies = (ArrayList)e.dependencies.clone();
            }
            else {
                list.add(e);
            }
            e.animation.addListener((AnimatorListener)this.mSetListener);
        }
        int n2;
        if (this.mStartDelay > 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            for (final Node node2 : list) {
                node2.animation.start();
                this.mPlayingSet.add(node2.animation);
            }
        }
        else {
            (this.mDelayAnim = ValueAnimator.ofFloat(0.0f, 1.0f)).setDuration(this.mStartDelay);
            this.mDelayAnim.addListener((AnimatorListener)new AnimatorListenerAdapter() {
                boolean canceled = false;
                
                @Override
                public void onAnimationCancel(final Animator animator) {
                    this.canceled = true;
                }
                
                @Override
                public void onAnimationEnd(final Animator animator) {
                    if (!this.canceled) {
                        for (int size = list.size(), i = 0; i < size; ++i) {
                            final Node node = list.get(i);
                            node.animation.start();
                            AnimatorSet.this.mPlayingSet.add(node.animation);
                        }
                    }
                }
            });
            this.mDelayAnim.start();
        }
        if (this.mListeners != null) {
            final ArrayList list2 = (ArrayList)this.mListeners.clone();
            for (int size3 = list2.size(), l = 0; l < size3; ++l) {
                ((AnimatorListener)list2.get(l)).onAnimationStart(this);
            }
        }
        if (this.mNodes.size() == 0 && this.mStartDelay == 0L) {
            this.mStarted = false;
            if (this.mListeners != null) {
                final ArrayList list3 = (ArrayList)this.mListeners.clone();
                for (int size4 = list3.size(), index = n; index < size4; ++index) {
                    ((AnimatorListener)list3.get(index)).onAnimationEnd(this);
                }
            }
        }
    }
    
    private class AnimatorSetListener implements AnimatorListener
    {
        private AnimatorSet mAnimatorSet;
        
        AnimatorSetListener(final AnimatorSet mAnimatorSet) {
            this.mAnimatorSet = mAnimatorSet;
        }
        
        @Override
        public void onAnimationCancel(final Animator animator) {
            if (!AnimatorSet.this.mTerminated && AnimatorSet.this.mPlayingSet.size() == 0 && AnimatorSet.this.mListeners != null) {
                for (int size = AnimatorSet.this.mListeners.size(), i = 0; i < size; ++i) {
                    ((AnimatorListener)AnimatorSet.this.mListeners.get(i)).onAnimationCancel(this.mAnimatorSet);
                }
            }
        }
        
        @Override
        public void onAnimationEnd(final Animator animator) {
            animator.removeListener((AnimatorListener)this);
            AnimatorSet.this.mPlayingSet.remove(animator);
            this.mAnimatorSet.mNodeMap.get(animator).done = true;
            if (!AnimatorSet.this.mTerminated) {
                final ArrayList access$200 = this.mAnimatorSet.mSortedNodes;
                final int size = access$200.size();
                int i = 0;
                while (true) {
                    while (i < size) {
                        if (((Node)access$200.get(i)).done) {
                            ++i;
                        }
                        else {
                            final int n = 0;
                            if (n != 0) {
                                if (AnimatorSet.this.mListeners != null) {
                                    final ArrayList list = (ArrayList)AnimatorSet.this.mListeners.clone();
                                    for (int size2 = list.size(), j = 0; j < size2; ++j) {
                                        ((AnimatorListener)list.get(j)).onAnimationEnd(this.mAnimatorSet);
                                    }
                                }
                                this.mAnimatorSet.mStarted = false;
                            }
                            return;
                        }
                    }
                    final int n = 1;
                    continue;
                }
            }
        }
        
        @Override
        public void onAnimationRepeat(final Animator animator) {
        }
        
        @Override
        public void onAnimationStart(final Animator animator) {
        }
    }
    
    public class Builder
    {
        private Node mCurrentNode;
        
        Builder(final Animator animator) {
            this.mCurrentNode = AnimatorSet.this.mNodeMap.get(animator);
            if (this.mCurrentNode == null) {
                this.mCurrentNode = new Node(animator);
                AnimatorSet.this.mNodeMap.put(animator, this.mCurrentNode);
                AnimatorSet.this.mNodes.add(this.mCurrentNode);
            }
        }
        
        public Builder after(final long duration) {
            final ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            ofFloat.setDuration(duration);
            this.after(ofFloat);
            return this;
        }
        
        public Builder after(final Animator animator) {
            final Node node = AnimatorSet.this.mNodeMap.get(animator);
            Node node2;
            if (node != null) {
                node2 = node;
            }
            else {
                final Node node3 = new Node(animator);
                AnimatorSet.this.mNodeMap.put(animator, node3);
                AnimatorSet.this.mNodes.add(node3);
                node2 = node3;
            }
            this.mCurrentNode.addDependency(new Dependency(node2, 1));
            return this;
        }
        
        public Builder before(final Animator animator) {
            final Node node = AnimatorSet.this.mNodeMap.get(animator);
            Node node2;
            if (node != null) {
                node2 = node;
            }
            else {
                final Node node3 = new Node(animator);
                AnimatorSet.this.mNodeMap.put(animator, node3);
                AnimatorSet.this.mNodes.add(node3);
                node2 = node3;
            }
            node2.addDependency(new Dependency(this.mCurrentNode, 1));
            return this;
        }
        
        public Builder with(final Animator animator) {
            final Node node = AnimatorSet.this.mNodeMap.get(animator);
            Node node2;
            if (node != null) {
                node2 = node;
            }
            else {
                final Node node3 = new Node(animator);
                AnimatorSet.this.mNodeMap.put(animator, node3);
                AnimatorSet.this.mNodes.add(node3);
                node2 = node3;
            }
            node2.addDependency(new Dependency(this.mCurrentNode, 0));
            return this;
        }
    }
    
    private static class Dependency
    {
        static final int AFTER = 1;
        static final int WITH = 0;
        public Node node;
        public int rule;
        
        public Dependency(final Node node, final int rule) {
            this.node = node;
            this.rule = rule;
        }
    }
    
    private static class DependencyListener implements AnimatorListener
    {
        private AnimatorSet mAnimatorSet;
        private Node mNode;
        private int mRule;
        
        public DependencyListener(final AnimatorSet mAnimatorSet, final Node mNode, final int mRule) {
            this.mAnimatorSet = mAnimatorSet;
            this.mNode = mNode;
            this.mRule = mRule;
        }
        
        private void startIfReady(final Animator animator) {
            if (!this.mAnimatorSet.mTerminated) {
                while (true) {
                    for (int size = this.mNode.tmpDependencies.size(), i = 0; i < size; ++i) {
                        final Dependency dependency = this.mNode.tmpDependencies.get(i);
                        if (dependency.rule == this.mRule && dependency.node.animation == animator) {
                            animator.removeListener((AnimatorListener)this);
                            final Object o = dependency;
                            this.mNode.tmpDependencies.remove(o);
                            if (this.mNode.tmpDependencies.size() == 0) {
                                this.mNode.animation.start();
                                this.mAnimatorSet.mPlayingSet.add(this.mNode.animation);
                            }
                            return;
                        }
                    }
                    final Object o = null;
                    continue;
                }
            }
        }
        
        @Override
        public void onAnimationCancel(final Animator animator) {
        }
        
        @Override
        public void onAnimationEnd(final Animator animator) {
            if (this.mRule == 1) {
                this.startIfReady(animator);
            }
        }
        
        @Override
        public void onAnimationRepeat(final Animator animator) {
        }
        
        @Override
        public void onAnimationStart(final Animator animator) {
            if (this.mRule == 0) {
                this.startIfReady(animator);
            }
        }
    }
    
    private static class Node implements Cloneable
    {
        public Animator animation;
        public ArrayList<Dependency> dependencies;
        public boolean done;
        public ArrayList<Node> nodeDependencies;
        public ArrayList<Node> nodeDependents;
        public ArrayList<Dependency> tmpDependencies;
        
        public Node(final Animator animation) {
            this.dependencies = null;
            this.tmpDependencies = null;
            this.nodeDependencies = null;
            this.nodeDependents = null;
            this.done = false;
            this.animation = animation;
        }
        
        public void addDependency(final Dependency e) {
            if (this.dependencies == null) {
                this.dependencies = new ArrayList<Dependency>();
                this.nodeDependencies = new ArrayList<Node>();
            }
            this.dependencies.add(e);
            if (!this.nodeDependencies.contains(e.node)) {
                this.nodeDependencies.add(e.node);
            }
            final Node node = e.node;
            if (node.nodeDependents == null) {
                node.nodeDependents = new ArrayList<Node>();
            }
            node.nodeDependents.add(this);
        }
        
        public Node clone() {
            try {
                final Node node = (Node)super.clone();
                node.animation = this.animation.clone();
                return node;
            }
            catch (final CloneNotSupportedException ex) {
                throw new AssertionError();
            }
        }
    }
}
