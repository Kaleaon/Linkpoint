package android.support.v7.content.res;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.util.StateSet;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

final class AppCompatColorStateListInflater {
    private static final int DEFAULT_COLOR = -65536;

    private AppCompatColorStateListInflater() {
    }

    /* JADX WARNING: Removed duplicated region for block: B:4:0x000d  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0016  */
    @android.support.annotation.NonNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.content.res.ColorStateList createFromXml(@android.support.annotation.NonNull android.content.res.Resources r4, @android.support.annotation.NonNull org.xmlpull.v1.XmlPullParser r5, @android.support.annotation.Nullable android.content.res.Resources.Theme r6) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r3 = 2
            android.util.AttributeSet r0 = android.util.Xml.asAttributeSet(r5)
        L_0x0005:
            int r1 = r5.next()
            if (r1 != r3) goto L_0x0012
        L_0x000b:
            if (r1 != r3) goto L_0x0016
            android.content.res.ColorStateList r0 = createFromXmlInner(r4, r5, r0, r6)
            return r0
        L_0x0012:
            r2 = 1
            if (r1 != r2) goto L_0x0005
            goto L_0x000b
        L_0x0016:
            org.xmlpull.v1.XmlPullParserException r0 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r1 = "No start tag found"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.content.res.AppCompatColorStateListInflater.createFromXml(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.content.res.Resources$Theme):android.content.res.ColorStateList");
    }

    @NonNull
    private static ColorStateList createFromXmlInner(@NonNull Resources resources, @NonNull XmlPullParser xmlPullParser, @NonNull AttributeSet attributeSet, @Nullable Resources.Theme theme) throws XmlPullParserException, IOException {
        String name = xmlPullParser.getName();
        if (name.equals("selector")) {
            return inflate(resources, xmlPullParser, attributeSet, theme);
        }
        throw new XmlPullParserException(xmlPullParser.getPositionDescription() + ": invalid color state list tag " + name);
    }

    private static ColorStateList inflate(@NonNull Resources resources, @NonNull XmlPullParser xmlPullParser, @NonNull AttributeSet attributeSet, @Nullable Resources.Theme theme) throws XmlPullParserException, IOException {
        int depth;
        int i;
        int depth2 = xmlPullParser.getDepth() + 1;
        int[][] iArr = new int[20][];
        int[][] iArr2 = iArr;
        int i2 = 0;
        int[] iArr3 = new int[iArr.length];
        while (true) {
            int next = xmlPullParser.next();
            if (next != 1 && ((depth = xmlPullParser.getDepth()) >= depth2 || next != 3)) {
                if (next == 2 && depth <= depth2 && xmlPullParser.getName().equals("item")) {
                    TypedArray obtainAttributes = obtainAttributes(resources, theme, attributeSet, R.styleable.ColorStateListItem);
                    int color = obtainAttributes.getColor(R.styleable.ColorStateListItem_android_color, -65281);
                    float f = 1.0f;
                    if (obtainAttributes.hasValue(R.styleable.ColorStateListItem_android_alpha)) {
                        f = obtainAttributes.getFloat(R.styleable.ColorStateListItem_android_alpha, 1.0f);
                    } else if (obtainAttributes.hasValue(R.styleable.ColorStateListItem_alpha)) {
                        f = obtainAttributes.getFloat(R.styleable.ColorStateListItem_alpha, 1.0f);
                    }
                    obtainAttributes.recycle();
                    int i3 = 0;
                    int attributeCount = attributeSet.getAttributeCount();
                    int[] iArr4 = new int[attributeCount];
                    int i4 = 0;
                    while (i4 < attributeCount) {
                        int attributeNameResource = attributeSet.getAttributeNameResource(i4);
                        if (attributeNameResource == 16843173 || attributeNameResource == 16843551 || attributeNameResource == R.attr.alpha) {
                            i = i3;
                        } else {
                            int i5 = i3 + 1;
                            if (!attributeSet.getAttributeBooleanValue(i4, false)) {
                                attributeNameResource = -attributeNameResource;
                            }
                            iArr4[i3] = attributeNameResource;
                            i = i5;
                        }
                        i4++;
                        i3 = i;
                    }
                    int[] trimStateSet = StateSet.trimStateSet(iArr4, i3);
                    int modulateColorAlpha = modulateColorAlpha(color, f);
                    if (i2 != 0) {
                        int length = trimStateSet.length;
                    }
                    int[] append = GrowingArrayUtils.append(iArr3, i2, modulateColorAlpha);
                    i2++;
                    iArr2 = (int[][]) GrowingArrayUtils.append((T[]) iArr2, i2, trimStateSet);
                    iArr3 = append;
                }
            }
        }
        int[] iArr5 = new int[i2];
        int[][] iArr6 = new int[i2][];
        System.arraycopy(iArr3, 0, iArr5, 0, i2);
        System.arraycopy(iArr2, 0, iArr6, 0, i2);
        return new ColorStateList(iArr6, iArr5);
    }

    private static int modulateColorAlpha(int i, float f) {
        return ColorUtils.setAlphaComponent(i, Math.round(((float) Color.alpha(i)) * f));
    }

    private static TypedArray obtainAttributes(Resources resources, Resources.Theme theme, AttributeSet attributeSet, int[] iArr) {
        return theme != null ? theme.obtainStyledAttributes(attributeSet, iArr, 0, 0) : resources.obtainAttributes(attributeSet, iArr);
    }
}
