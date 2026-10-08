package com.nineoldandroids.animation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.animation.AnimationUtils;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class AnimatorInflater {
    private static final int AnimatorSet_ordering = 0;
    private static final int Animator_duration = 1;
    private static final int Animator_interpolator = 0;
    private static final int Animator_repeatCount = 3;
    private static final int Animator_repeatMode = 4;
    private static final int Animator_startOffset = 2;
    private static final int Animator_valueFrom = 5;
    private static final int Animator_valueTo = 6;
    private static final int Animator_valueType = 7;
    private static final int PropertyAnimator_propertyName = 0;
    private static final int TOGETHER = 0;
    private static final int VALUE_TYPE_FLOAT = 0;
    private static final int[] AnimatorSet = {16843490};
    private static final int[] PropertyAnimator = {16843489};
    private static final int[] Animator = {16843073, 16843160, 16843198, 16843199, 16843200, 16843486, 16843487, 16843488};

    private static Animator createAnimatorFromXml(Context context, XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        return createAnimatorFromXml(context, xmlPullParser, Xml.asAttributeSet(xmlPullParser), null, 0);
    }

    /* JADX WARN: Code restructure failed: missing block: B:35:0x009f, code lost:
        if (r2 == null) goto L36;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x00a1, code lost:
        r3 = new com.nineoldandroids.animation.Animator[r2.size()];
        r4 = r2.iterator();
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00af, code lost:
        if (r4.hasNext() != false) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00b1, code lost:
        if (r13 == 0) goto L45;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00b3, code lost:
        r12.playSequentially(r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x00b8, code lost:
        r3[r5] = (com.nineoldandroids.animation.Animator) r4.next();
        r5 = r5 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x00c4, code lost:
        r12.playTogether(r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0012, code lost:
        if (r12 != null) goto L37;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0014, code lost:
        return r1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static com.nineoldandroids.animation.Animator createAnimatorFromXml(android.content.Context r9, org.xmlpull.v1.XmlPullParser r10, android.util.AttributeSet r11, com.nineoldandroids.animation.AnimatorSet r12, int r13) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r3 = 0
            r5 = 0
            int r6 = r10.getDepth()
            r2 = r3
            r1 = r3
        L8:
            int r0 = r10.next()
            r4 = 3
            if (r0 == r4) goto L15
        Lf:
            r4 = 1
            if (r0 != r4) goto L1c
        L12:
            if (r12 != 0) goto L9f
        L14:
            return r1
        L15:
            int r4 = r10.getDepth()
            if (r4 > r6) goto Lf
            goto L12
        L1c:
            r4 = 2
            if (r0 != r4) goto L8
            java.lang.String r0 = r10.getName()
            java.lang.String r1 = "objectAnimator"
            boolean r1 = r0.equals(r1)
            if (r1 != 0) goto L5c
            java.lang.String r1 = "animator"
            boolean r1 = r0.equals(r1)
            if (r1 != 0) goto L65
            java.lang.String r1 = "set"
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L6a
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Unknown animator name: "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = r10.getName()
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L5c:
            com.nineoldandroids.animation.ObjectAnimator r1 = loadObjectAnimator(r9, r11)
        L60:
            if (r12 != 0) goto L92
            r0 = r2
        L63:
            r2 = r0
            goto L8
        L65:
            com.nineoldandroids.animation.ValueAnimator r1 = loadAnimator(r9, r11, r3)
            goto L60
        L6a:
            com.nineoldandroids.animation.AnimatorSet r1 = new com.nineoldandroids.animation.AnimatorSet
            r1.<init>()
            int[] r0 = com.nineoldandroids.animation.AnimatorInflater.AnimatorSet
            android.content.res.TypedArray r7 = r9.obtainStyledAttributes(r11, r0)
            android.util.TypedValue r0 = new android.util.TypedValue
            r0.<init>()
            r7.getValue(r5, r0)
            int r4 = r0.type
            r8 = 16
            if (r4 == r8) goto L8e
            r4 = r5
        L84:
            r0 = r1
            com.nineoldandroids.animation.AnimatorSet r0 = (com.nineoldandroids.animation.AnimatorSet) r0
            createAnimatorFromXml(r9, r10, r11, r0, r4)
            r7.recycle()
            goto L60
        L8e:
            int r0 = r0.data
            r4 = r0
            goto L84
        L92:
            if (r2 == 0) goto L99
        L94:
            r2.add(r1)
            r0 = r2
            goto L63
        L99:
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            goto L94
        L9f:
            if (r2 == 0) goto L14
            int r0 = r2.size()
            com.nineoldandroids.animation.Animator[] r3 = new com.nineoldandroids.animation.Animator[r0]
            java.util.Iterator r4 = r2.iterator()
        Lab:
            boolean r0 = r4.hasNext()
            if (r0 != 0) goto Lb8
            if (r13 == 0) goto Lc4
            r12.playSequentially(r3)
            goto L14
        Lb8:
            java.lang.Object r0 = r4.next()
            com.nineoldandroids.animation.Animator r0 = (com.nineoldandroids.animation.Animator) r0
            int r2 = r5 + 1
            r3[r5] = r0
            r5 = r2
            goto Lab
        Lc4:
            r12.playTogether(r3)
            goto L14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nineoldandroids.animation.AnimatorInflater.createAnimatorFromXml(android.content.Context, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, com.nineoldandroids.animation.AnimatorSet, int):com.nineoldandroids.animation.Animator");
    }

    public static Animator loadAnimator(Context context, int i) throws Resources.NotFoundException {
        XmlResourceParser xmlResourceParser = null;
        try {
            try {
                xmlResourceParser = context.getResources().getAnimation(i);
                return createAnimatorFromXml(context, xmlResourceParser);
            } catch (IOException e) {
                Resources.NotFoundException notFoundException = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(i));
                notFoundException.initCause(e);
                throw notFoundException;
            } catch (XmlPullParserException e2) {
                Resources.NotFoundException notFoundException2 = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(i));
                notFoundException2.initCause(e2);
                throw notFoundException2;
            }
        } finally {
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
        }
    }

    private static ValueAnimator loadAnimator(Context context, AttributeSet attributeSet, ValueAnimator valueAnimator) throws Resources.NotFoundException {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, Animator);
        long j = obtainStyledAttributes.getInt(1, 0);
        long j2 = obtainStyledAttributes.getInt(2, 0);
        int i = obtainStyledAttributes.getInt(7, 0);
        if (valueAnimator == null) {
            valueAnimator = new ValueAnimator();
        }
        boolean z = i == 0;
        TypedValue peekValue = obtainStyledAttributes.peekValue(5);
        boolean z2 = peekValue != null;
        int i2 = !z2 ? 0 : peekValue.type;
        TypedValue peekValue2 = obtainStyledAttributes.peekValue(6);
        boolean z3 = peekValue2 != null;
        int i3 = !z3 ? 0 : peekValue2.type;
        if ((z2 && i2 >= 28 && i2 <= 31) || (z3 && i3 >= 28 && i3 <= 31)) {
            z = false;
            valueAnimator.setEvaluator(new ArgbEvaluator());
        }
        if (z) {
            if (z2) {
                float f = i2 != 5 ? obtainStyledAttributes.getFloat(5, 0.0f) : obtainStyledAttributes.getDimension(5, 0.0f);
                if (z3) {
                    valueAnimator.setFloatValues(f, i3 != 5 ? obtainStyledAttributes.getFloat(6, 0.0f) : obtainStyledAttributes.getDimension(6, 0.0f));
                } else {
                    valueAnimator.setFloatValues(f);
                }
            } else {
                valueAnimator.setFloatValues(i3 != 5 ? obtainStyledAttributes.getFloat(6, 0.0f) : obtainStyledAttributes.getDimension(6, 0.0f));
            }
        } else if (z2) {
            int color = i2 != 5 ? (i2 >= 28 && i2 <= 31) ? obtainStyledAttributes.getColor(5, 0) : obtainStyledAttributes.getInt(5, 0) : (int) obtainStyledAttributes.getDimension(5, 0.0f);
            if (z3) {
                valueAnimator.setIntValues(color, i3 != 5 ? (i3 >= 28 && i3 <= 31) ? obtainStyledAttributes.getColor(6, 0) : obtainStyledAttributes.getInt(6, 0) : (int) obtainStyledAttributes.getDimension(6, 0.0f));
            } else {
                valueAnimator.setIntValues(color);
            }
        } else if (z3) {
            valueAnimator.setIntValues(i3 != 5 ? (i3 >= 28 && i3 <= 31) ? obtainStyledAttributes.getColor(6, 0) : obtainStyledAttributes.getInt(6, 0) : (int) obtainStyledAttributes.getDimension(6, 0.0f));
        }
        valueAnimator.setDuration(j);
        valueAnimator.setStartDelay(j2);
        if (obtainStyledAttributes.hasValue(3)) {
            valueAnimator.setRepeatCount(obtainStyledAttributes.getInt(3, 0));
        }
        if (obtainStyledAttributes.hasValue(4)) {
            valueAnimator.setRepeatMode(obtainStyledAttributes.getInt(4, 1));
        }
        int resourceId = obtainStyledAttributes.getResourceId(0, 0);
        if (resourceId > 0) {
            valueAnimator.setInterpolator(AnimationUtils.loadInterpolator(context, resourceId));
        }
        obtainStyledAttributes.recycle();
        return valueAnimator;
    }

    private static ObjectAnimator loadObjectAnimator(Context context, AttributeSet attributeSet) throws Resources.NotFoundException {
        ObjectAnimator objectAnimator = new ObjectAnimator();
        loadAnimator(context, attributeSet, objectAnimator);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, PropertyAnimator);
        objectAnimator.setPropertyName(obtainStyledAttributes.getString(0));
        obtainStyledAttributes.recycle();
        return objectAnimator;
    }
}
