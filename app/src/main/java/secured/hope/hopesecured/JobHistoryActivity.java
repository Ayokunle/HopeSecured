package secured.hope.hopesecured;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.onyx_enroll_wizard_sample_app.LaunchEnrollmentScannerActivity;

public class JobHistoryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobhistory);

        Button back = (Button)
                findViewById(R.id.backForm);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Intent intent = new Intent(JobHistoryActivity.this, JobHistoryActivity.class);
//                startActivity(intent);
                finish();
            }
        });

        Button next = (Button)
                findViewById(R.id.scanFinger);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(JobHistoryActivity.this, LaunchEnrollmentScannerActivity.class);
                startActivity(intent);
            }
        });
    }
}