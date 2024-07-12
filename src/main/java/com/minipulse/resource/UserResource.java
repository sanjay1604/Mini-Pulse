package com.minipulse.resource;

import com.minipulse.db.DBFactory;
import com.minipulse.db.MiniPulseDB;

public class UserResource {

    private MiniPulseDB db;

    public UserResource() {
        setDb(DBFactory.getDB());
    }

    public MiniPulseDB getDb() {
        return db;
    }

    public void setDb(MiniPulseDB db) {
        this.db = db;
    }

    public void saveUser(String user, String userName) {
        db.newUser(user, userName);
    }

    public String getUser(String user) {
        return db.getUser(user);
    }
}
