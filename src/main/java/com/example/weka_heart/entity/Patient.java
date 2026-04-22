package com.example.weka_heart.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Patient {
    private Long id;
    private double preg;
    private double plas;
    private double pres;
    private double skin;
    private double insu;
    private double mass;
    private double pedi;
    private double age;
}