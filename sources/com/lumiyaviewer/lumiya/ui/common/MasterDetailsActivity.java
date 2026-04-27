// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            DetailsActivity, FragmentActivityFactory, ReloadableFragment, FragmentHasTitle

public abstract class MasterDetailsActivity extends DetailsActivity
{

    protected static final String FROM_SAME_ACTIVITY = "fromSameActivity";
    private static final String IMPLICIT_DETAILS_TAG = "MasterDetailsActivityIsImplicitDetails";
    public static final String INTENT_SELECTION_KEY = "selection";
    public static final String WEAK_SELECTION_KEY = "weakSelection";
    private boolean isSplitScreen;

    public MasterDetailsActivity()
    {
        isSplitScreen = false;
    }

    protected abstract FragmentActivityFactory getDetailsFragmentFactory();

    protected Bundle getNewDetailsFragmentArguments(Bundle bundle, Bundle bundle1)
    {
        return bundle1;
    }

    protected boolean isAlwaysImplicitFragment(Class class1)
    {
        return false;
    }

    protected boolean isRootDetailsFragment(Class class1)
    {
        return getDetailsFragmentFactory().getFragmentClass().isAssignableFrom(class1);
    }

    public boolean isSplitScreen()
    {
        return isSplitScreen;
    }

    protected void onCreate(Bundle bundle)
    {
        Object obj;
        FragmentTransaction fragmenttransaction;
        Fragment fragment;
        Fragment fragment1;
        boolean flag;
        boolean flag1;
        boolean flag2;
        super.onCreate(bundle);
        isSplitScreen = LumiyaApp.isSplitScreenNeeded(this);
        boolean flag3;
        if (isSplitScreen)
        {
            setContentView(0x7f0400a4);
        } else
        {
            setContentView(0x7f0400a1);
        }
        if (findViewById(0x7f100286) != null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (getSupportFragmentManager().findFragmentById(0x7f100286) != null)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        if (findViewById(0x7f100114) != null)
        {
            flag2 = true;
        } else
        {
            flag2 = false;
        }
        if (getSupportFragmentManager().findFragmentById(0x7f100114) != null)
        {
            flag3 = true;
        } else
        {
            flag3 = false;
        }
        Debug.Printf("MasterDetailsActivity: hasSelectorView = %b, sel fragment %b, hasDetailsView = %b, details fragment %b", new Object[] {
            Boolean.valueOf(flag), Boolean.valueOf(flag1), Boolean.valueOf(flag2), Boolean.valueOf(flag3)
        });
        Debug.Printf("MasterDetailsActivity: intent = %s", new Object[] {
            getIntent()
        });
        fragmenttransaction = getSupportFragmentManager().beginTransaction();
        fragment = getSupportFragmentManager().findFragmentById(0x7f100286);
        fragment1 = getSupportFragmentManager().findFragmentById(0x7f100114);
        if (fragment1 == null) goto _L2; else goto _L1
_L1:
        obj = fragment1.getArguments();
        if (obj == null) goto _L2; else goto _L3
_L3:
        Debug.Printf("MasterDetailsActivity: implicit details tag = %b", new Object[] {
            Boolean.valueOf(((Bundle) (obj)).getBoolean("MasterDetailsActivityIsImplicitDetails", false))
        });
        if (((Bundle) (obj)).getBoolean("MasterDetailsActivityIsImplicitDetails", false)) goto _L2; else goto _L4
_L4:
        flag = true;
_L27:
        Debug.Printf("MasterDetailsActivity: hasExplicitDetails = %b", new Object[] {
            Boolean.valueOf(flag)
        });
        if (flag || bundle != null) goto _L6; else goto _L5
_L5:
        obj = getIntent().getBundleExtra("selection");
        if (obj == null) goto _L6; else goto _L7
_L7:
        flag = true;
_L25:
        if (flag || bundle != null) goto _L9; else goto _L8
_L8:
        if (!isSplitScreen) goto _L11; else goto _L10
_L10:
        bundle = getIntent().getBundleExtra("weakSelection");
        if (bundle == null) goto _L11; else goto _L12
_L12:
        flag = true;
_L24:
        if (!isSplitScreen)
        {
            flag1 = flag ^ true;
        } else
        {
            flag1 = true;
        }
        if (flag1)
        {
            if (fragment != null)
            {
                obj = fragment.toString();
            } else
            {
                obj = "null";
            }
            Debug.Printf("MasterDetailsActivity: existing fragment %s", new Object[] {
                obj
            });
            if (fragment != null)
            {
                if (fragment.isVisible())
                {
                    obj = "visible";
                } else
                {
                    obj = "not visible";
                }
                Debug.Printf("MasterDetailsActivity: existing fragment is %s", new Object[] {
                    obj
                });
                if (fragment.isDetached())
                {
                    fragmenttransaction.attach(fragment);
                } else
                if (fragment.isHidden())
                {
                    fragmenttransaction.show(fragment);
                }
            } else
            {
                fragmenttransaction.add(0x7f100286, onCreateMasterFragment(getIntent(), bundle));
            }
        } else
        if (fragment != null && !fragment.isDetached())
        {
            fragmenttransaction.detach(fragment);
        }
        if (!isSplitScreen)
        {
            flag2 = flag;
        } else
        {
            flag2 = true;
        }
        Debug.Printf("MasterDetailsActivity: selectorVisible %b, detailsVisible %b, hasExplicitDetails %b", new Object[] {
            Boolean.valueOf(flag1), Boolean.valueOf(flag2), Boolean.valueOf(flag)
        });
        if (!flag2) goto _L14; else goto _L13
_L13:
        if (fragment1 != null) goto _L16; else goto _L15
_L15:
        Debug.Printf("MasterDetailsActivity: creating new details fragment", new Object[0]);
        if (fragment == null) goto _L18; else goto _L17
_L17:
        obj = fragment.getArguments();
_L21:
        obj = getNewDetailsFragmentArguments(((Bundle) (obj)), bundle);
        bundle = (Fragment)getDetailsFragmentFactory().getFragmentClass().newInstance();
        if (!(bundle instanceof ReloadableFragment)) goto _L20; else goto _L19
_L19:
        bundle.setArguments(new Bundle());
        ((ReloadableFragment)bundle).setFragmentArgs(getIntent(), ((Bundle) (obj)));
_L22:
        if (flag)
        {
            break MISSING_BLOCK_LABEL_529;
        }
        obj = bundle.getArguments();
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_529;
        }
        ((Bundle) (obj)).putBoolean("MasterDetailsActivityIsImplicitDetails", true);
        Debug.Printf("MasterDetailsActivity: adding new details fragment: %s", new Object[] {
            bundle
        });
        fragmenttransaction.add(0x7f100114, bundle, "defaultDetails");
_L23:
        if (!fragmenttransaction.isEmpty())
        {
            fragmenttransaction.commit();
        }
        return;
_L9:
        bundle = ((Bundle) (obj));
        continue; /* Loop/switch isn't completed */
_L18:
        obj = null;
          goto _L21
_L20:
        bundle.setArguments(((Bundle) (obj)));
          goto _L22
        bundle;
        Debug.Warning(bundle);
          goto _L23
_L16:
        Debug.Printf("MasterDetailsActivity: not creating new details fragment. existing is detached: %b (%s)", new Object[] {
            Boolean.valueOf(fragment1.isDetached()), fragment1
        });
        if (fragment1.isDetached())
        {
            fragmenttransaction.attach(fragment1);
        }
          goto _L23
_L14:
        if (fragment1 != null && !fragment1.isDetached())
        {
            fragmenttransaction.remove(fragment1);
        }
          goto _L23
_L11:
        bundle = ((Bundle) (obj));
        if (true) goto _L24; else goto _L6
_L6:
        obj = null;
        if (true) goto _L25; else goto _L2
_L2:
        flag = false;
        if (true) goto _L27; else goto _L26
_L26:
    }

    protected abstract Fragment onCreateMasterFragment(Intent intent, Bundle bundle);

    protected boolean onDetailsStackEmpty()
    {
        Object obj;
        Fragment fragment;
        if (isSplitScreen)
        {
            return true;
        }
        obj = getSupportFragmentManager();
        fragment = ((FragmentManager) (obj)).findFragmentById(0x7f100114);
        if (fragment == null || !(fragment.isDetached() ^ true)) goto _L2; else goto _L1
_L1:
        FragmentTransaction fragmenttransaction;
        Debug.Printf("MasterDetailsFragment: onDetailsStackEmpty has detailsFragment (%s), detached: %b", new Object[] {
            fragment, Boolean.valueOf(fragment.isDetached())
        });
        fragmenttransaction = ((FragmentManager) (obj)).beginTransaction();
        fragmenttransaction.setCustomAnimations(0x10a0000, 0x7f050012, 0, 0x10a0001);
        fragmenttransaction.remove(fragment);
        obj = ((FragmentManager) (obj)).findFragmentById(0x7f100286);
        boolean flag;
        boolean flag1;
        boolean flag2;
        if (obj != null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (obj != null)
        {
            flag1 = ((Fragment) (obj)).isDetached();
        } else
        {
            flag1 = false;
        }
        if (obj != null)
        {
            flag2 = ((Fragment) (obj)).isHidden();
        } else
        {
            flag2 = false;
        }
        Debug.Printf("MasterDetailsFragment: existing selector %b, detached %b, hidden %b", new Object[] {
            Boolean.valueOf(flag), Boolean.valueOf(flag1), Boolean.valueOf(flag2)
        });
        if (obj != null) goto _L4; else goto _L3
_L3:
        fragmenttransaction.add(0x7f100286, onCreateMasterFragment(getIntent(), null));
_L5:
        fragmenttransaction.commit();
        updateTitle();
        return false;
_L4:
        if (((Fragment) (obj)).isDetached())
        {
            fragmenttransaction.attach(((Fragment) (obj)));
        }
        if (((Fragment) (obj)).isHidden())
        {
            fragmenttransaction.show(((Fragment) (obj)));
        }
        if (true) goto _L5; else goto _L2
_L2:
        return true;
    }

    protected void onNewIntent(Intent intent)
    {
        Bundle bundle1 = null;
        super.onNewIntent(intent);
        Debug.Printf("MasterDetailsActivity: onNewIntent, intent = %s", new Object[] {
            intent
        });
        Bundle bundle;
        if (intent.hasExtra("selection"))
        {
            bundle = intent.getBundleExtra("selection");
        } else
        {
            bundle = null;
        }
        if (intent.hasExtra("weakSelection"))
        {
            bundle1 = intent.getBundleExtra("weakSelection");
        }
        if (bundle != null)
        {
            showDetails(this, getDetailsFragmentFactory(), bundle);
        } else
        {
            if (isSplitScreen && bundle1 != null)
            {
                showDetails(this, getDetailsFragmentFactory(), bundle1);
                return;
            }
            if (!isSplitScreen)
            {
                if (getSupportFragmentManager().findFragmentById(0x7f100114) == null && bundle1 != null && intent.getBooleanExtra("fromSameActivity", false))
                {
                    showDetails(this, getDetailsFragmentFactory(), bundle1);
                    return;
                } else
                {
                    clearDetailsStack();
                    onDetailsStackEmpty();
                    return;
                }
            }
        }
    }

    protected void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
    }

    protected void replaceDetailsFragment(FragmentManager fragmentmanager, Fragment fragment)
    {
        FragmentTransaction fragmenttransaction = fragmentmanager.beginTransaction();
        fragmenttransaction.setCustomAnimations(0x7f050010, 0x10a0001, 0, 0x10a0001);
        if (!isSplitScreen)
        {
            fragmentmanager = fragmentmanager.findFragmentById(0x7f100286);
            if (fragmentmanager != null && fragmentmanager.isVisible())
            {
                fragmenttransaction.hide(fragmentmanager);
            }
        }
        fragmenttransaction.replace(0x7f100114, fragment);
        fragmenttransaction.commit();
        updateTitle();
    }

    public Fragment showDetailsFragment(Class class1, Intent intent, Bundle bundle)
    {
        intent = super.showDetailsFragment(class1, intent, bundle);
        if (intent != null)
        {
            bundle = intent.getArguments();
            if (bundle != null)
            {
                bundle.putBoolean("MasterDetailsActivityIsImplicitDetails", isAlwaysImplicitFragment(class1));
            }
        }
        return intent;
    }

    protected void updateTitleNoDetails()
    {
        Object obj = getSupportFragmentManager().findFragmentById(0x7f100286);
        if (obj == null || !(obj instanceof FragmentHasTitle) || !((Fragment) (obj)).isAdded()) goto _L2; else goto _L1
_L1:
        if (!(((Fragment) (obj)).isDetached() ^ true)) goto _L4; else goto _L3
_L3:
        String s;
        s = ((FragmentHasTitle)obj).getTitle();
        obj = ((FragmentHasTitle)obj).getSubTitle();
        if (s == null) goto _L4; else goto _L5
_L5:
        boolean flag;
        flag = true;
        setActivityTitle(s, ((String) (obj)));
_L7:
        if (!flag)
        {
            super.updateTitleNoDetails();
        }
        return;
_L2:
        flag = false;
        continue; /* Loop/switch isn't completed */
_L4:
        flag = false;
        if (true) goto _L7; else goto _L6
_L6:
    }
}
