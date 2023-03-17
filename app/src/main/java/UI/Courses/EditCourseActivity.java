package UI.Courses;

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

import java.text.ParseException;
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

public class EditCourseActivity extends AppCompatActivity {

    List<CourseTable> courseTableList;
    CourseTable course;

    private String startDateIntent;
    private String endDateIntent;

    private int courseId;
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
        setContentView(R.layout.activity_edit_course);

        courseId = getIntent().getIntExtra("courseId", -1);
        startDateIntent = getIntent().getStringExtra("startDate");
        endDateIntent = getIntent().getStringExtra("endDate");

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        CourseDAO dao = db.courseDAO();
        course = dao.getCourseById(courseId);

        // INITIALIZE UI ELEMENTS
        courseTitleEditText = findViewById(R.id.courseTitleEditText);
        courseStartPicker = findViewById(R.id.courseStartPicker);
        courseEndPicker = findViewById(R.id.courseEndPicker);
        statusSpinner = findViewById(R.id.statusSpinner);
        instNameTextView = findViewById(R.id.instNameTextView);
        instEmailTextView = findViewById(R.id.instEmailTextView);
        instPhoneTextView = findViewById(R.id.instPhoneTextView);

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
            courseStartPicker.init(sYear, sMonth, sDay, null);
            courseEndPicker.init(eYear, eMonth, eDay, null);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        if (course != null) {
            courseTitleEditText.setText(course.getCourseTitle());
            statusSpinner.setPrompt(course.getStatus());
            instNameTextView.setText(course.getInstName());
            instEmailTextView.setText(course.getInstEmail());
            instPhoneTextView.setText(course.getInstPhone());
        }

        // SET UP STATUS SPINNER
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        // SAVE MODIFIED COURSE
        FloatingActionButton editCourse = findViewById(R.id.addModifiedCourse);
        editCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // RETRIEVE USER INPUT
                String title = courseTitleEditText.getText().toString();
                String status = statusSpinner.getSelectedItem().toString();
                String instName = instNameTextView.getText().toString();
                String instEmail = instEmailTextView.getText().toString();
                String instPhone = instPhoneTextView.getText().toString();

                int sDay = courseStartPicker.getDayOfMonth();
                int sMonth = courseStartPicker.getMonth();
                int sYear = courseStartPicker.getYear();
                int eDay = courseEndPicker.getDayOfMonth();
                int eMonth = courseEndPicker.getMonth();
                int eYear = courseEndPicker.getYear();
                Calendar calendar = Calendar.getInstance();
                sCalendar.set(sYear, sMonth, sDay);
                eCalendar.set(eYear, eMonth, eDay);
                long startBeforeFormat = sCalendar.getTimeInMillis();
                long endBeforeFormat = eCalendar.getTimeInMillis();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                String sFormattedDate = dateFormat.format(new Date(startBeforeFormat));
                String eFormattedDate = dateFormat.format(new Date(endBeforeFormat));

                courseTableList = dao.getAllCourses();

                // VALIDATE USER INPUT
                if (title.isEmpty() || instName.isEmpty() || instEmail.isEmpty() || instPhone.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Field(s) Are Not Filled In.", Toast.LENGTH_SHORT).show();
                } else {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    CourseDAO dao = db.courseDAO();
                    course.setCourseTitle(title);
                    course.setStartDate(sFormattedDate);
                    course.setEndDate(eFormattedDate);
                    course.setStatus(status);
                    course.setInstName(instName);
                    course.setInstEmail(instEmail);
                    course.setInstPhone(instPhone);
                    int courseId = dao.updateCourse(course);
                    Toast.makeText(EditCourseActivity.this, "Course Modified Successfully!", Toast.LENGTH_SHORT).show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("courseId", (int) courseId);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
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
