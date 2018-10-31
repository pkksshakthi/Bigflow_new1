package models;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import DataBase.DataBaseHandler;
import constant.Constant;
import network.CallbackHandler;
import presenter.VolleyCallback;
import com.google.gson.Gson;

public class LocationSync extends Activity {

    public  static String LatLongSet(Context context){

           final List<Variables.Location>  locations ;

        try{

            //To Get the Values from Local SQLIte

            final DataBaseHandler dataBaseHandler = new DataBaseHandler(context);


            locations = dataBaseHandler.getLatLong(context);
            final Gson gson = new Gson();
            final String json_data = gson.toJson(locations);


            if (!locations.isEmpty()) {

                // To Set in Main Server.
                String URL = Constant.URL + "LatLong_Set?Action=MULTIPLE_INSERT";

                CallbackHandler.sendReqest(context, Request.Method.POST, json_data, URL, new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String status = jsonObject.getString("MESSAGE");
                            if (status.equals("SUCCESS")) {
                                String gids = "" ;
                                for (int i = 0; i < locations.size(); i++) {
                                    int latlong_gid = (locations.get(i).latlong_gid);

                                    if (gids.equals("")){
                                        gids = String.valueOf(latlong_gid);
                                    }else {
                                        gids = gids + ","+String.valueOf(latlong_gid);
                                        String test = gids;
                                    }

                                }
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(Constant.latlong_issync,"Y");
                                String Out_Message = dataBaseHandler.Update("fet_trn_tlatlong",contentValues,
                                        "latlong_gid in ( "+gids + ")");
                                String test = Out_Message;
                            }
                        } catch (JSONException e) {
                            Log.e("Location", result);
                        }
                   // return "SUCCESS";
                    }

                    @Override
                    public void onFailure(String result) {
                        Log.e("Location", result);

                    }
                });

            }
//            return "SUCCESS";

        }catch (Exception e){
            return e.getMessage();
        }
        return "SUCCESS";
    }

}
