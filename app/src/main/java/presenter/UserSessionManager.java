package presenter;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.HashMap;

import models.UserDetails;

public class UserSessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    public static final int PRIVATE_MODE = 0;

    public static final String user_id = "userId";
    public static final String user_password = "userPassword";
    public static final String user_loginDate = "userLoginDate";
    public static final String user_details = "userDetails";
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    public static final String PREFER_NAME = "userDetails";


    public UserSessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createUserLoginSession(String luser_id, String luser_password,String ldate,String luserDetails) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(user_id, luser_id);
        editor.putString(user_password, luser_password);
        editor.putString(user_loginDate,ldate);
        editor.putString(user_details,luserDetails);
        editor.commit();
    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(user_id, pref.getString(user_id, null));
        user.put(user_password, pref.getString(user_password, null));
        user.put(user_loginDate, pref.getString(user_loginDate, null));
        user.put(user_details, pref.getString(user_details, null));
        return user;
    }
}
