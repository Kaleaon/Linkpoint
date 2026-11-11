package android.support.graphics.drawable;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Build;
import android.support.annotation.AnimatorRes;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.graphics.PathParser;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.InflateException;
import com.google.vr.cardboard.VrSettingsProviderContract;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class AnimatorInflaterCompat {
    private static final boolean DBG_ANIMATOR_INFLATER = false;
    private static final int MAX_NUM_POINTS = 100;
    private static final String TAG = "AnimatorInflater";
    private static final int TOGETHER = 0;
    private static final int VALUE_TYPE_COLOR = 3;
    private static final int VALUE_TYPE_FLOAT = 0;
    private static final int VALUE_TYPE_INT = 1;
    private static final int VALUE_TYPE_PATH = 2;
    private static final int VALUE_TYPE_UNDEFINED = 4;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PathDataEvaluator implements TypeEvaluator<PathParser.PathDataNode[]> {
        private PathParser.PathDataNode[] mNodeArray;

        private PathDataEvaluator() {
        }

        PathDataEvaluator(PathParser.PathDataNode[] pathDataNodeArr) {
            this.mNodeArray = pathDataNodeArr;
        }

        @Override // android.animation.TypeEvaluator
        public PathParser.PathDataNode[] evaluate(float f, PathParser.PathDataNode[] pathDataNodeArr, PathParser.PathDataNode[] pathDataNodeArr2) {
            if (PathParser.canMorph(pathDataNodeArr, pathDataNodeArr2)) {
                if (this.mNodeArray == null || !PathParser.canMorph(this.mNodeArray, pathDataNodeArr)) {
                    this.mNodeArray = PathParser.deepCopyNodes(pathDataNodeArr);
                }
                for (int i = 0; i < pathDataNodeArr.length; i++) {
                    this.mNodeArray[i].interpolatePathDataNode(pathDataNodeArr[i], pathDataNodeArr2[i], f);
                }
                return this.mNodeArray;
            }
            throw new IllegalArgumentException("Can't interpolate between two incompatible pathData");
        }
    }

    private static Animator createAnimatorFromXml(Context context, Resources resources, Resources.Theme theme, XmlPullParser xmlPullParser, float f) throws XmlPullParserException, IOException {
        return createAnimatorFromXml(context, resources, theme, xmlPullParser, Xml.asAttributeSet(xmlPullParser), null, 0, f);
    }

    private static Animator createAnimatorFromXml(Context context, Resources resources, Resources.Theme theme, XmlPullParser xmlPullParser, AttributeSet attributeSet, AnimatorSet animatorSet, int i, float f) throws XmlPullParserException, IOException {
        boolean z;
        ValueAnimator valueAnimator = null;
        int depth = xmlPullParser.getDepth();
        ArrayList arrayList = null;
        while (true) {
            int next = xmlPullParser.next();
            if ((next == 3 && xmlPullParser.getDepth() <= depth) || next == 1) {
                break;
            } else if (next == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("objectAnimator")) {
                    valueAnimator = loadObjectAnimator(context, resources, theme, attributeSet, f, xmlPullParser);
                    z = false;
                } else if (name.equals("animator")) {
                    valueAnimator = loadAnimator(context, resources, theme, attributeSet, null, f, xmlPullParser);
                    z = false;
                } else if (name.equals("set")) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    TypedArray obtainAttributes = TypedArrayUtils.obtainAttributes(resources, theme, attributeSet, AndroidResources.STYLEABLE_ANIMATOR_SET);
                    createAnimatorFromXml(context, resources, theme, xmlPullParser, attributeSet, animatorSet2, TypedArrayUtils.getNamedInt(obtainAttributes, xmlPullParser, "ordering", 0, 0), f);
                    obtainAttributes.recycle();
                    z = false;
                    valueAnimator = animatorSet2;
                } else if (!name.equals("propertyValuesHolder")) {
                    throw new RuntimeException("Unknown animator name: " + xmlPullParser.getName());
                } else {
                    PropertyValuesHolder[] loadValues = loadValues(context, resources, theme, xmlPullParser, Xml.asAttributeSet(xmlPullParser));
                    if (loadValues != null && valueAnimator != null && (valueAnimator instanceof ValueAnimator)) {
                        valueAnimator.setValues(loadValues);
                    }
                    z = true;
                }
                if (animatorSet != null && !z) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(valueAnimator);
                }
                arrayList = arrayList;
            }
        }
        if (animatorSet != null && arrayList != null) {
            Animator[] animatorArr = new Animator[arrayList.size()];
            Iterator it = arrayList.iterator();
            int i2 = 0;
            while (it.hasNext()) {
                animatorArr[i2] = (Animator) it.next();
                i2++;
            }
            if (i != 0) {
                animatorSet.playSequentially(animatorArr);
            } else {
                animatorSet.playTogether(animatorArr);
            }
        }
        return valueAnimator;
    }

    private static Keyframe createNewKeyframe(Keyframe keyframe, float f) {
        return keyframe.getType() != Float.TYPE ? keyframe.getType() != Integer.TYPE ? Keyframe.ofObject(f) : Keyframe.ofInt(f) : Keyframe.ofFloat(f);
    }

    private static void distributeKeyframes(Keyframe[] keyframeArr, float f, int i, int i2) {
        float f2 = f / ((i2 - i) + 2);
        while (i <= i2) {
            keyframeArr[i].setFraction(keyframeArr[i - 1].getFraction() + f2);
            i++;
        }
    }

    private static void dumpKeyframes(Object[] objArr, String str) {
        if (objArr == null || objArr.length == 0) {
            return;
        }
        Log.d(TAG, str);
        int length = objArr.length;
        for (int i = 0; i < length; i++) {
            Keyframe keyframe = (Keyframe) objArr[i];
            Log.d(TAG, "Keyframe " + i + ": fraction " + (keyframe.getFraction() < 0.0f ? "null" : Float.valueOf(keyframe.getFraction())) + ", , value : " + (!keyframe.hasValue() ? "null" : keyframe.getValue()));
        }
    }

    private static PropertyValuesHolder getPVH(TypedArray typedArray, int i, int i2, int i3, String str) {
        PropertyValuesHolder ofFloat;
        TypedValue peekValue = typedArray.peekValue(i2);
        boolean z = peekValue != null;
        int i4 = !z ? 0 : peekValue.type;
        TypedValue peekValue2 = typedArray.peekValue(i3);
        boolean z2 = peekValue2 != null;
        int i5 = !z2 ? 0 : peekValue2.type;
        if (i == 4) {
            i = ((z && isColorType(i4)) || (z2 && isColorType(i5))) ? 3 : 0;
        }
        boolean z3 = i == 0;
        if (i == 2) {
            String string = typedArray.getString(i2);
            String string2 = typedArray.getString(i3);
            PathParser.PathDataNode[] createNodesFromPathData = PathParser.createNodesFromPathData(string);
            PathParser.PathDataNode[] createNodesFromPathData2 = PathParser.createNodesFromPathData(string2);
            if (createNodesFromPathData == null && createNodesFromPathData2 == null) {
                return null;
            }
            if (createNodesFromPathData == null) {
                if (createNodesFromPathData2 == null) {
                    return null;
                }
                return PropertyValuesHolder.ofObject(str, new PathDataEvaluator(), createNodesFromPathData2);
            }
            PathDataEvaluator pathDataEvaluator = new PathDataEvaluator();
            if (createNodesFromPathData2 == null) {
                return PropertyValuesHolder.ofObject(str, pathDataEvaluator, createNodesFromPathData);
            }
            if (PathParser.canMorph(createNodesFromPathData, createNodesFromPathData2)) {
                return PropertyValuesHolder.ofObject(str, pathDataEvaluator, createNodesFromPathData, createNodesFromPathData2);
            }
            throw new InflateException(" Can't morph from " + string + " to " + string2);
        }
        ArgbEvaluator argbEvaluator = i != 3 ? null : ArgbEvaluator.getInstance();
        if (z3) {
            if (z) {
                float f = i4 != 5 ? typedArray.getFloat(i2, 0.0f) : typedArray.getDimension(i2, 0.0f);
                if (z2) {
                    ofFloat = PropertyValuesHolder.ofFloat(str, f, i5 != 5 ? typedArray.getFloat(i3, 0.0f) : typedArray.getDimension(i3, 0.0f));
                } else {
                    ofFloat = PropertyValuesHolder.ofFloat(str, f);
                }
            } else {
                ofFloat = PropertyValuesHolder.ofFloat(str, i5 != 5 ? typedArray.getFloat(i3, 0.0f) : typedArray.getDimension(i3, 0.0f));
            }
        } else if (z) {
            int i6 = i4 != 5 ? !isColorType(i4) ? typedArray.getInt(i2, 0) : typedArray.getColor(i2, 0) : (int) typedArray.getDimension(i2, 0.0f);
            if (z2) {
                ofFloat = PropertyValuesHolder.ofInt(str, i6, i5 != 5 ? !isColorType(i5) ? typedArray.getInt(i3, 0) : typedArray.getColor(i3, 0) : (int) typedArray.getDimension(i3, 0.0f));
            } else {
                ofFloat = PropertyValuesHolder.ofInt(str, i6);
            }
        } else if (z2) {
            ofFloat = PropertyValuesHolder.ofInt(str, i5 != 5 ? !isColorType(i5) ? typedArray.getInt(i3, 0) : typedArray.getColor(i3, 0) : (int) typedArray.getDimension(i3, 0.0f));
        } else {
            ofFloat = null;
        }
        if (ofFloat == null || argbEvaluator == null) {
            return ofFloat;
        }
        ofFloat.setEvaluator(argbEvaluator);
        return ofFloat;
    }

    private static int inferValueTypeFromValues(TypedArray typedArray, int i, int i2) {
        TypedValue peekValue = typedArray.peekValue(i);
        boolean z = peekValue != null;
        int i3 = !z ? 0 : peekValue.type;
        TypedValue peekValue2 = typedArray.peekValue(i2);
        boolean z2 = peekValue2 != null;
        return ((z && isColorType(i3)) || (z2 && isColorType(!z2 ? 0 : peekValue2.type))) ? 3 : 0;
    }

    private static int inferValueTypeOfKeyframe(Resources resources, Resources.Theme theme, AttributeSet attributeSet, XmlPullParser xmlPullParser) {
        int i = 0;
        TypedArray obtainAttributes = TypedArrayUtils.obtainAttributes(resources, theme, attributeSet, AndroidResources.STYLEABLE_KEYFRAME);
        TypedValue peekNamedValue = TypedArrayUtils.peekNamedValue(obtainAttributes, xmlPullParser, VrSettingsProviderContract.SETTING_VALUE_KEY, 0);
        if ((peekNamedValue != null) && isColorType(peekNamedValue.type)) {
            i = 3;
        }
        obtainAttributes.recycle();
        return i;
    }

    private static boolean isColorType(int i) {
        return i >= 28 && i <= 31;
    }

    public static Animator loadAnimator(Context context, @AnimatorRes int i) throws Resources.NotFoundException {
        return Build.VERSION.SDK_INT < 24 ? loadAnimator(context, context.getResources(), context.getTheme(), i) : AnimatorInflater.loadAnimator(context, i);
    }

    public static Animator loadAnimator(Context context, Resources resources, Resources.Theme theme, @AnimatorRes int i) throws Resources.NotFoundException {
        return loadAnimator(context, resources, theme, i, 1.0f);
    }

    public static Animator loadAnimator(Context context, Resources resources, Resources.Theme theme, @AnimatorRes int i, float f) throws Resources.NotFoundException {
        XmlResourceParser xmlResourceParser = null;
        try {
            try {
                try {
                    xmlResourceParser = resources.getAnimation(i);
                    return createAnimatorFromXml(context, resources, theme, xmlResourceParser, f);
                } catch (IOException e) {
                    Resources.NotFoundException notFoundException = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(i));
                    notFoundException.initCause(e);
                    throw notFoundException;
                }
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

    private static ValueAnimator loadAnimator(Context context, Resources resources, Resources.Theme theme, AttributeSet attributeSet, ValueAnimator valueAnimator, float f, XmlPullParser xmlPullParser) throws Resources.NotFoundException {
        TypedArray obtainAttributes = TypedArrayUtils.obtainAttributes(resources, theme, attributeSet, AndroidResources.STYLEABLE_ANIMATOR);
        TypedArray obtainAttributes2 = TypedArrayUtils.obtainAttributes(resources, theme, attributeSet, AndroidResources.STYLEABLE_PROPERTY_ANIMATOR);
        if (valueAnimator == null) {
            valueAnimator = new ValueAnimator();
        }
        parseAnimatorFromTypeArray(valueAnimator, obtainAttributes, obtainAttributes2, f, xmlPullParser);
        int namedResourceId = TypedArrayUtils.getNamedResourceId(obtainAttributes, xmlPullParser, "interpolator", 0, 0);
        if (namedResourceId > 0) {
            valueAnimator.setInterpolator(AnimationUtilsCompat.loadInterpolator(context, namedResourceId));
        }
        obtainAttributes.recycle();
        if (obtainAttributes2 != null) {
            obtainAttributes2.recycle();
        }
        return valueAnimator;
    }

    private static Keyframe loadKeyframe(Context context, Resources resources, Resources.Theme theme, AttributeSet attributeSet, int i, XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        Keyframe ofInt;
        TypedArray obtainAttributes = TypedArrayUtils.obtainAttributes(resources, theme, attributeSet, AndroidResources.STYLEABLE_KEYFRAME);
        float namedFloat = TypedArrayUtils.getNamedFloat(obtainAttributes, xmlPullParser, "fraction", 3, -1.0f);
        TypedValue peekNamedValue = TypedArrayUtils.peekNamedValue(obtainAttributes, xmlPullParser, VrSettingsProviderContract.SETTING_VALUE_KEY, 0);
        boolean z = peekNamedValue != null;
        if (i == 4) {
            i = (z && isColorType(peekNamedValue.type)) ? 3 : 0;
        }
        if (z) {
            switch (i) {
                case 0:
                    ofInt = Keyframe.ofFloat(namedFloat, TypedArrayUtils.getNamedFloat(obtainAttributes, xmlPullParser, VrSettingsProviderContract.SETTING_VALUE_KEY, 0, 0.0f));
                    break;
                case 1:
                case 3:
                    ofInt = Keyframe.ofInt(namedFloat, TypedArrayUtils.getNamedInt(obtainAttributes, xmlPullParser, VrSettingsProviderContract.SETTING_VALUE_KEY, 0, 0));
                    break;
                case 2:
                default:
                    ofInt = null;
                    break;
            }
        } else {
            ofInt = i != 0 ? Keyframe.ofInt(namedFloat) : Keyframe.ofFloat(namedFloat);
        }
        int namedResourceId = TypedArrayUtils.getNamedResourceId(obtainAttributes, xmlPullParser, "interpolator", 1, 0);
        if (namedResourceId > 0) {
            ofInt.setInterpolator(AnimationUtilsCompat.loadInterpolator(context, namedResourceId));
        }
        obtainAttributes.recycle();
        return ofInt;
    }

    private static ObjectAnimator loadObjectAnimator(Context context, Resources resources, Resources.Theme theme, AttributeSet attributeSet, float f, XmlPullParser xmlPullParser) throws Resources.NotFoundException {
        ObjectAnimator objectAnimator = new ObjectAnimator();
        loadAnimator(context, resources, theme, attributeSet, objectAnimator, f, xmlPullParser);
        return objectAnimator;
    }

    private static PropertyValuesHolder loadPvh(Context context, Resources resources, Resources.Theme theme, XmlPullParser xmlPullParser, String str, int i) throws XmlPullParserException, IOException {
        ArrayList arrayList;
        int size;
        int i2;
        ArrayList arrayList2 = null;
        while (true) {
            arrayList = arrayList2;
            int next = xmlPullParser.next();
            if (next != 3 && next != 1) {
                if (xmlPullParser.getName().equals("keyframe")) {
                    int inferValueTypeOfKeyframe = i != 4 ? i : inferValueTypeOfKeyframe(resources, theme, Xml.asAttributeSet(xmlPullParser), xmlPullParser);
                    Keyframe loadKeyframe = loadKeyframe(context, resources, theme, Xml.asAttributeSet(xmlPullParser), inferValueTypeOfKeyframe, xmlPullParser);
                    if (loadKeyframe != null) {
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                        }
                        arrayList.add(loadKeyframe);
                    }
                    xmlPullParser.next();
                    arrayList2 = arrayList;
                    i = inferValueTypeOfKeyframe;
                } else {
                    arrayList2 = arrayList;
                }
            }
        }
        if (arrayList != null && (size = arrayList.size()) > 0) {
            Keyframe keyframe = (Keyframe) arrayList.get(0);
            Keyframe keyframe2 = (Keyframe) arrayList.get(size - 1);
            float fraction = keyframe2.getFraction();
            if (fraction >= 1.0f) {
                i2 = size;
            } else if (fraction < 0.0f) {
                keyframe2.setFraction(1.0f);
                i2 = size;
            } else {
                arrayList.add(arrayList.size(), createNewKeyframe(keyframe2, 1.0f));
                i2 = size + 1;
            }
            float fraction2 = keyframe.getFraction();
            if (fraction2 != 0.0f) {
                if (fraction2 < 0.0f) {
                    keyframe.setFraction(0.0f);
                } else {
                    arrayList.add(0, createNewKeyframe(keyframe, 0.0f));
                    i2++;
                }
            }
            Keyframe[] keyframeArr = new Keyframe[i2];
            arrayList.toArray(keyframeArr);
            for (int i3 = 0; i3 < i2; i3++) {
                Keyframe keyframe3 = keyframeArr[i3];
                if (keyframe3.getFraction() < 0.0f) {
                    if (i3 == 0) {
                        keyframe3.setFraction(0.0f);
                    } else if (i3 != i2 - 1) {
                        int i4 = i3;
                        for (int i5 = i3 + 1; i5 < i2 - 1 && keyframeArr[i5].getFraction() < 0.0f; i5++) {
                            i4 = i5;
                        }
                        distributeKeyframes(keyframeArr, keyframeArr[i4 + 1].getFraction() - keyframeArr[i3 - 1].getFraction(), i3, i4);
                    } else {
                        keyframe3.setFraction(1.0f);
                    }
                }
            }
            PropertyValuesHolder ofKeyframe = PropertyValuesHolder.ofKeyframe(str, keyframeArr);
            if (i == 3) {
                ofKeyframe.setEvaluator(ArgbEvaluator.getInstance());
                return ofKeyframe;
            }
            return ofKeyframe;
        }
        return null;
    }

    private static PropertyValuesHolder[] loadValues(Context context, Resources resources, Resources.Theme theme, XmlPullParser xmlPullParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        ArrayList arrayList = null;
        while (true) {
            int eventType = xmlPullParser.getEventType();
            if (eventType != 3 && eventType != 1) {
                if (eventType == 2) {
                    if (xmlPullParser.getName().equals("propertyValuesHolder")) {
                        TypedArray obtainAttributes = TypedArrayUtils.obtainAttributes(resources, theme, attributeSet, AndroidResources.STYLEABLE_PROPERTY_VALUES_HOLDER);
                        String namedString = TypedArrayUtils.getNamedString(obtainAttributes, xmlPullParser, "propertyName", 3);
                        int namedInt = TypedArrayUtils.getNamedInt(obtainAttributes, xmlPullParser, "valueType", 2, 4);
                        PropertyValuesHolder loadPvh = loadPvh(context, resources, theme, xmlPullParser, namedString, namedInt);
                        if (loadPvh == null) {
                            loadPvh = getPVH(obtainAttributes, namedInt, 0, 1, namedString);
                        }
                        if (loadPvh != null) {
                            if (arrayList == null) {
                                arrayList = new ArrayList();
                            }
                            arrayList.add(loadPvh);
                        }
                        obtainAttributes.recycle();
                    }
                    xmlPullParser.next();
                    arrayList = arrayList;
                } else {
                    xmlPullParser.next();
                }
            }
        }
        if (arrayList == null) {
            return null;
        }
        int size = arrayList.size();
        PropertyValuesHolder[] propertyValuesHolderArr = new PropertyValuesHolder[size];
        for (int i = 0; i < size; i++) {
            propertyValuesHolderArr[i] = (PropertyValuesHolder) arrayList.get(i);
        }
        return propertyValuesHolderArr;
    }

    private static void parseAnimatorFromTypeArray(ValueAnimator valueAnimator, TypedArray typedArray, TypedArray typedArray2, float f, XmlPullParser xmlPullParser) {
        long namedInt = TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "duration", 1, 300);
        long namedInt2 = TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "startOffset", 2, 0);
        int namedInt3 = TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "valueType", 7, 4);
        if (TypedArrayUtils.hasAttribute(xmlPullParser, "valueFrom") && TypedArrayUtils.hasAttribute(xmlPullParser, "valueTo")) {
            if (namedInt3 == 4) {
                namedInt3 = inferValueTypeFromValues(typedArray, 5, 6);
            }
            PropertyValuesHolder pvh = getPVH(typedArray, namedInt3, 5, 6, "");
            if (pvh != null) {
                valueAnimator.setValues(pvh);
            }
        }
        valueAnimator.setDuration(namedInt);
        valueAnimator.setStartDelay(namedInt2);
        valueAnimator.setRepeatCount(TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "repeatCount", 3, 0));
        valueAnimator.setRepeatMode(TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "repeatMode", 4, 1));
        if (typedArray2 == null) {
            return;
        }
        setupObjectAnimator(valueAnimator, typedArray2, namedInt3, f, xmlPullParser);
    }

    private static void setupObjectAnimator(ValueAnimator valueAnimator, TypedArray typedArray, int i, float f, XmlPullParser xmlPullParser) {
        ObjectAnimator objectAnimator = (ObjectAnimator) valueAnimator;
        String namedString = TypedArrayUtils.getNamedString(typedArray, xmlPullParser, "pathData", 1);
        if (namedString == null) {
            objectAnimator.setPropertyName(TypedArrayUtils.getNamedString(typedArray, xmlPullParser, "propertyName", 0));
            return;
        }
        String namedString2 = TypedArrayUtils.getNamedString(typedArray, xmlPullParser, "propertyXName", 2);
        String namedString3 = TypedArrayUtils.getNamedString(typedArray, xmlPullParser, "propertyYName", 3);
        if (i != 2) {
        }
        if (namedString2 == null && namedString3 == null) {
            throw new InflateException(typedArray.getPositionDescription() + " propertyXName or propertyYName is needed for PathData");
        }
        setupPathMotion(PathParser.createPathFromPathData(namedString), objectAnimator, 0.5f * f, namedString2, namedString3);
    }

    private static void setupPathMotion(Path path, ObjectAnimator objectAnimator, float f, String str, String str2) {
        int i;
        PathMeasure pathMeasure = new PathMeasure(path, false);
        float f2 = 0.0f;
        ArrayList arrayList = new ArrayList();
        arrayList.add(Float.valueOf(0.0f));
        do {
            f2 += pathMeasure.getLength();
            arrayList.add(Float.valueOf(f2));
        } while (pathMeasure.nextContour());
        PathMeasure pathMeasure2 = new PathMeasure(path, false);
        int min = Math.min(100, ((int) (f2 / f)) + 1);
        float[] fArr = new float[min];
        float[] fArr2 = new float[min];
        float[] fArr3 = new float[2];
        int i2 = 0;
        float f3 = f2 / (min - 1);
        int i3 = 0;
        float f4 = 0.0f;
        while (i3 < min) {
            pathMeasure2.getPosTan(f4, fArr3, null);
            pathMeasure2.getPosTan(f4, fArr3, null);
            fArr[i3] = fArr3[0];
            fArr2[i3] = fArr3[1];
            float f5 = f4 + f3;
            if (i2 + 1 >= arrayList.size()) {
                f4 = f5;
                i = i2;
            } else if (f5 > ((Float) arrayList.get(i2 + 1)).floatValue()) {
                f4 = f5 - ((Float) arrayList.get(i2 + 1)).floatValue();
                i = i2 + 1;
                pathMeasure2.nextContour();
            } else {
                f4 = f5;
                i = i2;
            }
            i3++;
            i2 = i;
        }
        PropertyValuesHolder ofFloat = str != null ? PropertyValuesHolder.ofFloat(str, fArr) : null;
        PropertyValuesHolder ofFloat2 = str2 != null ? PropertyValuesHolder.ofFloat(str2, fArr2) : null;
        if (ofFloat == null) {
            objectAnimator.setValues(ofFloat2);
        } else if (ofFloat2 != null) {
            objectAnimator.setValues(ofFloat, ofFloat2);
        } else {
            objectAnimator.setValues(ofFloat);
        }
    }
}
