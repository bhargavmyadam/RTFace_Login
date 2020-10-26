package com.example.iris.login1;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kevindeland on 1/6/18.
 *
 * This is a thread that plays the instructional tapping video (either English or Swahili) stored in R.raw
 */

public class PlayTappingVideo extends PlayVideoThread {

    // SurfaceHolder is used to show the video
    private SurfaceHolder surfaceHolder;

    private MediaPlayer mPlayer;
    private Handler mHandler;
    private Context context;
    private String language = "LANG_EN";
    private boolean isPlaying = true;

    public PlayTappingVideo(SurfaceHolder surfaceHolder,
                               Handler mhandler_, Context context) {
        this.surfaceHolder = surfaceHolder;
        this.context = context;
        this.mHandler = mhandler_;
    }

    @Override
    public void run() {
        try {
            getLanguage();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // set up the MediaPlayer
        if( language.equals(Common.LANG_EN) ) {
            // English version
            mPlayer = MediaPlayer.create(context,  R.raw.tapping_instruction_video_en);
        } else {
            // Swahili version
            mPlayer = MediaPlayer.create(context, R.raw.tapping_instruction_video_sw);
        }

        mPlayer.setDisplay(surfaceHolder);
        mPlayer.start();

        // send message to Handler / GalleryActivity
        mHandler.sendEmptyMessage(Common.UNCOVER_SCREEN);

        /* OnCompletionListener */
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mHandler.sendEmptyMessage(Common.PLAY_TAPPING_VIDEO_DONE);
                mPlayer.release();
                mPlayer = null;
            }
        });

        /* OnErrorListener */
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                mPlayer.reset();
                return false;
            }
        });
    }


    private void getLanguage() throws IOException, JSONException {
        String dataPath = "/sdcard/Download" + "/config.json";
        InputStream is = new FileInputStream(dataPath);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String myJson = new String(buffer, "UTF-8");
        JSONObject obj = new JSONObject(myJson);
        language = obj.getString("language_feature_id");
        Log.d("myJson",language);
    }



    @Override
    public boolean isPlayingVideo() {
        return isPlaying;
    }

    @Override
    public void stopPlayingVideo() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
