package com.example.demo.models;

import jakarta.persistence.*;

@Entity
@Table(name="students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String sid;
    private String name;
    private String weight;
    private String height;
    private String hairColor;
    private String gpa;
    private String relativeH;
    private String relativeW;
    private String marTop;

    public Student(){
        
    }
    

    public Student(String sid, String name, String weight, String height, String hairColor, String gpa) {
        this.sid=sid;
        this.name = name;
        this.weight = weight;
        this.height = height;
        this.hairColor = hairColor;
        this.gpa = gpa;
    }
    
    public String getSid() {
        return sid;
    }
    public void setSid(String sid) {
        this.sid = sid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getWeight() {
        return weight;
    }
    public void setWeight(String weight) {
        this.weight = weight;
    }
    public String getHeight() {
        return height;
    }
    public void setHeight(String height) {
        this.height = height;
    }
    public String getHairColor() {
        return hairColor;
    }
    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }
    public String getGpa() {
        return gpa;
    }
    public void setGpa(String gpa) {
        this.gpa = gpa;
    }


    public String getRelativeH() {
        return relativeH;
    }


    public void setRelativeH(String relativeH) {
        this.relativeH = relativeH;
    }


    public String getRelativeW() {
        return relativeW;
    }


    public void setRelativeW(String relativeW) {
        this.relativeW = relativeW;
    }


    public String getMarTop() {
        return marTop;
    }


    public void setMarTop(String marTop) {
        this.marTop = marTop;
    }
    
    
    
}
