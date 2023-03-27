package UI.Courses;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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

        // GETS THE INTENT FROM THE PREVIOUS ACTIVITY
        int termId = getIntent().getIntExtra("termId", -1);
        int courseId = getIntent().getIntExtra("courseId", -1);

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
                intent.putExtra("courseId", course.getCourseId());
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
                intent.putExtra("courseId", course.getCourseId());
                startActivity(intent);
            }
        });

        aRecyclerView.setAdapter(aAdapter);
        nRecyclerView.setAdapter(nAdapter);

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
                intent.putExtra("startDate", course.getStartDate());
                intent.putExtra("endDate", course.getEndDate());
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
        getMenuInflater().inflate(R.menu.coursemenuitems, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notifyStart:
                Toast.makeText(getApplicationContext(), "A notification is now set to alert you at 9AM on the day of your course start date", Toast.LENGTH_LONG).show();
                String startDateString = course.getStartDate();
                Date startDate = null;
                try {
                    startDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(startDateString);
                } catch (ParseException pe) {
                    pe.printStackTrace();
                }
                if (startDate != null) {
                    Calendar sCalendar = Calendar.getInstance();
                    sCalendar.setTimeInMillis(startDate.getTime());
                    sCalendar.set(Calendar.HOUR_OF_DAY, 8);
                    sCalendar.set(Calendar.MINUTE, 6);
                    sCalendar.set(Calendar.SECOND, 0);

                    Intent startIntent = new Intent(CourseDetailActivity.this, NotifyReceiver.class);
                    startIntent.putExtra("key", " Your course: " + course.getCourseTitle() + ": Starts Today!");
                    PendingIntent sPendingIntent = PendingIntent.getBroadcast(CourseDetailActivity.this, MainActivity.numAlert++, startIntent, PendingIntent.FLAG_MUTABLE);
                    AlarmManager sAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    sAlarmManager.set(AlarmManager.RTC_WAKEUP, sCalendar.getTimeInMillis(), sPendingIntent);
                }
                return true;

                case R.id.notifyEnd:
                    Toast.makeText(getApplicationContext(), "A notification is now set to alert you at 9AM on the day of your course end date", Toast.LENGTH_LONG).show();
                String endDateString = course.getEndDate();
                Date endDate = null;
                try {
                    endDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(endDateString);
                } catch (ParseException pe) {
                    pe.printStackTrace();
                }
                if (endDate != null) {
                    Calendar eCalendar = Calendar.getInstance();
                    eCalendar.setTimeInMillis(endDate.getTime());
                    eCalendar.set(Calendar.HOUR_OF_DAY, 9);
                    eCalendar.set(Calendar.MINUTE, 0);
                    eCalendar.set(Calendar.SECOND, 0);


                    Intent endIntent = new Intent(CourseDetailActivity.this, NotifyReceiver.class);
                    endIntent.putExtra("key", "Your course: " + course.getCourseTitle() + "Ends Today");
                    PendingIntent ePendingIntent = PendingIntent.getBroadcast(CourseDetailActivity.this, MainActivity.numAlert++, endIntent, PendingIntent.FLAG_MUTABLE);
                    AlarmManager eAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    eAlarmManager.set(AlarmManager.RTC_WAKEUP, eCalendar.getTimeInMillis(), ePendingIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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
