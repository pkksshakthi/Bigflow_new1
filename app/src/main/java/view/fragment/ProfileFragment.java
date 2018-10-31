package view.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.vsolv.bigflow.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import constant.Constant;
import models.CustomerAdapter;
import models.UserDetails;
import models.Variables;
import network.CallbackHandler;
import presenter.VolleyCallback;
import view.activity.ServiceActivity;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    TextView Employee_mailid_text, Employee_phoneno_text, Employee_address_text, text_changepswd, EmployeeName, EmployeeCode;
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;

    public ProfileFragment() {

    }


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Profile");
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        EmployeeName = rootView.findViewById(R.id.txtEmployeeName);
        EmployeeName.setText(UserDetails.getUser_name());
        EmployeeCode = rootView.findViewById(R.id.txtEmployeeCode);
        EmployeeCode.setText(UserDetails.getUser_code());
        Employee_mailid_text = rootView.findViewById(R.id.txtEmployeeMailId);
        Employee_phoneno_text = rootView.findViewById(R.id.txtEmployeePhoneNo);
        Employee_address_text = rootView.findViewById(R.id.txtEmployeeAddress);
        text_changepswd = rootView.findViewById(R.id.text_changepswd);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        text_changepswd.setOnClickListener(this);
        String URL = Constant.URL + "/Employee_Profile?Emp_gid=" + UserDetails.getUser_id() + "&Action=EMPLOYEE_EDIT" + "&Entity_gid=" + UserDetails.getEntity_gid();
        CallbackHandler.sendReqest(getContext(), Request.Method.GET, "", URL, new VolleyCallback() {

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("MESSAGE");
                    String employee_emailid = null, Phn_no = null, address = null;
                    if (message.equals("FOUND")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("DATA");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj_json = jsonArray.getJSONObject(i);
                            employee_emailid = obj_json.getString("employee_emailid");
                            Phn_no = obj_json.getString("employee_mobileno");
                            address = obj_json.getString("address_1");
                        }
                        Employee_mailid_text.setText("Email Id:" + employee_emailid);
                        Employee_phoneno_text.setText("Contact No:" + Phn_no);
                        Employee_address_text.setText("Address:" + address);

                    }
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(String result) {

                Log.e("Changepassword_fail", result);
            }

        });


        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if (view == text_changepswd) {
            builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_dialog_changepasswd, null);
            builder.setTitle("Change Password");
            final EditText text_oldpasswd = (EditText) dialogView.findViewById(R.id.txtOldPassword);
            final EditText text_NewPassword = (EditText) dialogView.findViewById(R.id.txtNewPassword);
            final EditText text_ConfirmPassword = (EditText) dialogView.findViewById(R.id.txtConfirmPassword);

            builder.setView(dialogView);

            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    String Oldpassword = text_oldpasswd.getText().toString();
                    String Newpassword = text_NewPassword.getText().toString();
                    String Confirmpassword = text_ConfirmPassword.getText().toString();

                    if (Oldpassword.trim().length() > 0 && Newpassword.trim().length() > 0 && Confirmpassword.trim().length() > 0) {

                        if (!Newpassword.equals(Confirmpassword)) {
                            Toast.makeText(getActivity(), "New And Confirm Password Not Matched", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject Json = new JSONObject();
                        JSONObject params_Json = new JSONObject();
                        try {
                            params_Json.put("emp_code", UserDetails.getUser_code());
                            params_Json.put("old_pswd", Oldpassword);
                            params_Json.put("new_pswd", Newpassword);
                            Json.put("params", params_Json);
                            if (Json.length() > 0) {
                                String OutMessage = PasswordSet(Json);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(getActivity(), "Fill All The Required Field", Toast.LENGTH_SHORT).show();

                    }
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialog.dismiss();
                }
            });
            dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            text_oldpasswd.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (text_oldpasswd.getText().toString().length() > 0 && text_NewPassword.getText().toString().length() >0
                            && text_ConfirmPassword.getText().toString().length() >0 ) {
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                    } else {
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            text_NewPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (text_oldpasswd.getText().toString().length() > 0 && text_NewPassword.getText().toString().length() >0
                            && text_ConfirmPassword.getText().toString().length() >0 ) {
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                    } else {
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            text_ConfirmPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (text_oldpasswd.getText().toString().length() > 0 && text_NewPassword.getText().toString().length() >0
                            && text_ConfirmPassword.getText().toString().length() >0 ) {
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                    } else {
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        }

    }

    public String PasswordSet(JSONObject jsonObject) {
        progressDialog.show();
        String URL = Constant.URL + "Change_Password?Type=PROFILE_EMPCHANGE_PASSWORD&Emp_gid="+UserDetails.getUser_id() ;
        CallbackHandler.sendReqest(getContext(), Request.Method.POST, jsonObject.toString(), URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("MESSAGE");
                    Toast.makeText(getContext(), status, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
//                    if (status.equals("SUCCESS")) {
//                        Toast.makeText(getContext(), "Password Changed Successfully.", Toast.LENGTH_LONG).show();
//                        progressDialog.dismiss();
//
//
//                    } else {
//                        progressDialog.dismiss();
//                        Toast.makeText(getContext(), "Password Not Changed Successfully.", Toast.LENGTH_LONG).show();
//                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Log.e("Password", e.getMessage());
                }
            }

            @Override
            public void onFailure(String result) {
                Log.e("Password", result);
                progressDialog.dismiss();
            }
        });
        return "";
    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}