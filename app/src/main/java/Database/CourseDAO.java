package Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Entities.CourseTable;

@Dao
public interface CourseDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCourse(CourseTable course);

    @Update
    int updateCourse(CourseTable course);

    @Delete
    void deleteCourse(CourseTable course);

    @Query("SELECT * FROM courses_table")
    List<CourseTable> getAllCourses();

    @Query("SELECT last_insert_rowid()")
    int getLastInsertedId();

    @Query("SELECT * FROM courses_table WHERE courseId = :courseId")
    CourseTable getCourseById(int courseId);

    @Query("SELECT * FROM courses_table WHERE courseTermId = :termId")
    List<CourseTable> getCoursesForTerm(int termId);
}
