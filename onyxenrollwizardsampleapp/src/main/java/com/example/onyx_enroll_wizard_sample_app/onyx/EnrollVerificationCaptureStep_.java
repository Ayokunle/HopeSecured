package com.example.onyx_enroll_wizard_sample_app.onyx;

/**
 * Created by Ayokunle on 17/07/2016.
 */

import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dft.onyx.FingerprintTemplate;
import com.dft.onyx.FingerprintTemplateVector;
import com.dft.onyx.enroll.util.CaptureAnimationCallbackUtil;
import com.dft.onyx.enroll.util.ConvertBitmapToFile;
import com.dft.onyx.enroll.util.EnrollmentMetric;
import com.dft.onyx.enroll.util.OnyxFragmentBuilder;
import com.dft.onyx.enroll.util.ProgressLayoutUtil;
import com.dft.onyx.enroll.util.SetFingerprintLocation;
import com.dft.onyx.enroll.util.imageareas.EnumFinger;
import com.dft.onyx.onyx_enroll_wizard.R.id;
import com.dft.onyx.onyx_enroll_wizard.R.layout;
import com.dft.onyx.verify.VerifyFingerprintTemplateVectorTask;
import com.dft.onyx.verify.VerifyFingerprintTemplateVectorTask.TemplateVectorResultCallback;
import com.dft.onyx.wizardroid.ContextVariable;
import com.dft.onyx.wizardroid.WizardActivity;
import com.dft.onyx.wizardroid.WizardStep;
import com.dft.onyx.wizardroid.enrollwizard.EnrollVerificationCaptureStep;
import com.dft.onyx.wizardroid.enrollwizard.EnrollWizard;
import com.dft.onyx.wizardroid.enrollwizard.EnrollWizard.EnrollmentCallback;
import com.dft.onyxcamera.ui.CaptureMetrics;
import com.dft.onyxcamera.ui.OnyxFragment;
import com.dft.onyxcamera.ui.OnyxFragment.CaptureAnimationCallback;
import com.dft.onyxcamera.ui.OnyxFragment.FingerprintTemplateCallback;
import com.dft.onyxcamera.ui.OnyxFragment.ProcessedBitmapCallback;
import java.io.File;

public class EnrollVerificationCaptureStep_ extends WizardStep implements TemplateVectorResultCallback, EnrollmentCallback {
    private static final String TAG = "EnrollVerificationCaptu";
    private Context mContext;
    private WizardActivity mWizardActivity;
    private OnyxFragment mFragment;
    private File mVerificationBitmapFile;
    @ContextVariable
    private EnumFinger enrollingFinger;
    @ContextVariable
    private EnrollmentMetric bestEnrollmentMetric;
    @ContextVariable
    private EnrollmentMetric[] bestEnrollmentMetricsArray;
    @ContextVariable
    private File mBestBitmapFile;
    @ContextVariable
    private FingerprintTemplate mVerificationFingerprintTemplate;
    @ContextVariable
    private double mVerificationFingerprintFocusQuality;
    @ContextVariable
    private boolean m_bUseVerificationFingerprintTemplate;
    @ContextVariable
    private boolean bMatchConfirmed;
    @ContextVariable
    private String bitmapToUse;
    private TemplateVectorResultCallback mInstance;
    private boolean processedBitmapAlreadyCaptured;
    private boolean fingerprintTemplateAlreadyCaptured;
    @ContextVariable
    int mBestStepNum;
    private FingerprintTemplateVector mGallery;
    private CaptureAnimationCallback mCaptureAnimationCallback;
    private ProcessedBitmapCallback mProcessedBitmapCallback;
    private FingerprintTemplateCallback mFingerprintTemplateCallback;

    public EnrollVerificationCaptureStep_() {
        this.bestEnrollmentMetric = new EnrollmentMetric(1.0F, this.enrollingFinger);
        this.m_bUseVerificationFingerprintTemplate = false;
        this.bMatchConfirmed = false;
        this.bitmapToUse = null;
        this.processedBitmapAlreadyCaptured = false;
        this.fingerprintTemplateAlreadyCaptured = false;
        this.mBestStepNum = 0;
        this.mProcessedBitmapCallback = new ProcessedBitmapCallback() {
            public void onProcessedBitmapReady(Bitmap processedBitmap, CaptureMetrics captureMetrics) {
                if(!EnrollVerificationCaptureStep_.this.processedBitmapAlreadyCaptured) {
                    EnrollVerificationCaptureStep_.this.mVerificationFingerprintFocusQuality = captureMetrics.getFocusQuality();
                    ConvertBitmapToFile cbtf = new ConvertBitmapToFile(processedBitmap, EnrollVerificationCaptureStep_.this.mVerificationBitmapFile);
                    EnrollVerificationCaptureStep_.this.mVerificationBitmapFile = cbtf.convertBitmapToFile(CompressFormat.JPEG);
                    if(EnrollVerificationCaptureStep_.this.mVerificationFingerprintFocusQuality > EnrollVerificationCaptureStep_.this.bestEnrollmentMetric.getFocusScore()) {
                        EnrollVerificationCaptureStep_.this.m_bUseVerificationFingerprintTemplate = true;
                    }

                    EnrollVerificationCaptureStep_.this.processedBitmapAlreadyCaptured = true;
                }

                processedBitmap.recycle();
            }
        };
        this.mFingerprintTemplateCallback = new FingerprintTemplateCallback() {
            public void onFingerprintTemplateReady(FingerprintTemplate fingerprintTemplate) {
                fingerprintTemplate = SetFingerprintLocation.setFingerprintLocation(fingerprintTemplate, EnrollVerificationCaptureStep_.this.enrollingFinger);
                if(!EnrollVerificationCaptureStep_.this.fingerprintTemplateAlreadyCaptured) {
                    EnrollVerificationCaptureStep_.this.mVerificationFingerprintTemplate = fingerprintTemplate;
                    if(null != EnrollVerificationCaptureStep_.this.bestEnrollmentMetricsArray) {
                        EnrollVerificationCaptureStep_.this.mGallery =
                                new FingerprintTemplateVector((long)EnrollVerificationCaptureStep_
                                        .this
                                        .bestEnrollmentMetricsArray.length);

                        for(int vftvt = 0; vftvt < EnrollVerificationCaptureStep_.this.mGallery.size(); ++vftvt) {
                            if(null != EnrollVerificationCaptureStep_.this.bestEnrollmentMetricsArray[vftvt]) {
                                EnrollVerificationCaptureStep_
                                        .this
                                        .mGallery
                                        .set(
                                                vftvt,
                                                EnrollVerificationCaptureStep_
                                                        .this.bestEnrollmentMetricsArray[vftvt]
                                                        .getFingerprintTemplateArray()[0]
                                        );

                            }
                        }
                    }

                    if(null != EnrollVerificationCaptureStep_.this.bestEnrollmentMetric && null != EnrollVerificationCaptureStep_.this.mVerificationFingerprintTemplate) {
                        VerifyFingerprintTemplateVectorTask var3 = new VerifyFingerprintTemplateVectorTask(EnrollVerificationCaptureStep_.this.mContext, EnrollVerificationCaptureStep_.this.mGallery, EnrollVerificationCaptureStep_.this.mInstance);
                        var3.execute(new FingerprintTemplate[]{EnrollVerificationCaptureStep_.this.mVerificationFingerprintTemplate});
                        EnrollVerificationCaptureStep_.this.fingerprintTemplateAlreadyCaptured = true;
                    }
                }

            }
        };
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(layout.base_layout, container, false);
        this.mContext = this.mWizardActivity = (WizardActivity)this.getActivity();
        this.mInstance = this;
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
        EnrollWizard parentActivity = (EnrollWizard)this.getActivity();
        parentActivity.setEnrollmentCallback(this);
        if(null != this.bestEnrollmentMetricsArray) {
            this.setupOnyxFragment();
        }

        int numEnrollCaptureSteps = this.mWizardActivity.getIntent().getIntExtra("num_enroll_capture_steps", 9);
        int enrollStepNum = this.mWizardActivity.getWizard().getCurrentStepPosition();
        View v = this.getView();
        this.mVerificationBitmapFile = new File(this.mContext.getFilesDir(), "verification_bitmap");
        this.m_bUseVerificationFingerprintTemplate = false;
        this.bMatchConfirmed = false;
        this.bitmapToUse = null;
        ProgressLayoutUtil.setProgressLayout(this.mContext, v, numEnrollCaptureSteps, enrollStepNum);
        v.requestLayout();
    }

    public void onMatchFinished(boolean match, float score) {
        if(match) {
            this.bMatchConfirmed = true;
            if(this.m_bUseVerificationFingerprintTemplate) {
                this.bitmapToUse = "verification_bitmap";
            } else {
                this.bitmapToUse = "enrollment_bitmap" + this.mBestStepNum;
            }
        } else {
            this.bMatchConfirmed = false;
            if(this.m_bUseVerificationFingerprintTemplate) {
                this.bitmapToUse = "enrollment_bitmap" + this.mBestStepNum;
            } else {
                this.bitmapToUse = "verification_bitmap";
            }
        }

        this.closeFragment();
        this.done();
    }

    private void closeFragment() {
        if(null != this.mFragment) {
            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
            ft.remove(this.mFragment);
            ft.commit();
        }

    }

    public void onEnrollmentComplete(EnrollmentMetric[] bestEnrollmentMetrics) {
        this.bestEnrollmentMetricsArray = bestEnrollmentMetrics;

        for(int i = 0; i < bestEnrollmentMetrics.length; ++i) {
            if(null != bestEnrollmentMetrics[i] && bestEnrollmentMetrics[i].getMatchScore() > this.bestEnrollmentMetric.getMatchScore()) {
                this.bestEnrollmentMetric = bestEnrollmentMetrics[i];
                this.mBestStepNum = bestEnrollmentMetrics[i].getStepNum();
                Log.d("EnrollVerificationCaptu", "Score = " + bestEnrollmentMetrics[i].getMatchScore() + "StepNum = " + this.mBestStepNum);
            }
        }

        this.setupOnyxFragment();
    }

    private void setupOnyxFragment() {
        this.mFragment = (new OnyxFragmentBuilder(this.mWizardActivity, id.fragment_content)).setEnrollmentMetric(this.bestEnrollmentMetric).setProcessedBitmapCallback(this.mProcessedBitmapCallback).setFingerprintTemplateCallback(this.mFingerprintTemplateCallback).setCaptureAnimationCallback(this.mCaptureAnimationCallback).setUseFingerReticleView().setWizard(this.mWizardActivity.getWizard()).build();
    }
}
