package models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.vsolv.bigflow.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import constant.Constant;
import network.CallbackHandler;
import presenter.VolleyCallback;

public class AutoProductAdapter extends ArrayAdapter {

    private final Context _Context;
    private final int _resource;
    List<Variables.Product> productList;

    public AutoProductAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this._Context = context;
        this._resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(_Context);
        View view = inflater.inflate(_resource, null);
        TextView Product_name = view.findViewById(R.id.txtproduct_auto);
        Variables.Product product = productList.get(position);
        Product_name.setText(product.product_name);
        return view;
    }

    @Override
    public int getCount() {
        // Last item will be the footer
        return productList.size();
    }

    @Override
    public String getItem(int position) {
        return productList.get(position).product_name;
    }

    public Variables.Product getSelectedItem(int position) {
        return productList.get(position);
    }

    public void productfilter(String product_name, ArrayList<Integer> favProduct) {
        if (product_name != null) {
            changeAdapder(product_name, favProduct);
        }
    }
    //Stock Autoproduct
    public void stock_product_load(ArrayList<Variables.Product> load_product){
        List<Variables.Product> sProductlist = new ArrayList<>();

        for(int i=0;i<load_product.size();i++){
            Variables.Product stock_product = load_product.get(i);
            stock_product.product_id= stock_product.product_id;
            stock_product.product_name=stock_product.product_name;

            sProductlist.add(stock_product);

        }

        productList = sProductlist;
        notifyDataSetChanged();
    }

    private void changeAdapder(final String product_name, final ArrayList<Integer> favProduct) {
        final List<Variables.Product> mProductlist = new ArrayList<>();
        String URL = Constant.URL + "Product_Sales?";
        URL = URL + "&Product_Name=" + product_name + "&Limit=10&Entity_gid=" + UserDetails.getEntity_gid();

        CallbackHandler.sendReqest(_Context, Request.Method.GET, "", URL, new VolleyCallback() {
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
                            product.product_id = obj_json.getInt("product_gid");
                            product.product_name = obj_json.getString("product_displayname");
                            if (favProduct.indexOf(product.product_id) < 0)
                                mProductlist.add(product);
                        }
                        productList = mProductlist;
                        notifyDataSetChanged();

                    } else {
                        notifyDataSetInvalidated();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    notifyDataSetInvalidated();
                }


            }

            @Override
            public void onFailure(String result) {
                notifyDataSetInvalidated();
                Log.e("Getdata-scheduletype", result);
            }
        });

    }
}
