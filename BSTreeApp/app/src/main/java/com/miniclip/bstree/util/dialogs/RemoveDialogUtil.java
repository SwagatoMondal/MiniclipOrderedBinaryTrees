package com.miniclip.bstree.util.dialogs;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.miniclip.bstree.R;

public class RemoveDialogUtil {

    @NonNull
    private final Activity activity;
    @NonNull
    private DialogListener color_listener;
    @NonNull
    private DialogListener val_listener;

    public RemoveDialogUtil(@NonNull Activity activity, @NonNull DialogListener color_listener,
                            @NonNull DialogListener val_listener) {
        this.activity = activity;
        this.color_listener = color_listener;
        this.val_listener = val_listener;
    }

    public void show() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setItems(R.array.remove_type, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        colorPicker();
                        break;
                    case 1:
                        valuePicker();
                        break;
                }
            }
        });
        builder.show();
    }

    private void pickerDialog(@NonNull DialogInterface.OnClickListener listener,
                              @NonNull CharSequence[] items) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setItems(items, listener);
        builder.show();
    }

    private void colorPicker() {
        pickerDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                color_listener.onSelected(which);
            }
        }, color_listener.getItems());
    }

    private void valuePicker() {
        pickerDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                val_listener.onSelected(which);
            }
        }, val_listener.getItems());
    }
}
