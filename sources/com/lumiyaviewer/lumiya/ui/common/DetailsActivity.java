// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import com.lumiyaviewer.lumiya.Debug;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            ConnectedActivity, FragmentActivityFactory, BackButtonHandler, ReloadableFragment, 
//            FragmentHasTitle

public class DetailsActivity extends ConnectedActivity
{
    private static class DetailsStackEntry
        implements Parcelable
    {

        public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

            public DetailsStackEntry createFromParcel(Parcel parcel)
            {
                return new DetailsStackEntry(parcel);
            }

            public volatile Object createFromParcel(Parcel parcel)
            {
                return createFromParcel(parcel);
            }

            public DetailsStackEntry[] newArray(int i)
            {
                return new DetailsStackEntry[i];
            }

            public volatile Object[] newArray(int i)
            {
                return newArray(i);
            }

        };
        public final Bundle arguments;
        public final String className;
        public final SoftReference fragment;
        public final android.support.v4.app.Fragment.SavedState savedState;

        public int describeContents()
        {
            return 0;
        }

        public Fragment getFragment(Context context)
        {
            Fragment fragment1 = (Fragment)fragment.get();
            Object obj = fragment1;
            if (fragment1 == null)
            {
                context = Fragment.instantiate(context, className, arguments);
                obj = context;
                if (savedState != null)
                {
                    context.setInitialSavedState(savedState);
                    obj = context;
                }
            }
            return ((Fragment) (obj));
        }

        public void writeToParcel(Parcel parcel, int i)
        {
            parcel.writeString(className);
            if (arguments != null)
            {
                parcel.writeByte((byte)1);
                parcel.writeBundle(arguments);
            } else
            {
                parcel.writeByte((byte)0);
            }
            if (savedState != null)
            {
                parcel.writeByte((byte)1);
                Bundle bundle = new Bundle();
                bundle.putParcelable("savedState", savedState);
                parcel.writeBundle(bundle);
                return;
            } else
            {
                parcel.writeByte((byte)0);
                return;
            }
        }


        protected DetailsStackEntry(Parcel parcel)
        {
            fragment = null;
            className = parcel.readString();
            if (parcel.readByte() != 0)
            {
                arguments = parcel.readBundle(getClass().getClassLoader());
            } else
            {
                arguments = null;
            }
            if (parcel.readByte() != 0)
            {
                savedState = (android.support.v4.app.Fragment.SavedState)parcel.readBundle(getClass().getClassLoader()).getParcelable("savedState");
                return;
            } else
            {
                savedState = null;
                return;
            }
        }

        private DetailsStackEntry(Fragment fragment1)
        {
            fragment = new SoftReference(fragment1);
            className = fragment1.getClass().getName();
            arguments = fragment1.getArguments();
            FragmentManager fragmentmanager = fragment1.getFragmentManager();
            if (fragmentmanager != null)
            {
                savedState = fragmentmanager.saveFragmentInstanceState(fragment1);
                return;
            } else
            {
                savedState = null;
                return;
            }
        }

        DetailsStackEntry(Fragment fragment1, DetailsStackEntry detailsstackentry)
        {
            this(fragment1);
        }
    }


    public static final String DEFAULT_DETAILS_FRAGMENT_TAG = "defaultDetails";
    private static final String DEFAULT_SUBTITLE_TAG = "DetailsActivity:defaultSubTitle";
    private static final String DEFAULT_TITLE_TAG = "DetailsActivity:defaultTitle";
    private static final String DETAILS_STACK_TAG = "DetailsActivity:DetailsStack";
    private String defaultSubTitle;
    private String defaultTitle;
    private final ArrayList detailsStack = new ArrayList();

    public DetailsActivity()
    {
        defaultTitle = null;
        defaultSubTitle = null;
    }

    private boolean goBack(FragmentManager fragmentmanager)
    {
        Debug.Printf("DetailsActivity: goBack, detailsStack size %d", new Object[] {
            Integer.valueOf(detailsStack.size())
        });
        if (detailsStack.size() != 0)
        {
            DetailsStackEntry detailsstackentry = (DetailsStackEntry)detailsStack.remove(detailsStack.size() - 1);
            fragmentmanager = fragmentmanager.beginTransaction();
            fragmentmanager.replace(0x7f100114, detailsstackentry.getFragment(this));
            fragmentmanager.commit();
            updateTitle();
            return true;
        } else
        {
            boolean flag = onDetailsStackEmpty();
            Debug.Printf("DetailsActivity: goBack, onDetailsStackEmpty: really empty: %b", new Object[] {
                Boolean.valueOf(flag)
            });
            return flag ^ true;
        }
    }

    public static void showDetails(Activity activity, FragmentActivityFactory fragmentactivityfactory, Bundle bundle)
    {
        if (!showEmbeddedDetails(activity, fragmentactivityfactory.getFragmentClass(), bundle))
        {
            activity.startActivity(fragmentactivityfactory.createIntent(activity, bundle));
        }
    }

    public static boolean showEmbeddedDetails(Activity activity, Class class1, Bundle bundle)
    {
        if ((activity instanceof DetailsActivity) && ((DetailsActivity)activity).acceptsDetailFragment(class1))
        {
            ((DetailsActivity)activity).showDetailsFragment(class1, activity.getIntent(), bundle);
            return true;
        } else
        {
            return false;
        }
    }

    protected boolean acceptsDetailFragment(Class class1)
    {
        return true;
    }

    protected void addDetailsToStack(FragmentManager fragmentmanager)
    {
        fragmentmanager = fragmentmanager.findFragmentById(0x7f100114);
        if (fragmentmanager != null)
        {
            detailsStack.add(new DetailsStackEntry(fragmentmanager, null));
        }
    }

    void clearDetailsStack()
    {
        detailsStack.clear();
    }

    public boolean closeDetailsFragment(Fragment fragment)
    {
        FragmentManager fragmentmanager = getSupportFragmentManager();
        if (fragmentmanager.findFragmentById(0x7f100114) == fragment)
        {
            return goBack(fragmentmanager);
        } else
        {
            return false;
        }
    }

    public Fragment getCurrentDetailsFragment()
    {
        Object obj = getSupportFragmentManager();
        if (obj != null)
        {
            obj = ((FragmentManager) (obj)).findFragmentById(0x7f100114);
            if (obj != null && ((Fragment) (obj)).isAdded() && ((Fragment) (obj)).isDetached() ^ true && ((Fragment) (obj)).isHidden() ^ true)
            {
                return ((Fragment) (obj));
            }
        }
        return null;
    }

    public boolean handleBackPressed()
    {
        FragmentManager fragmentmanager = getSupportFragmentManager();
        Fragment fragment = fragmentmanager.findFragmentById(0x7f100114);
        if ((fragment instanceof BackButtonHandler) && fragment.isAdded() && fragment.isDetached() ^ true && ((BackButtonHandler)fragment).onBackButtonPressed())
        {
            return true;
        }
        if (fragmentmanager.getBackStackEntryCount() != 0)
        {
            return false;
        } else
        {
            return goBack(fragmentmanager);
        }
    }

    protected boolean isRootDetailsFragment(Class class1)
    {
        return true;
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if (bundle != null)
        {
            ArrayList arraylist = bundle.getParcelableArrayList("DetailsActivity:DetailsStack");
            if (arraylist != null)
            {
                detailsStack.addAll(arraylist);
            }
            defaultTitle = bundle.getString("DetailsActivity:defaultTitle");
            defaultSubTitle = bundle.getString("DetailsActivity:defaultSubTitle");
        }
    }

    protected boolean onDetailsStackEmpty()
    {
        Object obj = getSupportFragmentManager();
        Fragment fragment = ((FragmentManager) (obj)).findFragmentById(0x7f100114);
        if (fragment != null)
        {
            obj = ((FragmentManager) (obj)).beginTransaction();
            ((FragmentTransaction) (obj)).remove(fragment);
            ((FragmentTransaction) (obj)).commit();
            updateTitle();
            return false;
        } else
        {
            return true;
        }
    }

    public void onFragmentTitleUpdated()
    {
        updateTitle();
    }

    protected void onPostCreate(Bundle bundle)
    {
        super.onPostCreate(bundle);
        updateTitle();
    }

    public void onRequestPermissionsResult(int i, String as[], int ai[])
    {
        super.onRequestPermissionsResult(i, as, ai);
        Object obj = getSupportFragmentManager().getFragments();
        if (obj != null)
        {
            for (obj = ((Iterable) (obj)).iterator(); ((Iterator) (obj)).hasNext(); ((Fragment)((Iterator) (obj)).next()).onRequestPermissionsResult(i, as, ai)) { }
        }
    }

    protected void onSaveInstanceState(Bundle bundle)
    {
        bundle.putParcelableArrayList("DetailsActivity:DetailsStack", detailsStack);
        bundle.putString("DetailsActivity:defaultTitle", defaultTitle);
        bundle.putString("DetailsActivity:defaultSubTitle", defaultSubTitle);
        super.onSaveInstanceState(bundle);
    }

    protected void removeAllDetails()
    {
        FragmentManager fragmentmanager = getSupportFragmentManager();
        if (fragmentmanager.findFragmentById(0x7f100114) != null)
        {
            clearDetailsStack();
            goBack(fragmentmanager);
        }
    }

    protected void replaceDetailsFragment(FragmentManager fragmentmanager, Fragment fragment)
    {
        fragmentmanager = fragmentmanager.beginTransaction();
        fragmentmanager.setCustomAnimations(0x7f050010, 0, 0, 0x7f050012);
        fragmentmanager.replace(0x7f100114, fragment);
        fragmentmanager.setTransition(4097);
        fragmentmanager.commit();
        updateTitle();
    }

    protected void setActivityTitle(String s, String s1)
    {
        ActionBar actionbar = getSupportActionBar();
        Debug.Printf("updateTitle: title '%s' actionBar %s", new Object[] {
            s, actionbar
        });
        if (actionbar != null)
        {
            actionbar.setTitle(s);
            actionbar.setSubtitle(s1);
        }
        setTitle(s);
    }

    public boolean setCurrentDetailsArguments(Class class1, Bundle bundle)
    {
        Object obj = getSupportFragmentManager();
        if (obj != null)
        {
            obj = ((FragmentManager) (obj)).findFragmentById(0x7f100114);
            if (obj != null && class1.isInstance(obj) && (obj instanceof ReloadableFragment) && ((Fragment) (obj)).getArguments() != null)
            {
                ((ReloadableFragment)obj).setFragmentArgs(getIntent(), bundle);
                return true;
            }
        }
        return false;
    }

    public void setDefaultTitle(String s, String s1)
    {
        defaultTitle = s;
        defaultSubTitle = s1;
        updateTitle();
    }

    public Fragment showDetailsFragment(Class class1, Intent intent, Bundle bundle)
    {
        FragmentManager fragmentmanager;
        Debug.Printf("DetailsActivity: fragmentClass %s, intent %s, arguments %s", new Object[] {
            class1.toString(), intent, bundle
        });
        fragmentmanager = getSupportFragmentManager();
        if (fragmentmanager == null) goto _L2; else goto _L1
_L1:
        Fragment fragment;
        boolean flag = isRootDetailsFragment(class1);
        fragment = fragmentmanager.findFragmentById(0x7f100114);
        Debug.Printf("DetailsActivity: isRootFragment %b existing fragment: %s", new Object[] {
            Boolean.valueOf(flag), fragment
        });
        if (fragment != null)
        {
            Debug.Printf("DetailsActivity: is good instance: %b", new Object[] {
                Boolean.valueOf(class1.isInstance(fragment))
            });
            Debug.Printf("DetailsActivity: is reloadable: %b", new Object[] {
                Boolean.valueOf(fragment instanceof ReloadableFragment)
            });
            Debug.Printf("DetailsActivity: has arguments: %b", new Object[] {
                fragment.getArguments()
            });
        }
        if (fragment != null && fragment.isVisible() && class1.isInstance(fragment) && (fragment instanceof ReloadableFragment) && fragment.getArguments() != null)
        {
            ((ReloadableFragment)fragment).setFragmentArgs(intent, bundle);
            invalidateOptionsMenu();
            return fragment;
        }
        if (flag)
        {
            clearDetailsStack();
        } else
        {
            addDetailsToStack(fragmentmanager);
        }
        class1 = (Fragment)class1.newInstance();
        if (!(class1 instanceof ReloadableFragment))
        {
            break MISSING_BLOCK_LABEL_262;
        }
        class1.setArguments(new Bundle());
        ((ReloadableFragment)class1).setFragmentArgs(intent, bundle);
_L3:
        replaceDetailsFragment(fragmentmanager, class1);
        return class1;
        class1.setArguments(bundle);
          goto _L3
        intent;
_L5:
        Debug.Warning(intent);
        return class1;
_L2:
        return null;
        intent;
        class1 = fragment;
        if (true) goto _L5; else goto _L4
_L4:
    }

    protected void updateTitle()
    {
        Object obj = getSupportFragmentManager();
        if (obj == null) goto _L2; else goto _L1
_L1:
        Object obj1;
        obj1 = ((FragmentManager) (obj)).findFragmentById(0x7f100114);
        Debug.Printf("updateTitle: detailsFragment %s", new Object[] {
            obj1
        });
        if (!(obj1 instanceof FragmentHasTitle)) goto _L2; else goto _L3
_L3:
        Debug.Printf("updateTitle: detailsFragment added %b hidden %b detached %b", new Object[] {
            Boolean.valueOf(((Fragment) (obj1)).isAdded()), Boolean.valueOf(((Fragment) (obj1)).isHidden()), Boolean.valueOf(((Fragment) (obj1)).isDetached())
        });
        if (!((Fragment) (obj1)).isAdded() || !(((Fragment) (obj1)).isHidden() ^ true)) goto _L5; else goto _L4
_L4:
        if (!(((Fragment) (obj1)).isDetached() ^ true)) goto _L2; else goto _L6
_L6:
        obj = ((FragmentHasTitle)obj1).getTitle();
        obj1 = ((FragmentHasTitle)obj1).getSubTitle();
        Debug.Printf("updateTitle: got title '%s', subtitle '%s'", new Object[] {
            obj, obj1
        });
        if (obj == null) goto _L2; else goto _L7
_L7:
        boolean flag;
        setActivityTitle(((String) (obj)), ((String) (obj1)));
        flag = true;
_L9:
        if (!flag)
        {
            updateTitleNoDetails();
        }
        return;
_L5:
        flag = false;
        continue; /* Loop/switch isn't completed */
_L2:
        flag = false;
        if (true) goto _L9; else goto _L8
_L8:
    }

    protected void updateTitleNoDetails()
    {
        if (defaultTitle != null)
        {
            setActivityTitle(defaultTitle, defaultSubTitle);
        }
    }
}
