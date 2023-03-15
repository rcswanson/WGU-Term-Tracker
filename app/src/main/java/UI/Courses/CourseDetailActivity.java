package UI.Courses;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.termtrackerfinal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Adapters.AssessmentAdapter;
import Adapters.NotesAdapter;
import Database.AppDatabase;
import Database.AssessmentDAO;
import Database.CourseDAO;
import Database.NoteDAO;
import Database.TermDAO;
import Entities.AssessmentTable;
import Entities.CourseTable;
import Entities.NotesTable;
import UI.Assessments.AddAssessmentActivity;
import UI.Assessments.AssessmentDetailActivity;
import UI.Notes.AddNotesActivity;
import UI.Notes.NotesDetailActivity;
import UI.Terms.TermDetailActivity;

public class CourseDetailActivity extends AppCompatActivity {

    RecyclerView aRecyclerView;
    RecyclerView nRecyclerView;
    AssessmentAdapter aAdapter;
    NotesAdapter nAdapter;
    List<AssessmentTable> assessmentList;
    List<NotesTable> notesList;

    private CourseTable course;
    private TextView courseTitleTextView;
    private TextView courseStartTextView;
    private TextView courseEndTextView;
    private TextView courseStatusTextView;
    private TextView instNameTextView;
    private TextView instEmailTextView;
    private TextView instPhoneTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        int termId = getIntent().getIntExtra("termId", -1);

        aRecyclerView = findViewById(R.id.assessmentsView);
        aRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        nRecyclerView = findViewById(R.id.notesView);
        nRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        assessmentList = new ArrayList<>();
        aAdapter = new AssessmentAdapter(assessmentList, new AssessmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AssessmentTable assessment) {
                Intent intent = new Intent(CourseDetailActivity.this, AssessmentDetailActivity.class);
                intent.putExtra("assessmentId", assessment.getAssessId());
                intent.putExtra("courseId", course.getCourseId());
                startActivity(intent);
            }
        });

        notesList = new ArrayList<>();
        nAdapter = new NotesAdapter(notesList, new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NotesTable note) {
                Intent intent = new Intent(CourseDetailActivity.this, NotesDetailActivity.class);
                intent.putExtra("noteId", note.getNoteId());
                intent.putExtra("courseId", course.getCourseId());
                startActivity(intent);
            }
        });

        aRecyclerView.setAdapter(aAdapter);
        nRecyclerView.setAdapter(nAdapter);


        // Initialize views
        courseTitleTextView = findViewById(R.id.courseTitleTextView);
        courseStartTextView = findViewById(R.id.courseStartTextView);
        courseEndTextView = findViewById(R.id.courseEndTextView);
        courseStatusTextView = findViewById(R.id.courseStatusTextView);
        instNameTextView = findViewById(R.id.instNameTextView);
        instEmailTextView = findViewById(R.id.instEmailTextView);
        instPhoneTextView = findViewById(R.id.instPhoneTextView);

        int courseId = getIntent().getIntExtra("courseId", -1);
        if (courseId != -1) {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            CourseDAO dao = db.courseDAO();
            course = dao.getCourseById(courseId);

            if (course != null) {
                updateUI();
            }
        }

        FloatingActionButton addAssessment = findViewById(R.id.AddAssessmentButton);
        addAssessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseDetailActivity.this, AddAssessmentActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });

        FloatingActionButton deleteCourse = findViewById(R.id.DeleteCourseButton);
        deleteCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (course != null) {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    AssessmentDAO dao = db.assessmentDAO();
                    List<AssessmentTable> assessments = dao.getAssessmentsForCourse(course.getCourseId());
                    if (assessments.isEmpty()) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(CourseDetailActivity.this);
                        alert.setMessage("Are you sure you want to delete this course?");
                        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                                CourseDAO dao = db.courseDAO();
                                dao.deleteCourse(course);
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                        alert.setNegativeButton("Cancel", null);
                        alert.show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Cannot delete term. There are assessments associated with it.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        FloatingActionButton editCourse = findViewById(R.id.EditCourseButton);
        editCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseDetailActivity.this, EditCourseActivity.class);
                intent.putExtra("courseId", courseId);
                intent.putExtra("termId", termId);
                startActivity(intent);
            }
        });

        FloatingActionButton addNote = findViewById(R.id.addNoteButton);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseDetailActivity.this, AddNotesActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });
    }

    private void loadAssessNotes(int courseId) {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        AssessmentDAO aDao = db.assessmentDAO();
        NoteDAO nDao = db.noteDAO();
        assessmentList = aDao.getAssessmentsForCourse(courseId);
        notesList = nDao.getNotesForCourse(courseId);
        aAdapter.setAssessmentList(assessmentList);
        nAdapter.setNotesList(notesList);
    }

    private void updateUI() {
        // Set text for the views
        courseTitleTextView.setText(course.getCourseTitle());
        courseStartTextView.setText(course.getStartDate());
        courseEndTextView.setText(course.getEndDate());
        courseStatusTextView.setText(course.getStatus());
        instNameTextView.setText(course.getInstName());
        instEmailTextView.setText(course.getInstEmail());
        instPhoneTextView.setText(course.getInstPhone());

        loadAssessNotes(course.getCourseId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAssessNotes(course.getCourseId());
        updateUI();
    }

}
