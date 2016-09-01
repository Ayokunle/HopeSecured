package com.example.onyx_enroll_wizard_sample_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dft.onyx.FingerprintTemplate;
import com.dft.onyx.core;
import com.dft.onyx.enroll.util.EnrollmentMetric;
import com.dft.onyxcamera.licensing.License;
import com.dft.onyxcamera.licensing.LicenseException;
import com.dft.onyxcamera.ui.CaptureConfiguration;
import com.dft.onyxcamera.ui.CaptureConfigurationBuilder;
import com.dft.onyxcamera.ui.CaptureMetrics;
import com.dft.onyxcamera.ui.OnyxFragment;
import com.dft.onyxcamera.ui.CaptureConfiguration.Flip;
import com.dft.onyxcamera.ui.OnyxFragment.FingerprintTemplateCallback;
import com.dft.onyxcamera.ui.OnyxFragment.ProcessedBitmapCallback;
import com.dft.onyxcamera.ui.OnyxFragment.WsqCallback;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.OpenCVLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class VerifyFinger extends Activity {

    private final static String TAG = VerifyFinger.class.getSimpleName();
    private final static String ENROLL_FILENAME = "enrolled_template.bin";

    private ImageView mFingerprintView;
    private Animation mFadeIn;
    private Animation mFadeOut;
    private OnyxFragment mFragment;
    private FingerprintTemplate mCurrentTemplate = null;
    private double mCurrentFocusQuality = 0.0;
    private FingerprintTemplate mEnrolledTemplate = null;
    ProgressBar progressBar;
    TextView tv;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Unable to load OpenCV!");
        } else {
            Log.i(TAG, "OpenCV loaded successfully");
            core.initOnyx();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
    }

    private void setupViews() {
        //setContentView(R.layout.base_layout);

        setContentView(R.layout.base_layout_verify);
        mFragment = (OnyxFragment) getFragmentManager().findFragmentById(R.id.onyx_frag);
        CaptureConfiguration captureConfig = new CaptureConfigurationBuilder()
                .setProcessedBitmapCallback(mProcessedCallback)
                .setWsqCallback(mWsqCallback)
                .setFingerprintTemplateCallback(mTemplateCallback)
                .setShouldInvert(true)
                .setFlip(Flip.VERTICAL)
                .buildCaptureConfiguration();
        mFragment.setCaptureConfiguration(captureConfig);
        mFragment.setErrorCallback(mErrorCallback);
        mFragment.startOneShotAutoCapture();

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);

        tv = (TextView)findViewById(R.id.textView);
        tv.setVisibility(View.INVISIBLE);

        createFadeInAnimation();
        createFadeOutAnimation();

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mFingerprintView = new ImageView(this);
        addContentView(mFingerprintView, layoutParams);
    }

    private void createFadeInAnimation() {
        mFadeIn = new AlphaAnimation(0.0f, 1.0f);
        mFadeIn.setDuration(500);
        mFadeIn.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                new CountDownTimer(1000, 1000) {

                    @Override
                    public void onFinish() {

                        mFingerprintView.startAnimation(mFadeOut);
                    }

                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                }.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                mFingerprintView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void createFadeOutAnimation() {
        mFadeOut = new AlphaAnimation(1.0f, 0.0f);
        mFadeOut.setDuration(500);
        mFadeOut.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                mFingerprintView.setVisibility(View.INVISIBLE);

                if (mEnrolledTemplate == null) {
                    //createEnrollQuestionDialog();
                } else {
                    //mFragment.startOneShotAutoCapture();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }

    private void createEnrollQuestionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.enroll_title);
        String enrollQuestion = getResources().getString(R.string.enroll_question);
        builder.setMessage(enrollQuestion + "\n\n" +
                "(Quality is " + (int) mCurrentFocusQuality + ")");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                enrollCurrentTemplate();
                dialog.dismiss();

                finish();
                //mFragment.startOneShotAutoCapture();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mFragment.startOneShotAutoCapture();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void enrollCurrentTemplate() {
        mEnrolledTemplate = mCurrentTemplate;

        deleteEnrolledTemplateIfExists();

        try {
            FileOutputStream enrollStream = this.openFileOutput(ENROLL_FILENAME, MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(enrollStream);
            oos.writeObject(mEnrolledTemplate);
            oos.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void deleteEnrolledTemplateIfExists() {
        File enrolledFile = getFileStreamPath(ENROLL_FILENAME);
        if (enrolledFile.exists()) {
            enrolledFile.delete();
        }
    }

    private ProcessedBitmapCallback mProcessedCallback = new ProcessedBitmapCallback() {

        @Override
        /**
         * This method handles the ProcessedBitmapReady event.
         * @param processedBitmap the Bitmap containing the processed fingerprint image.
         * @param metrics the metrics associated with this fingerprint image capture.
         */
        public void onProcessedBitmapReady(Bitmap processedBitmap, CaptureMetrics metrics) {
            mCurrentFocusQuality = metrics.getFocusQuality();
            mFingerprintView.setImageBitmap(processedBitmap);
            mFingerprintView.startAnimation(mFadeIn);
        }

    };

    private WsqCallback mWsqCallback = new WsqCallback() {

        @Override
        /**
         * This method handles the WsqReady event.
         * @param wsqData a byte array containing the compressed WSQ data of the fingerprint image.
         * @param metrics the metrics associated with this fingerprint image capture.
         */
        public void onWsqReady(byte[] wsqData, CaptureMetrics metrics) {
            // TODO Do something with WSQ data
            Log.d(TAG, "NFIQ: " + metrics.getNfiqMetrics().getNfiqScore() + ", MLP: " + metrics.getNfiqMetrics().getMlpScore());
        }

    };

    private FingerprintTemplateCallback mTemplateCallback = new FingerprintTemplateCallback() {

        @Override
        /**
         * This method handles the FingerprintTemplateReady event.
         * @param fingerprintTemplate the returned fingerprint template used for matching purposes.
         */
        public void onFingerprintTemplateReady(FingerprintTemplate fingerprintTemplate) {

            mCurrentTemplate = fingerprintTemplate;
            Log.d(TAG, "Template quality: " + mCurrentTemplate.getQuality());

            VerifyTask verifyTask = new VerifyTask(fingerprintTemplate.getData());
            try {
                verifyTask.execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*
            try {
                core.verify(mCurrentTemplate, mCurrentTemplate);
            } catch (Exception e) {

            }*/
        }
    };

    private OnyxFragment.ErrorCallback mErrorCallback = new OnyxFragment.ErrorCallback() {

        @Override
        /**
         * This method handles the errors that can be produced by the OnyxFragment.
         * @param error the specific error enumeration that occurred.
         * @param errorMessage the associated error message.
         * @param exception if not null, this is the exception that occurred.
         */
        public void onError(Error error, String errorMessage, Exception exception) {
            switch (error) {
                case AUTOFOCUS_FAILURE:
                    mFragment.startOneShotAutoCapture();
                    break;
                default:
                    Log.d(TAG, "Error occurred: " + errorMessage);
                    break;
            }
        }

    };

    @Override
    /**
     * This method handles the Android System onResume() callback.
     */
    public void onResume() {
        super.onResume();
        License lic = License.getInstance(this);
        try {
            lic.validate(getString(R.string.onyx_license));
        } catch (LicenseException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("License error")
                    .setMessage(e.getMessage())
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builder.create().show();
        }
        loadEnrolledTemplateIfExists();
    }

    /**
     * This method loads the fingerprint template if it exists.
     */
    private void loadEnrolledTemplateIfExists() {
        File enrolledFile = getFileStreamPath(ENROLL_FILENAME);
        if (enrolledFile.exists()) {
            try {
                FileInputStream enrollStream = openFileInput(ENROLL_FILENAME);
                ObjectInputStream ois = new ObjectInputStream(enrollStream);
                mEnrolledTemplate = (FingerprintTemplate) ois.readObject();
//                EnrollmentMetric em;
//                em.setFingerprintTemplateArray();
//                mEnrolledTemplate.getData();
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage());
            } catch (StreamCorruptedException e) {
                Log.e(TAG, e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (ClassNotFoundException e) {
                Log.e(TAG, e.getMessage());
            }
        }else{
            Toast.makeText(VerifyFinger.this, "No fingerp enrolled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    /**
     * Creates the default Settings menu.
     * @return true when the menu is successfully created.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    /**
     * Handles the Settings Menu options.
     * @param item the MenuItem selected by the user.
     * @return true if selection was valid, false otherwise.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method clears the currently enrolled fingerprint template.
     */
    private void menuClearEnrollment() {

    }


    public class VerifyTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        String line = null;
        InputStream is = null;
        String result;
        byte [] fingerprintTemplate;
        String responseStr;

        VerifyTask(byte [] fingerprintTemplate){
            this.fingerprintTemplate = fingerprintTemplate;
        }

        @Override
        protected void onPreExecute() {
//			progressDialog = ProgressDialog.show(mContext, "", "");
//			progressDialog.setCancelable(false);
//			progressDialog.setContentView(R.layout.progressdialog);
//			progressDialog.show();

            tv.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String bytesString = Base64.encodeToString(mCurrentTemplate.getData(), Base64.URL_SAFE | Base64.NO_WRAP).trim();

                JSONObject enrollmentJSON = new JSONObject();
                try {
                    enrollmentJSON.put("templates", bytesString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
            progressBar.setVisibility(View.INVISIBLE);
            AlertDialog.Builder builder = new AlertDialog.Builder(VerifyFinger.this);
            builder.setMessage("Result: "+responseStr)
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    })
                    .setTitle("Result");

            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

}
