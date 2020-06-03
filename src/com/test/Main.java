package com.test;

import java.util.*;

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
        List<Parent> parentList = new ArrayList<>() ;
        parentList.add(new Child());
        for (Parent p:
             parentList) {
            p.Introduce();
        }
    }
}
class Parent{
    public void Introduce(){
        System.out.println("Parent");
    }
}
class  Child extends Parent{
    @Override
    public void Introduce(){
        System.out.println("Child");
    }
}
