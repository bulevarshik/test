package com.test;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        List<Parent> parentList = new ArrayList<>() ;
        parentList.add(new Child());
        Main main = new Main();
        main.method(parentList);
    }
    void method(List<? super Parent> list){
        list.add(new GrandSon());
        list.add(new Child());
    }
}
class Parent{
    public void Introduce(){
        System.out.println("Parent");//comment
    }
}
class  Child extends Parent{
    @Override
    public void Introduce(){
        System.out.println("Child");
    }
}
class GrandSon extends Child{
    @Override
    public void Introduce(){
        System.out.println("GrandSon");
    }
}