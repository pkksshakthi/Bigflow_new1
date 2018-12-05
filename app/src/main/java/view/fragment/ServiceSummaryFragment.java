package view.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import DataBase.GetData;
import constant.Constant;
import models.Common;
import models.CustomDialogListener;
import models.CustomSpinnerAdapter;
import models.ServiceSummryAdapter;
import models.UserDetails;
import models.Variables;
import network.CallbackHandler;
import presenter.NetworkResult;
import presenter.VolleyCallback;
import view.activity.DashBoardActivity;

import static java.lang.Integer.parseInt;

public class ServiceSummaryFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_Title = "title";
    private static final String ARG_PARAM2 = "param2";
    public ServiceSummryAdapter adapter;
    private String mParam1, mParam2;
    private View fragmentView;
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private TextView empty_view, reload, txtCourierDate;
    private Bundle sessiondata;
    private ProgressDialog progressDialog;
    private ArrayList<Variables.Details> courierList;
    private List<Variables.ServiceSummary> serviceList;
    private Spinner spnCourier, spnmode, spnsendto;
    private EditText txtpacket, edtweight, AWB_no;
    private String[] mode_items;
    private String[] send_items;
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            txtCourierDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);

        }
    };
    private SearchView customerSearch;
    private GetData getdata;

    public static ServiceSummaryFragment newInstance(String Title, String param2) {
        ServiceSummaryFragment fragment = new ServiceSummaryFragment();
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
        getActivity().setTitle(Constant.title_service_summary);
        View view = inflater.inflate(R.layout.fragment_servicesummary, container, false);
        fragmentView = view;
        loadView(view);
        initializeView();
        loadData();
        Couriername();
        return view;
    }

    private void loadView(View view) {
        linearLayout = (LinearLayout) view.findViewById(R.id.linearService);
        recyclerView = (RecyclerView) view.findViewById(R.id.ServiceRecyclerView);
        customerSearch = view.findViewById(R.id.customer_search);
        empty_view = view.findViewById(R.id.txtEmptyView);
        reload = view.findViewById(R.id.txtSSReload);
    }


    private void initializeView() {
        mode_items = new String[]{"Air", "Road", "Ship"};
        send_items = new String[]{"Central office", "Branch office"};
        serviceList = new ArrayList<>();
        sessiondata = new Bundle();
        getdata = new GetData(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        customerSearch.setQueryHint("Search");

        reload.setOnClickListener(this);

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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }

        DashBoardActivity mainActivity = (DashBoardActivity) getActivity();
        mainActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (adapter.getSelectedList().size() > 0) {
                    forceopen();

                } else {
                    Toast.makeText(getActivity(), "Select atleast one record", Toast.LENGTH_LONG).show();

                }
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

        serviceList = getdata.serviceSummaryList(1, new NetworkResult() {
            @Override
            public void handlerResult(String result) {
                setAdapter();
            }

            @Override
            public void handlerError(String result) {
                progressDialog.dismiss();
            }
        });
    }

    public void setAdapter() {

        adapter = new ServiceSummryAdapter(getActivity(), serviceList);
        recyclerView.setAdapter(adapter);
        adapter.setOnclickListener(new ServiceSummryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Variables.ServiceSummary item, int position) {

            }

        });

        if (adapter.getItemCount() == 0) {
            empty_view.setText("" + getActivity().getResources().getString(R.string.no_data_available));
            setVisibility(View.GONE, View.VISIBLE, View.GONE);
        } else {
            setVisibility(View.VISIBLE, View.GONE, View.GONE);
        }
        progressDialog.dismiss();
    }

    private void forceopen() {
        final LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.alert_courier, null);
        AWB_no = alertLayout.findViewById(R.id.etxAWBillNo);
        txtpacket = (EditText) alertLayout.findViewById(R.id.et_packets);
        spnCourier = (Spinner) alertLayout.findViewById(R.id.spn_courier);
        spnmode = (Spinner) alertLayout.findViewById(R.id.mode);
        spnsendto = (Spinner) alertLayout.findViewById(R.id.sendto);
        txtCourierDate = (TextView) alertLayout.findViewById(R.id.et_date);
        txtCourierDate.setText(Common.convertDateString(new Date(), Constant.date_display_format));
        edtweight = (EditText) alertLayout.findViewById(R.id.et_weight);


        CustomSpinnerAdapter courierArrayAdapter = new CustomSpinnerAdapter(getActivity(), R.layout.spinner_item, courierList);
        spnCourier.setAdapter(courierArrayAdapter);

        ArrayAdapter<String> modeArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, mode_items);
        modeArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spnmode.setAdapter(modeArrayAdapter);

        ArrayAdapter<String> sendtoArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, send_items);
        sendtoArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spnsendto.setAdapter(sendtoArrayAdapter);


        txtCourierDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final Calendar currentDate = Calendar.getInstance();
                Variables.calendarDate c = new Variables.calendarDate(txtCourierDate.getText().toString());
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), datePickerListener, c.getYear(), c.getMonth(), c.getDayofmonth());
                datePickerDialog.getDatePicker().setMaxDate(currentDate.getTimeInMillis());
                datePickerDialog.setTitle("Choose Remark date");
                datePickerDialog.show();
            }
        });

        AlertDialog.Builder CourierBuilder = new AlertDialog.Builder(getActivity());
        CourierBuilder.setTitle("Courier Details");
        CourierBuilder.setView(alertLayout);


        CourierBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        CourierBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog courierDialog = CourierBuilder.create();
        courierDialog.show();
        CustomDialogListener a = new CustomDialogListener(courierDialog);
        a.setClickListener(new presenter.CustomDialogListener() {
            @Override
            public void OnClickPositive(View view, AlertDialog dialog) {
                int courier_gid = ((Variables.Details) spnCourier.getSelectedItem()).gid;
                String courier_mode = spnmode.getSelectedItem().toString();
                String courier_sentto = spnsendto.getSelectedItem().toString().equals(send_items[0]) ? "SEND TO CENTRAL OFFICE" : "SEND TO BRANCH OFFICE";
                String courier_date = Common.convertDateString(txtCourierDate.getText().toString(), Constant.date_display_format, "yyyy-MM-dd");
                String inOutFlag;
                if (AWB_no.getText().toString().trim().length() == 0) {
                    AWB_no.setError("*");
                    return;
                } else if (txtpacket.getText().toString().trim().length() == 0) {
                    txtpacket.setError("*");
                    return;
                } else if (edtweight.getText().toString().trim().length() == 0) {
                    edtweight.setError("*");
                    return;
                }
                if (courier_mode.equals(mode_items[0])) {
                    inOutFlag = "SERVICE_TOCENTRAL";
                } else {
                    inOutFlag = "SERVICE_TOBRANCH";
                }
                final JSONArray Dispatcharray = new JSONArray();
                JSONObject dispatchobj = new JSONObject();

                try {
                    dispatchobj.put("courier_gid", courier_gid);
                    dispatchobj.put("Dispatch_date", courier_date);
                    dispatchobj.put("send_by", UserDetails.getUser_id());
                    dispatchobj.put("awbno", AWB_no.getText().toString());
                    dispatchobj.put("dispatch_mode", courier_mode);
                    dispatchobj.put("dispatch_type", "Ndoc");
                    dispatchobj.put("packets", txtpacket.getText().toString());
                    dispatchobj.put("weight", edtweight.getText().toString());
                    dispatchobj.put("dispatch_to", courier_sentto);
                    dispatchobj.put("address", "");
                    dispatchobj.put("city", "");
                    dispatchobj.put("state", "");
                    dispatchobj.put("pincode", "");
                    dispatchobj.put("remark", "aaaaa");
                    dispatchobj.put("returned", "N");
                    dispatchobj.put("returned_on", "");
                    dispatchobj.put("returned_remark", "");
                    dispatchobj.put("pod", "");
                    dispatchobj.put("pod_image", "");
                    dispatchobj.put("isactive", "Y");
                    dispatchobj.put("isremoved", "N");
                    dispatchobj.put("dispatch_gid", 0);
                    dispatchobj.put("action", "Insert");
                    dispatchobj.put("type", "SERVICE");
                    dispatchobj.put("in_out", inOutFlag);
                    dispatchobj.put("status", courier_sentto);
                    Dispatcharray.put(dispatchobj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final JSONArray SRarray = new JSONArray();
                for (Variables.ServiceSummary productdtl : adapter.getSelectedList()) {
                    JSONObject objDetails = new JSONObject();

                    if (productdtl.is_selected) {
                        try {
                            objDetails.put("product_gid", productdtl.product_gid);
                            objDetails.put("product_slno", productdtl.product_Slno);
                            objDetails.put("invoice_no", "");
                            objDetails.put("remark", productdtl.service_remark);
                            objDetails.put("service_gid", productdtl.service_gid);
                            objDetails.put("dispatch_mode", "EXECUTIVE");
                            objDetails.put("pay_by", productdtl.service_courierexp);
                            objDetails.put("dispatch_gid", 0);
                            objDetails.put("central_off_dispatch_gid", 0);
                            SRarray.put(objDetails);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                JSONObject params_Json = new JSONObject();
                try {
                    params_Json.put("dispatch_data", Dispatcharray);
                    params_Json.put("service_dtl", new JSONObject().put("SERVICE", SRarray));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                DispatchSet(params_Json.toString(), dialog);

            }
        });
    }

    public void setVisibility(int recycleView, int emptyView, int reloadView) {
        linearLayout.setVisibility(recycleView);
        empty_view.setVisibility(emptyView);
        reload.setVisibility(reloadView);
    }

    @Override
    public void onClick(View view) {

    }

    public void DispatchSet(String jsonObject, final AlertDialog dialog) {
        progressDialog.show();
        String URL = Constant.URL + "Dispatch_set_API?Emp_gid=" + UserDetails.getUser_id() + "&Entity_gid=1";

        CallbackHandler.sendReqest(getActivity(), Request.Method.POST, jsonObject, URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    String message = json.getString("MESSAGE");
                    if (message.equals("SUCCESS")) {
                        Toast.makeText(getContext(), "Dispatched Successfully", Toast.LENGTH_LONG).show();
                        List<Variables.ServiceSummary> temp = adapter.getSelectedList();
                        for (int i = 0; i < temp.size(); i++) {
                            if (temp.get(i).is_selected)
                                serviceList.remove(temp.get(i));
                        }
                        adapter.clearSelectedList();
                        adapter.updateList(serviceList);
                        dialog.dismiss();
//loaddata
                    }
                } catch (Exception e) {
                }
                Log.e("Service", result);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(String result) {
                Log.e("Service", result);
                progressDialog.dismiss();
            }
        });
    }

    public void Couriername() {
        progressDialog.show();
        courierList = new ArrayList<>();
        String URL = Constant.URL + "CourierName_get?Emp_gid=" + UserDetails.getUser_id() + "&Entity_gid=" + UserDetails.getEntity_gid();
        JSONObject Json = new JSONObject();
        JSONObject params_Json = new JSONObject();
        try {
            Json.put("courier_gid", 0);
            Json.put("courier_name", "");
            params_Json.put("params", Json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CallbackHandler.sendReqest(getActivity(), Request.Method.POST, params_Json.toString(), URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("MESSAGE");
                    if (message.equals("FOUND")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("DATA");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj_json = jsonArray.getJSONObject(i);
                            Variables.Details details = new Variables.Details();
                            details.data = obj_json.getString("courier_name");
                            details.gid = obj_json.getInt("courier_gid");
                            courierList.add(details);
                        }
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String result) {
                Log.e("Courier-fail", result);
                progressDialog.dismiss();
            }
        });
    }

    public void filter(String text) {
        List<Variables.ServiceSummary> temp = new ArrayList();
        if (serviceList.size() > 0) {
            for (Variables.ServiceSummary d : serviceList) {

                if (d.customer_name.toLowerCase().replaceAll("\\s+", "").contains(text.toLowerCase().replaceAll("\\s+", ""))) {
                    temp.add(d);
                }
            }
            adapter.updateList(temp);
        }


    }

    @Override
    public void onDestroyView() {
        DashBoardActivity mainActivity = (DashBoardActivity) getActivity();
        mainActivity.fab.hide();
        super.onDestroyView();
    }
/*private DatePickerDialog.OnDateSetListener setDatepicker() {
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
    }*/
}
