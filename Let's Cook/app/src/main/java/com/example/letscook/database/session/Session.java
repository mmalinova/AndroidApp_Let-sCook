package com.example.letscook.database.session;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

//Define table
@Entity(tableName = "session")
public class Session implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "session_id")
    private long ID;
    @ColumnInfo(name = "installation_id")
    private long installationId;
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;
    @ColumnInfo(name = "created_at")
    private Date createdAt;
    @ColumnInfo(name = "restricted")
    private boolean restricted;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public long getInstallationId() {
        return installationId;
    }

    public void setInstallationId(long installationId) {
        this.installationId = installationId;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }
}
