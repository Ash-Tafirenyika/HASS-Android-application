package com.example.my_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AppConfig {
    public static final String ipAdd = "192.168.43.108";
    public static final String portNum = "15555";

    public void alertError(Context context, final String msg) {
        final String error = (msg == null) ? "Unknown error: " : msg;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(error).setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
