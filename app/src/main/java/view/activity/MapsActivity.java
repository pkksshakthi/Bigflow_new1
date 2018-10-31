package view.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vsolv.bigflow.R;

import java.util.Date;
import java.util.List;

import DataBase.GetData;
import models.Common;
import models.Variables;
import presenter.NetworkResult;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private int employee_gid;
    Bundle bundleDetails;
    private GetData getData;
    private ProgressDialog progressDialog;
    private List<Variables.LatLong> latLongList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (isMapServices()) {
            if (getIntent() != null) {
                bundleDetails = getIntent().getExtras();
                employee_gid = bundleDetails.getInt("employee_gid");
                String s = bundleDetails.getString("employee_name");
                getSupportActionBar().setTitle(bundleDetails.getString("employee_name"));
            }

            //loadView();
            initializeView();
            loadData();
        }
    }


    private void loadView() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initializeView() {
        progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        getData = new GetData(getBaseContext());

    }

    private void loadData() {
        //progressDialog.show();
        if (!Common.isOnline(getApplicationContext())) {
            Snackbar.with(getApplication(), null)
                    .type(Type.WARNING)
                    .message("Please Check Internet Connection.")
                    .duration(Duration.SHORT)
                    .fillParent(true)
                    .textAlign(Align.LEFT)
                    .show();
            //setVisibility(View.GONE, View.GONE, View.VISIBLE);
            progressDialog.cancel();
            return;
        }
        latLongList = getData.getLatLong(employee_gid, new Date(), new Date(), new NetworkResult() {
            @Override
            public void handlerResult(String result) {
                loadView();
//                setAdapter();
//                progressDialog.cancel();
            }

            @Override
            public void handlerError(String result) {
                progressDialog.cancel();
            }
        });

    }

    public boolean isMapServices() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if (available == ConnectionResult.SUCCESS) {
            Log.d("isMapServices", "Google Play services is Working");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (latLongList.size() > 0) {

            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            for (int i = 0; i < latLongList.size(); i++) {
                LatLng latLng = new LatLng(latLongList.get(i).latitude, latLongList.get(i).longitude);
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Sydney"));
                options.add(latLng);
            }
            LatLng sydney = new LatLng(latLongList.get(0).latitude, latLongList.get(0).longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f));
            mMap.addPolyline(options);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
