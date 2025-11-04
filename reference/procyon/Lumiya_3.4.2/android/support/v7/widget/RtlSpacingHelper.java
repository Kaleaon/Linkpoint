// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

class RtlSpacingHelper
{
    public static final int UNDEFINED = Integer.MIN_VALUE;
    private int mEnd;
    private int mExplicitLeft;
    private int mExplicitRight;
    private boolean mIsRelative;
    private boolean mIsRtl;
    private int mLeft;
    private int mRight;
    private int mStart;
    
    RtlSpacingHelper() {
        this.mLeft = 0;
        this.mRight = 0;
        this.mStart = Integer.MIN_VALUE;
        this.mEnd = Integer.MIN_VALUE;
        this.mExplicitLeft = 0;
        this.mExplicitRight = 0;
        this.mIsRtl = false;
        this.mIsRelative = false;
    }
    
    public int getEnd() {
        int n;
        if (!this.mIsRtl) {
            n = this.mRight;
        }
        else {
            n = this.mLeft;
        }
        return n;
    }
    
    public int getLeft() {
        return this.mLeft;
    }
    
    public int getRight() {
        return this.mRight;
    }
    
    public int getStart() {
        int n;
        if (!this.mIsRtl) {
            n = this.mLeft;
        }
        else {
            n = this.mRight;
        }
        return n;
    }
    
    public void setAbsolute(final int n, final int n2) {
        this.mIsRelative = false;
        if (n != Integer.MIN_VALUE) {
            this.mExplicitLeft = n;
            this.mLeft = n;
        }
        if (n2 != Integer.MIN_VALUE) {
            this.mExplicitRight = n2;
            this.mRight = n2;
        }
    }
    
    public void setDirection(final boolean mIsRtl) {
        if (mIsRtl != this.mIsRtl) {
            this.mIsRtl = mIsRtl;
            if (!this.mIsRelative) {
                this.mLeft = this.mExplicitLeft;
                this.mRight = this.mExplicitRight;
            }
            else if (!mIsRtl) {
                int mLeft;
                if (this.mStart == Integer.MIN_VALUE) {
                    mLeft = this.mExplicitLeft;
                }
                else {
                    mLeft = this.mStart;
                }
                this.mLeft = mLeft;
                int mRight;
                if (this.mEnd == Integer.MIN_VALUE) {
                    mRight = this.mExplicitRight;
                }
                else {
                    mRight = this.mEnd;
                }
                this.mRight = mRight;
            }
            else {
                int mLeft2;
                if (this.mEnd == Integer.MIN_VALUE) {
                    mLeft2 = this.mExplicitLeft;
                }
                else {
                    mLeft2 = this.mEnd;
                }
                this.mLeft = mLeft2;
                int mRight2;
                if (this.mStart == Integer.MIN_VALUE) {
                    mRight2 = this.mExplicitRight;
                }
                else {
                    mRight2 = this.mStart;
                }
                this.mRight = mRight2;
            }
        }
    }
    
    public void setRelative(final int mRight, final int mLeft) {
        this.mStart = mRight;
        this.mEnd = mLeft;
        this.mIsRelative = true;
        if (!this.mIsRtl) {
            if (mRight != Integer.MIN_VALUE) {
                this.mLeft = mRight;
            }
            if (mLeft != Integer.MIN_VALUE) {
                this.mRight = mLeft;
            }
        }
        else {
            if (mLeft != Integer.MIN_VALUE) {
                this.mLeft = mLeft;
            }
            if (mRight != Integer.MIN_VALUE) {
                this.mRight = mRight;
            }
        }
    }
}
