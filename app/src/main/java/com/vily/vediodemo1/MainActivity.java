package com.vily.vediodemo1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.SurfaceTexture;
import android.media.Image;
import android.media.MediaCodec;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import com.vily.vediodemo1.cache.CacheReadListener;
import com.vily.vediodemo1.cache.CacheReadThread;
import com.vily.vediodemo1.camero.decode.CameraRecordDecoder;
import com.vily.vediodemo1.camero.utils.AutoFitTextureView;
import com.vily.vediodemo1.camero.utils.Camera2Utils;

public class MainActivity extends AppCompatActivity {
    private ImageView mIv_change_flash;
    private ImageView mIv_change_camera;

    private static final String TAG = "MainActivity";

    private AutoFitTextureView mTextureView;

    private Camera2Utils mCamera2Utils;
    private SurfaceView mSv_surface;
    private CameraRecordDecoder mCameraRecordDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mTextureView = findViewById(R.id.texture);
        mIv_change_flash = findViewById(R.id.iv_change_flash);
        mIv_change_camera = findViewById(R.id.iv_change_camera);
        mSv_surface = findViewById(R.id.sv_surface);


        mCamera2Utils = new Camera2Utils(MainActivity.this, mTextureView);


        mSv_surface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                Log.i(TAG, "surfaceDestroyed: ------");

                onDestroy();
            }
        });

    }


    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {


            mCamera2Utils.openCamera(width,height);

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {

            mCamera2Utils.configureTransform(width, height);

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {

            Log.i(TAG, "onSurfaceTextureDestroyed: ------");
            release();

            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {

        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        mCamera2Utils.startBackgroundThread();
        if (mTextureView.isAvailable()) {
            mCamera2Utils.openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "onDestroy: ------");


    }

    private void release(){
        if(mCameraRecordDecoder!=null){
            mCameraRecordDecoder.release();
            mCameraRecordDecoder=null;
        }

        CacheReadThread.getInstance().setReadListener(null);
        CacheReadThread.getInstance().release();

        if(mCamera2Utils!=null){
            mCamera2Utils.release();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startRecord(View view) {

        mCamera2Utils.startRecordingVideo();

    }

    public void stopRecord(View view) {
        mCamera2Utils.stopRecordingVideo();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void readPreview(View view) {

        mCamera2Utils.readPreview();
    }

    public void stopReadPreview(View view) {
        mCamera2Utils.stopReadPreview();
    }

    public void encode(View view) {

        mCameraRecordDecoder = new CameraRecordDecoder();
        mCameraRecordDecoder.initCameraDecode(mSv_surface.getHolder());

        CacheReadThread.getInstance().start();
        CacheReadThread.getInstance().setReadListener(new CacheReadListener() {
            @Override
            public void readMessage(byte[] var1) {

                Log.i(TAG, "readMessage: -----:"+var1.length);
                mCameraRecordDecoder.onFrame(var1,0,var1.length);
            }
        });

        mCamera2Utils.encodePreview();
    }

    public void stopencode(View view) {
        if(mCameraRecordDecoder!=null){
            mCameraRecordDecoder.release();
            mCameraRecordDecoder=null;
        }

        CacheReadThread.getInstance().setReadListener(null);
        CacheReadThread.getInstance().release();
    }
}
