package com.dyg.siginprint.base.tools;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;

import java.io.IOException;

/**
 * 播放raw中的声音
 * 作者：DC-DingYG on 2018-05-29 18:16
 * 邮箱：dingyg012655@126.com
 */
public class SoundUtil {
    // 上下文
    static Context mContext;
    static SoundUtil soundUtil;
    MediaPlayer mediaPlayer;

    public SoundUtil(Context context) {
        mContext = context;
    }

    public static SoundUtil getInstense(Context context) {
        if (soundUtil == null) {
            soundUtil = new SoundUtil(context);
        }
        return soundUtil;
    }
    //开始播放
    public void pay(int rawId) {
        mediaPlayer = MediaPlayer.create(mContext.getApplicationContext(), rawId);
        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stop();
            }
        }, 3000);
        mediaPlayer.start();
    }
    public void stop() {
        try {
            if (mediaPlayer != null) mediaPlayer.stop();
        }catch (Exception e){}
    }
}
