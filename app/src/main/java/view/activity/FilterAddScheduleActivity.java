package view.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.vsolv.bigflow.R;

import java.text.BreakIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import DataBase.GetData;
import constant.Constant;
import models.Common;
import models.CustomSpinnerAdapter;
import models.UserDetails;
import models.Variables;
import presenter.NetworkResult;

public class FilterAddScheduleActivity extends AppCompatActivity implements View.OnClickListener {


    private GetData getdata;
    private List<Variables.Employee> employeeList;
    private List<Variables.Details> employeeDetailList;
    private DatePickerDialog.OnDateSetListener datePickerListener;
    private TextView fDate;
    Button btnClear, btnApply;
    private Bundle searchDetail;
    private Spinner ddlEmployee, ddlRoute, ddlTerritory, ddlMode, ddlCategory, ddlType, ddlSize, ddlConstitution;
    private CustomSpinnerAdapter employeeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_add_schedule);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Filter");
        searchDetail = new Bundle();
        if (getIntent() != null) {

            searchDetail = getIntent().getExtras();

        }
        loadView();
        initializeView();
        loadData();
    }

    private void loadView() {
        fDate = findViewById(R.id.txtASFFDate);
        btnClear = (Button) findViewById(R.id.btnASFClear);
        btnApply = (Button) findViewById(R.id.btnASFApply);
        ddlEmployee = (Spinner) findViewById(R.id.ddlASFEmployee);
        ddlRoute = (Spinner) findViewById(R.id.ddlASFRoute);
        ddlTerritory = (Spinner) findViewById(R.id.ddlASFTerritory);
        ddlMode = (Spinner) findViewById(R.id.ddlASFMode);
        ddlCategory = (Spinner) findViewById(R.id.ddlASFCategory);
        ddlConstitution = (Spinner) findViewById(R.id.ddlASFConstitution);
        ddlType = (Spinner) findViewById(R.id.ddlASFType);
        ddlSize = (Spinner) findViewById(R.id.ddlASFSize);
    }

    private void initializeView() {
        getdata = new GetData(getApplicationContext());
        datePickerListener = setDatepicker();
        btnApply.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        fDate.setOnClickListener(this);
        //filterList = new String[]{"Employee", "Route", "Territory", "Mode", "Category", "Type", "Size"};
        //arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, filterList);
    }

    private void loadData() {
        if (!Common.isOnline(getApplicationContext())) {
            Snackbar.with(getApplicationContext(), null)
                    .type(Type.WARNING)
                    .message(getApplicationContext().getResources().getString(R.string.check_internet_connection))
                    .duration(Duration.SHORT)
                    .fillParent(true)
                    .textAlign(Align.LEFT)
                    .show();
            return;
        }
        fDate.setText(searchDetail.getString(Constant.key_fdate, Common.convertDateString(new Date(), Constant.date_display_format)));
        getEmployee();
        getMode();
        getType();
        getSize();
        getCategory();
        getConstitution();
    }

    private void getEmployee() {
        employeeDetailList = getdata.EmployeeFilterList();
        Variables.Details temp = new Variables.Details();
        temp.gid = 0;
        temp.data = getResources().getString(R.string.choose);
        employeeDetailList.add(0, temp);
        employeeAdapter = new CustomSpinnerAdapter(getApplicationContext(), R.layout.list_item, employeeDetailList);
        ddlEmployee.setAdapter(employeeAdapter);
        ddlEmployee.setSelection(employeeAdapter.getPosition(searchDetail.getInt(Constant.key_employee_gid, 0)));
    }

    private void getConstitution() {
        List<Variables.Details> details = new ArrayList<>();
        details = getdata.TempTable("Constitution");
        Variables.Details temp = new Variables.Details();
        temp.gid = 0;
        temp.data = getResources().getString(R.string.choose);
        details.add(0, temp);
        CustomSpinnerAdapter detailsAdapter = new CustomSpinnerAdapter(getApplicationContext(), R.layout.list_item, details);
        ddlConstitution.setAdapter(detailsAdapter);
        ddlConstitution.setSelection(detailsAdapter.getPosition(searchDetail.getInt(Constant.key_cust_constitution_gid, 0)));
    }

    private void getCategory() {
        List<Variables.Details> details = new ArrayList<>();
        details = getdata.TempTable("Category");
        Variables.Details temp = new Variables.Details();
        temp.gid = 0;
        temp.data = getResources().getString(R.string.choose);
        details.add(0, temp);
        CustomSpinnerAdapter detailsAdapter = new CustomSpinnerAdapter(getApplicationContext(), R.layout.list_item, details);
        ddlCategory.setAdapter(detailsAdapter);
        ddlCategory.setSelection(detailsAdapter.getPosition(searchDetail.getInt(Constant.key_cust_category_gid, 0)));
    }

    private void getSize() {
        List<Variables.Details> details = new ArrayList<>();
        details = getdata.TempTable("Size");
        Variables.Details temp = new Variables.Details();
        temp.gid = 0;
        temp.data = getResources().getString(R.string.choose);
        details.add(0, temp);
        CustomSpinnerAdapter detailsAdapter = new CustomSpinnerAdapter(getApplicationContext(), R.layout.list_item, details);
        ddlSize.setAdapter(detailsAdapter);
        ddlSize.setSelection(detailsAdapter.getPosition(searchDetail.getInt(Constant.key_cust_size_gid, 0)));
    }

    private void getType() {
        List<Variables.Details> details = new ArrayList<>();
        details = getdata.TempTable("Type");
        Variables.Details temp = new Variables.Details();
        temp.gid = 0;
        temp.data = getResources().getString(R.string.choose);
        details.add(0, temp);
        CustomSpinnerAdapter detailsAdapter = new CustomSpinnerAdapter(getApplicationContext(), R.layout.list_item, details);
        ddlType.setAdapter(detailsAdapter);
        ddlType.setSelection(detailsAdapter.getPosition(searchDetail.getInt(Constant.key_cust_type_gid, 0)));
    }

    private void getMode() {
        List<Variables.Details> details = new ArrayList<>();
        details = getdata.TempTable("Mode");
        Variables.Details temp = new Variables.Details();
        temp.gid = 0;
        temp.data = getResources().getString(R.string.choose);
        details.add(0, temp);
        CustomSpinnerAdapter detailsAdapter = new CustomSpinnerAdapter(getApplicationContext(), R.layout.list_item, details);
        ddlMode.setAdapter(detailsAdapter);
        ddlMode.setSelection(detailsAdapter.getPosition(searchDetail.getInt(Constant.key_cust_mode_gid, 0)));
    }

    @Override
    public void onClick(View v) {
        DatePickerDialog datePickerDialog;
        switch (v.getId()) {

            case R.id.txtASFFDate:

                Variables.calendarDate cal = new Variables.calendarDate(fDate.getText().toString());
                datePickerDialog = new DatePickerDialog(this, datePickerListener, cal.getYear(), cal.getMonth(), cal.getDayofmonth());
                datePickerDialog.getDatePicker().setTag(R.id.txtASFFDate);
                datePickerDialog.show();
                break;

            case R.id.btnASFApply:
                setSearchData();
                break;
            case R.id.btnASFClear:
                clearSearchData();
                break;
        }
    }

    private void clearSearchData() {
        searchDetail = new Bundle();
        ddlEmployee.setSelection(0);
        ddlCategory.setSelection(0);
        ddlConstitution.setSelection(0);
        ddlMode.setSelection(0);
        ddlRoute.setSelection(0);
        ddlTerritory.setSelection(0);
        ddlSize.setSelection(0);
        ddlType.setSelection(0);
        fDate.setText(Common.convertDateString(new Date(), Constant.date_display_format));
    }

    private void setSearchData() {
        Variables.Details employee = (Variables.Details) ddlEmployee.getSelectedItem();
        searchDetail = new Bundle();
        searchDetail.putInt(Constant.key_employee_gid, employee.gid);
        searchDetail.putString(Constant.key_employee_name, employee.data);
        searchDetail.putInt(Constant.key_territory_gid, 0);
        searchDetail.putInt(Constant.key_route_gid, 0);
        searchDetail.putInt(Constant.key_cust_mode_gid, ((Variables.Details) ddlMode.getSelectedItem()).gid);
        Variables.Details cate = (Variables.Details) ddlCategory.getSelectedItem();
        searchDetail.putInt(Constant.key_cust_category_gid, ((Variables.Details) ddlCategory.getSelectedItem()).gid);
        searchDetail.putInt(Constant.key_cust_constitution_gid, ((Variables.Details) ddlConstitution.getSelectedItem()).gid);
        searchDetail.putInt(Constant.key_cust_type_gid, ((Variables.Details) ddlType.getSelectedItem()).gid);
        searchDetail.putString(Constant.key_cust_type, ((Variables.Details) ddlType.getSelectedItem()).data);
        searchDetail.putInt(Constant.key_cust_size_gid, ((Variables.Details) ddlSize.getSelectedItem()).gid);

        searchDetail.putString(Constant.key_fdate, fDate.getText().toString());

        Intent returnIntent = new Intent();
        returnIntent.putExtras(searchDetail);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private DatePickerDialog.OnDateSetListener setDatepicker() {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                int tag = ((Integer) view.getTag());

                if (tag == R.id.txtASFFDate) {
                    fDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                }
            }

        };
        return onDateSetListener;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
        super.onBackPressed();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


}
