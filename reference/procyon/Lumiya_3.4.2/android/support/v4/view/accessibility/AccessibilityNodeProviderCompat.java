// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.view.accessibility;

import java.util.ArrayList;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import java.util.List;
import android.support.annotation.Nullable;
import android.os.Build$VERSION;

public class AccessibilityNodeProviderCompat
{
    public static final int HOST_VIEW_ID = -1;
    private static final AccessibilityNodeProviderImpl IMPL;
    private final Object mProvider;
    
    static {
        if (Build$VERSION.SDK_INT < 19) {
            if (Build$VERSION.SDK_INT < 16) {
                IMPL = (AccessibilityNodeProviderImpl)new AccessibilityNodeProviderStubImpl();
            }
            else {
                IMPL = (AccessibilityNodeProviderImpl)new AccessibilityNodeProviderJellyBeanImpl();
            }
        }
        else {
            IMPL = (AccessibilityNodeProviderImpl)new AccessibilityNodeProviderKitKatImpl();
        }
    }
    
    public AccessibilityNodeProviderCompat() {
        this.mProvider = AccessibilityNodeProviderCompat.IMPL.newAccessibilityNodeProviderBridge(this);
    }
    
    public AccessibilityNodeProviderCompat(final Object mProvider) {
        this.mProvider = mProvider;
    }
    
    @Nullable
    public AccessibilityNodeInfoCompat createAccessibilityNodeInfo(final int n) {
        return null;
    }
    
    @Nullable
    public List<AccessibilityNodeInfoCompat> findAccessibilityNodeInfosByText(final String s, final int n) {
        return null;
    }
    
    @Nullable
    public AccessibilityNodeInfoCompat findFocus(final int n) {
        return null;
    }
    
    public Object getProvider() {
        return this.mProvider;
    }
    
    public boolean performAction(final int n, final int n2, final Bundle bundle) {
        return false;
    }
    
    interface AccessibilityNodeProviderImpl
    {
        Object newAccessibilityNodeProviderBridge(final AccessibilityNodeProviderCompat p0);
    }
    
    @RequiresApi(16)
    private static class AccessibilityNodeProviderJellyBeanImpl extends AccessibilityNodeProviderStubImpl
    {
        AccessibilityNodeProviderJellyBeanImpl() {
        }
        
        @Override
        public Object newAccessibilityNodeProviderBridge(final AccessibilityNodeProviderCompat accessibilityNodeProviderCompat) {
            return AccessibilityNodeProviderCompatJellyBean.newAccessibilityNodeProviderBridge((AccessibilityNodeProviderCompatJellyBean.AccessibilityNodeInfoBridge)new AccessibilityNodeProviderCompatJellyBean.AccessibilityNodeInfoBridge() {
                @Override
                public Object createAccessibilityNodeInfo(final int n) {
                    final AccessibilityNodeInfoCompat accessibilityNodeInfo = accessibilityNodeProviderCompat.createAccessibilityNodeInfo(n);
                    if (accessibilityNodeInfo != null) {
                        return accessibilityNodeInfo.unwrap();
                    }
                    return null;
                }
                
                @Override
                public List<Object> findAccessibilityNodeInfosByText(final String s, int i) {
                    final List<AccessibilityNodeInfoCompat> accessibilityNodeInfosByText = accessibilityNodeProviderCompat.findAccessibilityNodeInfosByText(s, i);
                    if (accessibilityNodeInfosByText != null) {
                        final ArrayList list = new ArrayList();
                        int size;
                        for (size = accessibilityNodeInfosByText.size(), i = 0; i < size; ++i) {
                            list.add(accessibilityNodeInfosByText.get(i).unwrap());
                        }
                        return list;
                    }
                    return null;
                }
                
                @Override
                public boolean performAction(final int n, final int n2, final Bundle bundle) {
                    return accessibilityNodeProviderCompat.performAction(n, n2, bundle);
                }
            });
        }
    }
    
    static class AccessibilityNodeProviderStubImpl implements AccessibilityNodeProviderImpl
    {
        @Override
        public Object newAccessibilityNodeProviderBridge(final AccessibilityNodeProviderCompat accessibilityNodeProviderCompat) {
            return null;
        }
    }
    
    @RequiresApi(19)
    private static class AccessibilityNodeProviderKitKatImpl extends AccessibilityNodeProviderStubImpl
    {
        AccessibilityNodeProviderKitKatImpl() {
        }
        
        @Override
        public Object newAccessibilityNodeProviderBridge(final AccessibilityNodeProviderCompat accessibilityNodeProviderCompat) {
            return AccessibilityNodeProviderCompatKitKat.newAccessibilityNodeProviderBridge((AccessibilityNodeProviderCompatKitKat.AccessibilityNodeInfoBridge)new AccessibilityNodeProviderCompatKitKat.AccessibilityNodeInfoBridge() {
                @Override
                public Object createAccessibilityNodeInfo(final int n) {
                    final AccessibilityNodeInfoCompat accessibilityNodeInfo = accessibilityNodeProviderCompat.createAccessibilityNodeInfo(n);
                    if (accessibilityNodeInfo != null) {
                        return accessibilityNodeInfo.unwrap();
                    }
                    return null;
                }
                
                @Override
                public List<Object> findAccessibilityNodeInfosByText(final String s, int i) {
                    final List<AccessibilityNodeInfoCompat> accessibilityNodeInfosByText = accessibilityNodeProviderCompat.findAccessibilityNodeInfosByText(s, i);
                    if (accessibilityNodeInfosByText != null) {
                        final ArrayList list = new ArrayList();
                        int size;
                        for (size = accessibilityNodeInfosByText.size(), i = 0; i < size; ++i) {
                            list.add(accessibilityNodeInfosByText.get(i).unwrap());
                        }
                        return list;
                    }
                    return null;
                }
                
                @Override
                public Object findFocus(final int n) {
                    final AccessibilityNodeInfoCompat focus = accessibilityNodeProviderCompat.findFocus(n);
                    if (focus != null) {
                        return focus.unwrap();
                    }
                    return null;
                }
                
                @Override
                public boolean performAction(final int n, final int n2, final Bundle bundle) {
                    return accessibilityNodeProviderCompat.performAction(n, n2, bundle);
                }
            });
        }
    }
}
