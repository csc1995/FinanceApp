package com.databases.example.model;

import android.os.Parcel;
import android.os.Parcelable;

//An Object Class used to hold the data of each transaction record
public class Transaction implements Parcelable {

    public final int id;
    public final int acctId;
    public final int planId;
    public final String name;
    public final String value;
    public final String type;
    public final String category;
    public final String checknum;
    public final String memo;
    public final String time;
    public final String date;
    public final String cleared;

    public Transaction(int id, int acctId, int planId, String name, String value, String type, String category, String checknum, String memo, String time, String date, String cleared) {
        this.id = id;
        this.acctId = acctId;
        this.planId = planId;
        this.name = name;
        this.value = value;
        this.type = type;
        this.category = category;
        this.checknum = checknum;
        this.memo = memo;
        this.time = time;
        this.date = date;
        this.cleared = cleared;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.acctId);
        dest.writeInt(this.planId);
        dest.writeString(this.name);
        dest.writeString(this.value);
        dest.writeString(this.type);
        dest.writeString(this.category);
        dest.writeString(this.checknum);
        dest.writeString(this.memo);
        dest.writeString(this.time);
        dest.writeString(this.date);
        dest.writeString(this.cleared);
    }

    protected Transaction(Parcel in) {
        this.id = in.readInt();
        this.acctId = in.readInt();
        this.planId = in.readInt();
        this.name = in.readString();
        this.value = in.readString();
        this.type = in.readString();
        this.category = in.readString();
        this.checknum = in.readString();
        this.memo = in.readString();
        this.time = in.readString();
        this.date = in.readString();
        this.cleared = in.readString();
    }

    public static final Parcelable.Creator<Transaction> CREATOR = new Parcelable.Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel source) {
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}
