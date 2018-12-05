package models;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

public class CustomDialogListener {
    private presenter.CustomDialogListener mListener;
    private AlertDialog mDialog;
    private Context context;

    public CustomDialogListener(AlertDialog dialog) {
        mDialog = dialog;
    }

    public void setClickListener(final presenter.CustomDialogListener listener) {
        Button positiveButton = mDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button nagativeButton = mDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnClickPositive(v, mDialog);
            }

        });
        nagativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnClickNagative(v, mDialog);
            }
        });
    }
}
