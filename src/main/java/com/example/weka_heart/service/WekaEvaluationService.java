package com.example.weka_heart.service;

import com.example.weka_heart.dto.ClassificationResult;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Encapsulates the two supported Weka evaluation strategies:
 * cross-validation and percentage split.
 *
 * Extracting this from {@code WekaService} keeps each class focused
 * on a single responsibility.
 */
@Service
public class WekaEvaluationService {

    /**
     * Evaluates a classifier using k-fold cross-validation.
     */
    public ClassificationResult crossValidate(Instances data, Classifier classifier,
                                              String algorithm, int folds, int seed) throws Exception {
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(classifier, data, folds, new Random(seed));
        return buildResult(eval, data, algorithm, "Cross-Validation (" + folds + " folds)");
    }

    /**
     * Evaluates a classifier using a percentage split (train/test split).
     */
    public ClassificationResult percentageSplit(Instances data, Classifier classifier,
                                                String algorithm, double trainPercent,
                                                int seed) throws Exception {
        Instances shuffled = new Instances(data);
        shuffled.randomize(new Random(seed));

        int trainSize = computeTrainSize(shuffled.numInstances(), trainPercent);
        int testSize  = shuffled.numInstances() - trainSize;

        Instances trainSet = new Instances(shuffled, 0, trainSize);
        Instances testSet  = new Instances(shuffled, trainSize, testSize);

        classifier.buildClassifier(trainSet);

        Evaluation eval = new Evaluation(trainSet);
        eval.evaluateModel(classifier, testSet);
        return buildResult(eval, data, algorithm, "Percentage Split (" + (int) trainPercent + "% train)");
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private int computeTrainSize(int totalInstances, double trainPercent) {
        long size = Math.round(totalInstances * trainPercent / 100.0);
        return (int) Math.min(size, (long) totalInstances - 1);
    }

    private ClassificationResult buildResult(Evaluation eval, Instances data,
                                             String algorithm, String method) throws Exception {
        int numClasses = data.numClasses();
        List<String> classNames = new ArrayList<>();
        double[] precision = new double[numClasses];
        double[] recall    = new double[numClasses];
        double[] fMeasure  = new double[numClasses];

        for (int i = 0; i < numClasses; i++) {
            classNames.add(data.classAttribute().value(i));
            precision[i] = safe(eval.precision(i));
            recall[i]    = safe(eval.recall(i));
            fMeasure[i]  = safe(eval.fMeasure(i));
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

    /** Replaces NaN / Infinity with 0.0 so JSON serialization never breaks. */
    private double safe(double value) {
        return Double.isNaN(value) || Double.isInfinite(value) ? 0.0 : value;
    }
}