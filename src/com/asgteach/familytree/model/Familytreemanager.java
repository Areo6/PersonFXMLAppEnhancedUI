/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asgteach.familytree.model;

//import com.asgteach.familytree.model.Person;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 *
 * @author Developer
 */
public class Familytreemanager {
    //private Person p;
    private final ObservableMap<Long,Person> map=FXCollections.observableHashMap();
    private static Familytreemanager instance=null;
    protected Familytreemanager(){
        
    }
    public static Familytreemanager getInstance(){
        if(instance==null){
            instance=new Familytreemanager();
            
        }
        return instance;
    }
    public void addListener(MapChangeListener<? super Long,? super Person> ml){
        map.addListener(ml);
    }
    public void removeListener(MapChangeListener<? super Long,? super Person> ml){
        map.removeListener(ml);
    }
    public void addListener(InvalidationListener il){
        map.addListener(il);
    }
    public void removeListener(InvalidationListener il){
        map.removeListener(il);
    }
    public void addPerson(Person p){
        Person person=new Person(p);
        map.put(person.getId(),person);
    }
    public void updatePerson(Person p){
        Person person=new Person(p);
        map.put(person.getId(), person);
    }
    public List<Person> getAllPeople(){
          List<Person> copyList = new ArrayList<>();
         map.values().stream().forEach((p) ->
            copyList.add(new Person(p)));
        return copyList;
    }
}
