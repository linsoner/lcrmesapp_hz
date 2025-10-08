package com.dingyg.qrcodelib.zxing.decoding;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dingyg.qrcodelib.zxing.CaptureActivity;
import com.dingyg.qrcodelib.zxing.camera.CameraManager;
import com.dingyg.qrcodelib.zxing.view.ViewfinderResultPointCallback;
import com.soonfor.repository.R;
import com.soonfor.repository.R.id;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Vector;

public final class CaptureActivityHandler extends Handler {
    private static final String TAG = CaptureActivityHandler.class.getSimpleName();
    private final CaptureActivity activity;
    private final DecodeThread decodeThread;
    private CaptureActivityHandler.State state;

    public CaptureActivityHandler(CaptureActivity activity, Vector<BarcodeFormat> decodeFormats, String characterSet) {
        this.activity = activity;
        this.decodeThread = new DecodeThread(activity, decodeFormats, characterSet, new ViewfinderResultPointCallback(activity.getViewfinderView()));
        this.decodeThread.start();
        this.state = CaptureActivityHandler.State.SUCCESS;
        CameraManager.get().startPreview();
        this.restartPreviewAndDecode();
    }

    @SuppressLint("WrongConstant")
    public void handleMessage(Message message) {
        if (message.what == R.id.auto_focus) {
            if (this.state == CaptureActivityHandler.State.PREVIEW) {
                CameraManager.get().requestAutoFocus(this, id.auto_focus);
            }
        } else if (message.what == R.id.restart_preview) {
            Log.d(TAG, "Got restart preview message");
            this.restartPreviewAndDecode();
        } else if (message.what == R.id.decode_succeeded) {
            Log.d(TAG, "Got decode succeeded message");
            this.state = CaptureActivityHandler.State.SUCCESS;
            Bundle bundle = message.getData();
            Bitmap barcode = bundle == null ? null : (Bitmap)bundle.getParcelable("barcode_bitmap");
            this.activity.handleDecode((Result)message.obj, barcode);
        } else if (message.what == R.id.decode_failed) {
            this.state = CaptureActivityHandler.State.PREVIEW;
            CameraManager.get().requestPreviewFrame(this.decodeThread.getHandler(), id.decode);
        } else if (message.what == R.id.return_scan_result) {
            Log.d(TAG, "Got return scan result message");
            this.activity.setResult(-1, (Intent)message.obj);
            this.activity.finish();
        } else if (message.what == R.id.launch_product_query) {
            Log.d(TAG, "Got product query message");
            String url = (String)message.obj;
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
            intent.addFlags(524288);
            this.activity.startActivity(intent);
        }

    }

    public void quitSynchronously() {
        this.state = CaptureActivityHandler.State.DONE;
        CameraManager.get().stopPreview();
        Message quit = Message.obtain(this.decodeThread.getHandler(), id.quit);
        quit.sendToTarget();

        try {
            this.decodeThread.join();
        } catch (InterruptedException var3) {
            ;
        }

        this.removeMessages(id.decode_succeeded);
        this.removeMessages(id.decode_failed);
    }

    private void restartPreviewAndDecode() {
        if (this.state == CaptureActivityHandler.State.SUCCESS) {
            this.state = CaptureActivityHandler.State.PREVIEW;
            CameraManager.get().requestPreviewFrame(this.decodeThread.getHandler(), id.decode);
            CameraManager.get().requestAutoFocus(this, id.auto_focus);
            this.activity.drawViewfinder();
        }

    }

    private static enum State {
        PREVIEW,
        SUCCESS,
        DONE;

        private State() {
        }
    }
}
