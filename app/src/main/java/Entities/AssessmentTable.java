package Entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "assessments_table", foreignKeys = @ForeignKey(
        entity = CourseTable.class, parentColumns = "courseId",
        childColumns = "assessCourseId", onDelete = ForeignKey.CASCADE))
public class AssessmentTable {

    @PrimaryKey(autoGenerate = true)
    private int assessId;
    private String assessmentType;
    private String title;
    private String dueDate;

    private int assessCourseId;

    public AssessmentTable(String assessmentType, String title, String dueDate, int assessCourseId) {
        this.assessmentType = assessmentType;
        this.title = title;
        this.dueDate = dueDate;
        this.assessCourseId = assessCourseId;
    }

    public int getAssessId() {
        return assessId;
    }

    public void setAssessId(int assessId) {
        this.assessId = assessId;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public int getAssessCourseId() { return assessCourseId; }

    public void setAssessCourseId(int assessCourseId) { this.assessCourseId = assessCourseId; }
}
