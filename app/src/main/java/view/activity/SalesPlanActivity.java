package view.activity;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.vsolv.bigflow.R;

import constant.Constant;

public class SalesPlanActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean doubleBackToExitPressedOnce;
    private Bundle customer_details;
    private TextView customer_name, product_name;
    TableLayout table_history, table_product;
    private TableRow tableRow;
    private TableRow.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_plan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent() != null) {
            customer_details = getIntent().getExtras();
        }
        getSupportActionBar().setTitle(customer_details.getString(Constant.key_customer_name, "Customer Name"));
        loadView();
        initializeView();
        loadData();
    }

    private void loadView() {
        customer_name = (TextView) findViewById(R.id.SPCustName);
        product_name = (TextView) findViewById(R.id.SPProductName);
        table_history = (TableLayout) findViewById(R.id.SPtable_history);
        table_product = (TableLayout) findViewById(R.id.SPtable_prodcut);
    }

    private void initializeView() {
        layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        setHistoryHeader();
        setProductHeader();
    }

    private void loadData() {
        customer_name.setText(customer_details.getString(Constant.key_customer_name, "Customer Name"));
    }

    private void setHistoryHeader() {

        tableRow = new TableRow(this);
        tableRow.setLayoutParams(layoutParams);

        TextView sNo = getTextView(getApplicationContext(), true, "Year");
        TextView sino = getTextView(getApplicationContext(), true, "Serial No");
        TextView Remark = getTextView(getApplicationContext(), true, "Remark");
        TextView delete = getTextView(getApplicationContext(), true, "Delete");

        tableRow.addView(sNo);
        tableRow.addView(sino);
        tableRow.addView(Remark);
        tableRow.addView(delete);

        table_history.addView(tableRow, 0);

    }

    private void setProductHeader() {

        tableRow = new TableRow(this);
        tableRow.setLayoutParams(layoutParams);

        TextView sNo = getTextView(getApplicationContext(), true, "S.No");
        TextView produ_name = getTextView(getApplicationContext(), true, "Product");
        TextView sino = getTextView(getApplicationContext(), true, "Serial No");
        TextView Remark = getTextView(getApplicationContext(), true, "Remark");
        TextView delete = getTextView(getApplicationContext(), true, "Delete");

        tableRow.addView(sNo);
        tableRow.addView(produ_name);
        tableRow.addView(sino);
        tableRow.addView(Remark);
        tableRow.addView(delete);

        table_product.addView(tableRow, 0);

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


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.click_again_exit), Toast.LENGTH_SHORT).show();

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
