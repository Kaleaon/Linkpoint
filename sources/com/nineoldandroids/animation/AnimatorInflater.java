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

public class AnimatorInflater {
    private static final int[] Animator = {16843073, 16843160, 16843198, 16843199, 16843200, 16843486, 16843487, 16843488};
    private static final int[] AnimatorSet = {16843490};
    private static final int AnimatorSet_ordering = 0;
    private static final int Animator_duration = 1;
    private static final int Animator_interpolator = 0;
    private static final int Animator_repeatCount = 3;
    private static final int Animator_repeatMode = 4;
    private static final int Animator_startOffset = 2;
    private static final int Animator_valueFrom = 5;
    private static final int Animator_valueTo = 6;
    private static final int Animator_valueType = 7;
    private static final int[] PropertyAnimator = {16843489};
    private static final int PropertyAnimator_propertyName = 0;
    private static final int TOGETHER = 0;
    private static final int VALUE_TYPE_FLOAT = 0;

    private static Animator createAnimatorFromXml(Context context, XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        return createAnimatorFromXml(context, xmlPullParser, Xml.asAttributeSet(xmlPullParser), (AnimatorSet) null, 0);
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x00b3  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00b8 A[LOOP:1: B:33:0x00ab->B:37:0x00b8, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00c4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.nineoldandroids.animation.Animator createAnimatorFromXml(android.content.Context r9, org.xmlpull.v1.XmlPullParser r10, android.util.AttributeSet r11, com.nineoldandroids.animation.AnimatorSet r12, int r13) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r3 = 0
            r5 = 0
            int r6 = r10.getDepth()
            r2 = r3
            r1 = r3
        L_0x0008:
            int r0 = r10.next()
            r4 = 3
            if (r0 == r4) goto L_0x0015
        L_0x000f:
            r4 = 1
            if (r0 != r4) goto L_0x001c
        L_0x0012:
            if (r12 != 0) goto L_0x009f
        L_0x0014:
            return r1
        L_0x0015:
            int r4 = r10.getDepth()
            if (r4 > r6) goto L_0x000f
            goto L_0x0012
        L_0x001c:
            r4 = 2
            if (r0 != r4) goto L_0x0008
            java.lang.String r0 = r10.getName()
            java.lang.String r1 = "objectAnimator"
            boolean r1 = r0.equals(r1)
            if (r1 != 0) goto L_0x005c
            java.lang.String r1 = "animator"
            boolean r1 = r0.equals(r1)
            if (r1 != 0) goto L_0x0065
            java.lang.String r1 = "set"
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L_0x006a
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
        L_0x005c:
            com.nineoldandroids.animation.ObjectAnimator r1 = loadObjectAnimator(r9, r11)
        L_0x0060:
            if (r12 != 0) goto L_0x0092
            r0 = r2
        L_0x0063:
            r2 = r0
            goto L_0x0008
        L_0x0065:
            com.nineoldandroids.animation.ValueAnimator r1 = loadAnimator(r9, r11, r3)
            goto L_0x0060
        L_0x006a:
            com.nineoldandroids.animation.AnimatorSet r1 = new com.nineoldandroids.animation.AnimatorSet
            r1.<init>()
            int[] r0 = AnimatorSet
            android.content.res.TypedArray r7 = r9.obtainStyledAttributes(r11, r0)
            android.util.TypedValue r0 = new android.util.TypedValue
            r0.<init>()
            r7.getValue(r5, r0)
            int r4 = r0.type
            r8 = 16
            if (r4 == r8) goto L_0x008e
            r4 = r5
        L_0x0084:
            r0 = r1
            com.nineoldandroids.animation.AnimatorSet r0 = (com.nineoldandroids.animation.AnimatorSet) r0
            createAnimatorFromXml(r9, r10, r11, r0, r4)
            r7.recycle()
            goto L_0x0060
        L_0x008e:
            int r0 = r0.data
            r4 = r0
            goto L_0x0084
        L_0x0092:
            if (r2 == 0) goto L_0x0099
        L_0x0094:
            r2.add(r1)
            r0 = r2
            goto L_0x0063
        L_0x0099:
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            goto L_0x0094
        L_0x009f:
            if (r2 == 0) goto L_0x0014
            int r0 = r2.size()
            com.nineoldandroids.animation.Animator[] r3 = new com.nineoldandroids.animation.Animator[r0]
            java.util.Iterator r4 = r2.iterator()
        L_0x00ab:
            boolean r0 = r4.hasNext()
            if (r0 != 0) goto L_0x00b8
            if (r13 == 0) goto L_0x00c4
            r12.playSequentially((com.nineoldandroids.animation.Animator[]) r3)
            goto L_0x0014
        L_0x00b8:
            java.lang.Object r0 = r4.next()
            com.nineoldandroids.animation.Animator r0 = (com.nineoldandroids.animation.Animator) r0
            int r2 = r5 + 1
            r3[r5] = r0
            r5 = r2
            goto L_0x00ab
        L_0x00c4:
            r12.playTogether((com.nineoldandroids.animation.Animator[]) r3)
            goto L_0x0014
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nineoldandroids.animation.AnimatorInflater.createAnimatorFromXml(android.content.Context, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, com.nineoldandroids.animation.AnimatorSet, int):com.nineoldandroids.animation.Animator");
    }

    public static Animator loadAnimator(Context context, int i) throws Resources.NotFoundException {
        XmlResourceParser xmlResourceParser = null;
        try {
            xmlResourceParser = context.getResources().getAnimation(i);
            Animator createAnimatorFromXml = createAnimatorFromXml(context, xmlResourceParser);
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
            return createAnimatorFromXml;
        } catch (XmlPullParserException e) {
            Resources.NotFoundException notFoundException = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(i));
            notFoundException.initCause(e);
            throw notFoundException;
        } catch (IOException e2) {
            Resources.NotFoundException notFoundException2 = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(i));
            notFoundException2.initCause(e2);
            throw notFoundException2;
        } catch (Throwable th) {
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
            throw th;
        }
    }

    private static ValueAnimator loadAnimator(Context context, AttributeSet attributeSet, ValueAnimator valueAnimator) throws Resources.NotFoundException {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, Animator);
        long j = (long) obtainStyledAttributes.getInt(1, 0);
        long j2 = (long) obtainStyledAttributes.getInt(2, 0);
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
        if (!z) {
            if (z2) {
                int color = i2 != 5 ? (i2 >= 28 && i2 <= 31) ? obtainStyledAttributes.getColor(5, 0) : obtainStyledAttributes.getInt(5, 0) : (int) obtainStyledAttributes.getDimension(5, 0.0f);
                if (!z3) {
                    valueAnimator.setIntValues(color);
                } else {
                    valueAnimator.setIntValues(color, i3 != 5 ? (i3 >= 28 && i3 <= 31) ? obtainStyledAttributes.getColor(6, 0) : obtainStyledAttributes.getInt(6, 0) : (int) obtainStyledAttributes.getDimension(6, 0.0f));
                }
            } else if (z3) {
                valueAnimator.setIntValues(i3 != 5 ? (i3 >= 28 && i3 <= 31) ? obtainStyledAttributes.getColor(6, 0) : obtainStyledAttributes.getInt(6, 0) : (int) obtainStyledAttributes.getDimension(6, 0.0f));
            }
        } else if (!z2) {
            valueAnimator.setFloatValues(i3 != 5 ? obtainStyledAttributes.getFloat(6, 0.0f) : obtainStyledAttributes.getDimension(6, 0.0f));
        } else {
            float f = i2 != 5 ? obtainStyledAttributes.getFloat(5, 0.0f) : obtainStyledAttributes.getDimension(5, 0.0f);
            if (!z3) {
                valueAnimator.setFloatValues(f);
            } else {
                valueAnimator.setFloatValues(f, i3 != 5 ? obtainStyledAttributes.getFloat(6, 0.0f) : obtainStyledAttributes.getDimension(6, 0.0f));
            }
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
