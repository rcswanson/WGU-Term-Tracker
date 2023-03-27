package UI.Notes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.termtrackerfinal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Database.AppDatabase;
import Database.AssessmentDAO;
import Database.NoteDAO;
import Entities.NotesTable;
import UI.Assessments.AssessmentDetailActivity;
import UI.Courses.CourseDetailActivity;
import UI.Courses.EditCourseActivity;

public class NotesDetailActivity extends AppCompatActivity {

    private NotesTable note;
    private TextView noteDetailTitleTextView;
    private TextView noteDetailTextView;
    private FloatingActionButton shareNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_detail);

        noteDetailTitleTextView = findViewById(R.id.noteDetailTitleTextView);
        noteDetailTextView = findViewById(R.id.noteDetailTextView);

        int courseId = getIntent().getIntExtra("courseId", -1);
        int noteId = getIntent().getIntExtra("noteId", -1);
        if (noteId != -1) {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            NoteDAO dao = db.noteDAO();
            note = dao.getNoteById(noteId);

            if (note != null) {
                updateUI();
            }
        }

        FloatingActionButton deleteNote = findViewById(R.id.deleteNoteButton);
        deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (note != null) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(NotesDetailActivity.this);
                    alert.setMessage("Are you sure you want to delete this assessment?");
                    alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                            NoteDAO dao = db.noteDAO();
                            dao.deleteNote(note);
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                } else {
                    Toast.makeText(NotesDetailActivity.this, "Assessment not found.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FloatingActionButton editNote = findViewById(R.id.editNoteButton);
        editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotesDetailActivity.this, EditNoteActivity.class);
                intent.putExtra("noteId", noteId);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });

        shareNote = findViewById(R.id.shareNoteButton);
        shareNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShareOptions();
            }
        });
    }

    private void showShareOptions() {
        PopupMenu popupMenu = new PopupMenu(this, shareNote);
        popupMenu.getMenuInflater().inflate(R.menu.share_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.share_email:
                        shareEmail();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void shareEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, note.getNoteTitle());
        intent.putExtra(Intent.EXTRA_TEXT, note.getNote());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Share"));
        } else {
            // Handle the case where no email app is available
            Toast.makeText(this, "No apps found", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateUI() {
        noteDetailTitleTextView.setText(note.getNoteTitle());
        noteDetailTextView.setText(note.getNote());
    }

    protected void onResume() {
        super.onResume();
        updateUI();
    }

}