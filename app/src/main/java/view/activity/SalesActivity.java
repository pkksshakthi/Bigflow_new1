package view.activity;

import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.widget.Toast;


import com.vsolv.bigflow.R;

import constant.Constant;
import models.ViewPagerAdapter;
import view.fragment.Promise_tobuy;
import view.fragment.Sales_order;

public class SalesActivity extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce;
    private Toolbar salesToolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Bundle customer_details;
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        salesToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(salesToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent() != null) {
            customer_details = getIntent().getExtras();
            int soheader_gid = customer_details.getInt(Constant.key_soheader_gid, 0);
            if (soheader_gid != 0)
                isEdit = true;
        }
        loadView();
        initializeView();
        loadData();


    }

    private void loadView() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
    }

    private void initializeView() {
        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
    }

    private void loadData() {

    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Sales_order(), "Sales");
        if (!isEdit)
            adapter.addFragment(new Promise_tobuy(), "P2B");
        viewPager.setAdapter(adapter);
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
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return super.onSupportNavigateUp();
    }

}
