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
import android.widget.LinearLayout;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import DataBase.GetData;
import constant.Constant;
import models.AutoProductAdapter;
import models.Common;
import models.UserDetails;
import models.Variables;
import network.CallbackHandler;
import presenter.NetworkResult;
import presenter.VolleyCallback;

public class Sales_order extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public Bundle customer_details;
    public Button btnsubmit_order, btnUpdate, btnCancel;
    public LinearLayout linearSales;
    private int customer_gid, soheader_gid, schedule_gid;
    boolean isEdit;
    private String mParam1;
    private String mParam2;
    private ProspectFragment.OnFragmentInteractionListener mListener;
    private TableRow tableRow;
    private AutoCompleteTextView auto_product;
    private TableLayout tableLayout;
    private AutoProductAdapter autoProductAdapter;
    private ArrayList<Integer> favProduct;
    private ProgressDialog progressDialog;
    private View fragmentView;
    private TableRow.LayoutParams layoutParams;
    private GetData getData;
    private List<Variables.SalesDetail> salesDetailList;

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
        schedule_gid = customer_details.getInt(Constant.key_schedule_gid, 0);
        soheader_gid = customer_details.getInt(Constant.key_soheader_gid, 0);
        customer_gid = customer_details.getInt(Constant.key_customer_gid, 0);
        if (soheader_gid != 0)
            isEdit = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        favProduct = new ArrayList<>();
        fragmentView = inflater.inflate(R.layout.fragment_sales_order, container, false);
        loadView();
        initializeView();
        loadData();

        if (isEdit) {
            linearSales.setVisibility(View.VISIBLE);
            btnsubmit_order.setVisibility(View.GONE);
        }
        return fragmentView;
    }

    private void loadView() {
        btnsubmit_order = fragmentView.findViewById(R.id.btnsubmit_order);
        linearSales = fragmentView.findViewById(R.id.relativeLayoutupdate);
        btnUpdate = fragmentView.findViewById(R.id.btnUpdate);
        btnCancel = fragmentView.findViewById(R.id.btnCancel);
        auto_product = fragmentView.findViewById(R.id.auto_product);
        tableLayout = fragmentView.findViewById(R.id.tbl_layout);
    }

    private void initializeView() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        tableLayout.setStretchAllColumns(true);
        getData = new GetData(getActivity());
        btnsubmit_order.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        auto_product.setThreshold(1);
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

        auto_product.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Variables.Product product = autoProductAdapter.getSelectedItem(position);
                Variables.SalesDetail detail = new Variables.SalesDetail();
                detail.product_gid = product.product_id;
                detail.product_name = product.product_name;
                detail.sales_date = Common.convertDateString(new Date(), Constant.date_display_format);
                Generate_Layout(detail, 1, 1);
                setTableIndex();
                auto_product.setText("");
            }
        });
    }

    private void loadData() {
        autoProductAdapter = new AutoProductAdapter(getActivity(), R.layout.item_product);
        auto_product.setAdapter(autoProductAdapter);
        setHeader();
        load_favProduct();
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

        if (!isEdit) {
            progressDialog.show();
            salesDetailList = getData.favProductList(1, new NetworkResult() {
                @Override
                public void handlerResult(String result) {
                    for (int i = 0; i < salesDetailList.size(); i++) {
                        Variables.SalesDetail salesDetail = salesDetailList.get(i);
                        Generate_Layout(salesDetail, i + 1, i + 1);
                    }
                    progressDialog.cancel();
                }

                @Override
                public void handlerError(String result) {
                    progressDialog.cancel();
                }
            });


        } else {
            salesDetailList = getData.orderedQuantityList(soheader_gid, new NetworkResult() {
                @Override
                public void handlerResult(String result) {
                    for (int i = 0; i < salesDetailList.size(); i++) {
                        Variables.SalesDetail salesDetail = salesDetailList.get(i);
                        Generate_Layout(salesDetail, i + 1, i + 1);
                        progressDialog.cancel();
                    }
                }

                @Override
                public void handlerError(String result) {
                    progressDialog.cancel();
                }
            });
        }
    }

    private void Generate_Layout(final Variables.SalesDetail salesDetail, int index, int insetIndex) {

        favProduct.add(Integer.valueOf(salesDetail.product_gid));
        tableRow = new TableRow(getActivity());
        tableRow.setLayoutParams(layoutParams);
        tableRow.setTag(salesDetail.sodetail_gid);

        TextView sNo = getTextView(getActivity(), false, String.valueOf(index));
        TextView productName = getTextView(getActivity(), false, salesDetail.product_name);
        TextView date = getTextView(getActivity(), false, salesDetail.sales_date);
        TextView qty = getTextView(getActivity(), false, String.valueOf(salesDetail.product_quantity));


        EditText orderQty = getEditTextView(getActivity(), null, "Qty");
        orderQty.setTag(salesDetail.product_gid);
        orderQty.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (isEdit) {
            orderQty.setText("" + salesDetail.order_quantity);
        }


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
        if (isEdit) {
            TextView btnRemove = getTextView(getActivity(), false, "");
            btnRemove.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_remove, 0, 0, 0);
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TableRow tableRow = (TableRow) v.getParent();
                    int sodetail_gid = Integer.parseInt(tableRow.getTag().toString());
                    if (sodetail_gid == 0) {
                        tableLayout.removeView(tableRow);
                        setTableIndex();
                    } else {
                        deleteSales(sodetail_gid);
                    }

                    //Toast.makeText(getActivity(), "You Cannot Delete This line.", Toast.LENGTH_LONG).show();
                }
            });
            tableRow.addView(btnRemove);
        }

        tableLayout.addView(tableRow, insetIndex);
    }

    private void setHeader() {

        tableRow = new TableRow(getActivity());
        tableRow.setLayoutParams(layoutParams);

        TextView sNo = getTextView(getActivity(), true, "S.No");
        TextView productName = getTextView(getActivity(), true, "Product Name");
        TextView date = getTextView(getActivity(), true, "Date");
        TextView qty = getTextView(getActivity(), true, "Qty");
        TextView orderQty = getTextView(getActivity(), true, "Order qty");

        tableRow.addView(sNo);
        tableRow.addView(productName);
        tableRow.addView(date);
        tableRow.addView(qty);
        tableRow.addView(orderQty);

        if (isEdit) {
            TextView delete = getTextView(getActivity(), true, "Delete");
            tableRow.addView(delete);
        }
        tableLayout.addView(tableRow, 0);


    }

    private TextView getTextView(Context context, Boolean isHeader, String setText) {
        TextView textView = new TextView(context);
        textView.setText(setText);
        textView.setGravity(Gravity.CENTER);
        if (isHeader) {
            textView.setTextColor(0xFFFFFFFF);
            textView.setTextSize(15);
            textView.setBackgroundResource(R.drawable.table_header);
        } else {
            textView.setTextSize(14);
            textView.setBackgroundResource(R.drawable.table_body);
        }
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    private EditText getEditTextView(Context context, String setText, String hint) {
        EditText editText = new EditText(context);
        editText.setText(setText);
        editText.setHint(hint);
        editText.setGravity(Gravity.LEFT);
        editText.setTextSize(14);
        editText.setBackgroundResource(R.drawable.table_body);
        editText.setLayoutParams(layoutParams);
        return editText;
    }

    private void setTableIndex() {
        int total = tableLayout.getChildCount();
        for (int j = 1; j <= total; j++) {
            View view = tableLayout.getChildAt(j);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                TextView t = (TextView) row.getChildAt(0);
                t.setText("" + j);
            }
        }
    }

    public void onClick(View view) {

        if (view.getId() == R.id.btnsubmit_order) {
            if (getSalesDetails().size() == 0) {
                Toast.makeText(getActivity(), "Add Product to submit!.", Toast.LENGTH_LONG).show();
                return;
            }
            createConfirmDialog("Are You Sure To Submit?.", view.getId());
        } else if (view == btnUpdate) {
            if (getSalesDetails().size() == 0) {
                Toast.makeText(getActivity(), "Fill order quantity!.", Toast.LENGTH_LONG).show();
                return;
            }
            createConfirmDialog("Are You Sure To Update?.", view.getId());
        } else if (view == btnCancel) {
            createConfirmDialog("Are You Sure To Cancel sales?.", view.getId());
        }

    }

    private void createConfirmDialog(String message, final int submitId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                if (submitId == R.id.btnsubmit_order) {
                    SalesOrderSet();
                } else if (submitId == R.id.btnCancel) {
                    salesCancel();
                } else if (submitId == R.id.btnUpdate) {
                    salesUpdate();
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
    }

    private void salesCancel() {
        progressDialog.show();
        getData.updateSalesDetails("CANCEL", customer_gid, schedule_gid, Common.convertDateString(new Date(), "dd-MMM-yyyy"), new JSONArray(), new NetworkResult() {
            @Override
            public void handlerResult(String result) {
                progressDialog.cancel();
                if (result.equals("SUCCESS")) {
                    Toast.makeText(getActivity(), "Sales Canceled!.", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "Sales not Canceled!.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void handlerError(String result) {
                Toast.makeText(getActivity(), "Sales not Canceled!.", Toast.LENGTH_LONG).show();
                progressDialog.cancel();
            }
        });
    }

    public void SalesOrderSet() {
        progressDialog.show();
        List<Variables.SalesDetail> salesDetails = getSalesDetails();
        JSONArray soDetails = new JSONArray();
        for (int i = 0; i < salesDetails.size(); i++) {
            Variables.SalesDetail detail = salesDetails.get(i);
            JSONObject objSoDetails = new JSONObject();

            try {
                objSoDetails.put("quantity", detail.order_quantity);
                objSoDetails.put("sodetails_product_gid", detail.product_gid);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            soDetails.put(objSoDetails);
        }


        getData.setSalesDetails(customer_gid, Common.convertDateString(new Date(), "dd-MMM-yyyy"), soDetails, new NetworkResult() {
            @Override
            public void handlerResult(String result) {
                progressDialog.cancel();
                if (result.equals("SUCCESS")) {
                    Toast.makeText(getActivity(), "Saved successfully!.", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "Not saved Successfully!.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void handlerError(String result) {
                progressDialog.cancel();
                Toast.makeText(getActivity(), "Not saved Successfully!.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void salesUpdate() {
        progressDialog.show();
        List<Variables.SalesDetail> salesDetails = getSalesDetails();
        JSONArray soDetails = new JSONArray();
        for (int i = 0; i < salesDetails.size(); i++) {
            Variables.SalesDetail detail = salesDetails.get(i);
            JSONObject objSoDetails = new JSONObject();

            try {

                objSoDetails.put("quantity", detail.order_quantity);
                objSoDetails.put("sodetails_product_gid", detail.product_gid);
                if (detail.sodetail_gid != 0) {
                    objSoDetails.put("sodetails_gid", detail.sodetail_gid);
                    objSoDetails.put("soheader_gid", detail.soheader_gid);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            soDetails.put(objSoDetails);
        }
        getData.updateSalesDetails("UPDATE", customer_gid, schedule_gid, Common.convertDateString(new Date(), "dd-MMM-yyyy"), soDetails, new NetworkResult() {
            @Override
            public void handlerResult(String result) {
                progressDialog.cancel();
                if (result.equals("SUCCESS")) {
                    Toast.makeText(getActivity(), "Updated successfully!.", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "Not Updated successfully!.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void handlerError(String result) {
                progressDialog.cancel();
                Toast.makeText(getActivity(), "Not Updated successfully!.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public List<Variables.SalesDetail> getSalesDetails() {
        List<Variables.SalesDetail> orderList = new ArrayList<>();
        for (int i = 1; i <= tableLayout.getChildCount(); i++) {
            View view1 = tableLayout.getChildAt(i);
            if (view1 instanceof TableRow) {
                TableRow row = (TableRow) view1;
                EditText qty = (EditText) row.getChildAt(4);
                String s = qty.getText().toString();
                if (qty.getText().toString().length() != 0) {
                    if (Integer.parseInt(qty.getText().toString()) > 0) {
                        Variables.SalesDetail detail = new Variables.SalesDetail();
                        detail.sodetail_gid = Integer.parseInt(row.getTag().toString());
                        detail.product_gid = Integer.parseInt(qty.getTag().toString());
                        detail.order_quantity = Integer.parseInt(qty.getText().toString());
                        detail.soheader_gid = soheader_gid;
                        orderList.add(detail);
                    }
                }
            }
        }
        return orderList;
    }

    public void deleteSales(int soDetail_gid) {
        progressDialog.show();
        JSONArray salearray = new JSONArray();
        try {
            salearray.put(new JSONObject().put("sodetails_gid", soDetail_gid));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getData.updateSalesDetails("DELETE", customer_gid, schedule_gid, Common.convertDateString(new Date(), "dd-MMM-yyyy"), salearray, new NetworkResult() {
            @Override
            public void handlerResult(String result) {
                progressDialog.cancel();
                if (result.equals("SUCCESS")) {
                    Toast.makeText(getActivity(), "Deleted!.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Not Deleted!.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void handlerError(String result) {
                Toast.makeText(getActivity(), "Not Deleted!.", Toast.LENGTH_LONG).show();
                progressDialog.cancel();
            }
        });
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}

