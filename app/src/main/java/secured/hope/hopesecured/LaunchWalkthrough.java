package secured.hope.hopesecured;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

public class LaunchWalkthrough extends ActionBarActivity {
    private static int REQUEST_CODE = 435;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Intent intent = new Intent(LaunchWalkthrough.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = new Intent(LaunchWalkthrough.this, com.kunall17.entryscreenmanager.Activities.MainActivity.class);
        startActivityForResult(i, REQUEST_CODE);
    }
}