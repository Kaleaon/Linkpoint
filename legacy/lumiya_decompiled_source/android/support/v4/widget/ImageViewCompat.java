package android.support.v4.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.ImageView;
/* loaded from: classes.dex */
public class ImageViewCompat {
    static final ImageViewCompatImpl IMPL;

    /* loaded from: classes.dex */
    static class BaseViewCompatImpl implements ImageViewCompatImpl {
        BaseViewCompatImpl() {
        }

        @Override // android.support.v4.widget.ImageViewCompat.ImageViewCompatImpl
        public ColorStateList getImageTintList(ImageView imageView) {
            if (imageView instanceof TintableImageSourceView) {
                return ((TintableImageSourceView) imageView).getSupportImageTintList();
            }
            return null;
        }

        @Override // android.support.v4.widget.ImageViewCompat.ImageViewCompatImpl
        public PorterDuff.Mode getImageTintMode(ImageView imageView) {
            if (imageView instanceof TintableImageSourceView) {
                return ((TintableImageSourceView) imageView).getSupportImageTintMode();
            }
            return null;
        }

        @Override // android.support.v4.widget.ImageViewCompat.ImageViewCompatImpl
        public void setImageTintList(ImageView imageView, ColorStateList colorStateList) {
            if (imageView instanceof TintableImageSourceView) {
                ((TintableImageSourceView) imageView).setSupportImageTintList(colorStateList);
            }
        }

        @Override // android.support.v4.widget.ImageViewCompat.ImageViewCompatImpl
        public void setImageTintMode(ImageView imageView, PorterDuff.Mode mode) {
            if (imageView instanceof TintableImageSourceView) {
                ((TintableImageSourceView) imageView).setSupportImageTintMode(mode);
            }
        }
    }

    /* loaded from: classes.dex */
    interface ImageViewCompatImpl {
        ColorStateList getImageTintList(ImageView imageView);

        PorterDuff.Mode getImageTintMode(ImageView imageView);

        void setImageTintList(ImageView imageView, ColorStateList colorStateList);

        void setImageTintMode(ImageView imageView, PorterDuff.Mode mode);
    }

    @RequiresApi(21)
    /* loaded from: classes.dex */
    static class LollipopViewCompatImpl extends BaseViewCompatImpl {
        LollipopViewCompatImpl() {
        }

        @Override // android.support.v4.widget.ImageViewCompat.BaseViewCompatImpl, android.support.v4.widget.ImageViewCompat.ImageViewCompatImpl
        public ColorStateList getImageTintList(ImageView imageView) {
            return imageView.getImageTintList();
        }

        @Override // android.support.v4.widget.ImageViewCompat.BaseViewCompatImpl, android.support.v4.widget.ImageViewCompat.ImageViewCompatImpl
        public PorterDuff.Mode getImageTintMode(ImageView imageView) {
            return imageView.getImageTintMode();
        }

        @Override // android.support.v4.widget.ImageViewCompat.BaseViewCompatImpl, android.support.v4.widget.ImageViewCompat.ImageViewCompatImpl
        public void setImageTintList(ImageView imageView, ColorStateList colorStateList) {
            boolean z = false;
            imageView.setImageTintList(colorStateList);
            if (Build.VERSION.SDK_INT != 21) {
                return;
            }
            Drawable drawable = imageView.getDrawable();
            if (imageView.getImageTintList() != null && imageView.getImageTintMode() != null) {
                z = true;
            }
            if (drawable == null || !z) {
                return;
            }
            if (drawable.isStateful()) {
                drawable.setState(imageView.getDrawableState());
            }
            imageView.setImageDrawable(drawable);
        }

        @Override // android.support.v4.widget.ImageViewCompat.BaseViewCompatImpl, android.support.v4.widget.ImageViewCompat.ImageViewCompatImpl
        public void setImageTintMode(ImageView imageView, PorterDuff.Mode mode) {
            boolean z = false;
            imageView.setImageTintMode(mode);
            if (Build.VERSION.SDK_INT != 21) {
                return;
            }
            Drawable drawable = imageView.getDrawable();
            if (imageView.getImageTintList() != null && imageView.getImageTintMode() != null) {
                z = true;
            }
            if (drawable == null || !z) {
                return;
            }
            if (drawable.isStateful()) {
                drawable.setState(imageView.getDrawableState());
            }
            imageView.setImageDrawable(drawable);
        }
    }

    static {
        if (Build.VERSION.SDK_INT < 21) {
            IMPL = new BaseViewCompatImpl();
        } else {
            IMPL = new LollipopViewCompatImpl();
        }
    }

    private ImageViewCompat() {
    }

    public static ColorStateList getImageTintList(ImageView imageView) {
        return IMPL.getImageTintList(imageView);
    }

    public static PorterDuff.Mode getImageTintMode(ImageView imageView) {
        return IMPL.getImageTintMode(imageView);
    }

    public static void setImageTintList(ImageView imageView, ColorStateList colorStateList) {
        IMPL.setImageTintList(imageView, colorStateList);
    }

    public static void setImageTintMode(ImageView imageView, PorterDuff.Mode mode) {
        IMPL.setImageTintMode(imageView, mode);
    }
}
