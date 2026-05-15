package com.example.weka_heart.service;

import com.example.weka_heart.request.PatientDataRequest;
import com.example.weka_heart.response.PredictionResponse;

import java.util.List;

public interface PredictionServiceImpl {

    PredictionResponse predict(PatientDataRequest request);

    List<PredictionResponse> getAllPredictions();


}
