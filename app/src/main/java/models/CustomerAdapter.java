package models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vsolv.bigflow.R;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    private Context mCtx;
    private List<Variables.Customer> customerList;
    private OnItemClickListener mListener;
    private boolean is_in_action;

    public interface OnItemClickListener {
        void onItemClick(View view, Variables.Customer item, int position);

        void onItemLongClick(View view, Variables.Customer item, int position);

        void onViewDetailsClick(Variables.Customer item, int position);

        void onHistroryClick(Variables.Customer item, int position);

        void onCommentClick(Variables.Customer item, int position);
    }

    public void setOnclickListener(OnItemClickListener Listener) {
        mListener = Listener;
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, locationName, txtCustViewDetails, txtHistory, txtComment;

        public CustomerViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            customerName = itemView.findViewById(R.id.txtCustomerName);
            locationName = itemView.findViewById(R.id.txtLocationName);
            txtCustViewDetails = itemView.findViewById(R.id.txtCustViewDetails);
            txtHistory = itemView.findViewById(R.id.txtCustHistory);
            txtComment = itemView.findViewById(R.id.txtCustcomment);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(v, getitem(position), position);
                        }
                    }

                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemLongClick(v, getitem(position), position);
                        }
                    }
                    return true;
                }
            });
            txtCustViewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onViewDetailsClick(getitem(position), position);
                        }
                    }
                }
            });
            txtHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onHistroryClick(getitem(position), position);
                        }
                    }

                }
            });
            txtComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onCommentClick(getitem(position), position);
                        }
                    }
                }
            });
        }
    }

    public CustomerAdapter(Context mCtx, List<Variables.Customer> productList) {
        this.mCtx = mCtx;
        this.customerList = productList;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_customer, null);
        return new CustomerViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {

        Variables.Customer customer = customerList.get(position);

        holder.customerName.setText(customer.customer_name.toUpperCase());
        holder.locationName.setText(customer.customer_location.toUpperCase());
        holder.txtCustViewDetails.setText(" Details");
        if (!is_in_action) {
            holder.customerName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            //holder.customerName.setCompoundDrawablesWithIntrinsicBounds(null, null, mCtx.getResources().getDrawable(R.drawable.ic_action_check), null);
        }
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public void updateList(List<Variables.Customer> list) {
        customerList = list;
        notifyDataSetChanged();
    }

    public Variables.Customer getitem(int position) {
        return customerList.get(position);
    }

    public void setAction(boolean action) {
        is_in_action = action;
    }

    public boolean getAction() {
        return is_in_action;
    }
}
