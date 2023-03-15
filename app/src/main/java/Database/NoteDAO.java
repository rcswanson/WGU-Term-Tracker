package Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Entities.NotesTable;

@Dao
public interface NoteDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNote(NotesTable note);

    @Update
    int updateNote(NotesTable note);

    @Delete
    void deleteNote(NotesTable note);

    @Query("SELECT * FROM notes_table")
    List<NotesTable> getAllNotes();

    @Query("SELECT * FROM notes_table WHERE noteId = :noteId")
    NotesTable getNoteById(int noteId);

    @Query("SELECT * FROM notes_table WHERE noteCourseId = :courseId")
    List<NotesTable> getNotesForCourse(int courseId);

}
