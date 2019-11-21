package com.vily.vediodemo1.camero.encode;

import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;

import androidx.annotation.RequiresApi;

import com.vily.vediodemo1.MainActivity;

import java.io.File;
import java.io.IOException;

/**
 *  * description :   录制视频
 *  * Author : Vily
 *  * Date : 2019-11-19
 *  
 **/
public class Camera2Record {

    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;

    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();
    private static Integer mSensorOrientation;

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    private static MediaRecorder mMediaRecorder;
    private static Camera2Record mCamera2Encode;


    private static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/zzzzz.mp4";

    public Camera2Record() {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
    }

    public static Camera2Record getInstance() {

        synchronized (Camera2Record.class) {

            if (mCamera2Encode == null) {
                mCamera2Encode = new Camera2Record();
            }
        }

        return mCamera2Encode;
    }

    public MediaRecorder getMediaRecord() {
        return mMediaRecorder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setUpMediaRecorder(Size mVideoSize, MainActivity activity) throws IOException {

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        File file = new File(path);

        mMediaRecorder.setOutputFile(file);
        mMediaRecorder.setVideoEncodingBitRate(100*10000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        switch (mSensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
                break;
        }
        mMediaRecorder.prepare();


    }


    public void setSensorOrientation(Integer sensorOrientation) {
        mSensorOrientation = sensorOrientation;
    }

    public void release() {
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }

        mCamera2Encode=null;

    }
}
