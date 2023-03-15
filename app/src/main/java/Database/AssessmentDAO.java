package Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Entities.AssessmentTable;

@Dao
public interface AssessmentDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAssessment(AssessmentTable assessment);

    @Update
    int updateAssessment(AssessmentTable assessment);

    @Delete
    void deleteAssessment(AssessmentTable assessment);

    @Query("SELECT * FROM assessments_table")
    List<AssessmentTable> getAllAssessments();

    @Query("SELECT last_insert_rowid()")
    int getLastInsertedId();

    @Query("SELECT * FROM assessments_table WHERE assessId = :assessId")
    AssessmentTable getAssessmentById(int assessId);

    @Query("SELECT * FROM assessments_table WHERE  assessCourseId = :courseId")
    List<AssessmentTable> getAssessmentsForCourse(int courseId);

}
