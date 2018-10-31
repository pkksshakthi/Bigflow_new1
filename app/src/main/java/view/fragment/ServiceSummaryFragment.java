package view.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import constant.Constant;
import models.Common;
import models.CustomerAdapter;
import models.ServiceSummryAdapter;
import models.UserDetails;
import models.Variables;
import network.CallbackHandler;
import presenter.VolleyCallback;

import static java.lang.Integer.parseInt;

public class ServiceSummaryFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_Title = "title";
    private static final String ARG_PARAM2 = "param2";
    public ServiceSummryAdapter adapter;
    private String mParam1, mParam2;
    private View fragmentView;
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private TextView empty_view, reload;
    private Bundle sessiondata;
    private ProgressDialog progressDialog;
    private FloatingActionButton Courier;
    private ArrayList<Variables.Courier> courierList = new ArrayList<>();
    private ArrayList<Variables.ServiceSummary_List> customerList;
    private Spinner spnCourier, spnmode, spnsendto;
    private EditText txtdate, txtpacket, edtweight, AWB_no;
    private int mYear, mMonth, mDay;
    private String[] mode_items = new String[]{"AIR", "ROAD", "SHIP"};
    private String[] send_items = new String[]{"SEND TO CENTRAL OFFICE", "SEND TO BRANCH OFFICE"};
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
            // String Date_db = day + "/" + month_to_db + "/" + year1;
            String Date_db = year1 + "-" + month_to_db + "-" + day;

            txtdate.setText(Date);
            Variables.courierset.Date = Date_db;

        }
    };

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
        getActivity().setTitle("Bigflow");
        View view = inflater.inflate(R.layout.fragment_servicesummary, container, false);
        fragmentView = view;
        loadView(view);
        initializeView();
        loadData();
        Couriername();
        return view;
    }

    private void loadView(View view) {
        linearLayout = (LinearLayout) view.findViewById(R.id.linearDirect);
        recyclerView = (RecyclerView) view.findViewById(R.id.ServiceRecyclerView);
        empty_view = view.findViewById(R.id.empty_view);
        reload = view.findViewById(R.id.custReload);
        Courier = view.findViewById(R.id.Couriericon);

    }

    private void forceopen() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.alert_courier, null);
        AWB_no = alertLayout.findViewById(R.id.et_AWB);
        txtpacket = (EditText) alertLayout.findViewById(R.id.et_packets);
        spnCourier = (Spinner) alertLayout.findViewById(R.id.spn_courier);
        spnmode = (Spinner) alertLayout.findViewById(R.id.mode);
        spnsendto = (Spinner) alertLayout.findViewById(R.id.sendto);
        txtdate = (EditText) alertLayout.findViewById(R.id.et_date);
        edtweight = (EditText) alertLayout.findViewById(R.id.et_weight);


        ArrayAdapter<Variables.Courier> courierArrayAdapter = new ArrayAdapter<Variables.Courier>(getActivity(), android.R.layout.simple_spinner_dropdown_item, courierList);
        spnCourier.setAdapter(courierArrayAdapter);

        ArrayAdapter<String> modeArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mode_items);
        spnmode.setAdapter(modeArrayAdapter);

        ArrayAdapter<String> sendtoArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, send_items);
        spnsendto.setAdapter(sendtoArrayAdapter);

        spnCourier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Variables.Courier swt = (Variables.Courier) parent.getItemAtPosition(position);
                Variables.courierset.courier_id = swt.getId();
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnmode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Variables.courierset.Mode = parent.getItemAtPosition(position).toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnsendto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Variables.courierset.Send_to = parent.getItemAtPosition(position).toString();

            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), datePickerListener, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                datePickerDialog.setCancelable(false);
                datePickerDialog.setTitle("Choose Remark date");
                datePickerDialog.show();
            }
        });

        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Courier Details");
        alert.setCancelable(false);
        alert.setView(alertLayout);

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Variables.courierset.AWB_no = AWB_no.getText().toString();
                Variables.courierset.Packet = txtpacket.getText().toString();
                Variables.courierset.Weight = edtweight.getText().toString();
                Variables.courierset courierset = new Variables.courierset();
                final JSONArray Dispatcharray = new JSONArray();
                JSONObject dispatchobj = new JSONObject();
                if (!courierset.anyUnset()) {
                    try {
                        dispatchobj.put("courier_gid", courierset.getCourierid());
                        dispatchobj.put("Dispatch_date", courierset.getDate());
                        dispatchobj.put("send_by", Integer.parseInt(UserDetails.getUser_id()));
                        dispatchobj.put("awbno", courierset.getAWB_no());
                        dispatchobj.put("dispatch_mode", courierset.getMode());
                        dispatchobj.put("dispatch_type", "Ndoc");
                        dispatchobj.put("packets", courierset.getPacket());
                        dispatchobj.put("weight", courierset.getWeight());
                        dispatchobj.put("dispatch_to", courierset.getSend_to());
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
                        dispatchobj.put("in_out", courierset.getinout_flag());
                        dispatchobj.put("status", courierset.getSend_to());
                        Dispatcharray.put(dispatchobj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {

                    Toast.makeText(getContext(), "Fill All The Field", Toast.LENGTH_LONG).show();
                }
                final JSONArray SRarray = new JSONArray();
                for (Variables.ServiceSummary_List productdtl : customerList) {
                    JSONObject objDetails = new JSONObject();

                    if (productdtl.isSelected()) {
                        try {
                            objDetails.put("product_gid", productdtl.getProductgid());
                            objDetails.put("product_slno", productdtl.getProductSlno());
                            objDetails.put("invoice_no", "");
                            objDetails.put("remark", productdtl.getRemark());
                            objDetails.put("service_gid", productdtl.getservicegid());
                            objDetails.put("dispatch_mode", "EXECUTIVE");
                            objDetails.put("pay_by", productdtl.getService_courierexp());
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
                    String a = "";
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (params_Json.length() > 0) {
                    String OutMessage = DispatchSet(params_Json);
                }
            }
        });


        final AlertDialog dialog = alert.create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        AWB_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (AWB_no.getText().toString().length() > 0 && txtpacket.getText().toString().length() >0
                        && edtweight.getText().toString().length() >0 &&
                        txtdate.getText().toString().length()>0) {
                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                } else {
                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        txtpacket.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (AWB_no.getText().toString().length() > 0 && txtpacket.getText().toString().length() >0
                        && edtweight.getText().toString().length() >0 &&
                        txtdate.getText().toString().length()>0) {
                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                } else {
                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtweight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (AWB_no.getText().toString().length() > 0 && txtpacket.getText().toString().length() >0
                        && edtweight.getText().toString().length() >0 &&
                        txtdate.getText().toString().length()>0) {
                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                } else {
                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        txtdate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (AWB_no.getText().toString().length() > 0 && txtpacket.getText().toString().length() >0
                        && edtweight.getText().toString().length() >0 &&
                        txtdate.getText().toString().length()>0) {
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

    private void initializeView() {

        sessiondata = new Bundle();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        customerList = new ArrayList<Variables.ServiceSummary_List>();
        reload.setOnClickListener(this);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        Courier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = 0;
                for (Variables.ServiceSummary_List productdtl : customerList) {
                    if (productdtl.isSelected()) {
                        count = 1;
                        break;
                    }
                }
                if (count == 1) {
                    forceopen();

                } else {
                    Toast.makeText(getActivity(), "Select Atleat One Product", Toast.LENGTH_LONG).show();

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
        JSONObject Json = new JSONObject();
        JSONObject params_Json = new JSONObject();
        try {
            params_Json.put("from_date", "");
            params_Json.put("to_date", "");
            params_Json.put("customer_gid", 0);
            params_Json.put("product_gid", 0);
            params_Json.put("service_gid", 0);
            params_Json.put("status", "INITIATED");
            Json.put("params", params_Json);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String URL = Constant.URL + "Service_SummaryGetAPI?Emp_gid=" + UserDetails.getUser_id();

        URL += "&Entity_gid=" + UserDetails.getEntity_gid();

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
                            String display_name = obj_json.getString("customer_name");
                            String product_name = obj_json.getString("product_name");
                            String Date = obj_json.getString("service_date");
                            int service_gid = obj_json.getInt("service_gid");
                            int product_gid = obj_json.getInt("service_product_gid");
                            String productslno, remark;
                            if (obj_json.has("service_productslno")) {
                                productslno = obj_json.getString("service_productslno");
                            } else {
                                productslno = "";
                            }
                            if (obj_json.has("remark")) {
                                remark = obj_json.getString("remark");
                            } else {
                                remark = "";
                            }
                            String service_courierexp = obj_json.getString("service_courierexp");

                            customerList.add(new Variables.ServiceSummary_List(display_name, product_name,
                                    Date, service_gid, product_gid, productslno, remark, service_courierexp));
                        }

                        setAdapter();
                    } else {
                        progressDialog.dismiss();
                        empty_view.setText(getResources().getString(R.string.error_loading));
                    }

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(String result) {

                Log.e("DirectSchecule", result);
            }

        });


    }

    public void setAdapter() {

        adapter = new ServiceSummryAdapter(getActivity(), customerList);
        recyclerView.setAdapter(adapter);

        progressDialog.dismiss();
        adapter.setOnclickListener(new ServiceSummryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Variables.ServiceSummary_List item, int position) {

            }

        });

        if (adapter.getItemCount() == 0) {
            empty_view.setText("" + getActivity().getResources().getString(R.string.loading));
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

    public String DispatchSet(JSONObject jsonObject) {
        progressDialog.show();
        String URL = Constant.URL + "Dispatch_set_API?Emp_gid=" + UserDetails.getUser_id() + "&Entity_gid=1";

        CallbackHandler.sendReqest(getActivity(), Request.Method.POST, jsonObject.toString(), URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    String message = json.getString("MESSAGE");
                    if (message.equals("SUCCESS")) {
                        Toast.makeText(getContext(), "Dispatched Successfully", Toast.LENGTH_LONG).show();
                        customerList.clear();
                        loadData();

                    }
                }catch (Exception e){}
                Log.e("Service", result);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(String result) {
                Log.e("Service", result);
                progressDialog.dismiss();
            }
        });

        return "";
    }

    public String Couriername() {
        progressDialog.show();
        String URL = Constant.URL + "CourierName_get?Emp_gid=" + UserDetails.getUser_id() + "&Entity_gid=1";
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
                            String courier_name = obj_json.getString("courier_name");
                            int courier_gid = obj_json.getInt("courier_gid");
                            courierList.add(new Variables.Courier(courier_gid, courier_name));
                            Log.e("Courier", result);

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

        return "";
    }
}
