package UI.Terms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.termtrackerfinal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_term);

        TermDAO dao = AppDatabase.getInstance(getApplicationContext()).termDAO();

        termTitleEditText = findViewById(R.id.termTitleEditText);
        startDatePicker = findViewById(R.id.startDatePicker);
        endDatePicker = findViewById(R.id.endDatePicker);

        Calendar sCalendar = Calendar.getInstance();
        int sYear = sCalendar.get(Calendar.YEAR);
        int sMonth = sCalendar.get(Calendar.MONTH);
        int sDay = sCalendar.get(Calendar.DAY_OF_MONTH);
        Calendar eCalendar = Calendar.getInstance();
        int eYear = eCalendar.get(Calendar.YEAR);
        int eMonth = eCalendar.get(Calendar.MONTH);
        int eDay = eCalendar.get(Calendar.DAY_OF_MONTH);

        startDatePicker.init(sYear, sMonth, sDay, null);
        endDatePicker.init(eYear, eMonth, eDay, null);

        FloatingActionButton AddTermToTable = findViewById(R.id.addTermToTableButton);
        AddTermToTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String termTitle = termTitleEditText.getText().toString();

                int sDay = startDatePicker.getDayOfMonth();
                int sMonth = startDatePicker.getMonth();
                int sYear = startDatePicker.getYear();

                int eDay = endDatePicker.getDayOfMonth();
                int eMonth = endDatePicker.getMonth();
                int eYear = endDatePicker.getYear();

                sCalendar.set(sYear, sMonth, sDay);
                eCalendar.set(eYear, eMonth, eDay);

                long sDateBeforeFormat = sCalendar.getTimeInMillis();
                long eDateBeforeFormat = eCalendar.getTimeInMillis();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                String formattedStart = dateFormat.format(new Date(sDateBeforeFormat));
                String formattedEnd = dateFormat.format(new Date(eDateBeforeFormat));

                TermTable term = new TermTable(termTitle, formattedStart, formattedEnd);
                termTableList = dao.getAllTerms();

                // VALIDATE USER INPUT
                if (termTitle.isEmpty() || formattedStart.isEmpty() || formattedEnd.isEmpty()) {
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