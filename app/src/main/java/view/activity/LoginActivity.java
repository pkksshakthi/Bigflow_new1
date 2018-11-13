package view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.Request;
import com.vsolv.bigflow.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


import DataBase.DataBaseHandler;
import constant.Constant;
import models.Common;
import models.UserDetails;
import models.UserMenu;
import models.Variables;
import network.CallbackHandler;
import presenter.UserSessionManager;
import presenter.VolleyCallback;

public class LoginActivity extends Activity {
    Button loginButton;
    EditText loginUserName, loginPassword;
    Integer errorCode;
    UserSessionManager session;
    // private Prog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new UserSessionManager(getApplicationContext());
        loginUserName = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        loginButton = (Button) findViewById(R.id.loginButton);
        errorCode = checkconnection();
        if (errorCode == 101) {
            Toast.makeText(getApplicationContext(), "Please switch on 'Internet' Connection", Toast.LENGTH_LONG).show();
            setVisibility(View.VISIBLE, View.GONE);
        } else if (errorCode == 102) {
            setVisibility(View.VISIBLE, View.GONE);
        } else if (errorCode == 103) {
            setVisibility(View.GONE, View.VISIBLE);
            HashMap<String, String> user = new HashMap<String, String>();
            user = session.getUserDetails();
            loginRequest(user.get(session.user_id), user.get(session.user_password));
        } else if (errorCode == 104) {
            setVisibility(View.GONE, View.VISIBLE);
            HashMap<String, String> user = session.getUserDetails();
            try {
                JSONObject jobj = new JSONObject(user.get(session.user_details));
                loadData(jobj);
            } catch (JSONException ex) {

            }
        } else if (errorCode == 105) {
            Toast.makeText(getApplicationContext(), "Switch on 'Internet' to Login", Toast.LENGTH_LONG).show();
            setVisibility(View.VISIBLE, View.GONE);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(View.GONE, View.VISIBLE);
                String userName = loginUserName.getText().toString();
                String password = loginPassword.getText().toString();
                if (checkconnection() == 101) {
                    Toast.makeText(getApplicationContext(), "Please switch on 'Internet' Connection", Toast.LENGTH_LONG).show();
                    setVisibility(View.VISIBLE, View.GONE);
                } else if (userName.length() > 0 && password.length() > 0) {
                    loginRequest(userName, password);
                } else {
                    if (userName.length() == 0) {
                        loginUserName.setError("Enter code");
                    } else {
                        loginPassword.setError("Enter password");
                    }
                    Toast.makeText(getApplicationContext(), "Please enter the user name and password", Toast.LENGTH_LONG).show();
                    setVisibility(View.VISIBLE, View.GONE);
                }
            }
        });
    }

    private void setVisibility(int ui, int pd) {

        findViewById(R.id.ln_login).setVisibility(ui);
        findViewById(R.id.progressBar).setVisibility(pd);
    }

    private void loginRequest(final String user_id, final String user_password) {

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put(Constant.USERNAME, user_id);
            jsonObject.put(Constant.PASSWORD, user_password);
        } catch (JSONException e) {
            Log.e("Login", e.getMessage());
        }

        String URL = Constant.URL + "login/";


        CallbackHandler.sendReqest(LoginActivity.this, Request.Method.POST, jsonObject.toString(), URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("result", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("MESSAGE");
                    if (status.equals("SUCCESS")) {
                        loadData(jsonObject.getJSONObject("DATA"));
                        session.createUserLoginSession(user_id, user_password, UserDetails.getToday_date(), jsonObject.getJSONObject("DATA").toString());

                    } else {

                        Toast.makeText(getApplicationContext(), "Please Check User Code & Password", Toast.LENGTH_LONG).show();
                        setVisibility(View.VISIBLE, View.GONE);
                    }
                } catch (JSONException e) {
                    Log.e("Login", e.getMessage());
                    setVisibility(View.VISIBLE, View.GONE);
                }

            }

            @Override
            public void onFailure(String result) {
                //pd.hide();
                Log.e("Login", result);
                setVisibility(View.VISIBLE, View.GONE);
            }
        });
    }

    private void loadData(JSONObject jsonObject) throws JSONException {

        UserDetails.setUser_code(jsonObject.getString("employee_code"));
        UserDetails.setToday_date(jsonObject.getString("date"));
        UserDetails.setUser_id(jsonObject.getInt("employee_gid"));
        UserDetails.setUser_name(jsonObject.getString("employee_name"));
        UserDetails.setEntity_gid(jsonObject.getInt("entity_gid"));
        if (isOnline(getApplicationContext())) {
            loadMenu(jsonObject);
        } else {
            startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
            finish();
            setVisibility(View.GONE, View.VISIBLE);
        }

    }

    private void loadMenu(JSONObject jsonObject) {
        String URL = Constant.URL + "user_rights?emp_gid=" + UserDetails.getUser_id();
        URL += "&Is_Mobile=Y";
        //Its from Session
        CallbackHandler.sendReqest(LoginActivity.this, Request.Method.GET, jsonObject.toString(), URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("MESSAGE");
                    if (status.equals("FOUND")) {

                        JSONArray jsonArray;
                        jsonArray = jsonObject.getJSONArray("DATA");

                        DataBaseHandler dataBaseHandler = new DataBaseHandler(LoginActivity.this);
                        dataBaseHandler.Table_Truncate("gal_mst_tmenu");


                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj_json = jsonArray.getJSONObject(i);
                            ContentValues contentValues = new ContentValues();

                            contentValues.put(Constant.menu_gid, obj_json.getInt("menu_gid"));
                            contentValues.put(Constant.menu_parent_gid, obj_json.getString("menu_parent_gid"));
                            contentValues.put(Constant.menu_name, obj_json.getString("menu_name"));
                            contentValues.put(Constant.menu_link, obj_json.getString("menu_link"));
                            contentValues.put(Constant.menu_displayorder, obj_json.getInt("menu_displayorder"));
                            contentValues.put(Constant.menu_level, obj_json.getString("menu_level"));


                            String Out_Message = dataBaseHandler.Insert("gal_mst_tmenu", contentValues);

                            if (!Out_Message.equals("SUCCESS")) {
                                Toast.makeText(getApplicationContext(), "Error.:" + "Error On Menu Creation.", Toast.LENGTH_LONG).show();
                            }

                        }


                    } else {
                        Toast.makeText(getApplicationContext(), "Unsuccessful", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e("Login", e.getMessage());
                }
                startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                finish();
                setVisibility(View.GONE, View.VISIBLE);

            }

            @Override
            public void onFailure(String result) {
                Log.e("Login", result);
                setVisibility(View.VISIBLE, View.GONE);
            }
        });
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnected());
    }

    public int checkconnection() {
        if (!session.isUserLoggedIn() && !isOnline(getApplicationContext())) {
            //no session and no Internet
            return 101;
        }
        if (!session.isUserLoggedIn() && isOnline(getApplicationContext())) {
            //no session and with internet
            return 102;
        }
        if (session.isUserLoggedIn()) {
            if (isOnline(getApplicationContext())) {
                // Internet with session
                return 103;
            } else {
                // No internet with session
                HashMap<String, String> user = session.getUserDetails();
                Date d1 = Common.convertDate(user.get(session.user_loginDate), "dd-MMM-yyyy");
                Date d2 = new Date();
                d2.setTime(d1.getTime());
                int I = d1.compareTo(d2);
//                if ((Common.convertDate(user.get(session.user_loginDate), "dd-MMM-yyyy")).equals(new Date())) {
                if (d1.compareTo(d2) == 0) {
                    return 104;
                } else {
                    return 105;
                }

            }
        }
        return 0;
    }

}
