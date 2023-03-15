package UI.Assessments;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.termtrackerfinal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import Database.AppDatabase;
import Database.AssessmentDAO;
import Entities.AssessmentTable;
import UI.Terms.AddTermActivity;

public class EditAssessmentActivity extends AppCompatActivity {

    List<AssessmentTable> assessmentTableList;
    AssessmentTable assessment;

    private int assessmentId;
    private Spinner assessmentTypeSpinner;
    private EditText titleEditText;
    private EditText dueDateEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assessment);

        assessmentId = getIntent().getIntExtra("assessmentId", -1);

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        AssessmentDAO dao = db.assessmentDAO();
        assessment = dao.getAssessmentById(assessmentId);

        assessmentTypeSpinner = findViewById(R.id.assessmentTypeSpinner);
        titleEditText = findViewById(R.id.titleEditText);
        dueDateEditText = findViewById(R.id.dueDateEditText);

        if (assessment != null) {
            assessmentTypeSpinner.setPrompt(assessment.getAssessmentType());
            titleEditText.setText(assessment.getTitle());
            dueDateEditText.setText(assessment.getDueDate());
        }

        FloatingActionButton editAssessment = findViewById(R.id.addModifiedAssess);
        editAssessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = assessmentTypeSpinner.getSelectedItem().toString();
                String title = titleEditText.getText().toString();
                String dueDate = dueDateEditText.getText().toString();

                assessmentTableList = dao.getAllAssessments();
                if (title.isEmpty() || type.isEmpty() || dueDate.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Field(s) Are Not Filled In.", Toast.LENGTH_SHORT).show();
                } else {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    AssessmentDAO dao = db.assessmentDAO();
                    assessment.setTitle(title);
                    assessment.setType(type);
                    assessment.setDueDate(dueDate);
                    int assessmentId = dao.updateAssessment(assessment);
                    Toast.makeText(EditAssessmentActivity.this, "assessment Added Successfully!", Toast.LENGTH_SHORT).show();

                    // Send back the new term ID to the calling activity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("assessmentId", (int) assessmentId);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        FloatingActionButton cancelAssessment = findViewById(R.id.cancelModifyAssessmentButton);
        cancelAssessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });
    }
}