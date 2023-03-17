package UI.Courses;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.security.identity.DocTypeNotSupportedException;
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
import Database.CourseDAO;
import Database.TermDAO;
import Entities.CourseTable;
import Entities.TermTable;

public class AddCourseActivity extends AppCompatActivity {

    List<CourseTable> courseTableList;

    private EditText courseTitleEditText;
    private DatePicker courseStartPicker;
    private DatePicker courseEndPicker;
    private Spinner statusSpinner;
    private EditText instNameTextView;
    private EditText instEmailTextView;
    private EditText instPhoneTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        CourseDAO dao = AppDatabase.getInstance(getApplicationContext()).courseDAO();

        // INITIALIZE UI ELEMENTS
        courseTitleEditText = findViewById(R.id.courseTitleEditText);
        courseStartPicker = findViewById(R.id.courseStartPicker);
        courseEndPicker = findViewById(R.id.courseEndPicker);
        statusSpinner = findViewById(R.id.statusSpinner);
        instNameTextView = findViewById(R.id.instNameTextView);
        instEmailTextView = findViewById(R.id.instEmailTextView);
        instPhoneTextView = findViewById(R.id.instPhoneTextView);

        // SET UP STATUS SPINNER
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        // ADD COURSE TO TERM
        FloatingActionButton addCourseToTableButton = findViewById(R.id.addCourseToTableButton);
        addCourseToTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // RETRIEVE USER INPUT
                String title = courseTitleEditText.getText().toString();
                String status = statusSpinner.getSelectedItem().toString();
                String instName = instNameTextView.getText().toString();
                String instEmail = instEmailTextView.getText().toString();
                String instPhone = instPhoneTextView.getText().toString();
                int termId = getIntent().getIntExtra("termId", -1);

                Calendar sCalendar = Calendar.getInstance();
                Calendar eCalendar = Calendar.getInstance();

                int sDay = courseStartPicker.getDayOfMonth();
                int sMonth = courseStartPicker.getMonth();
                int sYear = courseStartPicker.getYear();

                int eDay = courseEndPicker.getDayOfMonth();
                int eMonth = courseEndPicker.getMonth();
                int eYear = courseEndPicker.getYear();

                sCalendar.set(sYear, sMonth, sDay);
                eCalendar.set(eYear, eMonth, eDay);

                long sDateBeforeFormat = sCalendar.getTimeInMillis();
                long eDateBeforeFormat = eCalendar.getTimeInMillis();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                String formattedStart = dateFormat.format(new Date(sDateBeforeFormat));
                String formattedEnd = dateFormat.format(new Date(eDateBeforeFormat));

                CourseTable course = new CourseTable(title, formattedStart, formattedEnd, status, instName, instEmail, instPhone, termId);
                courseTableList = dao.getAllCourses();

                // VALIDATE USER INPUT
                if (title.isEmpty() || instName.isEmpty() || instEmail.isEmpty() || instPhone.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Field(s) Are Not Filled In.", Toast.LENGTH_SHORT).show();
                } else {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    CourseDAO dao = db.courseDAO();
                    long courseId = dao.insertCourse(course);

                    if (courseId > 0) {
                        Toast.makeText(AddCourseActivity.this, "Course Added Successfully!", Toast.LENGTH_SHORT).show();

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("courseId", (int) courseId);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                }
            }
        });

        // CANCEL ADD COURSE AND RETURN TO TERM
        FloatingActionButton cancelAddCourseButton = findViewById(R.id.cancelAddCourseButton);
        cancelAddCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // close the activity
            }
        });
    }
}
