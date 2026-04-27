// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.view.View;

class ViewBoundsCheck
{
    static final int CVE_PVE_POS = 12;
    static final int CVE_PVS_POS = 8;
    static final int CVS_PVE_POS = 4;
    static final int CVS_PVS_POS = 0;
    static final int EQ = 2;
    static final int FLAG_CVE_EQ_PVE = 8192;
    static final int FLAG_CVE_EQ_PVS = 512;
    static final int FLAG_CVE_GT_PVE = 4096;
    static final int FLAG_CVE_GT_PVS = 256;
    static final int FLAG_CVE_LT_PVE = 16384;
    static final int FLAG_CVE_LT_PVS = 1024;
    static final int FLAG_CVS_EQ_PVE = 32;
    static final int FLAG_CVS_EQ_PVS = 2;
    static final int FLAG_CVS_GT_PVE = 16;
    static final int FLAG_CVS_GT_PVS = 1;
    static final int FLAG_CVS_LT_PVE = 64;
    static final int FLAG_CVS_LT_PVS = 4;
    static final int GT = 1;
    static final int LT = 4;
    static final int MASK = 7;
    BoundFlags mBoundFlags;
    final Callback mCallback;
    
    ViewBoundsCheck(final Callback mCallback) {
        this.mCallback = mCallback;
        this.mBoundFlags = new BoundFlags();
    }
    
    View findOneViewWithinBoundFlags(int i, final int n, final int n2, final int n3) {
        final int parentStart = this.mCallback.getParentStart();
        final int parentEnd = this.mCallback.getParentEnd();
        int n4;
        if (n <= i) {
            n4 = -1;
        }
        else {
            n4 = 1;
        }
        View view = null;
        while (i != n) {
            final View child = this.mCallback.getChildAt(i);
            this.mBoundFlags.setBounds(parentStart, parentEnd, this.mCallback.getChildStart(child), this.mCallback.getChildEnd(child));
            if (n2 != 0) {
                this.mBoundFlags.resetFlags();
                this.mBoundFlags.addFlags(n2);
                if (this.mBoundFlags.boundsMatch()) {
                    return child;
                }
            }
            if (n3 != 0) {
                this.mBoundFlags.resetFlags();
                this.mBoundFlags.addFlags(n3);
                if (this.mBoundFlags.boundsMatch()) {
                    view = child;
                }
            }
            i += n4;
        }
        return view;
    }
    
    boolean isViewWithinBoundFlags(final View view, final int n) {
        this.mBoundFlags.setBounds(this.mCallback.getParentStart(), this.mCallback.getParentEnd(), this.mCallback.getChildStart(view), this.mCallback.getChildEnd(view));
        if (n == 0) {
            return false;
        }
        this.mBoundFlags.resetFlags();
        this.mBoundFlags.addFlags(n);
        return this.mBoundFlags.boundsMatch();
    }
    
    static class BoundFlags
    {
        int mBoundFlags;
        int mChildEnd;
        int mChildStart;
        int mRvEnd;
        int mRvStart;
        
        BoundFlags() {
            this.mBoundFlags = 0;
        }
        
        void addFlags(final int n) {
            this.mBoundFlags |= n;
        }
        
        boolean boundsMatch() {
            return ((this.mBoundFlags & 0x7) == 0x0 || (this.mBoundFlags & this.compare(this.mChildStart, this.mRvStart) << 0) != 0x0) && ((this.mBoundFlags & 0x70) == 0x0 || (this.mBoundFlags & this.compare(this.mChildStart, this.mRvEnd) << 4) != 0x0) && ((this.mBoundFlags & 0x700) == 0x0 || (this.mBoundFlags & this.compare(this.mChildEnd, this.mRvStart) << 8) != 0x0) && ((this.mBoundFlags & 0x7000) == 0x0 || (this.mBoundFlags & this.compare(this.mChildEnd, this.mRvEnd) << 12) != 0x0);
        }
        
        int compare(final int n, final int n2) {
            if (n > n2) {
                return 1;
            }
            if (n != n2) {
                return 4;
            }
            return 2;
        }
        
        void resetFlags() {
            this.mBoundFlags = 0;
        }
        
        void setBounds(final int mRvStart, final int mRvEnd, final int mChildStart, final int mChildEnd) {
            this.mRvStart = mRvStart;
            this.mRvEnd = mRvEnd;
            this.mChildStart = mChildStart;
            this.mChildEnd = mChildEnd;
        }
        
        void setFlags(final int n, final int n2) {
            this.mBoundFlags = ((this.mBoundFlags & ~n2) | (n & n2));
        }
    }
    
    interface Callback
    {
        View getChildAt(final int p0);
        
        int getChildCount();
        
        int getChildEnd(final View p0);
        
        int getChildStart(final View p0);
        
        View getParent();
        
        int getParentEnd();
        
        int getParentStart();
    }
    
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewBounds {
    }
}
