// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.view.ViewParent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.EditorInfo;
import android.util.AttributeSet;
import android.content.Context;
import android.support.v7.widget.AppCompatEditText;

public class TextInputEditText extends AppCompatEditText
{
    public TextInputEditText(final Context context) {
        super(context);
    }
    
    public TextInputEditText(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public TextInputEditText(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    public InputConnection onCreateInputConnection(final EditorInfo editorInfo) {
        final InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
        if (onCreateInputConnection != null && editorInfo.hintText == null) {
            for (ViewParent viewParent = this.getParent(); viewParent instanceof View; viewParent = viewParent.getParent()) {
                if (viewParent instanceof TextInputLayout) {
                    editorInfo.hintText = ((TextInputLayout)viewParent).getHint();
                    break;
                }
            }
        }
        return onCreateInputConnection;
    }
}
