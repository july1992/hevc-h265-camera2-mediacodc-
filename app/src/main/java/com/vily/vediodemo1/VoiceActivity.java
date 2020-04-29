package com.vily.vediodemo1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.vily.vediodemo1.camero.encode.AudioEncode;
import com.vily.vediodemo1.voice.AudioPlay;
import com.vily.vediodemo1.voice.CallAudio;

import java.util.Arrays;

public class VoiceActivity extends AppCompatActivity {

    private static final String TAG = "VoiceActivity";
    private CallAudio mCallAudio;
    private AudioPlay mAudioPlay;
    private AudioEncode mAudioEncode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            Log.i(TAG, "initPermission: -------RECORD_AUDIO");
            //请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    50);


        }



        initData();

    }

    private void initData() {
        mAudioPlay = new AudioPlay();
        mAudioPlay.start();

        mAudioEncode = AudioEncode.getInstance();
        mAudioEncode.initAudioCodec();


        mCallAudio = new CallAudio();
        mCallAudio.init(new CallAudio.RecordListener() {
            @Override
            public void onRecord(byte[] var1) {
                mAudioEncode.hardEncoder(var1,var1.length);
                Log.i(TAG, "onRecord: ----:"+var1.length+ Arrays.toString(var1));
//                mAudioPlay.addData(var1);
            }
        });
        mCallAudio.start();



    }


    public void startCall(View view) {

        mCallAudio.restartCall();
        mAudioPlay.startPlay();
    }

    public void stopCall(View view) {
        mCallAudio.stopRecord();
        mAudioPlay.stopPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCallAudio.release();
        mAudioPlay.release();
    }
}
