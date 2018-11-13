package view.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.vsolv.bigflow.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import constant.Constant;
import models.AutoProductAdapter;
import models.UserDetails;
import models.Variables;
import network.CallbackHandler;
import presenter.VolleyCallback;

import static java.lang.Integer.parseInt;

public class ServiceActivity extends AppCompatActivity {
    private AutoCompleteTextView auto_product_service;
    private boolean doubleBackToExitPressedOnce;
    private EditText product_count, service_date;
    private Spinner dropdown;
    public Button btnsubmit_service;
    public ImageButton add_service_Buttn;
    private TableRow tableRow;
    private Bundle customer_details;
    String[] items = new String[]{"Customer", "Executive"};
    ArrayList<Variables.Service> Service_obj = new ArrayList<Variables.Service>();
    private int i;
    private TableLayout tableLayout;
    private String Productname_tolayout, Remark_date, Exep_by;
    int Productgid_tolayout, customer_gid;
    private AutoProductAdapter autoProductAdapter;
    private ArrayList<Integer> favProduct;
    private int mYear, mMonth, mDay;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Service");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        loadView();
        initializeView();
        setHeader();
    }

    private void loadView() {
        btnsubmit_service = (Button) findViewById(R.id.btnsubmit_service);
        auto_product_service = (AutoCompleteTextView) findViewById(R.id.auto_product_service);
        autoProductAdapter = new AutoProductAdapter(this, R.layout.item_product);
        tableLayout = (TableLayout) findViewById(R.id.tbl_layout_service);
        add_service_Buttn = (ImageButton) findViewById(R.id.btnadd_service);
        product_count = (EditText) findViewById(R.id.txt_productcount);

    }


    private void initializeView() {
        favProduct = new ArrayList<>();
        btnsubmit_service.setOnClickListener(Submit_clickListener);
        auto_product_service.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Productgid_tolayout = 0;
                autoProductAdapter.productfilter(s.toString(), favProduct);
            }
        });
        auto_product_service.setThreshold(1);
        auto_product_service.setOnItemClickListener(autoItemClickListener);
        auto_product_service.setAdapter(autoProductAdapter);
        add_service_Buttn.setOnClickListener(add_clickListener);
        progressDialog = new ProgressDialog(ServiceActivity.this);
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        product_count.setText("1");
    }

    //AUTOCOMPLETE CLICK
    private AdapterView.OnItemClickListener autoItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Variables.Product product = autoProductAdapter.getSelectedItem(position);
            auto_product_service.setText(product.product_name);
            Productname_tolayout = product.product_name;
            Productgid_tolayout = product.product_id;
        }
    };


    //ADD BUTTON CLICK
    private View.OnClickListener add_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String count = product_count.getText().toString();
            String product = auto_product_service.getText().toString();
            if (count.trim().length() > 0 && product.trim().length() > 0 && Productgid_tolayout != 0) {
                int finalValue = Integer.parseInt(count);
                for (i = 0; i < finalValue; i++) {

                    Create_Layout(Productname_tolayout, Productgid_tolayout);
                }
                Productname_tolayout = "";
                Productgid_tolayout = 0;
                auto_product_service.setText("");
                product_count.setText("");

            } else {
                Toast.makeText(getApplicationContext(), "Enter Valid ProductName and Count.", Toast.LENGTH_LONG).show();

            }
        }
    };


    // SUBMIT BUTTON CLICK
    private View.OnClickListener Submit_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (Service_obj.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActivity.this);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Remark_date != null && Exep_by != null) {
                            final JSONArray ServiceDetails = new JSONArray();
                            int total = tableLayout.getChildCount();

                            for (int j = 1; j < total; j++) {
                                JSONObject objSoDetails = new JSONObject();
                                Variables.Service Service_object = Service_obj.get(j - 1);
                                try {
                                    View view = tableLayout.getChildAt(j);
                                    if (view instanceof TableRow) {
                                        TableRow row1 = (TableRow) view;
                                        EditText Product_sino = (EditText) ((TableRow) view).getChildAt(2);
                                        String a = Product_sino.getText().toString();
                                        EditText Remark = (EditText) ((TableRow) view).getChildAt(3);
                                        objSoDetails.put("product_gid", Service_object.product_id);
                                        objSoDetails.put("product_slno", Product_sino.getText().toString());
                                        objSoDetails.put("remark", Remark.getText().toString());
                                        objSoDetails.put("pay_by", Exep_by);
                                        ServiceDetails.put(objSoDetails);
                                    }

                                } catch (JSONException e) {
                                    Log.e("Service_json", String.valueOf(e));
                                }
                            }

                            customer_details = ServiceActivity.this.getIntent().getExtras();
                            customer_gid = customer_details.getInt("customer_id");
                            JSONObject Json = new JSONObject();
                            JSONObject params_Json = new JSONObject();
                            try {
                                params_Json.put("action", "Insert");
                                params_Json.put("entity_gid", 1);
                                params_Json.put("employee_gid", UserDetails.getUser_id());
                                params_Json.put("customer_gid", customer_gid);
                                params_Json.put("date", Remark_date);
                                params_Json.put("status", "INITIATED");
                                params_Json.put(Constant.entity_gid, 1);
                                params_Json.put("SERVICE_JSON", new JSONObject().put("SERVICE", ServiceDetails));
                                Json.put("params", params_Json);
                                if (Json.length() > 0) {
                                    String OutMessage = ServiceSet(Json);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(ServiceActivity.this, "Enter Valid Date And ExpBy", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_service_submit, null);

                builder.setView(dialogView);
                service_date = (EditText) dialogView.findViewById(R.id.service_date);
                dropdown = (Spinner) dialogView.findViewById(R.id.spinner1);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ServiceActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
                dropdown.setAdapter(adapter);
                dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItem = parent.getItemAtPosition(position).toString();
                        if (selectedItem == "Executive") {
                            Exep_by = "V";

                        } else {
                            Exep_by = "C";
                        }
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                service_date.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(ServiceActivity.this, datePickerListener, mYear, mMonth, mDay);
                        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                        datePickerDialog.setCancelable(false);
                        datePickerDialog.setTitle("Choose Remark date");
                        datePickerDialog.show();
                    }
                });

                final Dialog dialog = builder.create();
                dialog.show();
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                service_date.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (service_date.getText().toString().length() > 0) {
                            ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

            } else {

                Toast.makeText(getApplicationContext(), "List Is Empty", Toast.LENGTH_LONG).show();

            }
        }
    };


    //DELETE BUTTON CLICK
    private View.OnClickListener delete_clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            TableRow row = (TableRow) v.getParent();
            int rowIndex = tableLayout.indexOfChild(row);

            Service_obj.remove(rowIndex - 1);
            tableLayout.removeView(row);

            int total = tableLayout.getChildCount();
            for (int j = 1; j <= total; j++) {
                View view = tableLayout.getChildAt(j);
                if (view instanceof TableRow) {
                    TableRow row1 = (TableRow) view;
                    TextView t = (TextView) row1.getChildAt(0);
                    t.setText("" + (j));
                }
            }

        }
    };


    private void Create_Layout(String Name, int Gid) {
        try {

            JSONObject LayoutJsonObj = new JSONObject();
            tableLayout.setStretchAllColumns(true);
            tableLayout.setShrinkAllColumns(true);

            tableRow = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

            tableRow.setLayoutParams(lp);
            tableRow.setBackgroundColor(Color.WHITE);

            TextView sNo = new TextView(this);

            sNo.setBackground(getResources().getDrawable(R.drawable.table_body));
            sNo.setLayoutParams(lp);
            sNo.setText("1  ");
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

            TextView productName = new TextView(this);
            productName.setText(Name);
            LayoutJsonObj.put("ProductName", Name);

            productName.setBackground(getResources().getDrawable(R.drawable.table_body));
            productName.setLayoutParams(lp);
            productName.setFocusable(false);

            EditText Product_sino = new EditText(this);

            Product_sino.setBackground(getResources().getDrawable(R.drawable.table_body));
            Product_sino.setLayoutParams(lp);
            Product_sino.setHint("Si No");
            Product_sino.setGravity(Gravity.CENTER);
            Product_sino.setFocusable(true);
            //Product_sino.setInputType(InputType.TYPE_CLASS_TEXT);
            Product_sino.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
            Product_sino.setKeyListener(DigitsKeyListener.getInstance("abcdefghijklmnopqrstuvwxyz1234567890"));
            Product_sino.setRawInputType(InputType.TYPE_CLASS_TEXT);
            EditText Remark = new EditText(this);

            Remark.setBackground(getResources().getDrawable(R.drawable.table_body));
            Remark.setLayoutParams(lp);
            Remark.setHint("Remark");
            Remark.setGravity(Gravity.CENTER);
            Remark.setFocusable(true);
           // Remark.setInputType(InputType.TYPE_CLASS_TEXT);
            Remark.setFilters(new InputFilter[]{new InputFilter.LengthFilter(128)});
            Remark.setKeyListener(DigitsKeyListener.getInstance("abcdefghijklmnopqrstuvwxyz1234567890"));
            Remark.setRawInputType(InputType.TYPE_CLASS_TEXT);

            ImageButton minusBtn = new ImageButton(this);
            minusBtn.setImageResource(R.drawable.ic_action_remove);
            minusBtn.setLayoutParams(lp);
            minusBtn.setFocusable(true);

            Button Deletebtn = new Button(this);
            Deletebtn.setLayoutParams(lp);
            Deletebtn.setHint("Delete");
            Deletebtn.setGravity(Gravity.CENTER);
            Deletebtn.setFocusable(true);

            minusBtn.setOnClickListener(delete_clickListener);
            tableRow.addView(sNo);
            tableRow.addView(productName);
            tableRow.addView(Product_sino);
            tableRow.addView(Remark);
            tableRow.addView(minusBtn);
            tableLayout.addView(tableRow, 1);
            Variables.Service Service_set = new Variables.Service(Gid, Name);
            Service_obj.add(Service_set);



        } catch (Exception e) {
            String log = e.getMessage();
            Log.e("layout", log);
        }


    }

    private void setHeader() {

        tableLayout.setStretchAllColumns(true);
        tableLayout.setShrinkAllColumns(true);

        tableRow = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        tableRow.setLayoutParams(lp);

        TextView sNo = new TextView(this);
        sNo.setText("SR.No");
        sNo.setTextColor(0xFFFFFFFF);
        sNo.setTextSize(15);
        sNo.setGravity(Gravity.CENTER);
        sNo.setLayoutParams(lp);
        sNo.setBackgroundResource(R.drawable.table_header);

        TextView productName = new TextView(this);
        productName.setText("Product Name");
        productName.setTextColor(0xFFFFFFFF);
        productName.setTextSize(15);
        productName.setGravity(Gravity.CENTER);
        productName.setLayoutParams(lp);
        productName.setBackgroundResource(R.drawable.table_header);

        TextView sino = new TextView(this);
        sino.setText("Product SI NO");
        sino.setGravity(Gravity.CENTER);
        sino.setTextColor(0xFFFFFFFF);
        sino.setTextSize(15);
        sino.setLayoutParams(lp);
        sino.setBackgroundResource(R.drawable.table_header);

        TextView Remark = new TextView(this);
        Remark.setText("Remark");
        Remark.setGravity(Gravity.CENTER);
        Remark.setTextColor(0xFFFFFFFF);
        Remark.setTextSize(15);
        Remark.setLayoutParams(lp);
        Remark.setBackgroundResource(R.drawable.table_header);

        TextView delete = new TextView(this);
        delete.setText("Delete");
        delete.setGravity(Gravity.CENTER);
        delete.setTextColor(0xFFFFFFFF);
        delete.setTextSize(15);
        delete.setLayoutParams(lp);
        delete.setBackgroundResource(R.drawable.table_header);

        tableRow.addView(sNo);
        tableRow.addView(productName);
        tableRow.addView(sino);
        tableRow.addView(Remark);
        tableRow.addView(delete);

        tableLayout.addView(tableRow, 0);

    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            String year1 = String.valueOf(selectedYear);
            String month_todb = String.valueOf(selectedMonth + 1);
            int month1 = selectedMonth + 1;
            String day1 = String.valueOf(selectedDay);
            String day = String.format("%02d", parseInt(day1));
            String month_to_db = String.format("%02d", parseInt(month_todb));
            String month = new DateFormatSymbols().getShortMonths()[month1 - 1];
            String Date = day + "/" + month + "/" + year1;
            // String Date_db = day + "/" + month_to_db + "/" + year1;
            String Date_db = year1 + "-" + month_to_db + "-" + day;

            service_date.setText(Date);
            Remark_date = Date_db;

        }
    };

    public String ServiceSet(JSONObject jsonObject) {
        progressDialog.show();
        String URL = Constant.URL + "Service_SetAPI?Emp_gid=" + UserDetails.getUser_id() + "&Entity_gid=1";

        CallbackHandler.sendReqest(ServiceActivity.this, Request.Method.POST, jsonObject.toString(), URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("MESSAGE");
                    if (status.equals("SUCCESS")) {
                        Toast.makeText(ServiceActivity.this, "Service Saved Successfully.", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                        finish();
                        ServiceActivity.this.finish();

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(ServiceActivity.this, "Service Not Saved Successfully.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Log.e("Service", e.getMessage());
                }
            }

            @Override
            public void onFailure(String result) {
                Log.e("Service", result);
                progressDialog.dismiss();
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
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
