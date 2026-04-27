package android.support.transition;

import android.annotation.SuppressLint;
import android.support.annotation.StyleableRes;

@SuppressLint({"InlinedApi"})
class Styleable {
    @StyleableRes
    static final int[] ARC_MOTION = {16843901, 16843902, 16843903};
    @StyleableRes
    static final int[] CHANGE_BOUNDS = {16843983};
    @StyleableRes
    static final int[] CHANGE_TRANSFORM = {16843964, 16843965};
    @StyleableRes
    static final int[] FADE = {16843745};
    @StyleableRes
    static final int[] PATTERN_PATH_MOTION = {16843978};
    @StyleableRes
    static final int[] SLIDE = {16843824};
    @StyleableRes
    static final int[] TRANSITION = {16843073, 16843160, 16843746, 16843855};
    @StyleableRes
    static final int[] TRANSITION_MANAGER = {16843741, 16843742, 16843743};
    @StyleableRes
    static final int[] TRANSITION_SET = {16843744};
    @StyleableRes
    static final int[] TRANSITION_TARGET = {16842799, 16843740, 16843841, 16843842, 16843853, 16843854};
    @StyleableRes
    static final int[] VISIBILITY_TRANSITION = {16843900};

    interface ArcMotion {
        @StyleableRes
        public static final int MAXIMUM_ANGLE = 2;
        @StyleableRes
        public static final int MINIMUM_HORIZONTAL_ANGLE = 0;
        @StyleableRes
        public static final int MINIMUM_VERTICAL_ANGLE = 1;
    }

    interface ChangeBounds {
        @StyleableRes
        public static final int RESIZE_CLIP = 0;
    }

    interface ChangeTransform {
        @StyleableRes
        public static final int REPARENT = 0;
        @StyleableRes
        public static final int REPARENT_WITH_OVERLAY = 1;
    }

    interface Fade {
        @StyleableRes
        public static final int FADING_MODE = 0;
    }

    interface PatternPathMotion {
        @StyleableRes
        public static final int PATTERN_PATH_DATA = 0;
    }

    interface Slide {
        @StyleableRes
        public static final int SLIDE_EDGE = 0;
    }

    interface Transition {
        @StyleableRes
        public static final int DURATION = 1;
        @StyleableRes
        public static final int INTERPOLATOR = 0;
        @StyleableRes
        public static final int MATCH_ORDER = 3;
        @StyleableRes
        public static final int START_DELAY = 2;
    }

    interface TransitionManager {
        @StyleableRes
        public static final int FROM_SCENE = 0;
        @StyleableRes
        public static final int TO_SCENE = 1;
        @StyleableRes
        public static final int TRANSITION = 2;
    }

    interface TransitionSet {
        @StyleableRes
        public static final int TRANSITION_ORDERING = 0;
    }

    interface TransitionTarget {
        @StyleableRes
        public static final int EXCLUDE_CLASS = 3;
        @StyleableRes
        public static final int EXCLUDE_ID = 2;
        @StyleableRes
        public static final int EXCLUDE_NAME = 5;
        @StyleableRes
        public static final int TARGET_CLASS = 0;
        @StyleableRes
        public static final int TARGET_ID = 1;
        @StyleableRes
        public static final int TARGET_NAME = 4;
    }

    interface VisibilityTransition {
        @StyleableRes
        public static final int TRANSITION_VISIBILITY_MODE = 0;
    }

    Styleable() {
    }
}
