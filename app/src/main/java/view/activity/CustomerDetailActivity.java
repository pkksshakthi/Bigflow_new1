package view.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.vsolv.bigflow.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import constant.Constant;
import models.UserDetails;
import network.CallbackHandler;
import presenter.VolleyCallback;

public class CustomerDetailActivity extends AppCompatActivity {
    private TableLayout tableLayout_sales, tableLayout_Collection, tableLayout_Outstanding;
    private TableRow tableRow;
    private int customer_gid;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);
        getSupportActionBar().setTitle("Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            customer_gid = getIntent().getIntExtra("customer_gid", 0);
        }

        loadView();
        initializeView();
        loadData();
    }

    private void loadView() {
        tableLayout_sales = findViewById(R.id.tbl_layout_Sales);
        tableLayout_Collection = findViewById(R.id.tbl_layout_Collection);
        tableLayout_Outstanding = findViewById(R.id.tbl_layout_Outstanding);
    }

    private void initializeView() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        String[] header_sales = {"S.No", "Invoice No", "Invoice Date", "Invoice Amount"};
        setHeader(header_sales, tableLayout_sales);


        String[] header_Collection = {"S.No", "Branch Name", "Collected Amount", "Date", "Type", "Status"};
        setHeader(header_Collection, tableLayout_Collection);


        String[] header_Outstanding = {"S.No", "Invoice No", "Net Amount", "Pending Amount"};
        setHeader(header_Outstanding, tableLayout_Outstanding);
    }

    private void loadData() {
        progressDialog.show();
        JSONObject Full_Json = new JSONObject();
        JSONObject params_Json = new JSONObject();
        try {
            params_Json.put(Constant.Action, "Common");
            params_Json.put("customer_gid", customer_gid);
            params_Json.put(Constant.emp_gid, UserDetails.getUser_id());
            params_Json.put("Outstanding_Group", "outstandingcustomer");
            Full_Json.put(Constant.params, params_Json);

        } catch (JSONException e) {
            Log.e("details", e.getMessage());
        }

        String URL = Constant.URL + "Customerview_get?Limit=5&Entity_gid=1";
        CallbackHandler.sendReqest(getApplicationContext(), Request.Method.POST, Full_Json.toString(), URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    String status = jsonObject.getString("MESSAGE");
                    if (status.equals("FOUND")) {

                        JSONArray jsonoutstanding, jsonsales, jsoncollection;
                        jsonsales = jsonObject.getJSONArray("sales_history");
                        jsoncollection = jsonObject.getJSONArray("collection_history");
                        jsonoutstanding = jsonObject.getJSONArray("outstanding_detail");

                        for (int i = 0; i < jsonsales.length(); i++) {
                            JSONObject obj_json = jsonsales.getJSONObject(i);
                            String invoice_no = obj_json.getString("invoiceheader_no");
                            String lsDate = obj_json.getString("invoiceheader_date");
                            String invoice_amt = obj_json.getString("soheader_total");

                            String detail_value[] = {Integer.toString(i + 1), invoice_no, lsDate, invoice_amt};

                            setDetail(detail_value, tableLayout_sales);// Values will be passed from here
                        }
                        for (int i = 0; i < 5; i++) {
                            JSONObject obj_json = jsoncollection.getJSONObject(i);
                            String branch_name = obj_json.getString("branch_name");
                            String collection_amount = obj_json.getString("collection_amount");
                            String collection_date = obj_json.getString("collection_date");
                            String collection_mode = obj_json.getString("collection_mode");
                            String bankdeposit_status = obj_json.getString("bankdeposit_status");

                            String detail_value[] = {Integer.toString(i + 1), branch_name, collection_amount, collection_date, collection_mode, bankdeposit_status};
                            setDetail(detail_value, tableLayout_Collection);// Values will be passed from here
                        }

                        for (int i = 0; i < jsonoutstanding.length(); i++) {
                            JSONObject obj_json = jsonoutstanding.getJSONObject(i);
                            String invoiceno = obj_json.getString("soutstanding_invoiceno");
                            String netamount = obj_json.getString("soutstanding_netamount");
                            String pending = obj_json.getString("pending");
                            String detail_value[] = {Integer.toString(i + 1), invoiceno, netamount, pending};
                            setDetail(detail_value, tableLayout_Outstanding);// Values will be passed from here
                        }

                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "Data Not Found", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    Log.e("details", e.getMessage());
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(String result) {
                Log.e("details", result);

            }
        });
    }

    private void setHeader(String[] value, TableLayout layout) {

        layout.setStretchAllColumns(true);
        layout.setShrinkAllColumns(true);
        tableRow = new TableRow(getApplicationContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);//(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams fixedlp = new TableRow.LayoutParams(100, TableRow.LayoutParams.MATCH_PARENT);//(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        tableRow.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        tableRow.setLayoutParams(lp);

        String header_value[] = value;

        for (int i = 0; i < header_value.length; i++) {
            TextView sNo = new TextView(getApplicationContext());
            sNo.setText(header_value[i]);
            sNo.setTextColor(Color.BLACK);
            sNo.setTextSize(10);
            sNo.setTypeface(Typeface.DEFAULT_BOLD);
            sNo.setGravity(Gravity.CENTER);
            if (header_value[i] == "S.No" || header_value[i].contains("Amount")) {
                sNo.setLayoutParams(fixedlp);
            } else {
                sNo.setLayoutParams(lp);

            }
            sNo.setBackgroundResource(R.drawable.table_header);
            tableRow.addView(sNo);
        }

        layout.addView(tableRow, 0);

    }

    private void setDetail(String[] value, TableLayout layout) {

        layout.setStretchAllColumns(true);
        layout.setShrinkAllColumns(true);
        tableRow = new TableRow(getApplicationContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(100, TableRow.LayoutParams.MATCH_PARENT);//(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        // tableRow.setLayoutParams(lp);
        tableRow.setBackgroundColor(Color.WHITE);
        String detail_value[] = value;
        int index = 0;

        for (int i = 0; i < detail_value.length; i++) {
            TextView dtl = new TextView(getApplicationContext());
            dtl.setText(detail_value[i]);
            index = Integer.parseInt(detail_value[0]);
            dtl.setTextSize(9);
            dtl.setGravity(Gravity.CENTER);

            dtl.setBackground(getResources().getDrawable(R.drawable.table_body));
            dtl.setLayoutParams(lp);
            tableRow.addView(dtl);
        }

        layout.addView(tableRow, index);

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
