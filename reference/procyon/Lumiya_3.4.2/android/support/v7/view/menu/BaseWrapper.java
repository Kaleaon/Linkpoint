// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.view.menu;

class BaseWrapper<T>
{
    final T mWrappedObject;
    
    BaseWrapper(final T mWrappedObject) {
        if (mWrappedObject != null) {
            this.mWrappedObject = mWrappedObject;
            return;
        }
        throw new IllegalArgumentException("Wrapped Object can not be null.");
    }
    
    public T getWrappedObject() {
        return this.mWrappedObject;
    }
}
