package com.example.onyx_enroll_wizard_sample_app.onyx;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import com.dft.onyx.enroll.util.imageareas.ColorTool;
import com.dft.onyx.enroll.util.imageareas.EnumFinger;
import com.dft.onyx.enroll.util.imageareas.EnumHand;
import com.dft.onyx.wizardroid.WizardActivity;
import com.example.onyx_enroll_wizard_sample_app.R;

public class ColorTouchListenerHelper_ {
    private static final String TAG = "ColorTouchListenerHelper";
    public static final int RED = -65536;
    public static final int BLUE = -16770375;
    public static final int GREEN = -16467712;
    public static final int YELLOW = -6373;
    public static final int WHITE = -1;
    private ColorTouchListenerHelper_.FingerSelectedCallback mFsc;
    private WizardActivity mWizardActivity = null;

    public ColorTouchListenerHelper_() {
    }

    public boolean onTouch(WizardActivity wizardActivity,
                           View rootView, MotionEvent ev,
                           EnumHand hand, int defaultFingersImageViewResourceId,
                           int defaultFingersDrawableResourceId,
                           int fingerHotSpotsAreaImageViewResourceId,
                           ColorTouchListenerHelper_.FingerSelectedCallback fsc) {
        boolean handledHere = false;
        this.mWizardActivity = wizardActivity;
        this.mFsc = fsc;
        int action = ev.getAction();
        int evX = (int)ev.getX();
        int evY = (int)ev.getY();
        int nextDrawableResourceId = -1;
        ImageView imageView = (ImageView)rootView.findViewById(defaultFingersImageViewResourceId);
        if(imageView == null) {
            return false;
        } else {
            Integer tagNum = (Integer)imageView.getTag();
            int currentDrawableResourceId = tagNum == null?defaultFingersDrawableResourceId:tagNum.intValue();
            switch(action) {
                case 0:
                    int touchColor = this.getHotspotColor(hand, fingerHotSpotsAreaImageViewResourceId, evX, evY);
                    ColorTool ct = new ColorTool();
                    byte tolerance = 25;
                    if(currentDrawableResourceId == defaultFingersDrawableResourceId) {
                        nextDrawableResourceId = defaultFingersDrawableResourceId;
                        if(ct.closeMatch(-65536, touchColor, tolerance)) {
                            nextDrawableResourceId = R.drawable.fingers_index_selected;
                        } else if(ct.closeMatch(-16467712, touchColor, tolerance)) {
                            nextDrawableResourceId = R.drawable.fingers_middle_selected;
                        } else if(ct.closeMatch(-16770375, touchColor, tolerance)) {
                            nextDrawableResourceId = R.drawable.fingers_ring_selected;
                        } else if(ct.closeMatch(-6373, touchColor, tolerance)) {
                            nextDrawableResourceId = R.drawable.fingers_little_selected;
                        } else if(ct.closeMatch(-1, touchColor, tolerance)) {
                            nextDrawableResourceId = R.drawable.fingers_thumb_selected;
                        }

                        handledHere = true;
                    }
                    break;
                case 1:
                    int tColor = this.getHotspotColor(hand, fingerHotSpotsAreaImageViewResourceId, evX, evY);
                    ColorTool cTool = new ColorTool();
                    byte tol = 25;
                    if(cTool.closeMatch(-65536, tColor, tol)) {
                        if(EnumHand.LEFT_HAND == hand) {
                            this.setEnumFinger(EnumFinger.LEFT_INDEX);
                        } else {
                            this.setEnumFinger(EnumFinger.RIGHT_INDEX);
                        }
                    } else if(cTool.closeMatch(-16467712, tColor, tol)) {
                        if(EnumHand.LEFT_HAND == hand) {
                            this.setEnumFinger(EnumFinger.LEFT_MIDDLE);
                        } else {
                            this.setEnumFinger(EnumFinger.RIGHT_MIDDLE);
                        }
                    } else if(cTool.closeMatch(-16770375, tColor, tol)) {
                        if(EnumHand.LEFT_HAND == hand) {
                            this.setEnumFinger(EnumFinger.LEFT_RING);
                        } else {
                            this.setEnumFinger(EnumFinger.RIGHT_RING);
                        }
                    } else if(cTool.closeMatch(-6373, tColor, tol)) {
                        if(EnumHand.LEFT_HAND == hand) {
                            this.setEnumFinger(EnumFinger.LEFT_LITTLE);
                        } else {
                            this.setEnumFinger(EnumFinger.RIGHT_LITTLE);
                        }
                    } else if(cTool.closeMatch(-1, tColor, tol)) {
                        if(EnumHand.LEFT_HAND == hand) {
                            this.setEnumFinger(EnumFinger.LEFT_THUMB);
                        } else {
                            this.setEnumFinger(EnumFinger.RIGHT_THUMB);
                        }
                    } else if(currentDrawableResourceId != defaultFingersDrawableResourceId) {
                        nextDrawableResourceId = defaultFingersDrawableResourceId;
                    }

                    handledHere = true;
                    break;
                default:
                    handledHere = false;
            }

            if(handledHere && nextDrawableResourceId > 0) {
                if(EnumHand.LEFT_HAND == hand) {
                    imageView.setImageResource(nextDrawableResourceId);
                    imageView.setTag(Integer.valueOf(nextDrawableResourceId));
                } else {
                    imageView.setScaleX(-1.0F);
                    imageView.setImageResource(nextDrawableResourceId);
                    imageView.setTag(Integer.valueOf(nextDrawableResourceId));
                }
            }

            return handledHere;
        }
    }

    private int getHotspotColor(EnumHand hand, int hotspotId, int x, int y) {
        ImageView img = (ImageView)this.mWizardActivity.findViewById(hotspotId);
        if(img == null) {
            Log.d("ImageAreasActivity", "Hot spot image not found");
            return 0;
        } else {
            img.setDrawingCacheEnabled(true);
            Bitmap mHotSpots = Bitmap.createBitmap(img.getDrawingCache());
            if(mHotSpots == null) {
                Log.d("ImageAreasActivity", "Hot spot bitmap was not created");
                return 0;
            } else {
                img.setDrawingCacheEnabled(false);
                Log.d("ColorTouchListener", "Hot spot bitmap created.");
                return x < mHotSpots.getWidth() && y < mHotSpots.getHeight()?mHotSpots.getPixel(x, y):mHotSpots.getPixel(0, 0);
            }
        }
    }

    private void setEnumFinger(EnumFinger enumFinger) {
        this.mFsc.onFingerSelected(enumFinger);
    }

    public interface FingerSelectedCallback {
        void onFingerSelected(EnumFinger var1);
    }
}
