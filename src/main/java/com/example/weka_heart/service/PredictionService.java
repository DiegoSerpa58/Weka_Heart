package com.example.weka_heart.service;

import com.example.weka_heart.entities.PatientPrediction;
import com.example.weka_heart.entities.PredictionRequest;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import weka.classifiers.rules.ZeroR; // Importamos el algoritmo ZeroR
import weka.core.DenseInstance;
import weka.core.Instances;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class PredictionService {

    private static final Logger logger = LoggerFactory.getLogger(PredictionService.class);

    private ZeroR model; // Cambiamos el modelo a ZeroR
    private Instances datasetStructure;
    private List<PatientPrediction> predictionResults = new ArrayList<>();
    private int currentId = 1;

    private String apiKey = "Bearer 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";

    public PredictionService() {
        try {
            loadDatasetAndTrainZeroR();
        } catch (Exception e) {
            logger.error("Error al cargar dataset o entrenar modelo", e);
            throw new RuntimeException("Error: " + e.getMessage(), e);
        }
    }

    // Método para cargar el dataset y entrenar ZeroR
    private void loadDatasetAndTrainZeroR() throws Exception {
        try (InputStream datasetStream = getClass().getResourceAsStream("/DataSet/diabetes.arff");
             InputStreamReader reader = new InputStreamReader(datasetStream)) {
            
            datasetStructure = new Instances(reader);
            // El atributo a predecir es el último (class: tested_positive o tested_negative)
            datasetStructure.setClassIndex(datasetStructure.numAttributes() - 1);
            
            // Inicializar y entrenar el modelo ZeroR
            model = new ZeroR();
            model.buildClassifier(datasetStructure);
            logger.info("✅ Modelo ZeroR entrenado exitosamente con el dataset Diabetes.");
        }
    }

    public String predict(PredictionRequest request) {
        try {
            DenseInstance instance = new DenseInstance(datasetStructure.numAttributes());
            instance.setDataset(datasetStructure);

            // Asignar los valores recibidos al objeto Weka
            instance.setValue(datasetStructure.attribute("preg"), request.getPreg());
            instance.setValue(datasetStructure.attribute("plas"), request.getPlas());
            instance.setValue(datasetStructure.attribute("pres"), request.getPres());
            instance.setValue(datasetStructure.attribute("skin"), request.getSkin());
            instance.setValue(datasetStructure.attribute("insu"), request.getInsu());
            instance.setValue(datasetStructure.attribute("mass"), request.getMass());
            instance.setValue(datasetStructure.attribute("pedi"), request.getPedi());
            instance.setValue(datasetStructure.attribute("age"), request.getAge());

            // Hacer la predicción con ZeroR
            double result = model.classifyInstance(instance);
            String predictedClass = datasetStructure.classAttribute().value((int) result);

            String predictionText = buildPredictionText(predictedClass);

            // Guardar la predicción inmediatamente para que esté disponible en GET /api/prediction/patients
            PatientPrediction predictionResult = new PatientPrediction(
                    currentId++,
                    predictionText,
                    null,
                    request
            );
            predictionResults.add(predictionResult);
            logger.info("✅ Predicción guardada en JSON (id={}).", predictionResult.getId());

            // Intentar obtener consejos de la IA (no bloquea el guardado)
            String advice = getAdviceFromGroqAI(predictedClass);
            predictionResult.setAdvice(advice);

            return predictionText + (advice != null ? "\n\nConsejos de la IA:\n" + advice : "");

        } catch (Exception e) {
            logger.error("Error en la predicción", e);
            throw new RuntimeException("Error en la predicción: " + e.getMessage(), e);
        }
    }

    public List<PatientPrediction> getAllPredictions() {
        return new ArrayList<>(predictionResults);
    }

    public PatientPrediction savePrediction(PredictionRequest request, String predictedClass) {
        String predictionText = buildPredictionText(predictedClass);
        PatientPrediction predictionResult = new PatientPrediction(
                currentId++,
                predictionText,
                null,
                request
        );
        predictionResults.add(predictionResult);
        logger.info("✅ Predicción guardada en JSON (id={}).", predictionResult.getId());
        return predictionResult;
    }

    private String buildPredictionText(String predictedClass) {
        return "Predicción ZeroR: " + predictedClass + " (Nota: ZeroR siempre predice la clase mayoritaria).";
    }

    private String getAdviceFromGroqAI(String predictedClass) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .callTimeout(15, TimeUnit.SECONDS)
                    .build();

            String prompt = "Actúas como un médico endocrinólogo. Un paciente ha sido clasificado por un modelo de Machine Learning con el resultado: " +
                    predictedClass + " para diabetes. Proporciona recomendaciones médicas generales y breves.";

            JSONObject json = new JSONObject();
            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.put(userMessage);
            json.put("messages", messages);
            json.put("model", "llama-3.3-70b-versatile"); 
            json.put("max_tokens", 300);
            json.put("temperature", 0.7);

            RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions") 
                    .addHeader("Authorization", apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                return "No se pudo obtener el consejo de la IA en este momento.";
            }

            String responseString = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseString);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() > 0) {
                return choices.getJSONObject(0).getJSONObject("message").getString("content");
            } else {
                return "No se pudo obtener el consejo de la IA en este momento.";
            }

        } catch (Exception e) {
            return "No se pudo obtener una recomendación de la IA en este momento.";
        }
    }
}