package UI.Assessments;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.termtrackerfinal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Database.AppDatabase;
import Database.AssessmentDAO;
import Entities.AssessmentTable;
import UI.Terms.AddTermActivity;

public class EditAssessmentActivity extends AppCompatActivity {

    List<AssessmentTable> assessmentTableList;
    AssessmentTable assessment;

    private String dueDateIntent;
    private int courseId;
    private int assessmentId;
    private Spinner assessmentTypeSpinner;
    private EditText titleEditText;
    private DatePicker dueDateView;

    private DatePickerDialog.OnDateSetListener dueDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assessment);

        // RECEIVES INTENT FROM PREVIOUS ACTIVITY
        assessmentId = getIntent().getIntExtra("assessmentId", -1);
        courseId = getIntent().getIntExtra("courseId", -1);
        dueDateIntent = getIntent().getStringExtra("dueDate");

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        AssessmentDAO dao = db.assessmentDAO();
        assessment = dao.getAssessmentById(assessmentId);

        // INITIALIZES XML LAYOUTS
        assessmentTypeSpinner = findViewById(R.id.assessmentTypeSpinner);
        titleEditText = findViewById(R.id.titleEditText);
        dueDateView = findViewById(R.id.dueDateView);

        // INITIALIZES DATE PICKER FORMATS
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        try {
            Date dueDate = dateFormat.parse(dueDateIntent);
            calendar.setTime(dueDate);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            dueDateView.init(year, month, day, null);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        // SETS INPUT FIELDS TO SELECTED ASSESSMENT
        if (assessment != null) {
            assessmentTypeSpinner.setPrompt(assessment.getAssessmentType());
            titleEditText.setText(assessment.getTitle());
        }

        FloatingActionButton editAssessment = findViewById(R.id.addModifiedAssess);
        editAssessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = assessmentTypeSpinner.getSelectedItem().toString();
                String title = titleEditText.getText().toString();

                int day = dueDateView.getDayOfMonth();
                int month = dueDateView.getMonth();
                int year = dueDateView.getYear();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                long dateBeforeFormat = calendar.getTimeInMillis();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                String formattedDueDate = dateFormat.format(new Date(dateBeforeFormat));

                assessmentTableList = dao.getAllAssessments();
                if (title.isEmpty() || type.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Field(s) Are Not Filled In.", Toast.LENGTH_SHORT).show();
                } else {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    AssessmentDAO dao = db.assessmentDAO();
                    assessment.setTitle(title);
                    assessment.setType(type);
                    assessment.setDueDate(formattedDueDate);
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