package com.wordplat.ikvstockchart.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.wordplat.ikvstockchart.entry.Entry;
import com.wordplat.ikvstockchart.entry.EntrySet;
import com.wordplat.ikvstockchart.entry.SizeColor;
import com.wordplat.ikvstockchart.render.AbstractRender;
import com.wordplat.ikvstockchart.render.KLineRender;

/**
 * Created by rugovit on 1/23/2018.
 */

public class IndependentLineDrawing implements IDrawing{
    private Paint ma5Paint;
    private final RectF candleRect = new RectF(); // K 线图显示区域
    private AbstractRender render;

    // 计算 MA(5, 10, 20) 线条坐标用的
    private float[] ma5Buffer = new float[4];
    @Override
    public void onInit(RectF contentRect, AbstractRender render) {
        final SizeColor sizeColor = render.getSizeColor();
        this.render = render;
        if (ma5Paint == null) {
            ma5Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            ma5Paint.setStyle(Paint.Style.STROKE);
        }
        ma5Paint.setStrokeWidth(sizeColor.getMaLineSize());
        ma5Paint.setColor(sizeColor.getMa5Color());
        candleRect.set(contentRect);
    }

    @Override
    public void computePoint(int minIndex, int maxIndex, int currentIndex) {
        final int count = (maxIndex - minIndex) * 4;
        if (ma5Buffer.length < count) {
            ma5Buffer = new float[count];
        }

        final EntrySet entrySet = render.getEntrySet();
        final Entry entry = entrySet.getEntryList().get(currentIndex);
        final int i = currentIndex - minIndex;

        if (currentIndex < maxIndex - 1) {
            ma5Buffer[i * 4 + 0] = currentIndex + 0.5f;
            ma5Buffer[i * 4 + 1] = entry.getHigh();
            ma5Buffer[i * 4 + 2] = currentIndex + 1 + 0.5f;
            ma5Buffer[i * 4 + 3] = entrySet.getEntryList().get(currentIndex + 1).getHigh();
        }
    }

    @Override
    public void onComputeOver(Canvas canvas, int minIndex, int maxIndex, float minY, float maxY) {
        canvas.save();
        canvas.clipRect(candleRect);

        render.mapPoints(ma5Buffer);

        final int count = (maxIndex - minIndex) * 4;

        // 使用 drawLines 方法比依次调用 drawLine 方法要快
        canvas.drawLines(ma5Buffer, 0, count, ma5Paint);
        canvas.restore();
    }

    @Override
    public void onDrawOver(Canvas canvas) {

    }
}
