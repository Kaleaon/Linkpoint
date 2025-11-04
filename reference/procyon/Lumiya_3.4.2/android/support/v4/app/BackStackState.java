// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import java.util.List;
import android.util.Log;
import android.text.TextUtils;
import android.os.Parcel;
import java.util.ArrayList;
import android.os.Parcelable$Creator;
import android.os.Parcelable;

final class BackStackState implements Parcelable
{
    public static final Parcelable$Creator<BackStackState> CREATOR;
    final int mBreadCrumbShortTitleRes;
    final CharSequence mBreadCrumbShortTitleText;
    final int mBreadCrumbTitleRes;
    final CharSequence mBreadCrumbTitleText;
    final int mIndex;
    final String mName;
    final int[] mOps;
    final boolean mReorderingAllowed;
    final ArrayList<String> mSharedElementSourceNames;
    final ArrayList<String> mSharedElementTargetNames;
    final int mTransition;
    final int mTransitionStyle;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<BackStackState>() {
            public BackStackState createFromParcel(final Parcel parcel) {
                return new BackStackState(parcel);
            }
            
            public BackStackState[] newArray(final int n) {
                return new BackStackState[n];
            }
        };
    }
    
    public BackStackState(final Parcel parcel) {
        this.mOps = parcel.createIntArray();
        this.mTransition = parcel.readInt();
        this.mTransitionStyle = parcel.readInt();
        this.mName = parcel.readString();
        this.mIndex = parcel.readInt();
        this.mBreadCrumbTitleRes = parcel.readInt();
        this.mBreadCrumbTitleText = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mBreadCrumbShortTitleRes = parcel.readInt();
        this.mBreadCrumbShortTitleText = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mSharedElementSourceNames = parcel.createStringArrayList();
        this.mSharedElementTargetNames = parcel.createStringArrayList();
        this.mReorderingAllowed = (parcel.readInt() != 0);
    }
    
    public BackStackState(final BackStackRecord backStackRecord) {
        final int size = backStackRecord.mOps.size();
        this.mOps = new int[size * 6];
        if (backStackRecord.mAddToBackStack) {
            int i = 0;
            int n = 0;
            while (i < size) {
                final BackStackRecord.Op op = backStackRecord.mOps.get(i);
                final int[] mOps = this.mOps;
                final int n2 = n + 1;
                mOps[n] = op.cmd;
                final int[] mOps2 = this.mOps;
                final int n3 = n2 + 1;
                int mIndex;
                if (op.fragment == null) {
                    mIndex = -1;
                }
                else {
                    mIndex = op.fragment.mIndex;
                }
                mOps2[n2] = mIndex;
                final int[] mOps3 = this.mOps;
                final int n4 = n3 + 1;
                mOps3[n3] = op.enterAnim;
                final int[] mOps4 = this.mOps;
                final int n5 = n4 + 1;
                mOps4[n4] = op.exitAnim;
                final int[] mOps5 = this.mOps;
                final int n6 = n5 + 1;
                mOps5[n5] = op.popEnterAnim;
                final int[] mOps6 = this.mOps;
                n = n6 + 1;
                mOps6[n6] = op.popExitAnim;
                ++i;
            }
            this.mTransition = backStackRecord.mTransition;
            this.mTransitionStyle = backStackRecord.mTransitionStyle;
            this.mName = backStackRecord.mName;
            this.mIndex = backStackRecord.mIndex;
            this.mBreadCrumbTitleRes = backStackRecord.mBreadCrumbTitleRes;
            this.mBreadCrumbTitleText = backStackRecord.mBreadCrumbTitleText;
            this.mBreadCrumbShortTitleRes = backStackRecord.mBreadCrumbShortTitleRes;
            this.mBreadCrumbShortTitleText = backStackRecord.mBreadCrumbShortTitleText;
            this.mSharedElementSourceNames = backStackRecord.mSharedElementSourceNames;
            this.mSharedElementTargetNames = backStackRecord.mSharedElementTargetNames;
            this.mReorderingAllowed = backStackRecord.mReorderingAllowed;
            return;
        }
        throw new IllegalStateException("Not on back stack");
    }
    
    public int describeContents() {
        return 0;
    }
    
    public BackStackRecord instantiate(final FragmentManagerImpl fragmentManagerImpl) {
        int i = 0;
        final BackStackRecord obj = new BackStackRecord(fragmentManagerImpl);
        int j = 0;
        while (i < this.mOps.length) {
            final BackStackRecord.Op op = new BackStackRecord.Op();
            final int[] mOps = this.mOps;
            final int n = i + 1;
            op.cmd = mOps[i];
            if (FragmentManagerImpl.DEBUG) {
                Log.v("FragmentManager", "Instantiate " + obj + " op #" + j + " base fragment #" + this.mOps[n]);
            }
            final int[] mOps2 = this.mOps;
            final int n2 = n + 1;
            final int n3 = mOps2[n];
            if (n3 < 0) {
                op.fragment = null;
            }
            else {
                op.fragment = (Fragment)fragmentManagerImpl.mActive.get(n3);
            }
            final int[] mOps3 = this.mOps;
            final int n4 = n2 + 1;
            op.enterAnim = mOps3[n2];
            final int[] mOps4 = this.mOps;
            final int n5 = n4 + 1;
            op.exitAnim = mOps4[n4];
            final int[] mOps5 = this.mOps;
            i = n5 + 1;
            op.popEnterAnim = mOps5[n5];
            op.popExitAnim = this.mOps[i];
            obj.mEnterAnim = op.enterAnim;
            obj.mExitAnim = op.exitAnim;
            obj.mPopEnterAnim = op.popEnterAnim;
            obj.mPopExitAnim = op.popExitAnim;
            obj.addOp(op);
            ++j;
            ++i;
        }
        obj.mTransition = this.mTransition;
        obj.mTransitionStyle = this.mTransitionStyle;
        obj.mName = this.mName;
        obj.mIndex = this.mIndex;
        obj.mAddToBackStack = true;
        obj.mBreadCrumbTitleRes = this.mBreadCrumbTitleRes;
        obj.mBreadCrumbTitleText = this.mBreadCrumbTitleText;
        obj.mBreadCrumbShortTitleRes = this.mBreadCrumbShortTitleRes;
        obj.mBreadCrumbShortTitleText = this.mBreadCrumbShortTitleText;
        obj.mSharedElementSourceNames = this.mSharedElementSourceNames;
        obj.mSharedElementTargetNames = this.mSharedElementTargetNames;
        obj.mReorderingAllowed = this.mReorderingAllowed;
        obj.bumpBackStackNesting(1);
        return obj;
    }
    
    public void writeToParcel(final Parcel parcel, int n) {
        n = 0;
        parcel.writeIntArray(this.mOps);
        parcel.writeInt(this.mTransition);
        parcel.writeInt(this.mTransitionStyle);
        parcel.writeString(this.mName);
        parcel.writeInt(this.mIndex);
        parcel.writeInt(this.mBreadCrumbTitleRes);
        TextUtils.writeToParcel(this.mBreadCrumbTitleText, parcel, 0);
        parcel.writeInt(this.mBreadCrumbShortTitleRes);
        TextUtils.writeToParcel(this.mBreadCrumbShortTitleText, parcel, 0);
        parcel.writeStringList((List)this.mSharedElementSourceNames);
        parcel.writeStringList((List)this.mSharedElementTargetNames);
        if (this.mReorderingAllowed) {
            n = 1;
        }
        parcel.writeInt(n);
    }
}
