package android.support.v4.app;

import java.util.List;
/* loaded from: classes.dex */
public class FragmentManagerNonConfig {
    private final List<FragmentManagerNonConfig> mChildNonConfigs;
    private final List<Fragment> mFragments;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FragmentManagerNonConfig(List<Fragment> list, List<FragmentManagerNonConfig> list2) {
        this.mFragments = list;
        this.mChildNonConfigs = list2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<FragmentManagerNonConfig> getChildNonConfigs() {
        return this.mChildNonConfigs;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<Fragment> getFragments() {
        return this.mFragments;
    }
}
