package com.bingcrowsby.byoadventure.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by kevinfinn on 2/20/15.
 */
public class UserStateAlert {
    static private AlertDialog alertDialog = null;

    private UserStateAlert(Context context) {
    }

    public static AlertDialog getInstance(Context context, String message) {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(context).setTitle("USER STATE ALERT").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setCancelable(true).create();
            //customize alert here
        }
        alertDialog.setMessage(message);
        return alertDialog;
    }
}