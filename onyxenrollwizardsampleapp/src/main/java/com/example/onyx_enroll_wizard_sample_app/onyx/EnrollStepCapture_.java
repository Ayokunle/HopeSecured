package com.example.onyx_enroll_wizard_sample_app.onyx;


import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dft.onyx.FingerprintTemplate;
import com.dft.onyx.enroll.util.CaptureAnimationCallbackUtil;
import com.dft.onyx.enroll.util.ConvertBitmapToJPG;
import com.dft.onyx.enroll.util.EnrollmentMetric;
import com.dft.onyx.enroll.util.OnyxFragmentBuilder;
import com.dft.onyx.enroll.util.ProgressLayoutUtil;
import com.dft.onyx.enroll.util.SetFingerprintLocation;
import com.dft.onyx.enroll.util.imageareas.EnumFinger;
import com.dft.onyx.verify.EnrollmentScaleVerifier;
import com.dft.onyx.wizardroid.WizardActivity;
import com.dft.onyx.wizardroid.WizardStep;
import com.dft.onyx.wizardroid.enrollwizard.EnrollWizard;
import com.dft.onyxcamera.ui.CaptureMetrics;
import com.dft.onyxcamera.ui.OnyxFragment;
import com.dft.onyxcamera.ui.OnyxFragment.CaptureAnimationCallback;
import com.dft.onyxcamera.ui.OnyxFragment.FingerprintTemplateCallback;
import com.dft.onyxcamera.ui.OnyxFragment.ProcessedBitmapCallback;
import com.example.onyx_enroll_wizard_sample_app.R;

import java.io.File;

public class EnrollStepCapture_ extends WizardStep {
    private static final String TAG = "EnrollStepCapture_";
    private WizardActivity mWizardActivity;
    private Context mContext;
    private OnyxFragment mFragment;
    @ContextVariable
    private File mBitmapFile;
    @ContextVariable
    private EnrollmentMetric mCurrentStepEnrollmentMetric = null;
    @ContextVariable
    private EnrollmentMetric[] mEnrollmentMetricArray;
    @ContextVariable
    private EnumFinger enrollingFinger;
    private boolean processedBitmapAlreadyCaptured = false;
    private boolean fingerprintTemplateAlreadyCaptured = false;
    int mNumEnrollCaptureSteps;
    int mReticleScaleMultiplier = 1;
    int mThisStepNum = 0;
    int mNumCapturesPerScale;
    int mNumEnrollScales;
    private float mMaxReticleScale = 1.0F;
    private float mMinReticleScale = 0.8F;
    private EnrollWizard mEnrollWizard;
    private CaptureAnimationCallback mCaptureAnimationCallback;
    private ProcessedBitmapCallback mProcessedBitmapCallback = new ProcessedBitmapCallback() {
        public void onProcessedBitmapReady(Bitmap processedBitmap, CaptureMetrics captureMetrics) {
            if(!EnrollStepCapture_.this.processedBitmapAlreadyCaptured) {
                EnrollStepCapture_.this.mCurrentStepEnrollmentMetric.setFocusScore(captureMetrics.getFocusQuality());
                ConvertBitmapToJPG.convertBitmapToJPG(processedBitmap, EnrollStepCapture_.this.mBitmapFile);
                EnrollStepCapture_.this.processedBitmapAlreadyCaptured = true;
            }

        }
    };
    private FingerprintTemplateCallback mFingerprintTemplateCallback = new FingerprintTemplateCallback() {
        public void onFingerprintTemplateReady(FingerprintTemplate fingerprintTemplate) {
            fingerprintTemplate = SetFingerprintLocation.setFingerprintLocation(fingerprintTemplate, EnrollStepCapture_.this.getEnrollingFinger());
            if(!EnrollStepCapture_.this.fingerprintTemplateAlreadyCaptured) {
                FingerprintTemplate[] ftArray = new FingerprintTemplate[]{fingerprintTemplate};
                EnrollStepCapture_.this.mCurrentStepEnrollmentMetric.setFingerprintTemplateArray(ftArray);
                EnrollStepCapture_.this.mEnrollmentMetricArray[(EnrollStepCapture_.this.mThisStepNum - 1) % EnrollStepCapture_.this.mNumCapturesPerScale] = EnrollStepCapture_.this.mCurrentStepEnrollmentMetric;
                if(EnrollStepCapture_.this.mThisStepNum % EnrollStepCapture_.this.mNumCapturesPerScale == 0) {
                    EnrollStepCapture_.this.setBestEnrollmentForCurrentScale();
                }

                EnrollStepCapture_.this.done();
                EnrollStepCapture_.this.fingerprintTemplateAlreadyCaptured = true;
            }

        }
    };

    public EnrollStepCapture_() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mContext = this.mWizardActivity = (WizardActivity)this.getActivity();
        this.mEnrollWizard = (EnrollWizard)this.getActivity();
        if(null != this.getActivity().getActionBar()) {
            this.getActivity().getActionBar().hide();
        }

        View v = inflater.inflate(this.mEnrollWizard.getContentViewId(), container, false);
        if(null != this.mWizardActivity.getActionBar()) {
            this.mWizardActivity.getActionBar().hide();
        }

        this.mCaptureAnimationCallback = (new CaptureAnimationCallbackUtil()).createCaptureAnimationCallback(this.mWizardActivity);
        return v;
    }

    public void onResume() {
        super.onResume();
        this.processedBitmapAlreadyCaptured = false;
        this.fingerprintTemplateAlreadyCaptured = false;
        View v = this.getView();
        this.mNumEnrollCaptureSteps = this.mWizardActivity.getIntent().getIntExtra("num_enroll_capture_steps", 1);
        this.mNumCapturesPerScale = this.mWizardActivity.getIntent().getIntExtra("num_enroll_captures_per_scale", 3);
        this.mNumEnrollScales = this.mWizardActivity.getIntent().getIntExtra("num_enroll_scales", 3);
        Log.d("EnrollStepCapture_", "capture steps = " + this.mNumEnrollCaptureSteps + "scales = " + this.mNumEnrollScales + "captures = " + this.mNumCapturesPerScale);
        this.mMaxReticleScale = this.mWizardActivity.getIntent().getFloatExtra("max_reticle_scale", 1.0F);
        this.mMinReticleScale = this.mWizardActivity.getIntent().getFloatExtra("min_reticle_scale", 0.8F);
        this.mThisStepNum = this.mWizardActivity.getWizard().getCurrentStepPosition() - this.mEnrollWizard.getNumStepsBeforeEnrollStepCapture() + 1;
        if(null != this.mEnrollWizard.getFingerToEnroll()) {
            this.setEnrollingFinger(this.mEnrollWizard.getFingerToEnroll());
        }

        float scaleIncrement = (this.mMaxReticleScale - this.mMinReticleScale) / (float)this.mNumCapturesPerScale;
        this.mReticleScaleMultiplier = (this.mThisStepNum - 1) / this.mNumCapturesPerScale;
        float reticleScaleToUse = this.mMinReticleScale + scaleIncrement * (float)this.mReticleScaleMultiplier;
        this.mCurrentStepEnrollmentMetric = new EnrollmentMetric(reticleScaleToUse, this.getEnrollingFinger());
        this.mCurrentStepEnrollmentMetric.setStepNum(this.mThisStepNum);
        if(null == this.mEnrollmentMetricArray) {
            this.mEnrollmentMetricArray = new EnrollmentMetric[this.mNumCapturesPerScale];
            Log.d("EnrollStepCapture_", "creating EnrollmentMetric array.");
        }

        OnyxFragmentBuilder ofb = (new OnyxFragmentBuilder(this.mEnrollWizard,
                R.id.fragment_content)).setEnrollmentMetric(this.mCurrentStepEnrollmentMetric).setProcessedBitmapCallback(this.mProcessedBitmapCallback).setFingerprintTemplateCallback(this.mFingerprintTemplateCallback).setCaptureAnimationCallback(this.mCaptureAnimationCallback).setWizard(this.mWizardActivity.getWizard()).setEnrollWizard(this.mEnrollWizard).setEnrollError(this.mCurrentStepEnrollmentMetric).setEnrollErrorArray(this.mEnrollmentMetricArray);
        if(this.mEnrollWizard.isUseSelfEnroll()) {
            ofb.setUseFingerReticleView();
        } else {
            ofb.setUseEllipseReticleView();
        }

        this.mFragment = ofb.build();
        this.mBitmapFile = new File(this.mWizardActivity.getFilesDir(), "enrollment_bitmap" + this.mThisStepNum);
        ProgressLayoutUtil.setProgressLayout(this.mContext, v, this.mNumEnrollCaptureSteps, this.mThisStepNum);
        v.requestLayout();
    }

    public void onPause() {
        super.onPause();
        if(0 == this.mWizardActivity.getWizard().getCurrentStepPosition()) {
            FragmentTransaction ft = this.mWizardActivity.getFragmentManager().beginTransaction();
            ft.remove(this.mFragment);
            ft.commit();
        }

    }

    private void setBestEnrollmentForCurrentScale() {
        EnrollmentMetric[] currentScaleEMArray = new EnrollmentMetric[this.mNumCapturesPerScale];

        for(int esa = 0; esa < currentScaleEMArray.length; ++esa) {
            currentScaleEMArray[esa] = this.mEnrollmentMetricArray[esa];
        }

        EnrollmentScaleVerifier var3 = new EnrollmentScaleVerifier(this.mEnrollWizard, this.mThisStepNum);
        var3.execute(new EnrollmentMetric[][]{currentScaleEMArray});
        if(this.mThisStepNum != this.mNumEnrollCaptureSteps) {
            this.mEnrollWizard.signalCallbackRegistered();
        }

    }

    private EnumFinger getEnrollingFinger() {
        return this.enrollingFinger;
    }

    private void setEnrollingFinger(EnumFinger enrollingFinger) {
        this.enrollingFinger = enrollingFinger;
    }
}
