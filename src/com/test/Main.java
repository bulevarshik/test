package com.test;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World");
        Collection collection = new HashSet();
        collection.add("3");
        collection.add("2");
        collection.add("1");
        for(Object o:collection){
            System.out.println(o);
        }
    }
}
