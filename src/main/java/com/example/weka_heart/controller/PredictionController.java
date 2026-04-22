package com.example.weka_heart.controller;

import com.example.weka_heart.request.PatientDataRequest;
import com.example.weka_heart.response.PredictionResponse;
import com.example.weka_heart.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prediction")
@RequiredArgsConstructor
@CrossOrigin("*") // Permite solicitudes desde cualquier origen (útil para desarrollo con Angular, React, etc.)
public class PredictionController {


    private final PredictionService predictionService;

    @PostMapping
    public ResponseEntity<PredictionResponse> predict(@RequestBody PatientDataRequest request) {
        return ResponseEntity.ok(predictionService.predict(request));
    }

    @GetMapping("/patients")
    public ResponseEntity<List<PredictionResponse>> getAllPredictions() {
        return ResponseEntity.ok(predictionService.getAllPredictions());
    }
//    @Autowired
//    private PredictionService predictionService;
//
//    /**
//     * Recibe un cuerpo con los datos del paciente y retorna el resultado de la predicción y recomendaciones.
//     */
//    @PostMapping
//    public String predict(@RequestBody PatientDataRequest request) {
//        return predictionService.predict(request);
//    }
//
//    /**
//     * Retorna la lista de todas las predicciones realizadas hasta ahora.
//     */
//    @GetMapping("/patients")
//    public List<Patient> getAllPredictions() {
//        return predictionService.getAllPredictions();
//    }
}
