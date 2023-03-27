package UI.Assessments;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.termtrackerfinal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import Adapters.AssessmentAdapter;
import Database.AppDatabase;
import Database.AssessmentDAO;
import Database.CourseDAO;
import Entities.AssessmentTable;
import UI.Courses.CourseDetailActivity;
import UI.MainActivity;
import Utilities.NotifyReceiver;

public class AssessmentDetailActivity extends AppCompatActivity {

    private AssessmentTable assessment;
    private TextView assessmentTitleTextView;
    private TextView assessmentTypeTextView;
    private TextView startDateTextView;
    private TextView endDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);

        assessmentTitleTextView = findViewById(R.id.assessmentTitleTextView);
        assessmentTypeTextView = findViewById(R.id.assessmentTypeTextView);
        startDateTextView = findViewById(R.id.startDateTextView);
        endDateTextView = findViewById(R.id.endDateTextView);

        int courseId = getIntent().getIntExtra("courseId", -1);
        int assessId = getIntent().getIntExtra("assessmentId", -1);

        if (assessId != -1) {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            AssessmentDAO dao = db.assessmentDAO();
            assessment = dao.getAssessmentById(assessId);
            if (assessment != null) {
                updateUI();
            }
            }

        FloatingActionButton deleteAssessment = findViewById(R.id.DeleteAssessmentButton);
        deleteAssessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (assessment != null) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(AssessmentDetailActivity.this);
                    alert.setMessage("Are you sure you want to delete this assessment?");
                    alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                            AssessmentDAO dao = db.assessmentDAO();
                            dao.deleteAssessment(assessment);
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                } else {
                    Toast.makeText(AssessmentDetailActivity.this, "Assessment not found.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FloatingActionButton modifyAssessment = findViewById(R.id.ModifyAssessmentButton);
        modifyAssessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssessmentDetailActivity.this, EditAssessmentActivity.class);
                intent.putExtra("assessmentId", assessId);
                intent.putExtra("courseId", courseId);
                intent.putExtra("startDate", assessment.getStartDate());
                intent.putExtra("endDate", assessment.getEndDate());

                startActivity(intent);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.assessmentmenuitems, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notifyStart:
                Toast.makeText(getApplicationContext(), "A notification is now set to alert you at 9AM on the day of your assessment start date", Toast.LENGTH_LONG).show();
                String startDateString = assessment.getStartDate();
                Date startDate = null;
                try {
                    startDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(startDateString);
                } catch (ParseException pe) {
                    pe.printStackTrace();
                }
                if (startDate != null) {
                    Calendar sCalendar = Calendar.getInstance();
                    sCalendar.setTimeInMillis(startDate.getTime());
                    sCalendar.set(Calendar.HOUR_OF_DAY, 8);
                    sCalendar.set(Calendar.MINUTE, 11);
                    sCalendar.set(Calendar.SECOND, 0);

                    Intent startIntent = new Intent(AssessmentDetailActivity.this, NotifyReceiver.class);
                    startIntent.putExtra("key", " Your assessment: " + assessment.getTitle() + " Starts Today!");
                    PendingIntent sPendingIntent = PendingIntent.getBroadcast(AssessmentDetailActivity.this, MainActivity.numAlert++, startIntent, PendingIntent.FLAG_MUTABLE);
                    AlarmManager sAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    sAlarmManager.set(AlarmManager.RTC_WAKEUP, sCalendar.getTimeInMillis(), sPendingIntent);
                }
                return true;

            case R.id.notifyEnd:
                Toast.makeText(getApplicationContext(), "A notification is now set to alert you at 9AM on the day of your assessment end date", Toast.LENGTH_LONG).show();
                String endDateString = assessment.getEndDate();
                Date endDate = null;
                try {
                    endDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(endDateString);
                } catch (ParseException pe) {
                    pe.printStackTrace();
                }
                if (endDate != null) {
                    Calendar eCalendar = Calendar.getInstance();
                    eCalendar.setTimeInMillis(endDate.getTime());
                    eCalendar.set(Calendar.HOUR_OF_DAY, 8);
                    eCalendar.set(Calendar.MINUTE, 11);
                    eCalendar.set(Calendar.SECOND, 0);


                    Intent endIntent = new Intent(AssessmentDetailActivity.this, NotifyReceiver.class);
                    endIntent.putExtra("key", "Your assessment: " + assessment.getTitle() + " Ends Today");
                    PendingIntent ePendingIntent = PendingIntent.getBroadcast(AssessmentDetailActivity.this, MainActivity.numAlert++, endIntent, PendingIntent.FLAG_MUTABLE);
                    AlarmManager eAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    eAlarmManager.set(AlarmManager.RTC_WAKEUP, eCalendar.getTimeInMillis(), ePendingIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        assessmentTitleTextView.setText(assessment.getTitle());
        assessmentTypeTextView.setText(assessment.getAssessmentType());
        startDateTextView.setText(assessment.getStartDate());
        endDateTextView.setText(assessment.getEndDate());
    }

    protected void onResume() {
        super.onResume();
        updateUI();
    }
}
