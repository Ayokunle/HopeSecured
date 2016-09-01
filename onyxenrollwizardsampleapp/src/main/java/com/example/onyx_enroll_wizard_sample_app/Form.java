package com.example.onyx_enroll_wizard_sample_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dft.onyx.enroll.util.Consts;
import com.dft.onyx.wizardroid.enrollwizard.EnrollWizardBuilder;
import com.dft.onyx.wizardroid.enrollwizard.SelfEnrollIntentHelper;

import java.io.File;

/**
 * Created by Ayokunle on 01-Aug-15.
 */
public class Form extends Activity {

    private static final int ENROLL_REQUEST_CODE = 20342;
    int counter =0;
    private static final String TAG = "OnyxEnrollWizardSample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form);
    }


    static public Intent getSelfEnrollIntent(Context context, String onyxLicenseKey) {
        return (new EnrollWizardBuilder())
                .setLicenseKey(onyxLicenseKey)
                .setUseOnyxGuide(true, true, true)
                .setUseSelfEnroll(true)
                .build(context);
    }

    public void submit(final View v) {
        EditText fname = (EditText) findViewById(R.id.fname);
        EditText lname = (EditText) findViewById(R.id.lname);


        /*if (fingerprintExists()) {
            Log.v(TAG, "fp already enrolled");
            Intent data;
            data = new Intent();
            data.putExtra("fname", fname.getText().toString());
            data.putExtra("lname", lname.getText().toString());
            setResult(RESULT_OK, data);
            finish();
        }else {
            Log.v(TAG, "No fp enrolled");
            Intent onyxSelfEnrollIntent = getSelfEnrollIntent(this,
                    getString(R.string.onyx_license));
            startActivityForResult(onyxSelfEnrollIntent, ENROLL_REQUEST_CODE);
        }*/

        Intent onyxSelfEnrollIntent = getSelfEnrollIntent(this,
                getString(R.string.onyx_license));
        startActivityForResult(onyxSelfEnrollIntent, ENROLL_REQUEST_CODE);

//        Intent intent = new Intent(getBaseContext(), Enroll.class);
//        //startActivity(intent);
//        startActivity(intent);
//        finish();
    }

    static File mEnrolledFile = null;

    private boolean fingerprintExists() {
        mEnrolledFile = getFileStreamPath(Consts.ENROLLED_ENROLLMENT_METRIC_FILENAME);
        if (mEnrolledFile.exists()) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ENROLL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.e(TAG, "Form - In onActivityResult()");

                data = new Intent();
                EditText fname = (EditText) findViewById(R.id.fname);
                EditText lname = (EditText) findViewById(R.id.lname);

                data.putExtra("fname", fname.getText().toString());
                data.putExtra("lname", lname.getText().toString());
                setResult(RESULT_OK, data);
                Log.e(TAG, "Sending");
                finish();

//                counter++;
//                if(counter < 1){
//                    // First, generate an intent for the OnyxGuideActivity
//                    Intent onyxSelfEnrollIntent = new SelfEnrollIntentHelper().getSelfEnrollIntent(this,
//                            getString(R.string.onyx_license));
//                    // Then start it for result
//                    startActivityForResult(onyxSelfEnrollIntent, ENROLL_REQUEST_CODE);
//                }
//                Toast.makeText(this, getResources().getString(R.string.toast_enroll_success_message), Toast.LENGTH_LONG).show();
            }else {
                // Did not successfully enroll
                Toast.makeText(this, getResources().getString(R.string.toast_enroll_fail_message), Toast.LENGTH_LONG).show();
            }
        }
    }
}