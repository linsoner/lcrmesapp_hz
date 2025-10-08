package com.dingyg.qrcodelib.zxing.decoding;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;

public final class FinishListener implements OnClickListener, OnCancelListener, Runnable {
    private final Activity activityToFinish;

    public FinishListener(Activity activityToFinish) {
        this.activityToFinish = activityToFinish;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.run();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.run();
    }

    public void run() {
        this.activityToFinish.finish();
    }
}
