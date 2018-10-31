package DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.util.Log;

import com.android.volley.Request;
import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.vsolv.bigflow.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import constant.Constant;
import models.Common;
import models.UserDetails;
import models.Variables;
import network.CallbackHandler;
import presenter.NetworkResult;
import presenter.VolleyCallback;

public class GetData {
    private static String URL;
    private List<Variables.ScheduleType> mScheduleTypeList;
    private List<Variables.FollowupReason> mFollowupReasonList;
    private static List<Variables.Product> mProductList;
    private static Context mContext;

    public GetData(Context context) {
        this.mContext = context;
    }

    public List<Variables.ScheduleType> scheduleTypeList(final NetworkResult networkResult) {
        mScheduleTypeList = new ArrayList<>();
        URL = Constant.URL + "Schedule_Master?";
        URL = URL + "&Action=SCHEDULE_TYPE&Entity_gid=" + UserDetails.getEntity_gid();

        CallbackHandler.sendReqest(mContext, Request.Method.GET, "", URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("MESSAGE");
                    if (message.equals("FOUND")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("DATA");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj_json = jsonArray.getJSONObject(i);
                            Variables.ScheduleType scheduleType = new Variables.ScheduleType();
                            scheduleType.schedule_type_id = obj_json.getInt("scheduletype_gid");
                            scheduleType.schedule_type_name = obj_json.getString("scheduletype_name");
                            mScheduleTypeList.add(scheduleType);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                networkResult.handlerResult("SUCCESS");

            }

            @Override
            public void onFailure(String result) {
                networkResult.handlerError("ERROR");
                Log.e("Getdata-scheduletype", result);
            }
        });

        return mScheduleTypeList;
    }

    public List<Variables.FollowupReason> followupList(int scheduletype_id) {
        mFollowupReasonList = new ArrayList<>();
        URL = Constant.URL + "Schedule_Master?Schedule_Type_gid=" + scheduletype_id;
        URL = URL + "&Action=FOLLOWUP_REASON&Entity_gid=" + UserDetails.getEntity_gid();

        CallbackHandler.sendReqest(mContext, Request.Method.GET, "", URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("MESSAGE");
                    if (message.equals("FOUND")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("DATA");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj_json = jsonArray.getJSONObject(i);
                            Variables.FollowupReason followup = new Variables.FollowupReason();
                            followup.followup_id = obj_json.getInt("followupreason_gid");
                            followup.followup_name = obj_json.getString("followupreason_name");
                            mFollowupReasonList.add(followup);
                        }
                    }
                    Variables.FollowupReason followup = new Variables.FollowupReason();
                    followup.followup_id = 1;
                    followup.followup_name = "BOOKING";
                    mFollowupReasonList.add(followup);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(String result) {

                Log.e("Getdata-scheduletype", result);
            }
        });

        return mFollowupReasonList;
    }

    public static List<Variables.Product> productList(String product_name) {
        mProductList = new ArrayList<>();
        URL = Constant.URL + "Schedule_Master?";
        URL = URL + "&Action=SCHEDULE_TYPE&Entity_gid=" + UserDetails.getEntity_gid();

        CallbackHandler.sendReqest(mContext, Request.Method.GET, "", URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("MESSAGE");
                    if (message.equals("FOUND")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("DATA");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj_json = jsonArray.getJSONObject(i);
                            Variables.Product product = new Variables.Product();
                            product.product_id = obj_json.getInt("scheduletype_gid");
                            product.product_name = obj_json.getString("scheduletype_name");
                            mProductList.add(product);
                        }
                    }
                    Variables.Product product = new Variables.Product();
                    product.product_id = 1;
                    product.product_name = "BOOKING";
                    mProductList.add(product);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(String result) {

                Log.e("Getdata-scheduletype", result);
            }
        });

        return mProductList;
    }

    public List<Variables.Customer> ScheduledCustomerList(int employee_gid, String schedule_date, final NetworkResult networkResult) {
        final List<Variables.Customer> customerList = new ArrayList<>();

        URL = Constant.URL + "FET_Schedule?emp_gid=" + employee_gid;
        URL = URL + "&action=customerwise&date=" + schedule_date;
        JSONObject jsonObject = new JSONObject();
        try {
            List<Integer> list = new ArrayList<>();
            list.add(Integer.parseInt(UserDetails.getEntity_gid()));
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("entity_gid", new JSONArray(list));
            jsonObject1.put("client_gid", new JSONArray());
            jsonObject.put("Classification", jsonObject1);
            //jsonObject.put("Schedule_Date", Date);
        } catch (JSONException e) {
            Log.e("Login", e.getMessage());
        }
        CallbackHandler.sendReqest(mContext, Request.Method.POST, jsonObject.toString(), URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("MESSAGE");
                    if (status.equals("FOUND")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("DATA");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj_json = jsonArray.getJSONObject(i);
                            Variables.Customer customer = new Variables.Customer();
                            customer.customer_name = obj_json.getString("display_name");
                            customer.customer_location = obj_json.getString("location_name");
                            customer.customer_gid = obj_json.getInt("customer_gid");
                            customerList.add(customer);
                        }

                    }
                    networkResult.handlerResult("SUCCESS");
                } catch (JSONException e) {
                    Log.e("Login", e.getMessage());
                }

            }

            @Override
            public void onFailure(String result) {
                if (result.equals("NoConnectionError"))
                    ShowSnakbar(Type.WARNING, "Please Check Internet Connection.");
                networkResult.handlerError("ERROR");
                Log.e("Login", result);

            }
        });
        return customerList;
    }

    public List<Object> ScheduledScheduleType(int customer_gid, String Date, final NetworkResult networkResult) {

        final List<Object> scheduleTypeList = new ArrayList<>();

        String URL = Constant.URL + "FETScheduleCustomer?";
        URL = URL + "&Type=CUSTOMER&Sub_Type=UNIQUE";
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("Customer_Gid", customer_gid);
            jsonObject.put("Schedule_Date", Date);
        } catch (JSONException e) {
            Log.e("Login", e.getMessage());
        }


        CallbackHandler.sendReqest(mContext, Request.Method.POST, jsonObject.toString(), URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("MESSAGE");
                    if (message.equals("FOUND")) {
                        JSONObject objData = jsonObject.getJSONObject("DATA");
                        JSONArray scheduleTask = objData.getJSONArray("ScheudleTask");
                        JSONArray nonScheduleTask = objData.getJSONArray("Non_ScheduleTask");
                        JSONArray salesDetails = objData.getJSONArray("Sales_Details");
                        if (scheduleTask.length() != 0) {
                            scheduleTypeList.add("Scheduled");
                            for (int i = 0; i < scheduleTask.length(); i++) {
                                JSONObject obj_json = scheduleTask.getJSONObject(i);
                                Variables.ScheduleType scheduleType = new Variables.ScheduleType();
                                scheduleType.schedule_type_id = obj_json.getInt("ScheduleType_Gid");
                                scheduleType.schedule_type_name = obj_json.getString("ScheduleType_Name");
                                scheduleType.schedule_gid = obj_json.getInt("Schedule_Gid");
                                scheduleType.schedule_status = obj_json.getString("Schedule_Status");
                                if (scheduleType.schedule_type_name.equals("BOOKING") && salesDetails.length() > 0) {
                                    List<Variables.Details> detailsList = new ArrayList<>();
                                    for (int j = 0; j < salesDetails.length(); j++) {
                                        JSONObject sales_json = salesDetails.getJSONObject(j);
                                        Variables.Details details = new Variables.Details();
                                        details.data = sales_json.getString("SO_NO");
                                        details.gid = sales_json.getInt("SO_Gid");
                                        //details.dataColor=mContext.getResources().getColor(R.color.success);
                                        detailsList.add(details);
                                    }
                                    scheduleType.schedule_details = detailsList;
                                }
                                scheduleTypeList.add(scheduleType);
                            }
                        }
                        if (nonScheduleTask.length() != 0) {
                            scheduleTypeList.add("Unscheduled");
                            for (int i = 0; i < nonScheduleTask.length(); i++) {
                                JSONObject obj_json = nonScheduleTask.getJSONObject(i);
                                Variables.ScheduleType scheduleType = new Variables.ScheduleType();
                                scheduleType.schedule_type_id = obj_json.getInt("ScheduleType_Gid");
                                scheduleType.schedule_type_name = obj_json.getString("ScheduleType_Name");
                                scheduleType.schedule_gid = 0;
                                scheduleType.schedule_status = "";
                                scheduleTypeList.add(scheduleType);
                            }
                        }

                    }
                    networkResult.handlerResult("SUCCESS");

                } catch (JSONException e) {
                    e.printStackTrace();

                }


            }

            @Override
            public void onFailure(String result) {
                if (result.equals("NoConnectionError"))
                    ShowSnakbar(Type.WARNING, "Please Check Internet Connection.");
                Log.e("Getdata-scheduletype", result);
                networkResult.handlerError("Error");
            }
        });
        return scheduleTypeList;
    }

    public static void ShowSnakbar(Type type, String message) {

        Snackbar.with(mContext, null)
                .type(type)
                .message(message)
                .duration(Duration.SHORT)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

    public static List<Variables.History> HistoryList(int customer_gid, int employee_gid, final NetworkResult networkResult) {
        final List<Variables.History> mHistoryList;
        mHistoryList = new ArrayList<>();
        URL = Constant.URL + "FET_ScheduleHistory?";
        URL = URL + "&Employee_Gid=" + employee_gid + "&Customer_Gid=" + customer_gid + "&Limit=20&Entity_Gid=" + UserDetails.getEntity_gid();

        CallbackHandler.sendReqest(mContext, Request.Method.GET, "", URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("MESSAGE");
                    if (message.equals("FOUND")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("DATA");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj_json = jsonArray.getJSONObject(i);
                            Variables.History history = new Variables.History();
                            history.schedule_date = obj_json.getString("schedule_date");
                            history.schedule_type = obj_json.getString("scheduletype_name");
                            history.employee_name = obj_json.getString("employee_name");
                            history.followup = obj_json.getString("followupreason_name");
                            mHistoryList.add(history);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    networkResult.handlerResult("SUCCESS");
                }


            }

            @Override
            public void onFailure(String result) {
                if (result.equals("NoConnectionError"))
                    ShowSnakbar(Type.WARNING, "Please Check Internet Connection.");
                Log.e("Getdata-scheduletype", result);
            }
        });

        return mHistoryList;
    }

    public static List<Object> CustomerFilterList(int customer_gid) {
        final List<Object> mCustomerList;
        String selectQuary;
        SQLiteDatabase sqLiteDatabase;
        Cursor cursor;

        mCustomerList = new ArrayList<>();
        selectQuary = "Select * from " + Constant.AStable_name + " where " + Constant.ASismanagement + "=0";
        SQLiteOpenHelper database = new DataBaseHandler(mContext);
        sqLiteDatabase = database.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery(selectQuary, null);


        if (cursor.moveToFirst()) {
            mCustomerList.add("Management");
            do {

                Variables.Customer customer = new Variables.Customer();

                customer.customer_gid = cursor.getInt(cursor.getColumnIndex(Constant.AScustomer_gid));
                customer.customer_name = cursor.getString(cursor.getColumnIndex(Constant.AScustomer_name));
                customer.customer_location = cursor.getString(cursor.getColumnIndex(Constant.ASlocation_name));
                customer.isEditable = cursor.getString(cursor.getColumnIndex(Constant.ASiseditable));

                mCustomerList.add(customer);
            } while (cursor.moveToNext());
        }
        cursor.close();


        selectQuary = "Select * from " + Constant.AStable_name + " where " + Constant.ASstatus + "='FOLLOWUP'";
        cursor = sqLiteDatabase.rawQuery(selectQuary, null);

        if (cursor.moveToFirst()) {
            mCustomerList.add("Follow Up");
            do {

                Variables.Customer customer = new Variables.Customer();

                customer.customer_gid = cursor.getInt(cursor.getColumnIndex(Constant.AScustomer_gid));
                customer.customer_name = cursor.getString(cursor.getColumnIndex(Constant.AScustomer_name));
                customer.customer_location = cursor.getString(cursor.getColumnIndex(Constant.ASlocation_name));
                customer.isEditable = cursor.getString(cursor.getColumnIndex(Constant.ASiseditable));

                mCustomerList.add(customer);
            } while (cursor.moveToNext());
        }
        cursor.close();

        selectQuary = "Select * from " + Constant.AStable_name + " where " + Constant.ASstatus + "='RESCHEDULE'";
        cursor = sqLiteDatabase.rawQuery(selectQuary, null);

        if (cursor.moveToFirst()) {
            mCustomerList.add("ReSchedule");
            do {

                Variables.Customer customer = new Variables.Customer();

                customer.customer_gid = cursor.getInt(cursor.getColumnIndex(Constant.AScustomer_gid));
                customer.customer_name = cursor.getString(cursor.getColumnIndex(Constant.AScustomer_name));
                customer.customer_location = cursor.getString(cursor.getColumnIndex(Constant.ASlocation_name));
                customer.isEditable = cursor.getString(cursor.getColumnIndex(Constant.ASiseditable));

                mCustomerList.add(customer);
            } while (cursor.moveToNext());
        }
        cursor.close();


        selectQuary = "Select * from " + Constant.AStable_name + " where " + Constant.ASismanagement + "!=0";
        selectQuary += " and " + Constant.ASstatus + " not in ('FOLLOWUP','RESCHEDULE')";
        cursor = sqLiteDatabase.rawQuery(selectQuary, null);

        if (cursor.moveToFirst()) {
            mCustomerList.add("Others");
            do {

                Variables.Customer customer = new Variables.Customer();

                customer.customer_gid = cursor.getInt(cursor.getColumnIndex(Constant.AScustomer_gid));
                customer.customer_name = cursor.getString(cursor.getColumnIndex(Constant.AScustomer_name));
                customer.customer_location = cursor.getString(cursor.getColumnIndex(Constant.ASlocation_name));
                customer.customer_sch_status = cursor.getString(cursor.getColumnIndex(Constant.ASstatus));
                customer.isEditable = cursor.getString(cursor.getColumnIndex(Constant.ASiseditable));

                mCustomerList.add(customer);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return mCustomerList;

    }

    public static List<Variables.Details> EmployeeFilterList() {
        final List<Variables.Details> mEmployeeList;

        mEmployeeList = new ArrayList<>();
        String selectQuary = "Select * from " + Constant.employeetable_name;//+
        //" where " + Constant.ASismanagement + "=0";
        SQLiteOpenHelper database = new DataBaseHandler(mContext);
        SQLiteDatabase sqLiteDatabase = database.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuary, null);


        if (cursor.moveToFirst()) {
            do {

                Variables.Details employee = new Variables.Details();

                employee.gid = cursor.getInt(cursor.getColumnIndex(Constant.employee_gid));
                employee.data = cursor.getString(cursor.getColumnIndex(Constant.employee_name));
                mEmployeeList.add(employee);

            } while (cursor.moveToNext());
        }
        cursor.close();

        return mEmployeeList;

    }

    public static void LoadAddScheduleFilteredData(List<Integer> employee_gid, List<Integer> route_gid,
                                                   List<Integer> cluster_gid, Date schedule_date,
                                                   final NetworkResult networkResult) {
        final DataBaseHandler dataBaseHandler = new DataBaseHandler(mContext);
        dataBaseHandler.Table_Truncate(Constant.AStable_name);
        dataBaseHandler.Table_Truncate(Constant.employeetable_name);

        URL = Constant.URL + "CustomerFilter?";
        URL = URL + "&emp_gid=" + new JSONArray(employee_gid);
        URL += "&route_gid=0&cluster_gid=0&f_date=" + Common.convertDateString(schedule_date, "dd/MM/yyyy") + "&action=view";
        URL += "&Emp_gid=" + UserDetails.getUser_id() + "&date=" + UserDetails.getToday_date();
        URL += "&Entity_gid=" + UserDetails.getEntity_gid();

        CallbackHandler.sendReqest(mContext, Request.Method.GET, "", URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("MESSAGE");
                    if (message.equals("FOUND")) {
                        JSONObject jsonData = jsonObject.getJSONObject("DATA");
                        JSONArray jsonArray = jsonData.getJSONArray("customer");
                        JSONArray jsonEmployee = jsonData.getJSONArray("employee");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj_json = jsonArray.getJSONObject(i);
                            ContentValues customer = new ContentValues();
                            customer.put(Constant.AScustomer_name, obj_json.getString("customer_name"));
                            customer.put(Constant.ASlocation_name, obj_json.getString("location_name"));
                            customer.put(Constant.AScustomer_gid, obj_json.getInt("customer_gid"));
                            customer.put(Constant.ASismanagement, obj_json.getInt("Order_by"));
                            customer.put(Constant.ASstatus, obj_json.getString("sch_status"));
                            customer.put(Constant.ASiseditable, obj_json.getString("sch_edit"));
                            dataBaseHandler.Insert(Constant.AStable_name, customer);
                        }
                        List<Variables.Employee> employees = new ArrayList<>();
                        employees = getemp_list(employees, jsonEmployee);
                        for (int i = 0; i < employees.size(); i++) {
                            Variables.Employee obj_json = employees.get(i);
                            ContentValues employee = new ContentValues();
                            employee.put(Constant.employee_gid, obj_json.employee_gid);
                            employee.put(Constant.employee_name, obj_json.employee_name);
                            dataBaseHandler.Insert(Constant.employeetable_name, employee);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    networkResult.handlerResult("SUCCESS");
                }


            }

            @Override
            public void onFailure(String result) {
                if (result.equals("NoConnectionError"))
                    ShowSnakbar(Type.WARNING, "Please Check Internet Connection.");
                networkResult.handlerError("ERROR");
                Log.e("Getdata-FilteredData", result);
            }
        });


    }

    public static List<Variables.Employee> getemp_list(List<Variables.Employee> employees, JSONArray jsonArray) {

        try {
            for (int i = 0; jsonArray.length() > i; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Variables.Employee employee = new Variables.Employee();
                employee.employee_name = jsonObject.getString("emp_name");
                employee.employee_gid = jsonObject.getInt("emp_gid");
                JSONArray jsonArray1 = jsonObject.getJSONArray("child_list");
                employees.add(employee);

                if (jsonArray1.length() > 0) {
                    getemp_list(employees, jsonArray1);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return employees;
    }

    public List<Variables.Customer> SetScheduleSingle(int employee_gid, JSONArray data, Date schedule_date, final NetworkResult networkResult) {
        final List<Variables.Customer> customerList = new ArrayList<>();

        URL = Constant.URL + "FET_Schedule_Set?Emp_gid=" + UserDetails.getUser_id();
        URL = URL + "&Entity_gid=" + UserDetails.getEntity_gid();
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("emp_gid", employee_gid);
            jsonObject1.put("TYPE", "SCHEDULE_BULK");
            jsonObject1.put("Date", Common.convertDateString(schedule_date, "dd/MM/yyyy"));
            jsonObject1.put("data", new JSONObject().put("BULK_SCHEDULE", data));
            jsonObject.put("parms", jsonObject1);

        } catch (JSONException e) {
            Log.e("Login", e.getMessage());
        }
        CallbackHandler.sendReqest(mContext, Request.Method.POST, jsonObject.toString(), URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("MESSAGE");
                    networkResult.handlerResult(status);
                } catch (JSONException e) {
                    Log.e("Login", e.getMessage());
                }

            }

            @Override
            public void onFailure(String result) {
                if (result.equals("NoConnectionError"))
                    ShowSnakbar(Type.WARNING, "Please Check Internet Connection.");
                networkResult.handlerError("ERROR");
                Log.e("SetScheduleSingle", result);

            }
        });
        return customerList;
    }

    public List<Variables.Employee> EmployeeList(int employee_gid, final NetworkResult networkResult) {
        final List<Variables.Employee> mEmployeeList;
        mEmployeeList = new ArrayList<>();

        URL = Constant.URL + "Employee_Profile?";
        URL = URL + "&Action=HIERARCHY";
        URL += "&Emp_gid=" + employee_gid;
        URL += "&Entity_gid=" + UserDetails.getEntity_gid();

        CallbackHandler.sendReqest(mContext, Request.Method.GET, "", URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("MESSAGE");
                    if (message.equals("FOUND")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("DATA");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj_json = jsonArray.getJSONObject(i);
                            Variables.Employee employee = new Variables.Employee();
                            employee.employee_name = obj_json.getString("employee_name");
                            employee.employee_gid = obj_json.getInt("employee_gid");
                            mEmployeeList.add(employee);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    networkResult.handlerResult("SUCCESS");
                }


            }

            @Override
            public void onFailure(String result) {
                if (result.equals("NoConnectionError"))
                    ShowSnakbar(Type.WARNING, "Please Check Internet Connection.");
                networkResult.handlerError("ERROR");
                Log.e("Getdata-getLatLong", result);
            }
        });


        //networkResult.handlerResult("SUCCESS");
        return mEmployeeList;
    }

    public List<Variables.LatLong> getLatLong(int employee_gid, Date from_date, Date to_date, final NetworkResult networkResult) {
        final List<Variables.LatLong> mlatLongList;
        mlatLongList = new ArrayList<>();

        URL = Constant.URL + "LatLongFET?";
        URL = URL + "&Action=FET_EMPLOYEE&From_Date=" + Common.convertDateString(from_date, "yyyy-MM-dd");
        URL += "&To_Date=";
        URL += "&Emp_gid=" + employee_gid;
        URL += "&Entity_gid=" + UserDetails.getEntity_gid();

        CallbackHandler.sendReqest(mContext, Request.Method.GET, "", URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("MESSAGE");
                    if (message.equals("FOUND")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("DATA");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj_json = jsonArray.getJSONObject(i);
                            Variables.LatLong latLong = new Variables.LatLong();

                            latLong.latitude = obj_json.getDouble("latlong_latitude");
                            latLong.longitude = obj_json.getDouble("latlong_longitude");
                            latLong.title = obj_json.getString("actual_time");
                            mlatLongList.add(latLong);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    networkResult.handlerResult("SUCCESS");
                }


            }

            @Override
            public void onFailure(String result) {
                if (result.equals("NoConnectionError"))
                    ShowSnakbar(Type.WARNING, "Please Check Internet Connection.");
                networkResult.handlerError("ERROR");
                Log.e("Getdata-getLatLong", result);
            }
        });
        return mlatLongList;
    }

    public List<Variables.Comment> CommentList(List<Integer> customerList, final NetworkResult networkResult) {
        final List<Variables.Comment> mCommentList;
        mCommentList = new ArrayList<>();

        URL = Constant.URL + "Comment?";
        URL = URL + "&Action=FETCH";
        URL += "&Emp_gid=" + UserDetails.getUser_id();
        URL += "&Entity_gid=" + UserDetails.getEntity_gid();
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("action", "COMMENTS");
            jsonObject1.put("type", "TRANSACTION");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("Comment_For", "COMMENT_CUSTOMER");
            jsonObject2.put("Ref_No", new JSONArray(customerList));
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject2);
            jsonObject1.put("json", new JSONObject().put("Filters", jsonArray));
            jsonObject.put("params", jsonObject1);

        } catch (JSONException e) {
            Log.e("Login", e.getMessage());
        }

        CallbackHandler.sendReqest(mContext, Request.Method.POST, jsonObject.toString(), URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("MESSAGE");
                    if (message.equals("FOUND")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("DATA");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj_json = jsonArray.getJSONObject(i);
                            Variables.Comment comment = new Variables.Comment();

                            comment.comment_message = obj_json.getString("comments_text");
                            comment.comment_date = obj_json.getString("comments_date");
                            comment.comment_gid = obj_json.getInt("comments_gid");
                            comment.employee_gid = obj_json.getInt("comments_employeegid");
                            comment.employee_name = obj_json.getString("employee_name");
                            mCommentList.add(comment);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    networkResult.handlerResult("SUCCESS");
                }


            }

            @Override
            public void onFailure(String result) {
                if (result.equals("NoConnectionError"))
                    ShowSnakbar(Type.WARNING, "Please Check Internet Connection.");
                networkResult.handlerError("ERROR");
                Log.e("Getdata-getLatLong", result);
            }
        });
        return mCommentList;
    }

    public void SetComment(int customer_gid, Date comment_date, String Message, final NetworkResult networkResult) {
        final List<Variables.Comment> mCommentList;
        mCommentList = new ArrayList<>();

        URL = Constant.URL + "Comment?";
        URL = URL + "&Action=INSERT";
        URL += "&Emp_Gid=" + UserDetails.getUser_id();
        URL += "&Entity_Gid=" + UserDetails.getEntity_gid();
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("action", "Insert");
            jsonObject1.put("type", "EACH");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("Ref_No", customer_gid);
            jsonObject2.put("Remark", Message);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject2);

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("Employee_Gid", UserDetails.getUser_id());
            jsonObject3.put("Date", Common.convertDateString(comment_date, "yyyy-MM-dd hh:mm:ss"));
            jsonObject3.put("Comment_For", "COMMENT_CUSTOMER");
            jsonObject3.put("EACH", jsonArray);

            jsonObject1.put("json", jsonObject3);
            jsonObject.put("params", jsonObject1);

        } catch (JSONException e) {
            Log.e("Login", e.getMessage());
        }

        CallbackHandler.sendReqest(mContext, Request.Method.POST, jsonObject.toString(), URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("MESSAGE");
                    if (message.equals("FOUND")) {


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    networkResult.handlerResult(result);
                }


            }

            @Override
            public void onFailure(String result) {
                if (result.equals("NoConnectionError"))
                    ShowSnakbar(Type.WARNING, "Please Check Internet Connection.");
                networkResult.handlerError("ERROR");
                Log.e("Getdata-getLatLong", result);
            }
        });

    }
}
