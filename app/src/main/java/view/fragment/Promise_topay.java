package view.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.vsolv.bigflow.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import constant.Constant;
import models.UserDetails;
import network.CallbackHandler;
import presenter.VolleyCallback;
import view.activity.DashBoardActivity;

import static java.lang.Integer.parseInt;

public class Promise_topay extends Fragment implements View.OnClickListener {
    Button Pay_submit;
    EditText txtDate, amounttxt;
    private int mYear, mMonth, mDay;
    private Bundle customer_details, Remark_intent;
    int schedule_type_gid, customer_gid;
    private String date, pay_date;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Remark_intent = new Bundle();
        View rootView = inflater.inflate(R.layout.fragment_promise_topay, container, false);
        loadView(rootView);
        initializeView();
        return rootView;

    }

    private void loadView(View view) {
        txtDate = (EditText) view.findViewById(R.id.pay_date);
        Pay_submit = (Button) view.findViewById(R.id.pay_submitbtn);
        amounttxt = (EditText) view.findViewById(R.id.pay_amount);
    }

    private void initializeView() {
        txtDate.setOnClickListener(this);
        Pay_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == txtDate) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), datePickerListener, mYear, mMonth, mDay);
            c.add(Calendar.DAY_OF_MONTH, 1);
            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
            datePickerDialog.setCancelable(false);
            datePickerDialog.setTitle("Choose Remark date");
            datePickerDialog.show();
        } else if (view == Pay_submit) {
            if (pay_date != null) {
                if (getActivity().getIntent() != null) {
                    customer_details = getActivity().getIntent().getExtras();
                    customer_gid = customer_details.getInt(Constant.key_customer_gid,0);
                    schedule_type_gid = customer_details.getInt(Constant.key_sch_type_gid,0);
                }

                JSONObject json = new JSONObject();
                JSONObject paramjson = new JSONObject();

                try {

                    json.put("schedule_type_gid", schedule_type_gid);
                    json.put("schedule_gid", 977);
                    json.put("TYPE", "UPDATE");
                    json.put("customer_gid", customer_gid);
                    json.put("Date", "2018-10-1");
                    json.put("ls_followup_date", "02/10/2018");
                    json.put("followupreason_gid", 23);
                    json.put("sechedule_ref", 0);
                    json.put("resechedule_date", "02/10/2018");
                    paramjson.put("parms", json);

                } catch (JSONException e) {
                    Log.e("Remark", e.getMessage());
                }

                String URL = Constant.URL + "FET_Schedule_Set?Entity_gid=" + UserDetails.getEntity_gid() + "&Emp_gid=" + UserDetails.getUser_id();
                CallbackHandler.sendReqest(getActivity(), Request.Method.POST, paramjson.toString(), URL, new VolleyCallback() {

                    @Override
                    public void onSuccess(String result) {

                        if (("\"" + "SUCCESS" + "\"").equals(result)) {
                            Toast.makeText(getActivity(), "Saved successfully.!", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity(), "Not saved successfully.!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(String result) {

                        Log.e("p2p", result);
                    }


                });

            } else {

                Toast.makeText(getActivity(), "Enter Valid Date", Toast.LENGTH_LONG).show();

            }

        }


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
            String Date_db = day + "/" + month_to_db + "/" + year1;
            txtDate.setText(Date);
            pay_date = Date_db;

        }
    };
}
