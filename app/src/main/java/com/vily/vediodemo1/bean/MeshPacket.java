package com.vily.vediodemo1.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *  * description : 
 *  * Author : Vily
 *  * Date : 2020-01-06
 *  
 **/
public class MeshPacket implements Parcelable {

    private int id;
    private int sourceId;
    private int destId;
    private int type;
    private byte[] data;
    private int length;

    public static final Creator<MeshPacket> CREATOR = new Creator<MeshPacket>() {
        public MeshPacket createFromParcel(Parcel in) {
            return new MeshPacket(in);
        }

        public MeshPacket[] newArray(int size) {
            return new MeshPacket[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getDestId() {
        return destId;
    }

    public void setDestId(int destId) {
        this.destId = destId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public MeshPacket() {
    }

    protected MeshPacket(Parcel in) {
        this.id = in.readInt();
        this.sourceId = in.readInt();
        this.destId = in.readInt();
        this.type = in.readInt();
        this.length = in.readInt();
        if (this.length > 0) {
            this.data = new byte[this.length];
            in.readByteArray(this.data);
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.sourceId);
        dest.writeInt(this.destId);
        dest.writeInt(this.type);
        dest.writeInt(this.length);
        if (this.length > 0) {
            dest.writeByteArray(this.data);
        }

    }

    public void readFromParcel(Parcel reply) {
        this.id = reply.readInt();
        this.sourceId = reply.readInt();
        this.destId = reply.readInt();
        this.type = reply.readInt();
        this.length = reply.readInt();
        if (this.length > 0) {
            this.data = new byte[this.length];
            reply.readByteArray(this.data);
        }
    }
}
