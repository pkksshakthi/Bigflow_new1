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

public class EmployeeTrackingAdapter extends RecyclerView.Adapter<EmployeeTrackingAdapter.ViewHolder> {
    List<Variables.Employee> mEmployeeList;
    Context mContext;
    private OnItemClickListener mListener;

    public EmployeeTrackingAdapter(Context context, List<Variables.Employee> employeeList) {
        mEmployeeList = employeeList;
        mContext = context;
    }

    public interface OnItemClickListener {
        void onItemClick(Variables.Employee item, int position);

        void onDetailsClick(Variables.Employee item, int position);

        void onMapsClick(Variables.Employee item, int position);

    }

    public void setOnclickListener(OnItemClickListener Listener) {
        mListener = Listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item_emptracking, parent,false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Variables.Employee employee = mEmployeeList.get(position);

        holder.employeeName.setText(employee.employee_name);

    }

    @Override
    public int getItemCount() {
        return mEmployeeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView employeeName;
        ImageView details, maps;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            employeeName = itemView.findViewById(R.id.trackingEmployeeName);
            details = itemView.findViewById(R.id.trackingDetails);
            maps = itemView.findViewById(R.id.trackingMaps);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(getItem(position), position);
                        }
                    }

                }
            });
            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDetailsClick(getItem(position), position);
                        }
                    }
                }
            });
            maps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onMapsClick(getItem(position), position);
                        }
                    }
                }
            });
        }
    }

    public Variables.Employee getItem(int position) {
        return mEmployeeList.get(position);
    }
}
