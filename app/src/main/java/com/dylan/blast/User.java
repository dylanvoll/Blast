package com.dylan.blast;

/**
 * Created by Dylan on 2/13/2016.
 */
public class User {
    public String username;
    public String uid;
    public String firstName;
    public String lastName;
    public int multiplier;
    public UsersRowAdapter.ViewHolder view = null;

    public User(String username, String uid, String firstName, String lastName){
        this.username = username;
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String username, String uid, String firstName, String lastName, int multiplier){
        this.username = username;
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.multiplier = multiplier;
    }


}
