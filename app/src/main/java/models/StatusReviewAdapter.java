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

import constant.Constant;

public class StatusReviewAdapter extends RecyclerView.Adapter<StatusReviewAdapter.CustomerViewHolder> {
    private Context mCtx;
    private List<Variables.StatusReview> StausList;
    private OnItemClickListener mListener;

    public StatusReviewAdapter(Context mCtx, List<Variables.StatusReview> List) {
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

        final Variables.StatusReview statusReview = StausList.get(position);

        holder.customerName.setText(statusReview.customer_name);
        holder.EmployeeName.setText(statusReview.employee_name);
        holder.sch_type.setText(statusReview.schedule_type);
        holder.sch_date.setText(Common.convertDateString(statusReview.schedule_date, "yyyy-MM-dd", Constant.date_display_format));
        String followup = statusReview.followup_reason.equals("null") ? "" : " - " + statusReview.followup_reason;
        holder.sch_status.setText(statusReview.schedule_status + followup);
        if (!statusReview.followup_date.equals("null")) {
            holder.followup_date.setText(statusReview.followup_date);
        } else {
            holder.followup_date.setText("");
        }

        if (statusReview.schedule_status.equals("CLOSED") && statusReview.followup_reason.equals("Sales")) {
            holder.sch_type.setTextColor(mCtx.getResources().getColor(R.color.colorAccent));
        } else {
            holder.sch_type.setTextColor(mCtx.getResources().getColor(R.color.TextSecondary));
        }
        if (!statusReview.review_remarks.equals("null")) {
            holder.remark.setText("* " + statusReview.review_remarks);
        } else {
            holder.remark.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return StausList.size();
    }

    public void updateList(List<Variables.StatusReview> list) {
        StausList = list;
        notifyDataSetChanged();
    }

    public Variables.StatusReview getitem(int position) {
        return StausList.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(Variables.StatusReview item, int position);

        void TypeClick(Variables.StatusReview item, int position);


    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, EmployeeName, sch_type, sch_date, sch_status, followup_date, remark;

        public CustomerViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            customerName = itemView.findViewById(R.id.txtCustomerName);
            EmployeeName = itemView.findViewById(R.id.txtEmployeeName);
            sch_type = itemView.findViewById(R.id.txtsch_type);
            sch_date = itemView.findViewById(R.id.txtsch_date);
            sch_status = itemView.findViewById(R.id.txtStatus);
            followup_date = itemView.findViewById(R.id.txtFollowupDate);
            remark = itemView.findViewById(R.id.txtRemark);

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
            sch_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.TypeClick(getitem(position), position);
                        }
                    }
                }
            });


        }
    }
}

