package models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vsolv.bigflow.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    Context mContext;
    List<Variables.Comment> mCommentList;

    public CommentAdapter(Context context, List<Variables.Comment> commentList) {
        mContext = context;
        mCommentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item_chat, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Variables.Comment details = mCommentList.get(position);
        holder.txtMessage.setText(details.comment_message);
        holder.txtTime.setText(details.employee_name+"@"+details.comment_date);
        if (details.employee_gid != Integer.parseInt(UserDetails.getUser_id())) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.LEFT;
            holder.txtMessage.setLayoutParams(layoutParams);
            holder.txtTime.setLayoutParams(layoutParams);
        } else {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtMessage.setLayoutParams(layoutParams);
            holder.txtTime.setLayoutParams(layoutParams);
        }
    }


    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public void addCommentDetails(Variables.Comment comment) {
        mCommentList.add(0, comment);
        notifyDataSetChanged();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage, txtTime;

        public CommentViewHolder(View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtChatMessage);
            txtTime = itemView.findViewById(R.id.txtChatTime);
        }
    }
}
