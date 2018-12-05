package view.activity;

import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.vsolv.bigflow.R;

import java.util.ArrayList;
import java.util.List;

import constant.Constant;
import models.ViewPagerAdapter;
import view.fragment.Collection;
import view.fragment.OtherFragment;
import view.fragment.Promise_tobuy;
import view.fragment.Promise_topay;
import view.fragment.Sales_order;

public class CollectionActivity extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce;
    private Toolbar collectionToolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Bundle customer_details;
    private int schedule_type_gid, customer_gid;
    public FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        collectionToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(collectionToolbar);
        getSupportActionBar().setTitle(Constant.title_collection);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadView();
        initializeView();
        loadData();
    }

    private void loadView() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        fab = (FloatingActionButton) findViewById(R.id.fabCollection);
    }

    private void initializeView() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
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
    }

    private void loadData() {

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Collection(), "Collection");
        adapter.addFragment(new Promise_topay(), "P2P");
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
