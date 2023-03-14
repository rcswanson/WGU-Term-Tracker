package UI.Courses;

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
        import Database.CourseDAO;
        import Database.TermDAO;
        import Entities.CourseTable;
        import Entities.TermTable;

public class EditCourseActivity extends AppCompatActivity {

    List<CourseTable> courseTableList;
    CourseTable course;

    private EditText courseTitleEditText;
    private EditText courseStartEditText;
    private EditText courseEndEditText;
    private Spinner statusSpinner;
    private EditText instNameTextView;
    private EditText instEmailTextView;
    private EditText instPhoneTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        CourseDAO dao = AppDatabase.getInstance(getApplicationContext()).courseDAO();

        // INITIALIZE UI ELEMENTS
        courseTitleEditText = findViewById(R.id.courseTitleEditText);
        courseStartEditText = findViewById(R.id.courseStartEditText);
        courseEndEditText = findViewById(R.id.courseEndEditText);
        statusSpinner = findViewById(R.id.statusSpinner);
        instNameTextView = findViewById(R.id.instNameTextView);
        instEmailTextView = findViewById(R.id.instEmailTextView);
        instPhoneTextView = findViewById(R.id.instPhoneTextView);

//        courseTitleEditText.setText(course.getCourseTitle());
//        courseStartEditText.setText(course.getStartDate());
//        courseEndEditText.setText(course.getEndDate());
//        statusSpinner.setPrompt(course.getStatus());
//        instNameTextView.setText(course.getInstName());
//        instEmailTextView.setText(course.getInstEmail());
//        instPhoneTextView.setText(course.getInstPhone());

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
                String start = courseStartEditText.getText().toString();
                String end = courseEndEditText.getText().toString();
                String status = statusSpinner.getSelectedItem().toString();
                String instName = instNameTextView.getText().toString();
                String instEmail = instEmailTextView.getText().toString();
                String instPhone = instPhoneTextView.getText().toString();
                int termId = getIntent().getIntExtra("termId", -1);

                CourseTable course = new CourseTable(title, start, end, status, instName, instEmail, instPhone, termId);
                courseTableList = dao.getAllCourses();

                // VALIDATE USER INPUT
                if (title.isEmpty() || start.isEmpty() || end.isEmpty() ||
                        instName.isEmpty() || instEmail.isEmpty() || instPhone.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Field(s) Are Not Filled In.", Toast.LENGTH_SHORT).show();
                } else {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    CourseDAO dao = db.courseDAO();
                    int courseId = dao.updateCourse(course);

                    if (courseId == course.getCourseId()) {
                        Toast.makeText(EditCourseActivity.this, "Course Saved Successfully!", Toast.LENGTH_SHORT).show();

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("courseId", (int) courseId);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                }
            }
        });

        // CANCEL ADD COURSE AND RETURN TO TERM
        FloatingActionButton cancelAddCourseButton = findViewById(R.id.cancelEditCourseButton);
        cancelAddCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // close the activity
            }
        });
    }
}
