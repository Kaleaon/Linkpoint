// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.animation;

import android.view.animation.AnimationUtils;
import android.content.res.XmlResourceParser;
import android.content.res.Resources$NotFoundException;
import java.util.Iterator;
import android.content.res.TypedArray;
import java.util.ArrayList;
import android.util.TypedValue;
import android.util.AttributeSet;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import android.content.Context;

public class AnimatorInflater
{
    private static final int[] Animator;
    private static final int[] AnimatorSet;
    private static final int AnimatorSet_ordering = 0;
    private static final int Animator_duration = 1;
    private static final int Animator_interpolator = 0;
    private static final int Animator_repeatCount = 3;
    private static final int Animator_repeatMode = 4;
    private static final int Animator_startOffset = 2;
    private static final int Animator_valueFrom = 5;
    private static final int Animator_valueTo = 6;
    private static final int Animator_valueType = 7;
    private static final int[] PropertyAnimator;
    private static final int PropertyAnimator_propertyName = 0;
    private static final int TOGETHER = 0;
    private static final int VALUE_TYPE_FLOAT = 0;
    
    static {
        AnimatorSet = new int[] { 16843490 };
        PropertyAnimator = new int[] { 16843489 };
        Animator = new int[] { 16843073, 16843160, 16843198, 16843199, 16843200, 16843486, 16843487, 16843488 };
    }
    
    private static Animator createAnimatorFromXml(final Context context, final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        return createAnimatorFromXml(context, xmlPullParser, Xml.asAttributeSet(xmlPullParser), null, 0);
    }
    
    private static Animator createAnimatorFromXml(final Context context, final XmlPullParser xmlPullParser, final AttributeSet set, final AnimatorSet set2, final int n) throws XmlPullParserException, IOException {
        final int n2 = 0;
        final int depth = xmlPullParser.getDepth();
        ArrayList<AnimatorSet> list = null;
        Animator e = null;
        while (true) {
            final int next = xmlPullParser.next();
            if (next == 3 && xmlPullParser.getDepth() <= depth) {
                break;
            }
            if (next == 1) {
                break;
            }
            if (next != 2) {
                continue;
            }
            final String name = xmlPullParser.getName();
            if (!name.equals("objectAnimator")) {
                if (!name.equals("animator")) {
                    if (!name.equals("set")) {
                        throw new RuntimeException("Unknown animator name: " + xmlPullParser.getName());
                    }
                    e = new AnimatorSet();
                    final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, AnimatorInflater.AnimatorSet);
                    final TypedValue typedValue = new TypedValue();
                    obtainStyledAttributes.getValue(0, typedValue);
                    int data;
                    if (typedValue.type != 16) {
                        data = 0;
                    }
                    else {
                        data = typedValue.data;
                    }
                    createAnimatorFromXml(context, xmlPullParser, set, (AnimatorSet)e, data);
                    obtainStyledAttributes.recycle();
                }
                else {
                    e = loadAnimator(context, set, null);
                }
            }
            else {
                e = loadObjectAnimator(context, set);
            }
            if (set2 == null) {
                continue;
            }
            if (list == null) {
                list = new ArrayList<AnimatorSet>();
            }
            list.add((AnimatorSet)e);
        }
        while (true) {
            if (set2 != null && list != null) {
                final Animator[] array = new Animator[list.size()];
                final Iterator<AnimatorSet> iterator = list.iterator();
                int n3 = n2;
                while (iterator.hasNext()) {
                    array[n3] = iterator.next();
                    ++n3;
                }
                if (n != 0) {
                    set2.playSequentially(array);
                }
                else {
                    set2.playTogether(array);
                }
            }
            return e;
            continue;
        }
    }
    
    public static Animator loadAnimator(final Context context, final int n) throws Resources$NotFoundException {
        while (true) {
            XmlResourceParser xmlResourceParser = null;
            XmlResourceParser xmlResourceParser2 = null;
            XmlResourceParser animation = null;
            while (true) {
                try {
                    return createAnimatorFromXml(context, (XmlPullParser)(xmlResourceParser2 = (xmlResourceParser = (animation = context.getResources().getAnimation(n)))));
                }
                catch (final XmlPullParserException ex) {
                    xmlResourceParser = animation;
                    xmlResourceParser = animation;
                    xmlResourceParser = animation;
                    final StringBuilder sb = new StringBuilder();
                    xmlResourceParser = animation;
                    final Resources$NotFoundException ex2 = new Resources$NotFoundException(sb.append("Can't load animation resource ID #0x").append(Integer.toHexString(n)).toString());
                    xmlResourceParser = animation;
                    ex2.initCause((Throwable)ex);
                    xmlResourceParser = animation;
                    throw ex2;
                }
                catch (final IOException ex3) {
                    xmlResourceParser = xmlResourceParser2;
                    xmlResourceParser = xmlResourceParser2;
                    xmlResourceParser = xmlResourceParser2;
                    final StringBuilder sb2 = new StringBuilder();
                    xmlResourceParser = xmlResourceParser2;
                    final Resources$NotFoundException ex4 = new Resources$NotFoundException(sb2.append("Can't load animation resource ID #0x").append(Integer.toHexString(n)).toString());
                    xmlResourceParser = xmlResourceParser2;
                    ex4.initCause((Throwable)ex3);
                    xmlResourceParser = xmlResourceParser2;
                    throw ex4;
                }
                finally {
                    if (xmlResourceParser == null) {}
                }
                xmlResourceParser.close();
                continue;
            }
        }
    }
    
    private static ValueAnimator loadAnimator(final Context context, final AttributeSet set, ValueAnimator valueAnimator) throws Resources$NotFoundException {
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, AnimatorInflater.Animator);
        final long duration = obtainStyledAttributes.getInt(1, 0);
        final long startDelay = obtainStyledAttributes.getInt(2, 0);
        final int int1 = obtainStyledAttributes.getInt(7, 0);
        if (valueAnimator == null) {
            valueAnimator = new ValueAnimator();
        }
        boolean b;
        if (int1 != 0) {
            b = false;
        }
        else {
            b = true;
        }
        final TypedValue peekValue = obtainStyledAttributes.peekValue(5);
        boolean b2;
        if (peekValue == null) {
            b2 = false;
        }
        else {
            b2 = true;
        }
        int type;
        if (!b2) {
            type = 0;
        }
        else {
            type = peekValue.type;
        }
        final TypedValue peekValue2 = obtainStyledAttributes.peekValue(6);
        boolean b3;
        if (peekValue2 == null) {
            b3 = false;
        }
        else {
            b3 = true;
        }
        int type2;
        if (!b3) {
            type2 = 0;
        }
        else {
            type2 = peekValue2.type;
        }
        while (true) {
            Label_0233: {
                if (b2 && type >= 28 && type <= 31) {
                    break Label_0233;
                }
                int n;
                if (!b3) {
                    n = (b ? 1 : 0);
                }
                else {
                    n = (b ? 1 : 0);
                    if (type2 >= 28) {
                        if (type2 <= 31) {
                            break Label_0233;
                        }
                        n = (b ? 1 : 0);
                    }
                }
                if (n == 0) {
                    if (!b2) {
                        if (b3) {
                            int n2;
                            if (type2 != 5) {
                                if (type2 >= 28 && type2 <= 31) {
                                    n2 = obtainStyledAttributes.getColor(6, 0);
                                }
                                else {
                                    n2 = obtainStyledAttributes.getInt(6, 0);
                                }
                            }
                            else {
                                n2 = (int)obtainStyledAttributes.getDimension(6, 0.0f);
                            }
                            valueAnimator.setIntValues(n2);
                        }
                    }
                    else {
                        int n3;
                        if (type != 5) {
                            if (type >= 28 && type <= 31) {
                                n3 = obtainStyledAttributes.getColor(5, 0);
                            }
                            else {
                                n3 = obtainStyledAttributes.getInt(5, 0);
                            }
                        }
                        else {
                            n3 = (int)obtainStyledAttributes.getDimension(5, 0.0f);
                        }
                        if (!b3) {
                            valueAnimator.setIntValues(n3);
                        }
                        else {
                            int n4;
                            if (type2 != 5) {
                                if (type2 >= 28 && type2 <= 31) {
                                    n4 = obtainStyledAttributes.getColor(6, 0);
                                }
                                else {
                                    n4 = obtainStyledAttributes.getInt(6, 0);
                                }
                            }
                            else {
                                n4 = (int)obtainStyledAttributes.getDimension(6, 0.0f);
                            }
                            valueAnimator.setIntValues(n3, n4);
                        }
                    }
                }
                else if (!b2) {
                    float n5;
                    if (type2 != 5) {
                        n5 = obtainStyledAttributes.getFloat(6, 0.0f);
                    }
                    else {
                        n5 = obtainStyledAttributes.getDimension(6, 0.0f);
                    }
                    valueAnimator.setFloatValues(n5);
                }
                else {
                    float n6;
                    if (type != 5) {
                        n6 = obtainStyledAttributes.getFloat(5, 0.0f);
                    }
                    else {
                        n6 = obtainStyledAttributes.getDimension(5, 0.0f);
                    }
                    if (!b3) {
                        valueAnimator.setFloatValues(n6);
                    }
                    else {
                        float n7;
                        if (type2 != 5) {
                            n7 = obtainStyledAttributes.getFloat(6, 0.0f);
                        }
                        else {
                            n7 = obtainStyledAttributes.getDimension(6, 0.0f);
                        }
                        valueAnimator.setFloatValues(n6, n7);
                    }
                }
                valueAnimator.setDuration(duration);
                valueAnimator.setStartDelay(startDelay);
                if (obtainStyledAttributes.hasValue(3)) {
                    valueAnimator.setRepeatCount(obtainStyledAttributes.getInt(3, 0));
                }
                if (obtainStyledAttributes.hasValue(4)) {
                    valueAnimator.setRepeatMode(obtainStyledAttributes.getInt(4, 1));
                }
                final int resourceId = obtainStyledAttributes.getResourceId(0, 0);
                if (resourceId > 0) {
                    valueAnimator.setInterpolator(AnimationUtils.loadInterpolator(context, resourceId));
                }
                obtainStyledAttributes.recycle();
                return valueAnimator;
            }
            int n = 0;
            valueAnimator.setEvaluator(new ArgbEvaluator());
            continue;
        }
    }
    
    private static ObjectAnimator loadObjectAnimator(final Context context, final AttributeSet set) throws Resources$NotFoundException {
        final ObjectAnimator objectAnimator = new ObjectAnimator();
        loadAnimator(context, set, objectAnimator);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, AnimatorInflater.PropertyAnimator);
        objectAnimator.setPropertyName(obtainStyledAttributes.getString(0));
        obtainStyledAttributes.recycle();
        return objectAnimator;
    }
}
