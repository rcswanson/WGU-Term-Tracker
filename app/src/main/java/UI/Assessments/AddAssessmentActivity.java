package UI.Assessments;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.termtrackerfinal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import Database.AppDatabase;
import Database.AssessmentDAO;
import Database.CourseDAO;
import Entities.AssessmentTable;
import UI.Courses.AddCourseActivity;

public class AddAssessmentActivity extends AppCompatActivity {

    List<AssessmentTable> assessmentTableList;

    private Spinner assessmentTypeSpinner;
    private EditText assessmentTitleEditText;
    private EditText assessmentDueDateEditText;

    private AppDatabase db;

    private String selectedAssessmentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assessment);

        AssessmentDAO dao = AppDatabase.getInstance(getApplicationContext()).assessmentDAO();

        assessmentTypeSpinner = findViewById(R.id.assessmentTypeSpinner);
        assessmentTitleEditText = findViewById(R.id.titleEditText);
        assessmentDueDateEditText = findViewById(R.id.dueDateEditText);

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
                String dueDate = assessmentDueDateEditText.getText().toString();
                int assessCourseId = getIntent().getIntExtra("courseId", -1);

                AssessmentTable assessment = new AssessmentTable(type, title, dueDate, assessCourseId);

                // VALIDATE USER INPUT
                if (title.isEmpty() || dueDate.isEmpty()) {
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
