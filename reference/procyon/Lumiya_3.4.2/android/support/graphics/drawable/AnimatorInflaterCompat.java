// 
// Decompiled by Procyon v0.6.0
// 

package android.support.graphics.drawable;

import android.graphics.PathMeasure;
import android.graphics.Path;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.res.XmlResourceParser;
import android.content.res.Resources$NotFoundException;
import android.animation.AnimatorInflater;
import android.os.Build$VERSION;
import android.support.annotation.AnimatorRes;
import android.util.TypedValue;
import android.view.InflateException;
import android.support.v4.graphics.PathParser;
import android.animation.TypeEvaluator;
import android.util.Log;
import android.animation.Keyframe;
import java.util.Iterator;
import android.content.res.TypedArray;
import android.animation.PropertyValuesHolder;
import java.util.ArrayList;
import android.support.v4.content.res.TypedArrayUtils;
import android.animation.ValueAnimator;
import android.util.AttributeSet;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import android.animation.AnimatorSet;
import android.util.Xml;
import android.animation.Animator;
import org.xmlpull.v1.XmlPullParser;
import android.content.res.Resources$Theme;
import android.content.res.Resources;
import android.content.Context;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class AnimatorInflaterCompat
{
    private static final boolean DBG_ANIMATOR_INFLATER = false;
    private static final int MAX_NUM_POINTS = 100;
    private static final String TAG = "AnimatorInflater";
    private static final int TOGETHER = 0;
    private static final int VALUE_TYPE_COLOR = 3;
    private static final int VALUE_TYPE_FLOAT = 0;
    private static final int VALUE_TYPE_INT = 1;
    private static final int VALUE_TYPE_PATH = 2;
    private static final int VALUE_TYPE_UNDEFINED = 4;
    
    private static Animator createAnimatorFromXml(final Context context, final Resources resources, final Resources$Theme resources$Theme, final XmlPullParser xmlPullParser, final float n) throws XmlPullParserException, IOException {
        return createAnimatorFromXml(context, resources, resources$Theme, xmlPullParser, Xml.asAttributeSet(xmlPullParser), null, 0, n);
    }
    
    private static Animator createAnimatorFromXml(final Context context, final Resources resources, final Resources$Theme resources$Theme, final XmlPullParser xmlPullParser, final AttributeSet set, final AnimatorSet set2, final int n, final float n2) throws XmlPullParserException, IOException {
        Object e = null;
        final int depth = xmlPullParser.getDepth();
        ArrayList<ValueAnimator> list = null;
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
            boolean b;
            if (!name.equals("objectAnimator")) {
                if (!name.equals("animator")) {
                    if (!name.equals("set")) {
                        if (!name.equals("propertyValuesHolder")) {
                            throw new RuntimeException("Unknown animator name: " + xmlPullParser.getName());
                        }
                        final PropertyValuesHolder[] loadValues = loadValues(context, resources, resources$Theme, xmlPullParser, Xml.asAttributeSet(xmlPullParser));
                        if (loadValues != null && e != null && e instanceof ValueAnimator) {
                            ((ValueAnimator)e).setValues(loadValues);
                        }
                        b = true;
                    }
                    else {
                        e = new AnimatorSet();
                        final TypedArray obtainAttributes = TypedArrayUtils.obtainAttributes(resources, resources$Theme, set, AndroidResources.STYLEABLE_ANIMATOR_SET);
                        createAnimatorFromXml(context, resources, resources$Theme, xmlPullParser, set, (AnimatorSet)e, TypedArrayUtils.getNamedInt(obtainAttributes, xmlPullParser, "ordering", 0, 0), n2);
                        obtainAttributes.recycle();
                        b = false;
                    }
                }
                else {
                    e = loadAnimator(context, resources, resources$Theme, set, null, n2, xmlPullParser);
                    b = false;
                }
            }
            else {
                e = loadObjectAnimator(context, resources, resources$Theme, set, n2, xmlPullParser);
                b = false;
            }
            if (set2 == null || b) {
                continue;
            }
            if (list == null) {
                list = new ArrayList<ValueAnimator>();
            }
            list.add((ValueAnimator)e);
        }
        while (true) {
            if (set2 != null && list != null) {
                final Animator[] array = new Animator[list.size()];
                final Iterator<ValueAnimator> iterator = list.iterator();
                int n3 = 0;
                while (iterator.hasNext()) {
                    array[n3] = (Animator)iterator.next();
                    ++n3;
                }
                if (n != 0) {
                    set2.playSequentially(array);
                }
                else {
                    set2.playTogether(array);
                }
            }
            return (Animator)e;
            continue;
        }
    }
    
    private static Keyframe createNewKeyframe(Keyframe keyframe, final float n) {
        if (keyframe.getType() != Float.TYPE) {
            if (keyframe.getType() != Integer.TYPE) {
                keyframe = Keyframe.ofObject(n);
            }
            else {
                keyframe = Keyframe.ofInt(n);
            }
        }
        else {
            keyframe = Keyframe.ofFloat(n);
        }
        return keyframe;
    }
    
    private static void distributeKeyframes(final Keyframe[] array, float n, int i, final int n2) {
        n /= n2 - i + 2;
        while (i <= n2) {
            array[i].setFraction(array[i - 1].getFraction() + n);
            ++i;
        }
    }
    
    private static void dumpKeyframes(final Object[] array, String s) {
        if (array != null && array.length != 0) {
            Log.d("AnimatorInflater", s);
            for (int length = array.length, i = 0; i < length; ++i) {
                final Keyframe keyframe = (Keyframe)array[i];
                final StringBuilder append = new StringBuilder().append("Keyframe ").append(i).append(": fraction ");
                if (keyframe.getFraction() < 0.0f) {
                    s = "null";
                }
                else {
                    s = (String)Float.valueOf(keyframe.getFraction());
                }
                final StringBuilder append2 = append.append((Object)s).append(", ").append(", value : ");
                if (!keyframe.hasValue()) {
                    s = "null";
                }
                else {
                    s = (String)keyframe.getValue();
                }
                Log.d("AnimatorInflater", append2.append((Object)s).toString());
            }
        }
    }
    
    private static PropertyValuesHolder getPVH(final TypedArray typedArray, int n, int n2, final int n3, final String s) {
        final TypedValue peekValue = typedArray.peekValue(n2);
        boolean b;
        if (peekValue == null) {
            b = false;
        }
        else {
            b = true;
        }
        int type;
        if (!b) {
            type = 0;
        }
        else {
            type = peekValue.type;
        }
        final TypedValue peekValue2 = typedArray.peekValue(n3);
        boolean b2;
        if (peekValue2 == null) {
            b2 = false;
        }
        else {
            b2 = true;
        }
        int type2;
        if (!b2) {
            type2 = 0;
        }
        else {
            type2 = peekValue2.type;
        }
        if (n == 4) {
            if ((!b || !isColorType(type)) && (!b2 || !isColorType(type2))) {
                n = 0;
            }
            else {
                n = 3;
            }
        }
        int n4;
        if (n != 0) {
            n4 = 0;
        }
        else {
            n4 = 1;
        }
        PropertyValuesHolder propertyValuesHolder2;
        if (n != 2) {
            Object instance;
            if (n != 3) {
                instance = null;
            }
            else {
                instance = ArgbEvaluator.getInstance();
            }
            PropertyValuesHolder propertyValuesHolder;
            if (n4 == 0) {
                if (!b) {
                    if (!b2) {
                        propertyValuesHolder = null;
                    }
                    else {
                        if (type2 != 5) {
                            if (!isColorType(type2)) {
                                n = typedArray.getInt(n3, 0);
                            }
                            else {
                                n = typedArray.getColor(n3, 0);
                            }
                        }
                        else {
                            n = (int)typedArray.getDimension(n3, 0.0f);
                        }
                        propertyValuesHolder = PropertyValuesHolder.ofInt(s, new int[] { n });
                    }
                }
                else {
                    if (type != 5) {
                        if (!isColorType(type)) {
                            n = typedArray.getInt(n2, 0);
                        }
                        else {
                            n = typedArray.getColor(n2, 0);
                        }
                    }
                    else {
                        n = (int)typedArray.getDimension(n2, 0.0f);
                    }
                    if (!b2) {
                        propertyValuesHolder = PropertyValuesHolder.ofInt(s, new int[] { n });
                    }
                    else {
                        if (type2 != 5) {
                            if (!isColorType(type2)) {
                                n2 = typedArray.getInt(n3, 0);
                            }
                            else {
                                n2 = typedArray.getColor(n3, 0);
                            }
                        }
                        else {
                            n2 = (int)typedArray.getDimension(n3, 0.0f);
                        }
                        propertyValuesHolder = PropertyValuesHolder.ofInt(s, new int[] { n, n2 });
                    }
                }
            }
            else if (!b) {
                float n5;
                if (type2 != 5) {
                    n5 = typedArray.getFloat(n3, 0.0f);
                }
                else {
                    n5 = typedArray.getDimension(n3, 0.0f);
                }
                propertyValuesHolder = PropertyValuesHolder.ofFloat(s, new float[] { n5 });
            }
            else {
                float n6;
                if (type != 5) {
                    n6 = typedArray.getFloat(n2, 0.0f);
                }
                else {
                    n6 = typedArray.getDimension(n2, 0.0f);
                }
                if (!b2) {
                    propertyValuesHolder = PropertyValuesHolder.ofFloat(s, new float[] { n6 });
                }
                else {
                    float n7;
                    if (type2 != 5) {
                        n7 = typedArray.getFloat(n3, 0.0f);
                    }
                    else {
                        n7 = typedArray.getDimension(n3, 0.0f);
                    }
                    propertyValuesHolder = PropertyValuesHolder.ofFloat(s, new float[] { n6, n7 });
                }
            }
            if (propertyValuesHolder == null) {
                propertyValuesHolder2 = propertyValuesHolder;
            }
            else {
                propertyValuesHolder2 = propertyValuesHolder;
                if (instance != null) {
                    propertyValuesHolder.setEvaluator((TypeEvaluator)instance);
                    propertyValuesHolder2 = propertyValuesHolder;
                }
            }
        }
        else {
            final String string = typedArray.getString(n2);
            final String string2 = typedArray.getString(n3);
            final PathParser.PathDataNode[] nodesFromPathData = PathParser.createNodesFromPathData(string);
            final PathParser.PathDataNode[] nodesFromPathData2 = PathParser.createNodesFromPathData(string2);
            if (nodesFromPathData == null && nodesFromPathData2 == null) {
                propertyValuesHolder2 = null;
            }
            else if (nodesFromPathData == null) {
                if (nodesFromPathData2 == null) {
                    propertyValuesHolder2 = null;
                }
                else {
                    propertyValuesHolder2 = PropertyValuesHolder.ofObject(s, (TypeEvaluator)new PathDataEvaluator(), new Object[] { nodesFromPathData2 });
                }
            }
            else {
                final PathDataEvaluator pathDataEvaluator = new PathDataEvaluator();
                if (nodesFromPathData2 == null) {
                    propertyValuesHolder2 = PropertyValuesHolder.ofObject(s, (TypeEvaluator)pathDataEvaluator, new Object[] { nodesFromPathData });
                }
                else {
                    if (!PathParser.canMorph(nodesFromPathData, nodesFromPathData2)) {
                        throw new InflateException(" Can't morph from " + string + " to " + string2);
                    }
                    propertyValuesHolder2 = PropertyValuesHolder.ofObject(s, (TypeEvaluator)pathDataEvaluator, new Object[] { nodesFromPathData, nodesFromPathData2 });
                }
            }
        }
        return propertyValuesHolder2;
    }
    
    private static int inferValueTypeFromValues(final TypedArray typedArray, int n, int n2) {
        final int n3 = 1;
        final int n4 = 0;
        final TypedValue peekValue = typedArray.peekValue(n);
        if (peekValue == null) {
            n = 0;
        }
        else {
            n = 1;
        }
        int type;
        if (n == 0) {
            type = 0;
        }
        else {
            type = peekValue.type;
        }
        final TypedValue peekValue2 = typedArray.peekValue(n2);
        n2 = n3;
        if (peekValue2 == null) {
            n2 = 0;
        }
        int type2;
        if (n2 == 0) {
            type2 = 0;
        }
        else {
            type2 = peekValue2.type;
        }
        if (n != 0 && isColorType(type)) {
            return 3;
        }
        if (n2 == 0) {
            n = n4;
        }
        else {
            if (isColorType(type2)) {
                return 3;
            }
            n = n4;
        }
        return n;
        n = 3;
        return n;
    }
    
    private static int inferValueTypeOfKeyframe(final Resources resources, final Resources$Theme resources$Theme, final AttributeSet set, final XmlPullParser xmlPullParser) {
        final boolean b = false;
        final TypedArray obtainAttributes = TypedArrayUtils.obtainAttributes(resources, resources$Theme, set, AndroidResources.STYLEABLE_KEYFRAME);
        final TypedValue peekNamedValue = TypedArrayUtils.peekNamedValue(obtainAttributes, xmlPullParser, "value", 0);
        int n;
        if (peekNamedValue == null) {
            n = 0;
        }
        else {
            n = 1;
        }
        int n2;
        if (n == 0) {
            n2 = (b ? 1 : 0);
        }
        else {
            n2 = (b ? 1 : 0);
            if (isColorType(peekNamedValue.type)) {
                n2 = 3;
            }
        }
        obtainAttributes.recycle();
        return n2;
    }
    
    private static boolean isColorType(final int n) {
        return n >= 28 && n <= 31;
    }
    
    public static Animator loadAnimator(final Context context, @AnimatorRes final int n) throws Resources$NotFoundException {
        Animator animator;
        if (Build$VERSION.SDK_INT < 24) {
            animator = loadAnimator(context, context.getResources(), context.getTheme(), n);
        }
        else {
            animator = AnimatorInflater.loadAnimator(context, n);
        }
        return animator;
    }
    
    public static Animator loadAnimator(final Context context, final Resources resources, final Resources$Theme resources$Theme, @AnimatorRes final int n) throws Resources$NotFoundException {
        return loadAnimator(context, resources, resources$Theme, n, 1.0f);
    }
    
    public static Animator loadAnimator(final Context context, final Resources resources, final Resources$Theme resources$Theme, @AnimatorRes final int n, final float n2) throws Resources$NotFoundException {
        while (true) {
            XmlResourceParser xmlResourceParser = null;
            XmlResourceParser xmlResourceParser2 = null;
            XmlResourceParser animation = null;
            while (true) {
                try {
                    return createAnimatorFromXml(context, resources, resources$Theme, (XmlPullParser)(xmlResourceParser2 = (xmlResourceParser = (animation = resources.getAnimation(n)))), n2);
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
    
    private static ValueAnimator loadAnimator(final Context context, final Resources resources, final Resources$Theme resources$Theme, final AttributeSet set, ValueAnimator valueAnimator, final float n, final XmlPullParser xmlPullParser) throws Resources$NotFoundException {
        final TypedArray obtainAttributes = TypedArrayUtils.obtainAttributes(resources, resources$Theme, set, AndroidResources.STYLEABLE_ANIMATOR);
        final TypedArray obtainAttributes2 = TypedArrayUtils.obtainAttributes(resources, resources$Theme, set, AndroidResources.STYLEABLE_PROPERTY_ANIMATOR);
        if (valueAnimator == null) {
            valueAnimator = new ValueAnimator();
        }
        parseAnimatorFromTypeArray(valueAnimator, obtainAttributes, obtainAttributes2, n, xmlPullParser);
        final int namedResourceId = TypedArrayUtils.getNamedResourceId(obtainAttributes, xmlPullParser, "interpolator", 0, 0);
        if (namedResourceId > 0) {
            valueAnimator.setInterpolator((TimeInterpolator)AnimationUtilsCompat.loadInterpolator(context, namedResourceId));
        }
        obtainAttributes.recycle();
        if (obtainAttributes2 != null) {
            obtainAttributes2.recycle();
        }
        return valueAnimator;
    }
    
    private static Keyframe loadKeyframe(final Context context, final Resources resources, final Resources$Theme resources$Theme, final AttributeSet set, int namedResourceId, final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        final TypedArray obtainAttributes = TypedArrayUtils.obtainAttributes(resources, resources$Theme, set, AndroidResources.STYLEABLE_KEYFRAME);
        final float namedFloat = TypedArrayUtils.getNamedFloat(obtainAttributes, xmlPullParser, "fraction", 3, -1.0f);
        final TypedValue peekNamedValue = TypedArrayUtils.peekNamedValue(obtainAttributes, xmlPullParser, "value", 0);
        boolean b;
        if (peekNamedValue == null) {
            b = false;
        }
        else {
            b = true;
        }
        if (namedResourceId == 4) {
            if (b && isColorType(peekNamedValue.type)) {
                namedResourceId = 3;
            }
            else {
                namedResourceId = 0;
            }
        }
        Keyframe keyframe = null;
        if (!b) {
            if (namedResourceId != 0) {
                keyframe = Keyframe.ofInt(namedFloat);
            }
            else {
                keyframe = Keyframe.ofFloat(namedFloat);
            }
        }
        else {
            switch (namedResourceId) {
                default: {
                    keyframe = null;
                    break;
                }
                case 0: {
                    keyframe = Keyframe.ofFloat(namedFloat, TypedArrayUtils.getNamedFloat(obtainAttributes, xmlPullParser, "value", 0, 0.0f));
                    break;
                }
                case 1:
                case 3: {
                    keyframe = Keyframe.ofInt(namedFloat, TypedArrayUtils.getNamedInt(obtainAttributes, xmlPullParser, "value", 0, 0));
                    break;
                }
            }
        }
        namedResourceId = TypedArrayUtils.getNamedResourceId(obtainAttributes, xmlPullParser, "interpolator", 1, 0);
        if (namedResourceId > 0) {
            keyframe.setInterpolator((TimeInterpolator)AnimationUtilsCompat.loadInterpolator(context, namedResourceId));
        }
        obtainAttributes.recycle();
        return keyframe;
    }
    
    private static ObjectAnimator loadObjectAnimator(final Context context, final Resources resources, final Resources$Theme resources$Theme, final AttributeSet set, final float n, final XmlPullParser xmlPullParser) throws Resources$NotFoundException {
        final ObjectAnimator objectAnimator = new ObjectAnimator();
        loadAnimator(context, resources, resources$Theme, set, (ValueAnimator)objectAnimator, n, xmlPullParser);
        return objectAnimator;
    }
    
    private static PropertyValuesHolder loadPvh(final Context context, final Resources resources, final Resources$Theme resources$Theme, final XmlPullParser xmlPullParser, final String s, int i) throws XmlPullParserException, IOException {
        ArrayList<Keyframe> list = null;
        int n = i;
        while (true) {
            i = xmlPullParser.next();
            if (i == 3 || i == 1) {
                break;
            }
            if (!xmlPullParser.getName().equals("keyframe")) {
                continue;
            }
            if (n != 4) {
                i = n;
            }
            else {
                i = inferValueTypeOfKeyframe(resources, resources$Theme, Xml.asAttributeSet(xmlPullParser), xmlPullParser);
            }
            final Keyframe loadKeyframe = loadKeyframe(context, resources, resources$Theme, Xml.asAttributeSet(xmlPullParser), i, xmlPullParser);
            if (loadKeyframe != null) {
                if (list == null) {
                    list = new ArrayList<Keyframe>();
                }
                list.add(loadKeyframe);
            }
            xmlPullParser.next();
            n = i;
        }
        if (list != null) {
            i = list.size();
            if (i > 0) {
                final Keyframe keyframe = list.get(0);
                final Keyframe keyframe2 = list.get(i - 1);
                final float fraction = keyframe2.getFraction();
                if (fraction < 1.0f) {
                    if (fraction < 0.0f) {
                        keyframe2.setFraction(1.0f);
                    }
                    else {
                        list.add(list.size(), createNewKeyframe(keyframe2, 1.0f));
                        ++i;
                    }
                }
                final float fraction2 = keyframe.getFraction();
                int n2 = i;
                if (fraction2 != 0.0f) {
                    if (fraction2 < 0.0f) {
                        keyframe.setFraction(0.0f);
                        n2 = i;
                    }
                    else {
                        list.add(0, createNewKeyframe(keyframe, 0.0f));
                        n2 = i + 1;
                    }
                }
                final Keyframe[] a = new Keyframe[n2];
                list.toArray(a);
                Keyframe keyframe3;
                int n3;
                int n4;
                for (i = 0; i < n2; ++i) {
                    keyframe3 = a[i];
                    if (keyframe3.getFraction() < 0.0f) {
                        if (i != 0) {
                            if (i != n2 - 1) {
                                n3 = i + 1;
                                n4 = i;
                                while (n3 < n2 - 1 && a[n3].getFraction() < 0.0f) {
                                    n4 = n3;
                                    ++n3;
                                }
                                distributeKeyframes(a, a[n4 + 1].getFraction() - a[i - 1].getFraction(), i, n4);
                            }
                            else {
                                keyframe3.setFraction(1.0f);
                            }
                        }
                        else {
                            keyframe3.setFraction(0.0f);
                        }
                    }
                }
                PropertyValuesHolder ofKeyframe;
                final PropertyValuesHolder propertyValuesHolder = ofKeyframe = PropertyValuesHolder.ofKeyframe(s, a);
                if (n == 3) {
                    propertyValuesHolder.setEvaluator((TypeEvaluator)ArgbEvaluator.getInstance());
                    ofKeyframe = propertyValuesHolder;
                    return ofKeyframe;
                }
                return ofKeyframe;
            }
        }
        return null;
    }
    
    private static PropertyValuesHolder[] loadValues(final Context context, final Resources resources, final Resources$Theme resources$Theme, final XmlPullParser xmlPullParser, final AttributeSet set) throws XmlPullParserException, IOException {
        ArrayList<PropertyValuesHolder> list = null;
        while (true) {
            final int eventType = xmlPullParser.getEventType();
            if (eventType == 3 || eventType == 1) {
                break;
            }
            if (eventType == 2) {
                if (xmlPullParser.getName().equals("propertyValuesHolder")) {
                    final TypedArray obtainAttributes = TypedArrayUtils.obtainAttributes(resources, resources$Theme, set, AndroidResources.STYLEABLE_PROPERTY_VALUES_HOLDER);
                    final String namedString = TypedArrayUtils.getNamedString(obtainAttributes, xmlPullParser, "propertyName", 3);
                    final int namedInt = TypedArrayUtils.getNamedInt(obtainAttributes, xmlPullParser, "valueType", 2, 4);
                    PropertyValuesHolder e = loadPvh(context, resources, resources$Theme, xmlPullParser, namedString, namedInt);
                    if (e == null) {
                        e = getPVH(obtainAttributes, namedInt, 0, 1, namedString);
                    }
                    if (e != null) {
                        if (list == null) {
                            list = new ArrayList<PropertyValuesHolder>();
                        }
                        list.add(e);
                    }
                    obtainAttributes.recycle();
                }
                xmlPullParser.next();
            }
            else {
                xmlPullParser.next();
            }
        }
        PropertyValuesHolder[] array = null;
        if (list != null) {
            final int size = list.size();
            array = new PropertyValuesHolder[size];
            for (int i = 0; i < size; ++i) {
                array[i] = list.get(i);
            }
        }
        return array;
    }
    
    private static void parseAnimatorFromTypeArray(final ValueAnimator valueAnimator, final TypedArray typedArray, final TypedArray typedArray2, final float n, final XmlPullParser xmlPullParser) {
        final long duration = TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "duration", 1, 300);
        final long startDelay = TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "startOffset", 2, 0);
        int n2 = TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "valueType", 7, 4);
        int n3;
        if (!TypedArrayUtils.hasAttribute(xmlPullParser, "valueFrom")) {
            n3 = n2;
        }
        else {
            n3 = n2;
            if (TypedArrayUtils.hasAttribute(xmlPullParser, "valueTo")) {
                if (n2 == 4) {
                    n2 = inferValueTypeFromValues(typedArray, 5, 6);
                }
                final PropertyValuesHolder pvh = getPVH(typedArray, n2, 5, 6, "");
                n3 = n2;
                if (pvh != null) {
                    valueAnimator.setValues(new PropertyValuesHolder[] { pvh });
                    n3 = n2;
                }
            }
        }
        valueAnimator.setDuration(duration);
        valueAnimator.setStartDelay(startDelay);
        valueAnimator.setRepeatCount(TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "repeatCount", 3, 0));
        valueAnimator.setRepeatMode(TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "repeatMode", 4, 1));
        if (typedArray2 != null) {
            setupObjectAnimator(valueAnimator, typedArray2, n3, n, xmlPullParser);
        }
    }
    
    private static void setupObjectAnimator(final ValueAnimator valueAnimator, final TypedArray typedArray, final int n, final float n2, final XmlPullParser xmlPullParser) {
        final ObjectAnimator objectAnimator = (ObjectAnimator)valueAnimator;
        final String namedString = TypedArrayUtils.getNamedString(typedArray, xmlPullParser, "pathData", 1);
        if (namedString == null) {
            objectAnimator.setPropertyName(TypedArrayUtils.getNamedString(typedArray, xmlPullParser, "propertyName", 0));
        }
        else {
            final String namedString2 = TypedArrayUtils.getNamedString(typedArray, xmlPullParser, "propertyXName", 2);
            final String namedString3 = TypedArrayUtils.getNamedString(typedArray, xmlPullParser, "propertyYName", 3);
            if (n == 2) {}
            if (namedString2 == null && namedString3 == null) {
                throw new InflateException(typedArray.getPositionDescription() + " propertyXName or propertyYName is needed for PathData");
            }
            setupPathMotion(PathParser.createPathFromPathData(namedString), objectAnimator, 0.5f * n2, namedString2, namedString3);
        }
    }
    
    private static void setupPathMotion(final Path path, final ObjectAnimator objectAnimator, float n, final String s, final String s2) {
        final PathMeasure pathMeasure = new PathMeasure(path, false);
        float n2 = 0.0f;
        final ArrayList list = new ArrayList();
        list.add(0.0f);
        float f;
        do {
            f = n2 + pathMeasure.getLength();
            list.add(f);
            n2 = f;
        } while (pathMeasure.nextContour());
        final PathMeasure pathMeasure2 = new PathMeasure(path, false);
        final int min = Math.min(100, (int)(f / n) + 1);
        final float[] array = new float[min];
        final float[] array2 = new float[min];
        final float[] array3 = new float[2];
        int n3 = 0;
        final float n4 = f / (min - 1);
        int i = 0;
        n = 0.0f;
        while (i < min) {
            pathMeasure2.getPosTan(n, array3, (float[])null);
            pathMeasure2.getPosTan(n, array3, (float[])null);
            array[i] = array3[0];
            array2[i] = array3[1];
            n += n4;
            if (n3 + 1 < list.size()) {
                if (n > (float)list.get(n3 + 1)) {
                    n -= (float)list.get(n3 + 1);
                    ++n3;
                    pathMeasure2.nextContour();
                }
            }
            ++i;
        }
        PropertyValuesHolder ofFloat = null;
        final PropertyValuesHolder propertyValuesHolder = null;
        if (s != null) {
            ofFloat = PropertyValuesHolder.ofFloat(s, array);
        }
        PropertyValuesHolder ofFloat2;
        if (s2 == null) {
            ofFloat2 = propertyValuesHolder;
        }
        else {
            ofFloat2 = PropertyValuesHolder.ofFloat(s2, array2);
        }
        if (ofFloat != null) {
            if (ofFloat2 != null) {
                objectAnimator.setValues(new PropertyValuesHolder[] { ofFloat, ofFloat2 });
            }
            else {
                objectAnimator.setValues(new PropertyValuesHolder[] { ofFloat });
            }
        }
        else {
            objectAnimator.setValues(new PropertyValuesHolder[] { ofFloat2 });
        }
    }
    
    private static class PathDataEvaluator implements TypeEvaluator<PathParser.PathDataNode[]>
    {
        private PathParser.PathDataNode[] mNodeArray;
        
        private PathDataEvaluator() {
        }
        
        PathDataEvaluator(final PathParser.PathDataNode[] mNodeArray) {
            this.mNodeArray = mNodeArray;
        }
        
        public PathParser.PathDataNode[] evaluate(final float n, final PathParser.PathDataNode[] array, final PathParser.PathDataNode[] array2) {
            int i = 0;
            if (PathParser.canMorph(array, array2)) {
                if (this.mNodeArray == null || !PathParser.canMorph(this.mNodeArray, array)) {
                    this.mNodeArray = PathParser.deepCopyNodes(array);
                }
                while (i < array.length) {
                    this.mNodeArray[i].interpolatePathDataNode(array[i], array2[i], n);
                    ++i;
                }
                return this.mNodeArray;
            }
            throw new IllegalArgumentException("Can't interpolate between two incompatible pathData");
        }
    }
}
