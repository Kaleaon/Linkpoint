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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
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

    private static class PathDataEvaluator implements TypeEvaluator<PathParser.PathDataNode[]> {
        private PathParser.PathDataNode[] mNodeArray;

        private PathDataEvaluator() {
        }

        PathDataEvaluator(PathParser.PathDataNode[] pathDataNodeArr) {
            this.mNodeArray = pathDataNodeArr;
        }

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
        return createAnimatorFromXml(context, resources, theme, xmlPullParser, Xml.asAttributeSet(xmlPullParser), (AnimatorSet) null, 0, f);
    }

    /* JADX WARNING: Removed duplicated region for block: B:42:0x010a  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0111 A[LOOP:1: B:39:0x0102->B:43:0x0111, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x011d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.animation.Animator createAnimatorFromXml(android.content.Context r17, android.content.res.Resources r18, android.content.res.Resources.Theme r19, org.xmlpull.v1.XmlPullParser r20, android.util.AttributeSet r21, android.animation.AnimatorSet r22, int r23, float r24) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r5 = 0
            r4 = 0
            int r15 = r20.getDepth()
            r13 = r4
        L_0x0007:
            int r4 = r20.next()
            r6 = 3
            if (r4 == r6) goto L_0x0014
        L_0x000e:
            r6 = 1
            if (r4 != r6) goto L_0x001b
        L_0x0011:
            if (r22 != 0) goto L_0x00f4
        L_0x0013:
            return r5
        L_0x0014:
            int r6 = r20.getDepth()
            if (r6 > r15) goto L_0x000e
            goto L_0x0011
        L_0x001b:
            r6 = 2
            if (r4 != r6) goto L_0x0007
            java.lang.String r4 = r20.getName()
            r14 = 0
            java.lang.String r6 = "objectAnimator"
            boolean r6 = r4.equals(r6)
            if (r6 != 0) goto L_0x0065
            java.lang.String r6 = "animator"
            boolean r6 = r4.equals(r6)
            if (r6 != 0) goto L_0x007b
            java.lang.String r6 = "set"
            boolean r6 = r4.equals(r6)
            if (r6 != 0) goto L_0x008e
            java.lang.String r6 = "propertyValuesHolder"
            boolean r4 = r4.equals(r6)
            if (r4 != 0) goto L_0x00c4
            java.lang.RuntimeException r4 = new java.lang.RuntimeException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Unknown animator name: "
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r6 = r20.getName()
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r5 = r5.toString()
            r4.<init>(r5)
            throw r4
        L_0x0065:
            r4 = r17
            r5 = r18
            r6 = r19
            r7 = r21
            r8 = r24
            r9 = r20
            android.animation.ObjectAnimator r5 = loadObjectAnimator(r4, r5, r6, r7, r8, r9)
            r4 = r14
        L_0x0076:
            if (r22 != 0) goto L_0x00e5
        L_0x0078:
            r4 = r13
        L_0x0079:
            r13 = r4
            goto L_0x0007
        L_0x007b:
            r8 = 0
            r4 = r17
            r5 = r18
            r6 = r19
            r7 = r21
            r9 = r24
            r10 = r20
            android.animation.ValueAnimator r5 = loadAnimator(r4, r5, r6, r7, r8, r9, r10)
            r4 = r14
            goto L_0x0076
        L_0x008e:
            android.animation.AnimatorSet r12 = new android.animation.AnimatorSet
            r12.<init>()
            int[] r4 = android.support.graphics.drawable.AndroidResources.STYLEABLE_ANIMATOR_SET
            r0 = r18
            r1 = r19
            r2 = r21
            android.content.res.TypedArray r16 = android.support.v4.content.res.TypedArrayUtils.obtainAttributes(r0, r1, r2, r4)
            java.lang.String r4 = "ordering"
            r5 = 0
            r6 = 0
            r0 = r16
            r1 = r20
            int r10 = android.support.v4.content.res.TypedArrayUtils.getNamedInt(r0, r1, r4, r5, r6)
            r9 = r12
            android.animation.AnimatorSet r9 = (android.animation.AnimatorSet) r9
            r4 = r17
            r5 = r18
            r6 = r19
            r7 = r20
            r8 = r21
            r11 = r24
            createAnimatorFromXml(r4, r5, r6, r7, r8, r9, r10, r11)
            r16.recycle()
            r4 = r14
            r5 = r12
            goto L_0x0076
        L_0x00c4:
            android.util.AttributeSet r4 = android.util.Xml.asAttributeSet(r20)
            r0 = r17
            r1 = r18
            r2 = r19
            r3 = r20
            android.animation.PropertyValuesHolder[] r6 = loadValues(r0, r1, r2, r3, r4)
            if (r6 != 0) goto L_0x00d8
        L_0x00d6:
            r4 = 1
            goto L_0x0076
        L_0x00d8:
            if (r5 == 0) goto L_0x00d6
            boolean r4 = r5 instanceof android.animation.ValueAnimator
            if (r4 == 0) goto L_0x00d6
            r4 = r5
            android.animation.ValueAnimator r4 = (android.animation.ValueAnimator) r4
            r4.setValues(r6)
            goto L_0x00d6
        L_0x00e5:
            if (r4 != 0) goto L_0x0078
            if (r13 == 0) goto L_0x00ee
        L_0x00e9:
            r13.add(r5)
            r4 = r13
            goto L_0x0079
        L_0x00ee:
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            goto L_0x00e9
        L_0x00f4:
            if (r13 == 0) goto L_0x0013
            int r4 = r13.size()
            android.animation.Animator[] r8 = new android.animation.Animator[r4]
            r4 = 0
            java.util.Iterator r9 = r13.iterator()
            r6 = r4
        L_0x0102:
            boolean r4 = r9.hasNext()
            if (r4 != 0) goto L_0x0111
            if (r23 == 0) goto L_0x011d
            r0 = r22
            r0.playSequentially(r8)
            goto L_0x0013
        L_0x0111:
            java.lang.Object r4 = r9.next()
            android.animation.Animator r4 = (android.animation.Animator) r4
            int r7 = r6 + 1
            r8[r6] = r4
            r6 = r7
            goto L_0x0102
        L_0x011d:
            r0 = r22
            r0.playTogether(r8)
            goto L_0x0013
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.graphics.drawable.AnimatorInflaterCompat.createAnimatorFromXml(android.content.Context, android.content.res.Resources, android.content.res.Resources$Theme, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.animation.AnimatorSet, int, float):android.animation.Animator");
    }

    private static Keyframe createNewKeyframe(Keyframe keyframe, float f) {
        return keyframe.getType() != Float.TYPE ? keyframe.getType() != Integer.TYPE ? Keyframe.ofObject(f) : Keyframe.ofInt(f) : Keyframe.ofFloat(f);
    }

    private static void distributeKeyframes(Keyframe[] keyframeArr, float f, int i, int i2) {
        float f2 = f / ((float) ((i2 - i) + 2));
        while (i <= i2) {
            keyframeArr[i].setFraction(keyframeArr[i - 1].getFraction() + f2);
            i++;
        }
    }

    private static void dumpKeyframes(Object[] objArr, String str) {
        if (objArr != null && objArr.length != 0) {
            Log.d(TAG, str);
            int length = objArr.length;
            for (int i = 0; i < length; i++) {
                Keyframe keyframe = objArr[i];
                Log.d(TAG, "Keyframe " + i + ": fraction " + (keyframe.getFraction() < 0.0f ? "null" : Float.valueOf(keyframe.getFraction())) + ", " + ", value : " + (!keyframe.hasValue() ? "null" : keyframe.getValue()));
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: java.lang.Object[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.animation.PropertyValuesHolder getPVH(android.content.res.TypedArray r11, int r12, int r13, int r14, java.lang.String r15) {
        /*
            r10 = 5
            r9 = 0
            r2 = 1
            r7 = 0
            r1 = 0
            android.util.TypedValue r3 = r11.peekValue(r13)
            if (r3 != 0) goto L_0x0030
            r0 = r1
        L_0x000c:
            if (r0 != 0) goto L_0x0032
            r3 = r1
        L_0x000f:
            android.util.TypedValue r5 = r11.peekValue(r14)
            if (r5 != 0) goto L_0x0035
            r4 = r1
        L_0x0016:
            if (r4 != 0) goto L_0x0037
            r5 = r1
        L_0x0019:
            r6 = 4
            if (r12 == r6) goto L_0x003a
        L_0x001c:
            if (r12 == 0) goto L_0x004f
            r6 = r1
        L_0x001f:
            r8 = 2
            if (r12 == r8) goto L_0x0051
            r8 = 3
            if (r12 == r8) goto L_0x00c3
            r8 = r7
        L_0x0026:
            if (r6 != 0) goto L_0x00c9
            if (r0 != 0) goto L_0x0111
            if (r4 != 0) goto L_0x015b
            r0 = r7
        L_0x002d:
            if (r0 != 0) goto L_0x017c
        L_0x002f:
            return r0
        L_0x0030:
            r0 = r2
            goto L_0x000c
        L_0x0032:
            int r3 = r3.type
            goto L_0x000f
        L_0x0035:
            r4 = r2
            goto L_0x0016
        L_0x0037:
            int r5 = r5.type
            goto L_0x0019
        L_0x003a:
            if (r0 != 0) goto L_0x0040
        L_0x003c:
            if (r4 != 0) goto L_0x0048
        L_0x003e:
            r12 = r1
            goto L_0x001c
        L_0x0040:
            boolean r6 = isColorType(r3)
            if (r6 == 0) goto L_0x003c
        L_0x0046:
            r12 = 3
            goto L_0x001c
        L_0x0048:
            boolean r6 = isColorType(r5)
            if (r6 != 0) goto L_0x0046
            goto L_0x003e
        L_0x004f:
            r6 = r2
            goto L_0x001f
        L_0x0051:
            java.lang.String r0 = r11.getString(r13)
            java.lang.String r3 = r11.getString(r14)
            android.support.v4.graphics.PathParser$PathDataNode[] r4 = android.support.v4.graphics.PathParser.createNodesFromPathData(r0)
            android.support.v4.graphics.PathParser$PathDataNode[] r5 = android.support.v4.graphics.PathParser.createNodesFromPathData(r3)
            if (r4 == 0) goto L_0x0069
        L_0x0063:
            if (r4 != 0) goto L_0x006d
            if (r5 != 0) goto L_0x00b4
            r0 = r7
            goto L_0x002f
        L_0x0069:
            if (r5 != 0) goto L_0x0063
            r0 = r7
            goto L_0x002f
        L_0x006d:
            android.support.graphics.drawable.AnimatorInflaterCompat$PathDataEvaluator r6 = new android.support.graphics.drawable.AnimatorInflaterCompat$PathDataEvaluator
            r6.<init>()
            if (r5 != 0) goto L_0x007d
            java.lang.Object[] r0 = new java.lang.Object[r2]
            r0[r1] = r4
            android.animation.PropertyValuesHolder r0 = android.animation.PropertyValuesHolder.ofObject(r15, r6, r0)
            goto L_0x002f
        L_0x007d:
            boolean r7 = android.support.v4.graphics.PathParser.canMorph(r4, r5)
            if (r7 == 0) goto L_0x008f
            r0 = 2
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r0[r1] = r4
            r0[r2] = r5
            android.animation.PropertyValuesHolder r0 = android.animation.PropertyValuesHolder.ofObject(r15, r6, r0)
            goto L_0x002f
        L_0x008f:
            android.view.InflateException r1 = new android.view.InflateException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = " Can't morph from "
            java.lang.StringBuilder r2 = r2.append(r4)
            java.lang.StringBuilder r0 = r2.append(r0)
            java.lang.String r2 = " to "
            java.lang.StringBuilder r0 = r0.append(r2)
            java.lang.StringBuilder r0 = r0.append(r3)
            java.lang.String r0 = r0.toString()
            r1.<init>(r0)
            throw r1
        L_0x00b4:
            android.support.graphics.drawable.AnimatorInflaterCompat$PathDataEvaluator r0 = new android.support.graphics.drawable.AnimatorInflaterCompat$PathDataEvaluator
            r0.<init>()
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r1] = r5
            android.animation.PropertyValuesHolder r0 = android.animation.PropertyValuesHolder.ofObject(r15, r0, r2)
            goto L_0x002f
        L_0x00c3:
            android.support.graphics.drawable.ArgbEvaluator r8 = android.support.graphics.drawable.ArgbEvaluator.getInstance()
            goto L_0x0026
        L_0x00c9:
            if (r0 != 0) goto L_0x00db
            if (r5 == r10) goto L_0x010c
            float r0 = r11.getFloat(r14, r9)
        L_0x00d1:
            float[] r2 = new float[r2]
            r2[r1] = r0
            android.animation.PropertyValuesHolder r0 = android.animation.PropertyValuesHolder.ofFloat(r15, r2)
            goto L_0x002d
        L_0x00db:
            if (r3 == r10) goto L_0x00ee
            float r0 = r11.getFloat(r13, r9)
            r3 = r0
        L_0x00e2:
            if (r4 != 0) goto L_0x00f4
            float[] r0 = new float[r2]
            r0[r1] = r3
            android.animation.PropertyValuesHolder r0 = android.animation.PropertyValuesHolder.ofFloat(r15, r0)
            goto L_0x002d
        L_0x00ee:
            float r0 = r11.getDimension(r13, r9)
            r3 = r0
            goto L_0x00e2
        L_0x00f4:
            if (r5 == r10) goto L_0x0107
            float r0 = r11.getFloat(r14, r9)
        L_0x00fa:
            r4 = 2
            float[] r4 = new float[r4]
            r4[r1] = r3
            r4[r2] = r0
            android.animation.PropertyValuesHolder r0 = android.animation.PropertyValuesHolder.ofFloat(r15, r4)
            goto L_0x002d
        L_0x0107:
            float r0 = r11.getDimension(r14, r9)
            goto L_0x00fa
        L_0x010c:
            float r0 = r11.getDimension(r14, r9)
            goto L_0x00d1
        L_0x0111:
            if (r3 == r10) goto L_0x012a
            boolean r0 = isColorType(r3)
            if (r0 != 0) goto L_0x0131
            int r0 = r11.getInt(r13, r1)
            r3 = r0
        L_0x011e:
            if (r4 != 0) goto L_0x0137
            int[] r0 = new int[r2]
            r0[r1] = r3
            android.animation.PropertyValuesHolder r0 = android.animation.PropertyValuesHolder.ofInt(r15, r0)
            goto L_0x002d
        L_0x012a:
            float r0 = r11.getDimension(r13, r9)
            int r0 = (int) r0
            r3 = r0
            goto L_0x011e
        L_0x0131:
            int r0 = r11.getColor(r13, r1)
            r3 = r0
            goto L_0x011e
        L_0x0137:
            if (r5 == r10) goto L_0x0150
            boolean r0 = isColorType(r5)
            if (r0 != 0) goto L_0x0156
            int r0 = r11.getInt(r14, r1)
        L_0x0143:
            r4 = 2
            int[] r4 = new int[r4]
            r4[r1] = r3
            r4[r2] = r0
            android.animation.PropertyValuesHolder r0 = android.animation.PropertyValuesHolder.ofInt(r15, r4)
            goto L_0x002d
        L_0x0150:
            float r0 = r11.getDimension(r14, r9)
            int r0 = (int) r0
            goto L_0x0143
        L_0x0156:
            int r0 = r11.getColor(r14, r1)
            goto L_0x0143
        L_0x015b:
            if (r5 == r10) goto L_0x0171
            boolean r0 = isColorType(r5)
            if (r0 != 0) goto L_0x0177
            int r0 = r11.getInt(r14, r1)
        L_0x0167:
            int[] r2 = new int[r2]
            r2[r1] = r0
            android.animation.PropertyValuesHolder r0 = android.animation.PropertyValuesHolder.ofInt(r15, r2)
            goto L_0x002d
        L_0x0171:
            float r0 = r11.getDimension(r14, r9)
            int r0 = (int) r0
            goto L_0x0167
        L_0x0177:
            int r0 = r11.getColor(r14, r1)
            goto L_0x0167
        L_0x017c:
            if (r8 == 0) goto L_0x002f
            r0.setEvaluator(r8)
            goto L_0x002f
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.graphics.drawable.AnimatorInflaterCompat.getPVH(android.content.res.TypedArray, int, int, int, java.lang.String):android.animation.PropertyValuesHolder");
    }

    private static int inferValueTypeFromValues(TypedArray typedArray, int i, int i2) {
        boolean z = true;
        TypedValue peekValue = typedArray.peekValue(i);
        boolean z2 = peekValue != null;
        int i3 = !z2 ? 0 : peekValue.type;
        TypedValue peekValue2 = typedArray.peekValue(i2);
        if (peekValue2 == null) {
            z = false;
        }
        return ((z2 && isColorType(i3)) || (z && isColorType(!z ? 0 : peekValue2.type))) ? 3 : 0;
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
            xmlResourceParser = resources.getAnimation(i);
            Animator createAnimatorFromXml = createAnimatorFromXml(context, resources, theme, xmlResourceParser, f);
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
            if (!(next == 3 || next == 1)) {
                if (!xmlPullParser.getName().equals("keyframe")) {
                    arrayList2 = arrayList;
                } else {
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
                }
            }
        }
        if (arrayList == null || (size = arrayList.size()) <= 0) {
            return null;
        }
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
                    int i4 = i3 + 1;
                    int i5 = i3;
                    while (i4 < i2 - 1 && keyframeArr[i4].getFraction() < 0.0f) {
                        i5 = i4;
                        i4++;
                    }
                    distributeKeyframes(keyframeArr, keyframeArr[i5 + 1].getFraction() - keyframeArr[i3 - 1].getFraction(), i3, i5);
                } else {
                    keyframe3.setFraction(1.0f);
                }
            }
        }
        PropertyValuesHolder ofKeyframe = PropertyValuesHolder.ofKeyframe(str, keyframeArr);
        if (i != 3) {
            return ofKeyframe;
        }
        ofKeyframe.setEvaluator(ArgbEvaluator.getInstance());
        return ofKeyframe;
    }

    private static PropertyValuesHolder[] loadValues(Context context, Resources resources, Resources.Theme theme, XmlPullParser xmlPullParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        ArrayList arrayList = null;
        while (true) {
            int eventType = xmlPullParser.getEventType();
            if (!(eventType == 3 || eventType == 1)) {
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
        long namedInt = (long) TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "duration", 1, 300);
        long namedInt2 = (long) TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "startOffset", 2, 0);
        int namedInt3 = TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "valueType", 7, 4);
        if (TypedArrayUtils.hasAttribute(xmlPullParser, "valueFrom") && TypedArrayUtils.hasAttribute(xmlPullParser, "valueTo")) {
            if (namedInt3 == 4) {
                namedInt3 = inferValueTypeFromValues(typedArray, 5, 6);
            }
            PropertyValuesHolder pvh = getPVH(typedArray, namedInt3, 5, 6, "");
            if (pvh != null) {
                valueAnimator.setValues(new PropertyValuesHolder[]{pvh});
            }
        }
        valueAnimator.setDuration(namedInt);
        valueAnimator.setStartDelay(namedInt2);
        valueAnimator.setRepeatCount(TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "repeatCount", 3, 0));
        valueAnimator.setRepeatMode(TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "repeatMode", 4, 1));
        if (typedArray2 != null) {
            setupObjectAnimator(valueAnimator, typedArray2, namedInt3, f, xmlPullParser);
        }
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
        float f3 = f2 / ((float) (min - 1));
        int i3 = 0;
        float f4 = 0.0f;
        while (i3 < min) {
            pathMeasure2.getPosTan(f4, fArr3, (float[]) null);
            pathMeasure2.getPosTan(f4, fArr3, (float[]) null);
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
        PropertyValuesHolder propertyValuesHolder = null;
        PropertyValuesHolder propertyValuesHolder2 = null;
        if (str != null) {
            propertyValuesHolder = PropertyValuesHolder.ofFloat(str, fArr);
        }
        if (str2 != null) {
            propertyValuesHolder2 = PropertyValuesHolder.ofFloat(str2, fArr2);
        }
        if (propertyValuesHolder == null) {
            objectAnimator.setValues(new PropertyValuesHolder[]{propertyValuesHolder2});
        } else if (propertyValuesHolder2 != null) {
            objectAnimator.setValues(new PropertyValuesHolder[]{propertyValuesHolder, propertyValuesHolder2});
        } else {
            objectAnimator.setValues(new PropertyValuesHolder[]{propertyValuesHolder});
        }
    }
}
