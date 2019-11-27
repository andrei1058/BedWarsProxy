package com.andrei1058.bedwars.proxy.database;

public class SessionKeeper implements Runnable {

    private MySQL db;

    public SessionKeeper(MySQL db){
        this.db = db;
    }

    @Override
    public void run() {
        if (!db.isConnected()) db.connect();
        db.ping();
    }
}
