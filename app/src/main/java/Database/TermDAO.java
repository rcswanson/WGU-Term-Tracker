package Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Entities.TermTable;

@Dao
public interface TermDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTerm(TermTable term);

    @Update
    void updateTerm(TermTable term);

    @Delete
    void deleteTerm(TermTable term);

    @Query("SELECT * FROM terms_table")
    List<TermTable> getAllTerms();

    @Query("SELECT last_insert_rowid()")
    int getLastInsertedId();

    @Query("SELECT * FROM terms_table WHERE termId = :termId")
    TermTable getTermById(int termId);

}

