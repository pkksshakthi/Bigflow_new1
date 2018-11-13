package view.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.vsolv.bigflow.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DataBase.DataBaseHandler;
import DataBase.GetData;
import constant.Constant;
import models.AddScheduleAdapter;
import models.Common;
import models.CustomerFilterAdapter;
import models.ListAdapter;
import models.Variables;
import presenter.NetworkResult;
import view.activity.CommentActivity;
import view.activity.CustomerDetailActivity;
import view.activity.FilterAddScheduleActivity;
import view.activity.HistoryActivity;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class AddScheduleFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View fragmentView;
    private RecyclerView recyclerView;
    private TextView empty_view, filter, employee;
    private ProgressDialog progressDialog;
    private List<Object> mCustomerFilter;
    private CustomerFilterAdapter adapter;
    private int filterCode = 101;
    private GetData getData;
    private List<Object> scheduleTypeList;
    private AddScheduleAdapter scheduleForAdapter;
    private ListView listView;
    private AlertDialog alertDialog, employeeDialog;
    private List<Variables.Details> empDetailList;
    private int selected_employee_gid;
    private List<Integer> employee_gid, route_gid, cluster_gid;
    private SearchView customerSearch;

    public AddScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddSchedule.
     */
    // TODO: Rename and change types and number of parameters
    public static AddScheduleFragment newInstance(String param1, String param2) {
        AddScheduleFragment fragment = new AddScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Add Schedule");
        View view = inflater.inflate(R.layout.fragment_add_schedule, container, false);
        fragmentView = view;
        loadView();
        initializeView();
        loadData();
        return view;

    }

    private void loadView() {
        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.customer_AddSchedule_RecyclerView);
        customerSearch = fragmentView.findViewById(R.id.customer_search);
        empty_view = fragmentView.findViewById(R.id.addSchedule_Empty_view);
        filter = fragmentView.findViewById(R.id.txtAddSchedule_Filter);
        employee = fragmentView.findViewById(R.id.txtEmployee);
    }

    private void initializeView() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        customerSearch.setQueryHint("Search");
        mCustomerFilter = new ArrayList<>();
        filter.setOnClickListener(this);
        employee.setOnClickListener(this);
        getData = new GetData(getActivity());
        empDetailList = new ArrayList<>();
        employee_gid = new ArrayList<>();
        route_gid = new ArrayList<>();
        cluster_gid = new ArrayList<>();
        customerSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void loadData() {
        if (!Common.isOnline(getContext())) {
            Snackbar.with(getActivity(), null)
                    .type(Type.WARNING)
                    .message("Please Check Internet Connection.")
                    .duration(Duration.SHORT)
                    .fillParent(true)
                    .textAlign(Align.LEFT)
                    .show();
            setVisibility(View.GONE, View.GONE, View.VISIBLE);
            return;
        }
        progressDialog.show();
        getData.LoadAddScheduleFilteredData(employee_gid, route_gid, cluster_gid, new Date()
                , new NetworkResult() {
                    @Override
                    public void handlerResult(String result) {
                        mCustomerFilter = getData.CustomerFilterList(1);
                        empDetailList = getData.EmployeeFilterList();
                        if (empDetailList.size() == 1) {
                            selected_employee_gid = empDetailList.get(0).gid;
                            employee.setText(empDetailList.get(0).data);
                        }
                        setAdapter();
                        progressDialog.cancel();
                    }

                    @Override
                    public void handlerError(String result) {
                        progressDialog.cancel();
                    }
                });

    }

    public void setAdapter() {

        adapter = new CustomerFilterAdapter(getActivity(), mCustomerFilter);
        recyclerView.setAdapter(adapter);

        adapter.setOnclickListener(new CustomerFilterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Variables.Customer item, int position) {
                if (selected_employee_gid > 0) {
                    getScheduleType(item, new Date());
                } else {
                    Toast.makeText(getContext(), "Please choose employee.!", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onViewDetailsClick(Variables.Customer item, int position) {
                Intent intent = new Intent(getActivity(), CustomerDetailActivity.class);
                intent.putExtra("customer_gid", item.customer_gid);
                intent.putExtra("customer_name", item.customer_name);
                startActivity(intent);
            }

            @Override
            public void onHistroryClick(Variables.Customer item, int position) {
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                intent.putExtra("customer_gid", item.customer_gid);
                intent.putExtra("customer_name", item.customer_name);
                startActivity(intent);
            }

            @Override
            public void onCommentClick(Variables.Customer item, int position) {
                Intent intent = new Intent(getActivity(), CommentActivity.class);
                intent.putExtra("customer_gid", item.customer_gid);
                intent.putExtra("customer_name", item.customer_name);
                startActivity(intent);
            }

        });

        if (adapter.getItemCount() == 0) {
            empty_view.setText("" + getActivity().getResources().getString(R.string.loading));
            setVisibility(View.GONE, View.VISIBLE, View.GONE);
        } else {
            setVisibility(View.VISIBLE, View.GONE, View.GONE);
        }
    }

    public void filter(String text) {
        List<Object> temp = new ArrayList();
        if (mCustomerFilter.size() > 0) {
            for (Object d : mCustomerFilter) {
                if (d instanceof Variables.Customer) {
                    if (((Variables.Customer)d).customer_name.toLowerCase().replaceAll("\\s+", "").contains(text.toLowerCase().replaceAll("\\s+", ""))) {
                        temp.add(d);
                    }
                } else {
                    temp.add(d);
                }

            }
            adapter.updateList(temp);
        }


    }

    public void setVisibility(int recycleView, int emptyView, int reloadView) {
        //linearLayout.setVisibility(recycleView);
        empty_view.setVisibility(emptyView);
        //reload.setVisibility(reloadView);
    }

    public void getScheduleType(final Variables.Customer pfCustomer, Date date) {
        progressDialog.show();
        scheduleTypeList = getData.ScheduledScheduleType(pfCustomer.customer_gid, Common.convertDateString(new Date(), "yyyy-MM-dd"), new NetworkResult() {
            @Override
            public void handlerResult(String result) {
                createDialog(pfCustomer);
                progressDialog.cancel();
            }

            @Override
            public void handlerError(String result) {
                progressDialog.cancel();
            }
        });
    }

    public void createDialog(final Variables.Customer pfCustomer) {
        boolean isEditable = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Schedule");
        View customView = LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog_list, null, false);

        if (pfCustomer.isEditable.equals("N")) {
            isEditable = false;
        }
        scheduleForAdapter = new AddScheduleAdapter(getContext(), R.layout.list_add_schedule_item, scheduleTypeList, isEditable);
        listView = (ListView) customView.findViewById(R.id.listView_dialog);
        listView.setAdapter(scheduleForAdapter);
        builder.setView(customView);
        alertDialog = builder.create();
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<Integer> addSchedule = new ArrayList<>();
                List<Integer> removeSchedule = new ArrayList<>();
                List<Integer> selectedList = scheduleForAdapter.getSelectedList();
                List<Integer> removedList = scheduleForAdapter.getRemovedList();
                String type = "Add";
                List<Integer> customerList = new ArrayList<>();
                customerList.add(pfCustomer.customer_gid);
                for (int i = 0; i < selectedList.size(); i++) {
                    Variables.ScheduleType scheduleType = (Variables.ScheduleType) scheduleTypeList.get(selectedList.get(i));

                    if (scheduleType.schedule_gid == 0) {
                        addSchedule.add(scheduleType.schedule_type_id);
                    }
                }
                for (int i = 0; i < removedList.size(); i++) {
                    Variables.ScheduleType scheduleType = (Variables.ScheduleType) scheduleTypeList.get(removedList.get(i));

                    if (scheduleType.schedule_gid > 0) {
                        removeSchedule.add(scheduleType.schedule_gid);
                    }
                }
                if (removeSchedule.size() > 0)
                    type = "Remove";

                if (addSchedule.size() == 0 && removeSchedule.size() == 0) {
                    return;
                } else {
                    saveDetails(type, customerList, addSchedule, removeSchedule);
                }

            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }


    public void saveDetails(String type, List<Integer> customer_gid, List<Integer> scheduleType_gid, List<Integer> schedule_gid) {

        JSONArray schedulelist = new JSONArray();
        for (int i = 0; i < customer_gid.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("Customer_Gid", customer_gid.get(i));
                jsonObject.put("Schedule_Type_Gid", new JSONArray(scheduleType_gid));

                if (type.equals("Remove")) {
                    jsonObject.put("Is_Remove", "Y");
                } else {
                    jsonObject.put("Is_Remove", "N");
                }

                jsonObject.put("Schedule_Gid", new JSONArray(schedule_gid));
                jsonObject.put("Remark", "test");
                schedulelist.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        getData.SetScheduleSingle(selected_employee_gid, schedulelist, new Date(), new NetworkResult() {
            @Override
            public void handlerResult(String result) {
                if (result.equals("SUCCESS")) {
                    Toast.makeText(getContext(), "Schedule Saved Successfully.!", Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();
                } else {

                    Snackbar.with(getActivity(), null)
                            .type(Type.WARNING)
                            .message("Schedule not Saved Successfully.!")
                            .duration(Duration.SHORT)
                            .fillParent(true)
                            .textAlign(Align.LEFT)
                            .show();
                }
            }

            @Override
            public void handlerError(String result) {
                Snackbar.with(getActivity(), null)
                        .type(Type.ERROR)
                        .message("Schedule not Saved Successfully.!")
                        .duration(Duration.SHORT)
                        .fillParent(true)
                        .textAlign(Align.LEFT)
                        .show();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtAddSchedule_Filter:
                Intent intent = new Intent(getActivity(), FilterAddScheduleActivity.class);
                startActivityForResult(intent, filterCode);
                break;
            case R.id.txtEmployee:
                createEmployeeDialog();
                break;

        }
    }

    private void createEmployeeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Schedule");
        View employeeView = LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog_list, null, false);
        final ListAdapter listAdapter = new ListAdapter(getActivity(), empDetailList);
        ListView listView = (ListView) employeeView.findViewById(R.id.listView_dialog);

        listView.setAdapter(listAdapter);
        builder.setView(employeeView);
        employeeDialog = builder.create();
        employeeDialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_employee_gid = empDetailList.get(position).gid;
                employee.setText(empDetailList.get(position).data);
                employeeDialog.dismiss();
                employee_gid = new ArrayList<>();
                employee_gid.add(selected_employee_gid);
                loadData();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == filterCode){

            if (resultCode == RESULT_OK){

               // tvResultCode.setText("RESULT_OK");

            }else if(resultCode == RESULT_CANCELED){

               // tvResultCode.setText("RESULT_CANCELED");

            }

        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}