package com.example.weka_heart.entities;

public class PredictionRequest {
    private double preg; // Number of times pregnant
    private double plas; // Plasma glucose concentration
    private double pres; // Diastolic blood pressure
    private double skin; // Triceps skin fold thickness
    private double insu; // 2-Hour serum insulin
    private double mass; // Body mass index
    private double pedi; // Diabetes pedigree function
    private double age;  // Age

    // Getters y Setters
    public double getPreg() { return preg; }
    public void setPreg(double preg) { this.preg = preg; }

    public double getPlas() { return plas; }
    public void setPlas(double plas) { this.plas = plas; }

    public double getPres() { return pres; }
    public void setPres(double pres) { this.pres = pres; }

    public double getSkin() { return skin; }
    public void setSkin(double skin) { this.skin = skin; }

    public double getInsu() { return insu; }
    public void setInsu(double insu) { this.insu = insu; }

    public double getMass() { return mass; }
    public void setMass(double mass) { this.mass = mass; }

    public double getPedi() { return pedi; }
    public void setPedi(double pedi) { this.pedi = pedi; }

    public double getAge() { return age; }
    public void setAge(double age) { this.age = age; }
}