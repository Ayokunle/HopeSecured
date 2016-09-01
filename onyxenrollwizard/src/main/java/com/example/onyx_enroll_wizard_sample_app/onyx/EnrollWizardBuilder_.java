package com.example.onyx_enroll_wizard_sample_app.onyx;

/**
 * Created by Ayokunle on 17/07/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import com.dft.onyx.enroll.util.imageareas.EnumFinger;
import com.dft.onyx.wizardroid.enrollwizard.SelfEnrollWizardActivity;
import java.io.File;

public class EnrollWizardBuilder_ {
    int mNumCapturesPerScale = 2;
    int mNumEnrollScales = 3;
    float mMaxReticleScale = 1.0F;
    float mMinReticleScale = 0.8F;
    Bundle mBundle = null;
    private String mLicenseKey = null;
    private File mRawFile;
    private File mPreprocessedFile;
    private CompressFormat mCompressFormat;
    private boolean mUseSelfEnroll = true;
    private boolean mShowOnyxGuide = true;
    private boolean mEnrollAfterGuide = true;
    private boolean mIgnoreGuidePrefs = false;
    private String mUID = null;
    private EnumFinger mFingerToEnroll = null;
    public static final String NUM_ENROLL_CAPTURES_PER_SCALE = "num_enroll_captures_per_scale";
    public static final String NUM_ENROLL_SCALES = "num_enroll_scales";
    public static final String NUM_ENROLL_CAPTURE_STEPS = "num_enroll_capture_steps";
    public static final String MAX_RETICLE_SCALE = "max_reticle_scale";
    public static final String MIN_RETICLE_SCALE = "min_reticle_scale";
    public static final String RAW_FILE = "raw_file";
    public static final String PREPROCESSED_FILE = "preprocessed_file";
    public static final String COMPRESS_FORMAT = "compress_format";
    public static final String SELF_ENROLL = "self_enroll";
    public static final String SHOW_ONYX_GUIDE = "show_onyx_guide";
    public static final String ENROLL_AFTER_GUIDE = "enroll_after_guide";
    public static final String IGNORE_GUIDE_PREFS = "ignore_guide_prefs";
    public static final String UID = "UID";
    public static final String FINGER_TO_ENROLL = "finger_to_enroll";

    public EnrollWizardBuilder_() {
    }

    public EnrollWizardBuilder_ setLicenseKey(String licenseKey) {
        this.mLicenseKey = licenseKey;
        return this;
    }

    public EnrollWizardBuilder_ setNumEnrollCapturesPerScale(int numCapturesPerScale) {
        this.mNumCapturesPerScale = numCapturesPerScale;
        return this;
    }

    public EnrollWizardBuilder_ setNumEnrollScales(int numEnrollScales) {
        this.mNumEnrollScales = numEnrollScales;
        return this;
    }

    public EnrollWizardBuilder_ setMaxReticleScale(float maxReticleScale) {
        this.mMaxReticleScale = maxReticleScale;
        return this;
    }

    public EnrollWizardBuilder_ setMinReticleScale(float minReticleScale) {
        this.mMinReticleScale = minReticleScale;
        return this;
    }

    public EnrollWizardBuilder_ setRawFile(File rawFile) {
        this.mRawFile = rawFile;
        return this;
    }

    public EnrollWizardBuilder_ setPreprocessedFile(File preprocessedFile, CompressFormat compressFormat) {
        this.mPreprocessedFile = preprocessedFile;
        return this;
    }

    public EnrollWizardBuilder_ setUseSelfEnroll(boolean useSelfEnroll) {
        this.mUseSelfEnroll = useSelfEnroll;
        return this;
    }

    public EnrollWizardBuilder_ setUID(String uid) {
        this.mUID = uid;
        return this;
    }

    public EnrollWizardBuilder_ setUseOnyxGuide(boolean showOnyxGuide, boolean enrollAfterGuide, boolean ignoreGuidePrefs) {
        this.mShowOnyxGuide = showOnyxGuide;
        this.mEnrollAfterGuide = enrollAfterGuide;
        this.mIgnoreGuidePrefs = ignoreGuidePrefs;
        return this;
    }

    public EnrollWizardBuilder_ setFingerToEnroll(EnumFinger fingerToEnroll) {
        this.mFingerToEnroll = fingerToEnroll;
        return this;
    }

    public Bundle getBundle() {
        this.mBundle = new Bundle();
        if(null != this.mLicenseKey) {
            this.mBundle.putString("onyx_licence_key", this.mLicenseKey);
        }

        this.mBundle.putInt("num_enroll_capture_steps", this.mNumCapturesPerScale * this.mNumEnrollScales);
        this.mBundle.putInt("num_enroll_captures_per_scale", this.mNumCapturesPerScale);
        this.mBundle.putInt("num_enroll_scales", this.mNumEnrollScales);
        this.mBundle.putFloat("max_reticle_scale", this.mMaxReticleScale);
        this.mBundle.putFloat("min_reticle_scale", this.mMinReticleScale);
        this.mBundle.putSerializable("raw_file", this.mRawFile);
        this.mBundle.putSerializable("preprocessed_file", this.mPreprocessedFile);
        this.mBundle.putSerializable("compress_format", this.mCompressFormat);
        this.mBundle.putBoolean("self_enroll", this.mUseSelfEnroll);
        this.mBundle.putBoolean("show_onyx_guide", this.mShowOnyxGuide);
        this.mBundle.putBoolean("enroll_after_guide", this.mEnrollAfterGuide);
        this.mBundle.putBoolean("ignore_guide_prefs", this.mIgnoreGuidePrefs);
        this.mBundle.putString("UID", this.mUID);
        this.mBundle.putSerializable("finger_to_enroll", this.mFingerToEnroll);
        return this.mBundle;
    }

    public Intent build(Context context) {
        Intent enrollWizardIntent = new Intent();
        if(this.mUseSelfEnroll) {
            enrollWizardIntent.setClass(context, SelfEnrollWizardActivity_.class).addFlags(67108864).putExtras(this.getBundle());
        }

        return enrollWizardIntent;
    }
}
