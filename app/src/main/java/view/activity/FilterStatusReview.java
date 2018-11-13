package view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.vsolv.bigflow.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import DataBase.GetData;
import models.CustomSpinnerAdapter;
import models.UserDetails;
import models.Variables;
import presenter.NetworkResult;

public class FilterStatusReview extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Spinner ddlEmployee, ddlScheduleType, ddlCustGroup, ddlCustName, ddlCustLocation, ddlApproveStatus;
    List<String> categories;
    Bundle searchDetail;
    private TextView fDate, tDate, followup_fDate, followup_tDate, reschedule_fDate, reschedule_tDate;
    private Button btnApply, btnClear;
    private int employee_gid;
    DatePickerDialog.OnDateSetListener datePickerListener;
    private Calendar currentDate;
    private GetData getdata;
    private List<Variables.Employee> employeeList;
    private ArrayList<Variables.Details> employeeDetailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_status_review);
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            employee_gid = bundle.getInt("employee_gid", 0);
        }
        loadView();
        initializeView();
        loadData();
    }

    private void loadView() {
        ddlEmployee = (Spinner) findViewById(R.id.ddlSREmployee);
        ddlScheduleType = (Spinner) findViewById(R.id.ddlSRType);
        ddlApproveStatus = (Spinner) findViewById(R.id.ddlSRApproveStatus);
        ddlCustGroup = (Spinner) findViewById(R.id.ddlSRCustomerGroup);
        ddlCustName = (Spinner) findViewById(R.id.ddlSRCustomerName);
        ddlCustLocation = (Spinner) findViewById(R.id.ddlSRCustomerLocation);
        fDate = (TextView) findViewById(R.id.txtSRFDate);
        tDate = (TextView) findViewById(R.id.txtSRTDate);
        followup_fDate = (TextView) findViewById(R.id.txtSRFollowupFDate);
        followup_tDate = (TextView) findViewById(R.id.txtSRFollowupTDate);
        reschedule_fDate = (TextView) findViewById(R.id.txtSRRescheduleFDate);
        reschedule_tDate = (TextView) findViewById(R.id.txtSRRescheduleTDate);
        btnApply = (Button) findViewById(R.id.btnSRApply);
        btnClear = (Button) findViewById(R.id.btnSRClear);
    }

    private void initializeView() {
        searchDetail = new Bundle();
        currentDate = Calendar.getInstance();
        categories = new ArrayList<>();
        getdata = new GetData(getApplicationContext());
        ddlEmployee.setOnItemSelectedListener(this);
        ddlApproveStatus.setOnItemSelectedListener(this);
        ddlScheduleType.setOnItemSelectedListener(this);
        ddlCustGroup.setOnItemSelectedListener(this);
        ddlCustName.setOnItemSelectedListener(this);
        ddlCustLocation.setOnItemSelectedListener(this);

        fDate.setOnClickListener(this);
        tDate.setOnClickListener(this);
        followup_fDate.setOnClickListener(this);
        followup_tDate.setOnClickListener(this);
        reschedule_fDate.setOnClickListener(this);
        reschedule_tDate.setOnClickListener(this);
        btnApply.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        datePickerListener = setDatepicker();
    }


    private void loadData() {
        categories.add("Automobile");
        categories.add("Business Services");
        categories.add("Computers");
        categories.add("Education");
        categories.add("Personal");
        categories.add("Travel");

        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        ddlApproveStatus.setAdapter(dataAdapter);
        employeeList = getdata.EmployeeList(UserDetails.getUser_id(), new NetworkResult() {
            @Override
            public void handlerResult(String result) {
                employeeDetailList = new ArrayList<>();
                for (int i = 0; employeeList.size() > i; i++) {
                    Variables.Details details = new Variables.Details();
                    details.data = employeeList.get(i).employee_name;
                    details.gid = employeeList.get(i).employee_gid;
                    employeeDetailList.add(details);
                }
                setAdapter(dataAdapter);
            }

            @Override
            public void handlerError(String result) {

            }
        });


       /* List<Variables.Details> detailsList = new ArrayList<>();
        for (int i = 0; 5 > i; i++) {
            Variables.Details details = new Variables.Details();
            details.data = "ponraj" + i;
            details.gid = i;
            detailsList.add(details);
        }*/


    }

    private void setAdapter(ArrayAdapter<String> dataAdapter) {
        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getApplicationContext(), R.layout.list_item, employeeDetailList);

        ddlEmployee.setAdapter(spinnerAdapter);
        ddlEmployee.setSelection(spinnerAdapter.getPosition(2));
        //ddlEmployee.setSelection(employee_gid);
        ddlScheduleType.setAdapter(dataAdapter);
        ddlCustGroup.setAdapter(dataAdapter);
        ddlCustName.setAdapter(dataAdapter);
        ddlCustLocation.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, datePickerListener, year, month, dayOfMonth);
        switch (v.getId()) {

            case R.id.txtSRFDate:
                datePickerDialog.getDatePicker().setTag(R.id.txtSRFDate);
                datePickerDialog.show();
                break;
            case R.id.txtSRTDate:
                datePickerDialog.getDatePicker().setTag(R.id.txtSRTDate);
                datePickerDialog.show();
                break;
            case R.id.txtSRFollowupFDate:
                datePickerDialog.getDatePicker().setTag(R.id.txtSRFollowupFDate);
                datePickerDialog.show();
                break;
            case R.id.txtSRFollowupTDate:
                datePickerDialog.getDatePicker().setTag(R.id.txtSRFollowupTDate);
                datePickerDialog.show();
                break;
            case R.id.txtSRRescheduleFDate:
                datePickerDialog.getDatePicker().setTag(R.id.txtSRRescheduleFDate);
                datePickerDialog.show();
                break;
            case R.id.ddlSREmployee:
                datePickerDialog.getDatePicker().setTag(R.id.txtSRRescheduleTDate);
                datePickerDialog.show();
                break;
            case R.id.ddlSRType:
                datePickerDialog.getDatePicker().setTag(R.id.txtSRRescheduleTDate);
                datePickerDialog.show();
                break;
            case R.id.btnSRApply:
                setSearchData();
                break;
            case R.id.btnSRClear:
                clearSearchData();
                break;
        }
    }

    private void clearSearchData() {
        searchDetail = new Bundle();
        ddlEmployee.setSelection(0);
        ddlScheduleType.setSelection(0);
        ddlCustGroup.setSelection(0);
        ddlCustName.setSelection(0);
        ddlCustLocation.setSelection(0);
        String from_date = getResources().getString(R.string.from_date);
        String to_date = getResources().getString(R.string.to_date);
        fDate.setText(from_date);
        tDate.setText(to_date);
        followup_fDate.setText(from_date);
        followup_tDate.setText(to_date);
        reschedule_fDate.setText(from_date);
        reschedule_tDate.setText(to_date);
    }

    private void setSearchData() {
        String a = ddlEmployee.getSelectedItem().toString();
        searchDetail.putString("employee_gid", "");
        searchDetail.putString("scheduletype_gid", fDate.getText().toString());
        searchDetail.putString("approve_status", ddlApproveStatus.getSelectedItem().toString());
        searchDetail.putString("customer_group_gid", fDate.getText().toString());
        searchDetail.putString("customer_gid", fDate.getText().toString());
        searchDetail.putString("customer_location_gid", fDate.getText().toString());

        searchDetail.putString("fDate", fDate.getText().toString());
        searchDetail.putString("tDate", tDate.getText().toString());
        searchDetail.putString("f_fDate", followup_fDate.getText().toString());
        searchDetail.putString("f_tDate", followup_tDate.getText().toString());
        searchDetail.putString("r_fDate", reschedule_fDate.getText().toString());
        searchDetail.putString("r_tDate", reschedule_tDate.getText().toString());

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

                if (tag == R.id.txtSRFDate) {
                    fDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                } else if (tag == R.id.txtSRTDate) {
                    tDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                } else if (tag == R.id.txtSRFollowupFDate) {
                    followup_fDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                } else if (tag == R.id.txtSRFollowupTDate) {
                    followup_tDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                } else if (tag == R.id.txtSRRescheduleFDate) {
                    reschedule_fDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                } else if (tag == R.id.txtSRRescheduleTDate) {
                    reschedule_tDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                }
            }

        };
        return onDateSetListener;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
        super.onBackPressed();

    }
}
