package presenter;

import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.view.View;

public abstract class CustomDialogListener {
    public abstract void OnClickPositive(View view, AlertDialog dialog);

    public void OnClickNagative(View view, AlertDialog dialog) {
        dialog.dismiss();
    }
}
