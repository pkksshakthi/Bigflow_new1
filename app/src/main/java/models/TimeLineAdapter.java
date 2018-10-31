package models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;
import com.vsolv.bigflow.R;

import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.TimeLineViewHolder> {


    private final Context mContext;
    private final List<Variables.Timeline> mTimeLines;

    public TimeLineAdapter(Context context, List<Variables.Timeline> timelines) {

        mContext = context;
        mTimeLines = timelines;
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    @NonNull
    @Override
    public TimeLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_timeline, parent,false);
        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeLineViewHolder holder, int position) {
        Variables.Timeline timeline = mTimeLines.get(position);
        holder.title.setText(timeline.title);
        holder.subTitle.setText(timeline.subtitle);

        if (timeline.status == Variables.Timeline.Status.ACTIVE) {
            holder.txtTimeLine.setMarker(mContext.getResources().getDrawable(R.drawable.ic_marker_active));
        } else if (timeline.status == Variables.Timeline.Status.INACTIVE) {
            holder.txtTimeLine.setMarker(mContext.getResources().getDrawable(R.drawable.ic_marker_inactive));
        } else if (timeline.status == Variables.Timeline.Status.REJECTED) {
            holder.txtTimeLine.setMarker(mContext.getResources().getDrawable(R.drawable.ic_marker_reject));
        } else {
            holder.txtTimeLine.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.colorPrimary));
        }
    }

    @Override
    public int getItemCount() {
        return mTimeLines.size();
    }

    public class TimeLineViewHolder extends RecyclerView.ViewHolder {
        TextView title, subTitle;
        TimelineView txtTimeLine;

        public TimeLineViewHolder(View itemView, int viewType) {
            super(itemView);
            title = itemView.findViewById(R.id.txtTitle);
            subTitle = itemView.findViewById(R.id.txtSubTitle);
            txtTimeLine = itemView.findViewById(R.id.time_marker);
            txtTimeLine.initLine(viewType);
        }
    }
}
