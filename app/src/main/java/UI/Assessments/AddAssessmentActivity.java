package UI.Assessments;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.termtrackerfinal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Database.AppDatabase;
import Database.AssessmentDAO;
import Database.CourseDAO;
import Entities.AssessmentTable;
import UI.Courses.AddCourseActivity;

public class AddAssessmentActivity extends AppCompatActivity {

    List<AssessmentTable> assessmentTableList;

    private Spinner assessmentTypeSpinner;
    private EditText assessmentTitleEditText;
    private DatePicker assessmentDueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assessment);

        AssessmentDAO dao = AppDatabase.getInstance(getApplicationContext()).assessmentDAO();

        assessmentTypeSpinner = findViewById(R.id.assessmentTypeSpinner);
        assessmentTitleEditText = findViewById(R.id.titleEditText);
        assessmentDueDate = findViewById(R.id.dueDateView);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        assessmentDueDate.init(year, month, day, null);

        // SET UP TYPE SPINNER
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.assessment_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assessmentTypeSpinner.setAdapter(adapter);

        // ADD ASSESSMENT TO COURSE
        FloatingActionButton addAssessmentToTableButton = findViewById(R.id.addAssessmentToTableButton);
        addAssessmentToTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = assessmentTypeSpinner.getSelectedItem().toString();
                String title = assessmentTitleEditText.getText().toString();

                int day = assessmentDueDate.getDayOfMonth();
                int month = assessmentDueDate.getMonth();
                int year = assessmentDueDate.getYear();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                long dateBeforeFormat = calendar.getTimeInMillis();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                String formattedDueDate = dateFormat.format(new Date(dateBeforeFormat));

                int assessCourseId = getIntent().getIntExtra("courseId", -1);

                AssessmentTable assessment = new AssessmentTable(type, title, formattedDueDate, assessCourseId);

                // VALIDATE USER INPUT
                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Field(s) Are Not Filled In.", Toast.LENGTH_SHORT).show();
                } else {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    AssessmentDAO dao = db.assessmentDAO();
                    long assessmentId = dao.insertAssessment(assessment);

                    if (assessmentId > 0) {
                        Toast.makeText(AddAssessmentActivity.this, "Assessment Added Successfully!", Toast.LENGTH_SHORT).show();

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("assessmentId", (int) assessmentId);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                }
            }
        });

        // CANCEL ADD ASSESSMENT AND RETURN TO COURSE
        FloatingActionButton cancelAddAssessmentButton = findViewById(R.id.cancelAddAssessmentButton);
        cancelAddAssessmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // close the activity
            }
        });
    }
}
