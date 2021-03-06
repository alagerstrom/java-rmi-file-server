package com.andreas.server.model;

import com.andreas.common.dto.UserDTO;

public class User implements UserDTO{
    private int id;
    private String name;

    public User(String name) {
        this.id = -1;
        this.name = name;
    }

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "[(USER) " + id + ", " + name + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserDTO){
            UserDTO other = (UserDTO) obj;
            return other.getId() == id && other.getName().equals(name);
        }
        else return false;
    }

    @Override
    public int hashCode() {
        String string = toString();
        return string.hashCode();
    }
}
