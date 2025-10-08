package com.dingyg.qrcodelib.zxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.dingyg.qrcodelib.zxing.camera.CameraManager;
import com.google.zxing.ResultPoint;
import com.soonfor.repository.R;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class ViewfinderView extends View {
    public static int RECT_OFFSET_X;
    public static int RECT_OFFSET_Y;
    private static final int[] SCANNER_ALPHA = new int[]{0, 64, 128, 192, 255, 192, 128, 64};
    private static final int OPAQUE = 255;
    private static long ANIMATION_DELAY = 10L;
    private final Paint paint;
    private final int maskColor;
    private final int resultColor;
    private final int frameColor;
    private final int laserColor;
    private final int resultPointColor;
    private final int angleColor;
    private String hint;
    private int hintColor;
    private String errorHint;
    private int errorHintColor;
    private boolean showPossiblePoint;
    private Bitmap resultBitmap;
    private int scannerAlpha;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;
    private float translateY = 2.0F;
    private int cameraPermission = -1;

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.qr_ViewfinderView);
        this.angleColor = typedArray.getColor(R.styleable.qr_ViewfinderView_qr_angleColor, -1);
        this.hint = typedArray.getString(R.styleable.qr_ViewfinderView_qr_hint);
        this.hintColor = typedArray.getColor(R.styleable.qr_ViewfinderView_qr_textHintColor, -7829368);
        this.errorHint = typedArray.getString(R.styleable.qr_ViewfinderView_qr_errorHint);
        this.errorHintColor = typedArray.getColor(R.styleable.qr_ViewfinderView_qr_textErrorHintColor, -1);
        this.showPossiblePoint = typedArray.getBoolean(R.styleable.qr_ViewfinderView_qr_showPossiblePoint, false);
        RECT_OFFSET_X = typedArray.getInt(R.styleable.qr_ViewfinderView_qr_offsetX, 0);
        RECT_OFFSET_Y = typedArray.getInt(R.styleable.qr_ViewfinderView_qr_offsetY, 0);
        if (TextUtils.isEmpty(this.hint)) {
            this.hint = "将二维码置于框内扫描";
        }

        if (TextUtils.isEmpty(this.errorHint)) {
            this.errorHint = "请允许访问摄像头后重试";
        }

        if (this.showPossiblePoint) {
            ANIMATION_DELAY = 40L;
        }

        this.paint = new Paint();
        Resources resources = this.getResources();
        this.maskColor = resources.getColor(R.color.viewfinder_mask);
        this.resultColor = resources.getColor(R.color.result_view);
        this.frameColor = resources.getColor(R.color.viewfinder_frame);
        this.laserColor = resources.getColor(R.color.viewfinder_laser);
        this.resultPointColor = resources.getColor(R.color.possible_result_points);
        this.scannerAlpha = 0;
        this.possibleResultPoints = new HashSet(5);
        typedArray.recycle();
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = null;
        if (!this.isInEditMode()) {
            if (this.cameraPermission != 0) {
                this.cameraPermission = CameraManager.get().checkCameraPermission();
            }

            frame = CameraManager.get().getFramingRect();//RECT_OFFSET_X, RECT_OFFSET_Y
        }

        int width;
        int height;
        if (frame == null) {
            width = this.getResources().getDisplayMetrics().widthPixels;
            height = this.getResources().getDisplayMetrics().heightPixels;
            int leftOffset = (width) / 2;
            int topOffset = (height) / 2;
            frame = new Rect(leftOffset + RECT_OFFSET_X, topOffset + RECT_OFFSET_Y, leftOffset + width + RECT_OFFSET_X, topOffset + height + RECT_OFFSET_Y);
        }

        width = canvas.getWidth();
        height = canvas.getHeight();
        this.paint.setColor(this.resultBitmap != null ? this.resultColor : this.maskColor);
        canvas.drawRect(0.0F, 0.0F, (float)width, (float)frame.top, this.paint);
        canvas.drawRect(0.0F, (float)frame.top, (float)frame.left, (float)(frame.bottom + 1), this.paint);
        canvas.drawRect((float)(frame.right + 1), (float)frame.top, (float)width, (float)(frame.bottom + 1), this.paint);
        canvas.drawRect(0.0F, (float)(frame.bottom + 1), (float)width, (float)height, this.paint);
        this.drawText(canvas, frame);
        if (this.resultBitmap != null) {
            this.paint.setAlpha(255);
            canvas.drawBitmap(this.resultBitmap, (float)frame.left, (float)frame.top, this.paint);
        } else {
            this.paint.setColor(-7829368);
            canvas.drawRect((float)frame.left, (float)frame.top, (float)(frame.right + 1), (float)(frame.top + 2), this.paint);
            canvas.drawRect((float)frame.left, (float)(frame.top + 2), (float)(frame.left + 2), (float)(frame.bottom - 1), this.paint);
            canvas.drawRect((float)(frame.right - 1), (float)frame.top, (float)(frame.right + 1), (float)(frame.bottom - 1), this.paint);
            canvas.drawRect((float)frame.left, (float)(frame.bottom - 1), (float)(frame.right + 1), (float)(frame.bottom + 1), this.paint);
            this.drawAngle(canvas, frame);
            this.drawScanner(canvas, frame);
            if (this.showPossiblePoint) {
                this.drawPossiblePoint(canvas, frame);
            }

            this.postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
        }

    }

    public void drawViewfinder() {
        this.resultBitmap = null;
        this.invalidate();
    }

    public void drawResultBitmap(Bitmap barcode) {
        this.resultBitmap = barcode;
        this.invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        this.possibleResultPoints.add(point);
    }

    private void drawAngle(Canvas canvas, Rect frame) {
        int angleLength = 50;
        int angleWidth = 10;
        int top = frame.top;
        int bottom = frame.bottom;
        int left = frame.left;
        int right = frame.right;
        this.paint.setColor(this.angleColor);
        canvas.drawRect((float)(left - angleWidth), (float)(top - angleWidth), (float)(left + angleLength), (float)top, this.paint);
        canvas.drawRect((float)(left - angleWidth), (float)(top - angleWidth), (float)left, (float)(top + angleLength), this.paint);
        canvas.drawRect((float)(left - angleWidth), (float)bottom, (float)(left + angleLength), (float)(bottom + angleWidth), this.paint);
        canvas.drawRect((float)(left - angleWidth), (float)(bottom - angleLength), (float)left, (float)(bottom + angleWidth), this.paint);
        canvas.drawRect((float)(right - angleLength), (float)(top - angleWidth), (float)(right + angleWidth), (float)top, this.paint);
        canvas.drawRect((float)right, (float)(top - angleWidth), (float)(right + angleWidth), (float)(top + angleLength), this.paint);
        canvas.drawRect((float)(right - angleLength), (float)bottom, (float)right, (float)(bottom + angleWidth), this.paint);
        canvas.drawRect((float)right, (float)(bottom - angleLength), (float)(right + angleWidth), (float)(bottom + angleWidth), this.paint);
    }

    private void drawText(Canvas canvas, Rect frame) {
        String text;
        if (this.cameraPermission == 0) {
            this.paint.setColor(this.hintColor);
            this.paint.setTextSize(36.0F);
            text = this.hint;
            canvas.drawText(this.hint, (float)(frame.centerX() - text.length() * 36 / 2), (float)(frame.bottom + 35 + 20), this.paint);
        } else {
            this.paint.setColor(this.errorHintColor);
            this.paint.setTextSize(36.0F);
            text = this.errorHint;
            canvas.drawText(this.errorHint, (float)(frame.centerX() - text.length() * 36 / 2), (float)(frame.bottom + 35 + 20), this.paint);
        }
    }

    private void drawScanner(Canvas canvas, Rect frame) {
        if (this.showPossiblePoint) {
            this.paint.setColor(this.laserColor);
            this.paint.setAlpha(SCANNER_ALPHA[this.scannerAlpha]);
            this.scannerAlpha = (this.scannerAlpha + 1) % SCANNER_ALPHA.length;
            int middle = frame.height() / 2 + frame.top;
            canvas.drawRect((float)(frame.left + 20), (float)(middle - 1), (float)(frame.right - 20), (float)(middle + 2), this.paint);
        } else {
            this.paint.setColor(Color.parseColor("#0099ff"));
            this.scannerAlpha = (this.scannerAlpha + 1) % SCANNER_ALPHA.length;
            canvas.translate(0.0F, this.translateY);
            canvas.drawRect((float)(frame.left + 10), (float)frame.top, (float)(frame.right - 10), (float)(frame.top + 10), this.paint);
            this.translateY += 12.0F;
            if (this.translateY >= 550.0F) {
                this.translateY = 5.0F;
            }
        }
    }

    private void drawPossiblePoint(Canvas canvas, Rect frame) {
        Collection<ResultPoint> currentPossible = this.possibleResultPoints;
        Collection<ResultPoint> currentLast = this.lastPossibleResultPoints;
        Iterator var5;
        ResultPoint point;
        if (currentPossible.isEmpty()) {
            this.lastPossibleResultPoints = null;
        } else {
            this.possibleResultPoints = new HashSet(5);
            this.lastPossibleResultPoints = currentPossible;
            this.paint.setAlpha(255);
            this.paint.setColor(this.resultPointColor);
            var5 = currentPossible.iterator();

            while(var5.hasNext()) {
                point = (ResultPoint)var5.next();
                canvas.drawCircle((float)frame.left + point.getX(), (float)frame.top + point.getY(), 6.0F, this.paint);
            }
        }

        if (currentLast != null) {
            this.paint.setAlpha(127);
            this.paint.setColor(this.resultPointColor);
            var5 = currentLast.iterator();

            while(var5.hasNext()) {
                point = (ResultPoint)var5.next();
                canvas.drawCircle((float)frame.left + point.getX(), (float)frame.top + point.getY(), 3.0F, this.paint);
            }
        }

    }
}
