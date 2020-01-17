package com.vily.vediodemo1.bean;

import java.util.Arrays;

/**
 *  * description : 
 *  * Author : Vily
 *  * Date : 2020-01-08
 *  
 **/

public class AudioBean {
    private int leapId;
    private int type;
    private byte[] data;

    public AudioBean() {
    }

    public int getLeapId() {
        return this.leapId;
    }

    public void setLeapId(int leapId) {
        this.leapId = leapId;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String toString() {
        return "AudioBean{leapId=" + this.leapId + ", type=" + this.type + ", data=" + Arrays.toString(this.data) + '}';
    }
}
