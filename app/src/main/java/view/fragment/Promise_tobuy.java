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

import constant.Constant;
import models.Common;
import models.UserDetails;

import com.android.volley.Request;
import com.vsolv.bigflow.R;

import models.Variables;
import network.CallbackHandler;

import presenter.VolleyCallback;
import view.activity.DashBoardActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;

import static java.lang.Integer.parseInt;


public class Promise_tobuy extends Fragment implements View.OnClickListener {
    Button remark_submit;
    EditText txtDate, remark_text;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Bundle customer_details, Remark_intent;
    int schedule_type_gid, customer_gid;


    public Promise_tobuy() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Remark_intent = new Bundle();
        View rootView = inflater.inflate(R.layout.fragment_promise_tobuy, container, false);
        txtDate = (EditText) rootView.findViewById(R.id.in_date);
        remark_submit = (Button) rootView.findViewById(R.id.remark_submitbtn);
        remark_text = (EditText) rootView.findViewById(R.id.remark);
        txtDate.setOnClickListener(this);
        remark_submit.setOnClickListener(this);
        return rootView;

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

        } else if (view.getId() == R.id.remark_submitbtn) {

            if (txtDate.getText().toString().trim().length() == 0) {
                txtDate.setError("*");
                return;
            }
            if (remark_text.getText().toString().trim().length() == 0) {
                remark_text.setError("*");
                return;
            }


            if (getActivity().getIntent() != null) {
                customer_details = getActivity().getIntent().getExtras();
                customer_gid = customer_details.getInt(Constant.key_customer_gid,0);
                schedule_type_gid = customer_details.getInt(Constant.key_sch_type_gid,0);
            }

            JSONObject json = new JSONObject();
            JSONObject paramjson = new JSONObject();
            JSONObject schduljson = new JSONObject();
            JSONObject entityjson = new JSONObject();
            JSONObject entityjson1 = new JSONObject();
            try {

                schduljson.put("Cust_Gid", customer_gid);
                schduljson.put("Emp_Gid", UserDetails.getUser_id());
                schduljson.put("ScheduleType_Name", "BOOKING");
                schduljson.put("FollowUpReason_Gid", 20);
                schduljson.put("FollowUp_Date", Common.convertDateString(txtDate.getText().toString(), Constant.date_display_format, "yyyy-MM-dd"));
                schduljson.put("Ref_Gid", 0);
                schduljson.put("Create_By", UserDetails.getUser_id());
                schduljson.put("Remark", remark_text.getText().toString());

                json.put("Schedule_Update", schduljson);
                entityjson.put("Entity", entityjson1);
                json.put("Entity", entityjson);

                paramjson.put("PARMS", json);
                Log.e("PARMS", paramjson.toString());

            } catch (JSONException e) {
                Log.e("Remark", e.getMessage());
            }

            String URL = Constant.URL + "FET_Schedule_Update?Entity_gid=" + UserDetails.getEntity_gid() +
                    "&Type=SCHEDULE&Sub_Type=REFERENCE";
            CallbackHandler.sendReqest(getActivity(), Request.Method.POST, paramjson.toString(), URL, new VolleyCallback() {

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.getString("MESSAGE");

                        if (message.equals("SUCCESS")) {
                            Toast.makeText(getActivity(), "Saved successfully!.", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "Not saved successfully!.", Toast.LENGTH_LONG).show();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String result) {
                    Log.e("Remark", result);
                }


            });

        }
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            txtDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);

        }
    };

}