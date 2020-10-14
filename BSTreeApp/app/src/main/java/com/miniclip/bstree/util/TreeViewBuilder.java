package com.miniclip.bstree.util;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miniclip.bstree.entity.BSTree;

/**
 * Helper class to render the tree in inorder traversal
 */
public class TreeViewBuilder {

    public static void viewBuilder(@NonNull Context context, @Nullable BSTree tree,
                                   @NonNull ViewGroup container) {
        if (null == tree) return;

        viewBuilder(context, tree.getLeft(), container);

        final TextView textView = new TextView(context);
        textView.setText(String.valueOf(tree.value));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setBackgroundColor(Color.parseColor(tree.color));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, 50);
        params.bottomMargin = 5;
        params.topMargin = 5;
        params.leftMargin = 10;
        params.rightMargin = 10;
        textView.setLayoutParams(params);
        container.addView(textView);

        viewBuilder(context, tree.getRight(), container);
    }
}
