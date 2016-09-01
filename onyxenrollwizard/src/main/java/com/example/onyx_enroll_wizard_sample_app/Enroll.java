package com.example.onyx_enroll_wizard_sample_app;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.dft.onyxcamera.licensing.License;
import com.dft.onyxcamera.licensing.LicenseException;
import com.dft.onyxcamera.ui.CaptureConfiguration;
import com.dft.onyxcamera.ui.CaptureConfiguration.Flip;
import com.dft.onyxcamera.ui.CaptureConfigurationBuilder;
import com.dft.onyxcamera.ui.CaptureMetrics;
import com.dft.onyxcamera.ui.OnyxFragment;
import com.dft.onyxcamera.ui.OnyxFragment.FingerprintTemplateCallback;
import com.dft.onyxcamera.ui.OnyxFragment.ProcessedBitmapCallback;
import com.dft.onyxcamera.ui.OnyxFragment.WsqCallback;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;


public class Enroll extends Activity {
    private final static String TAG = Enroll.class.getSimpleName();
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
        mEnrolledTemplate = null;
        deleteEnrolledTemplateIfExists();
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
                mFingerprintView.startAnimation(mFadeOut);
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
                    createEnrollQuestionDialog();
                } else {
                    Toast.makeText(Enroll.this, "mEnrolledTemplate is empty", Toast.LENGTH_SHORT).show();
                    mFragment.startOneShotAutoCapture();
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

                //all chars in encoded are guaranteed to be 7-bit ASCII
                try {
                    File file = getFileStreamPath(ENROLL_FILENAME);

                    byte[] bytes = loadFile(file);
                    for(int i = 0; i< bytes.length; i++){
                        System.out.print(bytes[i]);
                    }
                    System.out.println();

                    byte[] encoded = Base64.encodeBase64(bytes);
                    for(int i = 0; i< encoded.length; i++){
                        System.out.print(encoded[i]);
                    }
                    System.out.println();

                    String encodedString = new String(encoded); //send to sever and decode
                    System.out.println("Encode "+ encodedString);

                    VerifyData task = new VerifyData(Enroll.this.getApplicationContext(), encodedString);
                    task.execute();

                    byte[] decoded = Base64.decodeBase64(encodedString.getBytes());
                    for(int i = 0; i< decoded.length; i++){
                        System.out.print(decoded[i]);
                    }
                    System.out.println();

                }catch(Exception e){
                    e.printStackTrace();
                }
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

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int)length];
        
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        is.close();
        return bytes;
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
            final JSONObject[] enrollmentJSON = {null};

            if (null != fingerprintTemplate) {
                System.out.println("1");
                String bytesString =
                        android.util.Base64.encodeToString(
                                fingerprintTemplate.getData(), android.util.Base64.URL_SAFE | android.util.Base64.NO_WRAP).trim();

                enrollmentJSON[0] = new JSONObject();
                try {
                    enrollmentJSON[0].put("templates", bytesString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                System.out.println("fingerprintTemplate is null");
            }

            if (mEnrolledTemplate != null) {
//                VerifyTask verifyTask = new VerifyTask(getApplicationContext());
//                verifyTask.execute(mCurrentTemplate, mEnrolledTemplate);
            }
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
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage());
            } catch (StreamCorruptedException e) {
                Log.e(TAG, e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (ClassNotFoundException e) {
                Log.e(TAG, e.getMessage());
            }
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
        Toast.makeText(this, "Clearing enrolled fingerprint.", Toast.LENGTH_SHORT).show();
        mEnrolledTemplate = null;
        deleteEnrolledTemplateIfExists();
    }

    public class VerifyData extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        String encoded = "";

        public VerifyData(Context context, String encoded ) {
            mContext = context;
            this.encoded = encoded;
        }

        ProgressDialog progressDialog;
        String line = null;
        InputStream is = null;
        String result;

        @Override
        protected void onPreExecute() {
//			progressDialog = ProgressDialog.show(mContext, "", "");
//			progressDialog.setCancelable(false);
//			progressDialog.setContentView(R.layout.progressdialog);
//			progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            if( encoded != null) {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("templates", encoded));

                try {
                    long startTime = System.nanoTime();

                    // post header
                    HttpPost httpPost = new HttpPost("http://ayokunle.tk/exp2014/getFile.php");
                    HttpClient client = new DefaultHttpClient();

                    File file = getFileStreamPath(ENROLL_FILENAME);
                    FileBody fileBody = new FileBody(file);

                    MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                    reqEntity.addPart("file", fileBody);
                    httpPost.setEntity(reqEntity);

                    // execute HTTP post request
                    HttpResponse response = client.execute(httpPost);
                    HttpEntity resEntity = response.getEntity();

                    if (resEntity != null) {

                        String responseStr = EntityUtils.toString(resEntity).trim();
                        Log.v(TAG, "Response: " + responseStr);
                    }else{
                        Log.v(TAG, "Response: NULL");
                    }
//                    HttpParams para = new BasicHttpParams();
//                    //this how tiny it might seems, is actually absoluty needed. otherwise http client lags for 2sec.
//                    HttpProtocolParams.setVersion(para, HttpVersion.HTTP_1_1);
//                    //HttpClient httpClient = new DefaultHttpClient(params);
//                    HttpClient httpclient = new DefaultHttpClient();
//                    HttpPost httppost = new HttpPost(
//                            "http://ayokunle.tk:8080/validate");
//                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//                    HttpResponse response = httpclient.execute(httppost);
//                    HttpEntity entity = response.getEntity();
//                    is = entity.getContent();
                    long endTime = System.nanoTime();
                    System.out.println("It took " + (endTime - startTime) / 1000000000.0 + " seconds.");
                    Log.e("pass 1", "Connection success ");
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                }


            }else{
                System.out.println("Is NULL");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//			progressDialog.dismiss();
        }
    }
}