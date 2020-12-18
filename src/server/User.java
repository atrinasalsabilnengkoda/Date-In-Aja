/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 *
 * @author atrin
 */
public class User {
    String name;
    String password;
    private String room;

    public User(String name, String pass) {
        this.name = name;
        this.password = pass;
    }

    public User(String name) {
        this.name = name;
    }

    public String getRoom() {
        return room;
    }

    
    public void setRoom(String room) {
        this.room = room;
    }
    
    
}