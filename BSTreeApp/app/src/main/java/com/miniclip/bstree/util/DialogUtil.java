package com.miniclip.bstree.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.miniclip.bstree.R;
import com.miniclip.bstree.entity.BSTree;
import com.miniclip.bstree.view.Node;

public class DialogUtil {

    @NonNull
    private final Activity activity;
    @NonNull
    private DialogListener color_listener;
    @NonNull
    private DialogListener val_listener;

    public DialogUtil(@NonNull Activity activity, @NonNull DialogListener color_listener,
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

    static void showTreeDialog(@NonNull Context context, @NonNull BSTree node) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Node view
        final Node view = new Node(context, node);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, 100);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        view.setLayoutParams(params);

        builder.setView(view);
        builder.show();
    }

    public interface DialogListener {
        CharSequence[] getItems();
        void onSelected(int item);
    }
}
