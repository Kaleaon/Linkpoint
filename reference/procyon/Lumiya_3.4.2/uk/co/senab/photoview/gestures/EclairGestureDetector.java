// 
// Decompiled by Procyon v0.6.0
// 

package uk.co.senab.photoview.gestures;

import uk.co.senab.photoview.Compat;
import android.view.MotionEvent;
import android.content.Context;
import android.annotation.TargetApi;

@TargetApi(5)
public class EclairGestureDetector extends CupcakeGestureDetector
{
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId;
    private int mActivePointerIndex;
    
    public EclairGestureDetector(final Context context) {
        super(context);
        this.mActivePointerId = -1;
        this.mActivePointerIndex = 0;
    }
    
    @Override
    float getActiveX(final MotionEvent motionEvent) {
        try {
            return motionEvent.getX(this.mActivePointerIndex);
        }
        catch (final Exception ex) {
            return motionEvent.getX();
        }
    }
    
    @Override
    float getActiveY(final MotionEvent motionEvent) {
        try {
            return motionEvent.getY(this.mActivePointerIndex);
        }
        catch (final Exception ex) {
            return motionEvent.getY();
        }
    }
    
    @Override
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final int n = 0;
        while (true) {
        Label_0101_Outer:
            while (true) {
                int mActivePointerId;
                int pointerIndex;
                int n2;
                Label_0052:Label_0093_Outer:
                while (true) {
                    while (true) {
                        switch (motionEvent.getAction() & 0xFF) {
                            case 0: {
                                Label_0081: {
                                    break Label_0081;
                                    while (true) {
                                        this.mActivePointerIndex = motionEvent.findPointerIndex(mActivePointerId);
                                        try {
                                            return super.onTouchEvent(motionEvent);
                                            pointerIndex = Compat.getPointerIndex(motionEvent.getAction());
                                            iftrue(Label_0052:)(motionEvent.getPointerId(pointerIndex) != this.mActivePointerId);
                                            while (true) {
                                                while (true) {
                                                    Block_4: {
                                                        break Block_4;
                                                        this.mActivePointerId = motionEvent.getPointerId(n2);
                                                        this.mLastTouchX = motionEvent.getX(n2);
                                                        this.mLastTouchY = motionEvent.getY(n2);
                                                        break Label_0052;
                                                        this.mActivePointerId = -1;
                                                        break Label_0052;
                                                        n2 = 0;
                                                        continue Label_0093_Outer;
                                                        mActivePointerId = this.mActivePointerId;
                                                        continue Label_0101_Outer;
                                                    }
                                                    iftrue(Label_0157:)(pointerIndex == 0);
                                                    continue;
                                                }
                                                Label_0157: {
                                                    n2 = 1;
                                                }
                                                continue Label_0093_Outer;
                                            }
                                            this.mActivePointerId = motionEvent.getPointerId(0);
                                            break Label_0052;
                                        }
                                        catch (final IllegalArgumentException ex) {
                                            return true;
                                        }
                                    }
                                }
                                break;
                            }
                            case 1:
                            case 3: {
                                continue;
                            }
                            case 6: {
                                continue Label_0093_Outer;
                            }
                        }
                        break;
                    }
                    break;
                }
                if (this.mActivePointerId == -1) {
                    mActivePointerId = n;
                    continue;
                }
                break;
            }
            continue;
        }
    }
}
