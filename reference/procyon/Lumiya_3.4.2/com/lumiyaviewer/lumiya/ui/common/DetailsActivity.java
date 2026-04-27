// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import javax.annotation.Nonnull;
import android.os.Parcel;
import java.lang.ref.SoftReference;
import android.os.Parcelable$Creator;
import android.os.Parcelable;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import java.util.Iterator;
import java.util.List;
import android.support.annotation.NonNull;
import java.util.Collection;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import com.lumiyaviewer.lumiya.Debug;
import android.support.v4.app.FragmentManager;
import java.util.ArrayList;
import javax.annotation.Nullable;

public class DetailsActivity extends ConnectedActivity
{
    public static final String DEFAULT_DETAILS_FRAGMENT_TAG = "defaultDetails";
    private static final String DEFAULT_SUBTITLE_TAG = "DetailsActivity:defaultSubTitle";
    private static final String DEFAULT_TITLE_TAG = "DetailsActivity:defaultTitle";
    private static final String DETAILS_STACK_TAG = "DetailsActivity:DetailsStack";
    @Nullable
    private String defaultSubTitle;
    @Nullable
    private String defaultTitle;
    private final ArrayList<DetailsStackEntry> detailsStack;
    
    public DetailsActivity() {
        this.detailsStack = new ArrayList<DetailsStackEntry>();
        this.defaultTitle = null;
        this.defaultSubTitle = null;
    }
    
    private boolean goBack(final FragmentManager fragmentManager) {
        Debug.Printf("DetailsActivity: goBack, detailsStack size %d", this.detailsStack.size());
        if (this.detailsStack.size() != 0) {
            final DetailsStackEntry detailsStackEntry = this.detailsStack.remove(this.detailsStack.size() - 1);
            final FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
            beginTransaction.replace(2131755284, detailsStackEntry.getFragment((Context)this));
            beginTransaction.commit();
            this.updateTitle();
            return true;
        }
        final boolean onDetailsStackEmpty = this.onDetailsStackEmpty();
        Debug.Printf("DetailsActivity: goBack, onDetailsStackEmpty: really empty: %b", onDetailsStackEmpty);
        return onDetailsStackEmpty ^ true;
    }
    
    public static void showDetails(final Activity activity, final FragmentActivityFactory fragmentActivityFactory, final Bundle bundle) {
        if (!showEmbeddedDetails(activity, fragmentActivityFactory.getFragmentClass(), bundle)) {
            activity.startActivity(fragmentActivityFactory.createIntent((Context)activity, bundle));
        }
    }
    
    public static boolean showEmbeddedDetails(final Activity activity, final Class<? extends Fragment> clazz, final Bundle bundle) {
        boolean b;
        if (activity instanceof DetailsActivity && ((DetailsActivity)activity).acceptsDetailFragment(clazz)) {
            ((DetailsActivity)activity).showDetailsFragment(clazz, activity.getIntent(), bundle);
            b = true;
        }
        else {
            b = false;
        }
        return b;
    }
    
    protected boolean acceptsDetailFragment(final Class<? extends Fragment> clazz) {
        return true;
    }
    
    protected void addDetailsToStack(final FragmentManager fragmentManager) {
        final Fragment fragmentById = fragmentManager.findFragmentById(2131755284);
        if (fragmentById != null) {
            this.detailsStack.add(new DetailsStackEntry(fragmentById, null));
        }
    }
    
    void clearDetailsStack() {
        this.detailsStack.clear();
    }
    
    public boolean closeDetailsFragment(final Fragment fragment) {
        final FragmentManager supportFragmentManager = this.getSupportFragmentManager();
        return supportFragmentManager.findFragmentById(2131755284) == fragment && this.goBack(supportFragmentManager);
    }
    
    @Nullable
    public Fragment getCurrentDetailsFragment() {
        final FragmentManager supportFragmentManager = this.getSupportFragmentManager();
        if (supportFragmentManager != null) {
            final Fragment fragmentById = supportFragmentManager.findFragmentById(2131755284);
            if (fragmentById != null && fragmentById.isAdded() && (fragmentById.isDetached() ^ true) && (fragmentById.isHidden() ^ true)) {
                return fragmentById;
            }
        }
        return null;
    }
    
    public boolean handleBackPressed() {
        final FragmentManager supportFragmentManager = this.getSupportFragmentManager();
        final Fragment fragmentById = supportFragmentManager.findFragmentById(2131755284);
        return (fragmentById instanceof BackButtonHandler && fragmentById.isAdded() && (fragmentById.isDetached() ^ true) && ((BackButtonHandler)fragmentById).onBackButtonPressed()) || (supportFragmentManager.getBackStackEntryCount() == 0 && this.goBack(supportFragmentManager));
    }
    
    protected boolean isRootDetailsFragment(final Class<? extends Fragment> clazz) {
        return true;
    }
    
    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            final ArrayList parcelableArrayList = bundle.getParcelableArrayList("DetailsActivity:DetailsStack");
            if (parcelableArrayList != null) {
                this.detailsStack.addAll(parcelableArrayList);
            }
            this.defaultTitle = bundle.getString("DetailsActivity:defaultTitle");
            this.defaultSubTitle = bundle.getString("DetailsActivity:defaultSubTitle");
        }
    }
    
    protected boolean onDetailsStackEmpty() {
        final FragmentManager supportFragmentManager = this.getSupportFragmentManager();
        final Fragment fragmentById = supportFragmentManager.findFragmentById(2131755284);
        if (fragmentById != null) {
            final FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
            beginTransaction.remove(fragmentById);
            beginTransaction.commit();
            this.updateTitle();
            return false;
        }
        return true;
    }
    
    public void onFragmentTitleUpdated() {
        this.updateTitle();
    }
    
    @Override
    protected void onPostCreate(@android.support.annotation.Nullable final Bundle bundle) {
        super.onPostCreate(bundle);
        this.updateTitle();
    }
    
    @Override
    public void onRequestPermissionsResult(final int n, @NonNull final String[] array, @NonNull final int[] array2) {
        super.onRequestPermissionsResult(n, array, array2);
        final List<Fragment> fragments = this.getSupportFragmentManager().getFragments();
        if (fragments != null) {
            final Iterator<Object> iterator = fragments.iterator();
            while (iterator.hasNext()) {
                iterator.next().onRequestPermissionsResult(n, array, array2);
            }
        }
    }
    
    @Override
    protected void onSaveInstanceState(final Bundle bundle) {
        bundle.putParcelableArrayList("DetailsActivity:DetailsStack", (ArrayList)this.detailsStack);
        bundle.putString("DetailsActivity:defaultTitle", this.defaultTitle);
        bundle.putString("DetailsActivity:defaultSubTitle", this.defaultSubTitle);
        super.onSaveInstanceState(bundle);
    }
    
    protected void removeAllDetails() {
        final FragmentManager supportFragmentManager = this.getSupportFragmentManager();
        if (supportFragmentManager.findFragmentById(2131755284) != null) {
            this.clearDetailsStack();
            this.goBack(supportFragmentManager);
        }
    }
    
    protected void replaceDetailsFragment(final FragmentManager fragmentManager, final Fragment fragment) {
        final FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        beginTransaction.setCustomAnimations(2131034128, 0, 0, 2131034130);
        beginTransaction.replace(2131755284, fragment);
        beginTransaction.setTransition(4097);
        beginTransaction.commit();
        this.updateTitle();
    }
    
    protected void setActivityTitle(@Nullable final String s, @Nullable final String subtitle) {
        final ActionBar supportActionBar = this.getSupportActionBar();
        Debug.Printf("updateTitle: title '%s' actionBar %s", s, supportActionBar);
        if (supportActionBar != null) {
            supportActionBar.setTitle(s);
            supportActionBar.setSubtitle(subtitle);
        }
        this.setTitle((CharSequence)s);
    }
    
    public boolean setCurrentDetailsArguments(final Class<? extends Fragment> clazz, final Bundle bundle) {
        final FragmentManager supportFragmentManager = this.getSupportFragmentManager();
        if (supportFragmentManager != null) {
            final Fragment fragmentById = supportFragmentManager.findFragmentById(2131755284);
            if (fragmentById != null && clazz.isInstance(fragmentById) && fragmentById instanceof ReloadableFragment && fragmentById.getArguments() != null) {
                ((ReloadableFragment)fragmentById).setFragmentArgs(this.getIntent(), bundle);
                return true;
            }
        }
        return false;
    }
    
    public void setDefaultTitle(@Nullable final String defaultTitle, @Nullable final String defaultSubTitle) {
        this.defaultTitle = defaultTitle;
        this.defaultSubTitle = defaultSubTitle;
        this.updateTitle();
    }
    
    @Nullable
    public Fragment showDetailsFragment(Class<? extends Fragment> o, final Intent ex, final Bundle arguments) {
        Debug.Printf("DetailsActivity: fragmentClass %s, intent %s, arguments %s", ((Class)o).toString(), ex, arguments);
        final FragmentManager supportFragmentManager = this.getSupportFragmentManager();
        Label_0286: {
            if (supportFragmentManager == null) {
                break Label_0286;
            }
            final boolean rootDetailsFragment = this.isRootDetailsFragment((Class<? extends Fragment>)o);
            Object fragmentById = supportFragmentManager.findFragmentById(2131755284);
            Debug.Printf("DetailsActivity: isRootFragment %b existing fragment: %s", rootDetailsFragment, fragmentById);
            if (fragmentById != null) {
                Debug.Printf("DetailsActivity: is good instance: %b", ((Class)o).isInstance(fragmentById));
                Debug.Printf("DetailsActivity: is reloadable: %b", fragmentById instanceof ReloadableFragment);
                Debug.Printf("DetailsActivity: has arguments: %b", ((Fragment)fragmentById).getArguments());
            }
            Label_0261: {
                Block_9: {
                    if (fragmentById != null && ((Fragment)fragmentById).isVisible() && ((Class)o).isInstance(fragmentById) && fragmentById instanceof ReloadableFragment && ((Fragment)fragmentById).getArguments() != null) {
                        ((ReloadableFragment)fragmentById).setFragmentArgs((Intent)ex, arguments);
                        this.invalidateOptionsMenu();
                        o = fragmentById;
                    }
                    else {
                        if (rootDetailsFragment) {
                            this.clearDetailsStack();
                            break Block_9;
                        }
                        break Label_0261;
                    }
                    return (Fragment)o;
                }
                while (true) {
                    try {
                        while (true) {
                            o = ((Class)o).newInstance();
                            try {
                                if (o instanceof ReloadableFragment) {
                                    fragmentById = new Bundle();
                                    ((Fragment)o).setArguments((Bundle)fragmentById);
                                    ((ReloadableFragment)o).setFragmentArgs((Intent)ex, arguments);
                                }
                                else {
                                    ((Fragment)o).setArguments(arguments);
                                }
                                this.replaceDetailsFragment(supportFragmentManager, (Fragment)o);
                                return (Fragment)o;
                                this.addDetailsToStack(supportFragmentManager);
                                continue;
                            }
                            catch (final Exception ex2) {}
                            break;
                        }
                        Debug.Warning(ex);
                        return (Fragment)o;
                        return null;
                    }
                    catch (final Exception ex) {
                        o = fragmentById;
                        continue;
                    }
                    break;
                }
            }
        }
    }
    
    protected void updateTitle() {
        final FragmentManager supportFragmentManager = this.getSupportFragmentManager();
        while (true) {
            Label_0166: {
                if (supportFragmentManager == null) {
                    break Label_0166;
                }
                final Fragment fragmentById = supportFragmentManager.findFragmentById(2131755284);
                Debug.Printf("updateTitle: detailsFragment %s", fragmentById);
                if (!(fragmentById instanceof FragmentHasTitle)) {
                    break Label_0166;
                }
                Debug.Printf("updateTitle: detailsFragment added %b hidden %b detached %b", fragmentById.isAdded(), fragmentById.isHidden(), fragmentById.isDetached());
                int n;
                if (fragmentById.isAdded() && (fragmentById.isHidden() ^ true)) {
                    if (!(fragmentById.isDetached() ^ true)) {
                        break Label_0166;
                    }
                    final String title = ((FragmentHasTitle)fragmentById).getTitle();
                    final String subTitle = ((FragmentHasTitle)fragmentById).getSubTitle();
                    Debug.Printf("updateTitle: got title '%s', subtitle '%s'", title, subTitle);
                    if (title == null) {
                        break Label_0166;
                    }
                    this.setActivityTitle(title, subTitle);
                    n = 1;
                }
                else {
                    n = 0;
                }
                if (n == 0) {
                    this.updateTitleNoDetails();
                }
                return;
            }
            int n = 0;
            continue;
        }
    }
    
    protected void updateTitleNoDetails() {
        if (this.defaultTitle != null) {
            this.setActivityTitle(this.defaultTitle, this.defaultSubTitle);
        }
    }
    
    private static class DetailsStackEntry implements Parcelable
    {
        public static final Parcelable$Creator<DetailsStackEntry> CREATOR;
        public final Bundle arguments;
        public final String className;
        public final SoftReference<Fragment> fragment;
        public final Fragment.SavedState savedState;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<DetailsStackEntry>() {
                public DetailsStackEntry createFromParcel(final Parcel parcel) {
                    return new DetailsStackEntry(parcel);
                }
                
                public DetailsStackEntry[] newArray(final int n) {
                    return new DetailsStackEntry[n];
                }
            };
        }
        
        protected DetailsStackEntry(final Parcel parcel) {
            this.fragment = null;
            this.className = parcel.readString();
            if (parcel.readByte() != 0) {
                this.arguments = parcel.readBundle(this.getClass().getClassLoader());
            }
            else {
                this.arguments = null;
            }
            if (parcel.readByte() != 0) {
                this.savedState = (Fragment.SavedState)parcel.readBundle(this.getClass().getClassLoader()).getParcelable("savedState");
            }
            else {
                this.savedState = null;
            }
        }
        
        private DetailsStackEntry(@Nonnull final Fragment referent) {
            this.fragment = new SoftReference<Fragment>(referent);
            this.className = referent.getClass().getName();
            this.arguments = referent.getArguments();
            final FragmentManager fragmentManager = referent.getFragmentManager();
            if (fragmentManager != null) {
                this.savedState = fragmentManager.saveFragmentInstanceState(referent);
            }
            else {
                this.savedState = null;
            }
        }
        
        public int describeContents() {
            return 0;
        }
        
        public Fragment getFragment(final Context context) {
            Fragment instantiate;
            if ((instantiate = this.fragment.get()) == null) {
                final Fragment fragment = instantiate = Fragment.instantiate(context, this.className, this.arguments);
                if (this.savedState != null) {
                    fragment.setInitialSavedState(this.savedState);
                    instantiate = fragment;
                }
            }
            return instantiate;
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            parcel.writeString(this.className);
            if (this.arguments != null) {
                parcel.writeByte((byte)1);
                parcel.writeBundle(this.arguments);
            }
            else {
                parcel.writeByte((byte)0);
            }
            if (this.savedState != null) {
                parcel.writeByte((byte)1);
                final Bundle bundle = new Bundle();
                bundle.putParcelable("savedState", (Parcelable)this.savedState);
                parcel.writeBundle(bundle);
            }
            else {
                parcel.writeByte((byte)0);
            }
        }
    }
}
