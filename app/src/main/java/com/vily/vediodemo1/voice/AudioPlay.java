package com.vily.vediodemo1.voice;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.provider.MediaStore;
import android.util.Log;

import com.vily.vediodemo1.cache.CacheBuffer;

/**
 *  * description : 
 *  * Author : Vily
 *  * Date : 2020-01-17
 *  
 **/
public class AudioPlay  extends Thread  {

    private static final String TAG = "AudioPlay";
    private static final int frequency = 8000;
    private static final int channelConfiguration = 16;
    private static final int audioEncoding = 2;
//    private int playBufSize = AudioTrack.getMinBufferSize(8000, 4, 2);
    private int playBufSize =1000;
    private AudioTrack audioTrack = null;
    private boolean isFlag = false;
    private CacheBuffer<byte[]> audioBuffer;

    public AudioPlay() {
//        AudioManager.STREAM_MUSIC
        int minBufferSize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, 2);
        Log.i(TAG, "AudioPlay: --:"+minBufferSize);

        this.audioTrack = new AudioTrack(AudioTrack.MODE_STREAM, 8000, 4, 2, this.playBufSize, 1);
        this.audioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
        this.isFlag = true;
        this.audioBuffer = new CacheBuffer();

        this.audioTrack.play();

    }



    public void addData(byte[] buffer) {
//        this.audioTrack.write(buffer, 0, buffer.length);
        this.audioBuffer.addQueue(buffer);
    }
    public void startPlay() {
        this.audioTrack.play();
        this.isFlag = true;
    }
    public void stopPlay() {
        audioTrack.pause();
        this.isFlag = false;
    }


    @Override
    public void run() {
        super.run();

        try {

            while (true){
                for(; this.isFlag; sleep(10L)) {
                    byte[] buffer =  this.audioBuffer.deQueue();

                    if (buffer != null && buffer.length > 0) {
                        Log.i(TAG, "run: ----:"+buffer.length);
                        this.audioTrack.write(buffer, 0, buffer.length);
                    }
                }


            }

        }catch (Exception e){
            e.printStackTrace();
            this.isFlag=false;
        }
    }


    public void release() {
        this.isFlag = false;
        if (this.audioTrack != null) {
            this.audioTrack.release();
        }
    }

}
