package models;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.vsolv.bigflow.R;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<Variables.Details> {
    private List<Variables.Details> mDetailsList;
    private Context mContext;

    public CustomSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Variables.Details> detailsList) {
        super(context, resource, detailsList);
        this.mContext = context;
        mDetailsList = detailsList;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomerDDView(position, convertView, parent);
    }

    private View getCustomerDDView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.spinner_dropdown_item, parent, false);
        }
        Variables.Details details = mDetailsList.get(position);
        TextView textView = (TextView) convertView;
        textView.setText(details.data);

        return convertView;
    }

    @Override
    public int getCount() {
        return mDetailsList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomerView(position, convertView, parent);
    }

    public View getCustomerView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }

        TextView textView = (TextView) convertView;
        Variables.Details details = mDetailsList.get(position);
        textView.setText(details.data);

        return convertView;

    }

    @Nullable
    @Override
    public Variables.Details getItem(int position) {
        return mDetailsList.get(position);
    }


    public int getPosition(int gid) {
        for (int i = 0; i < mDetailsList.size(); i++) {
            if (mDetailsList.get(i).gid == gid) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public int getPosition(@Nullable Variables.Details item) {
        return super.getPosition(item);
    }
}
