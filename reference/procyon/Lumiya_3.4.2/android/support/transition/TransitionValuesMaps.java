// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.support.v4.util.ArrayMap;
import android.support.v4.util.LongSparseArray;
import android.view.View;
import android.util.SparseArray;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
class TransitionValuesMaps
{
    final SparseArray<View> mIdValues;
    final LongSparseArray<View> mItemIdValues;
    final ArrayMap<String, View> mNameValues;
    final ArrayMap<View, TransitionValues> mViewValues;
    
    TransitionValuesMaps() {
        this.mViewValues = new ArrayMap<View, TransitionValues>();
        this.mIdValues = (SparseArray<View>)new SparseArray();
        this.mItemIdValues = new LongSparseArray<View>();
        this.mNameValues = new ArrayMap<String, View>();
    }
}
