package cn.cqray.android.widget.captcha;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import java.util.Random;

/**
 * 图形码
 * @author Cqray
 * @date 2021/4/20 11:26
 */
public class CodeCaptcha {

    /** 是否使用干扰线 **/
    private boolean mLineInterfere;
    /** 是否使用干扰点 **/
    private boolean mPointInterfere;
    /** 是否使用大写字母 **/
    private boolean mCapitalEnable;
    /** 文字加粗 **/
    private boolean mCodeBold;
    /** 验证码类型 0 数字 1 字母 2 数字和字母 **/
    private int mCodeType;
    /** 验证码长度 **/
    private int mCodeLength;
    /** 干扰线数量 **/
    private int mLineInterfereQuantity;
    /** 干扰点数量 **/
    private int mPointInterfereQuantity;
    /** 背景颜色 **/
    private int mBackgroundColor;
    private int mBackgroundRadius;
    private int mInterfereSize;
    private int mWidth;
    private int mHeight;

    private String mCode;
    private Paint mPaint;
    private Random mRandom;

    public CodeCaptcha() {
        mRandom = new Random();
        mPaint = new Paint();
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public Bitmap getBitmap() {
        checkSize();
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int partSize = mWidth / (mCodeLength + 1);
        int textSize = (int) Math.min(partSize * 2, mHeight * 0.7f);
        int rangeTop = ((mHeight - textSize) / 2);

        // 文字配置
        mPaint.setTextSize(textSize);
        if (mCodeBold) {
            mPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }

        generateRandomCode();
        drawBackground(canvas);
        for (int i = 0; i < mCodeLength; i++) {
            generateRandomPaint();
            int x = partSize / 2 + partSize * i + (mRandom.nextBoolean() ?
                    mRandom.nextInt(partSize / 4) : -mRandom.nextInt(partSize / 4));
            int y = textSize + (mRandom.nextBoolean() ?
                    mRandom.nextInt(rangeTop / 2) : -mRandom.nextInt(rangeTop / 2));
            canvas.drawText(mCode.charAt(i) + "", x, y, mPaint);
        }
        drawLine(canvas);
        drawPoint(canvas);
        canvas.save();
        canvas.restore();
        return bitmap;
    }

    public Drawable getDrawable() {
        Bitmap bitmap = getBitmap();
        return new BitmapDrawable(Resources.getSystem(), bitmap);
    }

    public String getCode() {
        return mCode;
    }

    public boolean isCodeMatch(String code) {
        return code != null && code.equalsIgnoreCase(mCode);
    }

    public void setCodeInto(@NonNull final View view) {
        if (view.getMeasuredWidth() == 0 && view.getMeasuredHeight() == 0) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    into(view);
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        } else {
            into(view);
        }
    }

    private void into(View view) {
        if (mWidth == 0) {
            mWidth = view.getMeasuredWidth();
        }
        if (mHeight == 0) {
            mHeight = view.getMeasuredHeight();
        }
        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(getDrawable());
        } else {
            ViewCompat.setBackground(view, getDrawable());
        }
    }

    private void checkSize() {
        if (mWidth == 0) {
            throw new IllegalArgumentException("Width is required if you don't call setCodeInto().");
        }
        if (mHeight == 0) {
            throw new IllegalArgumentException("Height is required if you don't call setCodeInto().");
        }
    }

    private void drawBackground(@NonNull Canvas canvas) {
        RectF rect = new RectF();
        rect.bottom = mHeight;
        rect.right = mWidth;
        mPaint.setColor(mBackgroundColor);
        canvas.drawRoundRect(rect, mBackgroundRadius, mBackgroundRadius, mPaint);
    }

    private void drawLine(@NonNull Canvas canvas) {
        if (mLineInterfere) {
            for (int i = 0; i < mLineInterfereQuantity; i++) {
                int color = generateRandomColor();
                int startX = mRandom.nextInt(mWidth);
                int startY = mRandom.nextInt(mHeight);
                int stopX = mRandom.nextInt(mWidth);
                int stopY = mRandom.nextInt(mHeight);
                mPaint.setStrokeWidth(mInterfereSize);
                mPaint.setColor(color);
                canvas.drawLine(startX, startY, stopX, stopY, mPaint);
            }
        }
    }

    private void drawPoint(@NonNull Canvas canvas) {
        if (mPointInterfere) {
            for (int i = 0; i < mPointInterfereQuantity; i++) {
                int color = generateRandomColor();
                int x = mRandom.nextInt(mWidth);
                int y = mRandom.nextInt(mHeight);
                mPaint.setStrokeWidth(mInterfereSize);
                mPaint.setColor(color);
                canvas.drawPoint(x, y, mPaint);
            }
        }
    }

    private void generateRandomCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mCodeLength; i++) {
            switch (mCodeType) {
                case 1:
                    sb.append(generateRandomWord());
                    break;
                case 2:
                    if (mRandom.nextBoolean()) {
                        sb.append(generateRandomNumber());
                    } else {
                        sb.append(generateRandomWord());
                    }
                    break;
                default:
                    sb.append(generateRandomNumber());
                    break;
            }
        }
        mCode = sb.toString();
    }

    private char generateRandomNumber() {
        return (char) (mRandom.nextInt(10) + 48);
    }

    private char generateRandomWord() {
        char c = (char) (mRandom.nextInt(26) + 97);
        if (mCapitalEnable && mRandom.nextBoolean()) {
            return Character.toUpperCase(c);
        }
        return c;
    }

    private int generateRandomColor() {
        StringBuilder sb = new StringBuilder();
        String haxString;
        int count = 3;
        for (int i = 0; i < count; i++) {
            haxString = Integer.toHexString(mRandom.nextInt(0xFF));
            if (haxString.length() == 1) {
                sb.append("0");
            }
            sb.append(haxString);
        }
        return Color.parseColor("#" + sb.toString());
    }

    private void generateRandomPaint() {
        int color = generateRandomColor();
        mPaint.setColor(color);
        mPaint.setFakeBoldText(mRandom.nextBoolean());
        float skewX = mRandom.nextInt(25) / 100f;
        skewX = mRandom.nextBoolean() ? skewX : -skewX;
        // float类型参数，负数表示右斜，整数左斜
        mPaint.setTextSkewX(skewX);
    }

    public static class Builder {
        private boolean mLineInterfere;
        private boolean mPointInterfere;
        private boolean mCapitalEnable;
        private boolean mCodeBold;
        private int mCodeType;
        private int mCodeLength;
        private int mLineInterfereQuantity;
        private int mPointInterfereQuantity;
        private int mBackgroundColor;
        private int mInterfereSize;
        private int mBackgroundRadius;
        private int mWidth;
        private int mHeight;

        private Builder() {
            mLineInterfere = true;
            mPointInterfere = true;
            mLineInterfereQuantity = 4;
            mPointInterfereQuantity = 100;
            mInterfereSize = dp2Px(2);
            mCodeType = 2;
            mCodeLength = 4;
            mCapitalEnable = true;
            mBackgroundColor = Color.parseColor("#DEDEDE");
        }

        public Builder width(float width) {
            mWidth = dp2Px(width);
            return this;
        }

        public Builder height(float height) {
            mHeight = dp2Px(height);
            return this;
        }

        public Builder numCode() {
            mCodeType = 0;
            return this;
        }

        public Builder wordCode() {
            mCodeType = 1;
            return this;
        }

        public Builder numWordCode() {
            mCodeType = 2;
            return this;
        }

        public Builder codeLength(int length) {
            mCodeLength = length;
            return this;
        }

        public Builder lineInterfereQuantity(int quantity) {
            mLineInterfereQuantity = quantity;
            return this;
        }

        public Builder pointInterfereQuantity(int quantity) {
            mPointInterfereQuantity = quantity;
            return this;
        }

        public Builder interfereSize(float size) {
            mInterfereSize = dp2Px(size);
            return this;
        }

        public Builder backgroundColor(int color) {
            mBackgroundColor = color;
            return this;
        }

        public Builder backgroundRadius(float radius) {
            mBackgroundRadius = dp2Px(radius);
            return this;
        }

        public Builder lineInterfere(boolean interfere) {
            mLineInterfere = interfere;
            return this;
        }

        public Builder pointInterfere(boolean interfere) {
            mPointInterfere = interfere;
            return this;
        }

        public Builder capitalEnable(boolean enable) {
            mCapitalEnable = enable;
            return this;
        }

        public Builder codeBold(boolean bold) {
            mCodeBold = bold;
            return this;
        }

        public CodeCaptcha build() {
            CodeCaptcha code = new CodeCaptcha();
            code.mWidth = mWidth;
            code.mHeight = mHeight;
            code.mCodeType = mCodeType;
            code.mCodeLength = mCodeLength;
            code.mLineInterfereQuantity = mLineInterfereQuantity;
            code.mPointInterfereQuantity = mPointInterfereQuantity;
            code.mInterfereSize = mInterfereSize;
            code.mBackgroundColor = mBackgroundColor;
            code.mBackgroundRadius = mBackgroundRadius;
            code.mLineInterfere = mLineInterfere;
            code.mPointInterfere = mPointInterfere;
            code.mCapitalEnable = mCapitalEnable;
            code.mCodeBold = mCodeBold;
            return code;
        }
    }

    private static int dp2Px(float dp) {
        return (int) (Resources.getSystem().getDisplayMetrics().density * dp + 0.5f);
    }
}
