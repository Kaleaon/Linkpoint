package android.support.transition;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.support.annotation.NonNull;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import android.view.ViewGroup;
import java.io.IOException;
import java.lang.reflect.Constructor;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class TransitionInflater {
    private static final ArrayMap<String, Constructor> CONSTRUCTORS = new ArrayMap<>();
    private static final Class<?>[] CONSTRUCTOR_SIGNATURE = {Context.class, AttributeSet.class};
    private final Context mContext;

    private TransitionInflater(@NonNull Context context) {
        this.mContext = context;
    }

    private Object createCustom(AttributeSet attributeSet, Class cls, String str) {
        Object newInstance;
        String attributeValue = attributeSet.getAttributeValue((String) null, "class");
        if (attributeValue != null) {
            try {
                synchronized (CONSTRUCTORS) {
                    Constructor<? extends U> constructor = CONSTRUCTORS.get(attributeValue);
                    if (constructor == null) {
                        Class<? extends U> asSubclass = this.mContext.getClassLoader().loadClass(attributeValue).asSubclass(cls);
                        if (asSubclass != null) {
                            constructor = asSubclass.getConstructor(CONSTRUCTOR_SIGNATURE);
                            constructor.setAccessible(true);
                            CONSTRUCTORS.put(attributeValue, constructor);
                        }
                    }
                    newInstance = constructor.newInstance(new Object[]{this.mContext, attributeSet});
                }
                return newInstance;
            } catch (Exception e) {
                throw new InflateException("Could not instantiate " + cls + " class " + attributeValue, e);
            }
        } else {
            throw new InflateException(str + " tag must have a 'class' attribute");
        }
    }

    private Transition createTransitionFromXml(XmlPullParser xmlPullParser, AttributeSet attributeSet, Transition transition) throws XmlPullParserException, IOException {
        Transition transition2;
        int depth = xmlPullParser.getDepth();
        TransitionSet transitionSet = !(transition instanceof TransitionSet) ? null : (TransitionSet) transition;
        Transition transition3 = null;
        while (true) {
            int next = xmlPullParser.next();
            if ((next == 3 && xmlPullParser.getDepth() <= depth) || next == 1) {
                return transition3;
            }
            if (next == 2) {
                String name = xmlPullParser.getName();
                if ("fade".equals(name)) {
                    transition2 = new Fade(this.mContext, attributeSet);
                } else if ("changeBounds".equals(name)) {
                    transition2 = new ChangeBounds(this.mContext, attributeSet);
                } else if ("slide".equals(name)) {
                    transition2 = new Slide(this.mContext, attributeSet);
                } else if ("explode".equals(name)) {
                    transition2 = new Explode(this.mContext, attributeSet);
                } else if ("changeImageTransform".equals(name)) {
                    transition2 = new ChangeImageTransform(this.mContext, attributeSet);
                } else if ("changeTransform".equals(name)) {
                    transition2 = new ChangeTransform(this.mContext, attributeSet);
                } else if ("changeClipBounds".equals(name)) {
                    transition2 = new ChangeClipBounds(this.mContext, attributeSet);
                } else if ("autoTransition".equals(name)) {
                    transition2 = new AutoTransition(this.mContext, attributeSet);
                } else if ("changeScroll".equals(name)) {
                    transition2 = new ChangeScroll(this.mContext, attributeSet);
                } else if ("transitionSet".equals(name)) {
                    transition2 = new TransitionSet(this.mContext, attributeSet);
                } else if ("transition".equals(name)) {
                    transition2 = (Transition) createCustom(attributeSet, Transition.class, "transition");
                } else if ("targets".equals(name)) {
                    getTargetIds(xmlPullParser, attributeSet, transition);
                    transition2 = transition3;
                } else if (!"arcMotion".equals(name)) {
                    if (!"pathMotion".equals(name)) {
                        if (!"patternPathMotion".equals(name)) {
                            throw new RuntimeException("Unknown scene name: " + xmlPullParser.getName());
                        } else if (transition != null) {
                            transition.setPathMotion(new PatternPathMotion(this.mContext, attributeSet));
                            transition2 = transition3;
                        } else {
                            throw new RuntimeException("Invalid use of patternPathMotion element");
                        }
                    } else if (transition != null) {
                        transition.setPathMotion((PathMotion) createCustom(attributeSet, PathMotion.class, "pathMotion"));
                        transition2 = transition3;
                    } else {
                        throw new RuntimeException("Invalid use of pathMotion element");
                    }
                } else if (transition != null) {
                    transition.setPathMotion(new ArcMotion(this.mContext, attributeSet));
                    transition2 = transition3;
                } else {
                    throw new RuntimeException("Invalid use of arcMotion element");
                }
                if (transition2 != null) {
                    if (!xmlPullParser.isEmptyElementTag()) {
                        createTransitionFromXml(xmlPullParser, attributeSet, transition2);
                    }
                    if (transitionSet != null) {
                        transitionSet.addTransition(transition2);
                        transition2 = null;
                    } else if (transition != null) {
                        throw new InflateException("Could not add transition to another transition.");
                    }
                }
                transition3 = transition2;
            }
        }
        return transition3;
    }

    private TransitionManager createTransitionManagerFromXml(XmlPullParser xmlPullParser, AttributeSet attributeSet, ViewGroup viewGroup) throws XmlPullParserException, IOException {
        TransitionManager transitionManager = null;
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if ((next == 3 && xmlPullParser.getDepth() <= depth) || next == 1) {
                return transitionManager;
            }
            if (next == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("transitionManager")) {
                    transitionManager = new TransitionManager();
                } else if (name.equals("transition") && transitionManager != null) {
                    loadTransition(attributeSet, xmlPullParser, viewGroup, transitionManager);
                }
            }
        }
        throw new RuntimeException("Unknown scene name: " + xmlPullParser.getName());
    }

    public static TransitionInflater from(Context context) {
        return new TransitionInflater(context);
    }

    private void getTargetIds(XmlPullParser xmlPullParser, AttributeSet attributeSet, Transition transition) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if ((next != 3 || xmlPullParser.getDepth() > depth) && next != 1) {
                if (next == 2) {
                    if (!xmlPullParser.getName().equals("target")) {
                        throw new RuntimeException("Unknown scene name: " + xmlPullParser.getName());
                    }
                    TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(attributeSet, Styleable.TRANSITION_TARGET);
                    int namedResourceId = TypedArrayUtils.getNamedResourceId(obtainStyledAttributes, xmlPullParser, "targetId", 1, 0);
                    if (namedResourceId == 0) {
                        int namedResourceId2 = TypedArrayUtils.getNamedResourceId(obtainStyledAttributes, xmlPullParser, "excludeId", 2, 0);
                        if (namedResourceId2 == 0) {
                            String namedString = TypedArrayUtils.getNamedString(obtainStyledAttributes, xmlPullParser, "targetName", 4);
                            if (namedString == null) {
                                String namedString2 = TypedArrayUtils.getNamedString(obtainStyledAttributes, xmlPullParser, "excludeName", 5);
                                if (namedString2 == null) {
                                    String namedString3 = TypedArrayUtils.getNamedString(obtainStyledAttributes, xmlPullParser, "excludeClass", 3);
                                    if (namedString3 == null) {
                                        try {
                                            String namedString4 = TypedArrayUtils.getNamedString(obtainStyledAttributes, xmlPullParser, "targetClass", 0);
                                            if (namedString4 != null) {
                                                transition.addTarget((Class) Class.forName(namedString4));
                                            }
                                        } catch (ClassNotFoundException e) {
                                            obtainStyledAttributes.recycle();
                                            throw new RuntimeException("Could not create " + namedString3, e);
                                        }
                                    } else {
                                        transition.excludeTarget((Class) Class.forName(namedString3), true);
                                    }
                                } else {
                                    transition.excludeTarget(namedString2, true);
                                }
                            } else {
                                transition.addTarget(namedString);
                            }
                        } else {
                            transition.excludeTarget(namedResourceId2, true);
                        }
                    } else {
                        transition.addTarget(namedResourceId);
                    }
                    obtainStyledAttributes.recycle();
                }
            } else {
                return;
            }
        }
    }

    private void loadTransition(AttributeSet attributeSet, XmlPullParser xmlPullParser, ViewGroup viewGroup, TransitionManager transitionManager) throws Resources.NotFoundException {
        Transition inflateTransition;
        Scene scene = null;
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(attributeSet, Styleable.TRANSITION_MANAGER);
        int namedResourceId = TypedArrayUtils.getNamedResourceId(obtainStyledAttributes, xmlPullParser, "transition", 2, -1);
        int namedResourceId2 = TypedArrayUtils.getNamedResourceId(obtainStyledAttributes, xmlPullParser, "fromScene", 0, -1);
        Scene sceneForLayout = namedResourceId2 >= 0 ? Scene.getSceneForLayout(viewGroup, namedResourceId2, this.mContext) : null;
        int namedResourceId3 = TypedArrayUtils.getNamedResourceId(obtainStyledAttributes, xmlPullParser, "toScene", 1, -1);
        if (namedResourceId3 >= 0) {
            scene = Scene.getSceneForLayout(viewGroup, namedResourceId3, this.mContext);
        }
        if (namedResourceId >= 0 && (inflateTransition = inflateTransition(namedResourceId)) != null) {
            if (scene == null) {
                throw new RuntimeException("No toScene for transition ID " + namedResourceId);
            } else if (sceneForLayout != null) {
                transitionManager.setTransition(sceneForLayout, scene, inflateTransition);
            } else {
                transitionManager.setTransition(scene, inflateTransition);
            }
        }
        obtainStyledAttributes.recycle();
    }

    public Transition inflateTransition(int i) {
        XmlResourceParser xml = this.mContext.getResources().getXml(i);
        try {
            Transition createTransitionFromXml = createTransitionFromXml(xml, Xml.asAttributeSet(xml), (Transition) null);
            xml.close();
            return createTransitionFromXml;
        } catch (XmlPullParserException e) {
            throw new InflateException(e.getMessage(), e);
        } catch (IOException e2) {
            throw new InflateException(xml.getPositionDescription() + ": " + e2.getMessage(), e2);
        } catch (Throwable th) {
            xml.close();
            throw th;
        }
    }

    public TransitionManager inflateTransitionManager(int i, ViewGroup viewGroup) {
        XmlResourceParser xml = this.mContext.getResources().getXml(i);
        try {
            TransitionManager createTransitionManagerFromXml = createTransitionManagerFromXml(xml, Xml.asAttributeSet(xml), viewGroup);
            xml.close();
            return createTransitionManagerFromXml;
        } catch (XmlPullParserException e) {
            InflateException inflateException = new InflateException(e.getMessage());
            inflateException.initCause(e);
            throw inflateException;
        } catch (IOException e2) {
            InflateException inflateException2 = new InflateException(xml.getPositionDescription() + ": " + e2.getMessage());
            inflateException2.initCause(e2);
            throw inflateException2;
        } catch (Throwable th) {
            xml.close();
            throw th;
        }
    }
}
