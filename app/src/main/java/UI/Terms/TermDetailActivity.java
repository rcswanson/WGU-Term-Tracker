package UI.Terms;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.termtrackerfinal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import Adapters.CourseAdapter;
import Adapters.TermAdapter;
import Database.AppDatabase;
import Database.CourseDAO;
import Database.TermDAO;
import Entities.CourseTable;
import Entities.TermTable;
import UI.Courses.AddCourseActivity;
import UI.Courses.CourseDetailActivity;
import UI.MainActivity;

public class TermDetailActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CourseAdapter adapter;
    List<CourseTable> courseList;

    private TermTable term;
    private TextView termTitleTextView;
    private TextView termStartDateTextView;
    private TextView termEndDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);

        recyclerView = findViewById(R.id.coursesView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        courseList = new ArrayList<>();
        adapter = new CourseAdapter(courseList, new CourseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CourseTable course) {
                Intent intent = new Intent(TermDetailActivity.this, CourseDetailActivity.class);
                intent.putExtra("courseId", course.getCourseId());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        termTitleTextView = findViewById(R.id.termTitleTextView);
        termStartDateTextView = findViewById(R.id.startDateTextView);
        termEndDateTextView = findViewById(R.id.endDateTextView);

        int termId = getIntent().getIntExtra("termId", -1);
        if (termId != -1) {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            TermDAO dao = db.termDAO();
            term = dao.getTermById(termId);

            if (term != null) {
                updateUI();
            }
        }

        FloatingActionButton deleteTerm = findViewById(R.id.DeleteTermButton);
        deleteTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (term != null) {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    CourseDAO courseDAO = db.courseDAO();
                    List<CourseTable> courses = courseDAO.getCoursesForTerm(term.getTermId());
                    if (courses.isEmpty()) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(TermDetailActivity.this);
                        alert.setMessage("Are you sure you want to delete this term?");
                        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                                TermDAO dao = db.termDAO();
                                dao.deleteTerm(term);
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                        alert.setNegativeButton("Cancel", null);
                        alert.show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Cannot delete term. There are courses associated with it.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        FloatingActionButton addCourse = findViewById(R.id.AddCourseButton);
        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TermDetailActivity.this, AddCourseActivity.class);
                intent.putExtra("termId", termId);
                startActivity(intent);
            }
        });
    }

    private void loadCourses(int termId) {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        CourseDAO dao = db.courseDAO();
        courseList = dao.getCoursesForTerm(termId);
        adapter.setCourseList(courseList);
    }


    private void updateUI() {
        termTitleTextView.setText(term.getTermTitle());
        termStartDateTextView.setText(term.getStartDate());
        termEndDateTextView.setText(term.getEndDate());

        loadCourses(term.getTermId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCourses(term.getTermId());
    }
}
