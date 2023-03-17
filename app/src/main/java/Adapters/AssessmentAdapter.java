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

import Database.AssessmentDAO;
import Entities.AssessmentTable;
import Entities.CourseTable;
import Entities.TermTable;
import UI.Assessments.AssessmentDetailActivity;
import UI.Courses.CourseDetailActivity;
import UI.Terms.TermDetailActivity;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.AssessmentViewHolder> {

    private List<AssessmentTable> assessmentList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AssessmentTable assessment);
    }

    public AssessmentAdapter(List<AssessmentTable> assessmentList, OnItemClickListener listener) {
        this.assessmentList = assessmentList;
        this.listener = listener;
    }

    public void setAssessmentList(List<AssessmentTable> assessmentList) {
        this.assessmentList = assessmentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AssessmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assessment_item, parent, false);
        return new AssessmentAdapter.AssessmentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentAdapter.AssessmentViewHolder holder, int position) {
        AssessmentTable assessment = assessmentList.get(position);
        holder.assessmentTitleTextView.setText(assessment.getTitle());
        holder.assessmentTypeTextView.setText(assessment.getAssessmentType());
        holder.assessmentDueDateTextView.setText(String.valueOf(assessment.getDueDate()));
    }

    @Override
    public int getItemCount() {
        return assessmentList.size();
    }

    public void updateDataSet(List<AssessmentTable> newDataSet) {
        assessmentList = newDataSet;
        notifyDataSetChanged();
    }

    public class AssessmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView assessmentTitleTextView;
        public TextView assessmentTypeTextView;
        public TextView assessmentDueDateTextView;

        public AssessmentViewHolder(@NonNull View itemView) {
            super(itemView);
            assessmentTitleTextView = itemView.findViewById(R.id.assessmentTitleTextView);
            assessmentTypeTextView = itemView.findViewById(R.id.assessmentTypeTextView);
            assessmentDueDateTextView = itemView.findViewById(R.id.assessmentDueDateTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                AssessmentTable assessment = assessmentList.get(position);
                Intent intent = new Intent(view.getContext(), AssessmentDetailActivity.class);
                intent.putExtra("assessmentId", assessment.getAssessId());
                view.getContext().startActivity(intent);
            }
        }
    }
}
