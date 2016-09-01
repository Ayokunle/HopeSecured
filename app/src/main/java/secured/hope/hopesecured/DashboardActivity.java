package secured.hope.hopesecured;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        getSupportActionBar().setTitle(null);

        TextView tx = (TextView)findViewById(R.id.logo);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/LobsterTwo-BoldItalic.otf");
        tx.setTypeface(custom_font);

        AppCompatButton enroll = (AppCompatButton) findViewById(R.id.enroll);
        enroll.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent startMain = new Intent(DashboardActivity.this, EnrollActivity.class);
                startActivity(startMain);
            }
        });
    }
}
