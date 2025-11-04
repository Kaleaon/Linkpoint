// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.media;

import android.support.annotation.RestrictTo;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.util.Log;
import android.os.Build$VERSION;
import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.os.Parcelable;

public final class RatingCompat implements Parcelable
{
    public static final Parcelable$Creator<RatingCompat> CREATOR;
    public static final int RATING_3_STARS = 3;
    public static final int RATING_4_STARS = 4;
    public static final int RATING_5_STARS = 5;
    public static final int RATING_HEART = 1;
    public static final int RATING_NONE = 0;
    private static final float RATING_NOT_RATED = -1.0f;
    public static final int RATING_PERCENTAGE = 6;
    public static final int RATING_THUMB_UP_DOWN = 2;
    private static final String TAG = "Rating";
    private Object mRatingObj;
    private final int mRatingStyle;
    private final float mRatingValue;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<RatingCompat>() {
            public RatingCompat createFromParcel(final Parcel parcel) {
                return new RatingCompat(parcel.readInt(), parcel.readFloat());
            }
            
            public RatingCompat[] newArray(final int n) {
                return new RatingCompat[n];
            }
        };
    }
    
    RatingCompat(final int mRatingStyle, final float mRatingValue) {
        this.mRatingStyle = mRatingStyle;
        this.mRatingValue = mRatingValue;
    }
    
    public static RatingCompat fromRating(final Object mRatingObj) {
        if (mRatingObj != null && Build$VERSION.SDK_INT >= 19) {
            final int ratingStyle = RatingCompatKitkat.getRatingStyle(mRatingObj);
            RatingCompat ratingCompat = null;
            if (!RatingCompatKitkat.isRated(mRatingObj)) {
                ratingCompat = newUnratedRating(ratingStyle);
            }
            else {
                switch (ratingStyle) {
                    default: {
                        return null;
                    }
                    case 1: {
                        ratingCompat = newHeartRating(RatingCompatKitkat.hasHeart(mRatingObj));
                        break;
                    }
                    case 2: {
                        ratingCompat = newThumbRating(RatingCompatKitkat.isThumbUp(mRatingObj));
                        break;
                    }
                    case 3:
                    case 4:
                    case 5: {
                        ratingCompat = newStarRating(ratingStyle, RatingCompatKitkat.getStarRating(mRatingObj));
                        break;
                    }
                    case 6: {
                        ratingCompat = newPercentageRating(RatingCompatKitkat.getPercentRating(mRatingObj));
                        break;
                    }
                }
            }
            ratingCompat.mRatingObj = mRatingObj;
            return ratingCompat;
        }
        return null;
    }
    
    public static RatingCompat newHeartRating(final boolean b) {
        float n;
        if (!b) {
            n = 0.0f;
        }
        else {
            n = 1.0f;
        }
        return new RatingCompat(1, n);
    }
    
    public static RatingCompat newPercentageRating(final float n) {
        boolean b;
        if (n < 0.0f) {
            b = true;
        }
        else {
            b = false;
        }
        if (b || n > 100.0f) {
            Log.e("Rating", "Invalid percentage-based rating value");
            return null;
        }
        return new RatingCompat(6, n);
    }
    
    public static RatingCompat newStarRating(final int i, final float n) {
        float n2 = 0.0f;
        switch (i) {
            default: {
                Log.e("Rating", "Invalid rating style (" + i + ") for a star rating");
                return null;
            }
            case 3: {
                n2 = 3.0f;
                break;
            }
            case 4: {
                n2 = 4.0f;
                break;
            }
            case 5: {
                n2 = 5.0f;
                break;
            }
        }
        boolean b;
        if (n < 0.0f) {
            b = true;
        }
        else {
            b = false;
        }
        if (b || n > n2) {
            Log.e("Rating", "Trying to set out of range star-based rating");
            return null;
        }
        return new RatingCompat(i, n);
    }
    
    public static RatingCompat newThumbRating(final boolean b) {
        float n;
        if (!b) {
            n = 0.0f;
        }
        else {
            n = 1.0f;
        }
        return new RatingCompat(2, n);
    }
    
    public static RatingCompat newUnratedRating(final int n) {
        switch (n) {
            default: {
                return null;
            }
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6: {
                return new RatingCompat(n, -1.0f);
            }
        }
    }
    
    public int describeContents() {
        return this.mRatingStyle;
    }
    
    public float getPercentRating() {
        if (this.mRatingStyle == 6 && this.isRated()) {
            return this.mRatingValue;
        }
        return -1.0f;
    }
    
    public Object getRating() {
        if (this.mRatingObj == null && Build$VERSION.SDK_INT >= 19) {
            if (!this.isRated()) {
                this.mRatingObj = RatingCompatKitkat.newUnratedRating(this.mRatingStyle);
            }
            else {
                switch (this.mRatingStyle) {
                    default: {
                        return null;
                    }
                    case 1: {
                        this.mRatingObj = RatingCompatKitkat.newHeartRating(this.hasHeart());
                        break;
                    }
                    case 2: {
                        this.mRatingObj = RatingCompatKitkat.newThumbRating(this.isThumbUp());
                        break;
                    }
                    case 3:
                    case 4:
                    case 5: {
                        this.mRatingObj = RatingCompatKitkat.newStarRating(this.mRatingStyle, this.getStarRating());
                        break;
                    }
                    case 6: {
                        this.mRatingObj = RatingCompatKitkat.newPercentageRating(this.getPercentRating());
                        break;
                    }
                }
            }
        }
        return this.mRatingObj;
    }
    
    public int getRatingStyle() {
        return this.mRatingStyle;
    }
    
    public float getStarRating() {
        switch (this.mRatingStyle) {
            case 3:
            case 4:
            case 5: {
                if (this.isRated()) {
                    return this.mRatingValue;
                }
                break;
            }
        }
        return -1.0f;
    }
    
    public boolean hasHeart() {
        boolean b = true;
        if (this.mRatingStyle == 1) {
            if (this.mRatingValue != 1.0f) {
                b = false;
            }
            return b;
        }
        return false;
    }
    
    public boolean isRated() {
        return this.mRatingValue >= 0.0f;
    }
    
    public boolean isThumbUp() {
        boolean b = false;
        if (this.mRatingStyle == 2) {
            if (this.mRatingValue == 1.0f) {
                b = true;
            }
            return b;
        }
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder append = new StringBuilder().append("Rating:style=").append(this.mRatingStyle).append(" rating=");
        String value;
        if (this.mRatingValue < 0.0f) {
            value = "unrated";
        }
        else {
            value = String.valueOf(this.mRatingValue);
        }
        return append.append(value).toString();
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeInt(this.mRatingStyle);
        parcel.writeFloat(this.mRatingValue);
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public @interface StarStyle {
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public @interface Style {
    }
}
