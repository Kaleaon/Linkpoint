// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.widget;

import android.graphics.drawable.Drawable;
import android.support.annotation.RequiresApi;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.widget.ImageView;
import android.os.Build$VERSION;

public class ImageViewCompat
{
    static final ImageViewCompatImpl IMPL;
    
    static {
        if (Build$VERSION.SDK_INT < 21) {
            IMPL = (ImageViewCompatImpl)new BaseViewCompatImpl();
        }
        else {
            IMPL = (ImageViewCompatImpl)new LollipopViewCompatImpl();
        }
    }
    
    private ImageViewCompat() {
    }
    
    public static ColorStateList getImageTintList(final ImageView imageView) {
        return ImageViewCompat.IMPL.getImageTintList(imageView);
    }
    
    public static PorterDuff$Mode getImageTintMode(final ImageView imageView) {
        return ImageViewCompat.IMPL.getImageTintMode(imageView);
    }
    
    public static void setImageTintList(final ImageView imageView, final ColorStateList list) {
        ImageViewCompat.IMPL.setImageTintList(imageView, list);
    }
    
    public static void setImageTintMode(final ImageView imageView, final PorterDuff$Mode porterDuff$Mode) {
        ImageViewCompat.IMPL.setImageTintMode(imageView, porterDuff$Mode);
    }
    
    static class BaseViewCompatImpl implements ImageViewCompatImpl
    {
        @Override
        public ColorStateList getImageTintList(final ImageView imageView) {
            ColorStateList supportImageTintList;
            if (!(imageView instanceof TintableImageSourceView)) {
                supportImageTintList = null;
            }
            else {
                supportImageTintList = ((TintableImageSourceView)imageView).getSupportImageTintList();
            }
            return supportImageTintList;
        }
        
        @Override
        public PorterDuff$Mode getImageTintMode(final ImageView imageView) {
            PorterDuff$Mode supportImageTintMode;
            if (!(imageView instanceof TintableImageSourceView)) {
                supportImageTintMode = null;
            }
            else {
                supportImageTintMode = ((TintableImageSourceView)imageView).getSupportImageTintMode();
            }
            return supportImageTintMode;
        }
        
        @Override
        public void setImageTintList(final ImageView imageView, final ColorStateList supportImageTintList) {
            if (imageView instanceof TintableImageSourceView) {
                ((TintableImageSourceView)imageView).setSupportImageTintList(supportImageTintList);
            }
        }
        
        @Override
        public void setImageTintMode(final ImageView imageView, final PorterDuff$Mode supportImageTintMode) {
            if (imageView instanceof TintableImageSourceView) {
                ((TintableImageSourceView)imageView).setSupportImageTintMode(supportImageTintMode);
            }
        }
    }
    
    interface ImageViewCompatImpl
    {
        ColorStateList getImageTintList(final ImageView p0);
        
        PorterDuff$Mode getImageTintMode(final ImageView p0);
        
        void setImageTintList(final ImageView p0, final ColorStateList p1);
        
        void setImageTintMode(final ImageView p0, final PorterDuff$Mode p1);
    }
    
    @RequiresApi(21)
    static class LollipopViewCompatImpl extends BaseViewCompatImpl
    {
        @Override
        public ColorStateList getImageTintList(final ImageView imageView) {
            return imageView.getImageTintList();
        }
        
        @Override
        public PorterDuff$Mode getImageTintMode(final ImageView imageView) {
            return imageView.getImageTintMode();
        }
        
        @Override
        public void setImageTintList(final ImageView imageView, final ColorStateList imageTintList) {
            boolean b = false;
            imageView.setImageTintList(imageTintList);
            if (Build$VERSION.SDK_INT == 21) {
                final Drawable drawable = imageView.getDrawable();
                if (imageView.getImageTintList() != null && imageView.getImageTintMode() != null) {
                    b = true;
                }
                if (drawable != null && b) {
                    if (drawable.isStateful()) {
                        drawable.setState(imageView.getDrawableState());
                    }
                    imageView.setImageDrawable(drawable);
                }
            }
        }
        
        @Override
        public void setImageTintMode(final ImageView imageView, final PorterDuff$Mode imageTintMode) {
            boolean b = false;
            imageView.setImageTintMode(imageTintMode);
            if (Build$VERSION.SDK_INT == 21) {
                final Drawable drawable = imageView.getDrawable();
                if (imageView.getImageTintList() != null && imageView.getImageTintMode() != null) {
                    b = true;
                }
                if (drawable != null && b) {
                    if (drawable.isStateful()) {
                        drawable.setState(imageView.getDrawableState());
                    }
                    imageView.setImageDrawable(drawable);
                }
            }
        }
    }
}
