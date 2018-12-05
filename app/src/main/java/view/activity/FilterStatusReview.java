package view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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

import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.vsolv.bigflow.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import DataBase.GetData;
import constant.Constant;
import models.Common;
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
    DatePickerDialog.OnDateSetListener datePickerListener;
    private Calendar currentDate;
    private GetData getdata;
    private List<Variables.Employee> employeeList;
    private List<Variables.ScheduleType> scheduleTypeList;
    private ArrayList<Variables.Details> employeeDetailList, scheduleDetailList;
    private ProgressDialog progressDialog;
    private CustomSpinnerAdapter spinnerAdapter;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_status_review);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bundle = new Bundle();
        if (getIntent() != null) {

            bundle = getIntent().getExtras();

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
        employeeList = new ArrayList<>();
        scheduleTypeList = new ArrayList<>();
        getdata = new GetData(getApplicationContext());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
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
        progressDialog.show();

        categories.add(Constant.status_review_pending);
        categories.add(Constant.status_review_approved);
        categories.add(Constant.status_review_reject);


        fDate.setText(bundle.getString(Constant.key_fdate, getResources().getString(R.string.choose_date)));
        tDate.setText(bundle.getString(Constant.key_fdate, getResources().getString(R.string.choose_date))
        );
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categories);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        ddlApproveStatus.setAdapter(dataAdapter);
        ddlApproveStatus.setSelection(dataAdapter.getPosition(bundle.getString(Constant.key_sch_review_status, Constant.status_review_pending)));

        employeeList = getdata.EmployeeList(UserDetails.getUser_id(), new NetworkResult() {
            @Override
            public void handlerResult(String result) {
                employeeDetailList = new ArrayList<>();
                Variables.Details details = new Variables.Details();
                details.data = getResources().getString(R.string.choose);
                details.gid = 0;
                employeeDetailList.add(details);
                for (int i = 0; employeeList.size() > i; i++) {
                    details = new Variables.Details();
                    details.data = employeeList.get(i).employee_name;
                    details.gid = employeeList.get(i).employee_gid;
                    employeeDetailList.add(details);
                }
                spinnerAdapter = new CustomSpinnerAdapter(getApplicationContext(), R.layout.list_item, employeeDetailList);
                ddlEmployee.setAdapter(spinnerAdapter);
                ddlEmployee.setSelection(spinnerAdapter.getPosition(bundle.getInt(Constant.key_employee_gid, 0)));
                getScheduleType();
            }

            @Override
            public void handlerError(String result) {
                progressDialog.cancel();
            }
        });


    }

    private void getScheduleType() {
        scheduleTypeList = getdata.scheduleTypeList(new NetworkResult() {
            @Override
            public void handlerResult(String result) {
                scheduleDetailList = new ArrayList<>();
                Variables.Details details = new Variables.Details();
                details.data = getResources().getString(R.string.choose);
                details.gid = 0;
                scheduleDetailList.add(details);
                for (int i = 0; scheduleTypeList.size() > i; i++) {
                    details = new Variables.Details();
                    details.data = scheduleTypeList.get(i).schedule_type_name;
                    details.gid = scheduleTypeList.get(i).schedule_type_id;
                    scheduleDetailList.add(details);
                }
                spinnerAdapter = new CustomSpinnerAdapter(getApplicationContext(), R.layout.list_item, scheduleDetailList);
                ddlScheduleType.setAdapter(spinnerAdapter);
                ddlScheduleType.setSelection(spinnerAdapter.getPosition(bundle.getInt(Constant.key_sch_type_gid, 0)));
                progressDialog.cancel();
            }

            @Override
            public void handlerError(String result) {
                progressDialog.cancel();
            }
        });
    }

    private void setAdapter(ArrayAdapter<String> dataAdapter) {


        //ddlEmployee.setSelection(employee_gid);

//        ddlCustGroup.setAdapter(dataAdapter);
//        ddlCustName.setAdapter(dataAdapter);
//        ddlCustLocation.setAdapter(dataAdapter);
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
                if (!fDate.getText().toString().equals(getResources().getString(R.string.choose_date))) {
                    Variables.calendarDate cal = new Variables.calendarDate(fDate.getText().toString());
                    datePickerDialog = new DatePickerDialog(this, datePickerListener, cal.getYear(), cal.getMonth(), cal.getDayofmonth());
                }
                datePickerDialog.getDatePicker().setTag(R.id.txtSRFDate);
                datePickerDialog.show();
                break;
            case R.id.txtSRTDate:
                if (!tDate.getText().toString().equals(getResources().getString(R.string.choose_date))) {
                    Variables.calendarDate cal = new Variables.calendarDate(tDate.getText().toString());
                    datePickerDialog = new DatePickerDialog(this, datePickerListener, cal.getYear(), cal.getMonth(), cal.getDayofmonth());
                }
                datePickerDialog.getDatePicker().setTag(R.id.txtSRTDate);
                datePickerDialog.show();
                break;
            case R.id.txtSRFollowupFDate:
                if (!followup_fDate.getText().toString().equals(getResources().getString(R.string.choose_date))) {
                    Variables.calendarDate cal = new Variables.calendarDate(followup_fDate.getText().toString());
                    datePickerDialog = new DatePickerDialog(this, datePickerListener, cal.getYear(), cal.getMonth(), cal.getDayofmonth());
                }
                datePickerDialog.getDatePicker().setTag(R.id.txtSRFollowupFDate);
                datePickerDialog.show();
                break;
            case R.id.txtSRFollowupTDate:
                if (!followup_tDate.getText().toString().equals(getResources().getString(R.string.choose_date))) {
                    Variables.calendarDate cal = new Variables.calendarDate(followup_tDate.getText().toString());
                    datePickerDialog = new DatePickerDialog(this, datePickerListener, cal.getYear(), cal.getMonth(), cal.getDayofmonth());
                }
                datePickerDialog.getDatePicker().setTag(R.id.txtSRFollowupTDate);
                datePickerDialog.show();
                break;
            case R.id.txtSRRescheduleFDate:
                if (!reschedule_fDate.getText().toString().equals(getResources().getString(R.string.choose_date))) {
                    Variables.calendarDate cal = new Variables.calendarDate(reschedule_fDate.getText().toString());
                    datePickerDialog = new DatePickerDialog(this, datePickerListener, cal.getYear(), cal.getMonth(), cal.getDayofmonth());
                }
                datePickerDialog.getDatePicker().setTag(R.id.txtSRRescheduleFDate);
                datePickerDialog.show();
                break;
            case R.id.txtSRRescheduleTDate:
                if (!reschedule_tDate.getText().toString().equals(getResources().getString(R.string.choose_date))) {
                    Variables.calendarDate cal = new Variables.calendarDate(reschedule_tDate.getText().toString());
                    datePickerDialog = new DatePickerDialog(this, datePickerListener, cal.getYear(), cal.getMonth(), cal.getDayofmonth());
                }
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
        String chooseDate = getResources().getString(R.string.choose_date);
        fDate.setText(chooseDate);
        tDate.setText(chooseDate);
        followup_fDate.setText(chooseDate);
        followup_tDate.setText(chooseDate);
        reschedule_fDate.setText(chooseDate);
        reschedule_tDate.setText(chooseDate);
    }

    private void setSearchData() {
        Variables.Details employee = (Variables.Details) ddlEmployee.getSelectedItem();
        searchDetail = new Bundle();
        searchDetail.putInt(Constant.key_employee_gid, employee.gid);
        searchDetail.putString(Constant.key_employee_name, employee.data);
        searchDetail.putInt(Constant.key_customer_gid, 0);
        searchDetail.putString(Constant.key_sch_review_status, ddlApproveStatus.getSelectedItem().toString());
        searchDetail.putInt(Constant.key_cust_group_gid, 0);
        searchDetail.putInt(Constant.key_loaction_gid, 0);
        Variables.Details type = (Variables.Details) ddlScheduleType.getSelectedItem();
        searchDetail.putInt(Constant.key_sch_type_gid, type.gid);

        searchDetail.putString(Constant.key_fdate, fDate.getText().toString());
        searchDetail.putString(Constant.key_tdate, tDate.getText().toString());
        searchDetail.putString(Constant.key_followup_fdate, followup_fDate.getText().toString());
        searchDetail.putString(Constant.getKey_followup_tdate, followup_tDate.getText().toString());
        searchDetail.putString(Constant.key_reschedule_fdate, reschedule_fDate.getText().toString());
        searchDetail.putString(Constant.key_reschedule_tdate, reschedule_tDate.getText().toString());

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
                    fDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                } else if (tag == R.id.txtSRTDate) {
                    tDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                } else if (tag == R.id.txtSRFollowupFDate) {
                    followup_fDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                } else if (tag == R.id.txtSRFollowupTDate) {
                    followup_tDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                } else if (tag == R.id.txtSRRescheduleFDate) {
                    reschedule_fDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                } else if (tag == R.id.txtSRRescheduleTDate) {
                    reschedule_tDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
