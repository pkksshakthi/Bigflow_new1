package view.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import android.widget.CheckedTextView;
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
import java.util.Date;
import java.util.List;

import constant.Constant;
import models.AutoProductAdapter;
import models.Common;
import models.UserDetails;
import models.Variables;
import network.CallbackHandler;
import presenter.VolleyCallback;

import static java.lang.Integer.parseInt;

public class ServiceActivity extends AppCompatActivity implements View.OnClickListener {
    private AutoCompleteTextView auto_product_service;
    private boolean doubleBackToExitPressedOnce;
    private EditText product_count;
    CheckedTextView service_date;
    private Spinner dropdown;
    public Button btnsubmit_service;
    public ImageButton add_service_Buttn;
    private TableRow tableRow;
    private Bundle customer_details;
    String[] items = new String[]{"Customer", "Executive"};
    private TableLayout tableLayout;
    private String Productname_tolayout;
    int Productgid_tolayout, customer_gid;
    private AutoProductAdapter autoProductAdapter;
    private ArrayList<Integer> favProduct;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private ArrayAdapter<String> payByAdapter;
    private TableRow.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(Constant.title_service_summary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (getIntent() != null) {
            customer_details = getIntent().getExtras();
            customer_gid = customer_details.getInt(Constant.key_customer_gid, 0);
        }

        loadView();
        initializeView();
        setHeader();
    }

    private void loadView() {
        btnsubmit_service = (Button) findViewById(R.id.btnsubmit_service);
        auto_product_service = (AutoCompleteTextView) findViewById(R.id.auto_product_service);
        tableLayout = (TableLayout) findViewById(R.id.tbl_layout_service);
        add_service_Buttn = (ImageButton) findViewById(R.id.btnadd_service);
        product_count = (EditText) findViewById(R.id.txt_productcount);

    }


    private void initializeView() {
        favProduct = new ArrayList<>();
        autoProductAdapter = new AutoProductAdapter(this, R.layout.item_product);
        layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        tableLayout.setStretchAllColumns(true);
        payByAdapter = new ArrayAdapter<String>(ServiceActivity.this, R.layout.spinner_item, items);
        payByAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        btnsubmit_service.setOnClickListener(this);
        add_service_Buttn.setOnClickListener(this);

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

        progressDialog = new ProgressDialog(ServiceActivity.this);
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        product_count.setText("1");
    }

    private AdapterView.OnItemClickListener autoItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Variables.Product product = autoProductAdapter.getSelectedItem(position);
            auto_product_service.setText(product.product_name);
            Productname_tolayout = product.product_name;
            Productgid_tolayout = product.product_id;
        }
    };


    private void createSubmitDialog(final List<Variables.Service> serviceList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActivity.this);
        builder.setTitle("Details");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_service_submit, null);
        service_date = (CheckedTextView) dialogView.findViewById(R.id.service_date);
        service_date.setText(Common.convertDateString(new Date(), Constant.date_display_format));
        dropdown = (Spinner) dialogView.findViewById(R.id.spinner1);
        dropdown.setAdapter(payByAdapter);

        service_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                Variables.calendarDate cal = new Variables.calendarDate(service_date.getText().toString());
                DatePickerDialog datePickerDialog = new DatePickerDialog(ServiceActivity.this, datePickerListener, cal.getYear(), cal.getMonth(), cal.getDayofmonth());
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                datePickerDialog.setTitle("Choose Remark date");
                datePickerDialog.show();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                final JSONArray ServiceDetails = new JSONArray();
                String Exep_by = dropdown.getSelectedItem().toString().equals(items[0]) ? "C" : "V";

                for (int j = 0; j < serviceList.size(); j++) {
                    try {
                        JSONObject objSoDetails = new JSONObject();
                        Variables.Service service = serviceList.get(j);

                        objSoDetails.put("product_gid", service.product_gid);
                        objSoDetails.put("product_slno", service.product_serial_no);
                        objSoDetails.put("remark", service.product_remark);
                        objSoDetails.put("dispatch_mode", "EXECUTIVE");
                        objSoDetails.put("pay_by", Exep_by);
                        ServiceDetails.put(objSoDetails);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


                JSONObject Json = new JSONObject();
                JSONObject params_Json = new JSONObject();
                try {
                    params_Json.put("action", "Insert");
                    params_Json.put("entity_gid", 1);
                    params_Json.put("employee_gid", UserDetails.getUser_id());
                    params_Json.put("customer_gid", customer_gid);
                    params_Json.put("date", Common.convertDateString(service_date.getText().toString(), Constant.date_display_format, "yyyy-MM-dd"));
                    params_Json.put("status", "INITIATED");
                    params_Json.put(Constant.entity_gid, 1);
                    params_Json.put("SERVICE_JSON", new JSONObject().put("SERVICE", ServiceDetails));
                    Json.put("params", params_Json);
                    ServiceSet(Json);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.setView(dialogView);
        final Dialog dialog = builder.create();
        dialog.show();
    }

    private View.OnClickListener delete_clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            TableRow row = (TableRow) v.getParent();
            int rowIndex = tableLayout.indexOfChild(row);
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

    private void setHeader() {

        tableRow = new TableRow(this);
        tableRow.setLayoutParams(layoutParams);

        TextView sNo = getTextView(getApplicationContext(), true, "S.No");
        TextView productName = getTextView(getApplicationContext(), true, "Product");
        TextView sino = getTextView(getApplicationContext(), true, "Serial No");
        TextView Remark = getTextView(getApplicationContext(), true, "Remark");
        TextView delete = getTextView(getApplicationContext(), true, "Delete");

        tableRow.addView(sNo);
        tableRow.addView(productName);
        tableRow.addView(sino);
        tableRow.addView(Remark);
        tableRow.addView(delete);

        tableLayout.addView(tableRow, 0);

    }

    private TextView getTextView(Context context, Boolean isHeader, String setText) {
        TextView textView = new TextView(getApplicationContext());
        textView.setText(setText);
        textView.setGravity(Gravity.CENTER);
        if (isHeader) {
            textView.setTextColor(0xFFFFFFFF);
            textView.setTextSize(15);
            textView.setBackgroundResource(R.drawable.table_header);
        } else {
            textView.setTextSize(14);
            textView.setBackgroundResource(R.drawable.table_body);
        }
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    private EditText getEditTextView(Context context, String setText, String hint) {
        EditText editText = new EditText(getApplicationContext());
        editText.setText(setText);
        editText.setHint(hint);
        editText.setGravity(Gravity.LEFT);
        editText.setTextSize(14);
        editText.setBackgroundResource(R.drawable.table_body);
        editText.setLayoutParams(layoutParams);
        return editText;
    }

    private void Create_Layout(String Name, int Gid) {
        try {
            tableRow = new TableRow(this);
            tableRow.setLayoutParams(layoutParams);
            tableRow.setTag(Gid);

            TextView sNo = getTextView(getApplicationContext(), false, "1");

            int total = tableLayout.getChildCount();
            for (int j = 1; j <= total; j++) {
                View view = tableLayout.getChildAt(j);
                if (view instanceof TableRow) {
                    TableRow row = (TableRow) view;
                    TextView t = (TextView) row.getChildAt(0);
                    t.setText("" + (j + 1));
                }
            }

            TextView productName = getTextView(getApplicationContext(), false, Name);

            EditText Product_sino = getEditTextView(getApplicationContext(), "", "Serial No");
            Product_sino.setFocusable(true);
            Product_sino.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
            Product_sino.setKeyListener(DigitsKeyListener.getInstance("abcdefghijklmnopqrstuvwxyz1234567890"));
            Product_sino.setRawInputType(InputType.TYPE_CLASS_TEXT);

            EditText Remark = getEditTextView(getApplicationContext(), "", "Remark");
            Remark.setFocusable(true);
            Remark.setFilters(new InputFilter[]{new InputFilter.LengthFilter(128)});
            Remark.setKeyListener(DigitsKeyListener.getInstance("abcdefghijklmnopqrstuvwxyz1234567890"));
            Remark.setRawInputType(InputType.TYPE_CLASS_TEXT);

            TextView remove = getTextView(getApplicationContext(), false, "");
            remove.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_remove, 0, 0, 0);
            remove.setOnClickListener(delete_clickListener);


            tableRow.addView(sNo);
            tableRow.addView(productName);
            tableRow.addView(Product_sino);
            tableRow.addView(Remark);
            tableRow.addView(remove);
            tableLayout.addView(tableRow, 1);


        } catch (Exception e) {
            String log = e.getMessage();
            Log.e("Service_tLayout", log);
        }


    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            service_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

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
                        finish();

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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnsubmit_service) {
            int total = tableLayout.getChildCount();
            List<Variables.Service> serviceList = new ArrayList<>();
            if ((total - 1) > 0) {
                for (int i = 1; i < total; i++) {
                    TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
                    Variables.Service service = new Variables.Service();

                    EditText Product_sino = (EditText) tableRow.getChildAt(2);
                    EditText Remark = (EditText) tableRow.getChildAt(3);

                    service.product_gid = Integer.parseInt(tableRow.getTag().toString());
                    service.product_serial_no = Product_sino.getText().toString();
                    service.product_remark = Remark.getText().toString();
                    if (service.product_remark.trim().length() == 0) {
                        Toast.makeText(getApplicationContext(), "Please enter remark!.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    serviceList.add(service);
                }

                createSubmitDialog(serviceList);
            } else {
                Toast.makeText(getApplicationContext(), "Add Product to submit.!", Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.btnadd_service) {
            String count = product_count.getText().toString();
            String product = auto_product_service.getText().toString();
            if (count.trim().length() > 0 && product.trim().length() > 0 && Productgid_tolayout != 0) {
                int finalValue = Integer.parseInt(count);
                if (finalValue > 500) {
                    Toast.makeText(getApplicationContext(), "Count not more then 500.!", Toast.LENGTH_LONG).show();
                    return;
                }
                for (int i = 0; i < finalValue; i++) {

                    Create_Layout(Productname_tolayout, Productgid_tolayout);
                }
                Productname_tolayout = "";
                Productgid_tolayout = 0;
                auto_product_service.setText("");
                product_count.setText("1");

            } else {
                Toast.makeText(getApplicationContext(), "Enter valid product or Count.!", Toast.LENGTH_LONG).show();

            }
        }
    }
}
