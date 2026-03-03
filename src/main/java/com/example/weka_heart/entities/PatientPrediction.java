package com.example.weka_heart.entities;

public class PatientPrediction {

    private int id;
    private String resultado;
    private String advice; // Consejo de la IA

    private double preg;
    private double plas;
    private double pres;
    private double skin;
    private double insu;
    private double mass;
    private double pedi;
    private double age;

    public PatientPrediction(int id, String resultado, String advice, PredictionRequest request) {
        this.id = id;
        this.resultado = resultado;
        this.advice = advice;

        this.preg = request.getPreg();
        this.plas = request.getPlas();
        this.pres = request.getPres();
        this.skin = request.getSkin();
        this.insu = request.getInsu();
        this.mass = request.getMass();
        this.pedi = request.getPedi();
        this.age = request.getAge();
    }

    // Getters y Setters
    public int getId() { return id; }
    public String getResultado() { return resultado; }
    public String getAdvice() { return advice; }
    public void setAdvice(String advice) { this.advice = advice; }
    public double getPreg() { return preg; }
    public double getPlas() { return plas; }
    public double getPres() { return pres; }
    public double getSkin() { return skin; }
    public double getInsu() { return insu; }
    public double getMass() { return mass; }
    public double getPedi() { return pedi; }
    public double getAge() { return age; }
}