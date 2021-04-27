package com.example.letscook.database.product;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.letscook.database.user.User;

import java.util.List;

// One-to-many relationship
public class ProductWithOwner {
    @Embedded
    public Product product;
    @Relation(
            parentColumn = "owner_id",
            entityColumn = "user_id"
    )
    public List<User> userList;
}
