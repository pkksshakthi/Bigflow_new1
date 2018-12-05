package view.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.vsolv.bigflow.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import constant.Constant;
import models.Common;
import models.UserDetails;
import models.Variables;
import models.ViewtaskAdapater;
import network.CallbackHandler;
import presenter.VolleyCallback;
import view.activity.SalesActivity;


public class ViewtaskFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_Title = "title";
    private static final String ARG_PARAM2 = "param2";
    private SearchView search;
    private TextView empty_view_task;
    private ArrayList<Variables.Viewtask_customer> cust_list_view;
    private ViewtaskAdapater adapter;
    private Bundle sessiondata;
    private LinearLayout linearLayout;
    private Button button;
    private ProgressDialog progressDialog;
    private String Title;
    private String mParam2;


    private RecyclerView recyclerView;
    private ArrayList<Variables.Timeline> timelines;

    public ViewtaskFragment() {
        // Required empty public constructor
    }


    public static ViewtaskFragment newInstance(String Title, String param2) {
        ViewtaskFragment fragment = new ViewtaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_Title, Title);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Title = getArguments().getString(ARG_Title);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Day's Summary");
        View view = inflater.inflate(R.layout.fragment_viewtask, container, false);
        Loadview(view);
        load_data();
        sessiondata = new Bundle();

        cust_list_view = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        return view;

    }

    private void load_data() {

        JSONObject part_Json_viwe = new JSONObject();
        JSONObject part2_Json_view = new JSONObject();
        JSONObject full_Json_view = new JSONObject();

        try {
            part2_Json_view.put(Constant.from_date, "2018-10-29");
            part2_Json_view.put(Constant.to_date, Common.convertDateString(new Date(), "yyyy-MM-dd"));
            part_Json_viwe.put(Constant.action, "view");
            part_Json_viwe.put(Constant.f_date, Common.convertDateString(new Date(), "dd/MM/yyyy"));
            part_Json_viwe.put("jsondata", part2_Json_view);
            full_Json_view.put("parms", part_Json_viwe);

            if (full_Json_view.length() > 0) {
                String OutMessage = Direct_outcome_summary(full_Json_view);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String Direct_outcome_summary(JSONObject jsonObject) {
        String URL = Constant.URL + "Direct_Outcome_Summary?Emp_Gid=" + UserDetails.getUser_id() +
                "&Entity_gid=1&action=view";

        CallbackHandler.sendReqest(getActivity(), Request.Method.POST, jsonObject.toString(), URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("MESSAGE");

                    if (message.equals("FOUND")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("DATA");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj_json = jsonArray.getJSONObject(i);
                            String display_name = obj_json.getString("display_name");
                            String schedule_date = obj_json.getString("schedule_date");
                            String type = obj_json.getString("scheduletype_name");
                            String status;
                            int Schedule_ref_gid, customer_gid, schedule_gid;
                            schedule_gid = obj_json.getInt("schedule_gid");

                            if (obj_json.isNull("Sales_Status")) {
                                status = " ";
                            } else {
                                status = obj_json.getString("Sales_Status");
                            }
                            String complete_for;
                            if (obj_json.isNull("followupreason_name")) {
                                complete_for = " ";
                            } else {
                                complete_for = obj_json.getString("followupreason_name");
                            }
                            if (obj_json.isNull("Schedule_ref_gid")) {
                                Schedule_ref_gid = 0;
                            } else {
                                Schedule_ref_gid = obj_json.getInt("Schedule_ref_gid");
                            }
                            if (obj_json.isNull("schedule_customer_gid")) {
                                customer_gid = 0;
                            } else {
                                customer_gid = obj_json.getInt("schedule_customer_gid");
                            }
                            cust_list_view.add(new Variables.Viewtask_customer(display_name, schedule_date, type, complete_for, status, Schedule_ref_gid, customer_gid, schedule_gid));
                        }

                        setAdapter();
                    } else {
                        empty_view_task.setText(getResources().getString(R.string.error_loading));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(String result) {

            }


        });
        return "";
    }

    private void Loadview(View view) {

        search = view.findViewById(R.id.cust_search);
        empty_view_task = (TextView) view.findViewById(R.id.empty_view_task);
        linearLayout = (LinearLayout) view.findViewById(R.id.linearDirect_viewtask);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_timeLine);


    }

    public void onClick(View view) {

        if (view == button) {


        }


    }

    public void setAdapter() {

        adapter = new ViewtaskAdapater(getActivity(), cust_list_view);
        recyclerView.setAdapter(adapter);
        adapter.setOnclickListener(new ViewtaskAdapater.OnItemClickListener() {
            @Override
            public void onItemClick(Variables.Viewtask_customer item, int position) {
                if (item.getSchedule_ref_gid() != 0 && !item.getStatus().equals("APPROVED") && !item.getStatus().equals("CANCELLED")) {
                    sessiondata.putInt(Constant.key_soheader_gid, item.getSchedule_ref_gid());
                    sessiondata.putInt(Constant.key_customer_gid, item.getCustomer_gid());
                    //sessiondata.putString("Status", item.getStatus());
                    sessiondata.putInt(Constant.key_schedule_gid, item.getSchedule_gid());
                    sessiondata.putString("Edit_flg", "Y");

                    Class aClass = SalesActivity.class;
                    Intent intent = new Intent(getActivity(), aClass);
                    intent.putExtras(sessiondata);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "You Cannot Edit This.", Toast.LENGTH_LONG).show();

                }
            }
        });

    }


}

