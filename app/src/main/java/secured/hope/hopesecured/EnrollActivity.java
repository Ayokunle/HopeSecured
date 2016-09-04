package secured.hope.hopesecured;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kunall17.entryscreenmanager.Fragments.Enroll;
import com.kunall17.entryscreenmanager.Fragments.Scan;
import com.kunall17.entryscreenmanager.Fragments.Verify;

import com.wdullaer.materialdatetimepicker.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;


public class EnrollActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText DOB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);

        DOB = (EditText) findViewById(R.id.DOB);
        DOB.measure(0, 0);
        int height = DOB.getMeasuredHeight();

        Button next = (Button)
                findViewById(R.id.jobHistory);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(EnrollActivity.this, JobHistoryActivity.class);
                startActivity(intent);
            }
        });

        Button DOB_button = (Button)
                findViewById(R.id.DOB_button);
        DOB_button.setHeight(height);
        DOB_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        EnrollActivity.this,
                        now.get(Calendar.YEAR)-18,
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setThemeDark(false);
                dpd.vibrate(true);
                dpd.dismissOnPause(true);
                dpd.showYearPickerFirst(true);
                dpd.setYearRange(now.get(Calendar.YEAR)-70, now.get(Calendar.YEAR)-18);
                if (true) {
                    dpd.setAccentColor(Color.parseColor("#9C27B0"));
                }
                if (true) {
                    dpd.setTitle("DATE OF BIRTH");
                }
                if (true) {
//                    Calendar[] dates = new Calendar[13];
//                    for (int i = -6; i <= 6; i++) {
//                        Calendar date = Calendar.getInstance();
//                        date.add(Calendar.MONTH, i);
//                        dates[i+6] = date;
//                    }
//                    dpd.setSelectableDays(dates);
                }
                if (true) {
//                    Calendar[] dates = new Calendar[13];
//                    for (int i = -6; i <= 6; i++) {
//                        Calendar date = Calendar.getInstance();
//                        date.add(Calendar.WEEK_OF_YEAR, i);
//                        dates[i+6] = date;
//                    }
//                    dpd.setHighlightedDays(dates);
                }
                if (true) {
//                    Calendar[] dates = new Calendar[3];
//                    for (int i = -1; i <= 1; i++) {
//                        Calendar date = Calendar.getInstance();
//                        date.add(Calendar.DAY_OF_MONTH, i);
//                        dates[i+1] = date;
//                    }
//                    dpd.setDisabledDays(dates);
                }
                dpd.show(getFragmentManager(), "Datepickerdialog");

            }
        });
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        DOB.setText(date);
    }
}
