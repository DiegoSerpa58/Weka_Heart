package com.example.weka_heart.service;

import com.example.weka_heart.config.AlgorithmRegistry;
import com.example.weka_heart.dto.ClassificationResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
public class WekaService {

    private final AlgorithmRegistry registry;

    public WekaService(AlgorithmRegistry registry) {
        this.registry = registry;
    }

    /**
     * Loads the uploaded file, runs the chosen classifier with the selected
     * evaluation strategy and returns a structured {@link ClassificationResult}.
     *
     * @param file             ARFF or CSV file to classify
     * @param algorithm        ID matching a registered algorithm in {@link AlgorithmRegistry}
     * @param evaluationMethod {@code "crossvalidation"} or {@code "percentagesplit"}
     * @param folds            number of folds (cross-validation only)
     * @param trainPercent     percentage of data used for training (percentage-split only)
     * @param seed             random seed for reproducibility
     */
    public ClassificationResult classify(MultipartFile file, String algorithm,
                                          String evaluationMethod, int folds,
                                          double trainPercent, int seed) throws Exception {
        if (folds < 2 || folds > 100) {
            throw new IllegalArgumentException("Number of folds must be between 2 and 100.");
        }
        if (trainPercent <= 0 || trainPercent >= 100) {
            throw new IllegalArgumentException("Training percentage must be between 1 and 99.");
        }
        Instances data = loadData(file);
        data.setClassIndex(data.numAttributes() - 1);

        Classifier classifier = registry.getClassifier(algorithm);

        if ("percentagesplit".equalsIgnoreCase(evaluationMethod)) {
            return evaluatePercentageSplit(data, classifier, algorithm, trainPercent, seed);
        } else {
            return evaluateCrossValidation(data, classifier, algorithm, folds, seed);
        }
    }

    // ── Data loading ───────────────────────────────────────────────────────────

    private Instances loadData(MultipartFile file) throws Exception {
        String filename = Objects.requireNonNullElse(file.getOriginalFilename(), "");
        if (filename.toLowerCase().endsWith(".csv")) {
            CSVLoader loader = new CSVLoader();
            loader.setSource(file.getInputStream());
            return loader.getDataSet();
        }
        // Default: ARFF
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream())) {
            return new Instances(reader);
        }
    }

    // ── Evaluation strategies ──────────────────────────────────────────────────

    private ClassificationResult evaluateCrossValidation(Instances data, Classifier classifier,
                                                          String algorithm, int folds, int seed) throws Exception {
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(classifier, data, folds, new Random(seed));
        return buildResult(eval, data, algorithm, "Cross-Validation (" + folds + " folds)");
    }

    private ClassificationResult evaluatePercentageSplit(Instances data, Classifier classifier,
                                                          String algorithm, double trainPercent,
                                                          int seed) throws Exception {
        Instances shuffled = new Instances(data);
        shuffled.randomize(new Random(seed));

        long trainSizeLong = Math.round(shuffled.numInstances() * trainPercent / 100.0);
        int trainSize = (int) Math.min(trainSizeLong, (long) shuffled.numInstances() - 1);
        int testSize  = shuffled.numInstances() - trainSize;

        Instances trainSet = new Instances(shuffled, 0, trainSize);
        Instances testSet  = new Instances(shuffled, trainSize, testSize);

        classifier.buildClassifier(trainSet);

        Evaluation eval = new Evaluation(trainSet);
        eval.evaluateModel(classifier, testSet);
        return buildResult(eval, data, algorithm, "Percentage Split (" + (int) trainPercent + "% train)");
    }

    // ── Result assembly ────────────────────────────────────────────────────────

    private ClassificationResult buildResult(Evaluation eval, Instances data,
                                              String algorithm, String method) throws Exception {
        int numClasses = data.numClasses();
        List<String> classNames = new ArrayList<>();
        for (int i = 0; i < numClasses; i++) {
            classNames.add(data.classAttribute().value(i));
        }

        double[] precision = new double[numClasses];
        double[] recall    = new double[numClasses];
        double[] fMeasure  = new double[numClasses];
        for (int i = 0; i < numClasses; i++) {
            precision[i] = safeDouble(eval.precision(i));
            recall[i]    = safeDouble(eval.recall(i));
            fMeasure[i]  = safeDouble(eval.fMeasure(i));
        }

        return new ClassificationResult(
                algorithm,
                method,
                eval.pctCorrect(),
                eval.kappa(),
                eval.meanAbsoluteError(),
                eval.rootMeanSquaredError(),
                classNames,
                eval.confusionMatrix(),
                precision,
                recall,
                fMeasure,
                data.numInstances(),
                data.numAttributes() - 1,
                data.relationName()
        );
    }

    /** Replaces NaN / Infinity with 0.0 so the JSON serializer doesn't break. */
    private double safeDouble(double value) {
        return Double.isNaN(value) || Double.isInfinite(value) ? 0.0 : value;
    }
}
