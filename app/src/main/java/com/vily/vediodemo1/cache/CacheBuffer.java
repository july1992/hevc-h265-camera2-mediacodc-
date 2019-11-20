package com.vily.vediodemo1.cache;

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;

/**
 *  * description : 
 *  * Author : Vily
 *  * Date : 2019-11-20
 *  
 **/
public class CacheBuffer<T> {
    private final int queueSize = 5000;
    private ArrayBlockingQueue<T> queue = new ArrayBlockingQueue(queueSize);

    public CacheBuffer() {
    }

    public void clear() {
        this.queue.clear();
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public void addQueue(T data) {
        if (this.queue != null && this.queue.size() >= queueSize) {
            Log.e("CacheBuffer", "list is full");
        } else {
            this.queue.add(data);
        }

    }

    public T deQueue() {
        return !this.queue.isEmpty() ? this.queue.poll() : null;
    }

    public int size() {
        return this.queue.size();
    }
}
