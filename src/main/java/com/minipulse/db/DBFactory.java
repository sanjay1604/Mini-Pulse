package com.minipulse.db;

public class DBFactory {
    private static final MiniPulseDB db = new MemoryDB();

    public static MiniPulseDB getDB() {
        return db;
    }
}
