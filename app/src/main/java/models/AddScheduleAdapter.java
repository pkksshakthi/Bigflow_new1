package models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.vsolv.bigflow.R;

import java.util.ArrayList;
import java.util.List;


public class AddScheduleAdapter extends BaseAdapter {

    Context _Context;
    int _resource;
    List<Object> _ScheduleTypeList;
    final int isHeader = 0;
    final int isItems = 1;
    boolean isEditable;
    List<Integer> selectedItem, removedItem;

    public AddScheduleAdapter(@NonNull Context context, int resource, List<Object> scheduleTypeList,boolean iseditable) {
        selectedItem = new ArrayList<>();
        removedItem=new ArrayList<>();
        isEditable=iseditable;
        this._Context = context;
        this._resource = resource;
        this._ScheduleTypeList = scheduleTypeList;
    }


    @Override
    public int getItemViewType(int position) {
        if (_ScheduleTypeList.get(position) instanceof Variables.ScheduleType) {
            return isItems;
        } else {
            return isHeader;
        }

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return _ScheduleTypeList.size();
    }

    @Override
    public Object getItem(int position) {
        return _ScheduleTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null) {
            switch (getItemViewType(position)) {
                case isHeader:
                    LayoutInflater inflater = LayoutInflater.from(_Context);
                    view = inflater.inflate(R.layout.list_header, null);
                    break;
                case isItems:
                    LayoutInflater inflater1 = LayoutInflater.from(_Context);
                    view = inflater1.inflate(_resource, null);
                    break;
            }
        }
        switch (getItemViewType(position)) {
            case isHeader:
                TextView headerText = view.findViewById(R.id.txtList_header);
                headerText.setText(_ScheduleTypeList.get(position).toString());
                break;
            case isItems:
                CheckBox scheduletype_name = view.findViewById(R.id.txtList_item);
                Variables.ScheduleType scheduleType = (Variables.ScheduleType) _ScheduleTypeList.get(position);
                scheduletype_name.setText(scheduleType.getSchedule_type_name());
                scheduletype_name.setTag(position);
                if (scheduleType.schedule_gid > 0)
                    scheduletype_name.setChecked(true);
                if (scheduleType.schedule_status != null && scheduleType.schedule_status.equals("CLOSED")) {
                    scheduletype_name.setTextColor(_Context.getResources().getColor(R.color.closed));
                    scheduletype_name.setEnabled(false);
                } else {
                    scheduletype_name.setTextColor(_Context.getResources().getColor(R.color.open));
                }
                if(!isEditable){
                    scheduletype_name.setEnabled(false);
                }
                scheduletype_name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int selectedId = (Integer) buttonView.getTag();
                        if (isChecked) {
                            int index = removedItem.indexOf(new Integer(selectedId));
                            if (index < 0) {
                                selectedItem.add(selectedId);
                            } else {
                                removedItem.remove(new Integer(selectedId));
                            }

                        } else {
                            int index = selectedItem.indexOf(new Integer(selectedId));
                            if (index < 0) {
                                removedItem.add(selectedId);
                            } else {
                                selectedItem.remove(new Integer(selectedId));
                            }

                        }
                    }
                });

                break;
        }


        return view;
    }

    public List<Integer> getSelectedList() {
        return selectedItem;
    }

    public List<Integer> getRemovedList() {
        return removedItem;
    }
}
