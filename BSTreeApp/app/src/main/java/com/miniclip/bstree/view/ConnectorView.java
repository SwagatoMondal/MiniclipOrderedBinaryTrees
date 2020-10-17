package com.miniclip.bstree.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class ConnectorView extends View {

    private int startX, startY;
    private int endX, endY;

    private Paint line;

    public ConnectorView(Context context, int startX, int startY, int endX, int endY) {
        super(context);

        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;

        line = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        line.setColor(Color.BLACK);
        line.setStrokeWidth(3);
        canvas.drawLine(startX, startY, endX, endY, line);
    }
}
