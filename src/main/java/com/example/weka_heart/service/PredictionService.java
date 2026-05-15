package com.example.weka_heart.service;

import ch.qos.logback.classic.Logger;
import com.example.weka_heart.request.PatientDataRequest;
import com.example.weka_heart.response.PredictionResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Collections;

import weka.classifiers.Classifier;
import weka.classifiers.rules.ZeroR; // Importamos el algoritmo ZeroR
import weka.core.Instances;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class PredictionService implements PredictionServiceImpl {

    private static final String DATASET_PATH = "/DataSet/diabetes.arff";

    private final DatasetLoader datasetLoader;
    private final DiabetesInstanceBuilder instanceBuilder;
    private final GroqAiClient groqAiClient;

    // Estado del modelo (inmutable después de init)
    private Instances datasetStructure;
    private Classifier model;

    // Thread-safe
    private final List<PredictionResponse> history = new CopyOnWriteArrayList<>();
    private final AtomicLong idSequence = new AtomicLong(1);


    // ─────────────────────────────────────────────
    // 🔹 Lifecycle controlado por Spring
    // ─────────────────────────────────────────────
    @PostConstruct
    public void init() {
        log.info("Initializing PredictionService...");

        try {
            this.datasetStructure = datasetLoader.loadFromClasspath(DATASET_PATH);
            this.model = trainModel(datasetStructure);

            log.info("Model initialized successfully.");
        } catch (Exception e) {
            log.error("Error initializing model", e);
            throw new IllegalStateException("Model initialization failed", e);
        }
    }

    // ─────────────────────────────────────────────
    // 🔹 Core business logic
    // ─────────────────────────────────────────────
    @Override
    public PredictionResponse predict(PatientDataRequest request) {

        validateModel();

        try {
            double rawResult = model.classifyInstance(
                    instanceBuilder.build(request, datasetStructure)
            );

            String predictedClass = datasetStructure
                    .classAttribute()
                    .value((int) rawResult);

            String label = buildLabel(predictedClass);

            // ⚠️ llamada externa → posible latencia
            String advice = safeAdvice(predictedClass);

            PredictionResponse response = buildResponse(
                    predictedClass,
                    label,
                    advice,
                    request
            );

            history.add(response);

            log.info("Prediction saved (id={}, class={})",
                    response.id(), predictedClass);

            return response;

        } catch (Exception e) {
            log.error("Error during prediction", e);
            throw new RuntimeException("Prediction failed", e);
        }
    }

    @Override
    public List<PredictionResponse> getAllPredictions() {
        return List.copyOf(history);
    }

    // ─────────────────────────────────────────────
    // 🔹 Private helpers (clean separation)
    // ─────────────────────────────────────────────

    private Classifier trainModel(Instances data) throws Exception {
        ZeroR zeroR = new ZeroR();
        zeroR.buildClassifier(data);
        return zeroR;
    }

    private void validateModel() {
        if (model == null || datasetStructure == null) {
            throw new IllegalStateException("Model is not initialized");
        }
    }

    private PredictionResponse buildResponse(
            String predictedClass,
            String label,
            String advice,
            PatientDataRequest request
    ) {
        return new PredictionResponse(
                idSequence.getAndIncrement(),
                predictedClass,
                label,
                advice,
                request
        );
    }

    private String safeAdvice(String predictedClass) {
        try {
            return groqAiClient.getAdvice(predictedClass);
        } catch (Exception e) {
            log.warn("AI advice failed, returning fallback", e);
            return "No advice available at the moment.";
        }
    }

    private String buildLabel(String predictedClass) {
        return "Predicción ZeroR: " + predictedClass +
                " (modelo baseline - clase mayoritaria)";
    }
}
    
//    private static final Logger log = LoggerFactory.getLogger(PredictionService.class);
//    private static final String DATASET_PATH = "/DataSet/diabetes.arff";
//
//    private final DatasetLoader datasetLoader;
//    private final DiabetesInstanceBuilder instanceBuilder;
//    private final GroqAiClient groqAiClient;
//
//    private final Instances datasetStructure;
//    private final ZeroR model;
//
//    private final List<PredictionResponse> history = new ArrayList<>();
//    private final AtomicInteger idSequence = new AtomicInteger(1);
//
//    public PredictionService(DatasetLoader datasetLoader,
//                             DiabetesInstanceBuilder instanceBuilder,
//                             GroqAiClient groqAiClient) {
//        this.datasetLoader   = datasetLoader;
//        this.instanceBuilder = instanceBuilder;
//        this.groqAiClient    = groqAiClient;
//
//        this.datasetStructure = loadAndTrainModel();
//        this.model            = trainZeroR(datasetStructure);
//    }
//
//    /**
//     * Classifies a patient using ZeroR and returns the prediction with optional AI advice.
//     */
//    @Override
//    public PredictionResponse predict(PatientDataRequest request) {
//        try {
//            double rawResult = model.classifyInstance(instanceBuilder.build(request, datasetStructure));
//            String predictedClass = datasetStructure.classAttribute().value((int) rawResult);
//
//            String label  = buildLabel(predictedClass);
//            String advice = groqAiClient.getAdvice(predictedClass);
//
//            PredictionResponse response = new PredictionResponse(
//                    Long.valueOf(idSequence.getAndIncrement()),
//                    predictedClass,
//                    label,
//                    advice,
//                    request
//            );
//
//            history.add(response);
//            log.info("Prediction saved (id={}, class={})", response.id(), predictedClass);
//            return response;
//
//        } catch (Exception e) {
//            log.error("Error during prediction", e);
//            throw new RuntimeException("Error during prediction: " + e.getMessage(), e);
//        }
//    }
//
//    /** Returns an unmodifiable view of all prediction history. */
//    @Override
//    public List<PredictionResponse> getAllPredictions() {
//        return Collections.unmodifiableList(history);
//    }
//
//    // ── Private helpers ───────────────────────────────────────────────────────
//
//    private Instances loadAndTrainModel() {
//        try {
//            return datasetLoader.loadFromClasspath(DATASET_PATH);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to load dataset from " + DATASET_PATH, e);
//        }
//    }
//
//    private ZeroR trainZeroR(Instances data) {
//        try {
//            ZeroR zeroR = new ZeroR();
//            zeroR.buildClassifier(data);
//            log.info("ZeroR model trained successfully.");
//            return zeroR;
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to train ZeroR model", e);
//        }
//    }
//
//    private String buildLabel(String predictedClass) {
//        return "Predicción ZeroR: " + predictedClass +
//                " (Nota: ZeroR siempre predice la clase mayoritaria).";
//    }










//    private static final Logger logger = LoggerFactory.getLogger(PredictionService.class);
//
//    private ZeroR model; // Cambiamos el modelo a ZeroR
//    private Instances datasetStructure;
//    private List<Patient> predictionResults = new ArrayList<>();
//    private int currentId = 1;
//
//    private String apiKey = "Bearer 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";
//
//    public PredictionService() {
//        try {
//            loadDatasetAndTrainZeroR();
//        } catch (Exception e) {
//            logger.error("Error al cargar dataset o entrenar modelo", e);
//            throw new RuntimeException("Error: " + e.getMessage(), e);
//        }
//    }
//
//    // Método para cargar el dataset y entrenar ZeroR
//    private void loadDatasetAndTrainZeroR() throws Exception {
//        try (InputStream datasetStream = getClass().getResourceAsStream("/DataSet/diabetes.arff");
//             InputStreamReader reader = new InputStreamReader(datasetStream)) {
//
//            datasetStructure = new Instances(reader);
//            // El atributo a predecir es el último (class: tested_positive o tested_negative)
//            datasetStructure.setClassIndex(datasetStructure.numAttributes() - 1);
//
//            // Inicializar y entrenar el modelo ZeroR
//            model = new ZeroR();
//            model.buildClassifier(datasetStructure);
//            logger.info("✅ Modelo ZeroR entrenado exitosamente con el dataset Diabetes.");
//        }
//    }
//
//    public String predict(PatientDataRequest request) {
//        try {
//            DenseInstance instance = new DenseInstance(datasetStructure.numAttributes());
//            instance.setDataset(datasetStructure);
//
//            // Asignar los valores recibidos al objeto Weka
//            instance.setValue(datasetStructure.attribute("preg"), request.getPreg());
//            instance.setValue(datasetStructure.attribute("plas"), request.getPlas());
//            instance.setValue(datasetStructure.attribute("pres"), request.getPres());
//            instance.setValue(datasetStructure.attribute("skin"), request.getSkin());
//            instance.setValue(datasetStructure.attribute("insu"), request.getInsu());
//            instance.setValue(datasetStructure.attribute("mass"), request.getMass());
//            instance.setValue(datasetStructure.attribute("pedi"), request.getPedi());
//            instance.setValue(datasetStructure.attribute("age"), request.getAge());
//
//            // Hacer la predicción con ZeroR
//            double result = model.classifyInstance(instance);
//            String predictedClass = datasetStructure.classAttribute().value((int) result);
//
//            String predictionText = buildPredictionText(predictedClass);
//
//            // Guardar la predicción inmediatamente para que esté disponible en GET /api/prediction/patients
//            Patient predictionResult = new Patient(
//                    currentId++,
//                    predictionText,
//                    null,
//                    request
//            );
//            predictionResults.add(predictionResult);
//            logger.info("✅ Predicción guardada en JSON (id={}).", predictionResult.getId());
//
//            // Intentar obtener consejos de la IA (no bloquea el guardado)
//            String advice = getAdviceFromGroqAI(predictedClass);
//            predictionResult.setAdvice(advice);
//
//            return predictionText + (advice != null ? "\n\nConsejos de la IA:\n" + advice : "");
//
//        } catch (Exception e) {
//            logger.error("Error en la predicción", e);
//            throw new RuntimeException("Error en la predicción: " + e.getMessage(), e);
//        }
//    }
//
//    public List<Patient> getAllPredictions() {
//        return new ArrayList<>(predictionResults);
//    }
//
//    public Patient savePrediction(PatientDataRequest request, String predictedClass) {
//        String predictionText = buildPredictionText(predictedClass);
//        Patient predictionResult = new Patient(
//                currentId++,
//                predictionText,
//                null,
//                request
//        );
//        predictionResults.add(predictionResult);
//        logger.info("✅ Predicción guardada en JSON (id={}).", predictionResult.getId());
//        return predictionResult;
//    }
//
//    private String buildPredictionText(String predictedClass) {
//        return "Predicción ZeroR: " + predictedClass + " (Nota: ZeroR siempre predice la clase mayoritaria).";
//    }
//
//    private String getAdviceFromGroqAI(String predictedClass) {
//        try {
//            OkHttpClient client = new OkHttpClient.Builder()
//                    .connectTimeout(5, TimeUnit.SECONDS)
//                    .readTimeout(10, TimeUnit.SECONDS)
//                    .writeTimeout(5, TimeUnit.SECONDS)
//                    .callTimeout(15, TimeUnit.SECONDS)
//                    .build();
//
//            String prompt = "Actúas como un médico endocrinólogo. Un paciente ha sido clasificado por un modelo de Machine Learning con el resultado: " +
//                    predictedClass + " para diabetes. Proporciona recomendaciones médicas generales y breves.";
//
//            JSONObject json = new JSONObject();
//            JSONArray messages = new JSONArray();
//            JSONObject userMessage = new JSONObject();
//            userMessage.put("role", "user");
//            userMessage.put("content", prompt);
//            messages.put(userMessage);
//            json.put("messages", messages);
//            json.put("model", "llama-3.3-70b-versatile");
//            json.put("max_tokens", 300);
//            json.put("temperature", 0.7);
//
//            RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
//
//            Request request = new Request.Builder()
//                    .url("https://api.groq.com/openai/v1/chat/completions")
//                    .addHeader("Authorization", apiKey)
//                    .addHeader("Content-Type", "application/json")
//                    .post(body)
//                    .build();
//
//            Response response = client.newCall(request).execute();
//
//            if (!response.isSuccessful()) {
//                return "No se pudo obtener el consejo de la IA en este momento.";
//            }
//
//            String responseString = response.body().string();
//            JSONObject jsonResponse = new JSONObject(responseString);
//            JSONArray choices = jsonResponse.getJSONArray("choices");
//            if (choices.length() > 0) {
//                return choices.getJSONObject(0).getJSONObject("message").getString("content");
//            } else {
//                return "No se pudo obtener el consejo de la IA en este momento.";
//            }
//
//        } catch (Exception e) {
//            return "No se pudo obtener una recomendación de la IA en este momento.";
//        }
//    }
