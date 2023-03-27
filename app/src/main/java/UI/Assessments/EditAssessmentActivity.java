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

    private String startDateIntent;
    private String endDateIntent;
    private int courseId;
    private int assessmentId;
    private Spinner assessmentTypeSpinner;
    private EditText titleEditText;
    private DatePicker startDateView;
    private DatePicker endDateView;

    private DatePickerDialog.OnDateSetListener dueDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assessment);

        // RECEIVES INTENT FROM PREVIOUS ACTIVITY
        assessmentId = getIntent().getIntExtra("assessmentId", -1);
        courseId = getIntent().getIntExtra("courseId", -1);
        startDateIntent = getIntent().getStringExtra("startDate");
        endDateIntent = getIntent().getStringExtra("endDate");

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        AssessmentDAO dao = db.assessmentDAO();
        assessment = dao.getAssessmentById(assessmentId);

        // INITIALIZES XML LAYOUTS
        assessmentTypeSpinner = findViewById(R.id.assessmentTypeSpinner);
        titleEditText = findViewById(R.id.titleEditText);
        startDateView = findViewById(R.id.startDateView);
        endDateView = findViewById(R.id.endDateView);

        // INITIALIZES DATE PICKER FORMATS
        Calendar sCalendar = Calendar.getInstance();
        Calendar eCalendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        try{
            Date start = dateFormat.parse(startDateIntent);
            Date end = dateFormat.parse(endDateIntent);
            sCalendar.setTime(start);
            eCalendar.setTime(end);
            int sYear = sCalendar.get(Calendar.YEAR);
            int sMonth = sCalendar.get(Calendar.MONTH);
            int sDay = sCalendar.get(Calendar.DAY_OF_MONTH);
            int eYear = eCalendar.get(Calendar.YEAR);
            int eMonth = eCalendar.get(Calendar.MONTH);
            int eDay = eCalendar.get(Calendar.DAY_OF_MONTH);
            startDateView.init(sYear, sMonth, sDay, null);
            endDateView.init(eYear, eMonth, eDay, null);
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

                int sDay = startDateView.getDayOfMonth();
                int sMonth = startDateView.getMonth();
                int sYear = startDateView.getYear();

                int eDay = endDateView.getDayOfMonth();
                int eMonth = endDateView.getMonth();
                int eYear = endDateView.getYear();
                Calendar sCalendar = Calendar.getInstance();
                Calendar eCalendar = Calendar.getInstance();
                sCalendar.set(sYear, sMonth, sDay);
                eCalendar.set(eYear, eMonth, eDay);
                long startBeforeFormat = sCalendar.getTimeInMillis();
                long endBeforeFormat = eCalendar.getTimeInMillis();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                String formattedStartDate = dateFormat.format(new Date(startBeforeFormat));
                String formattedEndDate = dateFormat.format(new Date(endBeforeFormat));

                assessmentTableList = dao.getAllAssessments();
                if (title.isEmpty() || type.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Field(s) Are Not Filled In.", Toast.LENGTH_SHORT).show();
                } else {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    AssessmentDAO dao = db.assessmentDAO();
                    assessment.setTitle(title);
                    assessment.setType(type);
                    assessment.setStartDate(formattedStartDate);
                    assessment.setEndDate(formattedEndDate);
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