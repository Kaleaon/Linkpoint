// 
// Decompiled by Procyon v0.6.0
// 

package android.support.graphics.drawable;

import android.content.res.XmlResourceParser;
import android.content.res.Resources$NotFoundException;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.animation.AnimationUtils;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.os.Build$VERSION;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.util.Xml;
import android.view.animation.Interpolator;
import org.xmlpull.v1.XmlPullParser;
import android.content.res.Resources$Theme;
import android.content.res.Resources;
import android.content.Context;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class AnimationUtilsCompat
{
    private static Interpolator createInterpolatorFromXml(final Context context, final Resources resources, final Resources$Theme resources$Theme, final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        Object o = null;
        final int depth = xmlPullParser.getDepth();
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
            final AttributeSet attributeSet = Xml.asAttributeSet(xmlPullParser);
            final String name = xmlPullParser.getName();
            if (!name.equals("linearInterpolator")) {
                if (!name.equals("accelerateInterpolator")) {
                    if (!name.equals("decelerateInterpolator")) {
                        if (!name.equals("accelerateDecelerateInterpolator")) {
                            if (!name.equals("cycleInterpolator")) {
                                if (!name.equals("anticipateInterpolator")) {
                                    if (!name.equals("overshootInterpolator")) {
                                        if (!name.equals("anticipateOvershootInterpolator")) {
                                            if (!name.equals("bounceInterpolator")) {
                                                if (!name.equals("pathInterpolator")) {
                                                    throw new RuntimeException("Unknown interpolator name: " + xmlPullParser.getName());
                                                }
                                                o = new PathInterpolatorCompat(context, attributeSet, xmlPullParser);
                                            }
                                            else {
                                                o = new BounceInterpolator();
                                            }
                                        }
                                        else {
                                            o = new AnticipateOvershootInterpolator(context, attributeSet);
                                        }
                                    }
                                    else {
                                        o = new OvershootInterpolator(context, attributeSet);
                                    }
                                }
                                else {
                                    o = new AnticipateInterpolator(context, attributeSet);
                                }
                            }
                            else {
                                o = new CycleInterpolator(context, attributeSet);
                            }
                        }
                        else {
                            o = new AccelerateDecelerateInterpolator();
                        }
                    }
                    else {
                        o = new DecelerateInterpolator(context, attributeSet);
                    }
                }
                else {
                    o = new AccelerateInterpolator(context, attributeSet);
                }
            }
            else {
                o = new LinearInterpolator();
            }
        }
        return (Interpolator)o;
    }
    
    public static Interpolator loadInterpolator(final Context context, final int n) throws Resources$NotFoundException {
        XmlResourceParser xmlResourceParser = null;
        XmlResourceParser xmlResourceParser2 = null;
        XmlResourceParser animation = null;
        Label_0075: {
            if (Build$VERSION.SDK_INT >= 21) {
                break Label_0075;
            }
            Label_0081: {
                if (n == 17563663) {
                    break Label_0081;
                }
                Label_0103: {
                    if (n == 17563661) {
                        break Label_0103;
                    }
                    Label_0125: {
                        if (n == 17563662) {
                            break Label_0125;
                        }
                        while (true) {
                            while (true) {
                                try {
                                    return createInterpolatorFromXml(context, context.getResources(), context.getTheme(), (XmlPullParser)(xmlResourceParser2 = (xmlResourceParser = (animation = context.getResources().getAnimation(n)))));
                                    final FastOutLinearInInterpolator fastOutLinearInInterpolator = new FastOutLinearInInterpolator();
                                    iftrue(Label_0095:)(false);
                                    return (Interpolator)fastOutLinearInInterpolator;
                                    Label_0117: {
                                        throw new NullPointerException();
                                    }
                                    Label_0139:
                                    throw new NullPointerException();
                                    final LinearOutSlowInInterpolator linearOutSlowInInterpolator = new LinearOutSlowInInterpolator();
                                    iftrue(Label_0139:)(false);
                                    return (Interpolator)linearOutSlowInInterpolator;
                                    return AnimationUtils.loadInterpolator(context, n);
                                    Label_0095:
                                    throw new NullPointerException();
                                    final FastOutSlowInInterpolator fastOutSlowInInterpolator = new FastOutSlowInInterpolator();
                                    iftrue(Label_0117:)(false);
                                    return (Interpolator)fastOutSlowInInterpolator;
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
                }
            }
        }
    }
}
