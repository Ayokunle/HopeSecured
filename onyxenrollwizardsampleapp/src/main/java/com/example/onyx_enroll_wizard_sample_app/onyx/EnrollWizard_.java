package com.example.onyx_enroll_wizard_sample_app.onyx;

import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.util.Log;
import com.dft.onyx.enroll.util.ClearEnrollmentDataUtil;
import com.dft.onyx.enroll.util.EnrollmentMetric;
import com.dft.onyx.enroll.util.GCUtil;
import com.dft.onyx.enroll.util.imageareas.EnumFinger;
import com.dft.onyx.verify.EnrollmentScaleVerifier.EnrollmentScaleAnalyzerCallback;
import com.dft.onyx.wizardroid.WizardActivity;
import com.dft.onyx.wizardroid.WizardFlow;
import com.dft.onyx.wizardroid.enrollwizard.Enrollable;
import com.example.onyx_enroll_wizard_sample_app.R;

import java.io.File;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class EnrollWizard_ extends WizardActivity implements EnrollmentScaleAnalyzerCallback, Enrollable {
    private static final String TAG = "EnrollWizard";
    private EnrollWizard_.EnrollmentCallback mEnrollmentCallback;
    private final Lock mLock = new ReentrantLock();
    private final Condition mCallbackRegistered;
    private boolean mWasSignalled;
    private int mNumCapturesPerScale;
    private int mNumEnrollScales;
    private File mRawFile;
    private File mPreprocessedFile;
    private CompressFormat mCompressFormat;
    private boolean mUseSelfEnroll;
    private boolean mShowOnyxGuide;
    private boolean mIgnoreGuidePrefs;
    private static final int ONYX_GUIDE_REQUEST_CODE = 681987;
    private static EnrollmentMetric[] mBestOfReticlesArray;
    protected WizardFlow mEnrollWizardFlow;
    protected EnumFinger mFingerToEnroll;

    public EnrollWizard_() {
        this.mCallbackRegistered = this.mLock.newCondition();
        this.mWasSignalled = false;
        this.mNumCapturesPerScale = 0;
        this.mNumEnrollScales = 0;
        this.mUseSelfEnroll = true;
        this.mShowOnyxGuide = true;
        this.mIgnoreGuidePrefs = false;
        this.mFingerToEnroll = null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null != this.getActionBar()) {
            this.getActionBar().setDisplayHomeAsUpEnabled(true);
            this.getActionBar().setDisplayShowTitleEnabled(false);
        }

        Intent enrollWizardBuilderIntent = this.getIntent();
        this.mNumCapturesPerScale = enrollWizardBuilderIntent.getIntExtra("num_enroll_captures_per_scale", 3);
        this.mNumEnrollScales = enrollWizardBuilderIntent.getIntExtra("num_enroll_scales", 3);
        if(enrollWizardBuilderIntent.hasExtra("raw_file")) {
            this.setRawFile((File)enrollWizardBuilderIntent.getSerializableExtra("raw_file"));
        }

        if(enrollWizardBuilderIntent.hasExtra("preprocessed_file")) {
            this.setPreprocessedFile((File)enrollWizardBuilderIntent.getSerializableExtra("preprocessed_file"));
        }

        if(enrollWizardBuilderIntent.hasExtra("compress_format")) {
            this.setCompressFormat((CompressFormat)enrollWizardBuilderIntent.getSerializableExtra("compress_format"));
        }

        if(enrollWizardBuilderIntent.hasExtra("self_enroll")) {
            this.setUseSelfEnroll(enrollWizardBuilderIntent.getBooleanExtra("self_enroll", true));
        }

        if(enrollWizardBuilderIntent.hasExtra("show_onyx_guide")) {
            this.setShowOnyxGuide(enrollWizardBuilderIntent.getBooleanExtra("show_onyx_guide", true));
        }

        if(this.isShowOnyxGuide()) {
            this.startActivityForResult((
                    new OnyxGuideIntentHelper_())
                    .getOnyxGuideIntent(this, this.mIgnoreGuidePrefs), 681987);
        }

        mBestOfReticlesArray = new EnrollmentMetric[this.mNumEnrollScales];
        this.setContentView(R.layout.enroll_wizard);
        this.getWindow().addFlags(128);
        GCUtil.gc();
    }

    public void onSetup(WizardFlow flow) {
        super.onSetup(this.getEnrollWizardFlow());
    }

    public void onWizardDone() {
        if(null != this.getCallingActivity()) {
            Intent data = new Intent();
            if(null == this.getParent()) {
                this.setResult(-1, data);
            } else {
                this.getParent().setResult(-1, data);
            }
        }

        this.finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 681987 && resultCode == 0) {
            this.finish();
        }

    }

    public void onAnalysisComplete(EnrollmentMetric bestEnrollmentMetric, int stepNum) {
        mBestOfReticlesArray[stepNum / this.mNumCapturesPerScale - 1] = bestEnrollmentMetric;
        if(null != this.mEnrollmentCallback) {
            this.mEnrollmentCallback.onEnrollmentComplete(mBestOfReticlesArray);
            Log.d("EnrollWizard", "Scores done, enrollmentComplete...");
        }

    }

    public void setEnrollmentCallback(EnrollWizard_.EnrollmentCallback ec) {
        this.mEnrollmentCallback = ec;
        this.signalCallbackRegistered();
    }

    public void signalCallbackRegistered() {
        this.mLock.lock();

        try {
            this.mWasSignalled = true;
            this.mCallbackRegistered.signal();
        } finally {
            this.mLock.unlock();
        }

    }

    public void awaitCallbackRegistered() throws InterruptedException {
        this.mLock.lock();

        try {
            if(!this.mWasSignalled) {
                this.mCallbackRegistered.await();
            }

            this.mWasSignalled = false;
        } finally {
            this.mLock.unlock();
        }

    }

    public void onDestroy() {
        super.onDestroy();
        ClearEnrollmentDataUtil.clearEnrollmentData(this);
    }

    public File getRawFile() {
        return this.mRawFile;
    }

    public void setRawFile(File mRawFile) {
        this.mRawFile = mRawFile;
    }

    public File getPreprocessedFile() {
        return this.mPreprocessedFile;
    }

    public void setPreprocessedFile(File mPreprocessedFile) {
        this.mPreprocessedFile = mPreprocessedFile;
    }

    public boolean isUseSelfEnroll() {
        return this.mUseSelfEnroll;
    }

    public void setUseSelfEnroll(boolean mUseSelfEnroll) {
        this.mUseSelfEnroll = mUseSelfEnroll;
    }

    public CompressFormat getCompressFormat() {
        return this.mCompressFormat;
    }

    public void setCompressFormat(CompressFormat mCompressFormat) {
        this.mCompressFormat = mCompressFormat;
    }

    public boolean isShowOnyxGuide() {
        return this.mShowOnyxGuide;
    }

    public void setShowOnyxGuide(boolean mShowGuide) {
        this.mShowOnyxGuide = mShowGuide;
    }

    public interface EnrollmentCallback {
        void onEnrollmentComplete(EnrollmentMetric[] var1);
    }
}
