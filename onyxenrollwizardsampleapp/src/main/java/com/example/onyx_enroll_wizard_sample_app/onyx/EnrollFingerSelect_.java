package com.example.onyx_enroll_wizard_sample_app.onyx;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import com.dft.onyx.core;
import com.dft.onyx.enroll.util.ClearEnrollmentDataUtil;
import com.dft.onyx.enroll.util.EnrollmentMetric;
import com.dft.onyx.enroll.util.LicenseCheckerUtil;
import com.dft.onyx.enroll.util.imageareas.EnumFinger;
import com.dft.onyx.enroll.util.imageareas.EnumHand;
import com.dft.onyx.wizardroid.WizardActivity;
import com.dft.onyx.wizardroid.WizardStep;
import com.dft.onyx.wizardroid.enrollwizard.EnrollWizard;
import com.example.onyx_enroll_wizard_sample_app.R;

import org.opencv.android.OpenCVLoader;

public class EnrollFingerSelect_ extends WizardStep implements ColorTouchListenerHelper_.FingerSelectedCallback {
    private static final String TAG = "EnrollFingerSelect";
    private WizardActivity mWizardActivity = null;
    private Context mContext = null;
    private EnrollWizard mEnrollWizard = null;
    private View mView = null;
    private ColorTouchListenerHelper_.FingerSelectedCallback mFsc;

    @ContextVariable
    private EnrollmentMetric[] mEnrollmentMetricArray;

    public EnrollFingerSelect_() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.begin_enroll_layout, container, false);
        this.mContext = this.mWizardActivity = (WizardActivity)this.getActivity();
        this.mEnrollWizard = (EnrollWizard)this.getActivity();
        this.mFsc = this;
        int numEnrollCaptureSteps = this.mWizardActivity.getIntent().getIntExtra("num_enroll_capture_steps", 1);
        this.mEnrollmentMetricArray = new EnrollmentMetric[numEnrollCaptureSteps];
        if(null != this.mWizardActivity.getActionBar()) {
            this.mWizardActivity.getActionBar().hide();
        }

        String licenseKey = this.mWizardActivity.getIntent().getExtras().getString("onyx_licence_key");
        if(null != licenseKey) {
            LicenseCheckerUtil.validateLicense(this.mContext, licenseKey);
        }

        return this.mView;
    }

    public void onResume() {
        super.onResume();
        ClearEnrollmentDataUtil.clearEnrollmentData(this.mWizardActivity);
        ImageView leftHandImage = (ImageView)this.mView.findViewById(R.id.begin_enroll_fingers_left_image);
        leftHandImage.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                ColorTouchListenerHelper_ ctl = new ColorTouchListenerHelper_();
                boolean result = ctl.onTouch(EnrollFingerSelect_.this.mWizardActivity,
                        EnrollFingerSelect_.this.mView, event,
                        EnumHand.LEFT_HAND,
                        R.id.begin_enroll_fingers_left_image,
                        R.drawable.fingers_default,
                        R.id.begin_enroll_fingers_left_hotspots,
                        EnrollFingerSelect_.this.mFsc);
                return result;
            }
        });
        ImageView rightHandImage = (ImageView)this.mView.findViewById(R.id.begin_enroll_fingers_right_image);
        rightHandImage.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                ColorTouchListenerHelper_ ctl = new ColorTouchListenerHelper_();
                boolean result = ctl.onTouch(EnrollFingerSelect_.this.mWizardActivity,
                        EnrollFingerSelect_.this.mView,
                        event,
                        EnumHand.RIGHT_HAND,
                        R.id.begin_enroll_fingers_right_image,
                        R.drawable.fingers_default,
                        R.id.begin_enroll_fingers_left_hotspots,
                        EnrollFingerSelect_.this.mFsc);
                return result;
            }
        });
    }

    public void onFingerSelected(EnumFinger enumFinger) {
        this.mEnrollWizard.setFingerToEnroll(enumFinger);
        this.done();
    }

    static {
        if(!OpenCVLoader.initDebug()) {
            Log.d("EnrollFingerSelect", "Unable to load OpenCV!");
        } else {
            Log.i("EnrollFingerSelect", "OpenCV loaded successfully");
            core.initOnyx();
        }

    }
}
