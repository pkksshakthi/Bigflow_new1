package models;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vsolv.bigflow.R;

import java.util.ArrayList;
import java.util.List;

public class ServiceSummryAdapter extends RecyclerView.Adapter<ServiceSummryAdapter.CustomerViewHolder> {
    private Context mCtx;
    private List<Variables.ServiceSummary> customerList;
    private List<Variables.ServiceSummary> selectedList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Variables.ServiceSummary item, int position);

    }

    public void setOnclickListener(OnItemClickListener Listener) {
        mListener = Listener;
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, productName, Date;

        public CustomerViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            customerName = itemView.findViewById(R.id.txtCustomerName);
            productName = itemView.findViewById(R.id.txtProductName);
            Date = itemView.findViewById(R.id.txtDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            boolean isSelected = getitem(position).is_selected;
                            if (!isSelected) {
                                selectedList.add(customerList.get(position));
                                customerList.get(position).is_selected = true;
                                customerName.setCompoundDrawablesWithIntrinsicBounds(null, null, mCtx.getResources().getDrawable(R.drawable.ic_action_check), null);
                            } else {
                                selectedList.remove(customerList.get(position));
                                customerList.get(position).is_selected = false;
                                customerName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            }
                            listener.onItemClick(getitem(position), position);
                        }
                    }

                }
            });

        }
    }

    public ServiceSummryAdapter(Context mCtx, List<Variables.ServiceSummary> List) {
        this.mCtx = mCtx;
        this.customerList = List;
        selectedList = new ArrayList<>();
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_servicesummary, parent, false);
        return new CustomerViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomerViewHolder holder, int position) {

        final Variables.ServiceSummary customer = customerList.get(position);
        holder.customerName.setText(customer.customer_name.toUpperCase());
        holder.productName.setText(customer.product_name);
        holder.Date.setText(customer.service_date);
        if (!customerList.get(position).is_selected) {
            holder.customerName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public void updateList(List<Variables.ServiceSummary> list) {
        customerList = list;
        notifyDataSetChanged();
    }

    public Variables.ServiceSummary getitem(int position) {
        return customerList.get(position);
    }

    public List<Variables.ServiceSummary> getCustomerList() {
        return customerList;
    }

    public List<Variables.ServiceSummary> getSelectedList() {
        return selectedList;
    }

    public void clearSelectedList() {
        selectedList = new ArrayList<>();
    }
}
