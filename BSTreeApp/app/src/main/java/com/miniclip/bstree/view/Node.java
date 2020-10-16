package com.miniclip.bstree.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;

import com.miniclip.bstree.entity.BSTree;

public class Node extends View {

    @NonNull
    private final BSTree node;

    public Node(@NonNull Context context, @NonNull BSTree node) {
        super(context);
        this.node = node;
    }

    private void drawNode(Canvas canvas, int cx, int cy, @NonNull BSTree tree) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor(tree.getColor()));
        canvas.drawCircle(cx, cy, 25, paint);

        Paint tColor = new Paint();
        tColor.setColor(Color.BLACK);
        tColor.setTextSize(20);
        tColor.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(tree.getValue()), cx, cy, tColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawNode(canvas, 100, 25, node);

        if (node.getLeft() != null) {
            Paint line = new Paint();
            line.setColor(Color.BLACK);
            line.setStrokeWidth(3);
            canvas.drawLine(100, 50, 50, 100, line);
            drawNode(canvas, 50, 125, node.getLeft());
        }

        if (node.getRight() != null) {
            Paint line = new Paint();
            line.setColor(Color.BLACK);
            line.setStrokeWidth(3);
            canvas.drawLine(100, 50, 150, 100, line);
            drawNode(canvas, 150, 125, node.getRight());
        }
    }
}
