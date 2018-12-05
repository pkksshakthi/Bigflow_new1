package view.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.vsolv.bigflow.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import DataBase.GetData;
import constant.Constant;
import models.Common;
import models.CustomDialogListener;
import models.CustomerAdapter;
import models.ListAdapter;
import models.ScheduleForAdapter;
import models.UserDetails;
import models.Variables;
import presenter.NetworkResult;
import view.activity.CollectionActivity;
import view.activity.CommentActivity;
import view.activity.CustomerDetailActivity;
import view.activity.DashBoardActivity;
import view.activity.HistoryActivity;
import view.activity.MapsActivity;
import view.activity.SalesActivity;
import view.activity.ServiceActivity;
import view.activity.StatusActivity;
import view.activity.StockActivity;


public class DayScheduleFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View fragmentView;
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private TextView empty_view, reload;
    private SearchView customerSearch;
    private Bundle sessiondata;
    private List<Variables.Customer> customerList;
    private ProgressDialog progressDialog;
    private GetData getData;
    private CustomerAdapter adapter;
    private ScheduleForAdapter scheduleForAdapter;
    private ListView listView;
    private List<Object> scheduleTypeList;
    private AlertDialog alertDialog;
    private FloatingActionButton fabNext;
    private FloatingActionButton fabPrevious;
    private Date selectedDate;
    private List<Variables.Customer> selectionList;
    private List<Integer> cust_gid;
    private DatePickerDialog.OnDateSetListener datePickerListener;
    private TextView resch_date;

    public DayScheduleFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
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
    public static DayScheduleFragment newInstance(String param1, String param2) {
        DayScheduleFragment fragment = new DayScheduleFragment();
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

        getActivity().setTitle(Constant.title_day_schedule);
        setHasOptionsMenu(true);
        fragmentView = inflater.inflate(R.layout.fragment_day_schedule, container, false);
        fragmentView.setFocusableInTouchMode(true);
        fragmentView.requestFocus();
        fragmentView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (adapter.getAction()) {
                        fabNext.show();
                        Integer a = new Date().compareTo(selectedDate);
                        if (a < 0)
                            fabPrevious.show();
                        Toolbar toolbar = ((DashBoardActivity) getActivity()).findViewById(R.id.toolbar);
                        toolbar.getMenu().clear();
                        toolbar.inflateMenu(R.menu.dash_board);
                        adapter.setAction(false);
                        adapter.notifyDataSetChanged();
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        });
        loadView();
        initializeView();
        loadData();
        return fragmentView;
    }

    private void loadView() {
        linearLayout = (LinearLayout) fragmentView.findViewById(R.id.linearDirect);
        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.RV_DaySchedule);
        empty_view = fragmentView.findViewById(R.id.empty_view);
        customerSearch = fragmentView.findViewById(R.id.customer_search);
        reload = fragmentView.findViewById(R.id.custReload);
        fabNext = fragmentView.findViewById(R.id.fabNext);
        fabPrevious = fragmentView.findViewById(R.id.fabPrevious);
    }

    private void initializeView() {
        sessiondata = new Bundle();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        customerList = new ArrayList<>();
        customerSearch.setQueryHint("Search");
        selectedDate = new Date();
        fabPrevious.hide();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        datePickerListener = setDatepicker();
        getData = new GetData(getActivity());
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
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date currentdate = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(selectedDate);
                c.add(Calendar.DATE, 1);
                Date temp = c.getTime();
                Integer a = currentdate.compareTo(temp);
                if (a < 0)
                    fabPrevious.show();
                selectedDate = temp;
                getActivity().setTitle(Constant.title_day_schedule + "(" + Common.convertDateString(selectedDate, Constant.date_display_format) + ")");
                loadData();
                //Toast.makeText(getActivity(), "hello", Toast.LENGTH_LONG).show();
            }
        });
        fabPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date currentdate = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(selectedDate);
                c.add(Calendar.DATE, -1);
                Date temp = c.getTime();
                Integer a = currentdate.compareTo(temp);
                if (a == 1)
                    fabPrevious.hide();
                selectedDate = temp;
                getActivity().setTitle(Constant.title_day_schedule + "(" + Common.convertDateString(selectedDate, Constant.date_display_format) + ")");
                loadData();
            }
        });
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
    }

    private void loadData() {
        progressDialog.show();
        if (!Common.isOnline(getContext())) {
            Snackbar.with(getActivity(), null)
                    .type(Type.WARNING)
                    .message(getActivity().getResources().getString(R.string.check_internet_connection))
                    .duration(Duration.SHORT)
                    .fillParent(true)
                    .textAlign(Align.LEFT)
                    .show();
            setVisibility(View.GONE, View.GONE, View.VISIBLE);
            progressDialog.cancel();
            return;
        }

        customerList = getData.ScheduledCustomerList(UserDetails.getUser_id(), Common.convertDateString(selectedDate, "yyyy-MM-dd"), new NetworkResult() {
            @Override
            public void handlerResult(String result) {
                setAdapter();
                progressDialog.cancel();
            }

            @Override
            public void handlerError(String result) {

            }
        });

    }

    public void setAdapter() {

        adapter = new CustomerAdapter(getActivity(), customerList);
        recyclerView.setAdapter(adapter);


        adapter.setOnclickListener(new CustomerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Variables.Customer item, int position) {
                if (adapter.getAction()) {
                    int index = selectionList.indexOf(item);
                    TextView cust_name = (TextView) view.findViewById(R.id.txtCustomerName);
                    if (index < 0) {
                        selectionList.add(item);
                        cust_name.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(R.drawable.ic_action_check), null);
                    } else {
                        selectionList.remove(item);
                        cust_name.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    }

                } else {
                    Date currentdate = new Date();
                    Integer a = currentdate.compareTo(selectedDate);
                    if (a == 1) {
                        getScheduleType(item.customer_gid);
                    } else {
                        Toast.makeText(getContext(), "You Can't edit this date.!", Toast.LENGTH_LONG).show();
                    }
                }


            }

            @Override
            public void onItemLongClick(View view, Variables.Customer item, int position) {
                if (!adapter.getAction()) {
                    adapter.setAction(true);
                    fabNext.hide();
                    fabPrevious.hide();
                    Toolbar toolbar = ((DashBoardActivity) getActivity()).findViewById(R.id.toolbar);
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.day_schedule);
                    selectionList = new ArrayList<>();
                    selectionList.add(item);
                    adapter.notifyDataSetChanged();
                    TextView cust_name = (TextView) view.findViewById(R.id.txtCustomerName);
                    cust_name.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(R.drawable.ic_action_check), null);
                } else {
                    selectionList.add(item);
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
            empty_view.setText("" + getActivity().getResources().getString(R.string.no_data_available));
            setVisibility(View.GONE, View.VISIBLE, View.GONE);
        } else {
            setVisibility(View.VISIBLE, View.GONE, View.GONE);
        }
    }

    public void getScheduleType(final int customer_gid) {
        progressDialog.show();
        scheduleTypeList = getData.ScheduledScheduleType(customer_gid, Common.convertDateString(new Date(), "yyyy-MM-dd"), new NetworkResult() {
            @Override
            public void handlerResult(String result) {
                sessiondata = new Bundle();
                sessiondata.putInt(Constant.key_customer_gid, customer_gid);
                createDialog();
                progressDialog.cancel();
            }

            @Override
            public void handlerError(String result) {
                progressDialog.cancel();
            }
        });
    }

    public void createDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Schedule");
        View customView = LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog_list, null, false);
        scheduleForAdapter = new ScheduleForAdapter(getContext(), R.layout.list_item, scheduleTypeList);
        listView = (ListView) customView.findViewById(R.id.listView_dialog);
        listView.setAdapter(scheduleForAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (scheduleTypeList.get(position) instanceof Variables.ScheduleType) {

                    Variables.ScheduleType scheduleType = (Variables.ScheduleType) scheduleTypeList.get(position);
                    sessiondata.putInt(Constant.key_schedule_gid, scheduleType.schedule_gid);
                    sessiondata.putInt(Constant.key_sch_type_gid, scheduleType.schedule_type_id);
                    switch (scheduleType.schedule_type_name) {
                        case "BOOKING":
                            gotoSales(scheduleType);
                            break;
                        case "COLLECTION":
                            if (scheduleType.schedule_gid == 0 || scheduleType.schedule_status.equals("OPEND")) {
                                gotoActivity(CollectionActivity.class);
                            } else {
                                Toast.makeText(getContext(), "Your Schedule Closed", Toast.LENGTH_LONG).show();
                            }
                            break;
                        case "SERVICE":
                            if (scheduleType.schedule_gid == 0 || scheduleType.schedule_status.equals("OPEND")) {
                                gotoActivity(ServiceActivity.class);
                            } else {
                                Toast.makeText(getContext(), "Your Schedule Closed", Toast.LENGTH_LONG).show();
                            }

                            break;
                        case "STOCK":
                            if (scheduleType.schedule_gid == 0 || scheduleType.schedule_status.equals("OPEND")) {
                                gotoActivity(StockActivity.class);
                            } else {
                                Toast.makeText(getContext(), "Your Schedule Closed", Toast.LENGTH_LONG).show();
                            }

                            break;
                        case "OTHERS":
                            gotoActivity(null);
                            break;
                    }

                    alertDialog.dismiss();
                }
            }
        });
        builder.setView(customView);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void gotoSales(Variables.ScheduleType scheduleType) {
        List<Variables.Details> detailsList = (List<Variables.Details>) scheduleType.schedule_details;
        if (detailsList != null) {
            Variables.Details details = new Variables.Details();
            details.data = "New Sales";
            details.gid = 0;
            details.dataColor = getActivity().getResources().getColor(R.color.colorAccent);
            detailsList.add(detailsList.size(), details);
            alertDialog.cancel();
            final AlertDialog salesDialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select Invoice");
            View salesView = LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog_list, null, false);

            final ListAdapter listAdapter = new ListAdapter(getActivity(), detailsList);
            ListView listView = (ListView) salesView.findViewById(R.id.listView_dialog);

            listView.setAdapter(listAdapter);
            builder.setView(salesView);
            salesDialog = builder.create();
            salesDialog.show();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Variables.Details details_toactivity = (Variables.Details) listAdapter.getItem(position);
                    sessiondata.putInt(Constant.key_soheader_gid, details_toactivity.gid);
                    salesDialog.cancel();
                    if (details_toactivity.status != "CANCELLED") {
                        gotoActivity(SalesActivity.class);
                    }
                }
            });


        } else {
            Class aClass = SalesActivity.class;
            gotoActivity(aClass);
        }
    }

    public void gotoActivity(Class aClass) {
        if (aClass != null) {
            Intent intent = new Intent(getActivity(), aClass);
            intent.putExtras(sessiondata);
            startActivity(intent);

        } else {
            Toast.makeText(getContext(), "This Module implement into new version.", Toast.LENGTH_LONG).show();
        }
    }

    public void setVisibility(int recycleView, int emptyView, int reloadView) {
        linearLayout.setVisibility(recycleView);
        empty_view.setVisibility(emptyView);
        reload.setVisibility(reloadView);
    }

    public void filter(String text) {
        List<Variables.Customer> temp = new ArrayList();
        if (customerList.size() > 0) {
            for (Variables.Customer d : customerList) {

                if (d.customer_name.toLowerCase().replaceAll("\\s+", "").contains(text.toLowerCase().replaceAll("\\s+", ""))) {
                    temp.add(d);
                }
            }
            adapter.updateList(temp);
        }


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

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.DSDelete) {

            if (selectionList.size() > 0) {
                cust_gid = new ArrayList<>();
                for (int i = 0; i < selectionList.size(); i++) {
                    cust_gid.add(selectionList.get(i).customer_gid);
                }
                createRescheduleDialog();
            } else {
                Toast.makeText(getActivity(), "Select any one.!", Toast.LENGTH_LONG).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void createRescheduleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Reschedule");
        View customView = LayoutInflater.from(getActivity()).inflate(R.layout.alert_reschedule, null, false);
        resch_date = customView.findViewById(R.id.txtRSDate);
        resch_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getResources().getString(R.string.choose_date).equals(resch_date.getText().toString()))
                    resch_date.setText(Common.convertDateString(new Date(), Constant.date_display_format));
                Variables.calendarDate cal = new Variables.calendarDate(resch_date.getText().toString());
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), datePickerListener, cal.getYear(), cal.getMonth(), cal.getDayofmonth());
                datePickerDialog.getDatePicker().setTag(R.id.txtASFFDate);
                datePickerDialog.show();
            }
        });
        builder.setView(customView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
        CustomDialogListener dialogListener = new CustomDialogListener(alertDialog);
        dialogListener.setClickListener(new presenter.CustomDialogListener() {
            @Override
            public void OnClickPositive(View view, AlertDialog dialog) {
                EditText remark = dialog.findViewById(R.id.etxRSRemark);
                if (remark.getText().toString().trim().length() == 0) {
                    remark.setError("*");
                    return;
                }
                setRescheduleDetails(resch_date.getText().toString().trim(), remark.getText().toString().trim());
            }
        });
    }

    private void setRescheduleDetails(String date, String remark) {
        getData.SetReschedule(cust_gid, date, Common.convertDateString(selectedDate, "dd/MM/yyyy"), remark, new NetworkResult() {
            @Override
            public void handlerResult(String result) {
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }

            @Override
            public void handlerError(String result) {
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        fabNext.show();
        fabPrevious.show();
        Toolbar toolbar = ((DashBoardActivity) getActivity()).findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.dash_board);
        adapter.setAction(false);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        fabNext.show();
        fabPrevious.show();
        Toolbar toolbar = ((DashBoardActivity) getActivity()).findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.dash_board);
        adapter.setAction(false);
        super.onDestroy();
    }

    private DatePickerDialog.OnDateSetListener setDatepicker() {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                resch_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

            }

        };
        return onDateSetListener;
    }
}