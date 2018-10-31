package view.activity;

import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.Request;
import com.vsolv.bigflow.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import constant.Constant;
import models.UserDetails;
import network.CallbackHandler;
import presenter.VolleyCallback;
import view.fragment.Promise_tobuy;
import view.fragment.Sales_order;
import view.fragment.Sales_others;

public class SalesActivity extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce;
    private Toolbar salesToolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Bundle customer_details;
    String EditStatus = "";
    private int schedule_type_gid,customer_gid;
    String newString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        salesToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(salesToolbar);

        getSupportActionBar().setTitle("Sale");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (getIntent() !=null){
            customer_details = getIntent().getExtras();
            customer_gid = customer_details.getInt("customer_id");
            schedule_type_gid = customer_details.getInt("scheduletype_id");
            if (customer_details.containsKey("Status")) {

                EditStatus = customer_details.getString("Status");
            }
            Bundle data =new Bundle();
            data.putInt("customer_gid",customer_gid);
            Fragment fragment=new Sales_order();
            fragment.setArguments(data);
        }
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
        loadData();


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Sales_order(), "Sales","FUR002");
        if(EditStatus == "") {
            adapter.addFragment(new Promise_tobuy(), "P2B", "FUR001");
        }
        //adapter.addFragment(new Sales_others(), "others");

        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private final List<String> followupreason_code = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }


        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title,String code) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void loadData() {

        String URL = Constant.URL + "Schedule_Master??Entity_gid="+ UserDetails.getEntity_gid()
                +"&Action=FOLLOWUP_REASON&Schedule_Type_gid=1" ;


        CallbackHandler.sendReqest(this, Request.Method.GET, "", URL, new VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("MESSAGE");

                    if (message.equals("FOUND")) {

                    } else {
                        Toast.makeText(SalesActivity.this, "No Data In Schedule Api", Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(String result) {

                Log.e("DirectSchecule", result);
            }

        });

    }
}
