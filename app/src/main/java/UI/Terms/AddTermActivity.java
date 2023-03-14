package UI.Terms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.termtrackerfinal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import Adapters.TermAdapter;
import Database.AppDatabase;
import Database.TermDAO;
import Entities.TermTable;
import UI.MainActivity;

public class AddTermActivity extends AppCompatActivity {

    List<TermTable> termTableList;
    RecyclerView recyclerView;
    TermAdapter termAdapter;

    private EditText termTitleEditText;
    private EditText startDateEditText;
    private EditText endDateEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_term);

        TermDAO dao = AppDatabase.getInstance(getApplicationContext()).termDAO();

        termTitleEditText = findViewById(R.id.termTitleEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);

        FloatingActionButton AddTermToTable = findViewById(R.id.addTermToTableButton);
        AddTermToTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String termTitle = termTitleEditText.getText().toString();
                String startDate = startDateEditText.getText().toString();
                String endDate = endDateEditText.getText().toString();
                TermTable term = new TermTable(termTitle, startDate, endDate);
                termTableList = dao.getAllTerms();
                if (termTitle.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Field(s) Are Not Filled In.", Toast.LENGTH_SHORT).show();
                } else {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    TermDAO dao = db.termDAO();
                    long termId = dao.insertTerm(term);

                    if (termId > 0) {
                        Toast.makeText(AddTermActivity.this, "Term Added Successfully!", Toast.LENGTH_SHORT).show();

                        // Send back the new term ID to the calling activity
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("termId", (int) termId);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                }
            }
        });

        FloatingActionButton CancelTerm = findViewById(R.id.cancelAddTermButton);
        CancelTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}