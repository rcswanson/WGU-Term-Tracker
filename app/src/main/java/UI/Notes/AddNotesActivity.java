package UI.Notes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.termtrackerfinal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import Database.AppDatabase;
import Database.NoteDAO;
import Entities.NotesTable;
import UI.Terms.AddTermActivity;

public class AddNotesActivity extends AppCompatActivity {

    List<NotesTable> noteTableList;

    private EditText noteTitleEditText;
    private EditText noteMultiLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        NoteDAO dao = AppDatabase.getInstance(getApplicationContext()).noteDAO();

        noteTitleEditText = findViewById(R.id.noteTitleEditText);
        noteMultiLine = findViewById(R.id.noteMultiLine);

        FloatingActionButton addNote = findViewById(R.id.addNoteToCourseButton);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = noteTitleEditText.getText().toString();
                String noteText = noteMultiLine.getText().toString();
                int courseId = getIntent().getIntExtra("courseId", -1);

                NotesTable note = new NotesTable(title, noteText, courseId);
                noteTableList = dao.getAllNotes();
                if (title.isEmpty() || noteText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Field(s) Are Not Filled In.", Toast.LENGTH_SHORT).show();
                } else {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    NoteDAO dao = db.noteDAO();
                    long noteId = dao.insertNote(note);

                    if (noteId > 0) {
                        Toast.makeText(AddNotesActivity.this, "Note Added Successfully!", Toast.LENGTH_SHORT).show();

                        // Send back the new term ID to the calling activity
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("noteId", (int) noteId);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                }
            }
        });

        FloatingActionButton cancelNote = findViewById(R.id.cancelAddNoteButton);
        cancelNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });
    }
}