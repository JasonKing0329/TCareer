package com.king.app.tcareer.view.widget.scoreboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.king.app.tcareer.utils.DebugLog;

public class ScoreView extends View {

    private Paint paint;

    private int textScoreSize = dp2px(16);
    private int textScoreSubSize = dp2px(12);
    private int bgColor = Color.WHITE;
    private int bgColorFocus = Color.parseColor("#64907f");
    private int textColor = Color.parseColor("#333333");
    private int textColorFocus = Color.WHITE;

    private boolean isFocus;

    private boolean isTextBold;

    private Integer score;

    private Integer scoreSub;

    public ScoreView(Context context) {
        super(context);
        init(null);
    }

    public ScoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ScoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        paint = new Paint();
    }

    public void setFocus(boolean focus) {
        isFocus = focus;
        invalidate();
    }

    public void setScore(Integer score) {
        this.score = score;
        invalidate();
    }

    public void setScoreSub(Integer scoreSub) {
        this.scoreSub = scoreSub;
        invalidate();
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public void setBgColorFocus(int bgColorFocus) {
        this.bgColorFocus = bgColorFocus;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setTextColorFocus(int textColorFocus) {
        this.textColorFocus = textColorFocus;
    }

    public void setTextBold(boolean textBold) {
        isTextBold = textBold;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        RectF rectF = new RectF(0, 0, getWidth(), getHeight());
        drawBackground(rectF, canvas);
        drawScore(rectF, canvas);
        paint.reset();
        super.onDraw(canvas);
    }

    private void drawScore(RectF rectF, Canvas canvas) {
        paint.setFakeBoldText(isTextBold);
        paint.setTextSize(textScoreSize);
        paint.setTextAlign(Paint.Align.CENTER);
        if (isFocus) {
            paint.setColor(textColorFocus);
        } else {
            paint.setColor(textColor);
        }
        String scoreMainText;
        if (score == null) {
            scoreMainText = "-";
        }
        else {
            scoreMainText = String.valueOf(score);
        }
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = rectF.centerY() + distance;
        canvas.drawText(scoreMainText, rectF.centerX(), baseline, paint);

        Rect mainTextRect = new Rect();
        paint.getTextBounds(scoreMainText, 0, scoreMainText.length(), mainTextRect);


        if (scoreSub == null) {
            return;
        }
        paint.setTextSize(textScoreSubSize);
        paint.setTextAlign(Paint.Align.LEFT);
        if (isFocus) {
            paint.setColor(textColorFocus);
        } else {
            paint.setColor(textColor);
        }
        float x = rectF.centerX() + mainTextRect.width() / 2 + dp2px(2);
        float y = baseline - mainTextRect.height();
        canvas.drawText(String.valueOf(scoreSub), x, y, paint);
    }

    private void drawBackground(RectF rectF, Canvas canvas) {
        if (isFocus) {
            paint.setColor(bgColorFocus);
        } else {
            paint.setColor(bgColor);
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rectF, paint);
    }

    private int dp2px(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

}
