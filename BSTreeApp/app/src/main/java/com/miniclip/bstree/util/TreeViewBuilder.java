package com.miniclip.bstree.util;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miniclip.bstree.entity.BSTree;
import com.miniclip.bstree.util.dialogs.DialogUtil;

/**
 * Helper class to render the tree in inorder traversal
 */
public class TreeViewBuilder {

    public static void viewBuilder(@NonNull final Context context, @Nullable final BSTree tree,
                                   @NonNull ViewGroup container) {
        if (null == tree) return;

        viewBuilder(context, tree.getLeft(), container);

        final Button nodeView = new Button(context);
        nodeView.setText(String.valueOf(tree.getValue()));
        nodeView.setGravity(Gravity.CENTER_HORIZONTAL);
        nodeView.setBackgroundColor(Color.parseColor(tree.getColor()));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 5;
        params.topMargin = 5;
        params.leftMargin = 10;
        params.rightMargin = 10;
        nodeView.setLayoutParams(params);
        container.addView(nodeView);

        nodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showTreeDialog(context, tree);
            }
        });

        viewBuilder(context, tree.getRight(), container);
    }
}
