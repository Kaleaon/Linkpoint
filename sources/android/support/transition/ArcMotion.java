package android.support.transition;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;

public class ArcMotion extends PathMotion {
    private static final float DEFAULT_MAX_ANGLE_DEGREES = 70.0f;
    private static final float DEFAULT_MAX_TANGENT = ((float) Math.tan(Math.toRadians(35.0d)));
    private static final float DEFAULT_MIN_ANGLE_DEGREES = 0.0f;
    private float mMaximumAngle = DEFAULT_MAX_ANGLE_DEGREES;
    private float mMaximumTangent = DEFAULT_MAX_TANGENT;
    private float mMinimumHorizontalAngle = 0.0f;
    private float mMinimumHorizontalTangent = 0.0f;
    private float mMinimumVerticalAngle = 0.0f;
    private float mMinimumVerticalTangent = 0.0f;

    public ArcMotion() {
    }

    public ArcMotion(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, Styleable.ARC_MOTION);
        XmlPullParser xmlPullParser = (XmlPullParser) attributeSet;
        setMinimumVerticalAngle(TypedArrayUtils.getNamedFloat(obtainStyledAttributes, xmlPullParser, "minimumVerticalAngle", 1, 0.0f));
        setMinimumHorizontalAngle(TypedArrayUtils.getNamedFloat(obtainStyledAttributes, xmlPullParser, "minimumHorizontalAngle", 0, 0.0f));
        setMaximumAngle(TypedArrayUtils.getNamedFloat(obtainStyledAttributes, xmlPullParser, "maximumAngle", 2, DEFAULT_MAX_ANGLE_DEGREES));
        obtainStyledAttributes.recycle();
    }

    private static float toTangent(float f) {
        if (!(f < 0.0f) && f <= 90.0f) {
            return (float) Math.tan(Math.toRadians((double) (f / 2.0f)));
        }
        throw new IllegalArgumentException("Arc must be between 0 and 90 degrees");
    }

    public float getMaximumAngle() {
        return this.mMaximumAngle;
    }

    public float getMinimumHorizontalAngle() {
        return this.mMinimumHorizontalAngle;
    }

    public float getMinimumVerticalAngle() {
        return this.mMinimumVerticalAngle;
    }

    public Path getPath(float f, float f2, float f3, float f4) {
        float f5;
        float f6;
        float f7;
        float f8;
        float f9;
        float f10;
        float f11;
        float f12;
        float f13;
        Path path = new Path();
        path.moveTo(f, f2);
        float f14 = f3 - f;
        float f15 = f4 - f2;
        float f16 = (f15 * f15) + (f14 * f14);
        float f17 = (f + f3) / 2.0f;
        float f18 = (f2 + f4) / 2.0f;
        float f19 = f16 * 0.25f;
        boolean z = f2 > f4;
        if (Math.abs(f14) < Math.abs(f15)) {
            float abs = Math.abs(f16 / (2.0f * f15));
            if (!z) {
                f12 = f2 + abs;
                f13 = f;
            } else {
                f12 = f4 + abs;
                f13 = f3;
            }
            f7 = f12;
            f8 = f13;
            f9 = this.mMinimumVerticalTangent * f19 * this.mMinimumVerticalTangent;
        } else {
            float f20 = f16 / (f14 * 2.0f);
            if (!z) {
                f5 = f3 - f20;
                f6 = f4;
            } else {
                f5 = f + f20;
                f6 = f2;
            }
            f7 = f6;
            f8 = f5;
            f9 = this.mMinimumHorizontalTangent * f19 * this.mMinimumHorizontalTangent;
        }
        float f21 = f17 - f8;
        float f22 = f18 - f7;
        float f23 = (f22 * f22) + (f21 * f21);
        float f24 = this.mMaximumTangent * f19 * this.mMaximumTangent;
        if (f23 >= f9) {
            f9 = f23 > f24 ? f24 : 0.0f;
        }
        if (f9 != 0.0f) {
            float sqrt = (float) Math.sqrt((double) (f9 / f23));
            f10 = f18 + (sqrt * (f7 - f18));
            f11 = ((f8 - f17) * sqrt) + f17;
        } else {
            f10 = f7;
            f11 = f8;
        }
        path.cubicTo((f + f11) / 2.0f, (f2 + f10) / 2.0f, (f11 + f3) / 2.0f, (f10 + f4) / 2.0f, f3, f4);
        return path;
    }

    public void setMaximumAngle(float f) {
        this.mMaximumAngle = f;
        this.mMaximumTangent = toTangent(f);
    }

    public void setMinimumHorizontalAngle(float f) {
        this.mMinimumHorizontalAngle = f;
        this.mMinimumHorizontalTangent = toTangent(f);
    }

    public void setMinimumVerticalAngle(float f) {
        this.mMinimumVerticalAngle = f;
        this.mMinimumVerticalTangent = toTangent(f);
    }
}
