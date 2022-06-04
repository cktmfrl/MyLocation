package com.example.mylocation.util;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class DialogUtil {

    private static final String TAG = DialogUtil.class.getSimpleName();

    public interface OnClickListener {
        void onOkClicked();
        void onCancelClicked();
    }

    public static void showDialog(Context context, String title, String message
            , String okButtonName, String cancelButtonName, OnClickListener listener) {

        AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
        localBuilder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(okButtonName,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                listener.onOkClicked();
                            }
                        })
                .setNegativeButton(cancelButtonName,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                listener.onCancelClicked();
                            }
                        })
                .create()
                .show();
    }

}
