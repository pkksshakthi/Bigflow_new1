
package view.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.vsolv.bigflow.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DataBase.GetData;
import models.Common;
import models.CustomerAdapter;
import models.EmployeeTrackingAdapter;
import models.UserDetails;
import models.Variables;
import presenter.NetworkResult;
import view.activity.MapsActivity;


public class EmployeeTrackingFragment extends Fragment {
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
    private RecyclerView rv_emptracking;
    private TextView empty_view, reload;
    private SearchView customerSearch;
    private ProgressDialog progressDialog;
    private GetData getData;
    private List<Variables.Employee> employeeList;
    private EmployeeTrackingAdapter adapter;

    public EmployeeTrackingFragment() {
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
    public static EmployeeTrackingFragment newInstance(String param1, String param2) {
        EmployeeTrackingFragment fragment = new EmployeeTrackingFragment();
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
        getActivity().setTitle("Tracking");
        fragmentView = inflater.inflate(R.layout.fragment_employee_tracking, container, false);
        loadView();
        initializeView();
        loadData();
        return fragmentView;

    }

    private void loadView() {
        linearLayout = (LinearLayout) fragmentView.findViewById(R.id.linearDirect);
        rv_emptracking = (RecyclerView) fragmentView.findViewById(R.id.trackingRecyclerView);
        empty_view = fragmentView.findViewById(R.id.empty_view);
        customerSearch = fragmentView.findViewById(R.id.trackingSearch);
        reload = fragmentView.findViewById(R.id.trackingReload);
    }

    private void initializeView() {
        rv_emptracking.setHasFixedSize(true);
        rv_emptracking.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        employeeList = new ArrayList<>();
        //customerSearch.setQueryHint("Search");
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        getData = new GetData(getActivity());
    }

    private void loadData() {
        progressDialog.show();
        if (!Common.isOnline(getContext())) {
            Snackbar.with(getActivity(), null)
                    .type(Type.WARNING)
                    .message("Please Check Internet Connection.")
                    .duration(Duration.SHORT)
                    .fillParent(true)
                    .textAlign(Align.LEFT)
                    .show();
            setVisibility(View.GONE, View.GONE, View.VISIBLE);
            progressDialog.cancel();
            return;
        }
        employeeList = getData.EmployeeList(UserDetails.getUser_id(), new NetworkResult() {
            @Override
            public void handlerResult(String result) {
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

        adapter = new EmployeeTrackingAdapter(getActivity(), employeeList);
        rv_emptracking.setAdapter(adapter);


        adapter.setOnclickListener(new EmployeeTrackingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Variables.Employee item, int position) {

            }

            @Override
            public void onDetailsClick(Variables.Employee item, int position) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("employee_gid", item.employee_gid);
                bundle.putString("employee_name", item.employee_name);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onMapsClick(Variables.Employee item, int position) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("employee_gid", item.employee_gid);
                bundle.putString("employee_name", item.employee_name);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        if (adapter.getItemCount() == 0) {
            empty_view.setText("No Record" + getActivity().getResources().getString(R.string.loading));
            setVisibility(View.GONE, View.VISIBLE, View.GONE);
        } else {
            setVisibility(View.VISIBLE, View.GONE, View.GONE);
        }
    }

    public void setVisibility(int recycleView, int emptyView, int reloadView) {
        linearLayout.setVisibility(recycleView);
        empty_view.setVisibility(emptyView);
        reload.setVisibility(reloadView);
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