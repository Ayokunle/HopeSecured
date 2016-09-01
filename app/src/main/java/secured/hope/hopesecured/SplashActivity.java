package secured.hope.hopesecured;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Locale;

public class SplashActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tx = (TextView)findViewById(R.id.logo);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/LobsterTwo-BoldItalic.otf");
        tx.setTypeface(custom_font);

        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }finally{
                    Intent menu = new Intent(SplashActivity.this, LaunchWalkthrough.class);
                    startActivity(menu);
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
}
