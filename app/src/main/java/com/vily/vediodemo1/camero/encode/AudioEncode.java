package com.vily.vediodemo1.camero.encode;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.net.rtp.AudioCodec;
import android.util.Log;

import com.vily.vediodemo1.MyConfig;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 *  * description : 
 *  * Author : Vily
 *  * Date : 2020-01-19
 *  
 **/
public class AudioEncode {

    private static final String TAG = "AudioEncode";

    private static AudioEncode mAudioEncode;
    private MediaCodec.BufferInfo mBufferInfo;
    private MediaFormat mAudioFormat;
    private MediaCodec mAudioCodec;

    public AudioEncode() {

    }

    public static AudioEncode getInstance() {

        synchronized (Camera2Codec.class) {

            if (mAudioEncode == null) {
                mAudioEncode = new AudioEncode();
            }
        }

        return mAudioEncode;
    }

    public void initAudioCodec() {
        try {

            mBufferInfo = new MediaCodec.BufferInfo();
            mAudioFormat = new MediaFormat();
            mAudioFormat.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_AUDIO_AAC);//音频编码
            mAudioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 16); //声道数（这里是数字）
            mAudioFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, 8000); //采样率
            mAudioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 25); //码率
            mAudioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);

            mAudioCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
            mAudioCodec.configure(mAudioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);// MediaCodec.CONFIGURE_FLAG_ENCODE 标识为编码器

            mAudioCodec.start();



        } catch (IOException e) {
            e.printStackTrace();

            mBufferInfo = null;
            mAudioFormat = null;
            mAudioCodec = null;
        }
    }

    public void hardEncoder(final byte[] buffer1, final int length) {

        //这里的处理就是和之前传送带取盒子放原料的流程一样了，注意一般在子线程中循环处理
        int index=mAudioCodec.dequeueInputBuffer(-1);//拿空盒子，index 拿到的盒子序号
        if(index>=0){
            final ByteBuffer buffer=mAudioCodec.getInputBuffer(index);
            buffer.clear();
            buffer.put(buffer1);
            if(length>0){
                mAudioCodec.queueInputBuffer(index,0,length,System.nanoTime()/1000,0);//往空盒子里塞要编码的数据
            }
        }
        MediaCodec.BufferInfo mInfo=new MediaCodec.BufferInfo();
        int outIndex;
        //每次取出的时候，把所有加工好的都循环取出来
        do{
            outIndex=mAudioCodec.dequeueOutputBuffer(mInfo,0);//取出已经编码好的数据，outIndex 表示盒子的位置
            if(outIndex>=0){
                ByteBuffer buffer=mAudioCodec.getOutputBuffer(outIndex);
                buffer.position(mInfo.offset);
                //AAC编码，需要加数据头，AAC编码数据头固定为7个字节
                byte[] temp=new byte[mInfo.size+7];
                buffer.get(temp,7,mInfo.size);
                addADTStoPacket(temp,temp.length, 8000, 16);//temp是处理后的acc数据，你可以把temp存储成一个文件就是一个acc的音频文件


                Log.i(TAG, "hardEncoder: ----:"+temp.length+"--:"+ Arrays.toString(temp));

                mAudioCodec.releaseOutputBuffer(outIndex,false);
            }else if(outIndex ==MediaCodec.INFO_TRY_AGAIN_LATER){

            }else if(outIndex==MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){

            }
        }while (outIndex>=0);
    }

    private void addADTStoPacket(byte[] packet, int packetLen, int sampleInHz, int chanCfgCounts) {
        int profile = 2; // AAC LC
        int freqIdx = 8; // 16KHz    39=MediaCodecInfo.CodecProfileLevel.AACObjectELD;

        switch (sampleInHz) {
            case 8000: {
                freqIdx = 11;
                break;
            }
            case 16000: {
                freqIdx = 8;
                break;
            }
            default:
                break;
        }
        int chanCfg = chanCfgCounts; // CPE
        // fill in ADTS data
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;

    }

}
