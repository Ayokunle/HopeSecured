package secured.hope.hopesecured;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.onyx_enroll_wizard_sample_app.LaunchVerificationScannerActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button enroll = (Button) findViewById(R.id.enroll);
        enroll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, EnrollActivity.class);
                startActivity(intent);
            }
        });

        Button verify = (Button)
                findViewById(R.id.verify);
        verify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, LaunchVerificationScannerActivity.class);
                startActivity(intent);
            }
        });
    }
}
