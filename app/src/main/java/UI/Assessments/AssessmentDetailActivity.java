package UI.Assessments;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import Adapters.AssessmentAdapter;
import Database.AppDatabase;
import Database.AssessmentDAO;
import Database.CourseDAO;
import Entities.AssessmentTable;

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

        assessmentTitleTextView = findViewById(R.id.assessmentTitleTextView);
        assessmentTypeTextView = findViewById(R.id.assessmentTypeTextView);
        dueDateTextView = findViewById(R.id.dueDateTextView);

        int courseId = getIntent().getIntExtra("courseId", -1);
        int assessId = getIntent().getIntExtra("assessmentId", -1);

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        int desiredHour = 8;
        int desiredMinute = 0;

        if (assessId != -1) {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            AssessmentDAO dao = db.assessmentDAO();
            assessment = dao.getAssessmentById(assessId);

            if (assessment != null) {
                // Retrieve the start date of the course
                Date currentDate = new Date();
                String dueDateString = assessment.getDueDate();
                SimpleDateFormat DateFormat = new SimpleDateFormat("MM/dd/yyyy");
                Date startDate = null;
                try {
                    startDate = DateFormat.parse(dueDateString);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                if (currentDate.equals(startDate)) {
                    if (currentHour == desiredHour && currentMinute == desiredMinute) {
                        Intent intent = new Intent("com.example.notification.ACTION_NOTIFICATION");
                        intent.putExtra("title", "Assessment: " + assessment.getTitle());
                        intent.putExtra("message", "Your Assessment " + assessment.getTitle() + "is due today!");
                        sendBroadcast(intent);
                    }
                }
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
