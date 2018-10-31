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

public class StatusReviewAdapter extends RecyclerView.Adapter<StatusReviewAdapter.CustomerViewHolder> {
    private Context mCtx;
    private List<Variables.StatusReview_List> StausList;
    private OnItemClickListener mListener;

    public StatusReviewAdapter(Context mCtx, List<Variables.StatusReview_List> List) {
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
        View view = inflater.inflate(R.layout.layout_statusreview, null);
        return new CustomerViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomerViewHolder holder, int position) {

        final Variables.StatusReview_List customer = StausList.get(position);

        holder.customerName.setText(customer.getCustomername());
        holder.EmployeeName.setText(customer.getEmployeename());
        holder.type.setText(customer.getType());
        if(customer.getFollowup() == "Sales" ){
            holder.followup.setText(customer.getFollowup());
        }else{
            holder.followup.setVisibility(View.GONE);
        }

        if (customer.getStatus().equals("OPEND")) {
            holder.customerName.setTextColor(Color.parseColor("#326914"));
           // ((CardView) holder.post_card_view).setBackgroundResource(R.color.lightgreen);
        } else {
            holder.customerName.setTextColor(Color.parseColor("#9b2c1f"));

            //    ((CardView) holder.post_card_view).setBackgroundResource(R.color.lightred);
        }

    }

    @Override
    public int getItemCount() {
        return StausList.size();
    }

    public void updateList(List<Variables.StatusReview_List> list) {
        StausList = list;
        notifyDataSetChanged();
    }

    public Variables.StatusReview_List getitem(int position) {
        return StausList.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(Variables.StatusReview_List item, int position);
        void TypeClick(Variables.StatusReview_List item, int position);


    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, EmployeeName, type,followup;
        CardView post_card_view;
        ImageView Approveimg;
        private View view;

        public CustomerViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            view = itemView;
            post_card_view = itemView.findViewById(R.id.post_card_view);
            customerName = itemView.findViewById(R.id.txtCustomerName);
            EmployeeName = itemView.findViewById(R.id.txtEmployeeName);
            type = itemView.findViewById(R.id.txttype);
            followup = itemView.findViewById(R.id.txtfollowup);


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
            followup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position =getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.TypeClick(getitem(position), position);
                        }
                    }
                }
            });


        }
    }
}

