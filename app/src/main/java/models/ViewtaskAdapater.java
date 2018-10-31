package models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vsolv.bigflow.R;

import java.util.List;

public class ViewtaskAdapater extends RecyclerView.Adapter<ViewtaskAdapater.ViewtaskViewHolder>  {
    private Context mCtx;
    private List<Variables.Viewtask_customer> customerList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Variables.Viewtask_customer item,int position);

    }

    public void setOnclickListener(ViewtaskAdapater.OnItemClickListener Listener) {
        mListener = Listener;
    }

    public class ViewtaskViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, Date,Type,For,Status;

        public ViewtaskViewHolder(View itemView, final ViewtaskAdapater.OnItemClickListener listener) {
            super(itemView);
            customerName = itemView.findViewById(R.id.txt_sales_CustomerName);
            Date = itemView.findViewById(R.id.txt_Date);
            Type=itemView.findViewById(R.id.txt_Type);
            For=itemView.findViewById(R.id.txt_For);
            Status=itemView.findViewById(R.id.txt_Staus);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener!=null){
                        int position =getAdapterPosition();

                        if (position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(getitem(position),position);
                        }
                    }

                }
            });


                }

        }


    public ViewtaskAdapater(Context mCtx, List<Variables.Viewtask_customer> productList) {
        this.mCtx = mCtx;
        this.customerList = productList;
    }

    @NonNull
    @Override
    public ViewtaskAdapater.ViewtaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_sales_customer, null);
        return new ViewtaskAdapater.ViewtaskViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewtaskAdapater.ViewtaskViewHolder holder, int position) {

        Variables.Viewtask_customer customer = customerList.get(position);

        holder.customerName.setText(customer.getCust_name());
        holder.Date.setText(customer.getDate());
       holder.Type.setText(customer.getType());
       holder.For.setText(customer.getComplete_for());
       holder.Status.setText(customer.getStatus());

           }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public void updateList(List<Variables.Viewtask_customer> list){
        customerList = list;
        notifyDataSetChanged();
    }
    public Variables.Viewtask_customer getitem(int position){
        return customerList.get(position);
    }
}

