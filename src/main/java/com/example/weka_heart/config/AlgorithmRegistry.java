package com.example.weka_heart.config;

import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.ZeroR;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Central registry of available Weka classifiers.
 * To add a new algorithm, call {@link #register} with a factory lambda.
 */
@Component
public class AlgorithmRegistry {

    public record AlgorithmEntry(String id, String name, String description, Supplier<Classifier> factory) {}

    private final LinkedHashMap<String, AlgorithmEntry> registry = new LinkedHashMap<>();

    public AlgorithmRegistry() {
        register("ZeroR",      "ZeroR",       "Predicts the majority class. Useful as a baseline.",                        ZeroR::new);
        register("OneR",       "OneR",        "Generates a one-rule classifier based on the best single attribute.",        OneR::new);
        register("NaiveBayes", "Naive Bayes", "Probabilistic classifier using Bayes theorem with attribute independence.", NaiveBayes::new);
    }

    /**
     * Register a new algorithm. Call this method in a subclass constructor or
     * @PostConstruct to add custom classifiers without modifying this class.
     */
    public void register(String id, String name, String description, Supplier<Classifier> factory) {
        registry.put(id, new AlgorithmEntry(id, name, description, factory));
    }

    /** Returns a fresh Classifier instance for the given algorithm ID. */
    public Classifier getClassifier(String id) {
        AlgorithmEntry entry = registry.get(id);
        if (entry == null) {
            throw new IllegalArgumentException("Unknown algorithm: " + id);
        }
        return entry.factory().get();
    }

    /** Returns metadata about all registered algorithms (for the frontend). */
    public List<Map<String, String>> getAlgorithmList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (AlgorithmEntry entry : registry.values()) {
            list.add(Map.of(
                "id",          entry.id(),
                "name",        entry.name(),
                "description", entry.description()
            ));
        }
        return list;
    }
}
