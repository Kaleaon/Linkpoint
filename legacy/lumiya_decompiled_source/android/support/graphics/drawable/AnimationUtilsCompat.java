package android.support.graphics.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.support.annotation.RestrictTo;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class AnimationUtilsCompat {
    /* JADX WARN: Code restructure failed: missing block: B:7:0x000f, code lost:
        return r0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static android.view.animation.Interpolator createInterpolatorFromXml(android.content.Context r4, android.content.res.Resources r5, android.content.res.Resources.Theme r6, org.xmlpull.v1.XmlPullParser r7) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r0 = 0
            int r1 = r7.getDepth()
        L5:
            int r2 = r7.next()
            r3 = 3
            if (r2 == r3) goto L10
        Lc:
            r3 = 1
            if (r2 != r3) goto L17
        Lf:
            return r0
        L10:
            int r3 = r7.getDepth()
            if (r3 > r1) goto Lc
            goto Lf
        L17:
            r3 = 2
            if (r2 != r3) goto L5
            android.util.AttributeSet r2 = android.util.Xml.asAttributeSet(r7)
            java.lang.String r0 = r7.getName()
            java.lang.String r3 = "linearInterpolator"
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L9a
            java.lang.String r3 = "accelerateInterpolator"
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto La1
            java.lang.String r3 = "decelerateInterpolator"
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto La8
            java.lang.String r3 = "accelerateDecelerateInterpolator"
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto Laf
            java.lang.String r3 = "cycleInterpolator"
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto Lb6
            java.lang.String r3 = "anticipateInterpolator"
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto Lbd
            java.lang.String r3 = "overshootInterpolator"
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto Lc4
            java.lang.String r3 = "anticipateOvershootInterpolator"
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto Lcb
            java.lang.String r3 = "bounceInterpolator"
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto Ld2
            java.lang.String r3 = "pathInterpolator"
            boolean r0 = r0.equals(r3)
            if (r0 != 0) goto Ld9
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Unknown interpolator name: "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = r7.getName()
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L9a:
            android.view.animation.LinearInterpolator r0 = new android.view.animation.LinearInterpolator
            r0.<init>()
            goto L5
        La1:
            android.view.animation.AccelerateInterpolator r0 = new android.view.animation.AccelerateInterpolator
            r0.<init>(r4, r2)
            goto L5
        La8:
            android.view.animation.DecelerateInterpolator r0 = new android.view.animation.DecelerateInterpolator
            r0.<init>(r4, r2)
            goto L5
        Laf:
            android.view.animation.AccelerateDecelerateInterpolator r0 = new android.view.animation.AccelerateDecelerateInterpolator
            r0.<init>()
            goto L5
        Lb6:
            android.view.animation.CycleInterpolator r0 = new android.view.animation.CycleInterpolator
            r0.<init>(r4, r2)
            goto L5
        Lbd:
            android.view.animation.AnticipateInterpolator r0 = new android.view.animation.AnticipateInterpolator
            r0.<init>(r4, r2)
            goto L5
        Lc4:
            android.view.animation.OvershootInterpolator r0 = new android.view.animation.OvershootInterpolator
            r0.<init>(r4, r2)
            goto L5
        Lcb:
            android.view.animation.AnticipateOvershootInterpolator r0 = new android.view.animation.AnticipateOvershootInterpolator
            r0.<init>(r4, r2)
            goto L5
        Ld2:
            android.view.animation.BounceInterpolator r0 = new android.view.animation.BounceInterpolator
            r0.<init>()
            goto L5
        Ld9:
            android.support.graphics.drawable.PathInterpolatorCompat r0 = new android.support.graphics.drawable.PathInterpolatorCompat
            r0.<init>(r4, r2, r7)
            goto L5
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.graphics.drawable.AnimationUtilsCompat.createInterpolatorFromXml(android.content.Context, android.content.res.Resources, android.content.res.Resources$Theme, org.xmlpull.v1.XmlPullParser):android.view.animation.Interpolator");
    }

    public static Interpolator loadInterpolator(Context context, int i) throws Resources.NotFoundException {
        XmlResourceParser xmlResourceParser = null;
        if (Build.VERSION.SDK_INT < 21) {
            try {
                try {
                    if (i != 17563663) {
                        if (i == 17563661) {
                            FastOutSlowInInterpolator fastOutSlowInInterpolator = new FastOutSlowInInterpolator();
                            if (0 != 0) {
                                xmlResourceParser.close();
                            }
                            return fastOutSlowInInterpolator;
                        } else if (i == 17563662) {
                            LinearOutSlowInInterpolator linearOutSlowInInterpolator = new LinearOutSlowInInterpolator();
                            if (0 != 0) {
                                xmlResourceParser.close();
                            }
                            return linearOutSlowInInterpolator;
                        } else {
                            XmlResourceParser animation = context.getResources().getAnimation(i);
                            Interpolator createInterpolatorFromXml = createInterpolatorFromXml(context, context.getResources(), context.getTheme(), animation);
                            if (animation != null) {
                                animation.close();
                            }
                            return createInterpolatorFromXml;
                        }
                    }
                    return new FastOutLinearInInterpolator();
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
                if (0 != 0) {
                    xmlResourceParser.close();
                }
            }
        }
        return AnimationUtils.loadInterpolator(context, i);
    }
}
