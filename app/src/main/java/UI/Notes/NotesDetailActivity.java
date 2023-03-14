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

        shareNote = findViewById(R.id.shareNoteButton);
        shareNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShareOptions();
            }
        });
    }

    private void updateUI() {
        noteDetailTitleTextView.setText(note.getNoteTitle());
        noteDetailTextView.setText(note.getNote());
    }

    private void showShareOptions() {
        PopupMenu popupMenu = new PopupMenu(this, shareNote);
        popupMenu.getMenuInflater().inflate(R.menu.share_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.share_message:
                        shareMessage();
                        return true;
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

    private void shareMessage() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:"));  // This ensures only SMS apps respond
        intent.putExtra("sms_body", note.getNote());  // The message to share
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Handle the case where no SMS app is available
            Toast.makeText(this, "No messaging app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Note from Term Tracker");
        intent.putExtra(Intent.EXTRA_TEXT, note.getNote());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Share via email"));
        } else {
            // Handle the case where no email app is available
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }
    }

}