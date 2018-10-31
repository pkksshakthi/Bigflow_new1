package models;

import android.content.Context;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vsolv.bigflow.R;

import java.util.List;

public class ListAdapter extends BaseAdapter {

    Context mContext;
    List<Variables.Details> mListDetails;

    public ListAdapter(Context context, List<Variables.Details> listDetails) {
        mContext = context;
        mListDetails = listDetails;
    }

    @Override
    public int getCount() {
        return mListDetails.size();
    }

    @Override
    public Object getItem(int position) {
        return mListDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        int as = mListDetails.get(position).gid;
        return mListDetails.get(position).gid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_item, null);
        }
        TextView itemText = convertView.findViewById(R.id.txtList_item);
        itemText.setText(mListDetails.get(position).data);
        if (mListDetails.get(position).dataColor != 0) {
            itemText.setTextColor(mListDetails.get(position).dataColor);
        } else {
            itemText.setTextColor(mContext.getResources().getColor(R.color.TextSecondary));
        }
        return convertView;
    }
}
