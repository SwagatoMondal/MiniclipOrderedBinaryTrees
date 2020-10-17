package com.miniclip.bstree.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miniclip.bstree.entity.BSTree;

/**
 * Class to represent the tree view for a given tree
 */
public class NodeView extends View {

    private static final int DEFAULT_X = 100;
    private static final int DEFAULT_Y = 800;

    private int y = DEFAULT_Y;
    @Nullable
    private BSTree tree;
    private Paint paint, tColor, line;

    public NodeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setup();
    }

    private void setup() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tColor = new Paint();

        line = new Paint();
        line.setColor(Color.BLACK);
        line.setStrokeWidth(3);
    }

    public void setTree(@NonNull BSTree tree) {
        this.tree = tree;
    }

    public void setHeight(int height) {
        y = (height*2)/3;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (tree != null) drawInternal(tree, canvas, null);
    }

    /**
     * Method to draw each node (includes circle shape and text)
     */
    private void drawNode(@NonNull BSTree tree, Canvas canvas, int x, int y) {
        paint.setColor(Color.parseColor(tree.getColor()));
        canvas.drawCircle(x, y, 25, paint);

        tColor.setColor(Color.BLACK);
        tColor.setTextSize(20);
        tColor.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(tree.getValue()), x-2, y-2, tColor);
    }

    /**
     * Recursive method to draw the entire tree in inorder traversal
     */
    private Pair<Integer, Integer> drawInternal(@Nullable BSTree tree, Canvas canvas,
                                                @Nullable Pair<Integer, Integer> pair) {
        if (null == tree) return null;

        Pair<Integer, Integer> left = drawInternal(tree.getLeft(), canvas, pair);

        if (left == null && pair == null) {
            pair = new Pair<>(DEFAULT_X, y);
        } else if (left != null) {
            pair = new Pair<>(left.first+50, left.second-100);

            // Draw left connector
            canvas.drawLine(pair.first, pair.second+25, left.first, left.second-25, line);
        }
        drawNode(tree, canvas, pair.first, pair.second);

        Pair<Integer, Integer> right = new Pair<>(pair.first+50, pair.second+100);
        right = drawInternal(tree.getRight(), canvas, right);
        // Draw right connector
        if (right != null) {
            canvas.drawLine(pair.first, pair.second+25, right.first, right.second-25, line);
        }
        return pair;
    }
}
