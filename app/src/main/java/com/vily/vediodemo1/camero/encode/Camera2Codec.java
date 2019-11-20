package com.vily.vediodemo1.camero.encode;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;

import androidx.annotation.RequiresApi;

import com.vily.vediodemo1.MainActivity;
import com.vily.vediodemo1.MyConfig;
import com.vily.vediodemo1.cache.CacheReadThread;
import com.vily.vediodemo1.camero.utils.Camera2Utils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *  * description :  硬编码
 *  * Author : Vily
 *  * Date : 2019-11-19
 *  
 **/
public class Camera2Codec {

    private static final String TAG = "Camera2Codec";

    private static Camera2Codec mCamera2Codec;
    private MediaCodec videoEncodec;


    private static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/zzzzz.mp4";
    private MediaFormat mVideoFormat;
    private Surface mEncodeSurface;
    private MediaCodec.BufferInfo mVideoBufferinfo;
    private boolean flag;

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



    public Camera2Codec() {
        flag=true;

    }

    public static Camera2Codec getInstance() {

        synchronized (Camera2Codec.class) {

            if (mCamera2Codec == null) {
                mCamera2Codec = new Camera2Codec();
            }
        }

        return mCamera2Codec;
    }


    public void initCodec() {
        try {

            mVideoBufferinfo = new MediaCodec.BufferInfo();
            mVideoFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, MyConfig.width, MyConfig.height);
            mVideoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            mVideoFormat.setInteger(MediaFormat.KEY_BIT_RATE, MyConfig.bitRate);
            mVideoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, MyConfig.frame);
            mVideoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);


            videoEncodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_HEVC);

            videoEncodec.configure(mVideoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            mEncodeSurface = videoEncodec.createInputSurface();

            videoEncodec.start();

        } catch (IOException e) {
            e.printStackTrace();

            videoEncodec = null;
            mVideoFormat = null;
            mVideoBufferinfo = null;
        }
    }

    private long pts;
    private byte[] sps;
    private byte[] pps;


    public void encode() {
        pts = 0;



        while (flag) {

            Log.i(TAG, "encode: ------flag:"+flag);


            int outputBufferIndex = videoEncodec.dequeueOutputBuffer(mVideoBufferinfo, 0);

            Log.i(TAG, "outputBufferIndex: ------:" + outputBufferIndex);

//            if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                Log.d(TAG, "INFO_OUTPUT_FORMAT_CHANGED-----------------");
//            }
//
//            ByteBuffer spsb = videoEncodec.getOutputFormat().getByteBuffer("csd-0");
//            sps = new byte[spsb.remaining()];
//            spsb.get(sps, 0, sps.length);

//                    ByteBuffer ppsb = videoEncodec.getOutputFormat().getByteBuffer("csd-1");
//                    pps = new byte[ppsb.remaining()];
//                    ppsb.get(pps, 0, pps.length);


            if (outputBufferIndex >= 0) {

                ByteBuffer outputBuffer = videoEncodec.getOutputBuffers()[outputBufferIndex];
                outputBuffer.position(mVideoBufferinfo.offset);
                outputBuffer.limit(mVideoBufferinfo.offset + mVideoBufferinfo.size);
                //
                if (pts == 0) {
                    pts = mVideoBufferinfo.presentationTimeUs;
                }
                mVideoBufferinfo.presentationTimeUs = mVideoBufferinfo.presentationTimeUs - pts;


                byte[] data = new byte[outputBuffer.remaining()];
                outputBuffer.get(data, 0, data.length);


                if (mVideoBufferinfo.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {

                    Log.i(TAG, "encode: -------是key frame");
                }

                Log.i(TAG, "encode: ------:" + data.length);

                CacheReadThread.getInstance().addData(data);

                videoEncodec.releaseOutputBuffer(outputBufferIndex, false);
                outputBufferIndex = videoEncodec.dequeueOutputBuffer(mVideoBufferinfo, 0);

            }
        }
    }



    public Surface getEncodeSurface() {

        return mEncodeSurface;
    }

    public void release() {
        Log.i(TAG, "release: ------销毁Camera2Codec");
        flag=false;


        if (videoEncodec != null) {
            videoEncodec.stop();
            videoEncodec.release();
            videoEncodec = null;

        }

        if (mVideoBufferinfo != null) {

            mVideoBufferinfo = null;
        }

        if (mVideoFormat != null) {
            mVideoFormat = null;
        }

        if(mCamera2Codec!=null){
            mCamera2Codec=null;
        }


    }


}
