package UI.Courses;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.termtrackerfinal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Adapters.AssessmentAdapter;
import Adapters.NotesAdapter;
import Database.AppDatabase;
import Database.AssessmentDAO;
import Database.CourseDAO;
import Database.NoteDAO;
import Entities.AssessmentTable;
import Entities.CourseTable;
import Entities.NotesTable;
import UI.Assessments.AddAssessmentActivity;
import UI.Assessments.AssessmentDetailActivity;
import UI.MainActivity;
import UI.Notes.AddNotesActivity;
import UI.Notes.NotesDetailActivity;
import Utilities.NotifyReceiver;

public class CourseDetailActivity extends AppCompatActivity {

    RecyclerView aRecyclerView;
    RecyclerView nRecyclerView;
    AssessmentAdapter aAdapter;
    NotesAdapter nAdapter;
    List<AssessmentTable> assessmentList;
    List<NotesTable> notesList;

    private CourseTable courseTable;
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

        // GETS THE INTENT FROM THE PREVIOUS ACTIVITY
        int termId = getIntent().getIntExtra("termId", -1);
        int courseId = getIntent().getIntExtra("courseId", -1);

        CourseDAO courseDao = AppDatabase.getInstance(this).courseDAO();
        CourseTable course = courseDao.getCourseById(courseId);

        Intent startIntent = new Intent(this, NotifyReceiver.class);
        startIntent.putExtra("key", "Your Course " + course.getCourseTitle() + " Starts Today!");
        startIntent.putExtra("notification_id", MainActivity.numAlert++); // unique id for the notification
        PendingIntent startPendingIntent = PendingIntent.getBroadcast(this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get the current date and time
        Calendar startCalendar = Calendar.getInstance();

        // Set the time to 8:00 AM
        startCalendar.set(Calendar.HOUR_OF_DAY, 5);
        startCalendar.set(Calendar.MINUTE, 41);
        startCalendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // Start Date
        String startDate = course.getStartDate();
        Date date = null;
        try {
            date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(startDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        startCalendar.setTime(date);
        Long startTrigger = startCalendar.getTimeInMillis();
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTrigger, startPendingIntent);

        // Get the current date and time
        Calendar endCalendar = Calendar.getInstance();

        // Set the time to 8:00 AM
        endCalendar.set(Calendar.HOUR_OF_DAY, 8);
        endCalendar.set(Calendar.MINUTE, 0);
        endCalendar.set(Calendar.SECOND, 0);

        // End Date
        Intent endIntent = new Intent(this, NotifyReceiver.class);
        endIntent.putExtra("key", "Your Course " + course.getCourseTitle() + " Ends Today!");
        endIntent.putExtra("notification_id", MainActivity.numAlert++); // unique id for the notification
        PendingIntent endPendingIntent = PendingIntent.getBroadcast(this, 0, endIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String endDate = course.getEndDate();
        try {
            date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(endDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        endCalendar.setTime(date);
        Long endTrigger = startCalendar.getTimeInMillis();
        alarmManager.set(AlarmManager.RTC_WAKEUP, endTrigger, endPendingIntent);


        // SETS RECYCLER VIEWS FROM THE XML FILE
        aRecyclerView = findViewById(R.id.assessmentsView);
        aRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        nRecyclerView = findViewById(R.id.notesView);
        nRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // SENDS USER TO A DETAIL ACTIVITY WHEN CLICKING ON AN ASSESSMENT
        assessmentList = new ArrayList<>();
        aAdapter = new AssessmentAdapter(assessmentList, new AssessmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AssessmentTable assessment) {
                Intent intent = new Intent(CourseDetailActivity.this, AssessmentDetailActivity.class);
                intent.putExtra("assessmentId", assessment.getAssessId());
                intent.putExtra("courseId", courseTable.getCourseId());
                startActivity(intent);
            }
        });

        // SENDS USER TO DETAIL ACTIVITY WHEN CLICKING ON A NOTE
        notesList = new ArrayList<>();
        nAdapter = new NotesAdapter(notesList, new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NotesTable note) {
                Intent intent = new Intent(CourseDetailActivity.this, NotesDetailActivity.class);
                intent.putExtra("noteId", note.getNoteId());
                intent.putExtra("courseId", courseTable.getCourseId());
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
                if (courseTable != null) {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    AssessmentDAO dao = db.assessmentDAO();
                    List<AssessmentTable> assessments = dao.getAssessmentsForCourse(courseTable.getCourseId());
                    if (assessments.isEmpty()) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(CourseDetailActivity.this);
                        alert.setMessage("Are you sure you want to delete this course?");
                        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                                CourseDAO dao = db.courseDAO();
                                dao.deleteCourse(courseTable);
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
                intent.putExtra("startDate", courseTable.getStartDate());
                intent.putExtra("endDate", courseTable.getEndDate());
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.coursedetailrefresh, menu);
        return true;
    }


    private void loadAssessNotes(int courseId) {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        AssessmentDAO aDao = db.assessmentDAO();
        NoteDAO nDao = AppDatabase.getInstance(this).noteDAO();
        assessmentList = aDao.getAssessmentsForCourse(courseId);
        notesList = nDao.getNotesForCourse(courseId);
        aAdapter.setAssessmentList(assessmentList);
        nAdapter.setNotesList(notesList);
    }

    private void updateUI() {
        // Set text for the views
        courseTitleTextView.setText(courseTable.getCourseTitle());
        courseStartTextView.setText(courseTable.getStartDate());
        courseEndTextView.setText(courseTable.getEndDate());
        courseStatusTextView.setText(courseTable.getStatus());
        instNameTextView.setText(courseTable.getInstName());
        instEmailTextView.setText(courseTable.getInstEmail());
        instPhoneTextView.setText(courseTable.getInstPhone());

        loadAssessNotes(courseTable.getCourseId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAssessNotes(courseTable.getCourseId());
        updateUI();
    }

}
