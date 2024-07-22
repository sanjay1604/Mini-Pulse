package com.minipulse.resource;

import com.minipulse.db.DBFactory;
import com.minipulse.db.MiniPulseDB;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("user")
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

    @POST
    @Path("{user}/{userName}")
    public void saveUser(@PathParam("user") String user, @PathParam("userName") String userName) {
        db.newUser(user, userName);
    }

    @GET
    @Path("{user}")
    public String getUser(@PathParam("user") String user) {
        return db.getUser(user);
    }
}
