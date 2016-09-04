package secured.hope.hopesecured;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class JobHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobhistory);

        Button next = (Button)
                findViewById(R.id.scanFinger);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Intent intent = new Intent(JobHistoryActivity.this, JobHistoryActivity.class);
//                startActivity(intent);
            }
        });
    }
}
