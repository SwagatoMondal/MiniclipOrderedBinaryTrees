package com.miniclip.bstree.util.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.miniclip.bstree.entity.BSTree;
import com.miniclip.bstree.view.Node;

public class DialogUtil {

    public static void showTreeDialog(@NonNull Context context, @NonNull BSTree node) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Node view
        final Node view = new Node(context, node);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, 100);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        view.setLayoutParams(params);

        builder.setView(view);
        builder.show();
    }

    public static void fileChooser(@NonNull Context context, @NonNull final DialogListener listener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(listener.getItems(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onSelected(which);
            }
        });
        builder.show();
    }
}
