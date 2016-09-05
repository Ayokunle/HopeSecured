package com.example.onyx_enroll_wizard_sample_app;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dft.onyx.core;
import com.dft.onyx.enroll.util.Consts;
import com.dft.onyx.wizardroid.enrollwizard.EnrollWizardBuilder;
import com.example.onyx_enroll_wizard_sample_app.onyx.EnrollWizardBuilder_;

import org.opencv.android.OpenCVLoader;

import java.io.File;

//import org.codehaus.jackson.annotate.JsonAutoDetect;
//import org.codehaus.jackson.annotate.JsonMethod;
//import org.codehaus.jackson.map.ObjectMapper;

public class LaunchEnrollmentScannerActivity extends ListActivity {

	private static final int ENROLL_REQUEST_CODE = 20342;
	private static final String TAG = "OnyxEnrollment";
	private final static String ENROLL_FILENAME = "enrolled_template.bin";

	int counter = 0;
	String fname = "", lname ="";


	static {
		if(!OpenCVLoader.initDebug()) {
			Log.d(TAG, "Unable to load OpenCV!");
		} else {
			Log.i(TAG, "OpenCV loaded successfully");
			core.initOnyx();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onyx_enroll_wizard_sample_main);

		Intent onyxSelfEnrollIntent = getSelfEnrollIntent(this,
				getString(R.string.onyx_license));
		startActivityForResult(onyxSelfEnrollIntent, ENROLL_REQUEST_CODE);
		finish();
	}

	static public Intent getSelfEnrollIntent(Context context, String onyxLicenseKey) {
		return (new EnrollWizardBuilder())
				.setNumEnrollScales(3)
				.setLicenseKey(onyxLicenseKey)
				.setUseOnyxGuide(true, true, true)
				.setUseSelfEnroll(true)
				.build(context);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			Log.e(TAG, "requestCode: " + requestCode);
			Log.e(TAG, "FName: " +data.getStringExtra("fname"));
			Log.e(TAG, "LName: " +data.getStringExtra("lname"));

			fname = data.getStringExtra("fname");
			lname = data.getStringExtra("lname");


			Log.e(TAG, "After - fingerprintExists(): " + fingerprintExists());
			Log.e(TAG, "Post to server");

			if (fingerprintExists()) {

//					InsertData task = new InsertData(this);
//					try {
//						task.execute().get(10000, TimeUnit.MILLISECONDS);
//					} catch (Exception e) {
//						Log.e(TAG, "Time out");
//						e.printStackTrace();
//					}
				Toast.makeText(this, "PASS", Toast.LENGTH_LONG).show();
			}
			Log.e(TAG, "Before - fingerprintExists(): " + fingerprintExists());
		} else {
			Log.e(TAG, "Problem here - 1 ");
		}



//		if (requestCode == ENROLL_REQUEST_CODE) {
//			if (resultCode == RESULT_OK) {

//		Bundle bundle = data.getExtras();
//		if (bundle != null) {
//			Set<String> keys = bundle.keySet();
//			Iterator<String> it = keys.iterator();
//			Log.e(TAG,"Dumping Intent start");
//			while (it.hasNext()) {
//				String key = it.next();
//				Log.e(TAG,"[" + key + "=" + bundle.get(key)+"]");
//			}
//			Log.e(TAG,"Dumping Intent end");
//		}
//			}
//		}

		/*
		if (requestCode == VERIFY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Successfully verified
				Toast.makeText(this, getResources().getString(R.string.toast_verify_success_message), Toast.LENGTH_LONG).show();
			} else {
				// Did not successfully verify
				Toast.makeText(this, getResources().getString(R.string.toast_verify_fail_message), Toast.LENGTH_LONG).show();
			}
		}
		*/
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

	private void deleteEnrolledTemplateIfExists() {
		if (fingerprintExists()) {
			mEnrolledFile.delete();
		}
	}

//	public class InsertData extends AsyncTask<Void, Void, Void> {
//
//		private Context mContext;
//
//		public InsertData(Context context) {
//			mContext = context;
//		}
//
//		ProgressDialog progressDialog;
//		String line = null;
//		InputStream is = null;
//		String result;
//
//		@Override
//		protected void onPreExecute() {
//			/*
//			progressDialog = ProgressDialog.show(mContext, "", "");
//			progressDialog.setCancelable(false);
//			progressDialog.setContentView(R.layout.progressdialog);
//			progressDialog.show();
//			*/
//		}
//
//		@Override
//		protected Void doInBackground(Void... params) {
//			EnrollmentMetric em =
//					LoadEnrolledEnrollmentMetric.loadEnrolledTemplateIfExists(mContext.getApplicationContext());
//			ObjectMapper mapper = new ObjectMapper();
//			mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
//
//			if (null != em) {
//
//				FingerprintTemplate[] fpta = em.getFingerprintTemplateArray();
//				ArrayList<String> fptList = new ArrayList<String>();
//
//				for (int i = 0; i < fpta.length; i++) {
//					FingerprintTemplate ft = fpta[i];
//
//					if (null != ft) {
//						/*
//						try {
//							System.out.println("indentify: " +
//									new IdentifyFingerprint().identifyFingerprint(
//											em.getFingerprintTemplateVector(), ft));
//							FileOutputStream enrollStream =
//									mContext.openFileOutput("enrolled_template_"+i+".bin", MODE_WORLD_WRITEABLE);
//							ObjectOutputStream oos = new ObjectOutputStream(enrollStream);
//							oos.writeObject(ft);
//							oos.close();
//						} catch (Exception e) {
//							Log.e(TAG, e.getMessage());
//						}
//						*/
//
//						byte[] bytes = ft.getData();
//						String bytesString = Base64.encodeToString(bytes, Base64.URL_SAFE | Base64.NO_WRAP);
//						fptList.add(bytesString);
//					}
//				}
//
//				String[] fptArray = fptList.toArray(new String[0]);
//
//				JSONObject enrollmentJSON = new JSONObject();
//				// jsonInString = mapper.writeValueAsString(em);
//
//				try {
//					enrollmentJSON.put("fname", fname);
//					enrollmentJSON.put("lname", lname);
//					enrollmentJSON.put("templates", new JSONArray(fptList));
//					enrollmentJSON.put("fpt_size", fptList.size());
//					Log.e(TAG, " enrollmentJSON: " + enrollmentJSON);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//				nameValuePairs.add(new BasicNameValuePair("templates", enrollmentJSON.toString()));
//
//				try {
//					long startTime = System.nanoTime();
//					HttpParams para = new BasicHttpParams();
//					//this how tiny it might seems, is actually absolutly needed. otherwise http client lags for 2sec.
//					HttpProtocolParams.setVersion(para, HttpVersion.HTTP_1_1);
//					//HttpClient httpClient = new DefaultHttpClient(params);
//					HttpClient httpclient = new DefaultHttpClient();
//					HttpPost httppost = new HttpPost(
//							"http://139.59.175.91:8080/enroll");
//					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//					HttpResponse response = httpclient.execute(httppost);
//					HttpEntity entity = response.getEntity();
//					is = entity.getContent();
//					long endTime = System.nanoTime();
//					System.out.println("It took " + (endTime - startTime) / 1000000000.0 + " seconds.");
//					Log.e("pass 1", "Connection success ");
//				} catch (Exception e) {
//					Log.e("Fail 1", e.toString());
//				}
//
//				try {
//					BufferedReader reader = new BufferedReader(
//							new InputStreamReader(is, "iso-8859-1"), 8);
//					StringBuilder sb = new StringBuilder();
//					while ((line = reader.readLine()) != null) {
//						sb.append(line);
//					}
//					is.close();
//					result = sb.toString();
//					System.out.println("Result: " + result);
//				} catch (Exception e) {
//					Log.e("Fail 2", e.toString());
//				}
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			// progressDialog.dismiss();
//		}
//	}
}