package view.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.vsolv.bigflow.R;

import java.util.ArrayList;
import java.util.List;

import DataBase.GetData;
import models.Common;
import models.HistroryAdapter;
import models.UserDetails;
import models.Variables;
import presenter.NetworkResult;

public class HistoryActivity extends AppCompatActivity {
    RecyclerView RV_History;
    TextView empty_view, reload;
    List<Variables.History> historyList;
    private ProgressDialog progressDialog;
    private GetData getData;
    private int customer_gid;
    private HistroryAdapter histroryAdapter;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent() != null) {
            customer_gid = getIntent().getIntExtra("customer_gid", 0);
        }

        loadView();
        initializeView();
        loadData();
    }

    private void loadView() {
        RV_History = findViewById(R.id.RV_History);
        empty_view = findViewById(R.id.empty_view);
        reload = findViewById(R.id.custReload);
        linearLayout = (LinearLayout) findViewById(R.id.linearDirect);
    }

    private void initializeView() {
        historyList = new ArrayList<>();
        RV_History.setHasFixedSize(true);
        RV_History.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        getData = new GetData(getApplicationContext());
    }

    private void loadData() {
        progressDialog.show();
        if (!Common.isOnline(getApplicationContext())) {
            Snackbar.with(getApplicationContext(), null)
                    .type(Type.WARNING)
                    .message("Please Check Internet Connection.")
                    .duration(Duration.SHORT)
                    .fillParent(true)
                    .textAlign(Align.LEFT)
                    .show();
            setVisibility(View.GONE, View.GONE, View.VISIBLE);
            progressDialog.cancel();
            return;
        }
        historyList = getData.HistoryList(customer_gid, UserDetails.getUser_id(), new NetworkResult() {
            @Override
            public void handlerResult(String result) {
                setAdapter();
                progressDialog.cancel();
            }

            @Override
            public void handlerError(String result) {
                progressDialog.cancel();
            }
        });

    }

    private void setAdapter() {
        histroryAdapter = new HistroryAdapter(getApplicationContext(), historyList);
        RV_History.setAdapter(histroryAdapter);
        if (histroryAdapter.getItemCount() == 0) {
            empty_view.setText("" + getResources().getString(R.string.no_data_available));
            setVisibility(View.GONE, View.VISIBLE, View.GONE);
        } else {
            setVisibility(View.VISIBLE, View.GONE, View.GONE);
        }
    }

    public void setVisibility(int recycleView, int emptyView, int reloadView) {
        linearLayout.setVisibility(recycleView);
        empty_view.setVisibility(emptyView);
        reload.setVisibility(reloadView);
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
