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

public class HistroryAdapter extends RecyclerView.Adapter<HistroryAdapter.historyViewholder> {
    Context mContext;
    List<Variables.History> mhistorylist;

    public HistroryAdapter(Context context, List<Variables.History> historyList) {
        mContext = context;
        mhistorylist = historyList;
    }

    @NonNull
    @Override
    public historyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item_history, parent,false);
        return new historyViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull historyViewholder holder, int position) {
        Variables.History history = mhistorylist.get(position);
        holder.followupDate.setText(history.schedule_date);
        holder.employeeName.setText(history.employee_name);
        holder.scheduleType.setText(history.schedule_type);
        holder.followUp.setText(history.followup);
    }

    @Override
    public int getItemCount() {
        return mhistorylist.size();
    }

    public class historyViewholder extends RecyclerView.ViewHolder {
        TextView followupDate, employeeName, scheduleType, followUp;

        public historyViewholder(View itemView) {
            super(itemView);
            followupDate = itemView.findViewById(R.id.txthistory_date);
            employeeName = itemView.findViewById(R.id.txthistory_employeename);
            scheduleType = itemView.findViewById(R.id.txthistory_type);
            followUp = itemView.findViewById(R.id.txthistory_followup);
        }
    }
}
