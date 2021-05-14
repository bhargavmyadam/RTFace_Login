package com.example.iris.login1;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import cmu.xprize.comp_logging.CLogManager;

/**
 * Created by Iris on 16/7/25.
 * Edited by Kevin DeLand on other dates.
 *
 * This is a thread that plays the enrollment video of one of the students
 */
//TODO need to make files to play all needed videos?
public class PlayEnrollmentVideo extends PlayVideoThread {
    private int realStartTime;
    // SurfaceHolder is used to show the video
    private SurfaceHolder surfaceHolder;
    public String vPath = "";
    public String pPath = "";
    private MediaPlayer mPlayer;
    private Handler mHandler;
    private Context context;

    private boolean isPlaying = true;

    static final String TAG = "CRTFaceLogin";

    public PlayEnrollmentVideo(SurfaceHolder surfaceHolder,
                               Handler mhandler_, Context context, String vPath_, String pPath_, int realStartTime) {
        this.realStartTime = realStartTime;
        this.surfaceHolder = surfaceHolder;
        this.vPath = vPath_;
        this.pPath = pPath_;
        this.context = context;
        this.mHandler = mhandler_;
    }

    @Override
    public void run() {

        // set up the MediaPlayer
        mPlayer = new MediaPlayer();

        final Surface surface = surfaceHolder.getSurface();
        if (surface == null || !surface.isValid()) {
            CLogManager.getInstance().postEvent_E(TAG, "PlayEnrollmentVideo:" + "Failed to create/prepare MediaPlayer Object. Surface Released!");
            Log.e("PlayEnrollmentVideo", "Failed to create/prepare MediaPlayer Object. Surface Released!");
            return;
        }

        try {
            mPlayer.setDisplay(surfaceHolder);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(vPath);
            mPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            CLogManager.getInstance().postEvent_E(TAG, "PlayEnrollmentVideo:" + "Failed to set data source for MediaPlayer.");
            Log.e("PlayEnrollmentVideo", "Failed to set data source for MediaPlayer.");
            e.printStackTrace();
            return;
        } catch (IOException e) {
            CLogManager.getInstance().postEvent_E(TAG, "PlayEnrollmentVideo:" + "Unexpected error occurred while configuring MediaPlayer Object.");
            Log.e("PlayEnrollmentVideo", "Unexpected error occurred while configuring MediaPlayer Object.");
            e.printStackTrace();
            return;
        } catch (NullPointerException e) {
            CLogManager.getInstance().postEvent_E(TAG, "PlayEnrollmentVideo:" + "Failed to create/prepare MediaPlayer Object.");
            Log.e("PlayEnrollmentVideo", "Failed to create/prepare MediaPlayer Object.");
            e.printStackTrace();
            return;
        }

        if (mPlayer != null) {
            /* Other listeners... not sure what for **/
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mPlayer.seekTo(realStartTime);
                    isPlaying = true;
                }
            });

            mPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                public void onSeekComplete(MediaPlayer m) {
                    if (isPlaying) {
                        mPlayer.start();
                        mHandler.sendEmptyMessage(Common.UNCOVER_SCREEN);
                        isPlaying = false;
                    }
                }
            });

            /* OnCompletionListener **/
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer arg0) {
                    if (mPlayer != null) {
                        mPlayer.seekTo(realStartTime + mPlayer.getDuration()); // set frame shown to last frame of video
                    }
                    mHandler.sendEmptyMessage(Common.REPLAY_EXISTING_VIDEO_DONE);      //after replay,login kids in
                }
            });

            /* OnErrorListener **/
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    mPlayer.reset();
                    return false;
                }
            });
        }
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
