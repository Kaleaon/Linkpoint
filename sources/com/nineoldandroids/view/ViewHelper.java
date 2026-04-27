package com.nineoldandroids.view;

import android.view.View;
import com.nineoldandroids.view.animation.AnimatorProxy;

public final class ViewHelper {

    private static final class Honeycomb {
        private Honeycomb() {
        }

        static float getAlpha(View view) {
            return view.getAlpha();
        }

        static float getPivotX(View view) {
            return view.getPivotX();
        }

        static float getPivotY(View view) {
            return view.getPivotY();
        }

        static float getRotation(View view) {
            return view.getRotation();
        }

        static float getRotationX(View view) {
            return view.getRotationX();
        }

        static float getRotationY(View view) {
            return view.getRotationY();
        }

        static float getScaleX(View view) {
            return view.getScaleX();
        }

        static float getScaleY(View view) {
            return view.getScaleY();
        }

        static float getScrollX(View view) {
            return (float) view.getScrollX();
        }

        static float getScrollY(View view) {
            return (float) view.getScrollY();
        }

        static float getTranslationX(View view) {
            return view.getTranslationX();
        }

        static float getTranslationY(View view) {
            return view.getTranslationY();
        }

        static float getX(View view) {
            return view.getX();
        }

        static float getY(View view) {
            return view.getY();
        }

        static void setAlpha(View view, float f) {
            view.setAlpha(f);
        }

        static void setPivotX(View view, float f) {
            view.setPivotX(f);
        }

        static void setPivotY(View view, float f) {
            view.setPivotY(f);
        }

        static void setRotation(View view, float f) {
            view.setRotation(f);
        }

        static void setRotationX(View view, float f) {
            view.setRotationX(f);
        }

        static void setRotationY(View view, float f) {
            view.setRotationY(f);
        }

        static void setScaleX(View view, float f) {
            view.setScaleX(f);
        }

        static void setScaleY(View view, float f) {
            view.setScaleY(f);
        }

        static void setScrollX(View view, int i) {
            view.setScrollX(i);
        }

        static void setScrollY(View view, int i) {
            view.setScrollY(i);
        }

        static void setTranslationX(View view, float f) {
            view.setTranslationX(f);
        }

        static void setTranslationY(View view, float f) {
            view.setTranslationY(f);
        }

        static void setX(View view, float f) {
            view.setX(f);
        }

        static void setY(View view, float f) {
            view.setY(f);
        }
    }

    private ViewHelper() {
    }

    public static float getAlpha(View view) {
        return !AnimatorProxy.NEEDS_PROXY ? Honeycomb.getAlpha(view) : AnimatorProxy.wrap(view).getAlpha();
    }

    public static float getPivotX(View view) {
        return !AnimatorProxy.NEEDS_PROXY ? Honeycomb.getPivotX(view) : AnimatorProxy.wrap(view).getPivotX();
    }

    public static float getPivotY(View view) {
        return !AnimatorProxy.NEEDS_PROXY ? Honeycomb.getPivotY(view) : AnimatorProxy.wrap(view).getPivotY();
    }

    public static float getRotation(View view) {
        return !AnimatorProxy.NEEDS_PROXY ? Honeycomb.getRotation(view) : AnimatorProxy.wrap(view).getRotation();
    }

    public static float getRotationX(View view) {
        return !AnimatorProxy.NEEDS_PROXY ? Honeycomb.getRotationX(view) : AnimatorProxy.wrap(view).getRotationX();
    }

    public static float getRotationY(View view) {
        return !AnimatorProxy.NEEDS_PROXY ? Honeycomb.getRotationY(view) : AnimatorProxy.wrap(view).getRotationY();
    }

    public static float getScaleX(View view) {
        return !AnimatorProxy.NEEDS_PROXY ? Honeycomb.getScaleX(view) : AnimatorProxy.wrap(view).getScaleX();
    }

    public static float getScaleY(View view) {
        return !AnimatorProxy.NEEDS_PROXY ? Honeycomb.getScaleY(view) : AnimatorProxy.wrap(view).getScaleY();
    }

    public static float getScrollX(View view) {
        return !AnimatorProxy.NEEDS_PROXY ? Honeycomb.getScrollX(view) : (float) AnimatorProxy.wrap(view).getScrollX();
    }

    public static float getScrollY(View view) {
        return !AnimatorProxy.NEEDS_PROXY ? Honeycomb.getScrollY(view) : (float) AnimatorProxy.wrap(view).getScrollY();
    }

    public static float getTranslationX(View view) {
        return !AnimatorProxy.NEEDS_PROXY ? Honeycomb.getTranslationX(view) : AnimatorProxy.wrap(view).getTranslationX();
    }

    public static float getTranslationY(View view) {
        return !AnimatorProxy.NEEDS_PROXY ? Honeycomb.getTranslationY(view) : AnimatorProxy.wrap(view).getTranslationY();
    }

    public static float getX(View view) {
        return !AnimatorProxy.NEEDS_PROXY ? Honeycomb.getX(view) : AnimatorProxy.wrap(view).getX();
    }

    public static float getY(View view) {
        return !AnimatorProxy.NEEDS_PROXY ? Honeycomb.getY(view) : AnimatorProxy.wrap(view).getY();
    }

    public static void setAlpha(View view, float f) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setAlpha(view, f);
        } else {
            AnimatorProxy.wrap(view).setAlpha(f);
        }
    }

    public static void setPivotX(View view, float f) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setPivotX(view, f);
        } else {
            AnimatorProxy.wrap(view).setPivotX(f);
        }
    }

    public static void setPivotY(View view, float f) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setPivotY(view, f);
        } else {
            AnimatorProxy.wrap(view).setPivotY(f);
        }
    }

    public static void setRotation(View view, float f) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setRotation(view, f);
        } else {
            AnimatorProxy.wrap(view).setRotation(f);
        }
    }

    public static void setRotationX(View view, float f) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setRotationX(view, f);
        } else {
            AnimatorProxy.wrap(view).setRotationX(f);
        }
    }

    public static void setRotationY(View view, float f) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setRotationY(view, f);
        } else {
            AnimatorProxy.wrap(view).setRotationY(f);
        }
    }

    public static void setScaleX(View view, float f) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setScaleX(view, f);
        } else {
            AnimatorProxy.wrap(view).setScaleX(f);
        }
    }

    public static void setScaleY(View view, float f) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setScaleY(view, f);
        } else {
            AnimatorProxy.wrap(view).setScaleY(f);
        }
    }

    public static void setScrollX(View view, int i) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setScrollX(view, i);
        } else {
            AnimatorProxy.wrap(view).setScrollX(i);
        }
    }

    public static void setScrollY(View view, int i) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setScrollY(view, i);
        } else {
            AnimatorProxy.wrap(view).setScrollY(i);
        }
    }

    public static void setTranslationX(View view, float f) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setTranslationX(view, f);
        } else {
            AnimatorProxy.wrap(view).setTranslationX(f);
        }
    }

    public static void setTranslationY(View view, float f) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setTranslationY(view, f);
        } else {
            AnimatorProxy.wrap(view).setTranslationY(f);
        }
    }

    public static void setX(View view, float f) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setX(view, f);
        } else {
            AnimatorProxy.wrap(view).setX(f);
        }
    }

    public static void setY(View view, float f) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setY(view, f);
        } else {
            AnimatorProxy.wrap(view).setY(f);
        }
    }
}
