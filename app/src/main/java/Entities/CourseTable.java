package Entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "courses_table", foreignKeys = @ForeignKey(
        entity = TermTable.class, parentColumns = "termId",
        childColumns = "courseTermId", onDelete = ForeignKey.CASCADE))
public class CourseTable {

    @PrimaryKey(autoGenerate = true)
    private int courseId;

    private String courseTitle;
    private String startDate;
    private String endDate;
    private String status;
    private String instName;
    private String instEmail;
    private String instPhone;

    private int courseTermId;

    public CourseTable(String courseTitle, String startDate, String endDate, String status, String instName, String instEmail, String instPhone, int courseTermId) {
        this.courseTitle = courseTitle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.instName = instName;
        this.instEmail = instEmail;
        this.instPhone = instPhone;
        this.courseTermId = courseTermId;
    }

    public int getCourseId() { return courseId; }

    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseTitle() { return courseTitle; }

    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getStartDate() { return startDate; }

    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }

    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getInstName() { return instName; }

    public void setInstName(String instName) { this.instName = instName; }

    public String getInstEmail() { return instEmail; }

    public void setInstEmail(String instEmail) { this.instEmail = instEmail; }

    public String getInstPhone() { return instPhone; }

    public void setInstPhone(String instPhone) { this.instPhone = instPhone; }

    public int getCourseTermId() { return courseTermId; }

    public void setCourseTermId(int courseTermId) { this.courseTermId = courseTermId; }

}
