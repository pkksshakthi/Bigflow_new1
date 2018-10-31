package models;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.vsolv.bigflow.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static Date convertDate(String inputString, String inputStringFormat) {
        SimpleDateFormat format = new SimpleDateFormat(inputStringFormat);
        try {
            Date date = format.parse(inputString);
            return date;
        } catch (ParseException pr) {
            return null;
        }

    }

    public static String convertDateString(Date date, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        String str = format.format(date);
        return str;
    }


    //General function
    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnected());
    }

    public static void hideKeyboard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showSnackbar_warning(Context context, View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(context.getResources().getColor(R.color.warning));
        TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(context.getResources().getColor(R.color.colorWhite));
        snackbar.show();
    }

    public static void showSnackbar_success(Context context, View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(context.getResources().getColor(R.color.success));
        TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(context.getResources().getColor(R.color.colorWhite));
        snackbar.show();
    }
}
