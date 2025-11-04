// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.content.res.XmlResourceParser;
import android.util.Xml;
import android.content.res.Resources$NotFoundException;
import android.content.res.TypedArray;
import android.support.v4.content.res.TypedArrayUtils;
import android.view.ViewGroup;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParser;
import android.view.InflateException;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.content.Context;
import java.lang.reflect.Constructor;
import android.support.v4.util.ArrayMap;

public class TransitionInflater
{
    private static final ArrayMap<String, Constructor> CONSTRUCTORS;
    private static final Class<?>[] CONSTRUCTOR_SIGNATURE;
    private final Context mContext;
    
    static {
        CONSTRUCTOR_SIGNATURE = new Class[] { Context.class, AttributeSet.class };
        CONSTRUCTORS = new ArrayMap<String, Constructor>();
    }
    
    private TransitionInflater(@NonNull final Context mContext) {
        this.mContext = mContext;
    }
    
    private Object createCustom(final AttributeSet set, final Class clazz, final String str) {
        final String attributeValue = set.getAttributeValue((String)null, "class");
        Label_0065: {
            if (attributeValue == null) {
                break Label_0065;
            }
            try {
                synchronized (TransitionInflater.CONSTRUCTORS) {
                    Constructor constructor = TransitionInflater.CONSTRUCTORS.get(attributeValue);
                    if (constructor == null) {
                        final Class<?> subclass = this.mContext.getClassLoader().loadClass(attributeValue).asSubclass((Class<Object>)clazz);
                        if (subclass != null) {
                            constructor = subclass.getConstructor(TransitionInflater.CONSTRUCTOR_SIGNATURE);
                            constructor.setAccessible(true);
                            TransitionInflater.CONSTRUCTORS.put(attributeValue, constructor);
                        }
                    }
                    return constructor.newInstance(this.mContext, set);
                    throw new InflateException(str + " tag must have a 'class' attribute");
                }
            }
            catch (final Exception ex) {
                throw new InflateException("Could not instantiate " + clazz + " class " + attributeValue, (Throwable)ex);
            }
        }
    }
    
    private Transition createTransitionFromXml(final XmlPullParser xmlPullParser, final AttributeSet set, final Transition transition) throws XmlPullParserException, IOException {
        final int depth = xmlPullParser.getDepth();
        TransitionSet set2;
        if (!(transition instanceof TransitionSet)) {
            set2 = null;
        }
        else {
            set2 = (TransitionSet)transition;
        }
        Transition transition2 = null;
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
            if (!"fade".equals(name)) {
                if (!"changeBounds".equals(name)) {
                    if (!"slide".equals(name)) {
                        if (!"explode".equals(name)) {
                            if (!"changeImageTransform".equals(name)) {
                                if (!"changeTransform".equals(name)) {
                                    if (!"changeClipBounds".equals(name)) {
                                        if (!"autoTransition".equals(name)) {
                                            if (!"changeScroll".equals(name)) {
                                                if (!"transitionSet".equals(name)) {
                                                    if (!"transition".equals(name)) {
                                                        if (!"targets".equals(name)) {
                                                            if (!"arcMotion".equals(name)) {
                                                                if (!"pathMotion".equals(name)) {
                                                                    if (!"patternPathMotion".equals(name)) {
                                                                        throw new RuntimeException("Unknown scene name: " + xmlPullParser.getName());
                                                                    }
                                                                    if (transition == null) {
                                                                        throw new RuntimeException("Invalid use of patternPathMotion element");
                                                                    }
                                                                    transition.setPathMotion(new PatternPathMotion(this.mContext, set));
                                                                }
                                                                else {
                                                                    if (transition == null) {
                                                                        throw new RuntimeException("Invalid use of pathMotion element");
                                                                    }
                                                                    transition.setPathMotion((PathMotion)this.createCustom(set, PathMotion.class, "pathMotion"));
                                                                }
                                                            }
                                                            else {
                                                                if (transition == null) {
                                                                    throw new RuntimeException("Invalid use of arcMotion element");
                                                                }
                                                                transition.setPathMotion(new ArcMotion(this.mContext, set));
                                                            }
                                                        }
                                                        else {
                                                            this.getTargetIds(xmlPullParser, set, transition);
                                                        }
                                                    }
                                                    else {
                                                        transition2 = (Transition)this.createCustom(set, Transition.class, "transition");
                                                    }
                                                }
                                                else {
                                                    transition2 = new TransitionSet(this.mContext, set);
                                                }
                                            }
                                            else {
                                                transition2 = new ChangeScroll(this.mContext, set);
                                            }
                                        }
                                        else {
                                            transition2 = new AutoTransition(this.mContext, set);
                                        }
                                    }
                                    else {
                                        transition2 = new ChangeClipBounds(this.mContext, set);
                                    }
                                }
                                else {
                                    transition2 = new ChangeTransform(this.mContext, set);
                                }
                            }
                            else {
                                transition2 = new ChangeImageTransform(this.mContext, set);
                            }
                        }
                        else {
                            transition2 = new Explode(this.mContext, set);
                        }
                    }
                    else {
                        transition2 = new Slide(this.mContext, set);
                    }
                }
                else {
                    transition2 = new ChangeBounds(this.mContext, set);
                }
            }
            else {
                transition2 = new Fade(this.mContext, set);
            }
            if (transition2 == null) {
                continue;
            }
            if (!xmlPullParser.isEmptyElementTag()) {
                this.createTransitionFromXml(xmlPullParser, set, transition2);
            }
            if (set2 == null) {
                if (transition != null) {
                    throw new InflateException("Could not add transition to another transition.");
                }
                continue;
            }
            else {
                set2.addTransition(transition2);
                transition2 = null;
            }
        }
        return transition2;
    }
    
    private TransitionManager createTransitionManagerFromXml(final XmlPullParser xmlPullParser, final AttributeSet set, final ViewGroup viewGroup) throws XmlPullParserException, IOException {
        TransitionManager transitionManager = null;
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
            final String name = xmlPullParser.getName();
            if (!name.equals("transitionManager")) {
                if (!name.equals("transition") || transitionManager == null) {
                    throw new RuntimeException("Unknown scene name: " + xmlPullParser.getName());
                }
                this.loadTransition(set, xmlPullParser, viewGroup, transitionManager);
            }
            else {
                transitionManager = new TransitionManager();
            }
        }
        return transitionManager;
    }
    
    public static TransitionInflater from(final Context context) {
        return new TransitionInflater(context);
    }
    
    private void getTargetIds(final XmlPullParser xmlPullParser, final AttributeSet set, final Transition transition) throws XmlPullParserException, IOException {
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
            if (!xmlPullParser.getName().equals("target")) {
                throw new RuntimeException("Unknown scene name: " + xmlPullParser.getName());
            }
            final TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(set, Styleable.TRANSITION_TARGET);
            final int namedResourceId = TypedArrayUtils.getNamedResourceId(obtainStyledAttributes, xmlPullParser, "targetId", 1, 0);
            Label_0225: {
                if (namedResourceId != 0) {
                    break Label_0225;
                }
                final int namedResourceId2 = TypedArrayUtils.getNamedResourceId(obtainStyledAttributes, xmlPullParser, "excludeId", 2, 0);
                Label_0235: {
                    if (namedResourceId2 != 0) {
                        break Label_0235;
                    }
                    String str = TypedArrayUtils.getNamedString(obtainStyledAttributes, xmlPullParser, "targetName", 4);
                    Label_0246: {
                        if (str != null) {
                            break Label_0246;
                        }
                        str = TypedArrayUtils.getNamedString(obtainStyledAttributes, xmlPullParser, "excludeName", 5);
                        Label_0256: {
                            if (str != null) {
                                break Label_0256;
                            }
                            final String namedString = TypedArrayUtils.getNamedString(obtainStyledAttributes, xmlPullParser, "excludeClass", 3);
                            Label_0267: {
                                if (namedString != null) {
                                    break Label_0267;
                                }
                                while (true) {
                                    str = namedString;
                                    while (true) {
                                        String namedString2 = null;
                                        Label_0321: {
                                            try {
                                                namedString2 = TypedArrayUtils.getNamedString(obtainStyledAttributes, xmlPullParser, "targetClass", 0);
                                                if (namedString2 != null) {
                                                    break Label_0321;
                                                }
                                                obtainStyledAttributes.recycle();
                                                break;
                                                transition.excludeTarget(str, true);
                                                continue;
                                                transition.addTarget(str);
                                                continue;
                                                str = namedString;
                                                transition.excludeTarget(Class.forName(namedString), true);
                                                continue;
                                                transition.addTarget(namedResourceId);
                                                continue;
                                                transition.excludeTarget(namedResourceId2, true);
                                                continue;
                                            }
                                            catch (final ClassNotFoundException cause) {
                                                obtainStyledAttributes.recycle();
                                                throw new RuntimeException("Could not create " + str, cause);
                                            }
                                        }
                                        str = namedString2;
                                        transition.addTarget(Class.forName(namedString2));
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void loadTransition(final AttributeSet set, final XmlPullParser xmlPullParser, final ViewGroup viewGroup, final TransitionManager transitionManager) throws Resources$NotFoundException {
        final Scene scene = null;
        final TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(set, Styleable.TRANSITION_MANAGER);
        final int namedResourceId = TypedArrayUtils.getNamedResourceId(obtainStyledAttributes, xmlPullParser, "transition", 2, -1);
        final int namedResourceId2 = TypedArrayUtils.getNamedResourceId(obtainStyledAttributes, xmlPullParser, "fromScene", 0, -1);
        Scene sceneForLayout;
        if (namedResourceId2 >= 0) {
            sceneForLayout = Scene.getSceneForLayout(viewGroup, namedResourceId2, this.mContext);
        }
        else {
            sceneForLayout = null;
        }
        final int namedResourceId3 = TypedArrayUtils.getNamedResourceId(obtainStyledAttributes, xmlPullParser, "toScene", 1, -1);
        Scene sceneForLayout2 = scene;
        if (namedResourceId3 >= 0) {
            sceneForLayout2 = Scene.getSceneForLayout(viewGroup, namedResourceId3, this.mContext);
        }
        if (namedResourceId >= 0) {
            final Transition inflateTransition = this.inflateTransition(namedResourceId);
            if (inflateTransition != null) {
                if (sceneForLayout2 == null) {
                    throw new RuntimeException("No toScene for transition ID " + namedResourceId);
                }
                if (sceneForLayout != null) {
                    transitionManager.setTransition(sceneForLayout, sceneForLayout2, inflateTransition);
                }
                else {
                    transitionManager.setTransition(sceneForLayout2, inflateTransition);
                }
            }
        }
        obtainStyledAttributes.recycle();
    }
    
    public Transition inflateTransition(final int n) {
        final XmlResourceParser xml = this.mContext.getResources().getXml(n);
        try {
            return this.createTransitionFromXml((XmlPullParser)xml, Xml.asAttributeSet((XmlPullParser)xml), null);
        }
        catch (final XmlPullParserException ex) {
            throw new InflateException(ex.getMessage(), (Throwable)ex);
        }
        catch (final IOException ex2) {
            throw new InflateException(xml.getPositionDescription() + ": " + ex2.getMessage(), (Throwable)ex2);
        }
        finally {
            xml.close();
        }
    }
    
    public TransitionManager inflateTransitionManager(final int n, final ViewGroup viewGroup) {
        final XmlResourceParser xml = this.mContext.getResources().getXml(n);
        try {
            return this.createTransitionManagerFromXml((XmlPullParser)xml, Xml.asAttributeSet((XmlPullParser)xml), viewGroup);
        }
        catch (final XmlPullParserException ex) {
            final InflateException ex2 = new InflateException(ex.getMessage());
            ex2.initCause((Throwable)ex);
            throw ex2;
        }
        catch (final IOException ex3) {
            final InflateException ex4 = new InflateException(xml.getPositionDescription() + ": " + ex3.getMessage());
            ex4.initCause((Throwable)ex3);
            throw ex4;
        }
        finally {
            xml.close();
        }
    }
}
