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

import java.util.HashMap;

/**
 * Class to represent the tree view for a given tree
 */
public class NodeView extends View {

    private static final int DEFAULT_X = 300;

    @Nullable
    private BSTree tree;
    private Paint paint, tColor, line;
    private HashMap<Integer, Pair<Integer, Integer>> levelMap;

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

        levelMap = new HashMap<>();
    }

    public void setTree(@NonNull BSTree tree) {
        this.tree = tree;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (tree != null) {
            levelMap.clear();
            drawInternal(tree, canvas, 0);
        }
    }

    /**
     * Method to draw each node (includes circle shape and text)
     */
    private void drawNode(@NonNull BSTree tree, Canvas canvas, int x, int y) {
        paint.setColor(Color.parseColor(tree.getColor()));
        canvas.drawCircle(x, y, 25, paint);

        tColor.setColor(Color.BLACK);
        tColor.setTextSize(25);
        tColor.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(tree.getValue()), x, y, tColor);
    }

    /**
     * Recursive method to draw the entire tree in inorder traversal
     */
    private Pair<Integer, Integer> drawInternal(@Nullable BSTree tree, Canvas canvas,
                                                int level) {
        if (null == tree) return null;

        Pair<Integer, Integer> left = drawInternal(tree.getLeft(), canvas, level + 1);
        Pair<Integer, Integer> right = drawInternal(tree.getRight(), canvas, level + 1);

        Pair<Integer, Integer> pair;
        if (left == null && right == null) {
            Pair<Integer, Integer> current = getLastLevelPair(level);
            if (current == null) {
                pair = new Pair<>(DEFAULT_X, level*100+25);
            } else {
                pair = new Pair<>(current.first, current.second);
            }
        } else {
            if (left != null) {
                pair = new Pair<>(left.first + 50, left.second - 100);
            } else {
                pair = new Pair<>(right.first - 50, right.second - 100);
            }
        }

        // Draw node
        drawNode(tree, canvas, pair.first, pair.second);
        levelMap.put(level, pair);

        // Draw node connectors
        if (left != null) {
            // Draw left connector
            canvas.drawLine(pair.first, pair.second + 25, left.first, left.second - 25, line);
        }
        if (right != null) {
            // Draw right connector
            canvas.drawLine(pair.first, pair.second+25, right.first, right.second-25, line);
        }

        return pair;
    }

    private Pair<Integer, Integer> getLastLevelPair(int currentLevel) {
        for (int i = currentLevel; i >= 0; i--) {
            Pair<Integer, Integer> temp = levelMap.get(i);
            if (temp != null) {
                return new Pair<>(temp.first+100, temp.second+(100*(currentLevel-i)));
            }
        }
        return null;
    }
}
