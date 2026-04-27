// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.internal;

import android.os.Parcel;
import android.os.Parcelable$ClassLoaderCreator;
import android.os.Parcelable$Creator;
import android.support.annotation.RestrictTo;
import android.os.Parcelable;
import android.util.SparseArray;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class ParcelableSparseArray extends SparseArray<Parcelable> implements Parcelable
{
    public static final Parcelable$Creator<ParcelableSparseArray> CREATOR;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$ClassLoaderCreator<ParcelableSparseArray>() {
            public ParcelableSparseArray createFromParcel(final Parcel parcel) {
                return new ParcelableSparseArray(parcel, null);
            }
            
            public ParcelableSparseArray createFromParcel(final Parcel parcel, final ClassLoader classLoader) {
                return new ParcelableSparseArray(parcel, classLoader);
            }
            
            public ParcelableSparseArray[] newArray(final int n) {
                return new ParcelableSparseArray[n];
            }
        };
    }
    
    public ParcelableSparseArray() {
    }
    
    public ParcelableSparseArray(final Parcel parcel, final ClassLoader classLoader) {
        final int int1 = parcel.readInt();
        final int[] array = new int[int1];
        parcel.readIntArray(array);
        final Parcelable[] parcelableArray = parcel.readParcelableArray(classLoader);
        for (int i = 0; i < int1; ++i) {
            this.put(array[i], (Object)parcelableArray[i]);
        }
    }
    
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        final int size = this.size();
        final int[] array = new int[size];
        final Parcelable[] array2 = new Parcelable[size];
        for (int i = 0; i < size; ++i) {
            array[i] = this.keyAt(i);
            array2[i] = (Parcelable)this.valueAt(i);
        }
        parcel.writeInt(size);
        parcel.writeIntArray(array);
        parcel.writeParcelableArray(array2, n);
    }
}
