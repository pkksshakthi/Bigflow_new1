package view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.vsolv.bigflow.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DataBase.GetData;
import models.CommentAdapter;
import models.Common;
import models.HistroryAdapter;
import models.UserDetails;
import models.Variables;
import presenter.NetworkResult;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView RV_Comment;
    private TextView empty_view, reload;
    private LinearLayout linearLayout;
    private List<Variables.Comment> commentList;
    private int customer_gid;
    List<Integer> customerList;
    private ProgressDialog progressDialog;
    private GetData getData;
    private CommentAdapter commentAdapter;
    private ImageButton btnSent;
    private EditText etxMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            customerList = new ArrayList<>();
            customer_gid = getIntent().getIntExtra("customer_gid", 0);
            customerList.add(customer_gid);
            getSupportActionBar().setTitle(getIntent().getStringExtra("customer_name"));
        }

        loadView();
        initializeView();
        loadData();
    }

    private void loadView() {
        RV_Comment = findViewById(R.id.RV_Comment);
        empty_view = findViewById(R.id.empty_view);
        reload = findViewById(R.id.commentReload);
        btnSent = findViewById(R.id.ibtnChatSent);
        etxMessage = findViewById(R.id.etxChatMessage);
    }

    private void initializeView() {
        commentList = new ArrayList<>();
        RV_Comment.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        RV_Comment.setLayoutManager(linearLayoutManager);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        getData = new GetData(getApplicationContext());
        btnSent.setOnClickListener(this);
    }

    private void loadData() {
        progressDialog.show();
        if (!Common.isOnline(getApplicationContext())) {
            Snackbar.with(getApplicationContext(), null)
                    .type(Type.WARNING)
                    .message("Please Check Internet Connection.!")
                    .duration(Duration.SHORT)
                    .fillParent(true)
                    .textAlign(Align.LEFT)
                    .show();
            setVisibility(View.GONE, View.GONE, View.VISIBLE);
            progressDialog.cancel();
            return;
        }
        commentList = getData.CommentList(customerList, new NetworkResult() {
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
        commentAdapter = new CommentAdapter(getApplicationContext(), commentList);
        RV_Comment.setAdapter(commentAdapter);
        if (commentAdapter.getItemCount() == 0) {
            empty_view.setText("" + getResources().getString(R.string.no_data_available));
            setVisibility(View.GONE, View.VISIBLE, View.GONE);
        } else {
            setVisibility(View.VISIBLE, View.GONE, View.GONE);
        }
    }

    public void setVisibility(int recycleView, int emptyView, int reloadView) {
        RV_Comment.setVisibility(recycleView);
        empty_view.setVisibility(emptyView);
        reload.setVisibility(reloadView);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ibtnChatSent) {
            getData.SetComment(customer_gid, new Date(), etxMessage.getText().toString(), new NetworkResult() {
                @Override
                public void handlerResult(String result) {
                    Variables.Comment comment = new Variables.Comment();
                    comment.employee_gid = UserDetails.getUser_id();
                    comment.employee_name = UserDetails.getUser_name();
                    comment.comment_date = Common.convertDateString(new Date(), "yyyy/MM/dd hh:mm:ss");
                    comment.comment_message = etxMessage.getText().toString();
                    comment.comment_gid = 0;
                    commentAdapter.addCommentDetails(comment);
                    etxMessage.setText("");
                    setVisibility(View.VISIBLE, View.GONE, View.GONE);
                }

                @Override
                public void handlerError(String result) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
