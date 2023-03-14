package Entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes_table", foreignKeys = @ForeignKey(
        entity = CourseTable.class, parentColumns = "courseId",
        childColumns = "noteCourseId", onDelete = ForeignKey.CASCADE))
public class NotesTable {

    @PrimaryKey(autoGenerate = true)
    private int noteId;

    private final int noteCourseId;
    private String noteTitle;
    private String note;

    public NotesTable(String noteTitle, String note, int noteCourseId) {
        this.noteTitle = noteTitle;
        this.note = note;
        this.noteCourseId = noteCourseId;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getNoteCourseId() { return noteCourseId; }
}
