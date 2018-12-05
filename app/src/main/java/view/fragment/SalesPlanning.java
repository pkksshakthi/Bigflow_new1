package view.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.vsolv.bigflow.R;

import java.util.ArrayList;
import java.util.List;

import DataBase.GetData;
import constant.Constant;
import models.Common;
import models.CustomerAdapter;
import models.UserDetails;
import models.Variables;
import presenter.NetworkResult;
import view.activity.CommentActivity;
import view.activity.CustomerDetailActivity;
import view.activity.HistoryActivity;
import view.activity.SalesActivity;
import view.activity.SalesPlanActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalesPlanning extends Fragment implements View.OnClickListener {


    private View fragmentView;
    private RecyclerView recyclerView;
    private TextView empty_view, reload;
    private LinearLayout linearLayout;
    private Bundle bundle;
    private ProgressDialog progressDialog;
    private GetData getData;
    private List<Variables.Customer> customerList;
    private CustomerAdapter adapter;

    public SalesPlanning() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(Constant.title_sales_planning);
        setHasOptionsMenu(true);
        fragmentView = inflater.inflate(R.layout.fragment_dirct_schedule, container, false);
        loadView();
        initializeView();
        loadData();
        return fragmentView;
    }

    private void loadView() {
        linearLayout = (LinearLayout) fragmentView.findViewById(R.id.linearDirect);
        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.customerRecyclerView);
        empty_view = fragmentView.findViewById(R.id.empty_view);
        reload = fragmentView.findViewById(R.id.custReload);
    }

    private void initializeView() {
        bundle = new Bundle();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        customerList = new ArrayList<>();
        reload.setOnClickListener(this);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        getData = new GetData(getActivity());
    }

    private void loadData() {
        if (!Common.isOnline(getContext())) {
            Snackbar.with(getActivity(), null)
                    .type(Type.WARNING)
                    .message(getActivity().getResources().getString(R.string.check_internet_connection))
                    .duration(Duration.SHORT)
                    .fillParent(true)
                    .textAlign(Align.LEFT)
                    .show();
            setVisibility(View.GONE, View.GONE, View.VISIBLE);
            return;
        }
        progressDialog.show();
        customerList = getData.MappedCustomerList(UserDetails.getUser_id(), new NetworkResult() {
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
                bundle.putInt(Constant.key_customer_gid, item.customer_gid);
                bundle.putString(Constant.key_customer_name,item.customer_name);
                Intent intent = new Intent(getActivity(), SalesPlanActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, Variables.Customer item, int position) {

            }

            @Override
            public void onViewDetailsClick(Variables.Customer item, int position) {

            }

            @Override
            public void onHistroryClick(Variables.Customer item, int position) {

            }

            @Override
            public void onCommentClick(Variables.Customer item, int position) {

            }
        });

        if (adapter.getItemCount() == 0) {
            empty_view.setText("" + getActivity().getResources().getString(R.string.no_data_available));
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.custReload) {
            loadData();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem searchitem = menu.findItem(R.id.action_search);
        SearchView customerSearch = (SearchView) searchitem.getActionView();
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
        super.onCreateOptionsMenu(menu, inflater);
    }
}
