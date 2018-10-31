package view.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.vsolv.bigflow.R;

public class FilterAddScheduleActivity extends AppCompatActivity {
    TextView dateFilter;
    ListView listViewFilter;
    String[] filterList;
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_add_schedule);
        //ActionBar actionBar=getActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Filter");
        loadView();
        initializeView();
        loadData();
    }

    private void loadView() {
        dateFilter = findViewById(R.id.txtASFDate);
        listViewFilter = findViewById(R.id.LV_ASFilter);
    }

    private void initializeView() {
        filterList = new String[]{"Employee", "Route", "Territory", "Mode", "Category", "Type", "Size"};
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, filterList);
    }

    private void loadData() {
        listViewFilter.setAdapter(arrayAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
