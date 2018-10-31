package view.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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

import constant.Constant;
import models.Common;
import models.StatusReviewAdapter;
import models.UserDetails;
import models.Variables;
import network.CallbackHandler;
import presenter.VolleyCallback;


public class StatusReviewFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_Title = "title";
    private static final String ARG_PARAM2 = "param2";
    public StatusReviewAdapter adapter;
    private String mParam1, mParam2;
    private View fragmentView;
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private TextView empty_view, reload;
    private EditText status_remark;
    private Bundle sessiondata;
    private RadioButton radioButton;
    private RadioGroup radioGroup;
    private ProgressDialog progressDialog;
    private ArrayList<Variables.StatusReview_List> statusReview_lists;
    private EditText alert_remark;


    public static StatusReviewFragment newInstance(String Title, String param2) {
        StatusReviewFragment fragment = new StatusReviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_Title, Title);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_Title);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("FET Review");
        View view = inflater.inflate(R.layout.fragment_statusreview, container, false);
        fragmentView = view;
        loadView(view);
        initializeView();
        loadData();

        return view;
    }

    private void loadView(View view) {
        linearLayout = (LinearLayout) view.findViewById(R.id.linearDirect);
        recyclerView = (RecyclerView) view.findViewById(R.id.StatusRecyclerView);
        empty_view = view.findViewById(R.id.empty_view);
        reload = view.findViewById(R.id.custReload);
    }


    private void initializeView() {

        sessiondata = new Bundle();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        statusReview_lists = new ArrayList<Variables.StatusReview_List>();
        reload.setOnClickListener(this);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

    }

    private void loadData_booking(final TableLayout layout, final AlertDialog.Builder builder, int SO_Header_gid) {
        if (!Common.isOnline(getContext())) {
            Snackbar.with(getActivity(), null)
                    .type(Type.WARNING)
                    .message("Please Check Internet Connection.")
                    .duration(Duration.SHORT)
                    .fillParent(true)
                    .textAlign(Align.LEFT)
                    .show();
            setVisibility(View.GONE, View.GONE, View.VISIBLE);
            return;
        }
        progressDialog.show();
        String URL = Constant.URL + "FET_SalesOrder_Get?SO_Header_gid=" + SO_Header_gid + "&Entity_gid=1&Action=BY_REF_GID";
        CallbackHandler.sendReqest(getActivity(), Request.Method.GET, "", URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    String status = jsonObject.getString("MESSAGE");
                    if (status.equals("FOUND")) {

                        JSONArray jsonsales;
                        jsonsales = jsonObject.getJSONArray("DATA");

                        int index = 0;
                        TableRow tableRow = null;
                        for (int i = 0; i < jsonsales.length(); i++) {
                            JSONObject obj_json = jsonsales.getJSONObject(i);
                            String soheader_no = obj_json.getString("soheader_no");
                            String product_name = obj_json.getString("product_name");
                            int quantity = obj_json.getInt("quantity");
                            int sodetails_amount = obj_json.getInt("sodetails_amount");
                            int sodetails_total = obj_json.getInt("sodetails_total");


                            String detail_value[] = {Integer.toString(i + 1), soheader_no, product_name, String.valueOf(quantity), String.valueOf(sodetails_amount), String.valueOf(sodetails_total)};
                            layout.setStretchAllColumns(true);
                            layout.setShrinkAllColumns(true);
                            tableRow = new TableRow(getActivity());
                            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                            tableRow.setLayoutParams(lp);
                            tableRow.setBackgroundColor(Color.WHITE);


                            for (int j = 0; j < detail_value.length; j++) {
                                TextView dtl = new TextView(getActivity());
                                dtl.setText(detail_value[j]);
                                index = Integer.parseInt(detail_value[0]);

                                dtl.setGravity(Gravity.CENTER);

                                dtl.setBackground(getResources().getDrawable(R.drawable.table_body));
                                dtl.setLayoutParams(lp);
                                tableRow.addView(dtl);
                            }

                            layout.addView(tableRow, index);
                        }

                        progressDialog.dismiss();
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(getActivity(), "Data Not Found", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    Log.e("details", e.getMessage());
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(String result) {
                Log.e("details", result);

            }
        });
    }

    private void loadData() {
        if (!Common.isOnline(getContext())) {
            Snackbar.with(getActivity(), null)
                    .type(Type.WARNING)
                    .message("Please Check Internet Connection.")
                    .duration(Duration.SHORT)
                    .fillParent(true)
                    .textAlign(Align.LEFT)
                    .show();
            setVisibility(View.GONE, View.GONE, View.VISIBLE);
            return;
        }
        progressDialog.show();
        JSONObject Json = new JSONObject();
        JSONObject filter_Json = new JSONObject();
        JSONObject classif_Json = new JSONObject();
        try {
            filter_Json.put("employee_gid", "");
            filter_Json.put("customer_gid", "");
            filter_Json.put("scheduletype_gid", 0);
            filter_Json.put("customergroup_gid", 0);
            filter_Json.put("location_gid", 0);
            filter_Json.put("login_emp_gid", UserDetails.getUser_id());

            JSONArray entity = new JSONArray();
            JSONArray client = new JSONArray();

            classif_Json.put("entity_gid", entity.put(1));
            classif_Json.put("client_gid", client);

            Json.put("Filter", filter_Json);
            Json.put("Classification", classif_Json);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String URL = Constant.URL + "FET_Review?Action=SCHEDULE_SUMMARY";


        CallbackHandler.sendReqest(getActivity(), Request.Method.POST, Json.toString(), URL, new VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("MESSAGE");

                    if (message.equals("FOUND")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("DATA");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj_json = jsonArray.getJSONObject(i);
                            String customer_name = obj_json.getString("customer_name");
                            String schedule_status = obj_json.getString("schedule_status");
                            String Employee_name = obj_json.getString("employee_name");
                            String schedule_accepted, scheduletype_name, review_remarks, review_reviewstatus, followup;
                            int schedulereview_gid;
                            int schedule_gid, soheader_gid = 0;
                            schedule_gid = obj_json.getInt("schedule_gid");
                            if (obj_json.isNull("ref_gid")) {
                                soheader_gid = 0;

                            } else {
                                soheader_gid = obj_json.getInt("ref_gid");
                            }
                            if (obj_json.getString("schedule_accepted") != null) {
                                schedule_accepted = obj_json.getString("schedule_accepted");
                            } else {
                                schedule_accepted = "";
                            }
                            if (obj_json.getString("scheduletype_name") != null) {
                                scheduletype_name = obj_json.getString("scheduletype_name");
                            } else {
                                scheduletype_name = "";
                            }
                            if (obj_json.isNull("schedulereview_gid")) {
                                schedulereview_gid = 0;
                            } else {
                                schedulereview_gid = obj_json.getInt("schedulereview_gid");

                            }
                            if (obj_json.has("schedulereview_remarks")) {
                                review_remarks = obj_json.getString("schedulereview_remarks");
                            } else {
                                review_remarks = "";
                            }
                            if (obj_json.has("schedulereview_reviewstatus")) {
                                review_reviewstatus = obj_json.getString("schedulereview_reviewstatus");
                            } else {
                                review_reviewstatus = "";
                            }
                            if (obj_json.getString("followupreason_name") != null) {
                                followup = obj_json.getString("followupreason_name");
                            } else {
                                followup = "";
                            }

                            statusReview_lists.add(new Variables.StatusReview_List(customer_name, Employee_name,
                                    schedule_status, scheduletype_name, schedule_accepted, schedulereview_gid
                                    , review_remarks, review_reviewstatus, schedule_gid, soheader_gid, followup));
                        }

                        setAdapter();
                    } else {
                        empty_view.setText(getResources().getString(R.string.error_loading));
                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }


            }

            @Override
            public void onFailure(String result) {

                Log.e("Status_Review", result);
            }

        });


    }

    public void setAdapter() {

        adapter = new StatusReviewAdapter(getActivity(), statusReview_lists);
        recyclerView.setAdapter(adapter);

        progressDialog.dismiss();
        adapter.setOnclickListener(new StatusReviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Variables.StatusReview_List item, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_fetapprove, null);
                status_remark = (EditText) dialogView.findViewById(R.id.status_remark);
                radioGroup = (RadioGroup) dialogView.findViewById(R.id.approve_group);
                builder.setTitle("Approve");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        radioButton = (RadioButton) dialogView.findViewById(selectedId);
                        final JSONArray Schedulearry = new JSONArray();
                        JSONObject schedulereviewobj = new JSONObject();
                        JSONObject paramJson = new JSONObject();
                        JSONObject detailJson = new JSONObject();
                        if (item.getSchedulereview_gid() != 0) {
                            try {
                                schedulereviewobj.put("schedulereview_gid", item.getSchedulereview_gid());
                                schedulereviewobj.put("status", radioButton.getText());
                                schedulereviewobj.put("remarks", status_remark.getText().toString());
                                detailJson.put("action", "Update");
                            } catch (Exception e) {

                            }
                        } else {
                            try {
                                schedulereviewobj.put("schedule_gid", item.getschedule_gid());
                                schedulereviewobj.put("status", radioButton.getText());
                                schedulereviewobj.put("remarks", status_remark.getText().toString());
                                detailJson.put("action", "Insert");
                            } catch (Exception e) {

                            }
                        }
                        try {
                            Schedulearry.put(schedulereviewobj);

                            detailJson.put("data", new JSONObject().put("schedulereview", Schedulearry));
                            paramJson.put("parms", detailJson);
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                        String URL = Constant.URL + "FET_ReviewApprove_set?Emp_Gid=" + UserDetails.getUser_id();
                        CallbackHandler.sendReqest(getActivity(), Request.Method.POST, paramJson.toString(), URL, new VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {

                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    String message = jsonObject.getString("MESSAGE");

                                    if (message.equals("SUCCESS")) {
                                        statusReview_lists.clear();
                                        loadData();
                                        //setAdapter();
                                        Toast.makeText(getContext(), "Message Saved Successfully.!", Toast.LENGTH_LONG).show();

                                    } else {
                                        Toast.makeText(getContext(), "Failed.!", Toast.LENGTH_LONG).show();

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(String result) {
                                Log.e("APPR_FAIL", result);
                            }

                        });
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setView(dialogView);
                final Dialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public void TypeClick(Variables.StatusReview_List item, int position) {
                if (item.getSoheader_gid() != 0) {
                    progressDialog.show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    Context dialogContext = builder.getContext();
                    LayoutInflater inflater = LayoutInflater.from(dialogContext);
                    View alertView = inflater.inflate(R.layout.alert_bookingview, null);
                    builder.setView(alertView);
                    TableLayout tableLayout = (TableLayout) alertView.findViewById(R.id.table_layout);
                    String[] header_Booking = {"S.No", "SO Header No", "Product Name", "Quantity", "Amount", "Total"};
                    tableLayout.setStretchAllColumns(true);
                    tableLayout.setShrinkAllColumns(true);

                    TableRow tableRow;
                    tableRow = new TableRow(getActivity());
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);//(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                    tableRow.setLayoutParams(lp);

                    for (int i = 0; i < header_Booking.length; i++) {
                        TextView sNo = new TextView(getActivity());
                        sNo.setText(header_Booking[i]);
                        sNo.setTextColor(0xFFFFFFFF);
                        sNo.setTextSize(15);
                        sNo.setGravity(Gravity.CENTER);
                        sNo.setLayoutParams(lp);
                        sNo.setBackgroundResource(R.drawable.table_header);
                        tableRow.addView(sNo);
                    }

                    tableLayout.addView(tableRow, 0);
                    loadData_booking(tableLayout, builder, item.getSoheader_gid());
                    builder.setCancelable(true);
                }
            }


        });

        if (adapter.getItemCount() == 0) {
            empty_view.setText("" + getActivity().getResources().getString(R.string.no_data_available));
            setVisibility(View.GONE, View.VISIBLE, View.GONE);
        } else {
            setVisibility(View.VISIBLE, View.GONE, View.GONE);
        }
    }

    public void setVisibility(int recycleView, int emptyView, int reloadView) {
        linearLayout.setVisibility(recycleView);
        empty_view.setVisibility(emptyView);
        reload.setVisibility(reloadView);
    }

    @Override
    public void onClick(View view) {

    }


}
