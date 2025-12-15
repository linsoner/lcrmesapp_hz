package com.dyg.siginprint.base.tools;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

public class QrUtils {
    private static byte[] yuvs;

    public QrUtils() {
    }

    public static byte[] getYUV420sp(int inputWidth, int inputHeight, Bitmap scaled) {
        int[] argb = new int[inputWidth * inputHeight];
        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);
        int requiredWidth = inputWidth % 2 == 0 ? inputWidth : inputWidth + 1;
        int requiredHeight = inputHeight % 2 == 0 ? inputHeight : inputHeight + 1;
        int byteLength = requiredWidth * requiredHeight * 3 / 2;
        if (yuvs != null && yuvs.length >= byteLength) {
            Arrays.fill(yuvs, (byte)0);
        } else {
            yuvs = new byte[byteLength];
        }

        encodeYUV420SP(yuvs, argb, inputWidth, inputHeight);
        scaled.recycle();
        return yuvs;
    }

    private static void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        int frameSize = width * height;
        int yIndex = 0;
        int uvIndex = frameSize;
        int argbIndex = 0;

        for(int j = 0; j < height; ++j) {
            for(int i = 0; i < width; ++i) {
                int R = (argb[argbIndex] & 16711680) >> 16;
                int G = (argb[argbIndex] & '\uff00') >> 8;
                int B = argb[argbIndex] & 255;
                ++argbIndex;
                int Y = (66 * R + 129 * G + 25 * B + 128 >> 8) + 16;
                int U = (-38 * R - 74 * G + 112 * B + 128 >> 8) + 128;
                int V = (112 * R - 94 * G - 18 * B + 128 >> 8) + 128;
                Y = Math.max(0, Math.min(Y, 255));
                U = Math.max(0, Math.min(U, 255));
                V = Math.max(0, Math.min(V, 255));
                yuv420sp[yIndex++] = (byte)Y;
                if (j % 2 == 0 && i % 2 == 0) {
                    yuv420sp[uvIndex++] = (byte)V;
                    yuv420sp[uvIndex++] = (byte)U;
                }
            }
        }

    }

    public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;

            for(int halfWidth = width / 2; halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth; inSampleSize *= 2) {
                ;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String imgPath, int reqWidth, int reqHeight) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgPath, options);
    }

    public static Result decodeImage(byte[] data, int width, int height) {
        Result result = null;

        try {
            Hashtable<DecodeHintType, Object> hints = new Hashtable();
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
            BinaryBitmap bitmap1 = new BinaryBitmap(new GlobalHistogramBinarizer(source));
            QRCodeReader reader2 = new QRCodeReader();
            result = reader2.decode(bitmap1, hints);
        } catch (ReaderException var8) {
            ;
        }

        return result;
    }

    public static Result decodeImage(String path) {
        Bitmap bitmap = decodeSampledBitmapFromFile(path, 256, 256);
        if (bitmap == null) {
            return null;
        } else {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            PlanarYUVLuminanceSource source1 = new PlanarYUVLuminanceSource(getYUV420sp(width, height, bitmap), width, height, 0, 0, width, height, false);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source1));
            HashMap<DecodeHintType, Object> hints = new HashMap();
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

            try {
                return (new MultiFormatReader()).decode(binaryBitmap, hints);
            } catch (NotFoundException var9) {
                var9.printStackTrace();
                return null;
            }
        }
    }

    //识别二维码图片
    public static String getImgResult(Activity activity, Intent data) {
        if (data != null) {
            Uri inputUri = data.getData();
            String path = null;
            if (URLUtil.isFileUrl(inputUri.toString())) {
                path = inputUri.getPath();
            } else {
                String[] proj = new String[]{"_data"};
                Cursor cursor = activity.getContentResolver().query(inputUri, proj, (String) null, (String[]) null, (String) null);
                if (cursor != null && cursor.moveToFirst()) {
                    path = cursor.getString(cursor.getColumnIndex("_data"));
                }
            }
            if (!TextUtils.isEmpty(path)) {
                Result result = decodeImage(path);
                if (result != null) {
                    return result.getText();
                } else {
                    Toast.makeText(activity,"此图片无法识别", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity,"图片路径未找到", Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }
    //获取扫描二维码结果
    public static String getScanResult(Intent data) {
        String result = null;
        if (data != null) {
            try {
                result = data.getExtras().getString("result").trim();
            } catch (Exception e) {
            }
        }
        return result;
    }

}
