package Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.termtrackerfinal.R;

import java.util.List;

import Entities.CourseTable;
import Entities.TermTable;
import UI.Courses.CourseDetailActivity;
import UI.Terms.TermDetailActivity;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<CourseTable> courseList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(CourseTable course);
    }

    public CourseAdapter(List<CourseTable> courseList, OnItemClickListener listener) {
        this.courseList = courseList;
        this.listener = listener;
    }

    public void setCourseList(List<CourseTable> courseList) {
        this.courseList = courseList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_item, parent, false);
        return new CourseAdapter.CourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.CourseViewHolder holder, int position) {
        CourseTable course = courseList.get(position);
        holder.courseTitleTextView.setText(course.getCourseTitle());
        holder.courseStartTextView.setText(course.getStartDate());
        holder.courseEndTextView.setText(course.getEndDate());
        holder.courseStatusTextView.setText(course.getStatus());
        holder.instNameTextView.setText(course.getInstName());
        holder.instEmailTextView.setText(course.getInstEmail());
        holder.instPhoneTextView.setText(course.getInstPhone());
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public void updateDataSet(List<CourseTable> newDataSet) {
        courseList = newDataSet;
        notifyDataSetChanged();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView courseTitleTextView;
        public TextView courseStartTextView;
        public TextView courseEndTextView;
        public TextView courseStatusTextView;
        public TextView instNameTextView;
        public TextView instEmailTextView;
        public TextView instPhoneTextView;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTitleTextView = itemView.findViewById(R.id.courseTitleTextView);
            courseStartTextView = itemView.findViewById(R.id.courseStartTextView);
            courseEndTextView = itemView.findViewById(R.id.courseEndTextView);
            courseStatusTextView = itemView.findViewById(R.id.courseStatusTextView);
            instNameTextView = itemView.findViewById(R.id.instNameTextView);
            instEmailTextView = itemView.findViewById(R.id.instEmailTextView);
            instPhoneTextView = itemView.findViewById(R.id.instPhoneTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                CourseTable course = courseList.get(position);
                Intent intent = new Intent(view.getContext(), CourseDetailActivity.class);
                intent.putExtra("courseId", course.getCourseId());
                view.getContext().startActivity(intent);
            }
        }
    }
}
