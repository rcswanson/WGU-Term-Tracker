package Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.termtrackerfinal.R;

import java.util.List;

import Entities.TermTable;
import UI.MainActivity;
import UI.Terms.TermDetailActivity;

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.TermViewHolder> {

    private List<TermTable> termList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TermTable term);
    }

    public TermAdapter(List<TermTable> termList, OnItemClickListener listener) {
        this.termList = termList;
        this.listener = listener;
    }

    public void setTermList(List<TermTable> termList) {
        this.termList = termList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TermViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.term_item, parent, false);
        return new TermViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TermViewHolder holder, int position) {
        TermTable term = termList.get(position);
        holder.termTitleTextView.setText(term.getTermTitle());
        holder.startDateTextView.setText(term.getStartDate());
        holder.endDateTextView.setText(term.getEndDate());
    }

    @Override
    public int getItemCount() {
        return termList.size();
    }

    public void updateDataSet(List<TermTable> newDataSet) {
        termList = newDataSet;
        notifyDataSetChanged();
    }

    public class TermViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView termTitleTextView;
        public TextView startDateTextView;
        public TextView endDateTextView;

        public TermViewHolder(@NonNull View itemView) {
            super(itemView);
            termTitleTextView = itemView.findViewById(R.id.termTitleTextView);
            startDateTextView = itemView.findViewById(R.id.startDateTextView);
            endDateTextView = itemView.findViewById(R.id.endDateTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                TermTable term = termList.get(position);
                Intent intent = new Intent(view.getContext(), TermDetailActivity.class);
                intent.putExtra("termId", term.getTermId());
                view.getContext().startActivity(intent);
            }
        }
    }
}
