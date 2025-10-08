package com.dingyg.qrcodelib.zxing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;

import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dingyg.qrcodelib.common.QrUtils;
import com.dingyg.qrcodelib.zxing.camera.CameraManager;
import com.dingyg.qrcodelib.zxing.decoding.CaptureActivityHandler;
import com.dingyg.qrcodelib.zxing.decoding.InactivityTimer;
import com.dingyg.qrcodelib.zxing.view.ViewfinderView;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.soonfor.repository.R;

import java.io.IOException;
import java.util.Vector;


public class CaptureActivity extends Activity implements Callback {
    private static final String TAG = CaptureActivity.class.getSimpleName();
    public static final int REQUEST_PERMISSION_CAMERA = 3999;
    public static final int REQUEST_PERMISSION_PHOTO = 4000;
    private CaptureActivity mActivity;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.1F;
    private boolean vibrate;
    private boolean flashLightOpen = false;
    private ImageView backIbtn;
    private ImageButton flashIbtn;
    private TextView galleryTv;
    private static final long VIBRATE_DURATION = 200L;
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    public CaptureActivity() {
    }

    @SuppressLint("WrongConstant")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(1);
        this.mActivity = CaptureActivity.this;
        this.hasSurface = false;
        this.inactivityTimer = new InactivityTimer(this);
        CameraManager.init(mActivity);
        if (VERSION.SDK_INT >= 23 && this.checkSelfPermission("android.permission.CAMERA") != 0) {
            this.requestPermissions(new String[]{"android.permission.CAMERA"}, 1000);
        }
        PermissionsUtil.requestPermission(this, new PermissionListener() {
                    @Override
                    public void permissionGranted(@NonNull String[] permissions) {
                        initView();
                    }

                    @Override
                    public void permissionDenied(@NonNull String[] permissions) {
                        finish();
                    }
                }, Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }

    protected void onResume() {
        super.onResume();
        Log.d(TAG, "xxxxxxxxxxxxxxxxxxxonResume");
        SurfaceView surfaceView = (SurfaceView)this.findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (this.hasSurface) {
            this.initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(3);
        }

        this.decodeFormats = null;
        this.characterSet = null;
        this.playBeep = true;
        @SuppressLint("WrongConstant") AudioManager audioService = (AudioManager)this.getSystemService("audio");
        if (audioService.getRingerMode() != 2) {
            this.playBeep = false;
        }

        this.initBeepSound();
        this.vibrate = true;
    }

    protected void onPause() {
        super.onPause();
        Log.d(TAG, "xxxxxxxxxxxxxxxxxxxonPause");
        if (this.handler != null) {
            this.handler.quitSynchronously();
            this.handler = null;
        }

        if (this.flashIbtn != null) {
            this.flashIbtn.setImageResource(R.mipmap.ic_flash_off_white_24dp);
        }

        CameraManager.get().closeDriver();
    }

    protected void onDestroy() {
        this.inactivityTimer.shutdown();
        super.onDestroy();
    }

    @SuppressLint("WrongConstant")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && data != null && requestCode == 1000) {
            Uri inputUri = data.getData();
            String path = null;
            if (URLUtil.isFileUrl(inputUri.toString())) {
                path = inputUri.getPath();
            } else {
                String[] proj = new String[]{"_data"};
                Cursor cursor = this.getContentResolver().query(inputUri, proj, (String)null, (String[])null, (String)null);
                if (cursor != null && cursor.moveToFirst()) {
                    path = cursor.getString(cursor.getColumnIndex("_data"));
                }
            }

            if (!TextUtils.isEmpty(path)) {
                Result result = QrUtils.decodeImage(path);
                if (result != null) {
                    this.handleDecode(result, (Bitmap)null);
                } else {
                    (new Builder(this)).setTitle("提示").setMessage("此图片无法识别").setPositiveButton("确定", (OnClickListener)null).show();
                }
            } else {
                Toast.makeText(this.mActivity, "图片路径未找到", 0).show();
            }
        }

    }


    public void handleDecode(Result result, Bitmap barcode) {
        this.inactivityTimer.onActivity();
        this.playBeepSoundAndVibrate();
        String resultString = result.getText();
        this.handleResult(resultString);
    }

    protected void handleResult(String resultString) {
        if (resultString.equals("")) {
            Toast.makeText(this, R.string.scan_failed, Toast.LENGTH_SHORT).show();
        } else {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("result", resultString);
            resultIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK, resultIntent);
        }
        mActivity.finish();
    }

    protected void initView() {
        this.requestWindowFeature(1);
        this.getWindow().addFlags(1024);
        this.setContentView(R.layout.qr_camera);
        this.backIbtn = (ImageView)this.findViewById(R.id.back_ibtn);
        this.viewfinderView = (ViewfinderView)this.findViewById(R.id.viewfinder_view);
        this.flashIbtn = (ImageButton)this.findViewById(R.id.flash_ibtn);
        this.galleryTv = (TextView)this.findViewById(R.id.gallery_tv);
        this.backIbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CaptureActivity.this.mActivity.finish();
            }
        });
        this.flashIbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (CaptureActivity.this.flashLightOpen) {
                    CaptureActivity.this.flashIbtn.setImageResource(R.mipmap.ic_flash_off_white_24dp);
                } else {
                    CaptureActivity.this.flashIbtn.setImageResource(R.mipmap.ic_flash_on_white_24dp);
                }

                CaptureActivity.this.toggleFlashLight();
            }
        });
        this.galleryTv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CaptureActivity.this.openGallery();
            }
        });
    }

    protected void setViewfinderView(ViewfinderView view) {
        this.viewfinderView = view;
    }

    public void toggleFlashLight() {
        if (this.flashLightOpen) {
            this.setFlashLightOpen(false);
        } else {
            this.setFlashLightOpen(true);
        }

    }

    public void setFlashLightOpen(boolean open) {
        if (this.flashLightOpen != open) {
            this.flashLightOpen = !this.flashLightOpen;
            CameraManager.get().setFlashLight(open);
        }
    }

    public boolean isFlashLightOpen() {
        return this.flashLightOpen;
    }

    @SuppressLint("WrongConstant")
    public void openGallery() {
        if (VERSION.SDK_INT >= 23 && this.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
            this.requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1001);
        } else {
            Intent i = new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            mActivity.startActivityForResult(i, 1000);
        }

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException var3) {
            return;
        } catch (RuntimeException var4) {
            return;
        }

        if (this.handler == null) {
            this.handler = new CaptureActivityHandler(this, this.decodeFormats, this.characterSet);
        }

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (!this.hasSurface) {
            this.hasSurface = true;
            this.initCamera(holder);
        }

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return this.viewfinderView;
    }

    public Handler getHandler() {
        return this.handler;
    }

    public void drawViewfinder() {
        this.viewfinderView.drawViewfinder();
    }

    protected void restartPreview() {
        if (this.handler != null) {
            Message restartMessage = Message.obtain();
            restartMessage.what = R.id.restart_preview;
            this.handler.handleMessage(restartMessage);
        }

    }

    private void initBeepSound() {
        if (this.playBeep && this.mediaPlayer == null) {
            this.setVolumeControlStream(3);
            this.mediaPlayer = new MediaPlayer();
            this.mediaPlayer.setAudioStreamType(3);
            this.mediaPlayer.setOnCompletionListener(this.beepListener);
            AssetFileDescriptor file = this.getResources().openRawResourceFd(R.raw.scan_beep);

            try {
                this.mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                this.mediaPlayer.setVolume(0.1F, 0.1F);
                this.mediaPlayer.prepare();
            } catch (IOException var3) {
                this.mediaPlayer = null;
            }
        }

    }

    @SuppressLint("MissingPermission")
    private void playBeepSoundAndVibrate() {
        if (this.playBeep && this.mediaPlayer != null) {
            this.mediaPlayer.start();
        }

        if (this.vibrate) {
            @SuppressLint("WrongConstant") Vibrator vibrator = (Vibrator) this.getSystemService("vibrator");
            vibrator.vibrate(200L);
        }
    }


    public static void startCaptureActivity(Activity activity, int requestCode){
        Intent intent = new Intent(activity, CaptureActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }
}

