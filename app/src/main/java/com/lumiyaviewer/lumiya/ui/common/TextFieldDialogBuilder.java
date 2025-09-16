package com.lumiyaviewer.lumiya.ui.common;
import java.util.*;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.FrameLayout;

public class TextFieldDialogBuilder {
    private OnTextCancelledListener cancelledListener = null;
    private final Context context;
    private String defaultText = "";
    private OnTextEnteredListener listener = null;
    private String title = null;

    public interface OnTextCancelledListener {
        void onTextCancelled();
    }

    public interface OnTextEnteredListener {
        void onTextEntered(String str);
    }

    public TextFieldDialogBuilder(Context context2) {
        this.context = context2;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_TextFieldDialogBuilder_2200  reason: not valid java name */
    public /* synthetic */ void m549lambda$com_lumiyaviewer_lumiya_ui_common_TextFieldDialogBuilder_2200(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        if (this.cancelledListener != null) {
            this.cancelledListener.onTextCancelled();
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_TextFieldDialogBuilder_2416  reason: not valid java name */
    public /* synthetic */ void m550lambda$com_lumiyaviewer_lumiya_ui_common_TextFieldDialogBuilder_2416(EditText editText, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        if (this.listener != null) {
            this.listener.onTextEntered(editText.getText().toString());
        }
    }

    public TextFieldDialogBuilder setDefaultText(String str) {
        this.defaultText = str;
        return this;
    }

    public TextFieldDialogBuilder setOnTextCancelledListener(OnTextCancelledListener onTextCancelledListener) {
        this.cancelledListener = onTextCancelledListener;
        return this;
    }

    public TextFieldDialogBuilder setOnTextEnteredListener(OnTextEnteredListener onTextEnteredListener) {
        this.listener = onTextEnteredListener;
        return this;
    }

    public TextFieldDialogBuilder setTitle(String str) {
        this.title = str;
        return this;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle(this.title);
        EditText editText = new EditText(this.context);
        editText.setText(this.defaultText);
        editText.setSingleLine(true);
        FrameLayout frameLayout = new FrameLayout(this.context);
        int applyDimension = (int) TypedValue.applyDimension(1, 10.0f, this.context.getResources().getDisplayMetrics());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2);
        layoutParams.leftMargin = applyDimension;
        layoutParams.rightMargin = applyDimension;
        editText.setLayoutParams(layoutParams);
        frameLayout.addView(editText);
        builder.setView(frameLayout);
        builder.setNegativeButton("Cancel", new $Lambda$PTYOAfnVIwPVEdUoAgskdOeAqDw(this));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(this, editText) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f372$f0;

            /* renamed from: -$f1 */
            private final /* synthetic */ Object f373$f1;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.common.-$Lambda$PTYOAfnVIwPVEdUoAgskdOeAqDw.1.$m$0(android.content.DialogInterface, int):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.common.-$Lambda$PTYOAfnVIwPVEdUoAgskdOeAqDw.1.$m$0(android.content.DialogInterface, int):void, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

        builder.create().show();
    }
}
