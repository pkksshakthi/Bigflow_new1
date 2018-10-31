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

public class CustomerFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mCtx;
    private List<Object> customerList;
    private OnItemClickListener mListener;
    private int isHeader = 0;
    private int isItem = 1;

    public interface OnItemClickListener {
        void onItemClick(Variables.Customer item, int position);

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
                            listener.onItemClick(getitem(position), position);
                        }
                    }

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

    public CustomerFilterAdapter(Context mCtx, List<Object> customerList) {
        this.mCtx = mCtx;
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == isHeader) {
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            View view = inflater.inflate(R.layout.list_header, parent, false);
            return new headerViewHolder(view, mListener);
        } else {
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            View view = inflater.inflate(R.layout.layout_customer, parent, false);
            return new CustomerViewHolder(view, mListener);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof headerViewHolder) {
            headerViewHolder headerViewHolder = (headerViewHolder) holder;
            headerViewHolder.headerText.setText(customerList.get(position).toString());

        } else {
            CustomerViewHolder itemViewholder = (CustomerViewHolder) holder;
            Variables.Customer customer = (Variables.Customer) customerList.get(position);

            itemViewholder.customerName.setText(customer.customer_name);
            if (customer.customer_sch_status.equals("OPEND")) {
                itemViewholder.customerName.setTextColor(mCtx.getResources().getColor(R.color.success));
            } else if (customer.customer_sch_status.equals("CLOSED")) {
                itemViewholder.customerName.setTextColor(mCtx.getResources().getColor(R.color.error));
            } else {
                itemViewholder.customerName.setTextColor(mCtx.getResources().getColor(R.color.TextPrimary));
            }

            itemViewholder.locationName.setText(customer.customer_location);
            itemViewholder.txtCustViewDetails.setText(" Details");
        }

    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public void updateList(List<Object> list) {
        customerList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (customerList.get(position) instanceof Variables.Customer) {
            return isItem;
        } else {
            return isHeader;
        }
    }

    public Variables.Customer getitem(int position) {
        return (Variables.Customer) customerList.get(position);
    }


    public class headerViewHolder extends RecyclerView.ViewHolder {
        TextView headerText;

        public headerViewHolder(View itemView, OnItemClickListener mListener) {
            super(itemView);
            headerText = itemView.findViewById(R.id.txtList_header);
            itemView.findViewById(R.id.txtHeaderLine).setVisibility(View.GONE);

        }
    }
}
