package android.support.graphics.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.support.annotation.RestrictTo;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class AnimationUtilsCompat {
    private static Interpolator createInterpolatorFromXml(Context context, Resources resources, Resources.Theme theme, XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        Interpolator interpolator = null;
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if ((next == 3 && xmlPullParser.getDepth() <= depth) || next == 1) {
                return interpolator;
            }
            if (next == 2) {
                AttributeSet asAttributeSet = Xml.asAttributeSet(xmlPullParser);
                String name = xmlPullParser.getName();
                if (name.equals("linearInterpolator")) {
                    interpolator = new LinearInterpolator();
                } else if (name.equals("accelerateInterpolator")) {
                    interpolator = new AccelerateInterpolator(context, asAttributeSet);
                } else if (name.equals("decelerateInterpolator")) {
                    interpolator = new DecelerateInterpolator(context, asAttributeSet);
                } else if (name.equals("accelerateDecelerateInterpolator")) {
                    interpolator = new AccelerateDecelerateInterpolator();
                } else if (name.equals("cycleInterpolator")) {
                    interpolator = new CycleInterpolator(context, asAttributeSet);
                } else if (name.equals("anticipateInterpolator")) {
                    interpolator = new AnticipateInterpolator(context, asAttributeSet);
                } else if (name.equals("overshootInterpolator")) {
                    interpolator = new OvershootInterpolator(context, asAttributeSet);
                } else if (name.equals("anticipateOvershootInterpolator")) {
                    interpolator = new AnticipateOvershootInterpolator(context, asAttributeSet);
                } else if (name.equals("bounceInterpolator")) {
                    interpolator = new BounceInterpolator();
                } else if (!name.equals("pathInterpolator")) {
                    throw new RuntimeException("Unknown interpolator name: " + xmlPullParser.getName());
                } else {
                    interpolator = new PathInterpolatorCompat(context, asAttributeSet, xmlPullParser);
                }
            }
        }
        return interpolator;
    }

    public static Interpolator loadInterpolator(Context context, int i) throws Resources.NotFoundException {
        XmlResourceParser xmlResourceParser = null;
        if (Build.VERSION.SDK_INT >= 21) {
            return AnimationUtils.loadInterpolator(context, i);
        }
        if (i == 17563663) {
            FastOutLinearInInterpolator fastOutLinearInInterpolator = new FastOutLinearInInterpolator();
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
            return fastOutLinearInInterpolator;
        } else if (i == 17563661) {
            FastOutSlowInInterpolator fastOutSlowInInterpolator = new FastOutSlowInInterpolator();
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
            return fastOutSlowInInterpolator;
        } else if (i != 17563662) {
            try {
                xmlResourceParser = context.getResources().getAnimation(i);
                Interpolator createInterpolatorFromXml = createInterpolatorFromXml(context, context.getResources(), context.getTheme(), xmlResourceParser);
                if (xmlResourceParser != null) {
                    xmlResourceParser.close();
                }
                return createInterpolatorFromXml;
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
        } else {
            LinearOutSlowInInterpolator linearOutSlowInInterpolator = new LinearOutSlowInInterpolator();
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
            return linearOutSlowInInterpolator;
        }
    }
}
