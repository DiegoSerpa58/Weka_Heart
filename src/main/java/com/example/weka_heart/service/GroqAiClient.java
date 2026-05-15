package com.example.weka_heart.service;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class GroqAiClient {
    private static final Logger log = LoggerFactory.getLogger(GroqAiClient.class);

    private static final String GROQ_URL   = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL      = "llama-3.3-70b-versatile";
    private static final String FALLBACK   = "No se pudo obtener una recomendación de la IA en este momento.";

    private final OkHttpClient httpClient;

    @Value("${groq.api.key:}")
    private String apiKey;

    public GroqAiClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(5,  TimeUnit.SECONDS)
                .readTimeout(10,    TimeUnit.SECONDS)
                .writeTimeout(5,    TimeUnit.SECONDS)
                .callTimeout(15,    TimeUnit.SECONDS)
                .build();
    }

    /**
     * Asks the LLM for brief medical recommendations given a diabetes prediction.
     *
     * @param predictedClass e.g. {@code "tested_positive"} or {@code "tested_negative"}
     * @return AI-generated advice string, or a fallback message on failure
     */
    public String getAdvice(String predictedClass) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Groq API key not configured — skipping AI advice.");
            return FALLBACK;
        }
        try {
            String prompt = buildPrompt(predictedClass);
            String requestBody = buildRequestBody(prompt);

            Request request = new Request.Builder()
                    .url(GROQ_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    log.warn("Groq API returned non-success: {}", response.code());
                    return FALLBACK;
                }
                return parseAdvice(response.body().string());
            }
        } catch (Exception e) {
            log.warn("Groq AI call failed: {}", e.getMessage());
            return FALLBACK;
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private String buildPrompt(String predictedClass) {
        return "Actúas como un médico endocrinólogo. Un paciente ha sido clasificado por un modelo " +
                "de Machine Learning con el resultado: " + predictedClass +
                " para diabetes. Proporciona recomendaciones médicas generales y breves.";
    }

    private String buildRequestBody(String prompt) {
        JSONObject userMessage = new JSONObject()
                .put("role", "user")
                .put("content", prompt);

        return new JSONObject()
                .put("model",      MODEL)
                .put("max_tokens", 300)
                .put("temperature", 0.7)
                .put("messages",   new JSONArray().put(userMessage))
                .toString();
    }

    private String parseAdvice(String responseBody) {
        JSONObject json    = new JSONObject(responseBody);
        JSONArray  choices = json.getJSONArray("choices");
        if (choices.isEmpty()) {
            return FALLBACK;
        }
        return choices.getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }
}
