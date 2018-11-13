package view.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
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

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import constant.Constant;
import models.ApprovalAdapter;
import models.Common;
import models.CustomerAdapter;
import models.StatusReviewAdapter;
import models.UserDetails;
import models.Variables;
import network.CallbackHandler;
import presenter.VolleyCallback;

import static java.lang.Integer.parseInt;

public class Approval_Fragment extends Fragment implements View.OnClickListener {
    private static final String ARG_Title = "title";
    private static final String ARG_PARAM2 = "param2";
    public ApprovalAdapter adapter;
    private String mParam1, mParam2;
    private View fragmentView;
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private TextView empty_view, reload;
    private Bundle sessiondata;
    private ProgressDialog progressDialog;
    private ArrayList<Variables.Approval_List> approval_lists;
    private EditText alert_remark;
    private RadioGroup radioGroup;
    private EditText appro_remark;
    private SearchView customerSearch;


    public static Approval_Fragment newInstance(String Title, String param2) {
        Approval_Fragment fragment = new Approval_Fragment();
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
        View view = inflater.inflate(R.layout.fragment_approval, container, false);
        fragmentView = view;
        loadView(view);
        initializeView();
        loadData();

        return view;
    }

    private void loadView(View view) {
        linearLayout = (LinearLayout) view.findViewById(R.id.linearDirect);
        recyclerView = (RecyclerView) view.findViewById(R.id.ApprovalRecyclerView);
        customerSearch = fragmentView.findViewById(R.id.customer_search);
        empty_view = view.findViewById(R.id.empty_view);
        reload = view.findViewById(R.id.custReload);
    }


    private void initializeView() {

        sessiondata = new Bundle();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        approval_lists = new ArrayList<Variables.Approval_List>();
        reload.setOnClickListener(this);
        customerSearch.setQueryHint("Search");
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
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
        progressDialog.show();
        if (!Common.isOnline(getContext())) {
            Snackbar.with(getActivity(), null)
                    .type(Type.WARNING)
                    .message("Please Check Internet Connection.")
                    .duration(Duration.SHORT)
                    .fillParent(true)
                    .textAlign(Align.LEFT)
                    .show();
            setVisibility(View.GONE, View.GONE, View.VISIBLE);
            progressDialog.cancel();
            return;
        }

        JSONObject Json = new JSONObject();
        JSONObject classif_Json = new JSONObject();
        try {
            JSONArray entity = new JSONArray();
            JSONArray client = new JSONArray();
            classif_Json.put("entity_gid", entity.put(1));
            classif_Json.put("client_gid", client);
            Json.put("Classification", classif_Json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String URL = Constant.URL + "FET_Approve?Action=PENDING&Date=&Customer_Gid=0&Emp_Gid=0&Limit=30&Entity_Gid=1";

        CallbackHandler.sendReqest(getActivity(), Request.Method.POST, Json.toString(), URL, new VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("MESSAGE");

                    if (message.equals("FOUND")) {
                        JSONObject json = jsonObject.getJSONObject("DATA");
                        JSONArray jsonArray = json.getJSONArray("SALES");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj_json = jsonArray.getJSONObject(i);
                            String customer_name = obj_json.getString("display_name");
                            String soheader = obj_json.getString("soheader_gid");
                            JSONArray jsnsalesdetail = obj_json.getJSONArray("Sale_Details");
                            JSONArray jsnoutdetail = obj_json.getJSONArray("Outstanding_Details");
                            JSONArray jsnpdcdetail = obj_json.getJSONArray("PDC_Pending");
                            String Employee_name;
                            if (obj_json.getString("soheader_date") != null) {
                                Employee_name = obj_json.getString("employee_name");
                            } else {
                                Employee_name = "";
                            }

                            approval_lists.add(new Variables.Approval_List(customer_name, Employee_name, jsnsalesdetail, jsnoutdetail, jsnpdcdetail, soheader));
                        }

                        setAdapter();

                    } else {
                        empty_view.setText(getResources().getString(R.string.error_loading));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    progressDialog.cancel();
                }


            }

            @Override
            public void onFailure(String result) {
                progressDialog.cancel();
                Log.e("DirectSchecule", result);
            }

        });


    }

    public void setAdapter() {

        adapter = new ApprovalAdapter(getActivity(), approval_lists);
        recyclerView.setAdapter(adapter);

        adapter.setOnclickListener(new ApprovalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Variables.Approval_List item, final int position) {

                final String[] action = {"Approve"};
                final String[] msg = {"Approved"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                Context dialogContext = builder.getContext();
                LayoutInflater inflater = LayoutInflater.from(dialogContext);
                View dialogView = inflater.inflate(R.layout.alert_approval, null);
                radioGroup = (RadioGroup) dialogView.findViewById(R.id.approve_group);
                appro_remark = (EditText) dialogView.findViewById(R.id.appro_remark);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        switch (i) {
                            case R.id.radioAccept:
                                action[0] = "Approve";
                                msg[0] = "Approved";
                                appro_remark.setEnabled(false);
                                break;

                            case R.id.radioCancel:
                                action[0] = "Cancel";
                                msg[0] = "Canceled";
                                appro_remark.setEnabled(false);
                                break;

                            case R.id.radioDecline:
                                action[0] = "Reject";
                                msg[0] = "Rejected";
                                appro_remark.setEnabled(true);
                                break;
                        }
                    }
                });
                builder.setTitle("Approve");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        JSONObject Json = new JSONObject();
                        JSONObject classif_Json = new JSONObject();
                        JSONObject paramjson = new JSONObject();
                        try {
                            JSONArray entity = new JSONArray();
                            JSONArray client = new JSONArray();
                            classif_Json.put("entity_gid", entity.put(1));
                            classif_Json.put("client_gid", client);
                            paramjson.put("action", action[0]);
                            paramjson.put("headergid", item.getSoheader());
                            paramjson.put("reason", appro_remark.getText().toString());
                            Json.put("Classification", classif_Json);
                            Json.put("parms", paramjson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        String URL = Constant.URL + "FET_Approve?Action=SALES_DATA&Emp_Gid=" + UserDetails.getUser_id() + "&Entity_Gid=1";
                        CallbackHandler.sendReqest(getActivity(), Request.Method.POST, Json.toString(), URL, new VolleyCallback() {

                            @Override
                            public void onSuccess(String result) {
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    String message = jsonObject.getString("MESSAGE");

                                    if (message.equals("SUCCESS")) {
                                        adapter.removeAt(position);
                                        //  approval_lists.clear();
                                        // loadData();
                                        Toast.makeText(getContext(), msg[0] + " Successfully", Toast.LENGTH_LONG).show();

                                    } else {
                                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(String result) {

                                Log.e("Approval", result);
                            }

                        });

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setView(dialogView);
                Dialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public void onViewSalesClick(Variables.Approval_List item, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());


                Context dialogContext = builder.getContext();
                LayoutInflater inflater = LayoutInflater.from(dialogContext);
                builder.setTitle("Details");

                View alertView = inflater.inflate(R.layout.alert_salesview, null);
                builder.setView(alertView);
                TextView saletotal = alertView.findViewById(R.id.txt_SaleTtl);

                TableLayout tableLayout = (TableLayout) alertView.findViewById(R.id.table_layout);
                String[] header_Booking = {"S.No", "Product Name", "Qty", "Amount"};
                tableLayout.setStretchAllColumns(true);
                tableLayout.setShrinkAllColumns(true);

                TableRow tableRow;
                tableRow = new TableRow(getActivity());
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);//(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                tableRow.setLayoutParams(lp);
                tableRow.setBackgroundColor(getActivity().getResources().getColor(R.color.colorAccent));

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

                JSONArray arraydata = (JSONArray) item.getJsonsalesdetail();
                int index = 0, total = 0;
                for (int i = 0; i < arraydata.length(); i++) {
                    JSONObject obj_json = null;
                    String soheader_no = null, product_name = null;
                    int qty = 0, amount = 0;
                    try {
                        obj_json = arraydata.getJSONObject(i);
                        product_name = obj_json.getString("product_name");
                        qty = obj_json.getInt("sodetails_qty");
                        amount = obj_json.getInt("detail_amount");
                        total = total + amount;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String detail_value[] = {Integer.toString(i + 1), product_name, String.valueOf(qty), String.valueOf(amount)};
                    tableLayout.setStretchAllColumns(true);
                    tableLayout.setShrinkAllColumns(true);
                    tableRow = new TableRow(getActivity());
                    TableRow.LayoutParams lpl = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                    tableRow.setLayoutParams(lpl);
                    tableRow.setBackgroundColor(Color.WHITE);

                    for (int j = 0; j < detail_value.length; j++) {
                        TextView dtl = new TextView(getActivity());
                        dtl.setText(detail_value[j]);
                        index = Integer.parseInt(detail_value[0]);
                        dtl.setGravity(Gravity.CENTER);
                        dtl.setBackground(getResources().getDrawable(R.drawable.table_body));
                        dtl.setLayoutParams(lpl);
                        tableRow.addView(dtl);
                    }

                    tableLayout.addView(tableRow, index);
                }
                saletotal.setText("Total : " + total);

                builder.setCancelable(true);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

            @Override
            public void onViewoutClick(Variables.Approval_List item, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                Context dialogContext = builder.getContext();
                LayoutInflater inflater = LayoutInflater.from(dialogContext);
                builder.setTitle("Details");

                View alertView = inflater.inflate(R.layout.alert_salesview, null);
                builder.setView(alertView);
                TextView saletotal = alertView.findViewById(R.id.txt_SaleTtl);
                TextView saletheader = alertView.findViewById(R.id.txt_Sales);
                saletotal.setVisibility(View.GONE);
                saletheader.setText("OUTSTANDING");
                TableLayout tableLayout = (TableLayout) alertView.findViewById(R.id.table_layout);
                String[] header_Outcom = {"S.No", "Invoice No", "pending"};
                tableLayout.setStretchAllColumns(true);
                tableLayout.setShrinkAllColumns(true);

                TableRow tableRow;
                tableRow = new TableRow(getActivity());
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);//(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                tableRow.setLayoutParams(lp);
                tableRow.setBackgroundColor(getActivity().getResources().getColor(R.color.colorAccent));

                for (int i = 0; i < header_Outcom.length; i++) {
                    TextView sNo = new TextView(getActivity());
                    sNo.setText(header_Outcom[i]);
                    sNo.setTextColor(0xFFFFFFFF);
                    sNo.setTextSize(15);
                    sNo.setGravity(Gravity.CENTER);
                    sNo.setLayoutParams(lp);
                    sNo.setBackgroundResource(R.drawable.table_header);
                    tableRow.addView(sNo);
                }

                tableLayout.addView(tableRow, 0);

                JSONArray arraydata = (JSONArray) item.getJsonoutstngdetail();
                int index = 0, total = 0;
                for (int i = 0; i < arraydata.length(); i++) {
                    JSONObject obj_json = null;
                    String soheader_no = null, outstanding = null;
                    int pending = 0;
                    try {
                        obj_json = arraydata.getJSONObject(i);
                        outstanding = obj_json.getString("soutstanding_invoiceno");
                        pending = obj_json.getInt("pending");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String detail_value[] = {Integer.toString(i + 1), outstanding, String.valueOf(pending)};
                    tableLayout.setStretchAllColumns(true);
                    tableLayout.setShrinkAllColumns(true);
                    tableRow = new TableRow(getActivity());
                    TableRow.LayoutParams lpl = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                    tableRow.setLayoutParams(lpl);
                    tableRow.setBackgroundColor(Color.WHITE);

                    for (int j = 0; j < detail_value.length; j++) {
                        TextView dtl = new TextView(getActivity());
                        dtl.setText(detail_value[j]);
                        index = Integer.parseInt(detail_value[0]);
                        dtl.setGravity(Gravity.CENTER);
                        dtl.setBackground(getResources().getDrawable(R.drawable.table_body));
                        dtl.setLayoutParams(lpl);
                        tableRow.addView(dtl);
                    }

                    tableLayout.addView(tableRow, index);
                }


                builder.setCancelable(true);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

            @Override
            public void onViewPdcClick(Variables.Approval_List item, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                Context dialogContext = builder.getContext();
                LayoutInflater inflater = LayoutInflater.from(dialogContext);
                builder.setTitle("Details");

                View alertView = inflater.inflate(R.layout.alert_salesview, null);
                builder.setView(alertView);
                TextView saletotal = alertView.findViewById(R.id.txt_SaleTtl);
                TextView saletheader = alertView.findViewById(R.id.txt_Sales);
                saletotal.setVisibility(View.GONE);
                saletheader.setText("PDC");
                TableLayout tableLayout = (TableLayout) alertView.findViewById(R.id.table_layout);
                String[] header_pdc = {"S.No", "ChqNo", "ChqAmount"};
                tableLayout.setStretchAllColumns(true);
                tableLayout.setShrinkAllColumns(true);

                TableRow tableRow;
                tableRow = new TableRow(getActivity());
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);//(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                tableRow.setLayoutParams(lp);
                tableRow.setBackgroundColor(getActivity().getResources().getColor(R.color.colorAccent));

                for (int i = 0; i < header_pdc.length; i++) {
                    TextView sNo = new TextView(getActivity());
                    sNo.setText(header_pdc[i]);
                    sNo.setTextColor(0xFFFFFFFF);
                    sNo.setTextSize(15);
                    sNo.setGravity(Gravity.CENTER);
                    sNo.setLayoutParams(lp);
                    sNo.setBackgroundResource(R.drawable.table_header);
                    tableRow.addView(sNo);
                }

                tableLayout.addView(tableRow, 0);

                JSONArray arraydata = (JSONArray) item.getJsonpdcsdetail();
                int index = 0, total = 0;
                for (int i = 0; i < arraydata.length(); i++) {
                    JSONObject obj_json = null;
                    String chequeno = null;
                    int chqamount = 0;
                    try {
                        obj_json = arraydata.getJSONObject(i);
                        chequeno = obj_json.getString("pdcdetail_chequeno");
                        chqamount = obj_json.getInt("pdc_chqamount");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String detail_value[] = {Integer.toString(i + 1), chequeno, String.valueOf(chqamount)};
                    tableLayout.setStretchAllColumns(true);
                    tableLayout.setShrinkAllColumns(true);
                    tableRow = new TableRow(getActivity());
                    TableRow.LayoutParams lpl = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                    tableRow.setLayoutParams(lpl);
                    tableRow.setBackgroundColor(Color.WHITE);

                    for (int j = 0; j < detail_value.length; j++) {
                        TextView dtl = new TextView(getActivity());
                        dtl.setText(detail_value[j]);
                        index = Integer.parseInt(detail_value[0]);
                        dtl.setGravity(Gravity.CENTER);
                        dtl.setBackground(getResources().getDrawable(R.drawable.table_body));
                        dtl.setLayoutParams(lpl);
                        tableRow.addView(dtl);
                    }

                    tableLayout.addView(tableRow, index);
                }


                builder.setCancelable(true);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        });

        if (adapter.getItemCount() == 0) {
            empty_view.setText("" + getActivity().getResources().getString(R.string.no_data_available));
            setVisibility(View.GONE, View.VISIBLE, View.GONE);
        } else {
            setVisibility(View.VISIBLE, View.GONE, View.GONE);
        }
    }

    public void filter(String text) {
        List<Variables.Approval_List> temp = new ArrayList();
        if (approval_lists.size() > 0) {
            for (Variables.Approval_List d : approval_lists) {

                if (d.getCustomername().toLowerCase().replaceAll("\\s+", "").contains(text.toLowerCase().replaceAll("\\s+", ""))) {
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

    }


}
