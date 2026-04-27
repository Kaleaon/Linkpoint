// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.Menu;
import javax.annotation.Nullable;
import android.os.Bundle;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import android.widget.TextView;
import android.view.View;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

public abstract class TextFieldEditFragment extends ChatterFragment implements BackButtonHandler
{
    private boolean hasChanged;
    private String originalText;
    private MenuItem undoMenuItem;
    
    public TextFieldEditFragment() {
        this.originalText = "";
        this.hasChanged = false;
    }
    
    private void closeFragment() {
        final FragmentActivity activity = this.getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity)activity).closeDetailsFragment(this);
        }
    }
    
    protected abstract String getFieldHint(final Context p0);
    
    @Override
    public boolean onBackButtonPressed() {
        final View view = this.getView();
        if (view != null) {
            final String string = ((TextView)view.findViewById(2131755727)).getText().toString();
            if (!Objects.equal(string, this.originalText)) {
                final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
                alertDialog$Builder.setMessage((CharSequence)this.getString(2131296992)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$DtZUcoBgRyVu_s24uOe08hwsuHo$2(this, string)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$DtZUcoBgRyVu_s24uOe08hwsuHo$1(this));
                alertDialog$Builder.create().show();
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        this.setHasOptionsMenu(true);
    }
    
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(2131886117, menu);
        (this.undoMenuItem = menu.findItem(2131755778)).setVisible(this.hasChanged);
    }
    
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968762, viewGroup, false);
        final TextView textView = (TextView)inflate.findViewById(2131755727);
        textView.setHint((CharSequence)this.getFieldHint(layoutInflater.getContext()));
        textView.addTextChangedListener((TextWatcher)new TextWatcher() {
            public void afterTextChanged(final Editable editable) {
                final boolean b = Objects.equal(textView.getText().toString(), TextFieldEditFragment.this.originalText) ^ true;
                if (b != TextFieldEditFragment.this.hasChanged) {
                    TextFieldEditFragment.this.hasChanged = b;
                    if (TextFieldEditFragment.this.undoMenuItem != null) {
                        TextFieldEditFragment.this.undoMenuItem.setVisible(TextFieldEditFragment.this.hasChanged);
                    }
                }
            }
            
            public void beforeTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
            }
            
            public void onTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
            }
        });
        return inflate;
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            default: {
                return super.onOptionsItemSelected(menuItem);
            }
            case 2131755778: {
                final View view = this.getView();
                if (view != null && !Objects.equal(((TextView)view.findViewById(2131755727)).getText().toString(), this.originalText)) {
                    final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
                    alertDialog$Builder.setMessage((CharSequence)this.getString(2131296512)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$DtZUcoBgRyVu_s24uOe08hwsuHo$3(this, view)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$DtZUcoBgRyVu_s24uOe08hwsuHo());
                    alertDialog$Builder.create().show();
                }
                return true;
            }
        }
    }
    
    protected abstract void saveEditedText(final SLAgentCircuit p0, final ChatterID p1, final String p2);
    
    protected void setOriginalText(final String s) {
        this.originalText = s;
        final View view = this.getView();
        if (view != null) {
            ((TextView)view.findViewById(2131755727)).setText((CharSequence)s);
        }
    }
}
