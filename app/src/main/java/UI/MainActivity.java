package UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.termtrackerfinal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import Adapters.TermAdapter;
import Database.AppDatabase;
import Database.TermDAO;
import Entities.TermTable;
import UI.Terms.AddTermActivity;
import UI.Terms.TermDetailActivity;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TermAdapter adapter;
    List<TermTable> termList;

    public static int numAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.termsView);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        termList = new ArrayList<>();
        adapter = new TermAdapter(termList, new TermAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TermTable term) {
                Intent intent = new Intent(MainActivity.this, TermDetailActivity.class);
                intent.putExtra("termId", term.getTermId());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        loadTerms();

        // Add Term Button
        FloatingActionButton addTerm = findViewById(R.id.addTermButton);
        addTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTermActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadTerms() {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        TermDAO dao = db.termDAO();

        termList = dao.getAllTerms();
        adapter.setTermList(termList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTerms();
    }

}