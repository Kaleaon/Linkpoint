package com.lumiyaviewer.lumiya.ui.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public abstract class TextFieldEditFragment extends ChatterFragment implements BackButtonHandler {
    private MenuItem undoMenuItem;
    private String originalText = "";
    private boolean hasChanged = false;

    private void closeFragment() {
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).closeDetailsFragment(this);
        }
    }

    protected abstract String getFieldHint(Context context);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_TextFieldEditFragment_1854  reason: not valid java name */
    public /* synthetic */ void m567x95f8ee07(View view, DialogInterface dialogInterface, int i) {
        ((TextView) view.findViewById(R.id.field_edit_text)).setText(this.originalText);
        dialogInterface.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_TextFieldEditFragment_4960  reason: not valid java name */
    public /* synthetic */ void m568x95fa4f00(String str, DialogInterface dialogInterface, int i) {
        SLAgentCircuit activeAgentCircuit;
        dialogInterface.dismiss();
        if (this.userManager != null && this.chatterID != null && (activeAgentCircuit = this.userManager.getActiveAgentCircuit()) != null) {
            saveEditedText(activeAgentCircuit, this.chatterID, str);
        }
        closeFragment();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_TextFieldEditFragment_5518  reason: not valid java name */
    public /* synthetic */ void m569x95fab3c8(DialogInterface dialogInterface, int i) {
        dialogInterface.cancel();
        closeFragment();
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.BackButtonHandler
    public boolean onBackButtonPressed() {
        View view = getView();
        if (view != null) {
            final String charSequence = ((TextView) view.findViewById(R.id.field_edit_text)).getText().toString();
            if (Objects.equal(charSequence, this.originalText)) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getString(R.string.save_changes_question)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$DtZUcoBgRyVu-s24uOe08hwsuHo.2
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    ((TextFieldEditFragment) this).m568x95fa4f00((String) charSequence, dialogInterface, i);
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$DtZUcoBgRyVu-s24uOe08hwsuHo.1
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    ((TextFieldEditFragment) this).m569x95fab3c8(dialogInterface, i);
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            });
            builder.create().show();
            return true;
        }
        return false;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, android.support.v4.app.Fragment
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @Override // android.support.v4.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.user_notes_edit_menu, menu);
        this.undoMenuItem = menu.findItem(R.id.item_undo);
        this.undoMenuItem.setVisible(this.hasChanged);
    }

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.user_text_field_edit, viewGroup, false);
        final TextView textView = (TextView) inflate.findViewById(R.id.field_edit_text);
        textView.setHint(getFieldHint(layoutInflater.getContext()));
        textView.addTextChangedListener(new TextWatcher() { // from class: com.lumiyaviewer.lumiya.ui.common.TextFieldEditFragment.1
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                boolean z = !Objects.equal(textView.getText().toString(), TextFieldEditFragment.this.originalText);
                if (z != TextFieldEditFragment.this.hasChanged) {
                    TextFieldEditFragment.this.hasChanged = z;
                    if (TextFieldEditFragment.this.undoMenuItem != null) {
                        TextFieldEditFragment.this.undoMenuItem.setVisible(TextFieldEditFragment.this.hasChanged);
                    }
                }
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
        return inflate;
    }

    @Override // android.support.v4.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_undo /* 2131755778 */:
                final View view = getView();
                if (view != null && !Objects.equal(((TextView) view.findViewById(R.id.field_edit_text)).getText().toString(), this.originalText)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(getString(R.string.discard_changes_question)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$DtZUcoBgRyVu-s24uOe08hwsuHo.3
                        private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                            ((TextFieldEditFragment) this).m567x95f8ee07((View) view, dialogInterface, i);
                        }

                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            $m$0(dialogInterface, i);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$DtZUcoBgRyVu-s24uOe08hwsuHo
                        private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }

                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            $m$0(dialogInterface, i);
                        }
                    });
                    builder.create().show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    protected abstract void saveEditedText(SLAgentCircuit sLAgentCircuit, ChatterID chatterID, String str);

    /* JADX INFO: Access modifiers changed from: protected */
    public void setOriginalText(String str) {
        this.originalText = str;
        View view = getView();
        if (view != null) {
            ((TextView) view.findViewById(R.id.field_edit_text)).setText(str);
        }
    }
}
