package view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.vsolv.bigflow.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.Attributes;

import constant.Constant;
import models.AutoProductAdapter;
import models.Common;
import models.UserDetails;
import models.Variables;
import network.CallbackHandler;
import presenter.VolleyCallback;

import static models.Common.hideKeyboard;

public class StockActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean doubleBackToExitPressedOnce;
    private AutoCompleteTextView stock_auto_product;
    private Toolbar stockToolbar;
    private Bundle customer_details;
    private int customer_gid, schedule_type_gid;
    private AutoProductAdapter autoProductAdapter;
    private ArrayList<Variables.Product> load_product;
    // public ArrayList<EditText> value;
    private TableRow tableRow;
    private int i = 100;
    private TableLayout tableLayout;
    private Button btnsubmit_stock;
    private String Date = "";
    private ArrayList<Variables.Stock> stock_list = new ArrayList<>();

    private AdapterView.OnItemClickListener autoItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Variables.Product product = autoProductAdapter.getSelectedItem(position);
            tableLayout.layout(1, 1, 1, position);
            stock_auto_product.setText(product.product_name);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        btnsubmit_stock = (Button) findViewById(R.id.btnsubmit_stock);
        btnsubmit_stock.setOnClickListener(this);
        load_product = new ArrayList<>();

        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (getIntent() != null) {
            customer_details = getIntent().getExtras();
            customer_gid = customer_details.getInt(Constant.key_customer_gid, 0);
            schedule_type_gid = customer_details.getInt(Constant.key_sch_type_gid, 0);
        }

//
        stock_auto_product = findViewById(R.id.auto_product_stock);
        stock_auto_product.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                autoProductAdapter.stock_product_load(load_product);
            }
        });
        stock_auto_product.setThreshold(1);
        stock_auto_product.setOnItemClickListener(autoItemClickListener);
        autoProductAdapter = new AutoProductAdapter(StockActivity.this, R.layout.item_product);
        stock_auto_product.setAdapter(autoProductAdapter);
        tableLayout = findViewById(R.id.tbl_layout_stock);
        load_Pre_Stock();
    }

    public void load_Pre_Stock() {
        final JSONObject part_json = new JSONObject();
        final JSONObject final_json = new JSONObject();
        try {
            part_json.put("action", "SALESTOCK");
            part_json.put("type", "");
            part_json.put("custid", customer_gid);
            part_json.put("todaydate", "");
            final_json.put(Constant.params, part_json);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String URL = Constant.URL + "Stock_GetAPI?Customer_gid=" + customer_gid + "&Entity_gid=1";
        CallbackHandler.sendReqest(StockActivity.this, Request.Method.POST, final_json.toString(), URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("MESSAGE");
//                    value = new ArrayList<>();
                    setHeader();
                    if (status.equals("FOUND")) {

                        JSONArray jsonArray;
                        jsonArray = jsonObject.getJSONArray("DATA");
//                        jsonArray = jsonArray;
//                        val = new ArrayList<>();
//                        setHeader(Sales_order.this);


                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj_json = jsonArray.getJSONObject(i);
                            String ProductName = obj_json.getString("product_displayname");
                            String Date = obj_json.getString("stock_date");
//                            if (lsDate.length() > 0) {
//                                Date = Common.convertDateString(Common.convertDate(lsDate, "yyyy-MMM-dd"),
//                                        "dd-MMM-yyyy");
//                            } else {
//                                Date = "";
//                            }
                            String Qty = obj_json.getString("stock_qty");
                            int ProductGid = obj_json.getInt("product_gid");


                            Generate_Layout(ProductName, Date, Qty, ProductGid, i);// Values will be passed from here
                        }
                    }
                } catch (JSONException e) {
                    Log.e("Login", e.getMessage());

                }
            }


            public void onFailure(String result) {
                //pd.hide();
                Log.e("Login", result);

            }


        });
    }

    private void Generate_Layout(String Name, String Date, String Qty, int ProductGid, int i) {
        try {

            Variables.Product product = new Variables.Product();
            product.product_id = ProductGid;
            product.product_name = Name;
            load_product.add(product);

            tableLayout.setStretchAllColumns(true);
            tableLayout.setShrinkAllColumns(true);

            tableRow = new TableRow(getApplicationContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

            tableRow.setLayoutParams(lp);
            tableRow.setBackgroundColor(Color.WHITE);

            TextView sNo = new TextView(getApplicationContext());

            sNo.setBackground(getResources().getDrawable(R.drawable.table_body));
            sNo.setLayoutParams(lp);
            sNo.setText("1");
            sNo.setGravity(Gravity.CENTER);
            int total = tableLayout.getChildCount();
            for (int j = 1; j <= total; j++) {
                View view = tableLayout.getChildAt(j);
                if (view instanceof TableRow) {
                    TableRow row = (TableRow) view;
                    TextView t = (TextView) row.getChildAt(0);
                    t.setText("" + (j + 1));
                }
            }


            TextView productName = new TextView(getApplicationContext());
            productName.setText(Name);
            productName.setBackground(getResources().getDrawable(R.drawable.table_body));
            productName.setLayoutParams(lp);
            productName.setFocusable(false);

            TextView date = new TextView(getApplicationContext());
            date.setText(Date);
            date.setBackground(getResources().getDrawable(R.drawable.table_body));
            date.setLayoutParams(lp);


            final TextView qty = new TextView(getApplicationContext());
            qty.setText(Qty);
            qty.setBackground(getResources().getDrawable(R.drawable.table_body));
            qty.setLayoutParams(lp);
            qty.setGravity(Gravity.CENTER);


            EditText orderQty = new EditText(getApplicationContext());
            orderQty.setTag(ProductGid);
            orderQty.setBackground(getResources().getDrawable(R.drawable.table_body));
            orderQty.setLayoutParams(lp);
            orderQty.setHint("Qty");
            orderQty.setGravity(Gravity.CENTER);
            orderQty.setFocusable(true);
            orderQty.setInputType(InputType.TYPE_CLASS_NUMBER);

            EditText remark = new EditText(getApplicationContext());
            remark.setTag(ProductGid);
            remark.setBackground(getResources().getDrawable(R.drawable.table_body));
            remark.setLayoutParams(lp);
            remark.setHint("Remark");
            remark.setGravity(Gravity.CENTER);
            remark.setFocusable(true);

            Variables.Stock stock = new Variables.Stock();

            stock.prduct_id = ProductGid;
            stock.current_stock_qty = orderQty;
            stock.remark = remark;

            stock_list.add(stock);

            tableRow.addView(sNo);
            tableRow.addView(productName);
            tableRow.addView(date);
            tableRow.addView(qty);
            tableRow.addView(orderQty);
            tableRow.addView(remark);
            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyboard(StockActivity.this);
                    TableRow row = (TableRow) v;
                    TextView qty = (TextView) ((TableRow) v).getChildAt(3);
                    EditText re = (EditText) row.getChildAt(4);
                    re.setText(qty.getText().toString());
                }
            });
            tableLayout.addView(tableRow, 1);

            i = i - 1;
        } catch (Exception e) {
            String Log = e.getMessage();
        }


    }

    private void setHeader() {

        tableLayout.setStretchAllColumns(true);
        tableLayout.setShrinkAllColumns(true);

        tableRow = new TableRow(getApplicationContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        tableRow.setLayoutParams(lp);


        TextView sNo = new TextView(getApplicationContext());
        sNo.setText("S.No");
        sNo.setTextColor(0xFFFFFFFF);
        sNo.setTextSize(15);
        sNo.setGravity(Gravity.CENTER);
        sNo.setLayoutParams(lp);
        sNo.setBackgroundResource(R.drawable.table_header);


        TextView productName = new TextView(getApplicationContext());
        productName.setText("Product Name");
        productName.setTextColor(0xFFFFFFFF);
        productName.setTextSize(15);
        productName.setGravity(Gravity.CENTER);
        productName.setLayoutParams(lp);
        productName.setBackgroundResource(R.drawable.table_header);

        TextView date = new TextView(getApplicationContext());
        date.setText("Last Date");
        date.setGravity(Gravity.CENTER);
        date.setTextColor(0xFFFFFFFF);
        date.setTextSize(15);
        date.setLayoutParams(lp);
        date.setBackgroundResource(R.drawable.table_header);

        TextView qty = new TextView(getApplicationContext());
        qty.setText("Pre-StockQty");
        qty.setGravity(Gravity.CENTER);
        qty.setTextColor(0xFFFFFFFF);
        qty.setTextSize(15);
        qty.setLayoutParams(lp);
        qty.setBackgroundResource(R.drawable.table_header);

        TextView orderQty = new TextView(getApplicationContext());
        orderQty.setText("CurrentQty");
        orderQty.setTextColor(0xFFFFFFFF);
        orderQty.setTextSize(15);
        orderQty.setGravity(Gravity.CENTER);
        orderQty.setLayoutParams(lp);
        orderQty.setBackgroundResource(R.drawable.table_header);

        TextView remark = new TextView(getApplicationContext());
        remark.setText("Remark");
        remark.setTextColor(0xFFFFFFFF);
        remark.setTextSize(15);
        remark.setGravity(Gravity.CENTER);
        remark.setLayoutParams(lp);
        remark.setBackgroundResource(R.drawable.table_header);


        tableRow.addView(sNo);
        tableRow.addView(productName);
        tableRow.addView(date);
        tableRow.addView(qty);
        tableRow.addView(orderQty);
        tableRow.addView(remark);

        tableLayout.addView(tableRow, 0);


    }

    public void onClick(View view) {

        if (view == btnsubmit_stock) {
            try {
                if (stock_list.size() > 0) {

                    final JSONArray stock_details = new JSONArray();
                    for (int i = 0; i < stock_list.size(); i++) {
                        Variables.Stock stk = stock_list.get(i);
                        int stock_pdct_gid = stk.prduct_id;
                        String cur_stock = stk.current_stock_qty.getText().toString().trim();
                        String remark = stk.remark.getText().toString().trim();

                        if (stock_pdct_gid > 0 && cur_stock.trim().length() > 0 && cur_stock != "0" && cur_stock != null
                                && Float.parseFloat(cur_stock.trim()) > 0) {

                            JSONObject jsonobj_stock_details = new JSONObject();

                            jsonobj_stock_details.put("product_gid", stock_pdct_gid);
                            jsonobj_stock_details.put("stock_qty", cur_stock);
                            jsonobj_stock_details.put("remark", remark);
                            stock_details.put(jsonobj_stock_details);

                        }
                    }

                    if (stock_details.length() > 0) {
                        // Get Confirm from User
                        final AlertDialog.Builder builder = new AlertDialog.Builder(StockActivity.this);
                        builder.setTitle("Confirm");
                        builder.setMessage("Are You Sure To Submit?");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                try {
                                    JSONObject Full_Json_Stock = new JSONObject();
                                    JSONObject params_Json_Stock = new JSONObject();
                                    String date;

                                    params_Json_Stock.put(Constant.ACTION, "Insert");
                                    params_Json_Stock.put(Constant.customer_gid, customer_gid);
                                    params_Json_Stock.put(Constant.todaydate, Common.convertDateString(new Date(), "dd/MM/yyyy"));
                                    params_Json_Stock.put(Constant.stckdet, new JSONObject().put(Constant.FET_STOCK, stock_details));
                                    Full_Json_Stock.put(Constant.params, params_Json_Stock);
//                                    Log.v("JSONPON", Full_Json.toString());

                                    if (Full_Json_Stock.length() > 0) {
                                        String OutMessage = Stock_set(Full_Json_Stock);
                                    }
                                } catch (JSONException e) {
                                    Log.e("Stock-Json", e.getMessage());
                                }
                            }

                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                }
            } catch (Exception e) {
                Log.e("Stock", e.getMessage());
            }


        }
    }

    public String Stock_set(JSONObject jsonObject) {

        String URL = Constant.URL + "Stock_SetAPI?Emp_Gid=" + UserDetails.getUser_id();
        CallbackHandler.sendReqest(StockActivity.this, Request.Method.POST, jsonObject.toString(), URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("MESSAGE");
                    if (status.equals("SUCCESS")) {
                        Toast.makeText(StockActivity.this, "Stock Saved Successfully.", Toast.LENGTH_LONG).show();

                        StockActivity.this.finish();


                    } else {
                        Toast.makeText(StockActivity.this, "Stock Not Saved.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e("Stock", e.getMessage());
                }
            }

            @Override
            public void onFailure(String result) {
                Log.e("Location", result);

            }
        });

        return "";

    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return super.onSupportNavigateUp();
    }

}