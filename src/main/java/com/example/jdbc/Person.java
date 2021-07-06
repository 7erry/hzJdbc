package com.example.jdbc;

import java.io.Serializable;

public class Person implements Serializable {
    public String name;
    public int age;

    public Person(String name, int age){
        this.name = name;
        this.age=age;
    }
}
