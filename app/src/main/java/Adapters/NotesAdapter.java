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

import Entities.NotesTable;
import Entities.NotesTable;
import UI.MainActivity;
import UI.Notes.NotesDetailActivity;
import UI.Terms.TermDetailActivity;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private List<NotesTable> notesList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(NotesTable note);
    }

    public NotesAdapter(List<NotesTable> notesList, OnItemClickListener listener) {
        this.notesList = notesList;
        this.listener = listener;
    }

    public void setNotesList(List<NotesTable> notesList) {
        this.notesList = notesList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NotesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        NotesTable note = notesList.get(position);
        holder.noteTitleTextView.setText(note.getNoteTitle());
        holder.noteTextView.setText(note.getNote());
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public void updateDataSet(List<NotesTable> newDataSet) {
        notesList = newDataSet;
        notifyDataSetChanged();
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView noteTitleTextView;
        public TextView noteTextView;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitleTextView = itemView.findViewById(R.id.noteTitleTextView);
            noteTextView = itemView.findViewById(R.id.noteTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                NotesTable note = notesList.get(position);
                Intent intent = new Intent(view.getContext(), NotesDetailActivity.class);
                intent.putExtra("noteId", note.getNoteId());
                view.getContext().startActivity(intent);
            }
        }
    }

}
