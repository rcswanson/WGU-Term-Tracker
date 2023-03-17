package UI.Assessments;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.termtrackerfinal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Adapters.AssessmentAdapter;
import Database.AppDatabase;
import Database.AssessmentDAO;
import Database.CourseDAO;
import Database.TermDAO;
import Entities.AssessmentTable;
import Entities.CourseTable;
import UI.Terms.TermDetailActivity;
import Utilities.NotificationController;
import Utilities.NotificationReceiver;

public class AssessmentDetailActivity extends AppCompatActivity {

    AssessmentAdapter adapter;

    private String due;
    private AssessmentTable assessment;
    private TextView assessmentTitleTextView;
    private TextView assessmentTypeTextView;
    private TextView dueDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);

        // INITIALIZES START AND END DATES FOR NOTIFICATIONS

        due = assessment.getDueDate();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Date notifyDue = null;
        try {
            notifyDue = format.parse(due);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // CREATES NOTIFICATION CHANNEL
        NotificationController.createNotificationChannel(this);

        // SCHEDULE START DATE NOTIFICATION
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(notifyDue);
        startCalendar.set(Calendar.HOUR_OF_DAY, 12); // Set the notification time to 9AM
        startCalendar.set(Calendar.MINUTE, 23);
        startCalendar.set(Calendar.SECOND, 0);

        long startNotificationTime = startCalendar.getTimeInMillis();
        long currentTime = System.currentTimeMillis();

        if (startNotificationTime > currentTime) {
            // The start notification time is in the future
            Intent startIntent = new Intent(this, NotificationReceiver.class);
            startIntent.putExtra(NotificationReceiver.NOTIFICATION_TITLE, "Assessment Due Date");
            startIntent.putExtra(NotificationReceiver.NOTIFICATION_MESSAGE, "Your Assessment Is Due Today.");
            PendingIntent startPendingIntent = PendingIntent.getBroadcast(this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, startNotificationTime, startPendingIntent);
        }

        assessmentTitleTextView = findViewById(R.id.assessmentTitleTextView);
        assessmentTypeTextView = findViewById(R.id.assessmentTypeTextView);
        dueDateTextView = findViewById(R.id.dueDateTextView);

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
                intent.putExtra("dueDate", assessment.getDueDate());
                startActivity(intent);
            }
        });
    }

    private void updateUI() {
        assessmentTitleTextView.setText(assessment.getTitle());
        assessmentTypeTextView.setText(assessment.getAssessmentType());
        dueDateTextView.setText(assessment.getDueDate());
    }

    protected void onResume() {
        super.onResume();
        updateUI();
    }
}
