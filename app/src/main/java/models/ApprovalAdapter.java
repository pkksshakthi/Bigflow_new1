package models;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vsolv.bigflow.R;

import java.util.List;

public class ApprovalAdapter extends RecyclerView.Adapter<ApprovalAdapter.CustomerViewHolder> {
    private Context mCtx;
    private List<Variables.Approval_List> StausList;
    private OnItemClickListener mListener;

    public ApprovalAdapter(Context mCtx, List<Variables.Approval_List> List) {
        this.mCtx = mCtx;
        this.StausList = List;
    }

    public void setOnclickListener(OnItemClickListener Listener) {
        mListener = Listener;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_approval, null);
        return new CustomerViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomerViewHolder holder, int position) {

        final Variables.Approval_List customer = StausList.get(position);

        holder.customerName.setText(customer.getCustomername());
        holder.EmployeeName.setText(customer.getEmployeename());
        holder.ViewSale.setText("View Sales");

    }

    @Override
    public int getItemCount() {
        return StausList.size();
    }

    public void updateList(List<Variables.Approval_List> list) {
        StausList = list;
        notifyDataSetChanged();
    }

    public Variables.Approval_List getitem(int position) {
        return StausList.get(position);

    }

    public void removeAt(int position) {
        StausList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, StausList.size());
    }

    public interface OnItemClickListener {
        void onItemClick(Variables.Approval_List item, int position);

        void onViewSalesClick(Variables.Approval_List item, int position);

        void onViewoutClick(Variables.Approval_List item, int position);

        void onViewPdcClick(Variables.Approval_List item, int position);
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, EmployeeName, ViewSale, ViewOutstndng, ViewPDC;
        CardView post_card_view;

        private View view;

        public CustomerViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            view = itemView;
            post_card_view = itemView.findViewById(R.id.post_card_view);
            customerName = itemView.findViewById(R.id.txtCustomerName);
            EmployeeName = itemView.findViewById(R.id.txtEmployeeName);
            ViewSale = itemView.findViewById(R.id.txtCustViewSales);
            ViewOutstndng = itemView.findViewById(R.id.txtCustViewOutstndg);
            ViewPDC = itemView.findViewById(R.id.txtCustViewpdc);


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

            ViewSale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onViewSalesClick(getitem(position), position);
                        }
                    }
                }
            });

            ViewOutstndng.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onViewoutClick(getitem(position), position);
                        }
                    }
                }
            });

            ViewPDC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onViewPdcClick(getitem(position), position);
                        }
                    }
                }
            });

        }
    }
}

