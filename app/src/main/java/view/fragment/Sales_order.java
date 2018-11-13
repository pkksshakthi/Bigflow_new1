package view.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.vsolv.bigflow.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import constant.Constant;
import models.AutoProductAdapter;
import models.Common;
import models.UserDetails;
import models.Variables;
import network.CallbackHandler;
import presenter.VolleyCallback;

public class Sales_order extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public Bundle customer_details;
    public Button btnsubmit_order, btnUpdate, btnCancel;
    public RelativeLayout relativeLayoutupdate;
    public ArrayList<EditText> val;
    int cust_gid, soheaderno = 0, Sale_Schedule_gid = 0;
    String EditStatus;
    ArrayList<Variables.Update_sales> updateSales = new ArrayList<Variables.Update_sales>();
    int j_sale = 1;
    List<Integer> DeleteList = new ArrayList<Integer>();
    private String mParam1;
    private String mParam2;
    private ProspectFragment.OnFragmentInteractionListener mListener;
    private TableRow tableRow;
    private AutoCompleteTextView auto_product;
    private int i = 100;
    private TableLayout tableLayout;
    private AutoProductAdapter autoProductAdapter;
    private ArrayList<Integer> favProduct;
    private ProgressDialog progressDialog;
    private View.OnClickListener delete_clickListener = new View.OnClickListener() {
        public void onClick(View v) {

            if (updateSales.size() != 1) {
                final TableRow row = (TableRow) v.getParent();
                final int rowIndex = tableLayout.indexOfChild(row);
                Variables.Update_sales updateSalesobj = updateSales.get(rowIndex - 1);
                DeleteList.add(updateSalesobj.sodetails_gid);
                updateSales.remove(rowIndex - 1);
                favProduct.remove(rowIndex - 1);
                tableLayout.removeView(row);
                int total = tableLayout.getChildCount();
                for (int j = 1; j <= total; j++) {
                    View view = tableLayout.getChildAt(j);
                    if (view instanceof TableRow) {
                        TableRow row1 = (TableRow) view;
                        TextView t = (TextView) row1.getChildAt(0);
                        t.setText("" + (j));
                    }
                }
                j_sale--;

            } else {
                Toast.makeText(getActivity(), "You Cannot Delete This line.", Toast.LENGTH_LONG).show();

            }
        }
    };
    private AdapterView.OnItemClickListener autoItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Variables.Product product = autoProductAdapter.getSelectedItem(position);
            Generate_Layout(product.product_name, Common.convertDateString(new Date(), "dd-MMM-yyyy"), "0", product.product_id, j_sale, 0);
            auto_product.setText("");
            j_sale++;
        }
    };

    public static ProspectFragment newInstance(String param1, String param2) {
        ProspectFragment fragment = new ProspectFragment();
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
        customer_details = getActivity().getIntent().getExtras();
        if (getActivity().getIntent() != null) {
            cust_gid = customer_details.getInt("customer_id");
            if (customer_details.containsKey("soheader_no")) {

                soheaderno = customer_details.getInt("soheader_no");
            }
            if (customer_details.containsKey("Status")) {

                EditStatus = customer_details.getString("Status");
            }
            if (customer_details.containsKey("Sale_Schedule_gid")) {

                Sale_Schedule_gid = customer_details.getInt("Sale_Schedule_gid");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        favProduct = new ArrayList<>();
        View rootView = inflater.inflate(R.layout.fragment_sales_order, container, false);
        btnsubmit_order = rootView.findViewById(R.id.btnsubmit_order);
        relativeLayoutupdate = rootView.findViewById(R.id.relativeLayoutupdate);
        btnUpdate = rootView.findViewById(R.id.btnUpdate);
        btnCancel = rootView.findViewById(R.id.btnCancel);
        btnsubmit_order.setOnClickListener(this);

        btnUpdate.setOnClickListener(this);

        btnCancel.setOnClickListener(this);

        if (soheaderno != 0) {
            relativeLayoutupdate.setVisibility(View.VISIBLE);
            btnsubmit_order.setVisibility(View.GONE);
        }

        auto_product = rootView.findViewById(R.id.auto_product);
        auto_product.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                autoProductAdapter.productfilter(s.toString(), favProduct);
            }
        });
        auto_product.setThreshold(1);
        auto_product.setOnItemClickListener(autoItemClickListener);
        autoProductAdapter = new AutoProductAdapter(getActivity(), R.layout.item_product);
        auto_product.setAdapter(autoProductAdapter);
        tableLayout = rootView.findViewById(R.id.tbl_layout);
        load_favProduct();
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

    public void load_favProduct() {

        if (soheaderno == 0) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading..");
            progressDialog.setCancelable(false);
            progressDialog.show();
            String URL = Constant.URL + "Product_SalesFav?Customer_gid=" + cust_gid + "&Entity_gid=1";
            CallbackHandler.sendReqest(getActivity(), Request.Method.GET, "", URL, new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String status = jsonObject.getString("MESSAGE");
                        val = new ArrayList<>();
                        setHeader();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (status.equals("FOUND")) {

                            JSONArray jsonArray;
                            jsonArray = jsonObject.getJSONArray("DATA");
                            jsonArray = jsonArray;
//                        val = new ArrayList<>();
//                        setHeader(Sales_order.this);


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj_json = jsonArray.getJSONObject(i);
                                String ProductName = obj_json.getString("product_displayname");
                                String lsDate = obj_json.getString("dat");
                                String Qty = obj_json.getString("sodetails_qty");
                                int ProductGid = obj_json.getInt("sodetails_product_gid");
                                String Date = Common.convertDateString(Common.convertDate(lsDate, "yyyy-MM-dd"),
                                        "dd-MMM-yyyy");

                                Generate_Layout(ProductName, Date, Qty, ProductGid, i, 0);// Values will be passed from here
                            }
                            btnsubmit_order.setEnabled(true);
                        }
                    } catch (JSONException e) {
                        Log.e("Login", e.getMessage());
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                }


                public void onFailure(String result) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Log.e("Login", result);

                }


            });
        } else {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading..");
            progressDialog.setCancelable(false);
            progressDialog.show();
            String URL = Constant.URL + "FET_SalesOrder_Get?SO_Header_gid=" + soheaderno + "&Entity_gid=1&Action=BY_REF_GID";
            CallbackHandler.sendReqest(getActivity(), Request.Method.GET, "", URL, new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String status = jsonObject.getString("MESSAGE");
                        val = new ArrayList<>();
                        setHeader();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (status.equals("FOUND")) {

                            JSONArray jsonArray;
                            jsonArray = jsonObject.getJSONArray("DATA");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj_json = jsonArray.getJSONObject(i);
                                String ProductName = obj_json.getString("product_name");
                                String Date = obj_json.getString("soheader_date");
                                String Qty = obj_json.getString("quantity");
                                int ProductGid = obj_json.getInt("sodetails_product_gid");
                                int sodetails_gid = obj_json.getInt("sodetails_gid");
                                String soheader_status = obj_json.getString("soheader_status");
                                if (soheader_status != "CANCELLED") {

                                    Generate_Layout(ProductName, Date, Qty, ProductGid, j_sale, sodetails_gid);
                                    j_sale++;
                                }
                            }
                            btnUpdate.setEnabled(true);
                            btnCancel.setEnabled(true);
                        }
                    } catch (JSONException e) {
                        Log.e("SALE", e.getMessage());
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                }


                public void onFailure(String result) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Log.e("sale", result);

                }


            });

        }
    }

    private void Generate_Layout(String Name, String Date, String Qty, int ProductGid, int i, int sodetails_gid) {
        try {
            favProduct.add(Integer.valueOf(ProductGid));
            tableLayout.setStretchAllColumns(true);
            tableLayout.setShrinkAllColumns(true);

            tableRow = new TableRow(getActivity());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

            tableRow.setLayoutParams(lp);
            tableRow.setBackgroundColor(Color.WHITE);

            TextView sNo = new TextView(getActivity());

            sNo.setBackground(getResources().getDrawable(R.drawable.table_body));
            sNo.setLayoutParams(lp);
//            sNo.setText("1");
            sNo.setGravity(Gravity.CENTER);
//            int total = tableLayout.getChildCount();
//            for (int j = 1; j <= total; j++) {
//                View view = tableLayout.getChildAt(j);
//                if (view instanceof TableRow) {
//                    TableRow row = (TableRow) view;
//                    TextView t = (TextView) row.getChildAt(0);
//                    t.setText("" + (j + 1));
//                }
//            }


            TextView productName = new TextView(getActivity());
            productName.setText(Name);
            productName.setBackground(getResources().getDrawable(R.drawable.table_body));
            productName.setLayoutParams(lp);
            productName.setFocusable(false);

            TextView date = new TextView(getActivity());
            date.setText(Date);
            date.setBackground(getResources().getDrawable(R.drawable.table_body));
            date.setLayoutParams(lp);


            final TextView qty = new TextView(getActivity());
            qty.setText(Qty);
            qty.setBackground(getResources().getDrawable(R.drawable.table_body));
            qty.setLayoutParams(lp);
            qty.setGravity(Gravity.CENTER);


            EditText orderQty = new EditText(getActivity());
            orderQty.setTag(ProductGid);
            orderQty.setBackground(getResources().getDrawable(R.drawable.table_body));
            orderQty.setLayoutParams(lp);
            orderQty.setHint("Qty");
            orderQty.setGravity(Gravity.CENTER);
            orderQty.setFocusable(true);
            orderQty.setInputType(InputType.TYPE_CLASS_NUMBER);
            if (soheaderno != 0) {
                orderQty.setText(Qty);
            }

            val.add(orderQty);

            tableRow.addView(sNo);
            tableRow.addView(productName);
            tableRow.addView(date);
            tableRow.addView(qty);
            tableRow.addView(orderQty);
            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.hideKeyboard(getActivity());
                    TableRow row = (TableRow) v;
                    TextView qty = (TextView) ((TableRow) v).getChildAt(3);
                    EditText re = (EditText) row.getChildAt(4);
                    re.setText(qty.getText().toString());
                }
            });
            if (soheaderno != 0) {
                ImageButton minusBtn = new ImageButton(getActivity());
                minusBtn.setImageResource(R.drawable.ic_action_remove);
                minusBtn.setLayoutParams(lp);
                minusBtn.setFocusable(true);
                minusBtn.setOnClickListener(delete_clickListener);
                tableRow.addView(minusBtn);
            }
            if (soheaderno == 0) {
                tableLayout.addView(tableRow, 1);
            } else {
                tableLayout.addView(tableRow, i);
                Variables.Update_sales updateSale_set = new Variables.Update_sales(ProductGid, sodetails_gid, Integer.parseInt(Qty));
                updateSales.add(updateSale_set);
            }
            int total = tableLayout.getChildCount();
            for (int j = 1; j <= total; j++) {
                View view = tableLayout.getChildAt(j);
                if (view instanceof TableRow) {
                    TableRow row1 = (TableRow) view;
                    TextView t = (TextView) row1.getChildAt(0);
                    t.setText("" + (j));
                }
            }

        } catch (Exception e) {
            String Log = e.getMessage();
        }


    }

    private void setHeader() {

        tableLayout.setStretchAllColumns(true);
        tableLayout.setShrinkAllColumns(true);

        tableRow = new TableRow(getActivity());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        tableRow.setLayoutParams(lp);


        TextView sNo = new TextView(getActivity());
        sNo.setText("S.No");
        sNo.setTextColor(0xFFFFFFFF);
        sNo.setTextSize(15);
        sNo.setGravity(Gravity.CENTER);
        sNo.setLayoutParams(lp);
        sNo.setBackgroundResource(R.drawable.table_header);


        TextView productName = new TextView(getActivity());
        productName.setText("Product Name");
        productName.setTextColor(0xFFFFFFFF);
        productName.setTextSize(15);
        productName.setGravity(Gravity.CENTER);
        productName.setLayoutParams(lp);
        productName.setBackgroundResource(R.drawable.table_header);

        TextView date = new TextView(getActivity());
        date.setText("Date");
        date.setGravity(Gravity.CENTER);
        date.setTextColor(0xFFFFFFFF);
        date.setTextSize(15);
        date.setLayoutParams(lp);
        date.setBackgroundResource(R.drawable.table_header);

        TextView qty = new TextView(getActivity());
        if (soheaderno == 0) {
            qty.setText("Qty");
        } else {
            qty.setText("Sale_Qty");
        }
        qty.setGravity(Gravity.CENTER);
        qty.setTextColor(0xFFFFFFFF);
        qty.setTextSize(15);
        qty.setLayoutParams(lp);
        qty.setBackgroundResource(R.drawable.table_header);

        TextView orderQty = new TextView(getActivity());
        orderQty.setText("Order Qty");
        orderQty.setTextColor(0xFFFFFFFF);
        orderQty.setTextSize(15);
        orderQty.setGravity(Gravity.CENTER);
        orderQty.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3) {
        }});
        orderQty.setFilters(new InputFilter[]{new Common.InputFilterMinMax("1", "500")});
        orderQty.setLayoutParams(lp);
        orderQty.setBackgroundResource(R.drawable.table_header);

        tableRow.addView(sNo);
        tableRow.addView(productName);
        tableRow.addView(date);
        tableRow.addView(qty);
        tableRow.addView(orderQty);

        if (soheaderno != 0) {
            TextView delete = new TextView(getActivity());
            delete.setText("Delete");
            delete.setGravity(Gravity.CENTER);
            delete.setTextColor(0xFFFFFFFF);
            delete.setTextSize(15);
            delete.setLayoutParams(lp);
            delete.setBackgroundResource(R.drawable.table_header);
            tableRow.addView(delete);
        }
        tableLayout.addView(tableRow, 0);


    }

    public void onClick(View view) {

        if (view == btnsubmit_order) {
            try {
                if (val.size() > 0) {

                    final JSONArray soDetails = new JSONArray();
                    for (i = 0; i < val.size(); i++) {
                        String pdct_gid = val.get(i).getTag().toString().trim();
                        String order_qty = val.get(i).getText().toString().trim();

                        if (pdct_gid.trim().length() > 0 && order_qty.trim().length() > 0 && pdct_gid != null && order_qty != null
                                && Float.parseFloat(order_qty.trim()) > 0) {

                            JSONObject objSoDetails = new JSONObject();

                            objSoDetails.put("sodetails_product_gid", val.get(i).getTag().toString());
                            objSoDetails.put("quantity", val.get(i).getText().toString());
                            soDetails.put(objSoDetails);
                        }
                    }

                    if (soDetails.length() > 0) {
                        // Get Confirm from User
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Confirm");
                        builder.setMessage("Are You Sure To Submit?");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                try {
                                    JSONObject Full_Json = new JSONObject();
                                    JSONObject params_Json = new JSONObject();
                                    params_Json.put(Constant.emp_gid, UserDetails.getUser_id());
                                    params_Json.put(Constant.soheader_gid, 0);
                                    params_Json.put(Constant.customer_gid, cust_gid);
                                    JSONObject detail_Json = new JSONObject();
                                    detail_Json.put(Constant.sodetails, soDetails);
                                    detail_Json.put(Constant.Schedule_Affect, "YES");
                                    params_Json.put(Constant.Data, detail_Json);
                                    params_Json.put(Constant.Action, "Insert");

                                    Full_Json.put(Constant.params, params_Json);
//                                    Log.v("JSONPON", Full_Json.toString());

                                    if (Full_Json.length() > 0) {
                                        String OutMessage = SalesOrderSet(Full_Json);
                                    }
                                } catch (JSONException e) {
                                    Log.e("Sales-Json", e.getMessage());
                                }


                            }

                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();

                    } else {
                        Toast.makeText(getActivity(), "No Data To Save.", Toast.LENGTH_LONG).show();
                    }


                }
            } catch (Exception e) {
                Log.e("Sales", e.getMessage());
            }
        } else if (view == btnUpdate) {
            if (val.size() > 0) {
                JSONArray salearray = new JSONArray();

                JSONObject saleJson = new JSONObject();
                JSONObject saleJson1 = new JSONObject();
                final JSONObject params_Json = new JSONObject();
                int total = tableLayout.getChildCount();
                try {
                    for (int j = 1; j < total; j++) {
                        View viewtbl = tableLayout.getChildAt(j);
                        if (viewtbl instanceof TableRow) {
                            EditText qty = (EditText) ((TableRow) viewtbl).getChildAt(4);
                            if (qty.length() != 0 && Integer.parseInt(qty.getText().toString()) >= 1) {
                                JSONObject Json = new JSONObject();
                                Variables.Update_sales updateSalesobj = updateSales.get(j - 1);
                                Json.put("sodetails_product_gid", updateSalesobj.product_id);
                                Json.put("quantity", Integer.parseInt(qty.getText().toString()));
                                if (updateSalesobj.sodetails_gid != 0) {
                                    Json.put("sodetails_gid", updateSalesobj.sodetails_gid);
                                    Json.put("soheader_gid", soheaderno);
                                }
                                salearray.put(Json);
                            } else {
                                Toast.makeText(getActivity(), "Fill All Quantity Field.", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }

                    saleJson.put("sodetails", salearray);
                    //saleJson.put("schedule_gid", Sale_Schedule_gid);
                    if (EditStatus == "REJECTED") {
                        saleJson.put("soheader", new JSONObject().put("status", "RESUBMIT"));
                    }
                    saleJson1.put("emp_gid", UserDetails.getUser_id());
                    saleJson1.put("custid", cust_gid);
                    saleJson1.put("data", saleJson);
                    saleJson1.put("ACTION", "Update");
                    params_Json.put("parms", saleJson1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (salearray.length() != 0) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Confirm");
                    builder.setMessage("Are You Sure To Submit?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String OutMessage = Delete_submit();
                            if (OutMessage == "Delete Success") {
                                String URL = Constant.URL + "FET_SalesOrder?Emp_gid=" + UserDetails.getUser_id() +
                                        "&Entity_gid=1&Date=" + Common.convertDateString(new Date(), "dd-MMM-yyyy");

                                CallbackHandler.sendReqest(getActivity(), Request.Method.POST, params_Json.toString(), URL, new VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(result);
                                            String status = jsonObject.getString("MESSAGE");
                                            if (status.equals("SUCCESS")) {

                                                Toast.makeText(getActivity(), "Sale Updated Succussfully.", Toast.LENGTH_LONG).show();
                                                getActivity().finish();

                                            } else {
                                                Toast.makeText(getActivity(), "Error While Edited.", Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            Log.e("Sales", e.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onFailure(String result) {
                                        Log.e("Location", result);

                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), "Problem With Delete.", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();

                } else {
                    Toast.makeText(getActivity(), "No Data To Save.", Toast.LENGTH_LONG).show();

                }
            } else {
                Toast.makeText(getActivity(), "No Data To Save.", Toast.LENGTH_LONG).show();

            }
        } else if (view == btnCancel) {

            final JSONArray salearray = new JSONArray();

            JSONObject saleJson = new JSONObject();
            JSONObject saleJson1 = new JSONObject();
            final JSONObject params_Json = new JSONObject();
            int total = tableLayout.getChildCount();
            try {
                for (int j = 1; j < total; j++) {
                    View viewtbl = tableLayout.getChildAt(j);
                    JSONObject Json = new JSONObject();
                    //  EditText qty = (EditText) ((TableRow) viewtbl).getChildAt(4);
                    Variables.Update_sales updateSalesobj = updateSales.get(j - 1);
                    Json.put("sodetails_product_gid", updateSalesobj.product_id);
                    // Json.put("quantity", Integer.parseInt(qty.getText().toString()));
                    Json.put("sodetails_gid", updateSalesobj.sodetails_gid);
                    Json.put("soheader_gid", soheaderno);
                    salearray.put(Json);

                }
                saleJson.put("sodetails", salearray);
                if(Sale_Schedule_gid != 0) {
                    saleJson.put("schedule_gid", Sale_Schedule_gid);
                }
                saleJson1.put("emp_gid", UserDetails.getUser_id());
                saleJson1.put("custid", cust_gid);
                saleJson1.put("data", saleJson);
                if(Sale_Schedule_gid != 0) {
                    saleJson1.put("ACTION", "Delete");
                }else{
                    saleJson1.put("ACTION", "DIRECTSALE_DELETE");

                }
                params_Json.put("parms", saleJson1);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Confirm");
            builder.setMessage("Are You Sure To Submit?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String URL = Constant.URL + "FET_SalesOrder?Emp_gid=" + UserDetails.getUser_id() +
                            "&Entity_gid=1&Date=" + Common.convertDateString(new Date(), "dd-MMM-yyyy");

                    CallbackHandler.sendReqest(getActivity(), Request.Method.POST, params_Json.toString(), URL, new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                String status = jsonObject.getString("MESSAGE");
                                if (status.equals("SUCCESS")) {

                                    Toast.makeText(getActivity(), "Sale Cancelled Succussfully.", Toast.LENGTH_LONG).show();
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(getActivity(), "Error While Canceling.", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Log.e("Sales", e.getMessage());
                            }
                        }

                        @Override
                        public void onFailure(String result) {
                            Log.e("Location", result);

                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();

        }

    }

    public String SalesOrderSet(JSONObject jsonObject) {

        // To Set in Main Server-SALES.
        String URL = Constant.URL + "FET_SalesOrder?Emp_gid=" + UserDetails.getUser_id() +
                "&Entity_gid=1&Date=" + Common.convertDateString(new Date(), "dd-MMM-yyyy");

        CallbackHandler.sendReqest(getActivity(), Request.Method.POST, jsonObject.toString(), URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("MESSAGE");
                    if (status.equals("SUCCESS")) {
                        Toast.makeText(getActivity(), "Sales Saved Successfully.", Toast.LENGTH_LONG).show();

                        getActivity().finish();


                    } else {
                        Toast.makeText(getActivity(), "Sales Not Saved Successfully.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e("Sales", e.getMessage());
                }
            }

            @Override
            public void onFailure(String result) {
                Log.e("Location", result);

            }
        });

        return "";
    }

    public String Delete_submit() {
        final String[] returnmsg = {"Delete Success"};
        ;
        final JSONArray salearray = new JSONArray();
        JSONObject Json = new JSONObject();
        JSONObject saleJson = new JSONObject();
        JSONObject saleJson1 = new JSONObject();
        JSONObject params_Json = new JSONObject();

        for (i = 0; i < DeleteList.size(); i++) {
            if (DeleteList.get(i) != 0) {
                try {
                    // Json.put("sodetails_product_gid", updateSalesobj.product_id);
                    // Json.put("quantity", updateSalesobj.qty);
                    Json.put("sodetails_gid", DeleteList.get(i));
                    //Json.put("soheader_gid", soheaderno);
                    salearray.put(Json);
                    saleJson.put("sodetails", salearray);
                    //saleJson.put("schedule_gid", Sale_Schedule_gid);
                    saleJson1.put("emp_gid", UserDetails.getUser_id());
                    saleJson1.put("custid", cust_gid);
                    saleJson1.put("data", saleJson);
                    saleJson1.put("ACTION", "Delete");
                    params_Json.put("parms", saleJson1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String URL = Constant.URL + "FET_SalesOrder?Emp_gid=" + UserDetails.getUser_id() +
                        "&Entity_gid=1&Date=" + Common.convertDateString(new Date(), "dd-MMM-yyyy");

                CallbackHandler.sendReqest(getActivity(), Request.Method.POST, params_Json.toString(), URL, new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String status = jsonObject.getString("MESSAGE");
                            if (status.equals("SUCCESS")) {

                                Toast.makeText(getActivity(), "Product Deleted Succussfully.", Toast.LENGTH_LONG).show();
                                returnmsg[0] = "Delete Success";

                            } else {
                                Toast.makeText(getActivity(), "Error While Deleting.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.e("Sales", e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(String result) {
                        Log.e("Location", result);
                        returnmsg[0] = "Delete Fail";
                    }
                });
            }
        }
        return returnmsg[0];
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}

