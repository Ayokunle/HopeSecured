package com.example.onyx_enroll_wizard_sample_app.onyx;

/**
 * Created by Ayokunle on 17/07/2016.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.dft.onyx.FingerprintTemplate;
import com.dft.onyx.enroll.ui.TipsActivity;
import com.dft.onyx.enroll.util.ClearEnrollmentDataUtil;
import com.dft.onyx.enroll.util.ConvertBitmapToFile;
import com.dft.onyx.enroll.util.EnrolledFingerprintDetails;
import com.dft.onyx.enroll.util.EnrollmentMetric;
import com.dft.onyx.enroll.util.RestoreBitmap;
import com.dft.onyx.enroll.util.SaveEnrollmentMetricTask.SaveEnrollmentMetricCallback;
import com.dft.onyx.onyx_enroll_wizard.R.color;
import com.dft.onyx.onyx_enroll_wizard.R.string;
import com.dft.onyx.wizardroid.WizardActivity;
import com.dft.onyx.wizardroid.WizardStep;
import com.dft.onyx.wizardroid.enrollwizard.EnrollWizard;
import com.example.onyx_enroll_wizard_sample_app.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class EnrollVerificationLastStep_ extends WizardStep {
    protected static final String TAG = "EnrollVerificationLastStep_";
    private static final int START_OVER_REQUEST_CODE = 1481912;
    private Context mContext;
    private WizardActivity mWizardActivity;
    @ContextVariable
    private EnrollmentMetric bestEnrollmentMetric;
    @ContextVariable
    private EnrollmentMetric[] bestEnrollmentMetricsArray;
    @ContextVariable
    private double mVerificationFingerprintFocusQuality;
    @ContextVariable
    private boolean m_bUseVerificationFingerprintTemplate;
    @ContextVariable
    private String bitmapToUse;
    @ContextVariable
    private boolean bMatchConfirmed;
    private ImageView mFingerprintView1;
    private ImageView mFingerprintView2;
    @ContextVariable
    int mBestStepNum;

    public EnrollVerificationLastStep_() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.confirm_or_fail_layout, container, false);
        this.mContext = this.mWizardActivity = (WizardActivity)this.getActivity();
        if(null != this.mWizardActivity.getActionBar()) {
            this.mWizardActivity.getActionBar().hide();
        }

        this.mFingerprintView1 = (ImageView)v.findViewById(R.id.confirm_or_fail_fingerprint_image_1);
        this.mFingerprintView2 = (ImageView)v.findViewById(R.id.confirm_or_fail_fingerprint_image_2);
        return v;
    }

    public void onResume() {
        super.onResume();
        View v = this.getView();

        EnrolledFingerprintDetails efd = EnrolledFingerprintDetails.getInstance();
        ArrayList tempFingerArrayList = new ArrayList();

        for(int fingerprintTemplates = 0; fingerprintTemplates < EnrollVerificationLastStep_.this.bestEnrollmentMetricsArray.length; ++fingerprintTemplates) {
            if(null != EnrollVerificationLastStep_.this.bestEnrollmentMetricsArray[fingerprintTemplates] && null != EnrollVerificationLastStep_.this.bestEnrollmentMetricsArray[fingerprintTemplates].getFingerprintTemplateArray()[0]) {
                tempFingerArrayList.add(EnrollVerificationLastStep_.this.bestEnrollmentMetricsArray[fingerprintTemplates].getFingerprintTemplateArray()[0]);
            }
        }

        FingerprintTemplate[] var7 = new FingerprintTemplate[tempFingerArrayList.size()];

        for(int i = 0; i < tempFingerArrayList.size(); ++i) {
            var7[i] = (FingerprintTemplate)tempFingerArrayList.get(i);
        }
        Log.e("Enroll - Verify", "Running asyn");
        VerifyTask task = new VerifyTask(var7);
        try {
            task.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("Enroll - Verify", "Asyn Done");

//        EnrollVerificationLastStep_.this.bestEnrollmentMetric.setFingerprintTemplateArray(var7);
//        efd.saveEnrollmentMetric(EnrollVerificationLastStep_.this.mContext, EnrollVerificationLastStep_.this.bestEnrollmentMetric, (SaveEnrollmentMetricCallback)null);
        EnrollVerificationLastStep_.this.done();
        Log.e("Enroll - Verify", "this.done()");

        /*
        Bitmap bitmap1 = (new RestoreBitmap()).restoreBitmapFile(this.mContext, "enrollment_bitmap" + this.mBestStepNum);
        if(null != bitmap1) {
            this.mFingerprintView1.setImageBitmap(bitmap1);
            TextView verificationBitmap = (TextView)v.findViewById(id.confirm_or_fail_fingerprint_your_image_1);
            verificationBitmap.setText(this.getResources().getString(string.enrollment_fingerprint_image));
        }

        Bitmap verificationBitmap1 = (new RestoreBitmap()).restoreBitmapFile(this.getActivity(), "verification_bitmap");
        EnrollWizard ew = (EnrollWizard)this.getActivity();
        if(null != ew.getPreprocessedFile()) {
            (new ConvertBitmapToFile(verificationBitmap1, ew.getPreprocessedFile())).convertBitmapToFile(ew.getCompressFormat());
        }

        TextView tv;
        if(null != verificationBitmap1) {
            this.mFingerprintView2.setImageBitmap(verificationBitmap1);
            tv = (TextView)v.findViewById(id.confirm_or_fail_fingerprint_your_image_2);
            tv.setText(this.getResources().getString(string.verification_fingerprint_image));
        }

        tv = (TextView)v.findViewById(id.match_status_textView);
        Button tipsButton = (Button)v.findViewById(id.tips_button);
        Button confirmOrCancelButton = (Button)v.findViewById(id.confirm_or_cancel_button);
        if(this.bMatchConfirmed) {
            tv.setText(this.getResources().getText(string.match_confirmed));
            tv.setTextColor(this.mWizardActivity.getResources().getColor(color.green));
            confirmOrCancelButton.setText(this.getResources().getText(string.enroll_wizard_use_this_fingerprint));
            tipsButton.setVisibility(View.VISIBLE);
        } else {
            tv.setText(this.getResources().getText(string.match_failed));
//            tv.setTextColor(-65536);
            confirmOrCancelButton.setText(this.getResources().getText(string.cancel_enrollment));
            tipsButton.setVisibility(View.INVISIBLE);
        }

        confirmOrCancelButton.setTextColor(-1);
        confirmOrCancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(EnrollVerificationLastStep_.this.bMatchConfirmed) {
                    File mEnrolledEnrollmentMetricFile = new File(EnrollVerificationLastStep_.this.mContext.getFilesDir(), "enrolled_enrollment_metric.bin");
                    if(mEnrolledEnrollmentMetricFile.exists()) {
                        mEnrolledEnrollmentMetricFile.delete();
                    }

                    EnrolledFingerprintDetails efd = EnrolledFingerprintDetails.getInstance();
                    ArrayList tempFingerArrayList = new ArrayList();

                    for(int fingerprintTemplates = 0; fingerprintTemplates < EnrollVerificationLastStep_.this.bestEnrollmentMetricsArray.length; ++fingerprintTemplates) {
                        if(null != EnrollVerificationLastStep_.this.bestEnrollmentMetricsArray[fingerprintTemplates] && null != EnrollVerificationLastStep_.this.bestEnrollmentMetricsArray[fingerprintTemplates].getFingerprintTemplateArray()[0]) {
                            tempFingerArrayList.add(EnrollVerificationLastStep_.this.bestEnrollmentMetricsArray[fingerprintTemplates].getFingerprintTemplateArray()[0]);
                        }
                    }

                    FingerprintTemplate[] var7 = new FingerprintTemplate[tempFingerArrayList.size()];

                    for(int i = 0; i < tempFingerArrayList.size(); ++i) {
                        var7[i] = (FingerprintTemplate)tempFingerArrayList.get(i);
                    }

                    EnrollVerificationLastStep_.this.bestEnrollmentMetric.setFingerprintTemplateArray(var7);
                    efd.saveEnrollmentMetric(EnrollVerificationLastStep_.this.mContext, EnrollVerificationLastStep_.this.bestEnrollmentMetric, (SaveEnrollmentMetricCallback)null);
                    EnrollVerificationLastStep_.this.done();
                } else {
                    EnrollVerificationLastStep_.this.getActivity().finish();
                }

                EnrollVerificationLastStep_.this.bestEnrollmentMetric = null;
                ClearEnrollmentDataUtil.clearEnrollmentData(EnrollVerificationLastStep_.this.mWizardActivity);
            }
        });
        tipsButton.setTextColor(-1);
        tipsButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EnrollVerificationLastStep_.this.startActivity(new Intent(EnrollVerificationLastStep_.this.getActivity(), TipsActivity.class));
            }
        });
        Button startOverButton = (Button)v.findViewById(id.start_over_button);
        startOverButton.setTextColor(-1);
        startOverButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent originalIntent = EnrollVerificationLastStep_.this.mWizardActivity.getIntent().setFlags(33554432);
                EnrollVerificationLastStep_.this.startActivity(originalIntent);
                EnrollVerificationLastStep_.this.mWizardActivity.finish();
            }
        });
        */
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1481912 && -1 == resultCode && null != this.mWizardActivity.getCallingActivity()) {
            if(null == this.mWizardActivity.getParent()) {
                this.mWizardActivity.setResult(-1, this.mWizardActivity.getIntent());
            } else {
                this.mWizardActivity.getParent().setResult(-1, this.mWizardActivity.getIntent());
            }
        }

        Log.e("Enroll - Verify", "on ActivityResult() -> finish()");
        this.mWizardActivity.finish();
    }

    public class VerifyTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        String line = null;
        InputStream is = null;
        String result;
        FingerprintTemplate [] fingerprintTemplate;
        String responseStr;

        VerifyTask(FingerprintTemplate [] fingerprintTemplate){
            this.fingerprintTemplate = fingerprintTemplate;
        }

        @Override
        protected void onPreExecute() {
//			progressDialog = ProgressDialog.show(mContext, "", "");
//			progressDialog.setCancelable(false);
//			progressDialog.setContentView(R.layout.progressdialog);
//			progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                ArrayList<String> fptList = new ArrayList<String>();

                for (int i = 0; i < fingerprintTemplate.length; i++) {
                    FingerprintTemplate ft = fingerprintTemplate[i];
                    Log.e("Enroll - Verify", "ft: " +  i);
                    if (null != ft) {
						/*
						try {
							System.out.println("indentify: " +
									new IdentifyFingerprint().identifyFingerprint(
											em.getFingerprintTemplateVector(), ft));

							FileOutputStream enrollStream =
									mContext.openFileOutput("enrolled_template_"+i+".bin", MODE_WORLD_WRITEABLE);
							ObjectOutputStream oos = new ObjectOutputStream(enrollStream);
							oos.writeObject(ft);
							oos.close();
						} catch (Exception e) {
							Log.e(TAG, e.getMessage());
						}
						*/

                        byte[] bytes = ft.getData();
                        String bytesString = Base64.encodeToString(bytes, Base64.URL_SAFE | Base64.NO_WRAP);
                        fptList.add(bytesString);
                    }
                }
                String[] fptArray = fptList.toArray(new String[0]);

                JSONObject enrollmentJSON = new JSONObject();
                enrollmentJSON.put("fpt_size", fptList.size());
                enrollmentJSON.put("templates", new JSONArray(fptList));

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("templates", enrollmentJSON.toString()));

                try {
                    long startTime = System.nanoTime();
                    HttpParams para = new BasicHttpParams();
                    //this how tiny it might seems, is actually absolutly needed. otherwise http client lags for 2sec.
                    HttpProtocolParams.setVersion(para, HttpVersion.HTTP_1_1);
                    //HttpClient httpClient = new DefaultHttpClient(params);
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(
                            "http://139.59.175.91:8080/verify");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    long endTime = System.nanoTime();
                    System.out.println("It took " + (endTime - startTime) / 1000000000.0 + " seconds.");
                    Log.e("pass 1", "Connection success ");
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                }

                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    is.close();
                    result = sb.toString();
                    System.out.println("Result: " + result);
                    //Log.e("pass 2", "Result: " + result);
                } catch (Exception e) {
                    Log.e("Fail 2", e.toString());
                }
            } catch (Exception e) {
                Log.e("Fail 1", e.toString());
            }

             /*
            EnrollmentMetric em =
                    LoadEnrolledEnrollmentMetric.loadEnrolledTemplateIfExists(mContext.getApplicationContext());
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);

            if (null != em) {

                FingerprintTemplate[] fpta = em.getFingerprintTemplateArray();
                ArrayList<String> fptList = new ArrayList<String>();

                for (int i = 0; i < fpta.length; i++) {
                    FingerprintTemplate ft = fpta[i];
                    if (null != ft) {
                        try {
                            Log.e(TAG, "core.verify: " + core.verify(ft, mCurrentTemplate));
                            System.out.println("indentify: " +
                                    new IdentifyFingerprint().identifyFingerprint(
                                            em.getFingerprintTemplateVector(), mCurrentTemplate));
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }
            }
            */

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//			progressDialog.dismiss();
            //progressBar.setVisibility(View.INVISIBLE);
            /*
            AlertDialog.Builder builder = new AlertDialog.Builder(mWizardActivity);
            builder.setMessage("Result: "+responseStr)
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
//                            finish();
                        }
                    })
                    .setTitle("Result");

            AlertDialog dialog = builder.create();
            dialog.show();
            */
        }

    }
}
