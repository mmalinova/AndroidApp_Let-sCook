package com.example.letscook.database.user;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.letscook.database.session.Session;

// One-to-one relationship
public class UserWithSession {
    @Embedded
    public Session session;
    @Relation(
            parentColumn = "session_id",
            entityColumn = "my_session"
    )
    public User user;
}
