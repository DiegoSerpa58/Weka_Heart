package com.example.weka_heart.config;

import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.Logistic;
import weka.classifiers.meta.ClassificationViaClustering;
import weka.classifiers.meta.RegressionByDiscretization;
import weka.classifiers.trees.RandomForest;
import weka.clusterers.EM;
import weka.clusterers.SimpleKMeans;

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

    public record AlgorithmEntry(
            String id,
            String name,
            String description,
            Supplier<Classifier> factory
    ) {}

    private final LinkedHashMap<String, AlgorithmEntry> registry = new LinkedHashMap<>();

    public AlgorithmRegistry() {
        register("Random", "Random", "Clasificador tipo Random Forest para la demo.", RandomForest::new);
        register("Regresion", "Regresión", "Regresión lineal adaptada a clasificación.", this::regressionClassifier);
        register("R.Logistica", "R.Logística", "Regresión logística para clasificación supervisada.", Logistic::new);
        register("Series", "Series", "Modelo orientado a series para la demo.", Logistic::new);
        register("Kmeans", "Kmeans", "Clasificación vía clustering usando SimpleKMeans.", this::kmeansClassifier);
        register("EM", "EM", "Clasificación vía clustering usando EM.", this::emClassifier);
    }

    /**
     * Register a new algorithm entry.
     * Can be called from a @PostConstruct or subclass constructor to extend the registry.
     */
    public void register(String id, String name, String description, Supplier<Classifier> factory) {
        registry.put(id, new AlgorithmEntry(id, name, description, factory));
    }

    /** Returns a fresh Classifier instance for the given algorithm ID. */
    public Classifier getClassifier(String id) {
        AlgorithmEntry entry = registry.get(id);
        if (entry == null) {
            throw new IllegalArgumentException("Unknown algorithm: '" + id + "'. Valid options: " + registry.keySet());
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

    private Classifier regressionClassifier() {
        RegressionByDiscretization model = new RegressionByDiscretization();
        model.setClassifier(new LinearRegression());
        return model;
    }

    private Classifier kmeansClassifier() {
        ClassificationViaClustering model = new ClassificationViaClustering();
        model.setClusterer(new SimpleKMeans());
        return model;
    }

    private Classifier emClassifier() {
        ClassificationViaClustering model = new ClassificationViaClustering();
        model.setClusterer(new EM());
        return model;
    }
//    public record AlgorithmEntry(String id, String name, String description, Supplier<Classifier> factory) {}
//
//    private final LinkedHashMap<String, AlgorithmEntry> registry = new LinkedHashMap<>();
//
//    public AlgorithmRegistry() {
//        register("ZeroR",      "ZeroR",       "Predicts the majority class. Useful as a baseline.",                        ZeroR::new);
//        register("OneR",       "OneR",        "Generates a one-rule classifier based on the best single attribute.",        OneR::new);
//        register("NaiveBayes", "Naive Bayes", "Probabilistic classifier using Bayes theorem with attribute independence.", NaiveBayes::new);
//    }
//
//    /**
//     * Register a new algorithm. Call this method in a subclass constructor or
//     * @PostConstruct to add custom classifiers without modifying this class.
//     */
//    public void register(String id, String name, String description, Supplier<Classifier> factory) {
//        registry.put(id, new AlgorithmEntry(id, name, description, factory));
//    }
//
//    /** Returns a fresh Classifier instance for the given algorithm ID. */
//    public Classifier getClassifier(String id) {
//        AlgorithmEntry entry = registry.get(id);
//        if (entry == null) {
//            throw new IllegalArgumentException("Unknown algorithm: " + id);
//        }
//        return entry.factory().get();
//    }
//
//    /** Returns metadata about all registered algorithms (for the frontend). */
//    public List<Map<String, String>> getAlgorithmList() {
//        List<Map<String, String>> list = new ArrayList<>();
//        for (AlgorithmEntry entry : registry.values()) {
//            list.add(Map.of(
//                "id",          entry.id(),
//                "name",        entry.name(),
//                "description", entry.description()
//            ));
//        }
//        return list;
//    }
}
