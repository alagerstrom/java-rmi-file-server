package com.andreas.common;

import java.io.Serializable;

public interface UserDTO extends Serializable{
    int getId();
    String getName();
    String toString();
}
