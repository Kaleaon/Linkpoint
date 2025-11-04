// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.graphics.PathMeasure;
import android.support.v4.graphics.PathParser;
import android.content.res.TypedArray;
import android.support.v4.content.res.TypedArrayUtils;
import org.xmlpull.v1.XmlPullParser;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Path;

public class PatternPathMotion extends PathMotion
{
    private Path mOriginalPatternPath;
    private final Path mPatternPath;
    private final Matrix mTempMatrix;
    
    public PatternPathMotion() {
        this.mPatternPath = new Path();
        this.mTempMatrix = new Matrix();
        this.mPatternPath.lineTo(1.0f, 0.0f);
        this.mOriginalPatternPath = this.mPatternPath;
    }
    
    public PatternPathMotion(Context obtainStyledAttributes, final AttributeSet set) {
        this.mPatternPath = new Path();
        this.mTempMatrix = new Matrix();
        obtainStyledAttributes = (Context)obtainStyledAttributes.obtainStyledAttributes(set, Styleable.PATTERN_PATH_MOTION);
        try {
            final String namedString = TypedArrayUtils.getNamedString((TypedArray)obtainStyledAttributes, (XmlPullParser)set, "patternPathData", 0);
            if (namedString != null) {
                this.setPatternPath(PathParser.createPathFromPathData(namedString));
                return;
            }
            throw new RuntimeException("pathData must be supplied for patternPathMotion");
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    public PatternPathMotion(final Path patternPath) {
        this.mPatternPath = new Path();
        this.mTempMatrix = new Matrix();
        this.setPatternPath(patternPath);
    }
    
    private static float distance(final float n, final float n2) {
        return (float)Math.sqrt(n * n + n2 * n2);
    }
    
    @Override
    public Path getPath(final float n, final float n2, float n3, float n4) {
        n3 -= n;
        n4 -= n2;
        final float distance = distance(n3, n4);
        final double atan2 = Math.atan2(n4, n3);
        this.mTempMatrix.setScale(distance, distance);
        this.mTempMatrix.postRotate((float)Math.toDegrees(atan2));
        this.mTempMatrix.postTranslate(n, n2);
        final Path path = new Path();
        this.mPatternPath.transform(this.mTempMatrix, path);
        return path;
    }
    
    public Path getPatternPath() {
        return this.mOriginalPatternPath;
    }
    
    public void setPatternPath(final Path mOriginalPatternPath) {
        final PathMeasure pathMeasure = new PathMeasure(mOriginalPatternPath, false);
        final float length = pathMeasure.getLength();
        final float[] array = new float[2];
        pathMeasure.getPosTan(length, array, (float[])null);
        final float n = array[0];
        final float n2 = array[1];
        pathMeasure.getPosTan(0.0f, array, (float[])null);
        final float n3 = array[0];
        final float n4 = array[1];
        if (n3 == n && n4 == n2) {
            throw new IllegalArgumentException("pattern must not end at the starting point");
        }
        this.mTempMatrix.setTranslate(-n3, -n4);
        final float n5 = n - n3;
        final float n6 = n2 - n4;
        final float n7 = 1.0f / distance(n5, n6);
        this.mTempMatrix.postScale(n7, n7);
        this.mTempMatrix.postRotate((float)Math.toDegrees(-Math.atan2(n6, n5)));
        mOriginalPatternPath.transform(this.mTempMatrix, this.mPatternPath);
        this.mOriginalPatternPath = mOriginalPatternPath;
    }
}
