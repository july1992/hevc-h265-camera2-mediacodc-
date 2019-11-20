package com.vily.vediodemo1.camero.decode;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.SurfaceHolder;

import com.vily.vediodemo1.MyConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/**
 *  * description :  通过 medecodec 去创建解码器，解码输出后会自动渲染到surface页面
 *  * Author : Vily
 *  * Date : 2019/1/7
 *  
 **/
public class CameraRecordDecoder {

    private static final String TAG = "CameraRecordDecoder";


    //解码器
    private MediaCodec mCodec0;
    private boolean isFinish = false;  // 是否解码结束

    private static final int FRAME_MAX_LEN = 300 * 1024;
    //根据帧率获取的解码每帧需要休眠的时间,根据实际帧率进行操作
    private int PRE_FRAME_TIME = 1000 / 25;

    //保存完整数据帧
    private byte[] frame = new byte[FRAME_MAX_LEN];
    private int frameNum;
    private String mPath;
    private boolean isExit = false;
    private DecoderThread mDecoderThread;

    public CameraRecordDecoder() {
        nalu = new NaluUnit();
    }

    // 初始化编码器
    public void initCameraDecode( SurfaceHolder holder) {
        try {
            //根据需要解码的类型创建解码器
            mCodec0 = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_HEVC); //"video/hevc"
        } catch (IOException e) {
            e.printStackTrace();
        }
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, MyConfig.width, MyConfig.height);

        //SurfaceView
        mCodec0.configure(mediaFormat, holder.getSurface(), null, 0); //直接解码送surface显示
        //开始解码
        mCodec0.start();

    }


    // 如果不走本地文件里读取数据，传进来到是实时预览帧，就用这个方法
    public void onFrame(byte[] buf, int offset, int length) {
        //-1表示一直等待；0表示不等待；其他大于0的参数表示等待毫秒数
        //Log.e(TAG,"        onFrame start       ");

        Log.i(TAG, "onFrame: ---------isfinish:" + isFinish);


        try {
            if (mCodec0 != null && !isFinish) {
                int inputBufferIndex = mCodec0.dequeueInputBuffer(-1);
                if (!isFinish && inputBufferIndex >= 0) {
                    ByteBuffer inputBuffer = mCodec0.getInputBuffer(inputBufferIndex);
                    inputBuffer.clear();
                    inputBuffer.put(buf, offset, length);
                    //解码
                    long timestamp = mCount0 * 1000000 / 25;
                    mCodec0.queueInputBuffer(inputBufferIndex, 0, length, timestamp, 0);
                    mCount0++;
                }

                //Log.e(TAG,"        onFrame middle      ");
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                int outputBufferIndex = mCodec0.dequeueOutputBuffer(bufferInfo, 0); //10
                //循环解码，直到数据全部解码完成
                while (outputBufferIndex >= 0 && !isFinish) {
                    //logger.d("outputBufferIndex = " + outputBufferIndex);
                    mCodec0.releaseOutputBuffer(outputBufferIndex, true);
                    outputBufferIndex = mCodec0.dequeueOutputBuffer(bufferInfo, 0);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

            release();
        }

    }


    // 如果调本地文件就走这个方法，开始解码
    public void decodeStart(String path) {
        mPath = path;
        Log.i(TAG, "decodeStart: ---------这里ma");
        mDecoderThread = new DecoderThread(mPath, new WeakReference<CameraRecordDecoder>(this));
        mDecoderThread.start();
    }


    //  解码线程
    private class DecoderThread extends Thread {

        private WeakReference<CameraRecordDecoder> encoder;  // 添加弱引用
        private File file = null;
        private boolean findFlag = false;
        private boolean isExit;
        private MediaCodec mMediaCodec;
        int readlen = 0;
        int writelen = 0;
        int i = 0;
        int pos = 0;
        int index = 0;
        int index0 = 0;

        public DecoderThread(String mPath, WeakReference<CameraRecordDecoder> encoder) {

            mMediaCodec = encoder.get().mCodec0;

            file = new File(mPath);
            if (!file.exists() || !file.canRead()) {
                Log.e(TAG, "failed to open h265 file.");
                return;
            }

        }

        public void exit() {
            isExit = true;
        }

        @Override
        public void run() {
            super.run();
            isExit = false;
//            mMediaCodec.start();
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            //每次从文件读取的数据
            frameNum = 0;
            long startTime = System.currentTimeMillis();
            while (!isExit) {    // 循环解码并播放

                try {
                    if (fis.available() > 0) {
                        readlen = fis.read(frame, pos, frame.length - pos);
                        if (readlen <= 0) {
                            break;
                        }
                        readlen += pos;

                        i = 0;
                        pos = 0;
                        writelen = readlen;

                        //while(i < readlen-4) {
                        Log.i(TAG, "run: --------finish:");
                        for (i = 0; i < readlen - 4; i++) {
                            findFlag = false;
                            index = 0;
                            index0 = 0;
                            if (frame[i] == 0x00 && frame[i + 1] == 0x00 && frame[i + 2] == 0x01) {
                                pos = i;
                                if (i > 0) {
                                    if (frame[i - 1] == 0x00) { //start with 0x00 0x00 0x00 0x01
                                        index = 1;
                                    }
                                }
                                //while (pos < readlen-4) {
                                for (pos = i + 3; pos < readlen - 4; pos++) {
                                    if (frame[pos] == 0x00 && frame[pos + 1] == 0x00 && frame[pos + 2] == 0x01) {
                                        findFlag = true;
                                        if (frame[pos - 1] == 0x00) {//start with 0x00 0x00 0x00 0x01
                                            index0 = 1;
                                        }
                                        break;
                                    }
                                }

                                Log.i(TAG, "run: ---------findFlag:" + findFlag + "----exit:" + isExit);

                                if (findFlag && !isExit) {
                                    nalu.type = (frame[i + 3] & 0x7E) >> 1;
                                    if (index == 1) {
                                        i = i - 1;
                                    }
                                    if (index0 == 1) {
                                        pos = pos - 1;
                                    }
                                    if (isExit) {
                                        return;
                                    }
                                    // 下面是解码操作
                                    //-------------------------------------------//
                                    if (mMediaCodec != null) {
                                        int inputBufferIndex = mMediaCodec.dequeueInputBuffer(-1);


                                        if (inputBufferIndex >= 0 && !isExit) {
                                            ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(inputBufferIndex);
                                            inputBuffer.clear();
                                            inputBuffer.put(frame, i, pos - i);
                                            //解码
                                            long timestamp = mCount0 * 1000000 / 25;
                                            mMediaCodec.queueInputBuffer(inputBufferIndex, 0, pos - i, timestamp, 0);
                                            mCount0++;
                                        }
                                    }
                                    //Log.e(TAG,"        onFrame middle      ");
                                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                                    int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0); //10
                                    //循环解码，直到数据全部解码完成
                                    while (!isExit && outputBufferIndex >= 0) {
                                        //logger.d("outputBufferIndex = " + outputBufferIndex);
                                        mMediaCodec.releaseOutputBuffer(outputBufferIndex, true);
                                        outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
                                    }
                                    //--------------------------------------------//
                                    i = pos;
                                    writelen = i;

                                    Log.i(TAG, " nalu type = " + nalu.type + ", nalu.size = " + i);

                                    frameNum++;
                                    //Log.i(TAG," frameNum = "+frameNum);
                                    long time = PRE_FRAME_TIME - (System.currentTimeMillis() - startTime);
                                    if (time > 0) {
                                        try {
                                            Thread.sleep(time);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    startTime = System.currentTimeMillis();
                                } else {
                                    writelen = i;
                                    break;
                                }
                            }
                        }

                        if (writelen > 0 && writelen < readlen) {
                            System.arraycopy(frame, writelen, frame, 0, readlen - writelen);
                            //Log.i(TAG, " readlen = "+readlen+", writelen = "+writelen);
                        }

                        pos = readlen - writelen;
                    } else {
                        isFinish = true;
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.e(TAG, "  error =  " + e.getMessage());
                    e.printStackTrace();
                }
            }

            Log.i(TAG, "         frameNum     " + frameNum);
        }


    }

    NaluUnit nalu;

    public class NaluUnit {
        byte[] data;
        int size;
        int type;

        public NaluUnit() {
            data = new byte[20 * 1024];
            size = 0;
        }
    }

    int mCount0 = 0;


    public void decodeStop() {
        if (mDecoderThread != null) {
            mDecoderThread.exit();
            mDecoderThread = null;
        }
    }

    public void stopPlay(boolean isStop) {
        isFinish = isStop;

//        if(!isStop){
//            mCodec0.flush();
//        }
    }

    public void release() {

        isFinish=true;
        mCodec0.stop();
        mCodec0.release();
        mCodec0=null;
    }

}
