package com.vily.vediodemo1.cache;

import android.util.Log;

/**
 *  * description : 
 *  * Author : Vily
 *  * Date : 2019-11-20
 *  
 **/
public class CacheReadThread extends Thread {

    private static final String TAG = "CacheReadThread";

    private CacheBuffer<byte[]> buffer = new CacheBuffer();
    private CacheReadListener readListener;
    private static CacheReadThread mCacheReadThread;


    public static CacheReadThread getInstance(){

        synchronized (CacheReadThread.class){
            if(mCacheReadThread==null){

                mCacheReadThread=new CacheReadThread();
            }
        }

        return mCacheReadThread;
    }


    public void addData(byte[] packet) {
        Log.i(TAG, "addData: ----入队:"+packet.length);
        if(buffer!=null){
            this.buffer.addQueue(packet);
        }

    }


    @Override
    public void run() {
        super.run();

        while (!isInterrupted()) {
            try {

                if (!this.buffer.isEmpty()) {
                    byte[] bytes = this.buffer.deQueue();
                    this.readListener.readMessage(bytes);
                    Log.i(TAG, "run: ----出队："+bytes.length);

                }

//                sleep(50L);
                continue;

            } catch (Exception var2) {
                var2.printStackTrace();
            }

            return;
        }
    }

    public void release() {

        if (this.buffer != null) {
            this.buffer.clear();
            this.buffer = null;
        }
        interrupt();

        mCacheReadThread=null;

    }

    public void setReadListener(CacheReadListener listener) {
        this.readListener = listener;
    }
}
