package com.example.weka_heart.response;

import com.example.weka_heart.request.PatientDataRequest;

public record PredictionResponse(
        Long id,
        String predictedClass,
        String predictionLabel,
        String aiAdvice,
        PatientDataRequest patientData
){

}
