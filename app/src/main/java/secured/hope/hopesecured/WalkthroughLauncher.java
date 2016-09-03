package secured.hope.hopesecured;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Ayokunle on 02/09/2016.
 */
public class WalkthroughLauncher extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startMain = new Intent(this, com.kunall17.entryscreenmanager.Activities.MainActivity.class);
        startActivityForResult(startMain, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent startMain = new Intent(this, DashboardActivity.class);
        startActivity(startMain);
        finish();
    }
}
