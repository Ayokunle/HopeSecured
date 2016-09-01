package com.example.onyx_enroll_wizard_sample_app.onyx;

/**
 * Created by Ayokunle on 17/07/2016.
 */

import android.os.Bundle;
import com.dft.onyx.enroll.util.imageareas.EnumFinger;
import com.dft.onyx.onyx_enroll_wizard.R.id;
import com.dft.onyx.onyx_enroll_wizard.R.layout;
import com.dft.onyx.wizardroid.WizardFlow;
import com.dft.onyx.wizardroid.WizardFlow.Builder;
import com.dft.onyx.wizardroid.enrollwizard.EnrollFingerSelect;
import com.dft.onyx.wizardroid.enrollwizard.EnrollStepCapture;
import com.dft.onyx.wizardroid.enrollwizard.EnrollVerificationCaptureStep;
import com.dft.onyx.wizardroid.enrollwizard.EnrollVerificationLastStep;
import com.dft.onyx.wizardroid.enrollwizard.EnrollWizard;
import com.dft.onyx.wizardroid.enrollwizard.Enrollable;

public class SelfEnrollWizardActivity_ extends EnrollWizard implements Enrollable {
    private int layoutResourceId;
    private String onyxLicenseKey;
    private String uid = null;
    private int numStepsBeforeEnrollStepCapture = 0;

    public SelfEnrollWizardActivity_() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        this.setContentViewId(layout.base_layout);
        int numCaptureSteps = this.getIntent().getExtras().getInt("num_enroll_capture_steps");
        this.setFingerToEnroll(this.getFingerToEnroll());
        Builder builder = (new Builder())
                .setActivity(this)
                .setContainerId(id.step_container)
                .addStep(EnrollFingerSelect.class);

        for(int i = 0; i < numCaptureSteps; ++i) {
            builder.addStep(EnrollStepCapture.class);
            this.setNumStepsBeforeEnrollStepCapture(1);
        }

        this.setEnrollWizardFlow(builder
                .addStep(EnrollVerificationCaptureStep_.class)
                .addStep(EnrollVerificationLastStep_.class)
                .create());
        super.onCreate(savedInstanceState);
    }

    public EnumFinger getFingerToEnroll() {
        return this.mFingerToEnroll;
    }

    public void setFingerToEnroll(EnumFinger fingerLocation) {
        this.mFingerToEnroll = fingerLocation;
    }

    public int getContentViewId() {
        return this.layoutResourceId;
    }

    public void setContentViewId(int layoutResourceId) {
        this.layoutResourceId = layoutResourceId;
    }

    public String getOnyxLicenseKey() {
        return this.onyxLicenseKey;
    }

    public void setOnyxLicenseKey(String onyxLicenseKey) {
        this.onyxLicenseKey = onyxLicenseKey;
    }

    public WizardFlow getEnrollWizardFlow() {
        return this.mEnrollWizardFlow;
    }

    public void setEnrollWizardFlow(WizardFlow flow) {
        this.mEnrollWizardFlow = flow;
    }

    public String getUID() {
        return this.uid;
    }

    public void setUID(String uid) {
        this.uid = uid;
    }

    public int getNumStepsBeforeEnrollStepCapture() {
        return this.numStepsBeforeEnrollStepCapture;
    }

    public void setNumStepsBeforeEnrollStepCapture(int numStepsBefore) {
        this.numStepsBeforeEnrollStepCapture = numStepsBefore;
    }
}

