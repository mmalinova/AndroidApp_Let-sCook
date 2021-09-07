package com.example.letscook.database.product;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

//Define table
@Entity(tableName = "product")
public class Product implements Serializable, Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "product_id")
    private long ID;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "measure_unit")
    private String measureUnit;
    @ColumnInfo(name = "quantity")
    private float quantity;
    @ColumnInfo(name = "belonging")
    private String belonging;
    @ColumnInfo(name = "is_sync")
    private boolean isSync;
    @ColumnInfo(name = "owner_id")
    private long ownerId;

    public Product() {
    }

    protected Product(Parcel in) {
        ID = in.readLong();
        name = in.readString();
        measureUnit = in.readString();
        quantity = in.readFloat();
        belonging = in.readString();
        ownerId = in.readLong();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(String measure_unit) {
        this.measureUnit = measure_unit;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getBelonging() {
        return belonging;
    }

    public void setBelonging(String belonging) {
        this.belonging = belonging;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(ID);
        dest.writeString(name);
        dest.writeString(measureUnit);
        dest.writeFloat(quantity);
        dest.writeString(belonging);
        dest.writeLong(ownerId);
    }
}
