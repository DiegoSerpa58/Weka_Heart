package com.example.weka_heart.request;

public record PatientDataRequest(
        double preg,
        double plas,
        double pres,
        double skin,
        double insu,
        double mass,
        double pedi,
        double age
) {
}