package com.vily.vediodemo1.voice;

import android.media.AudioRecord;
import android.util.Log;

/**
 *  * description :    对音频进行40倍压缩  现在640一包，压缩后是26一包
 *  * Author : Vily
 *  * Date : 2020-01-17
 *  
 **/
public class CallAudio extends Thread {

    private static final String TAG = "CallAudio";
    private static final int frequency = 8000;
    private static final int channelConfiguration = 16;
    private static final int audioEncoding = 2;
    private int recBufSize = AudioRecord.getMinBufferSize(8000, 16, 2);
//    private int recBufSize = 1000;
    private AudioRecord audioRecord = null;
    private boolean isFlag = false;
    private RecordListener recordListener;

    public CallAudio() {
        int minBufferSize = AudioRecord.getMinBufferSize(8000, 16, 2);
        Log.i(TAG, "CallAudio: ---:"+minBufferSize);
        this.audioRecord = new AudioRecord(1, 8000, 16, 2, this.recBufSize);

    }

    public void init(RecordListener listener) {
        this.recordListener = listener;
        this.audioRecord.startRecording();

        this.isFlag = true;
    }


    public void release() {
        this.isFlag = false;
        if (this.audioRecord != null) {
            this.audioRecord.release();
        }

    }

    public void run() {
        try {
            byte[] buffer = new byte[this.recBufSize];


            while (true) {
                if (this.isFlag) {
                    this.audioRecord.read(buffer, 0, buffer.length);
                    if (this.recordListener != null) {
                        this.recordListener.onRecord(buffer);
                    }
                }
            }
        } catch (Exception var2) {
            var2.printStackTrace();
            this.isFlag = false;
        }

        this.isFlag = false;
    }

    public void stopRecord() {
        this.isFlag = false;
    }

    public void restartCall() {
        this.isFlag = true;
    }

    public interface RecordListener {
        void onRecord(byte[] var1);
    }
}
