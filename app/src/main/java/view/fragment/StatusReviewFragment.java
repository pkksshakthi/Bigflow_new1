package view.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
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
import java.util.Date;
import java.util.List;

import DataBase.GetData;
import constant.Constant;
import models.Common;
import models.ListAdapter;
import models.StatusReviewAdapter;
import models.UserDetails;
import models.Variables;
import network.CallbackHandler;
import presenter.NetworkResult;
import presenter.VolleyCallback;
import view.activity.FilterStatusReview;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


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
    private RadioButton radioButton;
    private RadioGroup radioGroup;
    private ProgressDialog progressDialog;
    private int filterCode = 101;
    private List<Variables.StatusReview> statusReview_lists, tempstatusReview;
    private EditText alert_remark;
    private SearchView customerSearch;
    private GetData getdata;
    private List<Variables.SalesDetail> salesDetailList;
    private TextView txtEmployee;
    private TextView txtFilter;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private Bundle bundle;
    private List<Variables.Employee> employeeList;
    private ArrayList<Variables.Details> employeeDetailList;


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
        customerSearch = fragmentView.findViewById(R.id.customer_search);
        txtEmployee = fragmentView.findViewById(R.id.txtEmployee);
        txtFilter = fragmentView.findViewById(R.id.txtStatusReview_Filter);
        empty_view = view.findViewById(R.id.empty_view);
        reload = view.findViewById(R.id.custReload);
    }


    private void initializeView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        statusReview_lists = new ArrayList<>();
        tempstatusReview = new ArrayList<>();
        reload.setOnClickListener(this);
        txtEmployee.setOnClickListener(this);
        txtFilter.setOnClickListener(this);
        bundle = new Bundle();
        String current_date = Common.convertDateString(new Date(), Constant.date_display_format);
        bundle.putString(Constant.key_fdate, current_date);
        bundle.putString(Constant.key_tdate, current_date);
        getdata = new GetData(getActivity());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        customerSearch.setQueryHint("Search");
        customerSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

    }

    private void loadData() {
        if (!Common.isOnline(getContext())) {
            Snackbar.with(getActivity(), null)
                    .type(Type.WARNING)
                    .message(getActivity().getResources().getString(R.string.check_internet_connection))
                    .duration(Duration.SHORT)
                    .fillParent(true)
                    .textAlign(Align.LEFT)
                    .show();
            setVisibility(View.GONE, View.GONE, View.VISIBLE);
            return;
        }
        getLoadData(getFilteredList());
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
            }

            @Override
            public void handlerError(String result) {

            }
        });

    }

    private Variables.paramsStatusReview getFilteredList() {
        Variables.paramsStatusReview params = new Variables.paramsStatusReview();
        String current_date = Common.convertDateString(new Date(), Constant.date_display_format);
        params.employee_gid = bundle.getInt(Constant.key_employee_gid, 0);
        params.customer_gid = bundle.getInt(Constant.key_customer_gid, 0);
        params.scheduletype_gid = bundle.getInt(Constant.key_sch_type_gid, 0);
        params.custgroup_gid = bundle.getInt(Constant.key_cust_group_gid, 0);
        params.location_gid = bundle.getInt(Constant.key_loaction_gid, 0);
        params.sch_review_status = bundle.getString(Constant.key_sch_review_status, Constant.status_review_pending);
        params.from_date = bundle.getString(Constant.key_fdate, current_date);
        params.todate = bundle.getString(Constant.key_tdate, current_date);
        return params;
    }

    private void getLoadData(final Variables.paramsStatusReview params) {
        progressDialog.show();
        statusReview_lists = getdata.getStatusReview(params, new NetworkResult() {
            @Override
            public void handlerResult(String result) {
                tempstatusReview = new ArrayList<>();
                for (int i = 0; i < statusReview_lists.size(); i++) {
                    if (statusReview_lists.get(i).review_status.equals(params.sch_review_status)) {
                        tempstatusReview.add(statusReview_lists.get(i));
                    }
                }
                setAdapter();
                progressDialog.cancel();
            }

            @Override
            public void handlerError(String result) {
                progressDialog.cancel();
            }
        });
    }

    public void setAdapter() {

        adapter = new StatusReviewAdapter(getActivity(), tempstatusReview);
        recyclerView.setAdapter(adapter);

        progressDialog.dismiss();
        adapter.setOnclickListener(new StatusReviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Variables.StatusReview item, int position) {
                if (item.isAdmin) {
                    if (item.employee_gid != UserDetails.getUser_id()) {
                        createApproveDialog(item);
                    } else {
                        Toast.makeText(getContext(), "Can't approve your's details", Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void TypeClick(Variables.StatusReview item, int position) {
                if (item.soheader_gid != 0) {
                    progressDialog.show();
                    salesDetailList = getdata.getSalesDetails(item.soheader_gid, new NetworkResult() {
                        @Override
                        public void handlerResult(String result) {
                            if (salesDetailList.size() > 0) {
                                createSalesDialog();
                            } else {
                                Toast.makeText(getContext(), getActivity().getResources().getString(R.string.no_data_available), Toast.LENGTH_LONG).show();
                            }
                            progressDialog.cancel();

                        }

                        @Override
                        public void handlerError(String result) {

                        }
                    });

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

    private void createApproveDialog(final Variables.StatusReview item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_fetapprove, null);
        status_remark = (EditText) dialogView.findViewById(R.id.status_remark);
        radioGroup = (RadioGroup) dialogView.findViewById(R.id.approve_group);
        builder.setTitle("Approve");
        if (item.schedulereview_gid != 0) {
            status_remark.setText(item.review_remarks);
            status_remark.setSelection(item.review_remarks.length());
            if (item.review_status.equals("APPROVE")) {
                radioGroup.check(R.id.radioApprove);
            } else {
                radioGroup.check(R.id.radioReject);
            }

        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) dialogView.findViewById(selectedId);
                final JSONArray Schedulearry = new JSONArray();
                JSONObject schedulereviewobj = new JSONObject();
                JSONObject paramJson = new JSONObject();
                JSONObject detailJson = new JSONObject();
                if (item.schedulereview_gid != 0) {
                    try {
                        schedulereviewobj.put("schedulereview_gid", item.schedulereview_gid);
                        schedulereviewobj.put("status", radioButton.getText());
                        schedulereviewobj.put("remarks", status_remark.getText().toString());
                        detailJson.put("action", "Update");
                    } catch (Exception e) {

                    }
                } else {
                    try {
                        schedulereviewobj.put("schedule_gid", item.schedule_gid);
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

    private void createSalesDialog() {
        builder = new AlertDialog.Builder(getContext());
        Context dialogContext = builder.getContext();
        LayoutInflater inflater = LayoutInflater.from(dialogContext);
        View alertView = inflater.inflate(R.layout.alert_salesview, null);
        builder.setView(alertView);
        builder.setCancelable(true);

        TextView title = alertView.findViewById(R.id.txt_Sales);
        title.setText("Booking");
        TableLayout tableLayout = (TableLayout) alertView.findViewById(R.id.table_layout);
        tableLayout.setStretchAllColumns(true);
        String[] header_Booking = {"S.No", "SO No", "Product", "Quantity", "Amount", "Total"};


        TableRow tableRow;
        tableRow = new TableRow(getActivity());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);//(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        tableRow.setLayoutParams(lp);

        for (int i = 0; i < header_Booking.length; i++) {
            TextView textView = new TextView(getActivity());
            textView.setText(header_Booking[i]);
            textView.setTextColor(0xFFFFFFFF);
            textView.setTextSize(15);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(lp);
            textView.setBackgroundResource(R.drawable.table_header);
            tableRow.addView(textView);
        }

        tableLayout.addView(tableRow, 0);
        for (int i = 0; i < salesDetailList.size(); i++) {
            Variables.SalesDetail salesDetail = salesDetailList.get(i);
            String detail_value[] = {Integer.toString(i + 1), salesDetail.soheader_no, salesDetail.product_name
                    , String.valueOf(salesDetail.product_quantity), String.valueOf(salesDetail.product_price), String.valueOf(salesDetail.total_price)};

            tableRow = new TableRow(getActivity());
            tableRow.setLayoutParams(lp);

            for (int j = 0; j < detail_value.length; j++) {
                TextView dtl = new TextView(getActivity());
                dtl.setText(detail_value[j]);
                dtl.setGravity(Gravity.CENTER);
                dtl.setBackground(getResources().getDrawable(R.drawable.table_body));
                dtl.setLayoutParams(lp);
                tableRow.addView(dtl);
            }

            tableLayout.addView(tableRow, i + 1);
        }
        alertDialog = builder.create();
        alertDialog.show();

    }

    public void filter(String text) {
        List<Variables.StatusReview> temp = new ArrayList();
        if (tempstatusReview.size() > 0) {
            for (Variables.StatusReview d : tempstatusReview) {

                if (d.customer_name.toLowerCase().replaceAll("\\s+", "").contains(text.toLowerCase().replaceAll("\\s+", ""))) {
                    temp.add(d);
                }
            }
            adapter.updateList(temp);
        }


    }

    public void setVisibility(int recycleView, int emptyView, int reloadView) {
        linearLayout.setVisibility(recycleView);
        empty_view.setVisibility(emptyView);
        reload.setVisibility(reloadView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtStatusReview_Filter:
                Intent intent = new Intent(getActivity(), FilterStatusReview.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, filterCode);
                break;
            case R.id.txtEmployee:
                createEmployeeDialog();
                break;

        }
    }

    private void createEmployeeDialog() {
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Employee");
        Context dialogContext = builder.getContext();
        LayoutInflater inflater = LayoutInflater.from(dialogContext);
        View alertView = inflater.inflate(R.layout.alert_dialog_list, null);
        final ListAdapter listAdapter = new ListAdapter(getActivity(), employeeDetailList);
        ListView listView = (ListView) alertView.findViewById(R.id.listView_dialog);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                txtEmployee.setText(listAdapter.getItem(position).data);
                bundle.putInt(Constant.key_employee_gid, listAdapter.getItem(position).gid);
                getLoadData(getFilteredList());
                alertDialog.cancel();
            }
        });
        builder.setView(alertView);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == filterCode) {

            if (resultCode == RESULT_OK) {
                int temp;
                bundle = new Bundle();
                bundle = data.getExtras();
                temp = bundle.getInt(Constant.key_employee_gid);
                txtEmployee.setText(bundle.getString(Constant.key_employee_name));
                getLoadData(getFilteredList());

            } else if (resultCode == RESULT_CANCELED) {

                // tvResultCode.setText("RESULT_CANCELED");

            }

        }
    }
}
